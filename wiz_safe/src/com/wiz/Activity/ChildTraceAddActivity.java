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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceAddActivity extends Activity {
	
	ArrayAdapter<CharSequence> weekAdspin;
	ArrayAdapter<CharSequence> startTimeAdspin;
	ArrayAdapter<CharSequence> endTimeAdspin;
	ArrayAdapter<CharSequence> timeIntervalAdspin;
	
	//���������ν� API ����� �������� �����Ѵ�.
	String phonenum = "";
	String childName = "";
	String startWeek = "";			//���ۿ���
	String endWeek = "";				//�������
	String startTime = "";			//���۽ð�
	String endTime = "";			//����ð�
	String interval = "";			//����(�д���)
	String traceLogCode = "";		//��� SEQ
	String textDay = "";			//�������â�� �ѷ��� ���� - ��������
	String textStartTime = "";		//�������â�� �ѷ��� ���� - �����ð�
	String textEndTime = "";		//�������â�� �ѷ��� ���� - �����ð�
	String textInterval = "";		//�������â�� �ѷ��� ���� - ��������
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int addApiResult = -1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
		
        //���ܰ迡�� ���� �Ѱ� ���� �κ��� ó��
   		Intent intent = getIntent();
   		phonenum = intent.getStringExtra("phonenum");
   		childName = intent.getStringExtra("childName");
   		if(intent.getStringExtra("startWeek") != null){
   			startWeek = intent.getStringExtra("startWeek");
   		}
   		if(intent.getStringExtra("endWeek") != null){
   			endWeek = intent.getStringExtra("endWeek");
   		}
		if(intent.getStringExtra("startTime") != null){
			startTime = intent.getStringExtra("startTime");
		}
		if(intent.getStringExtra("endTime") != null){
			endTime = intent.getStringExtra("endTime");
		}
		if(intent.getStringExtra("interval") != null){
			interval = intent.getStringExtra("interval");
		}
		if(intent.getStringExtra("traceLogCode") != null){
			traceLogCode = intent.getStringExtra("traceLogCode");
			findViewById(R.id.btn_setup).setBackgroundResource(R.drawable.btn_modify_selector);
		}else{
			traceLogCode = "INSERT";
		}
        
        //����Ʈ �ڽ� ����(���ϼ���)
        Spinner weekSpiner = (Spinner)findViewById(R.id.weekSpinner);
        weekSpiner.setPrompt("������ �����ϼ���.");
        weekAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_weekSetup, R.layout.text_spinner_item);
        weekAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpiner.setAdapter(weekAdspin);
        
        if(intent.getStringExtra("startWeek") != null && intent.getStringExtra("endWeek") != null){
	        if("1".equals(startWeek) && "5".equals(endWeek)){
	        	weekSpiner.setSelection(0);
	        }else{
	        	weekSpiner.setSelection(1);
	        }
        }else{
        	weekSpiner.setSelection(0);
        }
        
        //����Ʈ �ڽ� �׼�(���ϼ���)
        weekSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	//����Ʈ �ɶ����� ź��.
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				textDay = weekAdspin.getItem(position).toString();
				switch(position){
				case 0:		//ù��°���� ���� ��~��
					startWeek = "1";
					endWeek = "5";
					break;
				case 1:		//�ι�°���� ���� ��~��
					startWeek = "1";
					endWeek = "0";
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
        	
		});
        
        //����Ʈ �ڽ� ����(���۽ð�����)
        Spinner timeStartSpinner = (Spinner)findViewById(R.id.timeStartSpinner);
        timeStartSpinner.setPrompt("���۽ð��� �����ϼ���.");
        startTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_startTimeSetup, R.layout.text_spinner_item);
        startTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeStartSpinner.setAdapter(startTimeAdspin);
        if(intent.getStringExtra("startTime") != null){
        	timeStartSpinner.setSelection(Integer.parseInt(startTime));
        }else{
        	timeStartSpinner.setSelection(8);
        }
        
        timeStartSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(Integer.toString(position).length() > 1){
        			startTime = Integer.toString(position);
        		}else{
        			startTime = "0" + Integer.toString(position);
        		}
        		
        		textStartTime = startTimeAdspin.getItem(position).toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //����Ʈ �ڽ� ����(����ð�����)
        Spinner timeEndSpinner = (Spinner)findViewById(R.id.timeEndSpinner);
        timeEndSpinner.setPrompt("����ð��� �����ϼ���.");
        endTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_endTimeSetup, R.layout.text_spinner_item);
        endTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeEndSpinner.setAdapter(endTimeAdspin);
        if(intent.getStringExtra("endTime") != null){
        	//����ð��� 00�� �̸� ����Ʈ�ڽ� ����Ʈ�� ���ϴ��� ���� 12�ø� �����Ѵ�.
        	if("00".equals(endTime)){
        		endTime = "24";
        	}
        	timeEndSpinner.setSelection(Integer.parseInt(endTime)-1);
        }else{
        	timeEndSpinner.setSelection(16);
        }
        	
        
        timeEndSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(Integer.toString(position + 1).length() > 1){
        			endTime = Integer.toString(position + 1);
        		}else{
        			endTime = "0" + Integer.toString(position + 1);
        		}
        		//24�ô� 00�÷� ��ü�ؼ� DB�� �����ϱ� ���� ����(����ð��� �ش��)
        		if("24".equals(endTime)){
        			endTime = "00";
        		}
        		
        		textEndTime = endTimeAdspin.getItem(position).toString();
        		
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
         
        //����Ʈ �ڽ� ����(���ݼ���)
        Spinner timeIntervalSpinner = (Spinner)findViewById(R.id.timeIntervalSpinner);
        timeIntervalSpinner.setFocusable(false);
        timeIntervalSpinner.setFocusableInTouchMode(false);
        timeIntervalSpinner.setPrompt("�ð������� �����ϼ���.");
        timeIntervalAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_timeIntervalSetup, R.layout.text_spinner_item);
        timeIntervalAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIntervalSpinner.setAdapter(timeIntervalAdspin);
        if(intent.getStringExtra("interval") != null){
        	timeIntervalSpinner.setSelection((Integer.parseInt(interval)/60)-1);
        }

        timeIntervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		interval = Integer.toString((position + 1) * 60);
        		textInterval = timeIntervalAdspin.getItem(position).toString();
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        
        //��ư �׼� ����
        findViewById(R.id.btn_setup).setOnClickListener(mClickListener);
        findViewById(R.id.btn_cancel).setOnClickListener(mClickListener);

	}
	
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_setup:
				//���â ���
				AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceAddActivity.this);
				submitAlert.setTitle("������ ����");
				submitAlert.setMessage("�޴��� ��ȣ : "+WizSafeUtil.setPhoneNum(phonenum)+"\n���� ���� : " + textDay + "\n���� �ð� : " + textStartTime + "~" + textEndTime + "����" + "\n���� ���� : " + textInterval + "\n�� 1�� 100����Ʈ �� �ڵ� ���� �˴ϴ�.");
				submitAlert.setPositiveButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//API ��� ������ �����Ѵ�.
				        WizSafeDialog.showLoading(ChildTraceAddActivity.this);	//Dialog ���̱�
				        CallChildTraceAddApiThread thread = new CallChildTraceAddApiThread();
						thread.start();
					}
				});
				submitAlert.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				submitAlert.show();
				break;
			case R.id.btn_cancel:
				ChildTraceAddActivity.this.finish();
				break;
			}
		}
	};
	
	
	//API ȣ�� ������
  	class CallChildTraceAddApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceAddActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/addChildTrace.jsp?ctn="+ URLEncoder.encode(enc_ctn) +"&childCtn="+ URLEncoder.encode(enc_childCtn) +"&childName="+ URLEncoder.encode(enc_childCtn) + "&startWeek=" + URLEncoder.encode(startWeek) + "&endWeek=" + URLEncoder.encode(endWeek) + "&startTime=" + URLEncoder.encode(startTime) + "&endTime=" + URLEncoder.encode(endTime) + "&interval=" + URLEncoder.encode(interval) + "&traceLogCode=" + URLEncoder.encode(traceLogCode);
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
					finish();
				}else{
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceAddActivity.this);
					String title = "��� ����";	
					String message = "������ ��� �� ������ �߻��Ͽ����ϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}
  			}if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceAddActivity.this);
				String title = "��Ʈ��ũ ����";	
				String message = "��Ʈ��ũ ������ �����ǰ� �ֽ��ϴ�.\n��Ʈ��ũ ���¸� Ȯ�� �Ŀ� �ٽ� �õ����ּ���.";	
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
