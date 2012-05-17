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

public class ChildSafezoneListActivity extends Activity {
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	String parentCtn = "";
	String childCtn = "";
	
	//���� �Ƚ����� ��ϵ� ����Ʈ ������
	int listSize = 0;
	
	//���� ���� �ܿ� ����Ʈ
	int myPoint = 0;
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	int httpMyPointResult = 1;
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
	}
	
	public void onResume(){
    	super.onResume();
    	setContentView(R.layout.child_safezone_list);
    	
    	//�� ���������� �ʿ��� ������ �����Ѵ�.
        Intent intent = getIntent();
        childCtn = intent.getStringExtra("phonenum");
        parentCtn = WizSafeUtil.getCtn(getBaseContext());
        
        //API ȣ�� ������ ����
    	//�Ƚ��� ����Ʈ�� �����´�.
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
			if("00000000000000".equals(textView2)){
				textArea2.setText("�Ƚ��� �ð� ����(������)");
				textArea2.setVisibility(View.VISIBLE);
			}
			
			
			if(arSrc.get(pos).getSafeAlarmDate() == null || "".equals(arSrc.get(pos).getSafeAlarmDate()) || arSrc.get(pos).getSafeAlarmDate().length() < 10){
				btn_modify.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
							Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
							intent.putExtra("safezoneCode", arSrc.get(pos).getSafezoneCode());
							intent.putExtra("latitude", arSrc.get(pos).getSafeLatitude());
							intent.putExtra("longitude", arSrc.get(pos).getSafeLongitude());
							intent.putExtra("radius", arSrc.get(pos).getSafeRadius());
							intent.putExtra("childCtn", childCtn);
							intent.putExtra("flag", "UPDATE");
							intent.putExtra("listSize", listSize);
							startActivity(intent);
						}
					}
				);
			}else{
				btn_modify.setBackgroundResource(R.drawable.btn_s_modify_long_on);
			}
			
			
			
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
    	private String safeLatitude;
		private String safeLongitude;
		private String safeRadius;
		private String safeCtn;
		private String safeChildCtn;
    	
    	public ChildSafezoneDetail (String _safezoneCode, String _safeAddress, String _safeAlarmDate, String _safeLatitude, String _safeLongitude, String _safeRadius, String _safeCtn, String _safeChildCtn){
    		this.safezoneCode = _safezoneCode;
    		this.safeAddress = _safeAddress;
    		this.safeAlarmDate = _safeAlarmDate;
    		this.safeLatitude = _safeLatitude;
    		this.safeLongitude = _safeLongitude;
    		this.safeRadius = _safeRadius;
    		this.safeCtn = _safeCtn;
    		this.safeChildCtn = _safeChildCtn;
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
    	private String getSafeLatitude(){
			return safeLatitude;
    	}
    	private String getSafeLongitude(){
			return safeLongitude;
    	}
    	private String getSafeRadius(){
			return safeRadius;
    	}
    	private String getSafeCtn(){
			return safeCtn;
    	}
    	private String getSafeChildCtn(){
			return safeChildCtn;
    	}
    }
	
	//API ȣ�� ������
  	class CallGetSafeZoneListApiThread extends Thread{
  		public void run(){
  			String url = "";
  			HttpURLConnection urlConn;
  			BufferedReader br;
			String temp;
  			try{
  				//���� �ܿ� ����Ʈ�� �������� ����
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildSafezoneListActivity.this));
  				url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				temp = "";
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode_mypoint = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				httpMyPointResult = Integer.parseInt(resultCode_mypoint);
  				if(httpMyPointResult == 0){
  					myPoint = Integer.parseInt(WizSafeSeed.seedDec(WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>")));
  				}else{
  					myPoint = 0;
  				}
  				
  				//�Ƚ��� ����Ʈ �������� ����
  				url = "https://www.heream.com/api/getChildSafezoneList.jsp?parent_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(parentCtn)) + "&child_ctn=" + URLEncoder.encode(WizSafeSeed.seedEnc(childCtn));
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				temp = "";
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ���� 
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				ArrayList<String> strSafezoneCode = new ArrayList<String>(); 
  				ArrayList<String> encAddress = new ArrayList<String>(); 
  				ArrayList<String> strAlarmDate = new ArrayList<String>(); 
  				ArrayList<String> encLatitude = new ArrayList<String>(); 
  				ArrayList<String> encLongitude = new ArrayList<String>(); 
  				ArrayList<String> encRadius = new ArrayList<String>(); 
  				ArrayList<String> encCtn = new ArrayList<String>();
  				ArrayList<String> encChildCtn = new ArrayList<String>();
  				strSafezoneCode = WizSafeParser.xmlParser_List(returnXML,"<SAFEZONE_CODE>");
  				encAddress = WizSafeParser.xmlParser_List(returnXML,"<ADDRESS>");
  				strAlarmDate = WizSafeParser.xmlParser_List(returnXML,"<ALARM_DATE>");
  				encLatitude = WizSafeParser.xmlParser_List(returnXML,"<LATITUDE>");
  				encLongitude = WizSafeParser.xmlParser_List(returnXML,"<LONGITUDE>");
  				encRadius = WizSafeParser.xmlParser_List(returnXML,"<RADIUS>");
  				encCtn = WizSafeParser.xmlParser_List(returnXML,"<CTN>");
  				encChildCtn = WizSafeParser.xmlParser_List(returnXML,"<CHILD_CTN>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				listSize = strSafezoneCode.size();
  				childSafezoneList = new String[listSize][8];
  				
  				if(strSafezoneCode.size() > 0){
  					for(int i=0; i < strSafezoneCode.size(); i++){
  						childSafezoneList[i][0] = (String) strSafezoneCode.get(i);
  					}
  				}
  				if(encAddress.size() > 0){
  					for(int i=0; i < encAddress.size(); i++){
  						String tempAddress = (String)encAddress.get(i);
  						//��ȣȭ
  						tempAddress = WizSafeSeed.seedDec(tempAddress);
  						//���ѹα� ����
  						tempAddress = WizSafeUtil.replaceStr(tempAddress,"���ѹα� ","");
  						childSafezoneList[i][1] = tempAddress;
  					}
  				}
  				if(strAlarmDate.size() > 0){
  					for(int i=0; i < strAlarmDate.size(); i++){
  						childSafezoneList[i][2] = (String) strAlarmDate.get(i);
  					}
  				}
  				if(encLatitude.size() > 0){
  					for(int i=0; i < encLatitude.size(); i++){
  						childSafezoneList[i][3] = WizSafeSeed.seedDec(encLatitude.get(i));
  					}
  				}
  				if(encLongitude.size() > 0){
  					for(int i=0; i < encLongitude.size(); i++){
  						childSafezoneList[i][4] = WizSafeSeed.seedDec(encLongitude.get(i));
  					}
  				}
  				if(encRadius.size() > 0){
  					for(int i=0; i < encRadius.size(); i++){
  						childSafezoneList[i][5] = WizSafeSeed.seedDec(encRadius.get(i));
  					}
  				}
  				if(encCtn.size() > 0){
  					for(int i=0; i < encCtn.size(); i++){
  						childSafezoneList[i][6] = WizSafeSeed.seedDec(encCtn.get(i));
  					}
  				}
  				if(encChildCtn.size() > 0){
  					for(int i=0; i < encChildCtn.size(); i++){
  						childSafezoneList[i][7] = WizSafeSeed.seedDec(encChildCtn.get(i));
  					}
  				}
  				
  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  				childSafezoneListArr = new ArrayList<ChildSafezoneDetail>();
  		    	if(childSafezoneList != null){
  			    	for(int i = 0 ; i < childSafezoneList.length ; i++){
  			    		ChildSafezoneDetail addChildSafezoneList = new ChildSafezoneDetail(childSafezoneList[i][0], childSafezoneList[i][1], childSafezoneList[i][2], childSafezoneList[i][3], childSafezoneList[i][4], childSafezoneList[i][5], childSafezoneList[i][6], childSafezoneList[i][7]);
  			    		childSafezoneListArr.add(addChildSafezoneList);
  			    	}
  		    	}
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}

  	
  	//API ȣ�� ������
  	class CallDeleteApiThread extends Thread{
  		public void run(){
  			try{
  				String selectedSafezoneCode = childSafezoneListArr.get(selectedRow).getSafezoneCode();
  				String selectedEncCtn = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeCtn());
  				String selectedEncChildCtn = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeChildCtn());
  				String selectedEncRadius = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeRadius());
  				String selectedEncAddr = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeAddress());
  				String selectedEncLat = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeLatitude());
  				String selectedEncLon = WizSafeSeed.seedEnc(childSafezoneListArr.get(selectedRow).getSafeLongitude());
  				String url = "https://www.heream.com/api/deleteChildSafezone.jsp?safezoneCode=" + URLEncoder.encode(selectedSafezoneCode)+"&parentCtn="+URLEncoder.encode(selectedEncCtn)+"&childCtn="+URLEncoder.encode(selectedEncChildCtn)+"&radius="+URLEncoder.encode(selectedEncRadius)+"&addr="+URLEncoder.encode(selectedEncAddr)+"&lat="+URLEncoder.encode(selectedEncLat)+"&lon="+URLEncoder.encode(selectedEncLon);
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
  						LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);  						
  			        	visibleArea1.setVisibility(View.VISIBLE);
  			        	visibleArea2.setVisibility(View.GONE);
  			        	
	  			        //�Ƚ��� ��� ��ư�׼�(����Ʈ �ִ°��)
	  			        findViewById(R.id.btn_addChild).setOnClickListener(
	  						new Button.OnClickListener(){
	  							public void onClick(View v) {
	  								if(myPoint >= 100){
	  									Intent intent = new Intent(ChildSafezoneListActivity.this, ChildSafezoneAddActivity.class);
		  								intent.putExtra("flag", "INSERT");
		  								intent.putExtra("childCtn", childCtn);
		  								intent.putExtra("listSize", listSize);
		  								startActivity(intent);
	  								}else{
	  									AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
	  									ad.setTitle("����Ʈ �ȳ�");
	  									ad.setMessage("������ ����Ʈ�� �����մϴ�. ����Ʈ ���� �� �ٽ� �̿��� �ּ���.");
	  									ad.setPositiveButton("����Ʈ\n�����ϱ�", new DialogInterface.OnClickListener() {
	  										public void onClick(DialogInterface dialog, int which) {
	  											Intent intent = new Intent(ChildSafezoneListActivity.this, PointChargeActivity.class);
	  				  							startActivity(intent);
	  										}
	  									});
	  									ad.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener(){
	  										public void onClick(DialogInterface dialog, int which) {
	  										}
	  									});
	  									ad.show();
	  								}
	  								
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
	  			  					intent.putExtra("flag", "INSERT");
	  			  					intent.putExtra("childCtn", childCtn);
	  			  					intent.putExtra("listSize", listSize);
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
				String title = "��Ʈ��ũ ����1";	
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
  			}else if(msg.what == 2){
  				//��Ƽ��Ƽ �����
				Intent intent = getIntent();
				finish();
				startActivity(intent);
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildSafezoneListActivity.this);
				String title = "��Ʈ��ũ ����";	
				String message = "��Ʈ��ũ ������ �����ǰ� �ֽ��ϴ�.\n��Ʈ��ũ ���¸� Ȯ�� �Ŀ� �ٽ� �õ����ּ���.";	
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
