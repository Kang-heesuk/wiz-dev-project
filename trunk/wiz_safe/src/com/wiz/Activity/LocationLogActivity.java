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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class LocationLogActivity extends Activity {

	//등록된 내위치 조회이력
	ArrayList<locationLogDetail> locationLogList = new ArrayList<locationLogDetail>();
	ArrayAdapter<locationLogDetail> locationLogAdapter;
    
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] locationLog;
	
	locationLogListAdapter listAdapter;
    ListView listView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_log_list);
        
        //API 호출 쓰레드 시작
    	//내위치 조회이력 리스트를 가져온다.
    	WizSafeDialog.showLoading(LocationLogActivity.this);	//Dialog 보이기
    	CallGetLocationLogApi thread = new CallGetLocationLogApi(); 
		thread.start();
    }
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
    	listAdapter = new locationLogListAdapter(this, R.layout.location_log_customlist, locationLogList);
        listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    //cms와 연동된 공지사항을 불러온다.
    public void getLocationLogList() {
    	
    	String[][] tempHardCoding = {{"01/12 14:30","01012341234","서울시 강남구 역삼동"},{"01/12 14:30","01012341234","서울시 강남구 역삼동"},{"01/12 14:30","01012341234","서울시 강남구 역삼동"},{"01/12 14:30","01012341234","서울시 강남구 역삼동"},{"01/12 14:30","01012341234","서울시 강남구 역삼동"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		locationLogDetail addLocationLogList = new locationLogDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		locationLogList.add(addLocationLogList);
    	}

    }
    
    
    class locationLogDetail {
    	private String regdate;
    	private String phonenum;
    	private String location;
    	
    	public locationLogDetail (String regdate, String phonenum, String location){
    		this.regdate = regdate;
    		this.phonenum = phonenum;
    		this.location = location;
    	}
    	private String getRegdate(){
			return regdate;
    	}
    	private String getPhonenum(){
			return phonenum;
    	}
    	private String getLocation(){
			return location;
    	}
    }
    
    
    class locationLogListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<locationLogDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public locationLogListAdapter(Context context, int alayout, ArrayList<locationLogDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public locationLogDetail getItem(int position) {
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
			TextView regdate = (TextView)convertView.findViewById(R.id.regdate);
			TextView phonenum = (TextView)convertView.findViewById(R.id.phonenum);
			TextView location = (TextView)convertView.findViewById(R.id.location);
			
			//날짜형식변환
			String tempRegdate = arSrc.get(position).getRegdate();
			if(tempRegdate.length() >= 14){
				tempRegdate =  tempRegdate.substring(4,6) + "/" + tempRegdate.substring(6,8) + " " + tempRegdate.substring(8,10) + ":" + tempRegdate.substring(10,12);
			}
			String tempPhonenum = WizSafeSeed.seedDec(arSrc.get(position).getPhonenum());
			String templocation = WizSafeSeed.seedDec(arSrc.get(position).getLocation());
			
			regdate.setText(tempRegdate);
			phonenum.setText(tempPhonenum);
			location.setText(templocation);
			
			//마퀴 효과를 보여주기 위한 추가 
			regdate.setSelected(true);
			phonenum.setSelected(true);
			location.setSelected(true);
			
			return convertView;
		}
    }
    
    //API 호출 쓰레드
  	class CallGetLocationLogApi extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(LocationLogActivity.this));
  				String url = "http://www.heream.com/api/getLocationBoard.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//결과를 XML 파싱하여 추출
  				ArrayList<String> regdate = new ArrayList<String>();
  				ArrayList<String> parentCtn = new ArrayList<String>();
  				ArrayList<String> addr = new ArrayList<String>();
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				regdate = WizSafeParser.xmlParser_List(returnXML,"<LIST_REGDATE>");
  				parentCtn = WizSafeParser.xmlParser_List(returnXML,"<LIST_PARENT_CTN>");
  				addr = WizSafeParser.xmlParser_List(returnXML,"<LIST_ADDRESS>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				locationLog = new String[regdate.size()][3];
  				if(regdate.size() > 0){
  					for(int i=0; i < regdate.size(); i++){
  						locationLog[i][0] = regdate.get(i);
  					}
  				}
  				if(parentCtn.size() > 0){
  					for(int i=0; i < parentCtn.size(); i++){
  						locationLog[i][1] = parentCtn.get(i);
  					}
  				}
  				if(addr.size() > 0){
  					for(int i=0; i < addr.size(); i++){
  						locationLog[i][2] =  addr.get(i);
  					}
  				}
  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  				locationLogList = new ArrayList<locationLogDetail>();
  		    	if(locationLog != null){
  			    	for(int i = 0 ; i < locationLog.length ; i++){
  			    		locationLogDetail addLocationLogList = new locationLogDetail(locationLog[i][0], locationLog[i][1], locationLog[i][2]);
  			    		locationLogList.add(addLocationLogList);
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
  				if(httpResult == 0){
  					upDateListView();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(LocationLogActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(LocationLogActivity.this);
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