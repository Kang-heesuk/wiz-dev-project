package com.wiz.Demon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeUtil;

public class WizSafeGetLocation extends Service implements LocationListener {
	
	//재시작에 쓰이는 변수
	Intent intent;
	PendingIntent sender;
	AlarmManager am;
	Calendar calendar;
	
	//현재 자신의 위치와 주소를 가져오기 위한 변수
	private LocationManager mLocationManager_GPS;
	private LocationManager mLocationManager_NETWORK;
	private Location mLocation_GPS;
	private Location mLocation_NETWORK;
	private Location selectedLocation = null;
	String provider = "";
	
	//위도 경도 변수
	double lat;
	double lon;
	
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
	PowerManager pm;
	PowerManager.WakeLock wakeLock;
	
	//현재 네트워크 상태가 3G 인지 4G 인지 WIFI 인지 판단하는 변수 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	//현재 백그라운드 서비스를 종료또는 시작 시키기 위한 변수
	ComponentName cn;
	
	public void onCreate(){ 
		super.onCreate(); 
		
		//5분뒤에 재시작되도록 설정함
		restartDemon();
		
		writeLog_print("서비스2번  ===== 온크리에이트");
		Log.i("childList","서비스2번  ===== 온크리에이트");
		
		//절전모드에서도 CPU가 계속 사용중이도록 셋팅
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		//네트워크 신호와 GPS신호 모두를 선언한다.
		mLocationManager_GPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_GPS.addNmeaListener(gpsQuentityLoad);
		mLocationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager_NETWORK = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_NETWORK.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//위치 정보를 가져오는 방식이 두개 모두 꺼져있는경우엔 그냥 서비스 종료한다.
		if(!mLocationManager_GPS.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager_NETWORK.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			
			writeLog_print("서비스 2번 *** 위치정보가져오는방식 모두 꺼짐 서비스 종료함");
			Log.i("childList","서비스 2번 *** 위치정보가져오는방식 모두 꺼짐 서비스 종료함");
			
			cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
			stopService(new Intent().setComponent(cn));
			return;
		}
		
		//쓰레드 시작
		CallInsertLocationThread thread = new CallInsertLocationThread(); 
		thread.start();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//해당 단말의 위도 경도를 가져오는 로직을 없애준다.
		mLocationManager_GPS.removeNmeaListener(gpsQuentityLoad);
		mLocationManager_GPS.removeUpdates(this);
		mLocationManager_NETWORK.removeUpdates(this);

		//슬립모드에서도 동작하도록 하는 로직을 없애준다.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
		
		writeLog_print("서비스2번  ===== 온디스트로이");
		writeLog_print("====================================");
		Log.i("childList","서비스2번  ===== 온디스트로이");
		
	}
	
	public void onLocationChanged(Location arg0) {
		writeLog_print("====LocationChange!!");
		
		//위치가 어떤것으로 변하던 두가지 방식 모두를 가지고 둘 중 조건에 맞는 값을 가져온다.
		mLocation_GPS = mLocationManager_GPS.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocation_NETWORK = mLocationManager_NETWORK.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		//최적의 조건으로 위치를 가져온 후 전역변수에 저장
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
  	
	//시작하고 10초동안 위치정보를 가져오도록 기다리고, 10초동안 쌓인 정보중 최근값을 API 통신한다.
	class CallInsertLocationThread extends Thread{ 
		public void run(){
			try{
				writeLog_print("서비스 2번의 스레드***시작");
				Log.i("childList","서비스 2번의 스레드***시작");
				try{Thread.sleep(1000 * 10);}catch(Exception e){}
				
				if(selectedLocation != null){
					writeLog_print("서비스 2번의 스레드***위치정보를 가져옴");
					//위치 정보 전역변수에 저장
					lat = selectedLocation.getLatitude();
					lon = selectedLocation.getLongitude();
					
					//해당 단말의 설정 에서 위치 숨김 기능을 사용하는 사람인지 아닌지 판별
					if(WizSafeUtil.isHiddenOnUser(WizSafeGetLocation.this)){
						temp_hiddenUser = "0";
					}else{
						temp_hiddenUser = "1";
					}
				
					//해당 정보 암호화
					enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
					enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
					hiddenUser = temp_hiddenUser;
					
					//API통신이 가능한 경우에만 통신
					cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					if(cm.getActiveNetworkInfo() != null){
						//API 통신
						try{
							enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(WizSafeGetLocation.this));
							dbInsertDate = WizSafeUtil.getInsertDbDate();
							url = "https://www.heream.com/api/insertLocation.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&dbInsertDate=" + URLEncoder.encode(dbInsertDate) + "&lat=" + URLEncoder.encode(enc_lat) + "&lon=" + URLEncoder.encode(enc_lon) + "&type=" + URLEncoder.encode(provider) + "&hiddenUser=" + URLEncoder.encode(hiddenUser);
							urlConn = (HttpURLConnection) new URL(url).openConnection();
							urlConn.setConnectTimeout(3000);
							urlConn.setReadTimeout(3000);
							br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
							writeLog_print("서비스 2번의 스레드***통신이완료되었슴");
						}catch(Exception e){
							writeLog_print("서비스 2번의 스레드***통신중에러남 : " + e.toString());
							Log.i("childList","서비스 2번의 스레드***통신중에러남 : " + e.toString());
						}finally{
							if(br != null){ br.close(); }
							if(urlConn != null) {urlConn.disconnect(); urlConn = null;}
						}
					}
				}
				WizSafeUtil.setLastSendLocation(WizSafeGetLocation.this, Long.toString(System.currentTimeMillis()));
				pHandler.sendEmptyMessage(0);
			}catch(Exception e){
				writeLog_print("서비스 2번의 스레드***개에러남 : " + e.toString());
				WizSafeUtil.setLastSendLocation(WizSafeGetLocation.this, Long.toString(System.currentTimeMillis()));
				pHandler.sendEmptyMessage(0);
			}
		}
	}
	
	//API통신이 끝나고 난 뒤에 핸들러로써 해당 서비스를 종료한다.
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			if(msg.what == 0){
  				cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
  				stopService(new Intent().setComponent(cn));
  			}
  		}
  	};
  	
  	//현재 포착된 위성의 갯수를 판별(이유는 왠지 GPS를 잘찾는거같아서)
  	GpsStatus.NmeaListener gpsQuentityLoad = new GpsStatus.NmeaListener(){
		public void onNmeaReceived(long timestamp, String nmea) {
		}
  	};
  	
  	//데몬을 재시작하기 위한 셋팅
  	public void restartDemon(){
		intent = new Intent(WizSafeGetLocation.this, RestartAlarmReceiver.class);
		sender = PendingIntent.getBroadcast(WizSafeGetLocation.this, 0, intent, 0);
		am = (AlarmManager)getSystemService(ALARM_SERVICE);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, (60*5)+15);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}
  	
  	public static void writeLog_print(String msg)
	{
		try{
			File file = new File(Environment.getExternalStorageDirectory(),"note.txt");
		    FileWriter fw = new FileWriter(file,true);
		    BufferedWriter out = new BufferedWriter(fw);
		    Date today = new Date();
		    StringBuffer buf = new StringBuffer();
		    SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss-SSS");
			buf.append(df1.format(today) + " " + df2.format(today)).append("\n");
			buf.append(" : ").append(msg).append("\n");
		    out.write(buf.toString());
		    out.flush();
		    out.close();
		}catch(Exception e){
		}
	}
  	
}
