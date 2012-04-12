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
import android.widget.EditText;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class QuestionActivity extends Activity {

	EditText editText1;
	EditText editText2;
	String tempEditText1 = "";
	String tempEditText2 = "";
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	//API 호출 후 RESULT_CD 부분을 받는 변수
	int addApiResult = -1;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
        
        Button btn_answer = (Button)findViewById(R.id.btn_answer);
        btn_answer.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(QuestionActivity.this, AnswerListActivity.class);
				startActivity(intent);
				finish();
			}
		});

        Button btn_question = (Button)findViewById(R.id.btn_question);
        btn_question.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				tempEditText1 = editText1.getText().toString();
				tempEditText2 = editText2.getText().toString();
				
				if(tempEditText1 == null){
					tempEditText1 = "";
				}
				if(tempEditText2 == null){
					tempEditText2 = "";
				}
				//제목이 없을경우
				if(tempEditText1 == null || "".equals(tempEditText1)){
					AlertDialog.Builder ad = new AlertDialog.Builder(QuestionActivity.this);
					String title = "입력 오류";	
					String message = "제목을 입력하여 주십시오.";	
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
				//문의사항이 없을경우
				if(tempEditText2 == null || "".equals(tempEditText2)){
					AlertDialog.Builder ad = new AlertDialog.Builder(QuestionActivity.this);
					String title = "입력 오류";	
					String message = "문의 내용을 입력하여 주십시오.";	
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
		        WizSafeDialog.showLoading(QuestionActivity.this);	//Dialog 보이기
		        CallManToManRequestApi thread = new CallManToManRequestApi();
				thread.start();

				
			}
		});
    }
    
    //API 호출 쓰레드
  	class CallManToManRequestApi extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(QuestionActivity.this));
  				String url = "https://www.heream.com/api/manToManRequest.jsp?ctn="+ URLEncoder.encode(enc_ctn) +"&title="+ URLEncoder.encode(WizSafeSeed.seedEnc(tempEditText1)) +"&content="+ URLEncoder.encode(WizSafeSeed.seedEnc(tempEditText2));
  				
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
					//자녀등록이 정상적으로 이루어진 경우 (ChildAddCompleteActivity) 로 간다	
  					Intent intent = new Intent(QuestionActivity.this, AnswerListActivity.class);
  					startActivity(intent);
  					finish();
				}else{
					AlertDialog.Builder ad = new AlertDialog.Builder(QuestionActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(QuestionActivity.this);
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