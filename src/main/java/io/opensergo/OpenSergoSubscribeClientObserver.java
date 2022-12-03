/*
 * Copyright 2022, OpenSergo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opensergo;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Any;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.opensergo.log.OpenSergoLogger;
import io.opensergo.subscribe.LocalDataNotifyResult;
import io.opensergo.subscribe.SubscribedData;
import io.opensergo.proto.transport.v1.DataWithVersion;
import io.opensergo.proto.transport.v1.Status;
import io.opensergo.proto.transport.v1.SubscribeRequest;
import io.opensergo.proto.transport.v1.SubscribeResponse;
import io.opensergo.subscribe.OpenSergoConfigSubscriber;
import io.opensergo.subscribe.SubscribeKey;
import io.opensergo.util.StringUtils;

/**
 * @author Eric Zhao
 */
public class OpenSergoSubscribeClientObserver implements ClientResponseObserver<SubscribeRequest, SubscribeResponse> {

    private ClientCallStreamObserver<SubscribeRequest> requestStream;

    private OpenSergoClient openSergoClient;

    public OpenSergoSubscribeClientObserver(OpenSergoClient openSergoClient) {
        this.openSergoClient = openSergoClient;
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<SubscribeRequest> requestStream) {
        this.requestStream = requestStream;
    }

    private LocalDataNotifyResult notifyDataChange(SubscribeKey subscribeKey, DataWithVersion dataWithVersion)
        throws Exception {
        long receivedVersion = dataWithVersion.getVersion();
        SubscribedData cachedData = this.openSergoClient.getConfigCache().getDataFor(subscribeKey);
        if (cachedData != null && cachedData.getVersion() > receivedVersion) {
            // The upcoming data is out-dated, so we'll not resolve the push request.
            return new LocalDataNotifyResult().setCode(OpenSergoTransportConstants.CODE_ERROR_VERSION_OUTDATED);
        }

        // Decode actual data from the raw "Any" data.
        List<Object> dataList = decodeActualData(subscribeKey.getKind().getKindName(), dataWithVersion.getDataList());
        // Update to local config cache.
        this.openSergoClient.getConfigCache().updateData(subscribeKey, dataList, receivedVersion);

        List<OpenSergoConfigSubscriber> subscribers = this.openSergoClient.getSubscribeRegistry().getSubscribersOf(subscribeKey);
        if (subscribers == null || subscribers.isEmpty()) {
            // no-subscriber is acceptable (just for cache-and-pull mode)
            return LocalDataNotifyResult.withSuccess(dataList);
        }

        List<Throwable> notifyErrors = new ArrayList<>();
        // Notify subscribers
        for (OpenSergoConfigSubscriber subscriber : subscribers) {
            try {
                subscriber.onConfigUpdate(subscribeKey, dataList);
            } catch (Throwable t) {
                OpenSergoLogger.error("Failed to notify OpenSergo config change event, subscribeKey={}, subscriber={}",
                    subscribeKey, subscriber);
                notifyErrors.add(t);
            }
        }

        if (notifyErrors.isEmpty()) {
            return LocalDataNotifyResult.withSuccess(dataList);
        } else {
            return new LocalDataNotifyResult().setCode(OpenSergoTransportConstants.CODE_ERROR_SUBSCRIBE_HANDLER_ERROR)
                .setDecodedData(dataList).setNotifyErrors(notifyErrors);
        }
    }

    @Override
    public void onNext(SubscribeResponse pushCommand) {
        if (!StringUtils.isEmpty(pushCommand.getAck())) {
            // This indicates a response
            int code = pushCommand.getStatus().getCode();
            if (code == OpenSergoTransportConstants.CODE_SUCCESS) {
                return;
            }
            if (code >= 4000 && code < 4100) {
                OpenSergoLogger.warn("Warn: req failed, command={}", pushCommand);
                // TODO: handle me
                return;
            }
        }
        // server-push command received.
        String kindName = pushCommand.getKind();

        try {
            ConfigKindMetadata kindMetadata = OpenSergoConfigKindRegistry.getKindMetadata(kindName);
            if (kindMetadata == null) {
                throw new IllegalArgumentException("unrecognized config kind: " + kindName);
            }

            SubscribeKey subscribeKey = new SubscribeKey(pushCommand.getNamespace(), pushCommand.getApp(),
                kindMetadata.getKind());
            // Decode the actual data and notify to upstream subscribers.
            LocalDataNotifyResult localResult = notifyDataChange(subscribeKey, pushCommand.getDataWithVersion());

            // TODO: handle partial-success (i.e. the data has been updated to cache, but error occurred in subscribers)
            Status status;
            switch (localResult.getCode()) {
                case OpenSergoTransportConstants.CODE_SUCCESS:
                    status = Status.newBuilder().setCode(OpenSergoTransportConstants.CODE_SUCCESS).build();
                    break;
                case OpenSergoTransportConstants.CODE_ERROR_SUBSCRIBE_HANDLER_ERROR:
                    StringBuilder message = new StringBuilder();
                    for (Throwable t : localResult.getNotifyErrors()) {
                        message.append(t.toString()).append('|');
                    }
                    status = Status.newBuilder().setMessage(message.toString()).setCode(
                        OpenSergoTransportConstants.CODE_SUCCESS).build();
                    break;
                case OpenSergoTransportConstants.CODE_ERROR_VERSION_OUTDATED:
                    status = Status.newBuilder().setCode(OpenSergoTransportConstants.CODE_ERROR_VERSION_OUTDATED)
                        .setMessage("outdated version").build();
                    break;
                default:
                    status = Status.newBuilder().setCode(localResult.getCode()).build();
                    break;
            }

            // ACK response
            SubscribeRequest pushAckResponse = SubscribeRequest.newBuilder().setStatus(status)
                .setResponseAck(OpenSergoTransportConstants.ACK_FLAG)
                .setRequestId(pushCommand.getResponseId()).build();
            requestStream.onNext(pushAckResponse);
        } catch (Exception ex) {
            // TODO: improve the error handling logic
            OpenSergoLogger.error("Handle push command failed", ex);

            // NACK response
            SubscribeRequest pushNackResponse = SubscribeRequest.newBuilder()
                .setStatus(Status.newBuilder().setCode(OpenSergoTransportConstants.CODE_ERROR_UNKNOWN)
                    .setMessage(ex.toString()).build())
                .setResponseAck(OpenSergoTransportConstants.NACK_FLAG).build();
            requestStream.onNext(pushNackResponse);
        }
    }

    private List<Object> decodeActualData(String kind, List<Any> rawList) throws Exception {
        ConfigKindMetadata kindMetadata = OpenSergoConfigKindRegistry.getKindMetadata(kind);
        if (kindMetadata == null) {
            throw new IllegalArgumentException("unrecognized config kind: " + kind);
        }
        List<Object> list = new ArrayList<>();
        for (Any e : rawList) {
            list.add(e.unpack((Class) kindMetadata.getKindClass()));
        }
        return list;
    }

    @Override
    public void onError(Throwable t) {
        // TODO add handles for different io.grpc.Status of Throwable from ClientCallStreamObserver<SubscribeRequest>
        io.grpc.Status.Code errorCode = io.grpc.Status.fromThrowable(t).getCode();
        if(errorCode.equals(io.grpc.Status.UNAVAILABLE.getCode())) {
            this.openSergoClient.status = OpenSergoClientStatus.INTERRUPTED;
        }
        OpenSergoLogger.error("Fatal error occurred on OpenSergo gRPC ClientObserver", t);
    }

    @Override
    public void onCompleted() {
        OpenSergoLogger.info("OpenSergoSubscribeClientObserver onCompleted");
    }
}
