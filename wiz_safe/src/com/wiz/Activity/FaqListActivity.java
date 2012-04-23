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

	//FAQ리스트
	ArrayList<faqDetail> faqList = new ArrayList<faqDetail>();
	ArrayAdapter<faqDetail> faqAdapter;
	

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_list);
        
        //등록된 자녀 리스트를 가져오는 프로세스를 진행한다. 진행하면 arrayList에 담긴다.
        getFaqList();

        faqListAdapter listAdapter = new faqListAdapter(this, R.layout.faq_list_customlist, faqList);
        ListView listView = (ListView)findViewById(R.id.list1);
        listView.setAdapter(listAdapter);
    }
    
    
    //cms와 연동된 공지사항을 불러온다.
    public void getFaqList() {
    	
    	ArrayList<String> title = new ArrayList<String>();
    	ArrayList<String> content = new ArrayList<String>();
    	
    	title.add("자녀랑 부모는 누구를 말하는 건가요?");
    	content.add("자녀는 위치를 찾고 싶은 사용자이며, 부모는 해당 자녀의 위치를 찾고 있는 사용자 입니다.");
    	
    	title.add("승인대기중, 승인거절은 무엇인가요?");
    	content.add("승인대기중은 현재 위치허용동의를 요청한 상태를 말하며, 해당 자녀가 아직 승낙을 하지 않은 상태입니다. 스마트자녀안심은 자녀의 위치조회를 위해서 해당 자녀의 위치허용동의가 필요하며, 자녀가 승낙을 하지 않으면 위치를 찾을 수 없습니다.\n승인거절은 해당 자녀가 승낙을 거절한 상태이며, 자녀가 승낙을 하지 않을 경우 위치를 찾을 수 없습니다.");
    	
    	title.add("조회불가란 무엇인가요?");
    	content.add("현재 해당 자녀의 휴대폰이 아래의 상태로 인해 위치조회가 불가능 상태를 말합니다.\n- GPS, 3G, Wi-fi 상태에 따른 위치조회 불가\n- 휴대폰 전원이 꺼짐\n- 위치숨김 상태\n- 스마트자녀안심 앱 삭제");
    	
    	title.add("현위치, 발자취, 안심존은 무엇인가요?");
    	content.add("현위치조회는 현재 자녀의 위치를 바로 조회하는 서비스 입니다.\n발자취는 지정된 시간 동안 해당 자녀의 위치를 매정시에 조회하는 서비스입니다.\n(해당 자녀의 이동 경로는 앱을 통해서만 조회 가능합니다.)\n안심존은 해당 자녀가 설정된 지역을 이탈할 경우 알려드리는 서비스입니다.");
    	
    	title.add("자녀의 위치를 찾을 수 없습니다.");
    	content.add("자녀의 위치를 찾기 위해서는 자녀의 위치허용동의가 필요하며, 위치허용동의는 자녀의 휴대폰에서만 가능합니다. 만약 위치허용동의 후에 위치가 조회 되지 않는다면 아래의 경우로 위치조회가 불가 합니다.\n- GPS, 3G, Wi-fi 상태에 따른 위치조회 불가\n- 휴대폰 전원이 꺼짐\n- 위치숨김 상태\n- 스마트자녀안심 앱 삭제");
    	
    	title.add("부모님이 위치를 조회 했다고 문자가 옵니다.");
    	content.add("스마트자녀안심은 “위치정보의 보호 및 이용 등에 관한 법률 제19조”에 의거 부모가 자녀의 위치조회 시 자녀에게 즉시 해당 내용을 문자로 통보하고 있습니다.");
    	
    	title.add("위치가 이상한 곳으로 나옵니다.");
    	content.add("스마트자녀안심은 자녀 휴대폰의 GPS, 3G, wi-fi의 상태에 따라 실제 위치와 다를 수 있습니다.\nWi-fi의 경우 공유기에 등록된 정보에 따라 전혀 다른 위치로 수집될 수 있으며 이는 공유기에 등록된 위치정보값의 문제이므로 수정이 불가능 합니다.");
    	
    	title.add("정지된 장소에 있었는데 움직였다고 나와요.");
    	content.add("스마트폰의 위치 조회는 GPS, 3G, Wi-fi를 통해 조회가 되며, 주변에 다수의 공유기나 전파 간섭 등에 따라 위치가 움직인 것으로 나올 수 있습니다.");
    	
    	title.add("일반 휴대폰을 이용하는데 스마트자녀안심을 이용할 수 없나요?");
    	content.add("스마트자녀안심은 자녀, 부모 모두 스마트폰을 이용하여야만 서비스 이용이 가능하며, 현재는 안드로이드 버전만 제공되고 있습니다. (아이폰은 제공 예정)");
    	
    	title.add("스마트자녀안심은 무료인가요?");
    	content.add("스마트자녀안심은 현위치조회는 1일 3회 무료, 안심존 설정은 1개 까지만 무료이며, 추가 이용 시 포인트가 차감 됩니다.\n※ 포인트는 유료로 충전하여 이용이 가능합니다.");
    	
    	title.add("더 이상 부모님께 위치를 제공하고 싶지 않습니다.");
    	content.add("스마트자녀안심 메인에 있는 “부모리스트”에서 위치를 제공하고 싶지 않은 부모를 삭제 할 경우 더 이상 위치가 제공되지 않습니다.");
    	
    	title.add("부모님을 다수 등록 할 수 있나요?");
    	content.add("네, “부모리스트”를 통해 다수의 부모님을 등록 할 수 있습니다.");
    	
    	title.add("발자취에 위치 정보가 없습니다.");
    	content.add("해당 자녀의 휴대폰 상태가 위치를 조회할 수 없거나, 포인트가 부족할 경우 위치 정보가 없을 수 있습니다.");
    	
    	title.add("1주일 이전의 발자취 정보는 볼 수 없나요?");
    	content.add("자녀의 발자취 정보는 1주일까지만 조회가 가능하며, 1주일 이전 정보는 조회할 수 없습니다.");
    	
    	title.add("결제는 됐는데 포인트 충전이 안됐어요.");
    	content.add("포인트 충전이 안됐을 경우 1:1문의를 통해 문의하여 주시면 확인 후에 조치하여 드리겠습니다.");
    	
    	title.add("회원 탈퇴를 하고 싶어요.");
    	content.add("스마트자녀안심은 별도의 회원탈퇴 기능이 없으며, 앱을 삭제 하시면 됩니다. 고객님의 개인정보는 관련법률에 의해 보유기간 종료 후 자동 파기 됩니다.");
    	
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
    	
    	//최초 커스텀리스트 뷰를 보여줄때
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
			
			//각 위젯 정의
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
							//gone 상태(8)이면
							faqContentArea.setVisibility(View.VISIBLE);
							
							//title부분 background 이미지 변경
							Drawable titleAreaImg = getResources().getDrawable(R.drawable.list_line_btn);
							titleBackImgArea.setBackgroundDrawable(titleAreaImg);
							
						}else{
							//visible 상태(0)이면
							faqContentArea.setVisibility(View.GONE);
							
							//title부분 background 이미지 변경
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