package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
				Toast.makeText(MainActivity.this, "btn02 pressed!!!", Toast.LENGTH_SHORT).show();
				Log.d("location", "btn02 pressed");
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

    }

    
    private void checkNetworkStat(Handler handler){
		ConnectivityManager con =  (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		if (con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ) {
			//3G연결 확인
			//ConnectivityManager.TYPE_MOBILE는 0값을 받습니다.
			handler.sendEmptyMessageDelayed(0, 2000); 
		}else if(con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING){
		
		
		//와이파이 연결
		//ConnectivityManager.TYPE_WIFI 는 1값을 갇습니다.
		} else{
			//연결상태 확인 x
		}

    }
        

    
    
}