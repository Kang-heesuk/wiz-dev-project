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
		
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.child_add_complete);
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
		Intent intent = getIntent();
		selectChildPhonenum = intent.getStringExtra("phonenum");
		
		//ù��° TextView �� �ѷ��� ���� ����
		String tempPhonenum = WizSafeUtil.setPhoneNum(selectChildPhonenum);
		String tempText = tempPhonenum + "�Բ� �ڳ��� ��û�� �Ϸ� �Ͽ����ϴ�.";
		TextView info1 = (TextView)findViewById(R.id.info1);
		info1.setText(tempText);
		
		
		//�ش� ���̾ƿ��� ��ư ����
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
		
		//��ư �̹��� ����
		if(passibleRetry()){
			btn_ok.setBackgroundResource(R.drawable.btn_certify_renum);
		}
		
		//��ư �׼� ����
		btn_ok.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//��õ� �����Ұ��
					if(passibleRetry()){
						frameViewHandler.sendEmptyMessage(0);
						
						//API ��� ������ �����Ѵ�.
						callChildReqestAPI thread = new callChildReqestAPI();
						thread.start();
					}else{
						finish();
					}
				}
			}
		);
	}
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				
			}else{
				//���� API ����� �� �ð��� �ܸ��� ���ο� �����Ѵ�.
				Long currentTime = System.currentTimeMillis();
				edit.putString(selectChildPhonenum, Long.toString(currentTime));		//name = ��û��ȣ , value = ��û�� ���� �����Ѵ�.
				edit.commit();
				
				Toast.makeText(ChildAddCompleteActivity.this, "API��� �Ϸ� - ����ð� ����(24�ð� �İ���)", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//API ����� �Ͽ� ���û�� �����ϴ� �κ�
	class callChildReqestAPI extends Thread {
		public void run(){
			
			//API����� �Ѵ�. ����� ������ �����ʾ����Ƿ� �׳� �ϵ��ڵ��̴�
			String apiReturnVal = "0";
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}
	
	public boolean passibleRetry(){
		
		boolean returnVal = false;
		
		//�ܸ��� �ȿ��� �ʿ��� ������ �����Ѵ�. �ֱ� ��û�� �ð�
        LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		edit = LocalSave.edit();
		getRecentRequestTime = LocalSave.getString(selectChildPhonenum, "0000000000000");
		//����ð�
		getCurentTime = System.currentTimeMillis();
		Long tempRecentTime = Long.parseLong(getRecentRequestTime);
		
		//24 * 60 * 60 * 1000 = 24�ð��� �и��ʷ� ȯ���Ѱ�
		//24 �ð��� �����ٸ� ���û�� �Ѵ�.
		if((getCurentTime - tempRecentTime) >= (24 * 60 * 60 * 1000)){
			returnVal = true;
		}else{
			returnVal = false;
		}
		
		return returnVal;
	}
	
}
