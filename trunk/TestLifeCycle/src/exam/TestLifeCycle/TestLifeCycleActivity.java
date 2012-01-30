package exam.TestLifeCycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestLifeCycleActivity extends Activity {
   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.goOtherActivity).setOnClickListener(mClickListener);
        
    } 
     
    Button.OnClickListener mClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			Intent intent = new Intent(TestLifeCycleActivity.this, LifeCycleActivity2.class);
	        startActivity(intent);
		}
    	
    };
    
    
	protected void onStart() { 
		super.onStart(); 
		Log.d("leftTest", "onStart    =================1"); 
	}
	
	protected void onRestart() { 
		super.onRestart(); 
		Log.d("leftTest", "onRestart    =================1"); 
	}
	
	protected void onResume() { 
		super.onResume(); 
		Log.d("leftTest", "onResume    =================1"); 
	}
	
	protected void onPause() { 
		super.onPause(); 
		Log.d("leftTest", "onPause    =================1"); 
	}
	 
	protected void onStop() { 
		super.onStop(); 
		Log.d("leftTest", "onStop    =================1"); 
	}
    
	protected void onDestroy() { 
		super.onDestroy(); 
		Log.d("leftTest", "onDestroy    =================1"); 
	}
    
    
    
}