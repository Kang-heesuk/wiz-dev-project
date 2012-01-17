package exam.DemonTest;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
import android.os.SystemClock;
import android.util.Log;

public class DemonService extends Service implements LocationListener{

	boolean Quit;
	private LocationManager mLocationManager;
	private Location mLocation = null;
	
	PowerManager.WakeLock wakeLock;

	public void onCreate(){ 
		Log.i("JYJ test","onCreate");
		super.onCreate(); 
	}
	
	public void onDestroy(){  
		super.onDestroy();
		Log.i("JYJ test","onDestroy");
		//프로세스 종료시 호출됨
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
		
		Quit = true;
	} 
	
	public int onStartCommand(Intent intent, int flags, int startId){
		
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		
		//절전모드에서도 CPU가 계속 사용중이도록 셋팅
		if(wakeLock == null){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
			wakeLock.acquire();
		}
		
		//해당 위치 좌표 가져오기
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String provider = mLocationManager.getBestProvider(criteria, true);
		mLocationManager.requestLocationUpdates(provider, 1000, 5, this);
		mLocation = mLocationManager.getLastKnownLocation(provider);

		//쓰레드 시작
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//데몬은 계속 두면 40분 정도 지나면 지가 죽어버린다. 재시작 시간을 셋팅한 알람을 호출해 놓는다.
		restartDemon();
		
		return START_STICKY;
 
	}
	

	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class DemonThread extends Thread{ 
		public void run(){
			for(int i = 0 ; Quit == false ; i++){

				if(mLocation != null){
					double lat = mLocation.getLatitude();
					double lon = mLocation.getLongitude();
					Log.i("JYJ test", lat + " // " + lon);
					callInsertLoactionApi(lat, lon);
				}else{
					Log.i("JYJ test","mLocation is Null");
				}
				Log.i("JYJ test","Quit == " + Quit);
				try{Thread.sleep(60000);}catch(Exception e){}
				
			}
		}
	}


	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void callInsertLoactionApi(double lat, double lon){
		try{
			URL url = new URL("http://210.109.111.213/test/JYJ/APItest.jsp?lat=" + lat + "&lon=" + lon);
			URLConnection url_conn = null;
			url_conn = (HttpURLConnection)url.openConnection();
			BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(url_conn.getInputStream()));
			Log.i("JYJ test", "====== Call API Success ======");
		}catch(Exception e){
			Log.i("JYJ test", "Connection Fail : " + e.toString());
		} 
	}
	
	public void restartDemon(){
		Intent intent = new Intent(DemonService.this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(DemonService.this, 0, intent, 0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 60*30);
		am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}

}  
