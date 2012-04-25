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

	//이전액티비티에서 받아올 값
	private String childCtn;
	//맵뷰위에 사용될 정보 선언 
	private int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	private int payResult = 1;		//0 - 차감성공, 그외 - 실패
	private int locationLogResult = 1;		//0 - 차감성공, 그외 - 실패
	private String regdate;
	private double longitude;
	private double latitude; 
	private String address;
	private String type;


	//최초 맵 기준  지정 변수 -> 현재는 시청으로 나중에는 자신의 위치로 변경하자.
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
	
	//※ 해당 위치정보는 3G/wi-fi/GPS수신 상태에 따라 실제 위치와 다를 수 있습니다. 토스트 등록 여부 판별 변수
	boolean isShowToast = true; 
	
	/** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.child_loca_view); //XML로 생성한 맵뷰를 SetContentView로 현재 레이아웃으로 셋팅
    	
    	Intent intent = getIntent();
        childCtn = intent.getStringExtra("phonenum");
    	if(childCtn == null){childCtn = "";}
    	
    	//NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude, latitude);
    	//먼저 해당 뷰의 부모를 초기화 - 하나의 뷰는 하나의 부모만을 가지기 때문에 부모를 초기화하여 재사용을 하자.
    	RelativeLayout parentView = (RelativeLayout) findViewById(R.id.relayout);
		parentView.removeView(mMapView);
		
		//정확한 맵을 보여주기 전까지 MapView 영역을 보이지 않게한다.
		parentView.setVisibility(View.INVISIBLE);
	
        //body 
        
    	// create map view
    	mMapView = (NMapView)findViewById(R.id.mapview); //앞으로의 작업을 위해 mapview의 객체를 하나 생성한다.
    	//네이버 로고를 이동시킨다.
    	//mMapView.setLogoImageOffset(100, 100);
    	// set a registered API key for Open MapViewer Library
    	mMapView.setApiKey(API_KEY); //지도 키 값을 셋팅한다.

    	// set the activity content to the parent view
    	//setContentView(mMapView);

    	// initialize map view
    	mMapView.setClickable(true); //지도 터치드래그가 가능하게 해주는 속성
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
    	//일반 지도 형식으로 보인다.
    	mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
    	
    	
    	//과금 대상인지 아닌지에 따라서 경고창 노출하지만, 결국 같은 쓰레드를 타게 되어있다.
    	if(getHowManyTry() >= 3){
    		AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
			ad.setTitle("현위치찾기");
			ad.setMessage("1일 무료 사용 건수를 초과 하였습니다.(1일 3회 무료)\n포인트로 조회 하시겠습니까?\n1회 조회 시 100포인트 소진");
			ad.setPositiveButton("위치찾기", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					payMode = "PAY";
					//API 호출 쓰레드 시작
			    	//class 최초 진입시 api 통신으로 위도경도를 가져온다.
			    	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog 보이기
			    	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
					thread.start();
				}
			});
			ad.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			ad.show();
    	}else{
    		payMode = "NOPAY";
    		//API 호출 쓰레드 시작
        	//class 최초 진입시 api 통신으로 위도경도를 가져온다.
        	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog 보이기
        	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
    		thread.start();
    	}
    }


	@Override
	protected void onDestroy() {
		//액티비티를 종료할때 맵뷰에 사용된 provider 를 반환한다. 
		LocationManager locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationMgr.removeUpdates(mMapLocationManager);
		super.onDestroy();
	}


	//API 호출 쓰레드
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
  	  				//결과를 XML 파싱하여 추출
  	  				
  	  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  	  				String strRegdate = WizSafeParser.xmlParser_String(returnXML,"<REGDATE>");
  	  				String encLongitude = WizSafeParser.xmlParser_String(returnXML,"<LONGITUDE>");
  	  				String encLatitude = WizSafeParser.xmlParser_String(returnXML,"<LATITUDE>");
  	  				String encAddress = WizSafeParser.xmlParser_String(returnXML,"<ADDRESS>");
  	  				String strType = WizSafeParser.xmlParser_String(returnXML,"<TYPE>");
  	  				//필요한 데이터 타입으로 형변환
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
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(2);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	
  	//차감 API 호출 쓰레드
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
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(4);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	
  	//LocationLog 쌓는 스레드 : 핸들러로 리턴되는것 없이 호출만하고 끝이다.
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
  				//핸들러 정상동작
  				if(httpResult == 0){
					//조회성공
  					//nmap 그리기에 필요한 값 선언
  					NMAP_LOCATION_DEFAULT = new NGeoPoint(longitude, latitude);
  					
  					TextView tv_checkTime = (TextView)findViewById(R.id.textView1); 
  			        if(tv_checkTime != null){
  			        	String showDate = WizSafeUtil.getDateFormat(regdate);
  			        	String gap = WizSafeUtil.getGapValue(type);
  			        	tv_checkTime.setText("일자 : "+ showDate +"\n오차범위 : "+ gap);
  			        }

  			        //새로고침 이미지 버튼 처리
  			        Button btn_alarm = (Button)findViewById(R.id.btn_retry);
  			        btn_alarm.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							try{
  								//과금 대상인지 아닌지에 따라서 경고창 노출
  						    	if(getHowManyTry() >= 3){
  						    		AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
  									ad.setTitle("현위치찾기");
  									ad.setMessage("1일 무료 사용 건수를 초과 하였습니다.(1일 3회 무료)\n포인트로 조회 하시겠습니까?\n1회 조회 시 100포인트 소진");
  									ad.setPositiveButton("위치찾기", new DialogInterface.OnClickListener() {
  										public void onClick(DialogInterface dialog, int which) {
  											payMode = "PAY";
  											//API 호출 쓰레드 시작
  									    	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog 보이기
  									    	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
  											thread.start();  											
  										}
  									});
  									ad.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
  										public void onClick(DialogInterface dialog, int which) {
  										}
  									});
  									ad.show();
  						    	}else{
  						    		payMode = "NOPAY";
  						    		//API 호출 쓰레드 시작
  						        	WizSafeDialog.showLoading(ChildLocationViewActivity.this);	//Dialog 보이기
  						        	CallGetNowLocationApiThread thread = new CallGetNowLocationApiThread(); 
  						    		thread.start();
  						    	}
  								
  		  			        }catch(Exception e){}
  						}
  					});
  			        
  			        try{

	  			  		//clear all overlay
	  			  		mOverlayManager.clearOverlays();
	  			  		
				    	//네이버 맵 위에 표시하고자 하는 곳의 위치를 오버레이로 띄운다.
				    	getCurrentLocationInfoPOIdataOverlay(); //Overlay에 띠울 것을 모아놓은 사용자 메소드 호출 (핀과 Path 그리기를 셋팅해놓은 사용자 메소드)
				    	//화면 로딩 완료후 안내정보 토스트를 4초간 보여준다.
  			        }catch(Exception e){}
  			       
  			        //과금이 아닐경우 - 맵뷰영역을 보이게 하고, 프로그래스를 종료하고, 오늘 몇번째 보여줬는지 셋팅
  			        //과금일 경우 - 현위치 조회로 인한 포인트 차감 API 호출
  			        if("NOPAY".equals(payMode)){
  		  			  	
  	  					//자녀에게 위치조회했음을 sms로 알린다. 
  	  					GregorianCalendar gc = new GregorianCalendar();
  	  					SimpleDateFormat dateFormat = new SimpleDateFormat("hh시 mm분");  
  	  					String checkTime = dateFormat.format(gc.getTime());

  	  					String myCtn = WizSafeUtil.getCtn(ChildLocationViewActivity.this);
  	  					String smsMsg = "[스마트안심]"+myCtn+"님이 "+ checkTime +"에 고객님의 위치를 조회했습니다.";
  	  					
  	  					//if(자녀 번호가 sms 수신이 가능한지 확인한다.){
  	  						//자녀에게 sms를 보낸
  	  						//boolean smsResult = WizSafeSms.sendSmsMsg(childCtn, smsMsg);
  	  					//}
  	  					//자녀에게 위치조회했음을 sms로 알린다.
  	  			        
  			        	//현재 부모가 자식을 조회했다는 Location 로그를 남기는 쓰레드시작 
  			        	CallInsertLocationLogThread thread = new CallInsertLocationLogThread(); 
						thread.start();

  			        }else if("PAY".equals(payMode)){
  			        	CallPayApiThread thread = new CallPayApiThread(); 
  						thread.start();
  			        }
  			        
				}else{
					WizSafeDialog.hideLoading();
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
					String title = "자녀현위치찾기실패";	
					String message = "자녀 현위치 정보를 조회할 수 없습니다.";	
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
  			}else if(msg.what == 1){
  				WizSafeDialog.hideLoading();
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
				String title = "통신 오류";	
				String message = "자녀 정보를 조회할 수 없습니다. 다시 시도해주세요.";	
				String buttonName = "확인";
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
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  			}else if(msg.what == 3){
  				if(payResult == 0){
  					//== 과금을 지불하고 호출되는 핸들러 ==
  					
  					//현재 부모가 자식을 조회했다는 Location 로그를 남기는 쓰레드시작 = 받는 핸들러는 없으며, 통신이 되면 종료됨 
			        CallInsertLocationLogThread thread = new CallInsertLocationLogThread(); 
					thread.start();
  	  				
  				}else if(payResult == 1){
  					//잔액부족인 경우
  					WizSafeDialog.hideLoading();
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
					ad.setTitle("포인트 안내");
					ad.setMessage("보유한 포인트가 부족합니다. 포인트 충전 후 다시 이용해 주세요.");
					ad.setPositiveButton("포인트\n충전하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
							Toast.makeText(ChildLocationViewActivity.this, "포인트 충전하기로 액티비티 이동", Toast.LENGTH_SHORT).show();
						}
					});
					ad.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
  				}
  			}else if(msg.what == 4){
  				WizSafeDialog.hideLoading();
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  			}else if(msg.what == 5){
  				if(locationLogResult == 0){
  					//== 최종적으로 맵뷰를 보여주며, 오늘 몇번 봤는지 셋팅하는 핸들러 ==
  	  				WizSafeDialog.hideLoading();
  	  				//맵뷰영역을 보이게 하고, 프로그래스를 종료하고, 오늘 몇번째 보여줬는지 셋팅
  	  				RelativeLayout mapViewArea = (RelativeLayout)findViewById(R.id.relayout);
  		        	mapViewArea.setVisibility(View.VISIBLE);
  		        	WizSafeDialog.hideLoading();
  		        	//오늘 현위치 조회를 몇번했는지 셋팅한다.
  		        	setHowManyTry();
  		        	
  			        //토스트플래그 값을 본후 토스트를 띄우고 난 후 플래그 변경하여 새로고침시에는 토스트를 띄우지 않도록 한다.
  			    	if(isShowToast){
  			    		Toast.makeText(ChildLocationViewActivity.this, "※ 해당 위치정보는 3G/wi-fi/GPS수신 상태에 따라 실제 위치와 다를 수 있습니다.", Toast.LENGTH_LONG).show();
  			    		isShowToast = false;
  			    	}
  				}else{
  					WizSafeDialog.hideLoading();
  	  				//핸들러 비정상
  	  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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
  				
  			}else if(msg.what == 6){
  				WizSafeDialog.hideLoading();
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildLocationViewActivity.this);
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


	//받아온 위도경도에 대해서 핀+주소말풍선을 지도에 표현하는 오버레이
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

	
	//현위치 찾기를 몇번이나 시도하였는지 확인한다.
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
		
		//오늘중에 시도한 횟수
		if(nowDate.equals(sdf.format(calendar.getTime()))){
			tryCount = Integer.parseInt(nowCount);
		}else{
			tryCount = 0;
		}

		return tryCount;
	}
	
	//위치찾기를 하고 난 후에 그날 몇번째 위치찾기를 하였는지 저장한다.
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