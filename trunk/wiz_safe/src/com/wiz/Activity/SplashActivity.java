package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class SplashActivity extends Activity {
	
	// ���� ���� �� �۽���� Ȯ��//////////////////////////////////////////////
	// 01 - �ȵ���̵� ����
	// 02 - Ƽ�����
	// 03 - �÷�����
	// 04 - �Ｚ�۽����
	// 05 - ���÷����۸���
	String AppVersion = "1.0";
	String Market = "01";
	// ////////////////////////////////////////////////////////////////////

	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	//API ȣ�� �� RESULT_CD �κ��� �޴� ����
	int addApiResult = -1;
	
	//���������ν� API ����� �������� �����Ѵ�.
	String result_version = "";
	String result_market = "";
	String result_usable = "";
	String result_update_code_url = "";	
	String result_comment1 = "";
	
	String authProcessValue;
	String splashValue;
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;

    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        
        //������ ������ �� �ִ� ȯ������ Ȯ��
        String OSver = android.os.Build.VERSION.RELEASE;
        int APIver = android.os.Build.VERSION.SDK_INT;
        
        if((OSver.indexOf("2.1") > -1 || OSver.indexOf("2.2") > -1) || APIver < 7){
        	AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
			String title = "�˸�";	
			String message = "�ش� �޴����� OS ���������� ���� �̿��� �Ұ��մϴ�.\nOS ������Ʈ �� �̿����ּ���.";	
			String buttonName = "Ȯ��";
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			ad.show();
        }else{
        	CallAppVersionCheckThread thread = new CallAppVersionCheckThread();
			thread.start();       
        }
    }
    
    //API ȣ�� ������
  	class CallAppVersionCheckThread extends Thread{
  		public void run(){
  			try{
  				String url = "https://www.heream.com/api/checkVersion.jsp?appVersion="+ URLEncoder.encode(AppVersion) +"&appStore="+ URLEncoder.encode(Market);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{	
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");  
  				addApiResult = Integer.parseInt(resultCode);
  				
  				if(addApiResult == 0 || addApiResult == 1){
  					result_version = WizSafeParser.xmlParser_String(returnXML,"<RESULT_VERSION>");
  					result_market = WizSafeParser.xmlParser_String(returnXML,"<RESULT_MARKET>");
  					result_usable = WizSafeParser.xmlParser_String(returnXML,"<RESULT_USABLE>");
  					result_update_code_url = WizSafeParser.xmlParser_String(returnXML,"<RESULT_UPDATE_CODE_URL>");
  					result_comment1 = WizSafeParser.xmlParser_String(returnXML,"<RESULT_COMMENT1>");
  					
  					if("1".equals(result_usable)){
  						pHandler.sendEmptyMessage(0);
  					}else{
  						pHandler.sendEmptyMessage(1);
  					}
  				}else{
  					pHandler.sendEmptyMessage(2);
  				}
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			if(msg.what == 0){
  				//������ ������� �ƴ��� �Ǻ�
  		        if(WizSafeUtil.isAuthOkUser(SplashActivity.this)){
  		        	
  		        	//��� ȭ���� �����ְ� ���� ��Ƽ��Ƽ�� �̵�
  		       		Timer timer = new Timer();
  		       		TimerTask myTask = new TimerTask(){
  		       			public void run(){
  		       				//main splash ��Ƽ��Ƽ�� ȣ��	
  		       		    	startActivity(new Intent(SplashActivity.this, MainActivity.class));
  		       		    	SplashActivity.this.finish();
  		       			}
  		       		};
  		       		
  		       		//3�� �� ����
  		       		timer.schedule(myTask, 3000);
  		        	
  		        }else{
  		        	
  		        	//��� ȭ���� �����ְ� �����ϱ� ��Ƽ��Ƽ�� �̵�
  		       		Timer timer = new Timer();
  		       		TimerTask myTask = new TimerTask(){
  		       			public void run(){
  		       				//main splash ��Ƽ��Ƽ�� ȣ��	
  		       		    	startActivity(new Intent(SplashActivity.this, JoinAcceptActivity.class));
  		       		    	SplashActivity.this.finish();
  		       			}
  		       		};
  		       		
  		       		//3�� �� ����
  		       		timer.schedule(myTask, 3000);
  		        }
  			}else if(msg.what == 1){
  				AlertDialog.Builder submitAlert = new AlertDialog.Builder(SplashActivity.this);
				submitAlert.setTitle("�˸�");
				submitAlert.setMessage("���ο� ������Ʈ��  �ֽ��ϴ�. ������Ʈ ���� �̿��� �ּ���.");
				submitAlert.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						Uri u = Uri.parse(result_update_code_url);
						i.setData(u);
						startActivity(i);
						finish();					
					}
				});
				submitAlert.setNegativeButton("���", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				submitAlert.show();
  			}else if(msg.what == 2){
  				AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
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
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
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