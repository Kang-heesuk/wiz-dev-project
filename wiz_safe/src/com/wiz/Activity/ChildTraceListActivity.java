package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	String myPoint = "";
	String startDay = "";
	String endDay = "";
	String startTime = "";
	String endTime = "";
	String interval = "";
	String nowOperationState = "";
	String traceLogCode = "";
	
	//현재 발자취등록을 하였는지 판단.
	boolean isRegisterTrace = false;
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
	//고객정보 API 호출 후의 결과값
	int customerInfoApiResult = -1;
	
	//삭제 API 호출 후의 결과값
	int deleteApiResult = -1;
	
	//삭제 API 호출 후의 결과값
	int switchApiResult = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_list);
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");
        
        //API 호출 쓰레드 시작
    	//발자취 정보를 가져온다.
    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog 보이기
    	CallChildTraceListApiThread thread = new CallChildTraceListApiThread(); 
		thread.start();

	}
	
	//API 호출 쓰레드
  	class CallChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);

  				//고객정보를 가져오는 통신
  				url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getCustomerInfo = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				customerInfoApiResult = Integer.parseInt(resultCode_getCustomerInfo);
  				if(customerInfoApiResult == 0){
  					myPoint = WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>");
  					if(myPoint == null) myPoint = WizSafeSeed.seedEnc("0");
  					myPoint = WizSafeSeed.seedDec(myPoint);
  				}
  				
  				//발자취 리스트 가져오는 통신
  				url = "https://www.heream.com/api/getChildTraceList.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&childCtn="+ URLEncoder.encode(enc_childCtn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getList = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode_getList);
  				
  				if(addApiResult == 0){
  					isRegisterTrace = true;
  				}else{
  					isRegisterTrace = false;
  				}
				startDay = WizSafeParser.xmlParser_String(returnXML,"<START_DAY>");
				endDay = WizSafeParser.xmlParser_String(returnXML,"<END_DAY>");
				startTime = WizSafeParser.xmlParser_String(returnXML,"<START_TIME>");
				endTime = WizSafeParser.xmlParser_String(returnXML,"<END_TIME>");
				interval = WizSafeParser.xmlParser_String(returnXML,"<INTERVAL>");
				nowOperationState = WizSafeParser.xmlParser_String(returnXML,"<TRACE_STATE>");
				traceLogCode = WizSafeParser.xmlParser_String(returnXML,"<TRACELOG_CODE>");

				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API 호출 쓰레드 삭제하기
  	class CallDeleteChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/deleteChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&childCtn="+ URLEncoder.encode(enc_childCtn) + "&traceLogCode="+ URLEncoder.encode(traceLogCode);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				deleteApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(2);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
  	//API 호출 쓰레드 시작정지
  	class CallSwitchChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String traceState = "";
  				
  				//변경할 상태값
  				if("0".equals(nowOperationState)){
  					traceState = "1";
  				}else{
  					traceState = "0";
  				}
  				
  				String url = "https://www.heream.com/api/switchChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&childCtn="+ URLEncoder.encode(enc_childCtn) + "&traceState="+ URLEncoder.encode(traceState) + "&traceLogCode="+ URLEncoder.encode(traceLogCode);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				switchApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(4);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(5);
  			}
  		}
  	}
  	
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(addApiResult == 0 || addApiResult == 1){
  					
  					//리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
  			        if(!isRegisterTrace){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_trace1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  			        
  			        LinearLayout layout_1 = (LinearLayout)findViewById(R.id.layout_1);
  			        Button nowStateBtn = (Button)findViewById(R.id.nowStateBtn);
  			        
  			        //현재 포인트에 따라서 100포인트도 없다면 포인트 부족을 보여주고, 동작상태를 비동작중으로 강제설정한다.
  			        int myRemainPoint = 0;
  			        if(myPoint == null || "".equals(myPoint)){
  			        	myPoint = "0";
  			        }
  			        myRemainPoint = Integer.parseInt(myPoint);
  			        TextView pointArea = (TextView)findViewById(R.id.pointArea) ;
  			        if(myRemainPoint < 100){
  			        	pointArea.setVisibility(View.VISIBLE);
  			        	nowOperationState = "0";
  			        }
  			        
  			        //현재 상태에 따라 동작중 또는 비동작중에대한 백그라운드 상태 및 클릭 가능 상태 변경
  			        if("1".equals(nowOperationState)){
  			        	//1. 백그라운드 변환
  			        	layout_1.setBackgroundResource(R.drawable.trace_playlist_bg);
  			        	//2. 레이아웃 클릭시 세부보기로 이동
  			        	layout_1.setOnClickListener(
  							new Button.OnClickListener(){
  								public void onClick(View v) {
  									Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceDetailListActivity.class);
  									intent.putExtra("phonenum", phonenum);
  									intent.putExtra("childName", childName);
  									intent.putExtra("startDay", startDay);
  									intent.putExtra("endDay", endDay);
  									intent.putExtra("startTime", startTime);
  									intent.putExtra("endTime", endTime);
  									intent.putExtra("interval", interval);
  									
  									startActivity(intent);
  								}
  							}
  						);
  			        }else{
  			        	//1. 백그라운드 변환, 클릭시 반응 없음.
  			        	layout_1.setBackgroundResource(R.drawable.trace_stoplist_bg);
  			        }
  			        
  			        //자녀발자취 실행 중지 버튼 액션 설정
  			        nowStateBtn.setOnClickListener(
						new Button.OnClickListener(){
							public void onClick(View v) {
								//API 호출 쓰레드 시작
						    	//현재 상태 on/off 변경 스레드 호출
						    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog 보이기
						    	CallSwitchChildTraceListApiThread thread = new CallSwitchChildTraceListApiThread(); 
								thread.start();
							}
						}
					);
  			        
  			        //자녀이름, 폰번호, 요일, 시간 , 간격을 화면에 노출
  					TextView childNameArea = (TextView)findViewById(R.id.childNameArea);
  					TextView phonenumArea = (TextView)findViewById(R.id.phonenumArea);
  			        TextView weekendArea = (TextView)findViewById(R.id.weekendArea);
  			        TextView timeArea = (TextView)findViewById(R.id.timeArea);
  			        TextView intervalArea = (TextView)findViewById(R.id.intervalArea);
  			        
  			        childNameArea.setText(childName);
  			        phonenumArea.setText("(" + WizSafeUtil.setPhoneNum(phonenum) + ")");
  			        weekendArea.setText("요일 : " + WizSafeUtil.dayConvertFromNumberToString(startDay) +" ~ "+ WizSafeUtil.dayConvertFromNumberToString(endDay));
  			        timeArea.setText("시간 : " + WizSafeUtil.timeConvertFromNumberToString0to23(startTime) +" ~ "+ WizSafeUtil.timeConvertFromNumberToString1to24(endTime));
  			        intervalArea.setText("간격 : " + WizSafeUtil.intervalConvertMinToHour(interval) + "시간");
  			        
  			        //수정하기와 삭제하기 버튼 액션 정의
  			        Button btn_modify = (Button)findViewById(R.id.btn_modify);
  			        Button btn_delete = (Button)findViewById(R.id.btn_delete);
  			        
  			        btn_modify.setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceAddActivity.class);
  								intent.putExtra("phonenum", phonenum);
  								intent.putExtra("childName", childName);
  								intent.putExtra("startDay", startDay);
  								intent.putExtra("endDay", endDay);
  								intent.putExtra("startTime", startTime);
  								intent.putExtra("endTime", endTime);
  								intent.putExtra("interval", interval);
  								intent.putExtra("nowOperationState", nowOperationState);
  								intent.putExtra("traceLogCode", traceLogCode);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        btn_delete.setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceListActivity.this);
  								submitAlert.setTitle("발자취삭제");
  								submitAlert.setMessage("발자취 설정을 삭제 하시겠습니까?\n휴대폰 번호 : "+ WizSafeUtil.setPhoneNum(phonenum) + "\n설정 요일 : " + WizSafeUtil.dayConvertFromNumberToString(startDay) + "~" + WizSafeUtil.dayConvertFromNumberToString(endDay) + "요일\n설정 시간 : " + WizSafeUtil.timeConvertFromNumberToString0to23(startTime)+ "~" + WizSafeUtil.timeConvertFromNumberToString1to24(endTime) + "까지" + "\n설정 간격 : " + WizSafeUtil.intervalConvertMinToHour(interval) + "시간\n※ 삭제 시 금일 포인트는 환급 되지 않습니다.");
  								submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
  									public void onClick(DialogInterface dialog, int which) {
  										//API 호출 쓰레드 시작
  								    	//발자취 정보를 가져온다.
  								    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog 보이기
  								    	CallDeleteChildTraceListApiThread thread = new CallDeleteChildTraceListApiThread(); 
  										thread.start();
  									}
  								});
  								submitAlert.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
  									public void onClick(DialogInterface dialog, int which) {
  									}
  								});
  								submitAlert.show();
  							}
  						}
  					);        
  			        
  			        //자녀등록하기 버튼액션
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceAddActivity.class);
  								intent.putExtra("phonenum", phonenum);
  								intent.putExtra("childName", childName);
  								startActivity(intent);
  							}
  						}
  					);
				}else if(addApiResult == -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  			}if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  			}else if(msg.what == 2){
  				
  				if(deleteApiResult == 0){
  					//액티비티 재시작
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
  					String title = "통신 오류";	
  					String message = "통신 중 오류가 발생하였습니다.";	
  					String buttonName = "확인";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  						}
  					});
  					ad.show();
  				}
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				ad.show();
  			}else if(msg.what == 4){
  				if(switchApiResult == 0){
  					//액티비티 재시작
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
  					String title = "통신 오류";	
  					String message = "통신 중 오류가 발생하였습니다.";	
  					String buttonName = "확인";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  						}
  					});
  					ad.show();
  				}
  			}else if(msg.what == 5){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				ad.show();
  			}
  		}
  	};

}
