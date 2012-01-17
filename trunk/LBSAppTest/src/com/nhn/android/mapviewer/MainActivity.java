package com.nhn.android.mapviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn_01 = (Button) findViewById(R.id.btn_01); 
        btn_01.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "준비중이야!!!", Toast.LENGTH_LONG).show();
				Log.d("location", "btn_01 pressed");
			}
		});
        
        Button btn_02 = (Button) findViewById(R.id.btn_02);
        btn_02.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, LocationInfoView.class);
				intent.putExtra("reqId", "banhong");
				intent.putExtra("reqCtn", "01084464664");
				startActivity(intent);
				
				Log.d("location", "btn_02 pressed");
			}
		});
        
        Button btn_03 = (Button) findViewById(R.id.btn_03);
        btn_03.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "준비중이야!!!", Toast.LENGTH_LONG).show();
				Log.d("location", "btn_03 pressed");
			}
		});
        
        Button btn_04 = (Button) findViewById(R.id.btn_04);
        btn_04.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "준비중이야!!!", Toast.LENGTH_LONG).show();
				Log.d("location", "btn_04 pressed");
			}
		});
    }
}