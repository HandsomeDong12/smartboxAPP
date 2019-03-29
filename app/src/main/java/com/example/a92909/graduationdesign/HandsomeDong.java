package com.example.a92909.graduationdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class HandsomeDong extends AppCompatActivity {
    private EditText accountEdit;
    private EditText passwordEdit;
    private CheckBox rememberBtn;
    private SharedPreferences settings;
    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handsome_dong);

        Button loginBtn = (Button) findViewById(R.id.btn_login);
        rememberBtn = (CheckBox) findViewById(R.id.checkbox_rem);
        Button registerBtn = (Button) findViewById(R.id.btn_register);
        Button findPasswordBtn = (Button) findViewById(R.id.btn_fdpswd);
        accountEdit = (EditText) findViewById(R.id.edit_account);
        passwordEdit = (EditText) findViewById(R.id.edit_passwd);


        settings = getPreferences(Context.MODE_PRIVATE);
        if (settings.getBoolean("remember", false)) {
            rememberBtn.setChecked(true);
            accountEdit.setText(settings.getString("username", ""));
            passwordEdit.setText(settings.getString("password", ""));
        }

        //记住密码
        rememberBtn.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                if (rememberBtn.isChecked()) {
                    editor.putBoolean("remember", true);
                    editor.putString("username", accountEdit.getText().toString());
                    editor.putString("password", passwordEdit.getText().toString());
                } else {
                    editor.putBoolean("remember", false);
                    editor.putString("username", "");
                    editor.putString("password", "");
                }
                editor.commit();
            }
        });

        //登陆按钮
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(checkUserInfo).start();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_Register = new Intent(HandsomeDong.this, Register.class);
                startActivity(go_Register);
            }
        });

        findPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

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

    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast toast = Toast.makeText(HandsomeDong.this, "账号或密码不正确", Toast.LENGTH_SHORT);
                toast.show();
            } else if (msg.what == 2) {
                Toast toast = Toast.makeText(HandsomeDong.this, "登陆成功", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(HandsomeDong.this, "网络连接失败", Toast.LENGTH_SHORT);
                toast.show();
            }

            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            Toast toast = Toast.makeText(HandsomeDong.this, "请输入账号密码", Toast.LENGTH_SHORT);
            toast.show();
            super.handleMessage(msg);
        }
    };


    //有参数的get请求
    Runnable checkUserInfo = new Runnable() {
        @Override
        public void run() {
            accountEdit = (EditText) findViewById(R.id.edit_account);
            passwordEdit = (EditText) findViewById(R.id.edit_passwd);
            String userId = accountEdit.getText().toString();
            String passWord = passwordEdit.getText().toString();
            URL url;
            int status = 3;
            String result;
            String token;

            if (accountEdit.length() > 0 && passwordEdit.length() > 0) {
                try {
                    url = new URL("http://119.29.247.25/smartbox/login?userId=" + userId + "&password=" + passWord);
                    HttpURLConnection loginRequest = (HttpURLConnection) url.openConnection();
                    loginRequest.setRequestMethod("GET");
                    loginRequest.connect();
                    InputStreamReader in = new InputStreamReader(loginRequest.getInputStream());
                    try (BufferedReader buffered = new BufferedReader(in)) {
                        result = buffered.readLine();
                        JSONObject resultJson = new JSONObject(result);
                        status = resultJson.getInt("status");
                        token = resultJson.getString("token");
                        saveToken(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message m1 = handler2.obtainMessage();
                if (status == 0)//登录失败
                {
                    m1.what = 1;
                    handler2.sendMessage(m1);
                } else if (status == 1) {//登录成功
                    Intent go_Me = new Intent(HandsomeDong.this, Me.class);
                    startActivity(go_Me);
                    m1.what = 2;
                    handler2.sendMessage(m1);
                } else {//其他情况：联网失败，服务器异常等
                    m1.what = 3;
                    handler2.sendMessage(m1);
                }
            } else {
                Message m = handler3.obtainMessage();
                handler3.sendMessage(m);
            }
        }
    };

    private void saveToken(String token) {
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.commit();
    }
}

