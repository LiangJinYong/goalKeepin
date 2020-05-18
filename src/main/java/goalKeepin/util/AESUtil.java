package goalKeepin.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	static public String aesEncrypt(String text) throws Exception {
		String key = "";
		byte[] sKey = null;
		byte[] bText = null;
		byte[] encrypted = null;
		String result = "";
		key = "PdSgVkYp3s6v9y$B";
		sKey = key.getBytes("UTF-8");
		bText = text.getBytes("UTF-8");
		// AES/ECB/PKCS5Padding
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sKey, "AES"));
		encrypted = cipher.doFinal(bText);
		result = toHexString(encrypted);
		return result;
	}

	static public String aesDecrypt(String text) throws Exception {
		String key = "";
		byte[] sKey = null;
		byte[] bText = null;
		byte[] decrypted = null;
		String result = "";
		key = "PdSgVkYp3s6v9y$B";
		sKey = key.getBytes("UTF-8");
		bText = hexToByte(text);
		// AES/ECB/PKCS5Padding
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sKey, "AES"));
		decrypted = cipher.doFinal(bText);
		result = new String(decrypted, "UTF-8");
		return result;
	}

	static private String toHexString(byte[] encrypted) {
		if (encrypted == null || encrypted.length == 0) {
			return null;

		}
		StringBuffer sb = new StringBuffer(encrypted.length * 2);
		String hexNumber;
		for (int x = 0; x < encrypted.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & encrypted[x]);
			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}

	static private byte[] hexToByte(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}
		byte[] byteArray = new byte[hex.length() / 2];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return byteArray;
	}

}
