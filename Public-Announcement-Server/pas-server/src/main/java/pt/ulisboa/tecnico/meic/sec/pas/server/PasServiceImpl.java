package pt.ulisboa.tecnico.meic.sec.pas.server;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingReply;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc.*;

import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;

import io.grpc.stub.StreamObserver;

public class PasServiceImpl extends PasServiceImplBase {
	
	public void ping(PingRequest req, StreamObserver<PingReply> responseObserver) {
		String received = req.getMessage();
	    PingReply reply = PingReply.newBuilder().setMessage(received).build();
	    responseObserver.onNext(reply);
	    responseObserver.onCompleted();
	}

    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    	
    	int replyStatus = 200;
    	
    	// these are the fields we received
    	byte[] encryptedMessage = request.getEncryptedMessage().toByteArray();
    	byte[] encryptedAESKey = request.getEncryptedAESKey().toByteArray();
    	byte[] publicKeyBytes = request.getPublicKeyBytes().toByteArray();
    	Key clientPubKey = RSA.toKey(publicKeyBytes);
    	
    	// retrieve the server's private key
    	// TODO cleartext password / what about multiple servers?
    	KeyPair keys = RSA.getKeyPairFromKeyStore("server1", "password", "password");
    	Key privKey = keys.getPrivate();
    	
    	// unencrypt aes key
    	byte[] encryptedAESKey_pass1 = RSA.decrypt(privKey, encryptedAESKey);
    	byte[] encryptedAESKey_pass2 = RSA.decrypt(clientPubKey, encryptedAESKey_pass1);
    	Key aesKey = RSA.toKey(encryptedAESKey_pass2);
    	
    	// unencrypt message
    	byte[] messageBytes = AES.decrypt(aesKey, encryptedMessage);
    	RegisterMessage message = (RegisterMessage) DataUtils.bytesToObj(messageBytes);
    	
    	// compare received digest to the calculated one
    	byte[] receivedDigest = message.getDigest().toByteArray();
    	
    	RegisterMessage.Data data = message.getData();
		byte[] dataBytes = DataUtils.objToBytes(data);
		byte[] digest = DataUtils.digest(dataBytes);
		
		// if the digest is valid, we try to register the new user
		if (MessageDigest.isEqual(digest, receivedDigest)) {
			// TODO implement server logic
			// replyStatus = register(key, name, password)
			// (in the server) check if the received fields are valid
		} else {
			replyStatus = 400;
		}
    	
        RegisterResponse response = RegisterResponse.newBuilder()
          .setStatus(replyStatus)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}