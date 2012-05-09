package com.wiz.Activity;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.Activity.MainActivity.CallGetCustomerInformationApiThread;
import com.wiz.Seed.WizSafeSeed;
import com.wiz.util.WizSafeDialog;
import com.wiz.util.WizSafeUtil;

public class PointChargeActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_charge_webview);
       
        //포인트 충전 웹뷰를 띄운다.
	    String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(PointChargeActivity.this));
	    WebView pointWebView = (WebView)findViewById(R.id.webview);
	    pointWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 실행 허용
	    pointWebView.getSettings().setPluginsEnabled(true); //각종 플러그인 실행허용
	    //신규 정의한 자바스크립트 펑션에 의한 안드로이드 메소드 호출
	    pointWebView.addJavascriptInterface(new JavaScriptExtention(), "android");
	    //스크롤 제거
	    pointWebView.setHorizontalScrollBarEnabled(false);
	    pointWebView.setVerticalScrollBarEnabled(false);
	    pointWebView.loadUrl("https://www.heream.com/microPayment/Start.jsp?ctn="+URLEncoder.encode(enc_ctn));
	    //pointWebView.setWebChromeClient(new WebChromeClient());
	    
	    pointWebView.setWebChromeClient(new WebChromeClient(){

			@Override
			public boolean onJsAlert(WebView view, String url, final String message, final android.webkit.JsResult result){
			   new AlertDialog.Builder(view.getContext())
			      .setTitle("스마트 자녀안심")
			      .setMessage(message)
			      .setPositiveButton(android.R.string.ok,
			            new AlertDialog.OnClickListener(){
			               public void onClick(DialogInterface dialog, int which) {
			                  result.confirm();
			                  //현재 웹뷰를 닫는다.
			                  if("소액결제를 취소하셨습니다.".equals(message)){
			                	  PointChargeActivity.this.finish();
			                  }
			               }
			            })
			      .setCancelable(true)
			      .create()
			      .show();
			   
			   return true;
			}

			
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);

				if(newProgress < 30){
					WizSafeDialog.showLoading(PointChargeActivity.this);	//Dialog 보이기
				}else if(newProgress == 100){
					WizSafeDialog.hideLoading();	//Dialog 숨기기
				}
			};
			
	    });
	    
    }
    
    
    final class JavaScriptExtention{
    	Dialog subWebViewDialog = new Dialog(PointChargeActivity.this, R.style.Dialog);
    	
    	JavaScriptExtention(){}
        	
    	public void closeWebView(){
    		
    		PointChargeActivity.this.finish();
    	}
    	
    	//must be final
    	public void openSubWebView(final String arg){
    		String url = "";
    		if("MoveHelp".equals(arg)){
				url = "http://web.teledit.com/Danal/TMobile/help/mguide.html";
			}else if("OpenYak".equals(arg)){
				url = "http://web.teledit.com/Danal/TMobile/help/myak.html";
			}
    		
    		//WebView subWebView = (WebView)findViewById(R.id.webview);
    		WebView subWebView = new WebView(PointChargeActivity.this);
    		
    		//다날측 페이지를 새로운 웹뷰로 띄운다.    			    	
			subWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 실행 허용
			subWebView.getSettings().setPluginsEnabled(true); //각종 플러그인 실행허용
			//스크롤 제거
			subWebView.setHorizontalScrollBarEnabled(false);
			subWebView.setVerticalScrollBarEnabled(false);
			subWebView.loadUrl(url);
			
			LayoutParams params = new LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
			subWebViewDialog.addContentView(subWebView, params);
			subWebViewDialog.setCancelable(true);
			subWebViewDialog.show();
    	} 
    	
    }
    
}