package com.wiz.Demon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeUtil;

public class WizSafeService extends Service implements LocationListener {
	
	//스레드 탈출 변수 프로세스 종료시 true 가 되면서 스레드를 탈출하게 된다.
	boolean Quit;
	
	//현재 자신의 위치와 주소를 가져오기 위한 변수
	private LocationManager mLocationManager_GPS;
	private LocationManager mLocationManager_NETWORK;
	private Location mLocation_GPS;
	private Location mLocation_NETWORK;
	private Location selectedLocation;
	Geocoder geoCoder;
	String provider = "";
	 
	//위도 경도 변수
	double lat;
	double lon;
	
	//해당 좌표의 주소 변수
	String temp_addr = "";
	List<Address> addresses;
	StringBuilder trace;
	Address address;
	
	//유저상태에 관한 변수
	String temp_hiddenUser = "1";
	
	//API 호출할때 파라메터로 붙이는 변수들
	String enc_ctn = "";
	String dbInsertDate = "";
	String enc_lat = "";
	String enc_lon = "";
	String hiddenUser = "";
	
	//API 통신에 쓰이는 변수들
	String url;
	HttpURLConnection urlConn;
	BufferedReader br;
	
	//절전모드일경우에도 사용가능하도록 셋팅에 필요
	PowerManager.WakeLock wakeLock;
	
	//현재 네트워크 상태가 3G 인지 4G 인지 WIFI 인지 판단하는 변수 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	//재시작에 쓰이는 변수
	Intent intent;
	PendingIntent sender;
	AlarmManager am;
	Calendar calendar;
	
	public void onCreate(){ 
		super.onCreate(); 
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//프로세스 종료시 호출됨
		Quit = true;
		
		//해당 단말의 위도 경도를 가져오는 로직을 없애준다.
		mLocationManager_GPS.removeUpdates(this);
		mLocationManager_NETWORK.removeUpdates(this);

		//슬립모드에서도 동작하도록 하는 로직을 없애준다.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
	} 
	
	public int onStartCommand(Intent intent, int flags, int startId){
		
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		
		//절전모드에서도 CPU가 계속 사용중이도록 셋팅
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		//네트워크 신호와 GPS신호 모두를 선언한다.
		mLocationManager_GPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager_NETWORK = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_NETWORK.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//해당 위치의 주소를 가져오기 위한 객체 선언
		geoCoder = new Geocoder(WizSafeService.this, Locale.KOREAN);
				
		//쓰레드 시작
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//데몬은 계속 두면 40분 정도 지나면 지가 죽어버린다. 재시작 시간을 셋팅한 알람을 호출해 놓는다.(30분마다)
		restartDemon();
		
		return START_STICKY;
	}
	
	
	//5분주기로 한번씩 동작하는 쓰레드 : 이 안에 GPS로 가져올 경우를 넣어놓고 GPS 값이 null 이면 네트워크 프로바이더로 대체한다.
	class DemonThread extends Thread{ 
		public void run(){
			
			while(Quit == false){
				
				//로케이션 정보를 가져오는 부분
				try{
					//현재 저장된 Location 정보가 null이 아닐때만 동작함
					if(selectedLocation != null){
						//위치 정보 전역변수에 저장
						lat = selectedLocation.getLatitude();
						lon = selectedLocation.getLongitude();
							
						//해당 단말의 설정 에서 위치 숨김 기능을 사용하는 사람인지 아닌지 판별
						if(WizSafeUtil.isHiddenOnUser(WizSafeService.this)){
							temp_hiddenUser = "0";
						}else{
							temp_hiddenUser = "1";
						}
						
						//해당 정보 암호화
						enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
						enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
						hiddenUser = temp_hiddenUser;
						
						//API통신을 통하여 DB에 위치정보 쌓기
						callInsertLoactionApi(enc_lat, enc_lon, hiddenUser, provider);
					}
				}catch(Exception e){
				}finally{
				}
				
				try{Thread.sleep(1000 * 5 * 5);}catch(Exception e){}
			}
		}
	}
	
	public void callInsertLoactionApi(String lat, String lon, String hiddenUser, String provider){
		try{
			enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(WizSafeService.this));
			dbInsertDate = WizSafeUtil.getInsertDbDate();
			url = "https://www.heream.com/api/insertLocation.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&dbInsertDate=" + URLEncoder.encode(dbInsertDate) + "&lat=" + URLEncoder.encode(lat) + "&lon=" + URLEncoder.encode(lon) + "&type=" + URLEncoder.encode(provider) + "&hiddenUser=" + URLEncoder.encode(hiddenUser);
			urlConn = (HttpURLConnection) new URL(url).openConnection();
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
		}catch(Exception e){
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
	
	//현재의 위치 정보가 변할때마다 호출된다.
	public void onLocationChanged(Location arg0) {
		//위치가 어떤것으로 변하던 두가지 방식 모두를 가지고 둘 중 조건에 맞는 값을 가져온다.
		mLocation_GPS = mLocationManager_GPS.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocation_NETWORK = mLocationManager_NETWORK.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		selectLocation(mLocation_GPS, mLocation_NETWORK);
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
	
	public void selectLocation(Location GPS, Location NETWORK){
	
		//1.초기화
		selectedLocation = null;
		provider = "";
		
		//2. 좌표 정보를 가져옴 - 두개의 정보중 우선순위  1.시간 , 2.GPS, 3.NETWORK
		//둘다 널이 아닐경우에 우선순위 적용
		if(GPS != null && NETWORK != null){
			//두개의 시간적 오차가 1분 이하일경우
			if(Math.abs(GPS.getTime() - NETWORK.getTime()) <= (1000 * 60)){
				selectedLocation = GPS;
				provider = "GPS";
			}else{
				selectedLocation = NETWORK;
				provider = "NETWORK";
			}
		//GPS가 NULL이 아니고, NETWORK가 NULL일 경우
		}else if(GPS != null && NETWORK == null){
			//최근 가져온 GPS신호의 시간이 현재시간과 10분 안으로 차이나면
			if(Math.abs(GPS.getTime() - System.currentTimeMillis()) <= (1000 * 60 * 10)){
				selectedLocation = GPS;
				provider = "GPS";
			}else{
				selectedLocation = null;
				provider = "";
			}
		//GPS가 NULL이고, NETWORK가 NULL이 아닌경우
		}else if(GPS == null && NETWORK != null){
			selectedLocation = NETWORK;
			provider = "NETWORK";
		//둘다 NULL일 경우
		}else if(GPS == null && NETWORK == null){
			selectedLocation = null;
			provider = "";
		}
		
		//3. 현재 상태가 네트워크 상태이면 접속된 통신상태를 설정한다.
		if("NETWORK".equals(provider)){
			cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null){
				activeNetwork = cm.getActiveNetworkInfo();
				switch (activeNetwork.getType()) {
				case ConnectivityManager.TYPE_WIMAX: // 4g 망 체크
					provider = "4G";
				break;
				case ConnectivityManager.TYPE_WIFI: // wifi망 체크
					provider = "WIFI";
				break;
				case ConnectivityManager.TYPE_MOBILE: // 3g 망 체크
					provider = "3G";
				break;
				}
			}
		}
	}
}
