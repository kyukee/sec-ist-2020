package pt.ulisboa.tecnico.meic.sec.pas.server;

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
import pt.ulisboa.tecnico.meic.sec.pas.server.domain.Announcement;
import pt.ulisboa.tecnico.meic.sec.pas.server.domain.Database;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.stub.StreamObserver;

public class PasServiceImpl extends PasServiceImplBase {

    private Database database;

    // this saves the last nonce (aes key hash) received for every public key
    private Map<Key,Integer> usedNonces = new HashMap<Key,Integer>();

    public PasServiceImpl(Database serverDatabase) {
        database = serverDatabase;
    }

    private KeyPair getServerKeys() {
        
        // TODO cleartext password && what about multiple servers?
        return RSA.getKeyPairFromKeyStore("server1", "password", "/server-keystore.jks", "password");
    }

    private boolean messageIsValid(long requestEpoch, byte[] dataBytes, byte[] receivedSignature, PublicKey clientPubKey, int nonce) {
        
        // check how long ago the message was sent
        int maxDelaySeconds = 120;
        long currentEpoch = System.currentTimeMillis() / 1000;

        if (currentEpoch - requestEpoch > maxDelaySeconds) {
            return false;
        }
        
        // compare received signature to the calculated one
        if ( ! RSA.verify(dataBytes, clientPubKey, receivedSignature)) {
            return false;
        }    

        // check if the aes key has been used before (the aes hash functions as a nonce)
        int lastNonce = usedNonces.getOrDefault(clientPubKey, 0);

        if (nonce == lastNonce) {
            return false;
        }

        return true;
    }

    private Protocol.Announcement convertToDto(Announcement announcement) {
        
        Protocol.Announcement dto = Protocol.Announcement.newBuilder()
			.setMessage(announcement.getMessage())
			.addAllReferences(announcement.getReferences())
            .setEpoch(announcement.getCreationTime())
            .setId(announcement.getId())
            .setUserId(announcement.getUser())
            .build();

        return dto;
    }

    public void ping(PingRequest req, StreamObserver<PingReply> responseObserver) {
        String received = req.getMessage();
        PingReply reply = PingReply.newBuilder().setMessage(received).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        
        System.out.println("Received a register request");

        int replyStatus = 200;
        
        // these are the fields we received
        byte[] encryptedMessage = request.getEncryptedMessage().toByteArray();
        byte[] encryptedAESKey = request.getEncryptedAESKey().toByteArray();
        byte[] publicKeyBytes = request.getPublicKeyBytes().toByteArray();
        PublicKey clientPubKey = RSA.toPublicKey(publicKeyBytes);
        
        // retrieve the server's private key
        KeyPair keys = getServerKeys();
        Key privKey = keys.getPrivate();
        
        // unencrypt aes key
        byte[] unencryptedAESKey = RSA.decrypt(privKey, encryptedAESKey);
        Key aesKey = AES.toKey(unencryptedAESKey);
        
        // unencrypt message
        byte[] messageBytes = AES.decrypt(aesKey, encryptedMessage);
        RegisterMessage message = (RegisterMessage) DataUtils.bytesToObj(messageBytes);
        
        // get the arguments to perform validity check
        byte[] receivedSignature = message.getSignature().toByteArray();

        RegisterMessage.Data data = message.getData();
        byte[] dataBytes = DataUtils.objToBytes(data);

        long requestEpoch = data.getEpoch();
                
        int aesKeyHash = aesKey.hashCode();

        // if the message is valid, we try to register the new user
        if (messageIsValid(requestEpoch, dataBytes, receivedSignature, clientPubKey, aesKeyHash)) {

            String clientName = data.getName();
            String clientPassword = data.getPassword();
            replyStatus = database.register(clientPubKey, clientName, clientPassword);

        } else {
            replyStatus = 400;
        }
        
        RegisterResponse response = RegisterResponse.newBuilder()
          .setStatus(replyStatus)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void post(PostRequest request, StreamObserver<PostResponse> responseObserver) {

        System.out.println("Received a post request");

        int replyStatus = 200;

        // these are the fields we received
        byte[] encryptedMessage = request.getEncryptedMessage().toByteArray();
        byte[] encryptedAESKey = request.getEncryptedAESKey().toByteArray();
        byte[] publicKeyBytes = request.getPublicKeyBytes().toByteArray();
        PublicKey clientPubKey = RSA.toPublicKey(publicKeyBytes);

        // retrieve the server's private key
        KeyPair keys = getServerKeys();
        Key privKey = keys.getPrivate();

        // unencrypt aes key
        byte[] unencryptedAESKey = RSA.decrypt(privKey, encryptedAESKey);
        Key aesKey = AES.toKey(unencryptedAESKey);

        // unencrypt message
        byte[] messageBytes = AES.decrypt(aesKey, encryptedMessage);
        PostMessage message = (PostMessage) DataUtils.bytesToObj(messageBytes);

        // get the arguments to perform validity check
        byte[] receivedSignature = message.getSignature().toByteArray();

        PostMessage.Data data = message.getData();
        byte[] dataBytes = DataUtils.objToBytes(data);

        long requestEpoch = data.getEpoch();

        int aesKeyHash = aesKey.hashCode();

        // if the message is valid, we try to post the new user
        if (messageIsValid(requestEpoch, dataBytes, receivedSignature, clientPubKey, aesKeyHash)) {

            replyStatus = database.post(clientPubKey, data.getMessage(), data.getReferencesList(), data.getEpoch(), data.getPassword());

        } else {
            replyStatus = 400;
        }

        PostResponse response = PostResponse.newBuilder().setStatus(replyStatus).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {

        System.out.println("Received a read request");

        List<Protocol.Announcement> reply = null;

        // these are the fields we received
        byte[] encryptedMessage = request.getEncryptedMessage().toByteArray();
        byte[] encryptedAESKey = request.getEncryptedAESKey().toByteArray();
        byte[] publicKeyBytes = request.getPublicKeyBytes().toByteArray();
        PublicKey clientPubKey = RSA.toPublicKey(publicKeyBytes);

        // retrieve the server's private key
        KeyPair keys = getServerKeys();
        Key privKey = keys.getPrivate();

        // unencrypt aes key
        byte[] unencryptedAESKey = RSA.decrypt(privKey, encryptedAESKey);
        Key aesKey = AES.toKey(unencryptedAESKey);

        // unencrypt message
        byte[] messageBytes = AES.decrypt(aesKey, encryptedMessage);
        ReadMessage message = (ReadMessage) DataUtils.bytesToObj(messageBytes);

        // get the arguments to perform validity check
        byte[] receivedSignature = message.getSignature().toByteArray();

        ReadMessage.Data data = message.getData();
        byte[] dataBytes = DataUtils.objToBytes(data);

        long requestEpoch = data.getEpoch();

        int aesKeyHash = aesKey.hashCode();

        // if the message is valid, we try to read the new user
        if (messageIsValid(requestEpoch, dataBytes, receivedSignature, clientPubKey, aesKeyHash)) {

            int number = data.getNumber();
            byte[] announcementKeyByes = data.getAnnouncementKeyBytes().toByteArray();
            PublicKey announcementKey = RSA.toPublicKey(announcementKeyByes);

            List<Protocol.Announcement> posts = new ArrayList<Protocol.Announcement>();

            for (Announcement post : database.read(announcementKey, number)) {
                posts.add(convertToDto(post));
            }
            
            reply = posts;

        } else {
            reply = null;
        }

        ReadResponse response = ReadResponse.newBuilder()
            .addAllPosts(reply)
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}