package com.wiz.Activity;

import java.util.ArrayList;
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
import com.wiz.util.WizSafeUtil;

public class ChildListActivity extends Activity {
	
	//메뉴버튼에서 DELETE 버튼을 눌렀을때 삭제하기 or 승인대기중 등을 보여주는 토글변수
	boolean deleteMenuToggle = false;
	
	//등록된 자녀의 이름
	ArrayList<childDetail> childList = new ArrayList<childDetail>();
	
	ArrayAdapter<childDetail> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.child_list);
               
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getMyChildren();
       
        childListAdapter listAdapter = new childListAdapter(this, R.layout.child_list_customlist, childList);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
        
        //자녀등록하기 버튼액션
        findViewById(R.id.btn_addChild).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					int whereFlag = childList.size() + 1;
					Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
					intent.putExtra("whereFlag", whereFlag);
					startActivity(intent);
				}
			}
		);
        
        
        
        //메뉴키 눌렀을경우 하단에 나오는 메뉴들의 액션 정의
        //1. 삭제
        findViewById(R.id.deleteButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					onClickdeleteButtonAction();
				}
			}
		);
        
        //2. 자녀추가
        findViewById(R.id.childAdditionButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					int whereFlag = childList.size() + 1;
					Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
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
					Intent intent = new Intent(ChildListActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
		);
        
    }
    
    //메뉴키 눌렀을경우 하단의 메뉴영역이 보이고 안보이고에 따라 레이아웃 조절
    public void bottomMenuToggle(){
    	LinearLayout listArea = (LinearLayout)findViewById(R.id.listArea);
		LinearLayout bottomMenuArea = (LinearLayout)findViewById(R.id.bottomMenuArea);
		listArea.setEnabled(true);
		bottomMenuArea.setEnabled(true);
		LinearLayout.LayoutParams listAreaParams = (LinearLayout.LayoutParams)listArea.getLayoutParams();
		LinearLayout.LayoutParams bottomAreaParams = (LinearLayout.LayoutParams)bottomMenuArea.getLayoutParams();
		if(bottomMenuArea.getVisibility() == View.VISIBLE){
			listAreaParams.weight = 93;
			bottomAreaParams.weight = 0;
			listArea.setLayoutParams(listAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			bottomMenuArea.setVisibility(View.GONE);
		}else{
			listAreaParams.weight = 77;
			bottomAreaParams.weight = 16;
			listArea.setLayoutParams(listAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			bottomMenuArea.setVisibility(View.VISIBLE);
		}
    }
   
    //메뉴키 눌렀을경우 레이아웃 정의(하단 바가 나온다. 하단 바가 나오면서 상단의 weight 값이 재조정된다.)
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
    	if(keyCode == 82){
    		bottomMenuToggle();    		
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    
    public void getMyChildren() {
    	//API 연동하여 현재 나에게 등록되어있는 자녀를 가져온다.
    	//연동 API 는 자녀가 있으면 자녀이름 과 인증여부를 가져오고 없다면 ""(공백)을 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값	[2] = 01 승인완료 / 02 승인대기 / 03 승인거절
    	String[][] tempHardCoding = {{"박재하","01","01012345678"},{"꽃분이","02","0105484565"},{"정용진","03","01024882698"},{"반홍","01","01084464664"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		childDetail addChildList = new childDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		childList.add(addChildList);
    	}
    	
    }
    
    class childDetail {
    	private String childName;
    	private String childRelation;
    	private String childPhone;
    	
    	public childDetail (String childName, String childRelation, String phonenum){
    		this.childName = childName;
    		this.childRelation = childRelation;
    		this.childPhone = phonenum;
    	}
    	private String getChildName(){
			return childName;
    	}
    	private String getChildRelation(){
			return childRelation;
    	}
    	private String getChildPhone(){
			return childPhone;
    	}
    }
    
    class childListAdapter extends BaseAdapter {

    	//메뉴에서 삭제 버튼을 눌렀는지에 대한 여부
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<childDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//삭제 버튼을 누른후 커스텀 리스트 뷰를 보여줄때
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public childDetail getItem(int position) {
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
			childPhonenum.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(position).getChildPhone()) + ")");
			
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
				btnChildState.setBackgroundResource(R.drawable.icon_3);
				btnChildState.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				btnChildState.setBackgroundResource(R.drawable.icon_1);
				btnChildState.setVisibility(View.VISIBLE);
			}else{
				btnChildState.setBackgroundResource(R.drawable.icon_2);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			
			//메뉴에서 삭제하기 눌렀을 경우 버튼 노출 문구 정의
			if(menuClickDelete){
				btnChildState.setBackgroundResource(R.drawable.icon_del_selector);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			//각 버튼 액션 정의
			btnChildState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//삭제하기 버튼을 클릭하였을 경우
						if(menuClickDelete){
							Toast.makeText(ChildListActivity.this, "삭제하기 API 통신", Toast.LENGTH_SHORT).show();
							
							//액티비티 재시작
							Intent intent = getIntent();
							finish();
							startActivity(intent);
							
						}else{	//그 외의 경우(일반적인 경우)
							if("01".equals(arSrc.get(pos).getChildRelation())){
								Intent intent = new Intent(ChildListActivity.this, ChildAddCompleteActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
								startActivity(intent);
							}
						}
					}
				}
			);
			
			btn_nowLocation.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildLocationViewActivity.class);
							startActivity(intent);
						}
					}
				}
			);
			
			btn_history.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildTraceListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				}
			);
			
			btn_safeZone.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildSafezoneListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				}
			);

			return convertView;
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
  		childListAdapter listAdapter = new childListAdapter(this, R.layout.child_list_customlist, childList, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
}