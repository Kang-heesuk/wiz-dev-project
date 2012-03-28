package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ParentAddActivity extends Activity {

	EditText parentPhone;
	EditText parentName;
	int whereFlag = -1;
	
	String parentPhoneTemp;
	String parentNameTemp;
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_add);
        
        //단말기 안에서 필요한 정보를 추출한다.
        LocalSave = getSharedPreferences("isAuthCheck", 0);
		edit = LocalSave.edit();
		
		//앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//몇번째 자리인지
        
        parentPhone = (EditText)findViewById(R.id.parentPhone);
        parentName = (EditText)findViewById(R.id.parentName);
        		
        parentName.setHint("미입력시 '부모" + whereFlag + "' 자동 설정 됨");
        
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
				parentPhoneTemp = parentPhone.getText().toString();
				parentNameTemp = parentName.getText().toString();
				
				if("".equals(parentNameTemp)){
					parentNameTemp = "자녀" + whereFlag;
				}
				
				//아무것도 입력안하면.. 나중엔 이것을 체크 양식으로 바꿔야하겠다.
				if("".equals(parentPhoneTemp)){
					Toast.makeText(ParentAddActivity.this, "먼가빼먹은듯요", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//먼저 FrameLayout을 띄워서 현재 처리중임을 보인다.
				frameViewHandler.sendEmptyMessage(0);
				
				//API 통신 쓰레드 시작한다.
				callParentAddAPI thread = new callParentAddAPI();
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
				}else{
					checkBoxView.setBackgroundResource(R.drawable.check);
				}
			}
		}
	};
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(parentPhone.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(parentName.getWindowToken(), 0);
			}else{
				//현재 API 통신이 된 시간을 단말기 내부에 저장한다.
				Long currentTime = System.currentTimeMillis();
				edit.putString(parentPhone.getText().toString(), Long.toString(currentTime));		//name = 요청번호 , value = 요청값 으로 저장한다.
				edit.commit();
				
				//통신을 끝내면 요청완료 액티비티 (ParentAddCompleteActivity) 로 간다
				Intent intent = new Intent(ParentAddActivity.this, ParentAddCompleteActivity.class);
				intent.putExtra("whereFlag", whereFlag);
				intent.putExtra("phonenum", parentPhone.getText().toString());
				startActivity(intent);
				finish();
			}
		}
	};
	
	//API 통신을 하는 쓰레드 부분
	class callParentAddAPI extends Thread {
		public void run(){
			
			//API통신을 한다. 현재는 구현이 되지않았으므로 그냥 하드코딩이다
			String apiReturnVal = "0";
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}

}
