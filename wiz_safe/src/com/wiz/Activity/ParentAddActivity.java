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

public class ParentAddActivity extends Activity {

	EditText parentPhone;
	EditText parentName;
	int whereFlag = -1;
	
	String parentPhoneTemp;
	String parentNameTemp;
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_add);
        
        //�ܸ��� �ȿ��� �ʿ��� ������ �����Ѵ�.
        LocalSave = getSharedPreferences("isAuthCheck", 0);
		edit = LocalSave.edit();
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//���° �ڸ�����
        
        parentPhone = (EditText)findViewById(R.id.parentPhone);
        parentName = (EditText)findViewById(R.id.parentName);
        		
        parentName.setHint("���Է½� '�θ�" + whereFlag + "' �ڵ� ���� ��");
        
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
				parentPhoneTemp = parentPhone.getText().toString();
				parentNameTemp = parentName.getText().toString();
				
				if("".equals(parentNameTemp)){
					parentNameTemp = "�ڳ�" + whereFlag;
				}
				
				//�ƹ��͵� �Է¾��ϸ�.. ���߿� �̰��� üũ ������� �ٲ���ϰڴ�.
				if("".equals(parentPhoneTemp)){
					Toast.makeText(ParentAddActivity.this, "�հ����������", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//���� FrameLayout�� ����� ���� ó�������� ���δ�.
				frameViewHandler.sendEmptyMessage(0);
				
				//API ��� ������ �����Ѵ�.
				callParentAddAPI thread = new callParentAddAPI();
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
				imm.hideSoftInputFromWindow(parentPhone.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(parentName.getWindowToken(), 0);
			}else{
				//���� API ����� �� �ð��� �ܸ��� ���ο� �����Ѵ�.
				Long currentTime = System.currentTimeMillis();
				edit.putString(parentPhone.getText().toString(), Long.toString(currentTime));		//name = ��û��ȣ , value = ��û�� ���� �����Ѵ�.
				edit.commit();
				
				//����� ������ ��û�Ϸ� ��Ƽ��Ƽ (ParentAddCompleteActivity) �� ����
				Intent intent = new Intent(ParentAddActivity.this, ParentAddCompleteActivity.class);
				intent.putExtra("whereFlag", whereFlag);
				intent.putExtra("phonenum", parentPhone.getText().toString());
				startActivity(intent);
				finish();
			}
		}
	};
	
	//API ����� �ϴ� ������ �κ�
	class callParentAddAPI extends Thread {
		public void run(){
			
			//API����� �Ѵ�. ����� ������ �����ʾ����Ƿ� �׳� �ϵ��ڵ��̴�
			String apiReturnVal = "0";
			
			if("0".equals(apiReturnVal)){
				frameViewHandler.sendEmptyMessage(1);
			}
		}
	}

}
