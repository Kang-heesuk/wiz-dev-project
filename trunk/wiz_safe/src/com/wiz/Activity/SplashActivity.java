package com.wiz.Activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
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
        
        //인증된 사람인지 아닌지 판별
        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
        	//인증된사람은 백그라운드 데몬서비스를 중지했다가 바로 다시실행
        	
        	
        	//잠시 화면을 보여주고 메인 엑티비티로 이동
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash 액티비티를 호출	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       		    	SplashActivity.this.finish();
       			}
       		};
       		
       		//1초 뒤 실행
       		timer.schedule(myTask, 1000);
        	
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
       		
       		//1초 뒤 실행
       		timer.schedule(myTask, 1000);
        }
    }
}