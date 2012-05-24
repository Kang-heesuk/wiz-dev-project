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
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
		Intent intent = getIntent();
		parentPhonenum = intent.getStringExtra("phonenum");
		
		//ù��° TextView �� �ѷ��� ���� ����
		String tempPhonenum = WizSafeUtil.setPhoneNum(parentPhonenum);
		String tempText = tempPhonenum + "���� �θ�� ��� �Ͽ����ϴ�.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		//�ش� ���̾ƿ��� ��ư ����
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
        
		//��ư �׼� ����
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					finish();
				}
			}
		);
		
		//�ڳడ �θ𸮽�Ʈ�� �θ� ����� �� ���� ���� ������ ��ġ ������ �����ϵ��� ����
		WizSafeUtil.setSendLocationUser(ParentAddCompleteActivity.this, true);	//���ú�������
		
		//�ٷ� ��ġ���� �ֱ����Ͽ� ��ġ ã�� ���� �ѹ� ����
		//������ ������� �ƴ��� �Ǻ��Ͽ� �ٷ� ��ġ �ѹ� �����Ѵ�.
        if(WizSafeUtil.isAuthOkUser(ParentAddCompleteActivity.this)){
        	//��׶��忡 DemonService��� Sevice�� �����ϴ��� ������.
    		ComponentName cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
    		//���� ����(������ ���� ��Ų ������ ���۽�Ŵ)
    		startService(new Intent().setComponent(cn));
        }
	}
}
