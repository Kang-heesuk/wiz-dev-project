package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wiz.View.NoticePopView;

public class MainActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        Button btn01 = (Button)findViewById(R.id.btn1);
        btn01.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ChildListActivity.class);
				startActivity(intent);
			} 
		}); 
  
        Button btn02 = (Button)findViewById(R.id.btn2);
        btn02.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ParentListActivity.class);
				startActivity(intent);
			}
		});
         
        Button btn03 = (Button)findViewById(R.id.btn3);
        btn03.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetupActivity.class);
				startActivity(intent);
			}
		});
        
        Button btn04 = (Button)findViewById(R.id.btn_charge_pt);
        btn04.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//Intent intent = new Intent(MainActivity.this, SetupActivity.class);
				//startActivity(intent);
				Toast.makeText(MainActivity.this, "open other site page!!", Toast.LENGTH_SHORT).show();

			}
		});
         
        //공지사항 팝업을 핸들러로 띄운다.
        handler.sendEmptyMessageDelayed(0, 500);
        
    }
    
    Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
	        NoticePopView noticePopView = new NoticePopView((LinearLayout)findViewById(R.id.mainlayout));
			noticePopView.show();
			super.handleMessage(msg);
		}
    };

    
}