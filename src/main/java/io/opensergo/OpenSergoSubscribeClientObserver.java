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
import io.opensergo.proto.transport.v1.Status;
import io.opensergo.proto.transport.v1.SubscribeRequest;
import io.opensergo.proto.transport.v1.SubscribeResponse;
import io.opensergo.subscribe.OpenSergoConfigSubscriber;
import io.opensergo.subscribe.SubscribeKey;
import io.opensergo.subscribe.SubscribeRegistry;
import io.opensergo.subscribe.SubscribedConfigCache;

/**
 * @author Eric Zhao
 */
public class OpenSergoSubscribeClientObserver implements ClientResponseObserver<SubscribeRequest, SubscribeResponse> {

    private ClientCallStreamObserver<SubscribeRequest> requestStream;

    private final SubscribedConfigCache configCache;
    private final SubscribeRegistry subscribeRegistry;

    public OpenSergoSubscribeClientObserver(SubscribedConfigCache configCache,
                                            SubscribeRegistry subscribeRegistry) {
        this.configCache = configCache;
        this.subscribeRegistry = subscribeRegistry;
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<SubscribeRequest> requestStream) {
        this.requestStream = requestStream;
    }

    public List<Object> notifyDataChange(String namespace, String appName, ConfigKindMetadata metadata,
                                         List<Any> rawDataList) throws Exception {
        SubscribeKey subscribeKey = new SubscribeKey(namespace, appName, metadata.getKind());

        // Decode actual data from the raw "Any" data.
        List<Object> dataList = decodeActualData(metadata.getKindName(), rawDataList);
        // Update to local config cache.
        configCache.updateData(subscribeKey, dataList);

        List<OpenSergoConfigSubscriber> subscribers = subscribeRegistry.getSubscribersOf(subscribeKey);
        if (subscribers == null || subscribers.isEmpty()) {
            // no-subscriber is acceptable (just for cache-and-pull mode)
            return dataList;
        }

        List<Throwable> notifyErrors = new ArrayList<>();
        // Notify subscribers
        for (OpenSergoConfigSubscriber subscriber : subscribers) {
            try {
                subscriber.onConfigUpdate(subscribeKey, dataList);
            } catch (Throwable t) {
                notifyErrors.add(t);
            }
        }

        // TODO: handle all errors and propagate to caller

        return dataList;
    }

    @Override
    public void onNext(SubscribeResponse pushCommand) {
        // server-push command received.
        String kindName = pushCommand.getKind();

        try {
            ConfigKindMetadata kindMetadata = OpenSergoConfigKindRegistry.getKindMetadata(kindName);
            if (kindMetadata == null) {
                throw new IllegalArgumentException("unrecognized config kind: " + kindName);
            }

            // Decode the actual data and notify to upstream subscribers.
            List<Object> dataList = notifyDataChange(pushCommand.getNamespace(), pushCommand.getApp(), kindMetadata,
                pushCommand.getDataList());
            // TODO: handle partial-success (i.e. the data has been updated to cache, but error occurred in subscribers)

            // TODO: track versionInfo and ackInfo

            // ACK response
            SubscribeRequest pushAckResponse = SubscribeRequest.newBuilder()
                .setStatus(Status.newBuilder().setCode(0).build())
                .build();
            requestStream.onNext(pushAckResponse);
        } catch (Exception ex) {
            // TODO: handle error (but not for ack error?)

            // NACK response
            SubscribeRequest pushNackResponse = SubscribeRequest.newBuilder()
                .setStatus(Status.newBuilder().setCode(-1).setMessage(ex.toString()).build())
                .build();
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
        // TODO: handle error
    }

    @Override
    public void onCompleted() {

    }
}
