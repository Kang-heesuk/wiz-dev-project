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
	
	//������ Ż�� ���� ���μ��� ����� true �� �Ǹ鼭 �����带 Ż���ϰ� �ȴ�.
	boolean Quit;
	
	//���� �ڽ��� ��ġ�� �ּҸ� �������� ���� ����
	private LocationManager mLocationManager_GPS;
	private LocationManager mLocationManager_NETWORK;
	private Location mLocation_GPS;
	private Location mLocation_NETWORK;
	private Location selectedLocation;
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
	String enc_lat = "";
	String enc_lon = "";
	String hiddenUser = "";
	
	//API ��ſ� ���̴� ������
	String url;
	HttpURLConnection urlConn;
	BufferedReader br;
	
	//��������ϰ�쿡�� ��밡���ϵ��� ���ÿ� �ʿ�
	PowerManager.WakeLock wakeLock;
	
	//���� ��Ʈ��ũ ���°� 3G ���� 4G ���� WIFI ���� �Ǵ��ϴ� ���� 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	//����ۿ� ���̴� ����
	Intent intent;
	PendingIntent sender;
	AlarmManager am;
	Calendar calendar;
	
	public void onCreate(){ 
		super.onCreate(); 
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//���μ��� ����� ȣ���
		Quit = true;
		
		//�ش� �ܸ��� ���� �浵�� �������� ������ �����ش�.
		mLocationManager_GPS.removeUpdates(this);
		mLocationManager_NETWORK.removeUpdates(this);

		//������忡���� �����ϵ��� �ϴ� ������ �����ش�.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
	} 
	
	public int onStartCommand(Intent intent, int flags, int startId){
		
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		
		//������忡���� CPU�� ��� ������̵��� ����
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		//��Ʈ��ũ ��ȣ�� GPS��ȣ ��θ� �����Ѵ�.
		mLocationManager_GPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager_NETWORK = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_NETWORK.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//�ش� ��ġ�� �ּҸ� �������� ���� ��ü ����
		geoCoder = new Geocoder(WizSafeService.this, Locale.KOREAN);
				
		//������ ����
		DemonThread thread = new DemonThread(); 
		thread.start();
		
		//������ ��� �θ� 40�� ���� ������ ���� �׾������. ����� �ð��� ������ �˶��� ȣ���� ���´�.(30�и���)
		restartDemon();
		
		return START_STICKY;
	}
	
	
	//5���ֱ�� �ѹ��� �����ϴ� ������ : �� �ȿ� GPS�� ������ ��츦 �־���� GPS ���� null �̸� ��Ʈ��ũ ���ι��̴��� ��ü�Ѵ�.
	class DemonThread extends Thread{ 
		public void run(){
			
			while(Quit == false){
				
				//�����̼� ������ �������� �κ�
				try{
					//���� ����� Location ������ null�� �ƴҶ��� ������
					if(selectedLocation != null){
						//��ġ ���� ���������� ����
						lat = selectedLocation.getLatitude();
						lon = selectedLocation.getLongitude();
							
						//�ش� �ܸ��� ���� ���� ��ġ ���� ����� ����ϴ� ������� �ƴ��� �Ǻ�
						if(WizSafeUtil.isHiddenOnUser(WizSafeService.this)){
							temp_hiddenUser = "0";
						}else{
							temp_hiddenUser = "1";
						}
						
						//�ش� ���� ��ȣȭ
						enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
						enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
						hiddenUser = temp_hiddenUser;
						
						//API����� ���Ͽ� DB�� ��ġ���� �ױ�
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
	
	//������ ��ġ ������ ���Ҷ����� ȣ��ȴ�.
	public void onLocationChanged(Location arg0) {
		//��ġ�� ������� ���ϴ� �ΰ��� ��� ��θ� ������ �� �� ���ǿ� �´� ���� �����´�.
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
	
		//1.�ʱ�ȭ
		selectedLocation = null;
		provider = "";
		
		//2. ��ǥ ������ ������ - �ΰ��� ������ �켱����  1.�ð� , 2.GPS, 3.NETWORK
		//�Ѵ� ���� �ƴҰ�쿡 �켱���� ����
		if(GPS != null && NETWORK != null){
			//�ΰ��� �ð��� ������ 1�� �����ϰ��
			if(Math.abs(GPS.getTime() - NETWORK.getTime()) <= (1000 * 60)){
				selectedLocation = GPS;
				provider = "GPS";
			}else{
				selectedLocation = NETWORK;
				provider = "NETWORK";
			}
		//GPS�� NULL�� �ƴϰ�, NETWORK�� NULL�� ���
		}else if(GPS != null && NETWORK == null){
			//�ֱ� ������ GPS��ȣ�� �ð��� ����ð��� 10�� ������ ���̳���
			if(Math.abs(GPS.getTime() - System.currentTimeMillis()) <= (1000 * 60 * 10)){
				selectedLocation = GPS;
				provider = "GPS";
			}else{
				selectedLocation = null;
				provider = "";
			}
		//GPS�� NULL�̰�, NETWORK�� NULL�� �ƴѰ��
		}else if(GPS == null && NETWORK != null){
			selectedLocation = NETWORK;
			provider = "NETWORK";
		//�Ѵ� NULL�� ���
		}else if(GPS == null && NETWORK == null){
			selectedLocation = null;
			provider = "";
		}
		
		//3. ���� ���°� ��Ʈ��ũ �����̸� ���ӵ� ��Ż��¸� �����Ѵ�.
		if("NETWORK".equals(provider)){
			cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null){
				activeNetwork = cm.getActiveNetworkInfo();
				switch (activeNetwork.getType()) {
				case ConnectivityManager.TYPE_WIMAX: // 4g �� üũ
					provider = "4G";
				break;
				case ConnectivityManager.TYPE_WIFI: // wifi�� üũ
					provider = "WIFI";
				break;
				case ConnectivityManager.TYPE_MOBILE: // 3g �� üũ
					provider = "3G";
				break;
				}
			}
		}
	}
}
