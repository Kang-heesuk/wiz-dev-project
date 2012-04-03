package com.wiz.Activity;

import java.util.ArrayList;
import com.wiz.util.WizSafeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
        
        //리스트가 존재하느냐 아니냐에 따라서 보이는 레이아웃이 달라진다.
        if(safetyList.size() <= 0){
        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
        	bgArea.setBackgroundResource(R.drawable.bg_safezone1);
        	visibleArea1.setVisibility(View.GONE);
        	visibleArea2.setVisibility(View.VISIBLE);
        }
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.child_safezone_list_customlist, safetyList);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
        //안심존 등록 버튼액션(리스트 있는경우)
        findViewById(R.id.btn_addChild).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
        //안심존 등록 버튼액션(리스트 없는경우)
        findViewById(R.id.btn_noElements).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
	}
	
    public void getSafeZoneList() {
    	//API 연동하여 현재 나에게 등록되어 있는 안심존 리스트를 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값	[0] = 시퀀스 넘버이다.  [3] = 안심존에 진입한 경우 날짜 + 시간이 들어오고, 아닌경우 공백이 들어온다.
    	String[][] tempHardCoding = {{"2",phonenum,"서울시특별시 송파구 가락동 78","2012030216"},{"1",phonenum,"서울특별시 송파구 가락동 81-5","0"}};
    	
    	if(tempHardCoding != null){
	    	for(int i = 0 ; i < tempHardCoding.length ; i++){
	    		safeList addSafeList = new safeList(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2], tempHardCoding[i][3]);
	    		safetyList.add(addSafeList);
	    	}
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
			String textView2 = arSrc.get(position).getsafeAlarmDate();
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
				textArea2.setText(year + "년 " + month + "월 " + day + "일 " + WizSafeUtil.timeConvertFromNumberToString(time) + " 진입");
				textArea2.setVisibility(View.VISIBLE);
			}
			
			btn_modify.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
						intent.putExtra("phonenum", phonenum);
						intent.putExtra("childName", childName);
						intent.putExtra("safeZoneCode", arSrc.get(pos).getSafeZoneCode());
						startActivity(intent);
					}
				}
			);
			
			btn_delete.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildSafezoneListActivity.this);
						submitAlert.setTitle("안심존삭제");
						submitAlert.setMessage("안심존 설정을 삭제 하시겠습니까?\n설정내용 : "+ arSrc.get(pos).getSafeAddress());
						submitAlert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Log.i("traceChild","==========통신시작!");
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
