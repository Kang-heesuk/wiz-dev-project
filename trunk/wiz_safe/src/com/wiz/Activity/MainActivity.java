package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wiz.View.NoticePopView;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;

public class MainActivity extends Activity {
 
//공자사항에 사용될 정보 선언 
private int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
private String seq;
private String title;
private String content;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
     	//API 호출 쓰레드 시작
    	//class 최초 진입시 api 통신으로 위도경도를 가져온다.
    	WizSafeDialog.showLoading(MainActivity.this);	//Dialog 보이기
        CallGetNoticeInfoApiThread thread = new CallGetNoticeInfoApiThread(); 
		thread.start();
        
    }
    
    //API 호출 쓰레드
  	class CallGetNoticeInfoApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String url = "https://www.heream.com/api/getNoticeInfo.jsp";
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//결과를 XML 파싱하여 추출
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String strSeq = WizSafeParser.xmlParser_String(returnXML,"<SEQ>");
  				String strTitle = WizSafeParser.xmlParser_String(returnXML,"<TITLE>");
  				String strContent = WizSafeParser.xmlParser_String(returnXML,"<CONTENT>");
  				//필요한 데이터 타입으로 형변환
  				httpResult = Integer.parseInt(resultCode);	
  				seq = strSeq;
  				title = strTitle;
  				content = strContent;
  				
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
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
  				//핸들러 정상동작
  				if(httpResult == 0){
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
  			      
					//조회성공
  					//로컬변수를 확인하여 공지사항 노출 여부를 확인
  					SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  					String noticePopVal = LocalSave.getString("noticePopVal","");
  					if(noticePopVal.equals(seq)){	
  						//seq 번호를 로컬변수에 가지고 있다면 공지사항 안띄운다
  						//로컬변수에 seq값 셋팅
	  					SharedPreferences.Editor edit;
	  				    edit = LocalSave.edit();
	  					edit.putString("noticePopVal", "aaaaa");
	  					edit.commit();
  					}else{
	  					// 공지사항 팝업 띄운다
	  					ArrayList<String> noticeData = new ArrayList<String>();
	  					noticeData.add(seq);
	  					noticeData.add(title);
	  					noticeData.add(content);
	  					NoticePopView noticePopView = new NoticePopView((LinearLayout)findViewById(R.id.mainlayout), noticeData);
	  					noticePopView.show();
	  					super.handleMessage(msg);
  					}
				}else{
					//조회실패
				}
  			}else if(msg.what == 1){
  				//핸들러 비정상
  			}
  		}
  	};
}