package exam.LocalValueTest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocalValueTestActivity extends Activity {
    
	EditText LocalValueArea;
	SharedPreferences LocalSave;
	SharedPreferences.Editor edit;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        
        LocalSave = getSharedPreferences("isAuthCheck", 0);
		edit = LocalSave.edit();

        LocalValueArea = (EditText)findViewById(R.id.val1);
        
        //버튼액션 정의
        findViewById(R.id.setVal).setOnClickListener(btnClickListener);
        findViewById(R.id.getVal).setOnClickListener(btnClickListener);
    }
    
    Button.OnClickListener btnClickListener = new Button.OnClickListener(){

		public void onClick(View v) {
			switch(v.getId()){
			case R.id.setVal:
				String setVal = LocalValueArea.getText().toString();
				edit.putString("LocalVal", setVal);
				edit.commit();
				break;
			case R.id.getVal:
				String getVal = LocalSave.getString("LocalVal","기본값"); 
				Toast.makeText(LocalValueTestActivity.this, "getValue = " + getVal, Toast.LENGTH_SHORT).show();
				break;
			}
		}
    };

}