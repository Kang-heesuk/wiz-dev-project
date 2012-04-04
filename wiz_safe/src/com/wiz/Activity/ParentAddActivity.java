package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ParentAddActivity extends Activity {

	EditText parentPhone;
	EditText parentName;
	
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	String parentPhoneTemp;
	String parentNameTemp;
		
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int addApiResult = -1;
	
	//üũ�ڽ� Ŭ�� �Ͽ����� �ƴ����� ���� �÷��� ����
	boolean haveCheck = true;
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_add);
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        whereFlag = intent.getIntExtra("whereFlag", 1);		//���° �ڸ�����
        alreadyRegCtn = intent.getStringExtra("alreadyRegCtn");		//�տ� ����� ��ȣ�� �ִ���
        
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
				if(parentPhoneTemp == null){
					parentPhoneTemp = "";
				}
				if("".equals(parentNameTemp)){
					parentNameTemp = "�θ�" + whereFlag;
				}
				
				if("".equals(parentPhoneTemp)){
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
					String title = "�Է� ����";	
					String message = "����� �θ��� �޴��� ��ȣ�� �Է��Ͽ� �ֽʽÿ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				if(parentPhoneTemp.equals(WizSafeUtil.getCtn(ParentAddActivity.this))){
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
					String title = "�Է� ����";	
					String message = "������ �θ�� ����� �� �����ϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				if(alreadyRegCtn.indexOf(parentPhoneTemp) > -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
					String title = "�Է� ����";	
					String message = "�̹� ��ϵ� �θ��Դϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				
				//API ��� ������ �����Ѵ�.
		        WizSafeDialog.showLoading(ParentAddActivity.this);	//Dialog ���̱�
				CallParentAddApiThread thread = new CallParentAddApiThread();
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
					haveCheck = false;
				}else{
					checkBoxView.setBackgroundResource(R.drawable.check);
					haveCheck = true;
				}
			}
		}
	};
	
	//API ȣ�� ������
  	class CallParentAddApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ParentAddActivity.this));
  				String enc_parentCtn = WizSafeSeed.seedEnc(parentPhoneTemp);
  				String enc_parentName = WizSafeSeed.seedEnc(parentNameTemp);
  				String url = "https://www.heream.com/api/addRelation.jsp?parentCtn="+ URLEncoder.encode(enc_parentCtn) +"&childCtn="+ URLEncoder.encode(enc_ctn) +"&parentName="+ URLEncoder.encode(enc_parentName) +"&type=" + URLEncoder.encode("02");
  				
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode);
  				
  				if(haveCheck){
  					url = "https://www.heream.com/api/sendAppDownSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&d_ctn=" + URLEncoder.encode(enc_parentCtn) + "&type=" + URLEncoder.encode("�θ�");
  					urlConn = (HttpURLConnection) new URL(url).openConnection();
  	  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));
  				}

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(addApiResult == 0){
					//�ڳ����� ���������� �̷���� ��� (ChildAddCompleteActivity) �� ����	
  					Intent intent = new Intent(ParentAddActivity.this, ParentAddCompleteActivity.class);
  					intent.putExtra("whereFlag", whereFlag);
  					intent.putExtra("phonenum", parentPhone.getText().toString());
  					startActivity(intent);
  					finish();

				}else if(addApiResult == 1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
					String title = "��� ����";	
					String message = "�̹� ��ϵ� �θ� �Դϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}else if(addApiResult == -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
					String title = "��� ����";	
					String message = "�θ��� �� ������ �߻��Ͽ����ϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}
  				
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ParentAddActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}
  		}
  	};
	

}
