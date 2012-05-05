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

	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] faqList;
	
	//FAQ����Ʈ
	ArrayList<FaqDetail> faqListArr = new ArrayList<FaqDetail>();
	ArrayAdapter<FaqDetail> faqAdapter;
	
	FaqListAdapter listAdapter;
	ListView listView;
	
	String selectedPosition;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_list);
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        //getFaqList();

        //API ȣ�� ������ ����
    	//�ڳ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(FaqListActivity.this);	//Dialog ���̱�
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
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		listAdapter = new FaqListAdapter(this, R.layout.faq_list_customlist, faqListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    //API ȣ�� ������
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
  				//����� XML �Ľ��Ͽ� ����
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				ArrayList<String> title = WizSafeParser.xmlParser_List(returnXML,"<TITLE>");  				
  				ArrayList<String> content = WizSafeParser.xmlParser_List(returnXML,"<CONTENT>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
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

  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  		    	if(faqList != null){
  			    	for(int i = 0 ; i < faqList.length ; i++){
  			    		FaqDetail addChildList = new FaqDetail(faqList[i][0], faqList[i][1]);
  			    		faqListArr.add(addChildList);
  			    	}
  		    	}

  		    	pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//����� �����߻�
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
  				//�ڵ鷯 ������
  				if(httpResult == 0){
					//��ȸ����
  			        if(faqListArr.size() > 0){
  			        	upDateListView();
  			        	
  			        }else{
  			        //��ȸ����
  						AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
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
					AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
					String title = "��� ����";	
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(FaqListActivity.this);
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
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
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
			
			//�� ���� ����
			TextView faqTitle = (TextView)convertView.findViewById(R.id.faqTitle);
			TextView faqContent = (TextView)convertView.findViewById(R.id.faqContent);
			final LinearLayout faqTitleArea = (LinearLayout)convertView.findViewById(R.id.faqTitleArea);
			final LinearLayout faqContentArea = (LinearLayout)convertView.findViewById(R.id.faqContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			faqTitle.setText(arSrc.get(position).getFaqTitle());
			faqContent.setText(arSrc.get(position).getFaqContent());
			
			if(arSrc.get(position).getFaqTitle().equals(selectedPosition)){
				faqContentArea.setVisibility(View.VISIBLE);
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}else{
				faqContentArea.setVisibility(View.GONE);
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
			}
			
			faqTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						if(faqContentArea.getVisibility() == View.GONE){
							selectedPosition = arSrc.get(pos).getFaqTitle();
							faqContentArea.setVisibility(View.VISIBLE);
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							faqContentArea.setVisibility(View.GONE);
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