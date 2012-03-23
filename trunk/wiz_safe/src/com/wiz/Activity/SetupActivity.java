package com.wiz.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SetupActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_list);
        
   
        //body 구성
        Button btn01 = (Button)findViewById(R.id.btn1);
        btn01.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, NoticeListActivity.class);
				startActivity(intent);
			}
		});

        Button btn02 = (Button)findViewById(R.id.btn2);
        btn02.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, UseInfoListActivity.class);
				startActivity(intent);
			}
		});
         
        Button btn03 = (Button)findViewById(R.id.btn3);
        btn03.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this, FaqListActivity.class);
				startActivity(intent);
			}
		});
        
        Button btn04 = (Button)findViewById(R.id.btn4);
        btn04.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(SetupActivity.this, "44444444444444 = ", Toast.LENGTH_SHORT).show();
			}
		});
        
        Button btn05 = (Button)findViewById(R.id.btn5);
        btn05.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(SetupActivity.this, "55555555555 = ", Toast.LENGTH_SHORT).show();
			}
		});
        
        Button btn06 = (Button)findViewById(R.id.btn6);
        btn06.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(SetupActivity.this, "6666666666 = ", Toast.LENGTH_SHORT).show();
			}
		});
         
        
        Button btn_sms = (Button)findViewById(R.id.btn_sms);
        btn_sms.setTag(R.drawable.btn_off);
        btn_sms.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int compareValue = (Integer)findViewById(R.id.btn_sms).getTag();
				if(compareValue == R.drawable.btn_off){
					findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_on);
					findViewById(R.id.btn_sms).setTag(R.drawable.btn_on);
				}else{
					findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_off);
					findViewById(R.id.btn_sms).setTag(R.drawable.btn_off);
				}
			}
		});
        
        Button btn_hide = (Button)findViewById(R.id.btn_hide);
        btn_hide.setTag(R.drawable.btn_off);
        btn_hide.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int compareValue = (Integer)findViewById(R.id.btn_hide).getTag();
				if(compareValue == R.drawable.btn_off){
					AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
					String title = "위치숨김";	
					String message = "위치숨김 기능을 활성화 하면 부모님이 안전하게 지켜줄 수 없습니다.";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setPositiveButton("켜기", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_on);
							findViewById(R.id.btn_hide).setTag(R.drawable.btn_on); 
						}
					});
					ad.setNegativeButton(R.string.btn_cancel, null);
					ad.show();
				}else{
					findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_off);
					findViewById(R.id.btn_hide).setTag(R.drawable.btn_off);
				}
			}
		});
        
    }
}