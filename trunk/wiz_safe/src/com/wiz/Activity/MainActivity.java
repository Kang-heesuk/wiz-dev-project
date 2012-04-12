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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	String relationCount = "";
	ArrayList<String> relationType;
	ArrayList<String> parentCtn;
	ArrayList<String> childCtn;
	ArrayList<String> relationState;
	
	//��ϴ������ �ڳฮ��Ʈ(��� ��ȣȭ �� ��ȣ)
	ArrayList<String> waitChildPhone = new ArrayList<String>();
	
	//��ϴ������ �θ𸮽�Ʈ(��� ��ȣȭ �� ��ȣ)
	ArrayList<String> waitParentPhone = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
     	//API ȣ�� ������ ����
    	WizSafeDialog.showLoading(MainActivity.this);	//Dialog ���̱�
    	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		thread.start();
        
    }
    
    //API ȣ�� ������ - ������
  	class CallGetCustomerInformationApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			String url = "";
  			HttpURLConnection urlConn;
  			BufferedReader br;
			String temp;
			
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this));
  				url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
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
					
					//�����̼�
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
  				}
  				
  				url = "https://www.heream.com/api/getNoticeInfo.jsp";
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
  				if(customerApiResult == 0){
  					
  					TextView textView1 = (TextView)findViewById(R.id.textView1);
  					TextView textView2 = (TextView)findViewById(R.id.textView2);
  					String myPhoneNumber = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(MainActivity.this));
  					String myPointStr = myPoint + "P";
  					textView1.setText("����ȣ(" + myPhoneNumber + ")");
  					textView2.setText(myPointStr);
  					
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
  							Intent intent = new Intent(MainActivity.this, JoinAcceptActivity.class);
  							startActivity(intent);
  							Toast.makeText(MainActivity.this, "open other site page!!", Toast.LENGTH_SHORT).show();
  						}
  					});
  			        
  			        
  			        //������� �ڳడ ������� �ڳฮ��Ʈ ���� ���ڷ� ǥ��
  			        if(waitChildPhone.size() > 0){
  			        	LinearLayout wait_child = (LinearLayout)findViewById(R.id.wait_child);
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
  			        if(waitParentPhone.size() > 0){
  			        	
  			        	LinearLayout wait_parent = (LinearLayout)findViewById(R.id.wait_parent);
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
  							}
  						});
  						ad.show();
  			        }
					
  			        //�������� ���⿩�� ����
  			        //���ú����� Ȯ���Ͽ� �������� ���� ���θ� Ȯ��, ���ú��� �������� ��ȣ�� ���ٸ� ���������
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

  				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
					String title = "��� ����";	
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
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�. ";	
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