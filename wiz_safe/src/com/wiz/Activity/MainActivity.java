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
     
        //���� ����üũ��(isAuthCheck)�� 0���� ����
   	 	LocalSave = getSharedPreferences("isAuthCheck", 0);
   		edit = LocalSave.edit();

    

		
        if(true){
        
	        //title �ε� ȭ���� ����. ���� ���� ����� 1ȸ�� �����ϵ��� �Ѵ�.
	        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
	        startActivity(intent);
        }	
        
        if("0".equals(getVal)){
	        initialize();	//�ð��� �ɸ��� �۾����� ó��
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
    		//�ʱ� ���� title ���� ó���� ������� ����.
    		//���� ���� ����
    		Log.i("banhong","==================");
    		Log.i("banhong","�������� ���� ž�ϴ�. ");
    		Log.i("banhong","==================");
    		
    		
    		authProcessValue = "0";
    		
    		//���� ���� ���� ������ isAuthCheck�� ����� ����
    		String setVal = authProcessValue;
			edit.putString("isAuthCheck", setVal);
			edit.commit();
    	}
    }
    
}