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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class LocationLogActivity extends Activity {

	//��ϵ� ����ġ ��ȸ�̷�
	ArrayList<locationLogDetail> locationLogList = new ArrayList<locationLogDetail>();
	ArrayAdapter<locationLogDetail> locationLogAdapter;
    
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] locationLog;
	
	locationLogListAdapter listAdapter;
    ListView listView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_log_list);
        
        //API ȣ�� ������ ����
    	//����ġ ��ȸ�̷� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(LocationLogActivity.this);	//Dialog ���̱�
    	CallGetLocationLogApi thread = new CallGetLocationLogApi(); 
		thread.start();
    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
    	listAdapter = new locationLogListAdapter(this, R.layout.location_log_customlist, locationLogList);
        listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getLocationLogList() {
    	
    	String[][] tempHardCoding = {{"01/12 14:30","01012341234","����� ������ ���ﵿ"},{"01/12 14:30","01012341234","����� ������ ���ﵿ"},{"01/12 14:30","01012341234","����� ������ ���ﵿ"},{"01/12 14:30","01012341234","����� ������ ���ﵿ"},{"01/12 14:30","01012341234","����� ������ ���ﵿ"}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		locationLogDetail addLocationLogList = new locationLogDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2]);
    		locationLogList.add(addLocationLogList);
    	}

    }
    
    
    class locationLogDetail {
    	private String regdate;
    	private String phonenum;
    	private String location;
    	
    	public locationLogDetail (String regdate, String phonenum, String location){
    		this.regdate = regdate;
    		this.phonenum = phonenum;
    		this.location = location;
    	}
    	private String getRegdate(){
			return regdate;
    	}
    	private String getPhonenum(){
			return phonenum;
    	}
    	private String getLocation(){
			return location;
    	}
    }
    
    
    class locationLogListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<locationLogDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public locationLogListAdapter(Context context, int alayout, ArrayList<locationLogDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public locationLogDetail getItem(int position) {
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
			TextView phonenum = (TextView)convertView.findViewById(R.id.phonenum);
			TextView location = (TextView)convertView.findViewById(R.id.location);
			
			//��¥���ĺ�ȯ
			String tempRegdate = arSrc.get(position).getRegdate();
			if(tempRegdate.length() >= 14){
				tempRegdate =  tempRegdate.substring(4,6) + "/" + tempRegdate.substring(6,8) + " " + tempRegdate.substring(8,10) + ":" + tempRegdate.substring(10,12);
			}
			String tempPhonenum = WizSafeSeed.seedDec(arSrc.get(position).getPhonenum());
			String templocation = WizSafeSeed.seedDec(arSrc.get(position).getLocation());
			
			regdate.setText(tempRegdate);
			phonenum.setText(tempPhonenum);
			location.setText(templocation);
			
			//���� ȿ���� �����ֱ� ���� �߰� 
			regdate.setSelected(true);
			phonenum.setSelected(true);
			location.setSelected(true);
			
			return convertView;
		}
    }
    
    //API ȣ�� ������
  	class CallGetLocationLogApi extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(LocationLogActivity.this));
  				String url = "http://www.heream.com/api/getLocationBoard.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				ArrayList<String> regdate = new ArrayList<String>();
  				ArrayList<String> parentCtn = new ArrayList<String>();
  				ArrayList<String> addr = new ArrayList<String>();
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				regdate = WizSafeParser.xmlParser_List(returnXML,"<LIST_REGDATE>");
  				parentCtn = WizSafeParser.xmlParser_List(returnXML,"<LIST_PARENT_CTN>");
  				addr = WizSafeParser.xmlParser_List(returnXML,"<LIST_ADDRESS>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				locationLog = new String[regdate.size()][3];
  				if(regdate.size() > 0){
  					for(int i=0; i < regdate.size(); i++){
  						locationLog[i][0] = regdate.get(i);
  					}
  				}
  				if(parentCtn.size() > 0){
  					for(int i=0; i < parentCtn.size(); i++){
  						locationLog[i][1] = parentCtn.get(i);
  					}
  				}
  				if(addr.size() > 0){
  					for(int i=0; i < addr.size(); i++){
  						locationLog[i][2] =  addr.get(i);
  					}
  				}
  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  				locationLogList = new ArrayList<locationLogDetail>();
  		    	if(locationLog != null){
  			    	for(int i = 0 ; i < locationLog.length ; i++){
  			    		locationLogDetail addLocationLogList = new locationLogDetail(locationLog[i][0], locationLog[i][1], locationLog[i][2]);
  			    		locationLogList.add(addLocationLogList);
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
  				if(httpResult == 0){
  					upDateListView();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(LocationLogActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(LocationLogActivity.this);
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