package com.wiz.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.util.WizSafeUtil;

public class ChildTraceListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	String startDay = "";
	String endDay = "";
	String startTime = "";
	String endTime = "";
	String interval = "";
	String nowOperationState = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_list);
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");
        
        //�������� ����� ���Ͽ� �ʿ��� ���� �����´�.========== ����� �ϵ��ڵ�
        startDay = "1";
        endDay = "5";
        startTime = "00";
        endTime = "07";
        interval = "60";
        nowOperationState = "1";
        
        
        
        LinearLayout layout_1 = (LinearLayout)findViewById(R.id.layout_1);
        
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
						startActivity(intent);
					}
				}
			);
        }else{
        	//1. ��׶��� ��ȯ, Ŭ���� ���� ����.
        	layout_1.setBackgroundResource(R.drawable.trace_stoplist_bg);
        }
        
        //�ڳ��̸�, ����ȣ, ����, �ð� , ������ ȭ�鿡 ����
		TextView childNameArea = (TextView)findViewById(R.id.childNameArea);
		TextView phonenumArea = (TextView)findViewById(R.id.phonenumArea);
        TextView weekendArea = (TextView)findViewById(R.id.weekendArea);
        TextView timeArea = (TextView)findViewById(R.id.timeArea);
        TextView intervalArea = (TextView)findViewById(R.id.intervalArea);
        
        childNameArea.setText(childName);
        phonenumArea.setText("(" + WizSafeUtil.setPhoneNum(phonenum) + ")");
        weekendArea.setText("���� : " + WizSafeUtil.dayConvertFromNumberToString(startDay) +" ~ "+ WizSafeUtil.dayConvertFromNumberToString(endDay));
        timeArea.setText("�ð� : " + WizSafeUtil.timeConvertFromNumberToString(startTime) +" ~ "+ WizSafeUtil.timeConvertFromNumberToString(endTime));
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
					startActivity(intent);
				}
			}
		);
        
        btn_delete.setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceListActivity.this);
					submitAlert.setTitle("���������");
					submitAlert.setMessage("������ ������ ���� �Ͻðڽ��ϱ�?\n�޴��� ��ȣ : "+ WizSafeUtil.setPhoneNum(phonenum) + "\n���� ���� : " + WizSafeUtil.dayConvertFromNumberToString(startDay) + "~" + WizSafeUtil.dayConvertFromNumberToString(endDay) + "����\n���� �ð� : " + WizSafeUtil.timeConvertFromNumberToString(startTime)+ "~" + WizSafeUtil.timeConvertFromNumberToString(endTime) + "����" + "\n���� ���� : " + WizSafeUtil.intervalConvertMinToHour(interval) + "�ð�\n�� ���� �� ���� ����Ʈ�� ȯ�� ���� �ʽ��ϴ�.");
					submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Log.i("traceChild","==========��Ž���!");
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
        /*
        findViewById(R.id.btn_addTrace).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ChildTraceListActivity.this, ChildTraceAddActivity.class);
					intent.putExtra("phonenum", phonenum);
					startActivity(intent);
				}
			}
		);
		*/

	}

}
