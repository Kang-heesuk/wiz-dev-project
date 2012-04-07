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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	String myPoint = "";
	String startDay = "";
	String endDay = "";
	String startTime = "";
	String endTime = "";
	String interval = "";
	String nowOperationState = "";
	String traceLogCode = "";
	
	//���� ���������� �Ͽ����� �Ǵ�.
	boolean isRegisterTrace = false;
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int addApiResult = -1;
	
	//������ API ȣ�� ���� �����
	int customerInfoApiResult = -1;
	
	//���� API ȣ�� ���� �����
	int deleteApiResult = -1;
	
	//���� API ȣ�� ���� �����
	int switchApiResult = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_list);
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");
        
        //API ȣ�� ������ ����
    	//������ ������ �����´�.
    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog ���̱�
    	CallChildTraceListApiThread thread = new CallChildTraceListApiThread(); 
		thread.start();

	}
	
	//API ȣ�� ������
  	class CallChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				HttpURLConnection urlConn;
  				BufferedReader br;
  				String temp;
  				String url;
  				
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);

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
  				customerInfoApiResult = Integer.parseInt(resultCode_getCustomerInfo);
  				if(customerInfoApiResult == 0){
  					myPoint = WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>");
  					if(myPoint == null) myPoint = WizSafeSeed.seedEnc("0");
  					myPoint = WizSafeSeed.seedDec(myPoint);
  				}
  				
  				//������ ����Ʈ �������� ���
  				url = "https://www.heream.com/api/getChildTraceList.jsp?ctn=" + URLEncoder.encode(enc_ctn) +"&childCtn="+ URLEncoder.encode(enc_childCtn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_getList = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode_getList);
  				
  				if(addApiResult == 0){
  					isRegisterTrace = true;
  				}else{
  					isRegisterTrace = false;
  				}
				startDay = WizSafeParser.xmlParser_String(returnXML,"<START_DAY>");
				endDay = WizSafeParser.xmlParser_String(returnXML,"<END_DAY>");
				startTime = WizSafeParser.xmlParser_String(returnXML,"<START_TIME>");
				endTime = WizSafeParser.xmlParser_String(returnXML,"<END_TIME>");
				interval = WizSafeParser.xmlParser_String(returnXML,"<INTERVAL>");
				nowOperationState = WizSafeParser.xmlParser_String(returnXML,"<TRACE_STATE>");
				traceLogCode = WizSafeParser.xmlParser_String(returnXML,"<TRACELOG_CODE>");

				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API ȣ�� ������ �����ϱ�
  	class CallDeleteChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/deleteChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&childCtn="+ URLEncoder.encode(enc_childCtn) + "&traceLogCode="+ URLEncoder.encode(traceLogCode);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				deleteApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(2);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
  	//API ȣ�� ������ ��������
  	class CallSwitchChildTraceListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String traceState = "";
  				
  				//������ ���°�
  				if("0".equals(nowOperationState)){
  					traceState = "1";
  				}else{
  					traceState = "0";
  				}
  				
  				String url = "https://www.heream.com/api/switchChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&childCtn="+ URLEncoder.encode(enc_childCtn) + "&traceState="+ URLEncoder.encode(traceState) + "&traceLogCode="+ URLEncoder.encode(traceLogCode);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				switchApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(4);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(5);
  			}
  		}
  	}
  	
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(addApiResult == 0 || addApiResult == 1){
  					
  					//����Ʈ�� �����ϴ��� �ƴϳĿ� ���� ���̴� ���̾ƿ��� �޶�����.
  			        if(!isRegisterTrace){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_trace1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  			        
  			        LinearLayout layout_1 = (LinearLayout)findViewById(R.id.layout_1);
  			        Button nowStateBtn = (Button)findViewById(R.id.nowStateBtn);
  			        
  			        //���� ����Ʈ�� ���� 100����Ʈ�� ���ٸ� ����Ʈ ������ �����ְ�, ���ۻ��¸� ���������� ���������Ѵ�.
  			        int myRemainPoint = 0;
  			        if(myPoint == null || "".equals(myPoint)){
  			        	myPoint = "0";
  			        }
  			        myRemainPoint = Integer.parseInt(myPoint);
  			        TextView pointArea = (TextView)findViewById(R.id.pointArea) ;
  			        if(myRemainPoint < 100){
  			        	pointArea.setVisibility(View.VISIBLE);
  			        	nowOperationState = "0";
  			        }
  			        
  			        //���� ���¿� ���� ������ �Ǵ� �����߿����� ��׶��� ���� �� Ŭ�� ���� ���� ����
  			        if("1".equals(nowOperationState)){
  			        	//1. ��׶��� ��ȯ
  			        	layout_1.setBackgroundResource(R.drawable.trace_playlist_bg);
  			        	//2. ���̾ƿ� Ŭ���� ���κ���� �̵�
  			        	layout_1.setOnClickListener(
  							new Button.OnClickListener(){
  								public void onClick(View v) {
  									Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceDetailListActivity.class);
  									intent.putExtra("phonenum", phonenum);
  									intent.putExtra("childName", childName);
  									intent.putExtra("startDay", startDay);
  									intent.putExtra("endDay", endDay);
  									intent.putExtra("startTime", startTime);
  									intent.putExtra("endTime", endTime);
  									intent.putExtra("interval", interval);
  									
  									startActivity(intent);
  								}
  							}
  						);
  			        }else{
  			        	//1. ��׶��� ��ȯ, Ŭ���� ���� ����.
  			        	layout_1.setBackgroundResource(R.drawable.trace_stoplist_bg);
  			        }
  			        
  			        //�ڳ������ ���� ���� ��ư �׼� ����
  			        nowStateBtn.setOnClickListener(
						new Button.OnClickListener(){
							public void onClick(View v) {
								//API ȣ�� ������ ����
						    	//���� ���� on/off ���� ������ ȣ��
						    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog ���̱�
						    	CallSwitchChildTraceListApiThread thread = new CallSwitchChildTraceListApiThread(); 
								thread.start();
							}
						}
					);
  			        
  			        //�ڳ��̸�, ����ȣ, ����, �ð� , ������ ȭ�鿡 ����
  					TextView childNameArea = (TextView)findViewById(R.id.childNameArea);
  					TextView phonenumArea = (TextView)findViewById(R.id.phonenumArea);
  			        TextView weekendArea = (TextView)findViewById(R.id.weekendArea);
  			        TextView timeArea = (TextView)findViewById(R.id.timeArea);
  			        TextView intervalArea = (TextView)findViewById(R.id.intervalArea);
  			        
  			        childNameArea.setText(childName);
  			        phonenumArea.setText("(" + WizSafeUtil.setPhoneNum(phonenum) + ")");
  			        weekendArea.setText("���� : " + WizSafeUtil.dayConvertFromNumberToString(startDay) +" ~ "+ WizSafeUtil.dayConvertFromNumberToString(endDay));
  			        timeArea.setText("�ð� : " + WizSafeUtil.timeConvertFromNumberToString0to23(startTime) +" ~ "+ WizSafeUtil.timeConvertFromNumberToString1to24(endTime));
  			        intervalArea.setText("���� : " + WizSafeUtil.intervalConvertMinToHour(interval) + "�ð�");
  			        
  			        //�����ϱ�� �����ϱ� ��ư �׼� ����
  			        Button btn_modify = (Button)findViewById(R.id.btn_modify);
  			        Button btn_delete = (Button)findViewById(R.id.btn_delete);
  			        
  			        btn_modify.setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceAddActivity.class);
  								intent.putExtra("phonenum", phonenum);
  								intent.putExtra("childName", childName);
  								intent.putExtra("startDay", startDay);
  								intent.putExtra("endDay", endDay);
  								intent.putExtra("startTime", startTime);
  								intent.putExtra("endTime", endTime);
  								intent.putExtra("interval", interval);
  								intent.putExtra("nowOperationState", nowOperationState);
  								intent.putExtra("traceLogCode", traceLogCode);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        btn_delete.setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceListActivity.this);
  								submitAlert.setTitle("���������");
  								submitAlert.setMessage("������ ������ ���� �Ͻðڽ��ϱ�?\n�޴��� ��ȣ : "+ WizSafeUtil.setPhoneNum(phonenum) + "\n���� ���� : " + WizSafeUtil.dayConvertFromNumberToString(startDay) + "~" + WizSafeUtil.dayConvertFromNumberToString(endDay) + "����\n���� �ð� : " + WizSafeUtil.timeConvertFromNumberToString0to23(startTime)+ "~" + WizSafeUtil.timeConvertFromNumberToString1to24(endTime) + "����" + "\n���� ���� : " + WizSafeUtil.intervalConvertMinToHour(interval) + "�ð�\n�� ���� �� ���� ����Ʈ�� ȯ�� ���� �ʽ��ϴ�.");
  								submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
  									public void onClick(DialogInterface dialog, int which) {
  										//API ȣ�� ������ ����
  								    	//������ ������ �����´�.
  								    	WizSafeDialog.showLoading(ChildTraceListActivity.this);	//Dialog ���̱�
  								    	CallDeleteChildTraceListApiThread thread = new CallDeleteChildTraceListApiThread(); 
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
  			        
  			        //�ڳ����ϱ� ��ư�׼�
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceAddActivity.class);
  								intent.putExtra("phonenum", phonenum);
  								intent.putExtra("childName", childName);
  								startActivity(intent);
  							}
  						}
  					);
				}else if(addApiResult == -1){
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  			}if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  				
  				if(deleteApiResult == 0){
  					//��Ƽ��Ƽ �����
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				ad.show();
  			}else if(msg.what == 4){
  				if(switchApiResult == 0){
  					//��Ƽ��Ƽ �����
  					Intent intent = getIntent();
  					finish();
  					startActivity(intent);
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
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
  			}else if(msg.what == 5){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceListActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				ad.show();
  			}
  		}
  	};

}
