package com.example.a92909.graduationdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by 92909 on 2018/11/2.
 */

public class Me extends AppCompatActivity {
    private String userId;
    private String userName;
    private TextView userNameText;
    private TextView userIdText;
    private Button medicineBtn;
    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me);

        userNameText = (TextView) findViewById(R.id.text_username);
        userIdText = (TextView) findViewById(R.id.text_usernum);
        medicineBtn = (Button) findViewById(R.id.btn_medicine);
        final Button historyBtn = (Button) findViewById(R.id.btn_history);
        Button logoutBtn = (Button) findViewById(R.id.btn_logout);

        historyBtn.setTextSize(25);
        historyBtn.setText("查看历史药品订单");
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        if (token.isEmpty()) {
            this.startActivity(new Intent(this.getApplicationContext(), HandsomeDong.class));
            finish();
        } else {
            new Thread(checkUserInfo).start();
            new Thread(checkMedicine).start();
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(logout).start();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_Orders = new Intent(Me.this, Orders.class);
                go_Orders.putExtra("type", 1);
                startActivity(go_Orders);
            }
        });

        medicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_Orders = new Intent(Me.this, Orders.class);
                go_Orders.putExtra("type", 2);
                startActivity(go_Orders);
            }
        });
    };

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                moveTaskToBack(true);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    Runnable logout = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("token");
            editor.commit();
            Intent go_Login = new Intent(Me.this, HandsomeDong.class);
            startActivity(go_Login);
        }
    };


    Runnable checkUserInfo = new Runnable() {
        @Override
        public void run() {
            URL url;
            int status = 3;
            String result;
            SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sp.getString("token", "");
            try {
                url = new URL("http://119.29.247.25/smartbox/user?token=" + token);
                HttpURLConnection loginRequest = (HttpURLConnection) url.openConnection();
                loginRequest.setRequestMethod("GET");
                loginRequest.connect();

                InputStreamReader in = new InputStreamReader(loginRequest.getInputStream());
                try (BufferedReader buffered = new BufferedReader(in)) {
                    result = buffered.readLine();
                    JSONObject resultJson = new JSONObject(result);
                    status = resultJson.getInt("status");
                    JSONObject userDataJson = resultJson.getJSONObject("userData");
                    userName = userDataJson.getString("userName");
                    userId = userDataJson.getString("userId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message m1 = handler1.obtainMessage();
            if (status == 0)//登录失败
            {
                m1.what = 1;
                handler1.sendMessage(m1);
            } else if (status == 1) {//登录成功
                m1.what = 2;
                handler1.sendMessage(m1);
            } else {//其他情况：联网失败，服务器异常等
                m1.what = 3;
                handler1.sendMessage(m1);
            }
        }
    };

    Runnable checkMedicine = new Runnable() {
        @Override
        public void run() {
            URL url;
            int error = 0;
            String result;
            SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sp.getString("token", "");
            try {
                url = new URL("http://119.29.247.25/smartbox/medicine?token=" + token);
                HttpURLConnection loginRequest = (HttpURLConnection) url.openConnection();
                loginRequest.setRequestMethod("GET");
                loginRequest.connect();

                InputStreamReader in = new InputStreamReader(loginRequest.getInputStream());
                try (BufferedReader buffered = new BufferedReader(in)) {
                    result = buffered.readLine();
                    JSONObject medicineJson = new JSONObject(result);
                    error = medicineJson.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message m1 = handler2.obtainMessage();
            if (error == 0) {
                m1.what = 1;
            } else {
                m1.what = 2;
            }
            handler2.sendMessage(m1);
        }
    };


    @SuppressLint("HandlerLeak")
    public Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast toast = Toast.makeText(Me.this, "用户名或密码不正确", Toast.LENGTH_SHORT);
                toast.show();
            } else if (msg.what == 2) {
                userNameText.setText(userName);
                userIdText.setText(userId);
                Toast toast = Toast.makeText(Me.this, "登陆成功", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(Me.this, "网络连接失败", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            medicineBtn.setTextSize(25);
            if (msg.what == 1) {
                medicineBtn.setText("您有药品，点我看进度！");
            } else {
                medicineBtn.setText("您目前没有药品哦！");
            }
            super.handleMessage(msg);
        }
    };
}
