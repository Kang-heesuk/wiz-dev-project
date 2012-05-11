package com.wiz.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.wiz.Seed.BASE64Decoder;
import com.wiz.Seed.BASE64Encoder;

public class WizSafeUtil {
	
	//��Ʈ�� ���� ���ڰ����� üũ
	public static boolean isNumeric(String s)
	{
		if(s == null || s.length() == 0) return false;

		for(int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);

			if(ch < '0' || ch > '9') return false;
		}

		return true;
	}
	//����ȣ ����� �´��� Ȯ��
	public static boolean isPhoneNum(String s)
	{
		//���ڰ� �ƴ� ���
		if(!isNumeric(s)){
			return false;
		}
		//01�� �������� �ʴ� ���
		if(!s.startsWith("01")){
			return false;
		}
		//010 or 011 or 016 or 017 or 018 or 019 �� �ƴ� ���
		if(!s.startsWith("010") && !s.startsWith("011") && 
		   !s.startsWith("016") && !s.startsWith("017") &&
		   !s.startsWith("018") && !s.startsWith("019")){
			return false;
		}
		//�ڸ����� 10 ~ 11�ڸ��� �ƴ� ���
		if(s.length() < 10 || s.length() > 11){		
			return false;
		}
		
		return true;
	}
	
	//����ȣ�� '-' �߰�
	public static String setPhoneNum(String num)
	{
		if(num == null) return "";

		num = num.trim();

		if(num.length() < 9) return num;

		StringBuffer buf = new StringBuffer();

		int pos1 = 0;

		if(num.startsWith("02"))
		{
			buf.append(num.substring(0, 2));
			pos1 = 2;
		}
		else
		{
			buf.append(num.substring(0, 3));
			pos1 = 3;
		}

		buf.append("-");

		int pos2 = num.length() - 4;

		buf.append(num.substring(pos1, pos2)).append("-").append(num.substring(pos2));

		return buf.toString();
	}
	
	//char ����
	public static String replaceChar(String str, char c1, String s)
	{
		if(str == null || str.indexOf(c1) == -1) return str;

		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < str.length(); i++)
		{
			char c2 = str.charAt(i);

			if(c1 != c2)
				buf.append(c2);
			else
				buf.append(s);
		}

		return buf.toString();
	}
	
	public static String dayConvertFromNumberToString(String num)
	{
		String returnVal = "";
		
		if("0".equals(num)){
			returnVal = "��";
		}else if("1".equals(num)){
			returnVal = "��";
		}else if("2".equals(num)){
			returnVal = "ȭ";
		}else if("3".equals(num)){
			returnVal = "��";
		}else if("4".equals(num)){
			returnVal = "��";
		}else if("5".equals(num)){
			returnVal = "��";
		}else if("6".equals(num)){
			returnVal = "��";
		}
		return returnVal;
	}
	
	public static String timeConvertFromNumberToString0to23(String num)
	{
		String returnVal = "";
		
		if("00".equals(num)){
			returnVal = "����0��";
		}else if("01".equals(num)){
			returnVal = "����1��";
		}else if("02".equals(num)){
			returnVal = "����2��";
		}else if("03".equals(num)){
			returnVal = "����3��";
		}else if("04".equals(num)){
			returnVal = "����4��";
		}else if("05".equals(num)){
			returnVal = "����5��";
		}else if("06".equals(num)){
			returnVal = "����6��";
		}else if("07".equals(num)){
			returnVal = "����7��";
		}else if("08".equals(num)){
			returnVal = "����8��";
		}else if("09".equals(num)){
			returnVal = "����9��";
		}else if("10".equals(num)){
			returnVal = "����10��";
		}else if("11".equals(num)){
			returnVal = "����11��";
		}else if("12".equals(num)){
			returnVal = "����12��";
		}else if("13".equals(num)){
			returnVal = "����1��";
		}else if("14".equals(num)){
			returnVal = "����2��";
		}else if("15".equals(num)){
			returnVal = "����3��";
		}else if("16".equals(num)){
			returnVal = "����4��";
		}else if("17".equals(num)){
			returnVal = "����5��";
		}else if("18".equals(num)){
			returnVal = "����6��";
		}else if("19".equals(num)){
			returnVal = "����7��";
		}else if("20".equals(num)){
			returnVal = "����8��";
		}else if("21".equals(num)){
			returnVal = "����9��";
		}else if("22".equals(num)){
			returnVal = "����10��";
		}else if("23".equals(num)){
			returnVal = "����11��";
		}
		return returnVal;
	}
	
	public static String timeConvertFromNumberToString1to24(String num)
	{
		String returnVal = "";
		
		if("00".equals(num)){
			returnVal = "����12��";
		}else if("01".equals(num)){
			returnVal = "����1��";
		}else if("02".equals(num)){
			returnVal = "����2��";
		}else if("03".equals(num)){
			returnVal = "����3��";
		}else if("04".equals(num)){
			returnVal = "����4��";
		}else if("05".equals(num)){
			returnVal = "����5��";
		}else if("06".equals(num)){
			returnVal = "����6��";
		}else if("07".equals(num)){
			returnVal = "����7��";
		}else if("08".equals(num)){
			returnVal = "����8��";
		}else if("09".equals(num)){
			returnVal = "����9��";
		}else if("10".equals(num)){
			returnVal = "����10��";
		}else if("11".equals(num)){
			returnVal = "����11��";
		}else if("12".equals(num)){
			returnVal = "����12��";
		}else if("13".equals(num)){
			returnVal = "����1��";
		}else if("14".equals(num)){
			returnVal = "����2��";
		}else if("15".equals(num)){
			returnVal = "����3��";
		}else if("16".equals(num)){
			returnVal = "����4��";
		}else if("17".equals(num)){
			returnVal = "����5��";
		}else if("18".equals(num)){
			returnVal = "����6��";
		}else if("19".equals(num)){
			returnVal = "����7��";
		}else if("20".equals(num)){
			returnVal = "����8��";
		}else if("21".equals(num)){
			returnVal = "����9��";
		}else if("22".equals(num)){
			returnVal = "����10��";
		}else if("23".equals(num)){
			returnVal = "����11��";
		}
		return returnVal;
	}
	
	public static String intervalConvertMinToHour(String num)
	{
		String returnVal = "";
		int tempMin;
		int tempMin2;
		if(num != null){
			tempMin = Integer.parseInt(num);
			tempMin2 = tempMin / 60;
			if(tempMin2 <= 0){
				tempMin2 = tempMin;
			}
			returnVal = Integer.toString(tempMin2);
		}else{
			returnVal = "Null";
		}
		return returnVal;
	}
	
	//replace �� char ������ �ƴ� string ������ ����
	public static String replaceStr(String org, String var, String tgt)
			throws Exception {
		StringBuffer str = new StringBuffer("");
		int end = 0;
		int begin = 0;
		if (org == null || org.equals(""))
			return org;

		try {
			while (true) {
				end = org.indexOf(var, begin);
				if (end == -1) {
					end = org.length();
					str.append(org.substring(begin, end));
					break;
				}
				str.append(org.substring(begin, end) + tgt);
				begin = end + var.length();
			}
		} catch (Exception e) {
			throw new Exception(e.toString());
		}
		return str.toString();
	}
	
	
	//�ȵ���̵� ����ȣ�� +82xxxxxxx => 010xxxxxxxx���·� �ٲ�
	public static String androidParseCtn(String num, boolean cutNationNum) {
		String ctn = "";

		if (num != null) {
			// ������ȣ ���ð��� false or �ڸ����� 9�ڸ� �̸� �̸� �״�� return�Ѵ�.
			if (cutNationNum == false || num.length() < 12) {
				ctn = num;
			} else {
				// ������ȣ ���ð��� true �̸�
				if (num.startsWith("+82")) {
					// �ѱ�ctn ���(������ȣ : +82)
					try {
						ctn = replaceStr(num, "+82", "0");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// �ѱ�ctn �ƴ� ��� -> ���� ����. �ϴ��� �״�� ����
					ctn = num;
				}
			}
		}

		return ctn;
	}
	
	/**
	  * ����Ʈ�Ƚ��ڳ� ���񽺿� ���Ͽ� �������� �Ϸ�� �ܸ������� �ƴ��� �Ǻ��Ѵ�. 
	  * In-Context, Out-boolean
	  * 
	  * @param  �ش� ��Ƽ��Ƽ�� ������ Context
	  * @return  boolean
	  */
	public static boolean isAuthOkUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
    	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
    	String tempString = LocalSave.getString("isAuthOkUser", "02");
    	//01 = �����Ϸ�Ȼ��, �׿� ������Ϸ�    	
    	if("01".equals(tempString)){
    		returnVal = true;
    	}else{
    		returnVal = false;
    	}
		return returnVal;
	}
	
	
	/**
	  * ����Ʈ �Ƚ��ڳ� ���񽺾����� ��ġ�� ���� �޴��� ����ȣ ���� 
	  * In-Context, Out-String
	  * 
	  * @param  �ش� ��Ƽ��Ƽ�� ������ Context
	  * @return  String
	  */
	public static String getCtn(Context context) {
		String returnVal = "";
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		returnVal = WizSafeUtil.androidParseCtn(telManager.getLine1Number(), true);
		return returnVal;
	}
	
	/**
	  * DB�� insert �Ҷ� ��¥�� �����Ѵ�. �� �� ������ ����ϵ��� �Ѵ�. 
	  * In-Context, Out-String
	  * 
	  * @param  �ش� ��Ƽ��Ƽ�� ������ Context
	  * @return  String
	  */
	public static String getInsertDbDate() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		Date current = new Date();
		String date = formater.format(current);
		Calendar cal = new GregorianCalendar();
	
		int mHour = cal.get(Calendar.HOUR_OF_DAY);
		String tempHour = Integer.toString(mHour);
		if(mHour < 10){
			tempHour = "0" + tempHour;
		}
		
		int mMinute = cal.get(Calendar.MINUTE);
		String tempMinute = Integer.toString(mMinute);
		if(mMinute < 10){
			tempMinute = "0" + tempMinute;
		}
		
		int mSecond = cal.get(Calendar.SECOND);
		String tempSecond = Integer.toString(mSecond);
		if(mSecond < 10){
			tempSecond = "0" + tempSecond;
		}
		String nowDate = date + tempHour + tempMinute + tempSecond;
		return nowDate;
	}
	
	
	/**
	  * ����Ʈ �Ƚ��ڳ� ���� �������� ��ġ���� ����� ����ϴ»��(��ġ���� ����X�λ��)�� �Ǻ� 
	  * In-Context, Out-boolean
	  * 
	  * @param  �ش� ��Ƽ��Ƽ�� ������ Context
	  * @return  boolean
	  */
	public static boolean isHiddenOnUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
	   	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
	   	String tempString = LocalSave.getString("isHiddenOnUser", "1");
	   	//01 = �����Ϸ�Ȼ��, �׿� ������Ϸ�    	
	   	if("0".equals(tempString)){
	   		returnVal = true;
	   	}else{
	   		returnVal = false;
	   	}
		return returnVal;
	}
	
	/**
	  * [yyyyMMdd24hmiss] �� ������ [MM�� dd �� ����(����) hh�� mi��] ���� ���� �ϴ� �Լ�
	  * In-String, Out-String
	  * 
	  * @param  yyyyMMdd24hmiss ����� String
	  * @return  String
	  */
	public static String getDateFormat(String orgDate) {
		String returnVal = "";
		try{
			if(orgDate.length() == 14){
			
				String yyyy = Integer.parseInt(orgDate.substring(0, 4)) + "�� ";
	    		String mm = Integer.parseInt(orgDate.substring(4, 6)) + "�� ";
	    		String dd = Integer.parseInt(orgDate.substring(6, 8)) + "�� ";
	    		String hour24 = orgDate.substring(8, 10);
	    		String AMPM_hour = "";
	    		if(Integer.parseInt(hour24) > 12){
	    			AMPM_hour = "����" + (Integer.parseInt(hour24) - 12) + "�� ";
	    		}else{
	    			AMPM_hour = "����" + Integer.parseInt(hour24) + "�� ";
	    		}
	    		hour24 += "�� ";
	    		String min = Integer.parseInt(orgDate.substring(10, 12)) + "�� ";
	    		String sec = orgDate.substring(12, 14) + "��";
				
				//�ʿ��� ����� ����
				returnVal = mm + dd + AMPM_hour + min;
			}else if(orgDate.length() == 10){
				
				String yyyy = Integer.parseInt(orgDate.substring(0, 4)) + "�� ";
	    		String mm = Integer.parseInt(orgDate.substring(4, 6)) + "�� ";
	    		String dd = Integer.parseInt(orgDate.substring(6, 8)) + "�� ";
	    		String hour24 = orgDate.substring(8, 10);
	    		String AMPM_hour = "";
	    		if(Integer.parseInt(hour24) > 12){
	    			AMPM_hour = "����" + (Integer.parseInt(hour24) - 12) + "�� ";
	    		}else{
	    			AMPM_hour = "����" + Integer.parseInt(hour24) + "�� ";
	    		}
	    		hour24 += "�� ";
	    		
				//�ʿ��� ����� ����
				returnVal = mm + dd + AMPM_hour;
			}else if(orgDate.length() == 8){
				
				String yyyy = Integer.parseInt(orgDate.substring(0, 4)) + "�� ";
	    		String mm = Integer.parseInt(orgDate.substring(4, 6)) + "�� ";
	    		String dd = Integer.parseInt(orgDate.substring(6, 8)) + "�� ";
	    		
				//�ʿ��� ����� ����
				returnVal = yyyy + mm + dd;
			}else if(orgDate.length() == 4){
				
	    		String mm = orgDate.substring(0, 2);
	    		String dd = orgDate.substring(2, 4);
	    		
				//�ʿ��� ����� ����
				returnVal = mm +"/"+ dd;
			}
		}catch(Exception e){
			returnVal = "Ȯ�κҰ�";
		}
		return returnVal;
	}
	
	
	/**
	  * ���� ��Ʈ��ũ ���¿� ���� ��ġ���� ���� ���� �����ϴ� �Լ�
	  * In-String, Out-String
	  * 
	  * @param  yyyyMMdd24hmiss ����� String
	  * @return  String
	  */
	public static String getGapValue(String networkState) {
		String returnVal = "";
		
		if(networkState != null){
			
			if("GPS".equals(networkState)){
				returnVal = "50m �̳�";
			}else if("WIFI".equals(networkState)){
				returnVal = "200m �̳�";
			}else if("3G".equals(networkState)){
				returnVal = "1km �̳�";
			}else if("4G".equals(networkState)){
				returnVal = "1km �̳�";
			}else{
				returnVal = "�˼�����";
			}
			
		}
		return returnVal;
	}
	
	
	/**
	  * Base64Encoding ������� ����Ʈ �迭�� �ƽ�Ű ���ڿ��� ���ڵ��Ѵ�. 
	  * In-Binary, Out-�ƽ�Ű���ڿ�
	  * 
	  * @param  ���ڵ��� ����Ʈ�迭 ���ڿ�(byte[])
	  * @return  ���ڵ��� �ƽ�Ű���ڿ� �迭(String)
	  */
	public static String base64encode(byte[] encodeBytes) {
		byte[] buf = null;
		String strResult = null;

		BASE64Encoder base64Encoder = new BASE64Encoder();
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(
				encodeBytes);
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

		try {
			base64Encoder.encodeBuffer(bin, bout);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		buf = bout.toByteArray();
		strResult = new String(buf).trim();
		return strResult;
	}

	/**
	  * Base64Decoding ������� �ƽ�Ű ���ڿ��� ����Ʈ �迭�� ���ڵ��Ѵ�. 
	  * In-Ascii, Out-Binany
	  * 
	  * @param  strDecode ���ڵ��� �ƽ�Ű ���ڿ�(String)
	  * @return  ���ڵ��� ����Ʈ �迭(byte[])
	  */
	public static byte[] base64decode(String strDecode) {
		byte[] buf = null;

		BASE64Decoder base64Decoder = new BASE64Decoder();
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(
				strDecode.getBytes());
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

		try {
			base64Decoder.decodeBuffer(bin, bout);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		buf = bout.toByteArray();
		return buf;
	}
	
	
	//�ش� �ܸ��� ������ ������ ��ġ������ ������ �������� �ƴ��� �Ǵ��ϴ� �޼ҵ�
	public static boolean isSendLocationUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
    	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
    	String tempString = LocalSave.getString("isSendLocationUser", "2");
    	//1 = �����̼� ������ ������ ����ϴ� ����    	
    	if("1".equals(tempString)){
    		returnVal = true;
    	}else{
    		returnVal = false;
    	}
		return returnVal;
	}
	
	//�ش� �ܸ��� ������ ������ ��ġ������ ������ ���� �������ִ� �޼ҵ�
	//���ù���� �ٲپ��ִ� �κ���
	//1. �θ����ϱ� �޴� : �ڳడ �θ𸮽�Ʈ�� �߰��ϴ� ���� ����
	//2. �ڳ����ϱ� �޴��� ��ϵ� �ڳడ ��ġ ��� �ϴ� ���� ����
	//3. �θ��ڳ������ ���� : ���谡 �����Ǵ� ���� API ����� ���Ͽ� ���� �ڳ����� �ƴ��� �Ǵ��� ����(�θ𸮽�Ʈ, �ڳฮ��Ʈ �α���)
	//�׿� : ������������ ���� �Ͽ��� ���, ���� �ڳ����� �ƴ��� �Ǵ��Ͽ� ����
	public static void setSendLocationUser(Context context, boolean setVal) {
		String tempSetVal = "2";
		if(setVal){
			tempSetVal = "1";
		}else{
			tempSetVal = "2";
		}
		SharedPreferences LocalSave;
		SharedPreferences.Editor edit;
    	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
		edit = LocalSave.edit();
		edit.putString("isSendLocationUser", tempSetVal);
		edit.commit();
	}
	
	
	//���������� ��ġ�� ������ �ð��� ���ú����� �����Ѵ�.
	public static void setLastSendLocation(Context context, String setVal) {
		String tempSetVal = setVal;
		SharedPreferences LocalSave;
		SharedPreferences.Editor edit;
    	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
		edit = LocalSave.edit();
		edit.putString("lastSendLocation", tempSetVal);
		edit.commit();
	}
	
	//���� ��ġ�� �������� ������ �Ǵ��Ѵ�.
	public static boolean isSendLocationPassibleUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
		LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
    	String tempString = LocalSave.getString("lastSendLocation", "0");
    	if(System.currentTimeMillis() - Long.parseLong(tempString) >= (1000 * 60* 5)){
    		returnVal = true;
    	}else{
    		returnVal = false;
    	}
    	return returnVal;
	}
	
}