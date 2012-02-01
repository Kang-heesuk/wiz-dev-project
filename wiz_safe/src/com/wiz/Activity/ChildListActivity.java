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
	
	//등록된 자녀의 이름
	ArrayList<childDetail> childRelationship = new ArrayList<childDetail>();
	
	//등록된 자녀의 구조체 전역변수 사용
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
        
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getMyChildren();
        
        child1 = childRelationship.get(0);
        child2 = childRelationship.get(1);
        child3 = childRelationship.get(2);
        
        Button btn_child1 = (Button)findViewById(R.id.btn_child1);
        if("".equals(child1.getChildName())){
        	btn_child1.setText("+ 자녀등록하기");
        }else{
        	if("Y".equals(child1.getChildRelation())){
        		btn_child1.setText(child1.getChildName() + "(완료)");
        	}else if("N".equals(child1.getChildRelation())){
        		btn_child1.setText(child1.getChildName() + "(대기)");
        	}else{
        		btn_child1.setText(child1.getChildName());
        	}
        }
        
        Button btn_child2 = (Button)findViewById(R.id.btn_child2);
        if("".equals(child2.getChildName())){
        	btn_child2.setText("+ 자녀등록하기");
        }else{
        	if("Y".equals(child2.getChildRelation())){
        		btn_child2.setText(child2.getChildName() + "(완료)");
        	}else if("N".equals(child2.getChildRelation())){
        		btn_child2.setText(child2.getChildName() + "(대기)");
        	}else{
        		btn_child2.setText(child2.getChildName());
        	}
        }
        
        
        Button btn_child3 = (Button)findViewById(R.id.btn_child3);
        if("".equals(child3.getChildName())){
        	btn_child3.setText("+ 자녀등록하기");
        }else{
        	if("Y".equals(child3.getChildRelation())){
        		btn_child3.setText(child3.getChildName() + "(완료)");
        	}else if("N".equals(child3.getChildRelation())){
        		btn_child3.setText(child3.getChildName() + "(대기)");
        	}else{
        		btn_child3.setText(child3.getChildName());
        	}
        }
        
        //버튼 액션 등록하기
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
			if("".equals(relationFlag)){	//자녀 등록하기
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}else if("Y".equals(relationFlag)){		//서로 수락한 상태
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}else if("N".equals(relationFlag)){		//수락 대기 상태
				intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
			}

			intent.putExtra("whereFlag", whereClick);
			startActivity(intent);
		}
    };
    
    
    //현재는 자녀를 3명 까지 등록가능하다고 하고 개발하는것임다.
    public void getMyChildren() {
    	//API 연동하여 현재 나에게 등록되어있는 자녀를 가져온다.
    	//연동 API 는 자녀가 있으면 자녀이름 과 인증여부를 가져오고 없다면 ""(공백)을 가져온다.
    	//요건 로직을 짜지 않았으므로 일단은 하드코딩한다.
    	
    	//가져온 값
    	String[][] tempHardCoding = {{"",""},{"정용진","Y"},{"반홍","N"}};
    	
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