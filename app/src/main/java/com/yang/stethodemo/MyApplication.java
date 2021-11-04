package com.yang.stethodemo;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化stetho
        Stetho.initializeWithDefaults(this);
    }
}
