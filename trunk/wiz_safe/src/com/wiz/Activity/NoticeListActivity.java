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

public class NoticeListActivity extends Activity {

	//등록된 자녀의 이름
	ArrayList<noticeDetail> noticeList = new ArrayList<noticeDetail>();
	
	ArrayAdapter<noticeDetail> noticeAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        
        
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getNoticeList();

        noticeListAdapter listAdapter = new noticeListAdapter(this, R.layout.notice_list_customlist, noticeList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms와 연동된 공지사항을 불러온다.
    public void getNoticeList() {
    	
    	String[][] tempHardCoding = {{"공지사항1","01/26","줄바꾸어~~~\n줄바꾸어~~~\n줄바꾸어~~~\n줄바꾸어~~~\n줄바꾸어~~~\n줄바꾸어~~~\n1231111111111111111010123111111111111111101012311111111111111110101231111\n111111111111010123111111111111111101012311111111111111110101231111111111111111010123111\n111111111111101012311111111\n1111111101012311111111\n111111101012345678"},{"공지사항2","03/26","22222222222222222105484565"},{"긴 공지사항도 문제없삼 후아아3","04/26","3333333333333333333333333333331024882698"},{"공지사항4","03/11","44444444444444444441084464664"},{"공지사항5","01/26","5555555555555441084464664"},{"공지사항6","01/26","66666666666666666684464664"},{"공지사항7","01/26","777777777777777777777084464664"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		noticeDetail addNoticeList = new noticeDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		noticeList.add(addNoticeList);
    	}

    }
    
    
    class noticeDetail {
    	private String noticeTitle;
    	private String noticeRegdate;
    	private String noticeContent;
    	
    	public noticeDetail (String noticeTitle, String noticeRegdate, String noticeContent){
    		this.noticeTitle = noticeTitle;
    		this.noticeRegdate = noticeRegdate;
    		this.noticeContent = noticeContent;
    	}
    	private String getNoticeTitle(){
			return noticeTitle;
    	}
    	private String getNoticeRegdate(){
			return noticeRegdate;
    	}
    	private String getNoticeContent(){
			return noticeContent;
    	}
    }
    
    
    class noticeListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<noticeDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public noticeListAdapter(Context context, int alayout, ArrayList<noticeDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public noticeDetail getItem(int position) {
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
			TextView noticeTitle = (TextView)convertView.findViewById(R.id.noticeTitle);
			TextView noticeRegdate = (TextView)convertView.findViewById(R.id.noticeRegdate);
			TextView noticeContent = (TextView)convertView.findViewById(R.id.noticeContent);
			final LinearLayout noticeTitleArea = (LinearLayout)convertView.findViewById(R.id.noticeTitleArea);
			final LinearLayout noticeContentArea = (LinearLayout)convertView.findViewById(R.id.noticeContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			noticeTitle.setText(arSrc.get(position).getNoticeTitle());
			noticeRegdate.setText(arSrc.get(position).getNoticeRegdate());
			noticeContent.setText(arSrc.get(position).getNoticeContent());
			
			//마퀴를 위한 추가
			noticeTitle.setSelected(true);
			
			noticeTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(noticeContentArea.getVisibility() == 8){
							//gone 상태(8)이면
							noticeContentArea.setVisibility(View.VISIBLE);
							
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible 상태(0)이면
							noticeContentArea.setVisibility(View.GONE);
							
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