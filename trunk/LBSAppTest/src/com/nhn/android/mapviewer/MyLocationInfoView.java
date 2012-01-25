package com.nhn.android.mapviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;


public class MyLocationInfoView extends NMapActivity {
	
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	private NMapView mMapView; 
	private NMapController mMapController;
	
	
	//최초 맵 기준  지정 변수 -> 현재는 시청으로 나중에는 자신의 위치로 변경하자.
	private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
	//private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(127.12201246666667, 37.495217644444445);
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
	private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

	private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "36edac1675eb7c7b1bb578113e82d540";

	private MapContainerView mMapContainerView;

	private SharedPreferences mPreferences;
	
	private NMapOverlayManager mOverlayManager;

	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;

	private NMapViewerResourceProvider mMapViewerResourceProvider;    
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info); 
        
 		
        String test1 = this.getIntent().getStringExtra("reqId");
        String test2 = this.getIntent().getStringExtra("reqCtn");
        
       
		//여기에서 디비를 조회해서 latitude, longitude 값을 가져온다.
		//double 형 변수 , double 형 변수
		double longitude01 = 127.12201246666667;
		double latitude01 = 37.495217644444445;
		
		
		//위에서 조회한 latitude, longitude 값으로 맵에 표시해보자.
		// create map view
 		mMapView = new NMapView(this);

 		// set a registered API key for Open MapViewer Library
 	 	mMapView.setApiKey(API_KEY);
 	 	
 	 	// create parent view to rotate map view
 		mMapContainerView = new MapContainerView(this);
 		mMapContainerView.addView(mMapView);

 		// set the activity content to the parent view
 		setContentView(mMapContainerView);

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

 	 
 		// create my location overlay - 현재 내위치를 보여주는 오버레이 : 위에서 파라메터 값을 반드시 생성해주어야 한다.
 		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
 		/*
        Button btn_go_main = (Button) findViewById(R.id.btn_go_main);
        btn_go_main.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(LocationInfoView.this, MainActivity.class);
				startActivity(intent);
				
				Log.d("location", "btn_go_main pressed");
			}
		});
		*/
 		

 		startMyLocation();
    }

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
  					Toast.makeText(MyLocationInfoView.this, "Please enable a My Location source in system settings",
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

		//@Override
		public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

			if (DEBUG) {
				Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
					+ ((placeMark != null) ? placeMark.toString() : null));
			}

			if (errInfo != null) {
				Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

				Toast.makeText(MyLocationInfoView.this, errInfo.toString(), Toast.LENGTH_LONG).show();
				return;
			}
/*
			if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
				mFloatingPOIdataOverlay.deselectFocusedPOIitem();

				if (placeMark != null) {
					mFloatingPOIitem.setTitle(placeMark.toString());
				}
				mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
			}
*/			
		}

	};
	
	

	private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

		//@Override
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
						Toast.makeText(MyLocationInfoView.this, text, Toast.LENGTH_LONG).show();
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

	
	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		//@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}

			return true;
		}

		//@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

			// stop location updating
			//			Runnable runnable = new Runnable() {
			//				public void run() {										
			//					stopMyLocation();
			//				}
			//			};
			//			runnable.run();	

			Toast.makeText(MyLocationInfoView.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		//@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(MyLocationInfoView.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

			stopMyLocation();
		}

	};
	
	
	
	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		//@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
				restoreInstanceState();

			} else { // fail
				Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

				Toast.makeText(MyLocationInfoView.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
			}
		}

		//@Override
		public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
			}
		}

		//@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

		//@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}

		//@Override
		public void onMapCenterChangeFine(NMapView mapView) {

		}
	};
    
	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

		//@Override
		public void onLongPress(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onLongPressCanceled(NMapView mapView) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onTouchDown(NMapView mapView, MotionEvent ev) {

		}

		//@Override
		public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
		}

	};
	
	private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

		//@Override
		public boolean isLocationTracking() {
			if (mMapLocationManager != null) {
				if (mMapLocationManager.isMyLocationEnabled()) {
					return mMapLocationManager.isMyLocationFixed();
				}
			}
			return false;
		}

	};


    
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

	
}