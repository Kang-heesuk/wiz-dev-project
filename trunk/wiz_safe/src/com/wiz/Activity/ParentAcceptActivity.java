package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ParentAcceptActivity extends Activity {
	
	String phonenum;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.parent_accept);

        //top-navigation �� ����
        TextView topTitle = (TextView)findViewById(R.id.textTitle);
        if(topTitle != null){
        	topTitle.setText(R.string.title_loca_accept);
        }
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ParentAcceptActivity.this.finish();
			}
		});
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        
        //body 
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");

        TextView bodyText = (TextView)findViewById(R.id.bodyText);
        if(bodyText != null){
        	bodyText.setText(phonenum+" �Կ��� ������ ��ġ������ ���� �˴ϴ�. ����Ͻðڽ��ϱ�?");
        }
        
         
        Button btn_01 = (Button)findViewById(R.id.btn_01);
        btn_01.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(ParentAcceptActivity.this, "������. ������ ����ϴ� ���� ����", Toast.LENGTH_SHORT).show();
				//���� ��� ���� ���� �� �� ��Ƽ��Ƽ�� ����
				ParentAcceptActivity.this.finish();
			}
		});
        
        
        
        Button btn_02 = (Button)findViewById(R.id.btn_02);
        btn_02.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ParentAcceptActivity.this.finish();
			}
		});
    }   
    
}