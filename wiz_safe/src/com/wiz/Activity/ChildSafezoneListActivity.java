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
        
		//top navi ����
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
            
        
        //������� body
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//���° �ڸ�����
        
        //�Ƚ��� ��� ��ư ����
        Button btn_addSafezone = (Button)findViewById(R.id.btn_addSafezone);
        btn_addSafezone.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
				startActivity(intent);
			}
		});

	}
}