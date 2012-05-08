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
       
        //����Ʈ ���� ���並 ����.
	    String enc_ctn = WizSafeSeed.seedEnc(WizSafeUtil.getCtn(PointChargeActivity.this));
	    WebView pointWebView = (WebView)findViewById(R.id.webview);
	    pointWebView.getSettings().setJavaScriptEnabled(true);//�ڹٽ�ũ��Ʈ ���� ���
	    pointWebView.getSettings().setPluginsEnabled(true); //���� �÷����� �������
	    pointWebView.loadUrl("https://www.heream.com/microPayment/Start.jsp?ctn="+URLEncoder.encode(enc_ctn));
	    pointWebView.setWebChromeClient(new WebChromeClient());
    }
    
   

}