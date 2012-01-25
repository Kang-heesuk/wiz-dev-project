package exam.AuthCheck;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthCheckActivity extends Activity {
    
	EditText phoneArea;
	EditText authArea;
	
	//키보드 보이고 안보이고
	InputMethodManager mImm;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        phoneArea = (EditText)findViewById(R.id.phonenum);
        authArea = (EditText)findViewById(R.id.authNum);
        
        phoneArea.setFilters(new InputFilter[] {
        	new InputFilter.LengthFilter(11)	
        });
        authArea.setFilters(new InputFilter[] {
           	new InputFilter.LengthFilter(4)	
        });
        
        mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        
        //버튼액션 정의
        findViewById(R.id.smsSend).setOnClickListener(btnClickListener);
        findViewById(R.id.goCheck).setOnClickListener(btnClickListener);
    }
    
    Button.OnClickListener btnClickListener = new Button.OnClickListener(){
		
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.smsSend:
				Log.i("authTest","1");
				mImm.hideSoftInputFromWindow(phoneArea.getWindowToken(),0);		//키보드를 숨기고
				int result = sendSMS(phoneArea.getText().toString());
				if(result == 0){
					Toast.makeText(AuthCheckActivity.this, phoneArea.getText()+"님께 SMS 정상발송하였습니다.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AuthCheckActivity.this, phoneArea.getText()+"님께 인증번호 발송 실패", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.id.goCheck:
				Log.i("authTest","2");
				mImm.hideSoftInputFromWindow(phoneArea.getWindowToken(),0);		//키보드를 숨기고
				int resultAuth = authCheck(phoneArea.getText().toString(), authArea.getText().toString());
				if(resultAuth == 0){
					Toast.makeText(AuthCheckActivity.this, phoneArea.getText()+"님은 정상적으로 인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AuthCheckActivity.this, phoneArea.getText()+"님은 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
    	
    };

    //sendSMS 펑션은 리턴값이 0 이면 SMS 가 정상적으로 발송이 된것이다.
    public int sendSMS(String phonenum){
    	
       	int returnVal;
    	int read = 0;
    	byte[] buf = new byte[1024];
    	InputStream is = null;
    	
    	try{
    		String result = "";
    		String url = "http://210.109.111.212/tb/JYJ/appApi/smsAuthSend.jsp?ctn=" + phonenum;
    		is = (new URL(url)).openStream();
    		while((read=is.read(buf)) != -1){
    			result = result + new String(buf,0,read,"utf-8");
    		}
    		Log.i("authTest","result==" + result);
    		if(result.indexOf("0") > -1){
    			returnVal = 0;
    		}else{
    			returnVal = 1;
    		}
    	}catch(Exception e){
    		Log.i("authTest","result == " + e.toString());
    		returnVal = 1;
    	}finally{
    		if(is != null){ try{is.close();}catch(Exception e){} }
    	}
    	
    	return returnVal;
    }
    
    public int authCheck(String phonenum, String authNum){
    	int returnVal;
    	int read = 0;
    	byte[] buf = new byte[1024];
    	InputStream is = null;
    	
    	try{
    		String result = "";
    		String url = "http://210.109.111.212/tb/JYJ/appApi/authCheck.jsp?ctn="+phonenum+"&authNum=" + authNum;
    		is = (new URL(url)).openStream();
    		while((read=is.read(buf)) != -1){
    			result = result + new String(buf,0,read,"utf-8");
    		}
    		Log.i("authTest","result==" + result);
    		if(result.indexOf("0") > -1){
    			returnVal = 0;
    		}else{
    			returnVal = 1;
    		}
    	}catch(Exception e){
    		Log.i("authTest","result == " + e.toString());
    		returnVal = 1;
    	}finally{
    		if(is != null){ try{is.close();}catch(Exception e){} }
    	}
    	
    	return returnVal;
    }
   
}
