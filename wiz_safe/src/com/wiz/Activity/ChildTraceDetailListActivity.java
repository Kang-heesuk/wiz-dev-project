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

	//발자취 리스트
	ArrayList<String> childLogList = new ArrayList<String>();
	ArrayAdapter<String> childAdapter;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.child_trace_detail_list);
       
        //여기부터 body 구성
        
        //1주일간 발자취 리스트를 가져온다. 진행하면 arrayList에 담긴다.
        getLogList();
   
        if(childLogList.size() > 0){
			childListAdapter listAdapter = new childListAdapter(this, R.layout.child_trace_detail_list_customlist, childLogList);
			ListView listView = (ListView)findViewById(R.id.list1);
			listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(new infoListSelection());
        }
    }
    
    
    public void getLogList() {
    			
    	//발자취 등록일이 20120131 이라고 하드코딩하고 작업했음
    	String regdate = String.valueOf(20110202);
    	
    	for(int i=0; i>=-6; i--){	//달력연산을 위해 -변수로 7회 반복하도록 작성
    		//현재 시간으로 부터 발자취 기간까지 리스트로 구성한다.
        	GregorianCalendar calendar = new GregorianCalendar();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	
    		//오늘날짜에서 i일전 값을 구해서 등록일 이후만 리스트에 포함시킨다.
        	calendar.add(GregorianCalendar.DAY_OF_MONTH, i);
        	String listdate = sdf.format(calendar.getTime());
        	
        	int i_regdate = Integer.parseInt(regdate);
        	int i_listdate = Integer.parseInt(listdate);
        	
    		if(i_listdate > i_regdate){
    			Date tmpDate = calendar.getTime();
    			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");
    			
    			childLogList.add((-i), sdf2.format(tmpDate)+" 발자취");
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
			
			//각 위젯 정의
			LinearLayout layout1 = (LinearLayout)convertView.findViewById(R.id.layout1); 
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			TextView textArea = (TextView)convertView.findViewById(R.id.textArea);
			
			//커스텀 리스트 뷰 앞쪽 이미지 숫자
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
			
			//각 버튼 액션 정의
			layout1.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//삭제하기 버튼을 클릭하였을 경우
						Toast.makeText(getApplicationContext(), "리스트뷰 "+Integer.toString(pos + 1)+"번째 눌렀다응", Toast.LENGTH_SHORT).show();
			            Intent intent = new Intent(ChildTraceDetailListActivity.this, ChildTraceViewActivity.class);
						startActivity(intent);
					}
				}
			);
			
			return convertView;
		}

    }
    
}