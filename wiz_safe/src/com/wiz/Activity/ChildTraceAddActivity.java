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
	
	//전역변수로써 API 통신할 변수들을 셋팅한다.
	String startDay = "1";		//시작요일
	String endDay = "5";		//종료요일
	String startTime = "13";	//시작시간
	String endTime = "19";		//종료시간
	String timeInterval = "60";	//간격(분단위)
	String textDay = "월~금요일";		//설정경고창에 뿌려질 문구 - 설정요일
	String textStartTime = "오후1시";	//설정경고창에 뿌려질 문구 - 설정시간
	String textEndTime = "오후7시";		//설정경고창에 뿌려질 문구 - 설정시간
	String textTimeInterval = "1시간";	//설정경고창에 뿌려질 문구 - 설정간격
	
	
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
        
        //여기부터 body 구성
        TextView bodyTopText = (TextView)findViewById(R.id.bodyTopText);
        bodyTopText.setText("자녀의 발자취를 확인하고 싶은 시간을 설정해 주세요.(1일 100포인트 소진)"); 
		
        
        //셀렉트 박스 구성(요일설정)
        Spinner weekSpiner = (Spinner)findViewById(R.id.weekSpinner);
        weekSpiner.setPrompt("요일을 설정하세요.");
        weekAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_weekSetup, android.R.layout.simple_spinner_item);
        weekAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpiner.setAdapter(weekAdspin);
        
        //셀렉트 박스 액션(요일설정)
        weekSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	//셀렉트 될때마다 탄다.
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				textDay = weekAdspin.getItem(position).toString();
				switch(position){
				case 0:		//첫번째것을 선택 월~금
					startDay = "1";
					endDay = "5";
					break;
				case 1:		//두번째것을 선택 월~일
					startDay = "1";
					endDay = "0";
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
        	
		});
        
        
        //셀렉트 박스 구성(시작시간설정)
        Spinner timeStartSpinner = (Spinner)findViewById(R.id.timeStartSpinner);
        timeStartSpinner.setPrompt("시작시간을 설정하세요.");
        startTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_startTimeSetup, android.R.layout.simple_spinner_item);
        startTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeStartSpinner.setAdapter(startTimeAdspin);
        timeStartSpinner.setSelection(13);	//디폴트 설정 - 디폴트로 커서는 가있지만 값은 설정이 안된다.
        
        timeStartSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		startTime = Integer.toString(position);
        		textStartTime = startTimeAdspin.getItem(position).toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //셀렉트 박스 구성(종료시간설정)
        Spinner timeEndSpinner = (Spinner)findViewById(R.id.timeEndSpinner);
        timeEndSpinner.setPrompt("종료시간을 설정하세요.");
        endTimeAdspin = ArrayAdapter.createFromResource(this, R.array.ChildTraceAddActivity_endTimeSetup, android.R.layout.simple_spinner_item);
        endTimeAdspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeEndSpinner.setAdapter(endTimeAdspin);
        timeEndSpinner.setSelection(18);	//디폴트 설정 - 디폴트로 커서는 가있지만 값은 설정이 안된다.
        
        timeEndSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		endTime = Integer.toString(position + 1);
        		textEndTime = endTimeAdspin.getItem(position).toString();
			}
        	
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        //셀렉트 박스 구성(간격설정)
        Spinner timeIntervalSpinner = (Spinner)findViewById(R.id.timeIntervalSpinner);
        timeIntervalSpinner.setPrompt("시간간격을 설정하세요.");
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
        
        //버튼 액션 정의
        findViewById(R.id.btn_setup).setOnClickListener(mClickListener);
        findViewById(R.id.btn_cancel).setOnClickListener(mClickListener);
        
        Log.i("childTraceAdd",startDay + "===" + endDay + "====" + startTime + "====" + endTime + "====" + timeInterval);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_setup:
				//경고창 출력
				AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildTraceAddActivity.this);
				submitAlert.setTitle("발자취 설정");
				submitAlert.setMessage("휴대폰 번호 : 인텐트로 넘김\n설정 요일 : " + textDay + "\n설정 시간 : " + textStartTime + "~" + textEndTime + "까지" + "\n설정 간격 : " + textTimeInterval + "\n※ 1일 100포인트 씩 자동 소진 됩니다.");
				submitAlert.setPositiveButton("등록", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.i("childTraceAdd","==========통신시작!");
					}
				});
				submitAlert.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
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
