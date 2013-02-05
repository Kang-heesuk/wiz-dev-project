package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.Demon.WizSafeGetLocation;
import com.wiz.Seed.WizSafeSeed;
import com.wiz.View.NoticePopView;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class MainActivity extends Activity {
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
 
	//���ڻ��׿� ���� ���� ���� 
	private int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	private String seq;
	private String title;
	private String content;
	
	//���� ������ ������ ���� ����(���ڵ� ���Ե�)
	int customerApiResult = -1;
	String myPoint = "";
	String stateSmsRecv = "";
	String relationCount = "";
	ArrayList<String> relationType;
	ArrayList<String> parentCtn;
	ArrayList<String> childCtn;
	ArrayList<String> relationState;
	
	//��ϴ������ �ڳฮ��Ʈ(��� ��ȣȭ �� ��ȣ)
	ArrayList<String> waitChildPhone = new ArrayList<String>();
	
	//��ϴ������ �θ𸮽�Ʈ(��� ��ȣȭ �� ��ȣ)
	ArrayList<String> waitParentPhone = new ArrayList<String>();
	
	//������� �θ𸮽�Ʈ�� ���Ͽ� ����â�� �������� �ȳ������� ��Ʈ���ϴ� ����
	boolean isParentAddAlert = true;
	
	//�������� �˾��� ����� ������ ���Ͽ� ��Ʈ���ϴ� ����(�� ����ÿ��� �߰�, �ȿ��� �޴� �̵��Ҷ��� �ȶ߰� ���ִ� ����)
	boolean isShowPopView = true;
	
	//�ڷΰ��� �ι� ������ ����ǵ��� �����ϱ� ���� ����
	private Handler mHandler;
	private boolean mFlag = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        chkGpsService();
        
        //������ ������� �ƴ��� �Ǻ��Ͽ� ���� ��ġ�� �����ؾ� �ϴ� USER �̸� ������ �ѹ� ������� ��ġ�� �����Ѵ�.
        if(WizSafeUtil.isAuthOkUser(MainActivity.this)){
            if(WizSafeUtil.isSendLocationPassibleUser(MainActivity.this)){
            	//��׶��忡 DemonService��� Sevice�� �����ϴ��� ������.
        		ComponentName cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
        		//���� ����(������ ���� ��Ų ������ ���۽�Ŵ)
        		startService(new Intent().setComponent(cn));
            }
        }
        
        //�ڷΰ��� �ι� ������ ����ǵ��� ����
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    mFlag = false;
                 }
            }
        };
    }
    
    public void onResume(){
    	super.onResume();
    	
    	//�� ���ʷ� ����� �˸� ���(3G������ ��� �� ������ ������ ������� ����...)
    	SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		String appFirstStartVal = LocalSave.getString("appFirstStart","0");
		
		//���� ����� ���â �߻� �� Ȯ�� ������ ���
    	if("0".equals(appFirstStartVal)){
    		//��ȸ����
			AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
			String title = "�˸�";	
			String message = "3G ������ ��� �� ������ ������ ������� ���� ���� ����� �߻� �� �� ������, �ڳ��� 3G/wi-fi/GPS���� ���¿� ���� ���� ��ġ�� �ٸ� �� �ֽ��ϴ�.";	
			String buttonName = "Ȯ��";
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//�ٽ� �Ⱥ��̵��� ���ú����� ������.
					SharedPreferences LocalSave_firststart = getSharedPreferences("WizSafeLocalVal", 0);
					Editor edit_firststart = LocalSave_firststart.edit(); 
					edit_firststart.putString("appFirstStart", "1");
					edit_firststart.commit();
					
					//API ȣ�� ������ ����
		        	WizSafeDialog.showLoading(MainActivity.this);	//Dialog ���̱�
		        	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		    		thread.start();
				}
			});
			ad.show();
    	}else{
    		//API ȣ�� ������ ����
        	WizSafeDialog.showLoading(MainActivity.this);	//Dialog ���̱�
        	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
    		thread.start();
    	}
    }

	//�ڷΰ��� 2�� ������ ���� �������� onKeyDown����
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!mFlag) {
                Toast.makeText(MainActivity.this, "'�ڷ�'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT).show();
                mFlag = true;
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    //API ȣ�� ������ - ������
  	class CallGetCustomerInformationApiThread extends Thread{
  		public void run(){
  			String url = "";
  			HttpURLConnection urlConn;
  			BufferedReader br;
			String temp;
			
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this));
  				url = "http://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				
  				//�ʿ��� ������ Ÿ������ ����ȯ
  				customerApiResult = Integer.parseInt(resultCode);
  				
  				if(customerApiResult == 0){
  					//�ܿ�����Ʈ
  					myPoint = WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>");
					if(myPoint == null) myPoint = WizSafeSeed.seedEnc("0");
					myPoint = WizSafeSeed.seedDec(myPoint);
					stateSmsRecv = WizSafeParser.xmlParser_String(returnXML,"<ALARMSTATE>");
					
					//�����̼�
					relationType = new ArrayList<String>();
					parentCtn = new ArrayList<String>();
					childCtn = new ArrayList<String>();
					relationState = new ArrayList<String>();
					waitParentPhone = new ArrayList<String>();
					waitChildPhone = new ArrayList<String>();
					relationCount = WizSafeParser.xmlParser_String(returnXML,"<RELATION_COUNT>");
					if(Integer.parseInt(relationCount) > 0){
						relationType = WizSafeParser.xmlParser_List(returnXML, "<RELATION_TYPE>");
						parentCtn = WizSafeParser.xmlParser_List(returnXML, "<RELATION_PARENTCTN>");
						childCtn = WizSafeParser.xmlParser_List(returnXML, "<RELATION_CHILDCTN>");
						relationState = WizSafeParser.xmlParser_List(returnXML, "<RELATION_STATE>");
						
						//��� ������� �ڳฮ��Ʈ �Ǻ�
						//�ڳ����ϱ�� ����� �ڳุ �ش�ȴ�.
						for(int i = 0 ; i < relationType.size() ; i++){
							if(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this)).equals(parentCtn.get(i)) && "01".equals(relationType.get(i)) && "01".equals(relationState.get(i))){
								waitChildPhone.add(WizSafeSeed.seedDec(childCtn.get(i)));
							}
						}
						//��� ������� �θ𸮽�Ʈ �Ǻ�
						//�θ𸮽�Ʈ�� ���� �ڳ����ϱ�� �� �θ� + �θ𸮽�Ʈ���� STATE�� 01�ΰ���  ����.
						//��, ���� �ڳ�� ����� ��� ROW�� ����.
						for(int i = 0 ; i < relationType.size() ; i++){
							if(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this)).equals(childCtn.get(i)) && "01".equals(relationState.get(i))){
								waitParentPhone.add(WizSafeSeed.seedDec(parentCtn.get(i)));
							}
						}
					}
					
					//���� ������ ��ġ�� �����ؾ��ϴ��� �ƴ��� �Ǵ��Ͽ� ����
					//�ڳฮ��Ʈ ��ȣ�� �� ����ȣ�� �����鼭, �� �ش� ���°��� 02 �ΰ�� �ش� �ܸ��� ��ġ ���� �ϵ��� ����
					WizSafeUtil.setSendLocationUser(MainActivity.this, false);
					for(int i = 0 ; i < childCtn.size() ; i++){
						if(childCtn.get(i).equals(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this))) && "02".equals(relationState.get(i))){
							WizSafeUtil.setSendLocationUser(MainActivity.this, true);
						}
					}
  				}
  				
  				url = "http://www.heream.com/api/getNoticeInfo.jsp";
  				urlConn = (HttpURLConnection) new URL(url).openConnection();
  				br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode_notice = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String strSeq = WizSafeParser.xmlParser_String(returnXML,"<SEQ>");
  				String strTitle = WizSafeParser.xmlParser_String(returnXML,"<TITLE>");
  				String strContent = WizSafeParser.xmlParser_String(returnXML,"<CONTENT>");
  				//�ʿ��� ������ Ÿ������ ����ȯ
  				httpResult = Integer.parseInt(resultCode_notice);	
  				seq = strSeq;
  				title = strTitle;
  				content = strContent;
  				
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
  				if(customerApiResult == 0){
  					TextView textView1 = (TextView)findViewById(R.id.textView1);
  					TextView textView2 = (TextView)findViewById(R.id.textView2);
  					String myPhoneNumber = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(MainActivity.this));
  					String myPointStr = myPoint + "P";
  					textView1.setText("����ȣ(" + myPhoneNumber + ")");
  					textView2.setText(myPointStr);
  					
  					//sms ���� ���¸� ���ù���� ����
  					SharedPreferences LocalSave_smsState = getSharedPreferences("WizSafeLocalVal", 0);
					Editor edit_smsState = LocalSave_smsState.edit(); 
					edit_smsState.putString("stateSmsRecv", stateSmsRecv);
					edit_smsState.commit();
  					
  					LinearLayout btn01 = (LinearLayout)findViewById(R.id.btn1);
  			        btn01.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, ChildListActivity.class);
  							startActivity(intent);
  						} 
  					}); 
  			   
  			 	    LinearLayout btn02 = (LinearLayout)findViewById(R.id.btn2);
  			        btn02.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, ParentListActivity.class);
  							startActivity(intent);
  						}
  					});
  			         
  			        LinearLayout btn03 = (LinearLayout)findViewById(R.id.btn3);
  			        btn03.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, SetupActivity.class);
  							startActivity(intent);
  						}
  					});
  			        
  			        Button btn04 = (Button)findViewById(R.id.btn_charge_pt);
  			        btn04.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, PointChargeActivity.class);
  							startActivity(intent);
  						}
  					});
  			        
  			        
  			        //������� �ڳడ ������� �ڳฮ��Ʈ ���� ���ڷ� ǥ��
  			        LinearLayout wait_child = (LinearLayout)findViewById(R.id.wait_child);
  			        if(waitChildPhone.size() <= 0){
  			        	wait_child.setVisibility(View.INVISIBLE);
  			        }else{
  			        	wait_child.setVisibility(View.VISIBLE);
  			        }
  			        if(waitChildPhone.size() > 0){
  			        	//���� �����̹��� ǥ��
  			        	if(waitChildPhone.size() == 1){
  							wait_child.setBackgroundResource(R.drawable.img_num_1);
  						}else if(waitChildPhone.size() == 2){
  							wait_child.setBackgroundResource(R.drawable.img_num_2);
  						}else if(waitChildPhone.size() == 3){
  							wait_child.setBackgroundResource(R.drawable.img_num_3);
  						}else if(waitChildPhone.size() == 4){
  							wait_child.setBackgroundResource(R.drawable.img_num_4);
  						}else if(waitChildPhone.size() == 5){
  							wait_child.setBackgroundResource(R.drawable.img_num_5);
  						}else if(waitChildPhone.size() == 6){
  							wait_child.setBackgroundResource(R.drawable.img_num_6);
  						}else if(waitChildPhone.size() == 7){
  							wait_child.setBackgroundResource(R.drawable.img_num_7);
  						}else if(waitChildPhone.size() == 8){
  							wait_child.setBackgroundResource(R.drawable.img_num_8);
  						}else if(waitChildPhone.size() == 9){
  							wait_child.setBackgroundResource(R.drawable.img_num_9);
  						}else if(waitChildPhone.size() == 10){
  							wait_child.setBackgroundResource(R.drawable.img_num_10);
  						}else if(waitChildPhone.size() == 11){
  							wait_child.setBackgroundResource(R.drawable.img_num_11);
  						}else if(waitChildPhone.size() == 12){
  							wait_child.setBackgroundResource(R.drawable.img_num_12);
  						}else if(waitChildPhone.size() == 13){
  							wait_child.setBackgroundResource(R.drawable.img_num_13);
  						}else if(waitChildPhone.size() == 14){
  							wait_child.setBackgroundResource(R.drawable.img_num_14);
  						}else if(waitChildPhone.size() == 15){
  							wait_child.setBackgroundResource(R.drawable.img_num_15);
  						}else if(waitChildPhone.size() == 16){
  							wait_child.setBackgroundResource(R.drawable.img_num_16);
  						}else if(waitChildPhone.size() == 17){
  							wait_child.setBackgroundResource(R.drawable.img_num_17);
  						}else if(waitChildPhone.size() == 18){
  							wait_child.setBackgroundResource(R.drawable.img_num_18);
  						}else if(waitChildPhone.size() == 19){
  							wait_child.setBackgroundResource(R.drawable.img_num_19);
  						}else if(waitChildPhone.size() == 20){
  							wait_child.setBackgroundResource(R.drawable.img_num_20);
  						}
  			        }
  			        
  			        
  			        //������� �θ� ������� �θ𸮽�Ʈ ���� ���ڷ� ǥ�� �� �� ���â ���
  			        LinearLayout wait_parent = (LinearLayout)findViewById(R.id.wait_parent);
  			        if(waitParentPhone.size() <= 0){
  			        	wait_parent.setVisibility(View.INVISIBLE);
			        }else{
			        	wait_parent.setVisibility(View.VISIBLE);
			        }
  			        if(waitParentPhone.size() > 0){
  			        	//���� �����̹��� ǥ��
  			        	if(waitParentPhone.size() == 1){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_1);
	  			        }else if(waitParentPhone.size() == 2){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_2);
	  			        }else if(waitParentPhone.size() == 3){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_3);
	  			        }else if(waitParentPhone.size() == 4){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_4);
	  			        }else if(waitParentPhone.size() == 5){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_5);
	  			        }else if(waitParentPhone.size() == 6){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_6);
	  			        }else if(waitParentPhone.size() == 7){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_7);
	  			        }else if(waitParentPhone.size() == 8){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_8);
	  			        }else if(waitParentPhone.size() == 9){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_9);
	  			        }else if(waitParentPhone.size() == 10){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_10);
	  			        }else if(waitParentPhone.size() == 11){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_11);
	  			        }else if(waitParentPhone.size() == 12){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_12);
	  			        }else if(waitParentPhone.size() == 13){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_13);
	  			        }else if(waitParentPhone.size() == 14){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_14);
	  			        }else if(waitParentPhone.size() == 15){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_15);
	  			        }else if(waitParentPhone.size() == 16){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_16);
	  			        }else if(waitParentPhone.size() == 17){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_17);
	  			        }else if(waitParentPhone.size() == 18){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_18);
	  			        }else if(waitParentPhone.size() == 19){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_19);
	  			        }else if(waitParentPhone.size() == 20){
	  			        	wait_parent.setBackgroundResource(R.drawable.img_num_20);
	  			        }
  			        	
	  			        //�θ��� ��û�� ���� ���â
	  			        if(isParentAddAlert){
	  			        	AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
	  						ad.setTitle("�θ��Ͽ�û");
	  						ad.setMessage(waitParentPhone.size() + "���� �θ��� ��û�� �ֽ��ϴ�.\nȮ�� �Ͻðڽ��ϱ�?");
	  						ad.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog, int which) {
	  								Intent intent = new Intent(MainActivity.this, AllowLocation.class);
	  								if(waitParentPhone.size() > 0){  								
	  									intent.putExtra("allowPhoneNumber", waitParentPhone.get(0));
	  								}
	  	  							startActivity(intent);
	  							}
	  						});
	  						ad.setNegativeButton("���", new DialogInterface.OnClickListener(){
	  							public void onClick(DialogInterface dialog, int which) {
	  								isParentAddAlert = false;
	  							}
	  						});
	  						ad.show();
	  			        }
  			        }
					
  			        //�������� ���⿩�� ����
  			        //���ú����� Ȯ���Ͽ� �������� ���� ���θ� Ȯ��, ���ú��� �������� ��ȣ�� ���ٸ� ���������
  			        if(isShowPopView){
  			        	SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  	  					String noticePopVal = LocalSave.getString("noticePopVal","");
  	  					if(!noticePopVal.equals(seq)){	
  	  						if(httpResult == 0){
  		  						//�������� �˾� ����
  		  						ArrayList<String> noticeData = new ArrayList<String>();
  		  						noticeData.add(seq);
  		  						noticeData.add(title);
  		  						noticeData.add(content);
  		  						NoticePopView noticePopView = new NoticePopView((LinearLayout)findViewById(R.id.mainlayout), noticeData);
  		  						noticePopView.show();
  	  						}
  	  					}
  	  					isShowPopView = false;
  			        }
  					

  				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
					String title = "��Ʈ��ũ ����";	
					String message = "������ ��ȸ�� �����Ͽ����ϴ�.";	
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
  				//��ȸ����
				AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
				String title = "��Ʈ��ũ ����";	
				String message = "��Ʈ��ũ ������ �����ǰ� �ֽ��ϴ�.\n��Ʈ��ũ ���¸� Ȯ�� �Ŀ� �ٽ� �õ����ּ���. ";	
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
  	
  	private boolean chkGpsService() {
  		String gs = android.provider.Settings.Secure.getString(getContentResolver(),
  		android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
  		if (gs.indexOf("gps", 0) < 0 && gs.indexOf("network", 0) < 0) {
  			// GPS OFF �϶� Dialog ����� ���� ȭ������ Ƣ��ϴ�..
  			AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
  			gsDialog.setTitle("��ġ��ȸ ��� ����");
  			gsDialog.setMessage("��ġ��ȸ ����� �����Ǿ� �ֽ��ϴ�.\n�ܸ����� 'ȯ�漳�� > ��ġ(���) �� ����'���� '���� ��Ʈ��ũ ���' �Ǵ� 'GPS ���� ���'�� Ȱ��ȭ ��Ų �� �̿����ּ���.\n\n�غ�Ȱ��ȭ �� ��ġ��ȸ�� �Ұ��� �մϴ�.");
  			gsDialog.setPositiveButton("ȯ�漳��", new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog, int which) {
  					// GPS���� ȭ������ Ƣ���
  					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
  					intent.addCategory(Intent.CATEGORY_DEFAULT);
  					startActivity(intent);
  				}
  			});
  			gsDialog.setNegativeButton("�ݱ�", new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog, int which) {
  					// alert â �ݱ�
  					dialog.cancel();
  				}
  			}).create().show();
  			return false;
  		} else {
  			return true;
  		}
  	}
}