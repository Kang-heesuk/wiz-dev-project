package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.util.*;

public class ChildAddCompleteActivity extends Activity {
	
	String selectChildPhonenum;
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	String getRecentRequestTime;
	Long getCurentTime;
	
	TextView waitFrame;
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_add_complete);
        
		ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildAddCompleteActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_req_finish);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
		Intent intent = getIntent();
		selectChildPhonenum = intent.getStringExtra("phonenum");
		
		//ù��° TextView �� �ѷ��� ���� ����
		String tempPhonenum = WizSafeUtil.setPhoneNum(selectChildPhonenum);
		String tempText = tempPhonenum + "�Բ� �ڳ��� ��û�� �Ϸ� �Ͽ����ϴ�.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		//API ��� �߿� ������ Frame ��ȿ� TextView
		waitFrame = (TextView)findViewById(R.id.processingFrame);
		
		//��ư �׼� ����
        findViewById(R.id.btn_ok).setOnClickListener(mClickListener);
        findViewById(R.id.btn_retry).setOnClickListener(mClickListener);
	}
	
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_ok:
				finish();
				break;
			case R.id.btn_retry:
				//�ܸ��� �ȿ��� �ʿ��� ������ �����Ѵ�. �ֱ� ��û�� �ð�
		        LocalSave = getSharedPreferences("isAuthCheck", 0);
				edit = LocalSave.edit();
				getRecentRequestTime = LocalSave.getString(selectChildPhonenum, "0000000000000");
				//����ð�
				getCurentTime = System.currentTimeMillis();
				Long tempRecentTime = Long.parseLong(getRecentRequestTime);
				
				//24 * 60 * 60 * 1000 = 24�ð��� �и��ʷ� ȯ���Ѱ�
				//24 �ð��� �����ٸ� ���û�� �Ѵ�.
				if((getCurentTime - tempRecentTime) >= (24 * 60 * 60 * 1000)){
					//���� FrameLayout�� ����� ���� ó�������� ���δ�.
					frameViewHandler.sendEmptyMessage(0);
					
					//API ��� ������ �����Ѵ�.
					callChildReqestAPI thread = new callChildReqestAPI();
					thread.start();
				}else{
					Toast.makeText(ChildAddCompleteActivity.this, "���� 24�ð��� ������ �ʾҽ��ϴ�.", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		
	};
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				waitFrame.setVisibility(View.VISIBLE);
			}else{
				//���� API ����� �� �ð��� �ܸ��� ���ο� �����Ѵ�.
				Long currentTime = System.currentTimeMillis();
				edit.putString(selectChildPhonenum, Long.toString(currentTime));		//name = ��û��ȣ , value = ��û�� ���� �����Ѵ�.
				edit.commit();
				
				waitFrame.setVisibility(View.INVISIBLE);
				Toast.makeText(ChildAddCompleteActivity.this, "API��� �Ϸ� �� ����ð� ����(24�ð� �İ���)", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//API ����� �Ͽ� ���û�� �����ϴ� �κ�
	class callChildReqestAPI extends Thread {
		public void run(){
			
			//API����� �Ѵ�. ����� ������ �����ʾ����Ƿ� �׳� �ϵ��ڵ��̴�
			String apiReturnVal = "0";
			
			//���� �Ǵ�ô�ϱ� ���Ͽ� �ð� ������ �ִ´�.
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}
	
}
