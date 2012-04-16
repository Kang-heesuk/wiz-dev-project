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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class SetupActivity extends Activity {
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
	//문자수신 상태값 변경하는 API 호출 후 RESULT_CD 부분을 받는 변수
	int updateApiResult = -1;
	
	//현재 나의 문자 수신 상태값
	String myAlramState = "1";
	
	//바꿀 나의 문자 수신 상태값
	String setMyAlramState = "";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_list);
        
        //API 호출 쓰레드 시작
    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog 보이기
    	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		thread.start();
    }
    
    //API 호출 쓰레드(고객의 문자수신 상태값 가져오기)
  	class CallGetCustomerInformationApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(SetupActivity.this));

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
  				addApiResult = Integer.parseInt(resultCode_getCustomerInfo);
  				if(addApiResult == 0){
  					myAlramState = WizSafeParser.xmlParser_String(returnXML,"<ALRAMSTATE>");
  				}

				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API 호출 쓰레드(고객의 문자수신 상태값 변경하기)
  	class CallSetCustomerAlramStateApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(SetupActivity.this));

  				//문자수신 상태값을 변경시키는 API
  				url = "https://www.heream.com/api/alramOnOff.jsp?ctn=" + URLEncoder.encode(enc_ctn) + "&setValue=" + URLEncoder.encode(setMyAlramState);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getCustomerInfo = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				updateApiResult = Integer.parseInt(resultCode_getCustomerInfo);

				pHandler.sendEmptyMessage(2);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(addApiResult == 0){
  					Button btn01 = (Button)findViewById(R.id.btn1);
  	  		        btn01.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, NoticeListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn02 = (Button)findViewById(R.id.btn2);
  	  		        btn02.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, UseInfoListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn03 = (Button)findViewById(R.id.btn3);
  	  		        btn03.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, FaqListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn04 = (Button)findViewById(R.id.btn4);
  	  		        btn04.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, QuestionActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn05 = (Button)findViewById(R.id.btn5);
  	  		        btn05.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, PointLogActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn06 = (Button)findViewById(R.id.btn6);
  	  		        btn06.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, LocationLogActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        
  	  		        Button btn_sms = (Button)findViewById(R.id.btn_sms);
  	  		        if("1".equals(myAlramState)){
  	  		        	btn_sms.setTag(R.drawable.btn_on);
  	  		        	btn_sms.setBackgroundResource(R.drawable.btn_on);
  	  		        }else{
  	  		        	btn_sms.setTag(R.drawable.btn_off);
  	  		        	btn_sms.setBackgroundResource(R.drawable.btn_off);
  	  		        }
  	  		        btn_sms.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						int compareValue = (Integer)findViewById(R.id.btn_sms).getTag();
  	  						if(compareValue == R.drawable.btn_off){
  	  							setMyAlramState = "1";
  	  							findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_on);
  	  							findViewById(R.id.btn_sms).setTag(R.drawable.btn_on);
  	  							
  	  							//API 호출 쓰레드 시작
	  	  				    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog 보이기
	  	  				    	CallSetCustomerAlramStateApiThread thread = new CallSetCustomerAlramStateApiThread(); 
	  	  						thread.start();
  	  						}else{
  	  							setMyAlramState = "0";
  	  							findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_off);
  	  							findViewById(R.id.btn_sms).setTag(R.drawable.btn_off);
  	  							
	  	  						//API 호출 쓰레드 시작
	  	  				    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog 보이기
	  	  				    	CallSetCustomerAlramStateApiThread thread = new CallSetCustomerAlramStateApiThread(); 
	  	  						thread.start();
  	  						}
  	  					}
  	  				});
  	  		        
  	  		        Button btn_hide = (Button)findViewById(R.id.btn_hide);
  	  		        if(WizSafeUtil.isHiddenOnUser(SetupActivity.this)){
  	  		        	btn_hide.setTag(R.drawable.btn_on);
  	  		        	btn_hide.setBackgroundResource(R.drawable.btn_on);
  	  		        }else{
  	  		        	btn_hide.setTag(R.drawable.btn_off);
  	  		        	btn_hide.setBackgroundResource(R.drawable.btn_off);
  	  		        }
  	  		        
  	  		        btn_hide.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						int compareValue = (Integer)findViewById(R.id.btn_hide).getTag	();
  	  						if(compareValue == R.drawable.btn_off){
  	  							AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  	  							String title = "위치숨김";	
  	  							String message = "위치숨김 기능을 활성화 하면 부모님이 안전하게 지켜줄 수 없습니다.";
  	  							ad.setTitle(title);
  	  							ad.setMessage(message);
  	  							ad.setPositiveButton("켜기", new DialogInterface.OnClickListener() {
  	  								public void onClick(DialogInterface dialog, int which) {
  	  									//휴대폰안에 저장된 위치 수신 플래그를 변경하여 재 저장한다.
  	  									LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  	  									edit = LocalSave.edit();
  	  									edit.putString("isHiddenOnUser", "0");
  	  									edit.commit();
  	  									findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_on);
  	  									findViewById(R.id.btn_hide).setTag(R.drawable.btn_on);
  	  								}
  	  							});
  	  							ad.setNegativeButton(R.string.btn_cancel, null);
  	  							ad.show();
  	  						}else{
  	  							//휴대폰안에 저장된 위치 수신 플래그를 변경하여 재 저장한다.
  	  							LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  	  							edit = LocalSave.edit();
  	  							edit.putString("isHiddenOnUser", "1");
  	  							edit.commit();
  	  							findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_off);
  	  							findViewById(R.id.btn_hide).setTag(R.drawable.btn_off);
  	  						}
  	  					}
  	  				});
  				}else{
  					//조회실패
  					AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  					String title = "통신 오류";	
  					String message = "통신 중 오류가 발생하였습니다. ";	
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
  				//조회실패
				AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다. ";	
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
  				if(updateApiResult == 0){
  					//액티비티 재시작
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					//조회실패
  					AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  					String title = "통신 오류";	
  					String message = "통신 중 오류가 발생하였습니다. ";	
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
  				//조회실패
				AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다. ";	
				String buttonName = "확인";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ad.show();
  			}
  		}
  	};
}