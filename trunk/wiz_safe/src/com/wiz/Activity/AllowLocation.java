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
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class AllowLocation extends Activity {
	
	String allowPhoneNumber = "";
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;

	//API 호출 후 RESULT_CD 부분을 받는 변수
	int allowApiResult = -1;
	int rejectApiResult = -1;
	
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allow_location);
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        allowPhoneNumber = intent.getStringExtra("allowPhoneNumber");
        
        //화면정의 위젯 정의
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        Button btn_agree = (Button)findViewById(R.id.btn_agree);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        
        //화면 위젯 설정
        textView1.setText(WizSafeUtil.setPhoneNum(allowPhoneNumber) + "님에게 고객님의 위치 정보가 제공 됩니다.");
        
        //동의 버튼 액션
        btn_agree.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//API 통신 쓰레드 시작한다.
			        WizSafeDialog.showLoading(AllowLocation.this);	//Dialog 보이기
			        CallAllowLocationAllowApiThread thread = new CallAllowLocationAllowApiThread();
					thread.start();
				}
			}
		);
        
        //거절 버튼 액션
        btn_cancel.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					AlertDialog.Builder submitAlert = new AlertDialog.Builder(AllowLocation.this);
					submitAlert.setTitle("위치허용동의");
					submitAlert.setMessage(WizSafeUtil.setPhoneNum(allowPhoneNumber)+"님의 부모 요청을 거절 하시겠습니까? ");
					submitAlert.setPositiveButton("거절", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//API 통신 쓰레드 시작한다.
					        WizSafeDialog.showLoading(AllowLocation.this);	//Dialog 보이기
					        CallAllowLocationRejectApiThread thread = new CallAllowLocationRejectApiThread();
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
	 }
	 
	//API 호출 쓰레드
  	class CallAllowLocationAllowApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(AllowLocation.this));
  				String enc_parentCtn = WizSafeSeed.seedEnc(allowPhoneNumber);
  				String type = "ACCEPT";
  				String url = "https://www.heream.com/api/allowLocation.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&parentCtn=" + URLEncoder.encode(enc_parentCtn) +"&type=" + URLEncoder.encode(type);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				allowApiResult = Integer.parseInt(resultCode);
  				
  				//자녀리스트 등록이 되고난 후 , 위치허용 동의를 한 순간 부터 서버로 위치 정보를 제공하도록 셋팅
  				if(allowApiResult == 0){
  					WizSafeUtil.setSendLocationUser(AllowLocation.this, true);	//로컬벨류셋팅
  				}

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  //API 호출 쓰레드
  	class CallAllowLocationRejectApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(AllowLocation.this));
  				String enc_parentCtn = WizSafeSeed.seedEnc(allowPhoneNumber);
  				String type = "REJECT";
  				String url = "https://www.heream.com/api/allowLocation.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&parentCtn=" + URLEncoder.encode(enc_parentCtn) +"&type=" + URLEncoder.encode(type);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				rejectApiResult = Integer.parseInt(resultCode);

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
  				if(allowApiResult == 0){
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
  					String title = "동의";	
  					String message = "지금부터 " + WizSafeUtil.setPhoneNum(allowPhoneNumber) + "님은 고객님의 위치를 찾을 수 있습니다.";	
  					String buttonName = "확인";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							finish();
  						}
  					});
  					ad.show();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
  				if(rejectApiResult == 0){
	  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
					String title = "위치허용동의";	
					String message = WizSafeUtil.setPhoneNum(allowPhoneNumber) + "님의 부모 요청을 거절하였습니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
