package com.wiz.View;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.Activity.R;
import com.wiz.util.WizSafeUtil;


public class NoticePopView extends PopView{

	String seq = "";
	boolean chkImgChecked = true;
	
	public NoticePopView(View anchor, ArrayList<String> noticeData) {
		super(anchor);
		// TODO Auto-generated constructor stub
		context  = anchor.getContext();
	    inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    root  = (ViewGroup) inflater.inflate(R.layout.popview, null);
	    setContentView(root);
	    //mTrack    = (ViewGroup) root.findViewById(R.id.viewRow); //�˾� View�� ������ �߰��� LinearLayout
	    mTrack    = (ViewGroup) root.findViewById(R.id.popFullLayer); //�˾� View�� ������ �߰��� LinearLayout
	    
	    seq = noticeData.get(0);
	      
	    //�����˾� x ��������� �׼�
	    Button btn_close = (Button)root.findViewById(R.id.btn_close);
	    btn_close.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//�˾��� �ݱ��� �ٽú������� üũ ���ο� ���� ���ð� ����
				if(chkImgChecked)setLocalValue4NoticePopFlag();
				dismiss();
			}
	    });
	      
	    //���� �ܸ����� ����� ���Ѵ�. -test
	    Display display = ((WindowManager)anchor.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int width = display.getWidth();
	    int height = display.getHeight();
	      
	    //���� �ܸ����� �ػ󵵸� ���Ѵ�. -test
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    ((WindowManager)anchor.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
	    int deviceWidth = displayMetrics.widthPixels;
	    int deviceHeight =displayMetrics.heightPixels;
	    //���� �ܸ����� �е��� ���Ѵ�. -test
	    int deviceDensity =displayMetrics.densityDpi;
	      
	    //�������� ������ �Է�
	    String noticeContent = "";
			try {
				noticeContent = WizSafeUtil.replaceStr(noticeData.get(2),"��","\n");
			} catch (Exception e) {}
	    TextView noticeStr = (TextView)root.findViewById(R.id.textView1);
	    noticeStr.setText("�������??\n���� : "+width+"\n���� : "+height+"\n\n�ػ󵵴�??\n���� : "+deviceWidth+"\n���� : "+deviceHeight+"\n�е� : "+deviceDensity+"\n\n" + noticeContent);
	      
	    //�����˾� �ϴ� üũ  ��������� �׼�
	    final LinearLayout checkLayout = (LinearLayout)root.findViewById(R.id.checkLayout);
	    final ImageView checkImg = (ImageView)root.findViewById(R.id.btn_check);
	    checkLayout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(checkImg.getVisibility() == View.VISIBLE){
					checkImg.setVisibility(View.GONE);
					chkImgChecked = false;
				}else{
					checkImg.setVisibility(View.VISIBLE);
					chkImgChecked = true;
				}
			}
	    });
	}
	   
   
	private final Context context;
	private final LayoutInflater inflater; 
	private final View root;
	private ViewGroup mTrack; 
 
	public void show () {
		preShow(); //��ӹ��� PopView�� �޼���
		int[] location   = new int[2];
		anchor.getLocationOnScreen(location);
		root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.showAtLocation(this.anchor, Gravity.CENTER, 0, 0); //��� ���� �Ͽ� ����
	}
   
  
	public void setLocalValue4NoticePopFlag(){
		//���ú����� seq�� ����
		SharedPreferences LocalSave = anchor.getContext().getSharedPreferences("WizSafeLocalVal", 0);
		SharedPreferences.Editor edit;
		edit = LocalSave.edit();
		edit.putString("noticePopVal", seq);
		edit.commit();
	}	
}