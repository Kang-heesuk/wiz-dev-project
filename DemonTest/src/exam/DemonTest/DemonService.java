package exam.DemonTest;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.params.HttpAbstractParamBean;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class DemonService extends Service implements LocationListener{
	
	int count = 0;
	
	boolean Quit;
	private LocationManager mLocationManager;
	private Location mLocation = null;
	
	public void onCreate(){ 
		super.onCreate();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		Quit = true;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		
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
		return START_STICKY;
	}
	
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class DemonThread extends Thread{ 
		public void run(){
			for(int i = 0 ; Quit == false ; i++){
				count++;

				if(mLocation != null){
					double lat = mLocation.getLatitude();
					double lon = mLocation.getLongitude();
					Log.i("JYJ test", lat + " // " + lon);
					callInsertLoactionApi(lat, lon);
				}
				
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
			URL url = new URL("http://210.109.111.212/tb/JYJ/testFile/promotionTotal.jsp?lat=" + lat + "&lon=" + lon);
			URLConnection url_conn = null;
			url_conn = (HttpURLConnection)url.openConnection();
			BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(url_conn.getInputStream()));
			Log.i("JYJ test", "====== Call API Success ======");
		}catch(Exception e){
			Log.i("JYJ test", "Connection Fail : " + e.toString());
		}
	}

}
