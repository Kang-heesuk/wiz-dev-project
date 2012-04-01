package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.util.*;

public class ChildAddCompleteActivity extends Activity {
	
	String selectChildPhonenum;
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	String getRecentRequestTime;
	Long getCurentTime;
		
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_add_complete);
		
		//앞 페이지에서 필요한 정보를 추출한다.
		Intent intent = getIntent();
		selectChildPhonenum = intent.getStringExtra("phonenum");
		
		//첫번째 TextView 에 뿌려질 문구 설정
		String tempPhonenum = WizSafeUtil.setPhoneNum(selectChildPhonenum);
		String tempText = tempPhonenum + "님께 자녀등록 요청을 완료 하였습니다.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		
		//해당 레이아웃에 버튼 선언
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
		
		//버튼 이미지 정의
		if(passibleRetry()){
			btn_ok.setBackgroundResource(R.drawable.btn_certify_renum);
		}
		
		//버튼 액션 정의
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//재시도 가능할경우
					if(passibleRetry()){
						frameViewHandler.sendEmptyMessage(0);
						
						//API 통신 쓰레드 시작한다.
						callChildReqestAPI thread = new callChildReqestAPI();
						thread.start();
					}else{
						finish();
					}
				}
			}
		);
	}
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				
			}else{
				//현재 API 통신이 된 시간을 단말기 내부에 저장한다.
				Long currentTime = System.currentTimeMillis();
				edit.putString(selectChildPhonenum, Long.toString(currentTime));		//name = 요청번호 , value = 요청값 으로 저장한다.
				edit.commit();
				
				Toast.makeText(ChildAddCompleteActivity.this, "API통신 완료 - 현재시간 저장(24시간 후가능)", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//API 통신을 하여 재요청을 수행하는 부분
	class callChildReqestAPI extends Thread {
		public void run(){
			
			//API통신을 한다. 현재는 구현이 되지않았으므로 그냥 하드코딩이다
			String apiReturnVal = "0";
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}
	
	public boolean passibleRetry(){
		
		boolean returnVal = false;
		
		//단말기 안에서 필요한 정보를 추출한다. 최근 요청한 시간
        LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		edit = LocalSave.edit();
		getRecentRequestTime = LocalSave.getString(selectChildPhonenum, "0000000000000");
		//현재시간
		getCurentTime = System.currentTimeMillis();
		Long tempRecentTime = Long.parseLong(getRecentRequestTime);
		
		//24 * 60 * 60 * 1000 = 24시간을 밀리초로 환산한것
		//24 시간이 지났다면 재요청을 한다.
		if((getCurentTime - tempRecentTime) >= (24 * 60 * 60 * 1000)){
			returnVal = true;
		}else{
			returnVal = false;
		}
		
		return returnVal;
	}
	
}
