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
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeUtil;

public class WizSafeService extends Service implements LocationListener {
	
	//스레드 탈출 변수 프로세스 종료시 true 가 되면서 스레드를 탈출하게 된다.
	boolean Quit;
	
	
	//현재 자신의 위치와 주소를 가져오기 위한 변수
	private LocationManager mLocationManager;
	private Location mLocation;
	Criteria criteria;
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
	String type = "";
	String enc_lat = "";
	String enc_lon = "";
	String enc_addr = "";
	String hiddenUser = "";
	String realProvider = "";
	
	//API 통신에 쓰이는 변수들
	String url;
	HttpURLConnection urlConn;
	BufferedReader br;
	
	//절전모드일경우에도 사용가능하도록 셋팅에 필요
	PowerManager.WakeLock wakeLock;
	
	//현재 네트워크 상태가 3G 인지 4G 인지 WIFI 인지 판단하는 변수 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
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
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		
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
			
			while(Quit == false){
				
				//해당 위치 좌표 가져오기
				mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
				provider = mLocationManager.getBestProvider(criteria, true);
				if(provider == null || "".equals(provider)){
					provider = LocationManager.NETWORK_PROVIDER;
				}
				mLocation = mLocationManager.getLastKnownLocation(provider);
				
				//해당 위치의 주소를 가져오기 위한 객체 선언
				geoCoder = new Geocoder(WizSafeService.this, Locale.KOREAN);
				
				//좌표와 주소 가져오는 로직
				if(mLocation != null){
					lat = mLocation.getLatitude();
					lon = mLocation.getLongitude();
					
					try {
						addresses = geoCoder.getFromLocation(lat, lon, 1);
						if(addresses.size() > 0){
							trace = new StringBuilder();
							address = addresses.get(0);
							trace.append(address.getLocality()).append(" ");
							trace.append(address.getThoroughfare()).append(" ");
							trace.append(address.getFeatureName());
							temp_addr = trace.toString();
						}else{
							temp_addr = "noAddress";
						}
						
					}catch (Exception e){
						temp_addr = "failAddress";
					}
					
					//해당 단말의 설정 에서 위치 숨김 기능을 사용하는 사람인지 아닌지 판별
					if(WizSafeUtil.isHiddenOnUser(WizSafeService.this)){
						temp_hiddenUser = "0";
					}else{
						temp_hiddenUser = "1";
					}
					
					//좌표를 가져온 자원을 판단하여 DB에 GPS, WIFI, 3G, 4G 등을 구분한다.
					cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					if(LocationManager.NETWORK_PROVIDER.equals(provider) && cm.getActiveNetworkInfo() != null){
						activeNetwork = cm.getActiveNetworkInfo();
						switch (activeNetwork.getType()) {
						case ConnectivityManager.TYPE_WIMAX: // 4g 망 체크
							realProvider = "4G";
						break;
						case ConnectivityManager.TYPE_WIFI: // wifi망 체크
							realProvider = "WIFI";
						break;
						case ConnectivityManager.TYPE_MOBILE: // 3g 망 체크
							realProvider = "3G";
						break;
						}
					}else if(LocationManager.GPS_PROVIDER.equals(provider)){
						realProvider = "GPS";
					}else{
						realProvider = "DEFAULT";
					}
					
					//암호화
					enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
					enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
					enc_addr = WizSafeSeed.seedEnc(temp_addr); 
					hiddenUser = temp_hiddenUser;
					
					//통신을통하여 DB에 위치정보를 쌓음
					callInsertLoactionApi(enc_lat, enc_lon, enc_addr, hiddenUser, realProvider);
					Log.i("childList","정상완료 : " + realProvider);
				}
				
				//자원 초기화
				mLocationManager = null;
				criteria = null;
				provider = null;
				mLocation = null;
				geoCoder = null;
				addresses = null;
				cm = null;
				activeNetwork = null;
				
				//5분간 대기
				try{Thread.sleep(1000 * 60 * 5);}catch(Exception e){}
			}
		}
	}
	
	public void callInsertLoactionApi(String lat, String lon, String addr, String hiddenUser, String realProvider){
		try{
			enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(WizSafeService.this));
			dbInsertDate = WizSafeUtil.getInsertDbDate();
			type = realProvider;
			url = "https://www.heream.com/api/insertLocation.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&dbInsertDate=" + URLEncoder.encode(dbInsertDate) + "&lat=" + URLEncoder.encode(lat) + "&lon=" + URLEncoder.encode(lon) + "&addr=" + URLEncoder.encode(addr) + "&type=" + URLEncoder.encode(type) + "&hiddenUser=" + URLEncoder.encode(hiddenUser);
			urlConn = (HttpURLConnection) new URL(url).openConnection();
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
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
