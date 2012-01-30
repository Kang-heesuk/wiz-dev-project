package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String authProcessValue;
	String splashValue;
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	String getVal;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        //최초 인증체크값(isAuthCheck)을 0으로 설정
   	 	LocalSave = getSharedPreferences("isAuthCheck", 0);
   		edit = LocalSave.edit();

    

		
        if(true){
        
	        //title 로딩 화면을 띄운다. 최초 어플 실행시 1회만 수행하도록 한다.
	        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
	        startActivity(intent);
        }	
        
        if("0".equals(getVal)){
	        initialize();	//시간이 걸리는 작업들을 처리
        }

        
        
        
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
				Toast.makeText(MainActivity.this, "getValue = " + getVal, Toast.LENGTH_SHORT).show();

				//Toast.makeText(MainActivity.this, "btn03 pressed!!!", Toast.LENGTH_SHORT).show();
				Log.d("location", "btn03 pressed");
			}
		});

       
    }  
    
    private void initialize(){
    	
    	InitializationRunnable init = new InitializationRunnable();
    	new Thread(init).start();
    }
    
    class InitializationRunnable implements Runnable{
    	
    	public void run(){
    		//초기 메인 title 띄우고 처리할 내용들을 넣자.
    		//인증 관련 로직
    		Log.i("banhong","==================");
    		Log.i("banhong","인증관련 로직 탑니다. ");
    		Log.i("banhong","==================");
    		
    		
    		authProcessValue = "0";
    		
    		//인증 관련 로직 수행후 isAuthCheck에 결과값 셋팅
    		String setVal = authProcessValue;
			edit.putString("isAuthCheck", setVal);
			edit.commit();
    	}
    }
    
}