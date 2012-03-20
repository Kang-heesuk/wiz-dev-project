package com.wiz.View;

import android.content.Context;
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
	      mTrack    = (ViewGroup) root.findViewById(R.id.viewRow); //�˾� View�� ������ �߰��� LinearLayout
	      
	      //�����˾� x ��������� �׼�
	      Button btn_close = (Button)root.findViewById(R.id.btn_close);
	      btn_close.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
	      });
	      
	      
	      //�������� ������ �Է�
	      TextView noticeStr = (TextView)root.findViewById(R.id.textView1);
	      noticeStr.setText("�� ���̹� �� ¥���� �̰� ���� �밡���� \n �ƽ� ��ٽð��̴� ����");
	      
	      //�����˾� x ��������� �׼�
	      Button btn_check = (Button)root.findViewById(R.id.btn_check);
	      btn_close.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					v.setVisibility(View.VISIBLE);
					dismiss();
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
	}