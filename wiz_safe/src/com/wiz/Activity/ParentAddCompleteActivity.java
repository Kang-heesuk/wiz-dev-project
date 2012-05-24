package com.wiz.Activity;

import com.wiz.Demon.WizSafeGetLocation;
import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.ComponentName;
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
		
		//자녀가 부모리스트에 부모를 등록을 한 순간 부터 서버로 위치 정보를 제공하도록 셋팅
		WizSafeUtil.setSendLocationUser(ParentAddCompleteActivity.this, true);	//로컬벨류셋팅
		
		//바로 위치값을 주기위하여 위치 찾기 데몬 한번 실행
		//인증된 사람인지 아닌지 판별하여 바로 위치 한번 전송한다.
        if(WizSafeUtil.isAuthOkUser(ParentAddCompleteActivity.this)){
        	//백그라운드에 DemonService라는 Sevice가 존재하는지 가져옴.
    		ComponentName cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
    		//서비스 시작(위에서 중지 시킨 데몬을 시작시킴)
    		startService(new Intent().setComponent(cn));
        }
	}
}
