package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChildAddActivity extends Activity {
	
	EditText childPhone;
	EditText childName;
	TextView waitFrame;
	int whereFlag = -1;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_add);
        
        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ChildAddActivity.this.finish();
			} 
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_reg_child);
        
        Button btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//���° �ڸ�����
        
        childPhone = (EditText)findViewById(R.id.childPhone);
        childName = (EditText)findViewById(R.id.childName);
        waitFrame = (TextView)findViewById(R.id.processingFrame);
        		
        //��ư �׼� ����
        findViewById(R.id.goSubmit).setOnClickListener(mClickListener);
        
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goSubmit:
				String childPhoneTemp = childPhone.getText().toString();
				String chileNameTemp = childName.getText().toString();
				
				if("".equals(childPhoneTemp) || "".equals(chileNameTemp)){
					//�ƹ��͵� �Է¾��ϸ�.. ���߿� �̰��� üũ ������� �ٲ���ϰڴ�.
					Toast.makeText(ChildAddActivity.this, "�հ����������", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//���� FrameLayout�� ����� ���� ó�������� ���δ�.
				frameViewHandler.sendEmptyMessage(0);
				
				//API ��� ������ �����Ѵ�.
				callChildAddAPI thread = new callChildAddAPI();
				thread.start();
				
				break;
			}
		}
		
	};
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(childPhone.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(childName.getWindowToken(), 0);
				waitFrame.setVisibility(View.VISIBLE);
			}else{
				waitFrame.setVisibility(View.INVISIBLE);
				Toast.makeText(ChildAddActivity.this, "���� API ����� �������ϴ�. ���ΰ����?", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	//API ����� �ϴ� ������ �κ�
	class callChildAddAPI extends Thread {
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
