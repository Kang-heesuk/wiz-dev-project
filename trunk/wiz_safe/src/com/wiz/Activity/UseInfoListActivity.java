package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UseInfoListActivity extends Activity {

	//등록된 자녀의 이름
	ArrayList<useInfoDetail> useInfoList = new ArrayList<useInfoDetail>();
	
	ArrayAdapter<useInfoDetail> useInfoAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useinfo_list);
        
        
        //이용안내 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getUseInfoList();

        useInfoListAdapter listAdapter = new useInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms와 연동된 공지사항을 불러온다.
    public void getUseInfoList() {
    	
    	String[][] tempHardCoding = {{"3G 데이터 사용 시","01/06","3G 데이터 사용 시 고객님의 데이터 요금제에 따라 별도 요금이 발생될 수 있으며, 자녀의 3G/Wi-Fi/GPS 수신 상태에 따라 실제 위치와 다를 수 있습니다."},{"조회불가","03/26","22222222222222222105484565"},{"안심존","04/26","3333333333333333333333333333331024882698"},{"포인트 충전","03/11","44444444444444444441084464664"},{"발자취 설정","01/26","5555555555555441084464664"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		useInfoDetail addUseInfoList = new useInfoDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		useInfoList.add(addUseInfoList);
    	}

    }
    
    
    class useInfoDetail {
    	private String useInfoTitle;
    	private String useInfoRegdate;
    	private String useInfoContent;
    	
    	public useInfoDetail (String useInfoTitle, String useInfoRegdate, String useInfoContent){
    		this.useInfoTitle = useInfoTitle;
    		this.useInfoRegdate = useInfoRegdate;
    		this.useInfoContent = useInfoContent;
    	}
    	private String getUseInfoTitle(){
			return useInfoTitle;
    	}
    	private String getUseInfoRegdate(){
			return useInfoRegdate;
    	}
    	private String getUseInfoContent(){
			return useInfoContent;
    	}
    }
    
    
    class useInfoListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<useInfoDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public useInfoListAdapter(Context context, int alayout, ArrayList<useInfoDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public useInfoDetail getItem(int position) {
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
			TextView useInfoTitle = (TextView)convertView.findViewById(R.id.useInfoTitle);
			TextView useInfoRegdate = (TextView)convertView.findViewById(R.id.useInfoRegdate);
			TextView useInfoContent = (TextView)convertView.findViewById(R.id.useInfoContent);
			final LinearLayout useInfoTitleArea = (LinearLayout)convertView.findViewById(R.id.useInfoTitleArea);
			final LinearLayout useInfoContentArea = (LinearLayout)convertView.findViewById(R.id.useInfoContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			useInfoTitle.setText(arSrc.get(position).getUseInfoTitle());
			useInfoRegdate.setText(arSrc.get(position).getUseInfoRegdate());
			useInfoContent.setText(arSrc.get(position).getUseInfoContent());
			
			//마퀴를 위한 추가
			useInfoTitle.setSelected(true);
			
			useInfoTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(useInfoContentArea.getVisibility() == 8){
							//gone 상태(8)이면
							useInfoContentArea.setVisibility(View.VISIBLE);
							
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible 상태(0)이면
							useInfoContentArea.setVisibility(View.GONE);
							
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}
					}
				}
			);
			return convertView;
		}
    }
}