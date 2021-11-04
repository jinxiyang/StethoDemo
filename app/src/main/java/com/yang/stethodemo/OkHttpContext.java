package com.yang.stethodemo;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class OkHttpContext {
    private volatile static OkHttpContext instance;

    private OkHttpClient okHttpClient;

    private OkHttpContext() {
        createOkHttpClient();
    }

    public static OkHttpContext getInstance() {
        if (instance == null) {
            synchronized (OkHttpContext.class) {
                if (instance == null) {
                    instance = new OkHttpContext();
                }
            }
        }
        return instance;
    }

    private void createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //添加StethoInterceptor网络拦截器
        builder.addNetworkInterceptor(new StethoInterceptor());
        //添加加解密网络拦截器
        builder.addNetworkInterceptor(new EncryptionInterceptor());
        okHttpClient = builder.build();
    }


    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
