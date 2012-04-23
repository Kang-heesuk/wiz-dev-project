package com.wiz.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wiz.util.WizSafeDialog;

public class PointChargeActivity extends Activity {
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_charge);
       
    }
    
    //API ȣ�� ������ - ������
  //API ȣ�� ������
  	class callAuthSMSApiThread extends Thread{
  		public void run(){
  			
  			try{
  				/*
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(PointChargeActivity.this));
  				String url = "https://www.heream.com/api/sendAuthSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				*/
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
  				
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(PointChargeActivity.this);
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
  			}
  		}
  	};
}