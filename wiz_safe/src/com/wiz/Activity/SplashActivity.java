package com.wiz.Activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
        
   		Handler handler = new Handler () {

            @Override
            public void handleMessage(Message msg)
            {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                finish();
            }
        };
        //별도의 수행을 가지는 핸들러로 3000ms 이후에 빈메시지를 한번 전송하도록 한다.
        handler.sendEmptyMessageDelayed(0, 3000);

       	if(true){	//인증 성공하면 true
       		
       		//잠시 화면을 보여주고 이동
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash 액티비티를 호출	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       			}
       		};
       		timer.schedule(myTask, 3000);
       		
       	}else{
       		//인증 splash 액티비티를 호출
           //startActivity(new Intent(this, SplashActivity.class));
       		Toast.makeText(SplashActivity.this, "auth failed!!!", Toast.LENGTH_SHORT).show();
       		
       	}
       	
    }
    

}