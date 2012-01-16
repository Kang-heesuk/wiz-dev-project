package exam.DemonTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DemonTestActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        Button btnstart = (Button)findViewById(R.id.startBtn);
        btnstart.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(DemonTestActivity.this, DemonService.class);
				startService(intent);
			}
        });
        
        Button btnstop = (Button)findViewById(R.id.stopBtn);
        btnstop.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(DemonTestActivity.this, DemonService.class);
				stopService(intent);
			}
        });
        
    }
    
    
    
}