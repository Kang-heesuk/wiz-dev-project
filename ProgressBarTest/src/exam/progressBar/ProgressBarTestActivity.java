package exam.progressBar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class ProgressBarTestActivity extends Activity {
    
	ProgressBar mProgBar;
	ProgressBar mProgCircle;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mProgBar = (ProgressBar)findViewById(R.id.progress);
        mProgCircle = (ProgressBar)findViewById(R.id.progcircle);
        
        findViewById(R.id.decfirst).setOnClickListener(mClickListener);
        findViewById(R.id.incfirst).setOnClickListener(mClickListener);
        findViewById(R.id.decsecond).setOnClickListener(mClickListener);
        findViewById(R.id.incsecond).setOnClickListener(mClickListener);
        findViewById(R.id.start).setOnClickListener(mClickListener);
        findViewById(R.id.stop).setOnClickListener(mClickListener);
        
    }
    
    Button.OnClickListener mClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			switch(v.getId()){
			case R.id.decfirst:
				mProgBar.incrementProgressBy(-2);
				break;
			case R.id.incfirst:
				mProgBar.incrementProgressBy(2);
				break;
			case R.id.decsecond:
				mProgBar.incrementSecondaryProgressBy(-2);
				break;
			case R.id.incsecond:
				mProgBar.incrementSecondaryProgressBy(2);
				break;
			case R.id.start:
				mProgCircle.setVisibility(View.VISIBLE);
				break;
			case R.id.stop:
				mProgCircle.setVisibility(View.INVISIBLE);
				break;
			}
		}
    	
    };
    
}