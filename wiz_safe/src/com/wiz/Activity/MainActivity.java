package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
    }
}