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
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useinfo_list);
        
        
        //이용안내 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getUseInfoList();

        useInfoListAdapter listAdapter = new useInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    
    //이용안내 내용을 가져온다.
    public void getUseInfoList() {
    	
    	ArrayList<String> title = new ArrayList<String>();
    	ArrayList<String> content = new ArrayList<String>();
    	
    	//이용안내 내용
    	title.add("자녀란? 부모란?");
    	content.add("자녀 : 위치를 찾고 싶은 사용자(피위치추적자)\n부모 : 위치를 조회하고 싶은 사용자(위치추적자)");
    	
    	title.add("현위치찾기/발자취/안심존이란");
    	content.add("현위치찾기 : 현재 자녀의 위치를 찾을 수 있는 메뉴\n발자취 : 지정된 시간 동안 자녀의 위치를 매정시에 조회/설정/변경/삭제 하는 메뉴\n안심존 : 지정된 지역을 설정 자녀가 이탈 시 알려주는 메뉴");
    	
    	title.add("위치허용동의란?");
    	content.add("자녀의 위치를 찾기 위해서 부모님은 위치허용동의를 자녀에게 받아야 합니다.\n동의가 없을 경우 위치 조회를 할 수 없습니다");
    	
    	title.add("이용요금 안내");
    	content.add("현위치찾기 : 1일 3회 무료(초과 이용 시 100포인트/건당)\n발자취 : 1일 100포인 소진(자동)\n안심존 : 1건 무료(초과 이용 시 100포인트/건당)");
    	
    	title.add("위치조회즉시통보 문자란?");
    	content.add("스마트자녀안심은 “위치정보의 보호 및 이용 등에 관한 법률 제19조”에 의거 부모가 위치조회 시 즉시 자녀에게 위치조회 결과 내용을 통보하고 있습니다.");
    	
    	useInfoList = new ArrayList<useInfoDetail>();
    	for(int i = 0 ; i < title.size() ; i++){
    		useInfoDetail addUseInfoList = new useInfoDetail(title.get(i), content.get(i));
    		useInfoList.add(addUseInfoList);
    	}
    }
    
    
    class useInfoDetail {
    	private String useInfoTitle;
    	private String useInfoContent;
    	
    	public useInfoDetail (String useInfoTitle,  String useInfoContent){
    		this.useInfoTitle = useInfoTitle;
    		this.useInfoContent = useInfoContent;
    	}
    	private String getUseInfoTitle(){
			return useInfoTitle;
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
		
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//각 위젯 정의
			TextView useInfoTitle = (TextView)convertView.findViewById(R.id.useInfoTitle);
			TextView useInfoContent = (TextView)convertView.findViewById(R.id.useInfoContent);
			final LinearLayout useInfoTitleArea = (LinearLayout)convertView.findViewById(R.id.useInfoTitleArea);
			final LinearLayout useInfoContentArea = (LinearLayout)convertView.findViewById(R.id.useInfoContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			useInfoTitle.setText(arSrc.get(position).getUseInfoTitle());
			useInfoContent.setText(arSrc.get(position).getUseInfoContent());
			
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