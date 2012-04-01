package com.wiz.Demon;

import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WizSafeService extends Service implements LocationListener {
	
	boolean Quit;
	private LocationManager mLocationManager;
	private Location mLocation = null;
	
	//��������ϰ�쿡�� ��밡���ϵ��� ���ÿ� �ʿ�
	PowerManager.WakeLock wakeLock;
	
	public void onCreate(){ 
		super.onCreate(); 
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//���μ��� ����� ȣ���
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
		Quit = true;
	} 
	
	public int onStartCommand(Intent intent, int flags, int startId){
		
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		
		//������忡���� CPU�� ��� ������̵��� ����
		if(wakeLock == null){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
			wakeLock.acquire();
		}
		
		//�ش� ��ġ ��ǥ ��������
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String provider = mLocationManager.getBestProvider(criteria, true);
		
		Log.i("childList","== provider : " + provider);
		
		mLocationManager.requestLocationUpdates(provider, 5000, 0, this);
		mLocation = mLocationManager.getLastKnownLocation(provider);

		//������ ����
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//������ ��� �θ� 40�� ���� ������ ���� �׾������. ����� �ð��� ������ �˶��� ȣ���� ���´�.(30�и���)
		restartDemon();
		
		return START_STICKY;
 
	}
	
	
	//5�и��� �ѹ��� �����Ѵ�.
	class DemonThread extends Thread{ 
		public void run(){
			for(int i = 0 ; Quit == false ; i++){
				if(mLocation != null){
					double lat = mLocation.getLatitude();
					double lon = mLocation.getLongitude();
					String enc_lat = Double.toString(lat);
					String enc_lon = Double.toString(lon);
					callInsertLoactionApi(enc_lat, enc_lon);
				}else{
				}
				try{Thread.sleep(1000 * 60 * 5);}catch(Exception e){}
			}
		}
	}
	
	public void callInsertLoactionApi(String lat, String lon){
		try{
			URL url = new URL("http://210.109.111.213/test/JYJ/APItest.jsp?lat=" + lat + "&lon=" + lon);
			URLConnection url_conn = null;
			url_conn = (HttpURLConnection)url.openConnection();
			BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(url_conn.getInputStream()));
		}catch(Exception e){
		} 
	}
	
	public void restartDemon(){
		Intent intent = new Intent(WizSafeService.this, RestartAlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(WizSafeService.this, 0, intent, 0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 60*30);
		am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}
	
	public void onLocationChanged(Location arg0) {
		
	}

	public void onProviderDisabled(String arg0) {
		
	}

	public void onProviderEnabled(String arg0) {
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}

}
