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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class AllowLocation extends Activity {
	
	String allowPhoneNumber = "";
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;

	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int allowApiResult = -1;
	int rejectApiResult = -1;
	
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allow_location);
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        allowPhoneNumber = intent.getStringExtra("allowPhoneNumber");
        
        //ȭ������ ���� ����
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        Button btn_agree = (Button)findViewById(R.id.btn_agree);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        
        //ȭ�� ���� ����
        textView1.setText(WizSafeUtil.setPhoneNum(allowPhoneNumber) + "�Կ��� ������ ��ġ ������ ���� �˴ϴ�.");
        
        //���� ��ư �׼�
        btn_agree.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					//API ��� ������ �����Ѵ�.
			        WizSafeDialog.showLoading(AllowLocation.this);	//Dialog ���̱�
			        CallAllowLocationAllowApiThread thread = new CallAllowLocationAllowApiThread();
					thread.start();
				}
			}
		);
        
        //���� ��ư �׼�
        btn_cancel.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					AlertDialog.Builder submitAlert = new AlertDialog.Builder(AllowLocation.this);
					submitAlert.setTitle("��ġ��뵿��");
					submitAlert.setMessage(WizSafeUtil.setPhoneNum(allowPhoneNumber)+"���� �θ� ��û�� ���� �Ͻðڽ��ϱ�? ");
					submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//API ��� ������ �����Ѵ�.
					        WizSafeDialog.showLoading(AllowLocation.this);	//Dialog ���̱�
					        CallAllowLocationRejectApiThread thread = new CallAllowLocationRejectApiThread();
							thread.start();
						}
					});
					submitAlert.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					submitAlert.show();
					
				}
			}
		);
	 }
	 
	//API ȣ�� ������
  	class CallAllowLocationAllowApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(AllowLocation.this));
  				String enc_parentCtn = WizSafeSeed.seedEnc(allowPhoneNumber);
  				String type = "ACCEPT";
  				String url = "https://www.heream.com/api/allowLocation.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&parentCtn=" + URLEncoder.encode(enc_parentCtn) +"&type=" + URLEncoder.encode(type);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				allowApiResult = Integer.parseInt(resultCode);
  				
  				//�ڳฮ��Ʈ ����� �ǰ� �� , ��ġ��� ���Ǹ� �� ���� ���� ������ ��ġ ������ �����ϵ��� ����
  				if(allowApiResult == 0){
  					WizSafeUtil.setSendLocationUser(AllowLocation.this, true);	//���ú�������
  				}

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  //API ȣ�� ������
  	class CallAllowLocationRejectApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(AllowLocation.this));
  				String enc_parentCtn = WizSafeSeed.seedEnc(allowPhoneNumber);
  				String type = "REJECT";
  				String url = "https://www.heream.com/api/allowLocation.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&parentCtn=" + URLEncoder.encode(enc_parentCtn) +"&type=" + URLEncoder.encode(type);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				rejectApiResult = Integer.parseInt(resultCode);

  				pHandler.sendEmptyMessage(2);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
	
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(allowApiResult == 0){
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
  					String title = "����";	
  					String message = "���ݺ��� " + WizSafeUtil.setPhoneNum(allowPhoneNumber) + "���� ������ ��ġ�� ã�� �� �ֽ��ϴ�.";	
  					String buttonName = "Ȯ��";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							finish();
  						}
  					});
  					ad.show();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
  					String title = "��� ����";	
  					String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
  			}else if(msg.what == 2){
  				if(rejectApiResult == 0){
	  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
					String title = "��ġ��뵿��";	
					String message = WizSafeUtil.setPhoneNum(allowPhoneNumber) + "���� �θ� ��û�� �����Ͽ����ϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
  					String title = "��� ����";	
  					String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
  					String buttonName = "Ȯ��";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  						}
  					});
  					ad.show();
  				}
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(AllowLocation.this);
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
