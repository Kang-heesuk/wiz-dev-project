package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wiz.util.WizSafeRecycleUtil;
import com.wiz.util.WizSafeUtil;

public class ChildAddCompleteActivity extends Activity {
	
	String selectChildPhonenum;
	
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
		
		//버튼 액션 정의
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//재시도 가능할경우
					finish();
				}
			}
		);
	}
	
	public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
}
