package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChildTraceAddActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//몇번째 자리인지
        
        Log.i("traceChild","====" + phonenum);
	}
}
