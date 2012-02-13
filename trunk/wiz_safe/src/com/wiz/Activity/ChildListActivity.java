package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    	
    	//가져온 값	[2] = 1 승인완료 / 2 승인대기 / 3 승인거절
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
			
			
			
			//각 버튼 이름 및 노출 여부 정의
			bottomBtnArea.setVisibility(View.VISIBLE);
			if("01".equals(arSrc.get(position).getChildRelation())){
				btn_accept.setText("승인대기중");
				btn_accept.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				btn_accept.setText("승인완료");
				btn_accept.setVisibility(View.VISIBLE);
			}else{
				btn_accept.setText("승인거절");
				btn_accept.setVisibility(View.VISIBLE);
			}
			
			
			//메뉴에서 삭제하기 눌렀을 경우 버튼 노출 문구 정의
			if(menuClickDelete){
				btn_accept.setText("삭제하기");
				btn_accept.setVisibility(View.VISIBLE);
			}
			
			//각 버튼 액션 정의
			btn_accept.setOnClickListener(
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
							Intent intent = new Intent(ChildListActivity.this, ChildTraceAddActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							startActivity(intent);
						}
					}
				}
			);
			
			btn_safeZone.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildSafezoneAddActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							startActivity(intent);
						}
					}
				}
			);

			return convertView;
		}
    }
    
    
    //메뉴키를 눌렀을때 동작하는 부분 menu xml 중 child_list 부분을 가져온다.
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.child_list, menu);
		return true; 
    }
    
    //메뉴에서 해당 ITEM 을 선택하였을 경우 동작하는 부분
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
   	  	case R.id.deleteMenu:
   	  		if(deleteMenuToggle == false){
   	  			deleteMenuToggle = true;
   	  		}else{
   	  			deleteMenuToggle = false;
   	  		}
   	  		//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
   	  		childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childList, deleteMenuToggle);
   	  		ListView listView = (ListView)findViewById(R.id.list);
        	listView.setAdapter(listAdapter);
        
   	  		return true;
    	}
    	return false;
    }
    
}