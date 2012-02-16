package com.wiz.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class ChildTraceAddActivity extends Activity {
	
	ArrayAdapter<CharSequence> weekAdspin;
	ArrayAdapter<CharSequence> startTimeAdspin;
	ArrayAdapter<CharSequence> endTimeAdspin;
	ArrayAdapter<CharSequence> timeIntervalAdspin;
	
	//���������ν� API ����� �������� �����Ѵ�.
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
		
		ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildTraceAddActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_log_setup);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        //������� body ����
        TextView bodyTopText = (TextView)findViewById(R.id.bodyTopText);
        bodyTopText.setText("�ڳ��� �����븦 Ȯ���ϰ� ���� �ð��� ������ �ּ���.(1�� 100����Ʈ ����)"); 
		
        
        //����Ʈ �ڽ� ����(���ϼ���)
        Spinner weekSpiner = (Spinner)findViewById(R.id.weekSpinner);
        weekSpiner.setPrompt("������ �����ϼ���.");
        weekAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_weekSetup, android.R.layout.simple_spinner_item);
        weekAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpiner.setAdapter(weekAdspin);
        
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
        timeStartSpinner.setSelection(13);	//����Ʈ ���� - ����Ʈ�� Ŀ���� �������� ���� ������ �ȵȴ�.
        
        timeStartSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		startTime = Integer.toString(position);
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
        timeEndSpinner.setSelection(18);	//����Ʈ ���� - ����Ʈ�� Ŀ���� �������� ���� ������ �ȵȴ�.
        
        timeEndSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		endTime = Integer.toString(position + 1);
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
        
        Log.i("childTraceAdd",startDay + "===" + endDay + "====" + startTime + "====" + endTime + "====" + timeInterval);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_setup:
				//���â ���
				AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceAddActivity.this);
				submitAlert.setTitle("������ ����");
				submitAlert.setMessage("�޴��� ��ȣ : ����Ʈ�� �ѱ�\n���� ���� : " + textDay + "\n���� �ð� : " + textStartTime + "~" + textEndTime + "����" + "\n���� ���� : " + textTimeInterval + "\n�� 1�� 100����Ʈ �� �ڵ� ���� �˴ϴ�.");
				submitAlert.setPositiveButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
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
