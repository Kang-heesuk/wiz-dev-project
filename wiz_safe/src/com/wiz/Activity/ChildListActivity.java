package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChildListActivity extends Activity {
	
	//��ϵ� �ڳ��� �̸�
	ArrayList<childDetail> childRelationship = new ArrayList<childDetail>();
	
	//��ϵ� �ڳ��� ����ü �������� ���
	childDetail child1;
    childDetail child2;
    childDetail child3;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.child_list);
        
        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ChildListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText(R.string.title_child);
        
        Button btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.i("testTop","Delete =============");
			} 
		});
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getMyChildren();
        
        child1 = childRelationship.get(0);
        child2 = childRelationship.get(1);
        child3 = childRelationship.get(2);
        
        Button btn_child1 = (Button)findViewById(R.id.btn_child1);
        if("".equals(child1.getChildName())){
        	btn_child1.setText("+ �ڳ����ϱ�");
        }else{
        	if("Y".equals(child1.getChildRelation())){
        		btn_child1.setText(child1.getChildName() + "(�Ϸ�)");
        	}else if("N".equals(child1.getChildRelation())){
        		btn_child1.setText(child1.getChildName() + "(���)");
        	}else{
        		btn_child1.setText(child1.getChildName());
        	}
        }
        
        Button btn_child2 = (Button)findViewById(R.id.btn_child2);
        if("".equals(child2.getChildName())){
        	btn_child2.setText("+ �ڳ����ϱ�");
        }else{
        	if("Y".equals(child2.getChildRelation())){
        		btn_child2.setText(child2.getChildName() + "(�Ϸ�)");
        	}else if("N".equals(child2.getChildRelation())){
        		btn_child2.setText(child2.getChildName() + "(���)");
        	}else{
        		btn_child2.setText(child2.getChildName());
        	}
        }
        
        
        Button btn_child3 = (Button)findViewById(R.id.btn_child3);
        if("".equals(child3.getChildName())){
        	btn_child3.setText("+ �ڳ����ϱ�");
        }else{
        	if("Y".equals(child3.getChildRelation())){
        		btn_child3.setText(child3.getChildName() + "(�Ϸ�)");
        	}else if("N".equals(child3.getChildRelation())){
        		btn_child3.setText(child3.getChildName() + "(���)");
        	}else{
        		btn_child3.setText(child3.getChildName());
        	}
        }
        
        //��ư �׼� ����ϱ�
        btn_child1.setOnClickListener(mClickListener);
        btn_child2.setOnClickListener(mClickListener); 
        btn_child3.setOnClickListener(mClickListener); 
    }
    
    Button.OnClickListener mClickListener = new Button.OnClickListener(){
    	
		public void onClick(View v) {
			int whereClick = -1;
			String relationFlag = "";
			switch(v.getId()){
			case R.id.btn_child1:
				whereClick = 1;
				relationFlag = child1.getChildRelation();
				break;
			case R.id.btn_child2:
				whereClick = 2;
				relationFlag = child2.getChildRelation();
				break;
			case R.id.btn_child3:
				whereClick = 3;
				relationFlag = child3.getChildRelation();
				break;
			}
			
			Intent intent = null;
			if("".equals(relationFlag)){	//�ڳ� ����ϱ�
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}else if("Y".equals(relationFlag)){		//���� ������ ����
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}else if("N".equals(relationFlag)){		//���� ��� ����
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}

			intent.putExtra("whereFlag", whereClick);
			startActivity(intent);
		}
    };
    
    
    //����� �ڳฦ 3�� ���� ��ϰ����ϴٰ� �ϰ� �����ϴ°��Ӵ�.
    public void getMyChildren() {
    	//API �����Ͽ� ���� ������ ��ϵǾ��ִ� �ڳฦ �����´�.
    	//���� API �� �ڳడ ������ �ڳ��̸� �� �������θ� �������� ���ٸ� ""(����)�� �����´�.
    	//��� ������ ¥�� �ʾ����Ƿ� �ϴ��� �ϵ��ڵ��Ѵ�.
    	
    	//������ ��
    	String[][] tempHardCoding = {{"",""},{"������","Y"},{"��ȫ","N"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		childDetail addChildList = new childDetail(tempHardCoding[i][0],tempHardCoding[i][1]);
    		childRelationship.add(addChildList);
    	}
    	
    }
    
    class childDetail {
    	private String childName;
    	private String childRelation;
    	
    	public childDetail (String childName, String childRelation){
    		this.childName = childName;
    		this.childRelation = childRelation;
    	}
    	private String getChildName(){
			return childName;
    	}
    	private String getChildRelation(){
			return childRelation;
    	}
    }
    
}