package test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String text = "�λ걤���� ������ ������ �� 30���� �λ���б� �ǰ����� ���п� ������������ 123�� 123ȣ";
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
