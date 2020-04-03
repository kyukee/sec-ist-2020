package pt.ulisboa.tecnico.meic.sec.pas.client;

import java.security.Key;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingReply;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class PasClientApp {

	PasServiceGrpc.PasServiceBlockingStub clientStub;

	public static ManagedChannel createChannel(String host, int port) {
		// gRPC provides a channel construct which abstracts out the underlying details like connection, connection pooling, load balancing, etc.
		ManagedChannel clientChannel = ManagedChannelBuilder.forAddress(host, port)
			.usePlaintext()
			.build();

		// don't forget to shutdown channels with channel.shutdown()
		// shutdown terminates the tcp connection to the current server

		return clientChannel;
	}

	public void startConnection(ManagedChannel channel) {

		// The stub is the primary way for clients to interacts with the server.
		// When using auto generate stubs, the stub class will have constructors for wrapping the channel.
		clientStub = PasServiceGrpc.newBlockingStub(channel);
	}

	// ping() returns the same string that was sent to the server
	public String ping(String text) {

	  	PingRequest request = PingRequest.newBuilder()
		  .setMessage(text)
		  .build();

		PingReply response = clientStub.ping(request);
		return response.getMessage();
  }

	public int register(Key privKey, Key pubKey, Key serverPubKey, String name, String password) throws Exception {

		// RegisterMessage contains data and a signature.
		RegisterMessage.Builder builder = RegisterMessage.newBuilder();

		// fill the data field of RegisterMessage
		long epoch = System.currentTimeMillis()/1000;
		RegisterMessage.Data data = RegisterMessage.Data.newBuilder()
			.setName(name)
			.setPassword(password)
			.setEpoch(epoch)
			.build();

		// create a signature of the data and insert into RegisterMessage
		byte[] dataBytes = DataUtils.objToBytes(data);
		byte[] signature = RSA.sign(dataBytes, privKey);

		RegisterMessage registerMessage = builder
			.setData(data)
			.setSignature(ByteString.copyFrom(signature))
			.build();

		// RegisterMessage can now be encrypted with AES
		Key aesKey = AES.generateKey();
		byte[] messageBytes = DataUtils.objToBytes(registerMessage);
		byte[] encryptedMessageBytes = AES.encrypt(aesKey, messageBytes);

		// the AES key is encrypted with the clients private key
		byte[] aesKeyBytes = AES.toBytes(aesKey);
		byte[] aesKeyBytes_rsa = RSA.encrypt(serverPubKey, aesKeyBytes);

		// the final message contains the encrypted RegisterMessage, encrypted AES key and user public key
		byte[] encoded = pubKey.getEncoded();
		RegisterRequest registerRequest = RegisterRequest.newBuilder()
			.setEncryptedMessage(ByteString.copyFrom(encryptedMessageBytes))
			.setEncryptedAESKey(ByteString.copyFrom(aesKeyBytes_rsa))
			.setPublicKeyBytes(ByteString.copyFrom(encoded))
			.build();

		// the return value is a http status code (200 if everything is fine)
		RegisterResponse registerResponse = clientStub.register(registerRequest);
		return registerResponse.getStatus();
	}

}
