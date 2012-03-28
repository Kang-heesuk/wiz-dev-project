package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.Activity.ChildListActivity.childDetail;
import com.wiz.Activity.ChildListActivity.childListAdapter;
import com.wiz.util.WizSafeUtil;

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

public class ParentListActivity extends Activity {
	
	//�޴���ư���� DELETE ��ư�� �������� �����ϱ� or ����� ���� �����ִ� ��ۺ���
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;
	
	
	//��ϵ� �θ𸮽�Ʈ�� ����Ʈ
	ArrayList<ParentDetail> parentListArr = new ArrayList<ParentDetail>();
	
	ArrayAdapter<ParentDetail> parentAdapter;
    
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.parent_list);
        
        getParentList();
        
      //����Ʈ�� �����ϴ��� �ƴϳĿ� ���� ���̴� ���̾ƿ��� �޶�����.
        if(parentListArr.size() <= 0){
        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
        	bgArea.setBackgroundResource(R.drawable.bg_parents1);
        	visibleArea1.setVisibility(View.GONE);
        	visibleArea2.setVisibility(View.VISIBLE);
        }
        
        parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
        ListView listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.parent_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(listAdapter);
        
        //�θ����ϱ�(����Ʈ �ִ°��)
        findViewById(R.id.btn_parent).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
        //�θ����ϱ�(����Ʈ ���°��)
        findViewById(R.id.btn_noElements).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
					startActivity(intent);
				}
			}
		);
        
        
      //�޴�Ű ��������� �ϴܿ� ������ �޴����� �׼� ����
        //1. ����
        if(parentListArr.size() <= 0){
        	findViewById(R.id.deleteButton).setBackgroundResource(R.drawable.b_menub_1_off);
        }
        findViewById(R.id.deleteButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					if(parentListArr.size() > 0){
						bottomMenuToggle();
						onClickdeleteButtonAction();
					}else{
						
					}
				}
			}
		);
        
        //2. �θ��߰�
        findViewById(R.id.childAdditionButton).setOnClickListener(
			new Button.OnClickListener(){
				public void onClick(View v) {
					bottomMenuToggle();
					int whereFlag = parentListArr.size() + 1;
					Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
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
					Intent intent = new Intent(ParentListActivity.this, UseInfoListActivity.class);
					startActivity(intent);
				}
			}
		);
        
    } 
	
    
    
    
	protected void onRestart() {

		super.onRestart();
		/*
		//body ����
		//����Ʈ�� �ʱ�ȭ �ϰ� ������ ���Ͽ� �ٽ� �����´�.
		parentList.clear();
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        //������ ����Ͽ� ����Ʈ ������ �����´�.        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
		getParentList();
        
        //����� ����
        parentListAdapter = new ParentListAdapter(this, R.layout.safe_list, parentList);
        ListView listView = (ListView)findViewById(R.id.list);
        //�����ϸ� ȭ�鿡 listview�� �����ش�.
        listView.setAdapter(parentListAdapter);
        */
	}
	
	//�޴�Ű ��������� �ϴ��� �޴������� ���̰� �Ⱥ��̰� ���� ���̾ƿ� ����
    public void bottomMenuToggle(){
    	LinearLayout bodyArea = (LinearLayout)findViewById(R.id.bodyArea);
		LinearLayout bottomMenuArea = (LinearLayout)findViewById(R.id.bottomMenuArea);
		LinearLayout subArea1 = (LinearLayout)findViewById(R.id.subArea1);
		LinearLayout subArea2 = (LinearLayout)findViewById(R.id.subArea2);
		LinearLayout subArea3 = (LinearLayout)findViewById(R.id.subArea3);
		bodyArea.setEnabled(true);
		bottomMenuArea.setEnabled(true);
		subArea1.setEnabled(true);
		subArea2.setEnabled(true);
		subArea3.setEnabled(true);
		LinearLayout.LayoutParams bodyAreaParams = (LinearLayout.LayoutParams)bodyArea.getLayoutParams();
		LinearLayout.LayoutParams bottomAreaParams = (LinearLayout.LayoutParams)bottomMenuArea.getLayoutParams();
		LinearLayout.LayoutParams subArea1Params = (LinearLayout.LayoutParams)subArea1.getLayoutParams();
		LinearLayout.LayoutParams subArea2Params = (LinearLayout.LayoutParams)subArea2.getLayoutParams();
		LinearLayout.LayoutParams subArea3Params = (LinearLayout.LayoutParams)subArea3.getLayoutParams();
		if(bottomMenuArea.getVisibility() == View.VISIBLE){
			bottomAreaIsOn = false;
			bodyAreaParams.weight = 93;
			bottomAreaParams.weight = 0;
			subArea1Params.weight = 28;
			subArea2Params.weight = 16;
			subArea3Params.weight = 56;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.GONE);
		}else{
			bottomAreaIsOn = true;
			bodyAreaParams.weight = 77;
			bottomAreaParams.weight = 16;
			subArea1Params.weight = (float)33.8;
			subArea2Params.weight = (float)19.2;
			subArea3Params.weight = 47;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.VISIBLE);
		}
    }
	
	//�޴�Ű ��������� ���̾ƿ� ����(�ϴ� �ٰ� ���´�. �ϴ� �ٰ� �����鼭 ����� weight ���� �������ȴ�.)
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
    	if(bottomAreaIsOn){
    		if(keyCode == 4){
        		bottomMenuToggle();    		
        		return true;
        	}
    	}
    	
    	if(keyCode == 82){
    		bottomMenuToggle();    		
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

	//�θ𸮽�Ʈ�� �������� ���� - �����ϸ� ����Ͽ� ����Ʈ�� �����ͼ�  arraylist�� ����.
	public void getParentList(){
		//API �����Ͽ� ���� ������ ��ϵǾ� �ִ� �θ��� ����Ʈ�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ �� [3] = �θ𸮽�Ʈ������ ���� ����
    	String[][] tempHardCoding = {{"�ҵ���","01029325100","02"},{"��ź��","01028283669","01"}};
    	
    	if(tempHardCoding != null){
	    	for(int i = 0 ; i < tempHardCoding.length ; i++){
	    		ParentDetail addParentList = new ParentDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
	    		parentListArr.add(addParentList);
	    	}
    	}
	}
	
	class parentListAdapter extends BaseAdapter {

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ParentDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//���� ��ư�� ������ Ŀ���� ����Ʈ �並 �����ٶ�
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ParentDetail getItem(int position) {
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
			TextView textArea1 = (TextView)convertView.findViewById(R.id.textArea1);
			TextView textArea2 = (TextView)convertView.findViewById(R.id.textArea2);
			Button imgState = (Button)convertView.findViewById(R.id.imgState);
			
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
			
			if("01".equals(arSrc.get(pos).getState())){
				imgState.setBackgroundResource(R.drawable.icon_6);
			}else if("02".equals(arSrc.get(pos).getState())){
				imgState.setBackgroundResource(R.drawable.icon_7);
			}
			
			//�޴����� �����ϱ� ������ ��� ��ư ���� ���� ����
			if(menuClickDelete){
				imgState.setBackgroundResource(R.drawable.icon_del_selector);
				imgState.setVisibility(View.VISIBLE);
			}
			
			//�� ��ư �׼� ����
			imgState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//�����ϱ� ��ư�� Ŭ���Ͽ��� ���
						if(menuClickDelete){
							Toast.makeText(ParentListActivity.this, "�����ϱ� API ���", Toast.LENGTH_SHORT).show();
							//��Ƽ��Ƽ �����
							Intent intent = getIntent();
							finish();
							startActivity(intent);
						}
					}
				}
			);
			
			textArea1.setText(arSrc.get(pos).getName());
			textArea2.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(pos).getPhoneNo()) + ")");

			return convertView;
		}
    }
	
	
	//custom list data �� inner class �� ����
	class ParentDetail {       
		
		private String name;
	    private String phoneNo;
	    private String relationState;
	    
	    public ParentDetail(String _name, String _pn, String _relationState) {
	        this.name = _name;
	        this.phoneNo = _pn;
	        this.relationState = _relationState;
	    }
	    
	    public String getName() {
	        return name;
	    }

	    public String getPhoneNo() {
	        return phoneNo;
	    }
	    
	    public String getState() {
	        return relationState;
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
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
	
}