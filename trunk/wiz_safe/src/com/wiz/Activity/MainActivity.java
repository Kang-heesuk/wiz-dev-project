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
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
 
	//공자사항에 사용될 정보 선언 
	private int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	private String seq;
	private String title;
	private String content;
	
	//고객의 정보를 가져올 변수 선언(인코딩 포함됨)
	int customerApiResult = -1;
	String myPoint = "";
	String relationCount = "";
	ArrayList<String> relationType;
	ArrayList<String> parentCtn;
	ArrayList<String> childCtn;
	ArrayList<String> relationState;
	
	//등록대기중인 자녀리스트(모두 복호화 된 번호)
	ArrayList<String> waitChildPhone = new ArrayList<String>();
	
	//등록대기중인 부모리스트(모두 복호화 된 번호)
	ArrayList<String> waitParentPhone = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
     	//API 호출 쓰레드 시작
    	WizSafeDialog.showLoading(MainActivity.this);	//Dialog 보이기
    	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		thread.start();
        
    }
    
    //API 호출 쓰레드 - 고객정보
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
  				//결과를 XML 파싱하여 추출
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				
  				//필요한 데이터 타입으로 형변환
  				customerApiResult = Integer.parseInt(resultCode);
  				
  				if(customerApiResult == 0){
  					//잔여포인트
  					myPoint = WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>");
					if(myPoint == null) myPoint = WizSafeSeed.seedEnc("0");
					myPoint = WizSafeSeed.seedDec(myPoint);
					
					//릴레이션
					relationCount = WizSafeParser.xmlParser_String(returnXML,"<RELATION_COUNT>");
					if(Integer.parseInt(relationCount) > 0){
						relationType = WizSafeParser.xmlParser_List(returnXML, "<RELATION_TYPE>");
						parentCtn = WizSafeParser.xmlParser_List(returnXML, "<RELATION_PARENTCTN>");
						childCtn = WizSafeParser.xmlParser_List(returnXML, "<RELATION_CHILDCTN>");
						relationState = WizSafeParser.xmlParser_List(returnXML, "<RELATION_STATE>");
						
						//등록 대기중인 자녀리스트 판별
						//자녀등록하기로 등록한 자녀만 해당된다.
						for(int i = 0 ; i < relationType.size() ; i++){
							if(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this)).equals(parentCtn.get(i)) && "01".equals(relationType.get(i)) && "01".equals(relationState.get(i))){
								waitChildPhone.add(WizSafeSeed.seedDec(childCtn.get(i)));
							}
						}
						//등록 대기중인 부모리스트 판별
						//부모리스트는 나를 자녀등록하기로 한 부모 + 부모리스트에서 STATE가 02가 아닌것을 본다.
						//즉, 나를 자녀로 등록한 모든 ROW를 본다.
						for(int i = 0 ; i < relationType.size() ; i++){
							if(WizSafeSeed.seedEnc(WizSafeUtil.getCtn(MainActivity.this)).equals(childCtn.get(i)) && !"02".equals(relationState.get(i))){
								waitParentPhone.add(WizSafeSeed.seedDec(parentCtn.get(i)));
							}
						}
					}
  				}
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				Log.i("childList","번호 : " + e.toString());
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}finally{
  				if(is != null){ try{is.close();}catch(Exception e){} }
  			}
  		}
  	}
    
    //API 호출 쓰레드 - 공지사항
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
  				
  				pHandler.sendEmptyMessage(2);
  			}catch(Exception e){
  				//통신중 에러발생
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
  				//핸들러 정상동작
  				if(customerApiResult == 0){
  					
  					TextView textView1 = (TextView)findViewById(R.id.textView1);
  					TextView textView2 = (TextView)findViewById(R.id.textView2);
  					String myPhoneNumber = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(MainActivity.this));
  					String myPointStr = myPoint + "P";
  					textView1.setText("내번호(" + myPhoneNumber + ")");
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
  			        
  			        
  			        //대기중인 자녀가 있을경우 자녀리스트 옆에 숫자로 표시
  			        
  			        //대기중인 부모가 있을경우 부모리스트 옆에 숫자로 표시 한 후 경고창 띄움
  			        if(waitParentPhone.size() > 0){
  			        	AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
  						ad.setTitle("부모등록요청");
  						ad.setMessage(waitParentPhone.size() + "건의 부모등록 요청이 있습니다.\n확인 하시겠습니까?");
  						ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog, int which) {
  								Toast.makeText(MainActivity.this, "위치허용동의액티비티로 이동", Toast.LENGTH_SHORT).show();
  							}
  						});
  						ad.setNegativeButton("취소", new DialogInterface.OnClickListener(){
  							public void onClick(DialogInterface dialog, int which) {
  							}
  						});
  						ad.show();
  			        }
					
				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
					String title = "통신 오류";	
					String message = "고객정보 조회에 실패하였습니다.";	
					String buttonName = "확인";
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
  				//핸들러 비정상
  			}else if(msg.what == 2){
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
  			}
  		}
  	};
}