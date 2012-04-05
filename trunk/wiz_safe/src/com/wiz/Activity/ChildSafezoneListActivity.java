package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

public class ChildSafezoneListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";

	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] childSafezoneList;
	
	//등록된 안심존 리스트
	ArrayList<ChildSafezoneDetail> childSafezoneListArr = new ArrayList<ChildSafezoneDetail>();
	
	ArrayAdapter<ChildSafezoneDetail> childSafezoneAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);

		//앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");
        
        //등록된 안심존 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        //getSafeZoneList();
        //API 호출 쓰레드 시작
    	//class 최초 진입시 api 통신으로 위도경도를 가져온다.
    	WizSafeDialog.showLoading(ChildSafezoneListActivity.this);	//Dialog 보이기
        CallGetSafeZoneListApiThread thread = new CallGetSafeZoneListApiThread(); 
		thread.start();
		
		
        //리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
        if(childSafezoneListArr.size() <= 0){
        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
        	bgArea.setBackgroundResource(R.drawable.bg_safezone1);
        	visibleArea1.setVisibility(View.GONE);
        	visibleArea2.setVisibility(View.VISIBLE);
        }
        
        ChildSafezoneListAdapter childSafezoneListAdapter = new ChildSafezoneListAdapter(this, R.layout.child_safezone_list_customlist, childSafezoneListArr);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(childSafezoneListAdapter);
        
        //안심존 등록 버튼액션(리스트 있는경우)
        findViewById(R.id.btn_addChild).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
        //안심존 등록 버튼액션(리스트 없는경우)
        findViewById(R.id.btn_noElements).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
	}
	
	
    class ChildSafezoneListAdapter extends BaseAdapter {

    	//메뉴에서 삭제 버튼을 눌렀는지에 대한 여부
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ChildSafezoneDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public ChildSafezoneListAdapter(Context context, int alayout, ArrayList<ChildSafezoneDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ChildSafezoneDetail getItem(int position) {
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
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			TextView textArea1 = (TextView)convertView.findViewById(R.id.textArea1);
			TextView textArea2 = (TextView)convertView.findViewById(R.id.textArea2);
			Button btn_modify = (Button)convertView.findViewById(R.id.btn_modify);
			Button btn_delete = (Button)convertView.findViewById(R.id.btn_delete);
			
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
			
			//문구 부분
			textArea1.setText(arSrc.get(position).getSafeAddress());
			String textView2 = arSrc.get(position).getsafeAlarmDate();
			if(textView2 != null && !"".equals(textView2) && textView2.length() >= 10){
				String year = textView2.substring(0, 4);
				String month = textView2.substring(4, 6);
				if("0".equals(month.substring(0,1))){
					month = month.substring(1,2);
				}
				String day = textView2.substring(6, 8);
				if("0".equals(day.substring(0,1))){
					day = day.substring(1,2);
				}
				String time = textView2.substring(8, 10);
				textArea2.setText(year + "년 " + month + "월 " + day + "일 " + WizSafeUtil.timeConvertFromNumberToString0to23(time) + " 진입");
				textArea2.setVisibility(View.VISIBLE);
			}
			
			btn_modify.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
						intent.putExtra("phonenum", phonenum);
						intent.putExtra("childName", childName);
						intent.putExtra("safeZoneCode", arSrc.get(pos).getSafeZoneCode());
						startActivity(intent);
					}
				}
			);
			
			btn_delete.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildSafezoneListActivity.this);
						submitAlert.setTitle("안심존삭제");
						submitAlert.setMessage("안심존 설정을 삭제 하시겠습니까?\n설정내용 : "+ arSrc.get(pos).getSafeAddress());
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

			return convertView;
		}
    }
    
	class ChildSafezoneDetail {
		private String safeZoneCode;
    	private String phonenum;
    	private String safeAddress;
    	private String safeAlarmDate;
    	
    	public ChildSafezoneDetail (String safeZoneCode, String phonenum, String safeAddress, String safeAlarmDate){
    		this.safeZoneCode = safeZoneCode;
    		this.phonenum = phonenum;
    		this.safeAddress = safeAddress;
    		this.safeAlarmDate = safeAlarmDate;
    	}
    	private String getSafeZoneCode(){
			return childName;
    	}
    	private String getPhonenum(){
			return phonenum;
    	}
    	private String getSafeAddress(){
			return safeAddress;
    	}
    	private String getsafeAlarmDate(){
			return safeAlarmDate;
    	}
    }
	
	//API 호출 쓰레드
  	class CallGetSafeZoneListApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String url = "https://www.heream.com/api/getNoticeInfo.jsp";
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
  				ArrayList<String> encParentName = WizSafeParser.xmlParser_List(returnXML,"<PARENT_NAME>");
  				ArrayList<String> encParentCtn = WizSafeParser.xmlParser_List(returnXML,"<PARENT_CTN>");
  				ArrayList<String> state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
  				ArrayList<String> acceptDate = WizSafeParser.xmlParser_List(returnXML,"<ACCEPT_DATE>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				childSafezoneList = new String[encParentCtn.size()][4];
  				
  				if(encParentCtn.size() > 0){
  					for(int i=0; i < encParentCtn.size(); i++){
  						childSafezoneList[i][1] = WizSafeSeed.seedDec((String) encParentCtn.get(i));
  					}
  				}
  				if(encParentName.size() > 0){
  					for(int i=0; i < encParentName.size(); i++){
  						childSafezoneList[i][0] = WizSafeSeed.seedDec((String) encParentName.get(i));
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						childSafezoneList[i][2] = (String) state.get(i);
  					}
  				}
  				if(acceptDate.size() > 0){
  					for(int i=0; i < acceptDate.size(); i++){
  						childSafezoneList[i][3] = (String) acceptDate.get(i);
  					}
  				}
  				
  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  		    	if(childSafezoneList != null){
  			    	for(int i = 0 ; i < childSafezoneList.length ; i++){
  			    		ChildSafezoneDetail addParentList = new ChildSafezoneDetail(childSafezoneList[i][0], childSafezoneList[i][1], childSafezoneList[i][2], childSafezoneList[i][3]);
  			    		childSafezoneListArr.add(addParentList);
  			    	}
  		    	}
  				
  				//pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				//pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  			
  			
  			Handler pHandler = new Handler(){
  		  		public void handleMessage(Message msg){
  					WizSafeDialog.hideLoading();
  		  			if(msg.what == 0){
  		  				//핸들러 정상동작
  		  				if(httpResult == 0){
  		  					
  						}else{
  							//조회실패
  						}
  		  			}else if(msg.what == 1){
  		  				//핸들러 비정상
  		  			}
  		  		}
  		  	};
  		}
  	}
	
}
