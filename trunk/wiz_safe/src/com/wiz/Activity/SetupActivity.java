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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class SetupActivity extends Activity {
	
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int addApiResult = -1;
	
	//���ڼ��� ���°� �����ϴ� API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int updateApiResult = -1;
	
	//���� ���� ���� ���� ���°�
	String myAlramState = "1";
	
	//�ٲ� ���� ���� ���� ���°�
	String setMyAlramState = "";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_list);
        
        //API ȣ�� ������ ����
    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog ���̱�
    	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		thread.start();
    }
    
    //API ȣ�� ������(���� ���ڼ��� ���°� ��������)
  	class CallGetCustomerInformationApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(SetupActivity.this));

  				//�������� �������� ���
  				url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getCustomerInfo = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode_getCustomerInfo);
  				if(addApiResult == 0){
  					myAlramState = WizSafeParser.xmlParser_String(returnXML,"<ALRAMSTATE>");
  				}

				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API ȣ�� ������(���� ���ڼ��� ���°� �����ϱ�)
  	class CallSetCustomerAlramStateApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(SetupActivity.this));

  				//���ڼ��� ���°��� �����Ű�� API
  				url = "https://www.heream.com/api/alramOnOff.jsp?ctn=" + URLEncoder.encode(enc_ctn) + "&setValue=" + URLEncoder.encode(setMyAlramState);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getCustomerInfo = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				updateApiResult = Integer.parseInt(resultCode_getCustomerInfo);

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
  				if(addApiResult == 0){
  					Button btn01 = (Button)findViewById(R.id.btn1);
  	  		        btn01.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, NoticeListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn02 = (Button)findViewById(R.id.btn2);
  	  		        btn02.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, UseInfoListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn03 = (Button)findViewById(R.id.btn3);
  	  		        btn03.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, FaqListActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn04 = (Button)findViewById(R.id.btn4);
  	  		        btn04.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, QuestionActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn05 = (Button)findViewById(R.id.btn5);
  	  		        btn05.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, PointLogActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        Button btn06 = (Button)findViewById(R.id.btn6);
  	  		        btn06.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						Intent intent = new Intent(SetupActivity.this, LocationLogActivity.class);
  	  						startActivity(intent);
  	  					}
  	  				});
  	  		        
  	  		        Button btn_sms = (Button)findViewById(R.id.btn_sms);
  	  		        if("1".equals(myAlramState)){
  	  		        	btn_sms.setTag(R.drawable.btn_on);
  	  		        	btn_sms.setBackgroundResource(R.drawable.btn_on);
  	  		        }else{
  	  		        	btn_sms.setTag(R.drawable.btn_off);
  	  		        	btn_sms.setBackgroundResource(R.drawable.btn_off);
  	  		        }
  	  		        btn_sms.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						int compareValue = (Integer)findViewById(R.id.btn_sms).getTag();
  	  						if(compareValue == R.drawable.btn_off){
  	  							setMyAlramState = "1";
  	  							findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_on);
  	  							findViewById(R.id.btn_sms).setTag(R.drawable.btn_on);
  	  							
  	  							//API ȣ�� ������ ����
	  	  				    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog ���̱�
	  	  				    	CallSetCustomerAlramStateApiThread thread = new CallSetCustomerAlramStateApiThread(); 
	  	  						thread.start();
  	  						}else{
  	  							setMyAlramState = "0";
  	  							findViewById(R.id.btn_sms).setBackgroundResource(R.drawable.btn_off);
  	  							findViewById(R.id.btn_sms).setTag(R.drawable.btn_off);
  	  							
	  	  						//API ȣ�� ������ ����
	  	  				    	WizSafeDialog.showLoading(SetupActivity.this);	//Dialog ���̱�
	  	  				    	CallSetCustomerAlramStateApiThread thread = new CallSetCustomerAlramStateApiThread(); 
	  	  						thread.start();
  	  						}
  	  					}
  	  				});
  	  		        
  	  		        Button btn_hide = (Button)findViewById(R.id.btn_hide);
  	  		        if(WizSafeUtil.isHiddenOnUser(SetupActivity.this)){
  	  		        	btn_hide.setTag(R.drawable.btn_on);
  	  		        	btn_hide.setBackgroundResource(R.drawable.btn_on);
  	  		        }else{
  	  		        	btn_hide.setTag(R.drawable.btn_off);
  	  		        	btn_hide.setBackgroundResource(R.drawable.btn_off);
  	  		        }
  	  		        
  	  		        btn_hide.setOnClickListener(new Button.OnClickListener() {
  	  					public void onClick(View v) {
  	  						int compareValue = (Integer)findViewById(R.id.btn_hide).getTag	();
  	  						if(compareValue == R.drawable.btn_off){
  	  							AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  	  							String title = "��ġ����";	
  	  							String message = "��ġ���� ����� Ȱ��ȭ �ϸ� �θ���� �����ϰ� ������ �� �����ϴ�.";
  	  							ad.setTitle(title);
  	  							ad.setMessage(message);
  	  							ad.setPositiveButton("�ѱ�", new DialogInterface.OnClickListener() {
  	  								public void onClick(DialogInterface dialog, int which) {
  	  									//�޴����ȿ� ����� ��ġ ���� �÷��׸� �����Ͽ� �� �����Ѵ�.
  	  									LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  	  									edit = LocalSave.edit();
  	  									edit.putString("isHiddenOnUser", "0");
  	  									edit.commit();
  	  									findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_on);
  	  									findViewById(R.id.btn_hide).setTag(R.drawable.btn_on);
  	  								}
  	  							});
  	  							ad.setNegativeButton(R.string.btn_cancel, null);
  	  							ad.show();
  	  						}else{
  	  							//�޴����ȿ� ����� ��ġ ���� �÷��׸� �����Ͽ� �� �����Ѵ�.
  	  							LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  	  							edit = LocalSave.edit();
  	  							edit.putString("isHiddenOnUser", "1");
  	  							edit.commit();
  	  							findViewById(R.id.btn_hide).setBackgroundResource(R.drawable.btn_off);
  	  							findViewById(R.id.btn_hide).setTag(R.drawable.btn_off);
  	  						}
  	  					}
  	  				});
  				}else{
  					//��ȸ����
  					AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  					String title = "��� ����";	
  					String message = "��� �� ������ �߻��Ͽ����ϴ�. ";	
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
  		        
  			}else if(msg.what == 1){
  				//��ȸ����
				AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�. ";	
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
  				if(updateApiResult == 0){
  					//��Ƽ��Ƽ �����
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					//��ȸ����
  					AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
  					String title = "��� ����";	
  					String message = "��� �� ������ �߻��Ͽ����ϴ�. ";	
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
  				//��ȸ����
				AlertDialog.Builder ad = new AlertDialog.Builder(SetupActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�. ";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ad.show();
  			}
  		}
  	};
}