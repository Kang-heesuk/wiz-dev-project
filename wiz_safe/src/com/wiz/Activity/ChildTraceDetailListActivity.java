package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class ChildTraceDetailListActivity extends Activity {
	
	String phonenum = "";
	String childName = "";
	
	//������ ����Ʈ
	ArrayList<TraceDetail> childLogList = new ArrayList<TraceDetail>();
	ArrayAdapter<TraceDetail> childAdapter;
	
	childListAdapter listAdapter;
	ListView listView;
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] traceList;
	
	//������� �޾ƿ� ����Ʈ�� ������ 
	int listSize = 0;
	
	//���õ� ROW�� ��ȣ
	int selectedRow = -1;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.child_trace_detail_list);
       
        //�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        phonenum = intent.getStringExtra("phonenum");
        childName = intent.getStringExtra("childName");

        //API ȣ�� ������ ����
    	//������ ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(ChildTraceDetailListActivity.this);	//Dialog ���̱�
    	CallDetailListApiThread thread = new CallDetailListApiThread(); 
		thread.start();

    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
    	listAdapter = new childListAdapter(this, R.layout.child_trace_detail_list_customlist, childLogList);
		listView = (ListView)findViewById(R.id.list1);
		listView.setAdapter(listAdapter);
    }

    class childListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<TraceDetail> arSrc;
    	int layout;
    	
    	public childListAdapter(Context context, int alayout, ArrayList<TraceDetail> aarSrc){
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
			
			String yyyymmdd = arSrc.get(pos).getTraceDay().substring(0,4) + "�� " + arSrc.get(pos).getTraceDay().substring(4,6) + "�� " + arSrc.get(pos).getTraceDay().substring(6,8) + "��";
			textArea.setText(yyyymmdd + " ������");
			
			//�� ��ư �׼� ����
			layout1.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
			            Intent intent = new Intent(ChildTraceDetailListActivity.this, ChildTraceViewActivity.class);
			            intent.putExtra("childCtn", phonenum);
						intent.putExtra("selectedDay", arSrc.get(pos).getTraceDay());
						startActivity(intent);
					}
				}
			);
			return convertView;
		}
    }
    
    class TraceDetail {
    	private String traceDay;
    	private String traceDayOfWeek;
    	
    	public TraceDetail(String traceDay, String traceDayOfWeek){
    		this.traceDay = traceDay;
    		this.traceDayOfWeek = traceDayOfWeek;
    	}
    	
    	private String getTraceDay(){
			return traceDay;
    	}
    	private String getTraceDayOfWeek(){
			return traceDayOfWeek;
    	}
    }
    
    //API ȣ�� ������
  	class CallDetailListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_parentCtn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildTraceDetailListActivity.this));
  				String enc_childCtn = WizSafeSeed.seedEnc(phonenum);
  				String url = "https://www.heream.com/api/getChildTraceDetailList.jsp?parentCtn="+ URLEncoder.encode(enc_parentCtn) + "&childCtn="+ URLEncoder.encode(enc_childCtn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String resultSize = WizSafeParser.xmlParser_String(returnXML,"<RESULT_COUNT>");
  				ArrayList<String> elementDay = WizSafeParser.xmlParser_List(returnXML,"<ELEMENT_DAY>");
  				ArrayList<String> elementDayOfWeek = WizSafeParser.xmlParser_List(returnXML,"<ELEMENT_DAY_OF_WEEK>");
  				
  				//2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				listSize = Integer.parseInt(resultSize);
  				
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2���� �迭�� �����Ѵ�.
  				traceList = new String[elementDay.size()][2]; 
  				
  				//��ȸ�ؿ� ���� 2���� �迭�� �ִ´�.
  				if(elementDay.size() > 0){
  					for(int i=0; i < elementDay.size(); i++){
  						traceList[i][0] = elementDay.get(i);
  					}
  				}
  				if(elementDayOfWeek.size() > 0){
  					for(int i=0; i < elementDayOfWeek.size(); i++){
  						traceList[i][1] = elementDayOfWeek.get(i);
  					}
  				}
  				
  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  		    	if(traceList != null){
  			    	for(int i = 0 ; i < traceList.length ; i++){
  			    		TraceDetail addChildLogList = new TraceDetail(traceList[i][0], traceList[i][1]);
  			    		childLogList.add(addChildLogList);
  			    	}
  		    	}
  		    	
  		    	pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//�ڵ鷯 ������
  				if(httpResult == 0){
					//��ȸ����
  					//����Ʈ�� 1�� �̻� �����Ұ��
  					if(listSize > 0){
  						upDateListView();
  					}else{
  						AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
  						String title = "������ ����";	
  						String message = "�ش� �������� �˻��� �ڳ� �����밡 �������� �ʽ��ϴ�.";	
  						String buttonName = "Ȯ��";
  						ad.setTitle(title);
  						ad.setMessage(message);
  						ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog, int which) {
  								finish();
  							}
  						});
  						ad.show();
  					}
  					
				}else{
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
					String title = "��Ʈ��ũ ����";	
					String message = "��Ʈ��ũ ������ �����ǰ� �ֽ��ϴ�.\n��Ʈ��ũ ���¸� Ȯ�� �Ŀ� �ٽ� �õ����ּ���.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					ad.show();
				}
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildTraceDetailListActivity.this);
				String title = "��Ʈ��ũ ����";	
				String message = "��Ʈ��ũ ������ �����ǰ� �ֽ��ϴ�.\n��Ʈ��ũ ���¸� Ȯ�� �Ŀ� �ٽ� �õ����ּ���.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();				
  			}
  		}
  	};

}