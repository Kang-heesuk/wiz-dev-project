package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AnswerListActivity extends Activity {

	//��ϵ� �ڳ��� �̸�
	ArrayList<answerDetail> answerList = new ArrayList<answerDetail>();
	
	ArrayAdapter<answerDetail> answerAdapter;
	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_list);
        
        
        Button btn_question = (Button)findViewById(R.id.btn_question);
        btn_question.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
				//Intent intent = new Intent(AnswerListActivity.this, QuestionActivity.class);
				//startActivity(intent);
			}
		});

        
        //�ش� ���� ����� 1:1���ǿ� ���� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getAnswerList();

        answerListAdapter listAdapter = new answerListAdapter(this, R.layout.answer_list_customlist, answerList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
        
    }
    
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getAnswerList() {
    	
    	String[][] tempHardCoding = {{"�ڳ� ����� ��� �ϳ���?","01/06","��ġ�� ã�� �� �ִ� �ڳడ �����ϴ�. �Ʒ� \"�ڳ����ϱ�\" �޴����� ����� �ּ���.", "1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�."},{"�θ� ����� ���� ���߾��.","03/26","22222222222222222105484565", "1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�.1:1 ���ǿ� ���� �亯�Դϴ�."},{"����Ʈ�� �����մϴ�.","04/26","3333333333333333333333333333331024882698", ""},{"�Ƚ��� ������ ���ؼ� ������ �ּ���.","03/11","44444444444444444441084464664", "1:1 ���ǿ� ���� �亯�Դϴ�."},{"���� ������̿���.","01/26","5555555555555441084464664", ""}};
    	
    	for(int i = 0 ; i < tempHardCoding.length ; i++){
    		
    		answerDetail addAnswerList = new answerDetail(tempHardCoding[i][0], tempHardCoding[i][1], tempHardCoding[i][2], tempHardCoding[i][3]);
    		answerList.add(addAnswerList);
    	}

    }
    
    
    class answerDetail {
    	private String answerTitle;
    	private String answerRegdate;
    	private String questionContent;
    	private String answerContent;
    	
    	public answerDetail (String answerTitle, String answerRegdate, String questionContent, String answerContent){
    		this.answerTitle = answerTitle;
    		this.answerRegdate = answerRegdate;
    		this.questionContent = questionContent;
    		this.answerContent = answerContent;
    	}
    	private String getAnswerTitle(){
			return answerTitle;
    	}
    	private String getAnswerRegdate(){
			return answerRegdate;
    	}
    	private String getQuestionContent(){
			return questionContent;
    	}
    	private String getAnswerContent(){
			return answerContent;
    	}
    }
    
    
    class answerListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<answerDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public answerListAdapter(Context context, int alayout, ArrayList<answerDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public answerDetail getItem(int position) {
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			//�亯 ���� ��
			final boolean answerExist = false;
			
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			//�� ���� ����
			TextView answerTitle = (TextView)convertView.findViewById(R.id.answerTitle);
			TextView answerRegdate = (TextView)convertView.findViewById(R.id.answerRegdate);
			TextView questionContent = (TextView)convertView.findViewById(R.id.questionContent);
			TextView answerContent = (TextView)convertView.findViewById(R.id.answerContent);
			final LinearLayout answerTitleArea = (LinearLayout)convertView.findViewById(R.id.answerTitleArea);
			final LinearLayout contentArea = (LinearLayout)convertView.findViewById(R.id.contentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			//�������뿵��
			final LinearLayout questionContentArea = (LinearLayout)convertView.findViewById(R.id.questionContentArea);
			//�亯���뿵��
			final LinearLayout answerContentArea = (LinearLayout)convertView.findViewById(R.id.answerContentArea);
			
			answerTitle.setText(arSrc.get(position).getAnswerTitle());
			answerRegdate.setText(arSrc.get(position).getAnswerRegdate());
			questionContent.setText(arSrc.get(position).getQuestionContent());
			
			if(!"".equals(arSrc.get(position).getAnswerContent())){
				//���� ������
				answerContent.setText(arSrc.get(position).getAnswerContent());	
				Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
			}else{
				answerContentArea.setVisibility(View.GONE);
				Drawable titleAreaImg2 = getResources().getDrawable(R.drawable.q_list_line_btn1_on);
				titleBackImgArea.setBackgroundDrawable(titleAreaImg2);
			}
			
			
			answerTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(contentArea.getVisibility() == 8){
							//gone ����(8)�̸�
							contentArea.setVisibility(View.VISIBLE);
							
							//title�κ� background �̹��� ����
							if(!"".equals(arSrc.get(pos).getAnswerContent())){
								//���� ������
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
							}else{
								Drawable titleAreaImg2 = getResources().getDrawable(R.drawable.q_list_line_btn1);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg2);
							}
							
						}else{
							//visible ����(0)�̸�
							contentArea.setVisibility(View.GONE);
							
							//title�κ� background �̹��� ����
							if(!"".equals(arSrc.get(pos).getAnswerContent())){
								Drawable titleAreaImg1 = getResources().getDrawable(R.drawable.q_list_line_btn2_on);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg1);
							}else{
								Drawable titleAreaImg2 = getResources().getDrawable(R.drawable.q_list_line_btn1_on);
								titleBackImgArea.setBackgroundDrawable(titleAreaImg2);
							}
						}
					}
				}
			);
			return convertView;
		}
    }
}