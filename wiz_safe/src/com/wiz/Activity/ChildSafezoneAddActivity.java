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
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
	
	//������� ���̹� ���� ����ϴ� ���� ����
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "12602e7037542bb1f774834ff437c15c";

	private NMapView mMapView; 
	private NMapController mMapController;

	double longitude01 = 127.12201246666667;
	double latitude01 = 37.495217644444445;

	//�ݰ��� �׸��� ���� �������� ����
	private RadiusOverlay radiusOverlay;
	//�ݰ� ���� �� 200,500,1000 ���� ��ȯ
	private int radiusValue = 200;

	//�˻�â
	EditText searchArea;
	//Ű���� ���̰� �Ⱥ��̰�
	InputMethodManager mImm;
	
	//���� �� ����  ���� ���� -> ����� ��û���� ���߿��� �ڽ��� ��ġ�� ��������.
	private NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude01, latitude01);
	//private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(127.12201246666667, 37.495217644444445);
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 12;
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
		setContentView(R.layout.child_safezone_add); //XML�� ������ �ʺ並 SetContentView�� ���� ���̾ƿ����� ����
		
		//���� �ش� ���� �θ� �ʱ�ȭ - �ϳ��� ��� �ϳ��� �θ��� ������ ������ �θ� �ʱ�ȭ�Ͽ� ������ ����.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout1);
		parentView.removeView(mMapView);
		
		//top-navigation �� ����
        TextView topTitle = (TextView)findViewById(R.id.textTitle);
        if(topTitle != null){
        	topTitle.setText(R.string.title_saftyzone_setup);
        }
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildSafezoneAddActivity.this.finish();
			}
		});

        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        //body
        
        //�˻�â ���� 
        searchArea = (EditText)findViewById(R.id.tv_search);
        searchArea.setFilters(new InputFilter[] {
        	new InputFilter.LengthFilter(20)	
        });
     
        
        mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //�˻���ư�׼� ����
        findViewById(R.id.btn_search).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			switch(v.getId()){
	    			case R.id.btn_search:
	    				mImm.hideSoftInputFromWindow(searchArea.getWindowToken(),0);	//Ű���带 �����
	    				goSearch(searchArea.getText().toString());
	    				break;
    			}
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
    	    			//���� ó��
    	    		}else{
    	    			//���� ó��
    	    		}
    	    	}catch(Exception e){
    	    		
    	    	}finally{
    	    		if(is != null){ try{is.close();}catch(Exception e){} }
    	    	}
    	    }
        });
        
        //�ݰ� ���� ���� ��ư
        ImageButton btn_radius = (ImageButton)findViewById(R.id.btn_radius);
        btn_radius.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				
				if(radiusValue == 200){
					radiusValue = 500;
				}else if(radiusValue == 500){
					radiusValue = 1000;
				}else{
					radiusValue = 200;
				}
				//Toast.makeText(ChildSafezoneAddActivity.this, "�ݰ�="+radiusValue, Toast.LENGTH_SHORT).show();
				
				//radius overlay �� �ٽ� �׸���.
				// �ʺ信 �ִ� �������̸� ��� �����´�.
				List<NMapOverlay> overlays = mMapView.getOverlays();
				overlays.clear();
				// �ű� ���̴� �������̸� �����.
				radiusOverlay = new RadiusOverlay(radiusValue);
				overlays.add(radiusOverlay);
				mOverlayManager.populate();
				
			}
		});
        
        //���� ��ư
        ImageButton btn_setup = (ImageButton)findViewById(R.id.btn_setup);
        btn_setup.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "�Ƚ��� ���";	
				String message = "";
				//if("�Ƚ��� ��� �Ǽ��� 1�� �̻��� ����"){
					message = "���� �ð����� 24�ð� �̳��� �ش� ��ġ�� ���� �� ���ڷ� 1ȸ�� �˷� �帳�ϴ�. \n �� �Ƚ��� �߰� ��� �� 100 ����Ʈ�� �����˴ϴ�.";
				//}else{
					message = "���� �ð����� 24�ð� �̳��� �ش� ��ġ�� ���� �� ���ڷ� 1ȸ�� �˷� �帳�ϴ�.";
				//}
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setPositiveButton(R.string.btn_regist, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(ChildSafezoneAddActivity.this, "��ϴ�����.api ��� �űŽ� ", Toast.LENGTH_SHORT).show();
					}
				});
				ad.setNegativeButton(R.string.btn_cancel, null);
				ad.show();
			}
		});
        
        //��� ��ư
        ImageButton btn_cancel = (ImageButton)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				
				ChildSafezoneAddActivity.this.finish();
			}
		});
        
        
		//=====================================================//
		// ������� �� ���� �� �����ֱ�
		//=====================================================//		
        
		// create map view
		//mMapView = new NMapView(this);
        mMapView = (NMapView)findViewById(R.id.mapview); //�������� �۾��� ���� mapview�� ��ü�� �ϳ� �����Ѵ�.
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
		
		
		// �ʺ信 �ִ� �������̸� ��� �����´�.
		List<NMapOverlay> overlays = mMapView.getOverlays();

		//NMapMyLocationOverlay ��ü�� ������ ������ �������̸� ��� �����Ѵ�.
		//mOverlayManager.clearOverlays();
		
		// �ű� ���̴� �������̸� �����. - �⺻ 200m �ݰ�
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
			//zoom ��ȭ �ÿ� ȭ���� �ݰ� �������̸� �ٽ� �׷��ش�.
			// �ʺ信 �ִ� �������̸� ��� �����´�.
			List<NMapOverlay> overlays = mMapView.getOverlays();
			overlays.clear();
			// �ű� ���̴� �������̸� �����.
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

	/* Menus */
	private static final int MENU_ITEM_CLEAR_MAP = 10;
	private static final int MENU_ITEM_MAP_MODE = 20;
	private static final int MENU_ITEM_MAP_MODE_SUB_VECTOR = MENU_ITEM_MAP_MODE + 1;
	private static final int MENU_ITEM_MAP_MODE_SUB_SATELLITE = MENU_ITEM_MAP_MODE + 2;
	private static final int MENU_ITEM_MAP_MODE_SUB_HYBRID = MENU_ITEM_MAP_MODE + 3;
	private static final int MENU_ITEM_MAP_MODE_SUB_TRAFFIC = MENU_ITEM_MAP_MODE + 4;
	private static final int MENU_ITEM_MAP_MODE_SUB_BICYCLE = MENU_ITEM_MAP_MODE + 5;
	private static final int MENU_ITEM_ZOOM_CONTROLS = 30;
	private static final int MENU_ITEM_MY_LOCATION = 40;

	private static final int MENU_ITEM_TEST_MODE = 50;
	private static final int MENU_ITEM_TEST_POI_DATA = MENU_ITEM_TEST_MODE + 1;
	private static final int MENU_ITEM_TEST_PATH_DATA = MENU_ITEM_TEST_MODE + 2;
	private static final int MENU_ITEM_TEST_FLOATING_DATA = MENU_ITEM_TEST_MODE + 3;
	private static final int MENU_ITEM_TEST_AUTO_ROTATE = MENU_ITEM_TEST_MODE + 4;
	//�߰��� �޴� ��ư�� ���Ǵ� ���
	private static final int MENU_ITEM_HONG_TEST = 60;

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 * 
	 * @param menu the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menuItem = null;
		SubMenu subMenu = null;

		menuItem = menu.add(Menu.NONE, MENU_ITEM_CLEAR_MAP, Menu.CATEGORY_SECONDARY, "Clear Map");
		menuItem.setAlphabeticShortcut('c');
		menuItem.setIcon(android.R.drawable.ic_menu_revert);

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_MAP_MODE, Menu.CATEGORY_SECONDARY, "Map mode");
		subMenu.setIcon(android.R.drawable.ic_menu_mapmode);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_VECTOR, Menu.NONE, "Standard");
		menuItem.setAlphabeticShortcut('m');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_SATELLITE, Menu.NONE, "Satellite");
		menuItem.setAlphabeticShortcut('s');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_HYBRID, Menu.NONE, "Hybrid");
		menuItem.setAlphabeticShortcut('h');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_TRAFFIC, Menu.NONE, "Traffic");
		menuItem.setAlphabeticShortcut('t');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_BICYCLE, Menu.NONE, "Bicycle");
		menuItem.setAlphabeticShortcut('b');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = menu.add(0, MENU_ITEM_ZOOM_CONTROLS, Menu.CATEGORY_SECONDARY, "Zoom Controls");
		menuItem.setAlphabeticShortcut('z');
		menuItem.setIcon(android.R.drawable.ic_menu_zoom);

		menuItem = menu.add(0, MENU_ITEM_MY_LOCATION, Menu.CATEGORY_SECONDARY, "My Location");
		menuItem.setAlphabeticShortcut('l');
		menuItem.setIcon(android.R.drawable.ic_menu_mylocation);

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_TEST_MODE, Menu.CATEGORY_SECONDARY, "Test mode");
		subMenu.setIcon(android.R.drawable.ic_menu_more);

		menuItem = subMenu.add(0, MENU_ITEM_TEST_POI_DATA, Menu.NONE, "Test POI data");
		menuItem.setAlphabeticShortcut('p');

		menuItem = subMenu.add(0, MENU_ITEM_TEST_PATH_DATA, Menu.NONE, "Test Path data");
		menuItem.setAlphabeticShortcut('t');

		menuItem = subMenu.add(0, MENU_ITEM_TEST_FLOATING_DATA, Menu.NONE, "Test Floating data");
		menuItem.setAlphabeticShortcut('f');

		menuItem = subMenu.add(0, MENU_ITEM_TEST_AUTO_ROTATE, Menu.NONE, "Test Auto Rotate");
		menuItem.setAlphabeticShortcut('a');

		//������° �׽�Ʈ ��ư �߰�
		menuItem = menu.add(0, MENU_ITEM_HONG_TEST, Menu.CATEGORY_SECONDARY, "Hong TEST");
		menuItem.setAlphabeticShortcut('h');	//����Ű ����
		menuItem.setIcon(android.R.drawable.ic_menu_mylocation); //������ �̹��� ����

		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu pMenu) {
		super.onPrepareOptionsMenu(pMenu);

		int viewMode = mMapController.getMapViewMode();
		boolean isTraffic = mMapController.getMapViewTrafficMode();
		boolean isBicycle = mMapController.getMapViewBicycleMode();

		pMenu.findItem(MENU_ITEM_CLEAR_MAP).setEnabled(
			(viewMode != NMapView.VIEW_MODE_VECTOR) || isTraffic || mOverlayManager.sizeofOverlays() > 0);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_VECTOR).setChecked(viewMode == NMapView.VIEW_MODE_VECTOR);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_SATELLITE).setChecked(viewMode == NMapView.VIEW_MODE_SATELLITE);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_HYBRID).setChecked(viewMode == NMapView.VIEW_MODE_HYBRID);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_TRAFFIC).setChecked(isTraffic);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_BICYCLE).setChecked(isBicycle);

		if (mMyLocationOverlay == null) {
			pMenu.findItem(MENU_ITEM_MY_LOCATION).setEnabled(false);
		}

		return true;
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

			//e.getAction() ��ġ �̺�Ʈ ���� ACTION_DOWN -> ACTION_MOVE -> ACTION_UP ������ ��ȯ
			// TODO Auto-generated method stub
			return super.onTouchEvent(e, mapView);
		}

		@Override
		public void draw(Canvas canvas, NMapView mapView, boolean shadow) {
			NMapProjection projection = mapView.getMapProjection();
			Point point = new Point();
			
			if (shadow == false) {
				// Get the current location
				//�ʺ信 ��ġ�� ��ǥ�� �ش��ϴ� �����浵�� �����´�.
				NGeoPoint geop = mapView.getMapProjection().fromPixels(current_x, current_y);
				
				//���� �׸��� ���� paint ���� ����
				Paint p = new Paint();
				p.setARGB(70,0,0,200);
				p.setAntiAlias(true); 
				
				// GeoPoint�� ��ũ�� ��ǥ�� ��ȯ
				point = projection.toPixels(geop,point);

				float radiusPixel = projection.metersToPixels(radius);
				canvas.drawCircle(current_x, current_y-110, radiusPixel, p);
				
				//�۾��� ���� paint ���� ����
				Paint p2 = new Paint();
				p2.setARGB(255,0,0,0);
				p2.setTextAlign(Align.CENTER);
				p2.setTextSize(25);
				//�ݰ� �� ���ʿ� ���� ������ ǥ��
				canvas.drawText(radius+"M", current_x, current_y - 110 - radiusPixel, p2);
				
				//�߽����� + ǥ��
				p2.setTextSize(40);
				canvas.drawText("+", current_x, current_y - 100, p2);
				
			}
			super.draw(canvas,mapView,shadow);
		}
		
		
		@Override
		public boolean onTap(int x, int y, NMapView mapView) {

			//����Ʈ Ű���尡 ���� ��� �����.
			mImm.hideSoftInputFromWindow(searchArea.getWindowToken(),0);	//Ű���带 �����
			
			//�ʺ信 ��ġ�� ��ǥ�� �ش��ϴ� �����浵�� �����´�.
			NGeoPoint geop = mapView.getMapProjection().fromPixels(x, y);
							
			//�ش�  �����浵 ���� �� �߽��� �ǵ��� ���� �̵���Ų��.
			mMapController.animateTo(geop);
			
			// TODO Auto-generated method stub
			return false;
		}
	
	}
	
	
}