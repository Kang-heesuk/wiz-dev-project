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
        
        //������ ������ �� �ִ� ȯ������ Ȯ��
        String OSver = android.os.Build.VERSION.RELEASE;
        int APIver = android.os.Build.VERSION.SDK_INT;
        
        if((OSver.indexOf("2.1") > -1 || OSver.indexOf("2.2") > -1) || APIver < 50){
        	AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
			String title = "�˸�";	
			String message = "�ش� OS ���������� �������� �ʽ��ϴ�.\nOS ������Ʈ �� �̿����ּ���.";	
			String buttonName = "Ȯ��";
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			ad.show();
        }else{
        
	        
	        //������ ������� �ƴ��� �Ǻ�
	        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
	        	
	        	//��� ȭ���� �����ְ� ���� ��Ƽ��Ƽ�� �̵�
	       		Timer timer = new Timer();
	       		TimerTask myTask = new TimerTask(){
	       			public void run(){
	       				//main splash ��Ƽ��Ƽ�� ȣ��	
	       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
	       		    	SplashActivity.this.finish();
	       			}
	       		};
	       		
	       		//3�� �� ����
	       		timer.schedule(myTask, 3000);
	        	
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
	       		
	       		//3�� �� ����
	       		timer.schedule(myTask, 3000);
	        }
	        
        }
    }
}