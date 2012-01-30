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

        //���� ����üũ��(isAuthCheck)�� 0���� ����
   	 	LocalSave = getSharedPreferences("isAuthCheck", 0);
   		edit = LocalSave.edit();

   		
       	//if("0".equals(getVal)){	//���� �����ϸ� true
   		if(true){	//���� �����ϸ� true
       		
       		//��� ȭ���� �����ְ� �̵�
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash ��Ƽ��Ƽ�� ȣ��	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       		    	
       		    	SplashActivity.this.finish();
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