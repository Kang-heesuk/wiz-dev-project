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

public class ParentListActivity extends Activity {
	
	//메뉴버튼에서 DELETE 버튼을 눌렀을때 삭제하기 or 대기중 등을 보여주는 토글변수
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;

	//등록된 부모리스트의 리스트
	ArrayList<ParentDetail> parentListArr = new ArrayList<ParentDetail>();
	ArrayAdapter<ParentDetail> parentAdapter;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] parentList;

	//다음액티비티로 넘길때 필요한 부분
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	parentListAdapter listAdapter;
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
    	setContentView(R.layout.parent_list);
        
        //API 호출 쓰레드 시작
    	//부모 리스트를 가져온다.
    	WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog 보이기
        CallGetParentListApiThread thread = new CallGetParentListApiThread(); 
		thread.start();
        
        listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.parent_list_footer, null, false);
        listView.addFooterView(footer);
    }
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    public void upDateListView(boolean deleteMenuToggle){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
	protected void onRestart() {

		super.onRestart();
		/*
		//body 정의
		//리스트를 초기화 하고 서버를 통하여 다시 가져온다.
		parentList.clear();
        //서버와 통신하여 리스트 정보를 가져온다.
        //서버와 통신하여 리스트 정보를 가져온다.        
        //서버와 통신하여 리스트 정보를 가져온다.
		getParentList();
        
        //어댑터 정의
        parentListAdapter = new ParentListAdapter(this, R.layout.safe_list, parentList);
        ListView listView = (ListView)findViewById(R.id.list);
        //실행하면 화면에 listview를 보여준다.
        listView.setAdapter(parentListAdapter);
        */
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

	class parentListAdapter extends BaseAdapter {

    	//메뉴에서 삭제 버튼을 눌렀는지에 대한 여부
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ParentDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//삭제 버튼을 누른후 커스텀 리스트 뷰를 보여줄때
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ParentDetail getItem(int position) {
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
			Button imgState = (Button)convertView.findViewById(R.id.imgState);
			
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
			
			if("".equals(arSrc.get(pos).getAcceptDate())){
				//날짜가 없으면 대기중
				imgState.setBackgroundResource(R.drawable.icon_6);
			}else{
				//날짜가 있으면 활성화
				imgState.setBackgroundResource(R.drawable.icon_7);
			}
			
			//메뉴에서 삭제하기 눌렀을 경우 버튼 노출 문구 정의
			if(menuClickDelete){
				imgState.setBackgroundResource(R.drawable.icon_del_selector);
				imgState.setVisibility(View.VISIBLE);
			}
			
			//각 버튼 액션 정의
			imgState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//삭제하기 버튼을 클릭하였을 경우
						if(menuClickDelete){
							selectedRow = pos;
							String selectedAcceptDate = arSrc.get(selectedRow).getAcceptDate();
							if("".equals(selectedAcceptDate)){
								//대기중 상태의것(수락 날짜가 없는 데이터)은 얼럿 없이 바로 삭제 api호출
								WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog 보이기
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
							}else{
								//02 승인 상태의 것은 얼럿을 통하여 재확인후 삭제  api호출
								//경고창 출력
								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ParentListActivity.this);
								submitAlert.setTitle("삭제하기");
								submitAlert.setMessage("부모("+WizSafeUtil.setPhoneNum(arSrc.get(selectedRow).getparentCtn())+")님을 삭제 하시겠습니까?\n삭제 시 해당 부모님은 자녀님의 위치를 찾을 수 없습니다.");
								submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										//삭제하기 API 호출 쓰레드 시작
								    	WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog 보이기
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
			
			textArea1.setText(arSrc.get(pos).getName());
			textArea2.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(pos).getparentCtn()) + ")");

			return convertView;
		}
    }
	
	
	//custom list data 를 inner class 로 선언
	class ParentDetail {       
		
		private String parentName;
	    private String parentCtn;
	    private String relationState;
	    private String acceptDate;
	    
	    public ParentDetail(String _name, String _pn, String _relationState, String _acceptDate) {
	        this.parentName = _name;
	        this.parentCtn = _pn;
	        this.relationState = _relationState;
	        this.acceptDate = _acceptDate;
	    }
	    
	    public String getName() {
	        return parentName;
	    }

	    public String getparentCtn() {
	        return parentCtn;
	    }
	    
	    public String getState() {
	        return relationState;
	    }
	    
	    public String getAcceptDate() {
	        return acceptDate;
	    }

	}
	
	
	
    //API 호출 쓰레드
  	class CallGetParentListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ParentListActivity.this));
  				String url = "https://www.heream.com/api/getParentList.jsp?ctn="+ URLEncoder.encode(enc_ctn);
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
  				ArrayList<String> encParentName = new ArrayList<String>();
  				ArrayList<String> encParentCtn = new ArrayList<String>();
  				ArrayList<String> state = new ArrayList<String>();
  				ArrayList<String> acceptDate = new ArrayList<String>();
  				encParentName = WizSafeParser.xmlParser_List(returnXML,"<PARENT_NAME>");
  				encParentCtn = WizSafeParser.xmlParser_List(returnXML,"<PARENT_CTN>");
  				state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
  				acceptDate = WizSafeParser.xmlParser_List(returnXML,"<ACCEPT_DATE>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				parentList = new String[encParentCtn.size()][4];
  				
  				if(encParentCtn.size() > 0){
  					for(int i=0; i < encParentCtn.size(); i++){
  						parentList[i][1] = WizSafeSeed.seedDec((String) encParentCtn.get(i));
  					}
  				}
  				if(encParentName.size() > 0){
  					for(int i=0; i < encParentName.size(); i++){
  						parentList[i][0] = WizSafeSeed.seedDec((String) encParentName.get(i));
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						parentList[i][2] = (String) state.get(i);
  					}
  				}
  				if(acceptDate.size() > 0){
  					for(int i=0; i < acceptDate.size(); i++){
  						parentList[i][3] = (String) acceptDate.get(i);
  					}
  				}
  				
  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  				parentListArr = new ArrayList<ParentDetail>();
  		    	if(parentList != null){
  			    	for(int i = 0 ; i < parentList.length ; i++){
  			    		ParentDetail addParentList = new ParentDetail(parentList[i][0], parentList[i][1], parentList[i][2], parentList[i][3]);
  			    		parentListArr.add(addParentList);
  			    	}
  		    	}
  		    	
  		    	whereFlag = parentListArr.size() + 1;
  				alreadyRegCtn = "";
  				if(parentListArr.size() > 0){
  					for(int i = 0 ; i < parentListArr.size() ; i++){
  						alreadyRegCtn = alreadyRegCtn + parentListArr.get(i).getparentCtn() + "|";
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
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ParentListActivity.this));
  				String enc_selectedCtn = WizSafeSeed.seedEnc(parentListArr.get(selectedRow).getparentCtn());
  				String url = "https://www.heream.com/api/deleteRelation.jsp?parentCtn="+ URLEncoder.encode(enc_selectedCtn) + "&childCtn=" + URLEncoder.encode(enc_ctn) + "&me=child";
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
					//조회성공
  					
  					//리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
  			        if(parentListArr.size() <= 0){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_parents1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  			     
  			        //부모등록하기(리스트 있는경우)
  			        findViewById(R.id.btn_parent).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //부모등록하기(리스트 없는경우)
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        
  			        //메뉴키 눌렀을경우 하단에 나오는 메뉴들의 액션 정의
  			        //1. 삭제
  			        if(parentListArr.size() <= 0){
  			        	findViewById(R.id.deleteButton).setBackgroundResource(R.drawable.b_menub_1_off);
  			        }
  			        findViewById(R.id.deleteButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								if(parentListArr.size() > 0){
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
  			        
  			        //2. 부모추가
  			        findViewById(R.id.childAdditionButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
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
  								Intent intent = new Intent(ParentListActivity.this, UseInfoListActivity.class);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        upDateListView();
			    	
				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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