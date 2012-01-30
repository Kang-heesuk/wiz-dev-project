package com.wiz.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        
        initialize();
        
    }
    
    private void initialize(){
        Handler handler = new Handler(){
        	public void handleMessage(Message msg){
        		super.handleMessage(msg); 
        		finish(); 
        	}
        };
        
        //���μ��� ó���� �Ʒ� �޼ҵ带 �����ϸ� �ش� �ð� �Ŀ� ���ξ�Ƽ��Ƽ�� �̵�
        handler.sendEmptyMessageDelayed(0, 1000);
    }
}