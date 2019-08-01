package com.example.myapplication.floatball.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.example.myapplication.floatball.model.StorageSize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;

public class AppUtil {

    /**
     * 描述：获取可用内存.
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化

        return memoryInfo.availMem;
    }

    //获取内存信息
    public static int getAvailPrecent(Context context) {
        int precent = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                if (mi.totalMem > 0) {
                    precent = (int) (mi.availMem * 100 / mi.totalMem);
                }
            }
        } catch (Exception ignore) {
        }
        return precent < 0 ? 0 : precent;
    }

    /**
     * 获取占用内存信息
     * @param context
     * @return 单位M
     */
    public static int getUsedSize(Context context) {
        int used = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                if (mi.totalMem > 0 && mi.totalMem >= mi.availMem) {
                    used = (int) ((mi.totalMem - mi.availMem) / 1048576);
                }
            }
        } catch (Exception ignore) {
        }

        return used <= 0 ? 300 : used;
    }

    //获取占用内存占比信息
    public static int getUsedPrecent(Context context) {
        int precent = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                if (mi.totalMem > 0 && mi.totalMem >= mi.availMem) {
                    precent = (int) ((mi.totalMem - mi.availMem) * 100 / mi.totalMem);
                }
            }
        } catch (Exception ignore) {
        }

        return precent < 0 ? 0 : precent;
    }

    /**
     * 描述：总内存.
     *
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
//            for (String str : strs) {
//                L.d(AppUtil.class, str + "\t");
//            }
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory * 1024;
    }

    public static StorageSize convertStorageSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        StorageSize sto = new StorageSize();
        if (size >= gb) {

            sto.suffix = "GB";
            sto.value = (float) size / gb;
            return sto;
        } else if (size >= mb) {

            sto.suffix = "MB";
            sto.value = (float) size / mb;

            return sto;
        } else if (size >= kb) {


            sto.suffix = "KB";
            sto.value = (float) size / kb;

            return sto;
        } else {
            sto.suffix = "B";
            sto.value = (float) size;

            return sto;
        }
    }

    /**
     * 获取屏幕宽度的方法
     *
     * @param context
     * @return
     */
    public static int getScreenWith(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度的方法
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();

    }

    //获取状态栏高度
    public static int getStatusHeight(@NonNull Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }
    }

}
