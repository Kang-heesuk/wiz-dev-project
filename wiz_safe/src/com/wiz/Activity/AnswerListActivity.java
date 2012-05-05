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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeRecycleUtil;
import com.wiz.util.WizSafeUtil;

public class AnswerListActivity extends Activity {

	//등록된 1:1 문의 내용
	ArrayList<answerDetail> answerList = new ArrayList<answerDetail>();
	ArrayAdapter<answerDetail> answerAdapter;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] manToManList;
	
	//리스트 어뎁터 변수
	answerListAdapter listAdapter;
    ListView listView;
	
    String selectedPosition;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_list);
        
        Button btn_question = (Button)findViewById(R.id.btn_question);
        btn_question.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AnswerListActivity.this, QuestionActivity.class);
				startActivity(intent);
				finish();
			}
		});
        
        //API 호출 쓰레드 시작
    	WizSafeDialog.showLoading(AnswerListActivity.this);	//Dialog 보이기
    	CallGetManToManListApiThread thread = new CallGetManToManListApiThread(); 
		thread.start();
    }

    public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
    	listAdapter = new answerListAdapter(this, R.layout.answer_list_customlist, answerList);
        listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    class answerDetail {
    	private String state;
    	private String answerTitle;
    	private String answerRegdate;
    	private String questionContent;
    	private String answerContent;
    	
    	public answerDetail (String state, String answerRegdate, String answerTitle, String questionContent, String answerContent){
    		this.state = state;
    		this.answerRegdate = answerRegdate;
    		this.answerTitle = answerTitle;
    		this.questionContent = questionContent;
    		this.answerContent = answerContent;
    	}
    	private String getState(){
			return state;
    	}
    	private String getAnswerTitle(){
			return answerTitle;
    	}
    	private String getAnswerRegdate(){
			return answerRegdate;
    	}
    	private String getQuestionContent(){
			return questionContent;
    	}
    	private String getAnswerContent(){
			return answerContent;
    	}
    }
    
    class answerListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<answerDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public answerListAdapter(Context context, int alayout, ArrayList<answerDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public answerDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//각 위젯 정의
			TextView answerTitle = (TextView)convertView.findViewById(R.id.answerTitle);
			TextView answerRegdate = (TextView)convertView.findViewById(R.id.answerRegdate);
			TextView questionContent = (TextView)convertView.findViewById(R.id.questionContent);
			TextView answerContent = (TextView)convertView.findViewById(R.id.answerContent);
			final LinearLayout answerTitleArea = (LinearLayout)convertView.findViewById(R.id.answerTitleArea);
			final LinearLayout contentArea = (LinearLayout)convertView.findViewById(R.id.contentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			final LinearLayout questionContentArea = (LinearLayout)convertView.findViewById(R.id.questionContentArea);
			final LinearLayout answerContentArea = (LinearLayout)convertView.findViewById(R.id.answerContentArea);
			
			answerTitle.setText(arSrc.get(position).getAnswerTitle());

			//날짜 형식 변환
			String tempDate = arSrc.get(position).getAnswerRegdate();
			tempDate = tempDate.substring(4,6) + "/" + tempDate.substring(6,8);
			answerRegdate.setText(tempDate);
			questionContent.setText(arSrc.get(position).getQuestionContent());
			
			if("1".equals(arSrc.get(position).getState())){
				//답이 있으면
				answerContent.setText(arSrc.get(position).getAnswerContent());	
				Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
			}else{
				answerContentArea.setVisibility(View.GONE);
				Drawable titleAreaImg2 = getResources().getDrawable(R.drawable.q_list_line_btn1_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg2);
			}
			
			if(arSrc.get(pos).getAnswerTitle().equals(selectedPosition)){
				if("1".equals(arSrc.get(pos).getState())){
					Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2);
					titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
					contentArea.setVisibility(View.VISIBLE);
					questionContentArea.setVisibility(View.VISIBLE);
					answerContentArea.setVisibility(View.VISIBLE);
				}else{
					Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn1);
					titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
					contentArea.setVisibility(View.VISIBLE);
					questionContentArea.setVisibility(View.VISIBLE);
					answerContentArea.setVisibility(View.GONE);
				}
			}else{
				if("1".equals(arSrc.get(pos).getState())){
					Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2_on);
					titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
				}else{
					Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn1_on);
					titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
				}
				contentArea.setVisibility(View.GONE);
				questionContentArea.setVisibility(View.GONE);
				answerContentArea.setVisibility(View.GONE);
			}
			
			answerTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if(contentArea.getVisibility() == View.GONE){
							selectedPosition = arSrc.get(pos).getAnswerTitle();
							if("1".equals(arSrc.get(pos).getState())){
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
								contentArea.setVisibility(View.VISIBLE);
								questionContentArea.setVisibility(View.VISIBLE);
								answerContentArea.setVisibility(View.VISIBLE);
							}else{
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn1);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
								contentArea.setVisibility(View.VISIBLE);
								questionContentArea.setVisibility(View.VISIBLE);
								answerContentArea.setVisibility(View.GONE);
							}
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							if("1".equals(arSrc.get(pos).getState())){
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2_on);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
							}else{
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn1_on);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
							}
							contentArea.setVisibility(View.GONE);
							questionContentArea.setVisibility(View.GONE);
							answerContentArea.setVisibility(View.GONE);
							listAdapter.notifyDataSetChanged();
						}
					}
				}
			);
			
			return convertView;
		}
    }
    
    
    //API 호출 쓰레드
    class CallGetManToManListApiThread extends Thread{
    	public void run(){
    		InputStream is = null;
    		try{
    			String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(AnswerListActivity.this));
    			String url = "https://www.heream.com/api/manToManResponse.jsp?ctn="+ URLEncoder.encode(enc_ctn);
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
    			ArrayList<String> state = WizSafeParser.xmlParser_List(returnXML,"<STATE>");
    			ArrayList<String> regdate = WizSafeParser.xmlParser_List(returnXML,"<REGDATE>");
    			ArrayList<String> title = WizSafeParser.xmlParser_List(returnXML,"<TITLE>");
    			ArrayList<String> question = WizSafeParser.xmlParser_List(returnXML,"<QUESTION>");
    			ArrayList<String> answer = WizSafeParser.xmlParser_List(returnXML,"<ANSWER>");
    			
    			//복호화 하여 2차원배열에 담는다.
    			httpResult = Integer.parseInt(resultCode);
    			
    			//정상적으로 리스트를 가져온 경우 
    			if(httpResult == 0){
    				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
    				manToManList = new String[state.size()][5];
    				
    				if(state.size() > 0){
    					for(int i=0; i < state.size(); i++){
    						manToManList[i][0] = (String) state.get(i);
    					}
    				}
    				if(regdate.size() > 0){
    					for(int i=0; i <regdate.size(); i++){
    						manToManList[i][1] = (String) regdate.get(i);
    					}
    				}
    				if(title.size() > 0){
    					for(int i=0; i <title.size(); i++){
    						manToManList[i][2] = (String) title.get(i);
    					}
    				}
    				if(question.size() > 0){
    					for(int i=0; i <question.size(); i++){
    						manToManList[i][3] = (String) question.get(i);
    					}
    				}
    				if(answer.size() > 0){
    					for(int i=0; i <answer.size(); i++){
    						manToManList[i][4] = (String) answer.get(i);
    					}
    				}
    				//2차원 배열을 커스텀 어레이리스트에 담는다.
    				if(manToManList != null){
    					for(int i = 0 ; i < manToManList.length ; i++){
    						answerDetail addAnswerList = new answerDetail(manToManList[i][0], manToManList[i][1], manToManList[i][2], manToManList[i][3], manToManList[i][4]);
    						answerList.add(addAnswerList);
    					}
    				}
    			}
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
  				if(httpResult == 0){
  					upDateListView();
  				}else if(httpResult == 1){
  					AlertDialog.Builder ad = new AlertDialog.Builder(AnswerListActivity.this);
  					String title = "조회 오류";	
  					String message = "등록된 1:1문의가 없습니다. 1:1문의를 등록 후 이용 가능합니다.";	
  					String buttonName = "확인";
  					ad.setTitle(title);
  					ad.setMessage(message);
  					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							Intent intent = new Intent(AnswerListActivity.this, QuestionActivity.class);
  							startActivity(intent);
  							finish();
  						}
  					});
  					ad.show();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(AnswerListActivity.this);
  					String title = "통신 오류";	
  					String message = "통신 중 오류가 발생하였습니다.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(AnswerListActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
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