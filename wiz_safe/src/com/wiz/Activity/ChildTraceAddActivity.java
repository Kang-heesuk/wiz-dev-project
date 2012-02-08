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
	//������ ��������� ��� ����Ʈ
	final ArrayList<TraceDetail> traceList = new ArrayList<TraceDetail>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_trace_add);
        
		 //top-navigation �� ����
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
            
        
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        String phonenum = intent.getStringExtra("phonenum");		//���° �ڸ�����
        


        //body ����
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        //������ ����Ͽ� ����Ʈ ������ �����´�.        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        
        
        getTraceList();
        
        //����� ����
        traceListAdapter = new TraceListAdapter(this, R.layout.safe_list, traceList);
        ListView listView = (ListView)findViewById(R.id.list);

        //�����ϸ� ȭ�鿡 listview�� �����ش�.
        listView.setAdapter(traceListAdapter);
        
	}
	
	//�θ𸮽�Ʈ�� �������� ���� - �����ϸ� ����Ͽ� ����Ʈ�� �����ͼ�  arraylist�� ����.
	public void getTraceList(){
		TraceDetail ne1 = new TraceDetail("������1","010-1111-1111", "0", "6", "00", "07", "1", R.drawable.noimg);
        traceList.add(ne1);
        TraceDetail ne2 = new TraceDetail("������22","010-2222-3333", "1", "5", "21", "23", "1", R.drawable.noimg);
        traceList.add(ne2);
	}	
	
	
	
	//custom list adapter �� inner class �� �����Ͽ� ���
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
			
			if (traceDetail != null) {             //�ϳ��� �̹������ 2���� �ؽ�Ʈ��� ��ư �Ѱ� ������ �޾ƿ´�.
				String rowInfo_1 = traceDetail.getTargetName()+"("+traceDetail.getTargetCtn()+")";
				String rowInfo_2 = "���� : "+traceDetail.getStartDay()+" ~ "+traceDetail.getEndDay();
				String rowInfo_3 = "�ð� : "+traceDetail.getStartTime()+" ~ "+traceDetail.getEndTime();
				String rowInfo_4 = "���� : "+traceDetail.getInterval();
				
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
				
				//�Ʒ� ��ư 3���� ���� ����(�� ��ư�� �̸��� �����ϰ� click �̺�Ʈ�� ����, ������ ��ư�� �����.)
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
			
			//Button ��� ����
			Button btn_more = (Button)convertView.findViewById(R.id.btn_accept);
			btn_more.setTag(position);
			//�³��ϱ� Ŭ�� ������
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
	
	
	//custom list data �� inner class �� ����
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
