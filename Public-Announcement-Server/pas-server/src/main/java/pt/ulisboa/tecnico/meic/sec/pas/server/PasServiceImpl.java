package pt.ulisboa.tecnico.meic.sec.pas.server;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.HelloRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.HelloResponse;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc.*;
import io.grpc.stub.StreamObserver;

public class PasServiceImpl extends PasServiceImplBase {

    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
          .append("Hello, ")
          .toString();

        HelloResponse response = HelloResponse.newBuilder()
          .setBoard(greeting)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}