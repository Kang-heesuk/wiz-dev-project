package com.wiz.Activity;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wiz.Seed.SeedCipher;
import com.wiz.View.NoticePopView;
import com.wiz.util.WizSafeUtil;

public class MainActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        //seed 암호화 테스트 로직 시작
        
        String text = "부산광역시 금정구 장전동 산 30번지 부산대학교 의과대학 대학원 예방의학전공 123동 123호";
		
		String key = "wizcommunication";
		StringBuilder trace = new StringBuilder();
		
		trace.append("Plain Text :: [").append(text).append("]");
		System.out.println(trace.toString());
		
		SeedCipher seed = new SeedCipher();
		byte[] byte_enc = null;
		try {
			byte_enc = seed.encrypt(text, key.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String encryptText = WizSafeUtil.encode(byte_enc);
		

		trace = new StringBuilder();
		trace.append("Encrypt Text (Base64 Encoding) :: [").append(encryptText).append("]");
		System.out.println(trace.toString());
		
		byte[] encryptbytes = WizSafeUtil.decode(encryptText);
		String decryptText = null;
		
		try {
			decryptText = seed.decryptAsString(encryptbytes, key.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		trace = new StringBuilder();
		trace.append("Decrypt Text :: [").append(decryptText).append("]");
		System.out.println(trace.toString());
        
        
        //seed 암호화 테스트 로직 끝
        
        
        
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
				Intent intent = new Intent(MainActivity.this, JoinAcceptActivity.class);
				startActivity(intent);
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