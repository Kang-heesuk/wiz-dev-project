package com.wiz.Seed;

import com.wiz.Seed.SeedCipher;
import com.wiz.util.WizSafeUtil;
import java.io.UnsupportedEncodingException;

public class WizSafeSeed {
	
	static String key = "wizcommunication";
	
	//인코딩 : DB에 들어갈 최종 스트링으로 인코딩한다. SEED + BASE64 => DB에 들어갈값
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
	
	//디코딩 : DB에서 가져온 암호화 된 값을 최종 복호화 된 스트링으로 리턴한다. 
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
