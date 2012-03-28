package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PointLogActivity extends Activity {

	//��ϵ� �ڳ��� �̸�
	ArrayList<pointLogDetail> pointLogList = new ArrayList<pointLogDetail>();
	
	ArrayAdapter<pointLogDetail> pointLogAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_log_list);
        
        //�ش� ���� ����Ʈ ��� �α׸� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getPointLogList();

        pointLogListAdapter listAdapter = new pointLogListAdapter(this, R.layout.point_log_customlist, pointLogList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getPointLogList() {
    	
    	String[][] tempHardCoding = {{"01/12","02","1","100"},{"01/12","03","1","100"},{"01/12","04","1","100"},{"01/12","02","1","100"},{"01/12","02","1","100"},{"01/12","01","0","1000"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		pointLogDetail addPointLogList = new pointLogDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2], tempHardCoding[i][3]);
    		pointLogList.add(addPointLogList);
    	}

    }
    
    
    class pointLogDetail {
    	private String regdate;
    	private String part;
    	private String state;
    	private String point;
    	
    	public pointLogDetail (String regdate, String part, String state, String point){
    		this.regdate = regdate;
    		this.part = part;
    		this.state = state;
    		this.point = point;
    	}
    	private String getRegdate(){
			return regdate;
    	}
    	private String getPart(){
			return part;
    	}
    	private String getState(){
			return state;
    	}
    	private String getPoint(){
			return point;
    	}
    }
    
    
    class pointLogListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<pointLogDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public pointLogListAdapter(Context context, int alayout, ArrayList<pointLogDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public pointLogDetail getItem(int position) {
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
			TextView regdate = (TextView)convertView.findViewById(R.id.regdate);
			TextView part = (TextView)convertView.findViewById(R.id.part);
			TextView state = (TextView)convertView.findViewById(R.id.state);
			TextView point = (TextView)convertView.findViewById(R.id.point);
			
			regdate.setText(arSrc.get(position).getRegdate());
			if("01".equals(arSrc.get(position).getPart())){
				part.setText("����Ʈ");
			}else if("02".equals(arSrc.get(position).getPart())){
				part.setText("����ġ");
			}else if("03".equals(arSrc.get(position).getPart())){
				part.setText("������");
			}else if("04".equals(arSrc.get(position).getPart())){
				part.setText("�Ƚ���");
			}else{
				part.setText("-");
			}
			
			if("0".equals(arSrc.get(position).getState())){
				state.setText("����");
				point.setText("+"+arSrc.get(position).getPoint()+"P");
			}else if("1".equals(arSrc.get(position).getState())){
				state.setText("���");
				point.setText("-"+arSrc.get(position).getPoint()+"P");
			}else{
				state.setText("-");
				point.setText(arSrc.get(position).getPoint()+"P");
			}
			
			return convertView;
		}
    }
}