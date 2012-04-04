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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildAddActivity extends Activity {
	
	EditText childPhone;
	EditText childName;
	
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	String childPhoneTemp;
	String childNameTemp;

	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
	//체크박스 클릭 하였는지 아닌지에 관한 플래그 변수
	boolean haveCheck = true;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_add);
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//몇번째 자리인지
        alreadyRegCtn = intent.getStringExtra("alreadyRegCtn");		//몇번째 자리인지
        
        childPhone = (EditText)findViewById(R.id.childPhone);
        childName = (EditText)findViewById(R.id.childName);
        childName.setHint("미입력시 '자녀" + whereFlag + "' 자동 설정 됨");
        
        //버튼 액션 및 체크박스 클릭 액션 정의
        findViewById(R.id.goSubmit).setOnClickListener(mClickListener);
        findViewById(R.id.checkBox1).setOnClickListener(mClickListener_checkBodx);
        findViewById(R.id.checkBox2).setOnClickListener(mClickListener_checkBodx);
        findViewById(R.id.checkBox3).setOnClickListener(mClickListener_checkBodx);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goSubmit:
				childPhoneTemp = childPhone.getText().toString();
				childNameTemp = childName.getText().toString();
				if(childPhoneTemp == null){
					childPhoneTemp = "";
				}
				if("".equals(childNameTemp)){
					childNameTemp = "자녀" + whereFlag;
				}
				
				if("".equals(childPhoneTemp)){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
					String title = "입력 오류";	
					String message = "등록할 자녀의 휴대폰 번호를 입력하여 주십시오.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				if(childPhoneTemp.equals(WizSafeUtil.getCtn(ChildAddActivity.this))){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
					String title = "입력 오류";	
					String message = "본인을 자녀로 등록할 수 없습니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				
				if(alreadyRegCtn.indexOf(childPhoneTemp) > -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
					String title = "입력 오류";	
					String message = "이미 등록된 자녀입니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				
				//API 통신 쓰레드 시작한다.
		        WizSafeDialog.showLoading(ChildAddActivity.this);	//Dialog 보이기
				callChildAddApiThread thread = new callChildAddApiThread();
				thread.start();
				
				break;
			}
		}
		
	};
	
	TextView.OnClickListener mClickListener_checkBodx = new Button.OnClickListener(){
		public void onClick(View v) {
			if(v.getId() == R.id.checkBox1 || v.getId() == R.id.checkBox2 || v.getId() == R.id.checkBox3){
				
				TextView checkBoxView = (TextView)findViewById(R.id.checkBox2);
				if(checkBoxView.getBackground().toString().indexOf("BitmapDrawable") > -1){
					checkBoxView.setBackgroundColor(Color.TRANSPARENT);
					haveCheck = false;
				}else{
					checkBoxView.setBackgroundResource(R.drawable.check);
					haveCheck = true;
				}
			}
		}
	};	
	
	
	//API 호출 쓰레드
  	class callChildAddApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildAddActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(childPhoneTemp);
  				String enc_childName = WizSafeSeed.seedEnc(childNameTemp);
  				String url = "https://www.heream.com/api/addRelation.jsp?parentCtn="+ URLEncoder.encode(enc_ctn) +"&childCtn="+ URLEncoder.encode(enc_childCtn) +"&childName="+ URLEncoder.encode(enc_childName) +"&type=" + URLEncoder.encode("01");
  				
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
  				
  				if(haveCheck){
  					url = "https://www.heream.com/api/sendAppDownSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&d_ctn=" + URLEncoder.encode(enc_childCtn);
  					urlConn = (HttpURLConnection) new URL(url).openConnection();
  	  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
  				}

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
					//자녀등록이 정상적으로 이루어진 경우 (ChildAddCompleteActivity) 로 간다	
  					Intent intent = new Intent(ChildAddActivity.this, ChildAddCompleteActivity.class);
  					intent.putExtra("whereFlag", whereFlag);
  					intent.putExtra("phonenum", childPhone.getText().toString());
  					startActivity(intent);
  					finish();

				}else if(addApiResult == 1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
					String title = "등록 오류";	
					String message = "이미 등록된 자녀 입니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}else if(addApiResult == -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
					String title = "등록 오류";	
					String message = "자녀등록 중 오류가 발생하였습니다.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildAddActivity.this);
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
