package exam.TestLifeCycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LifeCycleActivity2 extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("leftTest", "onCreate    =================2");
        setContentView(R.layout.preview);
        
        findViewById(R.id.preActivity).setOnClickListener(mClickListener);
 
    }
	
	Button.OnClickListener mClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			Intent intent = new Intent(LifeCycleActivity2.this, TestLifeCycleActivity.class);
	        startActivity(intent);
		}
    	
    };
    
    
	protected void onStart() { 
		super.onStart(); 
		Log.d("leftTest", "onStart    =================2"); 
	}
	
	protected void onRestart() { 
		super.onRestart(); 
		Log.d("leftTest", "onRestart    =================2"); 
	}
	
	protected void onResume() { 
		super.onResume(); 
		Log.d("leftTest", "onResume    =================2"); 
	}
	
	protected void onPause() { 
		super.onPause(); 
		Log.d("leftTest", "onPause    =================2");
	}
	 
	protected void onStop() { 
		super.onStop(); 
		Log.d("leftTest", "onStop    =================2"); 
	}
    
	protected void onDestroy() { 
		super.onDestroy(); 
		Log.d("leftTest", "onDestroy    =================2"); 
	}
	
	
}
