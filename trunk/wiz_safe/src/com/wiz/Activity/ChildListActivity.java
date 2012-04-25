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
import android.view.KeyEvent;
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


public class ChildListActivity extends Activity {
	  
	//�޴���ư���� DELETE ��ư�� �������� �����ϱ� or ���δ���� ���� �����ִ� ��ۺ���
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;
	
	//��ϵ� �ڳ��� �̸�
	ArrayList<ChildDetail> childListArr = new ArrayList<ChildDetail>();
	ArrayAdapter<ChildDetail> childAdapter;
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] childList;
	
	//������Ƽ��Ƽ�� �ѱ涧 �ʿ��� �κ�
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	ChildListAdapter listAdapter;
	ListView listView;
	
	//���õ� ROW�� ��ȣ
	int selectedRow = -1;
	
	//���� API ȣ�� ���� �����
	int deleteApiResult = -1;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
    }
    
    public void onResume(){
    	super.onResume();
    	setContentView(R.layout.child_list);
    	
    	listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.child_list_footer, null, false);
        listView.addFooterView(footer);
    	
    	 //API ȣ�� ������ ����
    	//�ڳ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog ���̱�
        CallGetChildListApiThread thread = new CallGetChildListApiThread(); 
		thread.start();
    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    public void upDateListView(boolean deleteMenuToggle){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.(������ư ��������� ��ȣ���ϴ°�)
  		listAdapter = new ChildListAdapter(this, R.layout.child_list_customlist, childListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //�޴�Ű ��������� �ϴ��� �޴������� ���̰� �Ⱥ��̰� ���� ���̾ƿ� ����
    public void bottomMenuToggle(){
    	LinearLayout bodyArea = (LinearLayout)findViewById(R.id.bodyArea);
		LinearLayout bottomMenuArea = (LinearLayout)findViewById(R.id.bottomMenuArea);
		LinearLayout subArea1 = (LinearLayout)findViewById(R.id.subArea1);
		LinearLayout subArea2 = (LinearLayout)findViewById(R.id.subArea2);
		LinearLayout subArea3 = (LinearLayout)findViewById(R.id.subArea3);
		bodyArea.setEnabled(true);
		bottomMenuArea.setEnabled(true);
		subArea1.setEnabled(true);
		subArea2.setEnabled(true);
		subArea3.setEnabled(true);
		LinearLayout.LayoutParams bodyAreaParams = (LinearLayout.LayoutParams)bodyArea.getLayoutParams();
		LinearLayout.LayoutParams bottomAreaParams = (LinearLayout.LayoutParams)bottomMenuArea.getLayoutParams();
		LinearLayout.LayoutParams subArea1Params = (LinearLayout.LayoutParams)subArea1.getLayoutParams();
		LinearLayout.LayoutParams subArea2Params = (LinearLayout.LayoutParams)subArea2.getLayoutParams();
		LinearLayout.LayoutParams subArea3Params = (LinearLayout.LayoutParams)subArea3.getLayoutParams();
		if(bottomMenuArea.getVisibility() == View.VISIBLE){
			bottomAreaIsOn = false;
			bodyAreaParams.weight = 93;
			bottomAreaParams.weight = 0;
			subArea1Params.weight = 28;
			subArea2Params.weight = 16;
			subArea3Params.weight = 56;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.GONE);
		}else{
			bottomAreaIsOn = true;
			bodyAreaParams.weight = 77;
			bottomAreaParams.weight = 16;
			subArea1Params.weight = (float)33.8;
			subArea2Params.weight = (float)19.2;
			subArea3Params.weight = 47;
			bodyArea.setLayoutParams(bodyAreaParams);
			bottomMenuArea.setLayoutParams(bottomAreaParams);
			subArea1.setLayoutParams(subArea1Params);
			subArea2.setLayoutParams(subArea2Params);
			subArea3.setLayoutParams(subArea3Params);
			bottomMenuArea.setVisibility(View.VISIBLE);
		}
    }
   
    //�޴�Ű ��������� ���̾ƿ� ����(�ϴ� �ٰ� ���´�. �ϴ� �ٰ� �����鼭 ����� weight ���� �������ȴ�.)
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	
    	if(bottomAreaIsOn){
    		if(keyCode == 4){
        		bottomMenuToggle();    		
        		return true;
        	}
    	}
    	
    	if(keyCode == 82){
    		bottomMenuToggle();    		
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    class ChildDetail {
    	private String childName;
    	private String childCtn;
    	private String childRelation;
    	private String childRegLocaInfo;
    	
    	public ChildDetail (String childName, String phonenum, String childRelation, String childRegLocaInfo){
    		this.childName = childName;
    		this.childCtn = phonenum;
    		this.childRelation = childRelation;
    		this.childRegLocaInfo = childRegLocaInfo;
    		
    	}
    	private String getChildName(){
			return childName;
    	}
    	private String getChildCtn(){
			return childCtn;
    	}
    	private String getChildRelation(){
			return childRelation;
    	}
    	private String getChildRegLocaInfo(){
			return childRegLocaInfo;
    	}
    }
    
    class ChildListAdapter extends BaseAdapter {

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ChildDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public ChildListAdapter(Context context, int alayout, ArrayList<ChildDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//���� ��ư�� ������ Ŀ���� ����Ʈ �並 �����ٶ�
    	public ChildListAdapter(Context context, int alayout, ArrayList<ChildDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ChildDetail getItem(int position) {
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
			Button btnChildState = (Button)convertView.findViewById(R.id.btnChildState);
			Button btn_nowLocation = (Button)convertView.findViewById(R.id.btn_nowLocation);
			Button btn_history = (Button)convertView.findViewById(R.id.btn_history);
			Button btn_safeZone = (Button)convertView.findViewById(R.id.btn_safeZone);
			TextView childName = (TextView)convertView.findViewById(R.id.childName);
			TextView childPhonenum = (TextView)convertView.findViewById(R.id.childPhonenum);
			
			childName.setText(arSrc.get(position).getChildName());
			childPhonenum.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(position).getChildCtn()) + ")");
			
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
			
			//�� ��ư �̸� �� ���� ���� ����
			if("01".equals(arSrc.get(position).getChildRelation())){
				//01:���δ��
				btnChildState.setBackgroundResource(R.drawable.icon_3);
				btnChildState.setVisibility(View.VISIBLE);
			}else if("02".equals(arSrc.get(position).getChildRelation())){
				//02:���οϷ�
				btnChildState.setBackgroundResource(R.drawable.icon_1);
				btnChildState.setVisibility(View.VISIBLE);
			}else{
				//��Ÿ:���ΰ���
				btnChildState.setBackgroundResource(R.drawable.icon_2);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			//�� ��ư �׼� ����
			btnChildState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//�����ϱ� ��ư�� Ŭ���Ͽ��� ���
						if(menuClickDelete){
							selectedRow = pos;
							String selectedState = arSrc.get(selectedRow).getChildRelation();
							//������ ���� ��ư �����Ͱ� ���ΰ��� �����͸� ���â ���� �ٷ� �����Ѵ�.
							if("03".equals(selectedState)){
								//���â ���� ���� api ȣ��
								//�����ϱ� API ȣ�� ������ ����
						    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog ���̱�
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
							}else{
								//���â ���
								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ChildListActivity.this);
								submitAlert.setTitle("�����ϱ�");
								submitAlert.setMessage("�ڳ�("+WizSafeUtil.setPhoneNum(arSrc.get(selectedRow).getChildCtn())+")���� ���� �Ͻðڽ��ϱ�?\n���� �� �ڳ��� ���Ǹ� �ٽ� �޾ƾ��ϸ� ���� ���� ������ �Բ� ���� �˴ϴ�.");
								submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										//�����ϱ� API ȣ�� ������ ����
								    	WizSafeDialog.showLoading(ChildListActivity.this);	//Dialog ���̱�
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
					}
				}
			);
			if("02".equals(arSrc.get(pos).getChildRelation())){
				//�ش� Ŭ�������ʴ� �����͸� ��ȸ�� �� �ִ� ��쿡�� Ȱ��ȭ ��Ų��.
				if(arSrc.get(pos).getChildRegLocaInfo() != null && !"".equals(arSrc.get(pos).getChildRegLocaInfo())){
					btn_nowLocation.setBackgroundResource(R.drawable.btn_s_now_selector);
					btn_nowLocation.setOnClickListener(
						new Button.OnClickListener(){
							public void onClick(View v) {
								Intent intent = new Intent(ChildListActivity.this, ChildLocationViewActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
								startActivity(intent);
							}
						}
					);
				}else{
					btnChildState.setBackgroundResource(R.drawable.icon_5);
				}
				btn_history.setBackgroundResource(R.drawable.btn_s_trace_selector);
				btn_history.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
							Intent intent = new Intent(ChildListActivity.this, ChildTraceListActivity.class);
							intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
							intent.putExtra("childName", arSrc.get(pos).getChildName());
							startActivity(intent);
						}
					}
				);
				btn_safeZone.setBackgroundResource(R.drawable.btn_s_safe_selector);
				btn_safeZone.setOnClickListener(
					new Button.OnClickListener(){
						public void onClick(View v) {
								Intent intent = new Intent(ChildListActivity.this, ChildSafezoneListActivity.class);
								intent.putExtra("phonenum", arSrc.get(pos).getChildCtn());
								intent.putExtra("childName", arSrc.get(pos).getChildName());
								startActivity(intent);
						}
					}
				);
			}
			
		
			//�޴����� �����ϱ� ������ ��� ��ư ���� ���� ����
			if(menuClickDelete){
				btnChildState.setBackgroundResource(R.drawable.icon_del_selector);
				btnChildState.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
    }
    
    //API ȣ�� ������
  	class CallGetChildListApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildListActivity.this));
  				String url = "https://www.heream.com/api/getChildList.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				ArrayList<String> encChildName = new ArrayList<String>();
  				ArrayList<String> encChildCtn = new ArrayList<String>();
  				ArrayList<String> state = new ArrayList<String>();
  				ArrayList<String> regLocaInfo = new ArrayList<String>();
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				encChildName = WizSafeParser.xmlParser_List(returnXML,"<CHILD_NAME>");
  				encChildCtn = WizSafeParser.xmlParser_List(returnXML,"<CHILD_CTN>");
  				state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
  				regLocaInfo = WizSafeParser.xmlParser_List(returnXML,"<REGLOCAINFO>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				childList = new String[encChildCtn.size()][4];
  				if(encChildCtn.size() > 0){
  					for(int i=0; i < encChildCtn.size(); i++){
  						childList[i][1] = WizSafeSeed.seedDec((String) encChildCtn.get(i));
  					}
  				}
  				if(encChildName.size() > 0){
  					for(int i=0; i < encChildName.size(); i++){
  						childList[i][0] = WizSafeSeed.seedDec((String) encChildName.get(i));
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						childList[i][2] = (String) state.get(i);
  					}
  				}
  				if(regLocaInfo.size() > 0){
  					for(int i=0; i < regLocaInfo.size(); i++){
  						childList[i][3] = (String) regLocaInfo.get(i);
  					}
  				}

  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  				childListArr = new ArrayList<ChildDetail>();
  		    	if(childList != null){
  			    	for(int i = 0 ; i < childList.length ; i++){
  			    		ChildDetail addChildList = new ChildDetail(childList[i][0], childList[i][1], childList[i][2], childList[i][3]);
  			    		childListArr.add(addChildList);
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
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ChildListActivity.this));
  				String enc_selectedCtn = WizSafeSeed.seedEnc(childListArr.get(selectedRow).getChildCtn());
  				String url = "https://www.heream.com/api/deleteRelation.jsp?parentCtn="+ URLEncoder.encode(enc_ctn) + "&childCtn=" + URLEncoder.encode(enc_selectedCtn) + "&me=parent";
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
					//��ȸ����
  					whereFlag = childListArr.size() + 1;
  					alreadyRegCtn = "";
  					if(childListArr.size() > 0){
  						for(int i = 0 ; i < childListArr.size() ; i++){
  							alreadyRegCtn = alreadyRegCtn + childListArr.get(i).getChildCtn() + "|";
  						}
  					}
  					
  					//����Ʈ�� �����ϴ��� �ƴϳĿ� ���� ���̴� ���̾ƿ��� �޶�����.
  			        if(childListArr.size() <= 0){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_child1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  					
  			        //�ڳ����ϱ�(����Ʈ �ִ°��)
  			        findViewById(R.id.btn_addChild).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //�ڳ����ϱ�(����Ʈ ���°��)
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        
  			        //�޴�Ű ��������� �ϴܿ� ������ �޴����� �׼� ����
  			        //1. ����
  			        if(childListArr.size() <= 0){
  			        	findViewById(R.id.deleteButton).setBackgroundResource(R.drawable.b_menub_1_off);
  			        }
  			        findViewById(R.id.deleteButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								if(childListArr.size() > 0){
  									bottomMenuToggle();
  									if(deleteMenuToggle == false){
  							  			deleteMenuToggle = true;
  							  		}else{
  							  			deleteMenuToggle = false;
  							  		}
  							  		upDateListView(deleteMenuToggle);
  								}else{
  									
  								}
  							}
  						}
  					);
  			        
  			        //2. �ڳ��߰�
  			        findViewById(R.id.childAdditionButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ChildListActivity.this, ChildAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //2. �̿�ȳ�
  			        findViewById(R.id.useInfoButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ChildListActivity.this, UseInfoListActivity.class);
  								startActivity(intent);
  							}
  						}
  					);
  					
  			      upDateListView();
			    	
				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
					String title = "��� ����";	
					String message = "�θ� ����Ʈ ������ ��ȸ�� �� �����ϴ�.";	
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
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ChildListActivity.this);
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