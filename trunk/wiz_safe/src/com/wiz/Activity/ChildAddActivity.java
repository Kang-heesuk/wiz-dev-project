package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChildAddActivity extends Activity {
	
	EditText childPhone;
	EditText childName;
	TextView waitFrame;
	int whereFlag = -1;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_add);
        
        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ChildAddActivity.this.finish();
			} 
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_reg_child);
        
        Button btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//몇번째 자리인지
        
        childPhone = (EditText)findViewById(R.id.childPhone);
        childName = (EditText)findViewById(R.id.childName);
        waitFrame = (TextView)findViewById(R.id.processingFrame);
        		
        //버튼 액션 정의
        findViewById(R.id.goSubmit).setOnClickListener(mClickListener);
        
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goSubmit:
				String childPhoneTemp = childPhone.getText().toString();
				String chileNameTemp = childName.getText().toString();
				
				if("".equals(childPhoneTemp) || "".equals(chileNameTemp)){
					//아무것도 입력안하면.. 나중엔 이것을 체크 양식으로 바꿔야하겠다.
					Toast.makeText(ChildAddActivity.this, "먼가빼먹은듯요", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//먼저 FrameLayout을 띄워서 현재 처리중임을 보인다.
				frameViewHandler.sendEmptyMessage(0);
				
				//API 통신 쓰레드 시작한다.
				callChildAddAPI thread = new callChildAddAPI();
				thread.start();
				
				break;
			}
		}
		
	};
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(childPhone.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(childName.getWindowToken(), 0);
				waitFrame.setVisibility(View.VISIBLE);
			}else{
				waitFrame.setVisibility(View.INVISIBLE);
				Toast.makeText(ChildAddActivity.this, "형님 API 통신이 끝났습니다. 어디로갈까요?", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	//API 통신을 하는 쓰레드 부분
	class callChildAddAPI extends Thread {
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
