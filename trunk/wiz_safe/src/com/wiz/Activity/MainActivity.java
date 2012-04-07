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
import android.util.Log;
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
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this));
  				String url = "https://www.heream.com/api/getCustomerInformation.jsp?ctn=" + URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
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
						//�θ𸮽�Ʈ�� ���� �ڳ����ϱ�� �� �θ� + �θ𸮽�Ʈ���� STATE�� 02�� �ƴѰ��� ����.
						//��, ���� �ڳ�� ����� ��� ROW�� ����.
						for(int i = 0 ; i < relationType.size() ; i++){
							if(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this)).equals(childCtn.get(i)) && !"02".equals(relationState.get(i))){
								waitParentPhone.add(WizSafeSeed.seedDec(parentCtn.get(i)));
							}
						}
					}
  				}
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				Log.i("childList","��ȣ : " + e.toString());
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
    
    //API ȣ�� ������ - ��������
  	class CallGetNoticeInfoApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String url = "https://www.heream.com/api/getNoticeInfo.jsp";
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String strSeq = WizSafeParser.xmlParser_String(returnXML,"<SEQ>");
  				String strTitle = WizSafeParser.xmlParser_String(returnXML,"<TITLE>");
  				String strContent = WizSafeParser.xmlParser_String(returnXML,"<CONTENT>");
  				//�ʿ��� ������ Ÿ������ ����ȯ
  				httpResult = Integer.parseInt(resultCode);	
  				seq = strSeq;
  				title = strTitle;
  				content = strContent;
  				
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
  				if(customerApiResult == 0){
  					
  					TextView textView1 = (TextView)findViewById(R.id.textView1);
  					TextView textView2 = (TextView)findViewById(R.id.textView2);
  					String myPhoneNumber = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(MainActivity.this));
  					String myPointStr = myPoint + "P";
  					textView1.setText("����ȣ(" + myPhoneNumber + ")");
  					textView2.setText(myPointStr);
  					
  					
  					
  					Button btn01 = (Button)findViewById(R.id.btn1);
  			        btn01.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, ChildListActivity.class);
  							startActivity(intent);
  						} 
  					}); 
  			   
  			        Button btn02 = (Button)findViewById(R.id.btn2);
  			        btn02.setOnClickListener(new Button.OnClickListener() {
  						public void onClick(View v) {
  							Intent intent = new Intent(MainActivity.this, ParentListActivity.class);
  							startActivity(intent);
  						}
  					});
  			         
  			        Button btn03 = (Button)findViewById(R.id.btn3);
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
  			        
  			        //������� �θ� ������� �θ𸮽�Ʈ ���� ���ڷ� ǥ�� �� �� ���â ���
  			        if(waitParentPhone.size() > 0){
  			        	AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
  						ad.setTitle("�θ��Ͽ�û");
  						ad.setMessage(waitParentPhone.size() + "���� �θ��� ��û�� �ֽ��ϴ�.\nȮ�� �Ͻðڽ��ϱ�?");
  						ad.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog, int which) {
  								Toast.makeText(MainActivity.this, "��ġ��뵿�Ǿ�Ƽ��Ƽ�� �̵�", Toast.LENGTH_SHORT).show();
  							}
  						});
  						ad.setNegativeButton("���", new DialogInterface.OnClickListener(){
  							public void onClick(DialogInterface dialog, int which) {
  							}
  						});
  						ad.show();
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
  				//�ڵ鷯 ������
  			}else if(msg.what == 2){
  				//��ȸ����
				//���ú����� Ȯ���Ͽ� �������� ���� ���θ� Ȯ��
				SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
				String noticePopVal = LocalSave.getString("noticePopVal","");
				if(noticePopVal.equals(seq)){	
					//seq ��ȣ�� ���ú����� ������ �ִٸ� �������� �ȶ���
					//���ú����� seq�� ����
				SharedPreferences.Editor edit;
			    edit = LocalSave.edit();
				edit.putString("noticePopVal", "aaaaa");
				edit.commit();
				}else{
				// �������� �˾� ����
				ArrayList<String> noticeData = new ArrayList<String>();
				noticeData.add(seq);
				noticeData.add(title);
				noticeData.add(content);
				NoticePopView noticePopView = new NoticePopView((LinearLayout)findViewById(R.id.mainlayout), noticeData);
				noticePopView.show();
				super.handleMessage(msg);
				}
  			}
  		}
  	};
}