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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceAddActivity extends Activity {
	
	ArrayAdapter<CharSequence> weekAdspin;
	ArrayAdapter<CharSequence> startTimeAdspin;
	ArrayAdapter<CharSequence> endTimeAdspin;
	ArrayAdapter<CharSequence> timeIntervalAdspin;
	
	//전역변수로써 API 통신할 변수들을 셋팅한다.
	String phonenum = "";
	String childName = "";
	String startDay = "";			//시작요일
	String endDay = "";				//종료요일
	String startTime = "";			//시작시간
	String endTime = "";			//종료시간
	String interval = "";			//간격(분단위)
	String traceLogCode = "";		//등록 SEQ
	String textDay = "";			//설정경고창에 뿌려질 문구 - 설정요일
	String textStartTime = "";		//설정경고창에 뿌려질 문구 - 설정시간
	String textEndTime = "";		//설정경고창에 뿌려질 문구 - 설정시간
	String textInterval = "";		//설정경고창에 뿌려질 문구 - 설정간격
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
		
        //전단계에서 값을 넘겨 받은 부분을 처리
   		Intent intent = getIntent();
   		phonenum = intent.getStringExtra("phonenum");
   		childName = intent.getStringExtra("childName");
   		if(intent.getStringExtra("startDay") != null){
   			startDay = intent.getStringExtra("startDay");
   		}
   		if(intent.getStringExtra("endDay") != null){
   			endDay = intent.getStringExtra("endDay");
   		}
		if(intent.getStringExtra("startTime") != null){
			startTime = intent.getStringExtra("startTime");
		}
		if(intent.getStringExtra("endTime") != null){
			endTime = intent.getStringExtra("endTime");
		}
		if(intent.getStringExtra("interval") != null){
			interval = intent.getStringExtra("interval");
		}
		if(intent.getStringExtra("traceLogCode") != null){
			traceLogCode = intent.getStringExtra("traceLogCode");
			findViewById(R.id.btn_setup).setBackgroundResource(R.drawable.btn_modify_selector);
		}else{
			traceLogCode = "INSERT";
		}
        
        //셀렉트 박스 구성(요일설정)
        Spinner weekSpiner = (Spinner)findViewById(R.id.weekSpinner);
        weekSpiner.setPrompt("요일을 설정하세요.");
        weekAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_weekSetup, android.R.layout.simple_spinner_item);
        weekAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpiner.setAdapter(weekAdspin);
        
        if(intent.getStringExtra("startDay") != null && intent.getStringExtra("endDay") != null){
	        if("1".equals(startDay) && "5".equals(endDay)){
	        	weekSpiner.setSelection(0);
	        }else{
	        	weekSpiner.setSelection(1);
	        }
        }else{
        	weekSpiner.setSelection(0);
        }
        
        //셀렉트 박스 액션(요일설정)
        weekSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	//셀렉트 될때마다 탄다.
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				textDay = weekAdspin.getItem(position).toString();
				switch(position){
				case 0:		//첫번째것을 선택 월~금
					startDay = "1";
					endDay = "5";
					break;
				case 1:		//두번째것을 선택 월~일
					startDay = "1";
					endDay = "0";
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
        	
		});
        
        //셀렉트 박스 구성(시작시간설정)
        Spinner timeStartSpinner = (Spinner)findViewById(R.id.timeStartSpinner);
        timeStartSpinner.setPrompt("시작시간을 설정하세요.");
        startTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_startTimeSetup, android.R.layout.simple_spinner_item);
        startTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeStartSpinner.setAdapter(startTimeAdspin);
        if(intent.getStringExtra("startTime") != null){
        	timeStartSpinner.setSelection(Integer.parseInt(startTime));
        }else{
        	timeStartSpinner.setSelection(13);
        }
        
        timeStartSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(Integer.toString(position).length() > 1){
        			startTime = Integer.toString(position);
        		}else{
        			startTime = "0" + Integer.toString(position);
        		}
        		
        		textStartTime = startTimeAdspin.getItem(position).toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //셀렉트 박스 구성(종료시간설정)
        Spinner timeEndSpinner = (Spinner)findViewById(R.id.timeEndSpinner);
        timeEndSpinner.setPrompt("종료시간을 설정하세요.");
        endTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_endTimeSetup, android.R.layout.simple_spinner_item);
        endTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeEndSpinner.setAdapter(endTimeAdspin);
        if(intent.getStringExtra("endTime") != null){
        	//종료시간이 00시 이면 셀렉트박스 리스트의 최하단의 오전 12시를 선택한다.
        	if("00".equals(endTime)){
        		endTime = "24";
        	}
        	timeEndSpinner.setSelection(Integer.parseInt(endTime)-1);
        }else{
        	timeEndSpinner.setSelection(18);
        }
        	
        
        timeEndSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(Integer.toString(position + 1).length() > 1){
        			endTime = Integer.toString(position + 1);
        		}else{
        			endTime = "0" + Integer.toString(position + 1);
        		}
        		//24시는 00시로 대체해서 DB에 저장하기 위한 변형(종료시간만 해당됨)
        		if("24".equals(endTime)){
        			endTime = "00";
        		}
        		
        		textEndTime = endTimeAdspin.getItem(position).toString();
        		
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //셀렉트 박스 구성(간격설정)
        Spinner timeIntervalSpinner = (Spinner)findViewById(R.id.timeIntervalSpinner);
        timeIntervalSpinner.setPrompt("시간간격을 설정하세요.");
        timeIntervalAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_timeIntervalSetup, android.R.layout.simple_spinner_item);
        timeIntervalAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIntervalSpinner.setAdapter(timeIntervalAdspin);
        if(intent.getStringExtra("interval") != null){
        	timeIntervalSpinner.setSelection((Integer.parseInt(interval)/60)-1);
        }
        
        
        timeIntervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		interval = Integer.toString((position + 1) * 60);
        		textInterval = timeIntervalAdspin.getItem(position).toString();
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //버튼 액션 정의
        findViewById(R.id.btn_setup).setOnClickListener(mClickListener);
        findViewById(R.id.btn_cancel).setOnClickListener(mClickListener);

	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_setup:
				//경고창 출력
				AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceAddActivity.this);
				submitAlert.setTitle("발자취 설정");
				submitAlert.setMessage("휴대폰 번호 : "+WizSafeUtil.setPhoneNum(phonenum)+"\n설정 요일 : " + textDay + "\n설정 시간 : " + textStartTime + "~" + textEndTime + "까지" + "\n설정 간격 : " + textInterval + "\n※ 1일 100포인트 씩 자동 소진 됩니다.");
				submitAlert.setPositiveButton("등록", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//API 통신 쓰레드 시작한다.
				        WizSafeDialog.showLoading(ChildTraceAddActivity.this);	//Dialog 보이기
				        CallChildTraceAddApiThread thread = new CallChildTraceAddApiThread();
						thread.start();
					}
				});
				submitAlert.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				submitAlert.show();
				break;
			case R.id.btn_cancel:
				ChildTraceAddActivity.this.finish();
				break;
			}
		}
	};
	
	
	//API 호출 쓰레드
  	class CallChildTraceAddApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceAddActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/addChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) +"&childCtn="+ URLEncoder.encode(enc_childCtn) +"&childName="+ URLEncoder.encode(enc_childCtn) + "&startDay=" + URLEncoder.encode(startDay) + "&endDay=" + URLEncoder.encode(endDay) + "&startTime=" + URLEncoder.encode(startTime) + "&endTime=" + URLEncoder.encode(endTime) + "&interval=" + URLEncoder.encode(interval) + "&traceLogCode=" + URLEncoder.encode(traceLogCode);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode);

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(addApiResult == 0){
					finish();
				}else if(addApiResult == 1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceAddActivity.this);
					ad.setTitle("포인트 안내");
					ad.setMessage("보유한 포인트가 부족합니다. 포인트 충전 후 다시 이용해 주세요.");
					ad.setPositiveButton("포인트\n충전하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(ChildTraceAddActivity.this, "포인트 충전하기로 액티비티 이동", Toast.LENGTH_SHORT).show();
						}
					});
					ad.setNegativeButton("발자취설정\n닫기", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
				}else if(addApiResult == -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceAddActivity.this);
					String title = "등록 오류";	
					String message = "발자취 등록 중 오류가 발생하였습니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}
  			}if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceAddActivity.this);
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
  	
}
