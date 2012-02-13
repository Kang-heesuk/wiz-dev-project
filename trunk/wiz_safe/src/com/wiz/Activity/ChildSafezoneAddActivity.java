package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChildSafezoneAddActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildSafezoneAddActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
            
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//몇번째 자리인지
        
        Log.i("traceChild","====" + phonenum);
	}
}
