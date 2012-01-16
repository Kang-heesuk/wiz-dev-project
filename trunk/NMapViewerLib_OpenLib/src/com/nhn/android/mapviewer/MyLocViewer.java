/**
 *  Program : GPS 위치 정보 구하기
 *  Author  : gxgsung
 *  Date    : 2010.12.31
 *  HomeUrl : gxgsung.blog.me
 */

package com.nhn.android.mapviewer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MyLocViewer extends Activity implements LocationListener {
	
	private LocationManager locManager;
	Geocoder geoCoder;
	private Location myLocation = null;
	double latPoint = 0;
	double lngPoint = 0;
	float  speed = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		// LocationListener의 핸들을 얻음
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //==================================================//
        // GPS or Network 중 정확한 정보를 가져오는 방법
        //==================================================//

        Criteria criteria = new Criteria();
        
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);	//GPS or Network
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);	//GPS or Network
        
        //바로 위에서 가져온 위치 정보중 더 정확한 정보를 선택한다.
        String provider = locManager.getBestProvider(criteria, true);
        	
        if("network".equals(provider)){
        	// 기지국으로 부터 위치정보를 업데이트 요청
	        locManager.requestLocationUpdates(provider, 1000, 0, this);
        }else{
        	// GPS로 부터 위치정보를 업데이트 요청, 1초마다 5km 이동시
	        locManager.requestLocationUpdates(provider, 1000, 5, this);
        }
        
        // 주소를 확인하기 위한 Geocoder KOREA 와 KOREAN 둘다 가능
        geoCoder = new Geocoder(this, Locale.KOREAN); 

        final Button gpsButton = (Button) findViewById(R.id.gpsButton);
		gpsButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				GetLocations();
				Log.d("location", "gpsButton pressed");
			}
		});

		final Button viewMapButton = (Button) findViewById(R.id.viewMapButton);
		viewMapButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.d("location", "viewMapButton pressed");

				StringBuffer juso = new StringBuffer();

				
				//다음 activity 로 이동한다. 값을 싣고 이동
				if (myLocation != null) {
					latPoint = myLocation.getLatitude();
					lngPoint = myLocation.getLongitude();
					speed = (float)(myLocation.getSpeed() * 3.6);

					try {
						// 위도,경도를 이용하여 현재 위치의 주소를 가져온다. 
						List<Address> addresses;
						addresses = geoCoder.getFromLocation(latPoint, lngPoint, 1);
						for(Address addr: addresses){
							int index = addr.getMaxAddressLineIndex();
							for(int i=0;i<=index;i++){
								juso.append(addr.getAddressLine(i));
								juso.append(" ");
							}
							juso.append("\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
				Intent intent = new Intent(MyLocViewer.this, NMapViewer.class);
				intent.putExtra("latitude", String.valueOf(latPoint));
				intent.putExtra("longitude", String.valueOf(lngPoint));
				intent.putExtra("speed", String.valueOf(speed));
				intent.putExtra("address", String.valueOf(juso));
				startActivity(intent);
			}
		});

    }
    
	public void GetLocations() {
		// 텍스트뷰를 찾음
		TextView latText = (TextView) findViewById(R.id.tvLatitude);
		TextView lngText = (TextView) findViewById(R.id.tvLongitude);
		TextView speedText = (TextView) findViewById(R.id.tvSpeed);
		TextView jusoText = (TextView) findViewById(R.id.tvAddress);
		StringBuffer juso = new StringBuffer();

		if (myLocation != null) {
			latPoint = myLocation.getLatitude();
			lngPoint = myLocation.getLongitude();
			speed = (float)(myLocation.getSpeed() * 3.6);

			try {
				// 위도,경도를 이용하여 현재 위치의 주소를 가져온다. 
				List<Address> addresses;
				addresses = geoCoder.getFromLocation(latPoint, lngPoint, 1);
				for(Address addr: addresses){
					int index = addr.getMaxAddressLineIndex();
					for(int i=0;i<=index;i++){
						juso.append(addr.getAddressLine(i));
						juso.append(" ");
					}
					juso.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		latText.setText(String.valueOf(latPoint));
		lngText.setText(String.valueOf(lngPoint)); 
		speedText.setText(String.valueOf(speed));
		jusoText.setText(String.valueOf(juso));
	}

	public void onLocationChanged(Location location) {
		Log.d("location", "location changed");
		myLocation = location;
	}

	public void onProviderDisabled(String s) {

	}

	public void onProviderEnabled(String s) {

	}

	public void onStatusChanged(String s, int i, Bundle bundle) {

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	
		menu.add(0,1,0,"프로그램 소개");
		menu.add(0,2,0,"종료");

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
    		new AlertDialog.Builder(this)
    		.setTitle("프로그램 소개")
    		.setMessage("안드로이드 학습을 위하여 만든 어플입니다.\n" +
    				"소스코드가 공개되어 있으며 자유롭게 사용하실수 있습니다.\n" +
    				"http://gxgsung.blog.me")
    		.setIcon(R.drawable.icon)
    		.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
    		.show();
			return true;
		case 2:
			finish();
			System.exit(0);
			return true;			
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			System.exit(0);
		case KeyEvent.KEYCODE_HOME:
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
		}
		return super.onKeyDown(keyCode, event);
	}

	
}


