package pt.ulisboa.tecnico.meic.sirs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
    public final static String cipherMode = "RSA/ECB/PKCS1Padding";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keys = keyGen.generateKeyPair();
        return keys;
    }

    // we use the public key as the identifier of the private key on the keystore
    public static void savePrivKeyToKeystore(PrivateKey privKey, PublicKey pubKey) {

    }

    public static void loadPrivKeyFromKeystore() {

    }

    public static byte[] encrypt(Key key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherMode);
    	cipher.init(Cipher.ENCRYPT_MODE, key);
    	return cipher.doFinal(data);
    }

    public static byte[] decrypt(Key key, byte[] cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherMode);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] original = cipher.doFinal(cipherText);
        return original;
    }

    public static byte[] toBytes(Key key) {
        return key.getEncoded();
    }

    public static Key toKey(byte[] key) {
        return new SecretKeySpec(key, "RSA");
    }

    public static void writeKeyToFile(Key key, String keyPath) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(keyPath)) {
            fos.write(toBytes(key));
        }
    }

    public static Key readKeyFromFile(String keyPath) throws IOException {
        byte[] encoded;
        try (FileInputStream fis = new FileInputStream(keyPath)) {
            encoded = new byte[fis.available()];
            fis.read(encoded);
        }
        return toKey(encoded);
    }

    public static void generateAndSaveKeyPair(String keyPathPriv, String keyPathPub) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        KeyPair keys = generateKeyPair();
        writeKeyToFile(keys.getPrivate(), keyPathPriv);
        writeKeyToFile(keys.getPublic(), keyPathPub);
    }
}
