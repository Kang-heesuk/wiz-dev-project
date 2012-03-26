package com.wiz.Activity;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wiz.util.WizSafeUtil;

public class ChildListActivity extends Activity {
	
	//�޴���ư���� DELETE ��ư�� �������� �����ϱ� or ���δ���� ���� �����ִ� ��ۺ���
	boolean deleteMenuToggle = false;
	
	//��ϵ� �ڳ��� �̸�
	ArrayList<childDetail> childList = new ArrayList<childDetail>();
	
	ArrayAdapter<childDetail> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.child_list);
               
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getMyChildren();
       
        childListAdapter listAdapter = new childListAdapter(this, R.layout.child_list_customlist, childList);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
        
        //�ڳ����ϱ� ��ư�׼�
        findViewById(R.id.btn_addChild).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					int whereFlag = childList.size() + 1;
					Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
					intent.putExtra("whereFlag", whereFlag);
					startActivity(intent);
				}
			}
		);
        
        
        
        //�޴�Ű ��������� �ϴܿ� ������ �޴����� �׼� ����
        //1. ����
        findViewById(R.id.deleteButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					onClickdeleteButtonAction();
				}
			}
		);
        
        //2. �ڳ��߰�
        findViewById(R.id.childAdditionButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					int whereFlag = childList.size() + 1;
					Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
					intent.putExtra("whereFlag", whereFlag);
					startActivity(intent);
				}
			}
		);
        
        //2. �̿�ȳ�
        findViewById(R.id.useInfoButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					Intent intent = new Intent(ChildListActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
		);
        
    }
    
    //�޴�Ű ��������� �ϴ��� �޴������� ���̰� �Ⱥ��̰� ���� ���̾ƿ� ����
    public void bottomMenuToggle(){
    	LinearLayout listArea = (LinearLayout)findViewById(R.id.listArea);
		LinearLayout bottomMenuArea = (LinearLayout)findViewById(R.id.bottomMenuArea);
		listArea.setEnabled(true);
		bottomMenuArea.setEnabled(true);
		LinearLayout.LayoutParams listAreaParams = (LinearLayout.LayoutParams)listArea.getLayoutParams();
		LinearLayout.LayoutParams bottomAreaParams = (LinearLayout.LayoutParams)bottomMenuArea.getLayoutParams();
		if(bottomMenuArea.getVisibility() == View.VISIBLE){
			listAreaParams.weight = 93;
			bottomAreaParams.weight = 0;
			listArea.setLayoutParams(listAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			bottomMenuArea.setVisibility(View.GONE);
		}else{
			listAreaParams.weight = 77;
			bottomAreaParams.weight = 16;
			listArea.setLayoutParams(listAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			bottomMenuArea.setVisibility(View.VISIBLE);
		}
    }
   
    //�޴�Ű ��������� ���̾ƿ� ����(�ϴ� �ٰ� ���´�. �ϴ� �ٰ� �����鼭 ����� weight ���� �������ȴ�.)
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
    	if(keyCode == 82){
    		bottomMenuToggle();    		
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    
    public void getMyChildren() {
    	//API �����Ͽ� ���� ������ ��ϵǾ��ִ� �ڳฦ �����´�.
    	//���� API �� �ڳడ ������ �ڳ��̸� �� �������θ� �������� ���ٸ� ""(����)�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ ��	[2] = 01 ���οϷ� / 02 ���δ�� / 03 ���ΰ���
    	String[][] tempHardCoding = {{"������","01","01012345678"},{"�ɺ���","02","0105484565"},{"������","03","01024882698"},{"��ȫ","01","01084464664"}};
    	
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

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<childDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//���� ��ư�� ������ Ŀ���� ����Ʈ �並 �����ٶ�
    	public childListAdapter(Context context, int alayout, ArrayList<childDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
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
			
			//�� ���� ����
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			Button btnChildState = (Button)convertView.findViewById(R.id.btnChildState);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			TextView childName = (TextView)convertView.findViewById(R.id.childName);
			TextView childPhonenum = (TextView)convertView.findViewById(R.id.childPhonenum);
			
			childName.setText(arSrc.get(position).getChildName());
			childPhonenum.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(position).getChildPhone()) + ")");
			
			//Ŀ���� ����Ʈ �� ���� �̹��� ����
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
			
			//�� ��ư �̸� �� ���� ���� ����
			if("01".equals(arSrc.get(position).getChildRelation())){
				btnChildState.setBackgroundResource(R.drawable.icon_3);
				btnChildState.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				btnChildState.setBackgroundResource(R.drawable.icon_1);
				btnChildState.setVisibility(View.VISIBLE);
			}else{
				btnChildState.setBackgroundResource(R.drawable.icon_2);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			
			//�޴����� �����ϱ� ������ ��� ��ư ���� ���� ����
			if(menuClickDelete){
				btnChildState.setBackgroundResource(R.drawable.icon_del_selector);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			//�� ��ư �׼� ����
			btnChildState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//�����ϱ� ��ư�� Ŭ���Ͽ��� ���
						if(menuClickDelete){
							Toast.makeText(ChildListActivity.this, "�����ϱ� API ���", Toast.LENGTH_SHORT).show();
							
							//��Ƽ��Ƽ �����
							Intent intent = getIntent();
							finish();
							startActivity(intent);
							
						}else{	//�� ���� ���(�Ϲ����� ���)
							if("01".equals(arSrc.get(pos).getChildRelation())){
								Intent intent = new Intent(ChildListActivity.this, ChildAddCompleteActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
								startActivity(intent);
							}
						}
					}
				}
			);
			
			btn_nowLocation.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildLocationViewActivity.class);
							startActivity(intent);
						}
					}
				}
			);
			
			btn_history.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildTraceListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				}
			);
			
			btn_safeZone.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildSafezoneListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				}
			);

			return convertView;
		}
    }
    
    
    //�޴����� ���� ��ư�� ������ ��� �׼�
    public void onClickdeleteButtonAction(){
  		if(deleteMenuToggle == false){
  			deleteMenuToggle = true;
  		}else{
  			deleteMenuToggle = false;
  		}
  		
  		//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		childListAdapter listAdapter = new childListAdapter(this, R.layout.child_list_customlist, childList, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
}