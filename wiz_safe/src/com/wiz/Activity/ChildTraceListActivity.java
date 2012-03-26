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
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");
        
        //서버와의 통신을 통하여 필요한 값을 가져온다.========== 현재는 하드코딩
        startDay = "1";
        endDay = "5";
        startTime = "00";
        endTime = "07";
        interval = "60";
        nowOperationState = "1";
        
        
        
        LinearLayout layout_1 = (LinearLayout)findViewById(R.id.layout_1);
        
        //현재 상태에 따라 동작중 또는 비동작중에대한 백그라운드 상태 및 클릭 가능 상태 변경
        if("1".equals(nowOperationState)){
        	//1. 백그라운드 변환
        	layout_1.setBackgroundResource(R.drawable.trace_playlist_bg);
        	//2. 레이아웃 클릭시 세부보기로 이동
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
        	//1. 백그라운드 변환, 클릭시 반응 없음.
        	layout_1.setBackgroundResource(R.drawable.trace_stoplist_bg);
        }
        
        //자녀이름, 폰번호, 요일, 시간 , 간격을 화면에 노출
		TextView childNameArea = (TextView)findViewById(R.id.childNameArea);
		TextView phonenumArea = (TextView)findViewById(R.id.phonenumArea);
        TextView weekendArea = (TextView)findViewById(R.id.weekendArea);
        TextView timeArea = (TextView)findViewById(R.id.timeArea);
        TextView intervalArea = (TextView)findViewById(R.id.intervalArea);
        
        childNameArea.setText(childName);
        phonenumArea.setText("(" + WizSafeUtil.setPhoneNum(phonenum) + ")");
        weekendArea.setText("요일 : " + WizSafeUtil.dayConvertFromNumberToString(startDay) +" ~ "+ WizSafeUtil.dayConvertFromNumberToString(endDay));
        timeArea.setText("시간 : " + WizSafeUtil.timeConvertFromNumberToString(startTime) +" ~ "+ WizSafeUtil.timeConvertFromNumberToString(endTime));
        intervalArea.setText("간격 : " + WizSafeUtil.intervalConvertMinToHour(interval) + "시간");
        
        //수정하기와 삭제하기 버튼 액션 정의
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
					submitAlert.setTitle("발자취삭제");
					submitAlert.setMessage("발자취 설정을 삭제 하시겠습니까?\n휴대폰 번호 : "+ WizSafeUtil.setPhoneNum(phonenum) + "\n설정 요일 : " + WizSafeUtil.dayConvertFromNumberToString(startDay) + "~" + WizSafeUtil.dayConvertFromNumberToString(endDay) + "요일\n설정 시간 : " + WizSafeUtil.timeConvertFromNumberToString(startTime)+ "~" + WizSafeUtil.timeConvertFromNumberToString(endTime) + "까지" + "\n설정 간격 : " + WizSafeUtil.intervalConvertMinToHour(interval) + "시간\n※ 삭제 시 금일 포인트는 환급 되지 않습니다.");
					submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Log.i("traceChild","==========통신시작!");
						}
					});
					submitAlert.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					submitAlert.show();
				}
			}
		);        
        
        //자녀등록하기 버튼액션
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
