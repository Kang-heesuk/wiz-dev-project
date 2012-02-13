package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChildSafezoneListActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);
        
		//top navi 정의
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildSafezoneListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
            
        
        //여기부터 body
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//몇번째 자리인지
        
        //안심존 등록 버튼 정의
        Button btn_addSafezone = (Button)findViewById(R.id.btn_addSafezone);
        btn_addSafezone.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
				startActivity(intent);
			}
		});

	}
}
