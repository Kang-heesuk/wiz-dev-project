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
	
	//������ Ż�� ���� ���μ��� ����� true �� �Ǹ鼭 �����带 Ż���ϰ� �ȴ�.
	boolean Quit;
	
	
	//���� �ڽ��� ��ġ�� �ּҸ� �������� ���� ����
	private LocationManager mLocationManager;
	private Location mLocation;
	Criteria criteria;
	Geocoder geoCoder;
	String provider = "";
	
	//���� �浵 ����
	double lat;
	double lon;
	
	//�ش� ��ǥ�� �ּ� ����
	String temp_addr = "";
	List<Address> addresses;
	StringBuilder trace;
	Address address;
	
	//�������¿� ���� ����
	String temp_hiddenUser = "1";
	
	//API ȣ���Ҷ� �Ķ���ͷ� ���̴� ������
	String enc_ctn = "";
	String dbInsertDate = "";
	String type = "";
	String enc_lat = "";
	String enc_lon = "";
	String enc_addr = "";
	String hiddenUser = "";
	String realProvider = "";
	
	//API ��ſ� ���̴� ������
	String url;
	HttpURLConnection urlConn;
	BufferedReader br;
	
	//��������ϰ�쿡�� ��밡���ϵ��� ���ÿ� �ʿ�
	PowerManager.WakeLock wakeLock;
	
	//���� ��Ʈ��ũ ���°� 3G ���� 4G ���� WIFI ���� �Ǵ��ϴ� ���� 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
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
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		
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
			
			while(Quit == false){
				
				//�ش� ��ġ ��ǥ ��������
				mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
				provider = mLocationManager.getBestProvider(criteria, true);
				if(provider == null || "".equals(provider)){
					provider = LocationManager.NETWORK_PROVIDER;
				}
				mLocation = mLocationManager.getLastKnownLocation(provider);
				
				//�ش� ��ġ�� �ּҸ� �������� ���� ��ü ����
				geoCoder = new Geocoder(WizSafeService.this, Locale.KOREAN);
				
				//��ǥ�� �ּ� �������� ����
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
					
					//�ش� �ܸ��� ���� ���� ��ġ ���� ����� ����ϴ� ������� �ƴ��� �Ǻ�
					if(WizSafeUtil.isHiddenOnUser(WizSafeService.this)){
						temp_hiddenUser = "0";
					}else{
						temp_hiddenUser = "1";
					}
					
					//��ǥ�� ������ �ڿ��� �Ǵ��Ͽ� DB�� GPS, WIFI, 3G, 4G ���� �����Ѵ�.
					cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					if(LocationManager.NETWORK_PROVIDER.equals(provider) && cm.getActiveNetworkInfo() != null){
						activeNetwork = cm.getActiveNetworkInfo();
						switch (activeNetwork.getType()) {
						case ConnectivityManager.TYPE_WIMAX: // 4g �� üũ
							realProvider = "4G";
						break;
						case ConnectivityManager.TYPE_WIFI: // wifi�� üũ
							realProvider = "WIFI";
						break;
						case ConnectivityManager.TYPE_MOBILE: // 3g �� üũ
							realProvider = "3G";
						break;
						}
					}else if(LocationManager.GPS_PROVIDER.equals(provider)){
						realProvider = "GPS";
					}else{
						realProvider = "DEFAULT";
					}
					
					//��ȣȭ
					enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
					enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
					enc_addr = WizSafeSeed.seedEnc(temp_addr); 
					hiddenUser = temp_hiddenUser;
					
					//��������Ͽ� DB�� ��ġ������ ����
					callInsertLoactionApi(enc_lat, enc_lon, enc_addr, hiddenUser, realProvider);
					Log.i("childList","����Ϸ� : " + realProvider);
				}
				
				//�ڿ� �ʱ�ȭ
				mLocationManager = null;
				criteria = null;
				provider = null;
				mLocation = null;
				geoCoder = null;
				addresses = null;
				cm = null;
				activeNetwork = null;
				
				//5�а� ���
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
