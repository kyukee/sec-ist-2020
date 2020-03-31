package pt.ulisboa.tecnico.meic.sirs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
    public final static String cipherMode = "RSA/ECB/PKCS1Padding";

    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        KeyPair keys = null;

        try {
            keyGen = KeyPairGenerator.getInstance(cipherMode);
            keyGen.initialize(1024);
            keys = keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keys;
    }

    // we use the public key as the identifier of the private key on the keystore
    public static void savePrivKeyToKeystore(PrivateKey privKey, PublicKey pubKey) {
        // TODO
    }

    public static void loadPrivKeyFromKeystore() {
        // TODO
    }

    public static byte[] encrypt(Key key, byte[] data) {
        Cipher cipher;
        byte[] cipherText = null;
		try {
			cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
    }

    public static byte[] decrypt(Key key, byte[] cipherText) {
    	Cipher cipher;
        byte[] original = null;
		try {
			cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.DECRYPT_MODE, key);
			original = cipher.doFinal(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return original;
    }

    public static byte[] toBytes(Key key) {
        return key.getEncoded();
    }

    public static Key toKey(byte[] key) {
        return new SecretKeySpec(key, "RSA");
    }

    public static void writeKeyToFile(Key key, String keyPath) {
        try (FileOutputStream fos = new FileOutputStream(keyPath)) {
            fos.write(toBytes(key));
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static Key readKeyFromFile(String keyPath) {
        byte[] encoded = null;
        try (FileInputStream fis = new FileInputStream(keyPath)) {
            encoded = new byte[fis.available()];
            fis.read(encoded);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return toKey(encoded);
    }

    public static void generateAndSaveKeyPair(String keyPathPriv, String keyPathPub) {
        KeyPair keys = generateKeyPair();
        writeKeyToFile(keys.getPrivate(), keyPathPriv);
        writeKeyToFile(keys.getPublic(), keyPathPub);
    }
}
