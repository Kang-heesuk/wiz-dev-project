package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				Log.i("testTop","Delete =============");
			} 
		}); 
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getMyChildren();
        
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
        
        childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childList);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //����� �ڳฦ 3�� ���� ��ϰ����ϴٰ� �ϰ� �����ϴ°��Ӵ�.
    public void getMyChildren() {
    	//API �����Ͽ� ���� ������ ��ϵǾ��ִ� �ڳฦ �����´�.
    	//���� API �� �ڳడ ������ �ڳ��̸� �� �������θ� �������� ���ٸ� ""(����)�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ ��	[2] = 1 ���οϷ� / 2 ���δ�� / 3 ���ΰ���
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
			LinearLayout bottomBtnArea= (LinearLayout)convertView.findViewById(R.id.bottomBtnArea);
			ImageView img = (ImageView)convertView.findViewById(R.id.ePhoto);
			Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			TextView eName = (TextView)convertView.findViewById(R.id.eName);
			TextView info1 = (TextView)convertView.findViewById(R.id.info1);
			
			eName.setText(arSrc.get(position).getChildName());
			info1.setText(WizSafeUtil.setPhoneNum(arSrc.get(position).getChildPhone()));
			
			
			
			//�� ��ư �̸� �� ���� ���� ����
			bottomBtnArea.setVisibility(View.VISIBLE);
			if("01".equals(arSrc.get(position).getChildRelation())){
				btn_accept.setText("���δ����");
				btn_accept.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				btn_accept.setText("���οϷ�");
				btn_accept.setVisibility(View.VISIBLE);
			}else{
				btn_accept.setText("���ΰ���");
				btn_accept.setVisibility(View.VISIBLE);
			}
			
			
			//�޴����� �����ϱ� ������ ��� ��ư ���� ���� ����
			if(menuClickDelete){
				btn_accept.setText("�����ϱ�");
				btn_accept.setVisibility(View.VISIBLE);
			}
			
			//�� ��ư �׼� ����
			btn_accept.setOnClickListener(
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
							Intent intent = new Intent(ChildListActivity.this, ChildTraceAddActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							startActivity(intent);
						}
					}
				}
			);
			
			btn_safeZone.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if("02".equals(arSrc.get(pos).getChildRelation())){
							Intent intent = new Intent(ChildListActivity.this, ChildSafezoneAddActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildPhone());
							startActivity(intent);
						}
					}
				}
			);

			return convertView;
		}
    }
    
    
    //�޴�Ű�� �������� �����ϴ� �κ� menu xml �� child_list �κ��� �����´�.
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.child_list, menu);
		return true; 
    }
    
    //�޴����� �ش� ITEM �� �����Ͽ��� ��� �����ϴ� �κ�
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
   	  	case R.id.deleteMenu:
   	  		if(deleteMenuToggle == false){
   	  			deleteMenuToggle = true;
   	  		}else{
   	  			deleteMenuToggle = false;
   	  		}
   	  		//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
   	  		childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childList, deleteMenuToggle);
   	  		ListView listView = (ListView)findViewById(R.id.list);
        	listView.setAdapter(listAdapter);
        
   	  		return true;
    	}
    	return false;
    }
    
}