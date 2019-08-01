package com.example.floatballv2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.system.StructTimespec;

public class SharePrerencesUtil {

    private static final String SP = "sp";

    public static final String OPEN_APP_COUNT = "open_app_count";
    public static final String KEY_IS_AUTO_START_HAD = "is_auto_start_had";

    public static final String KEY_FLOAT_WINDOW_TIP_VIDEO = "float_window_tip_video";
    public static final String KEY_FLOAT_WINDOW_TIP_CLEAN = "float_window_tip_clean";
    public static final String KEY_IS_ALREADY_TIP_CLEAN = "is_already_tip_clean";

    public static boolean writeData(Context context,String key,int value){
        SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key,value);
        return editor.commit();
    }

    public static int readData(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(SP,Context.MODE_PRIVATE);
        return sp.getInt(key,1);
    }

    public static boolean putData(Context context,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,value);
        return editor.commit();
    }

    public static boolean getData(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(SP,Context.MODE_PRIVATE);
        return sp.getBoolean(key,false);
    }

    public static boolean putData(Context context,String key,long videoPlayTime){
        SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key,videoPlayTime);
        return editor.commit();
    }

    public static long getData(Context context,String key,long defaut){
        SharedPreferences sp = context.getSharedPreferences(SP,Context.MODE_PRIVATE);
        return sp.getLong(key,defaut);
    }





}
