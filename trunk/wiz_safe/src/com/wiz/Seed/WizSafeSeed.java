package com.wiz.Seed;

import com.wiz.Seed.SeedCipher;
import com.wiz.util.WizSafeUtil;
import java.io.UnsupportedEncodingException;

public class WizSafeSeed {
	
	static String key = "wizcommunication";
	
	//���ڵ� : DB�� �� ���� ��Ʈ������ ���ڵ��Ѵ�. SEED + BASE64 => DB�� ����
	public static String seedEnc(String text)
	{
		String returnVal = "";
		String tempText = text;
		byte[] byte_enc = null;
		SeedCipher seed = new SeedCipher();
		try {
			byte_enc = seed.encrypt(tempText, key.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		returnVal = WizSafeUtil.base64encode(byte_enc);
		return returnVal;
	}
	
	//���ڵ� : DB���� ������ ��ȣȭ �� ���� ���� ��ȣȭ �� ��Ʈ������ �����Ѵ�. 
	public static String seedDec(String text)
	{
		String returnVal = "";
		String tempText = text;
		byte[] byte_dec = null;
		SeedCipher seed = new SeedCipher();
		try {
			byte_dec = WizSafeUtil.base64decode(tempText);
			returnVal = seed.decryptAsString(byte_dec, key.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnVal;
	}

}
