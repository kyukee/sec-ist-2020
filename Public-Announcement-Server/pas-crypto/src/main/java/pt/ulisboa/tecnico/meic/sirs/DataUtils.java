package pt.ulisboa.tecnico.meic.sirs;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DataUtils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] objToBytes(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            bytes = bos.toByteArray();
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
        	
            try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
        
        return bytes;
    }

    public static Object bytesToObj(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object obj = null;
        
        try {
            in = new ObjectInputStream(bis);
            obj = in.readObject();
        } catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (in != null) {
	                in.close();
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
        
        return obj;
    }
    
    public static byte[] digest(byte[] bytes) {
    	MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(bytes);
		byte[] digest = md.digest();
		return digest;
    }
    
}
