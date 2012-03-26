package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.Activity.ChildListActivity.childDetail;
import com.wiz.Activity.ChildListActivity.childListAdapter;
import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChildSafezoneListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	
	//등록된 안심존 리스트
	ArrayList<safeList> safetyList = new ArrayList<safeList>();
	
	ArrayAdapter<safeList> childAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);

		//앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");		
        childName = intent.getStringExtra("childName");
        
        //등록된 안심존 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getSafeZoneList();
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.child_safezone_list_customlist, safetyList);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
	}
	
    public void getSafeZoneList() {
    	//API 연동하여 현재 나에게 등록되어 있는 안심존 리스트를 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값	[1] = 시퀀스 넘버이다.  [3] = 안심존에 진입한 경우 날짜 + 시간이 들어오고, 아닌경우 공백이 들어온다.
    	String[][] tempHardCoding = {{"2",phonenum,"서울시특별시 송파구 가락동 78","2012032616"},{"1",phonenum,"서울특별시 송파구 가락동 81-5","0"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		safeList addSafeList = new safeList(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2], tempHardCoding[i][3]);
    		safetyList.add(addSafeList);
    	}
    }
	
    class childListAdapter extends BaseAdapter {

    	//메뉴에서 삭제 버튼을 눌렀는지에 대한 여부
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<safeList> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public childListAdapter(Context context, int alayout, ArrayList<safeList> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public safeList getItem(int position) {
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

			return convertView;
		}
    }
    
	class safeList {
		private String safeZoneCode;
    	private String phonenum;
    	private String safeAddress;
    	private String safeAlarmDate;
    	
    	public safeList (String safeZoneCode, String phonenum, String safeAddress, String safeAlarmDate){
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
	
}
