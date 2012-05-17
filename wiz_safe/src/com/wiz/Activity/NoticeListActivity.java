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

	//��ϵ� �ڳ��� �̸�
	ArrayList<NoticeDetail> noticeListArr = new ArrayList<NoticeDetail>();
	ArrayAdapter<NoticeDetail> noticeAdapter;
	
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] noticeList;

	NoticeListAdapter listAdapter;
	ListView listView;
    
	String selectedPosition;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        
        //API ȣ�� ������ ����
    	//�ڳ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(NoticeListActivity.this);	//Dialog ���̱�
        CallGetNoticeListApiThread thread = new CallGetNoticeListApiThread(); 
		thread.start();

        listAdapter = new NoticeListAdapter(this, R.layout.notice_list_customlist, noticeListArr);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		listAdapter = new NoticeListAdapter(this, R.layout.notice_list_customlist, noticeListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //API ȣ�� ������
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
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				ArrayList<String> title = WizSafeParser.xmlParser_List(returnXML,"<TITLE>");
  				ArrayList<String> regdate = WizSafeParser.xmlParser_List(returnXML,"<REGDATE>");
  				ArrayList<String> content = WizSafeParser.xmlParser_List(returnXML,"<CONTENT>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
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

  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  		    	if(noticeList != null){
  			    	for(int i = 0 ; i < noticeList.length ; i++){
  			    		NoticeDetail addChildList = new NoticeDetail(noticeList[i][0], noticeList[i][1], noticeList[i][2]);
  			    		noticeListArr.add(addChildList);
  			    	}
  		    	}

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
  				//�ڵ鷯 ������
  				if(httpResult == 0){
					//��ȸ����
  			        if(noticeListArr.size() > 0){
  			        	upDateListView();
  			        	
  			        }else{
  			        //��ȸ����
  						AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
  						String title = "��������";	
  						String message = "��ϵ� ���������� �����ϴ�.";	
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
				}else{
					//��ȸ����
					AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
					String title = "��Ʈ��ũ ����";	
					String message = "�θ� ����Ʈ ������ ��ȸ�� �� �����ϴ�.";	
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
  			}else if(msg.what == 1){
  				//�ڵ鷯 ������
  				AlertDialog.Builder ad = new AlertDialog.Builder(NoticeListActivity.this);
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
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
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
			
			//�� ���� ����
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
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}else{
				noticeContentArea.setVisibility(View.GONE);
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}
			
			noticeTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(noticeContentArea.getVisibility() == View.GONE){
							selectedPosition = arSrc.get(pos).getNoticeTitle();
							noticeContentArea.setVisibility(View.VISIBLE);
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							noticeContentArea.setVisibility(View.GONE);
							//title�κ� background �̹��� ����
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