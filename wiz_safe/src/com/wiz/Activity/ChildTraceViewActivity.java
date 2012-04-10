/* 
 * NMapViewer.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */
                    
package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.NMapCalloutBasicOverlay;
import com.nhn.android.mapviewer.NMapPOIflagType;
import com.nhn.android.mapviewer.NMapViewerResourceProvider;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.wiz.Activity.ChildListActivity.ChildDetail;
import com.wiz.Activity.ChildLocationViewActivity.CallGetNowLocationApiThread;
import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

/**
 * Sample class for map viewer library.
 * 
 * @author kyjkim
 */
public class ChildTraceViewActivity extends NMapActivity {	
	
	//여기부터 네이버 맵을 사용하는 변수 선언
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	private NMapView mMapView; 
	private NMapController mMapController;
	
	//API 통신에 사용할 변수
	private int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	private String childCtn;
	private String selectedDay;
	private String startTime;
	private String endTime;
	private String interval;
	//맵위에 위치를 표시할 값을 가진 arraylist
	ArrayList<ChildTraceViewDetail> childTraceViewListArr = new ArrayList<ChildTraceViewDetail>();

	//최초 맵 기준  지정 변수 -> 현재는 시청으로 나중에는 자신의 위치로 변경하자.
	private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
	private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "12602e7037542bb1f774834ff437c15c";

	private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

	private SharedPreferences mPreferences;

	private NMapOverlayManager mOverlayManager;

	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;

	private NMapViewerResourceProvider mMapViewerResourceProvider;

	private NMapPOIdataOverlay mFloatingPOIdataOverlay;
	private NMapPOIitem mFloatingPOIitem;


	
	
	/** Called when the activity is first created. */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_trace_view); //XML로 생성한 맵뷰를 SetContentView로 현재 레이아웃으로 셋팅
	
		//이전액티비티에서 넘겨준 값을 받아온다.
		Intent intent = getIntent();
		childCtn = intent.getStringExtra("phonenum");
		selectedDay = intent.getStringExtra("selectedDay");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        interval = intent.getStringExtra("interval");
		
    	//API 호출 쓰레드 시작
    	//class 최초 진입시 api 통신으로 위도경도를 가져온다.
    	WizSafeDialog.showLoading(ChildTraceViewActivity.this);	//Dialog 보이기
    	CallGetChildTraceViewApiThread thread = new CallGetChildTraceViewApiThread(); 
		thread.start();
		
		//먼저 해당 뷰의 부모를 초기화 - 하나의 뷰는 하나의 부모만을 가지기 때문에 부모를 초기화하여 재사용을 하자.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		parentView.removeView(mMapView);
		
        //body
	        
		//=====================================================//
		// 여기부터 맵 생성 및 보여주기
		//=====================================================//		
		
		// create map view
		//mMapView = new NMapView(this);
        mMapView = (NMapView)findViewById(R.id.mapview); //앞으로의 작업을 위해 mapview의 객체를 하나 생성한다.
		// set a registered API key for Open MapViewer Library
		mMapView.setApiKey(API_KEY);
		
		// initialize map view
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
		mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();

		// use built in zoom controls
		NMapView.LayoutParams lp = new NMapView.LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);

		// create resource provider
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

		// set data provider listener
		super.setMapDataProviderListener(onDataProviderListener);

		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

		// compass manager
		mMapCompassManager = new NMapCompassManager(this);

		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
		
		//show map path data
		mOverlayManager.clearOverlays();

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {

		// save map view state such as map center position and zoom level.
		saveInstanceState();

		super.onDestroy();
	}

    
    //API 호출 쓰레드
  	class CallGetChildTraceViewApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(childCtn);
  				String url = "https://www.heream.com/api/getChildTraceDetailView.jsp?ctn="+ URLEncoder.encode(enc_ctn)+"&selectedDay="+selectedDay+"&startTime="+startTime+"&endTime="+endTime+"&interval="+interval;
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  					Log.i("banhong", " :: "+new String(temp));
  				}
  				//결과를 XML 파싱하여 추출
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String strDate1 = WizSafeParser.xmlParser_String(returnXML,"<DATE>");
  				ArrayList<String> strDate2 = WizSafeParser.xmlParser_List(returnXML,"<DATE>");
  				ArrayList<String> strHour = WizSafeParser.xmlParser_List(returnXML,"<HOUR>");
  				ArrayList<String> encLongitude = WizSafeParser.xmlParser_List(returnXML,"<LONGITUDE>");
  				ArrayList<String> encLatitude = WizSafeParser.xmlParser_List(returnXML,"<LATITUDE>");
  				ArrayList<String> encAddress = WizSafeParser.xmlParser_List(returnXML,"<ADDRESS>");
  				ArrayList<String> strType = WizSafeParser.xmlParser_List(returnXML,"<TYPE>");
  				ArrayList<String> strHiddenState = WizSafeParser.xmlParser_List(returnXML,"<HIDDEN_STATE>");

  				//필요한 데이터 타입으로 형변환
  				httpResult = Integer.parseInt(resultCode);	
  				selectedDay = strDate1;
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				for(int i=0; i < strHour.size(); i++){
  					ChildTraceViewDetail tempBean = new ChildTraceViewDetail();
  					tempBean.setDay(strDate2.get(i));
  					tempBean.setHour(strHour.get(i));
  					tempBean.setLatitude(Double.parseDouble(WizSafeSeed.seedDec(encLatitude.get(i))));
  					tempBean.setLongitude(Double.parseDouble(WizSafeSeed.seedDec(encLongitude.get(i))));
  					tempBean.setAddress(WizSafeSeed.seedDec(encAddress.get(i)));
  					tempBean.setType(strType.get(i));
  					tempBean.setHiddenState(strHiddenState.get(i));
  					
  					childTraceViewListArr.add(tempBean);
  				}
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}

  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//핸들러 정상동작
  				if(httpResult == 0){
					//조회성공
  					
  					// add path POI data overlay
  					testPathPOIdataOverlay();
  					
  					TextView tv_checkTime = (TextView)findViewById(R.id.textView1); 
  			        if(tv_checkTime != null){
  			        	//연결 상태 확인하여 오차범위를 보여준다. - 미구현
  			        	String gap = "오차범위 : 50m ~2km";
  			        	
  			        	tv_checkTime.setText("일자 : "+WizSafeUtil.getDateFormat(selectedDay) +"\n"+ gap);
  			        }
			    	
				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceViewActivity.this);
					String title = "자녀현위치찾기실패";	
					String message = "자녀 현위치 정보를 조회할 수 없습니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}
  			}else if(msg.what == 1){
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceViewActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}
  		}
  	};
  	
	//이동경로 중에 중요 지점을 오버레이로 표시하기 위한 메소드 
	private void testPathPOIdataOverlay() {

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(4, mMapViewerResourceProvider, true);
		
		//api통신하여 받아온 결과 크기만큼 poi아이템을 추가한다.
		poiData.beginPOIdata(childTraceViewListArr.size());
		for(int i=0; i < childTraceViewListArr.size(); i++){
			ChildTraceViewDetail tempBean = new ChildTraceViewDetail();
			tempBean = childTraceViewListArr.get(i);
			double tmpLongitude = tempBean.getLongitude();
			double tmpLatitude = tempBean.getLatitude();
			String dateInfo = WizSafeUtil.getDateFormat(((String)tempBean.getDay()+(String)tempBean.getHour()));
			
			if(i == 0){
				//첫번째 데이터는 시작PIN을 사용한다.
				poiData.addPOIitem(tmpLongitude, tmpLatitude, dateInfo, NMapPOIflagType.CUSTOM_BASE + 2, 0);
			}else if(i == childTraceViewListArr.size()){
				//마지막 데이터는 종료PIN을 사용한다.
				poiData.addPOIitem(tmpLongitude, tmpLatitude, dateInfo, NMapPOIflagType.CUSTOM_BASE + 4, 0);
			}else{
				//처음과 마지막을 제외한 나머지는 일반PIN을 사용한다.
				poiData.addPOIitem(tmpLongitude, tmpLatitude, dateInfo, NMapPOIflagType.CUSTOM_BASE + 3, 0);
			}
			
		}
		//poi 데이터 추가를 종료한다.
		poiData.endPOIdata();

		// create POI data overlay
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

		// set event listener to the overlay
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
		
		if (poiDataOverlay != null) {		//오버레이가 정상 생성되어 존재한다면
			
			poiDataOverlay.showAllPOIdata(0);
		}
		
	}

	/* NMapDataProvider Listener */
	private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

		@Override
		public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

			if (DEBUG) {
				Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
					+ ((placeMark != null) ? placeMark.toString() : null));
			}

			if (errInfo != null) {
				Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

				Toast.makeText(ChildTraceViewActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
				return;
			}

			if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
				mFloatingPOIdataOverlay.deselectFocusedPOIitem();

				if (placeMark != null) {
					mFloatingPOIitem.setTitle(placeMark.toString());
				}
				mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
			}
		}

	};

	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}

			return true;
		}

		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

			// stop location updating
			//			Runnable runnable = new Runnable() {
			//				public void run() {										
			//					stopMyLocation();
			//				}
			//			};
			//			runnable.run();	

			Toast.makeText(ChildTraceViewActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(ChildTraceViewActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
		}

	};

	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
				//restoreInstanceState();

			} else { // fail
				Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

				Toast.makeText(ChildTraceViewActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
			}
		}

		@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}

		@Override
		public void onMapCenterChangeFine(NMapView mapView) {

		}
	};

	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

		@Override
		public void onLongPress(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLongPressCanceled(NMapView mapView) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTouchDown(NMapView mapView, MotionEvent ev) {

		}

		@Override
		public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
		}

	};

	private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

		@Override
		public boolean isLocationTracking() {
			if (mMapLocationManager != null) {
				if (mMapLocationManager.isMyLocationEnabled()) {
					return mMapLocationManager.isMyLocationFixed();
				}
			}
			return false;
		}

	};

	/* POI data State Change Listener*/
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

		@Override
		public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
			}
			
			// [[TEMP]] handle a click event of the callout
			Toast.makeText(ChildTraceViewActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			if (DEBUG) {
				if (item != null) {
					Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
				} else {
					Log.i(LOG_TAG, "onFocusChanged: ");
				}
			}
		}
	};

	private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

		@Override
		public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
			Rect itemBounds) {

			// handle overlapped items
			if (itemOverlay instanceof NMapPOIdataOverlay) {
				NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

				// check if it is selected by touch event
				if (!poiDataOverlay.isFocusedBySelectItem()) {
					int countOfOverlappedItems = 1;

					NMapPOIdata poiData = poiDataOverlay.getPOIdata();
					for (int i = 0; i < poiData.count(); i++) {
						NMapPOIitem poiItem = poiData.getPOIitem(i);

						// skip selected item
						if (poiItem == overlayItem) {
							//맵 위에 오버레이를 선택했을때 수행되는 메소드 영역
							continue;
						}

						// check if overlapped or not
						if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
							countOfOverlappedItems++;
						}
					}

					if (countOfOverlappedItems > 1) {
						//오버랩 되있는 아이템이 1개 이상이면 이곳을 수행한다.
						//String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
						//Toast.makeText(ChildTraceViewActivity.this, text, Toast.LENGTH_LONG).show();
						//return null;
					}
				}
			}

			// use custom callout overlay
			//return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

			// set basic callout overlay
			return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds, ChildTraceViewActivity.this);	 		
		}

	};

	/* Local Functions */

	private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);

		int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
		int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
		int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
	
		mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);
	}

	private void saveInstanceState() {
		if (mPreferences == null) {
			return;
		}

		NGeoPoint center = mMapController.getMapCenter();
		int level = mMapController.getZoomLevel();
		int viewMode = mMapController.getMapViewMode();
		boolean trafficMode = mMapController.getMapViewTrafficMode();
		boolean bicycleMode = mMapController.getMapViewBicycleMode();

		SharedPreferences.Editor edit = mPreferences.edit();

		edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
		edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
		edit.putInt(KEY_ZOOM_LEVEL, level);
		edit.putInt(KEY_VIEW_MODE, viewMode);
		edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
		edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

		edit.commit();

	}

	
	   
    class ChildTraceViewDetail {
    	private String day;
    	private String hour;
    	private double latitude;
		private double longitude;
    	private String address;
    	private String type;
    	private String hiddenState;
    	
    	public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
    	private String getHour() {
			return hour;
		}
		private void setHour(String hour) {
			this.hour = hour;
		}
		private double getLatitude() {
			return latitude;
		}
		private void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		private double getLongitude() {
			return longitude;
		}
		private void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		private String getAddress() {
			return address;
		}
		private void setAddress(String address) {
			this.address = address;
		}
		private String getType() {
			return type;
		}
		private void setType(String type) {
			this.type = type;
		}
		private String getHiddenState() {
			return hiddenState;
		}
		private void setHiddenState(String hiddenState) {
			this.hiddenState = hiddenState;
		}
    	
    	
    }
    	
}