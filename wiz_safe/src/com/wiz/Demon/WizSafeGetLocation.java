package com.wiz.Demon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeUtil;

public class WizSafeGetLocation extends Service implements LocationListener {

	//���� �ڽ��� ��ġ�� �ּҸ� �������� ���� ����
	private LocationManager mLocationManager_GPS;
	private LocationManager mLocationManager_NETWORK;
	private Location mLocation_GPS;
	private Location mLocation_NETWORK;
	private Location selectedLocation = null;
	String provider = "";
	
	//���� �浵 ����
	double lat;
	double lon;
	
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
	PowerManager pm;
	PowerManager.WakeLock wakeLock;
	
	//���� ��Ʈ��ũ ���°� 3G ���� 4G ���� WIFI ���� �Ǵ��ϴ� ���� 
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	//���� ��׶��� ���񽺸� ����Ǵ� ���� ��Ű�� ���� ����
	ComponentName cn;
	
	public void onCreate(){ 
		super.onCreate(); 
		
		//������忡���� CPU�� ��� ������̵��� ����
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Deomon WakeLock");
		wakeLock.acquire();
		
		//��Ʈ��ũ ��ȣ�� GPS��ȣ ��θ� �����Ѵ�.
		mLocationManager_GPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_GPS.addNmeaListener(gpsQuentityLoad);
		mLocationManager_GPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager_NETWORK = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager_NETWORK.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		//��ġ ������ �������� ����� �ΰ� ��� �����ִ°�쿣 �׳� ���� �����Ѵ�.
		if(!mLocationManager_GPS.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLocationManager_NETWORK.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
			stopService(new Intent().setComponent(cn));
			return;
		}
		
		//������ ����
		CallInsertLocationThread thread = new CallInsertLocationThread(); 
		thread.start();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		//�ش� �ܸ��� ���� �浵�� �������� ������ �����ش�.
		mLocationManager_GPS.removeNmeaListener(gpsQuentityLoad);
		mLocationManager_GPS.removeUpdates(this);
		mLocationManager_NETWORK.removeUpdates(this);

		//������忡���� �����ϵ��� �ϴ� ������ �����ش�.
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	public void onLocationChanged(Location arg0) {
		
		//��ġ�� ������� ���ϴ� �ΰ��� ��� ��θ� ������ �� �� ���ǿ� �´� ���� �����´�.
		mLocation_GPS = mLocationManager_GPS.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocation_NETWORK = mLocationManager_NETWORK.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		//������ �������� ��ġ�� ������ �� ���������� ����
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
  	
	//�����ϰ� 10�ʵ��� ��ġ������ ���������� ��ٸ���, 10�ʵ��� ���� ������ �ֱٰ��� API ����Ѵ�.
	class CallInsertLocationThread extends Thread{ 
		public void run(){
			try{
				try{Thread.sleep(1000 * 10);}catch(Exception e){}
				if(selectedLocation != null){
					//��ġ ���� ���������� ����
					lat = selectedLocation.getLatitude();
					lon = selectedLocation.getLongitude();
					
					//�ش� �ܸ��� ���� ���� ��ġ ���� ����� ����ϴ� ������� �ƴ��� �Ǻ�
					if(WizSafeUtil.isHiddenOnUser(WizSafeGetLocation.this)){
						temp_hiddenUser = "0";
					}else{
						temp_hiddenUser = "1";
					}
				
					//�ش� ���� ��ȣȭ
					enc_lat = WizSafeSeed.seedEnc(Double.toString(lat));
					enc_lon = WizSafeSeed.seedEnc(Double.toString(lon));
					hiddenUser = temp_hiddenUser;
					
					//API����� ������ ��쿡�� ���
					cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					if(cm.getActiveNetworkInfo() != null){
						//API ���
						try{
							enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(WizSafeGetLocation.this));
							dbInsertDate = WizSafeUtil.getInsertDbDate();
							url = "https://www.heream.com/api/insertLocation.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&dbInsertDate=" + URLEncoder.encode(dbInsertDate) + "&lat=" + URLEncoder.encode(enc_lat) + "&lon=" + URLEncoder.encode(enc_lon) + "&type=" + URLEncoder.encode(provider) + "&hiddenUser=" + URLEncoder.encode(hiddenUser);
							urlConn = (HttpURLConnection) new URL(url).openConnection();
							urlConn.setConnectTimeout(3000);
							urlConn.setReadTimeout(3000);
							br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
						}catch(Exception e){
						}finally{
							if(br != null){ br.close(); }
							if(urlConn != null) {urlConn.disconnect(); urlConn = null;}
						}
					}
				}
				pHandler.sendEmptyMessage(0);
			}catch(Exception e){
				pHandler.sendEmptyMessage(0);
			}
		}
	}
	
	//API����� ������ �� �ڿ� �ڵ鷯�ν� �ش� ���񽺸� �����Ѵ�.
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			if(msg.what == 0){
  				cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
  				stopService(new Intent().setComponent(cn));
  			}
  		}
  	};
  	
  	//���� ������ ������ ������ �Ǻ�(������ ���� GPS�� ��ã�°Ű��Ƽ�)
  	GpsStatus.NmeaListener gpsQuentityLoad = new GpsStatus.NmeaListener(){
		public void onNmeaReceived(long timestamp, String nmea) {
		}
  	};
}