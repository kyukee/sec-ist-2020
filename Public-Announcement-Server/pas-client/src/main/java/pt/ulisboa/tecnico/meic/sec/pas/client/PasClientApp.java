package pt.ulisboa.tecnico.meic.sec.pas.client;

import java.security.Key;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class PasClientApp {
		
	PasServiceGrpc.PasServiceBlockingStub clientStub;
	ManagedChannel clientChannel;

	public PasClientApp(String host, int port) {
		
		// gRPC provides a channel construct which abstracts out the underlying details like connection, connection pooling, load balancing, etc.
		ManagedChannel clientChannel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();		
				
		// The stub is the primary way for clients to interacts with the server.
		// When using auto generate stubs, the stub class will have constructors for wrapping the channel.
		clientStub = PasServiceGrpc.newBlockingStub(clientChannel);
	}
	
	// this terminates the tcp connection to a server
	public void channelShutdown() {
		clientChannel.shutdown();
	}
    
	public int register(Key privKey, Key pubKey, Key serverPubKey, String name, String password) throws Exception {
		
		// RegisterMessage contains data and a digest.
		RegisterMessage.Builder builder = RegisterMessage.newBuilder();
		
		// fill the data field of RegisterMessage
		byte[] encoded = pubKey.getEncoded();
		RegisterMessage.Data data = RegisterMessage.Data.newBuilder()
			.setPublicKeyBytes(ByteString.copyFrom(encoded))
			.setName(name)
			.setPassword(password)
			.build();
		
		// create a digest of the data and insert into RegisterMessage
		byte[] dataBytes = DataUtils.objToBytes(data);
		byte[] digest = DataUtils.digest(dataBytes);
		
		RegisterMessage registerMessage = builder
			.setData(data)
			.setDigest(ByteString.copyFrom(digest))
			.build();
		
		// RegisterMessage can now be encrypted with AES
		Key aesKey = AES.generateKey();
		byte[] messageBytes = DataUtils.objToBytes(registerMessage);
		byte[] encryptedMessageBytes = AES.encrypt(aesKey, messageBytes);
		
		// the AES key is encrypted with RSA keys
		// first with client's private key and then the server's public key
		byte[] aesKeyBytes = AES.toBytes(aesKey);
		byte[] aesKeyBytes_rsaPass1 = RSA.encrypt(privKey, aesKeyBytes);
		byte[] aesKeyBytes_rsaPass2 = RSA.encrypt(serverPubKey, aesKeyBytes_rsaPass1);
		
		// the final message contains the encrypted RegisterMessage and encrypted AES key
		RegisterRequest registerRequest = RegisterRequest.newBuilder()
			.setEncryptedMessage(ByteString.copyFrom(encryptedMessageBytes))
			.setEncryptedAESKey(ByteString.copyFrom(aesKeyBytes_rsaPass2))
			.build();
		
		// the return value is a http status code (200 if everything is fine)
		RegisterResponse registerResponse = clientStub.register(registerRequest);
		return registerResponse.getStatus();
	}
	
    public static void main(String[] args) {
//    	// receive and print arguments
//		System.out.printf("Received %d arguments%n", args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.printf("arg[%d] = %s%n", i, args[i]);
//		}
//
//		// check arguments
//		if (args.length < 2) {
//			System.err.println("Argument(s) missing!");
//			System.err.printf("Usage: java %s host port%n", PasClientApp.class.getName());
//			return;
//		}
//
//		// host and port
//		final String host = args[0];
//		final int port = Integer.parseInt(args[1]);
//    	
//		// gRPC provides a channel construct which abstracts out the underlying details like connection, connection pooling, load balancing, etc.
//        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
//          .usePlaintext()
//          .build();
// 
//        // The stub is the primary way for clients to interacts with the server.
//        // When using auto generate stubs, the stub class will have constructors for wrapping the channel.
//        PasServiceGrpc.PasServiceBlockingStub stub1 = PasServiceGrpc.newBlockingStub(channel);
// 
//        RegisterRequest registerRequest = RegisterRequest.newBuilder()
////      .setFirstName("John")
//        	.build();
//        
//        RegisterRequest request2 = registerRequest.getDefaultInstance();
//        
//        RegisterResponse helloResponse = stub1.register(registerRequest);
// 
//        channel.shutdown();
    }
    
    
    
}
