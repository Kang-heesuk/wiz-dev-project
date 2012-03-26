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

	//��ϵ� �ڳ��� �̸�
	ArrayList<useInfoDetail> useInfoList = new ArrayList<useInfoDetail>();
	
	ArrayAdapter<useInfoDetail> useInfoAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useinfo_list);
        
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getUseInfoList();

        useInfoListAdapter listAdapter = new useInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getUseInfoList() {
    	
    	String[][] tempHardCoding = {{"3G ������ ��� ��","01/06","3G ������ ��� �� �������� ������ ������� ���� ���� ����� �߻��� �� ������, �ڳ��� 3G/Wi-Fi/GPS ���� ���¿� ���� ���� ��ġ�� �ٸ� �� �ֽ��ϴ�."},{"��ȸ�Ұ�","03/26","22222222222222222105484565"},{"�Ƚ���","04/26","3333333333333333333333333333331024882698"},{"����Ʈ ����","03/11","44444444444444444441084464664"},{"������ ����","01/26","5555555555555441084464664"}};
    	
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
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
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
			
			//�� ���� ����
			TextView useInfoTitle = (TextView)convertView.findViewById(R.id.useInfoTitle);
			TextView useInfoRegdate = (TextView)convertView.findViewById(R.id.useInfoRegdate);
			TextView useInfoContent = (TextView)convertView.findViewById(R.id.useInfoContent);
			final LinearLayout useInfoTitleArea = (LinearLayout)convertView.findViewById(R.id.useInfoTitleArea);
			final LinearLayout useInfoContentArea = (LinearLayout)convertView.findViewById(R.id.useInfoContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			useInfoTitle.setText(arSrc.get(position).getUseInfoTitle());
			useInfoRegdate.setText(arSrc.get(position).getUseInfoRegdate());
			useInfoContent.setText(arSrc.get(position).getUseInfoContent());
			
			useInfoTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(useInfoContentArea.getVisibility() == 8){
							//gone ����(8)�̸�
							useInfoContentArea.setVisibility(View.VISIBLE);
							
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible ����(0)�̸�
							useInfoContentArea.setVisibility(View.GONE);
							
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