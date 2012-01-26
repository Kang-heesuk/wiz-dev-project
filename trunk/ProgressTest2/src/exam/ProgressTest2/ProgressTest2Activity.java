package exam.ProgressTest2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ProgressTest2Activity extends Activity {
    
	int mProg;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        findViewById(R.id.decfirst).setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		setProgressBarIndeterminateVisibility(true);
        	}
        });
        findViewById(R.id.incfirst).setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		setProgressBarIndeterminateVisibility(false);
        	}
        });
    }
}