package com.wiz.Activity;

import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ChildTraceAddActivity extends Activity {
	
	ArrayAdapter<CharSequence> weekAdspin;
	ArrayAdapter<CharSequence> startTimeAdspin;
	ArrayAdapter<CharSequence> endTimeAdspin;
	ArrayAdapter<CharSequence> timeIntervalAdspin;
	
	//���������ν� API ����� �������� �����Ѵ�.
	String phonenum = "";
	String startDay = "1";		//���ۿ���
	String endDay = "5";		//�������
	String startTime = "13";	//���۽ð�
	String endTime = "19";		//����ð�
	String timeInterval = "60";	//����(�д���)
	String textDay = "��~�ݿ���";		//�������â�� �ѷ��� ���� - ��������
	String textStartTime = "����1��";	//�������â�� �ѷ��� ���� - �����ð�
	String textEndTime = "����7��";		//�������â�� �ѷ��� ���� - �����ð�
	String textTimeInterval = "1�ð�";	//�������â�� �ѷ��� ���� - ��������
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
		
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_log_setup);
        
        //���ܰ迡�� ���� �Ѱ� ���� �κ��� ó��
   		Intent intent = getIntent();
   		phonenum = intent.getStringExtra("phonenum");
   		if(intent.getStringExtra("startDay") != null){
   			startDay = intent.getStringExtra("startDay");
   		}
   		if(intent.getStringExtra("endDay") != null){
   			endDay = intent.getStringExtra("endDay");
   		}
		if(intent.getStringExtra("startTime") != null){
			startTime = intent.getStringExtra("startTime");
		}
		if(intent.getStringExtra("endTime") != null){
			endTime = intent.getStringExtra("endTime");
		}
		if(intent.getStringExtra("interval") != null){
			timeInterval = intent.getStringExtra("interval");
		}
        
        
        //������� body ����
        TextView bodyTopText = (TextView)findViewById(R.id.bodyTopText);
        bodyTopText.setText("�ڳ��� �����븦 Ȯ���ϰ� ���� �ð��� ������ �ּ���.(1�� 100����Ʈ ����)"); 
		
        
        //����Ʈ �ڽ� ����(���ϼ���)
        Spinner weekSpiner = (Spinner)findViewById(R.id.weekSpinner);
        weekSpiner.setPrompt("������ �����ϼ���.");
        weekAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_weekSetup, android.R.layout.simple_spinner_item);
        weekAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpiner.setAdapter(weekAdspin);
        if("1".equals(startDay) && "5".equals(endDay)){
        	weekSpiner.setSelection(0);
        }else{
        	weekSpiner.setSelection(1);
        }
        
        //����Ʈ �ڽ� �׼�(���ϼ���)
        weekSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	//����Ʈ �ɶ����� ź��.
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				textDay = weekAdspin.getItem(position).toString();
				switch(position){
				case 0:		//ù��°���� ���� ��~��
					startDay = "1";
					endDay = "5";
					break;
				case 1:		//�ι�°���� ���� ��~��
					startDay = "1";
					endDay = "0";
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
        	
		});
        
        //����Ʈ �ڽ� ����(���۽ð�����)
        Spinner timeStartSpinner = (Spinner)findViewById(R.id.timeStartSpinner);
        timeStartSpinner.setPrompt("���۽ð��� �����ϼ���.");
        startTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_startTimeSetup, android.R.layout.simple_spinner_item);
        startTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeStartSpinner.setAdapter(startTimeAdspin);
        if(intent.getStringExtra("startTime") != null){
        	timeStartSpinner.setSelection(Integer.parseInt(startTime));
        }else{
        	timeStartSpinner.setSelection(13);
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
        endTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_endTimeSetup, android.R.layout.simple_spinner_item);
        endTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeEndSpinner.setAdapter(endTimeAdspin);
        if(intent.getStringExtra("endTime") != null){
        	timeEndSpinner.setSelection(Integer.parseInt(endTime)-1);
        }else{
        	timeEndSpinner.setSelection(18);
        }
        	
        
        timeEndSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(Integer.toString(position + 1).length() > 1){
        			endTime = Integer.toString(position + 1);
        		}else{
        			endTime = "0" + Integer.toString(position + 1);
        		}
        		textEndTime = endTimeAdspin.getItem(position).toString();
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //����Ʈ �ڽ� ����(���ݼ���)
        Spinner timeIntervalSpinner = (Spinner)findViewById(R.id.timeIntervalSpinner);
        timeIntervalSpinner.setPrompt("�ð������� �����ϼ���.");
        timeIntervalAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_timeIntervalSetup, android.R.layout.simple_spinner_item);
        timeIntervalAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIntervalSpinner.setAdapter(timeIntervalAdspin);
        if(intent.getStringExtra("interval") != null){
        	timeIntervalSpinner.setSelection((Integer.parseInt(timeInterval)/60)-1);
        }
        
        
        timeIntervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		timeInterval = Integer.toString((position + 1) * 60);
        		textTimeInterval = timeIntervalAdspin.getItem(position).toString();
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
				submitAlert.setMessage("�޴��� ��ȣ : "+WizSafeUtil.setPhoneNum(phonenum)+"\n���� ���� : " + textDay + "\n���� �ð� : " + textStartTime + "~" + textEndTime + "����" + "\n���� ���� : " + textTimeInterval + "\n�� 1�� 100����Ʈ �� �ڵ� ���� �˴ϴ�.");
				submitAlert.setPositiveButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.i("childTraceAdd",startDay + "===" + endDay + "====" + startTime + "====" + endTime + "====" + timeInterval);
						Log.i("childTraceAdd","==========��Ž���!");
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
}
