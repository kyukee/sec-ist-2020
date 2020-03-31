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

		// RegisterMessage contains data and a digest.
		RegisterMessage.Builder builder = RegisterMessage.newBuilder();

		// fill the data field of RegisterMessage
		byte[] encoded = pubKey.getEncoded();
		RegisterMessage.Data data = RegisterMessage.Data.newBuilder()
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
			.setPublicKeyBytes(ByteString.copyFrom(encoded))
			.build();

		// the return value is a http status code (200 if everything is fine)
		RegisterResponse registerResponse = clientStub.register(registerRequest);
		return registerResponse.getStatus();
	}

}
