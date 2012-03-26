package com.wiz.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QuestionActivity extends Activity {

	
    /** Called when the activity is first created. */ 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        
        Button btn_answer = (Button)findViewById(R.id.btn_answer);
        btn_answer.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(QuestionActivity.this, AnswerListActivity.class);
				startActivity(intent);
			}
		});

        Button btn_question = (Button)findViewById(R.id.btn_question);
        btn_question.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(QuestionActivity.this, "reg question data = ", Toast.LENGTH_SHORT).show();
			}
		});
       
    }
    
}