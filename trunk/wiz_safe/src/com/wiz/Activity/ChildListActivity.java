package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChildListActivity extends Activity {

	//등록된 자녀의 이름
	ArrayList<childDetail> childList = new ArrayList<childDetail>();
	
	ArrayAdapter<childDetail> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.child_list);
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				Log.i("testTop","Delete =============");
			} 
		}); 
        
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getMyChildren();
        
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
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childList);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //현재는 자녀를 3명 까지 등록가능하다고 하고 개발하는것임다.
    public void getMyChildren() {
    	//API 연동하여 현재 나에게 등록되어있는 자녀를 가져온다.
    	//연동 API 는 자녀가 있으면 자녀이름 과 인증여부를 가져오고 없다면 ""(공백)을 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값
    	String[][] tempHardCoding = {{"박재하","Y","01012345678"},{"꽃분이","N","0105484565"},{"정용진","Y","01024882698"},{"반홍","N","01084464664"}};
    	
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

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<childDetail> arSrc;
    	int layout;
    	
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
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
			LinearLayout bottomBtnArea= (LinearLayout)convertView.findViewById(R.id.bottomBtnArea);
			ImageView img = (ImageView)convertView.findViewById(R.id.ePhoto);
			Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			TextView eName = (TextView)convertView.findViewById(R.id.eName);
			TextView info1 = (TextView)convertView.findViewById(R.id.info1);
			
			eName.setText(arSrc.get(position).getChildName());
			info1.setText(WizSafeUtil.setPhoneNum(arSrc.get(position).getChildPhone()));
			
			//각 버튼 액션 정의
			btn_accept.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Intent intent = new Intent(ChildListActivity.this, ChildAddCompleteActivity.class);
						intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
						startActivity(intent);
					}
				}
			);
			
			btn_nowLocation.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Intent intent = new Intent(ChildListActivity.this, LocationViewActivity.class);
						startActivity(intent);
					}
				}
			);
			
			btn_history.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Log.i("childList","btn_history");
					}
				}
			);
			
			btn_safeZone.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Log.i("childList","btn_safeZone");
					}
				}
			);
			
			//자녀리스트 승낙 여부에 따라서 커스텀 뷰의 디자인이 다르다.
			if("Y".equals(arSrc.get(position).getChildRelation())){
				btn_accept.setVisibility(View.INVISIBLE);
				bottomBtnArea.setVisibility(View.VISIBLE);
			}else{
				btn_accept.setVisibility(View.VISIBLE);
				btn_accept.setText("재요청");
				bottomBtnArea.setVisibility(View.GONE);
			}
			
			return convertView;
		}

    }
    
}