package com.wiz.Activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.wiz.Demon.WizSafeService;
import com.wiz.util.WizSafeRecycleUtil;
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
        	
        	//�����Ȼ���� ��׶��忡 ������ ���ֳ� �Ǵ��Ͽ� ���ٸ� ���� ���� ����
        	ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        	List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50); 	
        	boolean isRunning = false;
        	for(int i=0; i<rs.size(); i++){
        		ActivityManager.RunningServiceInfo rsi = rs.get(i);
        		String tempRunningClass = rsi.service.getClassName();
        		if(tempRunningClass == null) tempRunningClass = "";
        		if("com.wiz.Demon.WizSafeService".equals(tempRunningClass)){
        			isRunning = true;
        		}
        	}
        	if(!isRunning){
        		//��׶��忡 DemonService��� Sevice�� �����ϴ��� ������.
				ComponentName cn = new ComponentName(getPackageName(), WizSafeService.class.getName());
				//���� ����(������ ���� ��Ų ������ ���۽�Ŵ)
				startService(new Intent().setComponent(cn));
        	}
			
        	
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
    
    public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
}