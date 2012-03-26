package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.Activity.ChildListActivity.childDetail;
import com.wiz.Activity.ChildListActivity.childListAdapter;
import com.wiz.util.WizSafeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChildSafezoneListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	
	//��ϵ� �Ƚ��� ����Ʈ
	ArrayList<safeList> safetyList = new ArrayList<safeList>();
	
	ArrayAdapter<safeList> childAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);

		//�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");		
        childName = intent.getStringExtra("childName");
        
        //��ϵ� �Ƚ��� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getSafeZoneList();
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.child_safezone_list_customlist, safetyList);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
	}
	
    public void getSafeZoneList() {
    	//API �����Ͽ� ���� ������ ��ϵǾ� �ִ� �Ƚ��� ����Ʈ�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ ��	[1] = ������ �ѹ��̴�.  [3] = �Ƚ����� ������ ��� ��¥ + �ð��� ������, �ƴѰ�� ������ ���´�.
    	String[][] tempHardCoding = {{"2",phonenum,"�����Ư���� ���ı� ������ 78","2012032616"},{"1",phonenum,"����Ư���� ���ı� ������ 81-5","0"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		safeList addSafeList = new safeList(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2], tempHardCoding[i][3]);
    		safetyList.add(addSafeList);
    	}
    }
	
    class childListAdapter extends BaseAdapter {

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<safeList> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
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
