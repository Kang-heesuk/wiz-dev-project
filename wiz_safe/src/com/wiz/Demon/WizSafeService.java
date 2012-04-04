package com.wiz.Demon;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeUtil;

public class WizSafeService extends Service implements LocationListener {
	
	boolean Quit;
	private LocationManager mLocationManager;
	private Location mLocation = null;
	Geocoder geoCoder;
	String provider = "null";
	
	//절전모드일경우에도 사용가능하도록 셋팅에 필요
	PowerManager.WakeLock wakeLock;
	
	public void onCreate(){ 
		super.onCreate(); 
	}
	
	public void onDestroy(){  
		super.onDestroy();
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
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		provider = mLocationManager.getBestProvider(criteria, true);
		
		mLocationManager.requestLocationUpdates(provider, 5000, 0, this);
		mLocation = mLocationManager.getLastKnownLocation(provider);

		//해당 위치의 주소를 가져오기 위한 객체 선언
		geoCoder = new Geocoder(WizSafeService.this, Locale.KOREAN);
		
		//쓰레드 시작
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//데몬은 계속 두면 40분 정도 지나면 지가 죽어버린다. 재시작 시간을 셋팅한 알람을 호출해 놓는다.(30분마다)
		restartDemon();
		
		return START_STICKY;
 
	}
	
	
	//5분마다 한번씩 동작한다.
	class DemonThread extends Thread{ 
		public void run(){
			for(int i = 0 ; Quit == false ; i++){
				Log.i("childList" , "==mLocation : " + mLocation);
				if(mLocation != null){
					double lat = mLocation.getLatitude();
					double lon = mLocation.getLongitude();
					
					//해당좌표의 주소 가져오기
					String temp_addr = "";
					try {
						
						List<Address> addresses = geoCoder.getFromLocation(lat, lon, 1);
						
						if(addresses.size() > 0){
							Address address = addresses.get(0);
							StringBuilder trace = new StringBuilder();
							trace.append(address.getLocality()).append(" ");
							trace.append(address.getThoroughfare()).append(" ");
							trace.append(address.getFeatureName());
							temp_addr = trace.toString();
						}else{
							temp_addr = "noAddress";
						}
					
					} catch (IOException e) {
						temp_addr = "failAddress";
					}
					
					//해당 단말의 설정 에서 위치 숨김 기능을 사용하는 사람인지 아닌지 판별
					String temp_hiddenUser = "1";
					if(WizSafeUtil.isHiddenOnUser(WizSafeService.this)){
						temp_hiddenUser = "0";
					}else{
						temp_hiddenUser = "1";
					}
					
					//암호화
					String enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
					String enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
					String enc_addr = WizSafeSeed.seedEnc(temp_addr); 
					String hiddenUser = temp_hiddenUser;
					
					callInsertLoactionApi(enc_lat, enc_lon, enc_addr, hiddenUser);
				}else{
					mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Criteria criteria = new Criteria();
					criteria.setAccuracy(Criteria.ACCURACY_COARSE);
					criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
					provider = mLocationManager.getBestProvider(criteria, true);
					mLocation = mLocationManager.getLastKnownLocation(provider);
				}
				
				try{Thread.sleep(1000 * 60 * 5);}catch(Exception e){}
			}
		}
	}
	
	public void callInsertLoactionApi(String lat, String lon, String addr, String hiddenUser){
		try{
			String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(WizSafeService.this));
			String type = provider;
			URL url = new URL("https://www.heream.com/api/insertLocation.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&lat=" + URLEncoder.encode(lat) + "&lon=" + URLEncoder.encode(lon) + "&addr=" + URLEncoder.encode(addr) + "&type=" + URLEncoder.encode(type) + "&hiddenUser=" + URLEncoder.encode(hiddenUser));
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
