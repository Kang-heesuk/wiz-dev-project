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

public class ChildSafezoneListActivity extends Activity {
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
	
	String parentCtn = "";
	String childCtn = "";
	
	//현재 안심존에 등록된 리스트 사이즈
	int listSize = 0;
	
	//현재 고객의 잔여 포인트
	int myPoint = 0;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	int httpMyPointResult = 1;
	String[][] childSafezoneList;
	
	//선택된 ROW의 번호
	int selectedRow = -1;
	
	//삭제 API 호출 후의 결과값
	int deleteApiResult = -1;	
		
	ChildSafezoneListAdapter childSafezoneListAdapter;
	ListView listView;

	
	//등록된 안심존 리스트
	ArrayList<ChildSafezoneDetail> childSafezoneListArr = new ArrayList<ChildSafezoneDetail>();
	
	ArrayAdapter<ChildSafezoneDetail> childSafezoneAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	}
	
	public void onResume(){
    	super.onResume();
    	setContentView(R.layout.child_safezone_list);
    	
    	//앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        childCtn = intent.getStringExtra("phonenum");
        parentCtn = WizSafeUtil.getCtn(getBaseContext());
        
        //API 호출 쓰레드 시작
    	//안심존 리스트를 가져온다.
    	WizSafeDialog.showLoading(ChildSafezoneListActivity.this);	//Dialog 보이기
        CallGetSafeZoneListApiThread thread = new CallGetSafeZoneListApiThread(); 
		thread.start();
		
        childSafezoneListAdapter = new ChildSafezoneListAdapter(this, R.layout.child_safezone_list_customlist, childSafezoneListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(childSafezoneListAdapter);
    }
	
	//리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
    	childSafezoneListAdapter = new ChildSafezoneListAdapter(this, R.layout.child_safezone_list_customlist, childSafezoneListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(childSafezoneListAdapter);
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
			String textView2 = arSrc.get(position).getSafeAlarmDate();
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
			if("00000000000000".equals(textView2)){
				textArea2.setText("안심존 시간 만료(미진입)");
				textArea2.setVisibility(View.VISIBLE);
			}
			
			
			if(arSrc.get(pos).getSafeAlarmDate() == null || "".equals(arSrc.get(pos).getSafeAlarmDate()) || arSrc.get(pos).getSafeAlarmDate().length() < 10){
				btn_modify.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
							Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
							intent.putExtra("safezoneCode", arSrc.get(pos).getSafezoneCode());
							intent.putExtra("latitude", arSrc.get(pos).getSafeLatitude());
							intent.putExtra("longitude", arSrc.get(pos).getSafeLongitude());
							intent.putExtra("radius", arSrc.get(pos).getSafeRadius());
							intent.putExtra("childCtn", childCtn);
							intent.putExtra("flag", "UPDATE");
							intent.putExtra("listSize", listSize);
							startActivity(intent);
						}
					}
				);
			}else{
				btn_modify.setBackgroundResource(R.drawable.btn_s_modify_long_on);
			}
			
			
			
			btn_delete.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildSafezoneListActivity.this);
						submitAlert.setTitle("안심존삭제");
						submitAlert.setMessage("안심존 설정을 삭제 하시겠습니까?\n설정내용 : "+ arSrc.get(pos).getSafeAddress());
						submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//선택한 rownum
								selectedRow = pos;
								//삭제하기 API 호출 쓰레드 시작
						    	WizSafeDialog.showLoading(ChildSafezoneListActivity.this);	//Dialog 보이기
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
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
		private String safezoneCode;
    	private String safeAddress;
    	private String safeAlarmDate;
    	private String safeLatitude;
		private String safeLongitude;
		private String safeRadius;
		private String safeCtn;
		private String safeChildCtn;
    	
    	public ChildSafezoneDetail (String _safezoneCode, String _safeAddress, String _safeAlarmDate, String _safeLatitude, String _safeLongitude, String _safeRadius, String _safeCtn, String _safeChildCtn){
    		this.safezoneCode = _safezoneCode;
    		this.safeAddress = _safeAddress;
    		this.safeAlarmDate = _safeAlarmDate;
    		this.safeLatitude = _safeLatitude;
    		this.safeLongitude = _safeLongitude;
    		this.safeRadius = _safeRadius;
    		this.safeCtn = _safeCtn;
    		this.safeChildCtn = _safeChildCtn;
    	}
    	private String getSafezoneCode(){
			return safezoneCode;
    	}
    	private String getSafeAddress(){
			return safeAddress;
    	}
    	private String getSafeAlarmDate(){
			return safeAlarmDate;
    	}
    	private String getSafeLatitude(){
			return safeLatitude;
    	}
    	private String getSafeLongitude(){
			return safeLongitude;
    	}
    	private String getSafeRadius(){
			return safeRadius;
    	}
    	private String getSafeCtn(){
			return safeCtn;
    	}
    	private String getSafeChildCtn(){
			return safeChildCtn;
    	}
    }
	
	//API 호출 쓰레드
  	class CallGetSafeZoneListApiThread extends Thread{
  		public void run(){
  			String url = "";
  			HttpURLConnection urlConn;
  			BufferedReader br;
			String temp;
  			try{
  				//고객의 잔여 포인트를 가져오는 로직
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildSafezoneListActivity.this));
  				url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				temp = "";
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_mypoint = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				httpMyPointResult = Integer.parseInt(resultCode_mypoint);
  				if(httpMyPointResult == 0){
  					myPoint = Integer.parseInt(WizSafeSeed.seedDec(WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>")));
  				}else{
  					myPoint = 0;
  				}
  				
  				//안심존 리스트 가져오는 로직
  				url = "https://www.heream.com/api/getChildSafezoneList.jsp?parent_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(parentCtn)) + "&child_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(childCtn));
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				temp = "";
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//결과를 XML 파싱하여 추출 
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				ArrayList<String> strSafezoneCode = new ArrayList<String>(); 
  				ArrayList<String> encAddress = new ArrayList<String>(); 
  				ArrayList<String> strAlarmDate = new ArrayList<String>(); 
  				ArrayList<String> encLatitude = new ArrayList<String>(); 
  				ArrayList<String> encLongitude = new ArrayList<String>(); 
  				ArrayList<String> encRadius = new ArrayList<String>(); 
  				ArrayList<String> encCtn = new ArrayList<String>();
  				ArrayList<String> encChildCtn = new ArrayList<String>();
  				strSafezoneCode = WizSafeParser.xmlParser_List(returnXML,"<SAFEZONE_CODE>");
  				encAddress = WizSafeParser.xmlParser_List(returnXML,"<ADDRESS>");
  				strAlarmDate = WizSafeParser.xmlParser_List(returnXML,"<ALARM_DATE>");
  				encLatitude = WizSafeParser.xmlParser_List(returnXML,"<LATITUDE>");
  				encLongitude = WizSafeParser.xmlParser_List(returnXML,"<LONGITUDE>");
  				encRadius = WizSafeParser.xmlParser_List(returnXML,"<RADIUS>");
  				encCtn = WizSafeParser.xmlParser_List(returnXML,"<CTN>");
  				encChildCtn = WizSafeParser.xmlParser_List(returnXML,"<CHILD_CTN>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				listSize = strSafezoneCode.size();
  				childSafezoneList = new String[listSize][8];
  				
  				if(strSafezoneCode.size() > 0){
  					for(int i=0; i < strSafezoneCode.size(); i++){
  						childSafezoneList[i][0] = (String) strSafezoneCode.get(i);
  					}
  				}
  				if(encAddress.size() > 0){
  					for(int i=0; i < encAddress.size(); i++){
  						String tempAddress = (String)encAddress.get(i);
  						//복호화
  						tempAddress = WizSafeSeed.seedDec(tempAddress);
  						//대한민국 제거
  						tempAddress = WizSafeUtil.replaceStr(tempAddress,"대한민국 ","");
  						childSafezoneList[i][1] = tempAddress;
  					}
  				}
  				if(strAlarmDate.size() > 0){
  					for(int i=0; i < strAlarmDate.size(); i++){
  						childSafezoneList[i][2] = (String) strAlarmDate.get(i);
  					}
  				}
  				if(encLatitude.size() > 0){
  					for(int i=0; i < encLatitude.size(); i++){
  						childSafezoneList[i][3] = WizSafeSeed.seedDec(encLatitude.get(i));
  					}
  				}
  				if(encLongitude.size() > 0){
  					for(int i=0; i < encLongitude.size(); i++){
  						childSafezoneList[i][4] = WizSafeSeed.seedDec(encLongitude.get(i));
  					}
  				}
  				if(encRadius.size() > 0){
  					for(int i=0; i < encRadius.size(); i++){
  						childSafezoneList[i][5] = WizSafeSeed.seedDec(encRadius.get(i));
  					}
  				}
  				if(encCtn.size() > 0){
  					for(int i=0; i < encCtn.size(); i++){
  						childSafezoneList[i][6] = WizSafeSeed.seedDec(encCtn.get(i));
  					}
  				}
  				if(encChildCtn.size() > 0){
  					for(int i=0; i < encChildCtn.size(); i++){
  						childSafezoneList[i][7] = WizSafeSeed.seedDec(encChildCtn.get(i));
  					}
  				}
  				
  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  				childSafezoneListArr = new ArrayList<ChildSafezoneDetail>();
  		    	if(childSafezoneList != null){
  			    	for(int i = 0 ; i < childSafezoneList.length ; i++){
  			    		ChildSafezoneDetail addChildSafezoneList = new ChildSafezoneDetail(childSafezoneList[i][0], childSafezoneList[i][1], childSafezoneList[i][2], childSafezoneList[i][3], childSafezoneList[i][4], childSafezoneList[i][5], childSafezoneList[i][6], childSafezoneList[i][7]);
  			    		childSafezoneListArr.add(addChildSafezoneList);
  			    	}
  		    	}
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}

  	
  	//API 호출 쓰레드
  	class CallDeleteApiThread extends Thread{
  		public void run(){
  			try{
  				String selectedSafezoneCode = childSafezoneListArr.get(selectedRow).getSafezoneCode();
  				String selectedEncCtn = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeCtn());
  				String selectedEncChildCtn = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeChildCtn());
  				String selectedEncRadius = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeRadius());
  				String selectedEncAddr = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeAddress());
  				String selectedEncLat = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeLatitude());
  				String selectedEncLon = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeLongitude());
  				String url = "https://www.heream.com/api/deleteChildSafezone.jsp?safezoneCode=" + URLEncoder.encode(selectedSafezoneCode)+"&parentCtn="+URLEncoder.encode(selectedEncCtn)+"&childCtn="+URLEncoder.encode(selectedEncChildCtn)+"&radius="+URLEncoder.encode(selectedEncRadius)+"&addr="+URLEncoder.encode(selectedEncAddr)+"&lat="+URLEncoder.encode(selectedEncLat)+"&lon="+URLEncoder.encode(selectedEncLon);
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
  				
  				deleteApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//핸들러 정상동작
  				if(httpResult == 0){
  					
  					if(childSafezoneListArr.size() > 0){
  						LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);  						
  			        	visibleArea1.setVisibility(View.VISIBLE);
  			        	visibleArea2.setVisibility(View.GONE);
  			        	
	  			        //안심존 등록 버튼액션(리스트 있는경우)
	  			        findViewById(R.id.btn_addChild).setOnClickListener(
	  						new Button.OnClickListener(){
	  							public void onClick(View v) {
	  								if(myPoint >= 100){
	  									Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
		  								intent.putExtra("flag", "INSERT");
		  								intent.putExtra("childCtn", childCtn);
		  								intent.putExtra("listSize", listSize);
		  								startActivity(intent);
	  								}else{
	  									AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
	  									ad.setTitle("포인트 안내");
	  									ad.setMessage("보유한 포인트가 부족합니다. 포인트 충전 후 다시 이용해 주세요.");
	  									ad.setPositiveButton("포인트\n충전하기", new DialogInterface.OnClickListener() {
	  										public void onClick(DialogInterface dialog, int which) {
	  											Intent intent = new Intent(ChildSafezoneListActivity.this, PointChargeActivity.class);
	  				  							startActivity(intent);
	  										}
	  									});
	  									ad.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
	  										public void onClick(DialogInterface dialog, int which) {
	  										}
	  									});
	  									ad.show();
	  								}
	  								
	  							}
	  						}
	  					);
	  			        
	  			        upDateListView();
	  			      
  					}else{
  			            //리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_safezone1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        
	  			        //안심존 등록 버튼액션(리스트 없는경우)
	  			        findViewById(R.id.btn_noElements).setOnClickListener(
	  			        	new Button.OnClickListener(){
	  			  				public void onClick(View v) {
	  			  					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
	  			  					intent.putExtra("flag", "INSERT");
	  			  					intent.putExtra("childCtn", childCtn);
	  			  					intent.putExtra("listSize", listSize);
	  			  					startActivity(intent);
	  			  				}
	  			  			}
	  			  		);
  					}
  					
				}else{
					//조회실패
				}
  			}else if(msg.what == 1){
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
				String title = "네트워크 오류1";	
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
  			}else if(msg.what == 2){
  				//액티비티 재시작
				Intent intent = getIntent();
				finish();
				startActivity(intent);
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
				String buttonName = "확인";
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
