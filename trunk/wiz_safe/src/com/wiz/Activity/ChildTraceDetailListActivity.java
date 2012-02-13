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
import android.widget.ImageButton;
import android.widget.ImageView;
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
        
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				ChildTraceDetailListActivity.this.finish();
			}
		});
        
        TextView textView1 = (TextView)findViewById(R.id.textTitle);
        textView1.setText(R.string.title_child_log);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_del);
        btn_del.setVisibility(View.INVISIBLE);
        
        //������� body ����
        TextView bodyTopText = (TextView)findViewById(R.id.bodyTopText);
        bodyTopText.setText("�˰�輼��? 1���� ������ ������ ������ �ڵ� ���� �˴ϴ�.");
        
        //1���ϰ� ������ ����Ʈ�� �����´�. �����ϸ� arrayList�� ����.
        getLogList();
        
   
        if(childLogList.size() > 0){
			childListAdapter listAdapter = new childListAdapter(this, R.layout.safe_list, childLogList);
			ListView listView = (ListView)findViewById(R.id.list);
			listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(new infoListSelection());
        }else{
			TextView bodyText = (TextView)findViewById(R.id.bodyText);
			if(bodyText != null){
				bodyText.setText("��ϵ� ������ ���� �̷��� �����ϴ�.");
			}
			bodyText.setVisibility(View.VISIBLE);
			
			ListView listView = (ListView)findViewById(R.id.list);
			listView.setVisibility(View.GONE);
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
        public void onItemClick(AdapterView<?> parent, View view, int position,    long id) {
            Toast.makeText(getApplicationContext(), "����Ʈ�� "+Integer.toString(position+1)+"��° ��������", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(ChildTraceDetailListActivity.this, ChildTraceViewActivity.class);
			startActivity(intent);
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
			ImageView img = (ImageView)convertView.findViewById(R.id.ePhoto);
			//�ѹ����� �̹����� �����Ѵ�.
			//img.setImageDrawable();
			TextView eName = (TextView)convertView.findViewById(R.id.eName);
			//�������� �Ⱥ��̰� ó���ϰ� ����Ʈ�並 ����Ѵ�.
			Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
			btn_accept.setVisibility(View.GONE);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			btn_nowLocation.setVisibility(View.GONE);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			btn_history.setVisibility(View.GONE);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			btn_safeZone.setVisibility(View.GONE);
			
			TextView info1 = (TextView)convertView.findViewById(R.id.info1);
			info1.setVisibility(View.GONE);
			
			eName.setText(arSrc.get(pos));
			
			return convertView;
		}

    }
    
}