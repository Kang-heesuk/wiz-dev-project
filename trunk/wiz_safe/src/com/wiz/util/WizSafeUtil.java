package com.wiz.util;

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
	
	public static String timeConvertFromNumberToString(String num)
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
	
}
