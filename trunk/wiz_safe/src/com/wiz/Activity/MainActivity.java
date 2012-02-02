package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
				Toast.makeText(MainActivity.this, "get123123 = ", Toast.LENGTH_SHORT).show();

				//Toast.makeText(MainActivity.this, "btn03 pressed!!!", Toast.LENGTH_SHORT).show();
				Log.d("location", "btn03 pressed");
			}
		});
        
        Button btn_04 = (Button) findViewById(R.id.btn4);
        btn_04.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, ChildLocationViewActivity.class);
				startActivity(intent);
				
				Log.d("location", "btn_04 pressed");
			}
		});

    }

       
    
}