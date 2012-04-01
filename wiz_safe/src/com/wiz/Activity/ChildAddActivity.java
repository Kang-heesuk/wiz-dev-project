package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChildAddActivity extends Activity {
	
	EditText childPhone;
	EditText childName;
	int whereFlag = -1;
	
	String childPhoneTemp;
	String childNameTemp;
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_add);
        
        //�ܸ��� �ȿ��� �ʿ��� ������ �����Ѵ�.
        LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		edit = LocalSave.edit();
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//���° �ڸ�����
        
        childPhone = (EditText)findViewById(R.id.childPhone);
        childName = (EditText)findViewById(R.id.childName);
        		
        childName.setHint("���Է½� '�ڳ�" + whereFlag + "' �ڵ� ���� ��");
        
        //��ư �׼� �� üũ�ڽ� Ŭ�� �׼� ����
        findViewById(R.id.goSubmit).setOnClickListener(mClickListener);
        findViewById(R.id.checkBox1).setOnClickListener(mClickListener_checkBodx);
        findViewById(R.id.checkBox2).setOnClickListener(mClickListener_checkBodx);
        findViewById(R.id.checkBox3).setOnClickListener(mClickListener_checkBodx);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goSubmit:
				childPhoneTemp = childPhone.getText().toString();
				childNameTemp = childName.getText().toString();
				
				if("".equals(childNameTemp)){
					childNameTemp = "�ڳ�" + whereFlag;
				}
				
				//�ƹ��͵� �Է¾��ϸ�.. ���߿� �̰��� üũ ������� �ٲ���ϰڴ�.
				if("".equals(childPhoneTemp)){
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
	
	TextView.OnClickListener mClickListener_checkBodx = new Button.OnClickListener(){
		public void onClick(View v) {
			if(v.getId() == R.id.checkBox1 || v.getId() == R.id.checkBox2 || v.getId() == R.id.checkBox3){
				
				TextView checkBoxView = (TextView)findViewById(R.id.checkBox2);
				if(checkBoxView.getBackground().toString().indexOf("BitmapDrawable") > -1){
					checkBoxView.setBackgroundColor(Color.TRANSPARENT);
				}else{
					checkBoxView.setBackgroundResource(R.drawable.check);
				}
			}
		}
	};	
	
	
	Handler frameViewHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(childPhone.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(childName.getWindowToken(), 0);
			}else{
				//���� API ����� �� �ð��� �ܸ��� ���ο� �����Ѵ�.
				Long currentTime = System.currentTimeMillis();
				edit.putString(childPhone.getText().toString(), Long.toString(currentTime));		//name = ��û��ȣ , value = ��û�� ���� �����Ѵ�.
				edit.commit();
				
				//����� ������ ��û�Ϸ� ��Ƽ��Ƽ (ChildAddCompleteActivity) �� ����
				Intent intent = new Intent(ChildAddActivity.this, ChildAddCompleteActivity.class);
				intent.putExtra("whereFlag", whereFlag);
				intent.putExtra("phonenum", childPhone.getText().toString());
				startActivity(intent);
				finish();
			}
		}
	};
	
	
	//API ����� �ϴ� ������ �κ�
	class callChildAddAPI extends Thread {
		public void run(){
			
			//API����� �Ѵ�. ����� ������ �����ʾ����Ƿ� �׳� �ϵ��ڵ��̴�
			String apiReturnVal = "0";
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}
	
}
