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

	//��ϵ� �ڳ��� �̸�
	ArrayList<noticeDetail> noticeList = new ArrayList<noticeDetail>();
	
	ArrayAdapter<noticeDetail> noticeAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getNoticeList();

        noticeListAdapter listAdapter = new noticeListAdapter(this, R.layout.notice_list_customlist, noticeList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getNoticeList() {
    	
    	String[][] tempHardCoding = {{"��������1","01/26","�ٹٲپ�~~~\n�ٹٲپ�~~~\n�ٹٲپ�~~~\n�ٹٲپ�~~~\n�ٹٲپ�~~~\n�ٹٲپ�~~~\n1231111111111111111010123111111111111111101012311111111111111110101231111\n111111111111010123111111111111111101012311111111111111110101231111111111111111010123111\n111111111111101012311111111\n1111111101012311111111\n111111101012345678"},{"��������2","03/26","22222222222222222105484565"},{"�� �������׵� �������� �ľƾ�3","04/26","3333333333333333333333333333331024882698"},{"��������4","03/11","44444444444444444441084464664"},{"��������5","01/26","5555555555555441084464664"},{"��������6","01/26","66666666666666666684464664"},{"��������7","01/26","777777777777777777777084464664"}};
    	
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
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
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
			
			//�� ���� ����
			TextView noticeTitle = (TextView)convertView.findViewById(R.id.noticeTitle);
			TextView noticeRegdate = (TextView)convertView.findViewById(R.id.noticeRegdate);
			TextView noticeContent = (TextView)convertView.findViewById(R.id.noticeContent);
			final LinearLayout noticeTitleArea = (LinearLayout)convertView.findViewById(R.id.noticeTitleArea);
			final LinearLayout noticeContentArea = (LinearLayout)convertView.findViewById(R.id.noticeContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			noticeTitle.setText(arSrc.get(position).getNoticeTitle());
			noticeRegdate.setText(arSrc.get(position).getNoticeRegdate());
			noticeContent.setText(arSrc.get(position).getNoticeContent());
			
			//������ ���� �߰�
			noticeTitle.setSelected(true);
			
			noticeTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(noticeContentArea.getVisibility() == 8){
							//gone ����(8)�̸�
							noticeContentArea.setVisibility(View.VISIBLE);
							
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible ����(0)�̸�
							noticeContentArea.setVisibility(View.GONE);
							
							//title�κ� background �̹��� ����
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