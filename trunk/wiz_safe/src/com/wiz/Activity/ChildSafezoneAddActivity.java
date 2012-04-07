/* 
 * NMapViewer.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package com.wiz.Activity;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.NMapViewerResourceProvider;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;


/**
 * Sample class for map viewer library.
 * 
 * @author kyjkim
 */
public class ChildSafezoneAddActivity extends NMapActivity {	
	
	//여기부터 네이버 맵을 사용하는 변수 선언
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "12602e7037542bb1f774834ff437c15c";

	private MapContainerView mMapContainerView;
	
	private NMapView mMapView; 
	private NMapController mMapController;
 
	//반경을 그리기 위한 오버레이 선언
	private RadiusOverlay radiusOverlay;
	//반경 정보 값 200,500,1000 으로 변환
	private int radiusValue = 200;
	//최초 시작지점 좌표
	private double longitude;
	private double latitude;

	//검색창
	EditText searchArea;
	//키보드 보이고 안보이고
	InputMethodManager mImm;
	
	//화면 진입시 맵뷰 중앙 시작지점
	private NGeoPoint startPosition;
	
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 10; 
	private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

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
		setContentView(R.layout.child_safezone_add); //XML로 생성한 맵뷰를 SetContentView로 현재 레이아웃으로 셋팅
		
		//먼저 해당 뷰의 부모를 초기화 - 하나의 뷰는 하나의 부모만을 가지기 때문에 부모를 초기화하여 재사용을 하자.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		parentView.removeView(mMapView);
		
        //body
        
        //검색창 영역 
        searchArea = (EditText)findViewById(R.id.tv_search);
        searchArea.setFilters(new InputFilter[] {
        	new InputFilter.LengthFilter(20)	
        });
     
        
        mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //검색버튼액션 정의
        findViewById(R.id.btn_search).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			switch(v.getId()){
	    			case R.id.btn_search: 
	    				mImm.hideSoftInputFromWindow(searchArea.getWindowToken(),0);	//키보드를 숨기고
	    				goSearch(searchArea.getText().toString());
	    				break;
    			}
    			Toast.makeText(ChildSafezoneAddActivity.this, "clicked search btn!!", Toast.LENGTH_SHORT).show();
    		}

    	    public void goSearch(String searchStr){
    	    	
    	    	int read = 0;
    	    	byte[] buf = new byte[1024];
    	    	InputStream is = null;
    	    	
    	    	try{
    	    		String result = "";
    	    		String url = "http://210.109.111.212/tb/JYJ/appApi/smsAuthSend.jsp?ctn=" + searchStr;
    	    		is = (new URL(url)).openStream();
    	    		while((read=is.read(buf)) != -1){
    	    			result = result + new String(buf,0,read,"utf-8");
    	    		}
    	    		Log.i("authTest","result==" + result);
    	    		if(result.indexOf("0") > -1){
    	    			//성공 처리
    	    		}else{
    	    			//실패 처리
    	    		}
    	    	}catch(Exception e){
    	    		
    	    	}finally{
    	    		if(is != null){ try{is.close();}catch(Exception e){} }
    	    	}
    	    }
        });
        
        //반경 정보 변경 버튼
        final Button btn_radius = (Button)findViewById(R.id.btn_radius);
        btn_radius.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				if(radiusValue == 200){
					radiusValue = 500;
					btn_radius.setBackgroundResource(R.drawable.btn_m_500_selector); 
				}else if(radiusValue == 500){
					radiusValue = 1000;
					btn_radius.setBackgroundResource(R.drawable.btn_m_1k_selector);
				}else{
					radiusValue = 200;
					btn_radius.setBackgroundResource(R.drawable.btn_m_200_selector);
				}
				
				//radius overlay 를 다시 그린다.
				// 맵뷰에 있는 오버레이를 모두 가져온다.
				List<NMapOverlay> overlays = mMapView.getOverlays();
				overlays.clear();
				// 신규 레이더 오버레이를 만든다.
				radiusOverlay = new RadiusOverlay(radiusValue);
				overlays.add(radiusOverlay);
				mOverlayManager.populate();
				
			}
		});
        
        //설정 버튼
        Button btn_setup = (Button)findViewById(R.id.btn_setup);
        btn_setup.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "안심존 등록";	
				String message = "";
				//if("안심존 등록 건수가 1개 이상일 경우는"){
					message = "현재 시간부터 24시간 이내에 해당 위치에 진입 시 문자로 1회만 알려 드립니다. \n ※ 안심존 추가 등록 시 100 포인트가 소진됩니다.";
				//}else{
					message = "현재 시간부터 24시간 이내에 해당 위치에 진입 시 문자로 1회만 알려 드립니다.";
				//}
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setPositiveButton(R.string.btn_regist, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(ChildSafezoneAddActivity.this, "등록눌렀다.api 통신 궈궈싱 ", Toast.LENGTH_SHORT).show();
					}
				});
				ad.setNegativeButton(R.string.btn_cancel, null);
				ad.show();
			}
		});
        
        //취소 버튼
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				ChildSafezoneAddActivity.this.finish();
			}
		});
        
        
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
	
		//현재 위치탐색 시작
		if (mMapLocationManager != null) {
			if(mMapLocationManager.enableMyLocation(false)){
				Log.i("banhong", "현재 위치를 탐색중입니까 === "+mMapLocationManager.isMyLocationEnabled());
				boolean scanningNowLocationFinished = false;
				while(true){
					Log.i("banhong", "현재 위치를 탐색중");
					if(mMapLocationManager.isMyLocationEnabled()){
						scanningNowLocationFinished = true;
						break;
					}
				}
				Log.i("banhong", "현재 위치 탐색이 끝났습니까=== "+ scanningNowLocationFinished);
				if(scanningNowLocationFinished) {
					
					Log.i("banhong", "현재 위치값을 구했습니까=== "+mMapLocationManager.isMyLocationFixed());
					if(mMapLocationManager.isMyLocationFixed()){
					
						startPosition = mMapLocationManager.getMyLocation();

						longitude = startPosition.getLongitudeE6();
						latitude = startPosition.getLatitudeE6();
					}else{
						//현재 위치 탐색이 실패한 경우 지정한 위도,경도를 넣는다.
						longitude = 126.978371;
						latitude = 37.5666091;
					}
				}
			}
		}
		// 맵뷰에 있는 오버레이를 모두 가져온다.
		List<NMapOverlay> overlays = mMapView.getOverlays();

		//NMapMyLocationOverlay 객체를 제외한 나머지 오버레이를 모두 제거한다.
		//mOverlayManager.clearOverlays();
		
		// 신규 레이더 오버레이를 만든다. - 기본 200m 반경
		radiusOverlay = new RadiusOverlay(200);
		
		overlays.add(radiusOverlay);
		
		mOverlayManager.populate();

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

	/* Test Functions */

	
	//액티비티가 활성화되면 mylocation 이 시작됨
	private void startMyLocation() {

		if (mMyLocationOverlay != null) {	//최초 내위치용 오버레이가 생성된 경우만 사용
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {	//현재 오버레이 매니져가 my location 오버레이를 가지고 있지 않으면
				mOverlayManager.addOverlay(mMyLocationOverlay);	//my location 오버레이를 추가
			}

			if (mMapLocationManager.isMyLocationEnabled()) {	//이미 내위치를 확인하는 오버레이가 있는 경우 나침반을 이용한 컴퍼스를 보여준다.

				if (!mMapView.isAutoRotateEnabled()) {	//나침반 기능이 활성화 되어있지 않으면
					mMyLocationOverlay.setCompassHeadingVisible(true);

					mMapCompassManager.enableCompass();

					mMapView.setAutoRotateEnabled(true, false);

					mMapContainerView.requestLayout();
				} else {	//나침반 컴퍼스가 활성화 되어 있으면 my location 정지!
					stopMyLocation();
				}

				mMapView.postInvalidate();	//별도의 스레드에서 화면갱신을 할때 사용
			} else {
				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
				if (!isMyLocationEnabled) {
					Toast.makeText(ChildSafezoneAddActivity.this, "Please enable a My Location source in system settings",
						Toast.LENGTH_LONG).show();

					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);

					return;
				}
			}
		}
	}
	
	//액티비티가 활성화되면 mylocation 이 중지됨
		private void stopMyLocation() {
			if (mMyLocationOverlay != null) {	//최초  내위치용 오버레이가 생성된 경우만 사용
				mMapLocationManager.disableMyLocation();

				if (mMapView.isAutoRotateEnabled()) {
					mMyLocationOverlay.setCompassHeadingVisible(false);

					mMapCompassManager.disableCompass();

					mMapView.setAutoRotateEnabled(false, false);

					mMapContainerView.requestLayout();
				}
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

				Toast.makeText(ChildSafezoneAddActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
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

			Toast.makeText(ChildSafezoneAddActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(ChildSafezoneAddActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
		}

	};

	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
				restoreInstanceState();

			} else { // fail
				Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

				Toast.makeText(ChildSafezoneAddActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
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
		}

		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			//zoom 변화 시에 화면의 반경 오버레이를 다시 그려준다.
			// 맵뷰에 있는 오버레이를 모두 가져온다.
			List<NMapOverlay> overlays = mMapView.getOverlays();
			overlays.clear();
			// 신규 레이더 오버레이를 만든다.
			radiusOverlay = new RadiusOverlay(radiusValue);
			overlays.add(radiusOverlay);
			mOverlayManager.populate();
			
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
							continue;
						}

						// check if overlapped or not
						if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
							countOfOverlappedItems++;
						}
					}

					if (countOfOverlappedItems > 1) {
						String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
						Toast.makeText(ChildSafezoneAddActivity.this, text, Toast.LENGTH_LONG).show();
						return null;
					}
				}
			}

			// use custom callout overlay
			return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

			// set basic callout overlay
			//return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);			
		}

	};

	/* Local Functions */

	private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);
		
		int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);
		
		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
		//시작 축척 레벨을 10로 고정
		level = 10;
		mMapController.setMapCenter(new NGeoPoint(longitude, latitude), level);
		
		//내위치로 이동
		startMyLocation();
		Log.i("banhong", "내위치로 이동해!!!'");
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

	
	 /** 
		 * Container view class to rotate map view.
		 */
		private class MapContainerView extends ViewGroup {

			public MapContainerView(Context context) {
				super(context);
			}

			@Override
			//view 가 자식들에게 크기와 위치를 할당할때 호출된다.
			protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
				final int width = getWidth();
				final int height = getHeight();
				final int count = getChildCount();
				for (int i = 0; i < count; i++) {
					final View view = getChildAt(i);
					final int childWidth = view.getMeasuredWidth();
					final int childHeight = view.getMeasuredHeight();
					final int childLeft = (width - childWidth) / 2;
					final int childTop = (height - childHeight) / 2;
					view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
				}
				
				if (changed) {
					
					//뷰의 크기가 바뀔대마다 호출됩니다.
					//mOverlayManager.onSizeChanged(width, height);
					//에러를 발생해서 기존의 안드로이드의 메소드를 실행
					onSizeChanged(width, height, width, height);
				}
			}

			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
				int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
				int sizeSpecWidth = widthMeasureSpec;
				int sizeSpecHeight = heightMeasureSpec;

				final int count = getChildCount();
				for (int i = 0; i < count; i++) {
					final View view = getChildAt(i);

					if (view instanceof NMapView) {
						if (mMapView.isAutoRotateEnabled()) {
							int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
							sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
							sizeSpecHeight = sizeSpecWidth;
						}
					}

					view.measure(sizeSpecWidth, sizeSpecHeight);
				}
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		}

	
	class RadiusOverlay extends NMapOverlay{

		int current_x;
		int current_y;
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int windowWidth = display.getWidth();
		int windowHeight = display.getHeight();
		int radius = 200;
			
		public RadiusOverlay(int raidusValue){
			current_x = windowWidth / 2;
			current_y = windowHeight / 2;
			
			if(raidusValue != 200){
				radius = raidusValue;
			}
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent e, NMapView mapView) {

			//e.getAction() 터치 이벤트 값이 ACTION_DOWN -> ACTION_MOVE -> ACTION_UP 순으로 반환
			// TODO Auto-generated method stub
			return super.onTouchEvent(e, mapView);
		}

		@Override
		public void draw(Canvas canvas, NMapView mapView, boolean shadow) {
			NMapProjection projection = mapView.getMapProjection();
			Point point = new Point();
			
			if (shadow == false) {
				// Get the current location
				//맵뷰에 터치한 좌표에 해당하는 위도경도를 가져온다.
				NGeoPoint geop = mapView.getMapProjection().fromPixels(current_x, current_y);
				
				//원을 그리기 위한 paint 정보 선언
				Paint p1 = new Paint();
				p1.setARGB(60,255,93,64);
				p1.setAntiAlias(true); 
				
				//테두리를 그리기 위한 paint 정보 선언
				Paint p2 = new Paint();
				p2.setColor(Color.GRAY);
				p2.setStyle(Paint.Style.STROKE);
				p2.setStrokeWidth(4);
				
				// GeoPoint를 스크린 좌표로 변환
				point = projection.toPixels(geop,point);

				float radiusPixel = projection.metersToPixels(radius);
				canvas.drawCircle(current_x, current_y-110, radiusPixel, p1);
				canvas.drawCircle(current_x, current_y-110, radiusPixel, p2);
				
				
				//범위 중앙에 +표시를 위한 paint 정보 선언
				Paint p3 = new Paint();
				p3.setARGB(200,255,44,44);
				p3.setTextAlign(Align.CENTER);
				p3.setTypeface(Typeface.create((String)null, Typeface.BOLD));
				p3.setTextSize(50);
				canvas.drawText("+", current_x, current_y - 100, p3);
				//중앙에 +표시를 이미지로 하려면 아래 코드를 사용
				//Drawable centerMark = getResources().getDrawable(R.drawable.notice_popup_ck);
				//Bitmap bitmap = ((BitmapDrawable)centerMark).getBitmap();
				//canvas.drawBitmap(bitmap, current_x, current_y - 100, null);
				
				
				//반경 원 위쪽에 미터 정보를 표시
				Drawable dis_info = getResources().getDrawable(R.drawable.d_200m);
				switch (radius) {
					case 200 : dis_info = getResources().getDrawable(R.drawable.d_200m);
						break;
					case 500 : dis_info = getResources().getDrawable(R.drawable.d_500m);
						break;
					case 1000 : dis_info = getResources().getDrawable(R.drawable.d_1km);
						break;
					default : dis_info = getResources().getDrawable(R.drawable.d_200m);
						break;
				}
				
				Bitmap bitmap2 = ((BitmapDrawable)dis_info).getBitmap();
				canvas.drawBitmap(bitmap2, current_x - ((windowWidth*6)/58), current_y - radiusPixel - (windowHeight*12)/99, null);
				
			}
			super.draw(canvas,mapView,shadow);
		}
		
		
		@Override
		public boolean onTap(int x, int y, NMapView mapView) {

			//소프트 키보드가 있을 경우 숨긴다.
			mImm.hideSoftInputFromWindow(searchArea.getWindowToken(),0);	//키보드를 숨기고
			
			//맵뷰에 터치한 좌표에 해당하는 위도경도를 가져온다.
			NGeoPoint geop = mapView.getMapProjection().fromPixels(x, y);
							
			//해당  위도경도 값이 맵 중심이 되도록 맵을 이동시킨다.
			mMapController.animateTo(geop);
			
			// TODO Auto-generated method stub
			return false;
		}
	
	}
	
	
	
	
	
	
	
	
}