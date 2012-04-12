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
						//부모리스트는 나를 자녀등록하기로 한 부모 + 부모리스트에서 STATE가 01인것을  본다.
						//즉, 나를 자녀로 등록한 모든 ROW를 본다.
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
  				
  				//결과를 XML 파싱하여 추출
  				String resultCode_notice = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				String strSeq = WizSafeParser.xmlParser_String(returnXML,"<SEQ>");
  				String strTitle = WizSafeParser.xmlParser_String(returnXML,"<TITLE>");
  				String strContent = WizSafeParser.xmlParser_String(returnXML,"<CONTENT>");
  				//필요한 데이터 타입으로 형변환
  				httpResult = Integer.parseInt(resultCode_notice);	
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
  				if(customerApiResult == 0){
  					
  					TextView textView1 = (TextView)findViewById(R.id.textView1);
  					TextView textView2 = (TextView)findViewById(R.id.textView2);
  					String myPhoneNumber = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(MainActivity.this));
  					String myPointStr = myPoint + "P";
  					textView1.setText("내번호(" + myPhoneNumber + ")");
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
  			        
  			        
  			        //대기중인 자녀가 있을경우 자녀리스트 옆에 숫자로 표시
  			        if(waitChildPhone.size() > 0){
  			        	LinearLayout wait_child = (LinearLayout)findViewById(R.id.wait_child);
  			        	//대기수 숫자이미지 표시
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
  			        
  			        
  			        //대기중인 부모가 있을경우 부모리스트 옆에 숫자로 표시 한 후 경고창 띄움
  			        if(waitParentPhone.size() > 0){
  			        	
  			        	LinearLayout wait_parent = (LinearLayout)findViewById(R.id.wait_parent);
  			        	//대기수 숫자이미지 표시
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
  						ad.setTitle("부모등록요청");
  						ad.setMessage(waitParentPhone.size() + "건의 부모등록 요청이 있습니다.\n확인 하시겠습니까?");
  						ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog, int which) {
  								Intent intent = new Intent(MainActivity.this, AllowLocation.class);
  								if(waitParentPhone.size() > 0){  								
  									intent.putExtra("allowPhoneNumber", waitParentPhone.get(0));
  								}
  	  							startActivity(intent);
  							}
  						});
  						ad.setNegativeButton("취소", new DialogInterface.OnClickListener(){
  							public void onClick(DialogInterface dialog, int which) {
  							}
  						});
  						ad.show();
  			        }
					
  			        //공지사항 노출여부 결정
  			        //로컬변수를 확인하여 공지사항 노출 여부를 확인, 로컬변수 공지사항 번호랑 같다면 띄우지않음
  					SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
  					String noticePopVal = LocalSave.getString("noticePopVal","");
  					if(!noticePopVal.equals(seq)){	
  						if(httpResult == 0){
	  						//공지사항 팝업 띄운다
	  						ArrayList<String> noticeData = new ArrayList<String>();
	  						noticeData.add(seq);
	  						noticeData.add(title);
	  						noticeData.add(content);
	  						NoticePopView noticePopView = new NoticePopView((LinearLayout)findViewById(R.id.mainlayout), noticeData);
	  						noticePopView.show();
  						}
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
  			//조회실패
				AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다. ";	
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
  		}
  	};
}