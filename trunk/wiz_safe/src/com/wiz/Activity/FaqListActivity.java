package com.wiz.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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

public class FaqListActivity extends Activity {

	//FAQ����Ʈ
	ArrayList<faqDetail> faqList = new ArrayList<faqDetail>();
	ArrayAdapter<faqDetail> faqAdapter;
	

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_list);
        
        //��ϵ� �ڳ� ����Ʈ�� �������� ���μ����� �����Ѵ�. �����ϸ� arrayList�� ����.
        getFaqList();

        faqListAdapter listAdapter = new faqListAdapter(this, R.layout.faq_list_customlist, faqList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    
    //cms�� ������ ���������� �ҷ��´�.
    public void getFaqList() {
    	
    	ArrayList<String> title = new ArrayList<String>();
    	ArrayList<String> content = new ArrayList<String>();
    	
    	title.add("�ڳ�� �θ�� ������ ���ϴ� �ǰ���?");
    	content.add("�ڳ�� ��ġ�� ã�� ���� ������̸�, �θ�� �ش� �ڳ��� ��ġ�� ã�� �ִ� ����� �Դϴ�.");
    	
    	title.add("���δ����, ���ΰ����� �����ΰ���?");
    	content.add("���δ������ ���� ��ġ��뵿�Ǹ� ��û�� ���¸� ���ϸ�, �ش� �ڳడ ���� �³��� ���� ���� �����Դϴ�. ����Ʈ�ڳ�Ƚ��� �ڳ��� ��ġ��ȸ�� ���ؼ� �ش� �ڳ��� ��ġ��뵿�ǰ� �ʿ��ϸ�, �ڳడ �³��� ���� ������ ��ġ�� ã�� �� �����ϴ�.\n���ΰ����� �ش� �ڳడ �³��� ������ �����̸�, �ڳడ �³��� ���� ���� ��� ��ġ�� ã�� �� �����ϴ�.");
    	
    	title.add("��ȸ�Ұ��� �����ΰ���?");
    	content.add("���� �ش� �ڳ��� �޴����� �Ʒ��� ���·� ���� ��ġ��ȸ�� �Ұ��� ���¸� ���մϴ�.\n- GPS, 3G, Wi-fi ���¿� ���� ��ġ��ȸ �Ұ�\n- �޴��� ������ ����\n- ��ġ���� ����\n- ����Ʈ�ڳ�Ƚ� �� ����");
    	
    	title.add("����ġ, ������, �Ƚ����� �����ΰ���?");
    	content.add("����ġ��ȸ�� ���� �ڳ��� ��ġ�� �ٷ� ��ȸ�ϴ� ���� �Դϴ�.\n������� ������ �ð� ���� �ش� �ڳ��� ��ġ�� �����ÿ� ��ȸ�ϴ� �����Դϴ�.\n(�ش� �ڳ��� �̵� ��δ� ���� ���ؼ��� ��ȸ �����մϴ�.)\n�Ƚ����� �ش� �ڳడ ������ ������ ��Ż�� ��� �˷��帮�� �����Դϴ�.");
    	
    	title.add("�ڳ��� ��ġ�� ã�� �� �����ϴ�.");
    	content.add("�ڳ��� ��ġ�� ã�� ���ؼ��� �ڳ��� ��ġ��뵿�ǰ� �ʿ��ϸ�, ��ġ��뵿�Ǵ� �ڳ��� �޴��������� �����մϴ�. ���� ��ġ��뵿�� �Ŀ� ��ġ�� ��ȸ ���� �ʴ´ٸ� �Ʒ��� ���� ��ġ��ȸ�� �Ұ� �մϴ�.\n- GPS, 3G, Wi-fi ���¿� ���� ��ġ��ȸ �Ұ�\n- �޴��� ������ ����\n- ��ġ���� ����\n- ����Ʈ�ڳ�Ƚ� �� ����");
    	
    	title.add("�θ���� ��ġ�� ��ȸ �ߴٰ� ���ڰ� �ɴϴ�.");
    	content.add("����Ʈ�ڳ�Ƚ��� ����ġ������ ��ȣ �� �̿� � ���� ���� ��19������ �ǰ� �θ� �ڳ��� ��ġ��ȸ �� �ڳ࿡�� ��� �ش� ������ ���ڷ� �뺸�ϰ� �ֽ��ϴ�.");
    	
    	title.add("��ġ�� �̻��� ������ ���ɴϴ�.");
    	content.add("����Ʈ�ڳ�Ƚ��� �ڳ� �޴����� GPS, 3G, wi-fi�� ���¿� ���� ���� ��ġ�� �ٸ� �� �ֽ��ϴ�.\nWi-fi�� ��� �����⿡ ��ϵ� ������ ���� ���� �ٸ� ��ġ�� ������ �� ������ �̴� �����⿡ ��ϵ� ��ġ�������� �����̹Ƿ� ������ �Ұ��� �մϴ�.");
    	
    	title.add("������ ��ҿ� �־��µ� �������ٰ� ���Ϳ�.");
    	content.add("����Ʈ���� ��ġ ��ȸ�� GPS, 3G, Wi-fi�� ���� ��ȸ�� �Ǹ�, �ֺ��� �ټ��� �����⳪ ���� ���� � ���� ��ġ�� ������ ������ ���� �� �ֽ��ϴ�.");
    	
    	title.add("�Ϲ� �޴����� �̿��ϴµ� ����Ʈ�ڳ�Ƚ��� �̿��� �� ������?");
    	content.add("����Ʈ�ڳ�Ƚ��� �ڳ�, �θ� ��� ����Ʈ���� �̿��Ͽ��߸� ���� �̿��� �����ϸ�, ����� �ȵ���̵� ������ �����ǰ� �ֽ��ϴ�. (�������� ���� ����)");
    	
    	title.add("����Ʈ�ڳ�Ƚ��� �����ΰ���?");
    	content.add("����Ʈ�ڳ�Ƚ��� ����ġ��ȸ�� 1�� 3ȸ ����, �Ƚ��� ������ 1�� ������ �����̸�, �߰� �̿� �� ����Ʈ�� ���� �˴ϴ�.\n�� ����Ʈ�� ����� �����Ͽ� �̿��� �����մϴ�.");
    	
    	title.add("�� �̻� �θ�Բ� ��ġ�� �����ϰ� ���� �ʽ��ϴ�.");
    	content.add("����Ʈ�ڳ�Ƚ� ���ο� �ִ� ���θ𸮽�Ʈ������ ��ġ�� �����ϰ� ���� ���� �θ� ���� �� ��� �� �̻� ��ġ�� �������� �ʽ��ϴ�.");
    	
    	title.add("�θ���� �ټ� ��� �� �� �ֳ���?");
    	content.add("��, ���θ𸮽�Ʈ���� ���� �ټ��� �θ���� ��� �� �� �ֽ��ϴ�.");
    	
    	title.add("�����뿡 ��ġ ������ �����ϴ�.");
    	content.add("�ش� �ڳ��� �޴��� ���°� ��ġ�� ��ȸ�� �� ���ų�, ����Ʈ�� ������ ��� ��ġ ������ ���� �� �ֽ��ϴ�.");
    	
    	title.add("1���� ������ ������ ������ �� �� ������?");
    	content.add("�ڳ��� ������ ������ 1���ϱ����� ��ȸ�� �����ϸ�, 1���� ���� ������ ��ȸ�� �� �����ϴ�.");
    	
    	title.add("������ �ƴµ� ����Ʈ ������ �ȵƾ��.");
    	content.add("����Ʈ ������ �ȵ��� ��� 1:1���Ǹ� ���� �����Ͽ� �ֽø� Ȯ�� �Ŀ� ��ġ�Ͽ� �帮�ڽ��ϴ�.");
    	
    	title.add("ȸ�� Ż�� �ϰ� �;��.");
    	content.add("����Ʈ�ڳ�Ƚ��� ������ ȸ��Ż�� ����� ������, ���� ���� �Ͻø� �˴ϴ�. ������ ���������� ���ù����� ���� �����Ⱓ ���� �� �ڵ� �ı� �˴ϴ�.");
    	
    	faqList = new ArrayList<faqDetail>();
    	for(int i = 0 ; i < title.size() ; i++){
    		faqDetail addFaqList = new faqDetail(title.get(i), content.get(i));
    		faqList.add(addFaqList);
    	}

    }
    
    
    class faqDetail {
    	private String faqTitle;
    	private String faqContent;
    	
    	public faqDetail (String faqTitle, String faqContent){
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
    
    
    class faqListAdapter extends BaseAdapter {

    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<faqDetail> arSrc;
    	int layout;
    	
    	//���� Ŀ���Ҹ���Ʈ �並 �����ٶ�
    	public faqListAdapter(Context context, int alayout, ArrayList<faqDetail> aarSrc){
    		maincon = context;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarSrc;
    		layout = alayout;
    	}
    	
		public int getCount() {
			return arSrc.size();
		}

		public faqDetail getItem(int position) {
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
			TextView faqTitle = (TextView)convertView.findViewById(R.id.faqTitle);
			TextView faqContent = (TextView)convertView.findViewById(R.id.faqContent);
			final LinearLayout faqTitleArea = (LinearLayout)convertView.findViewById(R.id.faqTitleArea);
			final LinearLayout faqContentArea = (LinearLayout)convertView.findViewById(R.id.faqContentArea);
			final LinearLayout titleBackImgArea = (LinearLayout)convertView.findViewById(R.id.titleBackImgArea);
			
			
			faqTitle.setText(arSrc.get(position).getFaqTitle());
			faqContent.setText(arSrc.get(position).getFaqContent());
			
			faqTitleArea.setOnClickListener(
				new Button.OnClickListener(){
					public void onClick(View v) {
						
						if(faqContentArea.getVisibility() == 8){
							//gone ����(8)�̸�
							faqContentArea.setVisibility(View.VISIBLE);
							
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible ����(0)�̸�
							faqContentArea.setVisibility(View.GONE);
							
							//title�κ� background �̹��� ����
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn_on);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}
					}
				}
			);
			return convertView;
		}
    }
}