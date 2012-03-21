package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JoinAuthActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_auth);
        
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