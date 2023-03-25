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

import io.grpc.internal.GrpcUtil;
import io.grpc.internal.ManagedChannelImplBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiangnan Jia
 * @author Eric Zhao
 * @author Jax4Li
 */
public class OpenSergoClientConfig {
    private final int maxInboundMessageSize;
    private final int maxRetryAttempts;
    private final int maxHedgedAttempts;
    private final long retryBufferSize;
    private final long perRpcBufferLimit;
    private final long idleTimeoutMillis;
    private final long keepAliveTimeMillis;
    private final long keepAliveTimeoutMillis;

    private final boolean serverSideTls;
    private final File serverTrustCertFile;
    private final boolean clientSideTls;
    private final File clientCertChainFile;
    private final File clientPrivateKeyFile;
    private final String clientPrivateKeyPwd;

    private OpenSergoClientConfig(Builder builder) {
        this.maxInboundMessageSize = builder.maxInboundMessageSize;
        this.maxRetryAttempts = builder.maxRetryAttempts;
        this.maxHedgedAttempts = builder.maxHedgedAttempts;
        this.retryBufferSize = builder.retryBufferSize;
        this.perRpcBufferLimit = builder.perRpcBufferLimit;
        this.idleTimeoutMillis = builder.idleTimeoutMillis;
        this.keepAliveTimeMillis = builder.keepAliveTimeMillis;
        this.keepAliveTimeoutMillis = builder.keepAliveTimeoutMillis;
        this.serverTrustCertFile = builder.serverTrustCertFile;
        this.clientCertChainFile = builder.clientCertChainFile;
        this.clientPrivateKeyFile = builder.clientPrivateKeyFile;
        this.clientPrivateKeyPwd = builder.clientPrivateKeyPwd;
        this.serverSideTls = builder.serverSideTls;
        this.clientSideTls = builder.clientSideTls;
    }

    public OpenSergoClientConfig() {
        this(new Builder());
    }

    public SslContext newSslContext(){
        SslContextBuilder sslContextBuilder = GrpcSslContexts.forClient();
        if (this.isServerSideTls()){
            sslContextBuilder.trustManager(this.getServerTrustCertFile());
        }
        if (this.isClientSideTls()){
            sslContextBuilder.keyManager(this.getClientCertChainFile(),
                    this.getClientPrivateKeyFile(), this.getClientPrivateKeyPwd());
        }
        try {
            return sslContextBuilder.build();
        } catch (SSLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public int getMaxHedgedAttempts() {
        return maxHedgedAttempts;
    }

    public long getRetryBufferSize() {
        return retryBufferSize;
    }

    public long getPerRpcBufferLimit() {
        return perRpcBufferLimit;
    }

    public long getIdleTimeoutMillis() {
        return idleTimeoutMillis;
    }

    public long getKeepAliveTimeMillis() {
        return keepAliveTimeMillis;
    }

    public long getKeepAliveTimeoutMillis() {
        return keepAliveTimeoutMillis;
    }

    public File getServerTrustCertFile() {
        return serverTrustCertFile;
    }

    public File getClientCertChainFile() {
        return clientCertChainFile;
    }

    public File getClientPrivateKeyFile() {
        return clientPrivateKeyFile;
    }

    public String getClientPrivateKeyPwd() {
        return clientPrivateKeyPwd;
    }

    public boolean isServerSideTls() {
        return serverSideTls;
    }

    public boolean isClientSideTls() {
        return clientSideTls;
    }

    public static class Builder {
        private boolean serverSideTls;
        private boolean clientSideTls;
        /** The file should include a collection of X.509 certificates in PEM/CRT format that can be used for verification of the remote server's certificate. */
        private File serverTrustCertFile;
        /** An X.509 certificate chain file in PEM/CRT format is from client. */
        private File clientCertChainFile;
        /** A PKCS#8 private key file in PEM format is from client.*/
        private File clientPrivateKeyFile;
        /** The password of the keyFile, or null if it's not password-protected*/
        private String clientPrivateKeyPwd;

        /** @see io.grpc.internal.AbstractManagedChannelImplBuilder#maxInboundMessageSize */
        private int maxInboundMessageSize = GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE;
        /** @see io.grpc.internal.ManagedChannelImplBuilder#maxRetryAttempts */
        private int maxRetryAttempts = 5;
        /** @see ManagedChannelImplBuilder#maxHedgedAttempts */
        private int maxHedgedAttempts = 5;
        /** @see ManagedChannelImplBuilder#retryBufferSize */
        private long retryBufferSize = 1L << 24;
        /** @see ManagedChannelImplBuilder#perRpcBufferLimit */
        private long perRpcBufferLimit = 1L << 20;
        /** @see ManagedChannelImplBuilder#IDLE_MODE_DEFAULT_TIMEOUT_MILLIS */
        private long idleTimeoutMillis = TimeUnit.MINUTES.toMillis(30);
        /** @see GrpcUtil#DEFAULT_KEEPALIVE_TIMEOUT_NANOS */
        private long keepAliveTimeoutMillis = TimeUnit.SECONDS.toMillis(20);
        /** @see NettyChannelBuilder#keepAliveTimeNanos */
        private long keepAliveTimeMillis = TimeUnit.NANOSECONDS.toMillis(GrpcUtil.KEEPALIVE_TIME_NANOS_DISABLED);

        public OpenSergoClientConfig.Builder serverSideTls(@Nonnull File serverTrustCertFile) {
            this.serverSideTls = true;
            this.serverTrustCertFile = serverTrustCertFile;
            return this;
        }

        public OpenSergoClientConfig.Builder clientSideTls(@Nonnull File clientCertChainFile, @Nonnull File clientPrivateKeyFile,
                                                           @Nullable String clientPrivateKeyPwd) {
            this.clientSideTls = true;
            this.clientCertChainFile = clientCertChainFile;
            this.clientPrivateKeyFile = clientPrivateKeyFile;
            this.clientPrivateKeyPwd = clientPrivateKeyPwd;
            return this;
        }

        public OpenSergoClientConfig.Builder maxInboundMessageSize(int maxInboundMessageSize) {
            this.maxInboundMessageSize = maxInboundMessageSize;
            return this;
        }

        public OpenSergoClientConfig.Builder maxRetryAttempts(int maxRetryAttempts) {
            this.maxRetryAttempts = maxRetryAttempts;
            return this;
        }

        public OpenSergoClientConfig.Builder maxHedgedAttempts(int maxHedgedAttempts) {
            this.maxHedgedAttempts = maxHedgedAttempts;
            return this;
        }

        public OpenSergoClientConfig.Builder retryBufferSize(long retryBufferSize) {
            this.retryBufferSize = retryBufferSize;
            return this;
        }

        public OpenSergoClientConfig.Builder perRpcBufferLimit(long perRpcBufferLimit) {
            this.perRpcBufferLimit = perRpcBufferLimit;
            return this;
        }

        public OpenSergoClientConfig.Builder idleTimeoutMillis(long idleTimeoutMillis) {
            this.idleTimeoutMillis = idleTimeoutMillis;
            return this;
        }

        public OpenSergoClientConfig.Builder keepAliveTimeMillis(long keepAliveTimeMillis) {
            this.keepAliveTimeMillis = keepAliveTimeMillis;
            return this;
        }

        public OpenSergoClientConfig.Builder keepAliveTimeoutMillis(long keepAliveTimeoutMillis) {
            this.keepAliveTimeoutMillis = keepAliveTimeoutMillis;
            return this;
        }

        public OpenSergoClientConfig build() {
            return new OpenSergoClientConfig(this);
        }
    }
}
