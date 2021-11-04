package com.yang.stethodemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //页码
    private int pageNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(v -> sendRequest());

        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(v -> modifySharedPreferences());
    }

    private void sendRequest() {
        OkHttpClient okHttpClient = OkHttpContext.getInstance().getOkHttpClient();
        Request request = new Request.Builder()
                .get()
                //玩Android   首页文章列表接口
                .url("https://www.wanandroid.com/article/list/" + (pageNo++) + "/json")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
        });
    }

    private void modifySharedPreferences() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        //当前时间
        String time = format.format(new Date());
        SharedPreferencesUtils.putString(this, "time", time);
        Toast.makeText(this, "当前时间：" + time, Toast.LENGTH_SHORT).show();
    }

}