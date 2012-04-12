/* 
 * NMapViewer.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;


/**
 * Sample class for map viewer library.
 * 
 * @author kyjkim
 */
public class ChildSafezoneAddActivity extends NMapActivity {	
	
	//�ű� ������� ���������� �����ϴ� ��
	private String flag = "";
	private int listSize = 0;
	private String childCtn = "";
	private String safezoneCode = "";
	
	//������� ���̹� ���� ����ϴ� ���� ����
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "12602e7037542bb1f774834ff437c15c";

	private MapContainerView mMapContainerView;
	
	private NMapView mMapView; 
	private NMapController mMapController;
 
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String searchAddress = "";
	//�ݰ��� �׸��� ���� �������� ����
	private RadiusOverlay radiusOverlay;
	//�ݰ� ���� �� 200,500,1000 ���� ��ȯ
	private int radiusValue = 200;
	//���� �������� ��ǥ
	private double longitude;
	private double latitude;

	//�˻���
	private String searchText;
	//�˻� �� ������ ���� �浵�� ��� ����
	ArrayList<String> locationInfo;
	//�˻�â
	EditText searchArea;
	//Ű���� ���̰� �Ⱥ��̰�
	InputMethodManager mImm;
	
	private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
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
		setContentView(R.layout.child_safezone_add); //XML�� ������ �ʺ並 SetContentView�� ���� ���̾ƿ����� ����
		
		//���� �ش� ���� �θ� �ʱ�ȭ - �ϳ��� ��� �ϳ��� �θ��� ������ ������ �θ� �ʱ�ȭ�Ͽ� ������ ����.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		parentView.removeView(mMapView);
		
		//�űԵ������ �������� Ȯ���� ���ؼ� ���� intent���� ���� �����´�.
		Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        listSize = intent.getIntExtra("listSize", 0);
        childCtn = intent.getStringExtra("childCtn");
        if("UPDATE".equals(flag)){
	        safezoneCode = intent.getStringExtra("safezoneCode");
	        if(safezoneCode == null ){safezoneCode = "";}
	        latitude = Double.parseDouble(intent.getStringExtra("latitude"));
	        longitude = Double.parseDouble(intent.getStringExtra("longitude"));
	        String strRadiusValue = intent.getStringExtra("radius");
	        
	        if(strRadiusValue != null ){
	        	radiusValue = Integer.parseInt(strRadiusValue);
	        }else{
	        	radiusValue = 200;
	        }
	        
	        final Button btn_radius = (Button)findViewById(R.id.btn_radius);
	        if(radiusValue == 200){
				btn_radius.setBackgroundResource(R.drawable.btn_m_500_selector); 
			}else if(radiusValue == 500){
				btn_radius.setBackgroundResource(R.drawable.btn_m_1k_selector);
			}else{
				btn_radius.setBackgroundResource(R.drawable.btn_m_200_selector);
			}
	        radiusOverlay = new RadiusOverlay(radiusValue);
	        
        }
        
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
	    				
	    				searchText = searchArea.getText().toString();
	    				//API ȣ�� ������ ����
	    				//�ּҸ� ���ؼ� ���� �浵�� ���ϰ� �ش� ��ġ�� �̵��Ѵ�.
	    		    	WizSafeDialog.showLoading(ChildSafezoneAddActivity.this);	//Dialog ���̱�
	    		        CallGetLocaInfoAndMoveApiThread thread = new CallGetLocaInfoAndMoveApiThread(); 
	    				thread.start();
	    		    	//getLocaInfoAndMove(searchArea.getText().toString());
    			}
    		}
        });
        
        //�ݰ� ���� ���� ��ư
        final Button btn_radius = (Button)findViewById(R.id.btn_radius);
        btn_radius.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				if(radiusValue == 200){
					radiusValue = 500;
					btn_radius.setBackgroundResource(R.drawable.btn_m_1k_selector); 
				}else if(radiusValue == 500){
					radiusValue = 1000;
					btn_radius.setBackgroundResource(R.drawable.btn_m_200_selector);
				}else{
					radiusValue = 200;
					btn_radius.setBackgroundResource(R.drawable.btn_m_500_selector);
				}
				
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
        Button btn_setup = (Button)findViewById(R.id.btn_setup);
        //����flag�� ������ ��ư�� �������� �����ش�.
        if("UPDATE".equals(flag)){
        	btn_setup.setBackgroundResource(R.drawable.btn_modify_selector);
        }
        btn_setup.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "�Ƚ��� ���";	
				if("UPDATE".equals(flag)){
					title = "�Ƚ��� ����";	
				}
				String message = "";
				if(listSize > 1){
					message = "���� �ð����� 24�ð� �̳��� �ش� ��ġ�� ���� �� ���ڷ� 1ȸ�� �˷� �帳�ϴ�. \n �� �Ƚ��� �߰� ��� �� 100 ����Ʈ�� �����˴ϴ�.";
				}else{
					message = "���� �ð����� 24�ð� �̳��� �ش� ��ġ�� ���� �� ���ڷ� 1ȸ�� �˷� �帳�ϴ�.";
				}
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setPositiveButton(R.string.btn_regist, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//���� �߾��� ���� �浵�� ���Ѵ�.
						NGeoPoint centerValue = mMapController.getMapCenter();
						latitude = centerValue.getLatitude();
						longitude = centerValue.getLongitude();
						
						//API ȣ�� ������ ����
				    	//�Ƚ����� ���/�����Ѵ�.
				    	WizSafeDialog.showLoading(ChildSafezoneAddActivity.this);	//Dialog ���̱�
				        CallInsertSafeZoneApiThread thread = new CallInsertSafeZoneApiThread(); 
						thread.start();
					}
				});
				ad.setNegativeButton(R.string.btn_cancel, null);
				ad.show();
			}
		});
        
        //��� ��ư
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
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
		
		// compass manager - �������� ������ �����ִ� ���۽�
		//mMapCompassManager = new NMapCompassManager(this);
		
		// create my location overlay
		//mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
		//���۽� ��� ����
		//mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, null);
		
		// �ʺ信 �ִ� �������̸� ��� �����´�.
		List<NMapOverlay> overlays = mMapView.getOverlays();

		//NMapMyLocationOverlay ��ü�� ������ ������ �������̸� ��� �����Ѵ�.
		mOverlayManager.clearOverlays();
		
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

  	//API ȣ�� ������
  	class CallInsertSafeZoneApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String encCtn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildSafezoneAddActivity.this));
  				String encChildCtn = WizSafeSeed.seedEnc(childCtn);
  				String encRadius = WizSafeSeed.seedEnc(Integer.toString(radiusValue));
  				String encLatitude = WizSafeSeed.seedEnc(Double.toString(latitude));
  				String encLongitude = WizSafeSeed.seedEnc(Double.toString(longitude));
  				
  				StringBuffer url = new StringBuffer();
  				url.append("https://www.heream.com/api/addChildSafezone.jsp");
  				url.append("?safezoneCode=" + URLEncoder.encode(safezoneCode));
  				url.append("&ctn=" + URLEncoder.encode(encCtn));
  				url.append("&child_ctn=" + URLEncoder.encode(encChildCtn));
  				url.append("&radius=" + URLEncoder.encode(encRadius));
  				url.append("&lat=" + URLEncoder.encode(encLatitude));
  				url.append("&lon=" + URLEncoder.encode(encLongitude));

  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url.toString()).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				httpResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	

  	//API ȣ�� ������
  	class CallGetLocaInfoAndMoveApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			locationInfo = new ArrayList<String>();
  			try{
  				String strSql = "http://maps.google.co.kr/maps/geo?q="+URLEncoder.encode(searchText)+"&gl=KR&output=xml&key=";
  				
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(strSql).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				
  				String parseXML = "";
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  					parseXML = parseXML + temp;
  				}
  				
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<code>");
  				ArrayList<String> coordinates = WizSafeParser.xmlParser_List(returnXML,"<coordinates>");
  				if("200".equals(resultCode)){	//���� ���
  					httpResult = 0;
  				}
  				
  				try{
  					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  					DocumentBuilder builder = factory.newDocumentBuilder();
  					is = new ByteArrayInputStream(parseXML.getBytes("utf-8"));
  					Document doc = builder.parse(is);
  					Element order = doc.getDocumentElement();
  					NodeList items = order.getElementsByTagName("Point");	//1depth������Ʈ��
  					
  					for(int i = 0 ; i < items.getLength() ; i++){
  						Node item = items.item(i);
  						NodeList itemChildNodeList = item.getChildNodes();
  						for(int j = 0 ; j < itemChildNodeList.getLength() ; j++){
  							Node childItem = itemChildNodeList.item(j);
  							if("coordinates".equals(childItem.getNodeName())){
  	  							locationInfo.add(WizSafeUtil.replaceStr(childItem.getTextContent(), ",0", ""));
  	  						}
  						}
  					}
  				}catch(Exception e){
  					
  				}finally{
  					if(is != null){ try{is.close();}catch(Exception e){} }
  				}
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}  	
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//�ڵ鷯 ������
  				if(httpResult == 0){
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
  					String title = "�Ƚ��� ���";	
  					String message = "�Ƚ����� ��ϵǾ����ϴ�.";	
  					String buttonName = "Ȯ��";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							finish();
  						}
  					});
  					ad.show();
				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
					String title = "��� ����";
					if("UPDATE".equals(flag)){title = "���� ����";}
					String message = "�Ƚ��� ��� �� ������ �߻��Ͽ����ϴ�.";	
					if("UPDATE".equals(flag)){message = "�Ƚ��� ���� �� ������ �߻��Ͽ����ϴ�.";}
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
				}
  			}else if(msg.what == 1){
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}else if(msg.what == 2){	
  				
  				if(locationInfo.size() > 0){
  					double firstLon = Double.parseDouble(locationInfo.get(0).substring(0, locationInfo.get(0).indexOf(","))); 
  					double firstLat = Double.parseDouble(locationInfo.get(0).substring(locationInfo.get(0).indexOf(",")+1));
  					//���� ���� �浵�� �ٽ� ���߽��� �����Ѵ�.
					mPreferences = getPreferences(MODE_PRIVATE);
					
					int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
					int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
					boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
					boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);
					
					mMapController.setMapViewMode(viewMode);
					mMapController.setMapViewTrafficMode(trafficMode);
					mMapController.setMapViewBicycleMode(bicycleMode);
					//���� ��ô ������ 10�� ����
					level = 10;
					mMapController.setMapCenter(new NGeoPoint(firstLon, firstLat), level);
					//���� ���� �浵�� �ٽ� ���߽��� �����Ѵ�.
  				}else{
  					//��ȸ ��� ����
  	  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
  					String title = "��ġ �˻�";	
  					String message = "�˻��� ������ �����ϴ�.";	
  					String buttonName = "Ȯ��";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  						}
  					});
  				}
				
  			}else if(msg.what == 3){
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
  			}
  		}
  	};
  	
	
  //��Ƽ��Ƽ�� Ȱ��ȭ�Ǹ� mylocation �� ���۵�
  	private void moveStartLocation() {
  		
		if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {	//���� �������� �Ŵ����� my location �������̸� ������ ���� ������
			mOverlayManager.addOverlay(mMyLocationOverlay);	//my location �������̸� �߰�
  			}
  
			boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
			if (!isMyLocationEnabled) {
				Toast.makeText(ChildSafezoneAddActivity.this, "Please enable a My Location source in system settings",
				Toast.LENGTH_LONG).show();

			Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(goToSettings);

			return;
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

		}

	};

	/* Local Functions */

	private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);
		int restoreLongitude = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
		int restoreLatitude = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
		int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);
		
		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
		//���� ��ô ������ 10�� ����
		level = 10;
		if("INSERT".equals(flag)){
			mMapController.setMapCenter(new NGeoPoint(restoreLongitude, restoreLatitude), level);
		}else{
			mMapController.setMapCenter(new NGeoPoint(longitude, latitude), level);
		}
		//����ġ�� �̵�
		if("INSERT".equals(flag)){
			moveStartLocation();
		}
		Toast.makeText(ChildSafezoneAddActivity.this, "��ø� ��ٷ� �ּ���...", Toast.LENGTH_LONG).show();
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

	
	class RadiusOverlay extends NMapOverlay{

		int current_x;
		int current_y;
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int windowWidth = display.getWidth();
		int windowHeight = display.getHeight();
		int radius = radiusValue;
			
		public RadiusOverlay(int raidusValue){
			current_x = windowWidth / 2;
			current_y = windowHeight / 2;
			if(raidusValue != 200 && raidusValue != 500 && raidusValue != 1000){
				radius = 200;
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
				Paint p1 = new Paint();
				p1.setARGB(60,255,93,64);
				p1.setAntiAlias(true); 
				
				//�׵θ��� �׸��� ���� paint ���� ����
				Paint p2 = new Paint();
				p2.setColor(Color.GRAY);
				p2.setStyle(Paint.Style.STROKE);
				p2.setStrokeWidth(4);
				
				// GeoPoint�� ��ũ�� ��ǥ�� ��ȯ
				point = projection.toPixels(geop,point);

				float radiusPixel = projection.metersToPixels(radius);
				canvas.drawCircle(current_x, current_y-60, radiusPixel, p1);
				canvas.drawCircle(current_x, current_y-60, radiusPixel, p2);
				
				
				//���� �߾ӿ� +ǥ�ø� ���� paint ���� ����
				Paint p3 = new Paint();
				p3.setARGB(200,255,44,44);
				p3.setTextAlign(Align.CENTER);
				p3.setTypeface(Typeface.create((String)null, Typeface.BOLD));
				p3.setTextSize(50);
				canvas.drawText("+", current_x, current_y - 50, p3);
				//�߾ӿ� +ǥ�ø� �̹����� �Ϸ��� �Ʒ� �ڵ带 ���
				//Drawable centerMark = getResources().getDrawable(R.drawable.notice_popup_ck);
				//Bitmap bitmap = ((BitmapDrawable)centerMark).getBitmap();
				//canvas.drawBitmap(bitmap, current_x, current_y - 100, null);
				
				//�ݰ� �� ���ʿ� ���� ������ ǥ��
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
				canvas.drawBitmap(bitmap2, (windowWidth/2 - (bitmap2.getWidth()/2)), windowHeight/2-60-radiusPixel, null);
				
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
	
	//API ȣ�� ������
  	public void getLocaInfoAndMove (String searchAddress){
			
		Locale.setDefault(Locale.KOREA); // = ko_KO, ����Ʈ�� �Ǿ� ������ ����
		Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		List<Address> addressInfoList = null;
		Log.i("banhong", searchAddress+"�����̴�???11111 : "+longitude+","+latitude);
		try {
			//�ּҷκ��� ���� �浵�� ���Ѵ�.
			addressInfoList = geocoder.getFromLocationName(searchAddress, 1);
			Log.i("banhong", searchAddress+"�����̴�???222222 : "+longitude+","+latitude);
			if(addressInfoList.size() == 0){
				Log.i("banhong", searchAddress+"�����̴�???33333 : "+longitude+","+latitude);
				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneAddActivity.this);
				String title = "�˻� ���� �ȳ�";	
				String message = "�˻�� �ùٸ��� �ʽ��ϴ�. \n��Ȯ�� �������� �Է��� �ּ���.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ad.show();
			}else{
				Log.i("banhong", searchAddress+"�����̴�???44444 : "+longitude+","+latitude);
				Address addr = addressInfoList.get(0);
				longitude = addr.getLongitude();
				latitude = addr.getLatitude();
			}
			Log.i("banhong", searchAddress+"�������� �Ǳ� �ϴ°Ŵ�5555??? : "+longitude+","+latitude);
			//���� ���� �浵�� �ٽ� ���߽��� �����Ѵ�.
			mPreferences = getPreferences(MODE_PRIVATE);
			
			int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
			int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
			boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
			boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);
			
			mMapController.setMapViewMode(viewMode);
			mMapController.setMapViewTrafficMode(trafficMode);
			mMapController.setMapViewBicycleMode(bicycleMode);
			//���� ��ô ������ 10�� ����
			level = 10;
			mMapController.setMapCenter(new NGeoPoint(longitude, latitude), level);
			//���� ���� �浵�� �ٽ� ���߽��� �����Ѵ�.
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("banhong", "�ͼ���!!!!   : "+e.toString());
		}
	
		
	}
	
}