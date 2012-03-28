package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocationLogActivity extends Activity {

	//등록된 자녀의 이름
	ArrayList<locationLogDetail> locationLogList = new ArrayList<locationLogDetail>();
	
	ArrayAdapter<locationLogDetail> locationLogAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_log_list);
        
        //해당 고객의 포인트 사용 로그를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getLocationLogList();

        locationLogListAdapter listAdapter = new locationLogListAdapter(this, R.layout.location_log_customlist, locationLogList);
        ListView listView = (ListView)findViewById(R.id.list1);
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
			
			regdate.setText(arSrc.get(position).getRegdate());
			phonenum.setText(arSrc.get(position).getPhonenum());
			location.setText(arSrc.get(position).getLocation());
			
			//마퀴 효과를 보여주기 위한 추가 
			regdate.setSelected(true);
			phonenum.setSelected(true);
			location.setSelected(true);
			
			return convertView;
		}
    }
}