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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.nhn.android.mapviewer.NMapPOIflagType;
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


public class ChildLocationViewActivity extends NMapActivity {
	
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	private NMapView mMapView; 
	private NMapController mMapController;

	//������Ƽ��Ƽ���� �޾ƿ� ��
	private String childCtn;
	//�ʺ����� ���� ���� ���� 
	private int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	private int payResult = 1;		//0 - ��������, �׿� - ����
	private int locationLogResult = 1;		//0 - ��������, �׿� - ����
	private String regdate;
	private double longitude;
	private double latitude; 
	private String address;
	private String type;


	//���� �� ����  ���� ���� -> ����� ��û���� ���߿��� �ڽ��� ��ġ�� ��������.
	private NGeoPoint NMAP_LOCATION_DEFAULT;
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
	
	String payMode = "NOPAY";
	
	//�� �ش� ��ġ������ 3G/wi-fi/GPS���� ���¿� ���� ���� ��ġ�� �ٸ� �� �ֽ��ϴ�. �佺Ʈ ��� ���� �Ǻ� ����
	boolean isShowToast = true; 
	
	/** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.child_loca_view); //XML�� ������ �ʺ並 SetContentView�� ���� ���̾ƿ����� ����
    	
    	Intent intent = getIntent();
        childCtn = intent.getStringExtra("phonenum");
    	if(childCtn == null){childCtn = "";}
    	
    	//NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude, latitude);
    	//���� �ش� ���� �θ� �ʱ�ȭ - �ϳ��� ��� �ϳ��� �θ��� ������ ������ �θ� �ʱ�ȭ�Ͽ� ������ ����.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		parentView.removeView(mMapView);
		
		//��Ȯ�� ���� �����ֱ� ������ MapView ������ ������ �ʰ��Ѵ�.
		parentView.setVisibility(View.INVISIBLE);
	
        //body 
        
    	// create map view
    	mMapView = (NMapView)findViewById(R.id.mapview); //�������� �۾��� ���� mapview�� ��ü�� �ϳ� �����Ѵ�.
    	//���̹� �ΰ� �̵���Ų��.
    	//mMapView.setLogoImageOffset(100, 100);
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
    	//�Ϲ� ���� �������� ���δ�.
    	mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
    	
    	
    	//���� ������� �ƴ����� ���� ���â ����������, �ᱹ ���� �����带 Ÿ�� �Ǿ��ִ�.
    	if(getHowManyTry() >= 3){
    		AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
			ad.setTitle("����ġã��");
			ad.setMessage("1�� ���� ��� �Ǽ��� �ʰ� �Ͽ����ϴ�.(1�� 3ȸ ����)\n����Ʈ�� ��ȸ �Ͻðڽ��ϱ�?\n1ȸ ��ȸ �� 100����Ʈ ����");
			ad.setPositiveButton("��ġã��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					payMode = "PAY";
					//API ȣ�� ������ ����
			    	//class ���� ���Խ� api ������� �����浵�� �����´�.
			    	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog ���̱�
			    	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
					thread.start();
				}
			});
			ad.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			ad.show();
    	}else{
    		payMode = "NOPAY";
    		//API ȣ�� ������ ����
        	//class ���� ���Խ� api ������� �����浵�� �����´�.
        	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog ���̱�
        	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
    		thread.start();
    	}
    }


	@Override
	protected void onDestroy() {
		//��Ƽ��Ƽ�� �����Ҷ� �ʺ信 ���� provider �� ��ȯ�Ѵ�. 
		LocationManager locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationMgr.removeUpdates(mMapLocationManager);
		super.onDestroy();
	}


	//API ȣ�� ������
  	class CallGetNowLocationApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = "";
  				if(childCtn != null && !"".equals(childCtn)){
  					enc_ctn = WizSafeSeed.seedEnc(childCtn);
  					String enc_ParentCtn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildLocationViewActivity.this));
  					String url = "https://www.heream.com/api/getNowLocation.jsp?childCtn="+ URLEncoder.encode(enc_ctn) + "&parentCtn=" + URLEncoder.encode(enc_ParentCtn);
  					
  					HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  	  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  	  				String temp;
  	  				ArrayList<String> returnXML = new ArrayList<String>();
  	  				while((temp = br.readLine()) != null)
  	  				{
  	  					returnXML.add(new String(temp));
  	  				}
  	  				//����� XML �Ľ��Ͽ� ����
  	  				
  	  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  	  				String strRegdate = WizSafeParser.xmlParser_String(returnXML,"<REGDATE>");
  	  				String encLongitude = WizSafeParser.xmlParser_String(returnXML,"<LONGITUDE>");
  	  				String encLatitude = WizSafeParser.xmlParser_String(returnXML,"<LATITUDE>");
  	  				String encAddress = WizSafeParser.xmlParser_String(returnXML,"<ADDRESS>");
  	  				String strType = WizSafeParser.xmlParser_String(returnXML,"<TYPE>");
  	  				//�ʿ��� ������ Ÿ������ ����ȯ
  	  				httpResult = Integer.parseInt(resultCode);
  	  				if(httpResult == 0){
	  	  				regdate = strRegdate;
	  	  				longitude = Double.parseDouble(WizSafeSeed.seedDec(encLongitude));
	  	  				latitude = Double.parseDouble(WizSafeSeed.seedDec(encLatitude));
	  	  				address = WizSafeSeed.seedDec(encAddress);
	  	  				type = strType;
  	  				}
  	  				
  	  				pHandler.sendEmptyMessage(0);
  				}else{
  					pHandler.sendEmptyMessage(1);
  				}
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(2);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	
  	//���� API ȣ�� ������
  	class CallPayApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildLocationViewActivity.this));
  				String url = "https://www.heream.com/api/nowLocationDeductPoint.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				payResult = Integer.parseInt(resultCode);
  				pHandler.sendEmptyMessage(3);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(4);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	
  	//LocationLog �״� ������ : �ڵ鷯�� ���ϵǴ°� ���� ȣ�⸸�ϰ� ���̴�.
  	class CallInsertLocationLogThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildLocationViewActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(childCtn);
  				String enc_address = WizSafeSeed.seedEnc(address);
  				String url = "https://www.heream.com/api/insertLocationBoard.jsp?parentCtn="+ URLEncoder.encode(enc_ctn) + "&childCtn="+ URLEncoder.encode(enc_childCtn) + "&address=" + URLEncoder.encode(enc_address);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				locationLogResult = Integer.parseInt(resultCode);
  				pHandler.sendEmptyMessage(5);
  			}catch(Exception e){
  				pHandler.sendEmptyMessage(6);
  			}
  		}
  	}
  	
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			if(msg.what == 0){
  				//�ڵ鷯 ������
  				if(httpResult == 0){
					//��ȸ����
  					//nmap �׸��⿡ �ʿ��� �� ����
  					NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude, latitude);
  					
  					TextView tv_checkTime = (TextView)findViewById(R.id.textView1); 
  			        if(tv_checkTime != null){
  			        	String showDate = WizSafeUtil.getDateFormat(regdate);
  			        	String gap = WizSafeUtil.getGapValue(type);
  			        	tv_checkTime.setText("���� : "+ showDate +"\n�������� : "+ gap);
  			        }

  			        //���ΰ�ħ �̹��� ��ư ó��
  			        Button btn_alarm = (Button)findViewById(R.id.btn_retry);
  			        btn_alarm.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							try{
  								//���� ������� �ƴ����� ���� ���â ����
  						    	if(getHowManyTry() >= 3){
  						    		AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
  									ad.setTitle("����ġã��");
  									ad.setMessage("1�� ���� ��� �Ǽ��� �ʰ� �Ͽ����ϴ�.(1�� 3ȸ ����)\n����Ʈ�� ��ȸ �Ͻðڽ��ϱ�?\n1ȸ ��ȸ �� 100����Ʈ ����");
  									ad.setPositiveButton("��ġã��", new DialogInterface.OnClickListener() {
  										public void onClick(DialogInterface dialog, int which) {
  											payMode = "PAY";
  											//API ȣ�� ������ ����
  									    	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog ���̱�
  									    	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
  											thread.start();  											
  										}
  									});
  									ad.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
  										public void onClick(DialogInterface dialog, int which) {
  										}
  									});
  									ad.show();
  						    	}else{
  						    		payMode = "NOPAY";
  						    		//API ȣ�� ������ ����
  						        	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog ���̱�
  						        	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
  						    		thread.start();
  						    	}
  								
  		  			        }catch(Exception e){}
  						}
  					});
  			        
  			        try{

	  			  		//clear all overlay
	  			  		mOverlayManager.clearOverlays();
	  			  		
				    	//���̹� �� ���� ǥ���ϰ��� �ϴ� ���� ��ġ�� �������̷� ����.
				    	getCurrentLocationInfoPOIdataOverlay(); //Overlay�� ��� ���� ��Ƴ��� ����� �޼ҵ� ȣ�� (�ɰ� Path �׸��⸦ �����س��� ����� �޼ҵ�)
				    	//ȭ�� �ε� �Ϸ��� �ȳ����� �佺Ʈ�� 4�ʰ� �����ش�.
  			        }catch(Exception e){}
  			       
  			        //������ �ƴҰ�� - �ʺ俵���� ���̰� �ϰ�, ���α׷����� �����ϰ�, ���� ���° ��������� ����
  			        //������ ��� - ����ġ ��ȸ�� ���� ����Ʈ ���� API ȣ��
  			        if("NOPAY".equals(payMode)){
  		  			  	
  	  					//�ڳ࿡�� ��ġ��ȸ������ sms�� �˸���. 
  	  					GregorianCalendar gc = new GregorianCalendar();
  	  					SimpleDateFormat dateFormat = new SimpleDateFormat("hh�� mm��");  
  	  					String checkTime = dateFormat.format(gc.getTime());

  	  					String myCtn = WizSafeUtil.getCtn(ChildLocationViewActivity.this);
  	  					String smsMsg = "[����Ʈ�Ƚ�]"+myCtn+"���� "+ checkTime +"�� ������ ��ġ�� ��ȸ�߽��ϴ�.";
  	  					
  	  					//if(�ڳ� ��ȣ�� sms ������ �������� Ȯ���Ѵ�.){
  	  						//�ڳ࿡�� sms�� ����
  	  						//boolean smsResult = WizSafeSms.sendSmsMsg(childCtn, smsMsg);
  	  					//}
  	  					//�ڳ࿡�� ��ġ��ȸ������ sms�� �˸���.
  	  			        
  			        	//���� �θ� �ڽ��� ��ȸ�ߴٴ� Location �α׸� ����� ��������� 
  			        	CallInsertLocationLogThread thread = new CallInsertLocationLogThread(); 
						thread.start();

  			        }else if("PAY".equals(payMode)){
  			        	CallPayApiThread thread = new CallPayApiThread(); 
  						thread.start();
  			        }
  			        
				}else{
					WizSafeDialog.hideLoading();
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
					String title = "�ڳ�����ġã�����";	
					String message = "�ڳ� ����ġ ������ ��ȸ�� �� �����ϴ�.";	
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
  				WizSafeDialog.hideLoading();
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
				String title = "��� ����";	
				String message = "�ڳ� ������ ��ȸ�� �� �����ϴ�. �ٽ� �õ����ּ���.";	
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
  				WizSafeDialog.hideLoading();
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  			}else if(msg.what == 3){
  				if(payResult == 0){
  					//== ������ �����ϰ� ȣ��Ǵ� �ڵ鷯 ==
  					
  					//���� �θ� �ڽ��� ��ȸ�ߴٴ� Location �α׸� ����� ��������� = �޴� �ڵ鷯�� ������, ����� �Ǹ� ����� 
			        CallInsertLocationLogThread thread = new CallInsertLocationLogThread(); 
					thread.start();
  	  				
  				}else if(payResult == 1){
  					//�ܾ׺����� ���
  					WizSafeDialog.hideLoading();
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
					ad.setTitle("����Ʈ �ȳ�");
					ad.setMessage("������ ����Ʈ�� �����մϴ�. ����Ʈ ���� �� �ٽ� �̿��� �ּ���.");
					ad.setPositiveButton("����Ʈ\n�����ϱ�", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
							Toast.makeText(ChildLocationViewActivity.this, "����Ʈ �����ϱ�� ��Ƽ��Ƽ �̵�", Toast.LENGTH_SHORT).show();
						}
					});
					ad.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
  				}
  			}else if(msg.what == 4){
  				WizSafeDialog.hideLoading();
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  			}else if(msg.what == 5){
  				if(locationLogResult == 0){
  					//== ���������� �ʺ並 �����ָ�, ���� ��� �ô��� �����ϴ� �ڵ鷯 ==
  	  				WizSafeDialog.hideLoading();
  	  				//�ʺ俵���� ���̰� �ϰ�, ���α׷����� �����ϰ�, ���� ���° ��������� ����
  	  				RelativeLayout mapViewArea = (RelativeLayout)findViewById(R.id.relayout);
  		        	mapViewArea.setVisibility(View.VISIBLE);
  		        	WizSafeDialog.hideLoading();
  		        	//���� ����ġ ��ȸ�� ����ߴ��� �����Ѵ�.
  		        	setHowManyTry();
  		        	
  			        //�佺Ʈ�÷��� ���� ���� �佺Ʈ�� ���� �� �� �÷��� �����Ͽ� ���ΰ�ħ�ÿ��� �佺Ʈ�� ����� �ʵ��� �Ѵ�.
  			    	if(isShowToast){
  			    		Toast.makeText(ChildLocationViewActivity.this, "�� �ش� ��ġ������ 3G/wi-fi/GPS���� ���¿� ���� ���� ��ġ�� �ٸ� �� �ֽ��ϴ�.", Toast.LENGTH_LONG).show();
  			    		isShowToast = false;
  			    	}
  				}else{
  					WizSafeDialog.hideLoading();
  	  				//�ڵ鷯 ������
  	  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  				}
  				
  			}else if(msg.what == 6){
  				WizSafeDialog.hideLoading();
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  			}
  		}
  	};


	//�޾ƿ� �����浵�� ���ؼ� ��+�ּҸ�ǳ���� ������ ǥ���ϴ� ��������
	private void getCurrentLocationInfoPOIdataOverlay() {
 
		// Markers for POI item
		//int markerId = NMapPOIflagType.PIN;
		int markerId = NMapPOIflagType.CUSTOM_BASE + 1;

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);
		poiData.addPOIitem(longitude, latitude, address.trim(), markerId, 0);
		poiData.endPOIdata();

		// create POI data overlay
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

		// set event listener to the overlay
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

		// select an item
		//poiDataOverlay.selectPOIitem(0, true);
		
		// show all POI data
		poiDataOverlay.showAllPOIdata(0);	
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
		
		}

		//@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

		}

	};
	
	
	
	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		//@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
				//restoreInstanceState();

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

	
	//����ġ ã�⸦ ����̳� �õ��Ͽ����� Ȯ���Ѵ�.
	public int getHowManyTry(){
		int tryCount =0;
		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		String getNowChildLocCnt = LocalSave.getString("getNowChildLocCnt",sdf.format(calendar.getTime())+"_0");
		String nowDate = "";
		String nowCount = "0";
		StringTokenizer st = new StringTokenizer(getNowChildLocCnt, "_");
		
		while(st.hasMoreTokens())
		{
			nowDate = st.nextToken();
			nowCount = st.nextToken();
		}
		
		//�����߿� �õ��� Ƚ��
		if(nowDate.equals(sdf.format(calendar.getTime()))){
			tryCount = Integer.parseInt(nowCount);
		}else{
			tryCount = 0;
		}

		return tryCount;
	}
	
	//��ġã�⸦ �ϰ� �� �Ŀ� �׳� ���° ��ġã�⸦ �Ͽ����� �����Ѵ�.
	public void setHowManyTry(){
		int tryCount =0;
		GregorianCalendar calendar = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		String getNowChildLocCnt = LocalSave.getString("getNowChildLocCnt",sdf.format(calendar.getTime())+"_0");
		String nowDate = "";
		String nowCount = "0";
		StringTokenizer st = new StringTokenizer(getNowChildLocCnt, "_");
		while(st.hasMoreTokens())
		{
			nowDate = st.nextToken();
			nowCount = st.nextToken();
		}
		tryCount = Integer.parseInt(nowCount)+1;
		SharedPreferences.Editor edit;
		edit = LocalSave.edit();
		edit.putString("getNowChildLocCnt", sdf.format(calendar.getTime())+"_"+tryCount);
		edit.commit();
	}
}