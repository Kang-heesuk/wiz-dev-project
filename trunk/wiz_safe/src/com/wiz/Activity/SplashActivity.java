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

        
        //���� ����üũ��(isAuthCheck)�� 0���� ����
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
        //������ ������ ������ �ڵ鷯�� 3000ms ���Ŀ� ��޽����� �ѹ� �����ϵ��� �Ѵ�.
        handler.sendEmptyMessageDelayed(0, 3000);

       	if(true){	//���� �����ϸ� true
       		
       		//��� ȭ���� �����ְ� �̵�
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash ��Ƽ��Ƽ�� ȣ��	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       			}
       		};
       		timer.schedule(myTask, 3000);
       		
       	}else{
       		//���� splash ��Ƽ��Ƽ�� ȣ��
           //startActivity(new Intent(this, SplashActivity.class));
       		Toast.makeText(SplashActivity.this, "auth failed!!!", Toast.LENGTH_SHORT).show();
       		
       	}
       	
    }
    

}