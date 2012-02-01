package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChildListActivity extends Activity {

	//��ϵ� �ڳ��� �̸�
	ArrayList<childDetail> childList = new ArrayList<childDetail>();
	
	ArrayAdapter<childDetail> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.child_list);
        
        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ChildListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        Button btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.i("testTop","Delete =============");
			} 
		});
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getMyChildren();
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childList);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //����� �ڳฦ 3�� ���� ��ϰ����ϴٰ� �ϰ� �����ϴ°��Ӵ�.
    public void getMyChildren() {
    	//API �����Ͽ� ���� ������ ��ϵǾ��ִ� �ڳฦ �����´�.
    	//���� API �� �ڳడ ������ �ڳ��̸� �� �������θ� �������� ���ٸ� ""(����)�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ ��
    	String[][] tempHardCoding = {{"","",""},{"������","Y","01024882698"},{"��ȫ","N","01084464664"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		childDetail addChildList = new childDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		childList.add(addChildList);
    	}
    	
    }
    
    class childDetail {
    	private String childName;
    	private String childRelation;
    	private String childPhone;
    	
    	public childDetail (String childName, String childRelation, String phonenum){
    		this.childName = childName;
    		this.childRelation = childRelation;
    		this.childPhone = phonenum;
    	}
    	private String getChildName(){
			return childName;
    	}
    	private String getChildRelation(){
			return childRelation;
    	}
    	private String getChildPhone(){
			return childPhone;
    	}
    }
    
    class childListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<childDetail> arSrc;
    	int layout;
    	
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public childDetail getItem(int position) {
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
			
			//����Ʈ �ȿ� �̹��� ��
			ImageView img = (ImageView)convertView.findViewById(R.id.ePhoto);
			
			LinearLayout bottomBtnArea= (LinearLayout)convertView.findViewById(R.id.bottomBtnArea);
			bottomBtnArea.setVisibility(View.VISIBLE);
			
			
			
			return convertView;
		}
    	
    }
    
}