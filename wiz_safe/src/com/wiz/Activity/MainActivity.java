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
	String stateSmsRecv = "";
	String relationCount = "";
	ArrayList<String> relationType;
	ArrayList<String> parentCtn;
	ArrayList<String> childCtn;
	ArrayList<String> relationState;
	
	//등록대기중인 자녀리스트(모두 복호화 된 번호)
	ArrayList<String> waitChildPhone = new ArrayList<String>();
	
	//등록대기중인 부모리스트(모두 복호화 된 번호)
	ArrayList<String> waitParentPhone = new ArrayList<String>();
	
	//대기중인 부모리스트에 관하여 설정창이 나오는지 안나오는지 컨트롤하는 변수
	boolean isParentAddAlert = true;
	
	//공지사항 팝업을 띄울지 말지에 관하여 컨트롤하는 변수(앱 실행시에만 뜨고, 안에서 메뉴 이동할때는 안뜨게 해주는 변수)
	boolean isShowPopView = true;
	
	//뒤로가기 두번 눌러야 종료되도록 설정하기 위한 변수
	private Handler mHandler;
	private boolean mFlag = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        chkGpsService();
        
        //인증된 사람인지 아닌지 판별하여 현재 위치를 전송해야 하는 USER 이면 데몬을 한번 실행시켜 위치를 전송한다.
        if(WizSafeUtil.isAuthOkUser(MainActivity.this)){
            if(WizSafeUtil.isSendLocationPassibleUser(MainActivity.this)){
            	//백그라운드에 DemonService라는 Sevice가 존재하는지 가져옴.
        		ComponentName cn = new ComponentName(getPackageName(), WizSafeGetLocation.class.getName());
        		//서비스 시작(위에서 중지 시킨 데몬을 시작시킴)
        		startService(new Intent().setComponent(cn));
            }
        }
        
        //뒤로가기 두번 눌러야 종료되도록 설정
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
    	
    	//앱 최초로 실행시 알림 경고(3G데이터 사용 시 고객님의 데이터 요금제에 따라...)
    	SharedPreferences LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
		String appFirstStartVal = LocalSave.getString("appFirstStart","0");
		
		//최초 실행시 경고창 발생 후 확인 누르면 통신
    	if("0".equals(appFirstStartVal)){
    		//조회실패
			AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
			String title = "알림";	
			String message = "3G 데이터 사용 시 고객님의 데이터 요금제에 따라 별도 요금이 발생 될 수 있으며, 자녀의 3G/wi-fi/GPS수신 상태에 따라 실제 위치와 다를 수 있습니다.";	
			String buttonName = "확인";
			ad.setTitle(title);
			ad.setMessage(message);
			ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//다신 안보이도록 로컬변수에 저장함.
					SharedPreferences LocalSave_firststart = getSharedPreferences("WizSafeLocalVal", 0);
					Editor edit_firststart = LocalSave_firststart.edit(); 
					edit_firststart.putString("appFirstStart", "1");
					edit_firststart.commit();
					
					//API 호출 쓰레드 시작
		        	WizSafeDialog.showLoading(MainActivity.this);	//Dialog 보이기
		        	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
		    		thread.start();
				}
			});
			ad.show();
    	}else{
    		//API 호출 쓰레드 시작
        	WizSafeDialog.showLoading(MainActivity.this);	//Dialog 보이기
        	CallGetCustomerInformationApiThread thread = new CallGetCustomerInformationApiThread(); 
    		thread.start();
    	}
    }

	//뒤로가기 2번 눌러야 어플 꺼지도록 onKeyDown설정
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!mFlag) {
                Toast.makeText(MainActivity.this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                mFlag = true;
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    //API 호출 쓰레드 - 고객정보
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
  				//결과를 XML 파싱하여 추출
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				
  				//필요한 데이터 타입으로 형변환
  				customerApiResult = Integer.parseInt(resultCode);
  				
  				if(customerApiResult == 0){
  					//잔여포인트
  					myPoint = WizSafeParser.xmlParser_String(returnXML,"<MYPOINT>");
					if(myPoint == null) myPoint = WizSafeSeed.seedEnc("0");
					myPoint = WizSafeSeed.seedDec(myPoint);
					stateSmsRecv = WizSafeParser.xmlParser_String(returnXML,"<ALARMSTATE>");
					
					//릴레이션
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
					
					//내가 서버로 위치를 제공해야하는지 아닌지 판단하여 셋팅
					//자녀리스트 번호에 내 폰번호가 있으면서, 그 해당 상태값이 02 인경우 해당 단말은 위치 제공 하도록 셋팅
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
  					
  					//sms 수신 상태를 로컬밸류에 셋팅
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
  			        
  			        
  			        //대기중인 자녀가 있을경우 자녀리스트 옆에 숫자로 표시
  			        LinearLayout wait_child = (LinearLayout)findViewById(R.id.wait_child);
  			        if(waitChildPhone.size() <= 0){
  			        	wait_child.setVisibility(View.INVISIBLE);
  			        }else{
  			        	wait_child.setVisibility(View.VISIBLE);
  			        }
  			        if(waitChildPhone.size() > 0){
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
  			        LinearLayout wait_parent = (LinearLayout)findViewById(R.id.wait_parent);
  			        if(waitParentPhone.size() <= 0){
  			        	wait_parent.setVisibility(View.INVISIBLE);
			        }else{
			        	wait_parent.setVisibility(View.VISIBLE);
			        }
  			        if(waitParentPhone.size() > 0){
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
  			        	
	  			        //부모등록 요청에 관한 경고창
	  			        if(isParentAddAlert){
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
	  								isParentAddAlert = false;
	  							}
	  						});
	  						ad.show();
	  			        }
  			        }
					
  			        //공지사항 노출여부 결정
  			        //로컬변수를 확인하여 공지사항 노출 여부를 확인, 로컬변수 공지사항 번호랑 같다면 띄우지않음
  			        if(isShowPopView){
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
  	  					isShowPopView = false;
  			        }
  					

  				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
					String title = "네트워크 오류";	
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
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요. ";	
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
  	
  	private boolean chkGpsService() {
  		String gs = android.provider.Settings.Secure.getString(getContentResolver(),
  		android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
  		if (gs.indexOf("gps", 0) < 0 && gs.indexOf("network", 0) < 0) {
  			// GPS OFF 일때 Dialog 띄워서 설정 화면으로 튀어봅니다..
  			AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
  			gsDialog.setTitle("위치조회 사용 설정");
  			gsDialog.setMessage("위치조회 기능이 해제되어 있습니다.\n단말기의 '환경설정 > 위치(장소) 및 보안'에서 '무선 네트워크 사용' 또는 'GPS 위성 사용'을 활성화 시킨 후 이용해주세요.\n\n※비활성화 시 위치조회가 불가능 합니다.");
  			gsDialog.setPositiveButton("환경설정", new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog, int which) {
  					// GPS설정 화면으로 튀어요
  					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
  					intent.addCategory(Intent.CATEGORY_DEFAULT);
  					startActivity(intent);
  				}
  			});
  			gsDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog, int which) {
  					// alert 창 닫기
  					dialog.cancel();
  				}
  			}).create().show();
  			return false;
  		} else {
  			return true;
  		}
  	}
}