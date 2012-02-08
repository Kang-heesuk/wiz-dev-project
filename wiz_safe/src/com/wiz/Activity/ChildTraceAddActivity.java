package com.wiz.Activity;

import java.util.ArrayList;

import com.wiz.Activity.R.string;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChildTraceAddActivity extends Activity {

	TraceListAdapter traceListAdapter;
	//발자취 등록정보를 담는 리스트
	final ArrayList<TraceDetail> traceList = new ArrayList<TraceDetail>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
        
		 //top-navigation 값 정의
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildTraceAddActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
            
        
        //앞 페이지에서 필요한 정보를 추출한다.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//몇번째 자리인지
        


        //body 정의
        //서버와 통신하여 리스트 정보를 가져온다.
        //서버와 통신하여 리스트 정보를 가져온다.        
        //서버와 통신하여 리스트 정보를 가져온다.
        
        
        getTraceList();
        
        //어댑터 정의
        traceListAdapter = new TraceListAdapter(this, R.layout.safe_list, traceList);
        ListView listView = (ListView)findViewById(R.id.list);

        //실행하면 화면에 listview를 보여준다.
        listView.setAdapter(traceListAdapter);
        
	}
	
	//부모리스트를 가져오는 로직 - 실행하면 통신하여 리스트를 가져와서  arraylist에 담긴다.
	public void getTraceList(){
		TraceDetail ne1 = new TraceDetail("내쉐퀴1","010-1111-1111", "0", "6", "00", "07", "1", R.drawable.noimg);
        traceList.add(ne1);
        TraceDetail ne2 = new TraceDetail("내쉐퀴22","010-2222-3333", "1", "5", "21", "23", "1", R.drawable.noimg);
        traceList.add(ne2);
	}	
	
	
	
	//custom list adapter 를 inner class 로 선언하여 사용
	class TraceListAdapter extends BaseAdapter {       
		
		Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<TraceDetail> arSrc;
    	int layout;
		 
		public TraceListAdapter(Context context, int alayout, ArrayList<TraceDetail> aarSrc) {
			maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout; 
		}
		
		public int getCount() {
			return arSrc.size();
		}

		public TraceDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 
		
		@Override    
		public View getView(final int position, View convertView, ViewGroup parent) {
			final int pos = position;
			
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}       
			
			TraceDetail traceDetail = getItem(position);
			
			if (traceDetail != null) {             //하나의 이미지뷰와 2개의 텍스트뷰와 버튼 한개 정보를 받아온다.
				String rowInfo_1 = traceDetail.getTargetName()+"("+traceDetail.getTargetCtn()+")";
				String rowInfo_2 = "요일 : "+traceDetail.getStartDay()+" ~ "+traceDetail.getEndDay();
				String rowInfo_3 = "시간 : "+traceDetail.getStartTime()+" ~ "+traceDetail.getEndTime();
				String rowInfo_4 = "간격 : "+traceDetail.getInterval();
				
				((TextView)convertView.findViewById(R.id.eName)).setText(rowInfo_1);
				((TextView)convertView.findViewById(R.id.info1)).setText(rowInfo_2);
				((TextView)convertView.findViewById(R.id.info2)).setText(rowInfo_3);
				((TextView)convertView.findViewById(R.id.info3)).setText(rowInfo_4);
				TextView textInfo2 = (TextView)convertView.findViewById(R.id.info2);
				TextView textInfo3 = (TextView)convertView.findViewById(R.id.info3);
				textInfo2.setVisibility(View.VISIBLE);
				textInfo3.setVisibility(View.VISIBLE);
	           
				if (arSrc.get(pos).getPhotoId() != -1) {
	            	((ImageView)convertView.findViewById(R.id.ePhoto)).setImageResource(traceDetail.getPhotoId());                
	            } else {
	            	((ImageView)convertView.findViewById(R.id.ePhoto)).setImageResource(R.drawable.noimg);  
	            }

				Button btn_more = (Button)convertView.findViewById(R.id.btn_accept);
				btn_more.setText(" > ");
				btn_more.setVisibility(View.VISIBLE);
				
				//아래 버튼 3개에 대한 정의(각 버튼의 이름을 변경하고 click 이벤트를 정의, 마지막 버튼은 숨긴다.)
				LinearLayout bottomBtnArea = (LinearLayout)convertView.findViewById(R.id.bottomBtnArea);
				bottomBtnArea.setVisibility(View.VISIBLE);
				
				Button btn_modify = (Button)convertView.findViewById(R.id.btn_nowLocation);
				btn_modify.setText(R.string.btn_modify);
				btn_modify.setVisibility(View.VISIBLE);

				Button btn_delete = (Button)convertView.findViewById(R.id.btn_history);
				btn_delete.setText(R.string.btn_del);
				btn_delete.setVisibility(View.VISIBLE);
				
				Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
				btn_safeZone.setVisibility(View.GONE);
				
				
				
				
			}            
			
			//Button 기능 셋팅
			Button btn_more = (Button)convertView.findViewById(R.id.btn_accept);
			btn_more.setTag(position);
			//승낙하기 클릭 리스너
			btn_more.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phonenum = (String)getItem(position).getTargetCtn();
					
					Intent intent = new Intent(ChildTraceAddActivity.this, ChildTraceListActivity.class);
					intent.putExtra("phonenum", phonenum);
					startActivity(intent);
					
				}

			});
			
			return convertView;     
			}

		}  
	
	
	//custom list data 를 inner class 로 선언
	class TraceDetail {       
		
		private String targetName;
		private String targetCtn;
	    private String startDay;
	    private String endDay;
	    private String startTime;
	    private String endTime;
	    private String interval;
	    private int photo;
	    
	    public TraceDetail(String _targetname, String _targetctn, String _startday, String _endday, String _starttime, String _endtime, String _interval, int _photo) {
	    	this.targetName = _targetname;
	    	this.targetCtn = _targetctn;
	        this.startDay = _startday;
	        this.endDay = _endday;
	        this.startTime = _starttime;
	        this.endTime = _endtime;
	        this.interval = _interval;
	        this.photo = _photo;
	    }
	
		public String getTargetName() {
			return targetName;
		}

		public String getTargetCtn() {
			return targetCtn;
		}
	
		public String getStartDay() {
			return startDay;
		}

		public String getEndDay() {
			return endDay;
		}

		public String getStartTime() {
			return startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public String getInterval() {
			return interval;
		}

		public int getPhotoId() {
			return photo;
		}

	
	    
	    
	}
	
	
	
}
