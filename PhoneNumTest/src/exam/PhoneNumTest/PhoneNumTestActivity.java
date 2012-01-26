package exam.PhoneNumTest;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class PhoneNumTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView tv = new TextView(this);
        this.setContentView(tv);
        tv.setText(getSysInfo());
        
    }
    
    public String getSysInfo(){
    	StringBuffer sb = new StringBuffer();
    	
    	TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        
        sb.append("os.name=").append(System.getProperty("os.name")).append("\n");
        sb.append("os.arch=").append(System.getProperty("os.arch")).append("\n");
        sb.append("os.version=").append(System.getProperty("os.version")).append("\n");
        sb.append("brand=").append(Build.BRAND).append("\n");
        sb.append("model=").append(Build.MODEL).append("\n");
        
        if(tm.getLine1Number() != null){
        	sb.append("phone number=").append(tm.getLine1Number()).append("\n");
        }
    	
    	return sb.toString();
    }
    
}