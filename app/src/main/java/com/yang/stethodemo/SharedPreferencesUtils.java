package com.yang.stethodemo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private static final String SP_NAME = "sp_app";

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void putString(Context context, String key, String value){
        getSharedPreferences(context).edit()
                .putString(key, value)
                .apply();
    }
}
