package pt.ulisboa.tecnico.meic.sec.pas.client;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.Announcement;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingReply;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PostMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PostRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PostResponse;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.ReadMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.ReadRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.ReadResponse;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class PasClientApp {

	private PasServiceGrpc.PasServiceBlockingStub clientStub;
	private Key privKey;
	private Key pubKey;
	private Key serverPubKey;

	public PasClientApp(Key privKey, Key pubKey, Key serverPubKey) {
		this.privKey = privKey;
		this.pubKey = pubKey;
		this.serverPubKey = serverPubKey;
	}

	public Key getPrivKey() {
		return this.privKey;
	}

	public void setPrivKey(Key privKey) {
		this.privKey = privKey;
	}

	public Key getPubKey() {
		return this.pubKey;
	}

	public void setPubKey(Key pubKey) {
		this.pubKey = pubKey;
	}

	public Key getServerPubKey() {
		return this.serverPubKey;
	}

	public void setServerPubKey(Key serverPubKey) {
		this.serverPubKey = serverPubKey;
	}

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

	public int register(String name, String password) throws Exception {

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

	public int post(String password, String message, List<Long> references) throws Exception {

		// PostMessage contains data and a signature.
		PostMessage.Builder builder = PostMessage.newBuilder();

		// fill the data field of PostMessage
		long epoch = System.currentTimeMillis() / 1000;

		// cut message to length
 		if (message != null && message.length() > 255) {
			message = message.substring(0, 255);
        }

        // check if references field is null
        if (references == null) {
            references = new ArrayList<Long>();
        }

		PostMessage.Data data = PostMessage.Data.newBuilder()
			.setMessage(message)
			.addAllReferences(references)
			.setPassword(password)
			.setEpoch(epoch)
			.build();

		// create a signature of the data and insert into PostMessage
		byte[] dataBytes = DataUtils.objToBytes(data);
		byte[] signature = RSA.sign(dataBytes, privKey);

		PostMessage postMessage = builder
			.setData(data)
			.setSignature(ByteString.copyFrom(signature))
			.build();

		// PostMessage can now be encrypted with AES
		Key aesKey = AES.generateKey();
		byte[] messageBytes = DataUtils.objToBytes(postMessage);
		byte[] encryptedMessageBytes = AES.encrypt(aesKey, messageBytes);

		// the AES key is encrypted with the clients private key
		byte[] aesKeyBytes = AES.toBytes(aesKey);
		byte[] aesKeyBytes_rsa = RSA.encrypt(serverPubKey, aesKeyBytes);

		// the final message contains the encrypted PostMessage, encrypted AES key
		// and user public key
		byte[] encoded = pubKey.getEncoded();
		PostRequest postRequest = PostRequest.newBuilder()
			.setEncryptedMessage(ByteString.copyFrom(encryptedMessageBytes))
			.setEncryptedAESKey(ByteString.copyFrom(aesKeyBytes_rsa))
			.setPublicKeyBytes(ByteString.copyFrom(encoded)).build();

		// the return value is a http status code (200 if everything is fine)
		PostResponse postResponse = clientStub.post(postRequest);
		return postResponse.getStatus();
	}

	public List<Announcement> read(Key announcementKey, int number) throws Exception {

		// ReadMessage contains data and a signature.
		ReadMessage.Builder builder = ReadMessage.newBuilder();

		// fill the data field of ReadMessage
		long epoch = System.currentTimeMillis() / 1000;
		byte[] announcementKeyBytes = RSA.toBytes(announcementKey);

		ReadMessage.Data data = ReadMessage.Data.newBuilder()
			.setNumber(number)
			.setAnnouncementKeyBytes(ByteString.copyFrom(announcementKeyBytes))
			.setEpoch(epoch)
			.build();

		// create a signature of the data and insert into ReadMessage
		byte[] dataBytes = DataUtils.objToBytes(data);
		byte[] signature = RSA.sign(dataBytes, privKey);

		ReadMessage readMessage = builder.setData(data).setSignature(ByteString.copyFrom(signature)).build();

		// ReadMessage can now be encrypted with AES
		Key aesKey = AES.generateKey();
		byte[] messageBytes = DataUtils.objToBytes(readMessage);
		byte[] encryptedMessageBytes = AES.encrypt(aesKey, messageBytes);

		// the AES key is encrypted with the clients private key
		byte[] aesKeyBytes = AES.toBytes(aesKey);
		byte[] aesKeyBytes_rsa = RSA.encrypt(serverPubKey, aesKeyBytes);

		// the final message contains the encrypted ReadMessage, encrypted AES key
		// and user public key
		byte[] encoded = pubKey.getEncoded();
		ReadRequest readRequest = ReadRequest.newBuilder()
				.setEncryptedMessage(ByteString.copyFrom(encryptedMessageBytes))
				.setEncryptedAESKey(ByteString.copyFrom(aesKeyBytes_rsa))
				.setPublicKeyBytes(ByteString.copyFrom(encoded)).build();

		// the return value is a http status code (200 if everything is fine)
		ReadResponse readResponse = clientStub.read(readRequest);
		List<Announcement> list = readResponse.getPostsList();
		return list;
	}

}
