package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChildTraceAddActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//���° �ڸ�����
        
        Log.i("traceChild","====" + phonenum);
	}
}
