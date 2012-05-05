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
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
		Intent intent = getIntent();
		selectChildPhonenum = intent.getStringExtra("phonenum");
		
		//ù��° TextView �� �ѷ��� ���� ����
		String tempPhonenum = WizSafeUtil.setPhoneNum(selectChildPhonenum);
		String tempText = tempPhonenum + "�Բ� �ڳ��� ��û�� �Ϸ� �Ͽ����ϴ�.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		//�ش� ���̾ƿ��� ��ư ����
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
		
		//��ư �׼� ����
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//��õ� �����Ұ��
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
