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

public class UseInfoListActivity extends Activity {

	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] useInfoList;

	UseInfoListAdapter listAdapter;
	ListView listView;
	
	String selectedPosition;
	
	//��ϵ� �ڳ��� �̸�
	ArrayList<UseInfoDetail> useInfoListArr = new ArrayList<UseInfoDetail>();
	ArrayAdapter<UseInfoDetail> useInfoAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useinfo_list);
        
        
        //�̿�ȳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        //API ȣ�� ������ ����
    	//�ڳ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(UseInfoListActivity.this);	//Dialog ���̱�
        CallGetUseInfoListApiThread thread = new CallGetUseInfoListApiThread(); 
		thread.start();

        listAdapter = new UseInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoListArr);
        listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		listAdapter = new UseInfoListAdapter(this, R.layout.useinfo_list_customlist, useInfoListArr);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
      
    
    //API ȣ�� ������
  	class CallGetUseInfoListApiThread extends Thread{
  		public void run(){
  			try{
  				String url = "https://www.heream.com/api/getBoardList.jsp?part="+URLEncoder.encode("02");
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
  				useInfoList = new String[title.size()][2];
  				if(title.size() > 0){
  					for(int i=0; i < title.size(); i++){
  						useInfoList[i][0] = (String) title.get(i);
  					}
  				}
  				if(content.size() > 0){
  					for(int i=0; i < content.size(); i++){
  						useInfoList[i][1] = (String) content.get(i);
  					}
  				}

  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  		    	if(useInfoList != null){
  			    	for(int i = 0 ; i < useInfoList.length ; i++){
  			    		UseInfoDetail useInfoDetail = new UseInfoDetail(useInfoList[i][0], useInfoList[i][1]);
  			    		useInfoListArr.add(useInfoDetail);
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
  			        if(useInfoListArr.size() > 0){
  			        	upDateListView();
  			        	
  			        }else{
  			        //��ȸ����
  						AlertDialog.Builder ad = new AlertDialog.Builder(UseInfoListActivity.this);
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
					AlertDialog.Builder ad = new AlertDialog.Builder(UseInfoListActivity.this);
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
  				AlertDialog.Builder ad = new AlertDialog.Builder(UseInfoListActivity.this);
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
    
    
    
    class UseInfoDetail {
    	private String useInfoTitle;
    	private String useInfoContent;
    	
    	public UseInfoDetail (String useInfoTitle,  String useInfoContent){
    		this.useInfoTitle = useInfoTitle;
    		this.useInfoContent = useInfoContent;
    	}
    	private String getUseInfoTitle(){
			return useInfoTitle;
    	}
    	
    	private String getUseInfoContent(){
			return useInfoContent;
    	}
    }
    
    
    class UseInfoListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<UseInfoDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public UseInfoListAdapter(Context context, int alayout, ArrayList<UseInfoDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public UseInfoDetail getItem(int position) {
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
			TextView useInfoTitle = (TextView)convertView.findViewById(R.id.useInfoTitle);
			TextView useInfoContent = (TextView)convertView.findViewById(R.id.useInfoContent);
			final LinearLayout useInfoTitleArea = (LinearLayout)convertView.findViewById(R.id.useInfoTitleArea);
			final LinearLayout useInfoContentArea = (LinearLayout)convertView.findViewById(R.id.useInfoContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			useInfoTitle.setText(arSrc.get(position).getUseInfoTitle());
			useInfoContent.setText(arSrc.get(position).getUseInfoContent());

			if(arSrc.get(position).getUseInfoTitle().equals(selectedPosition)){
				useInfoContentArea.setVisibility(View.VISIBLE);
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
				listAdapter.notifyDataSetChanged();
			}else{
				useInfoContentArea.setVisibility(View.GONE);
				//title�κ� background �̹��� ����
				Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg);
				listAdapter.notifyDataSetChanged();
			}
			
			useInfoTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(useInfoContentArea.getVisibility() == 8){
							selectedPosition = arSrc.get(pos).getUseInfoTitle();
							useInfoContentArea.setVisibility(View.VISIBLE);
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							listAdapter.notifyDataSetChanged();
						}else{
							selectedPosition = "";
							useInfoContentArea.setVisibility(View.GONE);
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