package com.example.a92909.graduationdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by 92909 on 2018/11/2.
 */

public class Medicine extends AppCompatActivity {
    public TextView process;
    public TextView updateTime;
    int status = 0;
    int boxId = 0;
    int verification = 0;
    String medicine = null;
    String timeData = null;
    JSONObject medicineJson;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine);
        process = (TextView) findViewById(R.id.process);
        updateTime = (TextView) findViewById(R.id.time);
        String time;

        Intent intent = getIntent();
        String medicineData = intent.getStringExtra("medicineData");
        try {
            medicineJson = new JSONObject(medicineData);
            status = medicineJson.getInt("status");
            timeData = medicineJson.getString("updateTime");
            boxId = medicineJson.getInt("boxId");
            verification = medicineJson.getInt("verification");
            medicine = medicineJson.getString("medicine");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (status) {
            case 1:
                process.setText("正在配药中，请耐心等候……");
                break;
            case 2:
                process.setText("正在运送到" + boxId + "号柜子，请耐心等候……");
                time = timedate(timeData);
                updateTime.setText("最后更新时间：\n" + time);
                break;
            case 3:
                process.setText("您的药品已放到" + boxId + "号柜子中，验证码为" + verification + "。\n\n药方: " + medicine);
                time = timedate(timeData);
                updateTime.setText("最后更新时间：\n" + time);
                break;
            default:
                process.setText(" 您目前没有药品！");
        }
    }

    public static String timedate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }
}
