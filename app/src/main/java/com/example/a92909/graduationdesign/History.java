package com.example.a92909.graduationdesign;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class History extends AppCompatActivity {
    public TextView history;
    public TextView takeTime;
    String id = null;
    String medicine = null;
    String timeData = null;
    JSONObject medicineJson;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        history = (TextView) findViewById(R.id.text_history);
        takeTime = (TextView) findViewById(R.id.time_history);
        String time;

        Intent intent = getIntent();
        String medicineData = intent.getStringExtra("medicineData");
        try {
            medicineJson = new JSONObject(medicineData);
            id = medicineJson.getString("id");
            timeData = medicineJson.getString("updateTime");
            medicine = medicineJson.getString("medicine");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        history.setText("订单号：" + id + "\n药品：" + medicine);
        time = Medicine.timedate(timeData);
        takeTime.setText("取药时间：" + time);
    }
}
