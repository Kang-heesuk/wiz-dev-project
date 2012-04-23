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
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useinfo_list);
        
        
        //�̿�ȳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getUseInfoList();

        useInfoListAdapter listAdapter = new useInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    
    //�̿�ȳ� ������ �����´�.
    public void getUseInfoList() {
    	
    	ArrayList<String> title = new ArrayList<String>();
    	ArrayList<String> content = new ArrayList<String>();
    	
    	//�̿�ȳ� ����
    	title.add("�ڳ��? �θ��?");
    	content.add("�ڳ� : ��ġ�� ã�� ���� �����(����ġ������)\n�θ� : ��ġ�� ��ȸ�ϰ� ���� �����(��ġ������)");
    	
    	title.add("����ġã��/������/�Ƚ����̶�");
    	content.add("����ġã�� : ���� �ڳ��� ��ġ�� ã�� �� �ִ� �޴�\n������ : ������ �ð� ���� �ڳ��� ��ġ�� �����ÿ� ��ȸ/����/����/���� �ϴ� �޴�\n�Ƚ��� : ������ ������ ���� �ڳడ ��Ż �� �˷��ִ� �޴�");
    	
    	title.add("��ġ��뵿�Ƕ�?");
    	content.add("�ڳ��� ��ġ�� ã�� ���ؼ� �θ���� ��ġ��뵿�Ǹ� �ڳ࿡�� �޾ƾ� �մϴ�.\n���ǰ� ���� ��� ��ġ ��ȸ�� �� �� �����ϴ�");
    	
    	title.add("�̿��� �ȳ�");
    	content.add("����ġã�� : 1�� 3ȸ ����(�ʰ� �̿� �� 100����Ʈ/�Ǵ�)\n������ : 1�� 100���� ����(�ڵ�)\n�Ƚ��� : 1�� ����(�ʰ� �̿� �� 100����Ʈ/�Ǵ�)");
    	
    	title.add("��ġ��ȸ����뺸 ���ڶ�?");
    	content.add("����Ʈ�ڳ�Ƚ��� ����ġ������ ��ȣ �� �̿� � ���� ���� ��19������ �ǰ� �θ� ��ġ��ȸ �� ��� �ڳ࿡�� ��ġ��ȸ ��� ������ �뺸�ϰ� �ֽ��ϴ�.");
    	
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
		
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//�� ���� ����
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