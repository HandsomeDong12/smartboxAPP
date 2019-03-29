package com.example.a92909.graduationdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressLint("Registered")
public class Orders extends AppCompatActivity {
    String token;
    JSONArray medicine;
    LinearLayout linearLayout;
    int uiType;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);
        Intent intent = getIntent();
        uiType = intent.getIntExtra("type", 0);

        TextView title = (TextView) findViewById(R.id.ordersTitle);
        linearLayout = (LinearLayout) findViewById(R.id.orders);

        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        if (uiType == 1) {
            title.setText("历史药品订单");
        } else if (uiType == 2) {
            title.setText("目前药品订单");
        } else {
            title.setText("错误！");
        }
        new Thread(getOrders).start();
    }


    protected String getResult(URL url) {
        String result = null;
        try {
            HttpURLConnection loginRequest = (HttpURLConnection) url.openConnection();
            loginRequest.setRequestMethod("GET");
            loginRequest.connect();
            InputStreamReader in = new InputStreamReader(loginRequest.getInputStream());
            try (BufferedReader buffered = new BufferedReader(in)) {
                result = buffered.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    Runnable getOrders = new Runnable() {
        @Override
        public void run() {
            String result;
            JSONObject medicineJson;
            int error = 0;
            Message m = handler.obtainMessage();
            try {
                URL url;
                if (uiType == 1) {
                    url = new URL("http://119.29.247.25/smartbox/history?token=" + token);
                } else {
                    url = new URL("http://119.29.247.25/smartbox/medicine?token=" + token);
                }

                result = getResult(url);
                medicineJson = new JSONObject(result);
                error = medicineJson.getInt("error");
                if (error == 0) {
                    medicine = medicineJson.getJSONArray("data");
                    m.what = 0;
                } else {
                    m.what = 1;
                }

                handler.sendMessage(m);
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                addItems(medicine);
            } else {
                noItem();
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("SetTextI18n")
    private void addItems(JSONArray itemArray) {
        Button itemBtn[] = new Button[itemArray.length()];
        for (int i = 0; i < itemArray.length(); i++) {
            try {
                final JSONObject itemJson = itemArray.getJSONObject(i);
                String itemId = itemJson.getString("id");

                LayoutInflater inflater = LayoutInflater.from(Orders.this);
                RelativeLayout layout = (RelativeLayout) inflater.inflate(
                        R.layout.item, null).findViewById(R.id.item);
                linearLayout.addView(layout);
                itemBtn[i] = (Button) findViewById(R.id.btn_item);

                itemBtn[i].setId(2000 + i);
                itemBtn[i].setText("订单编号：" + itemId);
                itemBtn[i].setTextSize(25);
                itemBtn[i].setTag(i);

                if (uiType == 1) {
                    itemBtn[i].setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent go_History = new Intent(Orders.this, History.class);
                            String medicineData = itemJson.toString();
                            go_History.putExtra("medicineData", medicineData);
                            startActivity(go_History);
                        }
                    });
                } else {
                    itemBtn[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent go_Medicine = new Intent(Orders.this, Medicine.class);
                            String medicineData = itemJson.toString();
                            go_Medicine.putExtra("medicineData", medicineData);
                            startActivity(go_Medicine);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void noItem() {
        LayoutInflater inflater = LayoutInflater.from(Orders.this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.item, null).findViewById(R.id.item);
        linearLayout.addView(layout);
        TextView itemIdText = (TextView) findViewById(R.id.item_id);

        itemIdText.setText("无订单");
    }

}