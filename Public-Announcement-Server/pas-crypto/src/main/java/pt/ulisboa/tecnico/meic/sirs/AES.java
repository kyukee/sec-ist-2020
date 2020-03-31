package pt.ulisboa.tecnico.meic.sirs;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AES {

  // We use ECB because a key is never used to encrypt more then one time
  public final static String cipherMode = "AES/ECB/PKCS5Padding";

  public static Key generateKey() {
    KeyGenerator keyGen;
    Key key = null;

	try {
		keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
	    key = keyGen.generateKey();
	} catch (Exception e) {
		e.printStackTrace();
	}

    return key;
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
    return new SecretKeySpec(key, "AES");
  }
}

