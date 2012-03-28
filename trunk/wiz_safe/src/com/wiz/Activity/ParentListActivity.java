package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.Activity.ChildListActivity.childDetail;
import com.wiz.Activity.ChildListActivity.childListAdapter;
import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

public class ParentListActivity extends Activity {
	
	//메뉴버튼에서 DELETE 버튼을 눌렀을때 삭제하기 or 대기중 등을 보여주는 토글변수
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;
	
	
	//등록된 부모리스트의 리스트
	ArrayList<ParentDetail> parentListArr = new ArrayList<ParentDetail>();
	
	ArrayAdapter<ParentDetail> parentAdapter;
    
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.parent_list);
        
        getParentList();
        
      //리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
        if(parentListArr.size() <= 0){
        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
        	bgArea.setBackgroundResource(R.drawable.bg_parents1);
        	visibleArea1.setVisibility(View.GONE);
        	visibleArea2.setVisibility(View.VISIBLE);
        }
        
        parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.parent_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
        //부모등록하기(리스트 있는경우)
        findViewById(R.id.btn_parent).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
        //부모등록하기(리스트 없는경우)
        findViewById(R.id.btn_noElements).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
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
						onClickdeleteButtonAction();
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
					int whereFlag = parentListArr.size() + 1;
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
					intent.putExtra("whereFlag", whereFlag);
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

	//부모리스트를 가져오는 로직 - 실행하면 통신하여 리스트를 가져와서  arraylist에 담긴다.
	public void getParentList(){
		//API 연동하여 현재 나에게 등록되어 있는 부모의 리스트를 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값 [3] = 부모리스트에서의 현재 상태
    	String[][] tempHardCoding = {{"소독용","01029325100","02"},{"에탄올","01028283669","01"}};
    	
    	if(tempHardCoding != null){
	    	for(int i = 0 ; i < tempHardCoding.length ; i++){
	    		ParentDetail addParentList = new ParentDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
	    		parentListArr.add(addParentList);
	    	}
    	}
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
			
			if("01".equals(arSrc.get(pos).getState())){
				imgState.setBackgroundResource(R.drawable.icon_6);
			}else if("02".equals(arSrc.get(pos).getState())){
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
							Toast.makeText(ParentListActivity.this, "삭제하기 API 통신", Toast.LENGTH_SHORT).show();
							//액티비티 재시작
							Intent intent = getIntent();
							finish();
							startActivity(intent);
						}
					}
				}
			);
			
			textArea1.setText(arSrc.get(pos).getName());
			textArea2.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(pos).getPhoneNo()) + ")");

			return convertView;
		}
    }
	
	
	//custom list data 를 inner class 로 선언
	class ParentDetail {       
		
		private String name;
	    private String phoneNo;
	    private String relationState;
	    
	    public ParentDetail(String _name, String _pn, String _relationState) {
	        this.name = _name;
	        this.phoneNo = _pn;
	        this.relationState = _relationState;
	    }
	    
	    public String getName() {
	        return name;
	    }

	    public String getPhoneNo() {
	        return phoneNo;
	    }
	    
	    public String getState() {
	        return relationState;
	    }

	}
	
	//메뉴에서 삭제 버튼을 눌렀을 경우 액션
    public void onClickdeleteButtonAction(){
  		if(deleteMenuToggle == false){
  			deleteMenuToggle = true;
  		}else{
  			deleteMenuToggle = false;
  		}
  		
  		//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
	
}