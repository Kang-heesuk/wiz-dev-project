package com.wiz.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.wiz.Activity.R;


public class NoticePopView extends PopView{
	   public NoticePopView(View anchor) {
		super(anchor);
		// TODO Auto-generated constructor stub
		context  = anchor.getContext();
	      inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      root  = (ViewGroup) inflater.inflate(R.layout.popview, null);
	      setContentView(root);
	      mTrack    = (ViewGroup) root.findViewById(R.id.viewRow); //팝업 View의 내용을 추가한 LinearLayout
	      
	      //공지팝업 x 누른경우의 액션
	      Button btn_close = (Button)root.findViewById(R.id.btn_close);
	      btn_close.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
	      });
	      
	      
	      //공지사항 내용을 입력
	      TextView noticeStr = (TextView)root.findViewById(R.id.textView1);
	      noticeStr.setText("공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다공지사항입니다. \n공지사항입니다. \n공지사항입니다. \n공지사항입니다. \n공지사항입니다. \n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다.\n공지사항입니다. \n공지사항입니다. \n공지사항입니다. \n 아싸 퇴근시간이당 히히");
	      
	      //공지팝업 하단 체크  누른경우의 액션
	      final Button btn_check = (Button)root.findViewById(R.id.btn_check);
	      final Button btn_blank = (Button)root.findViewById(R.id.btn_blank);
	      Drawable alpha = btn_blank.getBackground();
	      alpha.setAlpha(0);
	      btn_check.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_check.setVisibility(View.GONE);
					btn_blank.setVisibility(View.VISIBLE);
				}
	      });
	      //공지팝업 하단 빈칸 누른경우의 액션
	      btn_blank.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					btn_blank.setVisibility(View.GONE);
					btn_check.setVisibility(View.VISIBLE);
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
	}