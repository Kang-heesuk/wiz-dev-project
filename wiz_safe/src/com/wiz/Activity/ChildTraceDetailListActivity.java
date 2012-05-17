package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceDetailListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	
	//발자취 리스트
	ArrayList<TraceDetail> childLogList = new ArrayList<TraceDetail>();
	ArrayAdapter<TraceDetail> childAdapter;
	
	childListAdapter listAdapter;
	ListView listView;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] traceList;
	
	//통신으로 받아온 리스트의 사이즈 
	int listSize = 0;
	
	//선택된 ROW의 번호
	int selectedRow = -1;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.child_trace_detail_list);
       
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");

        //API 호출 쓰레드 시작
    	//발자취 리스트를 가져온다.
    	WizSafeDialog.showLoading(ChildTraceDetailListActivity.this);	//Dialog 보이기
    	CallDetailListApiThread thread = new CallDetailListApiThread(); 
		thread.start();

    }
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
    	listAdapter = new childListAdapter(this, R.layout.child_trace_detail_list_customlist, childLogList);
		listView = (ListView)findViewById(R.id.list1);
		listView.setAdapter(listAdapter);
    }

    class childListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<TraceDetail> arSrc;
    	int layout;
    	
    	public childListAdapter(Context context, int alayout, ArrayList<TraceDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public TraceDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//각 위젯 정의
			LinearLayout layout1 = (LinearLayout)convertView.findViewById(R.id.layout1); 
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			TextView textArea = (TextView)convertView.findViewById(R.id.textArea);
			
			//커스텀 리스트 뷰 앞쪽 이미지 숫자
			if((position + 1) == 1){
				imgNum.setBackgroundResource(R.drawable.img_num_1);
			}else if((position + 1) == 2){
				imgNum.setBackgroundResource(R.drawable.img_num_2);
			}else if((position + 1) == 3){
				imgNum.setBackgroundResource(R.drawable.img_num_3);
			}else if((position + 1) == 4){
				imgNum.setBackgroundResource(R.drawable.img_num_4);
			}else if((position + 1) == 5){
				imgNum.setBackgroundResource(R.drawable.img_num_5);
			}else if((position + 1) == 6){
				imgNum.setBackgroundResource(R.drawable.img_num_6);
			}else if((position + 1) == 7){
				imgNum.setBackgroundResource(R.drawable.img_num_7);
			}else if((position + 1) == 8){
				imgNum.setBackgroundResource(R.drawable.img_num_8);
			}else if((position + 1) == 9){
				imgNum.setBackgroundResource(R.drawable.img_num_9);
			}else if((position + 1) == 10){
				imgNum.setBackgroundResource(R.drawable.img_num_10);
			}else if((position + 1) == 11){
				imgNum.setBackgroundResource(R.drawable.img_num_11);
			}else if((position + 1) == 12){
				imgNum.setBackgroundResource(R.drawable.img_num_12);
			}else if((position + 1) == 13){
				imgNum.setBackgroundResource(R.drawable.img_num_13);
			}else if((position + 1) == 14){
				imgNum.setBackgroundResource(R.drawable.img_num_14);
			}else if((position + 1) == 15){
				imgNum.setBackgroundResource(R.drawable.img_num_15);
			}else if((position + 1) == 16){
				imgNum.setBackgroundResource(R.drawable.img_num_16);
			}else if((position + 1) == 17){
				imgNum.setBackgroundResource(R.drawable.img_num_17);
			}else if((position + 1) == 18){
				imgNum.setBackgroundResource(R.drawable.img_num_18);
			}else if((position + 1) == 19){
				imgNum.setBackgroundResource(R.drawable.img_num_19);
			}else if((position + 1) == 20){
				imgNum.setBackgroundResource(R.drawable.img_num_20);
			}
			
			String yyyymmdd = arSrc.get(pos).getTraceDay().substring(0,4) + "년 " + arSrc.get(pos).getTraceDay().substring(4,6) + "월 " + arSrc.get(pos).getTraceDay().substring(6,8) + "일";
			textArea.setText(yyyymmdd + " 발자취");
			
			//각 버튼 액션 정의
			layout1.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
			            Intent intent = new Intent(ChildTraceDetailListActivity.this, ChildTraceViewActivity.class);
			            intent.putExtra("childCtn", phonenum);
						intent.putExtra("selectedDay", arSrc.get(pos).getTraceDay());
						startActivity(intent);
					}
				}
			);
			return convertView;
		}
    }
    
    class TraceDetail {
    	private String traceDay;
    	private String traceDayOfWeek;
    	
    	public TraceDetail(String traceDay, String traceDayOfWeek){
    		this.traceDay = traceDay;
    		this.traceDayOfWeek = traceDayOfWeek;
    	}
    	
    	private String getTraceDay(){
			return traceDay;
    	}
    	private String getTraceDayOfWeek(){
			return traceDayOfWeek;
    	}
    }
    
    //API 호출 쓰레드
  	class CallDetailListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_parentCtn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceDetailListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/getChildTraceDetailList.jsp?parentCtn="+ URLEncoder.encode(enc_parentCtn) + "&childCtn="+ URLEncoder.encode(enc_childCtn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//결과를 XML 파싱하여 추출
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String resultSize = WizSafeParser.xmlParser_String(returnXML,"<RESULT_COUNT>");
  				ArrayList<String> elementDay = WizSafeParser.xmlParser_List(returnXML,"<ELEMENT_DAY>");
  				ArrayList<String> elementDayOfWeek = WizSafeParser.xmlParser_List(returnXML,"<ELEMENT_DAY_OF_WEEK>");
  				
  				//2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				listSize = Integer.parseInt(resultSize);
  				
  				//조회해온 리스트 사이즈 만큼의 2차원 배열을 선언한다.
  				traceList = new String[elementDay.size()][2]; 
  				
  				//조회해온 값을 2차원 배열에 넣는다.
  				if(elementDay.size() > 0){
  					for(int i=0; i < elementDay.size(); i++){
  						traceList[i][0] = elementDay.get(i);
  					}
  				}
  				if(elementDayOfWeek.size() > 0){
  					for(int i=0; i < elementDayOfWeek.size(); i++){
  						traceList[i][1] = elementDayOfWeek.get(i);
  					}
  				}
  				
  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  		    	if(traceList != null){
  			    	for(int i = 0 ; i < traceList.length ; i++){
  			    		TraceDetail addChildLogList = new TraceDetail(traceList[i][0], traceList[i][1]);
  			    		childLogList.add(addChildLogList);
  			    	}
  		    	}
  		    	
  		    	pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//핸들러 정상동작
  				if(httpResult == 0){
					//조회성공
  					//리스트가 1개 이상 존재할경우
  					if(listSize > 0){
  						upDateListView();
  					}else{
  						AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
  						String title = "발자취 없음";	
  						String message = "해당 설정으로 검색한 자녀 발자취가 존재하지 않습니다.";	
  						String buttonName = "확인";
  						ad.setTitle(title);
  						ad.setMessage(message);
  						ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog, int which) {
  								finish();
  							}
  						});
  						ad.show();
  					}
  					
				}else{
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
					String title = "네트워크 오류";	
					String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
					String buttonName = "확인";
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
				String buttonName = "확인";
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