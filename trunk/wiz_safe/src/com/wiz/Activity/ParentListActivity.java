package com.wiz.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentListActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.top_navigation);
         
        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ParentListActivity.this.finish();
			}
		});
    }
}