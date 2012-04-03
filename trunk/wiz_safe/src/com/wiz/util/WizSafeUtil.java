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
	
	//스트링 값이 숫자값인지 체크
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
	
	//폰번호에 '-' 추가
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
	
	//char 변경
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
			returnVal = "일";
		}else if("1".equals(num)){
			returnVal = "월";
		}else if("2".equals(num)){
			returnVal = "화";
		}else if("3".equals(num)){
			returnVal = "수";
		}else if("4".equals(num)){
			returnVal = "목";
		}else if("5".equals(num)){
			returnVal = "금";
		}else if("6".equals(num)){
			returnVal = "토";
		}
		return returnVal;
	}
	
	public static String timeConvertFromNumberToString(String num)
	{
		String returnVal = "";
		
		if("00".equals(num)){
			returnVal = "오전0시";
		}else if("01".equals(num)){
			returnVal = "오전1시";
		}else if("02".equals(num)){
			returnVal = "오전2시";
		}else if("03".equals(num)){
			returnVal = "오전3시";
		}else if("04".equals(num)){
			returnVal = "오전4시";
		}else if("05".equals(num)){
			returnVal = "오전5시";
		}else if("06".equals(num)){
			returnVal = "오전6시";
		}else if("07".equals(num)){
			returnVal = "오전7시";
		}else if("08".equals(num)){
			returnVal = "오전8시";
		}else if("09".equals(num)){
			returnVal = "오전9시";
		}else if("10".equals(num)){
			returnVal = "오전10시";
		}else if("11".equals(num)){
			returnVal = "오전11시";
		}else if("12".equals(num)){
			returnVal = "오후12시";
		}else if("13".equals(num)){
			returnVal = "오후1시";
		}else if("14".equals(num)){
			returnVal = "오후2시";
		}else if("15".equals(num)){
			returnVal = "오후3시";
		}else if("16".equals(num)){
			returnVal = "오후4시";
		}else if("17".equals(num)){
			returnVal = "오후5시";
		}else if("18".equals(num)){
			returnVal = "오후6시";
		}else if("19".equals(num)){
			returnVal = "오후7시";
		}else if("20".equals(num)){
			returnVal = "오후8시";
		}else if("21".equals(num)){
			returnVal = "오후9시";
		}else if("22".equals(num)){
			returnVal = "오후10시";
		}else if("23".equals(num)){
			returnVal = "오후11시";
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
	
	//replace 를 char 단위가 아닌 string 단위로 수행
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
	
	
	//안드로이드 폰번호를 +82xxxxxxx => 010xxxxxxxx형태로 바꿈
	public static String androidParseCtn(String num, boolean cutNationNum) {
		String ctn = "";

		if (num != null) {
			// 국가번호 컷팅값이 false or 자릿수가 9자리 미만 이면 그대로 return한다.
			if (cutNationNum == false || num.length() < 12) {
				ctn = num;
			} else {
				// 국가번호 컷팅값이 true 이면
				if (num.startsWith("+82")) {
					// 한국ctn 경우(국가번호 : +82)
					try {
						ctn = replaceStr(num, "+82", "0");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// 한국ctn 아닌 경우 -> 차후 개발. 일단은 그대로 리턴
					ctn = num;
				}
			}
		}

		return ctn;
	}
	
	/**
	  * 스마트안심자녀 서비스에 관하여 인증까지 완료된 단말기인지 아닌지 판별한다. 
	  * In-Context, Out-boolean
	  * 
	  * @param  해당 액티비티나 서비스의 Context
	  * @return  boolean
	  */
	public static boolean isAuthOkUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
    	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
    	String tempString = LocalSave.getString("isAuthOkUser", "02");
    	//01 = 인증완료된사람, 그외 인증비완료    	
    	if("01".equals(tempString)){
    		returnVal = true;
    	}else{
    		returnVal = false;
    	}
		return returnVal;
	}
	
	
	/**
	  * 스마트 안심자녀 서비스어플을 설치한 현재 휴대폰 폰번호 추출 
	  * In-Context, Out-String
	  * 
	  * @param  해당 액티비티나 서비스의 Context
	  * @return  String
	  */
	public static String getCtn(Context context) {
		String returnVal = "";
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		returnVal = WizSafeUtil.androidParseCtn(telManager.getLine1Number(), true);
		return returnVal;
	}
	
	/**
	  * DB에 insert 할때 날짜를 셋팅한다. 꼭 이 형식을 사용하도록 한다. 
	  * In-Context, Out-String
	  * 
	  * @param  해당 액티비티나 서비스의 Context
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
	  * 스마트 안심자녀 서비스 설정에서 위치숨김 기능을 사용하는사람(위치정보 제공X인사람)을 판별 
	  * In-Context, Out-boolean
	  * 
	  * @param  해당 액티비티나 서비스의 Context
	  * @return  boolean
	  */
	public static boolean isHiddenOnUser(Context context) {
		boolean returnVal = false;
		SharedPreferences LocalSave;
	   	LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
	   	String tempString = LocalSave.getString("isHiddenOnUser", "1");
	   	//01 = 인증완료된사람, 그외 인증비완료    	
	   	if("0".equals(tempString)){
	   		returnVal = true;
	   	}else{
	   		returnVal = false;
	   	}
		return returnVal;
	}
	
	
	/**
	  * Base64Encoding 방식으로 바이트 배열을 아스키 문자열로 인코딩한다. 
	  * In-Binary, Out-아스키문자열
	  * 
	  * @param  인코딩할 바이트배열 문자열(byte[])
	  * @return  인코딩된 아스키문자열 배열(String)
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
	  * Base64Decoding 방식으로 아스키 문자열을 바이트 배열로 디코딩한다. 
	  * In-Ascii, Out-Binany
	  * 
	  * @param  strDecode 디코딩할 아스키 문자열(String)
	  * @return  디코딩된 바이트 배열(byte[])
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
}
