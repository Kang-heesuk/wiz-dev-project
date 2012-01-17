package com.nhn.android.mapviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LocationInfoView extends Activity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info); 
        
 		
        String test1 = this.getIntent().getStringExtra("reqId");
        String test2 = this.getIntent().getStringExtra("reqCtn");
        
		Log.i("banhong log", "===="+test1);
		Log.i("banhong log", "===="+test2);
       
		//���⿡�� ��� ��ȸ�ؼ� latitude, longitude ���� �����´�.
		//double �� ���� , double �� ����, �ش� ��ġ ǥ���� string
		//double longitude01 = 127.12201246666667;
		//double latitude01 = 37.495217644444445;
		//String placeInfo01 = "IT Becher Tower";
		
		
		
		//������ ��ȸ�� latitude, longitude ������ �ʿ� ǥ���غ���.
		
        Button btn_go_main = (Button) findViewById(R.id.btn_go_main);
        btn_go_main.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(LocationInfoView.this, MainActivity.class);
				startActivity(intent);
				
				Log.d("location", "btn_go_main pressed");
			}
		});
    }

	
}