package com.wiz.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeUtil;

public class JoinAcceptActivity extends Activity {

	EditText editText1;
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_accept);
        
        //자신의 폰번호 노출되는 부분
        final TextView editText1 = (TextView)findViewById(R.id.editText1);
        editText1.setText(WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(JoinAcceptActivity.this)));
        
        //공지팝업 하단 체크  누른경우의 액션
	    final LinearLayout check1Layout = (LinearLayout)findViewById(R.id.check1Layout);
	    final ImageView checkImg1 = (ImageView)findViewById(R.id.btn_check1);
	    check1Layout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(checkImg1.getVisibility() == View.VISIBLE){
					checkImg1.setVisibility(View.GONE);
				}else{
					checkImg1.setVisibility(View.VISIBLE);
				}
			}
	    });
	    
	    //공지팝업 하단 체크  누른경우의 액션
	    final LinearLayout check2Layout = (LinearLayout)findViewById(R.id.check2Layout);
	    final ImageView checkImg2 = (ImageView)findViewById(R.id.btn_check2);
	    check2Layout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(checkImg2.getVisibility() == View.VISIBLE){
					checkImg2.setVisibility(View.GONE);
				}else{
					checkImg2.setVisibility(View.VISIBLE);
				}
			}
	    });
	   
        Button btn_accept = (Button)findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//체크항목을 모두 채우지 않았을경우에 버튼을 비활성화
		        if(checkImg1.getVisibility() != View.VISIBLE || checkImg2.getVisibility() != View.VISIBLE){
		        	AlertDialog.Builder ad = new AlertDialog.Builder(JoinAcceptActivity.this);
					String title = "이용 동의";	
					String message = "이용약관 및 개인정보 수집 동의 후 가능 합니다.";	
					String buttonName = "확인";
					ad.setTitle(title);
					ad.setMessage(message);
					ad.setNeutralButton(buttonName, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					ad.show();
		        	return;
		        }
		        //API 호출 쓰레드 시작
		        WizSafeDialog.showLoading(JoinAcceptActivity.this);	//Dialog 보이기
		        callAuthSMSApiThread thread = new callAuthSMSApiThread(); 
				thread.start();
			}
		});
        
        //위치기반서비스 이용약관
        StringBuffer strBuf1 = new StringBuffer();
        strBuf1.append("제 1 조 (목적) 본 약관은 회원(스마트자녀안심 서비스 약관에 동의한 자를 말합니다. 이하 \"회원\"이라고 합니다.)이 주식회사 위즈커뮤니케이션(이하 \"회사\"라고 합니다.)이 제공하는 스마트자녀안심 서비스(이하 \"서비스\"라고 합니다)를 이용함에 있어 회사와 회원의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 2 조 (이용약관의 효력 및 변경)\n");
        strBuf1.append("①본 약관은 서비스를 신청한 고객 또는 개인위치정보주체가 본 약관에 동의하고 회사가 정한 소정의 절차에 따라 서비스의 이용자로 등록함으로써 효력이 발생합니다.\n");
        strBuf1.append("②회원이 온라인에서 본 약관의 \"동의하기\" 버튼을 클릭하였을 경우 본 약관의 내용을 모두 읽고 이를 충분히 이해하였으며, 그 적용에 동의한 것으로 봅니다.\n");
        strBuf1.append("③회사는 위치정보의 보호 및 이용 등에 관한 법률, 콘텐츠산업 진흥법, 전자상거래 등에서의 소비자보호에 관한 법률, 소비자기본법 약관의 규제에 관한 법률 등 관련법령을 위배하지 않는 범위에서 본 약관을 개정할 수 있습니다.\n");
        strBuf1.append("④회사가 약관을 개정할 경우에는 기존약관과 개정약관 및 개정약관의 적용일자와 개정사유를 명시하여 현행약관과 함께 그 적용일자 10일 전부터 적용일 이후 상당한 기간 동안 공지만을 하고, 개정 내용이 회원에게 불리한 경우에는 그 적용일자 30일 전부터 적용일 이후 상당한 기간 동안 각각 이를 서비스 홈페이지에 게시하거나 회원에게 전자적 형태(전자우편, SMS 등)로 약관 개정 사실을 발송하여 고지합니다. \n");
        strBuf1.append("⑤회사가 전항에 따라 회원에게 통지하면서 공지 또는 공지?고지일로부터 개정약관 시행일 7일 후까지 거부의사를 표시하지 아니하면 이용약관에 승인한 것으로 봅니다. 회원이 개정약관에 동의하지 않을 경우 회원은 이용계약을 해지할 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 3 조 (관계법령의 적용) 본 약관은 신의성실의 원칙에 따라 공정하게 적용하며, 본 약관에 명시되지 아니한 사항에 대하여는 관계법령 또는 상관례에 따릅니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 4 조 (서비스의 내용) 회사가 제공하는 서비스는 아래와 같습니다.\n");
        strBuf1.append("서비스명 : 스마트자녀안심(제3자 통보형 서비스)\n");
        strBuf1.append("서비스내용 : o 피추적자의 동의를 받은 추적자의 피추적자 현재위치 조회 서비스\n");
        strBuf1.append("o 피추적자의 동의를 받은 추적자의 피추적자 이동 경로 위치 서비스\n");
        strBuf1.append("o 피추적자의 동의를 받은 추적자의 피추적자 지정 지역 진입 알림 서비스\n");
        strBuf1.append("\n");
        strBuf1.append("제 5 조 (서비스 이용요금) \n");
        strBuf1.append("①회사가 제공하는 서비스의 어플리케이션 다운로드 비용은 무료입니다. 단, 별도의 유료 서비스의 경우 해당 서비스에 명시된 요금을 지불하여야 사용 가능합니다.\n");
        strBuf1.append("- 현위치찾기 : 100포인트(건당)\n");
        strBuf1.append("- 자녀발자취 : 100포인트(1일)\n");
        strBuf1.append("- 안심존 : 100포인트(등록 건당)\n");
        strBuf1.append("- 포인트충전 : 1,000P(1,100원)/3,000P(3,300원)/7,000P(7,700원)/10,000P(11,000원)\n");
        strBuf1.append("②회사는 유료 서비스 이용요금을 회사와 계약한 전자지불업체에서 정한 방법에 의하거나 회사가 정한 청구서에 합산하여 청구할 수 있습니다.\n");
        strBuf1.append("③유료서비스 이용을 통하여 결제된 대금에 대한 취소 및 환불은 회사의 중대한 과실이 인정되지 않는 이상 불가능 합니다.\n");
        strBuf1.append("④회원의 과실 및 서비스 해지 등의 사유로 포인트를 환불 받을 수 없습니다.\n");
        strBuf1.append("⑤회원의 개인정보도용 및 결제사기로 인한 환불요청 또는 결제자의 개인정보 요구는 법률이 정한 경우 외에는 거절될 수 있습니다.\n");
        strBuf1.append("⑥무선 서비스 이용 시 발생하는 데이터 통신료는 별도이며, 회원이 가입한 각 요금제에 따라 별도 요금이 발생할 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 6 조 (서비스내용변경 통지 등)\n");
        strBuf1.append("①회사가 서비스 내용을 변경하거나 종료하는 경우 회사는 회원의 휴대폰 번호로 서비스 내용의 변경 사항 또는 종료를 SMS로 통지할 수 있습니다.\n");
        strBuf1.append("②①항의 경우 불특정 다수인을 상대로 통지를 함에 있어서는 어플리케이션에서 제공하는 공지사항을 통하여 회원들에게 통지할 수 있습니다.\n");
        strBuf1.append("제 7 조 (서비스이용의 제한 및 중지)\n");
        strBuf1.append("①회사는 아래 각 호의 1에 해당하는 사유가 발생한 경우에는 회원의 서비스 이용을 제한하거나 중지시킬 수 있습니다.\n");
        strBuf1.append("1. 회원이 회사 서비스의 운영을 고의 또는 중과실로 방해하는 경우\n");
        strBuf1.append("2. 서비스용 설비 점검, 보수 또는 공사로 인하여 부득이한 경우\n");
        strBuf1.append("3. 전기통신사업법에 규정된 기간통신사업자가 전기통신 서비스를 중지했을 경우\n");
        strBuf1.append("4. 국가비상사태, 서비스 설비의 장애 또는 서비스 이용의 폭주 등으로 서비스 이용에 지장이 있는 때\n");
        strBuf1.append("5. 기타 중대한 사유로 인하여 회사가 서비스 제공을 지속하는 것이 불가능하다고 인정하는 경우\n");
        strBuf1.append("②회사는 전항의 규정에 의하여 서비스의 이용을 제한하거나 중지한 때에는 그 사유 및 제한기간 등을 회원에게 알려야 하며, 회원에게 고지를 했을 경우 발생하는 손해에 대해 회사는 면책을 받을 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 8 조 (개인위치정보의 이용 또는 제공)\n");
        strBuf1.append("①회사는 개인위치정보를 이용하여 서비스를 제공하고자 하는 경우에는 미리 이용약관에 명시한 후 개인위치정보주체의 동의를 얻어야 합니다.\n");
        strBuf1.append("②회원 및 법정대리인의 권리와 그 행사방법은 제소 당시의 이용자의 주소에 의하며, 주소가 없는 경우에는 거소를 관할하는 지방법원의 전속관할로 합니다. 다만, 제소 당시 이용자의 주소 또는 거소가 분명하지 않거나 외국 거주자의 경우에는 민사소송법상의 관할법원에 제기합니다.\n");
        strBuf1.append("③회사는 타사업자 또는 이용 고객과의 요금정산 및 민원처리를 위해 위치정보 이용·제공?사실 확인자료를 자동 기록·보존하며, 해당 자료는 1년간 보관합니다.\n");
        strBuf1.append("④회사는 개인위치정보를 회원이 지정하는 제3자에게 제공하는 경우에는 개인위치정보를 수집한 당해 통신 단말장치로 매회 회원에게 제공받는 자, 제공일시 및 제공목적을 즉시 통보합니다. 단, 아래 각 호의 1에 해당하는 경우에는 회원이 미리 특정하여 지정한 통신 단말장치 또는 전자우편주소로 통보합니다.\n");
        strBuf1.append("1. 개인위치정보를 수집한 당해 통신단말장치가 문자, 음성 또는 영상의 수신기능을 갖추지 아니한 경우\n");
        strBuf1.append("2. 회원이 요청한 별도의 방법을 통해 통보할 것을 미리 요청한 경우\n");
        strBuf1.append("\n");
        strBuf1.append("제 9 조 (개인위치정보주체의 권리)\n");
        strBuf1.append("①회원은 회사에 대하여 언제든지 개인위치정보를 이용한 위치기반서비스 제공 및 개인위치정보의 제3자 제공에 대한 동의의 전부 또는 일부를 철회할 수 있습니다. 이 경우 회사는 수집한 개인위치정보 및 위치정보 이용, 제공사실 확인자료를 파기합니다.\n");
        strBuf1.append("②회원은 회사에 대하여 언제든지 개인위치정보의 수집, 이용 또는 제공의 일시적인 중지를 요구할 수 있으며, 회사는 이를 거절할 수 없고 이를 위한 기술적 수단을 갖추고 있습니다.\n");
        strBuf1.append("③회원은 회사에 대하여 아래 각 호의 자료에 대한 열람 또는 고지를 요구할 수 있고, 당해 자료에 오류가 있는 경우에는 그 정정을 요구할 수 있습니다. 이 경우 회사는 정당한 사유 없이 회원의 요구를 거절할 수 없습니다.\n");
        strBuf1.append("1. 본인에 대한 위치정보 수집, 이용, 제공사실 확인자료\n");
        strBuf1.append("2. 본인의 개인위치정보가 위치정보의 보호 및 이용 등에 관한 법률 또는 다른 법률 규정에 의하여 제3자에게 제공된 이유 및 내용\n");
        strBuf1.append("④회원은 제1호 내지 제3호의 권리행사를 위해 회사의 소정의 절차를 통해 요구할 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 10 조 (법정대리인의 권리) \n");
        strBuf1.append("①회사는 14세 미만의 회원에 대해서는 개인위치정보를 이용한 위치기반서비스 제공 및 개인위치정보의 제3자 제공에 대한 동의를 당해 회원과 당해 회원의 법정대리인으로부터 동의를 받아야 합니다. 이 경우 법정대리인은 제7조에 의한 회원의 권리를 모두 가집니다.\n");
        strBuf1.append("②회사는 14세 미만의 아동의 개인위치정보 또는 위치정보 이용?제공사실 확인자료를 이용약관에 명시 또는 고지한 범위를 넘어 이용하거나 제3자에게 제공하고자 하는 경우에는 14세미만의 아동과 그 법정대리인의 동의를 받아야 합니다. 단, 아래의 경우는 제외합니다.\n");
        strBuf1.append("1. 위치정보 및 위치기반서비스 제공에 따른 요금정산을 위하여 위치정보 이용, 제공사실 확인자료가 필요한 경우\n");
        strBuf1.append("2. 통계작성, 학술연구 또는 시장조사를 위하여 특정 개인을 알아볼 수 없는 형태로 가공하여 제공하는 경우\n");
        strBuf1.append("\n");
        strBuf1.append("제 11 조 (8세 이하의 아동 등의 보호의무자의 권리) \n");
        strBuf1.append("①회사는 아래의 경우에 해당하는 자(이하 \"8세 이하의 아동\"등이라 한다)의 보호의무자가 8세 이하의 아동 등의 생명 또는 신체보호를 위하여 개인위치정보의 이용 또는 제공에 동의하는 경우에는 본인의 동의가 있는 것으로 봅니다.\n");
        strBuf1.append("1. 8세 이하의 아동\n");
        strBuf1.append("2. 금치산자\n");
        strBuf1.append("3. 장애인복지법제2조제2항제2호의 규정에 의한 정신적 장애를 가진 자로서장애인고용촉진및직업재활법 제2조제2호의 규정에 의한 중증장애인에 해당하는 자(장애인복지법 제29조의 규정에 의하여 장애인등록을 한 자에 한한다)\n");
        strBuf1.append("②8세 이하의 아동 등의 생명 또는 신체의 보호를 위하여 개인위치정보의 이용 또는 제공에 동의를 하고자 하는 보호의무자는 서면동의서에 보호의무자임을 증명하는 서면을 첨부하여 회사에 제출하여야 합니다.\n");
        strBuf1.append("③보호의무자는 8세 이하의 아동 등의 개인위치정보 이용 또는 제공에 동의하는 경우 개인위치정보주체 권리의 전부를 행사할 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 12 조 (위치정보관리책임자의 지정)\n");
        strBuf1.append("①회사는 위치정보를 적절히 관리?보호하고 개인위치정보주체의 불만을 원활히 처리할 수 있도록 실질적인 책임을 질 수 있는 지위에 있는 자를 위치정보관리책임자로 지정해 운영합니다. \n");
        strBuf1.append("②위치정보관리책임자는 위치기반서비스를 제공하는 부서의 부서장으로서 구체적인 사항은 본 약관의 부칙에 따릅니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 13 조 (손해배상) \n");
        strBuf1.append("①회사가 위치정보의 보호 및 이용 등에 관한 법률 제15조 내지 제26조의 규정을 위반한 행위로 회원에게 손해가 발생한 경우 회원은 회사에 대하여 손해배상 청구를 할 수 있습니다. 이 경우 회사는 고의, 과실이 없음을 입증하지 못하는 경우 책임을 면할 수 없습니다.\n");
        strBuf1.append("②회원이 본 약관의 규정을 위반하여 회사에 손해가 발생한 경우 회사는 회원에 대하여 손해배상을 청구할 수 있습니다. 이 경우 회원은 고의, 과실이 없음을 입증하지 못하는 경우 책임을 면할 수 없습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 14 조 (면책) \n");
        strBuf1.append("①회사는 다음 각 호의 경우로 서비스를 제공할 수 없는 경우 이로 인하여 회원에게 발생한 손해에 대해서는 책임을 부담하지 않습니다.\n");
        strBuf1.append("1. 천재지변 또는 이에 준하는 불가항력의 상태가 있는 경우\n");
        strBuf1.append("2. 서비스 제공을 위하여 회사와 서비스 제휴계약을 체결한 제3자의 고의적인 서비스 방해가 있는 경우\n");
        strBuf1.append("3. 회원의 귀책사유로 서비스 이용에 장애가 있는 경우\n");
        strBuf1.append("4. 제1호 내지 제3호를 제외한 기타 회사의 고의?과실이 없는 사유로 인한 경우\n");
        strBuf1.append("5. 회사는 개인위치정보주체의 귀책사유로 인한 서비스 이용장애에 대하여 책임을 지지\n");
        strBuf1.append("않음\n");
        strBuf1.append("6. 회사는 고객이 서비스를 이용한 기대수익을 상실한 것에 대한 책임을지지 않음.\n");
        strBuf1.append("7. 전파 및 GPS 특성 등 예측할 수 없는 음영지역 발생 및 장비에 부여된 정보 오류에\n");
        strBuf1.append("따른 위치정보 부정확성에 대해 책임이 면제됨.\n");
        strBuf1.append("\n");
        strBuf1.append("②회사는 서비스 및 서비스에 게재된 정보, 자료, 사실의 신뢰도, 정확성 등에 대해서는 보증을 하지 않으며 이로 인해 발생한 회원의 손해에 대하여는 책임을 부담하지 아니합니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 15 조 (규정의 준용) \n");
        strBuf1.append("①본 약관은 대한민국법령에 의하여 규정되고 이행됩니다.\n");
        strBuf1.append("②본 약관에 규정되지 않은 사항에 대해서는 관련법령 및 상관습에 의합니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 16 조 (분쟁의 조정 및 기타) \n");
        strBuf1.append("①회사는 위치정보와 관련된 분쟁에 대해 당사자간 협의가 이루어지지 아니하거나 협의를 할 수 없는 경우에는 전기통신기본법 제40조의2의 규정에 의한 방송통신위원회에 재정을 신청할 수 있습니다.\n");
        strBuf1.append("②회사 또는 고객은 위치정보와 관련된 분쟁에 대해 당사자간 협의가 이루어지지 아니하거나 협의를 할 수 없는 경우에는 정보통신망이용촉진및정보보호등에관한 법률 제 33조의 규정에 의한 개인정보분쟁조정위원회에 조정을 신청할 수 있습니다.\n");
        strBuf1.append("\n");
        strBuf1.append("제 17 조 (회사의 연락처) 회사의 상호 및 주소 등은 다음과 같습니다.\n");
        strBuf1.append("1. 상 호 : 주식회사 위즈커뮤니케이션\n");
        strBuf1.append("2. 대 표 자 : 심 재 욱\n");
        strBuf1.append("3. 주 소 : 서울시 송파구 가락동 78번지 송파IT벤처타워 서관 10층\n");
        strBuf1.append("4. 대표전화 : 02-2008-7700\n");
        strBuf1.append("\n");
        strBuf1.append("부 칙\n");
        strBuf1.append("\n");
        strBuf1.append("제1조 (시행일) 이 약관은 2012년 03월 27일부터 시행한다.\n");
        strBuf1.append("제2조 위치정보관리책임자는 2012년 03월을 기준으로 다음과 같이 지정합니다.\n");
        strBuf1.append("1. 소 속 : 인터넷사업본부\n");
        strBuf1.append("2. 성 명 : 박순호\n");
        strBuf1.append("3. 연락처 : 02-2008-7700");

        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText(strBuf1.toString());
        
        //개인정보 수집 및 이용안내
        StringBuffer strBuf2 = new StringBuffer();
        strBuf2.append("㈜위즈커뮤니케이션(이하 \"회사\"라 함)는 통신비밀보호법, 전기통신사업법, 정보통신망 이용촉진 및 정보보호 등에 관한 법률 등 정보통신서비스제공자가 준수하여야 할 관련 법령상의 개인정보보호 규정을 준수하며, 관련 법령에 의거한 개인정보취급방침을 정하여 이용자 권익 보호에 최선을 다하고 있으며,\n");
        strBuf2.append("개인정보취급방침은 다음과 같습니다\n");
        strBuf2.append("1. 개인정보의 수집목적 및 이용목적\n");
        strBuf2.append("1) 회사는 서비스 제공의 목적으로 서비스 이용자 본인의 개인 정보 및 결제, 이벤트 진행 시 경품 발송, 마케팅 통계/분석을 위한 자료로써 필요한 최소한의 회원의 개인정보를 수집 이용하고 있습니다. 개인정보의 수집 시 관련 법규에 따라 회원 가입 페이지 또는 이용약관에 그 수집범위 및 목적을 사전에 고지합니다.\n");
        strBuf2.append("2) 회사에서 회원 가입 시 수집하는 개인정보와 이용목적은 다음과 같습니다.\n");
        strBuf2.append(" 휴대폰 번호 : 서비스 이용에 따른 본인식별절차 및 고객센터 처리 시 휴대폰 기종 조회 및 통신사 확인을 통한 고객민원 처리 및 이벤트, 공지 등의 기타 SMS안내 \n");
        strBuf2.append("개인위치정보 : 서비스 이용을 위한 정보 수집\n");
        strBuf2.append("3) 회사는 이용자의 기본적 인권 침해의 우려가 있는 민감한 개인정보(인종 및 민족, 사상 및 신조, 출신지 및 본적지, 정치적 성향 및 범죄기록, 과거의 병력, 유전정보 등 건강상태 및 성생활 등)는 수집하지 않습니다.\n");
        strBuf2.append("4) 회사는 관계법령을 위반하거나 회사의 서비스 이용약관을 위반한 불량 이용자의 부정이용 재발 방지를 위하여 해당 이용자의 IP주소, 접속 일시 등을 수집할 수 있습니다.\n");
        strBuf2.append("5) 회사는 회원의 유료정보 이용에 따른 추후 환불요청이나 결제확인에 필요한 휴대폰 정보, 신용카드 등의 결제정보와 결제 내역 등의 정보를 수집, 이용하고 있습니다.\n");
        strBuf2.append("6) 회원의 서비스 이용 중에 생성되거나 수집되는 다음의 개인 정보를 아래와 같은 목적을 위해 이용하고 있습니다.\n");
        strBuf2.append("휴대폰 번호, 신용카드 정보 등 금융거래정보 : 서비스 이용을 위한 요금 결제, 고객민원 처리 \n");
        strBuf2.append("2. 개인정보의 보관기간 및 이용기간\n");
        strBuf2.append("1) 원칙적으로, 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관계법령의 규정에 의하여 보존할 필요가 있는 경우 아래와 같이 관계법령에서 정한 일정한 기간 동안 회원정보를 보관합니다.\n");
        strBuf2.append("① 표시/광고에 관한 기록 : 6개월 (전자상거래등에서의 소비자보호에 관한 법률)\n");
        strBuf2.append("② 계약 또는 청약철회 등에 관한 기록 : 5년 (전자상거래등에서의 소비자보호에 관한 법률)\n");
        strBuf2.append("③ 대금결제 및 재화 등의 공급에 관한 기록 : 5년 (전자상거래등에서의 소비자보호에 관한 법률)\n");
        strBuf2.append("④ 소비자의 불만 또는 분쟁처리에 관한 기록 : 3년 (전자상거래등에서의 소비자보호에 관한 법률)\n");
        strBuf2.append("⑤ 본인확인에 관한 기록 : 6개월 (정보통신 이용촉진 및 정보보호 등에 관한 법률)\n");
        strBuf2.append("⑥ 방문에 관한 기록 : 3개월 (통신비밀보호법)\n");
        strBuf2.append("2) 이용약관 및 관계법령을 위반한 회원의 경우 다른 회원을 보호하고 사법기관 수사 의뢰 시 증거자료로 활용하기 위해 회원탈퇴 후 1년간 원정보를 보관할 수 있습니다.\n");
        strBuf2.append("3. 개인정보의 파기절차\n");
        strBuf2.append("회사는 원칙적으로 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 파기절차 및 방법은 다음과 같습니다. \n");
        strBuf2.append("1) 파기절차\n");
        strBuf2.append("회원님이 회원가입 등을 위해 입력하신 정보는 목적이 달성된 후 내부 방침 및 기타 관련 법령에 의한 정보보호 사유에 따라(보유 및 이용기간 참조) 일정 기간 저장된 후 파기되어집니다.\n");
        strBuf2.append("동 개인정보는 법률에 의한 경우가 아니고서는 보유 되어지는 이외의 다른 목적으로 이용되지 않습니다. \n");
        strBuf2.append("2) 파기방법 \n");
        strBuf2.append("종이에 출력된 개인정보는 분쇄기로 분쇄하거나 소각을 통하여 파기하고\n");
        strBuf2.append("전자적 파일형태로 저장된 개인정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 삭제합니다.\n");
        strBuf2.append("4. 이용자 및 법정대리인의 권리와 그 행사방법\n");
        strBuf2.append("1) \"회원\" 및 법정 대리인은 언제든지 등록되어 있는 자신 혹은 당해 만 14세 미만 아동의 개인정보를 조회하거나 수정할 수 있으며 계약 해지 등을 요청할 수 있습니다. \"회원\" 혹은 만 14세 미만 아동의 개인정보 조회/수정을 위해서는 '회원정보수정'을, 가입해지(동의철회)를 위해서는 \"해지신청\"을 클릭하여 본인 확인 절차를 거치신 후 직접 열람, 정정 또는 탈퇴가 가능하며, 직접 열람, 정정 또는 탈퇴가 어려운 경우 개인정보관리책임자에게 서면, 전화 또는 이메일로 연락하시면 지체 없이 조치합니다. \n");
        strBuf2.append("2) \"회원\"이 개인정보의 오류에 대한 정정을 요청하는 경우 정정을 완료하기 전까지 당해 개인정보를 이용 또는 제공하지 않습니다. 또한 잘못된 개인정보를 제3자에게 이미 제공한 경우에는 정정 처리결과를 제3자에게 지체 없이 통지하여 정정합니다. \n");
        strBuf2.append("3) \"회사\"는 \"회원\" 혹은 법정대리인의 요청에 의해 해지 또는 삭제된 개인정보를 \"회사가 수집하는 개인정보의 보유 및 이용기간\"에 명시된 바에 따라 처리하고 그 외의 용도로 열람 또는 이용할 수 없도록 하고 있습니다. \n");
        strBuf2.append("\n");
        strBuf2.append("5. 아동의 개인정보보보호\n");
        strBuf2.append("만14세 미만 아동의 개인정보는 법정대리인이 아동의 개인정보의 열람, 정정, 동의철회를 요청할 수 있으며, 이러한 요청이 있을 경우 회사는 지체없이 필요한 조치를 취합니다. 만14세 미만의 아동은 자신에 대한 개인정보를 다른 사람에게 보내면 안되며, 보내기 전에는 항상 법정 대리인의 동의를 받아야 합니다. 따라서 필요시 만14세 미만의 회원의 법정대리인의 동의를 요청할 수 있습니다.\n");
        strBuf2.append("\n");
        strBuf2.append("6. 개인정보의 기술적/관리적 보호 대책\n");
        strBuf2.append("\"회사\"는 \"회원\"의 개인정보를 취급함에 있어 개인정보가 분실, 도난, 누출, 변조 또는 훼손되지 않도록 안전성 확보를 위하여 다음과 같은 기술적/관리적 대책을 강구하고 있습니다. \n");
        strBuf2.append("1) 개인정보 암호화\n");
        strBuf2.append("\"회원\"의 휴대폰번호 및 위치정보는 암호화되어 저장 및 관리되고 있어 해킹을 통한 누출 및 변조를 차단하며, 서비스를 통해 성립되는 사용자에게만 제공 됩니다.\n");
        strBuf2.append("2) 해킹 등에 대비한 대책\n");
        strBuf2.append("\"회사\"는 해킹이나 컴퓨터 바이러스 등에 의해 \"회원\"의 개인정보가 유출되거나 훼손되는 것을 막기 위해 최선을 다하고 있습니다.\n");
        strBuf2.append("개인정보의 훼손에 대비해서 자료를 수시로 백업하고 있고, 최신 백신프로그램을 이용하여 \"회원\"의 개인정보나 자료가 누출되거나 손상되지 않도록 방지하고 있으며, 암호화통신 등을 통하여 네트워크상에서 개인정보를 안전하게 전송할 수 있도록 하고 있습니다.\n");
        strBuf2.append("그리고 침입차단시스템을 이용하여 외부로부터의 무단 접근을 통제하고 있으며, 기타 시스템적으로 보안성을 확보하기 위한 가능한 모든 기술적 장치를 갖추려 노력하고 있습니다. \n");
        strBuf2.append("3) 취급 직원의 최소화 및 교육\n");
        strBuf2.append("\"회사\"의 개인정보관련 취급 직원은 담당자에 한정시키고 있고 이를 위한 별도의 비밀번호를 부여하여 정기적으로 갱신하고 있으며, 담당자에 대한 수시 교육을 통하여 개인정보취급방침의 준수를 항상 강조하고 있습니다. \n");
        strBuf2.append("4) 개인정보처리시스템 접근 제한\n");
        strBuf2.append("\"회사\"는 개인정보를 처리할 수 있도록 체계적으로 구성한 데이터베이스시스템에 대한 접근권한의 부여, 변경, 말소 등에 관한 기준을 수립하고 비밀번호의 생성 방법, 변경 주기 등을 규정 운영하며 기타 개인정보에 대한 접근통제를 위해 필요한 조치를 다하고 있습니다. \n");
        strBuf2.append("5) 개인 휴대폰 관리\n");
        strBuf2.append("\"회원\"이 사용하는 휴대폰은 원칙적으로 \"회원\"만이 사용하도록 되어 있습니다. \"회사\"는 \"회원\"의 개인적인 부주의로 휴대폰을 통한 개인정보가 유출되어 발생한 문제와 기본적인 인터넷의 위험성 때문에 일어나는 일들에 대해 책임을 지지 않습니다. 휴대폰 관리에 대한 보안 의식을 가지고 각별한 주의를 기울여 주시기 바랍니다.\n");
        strBuf2.append("\n");
        strBuf2.append("7. 개인정보관리책임자 및 담당자의 연락처\n");
        strBuf2.append("\"회원\" 및 비회원은 \"회사\"의 \"서비스\"를 이용하는 중 발생하는 모든 개인정보보호 관련 민원을 개인정보관리책임자 혹은 담당자에게 신고하실 수 있습니다. \"회사\"는 \"회원\" 및 비회원의 신고사항에 대해 신속하게 충분한 답변을 드리겠습니다.\n");
        strBuf2.append("개인정보관리책임자\n");
        strBuf2.append("이름 : 박순호\n");
        strBuf2.append("소속/직위 : 인터넷 사업본부장 / 전무\n");
        strBuf2.append("전화 : 02-2008-7700\n");
        strBuf2.append("메일 : smartchild@wiz.co.kr \n");
        strBuf2.append(" \n");
        strBuf2.append("개인정보관리책임자\n");
        strBuf2.append("이름 : 송현석\n");
        strBuf2.append("소속/직위 : 서비스개발팀 / 팀장\n");
        strBuf2.append("전화 : 02-2008-7700\n");
        strBuf2.append("메일 : smartchild@wiz.co.kr\n");
        strBuf2.append(" \n");
        strBuf2.append("기타 개인정보침해에 대한 신고나 상담이 필요하신 경우에는 아래 기관에 문의하시기 바랍니다. \n");
        strBuf2.append("- 개인정보침해신고센터 (http://www.118.or.kr / 118) \n");
        strBuf2.append("- 정보보호마크인증위원회 (http://www.eprivacy.or.kr / 02-580-0533~4) \n");
        strBuf2.append("- 대검찰청 인터넷범죄수사센터 (http://www.spo.go.kr / 02-3480-3600) \n");
        strBuf2.append("- 경찰청 사이버테러대응센터 (http://www.ctrc.go.kr / 02-392-0330)\n");
        strBuf2.append("\n");
        strBuf2.append("8. 고지의 의무 \n");
        strBuf2.append("현 개인정보취급방침 내용 추가, 삭제 및 수정이 있을 시에는 개정 최소 1주일전 부터 서비스의 '공지사항'을 통해 고지할 것입니다. \n");
        strBuf2.append("- 공고일자 : 2012년 3월 27일\n");
        strBuf2.append("- 시행일자 : 2012년 3월 27일\n");

        TextView textView2 = (TextView)findViewById(R.id.textView2);
        textView2.setText(strBuf2.toString());
    }
    
    
    //API 호출 쓰레드
  	class callAuthSMSApiThread extends Thread{
  		public void run(){
  			
  			try{
  				String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAcceptActivity.this));
  				String url = "https://www.heream.com/api/sendAuthSMS.jsp?ctn="+ URLEncoder.encode(enc_ctn);
  				HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
  				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
  				String temp;
  				returnXML = new ArrayList<String>();
  				while((temp = br.readLine()) != null)
  				{
  					returnXML.add(new String(temp));
  				}
  				pHandler.sendEmptyMessage(0);
  			}catch(Exception e){
  				//통신중 에러발생
  				pHandler.sendEmptyMessage(1);
  			}
  		}
  	}
  	
  	Handler pHandler = new Handler(){
  		public void handleMessage(Message msg){
  			WizSafeDialog.hideLoading();
  			if(msg.what == 0){
  				
  				Intent intent = new Intent(JoinAcceptActivity.this, JoinAuthActivity.class);
				startActivity(intent);
				JoinAcceptActivity.this.finish();
				
  			}else if(msg.what == 1){
  				AlertDialog.Builder ad = new AlertDialog.Builder(JoinAcceptActivity.this);
				String title = "통신 오류";	
				String message = "통신 중 오류가 발생하였습니다.";	
				String buttonName = "확인";
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