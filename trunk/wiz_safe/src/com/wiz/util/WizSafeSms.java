package com.wiz.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

import com.wiz.Seed.WizSafeSeed;

public class WizSafeSms {
	// sms ���� ������ �������� �Ǻ�
	public static boolean stateSmsReceive(String ctn, Context context) {
		String decCtn = "";
		if(ctn == null || "".equals(ctn)){
			return false;
		}else{			
			if(!WizSafeUtil.isNumeric(ctn)){
				//�Ѿ�� ctn �� ���ڰ� �ƴϸ� ��ȣȭ���ϼ� �����Ƿ�
				decCtn = WizSafeSeed.seedDec(ctn);
			}else{
				//�Ѿ�� ctn �� ���ڸ� �ٷ� ��´�.
				decCtn = ctn;
			}

			if(decCtn.length() < 10){
				//ctn �ּ� �ڸ����� 10�ڸ��� �ȵǸ�
				return false;
			}
		}

		boolean result = false;
		//���� ����� �ִ� sms ���� ���� ���θ� �Ǻ�
		SharedPreferences LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
		String stateSmsRecv = LocalSave.getString("stateSmsRecv","0");
		if("1".equals(stateSmsRecv)){
			result = true;
		}
		
		return result;
	}


	//sms �� http url ��� ������� �߼�
	public static boolean sendSmsMsg(String ctn, String msg) {
		
		String decCtn = "";
		if(ctn == null || "".equals(ctn)){
			return false;
		}else{			
			if(!WizSafeUtil.isNumeric(ctn)){
				//�Ѿ�� ctn �� ���ڰ� �ƴϸ� ��ȣȭ���ϼ� �����Ƿ�
				decCtn = WizSafeSeed.seedDec(ctn);
			}else{
				//�Ѿ�� ctn �� ���ڸ� �ٷ� ��´�.
				decCtn = ctn;
			}

			if(decCtn.length() < 10){
				//ctn �ּ� �ڸ����� 10�ڸ��� �ȵǸ�
				return false;
			}
		}

		boolean result = false;

		try{
			
			String url = "http://infra.bellpang.com/sendsms/sendsms.asp?ctn=" + decCtn + "&msg=" + java.net.URLEncoder.encode(msg, "euc-kr");
			HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "euc-kr"));	

			String temp;
			StringBuffer returnMsg = new StringBuffer();
			while((temp = br.readLine()) != null)
			{				
				returnMsg.append(temp.trim());
			}
			
			br.close();
			
			if("0".equals(returnMsg.toString())){
				result = true;
			}
			
		}catch(Exception e){
		}
		
		return result;
	}	
}
