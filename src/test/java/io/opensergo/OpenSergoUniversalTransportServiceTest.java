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

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;
import io.opensergo.proto.fault_tolerance.v1.FaultToleranceRule;
import io.opensergo.proto.transport.v1.*;
import io.opensergo.subscribe.OpenSergoConfigSubscriber;
import io.opensergo.subscribe.SubscribeKey;
import io.opensergo.util.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * @author Jax4Li
 */
public class OpenSergoUniversalTransportServiceTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();
    private OpenSergoClient client;

    @Before
    public void setup() throws Exception {
        Server server = ServerBuilder.forPort(0).fallbackHandlerRegistry(serviceRegistry).directExecutor().build();
        grpcCleanup.register(server.start());
        client = OpenSergoClientManager.get().getOrCreateClient("localhost", server.getPort());
        client.start();
    }

    @After
    public void cleanup() throws Exception {
        client.close();
    }

    /**
     * client(without subscriber) subscribe config, then server response ack with success code
     * @throws InterruptedException
     */
    @Test
    public void testSubscribeConfigServerResponseSuccess() throws InterruptedException {
        testSubscribeConfigServerResponse(OpenSergoTransportConstants.CODE_SUCCESS, true);
    }

    /**
     * client(without subscriber) subscribe config, but server response ack with error code
     * @throws Exception
     */
    @Test
    public void testSubscribeConfigServerResponseError() throws Exception {
        testSubscribeConfigServerResponse(OpenSergoTransportConstants.CODE_ERROR_SUBSCRIBE_HANDLER_ERROR, true);
    }

    /**
     * client(without subscriber) unsubscribe config, then server response ack with success code
     * @throws InterruptedException
     */
    @Test
    public void testUnsubscribeConfigServerResponseSuccess() throws InterruptedException {
        testSubscribeConfigServerResponse(OpenSergoTransportConstants.CODE_SUCCESS, false);
    }

    private void testSubscribeConfigServerResponse(int code, boolean subscribe) throws InterruptedException {
        final AtomicReference<SubscribeRequest> actualRequest = new AtomicReference<>();

        // implement the fake service
        OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase serviceImpl =
                new OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase(){
                    @Override
                    public StreamObserver<SubscribeRequest> subscribeConfig(StreamObserver<SubscribeResponse> responseObserver) {
                        return (FakerStreamObserver) subscribeRequest -> {
                            actualRequest.set(subscribeRequest);
                            SubscribeResponse response = buildACKResponse(code);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        };
                    }
                };
        serviceRegistry.addService(serviceImpl);

        // client call service
        SubscribeKey subscribeKey = new SubscribeKey("default", "my-service", ConfigKind.TRAFFIC_ROUTER_STRATEGY);
        if (subscribe){
            client.subscribeConfig(subscribeKey);
        }else {
            client.unsubscribeConfig(subscribeKey);
        }

        // wait for response finish
        Thread.sleep(1000);
        assertNotNull(actualRequest.get());
        assertNotNull(actualRequest.get().getTarget());
        assertEquals(subscribeKey.getApp(), actualRequest.get().getTarget().getApp());
        assertEquals(subscribeKey.getNamespace(), actualRequest.get().getTarget().getNamespace());
        assertEquals(subscribeKey.getKind().getKindName(), actualRequest.get().getTarget().getKinds(0));
    }

    /**
     * client(with subscriber) subscribe faultToleranceRule, then server push the updated faultToleranceRule
     * @throws InterruptedException
     * @throws InvalidProtocolBufferException
     */
    @Test
    public void testServerPushDataSubscriberSuccess() throws InterruptedException, InvalidProtocolBufferException {
        final AtomicReference<Object> actualData = new AtomicReference<>();
        final AtomicReference<SubscribeRequest> actualRequest = new AtomicReference<>();
        final AtomicReference<SubscribeResponse> expectedResponse = new AtomicReference<>();

        // implement the fake service
        OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase serviceImpl =
                new OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase(){
                    @Override
                    public StreamObserver<SubscribeRequest> subscribeConfig(StreamObserver<SubscribeResponse> responseObserver) {
                        return (FakerStreamObserver) subscribeRequest -> {
                            if (StringUtils.isBlank(subscribeRequest.getResponseAck())) {
                                actualRequest.set(subscribeRequest);
                            }else {
                                // from client ack, skip
                                return;
                            }
                            SubscribeResponse response = buildSpecificFaultToleranceRuleSubscribeResponse();
                            expectedResponse.set(response);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        };
                    }
                };

        // client side subscriber
        OpenSergoConfigSubscriber configSubscriber = (subscribeKey, data) -> {
            actualData.set(data);
            return true;
        };
        testServerPushFaultToleranceRule(configSubscriber, serviceImpl, actualData, actualRequest, expectedResponse);
    }

    /**
     * client(with subscriber) subscribe faultToleranceRule, then server push the outdated faultToleranceRule
     * @throws InterruptedException
     * @throws InvalidProtocolBufferException
     */
    @Test
    public void testServerPushOutdatedVersionData() throws InterruptedException, InvalidProtocolBufferException {
        final AtomicReference<Object> actualData = new AtomicReference<>();
        final AtomicReference<SubscribeRequest> actualRequest = new AtomicReference<>();
        final AtomicReference<SubscribeResponse> expectedResponse = new AtomicReference<>();

        // implement the fake service
        OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase serviceImpl =
                new OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase(){
                    @Override
                    public StreamObserver<SubscribeRequest> subscribeConfig(StreamObserver<SubscribeResponse> responseObserver) {
                        return (FakerStreamObserver) subscribeRequest -> {
                            if (StringUtils.isBlank(subscribeRequest.getResponseAck())) {
                                actualRequest.set(subscribeRequest);
                            }else {
                                // from client ack, skip
                                return;
                            }
                            // push first time data, it could be cached by client
                            SubscribeResponse response = buildSpecificFaultToleranceRuleSubscribeResponse();
                            expectedResponse.set(response);
                            responseObserver.onNext(response);

                            try {
                                // wait for first time pushing client finish
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {}
                            // push second time data, but it is outdated.
                            DataWithVersion dataWithVersion = DataWithVersion.newBuilder().setVersion(0)
                                    .addData(response.getDataWithVersion().getDataList().get(0)).build();
                            response = SubscribeResponse.newBuilder()
                                    .setNamespace(response.getNamespace())
                                    .setApp(response.getApp())
                                    .setDataWithVersion(dataWithVersion)
                                    .setResponseId("2")
                                    .setKind(response.getKind())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        };
                    }
                };

        // client side subscriber
        OpenSergoConfigSubscriber configSubscriber = (subscribeKey, data) -> {
            actualData.set(data);
            return true;
        };
        testServerPushFaultToleranceRule(configSubscriber, serviceImpl, actualData, actualRequest, expectedResponse);
    }

    /**
     * client(with subscriber) subscribe faultToleranceRule, then server push the updated faultToleranceRule, but client side subscriber handler error
     * @throws InterruptedException
     * @throws InvalidProtocolBufferException
     */
    @Test
    public void testServerPushDataButSubscriberError() throws InterruptedException, InvalidProtocolBufferException {
        final AtomicReference<Object> actualData = new AtomicReference<>();
        final AtomicReference<SubscribeRequest> actualRequest = new AtomicReference<>();
        final AtomicReference<SubscribeResponse> expectedResponse = new AtomicReference<>();

        // implement the fake service
        OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase serviceImpl =
                new OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase(){
                    @Override
                    public StreamObserver<SubscribeRequest> subscribeConfig(StreamObserver<SubscribeResponse> responseObserver) {
                        return (FakerStreamObserver) subscribeRequest -> {
                            if (StringUtils.isBlank(subscribeRequest.getResponseAck())) {
                                actualRequest.set(subscribeRequest);
                            }else {
                                // from client ack, skip
                                return;
                            }
                            SubscribeResponse response = buildSpecificFaultToleranceRuleSubscribeResponse();
                            expectedResponse.set(response);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        };
                    }
                };

        // client side subscriber
        OpenSergoConfigSubscriber configSubscriber = (subscribeKey, data) -> {
            actualData.set(data);
            // throw error
            throw new RuntimeException("SUBSCRIBE_HANDLER_ERROR");
        };
        testServerPushFaultToleranceRule(configSubscriber, serviceImpl, actualData, actualRequest, expectedResponse);
    }

    private void testServerPushFaultToleranceRule(OpenSergoConfigSubscriber subscriber,
                                                  OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceImplBase serviceImpl,
                                                  AtomicReference<Object> actualData, AtomicReference<SubscribeRequest> actualRequest,
                                                  AtomicReference<SubscribeResponse> expectedResponse) throws InterruptedException, InvalidProtocolBufferException {

        // registry fake service
        serviceRegistry.addService(serviceImpl);

        // client call service
        SubscribeKey subscribeKey = new SubscribeKey("default", "my-service2", ConfigKind.FAULT_TOLERANCE_RULE);
        client.subscribeConfig(subscribeKey, subscriber);

        // wait for request/response finish
        Thread.sleep(2000);
        assertNotNull(actualRequest.get());
        assertNotNull(actualRequest.get().getTarget());
        assertEquals(subscribeKey.getApp(), actualRequest.get().getTarget().getApp());
        assertEquals(subscribeKey.getNamespace(), actualRequest.get().getTarget().getNamespace());
        assertEquals(subscribeKey.getKind().getKindName(), actualRequest.get().getTarget().getKinds(0));

        assertNotNull(actualData.get());
        assertTrue(actualData.get() instanceof List);
        List<Object> dataList = (List<Object>) actualData.get();
        assertNotNull(dataList.get(0));
        assertEquals(1, dataList.size());
        assertTrue(dataList.get(0) instanceof FaultToleranceRule);
        FaultToleranceRule faultToleranceRule = (FaultToleranceRule) dataList.get(0);
        assertNotNull(faultToleranceRule.getAction());

        assertNotNull(expectedResponse.get());
        assertNotNull(expectedResponse.get().getDataWithVersion());
        Any expectedData = expectedResponse.get().getDataWithVersion().getData(0);
        assertNotNull(expectedData);
        assertTrue(expectedData.is(FaultToleranceRule.class));
        FaultToleranceRule  expectedFaultToleranceRule = expectedData.unpack(FaultToleranceRule.class);

        assertEquals(expectedFaultToleranceRule.getAction(), faultToleranceRule.getAction());
        assertEquals(expectedFaultToleranceRule.getStrategiesList(), faultToleranceRule.getStrategiesList());
        assertEquals(expectedFaultToleranceRule.getTargetsList(), faultToleranceRule.getTargetsList());
    }

    private SubscribeResponse buildACKResponse(int code) {
        return SubscribeResponse.newBuilder().setAck(OpenSergoTransportConstants.ACK_FLAG)
                .setStatus(Status.newBuilder().setCode(code)).build();
    }

    private SubscribeResponse buildSpecificFaultToleranceRuleSubscribeResponse() {
        FaultToleranceRule.FaultToleranceRuleTargetRef ruleTargetRef = FaultToleranceRule.FaultToleranceRuleTargetRef.newBuilder()
                .setTargetResourceName("/foo")
                .build();
        FaultToleranceRule.FaultToleranceStrategyRef strategyRef = FaultToleranceRule.FaultToleranceStrategyRef.newBuilder()
                .setKind("RateLimitStrategy")
                .setName("rate-limit-foo")
                .build();
        FaultToleranceRule.FaultToleranceActionRef actionRef = FaultToleranceRule.FaultToleranceActionRef.newBuilder()
                .setKind("HttpRequestFallbackAction")
                .setName("fallback-foo")
                .build();
        FaultToleranceRule rule = FaultToleranceRule.newBuilder()
                .setAction(actionRef)
                .addStrategies(strategyRef)
                .addTargets(ruleTargetRef)
                .build();
        DataWithVersion dataWithVersion = DataWithVersion.newBuilder().setVersion(1)
                .addData(Any.newBuilder().setTypeUrl("/"+FaultToleranceRule.class.getName()).setValue(rule.toByteString()).build()).build();
        return SubscribeResponse.newBuilder()
                .setNamespace("default")
                .setApp("my-service2")
                .setDataWithVersion(dataWithVersion)
                .setResponseId("1")
                .setKind(ConfigKind.FAULT_TOLERANCE_RULE.getKindName())
                .build();
    }

    public interface FakerStreamObserver extends StreamObserver<SubscribeRequest> {
        @Override
        default void onError(Throwable throwable){}
        @Override
        default void onCompleted(){}
    }
}
