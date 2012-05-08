package com.wiz.Activity;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.wiz.Seed.WizSafeSeed;
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
	    pointWebView.loadUrl("https://www.heream.com/microPayment/Start.jsp?ctn="+URLEncoder.encode(enc_ctn));
	    pointWebView.setWebChromeClient(new WebChromeClient());
    }
    
   

}