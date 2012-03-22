package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinAcceptActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_accept);
         
        
		//공지팝업 하단 체크  누른경우의 액션
		  final Button btn_check1 = (Button)findViewById(R.id.btn_check1);
		  final Button btn_blank1 = (Button)findViewById(R.id.btn_blank1);
		  Drawable alpha = btn_blank1.getBackground();
		  alpha.setAlpha(0);
		  btn_check1.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_check1.setVisibility(View.GONE); 
					btn_blank1.setVisibility(View.VISIBLE);
				}
		  });
		  //공지팝업 하단 빈칸 누른경우의 액션
		  btn_blank1.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_blank1.setVisibility(View.GONE);
					btn_check1.setVisibility(View.VISIBLE);
				}
		  });
        
		//공지팝업 하단 체크  누른경우의 액션2
		  final Button btn_check2 = (Button)findViewById(R.id.btn_check2);
		  final Button btn_blank2 = (Button)findViewById(R.id.btn_blank2);
		  Drawable alpha2 = btn_blank2.getBackground();
		  alpha2.setAlpha(0);
		  btn_check2.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_check2.setVisibility(View.GONE); 
					btn_blank2.setVisibility(View.VISIBLE);
				}
		  });
		  //공지팝업 하단 빈칸 누른경우의 액션
		  btn_blank2.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_blank2.setVisibility(View.GONE);
					btn_check2.setVisibility(View.VISIBLE);
				}
		  });
		  
        Button btn_accept = (Button)findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(JoinAcceptActivity.this, JoinAuthActivity.class);
				startActivity(intent);
			}
		}); 
        
    }
     
}