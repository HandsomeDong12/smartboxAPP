package com.example.a92909.graduationdesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Button returnLoginBtn = (Button) findViewById(R.id.returnLogin);

        returnLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_Login = new Intent(Result.this, HandsomeDong.class);
                startActivity(go_Login);
            }
        });
    }
}
