package exam.testest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        //ImageView img = (ImageView)findViewById(R.id.delay);
        
        Handler handler = new Handler(){
        	public void handleMessage(Message msg){
        		super.handleMessage(msg);
        		finish();
        	}
        };
        handler.sendEmptyMessageDelayed(0, 5000);
    }

}
