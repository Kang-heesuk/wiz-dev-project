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

public class FaqListActivity extends Activity {

	//��ϵ� �ڳ��� �̸�
	ArrayList<faqDetail> faqList = new ArrayList<faqDetail>();
	
	ArrayAdapter<faqDetail> faqAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_list);
        
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getFaqList();

        faqListAdapter listAdapter = new faqListAdapter(this, R.layout.faq_list_customlist, faqList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getFaqList() {
    	
    	String[][] tempHardCoding = {{"�ڳ� ����� ��� �ϳ���?","01/06","��ġ�� ã�� �� �ִ� �ڳడ �����ϴ�. �Ʒ� \"�ڳ����ϱ�\" �޴����� ����� �ּ���."},{"�θ� ����� ���� ���߾��.","03/26","22222222222222222105484565"},{"����Ʈ�� �����մϴ�.","04/26","3333333333333333333333333333331024882698"},{"�Ƚ��� ������ ���ؼ� ������ �ּ���.","03/11","44444444444444444441084464664"},{"���� ������̿���.","01/26","5555555555555441084464664"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		faqDetail addFaqList = new faqDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		faqList.add(addFaqList);
    	}

    }
    
    
    class faqDetail {
    	private String faqTitle;
    	private String faqRegdate;
    	private String faqContent;
    	
    	public faqDetail (String faqTitle, String faqRegdate, String faqContent){
    		this.faqTitle = faqTitle;
    		this.faqRegdate = faqRegdate;
    		this.faqContent = faqContent;
    	}
    	private String getFaqTitle(){
			return faqTitle;
    	}
    	private String getFaqRegdate(){
			return faqRegdate;
    	}
    	private String getFaqContent(){
			return faqContent;
    	}
    }
    
    
    class faqListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<faqDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public faqListAdapter(Context context, int alayout, ArrayList<faqDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public faqDetail getItem(int position) {
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
			TextView faqTitle = (TextView)convertView.findViewById(R.id.faqTitle);
			TextView faqRegdate = (TextView)convertView.findViewById(R.id.faqRegdate);
			TextView faqContent = (TextView)convertView.findViewById(R.id.faqContent);
			final LinearLayout faqTitleArea = (LinearLayout)convertView.findViewById(R.id.faqTitleArea);
			final LinearLayout faqContentArea = (LinearLayout)convertView.findViewById(R.id.faqContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			faqTitle.setText(arSrc.get(position).getFaqTitle());
			faqRegdate.setText(arSrc.get(position).getFaqRegdate());
			faqContent.setText(arSrc.get(position).getFaqContent());
			
			//������ ���� �߰�
			faqTitle.setSelected(true);
			
			faqTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(faqContentArea.getVisibility() == 8){
							//gone ����(8)�̸�
							faqContentArea.setVisibility(View.VISIBLE);
							
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible ����(0)�̸�
							faqContentArea.setVisibility(View.GONE);
							
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