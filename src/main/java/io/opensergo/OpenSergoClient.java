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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.opensergo.log.OpenSergoLogger;
import io.opensergo.proto.transport.v1.OpenSergoUniversalTransportServiceGrpc;
import io.opensergo.proto.transport.v1.SubscribeOpType;
import io.opensergo.proto.transport.v1.SubscribeRequest;
import io.opensergo.proto.transport.v1.SubscribeRequestTarget;
import io.opensergo.subscribe.OpenSergoConfigSubscriber;
import io.opensergo.subscribe.SubscribeKey;
import io.opensergo.subscribe.SubscribeRegistry;
import io.opensergo.subscribe.SubscribedConfigCache;
import io.opensergo.util.AssertUtils;
import io.opensergo.util.IdentifierUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eric Zhao
 */
public class OpenSergoClient implements AutoCloseable {

    private final ManagedChannel channel;
    private final OpenSergoUniversalTransportServiceGrpc.OpenSergoUniversalTransportServiceStub transportGrpcStub;

    private StreamObserver<SubscribeRequest> requestAndResponseWriter;

    private final SubscribedConfigCache configCache;
    private final SubscribeRegistry subscribeRegistry;

    private AtomicInteger reqId;
    protected volatile OpenSergoClientStatus status;

    public OpenSergoClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            // TODO: support TLS
            .usePlaintext()
            .build();
        this.transportGrpcStub = OpenSergoUniversalTransportServiceGrpc.newStub(channel);
        this.configCache = new SubscribedConfigCache();
        this.subscribeRegistry = new SubscribeRegistry();
        this.reqId = new AtomicInteger(0);
        status = OpenSergoClientStatus.INITIAL;
    }

    public void registerSubscribeInfo(OpenSergoClientSubscribeInfo subscribeInfo) {
        // Register subscriber to local.
        if (Optional.of(subscribeInfo.getSubscriberList()).isPresent() && subscribeInfo.getSubscriberList().size() > 0) {
            subscribeInfo.getSubscriberList().forEach(subscriber -> {
                this.subscribeRegistry.registerSubscriber(subscribeInfo.getSubscribeKey(), subscriber);
                OpenSergoLogger.info("OpenSergo subscribeinfo registered, subscribeKey={}, subscriber={}", subscribeInfo.getSubscribeKey(), subscriber);

                if (requestAndResponseWriter != null && this.status == OpenSergoClientStatus.STARTED) {
                    this.subscribeConfig(subscribeInfo.getSubscribeKey());
                }
            });
        }
    }

    public void start() throws Exception {
        OpenSergoLogger.info("OpensergoClient is starting...");

        if (status == OpenSergoClientStatus.INITIAL) {
            OpenSergoLogger.info("open keepavlive thread");
            Thread keepAliveThread = new Thread(this::keepAlive);
            keepAliveThread.setName("thread-opensergo-keepalive-" + keepAliveThread.getId());
            keepAliveThread.setDaemon(true);
            keepAliveThread.start();
        }

        status = OpenSergoClientStatus.STARTING;

        this.requestAndResponseWriter = transportGrpcStub.withWaitForReady()
            .subscribeConfig(new OpenSergoSubscribeClientObserver(this));

        OpenSergoLogger.info("begin to subscribe config-data...");
        this.subscribeRegistry.getSubscriberKeysAll().forEach(subscribeKey -> {
            this.subscribeConfig(subscribeKey);
        });

        OpenSergoLogger.info("openSergoClient is started");
        status = OpenSergoClientStatus.STARTED;
    }

    private void keepAlive() {
        try {
            if (status != OpenSergoClientStatus.STARTING
                    && status != OpenSergoClientStatus.STARTED
                    && status != OpenSergoClientStatus.SHUTDOWN) {
                OpenSergoLogger.info("try to restart openSergoClient...");
                this.start();
            }
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            if( status != OpenSergoClientStatus.SHUTDOWN) {
                keepAlive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        requestAndResponseWriter.onCompleted();

        // stop the keepAliveThread
        status = OpenSergoClientStatus.SHUTDOWN;

        // gracefully drain the requests, then close the connection
        channel.shutdown();
    }

    public boolean unsubscribeConfig(SubscribeKey subscribeKey) {
        AssertUtils.assertNotNull(subscribeKey, "subscribeKey cannot be null");
        AssertUtils.assertNotNull(subscribeKey.getApp(), "app cannot be null");
        AssertUtils.assertNotNull(subscribeKey.getKind(), "kind cannot be null");

        if (requestAndResponseWriter == null) {
            OpenSergoLogger.error("Fatal error occurred on OpenSergo gRPC ClientObserver", new IllegalStateException("gRPC stream is not ready"));
            status = OpenSergoClientStatus.INTERRUPTED;
        }
        SubscribeRequestTarget subTarget = SubscribeRequestTarget.newBuilder()
            .setNamespace(subscribeKey.getNamespace()).setApp(subscribeKey.getApp())
            .addKinds(subscribeKey.getKind().getKindName())
            .build();
        SubscribeRequest request = SubscribeRequest.newBuilder()
            .setTarget(subTarget).setOpType(SubscribeOpType.UNSUBSCRIBE)
            .build();
        // Send SubscribeRequest (unsubscribe command)
        requestAndResponseWriter.onNext(request);

        // Remove subscribers of the subscribe target.
        subscribeRegistry.removeAllSubscribers(subscribeKey);

        return true;
    }

    public boolean subscribeConfig(SubscribeKey subscribeKey) {
        return subscribeConfig(subscribeKey, null);
    }

    public boolean subscribeConfig(SubscribeKey subscribeKey, OpenSergoConfigSubscriber subscriber) {
        AssertUtils.assertNotNull(subscribeKey, "subscribeKey cannot be null");
        AssertUtils.assertNotNull(subscribeKey.getApp(), "app cannot be null");
        AssertUtils.assertNotNull(subscribeKey.getKind(), "kind cannot be null");

        if (requestAndResponseWriter == null) {
            OpenSergoLogger.error("Fatal error occurred on OpenSergo gRPC ClientObserver", new IllegalStateException("gRPC stream is not ready"));
            status = OpenSergoClientStatus.INTERRUPTED;
        }
        SubscribeRequestTarget subTarget = SubscribeRequestTarget.newBuilder()
            .setNamespace(subscribeKey.getNamespace()).setApp(subscribeKey.getApp())
            .addKinds(subscribeKey.getKind().getKindName())
            .build();
        SubscribeRequest request = SubscribeRequest.newBuilder()
            .setRequestId(String.valueOf(reqId.incrementAndGet()))
            .setTarget(subTarget).setOpType(SubscribeOpType.SUBSCRIBE)
            .setIdentifier(IdentifierUtils.generateIdentifier(System.identityHashCode(this)))
            .build();
        // Send SubscribeRequest
        requestAndResponseWriter.onNext(request);

        return true;
    }

    public SubscribedConfigCache getConfigCache() {
        return configCache;
    }

    public SubscribeRegistry getSubscribeRegistry() {
        return subscribeRegistry;
    }

}
