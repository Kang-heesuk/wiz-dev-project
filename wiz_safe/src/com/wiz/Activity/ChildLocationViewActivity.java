package com.wiz.Activity;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.nhn.android.mapviewer.NMapPOIflagType;
import com.nhn.android.mapviewer.NMapViewerResourceProvider;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;


public class ChildLocationViewActivity extends NMapActivity {
	
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	private NMapView mMapView; 
	private NMapController mMapController;

	//String test1 = this.getIntent().getStringExtra("reqId");
    //String test2 = this.getIntent().getStringExtra("reqCtn");

    double longitude01 = 127.12201246666667;
	double latitude01 = 37.495217644444445;
	
    
	//���� �� ����  ���� ���� -> ����� ��û���� ���߿��� �ڽ��� ��ġ�� ��������.
	private NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude01, latitude01);
	//private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(127.12201246666667, 37.495217644444445);
	
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 15;
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
	private static final String API_KEY = "12602e7037542bb1f774834ff437c15c";
	                                        

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
    	setContentView(R.layout.child_loca_view); //XML�� ������ �ʺ並 SetContentView�� ���� ���̾ƿ����� ����
    	
    	//���� �ش� ���� �θ� �ʱ�ȭ - �ϳ��� ��� �ϳ��� �θ��� ������ ������ �θ� �ʱ�ȭ�Ͽ� ������ ����.
    	//RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		//parentView.removeView(mMapView);
		
        //body 
        //���� �ð� ���
        TextView tv_checkTime = (TextView)findViewById(R.id.tv_checkTime); 
        if(tv_checkTime != null){
        	GregorianCalendar calendar = new GregorianCalendar();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��  aa hh:ss");
        	
        	tv_checkTime.setText("��ȸ�ð� : "+sdf.format(calendar.getTime()));
        }
         
        //��Ż�˸� �̹��� ��ư ó��
        /*ImageButton btn_alarm = (ImageButton)findViewById(R.id.btn_alarm);
        btn_alarm.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				//Toast.makeText(LocationViewActivity.this, "��Ż �˸� ��ư ������!!!", Toast.LENGTH_SHORT).show();
				
				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
				String title = "��Ż �˸�";	
				String message = "���� �ð����� 24�ð� �̳��� �ش� ��ġ���� ��Ż �� �˷��帳�ϴ�.";	
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setPositiveButton(R.string.btn_regist, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(ChildLocationViewActivity.this, "���� ������. ���� ȭ���� ��ġ�� �����ϰ� ����� Ǫ�����ֱ� ", Toast.LENGTH_SHORT).show();
					}
				});
				ad.setNegativeButton(R.string.btn_cancel, null);
				ad.show();
			}
		});*/
        
    	// create map view
    	//mMapView = new NMapView(this);
    	
    	mMapView = (NMapView)findViewById(R.id.mapview); //�������� �۾��� ���� mapview�� ��ü�� �ϳ� �����Ѵ�.

    	// set a registered API key for Open MapViewer Library
    	mMapView.setApiKey(API_KEY); //���� Ű ���� �����Ѵ�.


    	// set the activity content to the parent view
    	//setContentView(mMapView);


    	// initialize map view
    	mMapView.setClickable(true); //���� ��ġ�巡�װ� �����ϰ� ���ִ� �Ӽ�
    	mMapView.setEnabled(true);
    	mMapView.setFocusable(true);
    	mMapView.setFocusableInTouchMode(true);
    	mMapView.requestFocus();


    	// register listener for map state changes 
    	mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
    	// mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
    	// mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);


    	// use map controller to zoom in/out, pan and set map center, zoom level etc.
    	mMapController = mMapView.getMapController();


    	// use built in zoom controls
    	NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
    	NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
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

    	mMapController.setMapViewMode(NMapView.VIEW_MODE_SATELLITE);

    	//���̹� �� ���� ǥ���ϰ��� �ϴ� ���� ��ġ�� �������̷� ����.
    	getCurrentLocationInfoPOIdataOverlay(); //Overlay�� ��� ���� ��Ƴ��� ����� �޼ҵ� ȣ�� (�ɰ� Path �׸��⸦ �����س��� ����� �޼ҵ�)
 		
    }


	//��Ƽ��Ƽ�� Ȱ��ȭ�Ǹ� mylocation �� ���۵�
  	private void startMyLocation() {

  		if (mMyLocationOverlay != null) {	//���� ����ġ�� �������̰� ������ ��츸 ���
  			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {	//���� �������� �Ŵ����� my location �������̸� ������ ���� ������
  				mOverlayManager.addOverlay(mMyLocationOverlay);	//my location �������̸� �߰�
  			}

  			if (mMapLocationManager.isMyLocationEnabled()) {	//�̹� ����ġ�� Ȯ���ϴ� �������̰� �ִ� ��� ��ħ���� �̿��� ���۽��� �����ش�.

  				if (!mMapView.isAutoRotateEnabled()) {	//��ħ�� ����� Ȱ��ȭ �Ǿ����� ������
  					mMyLocationOverlay.setCompassHeadingVisible(true);

  					mMapCompassManager.enableCompass();

  					mMapView.setAutoRotateEnabled(true, false);

  					mMapContainerView.requestLayout();
  				} else {	//��ħ�� ���۽��� Ȱ��ȭ �Ǿ� ������ my location ����!
  					stopMyLocation();
  				}

  				mMapView.postInvalidate();	//������ �����忡�� ȭ�鰻���� �Ҷ� ���
  			} else {
  				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
  				if (!isMyLocationEnabled) {
  					Toast.makeText(ChildLocationViewActivity.this, "Please enable a My Location source in system settings",
  						Toast.LENGTH_LONG).show();

  					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
  					startActivity(goToSettings);

  					return;
  				}
  			}
  		}
  	}



	//��Ƽ��Ƽ�� Ȱ��ȭ�Ǹ� mylocation �� ������
	private void stopMyLocation() {
		if (mMyLocationOverlay != null) {	//����  ����ġ�� �������̰� ������ ��츸 ���
			mMapLocationManager.disableMyLocation();

			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);

				mMapCompassManager.disableCompass();

				mMapView.setAutoRotateEnabled(false, false);

				mMapContainerView.requestLayout();
			}
		}
	}


	//��ǥ�� �ش��ϴ� �ּҸ� ������ ǥ���ϴ� ��������
	private void getCurrentLocationInfoPOIdataOverlay() {
 
		//���� �޾ƿͼ� �����ϸ� �ش� ��ġ�� ǥ���ϴ� �������̸� ������ �� ����
		//double �� ���� , double �� ����, �ش� ��ġ ǥ���� string
		double longitude01 = 127.12201246666667;
		double latitude01 = 37.495217644444445;
		String placeInfo01 = "IT Becher Tower";
		
		// Markers for POI item
		int markerId = NMapPOIflagType.PIN;

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
		poiData.beginPOIdata(2);
		//poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerId, 0);
		//poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
		poiData.addPOIitem(longitude01, latitude01, placeInfo01, markerId, 0);
		poiData.endPOIdata();

		// create POI data overlay
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

		// set event listener to the overlay
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

		// select an item
		poiDataOverlay.selectPOIitem(0, true);

		// show all POI data
		//poiDataOverlay.showAllPOIdata(0);
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

				Toast.makeText(ChildLocationViewActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
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
						Toast.makeText(ChildLocationViewActivity.this, text, Toast.LENGTH_LONG).show();
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

			Toast.makeText(ChildLocationViewActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		//@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(ChildLocationViewActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

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

				Toast.makeText(ChildLocationViewActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
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


	/* POI data State Change Listener*/
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

		//@Override
		public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
			}

			// [[TEMP]] handle a click event of the callout
			Toast.makeText(ChildLocationViewActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
		}

		//@Override
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

    
    /** 
	 * Container view class to rotate map view.
	 */
	private class MapContainerView extends ViewGroup {

		public MapContainerView(Context context) {
			super(context);
		}

		@Override
		//view �� �ڽĵ鿡�� ũ��� ��ġ�� �Ҵ��Ҷ� ȣ��ȴ�.
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
				
				//���� ũ�Ⱑ �ٲ�븶�� ȣ��˴ϴ�.
				//mOverlayManager.onSizeChanged(width, height);
				//������ �߻��ؼ� ������ �ȵ���̵��� �޼ҵ带 ����
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