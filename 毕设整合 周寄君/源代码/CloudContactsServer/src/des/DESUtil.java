package des;

import java.security.Key;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtil {
	
	public static final String KEY_ALGORITHM = "DES";
	
	public static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";
	
	public static final String key = "8bcd401fe928be1bd";
	
	private static SecretKey keyGenerator(String keyStr) throws Exception{
		byte input[] = HexString2Bytes(keyStr);
		DESKeySpec desKey = new DESKeySpec(input);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		return securekey;
	}
	private static int parse(char c){
		if (c>='a') return (c-'a'+10) & 0x0f;
		if (c>='A') return (c-'A'+10) & 0x0f;
		return (c-'0') & 0x0f;
	}
	
	public static byte[] HexString2Bytes(String hexStr){
		byte[] byteArray = new byte[hexStr.length()/2];
		int j = 0;
		for(int i=0;i<byteArray.length;i++){
			char c0 = hexStr.charAt(j++);
			char c1 = hexStr.charAt(j++);
			byteArray[i] = (byte) (parse(c0)<<4 | parse(c1));
		}
		return byteArray;
	}
	
	public static String encrypt(String data,String key){
		data = makeLength8k(data);
		try{
			Key desKey = keyGenerator(key);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			SecureRandom random = new SecureRandom();
			cipher.init(Cipher.ENCRYPT_MODE, desKey, random);
			byte[] result = cipher.doFinal(data.getBytes());
			Base64 base64 = new Base64();
			return new String(base64.encode(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String data,String key){
		try{
			Key desKey = keyGenerator(key);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			Base64 base64 = new Base64();
			String result = new String(cipher.doFinal(base64.decode(data.getBytes())));
			return result.trim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static String makeLength8k(String source){
		if (source.length() % 8!= 0){
			int len = (8 - source.length() % 8);
			for(int i = 0;i<len;i++){
				source += " ";
			}
		}
		return source;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new java.util.Date().getTime());
		String source = "{\"code\":\"0\",\"data\":\"exit\",\"time\":1456906532739}";
		//source = HttpEncoder.encode(source)
		if (source.length() % 8!= 0){
			int len = (8 - source.length() % 8);
			for(int i = 0;i<len;i++){
				source += " ";
			}
		}
		//8位16进制数的key
		String key = "8bcd401fe928be1bd";
		String encryptData = encrypt(source, key);
		//String url_encryptData = URLEncoder.encode(encryptData);
		System.out.println(encryptData);
		//encryptData = URLDecoder.decode(url_encryptData); 
		String decryptData = decrypt(encryptData, key);
		System.out.println(decryptData);
	}
}
