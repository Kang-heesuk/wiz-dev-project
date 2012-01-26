package exam.testest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class TestestActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        Intent intent = new Intent(TestestActivity.this, StartActivity.class);
        startActivity(intent);
    }
}