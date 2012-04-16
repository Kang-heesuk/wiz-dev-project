package com.wiz.Demon;

import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class WizSafeService extends Service {
	
	//스레드 탈출 변수 프로세스 종료시 true 가 되면서 스레드를 탈출하게 된다.
	boolean Quit;
	
	//절전모드일경우에도 사용가능하도록 셋팅에 필요
	PowerManager.WakeLock wakeLock;
	
	//재시작에 쓰이는 변수
	Intent intent;
	PendingIntent sender;
	AlarmManager am;
	Calendar calendar;
	
	//백그라운드에 데몬이 떠있나 판단하는 변수
	ActivityManager DemonManager;
	List<ActivityManager.RunningServiceInfo> RunningService;
	boolean isBackGroundRunning = false;
	ActivityManager.RunningServiceInfo rsi;
	String tempRunningClass = "";
	int count = 0;
	
	//현재 백그라운드 서비스를 종료또는 시작 시키기 위한 변수
	ComponentName cn;
	ComponentName startsvc;

	public void onCreate(){ 
		super.onCreate();
		
		Quit = false;
		
		//절전모드에서도 CPU가 계속 사용중이도록 셋팅
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
				
		//쓰레드 시작
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//데몬은 계속 두면 40분 정도 지나면 지가 죽어버린다. 재시작 시간을 셋팅한 알람을 호출해 놓는다.(30분마다)
		restartDemon();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//프로세스 종료시 호출됨
		Quit = true;

		//슬립모드에서도 동작하도록 하는 로직을 없애준다.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
	} 
	
	
	//5분주기로 한번씩 동작하는 쓰레드 : 이 안에 GPS로 가져올 경우를 넣어놓고 GPS 값이 null 이면 네트워크 프로바이더로 대체한다.
	class DemonThread extends Thread{ 
		public void run(){
			
			while(Quit == false){
				
				try{
					//위치찾기 백그라운드 데몬을 죽였다가 살린다.
					cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
					stopService(new Intent().setComponent(cn));
					WizSafeService.this.startService(new Intent().setComponent(cn));
					
					//최대 17초 유지
					try{Thread.sleep(1000 * 18);}catch(Exception e){}
					
					//위치찾기 백그라운드 데몬을 죽인다.(위치를 못가져오면서 가만히 동작안할경우를 대비하여 죽인다. = 확인사살)
					cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
					stopService(new Intent().setComponent(cn));
				}catch(Exception e){
				}finally{
				}
				try{Thread.sleep(1000 * 60 * 2);}catch(Exception e){}
			}
		}
	}
	
	public void restartDemon(){
		intent = new Intent(WizSafeService.this, RestartAlarmReceiver.class);
		sender = PendingIntent.getBroadcast(WizSafeService.this, 0, intent, 0);
		am = (AlarmManager)getSystemService(ALARM_SERVICE);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 60*30);
		am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}

	public IBinder onBind(Intent intent) {
		return null;
	}
}
