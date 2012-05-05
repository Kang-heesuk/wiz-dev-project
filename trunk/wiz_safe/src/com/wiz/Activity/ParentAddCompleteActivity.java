package com.wiz.Activity;

import com.wiz.util.WizSafeRecycleUtil;
import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ParentAddCompleteActivity extends Activity {
	
	String parentPhonenum;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_add_complete);
        
        //앞 페이지에서 필요한 정보를 추출한다.
		Intent intent = getIntent();
		parentPhonenum = intent.getStringExtra("phonenum");
		
		//첫번째 TextView 에 뿌려질 문구 설정
		String tempPhonenum = WizSafeUtil.setPhoneNum(parentPhonenum);
		String tempText = tempPhonenum + "님을 부모로 등록 하였습니다.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		//해당 레이아웃에 버튼 선언
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
        
		//버튼 액션 정의
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
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
