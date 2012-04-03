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
        
        //������ ������� �ƴ��� �Ǻ�
        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
        	//�����Ȼ���� ��׶��� ���󼭺񽺸� �����ߴٰ� �ٷ� �ٽý���
        	
        	
        	//��� ȭ���� �����ְ� ���� ��Ƽ��Ƽ�� �̵�
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash ��Ƽ��Ƽ�� ȣ��	
       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
       		    	SplashActivity.this.finish();
       			}
       		};
       		
       		//1�� �� ����
       		timer.schedule(myTask, 1000);
        	
        }else{
        	
        	//��� ȭ���� �����ְ� �����ϱ� ��Ƽ��Ƽ�� �̵�
       		Timer timer = new Timer();
       		TimerTask myTask = new TimerTask(){
       			public void run(){
       				//main splash ��Ƽ��Ƽ�� ȣ��	
       		    	startActivity(new Intent(SplashActivity.this, JoinAcceptActivity.class));
       		    	SplashActivity.this.finish();
       			}
       		};
       		
       		//1�� �� ����
       		timer.schedule(myTask, 1000);
        }
    }
}