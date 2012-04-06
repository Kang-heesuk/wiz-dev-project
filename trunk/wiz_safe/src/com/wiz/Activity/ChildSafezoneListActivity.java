package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStream;
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

public class ChildSafezoneListActivity extends Activity {
	
	String parentCtn = "";
	String childCtn = "";
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] childSafezoneList;
	
	//���õ� ROW�� ��ȣ
	int selectedRow = -1;
	//���� API ȣ�� ���� �����
	int deleteApiResult = -1;	
		
	ChildSafezoneListAdapter childSafezoneListAdapter;
	ListView listView;

	
	//��ϵ� �Ƚ��� ����Ʈ
	ArrayList<ChildSafezoneDetail> childSafezoneListArr = new ArrayList<ChildSafezoneDetail>();
	
	ArrayAdapter<ChildSafezoneDetail> childSafezoneAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.child_safezone_list);
		
		//�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        childCtn = intent.getStringExtra("phonenum");
        parentCtn = WizSafeUtil.getCtn(getBaseContext());
        
        //API ȣ�� ������ ����
    	//class ���� ���Խ� api ������� �����浵�� �����´�.
    	WizSafeDialog.showLoading(ChildSafezoneListActivity.this);	//Dialog ���̱�
        CallGetSafeZoneListApiThread thread = new CallGetSafeZoneListApiThread(); 
		thread.start();
		
        childSafezoneListAdapter = new ChildSafezoneListAdapter(this, R.layout.child_safezone_list_customlist, childSafezoneListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_safezone_list_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(childSafezoneListAdapter);
	}
	
	//����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
    	childSafezoneListAdapter = new ChildSafezoneListAdapter(this, R.layout.child_safezone_list_customlist, childSafezoneListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(childSafezoneListAdapter);
    }
	
    class ChildSafezoneListAdapter extends BaseAdapter {

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ChildSafezoneDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public ChildSafezoneListAdapter(Context context, int alayout, ArrayList<ChildSafezoneDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ChildSafezoneDetail getItem(int position) {
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
			Button imgNum = (Button)convertView.findViewById(R.id.imgNum);
			TextView textArea1 = (TextView)convertView.findViewById(R.id.textArea1);
			TextView textArea2 = (TextView)convertView.findViewById(R.id.textArea2);
			Button btn_modify = (Button)convertView.findViewById(R.id.btn_modify);
			Button btn_delete = (Button)convertView.findViewById(R.id.btn_delete);
			
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
			
			//���� �κ�
			textArea1.setText(arSrc.get(position).getSafeAddress());
			String textView2 = arSrc.get(position).getSafeAlarmDate();
			if(textView2 != null && !"".equals(textView2) && textView2.length() >= 10){
				String year = textView2.substring(0, 4);
				String month = textView2.substring(4, 6);
				if("0".equals(month.substring(0,1))){
					month = month.substring(1,2);
				}
				String day = textView2.substring(6, 8);
				if("0".equals(day.substring(0,1))){
					day = day.substring(1,2);
				}
				String time = textView2.substring(8, 10);
				textArea2.setText(year + "�� " + month + "�� " + day + "�� " + WizSafeUtil.timeConvertFromNumberToString0to23(time) + " ����");
				textArea2.setVisibility(View.VISIBLE);
			}
			
			btn_modify.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
						intent.putExtra("phonenum", childCtn);
						intent.putExtra("safeZoneCode", arSrc.get(pos).getSafezoneCode());
						startActivity(intent);
					}
				}
			);
			
			btn_delete.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildSafezoneListActivity.this);
						submitAlert.setTitle("�Ƚ�������");
						submitAlert.setMessage("�Ƚ��� ������ ���� �Ͻðڽ��ϱ�?\n�������� : "+ arSrc.get(pos).getSafeAddress());
						submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//������ rownum
								selectedRow = pos;
								//�����ϱ� API ȣ�� ������ ����
						    	WizSafeDialog.showLoading(ChildSafezoneListActivity.this);	//Dialog ���̱�
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
							}
						});
						submitAlert.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						submitAlert.show();
					}
				}
			);			

			return convertView;
		}
    }
    
	class ChildSafezoneDetail {
		private String safezoneCode;
    	private String safeAddress;
    	private String safeAlarmDate;
    	
    	public ChildSafezoneDetail (String _safezoneCode, String _safeAddress, String _safeAlarmDate){
    		this.safezoneCode = _safezoneCode;
    		this.safeAddress = _safeAddress;
    		this.safeAlarmDate = _safeAlarmDate;
    	}
    	private String getSafezoneCode(){
			return safezoneCode;
    	}
    	private String getSafeAddress(){
			return safeAddress;
    	}
    	private String getSafeAlarmDate(){
			return safeAlarmDate;
    	}
    }
	
	//API ȣ�� ������
  	class CallGetSafeZoneListApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String url = "https://www.heream.com/api/getChildSafezoneList.jsp?parent_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(parentCtn)) + "&child_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(childCtn));
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
  				ArrayList<String> strSafezoneCode = WizSafeParser.xmlParser_List(returnXML,"<SAFEZONE_CODE>");
  				ArrayList<String> encAddress = WizSafeParser.xmlParser_List(returnXML,"<ADDRESS>");
  				ArrayList<String> strAlarmDate = WizSafeParser.xmlParser_List(returnXML,"<ALARM_DATE>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				childSafezoneList = new String[strSafezoneCode.size()][3];
  				
  				if(strSafezoneCode.size() > 0){
  					for(int i=0; i < strSafezoneCode.size(); i++){
  						childSafezoneList[i][0] = (String) strSafezoneCode.get(i);
  					}
  				}
  				if(encAddress.size() > 0){
  					for(int i=0; i < encAddress.size(); i++){
  						childSafezoneList[i][1] = WizSafeSeed.seedDec((String) encAddress.get(i));
  					}
  				}
  				if(strAlarmDate.size() > 0){
  					for(int i=0; i < strAlarmDate.size(); i++){
  						childSafezoneList[i][2] = (String) strAlarmDate.get(i);
  					}
  				}
  				
  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  		    	if(childSafezoneList != null){
  			    	for(int i = 0 ; i < childSafezoneList.length ; i++){
  			    		ChildSafezoneDetail addChildSafezoneList = new ChildSafezoneDetail(childSafezoneList[i][0], childSafezoneList[i][1], childSafezoneList[i][2]);
  			    		childSafezoneListArr.add(addChildSafezoneList);
  			    	}
  		    	}
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}

  	
  //API ȣ�� ������
  	class CallDeleteApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String selectedSafezoneCode = childSafezoneListArr.get(selectedRow).getSafezoneCode();
  				String url = "https://www.heream.com/api/deleteChildSafezoneData.jsp?safezoneCode=" + URLEncoder.encode(selectedSafezoneCode);
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
  				
  				deleteApiResult = Integer.parseInt(resultCode);
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				//�ڵ鷯 ������
  				if(httpResult == 0){
  					
  					if(childSafezoneListArr.size() > 0){
	  			        //�Ƚ��� ��� ��ư�׼�(����Ʈ �ִ°��)
	  			        findViewById(R.id.btn_addChild).setOnClickListener(
	  						new Button.OnClickListener(){
	  							public void onClick(View v) {
	  								Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
	  								startActivity(intent);
	  							}
	  						}
	  					);
	  			        
	  			        upDateListView();
	  			      
  					}else{
  			            //����Ʈ�� �����ϴ��� �ƴϳĿ� ���� ���̴� ���̾ƿ��� �޶�����.
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_safezone1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        
	  			        //�Ƚ��� ��� ��ư�׼�(����Ʈ ���°��)
	  			        findViewById(R.id.btn_noElements).setOnClickListener(
	  			        	new Button.OnClickListener(){
	  			  				public void onClick(View v) {
	  			  					Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
	  			  					startActivity(intent);
	  			  				}
	  			  			}
	  			  		);
  					}
  					
				}else{
					//��ȸ����
				}
  			}else if(msg.what == 1){
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				ad.show();
  			}else if(msg.what == 2){
  				//��Ƽ��Ƽ �����
				Intent intent = getIntent();
				finish();
				startActivity(intent);
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				ad.show();
  			}
  		}
  	};
}
