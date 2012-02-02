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
	
	TextView waitFrame;
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_add_complete);
        
		ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildAddCompleteActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_req_finish);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
		
		//앞 페이지에서 필요한 정보를 추출한다.
		Intent intent = getIntent();
		selectChildPhonenum = intent.getStringExtra("phonenum");
		
		//첫번째 TextView 에 뿌려질 문구 설정
		String tempPhonenum = WizSafeUtil.setPhoneNum(selectChildPhonenum);
		String tempText = tempPhonenum + "님께 자녀등록 요청을 완료 하였습니다.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		//API 통신 중에 보여질 Frame 뷰안에 TextView
		waitFrame = (TextView)findViewById(R.id.processingFrame);
		
		//버튼 액션 정의
        findViewById(R.id.btn_ok).setOnClickListener(mClickListener);
        findViewById(R.id.btn_retry).setOnClickListener(mClickListener);
	}
	
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_ok:
				finish();
				break;
			case R.id.btn_retry:
				//단말기 안에서 필요한 정보를 추출한다. 최근 요청한 시간
		        LocalSave = getSharedPreferences("isAuthCheck", 0);
				edit = LocalSave.edit();
				getRecentRequestTime = LocalSave.getString(selectChildPhonenum, "0000000000000");
				//현재시간
				getCurentTime = System.currentTimeMillis();
				Long tempRecentTime = Long.parseLong(getRecentRequestTime);
				
				//24 * 60 * 60 * 1000 = 24시간을 밀리초로 환산한것
				//24 시간이 지났다면 재요청을 한다.
				if((getCurentTime - tempRecentTime) >= (24 * 60 * 60 * 1000)){
					//먼저 FrameLayout을 띄워서 현재 처리중임을 보인다.
					frameViewHandler.sendEmptyMessage(0);
					
					//API 통신 쓰레드 시작한다.
					callChildReqestAPI thread = new callChildReqestAPI();
					thread.start();
				}else{
					Toast.makeText(ChildAddCompleteActivity.this, "아직 24시간이 지나지 않았습니다.", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		
	};
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				waitFrame.setVisibility(View.VISIBLE);
			}else{
				//현재 API 통신이 된 시간을 단말기 내부에 저장한다.
				Long currentTime = System.currentTimeMillis();
				edit.putString(selectChildPhonenum, Long.toString(currentTime));		//name = 요청번호 , value = 요청값 으로 저장한다.
				edit.commit();
				
				waitFrame.setVisibility(View.INVISIBLE);
				Toast.makeText(ChildAddCompleteActivity.this, "API통신 완료 후 현재시간 저장(24시간 후가능)", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//API 통신을 하여 재요청을 수행하는 부분
	class callChildReqestAPI extends Thread {
		public void run(){
			
			//API통신을 한다. 현재는 구현이 되지않았으므로 그냥 하드코딩이다
			String apiReturnVal = "0";
			
			//뭔가 되는척하기 위하여 시간 지연을 넣는다.
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}
	
}
