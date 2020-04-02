package pt.ulisboa.tecnico.meic.sec.pas.server;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingReply;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.PingRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterMessage;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.RegisterResponse;
import pt.ulisboa.tecnico.meic.sec.pas.server.domain.Database;
import pt.ulisboa.tecnico.meic.sirs.AES;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc.*;

import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.HashMap;
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
        String serverKeystore = "/server-keystore.jks";
        return RSA.getKeyPairFromKeyStore("server1", serverKeystore, "password", "password");
    }

    private boolean messageIsValid(long requestEpoch, byte[] dataBytes, byte[] receivedDigest, Key clientPubKey, int nonce) {
        
        // check how long ago the message was sent
        int maxDelaySeconds = 120;
        long currentEpoch = System.currentTimeMillis() / 1000;

        if (currentEpoch - requestEpoch > maxDelaySeconds) {
            return false;
        }
        
        // compare received digest to the calculated one
        byte[] digest = DataUtils.digest(dataBytes);
        
        if ( ! MessageDigest.isEqual(digest, receivedDigest)) {
            return false;
        }    

        // check if the aes key has been used before (the aes hash functions as a nonce)
        int lastNonce = usedNonces.getOrDefault(clientPubKey, 0);

        if (nonce == lastNonce) {
            return false;
        }

        return true;
    }

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
        // TODO cleartext password && what about multiple servers?
        KeyPair keys = getServerKeys();
        Key privKey = keys.getPrivate();
        
        // unencrypt aes key
        byte[] encryptedAESKey_pass1 = RSA.decrypt(privKey, encryptedAESKey);
        byte[] encryptedAESKey_pass2 = RSA.decrypt(clientPubKey, encryptedAESKey_pass1);
        Key aesKey = RSA.toKey(encryptedAESKey_pass2);
        
        // unencrypt message
        byte[] messageBytes = AES.decrypt(aesKey, encryptedMessage);
        RegisterMessage message = (RegisterMessage) DataUtils.bytesToObj(messageBytes);
        
        // get the arguments to perform validity check
        byte[] receivedDigest = message.getDigest().toByteArray();

        RegisterMessage.Data data = message.getData();
        byte[] dataBytes = DataUtils.objToBytes(data);

        long requestEpoch = data.getEpoch();
                
        int aesKeyHash = aesKey.hashCode();

        // if the message is valid, we try to register the new user
        if (messageIsValid(requestEpoch, dataBytes, receivedDigest, clientPubKey, aesKeyHash)) {

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

}