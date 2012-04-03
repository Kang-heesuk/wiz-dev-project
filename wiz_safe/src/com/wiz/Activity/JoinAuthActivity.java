package com.wiz.Activity;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeParser;
import com.wiz.util.WizSafeUtil;

public class JoinAuthActivity extends Activity {
    
	EditText editText1;
	String tempEditText = "";
	int authResult = 1;		//0 - 인증번호매치 및 가입성공 , 그외 - 실패
	
	//API 호출 후 리턴XML을 받는 벡터
	ArrayList<String> returnXML = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_auth);
        
        editText1 = (EditText)findViewById(R.id.editText1);
        
        String ctn = WizSafeUtil.setPhoneNum(WizSafeUtil.getCtn(JoinAuthActivity.this));
        TextView textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText("　 " + ctn + " 번호로\n인증번호가 발송되었습니다.");
        
        Button btn_join = (Button)findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				tempEditText = editText1.getText().toString();
				
				callAuthCompleteApi();
				
				if(authResult == 0){
					//가입이 정상적으로 이루어진 사람이므로, 로컬밸류의 가입부분을 업데이트 해준다.					
					SharedPreferences LocalSave;
					SharedPreferences.Editor edit;
			    	LocalSave = getSharedPreferences("WizSafeLocalVal", 0);
					edit = LocalSave.edit();
					edit.putString("isAuthOkUser", "01");
					edit.commit();
			    	
			    	//스플레시 액티비티로 이동
					Intent intent = new Intent(JoinAuthActivity.this, SplashActivity.class);
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(JoinAuthActivity.this, "인증번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
				}
			} 
		}); 
        
        Button btn_renum = (Button)findViewById(R.id.btn_renum);
        btn_renum.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(JoinAuthActivity.this, "인증 번호가 재전송 되었습니다.", Toast.LENGTH_SHORT).show();
				
			} 
		}); 
    }
    
    public void callAuthCompleteApi(){
    	InputStream is = null;
		try{
			String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(JoinAuthActivity.this));
			String enc_authNum = WizSafeSeed.seedEnc(tempEditText);
			String url = "https://www.heream.com/api/authComplete.jsp?ctn="+ URLEncoder.encode(enc_ctn) + "&authNum=" + URLEncoder.encode(enc_authNum);
			HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"euc-kr"));	
			String temp;
			returnXML = new ArrayList<String>();
			while((temp = br.readLine()) != null)
			{
				returnXML.add(new String(temp));
			}
			String resultCode = WizSafeParser.xmlParser_String(returnXML,"<RESULT_CD>");
			authResult = Integer.parseInt(resultCode);		
		}catch(Exception e){
		}finally{
			if(is != null){ try{is.close();}catch(Exception e){} }
		}
	}
     
}