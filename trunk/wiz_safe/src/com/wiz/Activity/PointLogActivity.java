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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeRecycleUtil;
import com.wiz.util.WizSafeUtil;

public class PointLogActivity extends Activity {

	//��ϵ� �ڳ��� �̸�
	ArrayList<pointLogDetail> pointLogList = new ArrayList<pointLogDetail>();
	ArrayAdapter<pointLogDetail> pointLogAdapter;
 
	//API ��� �������� ���� 
	int httpResult = 1;		//0 - ��ȸ���� , �׿� - ����
	String[][] pointLog;
	
	pointLogListAdapter listAdapter;
	ListView listView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_log_list);
        
        //API ȣ�� ������ ����
    	//�ڳ� ����Ʈ�� �����´�.
    	WizSafeDialog.showLoading(PointLogActivity.this);	//Dialog ���̱�
    	CallGetPointLogApi thread = new CallGetPointLogApi(); 
		thread.start();
    }
    
    public void onDestroy() {
    	
    	WizSafeRecycleUtil.recursiveRecycle(getWindow().getDecorView());
    	System.gc();
    	super.onDestroy();
	}
    
    //����Ʈ�並 ���ε�
    public void upDateListView(){
    	//��ȣ��ν� Ŀ���� ����Ʈ �並 �ٽ� �����ش�.
  		listAdapter = new pointLogListAdapter(this, R.layout.point_log_customlist, pointLogList);
  		listView = (ListView)findViewById(R.id.list1);
    	listView.setAdapter(listAdapter);
    }
    
    class pointLogDetail {
    	private String regdate;
    	private String part;
    	private String state;
    	private String point;
    	
    	public pointLogDetail (String regdate, String part, String state, String point){
    		this.regdate = regdate;
    		this.part = part;
    		this.state = state;
    		this.point = point;
    	}
    	private String getRegdate(){
			return regdate;
    	}
    	private String getPart(){
			return part;
    	}
    	private String getState(){
			return state;
    	}
    	private String getPoint(){
			return point;
    	}
    }
    
    class pointLogListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<pointLogDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public pointLogListAdapter(Context context, int alayout, ArrayList<pointLogDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public pointLogDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//�� ���� ����
			TextView regdate = (TextView)convertView.findViewById(R.id.regdate);
			TextView part = (TextView)convertView.findViewById(R.id.part);
			TextView state = (TextView)convertView.findViewById(R.id.state);
			TextView point = (TextView)convertView.findViewById(R.id.point);
			
			//���� ����
			String strRegdate = arSrc.get(position).getRegdate();
			strRegdate = strRegdate.substring(4,6) + "/" + strRegdate.substring(6,8);
			regdate.setText(strRegdate);
			
			//���� ����
			if("01".equals(arSrc.get(position).getPart())){
				part.setText("����Ʈ");
			}else if("02".equals(arSrc.get(position).getPart())){
				part.setText("����ġ");
			}else if("03".equals(arSrc.get(position).getPart())){
				part.setText("������");
			}else if("04".equals(arSrc.get(position).getPart())){
				part.setText("�Ƚ���");
			}else{
				part.setText("-");
			}
			
			//����/��� ����
			if("0".equals(arSrc.get(position).getState())){
				state.setText("����");
				point.setText("+"+arSrc.get(position).getPoint()+"P");
			}else if("1".equals(arSrc.get(position).getState())){
				state.setText("���");
				point.setText("-"+arSrc.get(position).getPoint()+"P");
			}else{
				state.setText("-");
				point.setText(arSrc.get(position).getPoint()+"P");
			}
			
			return convertView;
		}
    }
    
    
    //API ȣ�� ������
  	class CallGetPointLogApi extends Thread{
  		public void run(){
  			InputStream is = null;
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(PointLogActivity.this));
  				String url = "https://www.heream.com/api/getPointLog.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				ArrayList<String> returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				//����� XML �Ľ��Ͽ� ����
  				ArrayList<String> regdate = new ArrayList<String>();
  				ArrayList<String> part = new ArrayList<String>();
  				ArrayList<String> point = new ArrayList<String>();
  				ArrayList<String> state = new ArrayList<String>();
  				String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
  				regdate = WizSafeParser.xmlParser_List(returnXML,"<POINT_REGDATE>");
  				part = WizSafeParser.xmlParser_List(returnXML,"<POINT_PART>");
  				point = WizSafeParser.xmlParser_List(returnXML,"<POINT_VALUE>");
  				state = WizSafeParser.xmlParser_List(returnXML,"<POINT_STATE>");
  				
  				//��ȣȭ �Ͽ� 2�����迭�� ��´�.
  				httpResult = Integer.parseInt(resultCode);
  				//��ȸ�ؿ� ����Ʈ ������ ��ŭ�� 2�����迭�� �����Ѵ�.
  				pointLog = new String[regdate.size()][4];
  				if(regdate.size() > 0){
  					for(int i=0; i < regdate.size(); i++){
  						pointLog[i][0] = regdate.get(i);
  					}
  				}
  				if(part.size() > 0){
  					for(int i=0; i < part.size(); i++){
  						pointLog[i][1] = part.get(i);
  					}
  				}
  				if(state.size() > 0){
  					for(int i=0; i < state.size(); i++){
  						pointLog[i][2] =  state.get(i);
  					}
  				}
  				if(point.size() > 0){
  					for(int i=0; i < point.size(); i++){
  						pointLog[i][3] =  WizSafeSeed.seedDec(point.get(i));
  					}
  				}

  				//2���� �迭�� Ŀ���� ��̸���Ʈ�� ��´�.
  				pointLogList = new ArrayList<pointLogDetail>();
  		    	if(pointLog != null){
  			    	for(int i = 0 ; i < pointLog.length ; i++){
  			    		pointLogDetail addPointLogList = new pointLogDetail(pointLog[i][0], pointLog[i][1], pointLog[i][2], pointLog[i][3]);
  			    		pointLogList.add(addPointLogList);
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
  				if(httpResult == 0){
  					upDateListView();
  				}else{
  					AlertDialog.Builder ad = new AlertDialog.Builder(PointLogActivity.this);
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
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(PointLogActivity.this);
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
}