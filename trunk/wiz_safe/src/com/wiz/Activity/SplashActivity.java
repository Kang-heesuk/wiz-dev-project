package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class SplashActivity extends Activity {
	
	// 어플 버전 및 앱스토어 확인//////////////////////////////////////////////
	// 01 - 안드로이드 마켓
	// 02 - 티스토어
	// 03 - 올레마켓
	// 04 - 삼성앱스토어
	// 05 - 유플러스앱마켓
	String AppVersion = "1.0";
	String Market = "01";
	// ////////////////////////////////////////////////////////////////////

	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
	//전역변수로써 API 통신할 변수들을 셋팅한다.
	String result_version = "";
	String result_market = "";
	String result_usable = "";
	String result_update_code_url = "";	
	String result_comment1 = "";
	
	String authProcessValue;
	String splashValue;
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;

    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        
        //어플을 실행할 수 있는 환경인지 확인
        String OSver = android.os.Build.VERSION.RELEASE;
        int APIver = android.os.Build.VERSION.SDK_INT;
        
        if((OSver.indexOf("2.1") > -1 || OSver.indexOf("2.2") > -1) || APIver < 7){
        	AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
			String title = "알림";	
			String message = "해당 휴대폰의 OS 버전에서는 서비스 이용이 불가합니다.\nOS 업데이트 후 이용해주세요.";	
			String buttonName = "확인";
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			ad.show();
        }else{
        	CallAppVersionCheckThread thread = new CallAppVersionCheckThread();
			thread.start();       
        }
    }
    
    //API 호출 쓰레드
  	class CallAppVersionCheckThread extends Thread{
  		public void run(){
  			try{
  				String url = "https://www.heream.com/api/checkVersion.jsp?appVersion="+ URLEncoder.encode(AppVersion) +"&appStore="+ URLEncoder.encode(Market);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{	
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode);
  				
  				if(addApiResult == 0 || addApiResult == 1){
  					result_version = WizSafeParser.xmlParser_String(returnXML,"<RESULT_VERSION>");
  					result_market = WizSafeParser.xmlParser_String(returnXML,"<RESULT_MARKET>");
  					result_usable = WizSafeParser.xmlParser_String(returnXML,"<RESULT_USABLE>");
  					result_update_code_url = WizSafeParser.xmlParser_String(returnXML,"<RESULT_UPDATE_CODE_URL>");
  					result_comment1 = WizSafeParser.xmlParser_String(returnXML,"<RESULT_COMMENT1>");
  					
  					if("1".equals(result_usable)){
  						pHandler.sendEmptyMessage(0);
  					}else{
  						pHandler.sendEmptyMessage(1);
  					}
  				}else{
  					pHandler.sendEmptyMessage(2);
  				}
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			if(msg.what == 0){
  				//인증된 사람인지 아닌지 판별
  		        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
  		        	
  		        	//잠시 화면을 보여주고 메인 엑티비티로 이동
  		       		Timer timer = new Timer();
  		       		TimerTask myTask = new TimerTask(){
  		       			public void run(){
  		       				//main splash 액티비티를 호출	
  		       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
  		       		    	SplashActivity.this.finish();
  		       			}
  		       		};
  		       		
  		       		//3초 뒤 실행
  		       		timer.schedule(myTask, 3000);
  		        	
  		        }else{
  		        	
  		        	//잠시 화면을 보여주고 가입하기 액티비티로 이동
  		       		Timer timer = new Timer();
  		       		TimerTask myTask = new TimerTask(){
  		       			public void run(){
  		       				//main splash 액티비티를 호출	
  		       		    	startActivity(new Intent(SplashActivity.this, JoinAcceptActivity.class));
  		       		    	SplashActivity.this.finish();
  		       			}
  		       		};
  		       		
  		       		//3초 뒤 실행
  		       		timer.schedule(myTask, 3000);
  		        }
  			}else if(msg.what == 1){
  				AlertDialog.Builder submitAlert = new AlertDialog.Builder(SplashActivity.this);
				submitAlert.setTitle("알림");
				submitAlert.setMessage("새로운 업데이트가  있습니다. 업데이트 이후 이용해 주세요.");
				submitAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						Uri u = Uri.parse(result_update_code_url);
						i.setData(u);
						startActivity(i);
						finish();					
					}
				});
				submitAlert.setNegativeButton("취소", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				submitAlert.show();
  			}else if(msg.what == 2){
  				AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}
  				
  		}
  	};
}