package pt.ulisboa.tecnico.meic.sec.pas.server;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc.*;
import io.grpc.stub.StreamObserver;

public class PasServiceImpl extends PasServiceImplBase {

    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    	
    	int replyStatus = 200;
    	
    	// these are the fields we received
    	byte[] encryptedMessage = request.getEncryptedMessage().toByteArray();
    	byte[] encryptedAESKey = request.getEncryptedAESKey().toByteArray();
    	
    	
    	
    	
    	// get the server's private key
    	    	
    	
    	// unencrypt aes key
    	
    	
    	// unencrypt message
    	
    	
    	// compare received digest to the calculated one
    	
    	
    	// if the digest is valid, we try to register the new user    	
    	// (in the server) check if the received fields are valid
    	
    	
        // if valid > register(key, name, password)
        // else > replyStatus = 400
        
        RegisterResponse response = RegisterResponse.newBuilder()
          .setStatus(replyStatus)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}