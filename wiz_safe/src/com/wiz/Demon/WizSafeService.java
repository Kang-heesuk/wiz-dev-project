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
	
	//������ Ż�� ���� ���μ��� ����� true �� �Ǹ鼭 �����带 Ż���ϰ� �ȴ�.
	boolean Quit;
	
	//��������ϰ�쿡�� ��밡���ϵ��� ���ÿ� �ʿ�
	PowerManager.WakeLock wakeLock;
	
	//����ۿ� ���̴� ����
	Intent intent;
	PendingIntent sender;
	AlarmManager am;
	Calendar calendar;
	
	//��׶��忡 ������ ���ֳ� �Ǵ��ϴ� ����
	ActivityManager DemonManager;
	List<ActivityManager.RunningServiceInfo> RunningService;
	boolean isBackGroundRunning = false;
	ActivityManager.RunningServiceInfo rsi;
	String tempRunningClass = "";
	int count = 0;
	
	//���� ��׶��� ���񽺸� ����Ǵ� ���� ��Ű�� ���� ����
	ComponentName cn;
	ComponentName startsvc;

	public void onCreate(){ 
		super.onCreate();
		
		Quit = false;
		
		//������忡���� CPU�� ��� ������̵��� ����
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
				
		//������ ����
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//������ ��� �θ� 40�� ���� ������ ���� �׾������. ����� �ð��� ������ �˶��� ȣ���� ���´�.(30�и���)
		restartDemon();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//���μ��� ����� ȣ���
		Quit = true;

		//������忡���� �����ϵ��� �ϴ� ������ �����ش�.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
	} 
	
	
	//5���ֱ�� �ѹ��� �����ϴ� ������ : �� �ȿ� GPS�� ������ ��츦 �־���� GPS ���� null �̸� ��Ʈ��ũ ���ι��̴��� ��ü�Ѵ�.
	class DemonThread extends Thread{ 
		public void run(){
			
			while(Quit == false){
				
				try{
					//��ġã�� ��׶��� ������ �׿��ٰ� �츰��.
					cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
					stopService(new Intent().setComponent(cn));
					WizSafeService.this.startService(new Intent().setComponent(cn));
					
					//�ִ� 17�� ����
					try{Thread.sleep(1000 * 18);}catch(Exception e){}
					
					//��ġã�� ��׶��� ������ ���δ�.(��ġ�� ���������鼭 ������ ���۾��Ұ�츦 ����Ͽ� ���δ�. = Ȯ�λ��)
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
