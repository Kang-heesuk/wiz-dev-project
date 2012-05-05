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

import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeRecycleUtil;

public class FaqListActivity extends Activity {

	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] faqList;
	
	//FAQ리스트
	ArrayList<FaqDetail> faqListArr = new ArrayList<FaqDetail>();
	ArrayAdapter<FaqDetail> faqAdapter;
	
	FaqListAdapter listAdapter;
	ListView listView;
	
	String selectedPosition;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_list);
        
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        //getFaqList();

        //API 호출 쓰레드 시작
    	//자녀 리스트를 가져온다.
    	WizSafeDialog.showLoading(FaqListActivity.this);	//Dialog 보이기
        CallGetNoticeListApiThread thread = new CallGetNoticeListApiThread(); 
		thread.start();

        listAdapter = new FaqListAdapter(this, R.layout.faq_list_customlist, faqListArr);
        listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
        
    }
    
    public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		listAdapter = new FaqListAdapter(this, R.layout.faq_list_customlist, faqListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //API 호출 쓰레드
  	class CallGetNoticeListApiThread extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String url = "https://www.heream.com/api/getBoardList.jsp?part="+URLEncoder.encode("03");
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
  				ArrayList<String> title = WizSafeParser.xmlParser_List(returnXML,"<TITLE>");  				
  				ArrayList<String> content = WizSafeParser.xmlParser_List(returnXML,"<CONTENT>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				faqList = new String[title.size()][2];
  				if(title.size() > 0){
  					for(int i=0; i < title.size(); i++){
  						faqList[i][0] = (String) title.get(i);
  					}
  				}
  				if(content.size() > 0){
  					for(int i=0; i < content.size(); i++){
  						faqList[i][1] = (String) content.get(i);
  					}
  				}

  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  		    	if(faqList != null){
  			    	for(int i = 0 ; i < faqList.length ; i++){
  			    		FaqDetail addChildList = new FaqDetail(faqList[i][0], faqList[i][1]);
  			    		faqListArr.add(addChildList);
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
  				//핸들러 정상동작
  				if(httpResult == 0){
					//조회성공
  			        if(faqListArr.size() > 0){
  			        	upDateListView();
  			        	
  			        }else{
  			        //조회실패
  						AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
  						String title = "공지사항";	
  						String message = "등록된 공지사항이 없습니다.";	
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
				}else{
					//조회실패
					AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
					String title = "통신 오류";	
					String message = "부모 리스트 정보를 조회할 수 없습니다.";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
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
    
    
    class FaqDetail {
    	private String faqTitle;
    	private String faqContent;
    	
    	public FaqDetail (String faqTitle, String faqContent){
    		this.faqTitle = faqTitle;
    		this.faqContent = faqContent;
    	}
    	private String getFaqTitle(){
			return faqTitle;
    	}

    	private String getFaqContent(){
			return faqContent;
    	}
    }
    
    
    class FaqListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<FaqDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public FaqListAdapter(Context context, int alayout, ArrayList<FaqDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public FaqDetail getItem(int position) {
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
			TextView faqTitle = (TextView)convertView.findViewById(R.id.faqTitle);
			TextView faqContent = (TextView)convertView.findViewById(R.id.faqContent);
			final LinearLayout faqTitleArea = (LinearLayout)convertView.findViewById(R.id.faqTitleArea);
			final LinearLayout faqContentArea = (LinearLayout)convertView.findViewById(R.id.faqContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			faqTitle.setText(arSrc.get(position).getFaqTitle());
			faqContent.setText(arSrc.get(position).getFaqContent());
			
			if(arSrc.get(position).getFaqTitle().equals(selectedPosition)){
				faqContentArea.setVisibility(View.VISIBLE);
				//title부분 background 이미지 변경
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}else{
				faqContentArea.setVisibility(View.GONE);
				//title부분 background 이미지 변경
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}
			
			faqTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if(faqContentArea.getVisibility() == View.GONE){
							selectedPosition = arSrc.get(pos).getFaqTitle();
							faqContentArea.setVisibility(View.VISIBLE);
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							faqContentArea.setVisibility(View.GONE);
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}
					}
				}
			);
			return convertView;
		}
    }
}