package com.wiz.Activity;
 
import java.io.BufferedReader;
import java.io.InputStream;
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
import android.view.KeyEvent;
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


public class ChildListActivity extends Activity {
	  
	//메뉴버튼에서 DELETE 버튼을 눌렀을때 삭제하기 or 승인대기중 등을 보여주는 토글변수
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;
	
	//등록된 자녀의 이름
	ArrayList<ChildDetail> childListArr = new ArrayList<ChildDetail>();
	ArrayAdapter<ChildDetail> childAdapter;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] childList;
	
	//다음액티비티로 넘길때 필요한 부분
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	ChildListAdapter listAdapter;
	ListView listView;
	
	//선택된 ROW의 번호
	int selectedRow = -1;
	
	//삭제 API 호출 후의 결과값
	int deleteApiResult = -1;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
    }
    
    public void onResume(){
    	super.onResume();
    	setContentView(R.layout.child_list);
    	
    	listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_list_footer, null, false);
        listView.addFooterView(footer);
    	
    	 //API 호출 쓰레드 시작
    	//자녀 리스트를 가져온다.
    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog 보이기
        CallGetChildListApiThread thread = new CallGetChildListApiThread(); 
		thread.start();
    }
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    public void upDateListView(boolean deleteMenuToggle){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.(삭제버튼 눌렀을경우 재호출하는것)
  		listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //메뉴키 눌렀을경우 하단의 메뉴영역이 보이고 안보이고에 따라 레이아웃 조절
    public void bottomMenuToggle(){
    	LinearLayout bodyArea = (LinearLayout)findViewById(R.id.bodyArea);
		LinearLayout bottomMenuArea = (LinearLayout)findViewById(R.id.bottomMenuArea);
		LinearLayout subArea1 = (LinearLayout)findViewById(R.id.subArea1);
		LinearLayout subArea2 = (LinearLayout)findViewById(R.id.subArea2);
		LinearLayout subArea3 = (LinearLayout)findViewById(R.id.subArea3);
		bodyArea.setEnabled(true);
		bottomMenuArea.setEnabled(true);
		subArea1.setEnabled(true);
		subArea2.setEnabled(true);
		subArea3.setEnabled(true);
		LinearLayout.LayoutParams bodyAreaParams = (LinearLayout.LayoutParams)bodyArea.getLayoutParams();
		LinearLayout.LayoutParams bottomAreaParams = (LinearLayout.LayoutParams)bottomMenuArea.getLayoutParams();
		LinearLayout.LayoutParams subArea1Params = (LinearLayout.LayoutParams)subArea1.getLayoutParams();
		LinearLayout.LayoutParams subArea2Params = (LinearLayout.LayoutParams)subArea2.getLayoutParams();
		LinearLayout.LayoutParams subArea3Params = (LinearLayout.LayoutParams)subArea3.getLayoutParams();
		if(bottomMenuArea.getVisibility() == View.VISIBLE){
			bottomAreaIsOn = false;
			bodyAreaParams.weight = 93;
			bottomAreaParams.weight = 0;
			subArea1Params.weight = 28;
			subArea2Params.weight = 16;
			subArea3Params.weight = 56;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.GONE);
		}else{
			bottomAreaIsOn = true;
			bodyAreaParams.weight = 77;
			bottomAreaParams.weight = 16;
			subArea1Params.weight = (float)33.8;
			subArea2Params.weight = (float)19.2;
			subArea3Params.weight = 47;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.VISIBLE);
		}
    }
   
    //메뉴키 눌렀을경우 레이아웃 정의(하단 바가 나온다. 하단 바가 나오면서 상단의 weight 값이 재조정된다.)
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
    	if(bottomAreaIsOn){
    		if(keyCode == 4){
        		bottomMenuToggle();    		
        		return true;
        	}
    	}
    	
    	if(keyCode == 82){
    		bottomMenuToggle();    		
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    class ChildDetail {
    	private String childName;
    	private String childCtn;
    	private String childRelation;
    	private String childRegLocaInfo;
    	
    	public ChildDetail (String childName, String phonenum, String childRelation, String childRegLocaInfo){
    		this.childName = childName;
    		this.childCtn = phonenum;
    		this.childRelation = childRelation;
    		this.childRegLocaInfo = childRegLocaInfo;
    		
    	}
    	private String getChildName(){
			return childName;
    	}
    	private String getChildCtn(){
			return childCtn;
    	}
    	private String getChildRelation(){
			return childRelation;
    	}
    	private String getChildRegLocaInfo(){
			return childRegLocaInfo;
    	}
    }
    
    class ChildListAdapter extends BaseAdapter {

    	//메뉴에서 삭제 버튼을 눌렀는지에 대한 여부
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ChildDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public ChildListAdapter(Context context, int alayout, ArrayList<ChildDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//삭제 버튼을 누른후 커스텀 리스트 뷰를 보여줄때
    	public ChildListAdapter(Context context, int alayout, ArrayList<ChildDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ChildDetail getItem(int position) {
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
			Button btnChildState = (Button)convertView.findViewById(R.id.btnChildState);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			TextView childName = (TextView)convertView.findViewById(R.id.childName);
			TextView childPhonenum = (TextView)convertView.findViewById(R.id.childPhonenum);
			
			childName.setText(arSrc.get(position).getChildName());
			childPhonenum.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(position).getChildCtn()) + ")");
			
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
			
			//각 버튼 이름 및 노출 여부 정의
			if("01".equals(arSrc.get(position).getChildRelation())){
				//01:승인대기
				btnChildState.setBackgroundResource(R.drawable.icon_3);
				btnChildState.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				//02:승인완료
				btnChildState.setBackgroundResource(R.drawable.icon_1);
				btnChildState.setVisibility(View.VISIBLE);
			}else{
				//기타:승인거절
				btnChildState.setBackgroundResource(R.drawable.icon_2);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			//각 버튼 액션 정의
			btnChildState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//삭제하기 버튼을 클릭하였을 경우
						if(menuClickDelete){
							selectedRow = pos;
							String selectedState = arSrc.get(selectedRow).getChildRelation();
							//선택한 삭제 버튼 데이터가 승인거절 데이터면 경고창 없이 바로 삭제한다.
							if("03".equals(selectedState)){
								//경고창 없이 삭제 api 호출
								//삭제하기 API 호출 쓰레드 시작
						    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog 보이기
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
							}else{
								//경고창 출력
								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildListActivity.this);
								submitAlert.setTitle("삭제하기");
								submitAlert.setMessage("자녀("+WizSafeUtil.setPhoneNum(arSrc.get(selectedRow).getChildCtn())+")님을 삭제 하시겠습니까?\n삭제 시 자녀의 동의를 다시 받아야하며 기존 설정 내역도 함께 삭제 됩니다.");
								submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										//삭제하기 API 호출 쓰레드 시작
								    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog 보이기
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
					}
				}
			);
			if("02".equals(arSrc.get(pos).getChildRelation())){
				//해당 클릭리스너는 데이터를 조회할 수 있는 경우에만 활성화 시킨다.
				if(arSrc.get(pos).getChildRegLocaInfo() != null && !"".equals(arSrc.get(pos).getChildRegLocaInfo())){
					btn_nowLocation.setBackgroundResource(R.drawable.btn_s_now_selector);
					btn_nowLocation.setOnClickListener(
						new Button.OnClickListener(){
							public void onClick(View v) {
								Intent intent = new Intent(ChildListActivity.this, ChildLocationViewActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
								startActivity(intent);
							}
						}
					);
				}else{
					btnChildState.setBackgroundResource(R.drawable.icon_5);
				}
				btn_history.setBackgroundResource(R.drawable.btn_s_trace_selector);
				btn_history.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
							Intent intent = new Intent(ChildListActivity.this, ChildTraceListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				);
				btn_safeZone.setBackgroundResource(R.drawable.btn_s_safe_selector);
				btn_safeZone.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
								Intent intent = new Intent(ChildListActivity.this, ChildSafezoneListActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
								intent.putExtra("childName", arSrc.get(pos).getChildName());
								startActivity(intent);
						}
					}
				);
			}
			
		
			//메뉴에서 삭제하기 눌렀을 경우 버튼 노출 문구 정의
			if(menuClickDelete){
				btnChildState.setBackgroundResource(R.drawable.icon_del_selector);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
    }
    
    //API 호출 쓰레드
  	class CallGetChildListApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildListActivity.this));
  				String url = "https://www.heream.com/api/getChildList.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//결과를 XML 파싱하여 추출
  				ArrayList<String> encChildName = new ArrayList<String>();
  				ArrayList<String> encChildCtn = new ArrayList<String>();
  				ArrayList<String> state = new ArrayList<String>();
  				ArrayList<String> regLocaInfo = new ArrayList<String>();
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				encChildName = WizSafeParser.xmlParser_List(returnXML,"<CHILD_NAME>");
  				encChildCtn = WizSafeParser.xmlParser_List(returnXML,"<CHILD_CTN>");
  				state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
  				regLocaInfo = WizSafeParser.xmlParser_List(returnXML,"<REGLOCAINFO>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				childList = new String[encChildCtn.size()][4];
  				if(encChildCtn.size() > 0){
  					for(int i=0; i < encChildCtn.size(); i++){
  						childList[i][1] = WizSafeSeed.seedDec((String) encChildCtn.get(i));
  					}
  				}
  				if(encChildName.size() > 0){
  					for(int i=0; i < encChildName.size(); i++){
  						childList[i][0] = WizSafeSeed.seedDec((String) encChildName.get(i));
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						childList[i][2] = (String) state.get(i);
  					}
  				}
  				if(regLocaInfo.size() > 0){
  					for(int i=0; i < regLocaInfo.size(); i++){
  						childList[i][3] = (String) regLocaInfo.get(i);
  					}
  				}

  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  				childListArr = new ArrayList<ChildDetail>();
  		    	if(childList != null){
  			    	for(int i = 0 ; i < childList.length ; i++){
  			    		ChildDetail addChildList = new ChildDetail(childList[i][0], childList[i][1], childList[i][2], childList[i][3]);
  			    		childListArr.add(addChildList);
  			    	}
  		    	}
  		    	
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
  	
  	
  	//API 호출 쓰레드
  	class CallDeleteApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildListActivity.this));
  				String enc_selectedCtn = WizSafeSeed.seedEnc(childListArr.get(selectedRow).getChildCtn());
  				String url = "https://www.heream.com/api/deleteRelation.jsp?parentCtn="+ URLEncoder.encode(enc_ctn) + "&childCtn=" + URLEncoder.encode(enc_selectedCtn) + "&me=parent";
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
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
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
  					whereFlag = childListArr.size() + 1;
  					alreadyRegCtn = "";
  					if(childListArr.size() > 0){
  						for(int i = 0 ; i < childListArr.size() ; i++){
  							alreadyRegCtn = alreadyRegCtn + childListArr.get(i).getChildCtn() + "|";
  						}
  					}
  					
  					//리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
  			        if(childListArr.size() <= 0){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_child1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  					
  			        //자녀등록하기(리스트 있는경우)
  			        findViewById(R.id.btn_addChild).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //자녀등록하기(리스트 없는경우)
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        
  			        //메뉴키 눌렀을경우 하단에 나오는 메뉴들의 액션 정의
  			        //1. 삭제
  			        if(childListArr.size() <= 0){
  			        	findViewById(R.id.deleteButton).setBackgroundResource(R.drawable.b_menub_1_off);
  			        }
  			        findViewById(R.id.deleteButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								if(childListArr.size() > 0){
  									bottomMenuToggle();
  									if(deleteMenuToggle == false){
  							  			deleteMenuToggle = true;
  							  		}else{
  							  			deleteMenuToggle = false;
  							  		}
  							  		upDateListView(deleteMenuToggle);
  								}else{
  									
  								}
  							}
  						}
  					);
  			        
  			        //2. 자녀추가
  			        findViewById(R.id.childAdditionButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //2. 이용안내
  			        findViewById(R.id.useInfoButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ChildListActivity.this, UseInfoListActivity.class);
  								startActivity(intent);
  							}
  						}
  					);
  					
  			      upDateListView();
			    	
				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
					String title = "통신 오류";	
					String message = "부모 리스트 정보를 조회할 수 없습니다.";	
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
  				//핸들러 비정상
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
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