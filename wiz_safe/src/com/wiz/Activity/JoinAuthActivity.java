package com.wiz.Activity;
 
import java.io.BufferedReader;
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
import android.widget.EditText;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class JoinAuthActivity extends Activity {
    
	EditText editText1;
	String tempEditText = "";
	int authResult = 1;		//0 - ������ȣ��ġ �� ���Լ��� , �׿� - ����
	
	//API ȣ�� �� ����XML�� �޴� ����
	ArrayList<String> returnXML;
	
	long tempTime;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_auth);
        
        //�����ԵǸ� ������ȣ ������ ��Ȱ��ȭ
        renumBtnImgSetThread subThread = new renumBtnImgSetThread(); 
        subThread.start();
        tempTime = System.currentTimeMillis();
                
        editText1 = (EditText)findViewById(R.id.editText1);
        
        String ctn = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(JoinAuthActivity.this));
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText(ctn + " ��ȣ��\n������ȣ�� �߼۵Ǿ����ϴ�.");
        
        Button btn_join = (Button)findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				tempEditText = editText1.getText().toString();
				
				if("".equals(tempEditText.trim())){
					AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
					String title = "���� ����";	
					String message = "������ȣ�� �Է����ּ���.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
					return;
				}
				
				//API ȣ�� ������ ����
		        WizSafeDialog.showLoading(JoinAuthActivity.this);	//Dialog ���̱�
		        callAuthCompleteApiThread thread = new callAuthCompleteApiThread(); 
				thread.start();

			} 
		}); 
        
        final Button btn_renum = (Button)findViewById(R.id.btn_renum);
        btn_renum.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
  				
				//��Ȱ��ȭ ���� 2���� �����ٸ� Ȱ��ȭ ��Ŵ
				if(System.currentTimeMillis() - tempTime >= (1000 * 60 * 2)){
					
					btn_renum.setBackgroundResource(R.drawable.btn_certify_renum_on);
	  				
					//API ȣ�� ������ ����
			        WizSafeDialog.showLoading(JoinAuthActivity.this);	//Dialog ���̱�
			        callAuthSMSApiThread thread = new callAuthSMSApiThread(); 
					thread.start();
				}
			} 
		});
    }
    
  	
  	//API ȣ�� ������
  	class callAuthCompleteApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAuthActivity.this));
  				String enc_authNum = WizSafeSeed.seedEnc(tempEditText);
  				String url = "https://www.heream.com/api/authComplete.jsp?ctn=" + URLEncoder.encode(enc_ctn) + "&authNum=" + URLEncoder.encode(enc_authNum);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				authResult = Integer.parseInt(resultCode);

  				pHandler.sendEmptyMessage(0);
  				
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	//API ȣ�� ������
  	class callAuthSMSApiThread extends Thread{
  		public void run(){
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAuthActivity.this));
  				String url = "https://www.heream.com/api/sendAuthSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//����� �����߻�
  				pHandler.sendEmptyMessage(3);
  			}
  		}
  	}
  	
	//API ȣ�� ������
	class renumBtnImgSetThread extends Thread{
		public void run(){
			try{
				//120sec ��
				renumBtnImgSetThread.sleep(1000*60*2);

				pHandler.sendEmptyMessage(4);
			}catch(Exception e){				
				pHandler.sendEmptyMessage(5);
			}
		}
	}

  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				if(authResult == 0){
					//������ ���������� �̷���� ����̹Ƿ�, ���ù���� ���Ժκ��� ������Ʈ ���ش�.					
					SharedPreferences LocalSave;
					SharedPreferences.Editor edit;
			    	LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
					edit = LocalSave.edit();
					edit.putString("isAuthOkUser", "01");
					edit.commit();
			    	
			    	//���� ��Ƽ��Ƽ�� �̵�
					Intent intent = new Intent(JoinAuthActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}else{	
					AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
					String title = "���� ����";	
					String message = "������ȣ�� �ùٸ��� �ʽ��ϴ�.";	
					String buttonName = "Ȯ��";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
				}
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
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
  				
  				//API ȣ�� ������ ���� - 2�� üũ ������
		        renumBtnImgSetThread subThread = new renumBtnImgSetThread(); 
		        subThread.start();
  				
  				//����ð��� �ٽ� ����
  				tempTime = System.currentTimeMillis();
  			}else if(msg.what == 3){
  				AlertDialog.Builder ad = new AlertDialog.Builder(JoinAuthActivity.this);
				String title = "��� ����";	
				String message = "��� �� ������ �߻��Ͽ����ϴ�.";	
				String buttonName = "Ȯ��";
				ad.setTitle(title);
				ad.setMessage(message);
				ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ad.show();
  			}else if(msg.what == 4){
  				//2�� �ڰ� �Ͼ�� ��ư Ȱ��ȭ
  				Button btn_renum = (Button)findViewById(R.id.btn_renum);
  				btn_renum.setBackgroundResource(R.drawable.btn_certify_renum);
  			}else if(msg.what == 5){
  				//2�� ���� �ͼ��ǽ� ��ư ��Ȱ��ȭ
  				Button btn_renum = (Button)findViewById(R.id.btn_renum);
  				btn_renum.setBackgroundResource(R.drawable.btn_certify_renum);
  			}
  		}
  	};
     
}