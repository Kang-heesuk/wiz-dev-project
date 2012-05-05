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
        
        //인증된 사람인지 아닌지 판별
        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
        	
        	//인증된사람은 백그라운드에 데몬이 떠있나 판단하여 없다면 데몬 서비스 실행
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
        		//백그라운드에 DemonService라는 Sevice가 존재하는지 가져옴.
				ComponentName cn = new ComponentName(getPackageName(), WizSafeService.class.getName());
				//서비스 시작(위에서 중지 시킨 데몬을 시작시킴)
				startService(new Intent().setComponent(cn));
        	}
			
        	
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
    
    public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
}