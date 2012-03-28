package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class JoinAcceptActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_accept);
         
        
        //공지팝업 하단 체크  누른경우의 액션
	    final LinearLayout check1Layout = (LinearLayout)findViewById(R.id.check1Layout);
	    final ImageView checkImg1 = (ImageView)findViewById(R.id.btn_check1);
	    check1Layout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(checkImg1.getVisibility() == View.VISIBLE){
					checkImg1.setVisibility(View.GONE);
				}else{
					checkImg1.setVisibility(View.VISIBLE);
				}
			}
	    });
	    
	    //공지팝업 하단 체크  누른경우의 액션
	    final LinearLayout check2Layout = (LinearLayout)findViewById(R.id.check2Layout);
	    final ImageView checkImg2 = (ImageView)findViewById(R.id.btn_check2);
	    check2Layout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(checkImg2.getVisibility() == View.VISIBLE){
					checkImg2.setVisibility(View.GONE);
				}else{
					checkImg2.setVisibility(View.VISIBLE);
				}
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