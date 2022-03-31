package io.opensergo.proto.service_contract.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * MetadataService report metadata
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.1)",
    comments = "Source: opensergo/proto/service_contract/v1/service_contract.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MetadataServiceGrpc {

  private MetadataServiceGrpc() {}

  public static final String SERVICE_NAME = "opensergo.proto.service_contract.v1.MetadataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.opensergo.proto.service_contract.v1.ReportMetadataRequest,
      io.opensergo.proto.service_contract.v1.ReportMetadataReply> getReportMetadataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReportMetadata",
      requestType = io.opensergo.proto.service_contract.v1.ReportMetadataRequest.class,
      responseType = io.opensergo.proto.service_contract.v1.ReportMetadataReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.opensergo.proto.service_contract.v1.ReportMetadataRequest,
      io.opensergo.proto.service_contract.v1.ReportMetadataReply> getReportMetadataMethod() {
    io.grpc.MethodDescriptor<io.opensergo.proto.service_contract.v1.ReportMetadataRequest, io.opensergo.proto.service_contract.v1.ReportMetadataReply> getReportMetadataMethod;
    if ((getReportMetadataMethod = MetadataServiceGrpc.getReportMetadataMethod) == null) {
      synchronized (MetadataServiceGrpc.class) {
        if ((getReportMetadataMethod = MetadataServiceGrpc.getReportMetadataMethod) == null) {
          MetadataServiceGrpc.getReportMetadataMethod = getReportMetadataMethod =
              io.grpc.MethodDescriptor.<io.opensergo.proto.service_contract.v1.ReportMetadataRequest, io.opensergo.proto.service_contract.v1.ReportMetadataReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReportMetadata"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.opensergo.proto.service_contract.v1.ReportMetadataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.opensergo.proto.service_contract.v1.ReportMetadataReply.getDefaultInstance()))
              .setSchemaDescriptor(new MetadataServiceMethodDescriptorSupplier("ReportMetadata"))
              .build();
        }
      }
    }
    return getReportMetadataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MetadataServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetadataServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetadataServiceStub>() {
        @java.lang.Override
        public MetadataServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetadataServiceStub(channel, callOptions);
        }
      };
    return MetadataServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MetadataServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetadataServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetadataServiceBlockingStub>() {
        @java.lang.Override
        public MetadataServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetadataServiceBlockingStub(channel, callOptions);
        }
      };
    return MetadataServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MetadataServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetadataServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetadataServiceFutureStub>() {
        @java.lang.Override
        public MetadataServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetadataServiceFutureStub(channel, callOptions);
        }
      };
    return MetadataServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * MetadataService report metadata
   * </pre>
   */
  public static abstract class MetadataServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * ReportMetadata report Metadata to server
     * </pre>
     */
    public void reportMetadata(io.opensergo.proto.service_contract.v1.ReportMetadataRequest request,
        io.grpc.stub.StreamObserver<io.opensergo.proto.service_contract.v1.ReportMetadataReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportMetadataMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReportMetadataMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.opensergo.proto.service_contract.v1.ReportMetadataRequest,
                io.opensergo.proto.service_contract.v1.ReportMetadataReply>(
                  this, METHODID_REPORT_METADATA)))
          .build();
    }
  }

  /**
   * <pre>
   * MetadataService report metadata
   * </pre>
   */
  public static final class MetadataServiceStub extends io.grpc.stub.AbstractAsyncStub<MetadataServiceStub> {
    private MetadataServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetadataServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetadataServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * ReportMetadata report Metadata to server
     * </pre>
     */
    public void reportMetadata(io.opensergo.proto.service_contract.v1.ReportMetadataRequest request,
        io.grpc.stub.StreamObserver<io.opensergo.proto.service_contract.v1.ReportMetadataReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportMetadataMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * MetadataService report metadata
   * </pre>
   */
  public static final class MetadataServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<MetadataServiceBlockingStub> {
    private MetadataServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetadataServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetadataServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * ReportMetadata report Metadata to server
     * </pre>
     */
    public io.opensergo.proto.service_contract.v1.ReportMetadataReply reportMetadata(io.opensergo.proto.service_contract.v1.ReportMetadataRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportMetadataMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * MetadataService report metadata
   * </pre>
   */
  public static final class MetadataServiceFutureStub extends io.grpc.stub.AbstractFutureStub<MetadataServiceFutureStub> {
    private MetadataServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetadataServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetadataServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * ReportMetadata report Metadata to server
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.opensergo.proto.service_contract.v1.ReportMetadataReply> reportMetadata(
        io.opensergo.proto.service_contract.v1.ReportMetadataRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportMetadataMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REPORT_METADATA = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MetadataServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MetadataServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REPORT_METADATA:
          serviceImpl.reportMetadata((io.opensergo.proto.service_contract.v1.ReportMetadataRequest) request,
              (io.grpc.stub.StreamObserver<io.opensergo.proto.service_contract.v1.ReportMetadataReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class MetadataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MetadataServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.opensergo.proto.service_contract.v1.ServiceContractProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MetadataService");
    }
  }

  private static final class MetadataServiceFileDescriptorSupplier
      extends MetadataServiceBaseDescriptorSupplier {
    MetadataServiceFileDescriptorSupplier() {}
  }

  private static final class MetadataServiceMethodDescriptorSupplier
      extends MetadataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MetadataServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MetadataServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MetadataServiceFileDescriptorSupplier())
              .addMethod(getReportMetadataMethod())
              .build();
        }
      }
    }
    return result;
  }
}
