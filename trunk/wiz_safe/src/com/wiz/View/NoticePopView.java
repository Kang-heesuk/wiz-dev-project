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
	    //mTrack    = (ViewGroup) root.findViewById(R.id.viewRow); //팝업 View의 내용을 추가한 LinearLayout
	    mTrack    = (ViewGroup) root.findViewById(R.id.popFullLayer); //팝업 View의 내용을 추가한 LinearLayout
	    
	    seq = noticeData.get(0);
	      
	    //공지팝업 x 누른경우의 액션
	    Button btn_close = (Button)root.findViewById(R.id.btn_close);
	    btn_close.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//팝업을 닫기전 다시보지않음 체크 여부에 따라 로컬값 셋팅
				if(chkImgChecked)setLocalValue4NoticePopFlag();
				dismiss();
			}
	    });
	      
	    //실행 단말기의 사이즈를 구한다. -test
	    Display display = ((WindowManager)anchor.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int width = display.getWidth();
	    int height = display.getHeight();
	      
	    //실행 단말기의 해상도를 구한다. -test
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    ((WindowManager)anchor.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
	    int deviceWidth = displayMetrics.widthPixels;
	    int deviceHeight =displayMetrics.heightPixels;
	    //실행 단말기의 밀도를 구한다. -test
	    int deviceDensity =displayMetrics.densityDpi;
	      
	    //공지사항 내용을 입력
	    String noticeContent = "";
			try {
				noticeContent = WizSafeUtil.replaceStr(noticeData.get(2),"☆","\n");
			} catch (Exception e) {}
	    TextView noticeStr = (TextView)root.findViewById(R.id.textView1);
	    noticeStr.setText("사이즈는??\n가로 : "+width+"\n세로 : "+height+"\n\n해상도는??\n가로 : "+deviceWidth+"\n세로 : "+deviceHeight+"\n밀도 : "+deviceDensity+"\n\n" + noticeContent);
	      
	    //공지팝업 하단 체크  누른경우의 액션
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
		preShow(); //상속받은 PopView의 메서드
		int[] location   = new int[2];
		anchor.getLocationOnScreen(location);
		root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.showAtLocation(this.anchor, Gravity.CENTER, 0, 0); //가운데 정렬 하여 보임
	}
   
  
	public void setLocalValue4NoticePopFlag(){
		//로컬변수에 seq값 셋팅
		SharedPreferences LocalSave = anchor.getContext().getSharedPreferences("WizSafeLocalVal", 0);
		SharedPreferences.Editor edit;
		edit = LocalSave.edit();
		edit.putString("noticePopVal", seq);
		edit.commit();
	}	
}