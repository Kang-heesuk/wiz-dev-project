package com.wiz.Activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.wiz.util.WizSafeUtil;

public class SplashActivity extends Activity {
	
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
        
        if((OSver.indexOf("2.1") > -1 || OSver.indexOf("2.2") > -1) || APIver < 50){
        	AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
			String title = "알림";	
			String message = "해당 OS 버전에서는 지원하지 않습니다.\nOS 업데이트 후 이용해주세요.";	
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
	        
        }
    }
}