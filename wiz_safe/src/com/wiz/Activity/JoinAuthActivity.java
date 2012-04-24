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
import android.widget.EditText;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class JoinAuthActivity extends Activity {
    
	EditText editText1;
	String tempEditText = "";
	int authResult = 1;		//0 - 인증번호매치 및 가입성공 , 그외 - 실패
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	long tempTime;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_auth);
        
        //들어오게되면 인증번호 재전송 비활성화
        renumBtnImgSetThread subThread = new renumBtnImgSetThread(); 
        subThread.start();
        tempTime = System.currentTimeMillis();
                
        editText1 = (EditText)findViewById(R.id.editText1);
        
        String ctn = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(JoinAuthActivity.this));
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText(ctn + " 번호로\n인증번호가 발송되었습니다.");
        
        Button btn_join = (Button)findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				tempEditText = editText1.getText().toString();
				
				if("".equals(tempEditText.trim())){
					AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
					String title = "인증 오류";	
					String message = "인증번호를 입력해주세요.";	
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
				
				//API 호출 쓰레드 시작
		        WizSafeDialog.showLoading(JoinAuthActivity.this);	//Dialog 보이기
		        callAuthCompleteApiThread thread = new callAuthCompleteApiThread(); 
				thread.start();

			} 
		}); 
        
        final Button btn_renum = (Button)findViewById(R.id.btn_renum);
        btn_renum.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
  				
				//비활성화 된지 2분이 지났다면 활성화 시킴
				if(System.currentTimeMillis() - tempTime >= (1000 * 60 * 2)){
					
					btn_renum.setBackgroundResource(R.drawable.btn_certify_renum_on);
	  				
					//API 호출 쓰레드 시작
			        WizSafeDialog.showLoading(JoinAuthActivity.this);	//Dialog 보이기
			        callAuthSMSApiThread thread = new callAuthSMSApiThread(); 
					thread.start();
				}
			} 
		});
    }
    
  	
  	//API 호출 쓰레드
  	class callAuthCompleteApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAuthActivity.this));
  				String enc_authNum = WizSafeSeed.seedEnc(tempEditText);
  				String url = "https://www.heream.com/api/authComplete.jsp?ctn=" + URLEncoder.encode(enc_ctn) + "&authNum=" + URLEncoder.encode(enc_authNum);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				authResult = Integer.parseInt(resultCode);

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API 호출 쓰레드
  	class callAuthSMSApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAuthActivity.this));
  				String url = "https://www.heream.com/api/sendAuthSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
	//API 호출 쓰레드
	class renumBtnImgSetThread extends Thread{
		public void run(){
			try{
				//120sec 후
				renumBtnImgSetThread.sleep(1000*60*2);

				pHandler.sendEmptyMessage(4);
			}catch(Exception e){				
				pHandler.sendEmptyMessage(5);
			}
		}
	}

  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(authResult == 0){
					//가입이 정상적으로 이루어진 사람이므로, 로컬밸류의 가입부분을 업데이트 해준다.					
					SharedPreferences LocalSave;
					SharedPreferences.Editor edit;
			    	LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
					edit = LocalSave.edit();
					edit.putString("isAuthOkUser", "01");
					edit.commit();
			    	
			    	//메인 액티비티로 이동
					Intent intent = new Intent(JoinAuthActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}else{	
					AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
					String title = "인증 오류";	
					String message = "인증번호가 올바르지 않습니다.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
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
  				
  				//API 호출 쓰레드 시작 - 2분 체크 스레드
		        renumBtnImgSetThread subThread = new renumBtnImgSetThread(); 
		        subThread.start();
  				
  				//현재시간을 다시 셋팅
  				tempTime = System.currentTimeMillis();
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
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
  			}else if(msg.what == 4){
  				//2분 자고 일어나서 버튼 활성화
  				Button btn_renum = (Button)findViewById(R.id.btn_renum);
  				btn_renum.setBackgroundResource(R.drawable.btn_certify_renum);
  			}else if(msg.what == 5){
  				//2분 재우기 익셉션시 버튼 재활성화
  				Button btn_renum = (Button)findViewById(R.id.btn_renum);
  				btn_renum.setBackgroundResource(R.drawable.btn_certify_renum);
  			}
  		}
  	};
     
}