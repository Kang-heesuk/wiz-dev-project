package com.wiz.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.util.WizSafeUtil;

public class JoinAuthActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_auth);
        
         
        //���� ����̽��� ctn ���� - ����� ctn ���� ���� sms �߼��Ѵ�.
        TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String returnValue = mTelephonyMgr.getLine1Number();

        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText("�� "+WizSafeUtil.setPhoneNum(WizSafeUtil.androidParseCtn(returnValue, true))+" ��ȣ�� ������ȣ�� �߼� �Ǿ����ϴ�.");
        
        
        Button btn_join = (Button)findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(JoinAuthActivity.this, "�������� ����~", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(JoinAuthActivity.this, MainActivity.class);
				startActivity(intent);
			} 
		}); 
        
        Button btn_renum = (Button)findViewById(R.id.btn_renum);
        btn_renum.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(JoinAuthActivity.this, "���� ��ȣ�� ������ �Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
				
			} 
		}); 
    }
     
}