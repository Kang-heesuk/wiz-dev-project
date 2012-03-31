package test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String text = "부산광역시 금정구 장전동 산 30번지 부산대학교 의과대학 대학원 예방의학전공 123동 123호";
		//String text = "127.12201246666667";
		
		String key = "wizcommunication";
		StringBuilder trace = new StringBuilder();
		
		trace.append("Plain Text :: [").append(text).append("]");
		System.out.println(trace.toString());
		
		SeedCipher seed = new SeedCipher();
		String encryptText = Base64.encode(seed.encrypt(text, key.getBytes(), "UTF-8"));

		trace = new StringBuilder();
		trace.append("Encrypt Text (Base64 Encoding) :: [").append(encryptText).append("]");
		System.out.println(trace.toString());
		
		byte[] encryptbytes = Base64.decode(encryptText);
		String decryptText = seed.decryptAsString(encryptbytes, key.getBytes(), "UTF-8");
		
		trace = new StringBuilder();
		trace.append("Decrypt Text :: [").append(decryptText).append("]");
		System.out.println(trace.toString());
	}


}
