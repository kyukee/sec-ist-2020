package pt.ulisboa.tecnico.meic.sirs;

import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

	// public static void savePrivKeyToKeystore(String alias, String keyPass,
	// PrivateKey privKey, PublicKey pubKey, String storePass) throws Exception{
	//
	// // load keystore
	// KeyStore keyStore = KeyStore.getInstance("JCEKS");
	// try(InputStream ins = RSA.class.getResourceAsStream("/keystore.jks")){
	// keyStore.load(ins, storePass.toCharArray());
	// }
	//
	//
	// // set entry in the keystore
	// KeyPair keys = new KeyPair(pubKey, privKey);
	//
	// // TODO the only missing thing to make this work is the certificate
	// Certificate certChain = null;
	//
	// keyStore.setKeyEntry("alias", privKey, keyPass.toCharArray(), certChain);
	//
	//
	// // get keystore path
	// URL resource = RSA.class.getResource("/keystore.jks");
	// File file = Paths.get(resource.toURI()).toFile();
	// String keystorePath = file.getAbsolutePath();
	//
	// // save keystore
	// try (FileOutputStream keyStoreOutputStream = new
	// FileOutputStream(keystorePath)) {
	// keyStore.store(keyStoreOutputStream, storePass.toCharArray());
	// }
	//
	// }

	public static KeyPair getKeyPairFromKeyStore(String alias, String keyPass, String storePath, String storePass) {

		// load keystore
		KeyStore keyStore = null;

		try (InputStream ins = RSA.class.getResourceAsStream(storePath)) {

			if (ins == null) {
				throw new FileNotFoundException();
			}

			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(ins, storePass.toCharArray());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// get private key
		char[] keyPassword = keyPass.toCharArray();
		PrivateKey privateKey = null;

		try {
			privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get public key
		java.security.cert.Certificate cert = null;
		PublicKey publicKey = null;

		try {
			cert = keyStore.getCertificate(alias);
			publicKey = cert.getPublicKey();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		return new KeyPair(publicKey, privateKey);
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

	public static PublicKey toPublicKey(byte[] encKey) {

		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
		
		KeyFactory keyFactory;
		PublicKey pubKey = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			pubKey = keyFactory.generatePublic(pubKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pubKey;
	}


	public static Key toPrivateKey(byte[] encKey) {
		PKCS8EncodedKeySpec pubKeySpec = new PKCS8EncodedKeySpec(encKey);
		
		KeyFactory keyFactory;
		PublicKey pubKey = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			pubKey = keyFactory.generatePublic(pubKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pubKey;
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
		return new SecretKeySpec(encoded, "RSA");
	}

	public static void generateAndSaveKeyPair(String keyPathPriv, String keyPathPub) {
		KeyPair keys = generateKeyPair();
		writeKeyToFile(keys.getPrivate(), keyPathPriv);
		writeKeyToFile(keys.getPublic(), keyPathPub);
	}

	public static byte[] sign(byte[] data, Key privateKey) {

		byte[] signature = null;

		try {
			Signature privateSignature = Signature.getInstance("SHA256withRSA");
			privateSignature.initSign((PrivateKey) privateKey);
			privateSignature.update(data);
			signature = privateSignature.sign();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return signature;
	}

	public static boolean verify(byte[] data, PublicKey publicKey, byte[] signature) {

		boolean valid = false;

		try {
			Signature publicSignature = Signature.getInstance("SHA256withRSA");
			publicSignature.initVerify(publicKey);
			publicSignature.update(data);

			valid = publicSignature.verify(signature);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return valid;
	}

}
