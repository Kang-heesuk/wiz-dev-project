package com.wiz.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChildTraceDetailListActivity extends Activity {

	//������ ����Ʈ
	ArrayList<String> childLogList = new ArrayList<String>();
	ArrayAdapter<String> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.child_trace_detail_list);
       
        //������� body ����
        
        //1���ϰ� ������ ����Ʈ�� �����´�. �����ϸ� arrayList�� ����.
        getLogList();
   
        if(childLogList.size() > 0){
			childListAdapter listAdapter = new childListAdapter(this, R.layout.child_trace_detail_list_customlist, childLogList);
			ListView listView = (ListView)findViewById(R.id.list1);
			listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(new infoListSelection());
        }
    }
    
    
    public void getLogList() {
    			
    	//������ ������� 20120131 �̶�� �ϵ��ڵ��ϰ� �۾�����
    	String regdate = String.valueOf(20110202);
    	
    	for(int i=0; i>=-6; i--){	//�޷¿����� ���� -������ 7ȸ �ݺ��ϵ��� �ۼ�
    		//���� �ð����� ���� ������ �Ⱓ���� ����Ʈ�� �����Ѵ�.
        	GregorianCalendar calendar = new GregorianCalendar();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	
    		//���ó�¥���� i���� ���� ���ؼ� ����� ���ĸ� ����Ʈ�� ���Խ�Ų��.
        	calendar.add(GregorianCalendar.DAY_OF_MONTH, i);
        	String listdate = sdf.format(calendar.getTime());
        	
        	int i_regdate = Integer.parseInt(regdate);
        	int i_listdate = Integer.parseInt(listdate);
        	
    		if(i_listdate > i_regdate){
    			Date tmpDate = calendar.getTime();
    			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy�� MM�� dd��");
    			
    			childLogList.add((-i), sdf2.format(tmpDate)+" ������");
    		}
    	}
    }
    
    class infoListSelection implements OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
        }
    }


    
    
    class childListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<String> arSrc;
    	int layout;
    	
    	public childListAdapter(Context context, int alayout, ArrayList<String> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public String getItem(int position) {
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
			LinearLayout layout1 = (LinearLayout)convertView.findViewById(R.id.layout1); 
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			TextView textArea = (TextView)convertView.findViewById(R.id.textArea);
			
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
			
			textArea.setText(arSrc.get(position));
			
			//�� ��ư �׼� ����
			layout1.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//�����ϱ� ��ư�� Ŭ���Ͽ��� ���
						Toast.makeText(getApplicationContext(), "����Ʈ�� "+Integer.toString(pos + 1)+"��° ��������", Toast.LENGTH_SHORT).show();
			            Intent intent = new Intent(ChildTraceDetailListActivity.this, ChildTraceViewActivity.class);
						startActivity(intent);
					}
				}
			);
			
			return convertView;
		}

    }
    
}