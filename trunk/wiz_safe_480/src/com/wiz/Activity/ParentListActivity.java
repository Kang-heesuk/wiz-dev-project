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

public class ParentListActivity extends Activity {
	
	//�޴���ư���� DELETE ��ư�� �������� �����ϱ� or ����� ���� �����ִ� ��ۺ���
	boolean deleteMenuToggle = false;
	boolean bottomAreaIsOn = false;

	//��ϵ� �θ𸮽�Ʈ�� ����Ʈ
	ArrayList<ParentDetail> parentListArr = new ArrayList<ParentDetail>();
	ArrayAdapter<ParentDetail> parentAdapter;
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] parentList;

	//������Ƽ��Ƽ�� �ѱ涧 �ʿ��� �κ�
	int whereFlag = -1;
	String alreadyRegCtn = "";
	
	parentListAdapter listAdapter;
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
    	setContentView(R.layout.parent_list);
        
        //API ȣ�� ������ ����
    	//�θ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog ���̱�
        CallGetParentListApiThread thread = new CallGetParentListApiThread(); 
		thread.start();
        
        listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
        listView = (ListView)findViewById(R.id.list1);
        View footer = getLayoutInflater().inflate(R.layout.parent_list_footer, null, false);
        listView.addFooterView(footer);
    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    public void upDateListView(boolean deleteMenuToggle){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		parentListAdapter listAdapter = new parentListAdapter(this, R.layout.parent_list_customlist, parentListArr, deleteMenuToggle);
  		ListView listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
	protected void onRestart() {

		super.onRestart();
		/*
		//body ����
		//����Ʈ�� �ʱ�ȭ �ϰ� ������ ���Ͽ� �ٽ� �����´�.
		parentList.clear();
        //������ ����Ͽ� ����Ʈ ������ �����´�.
        //������ ����Ͽ� ����Ʈ ������ �����´�.        
        //������ ����Ͽ� ����Ʈ ������ �����´�.
		getParentList();
        
        //����� ����
        parentListAdapter = new ParentListAdapter(this, R.layout.safe_list, parentList);
        ListView listView = (ListView)findViewById(R.id.list);
        //�����ϸ� ȭ�鿡 listview�� �����ش�.
        listView.setAdapter(parentListAdapter);
        */
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

	class parentListAdapter extends BaseAdapter {

    	//�޴����� ���� ��ư�� ���������� ���� ����
    	boolean menuClickDelete = false;
    	
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<ParentDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
    	//���� ��ư�� ������ Ŀ���� ����Ʈ �並 �����ٶ�
    	public parentListAdapter(Context context, int alayout, ArrayList<ParentDetail> aarSrc, boolean menuClickDelete){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    		this.menuClickDelete = menuClickDelete;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public ParentDetail getItem(int position) {
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
			Button imgState = (Button)convertView.findViewById(R.id.imgState);
			
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
			
			if("".equals(arSrc.get(pos).getAcceptDate())){
				//��¥�� ������ �����
				imgState.setBackgroundResource(R.drawable.icon_6);
			}else{
				//��¥�� ������ Ȱ��ȭ
				imgState.setBackgroundResource(R.drawable.icon_7);
			}
			
			//�޴����� �����ϱ� ������ ��� ��ư ���� ���� ����
			if(menuClickDelete){
				imgState.setBackgroundResource(R.drawable.icon_del_selector);
				imgState.setVisibility(View.VISIBLE);
			}
			
			//�� ��ư �׼� ����
			imgState.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						//�����ϱ� ��ư�� Ŭ���Ͽ��� ���
						if(menuClickDelete){
							selectedRow = pos;
							String selectedAcceptDate = arSrc.get(selectedRow).getAcceptDate();
							if("".equals(selectedAcceptDate)){
								//����� �����ǰ�(���� ��¥�� ���� ������)�� �� ���� �ٷ� ���� apiȣ��
								WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog ���̱�
						        CallDeleteApiThread thread = new CallDeleteApiThread(); 
								thread.start();
							}else{
								//02 ���� ������ ���� ���� ���Ͽ� ��Ȯ���� ����  apiȣ��
								//���â ���
								AlertDialog.Builder submitAlert = new AlertDialog.Builder(ParentListActivity.this);
								submitAlert.setTitle("�����ϱ�");
								submitAlert.setMessage("�θ�("+WizSafeUtil.setPhoneNum(arSrc.get(selectedRow).getparentCtn())+")���� ���� �Ͻðڽ��ϱ�?\n���� �� �ش� �θ���� �ڳ���� ��ġ�� ã�� �� �����ϴ�.");
								submitAlert.setPositiveButton("����", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										//�����ϱ� API ȣ�� ������ ����
								    	WizSafeDialog.showLoading(ParentListActivity.this);	//Dialog ���̱�
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
			
			textArea1.setText(arSrc.get(pos).getName());
			textArea2.setText("(" + WizSafeUtil.setPhoneNum(arSrc.get(pos).getparentCtn()) + ")");

			return convertView;
		}
    }
	
	
	//custom list data �� inner class �� ����
	class ParentDetail {       
		
		private String parentName;
	    private String parentCtn;
	    private String relationState;
	    private String acceptDate;
	    
	    public ParentDetail(String _name, String _pn, String _relationState, String _acceptDate) {
	        this.parentName = _name;
	        this.parentCtn = _pn;
	        this.relationState = _relationState;
	        this.acceptDate = _acceptDate;
	    }
	    
	    public String getName() {
	        return parentName;
	    }

	    public String getparentCtn() {
	        return parentCtn;
	    }
	    
	    public String getState() {
	        return relationState;
	    }
	    
	    public String getAcceptDate() {
	        return acceptDate;
	    }

	}
	
	
	
    //API ȣ�� ������
  	class CallGetParentListApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ParentListActivity.this));
  				String url = "https://www.heream.com/api/getParentList.jsp?ctn="+ URLEncoder.encode(enc_ctn);
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
  				ArrayList<String> encParentName = new ArrayList<String>();
  				ArrayList<String> encParentCtn = new ArrayList<String>();
  				ArrayList<String> state = new ArrayList<String>();
  				ArrayList<String> acceptDate = new ArrayList<String>();
  				encParentName = WizSafeParser.xmlParser_List(returnXML,"<PARENT_NAME>");
  				encParentCtn = WizSafeParser.xmlParser_List(returnXML,"<PARENT_CTN>");
  				state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
  				acceptDate = WizSafeParser.xmlParser_List(returnXML,"<ACCEPT_DATE>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				parentList = new String[encParentCtn.size()][4];
  				
  				if(encParentCtn.size() > 0){
  					for(int i=0; i < encParentCtn.size(); i++){
  						parentList[i][1] = WizSafeSeed.seedDec((String) encParentCtn.get(i));
  					}
  				}
  				if(encParentName.size() > 0){
  					for(int i=0; i < encParentName.size(); i++){
  						parentList[i][0] = WizSafeSeed.seedDec((String) encParentName.get(i));
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						parentList[i][2] = (String) state.get(i);
  					}
  				}
  				if(acceptDate.size() > 0){
  					for(int i=0; i < acceptDate.size(); i++){
  						parentList[i][3] = (String) acceptDate.get(i);
  					}
  				}
  				
  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  				parentListArr = new ArrayList<ParentDetail>();
  		    	if(parentList != null){
  			    	for(int i = 0 ; i < parentList.length ; i++){
  			    		ParentDetail addParentList = new ParentDetail(parentList[i][0], parentList[i][1], parentList[i][2], parentList[i][3]);
  			    		parentListArr.add(addParentList);
  			    	}
  		    	}
  		    	
  		    	whereFlag = parentListArr.size() + 1;
  				alreadyRegCtn = "";
  				if(parentListArr.size() > 0){
  					for(int i = 0 ; i < parentListArr.size() ; i++){
  						alreadyRegCtn = alreadyRegCtn + parentListArr.get(i).getparentCtn() + "|";
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
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(ParentListActivity.this));
  				String enc_selectedCtn = WizSafeSeed.seedEnc(parentListArr.get(selectedRow).getparentCtn());
  				String url = "https://www.heream.com/api/deleteRelation.jsp?parentCtn="+ URLEncoder.encode(enc_selectedCtn) + "&childCtn=" + URLEncoder.encode(enc_ctn) + "&me=child";
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
					//��ȸ����
  					
  					//����Ʈ�� �����ϴ��� �ƴϳĿ� ���� ���̴� ���̾ƿ��� �޶�����.
  			        if(parentListArr.size() <= 0){
  			        	LinearLayout bgArea = (LinearLayout)findViewById(R.id.bgArea);
  			        	LinearLayout visibleArea1 = (LinearLayout)findViewById(R.id.visibleArea1);
  			        	LinearLayout visibleArea2 = (LinearLayout)findViewById(R.id.visibleArea2);
  			        	bgArea.setBackgroundResource(R.drawable.bg_parents1);
  			        	visibleArea1.setVisibility(View.GONE);
  			        	visibleArea2.setVisibility(View.VISIBLE);
  			        }
  			     
  			        //�θ����ϱ�(����Ʈ �ִ°��)
  			        findViewById(R.id.btn_parent).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        //�θ����ϱ�(����Ʈ ���°��)
  			        findViewById(R.id.btn_noElements).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
  								intent.putExtra("whereFlag", whereFlag);
  								intent.putExtra("alreadyRegCtn", alreadyRegCtn);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        
  			        //�޴�Ű ��������� �ϴܿ� ������ �޴����� �׼� ����
  			        //1. ����
  			        if(parentListArr.size() <= 0){
  			        	findViewById(R.id.deleteButton).setBackgroundResource(R.drawable.b_menub_1_off);
  			        }
  			        findViewById(R.id.deleteButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								if(parentListArr.size() > 0){
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
  			        
  			        //2. �θ��߰�
  			        findViewById(R.id.childAdditionButton).setOnClickListener(
  						new Button.OnClickListener(){
  							public void onClick(View v) {
  								bottomMenuToggle();
  								Intent intent = new Intent(ParentListActivity.this, ParentAddActivity.class);
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
  								Intent intent = new Intent(ParentListActivity.this, UseInfoListActivity.class);
  								startActivity(intent);
  							}
  						}
  					);
  			        
  			        upDateListView();
			    	
				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(ParentListActivity.this);
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