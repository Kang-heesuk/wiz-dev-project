package com.wiz.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        
        initialize();
        
    }
    
    private void initialize(){
        Handler handler = new Handler(){
        	public void handleMessage(Message msg){
        		super.handleMessage(msg); 
        		finish(); 
        	}
        };
        
        //프로세스 처리후 아래 메소드를 실행하면 해당 시간 후에 메인액티비티로 이동
        handler.sendEmptyMessageDelayed(0, 1000);
    }
}