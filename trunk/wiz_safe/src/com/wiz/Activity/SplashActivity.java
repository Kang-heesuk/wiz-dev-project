package com.wiz.Activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	String authProcessValue;
	String splashValue;
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	String getVal;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 

        //최초 인증체크값(isAuthCheck)을 0으로 설정
   	 	LocalSave = getSharedPreferences("isAuthCheck", 0);
   		edit = LocalSave.edit();

   		
       	//if("0".equals(getVal)){	//인증 성공하면 true
   		if(true){	//인증 성공하면 true
       		
       		//잠시 화면을 보여주고 이동
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash 액티비티를 호출	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       		    	
       		    	SplashActivity.this.finish();
       			}
       		};

       		//내가 일부러 0초로 만듬 빠른 테스트를 위하여
       		timer.schedule(myTask, 0);
       		
       	}else{
       		//인증 splash 액티비티를 호출
           //startActivity(new Intent(this, SplashActivity.class));
       		Toast.makeText(SplashActivity.this, "auth failed!!!", Toast.LENGTH_SHORT).show();
       		
       	}
       	
    }
    

}