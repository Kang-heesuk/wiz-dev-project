package com.wiz.Activity;

import java.io.BufferedReader;
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
import com.wiz.util.WizSafeUtil;

public class NoticeListActivity extends Activity {

	//등록된 자녀의 이름
	ArrayList<NoticeDetail> noticeListArr = new ArrayList<NoticeDetail>();
	ArrayAdapter<NoticeDetail> noticeAdapter;
	
	//API 통신 성공유무 변수 
	int httpResult = 1;		//0 - 조회성공 , 그외 - 실패
	String[][] noticeList;

	NoticeListAdapter listAdapter;
	ListView listView;
    
	String selectedPosition;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        
        //API 호출 쓰레드 시작
    	//자녀 리스트를 가져온다.
    	WizSafeDialog.showLoading(NoticeListActivity.this);	//Dialog 보이기
        CallGetNoticeListApiThread thread = new CallGetNoticeListApiThread(); 
		thread.start();

        listAdapter = new NoticeListAdapter(this, R.layout.notice_list_customlist, noticeListArr);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    //리스트뷰를 리로드
    public void upDateListView(){
    	//재호출로써 커스텀 리스트 뷰를 다시 보여준다.
  		listAdapter = new NoticeListAdapter(this, R.layout.notice_list_customlist, noticeListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //API 호출 쓰레드
  	class CallGetNoticeListApiThread extends Thread{
  		public void run(){
  			try{
  				String url = "https://www.heream.com/api/getBoardList.jsp?part="+URLEncoder.encode("01");
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
  				ArrayList<String> regdate = WizSafeParser.xmlParser_List(returnXML,"<REGDATE>");
  				ArrayList<String> content = WizSafeParser.xmlParser_List(returnXML,"<CONTENT>");
  				
  				//복호화 하여 2차원배열에 담는다.
  				httpResult = Integer.parseInt(resultCode);
  				//조회해온 리스트 사이즈 만큼의 2차원배열을 선언한다.
  				noticeList = new String[title.size()][3];
  				if(title.size() > 0){
  					for(int i=0; i < title.size(); i++){
  						noticeList[i][0] = (String) title.get(i);
  					}
  				}
  				if(regdate.size() > 0){
  					for(int i=0; i < regdate.size(); i++){
  						noticeList[i][1] = WizSafeUtil.getDateFormat(regdate.get(i));
  					}
  				}
  				if(content.size() > 0){
  					for(int i=0; i < content.size(); i++){
  						noticeList[i][2] = (String) content.get(i);
  					}
  				}

  				//2차원 배열을 커스텀 어레이리스트에 담는다.
  		    	if(noticeList != null){
  			    	for(int i = 0 ; i < noticeList.length ; i++){
  			    		NoticeDetail addChildList = new NoticeDetail(noticeList[i][0], noticeList[i][1], noticeList[i][2]);
  			    		noticeListArr.add(addChildList);
  			    	}
  		    	}

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
  				if(httpResult == 0){
					//조회성공
  			        if(noticeListArr.size() > 0){
  			        	upDateListView();
  			        	
  			        }else{
  			        //조회실패
  						AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
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
					AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
					String title = "네트워크 오류";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
				String title = "네트워크 오류";	
				String message = "네트워크 접속이 지연되고 있습니다.\n네트워크 상태를 확인 후에 다시 시도해주세요.";	
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
    
    class NoticeDetail {
    	private String noticeTitle;
    	private String noticeRegdate;
    	private String noticeContent;
    	
    	public NoticeDetail (String noticeTitle, String noticeRegdate, String noticeContent){
    		this.noticeTitle = noticeTitle;
    		this.noticeRegdate = noticeRegdate;
    		this.noticeContent = noticeContent;
    	}
    	private String getNoticeTitle(){
			return noticeTitle;
    	}
    	private String getNoticeRegdate(){
			return noticeRegdate;
    	}
    	private String getNoticeContent(){
			return noticeContent;
    	}
    }
    
    
    class NoticeListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<NoticeDetail> arSrc;
    	int layout;
    	
    	//최초 커스텀리스트 뷰를 보여줄때
    	public NoticeListAdapter(Context context, int alayout, ArrayList<NoticeDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public NoticeDetail getItem(int position) {
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
			TextView noticeTitle = (TextView)convertView.findViewById(R.id.noticeTitle);
			TextView noticeRegdate = (TextView)convertView.findViewById(R.id.noticeRegdate);
			TextView noticeContent = (TextView)convertView.findViewById(R.id.noticeContent);
			final LinearLayout noticeTitleArea = (LinearLayout)convertView.findViewById(R.id.noticeTitleArea);
			final LinearLayout noticeContentArea = (LinearLayout)convertView.findViewById(R.id.noticeContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			noticeTitle.setText(arSrc.get(position).getNoticeTitle());
			noticeRegdate.setText(arSrc.get(position).getNoticeRegdate());
			noticeContent.setText(arSrc.get(position).getNoticeContent());
			
			if(arSrc.get(position).getNoticeTitle().equals(selectedPosition)){
				noticeContentArea.setVisibility(View.VISIBLE);
				//title부분 background 이미지 변경
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}else{
				noticeContentArea.setVisibility(View.GONE);
				//title부분 background 이미지 변경
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}
			
			noticeTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(noticeContentArea.getVisibility() == View.GONE){
							selectedPosition = arSrc.get(pos).getNoticeTitle();
							noticeContentArea.setVisibility(View.VISIBLE);
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							noticeContentArea.setVisibility(View.GONE);
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