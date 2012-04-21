package com.wiz.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

import com.wiz.Seed.WizSafeSeed;

public class WizSafeSms {
	// sms 수신 가능한 상태인지 판별
	public static boolean stateSmsReceive(String ctn, Context context) {
		String decCtn = "";
		if(ctn == null || "".equals(ctn)){
			return false;
		}else{			
			if(!WizSafeUtil.isNumeric(ctn)){
				//넘어온 ctn 이 숫자가 아니면 복호화값일수 있으므로
				decCtn = WizSafeSeed.seedDec(ctn);
			}else{
				//넘어온 ctn 이 숫자면 바로 담는다.
				decCtn = ctn;
			}

			if(decCtn.length() < 10){
				//ctn 최소 자리수인 10자리가 안되면
				return false;
			}
		}

		boolean result = false;
		//로컬 밸류에 있는 sms 수신 상태 여부를 판별
		SharedPreferences LocalSave = context.getSharedPreferences("WizSafeLocalVal", 0);
		String stateSmsRecv = LocalSave.getString("stateSmsRecv","0");
		if("1".equals(stateSmsRecv)){
			result = true;
		}
		
		return result;
	}


	//sms 를 http url 통신 방식으로 발송
	public static boolean sendSmsMsg(String ctn, String msg) {
		
		String decCtn = "";
		if(ctn == null || "".equals(ctn)){
			return false;
		}else{			
			if(!WizSafeUtil.isNumeric(ctn)){
				//넘어온 ctn 이 숫자가 아니면 복호화값일수 있으므로
				decCtn = WizSafeSeed.seedDec(ctn);
			}else{
				//넘어온 ctn 이 숫자면 바로 담는다.
				decCtn = ctn;
			}

			if(decCtn.length() < 10){
				//ctn 최소 자리수인 10자리가 안되면
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
