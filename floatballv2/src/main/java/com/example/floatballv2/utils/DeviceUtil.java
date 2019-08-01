package com.example.floatballv2.utils;

import android.os.Build;
import android.text.TextUtils;

public class DeviceUtil {

    public DeviceUtil() {
    }


    public static String getPhoneMode() {
        String mode = Build.MODEL;
        if (TextUtils.isEmpty(mode)) {
            mode = "";
        }

        return mode;
    }


    public static boolean isOPPO() {
        return "OPPO".equalsIgnoreCase(Build.BRAND);
    }

    public static boolean isVivo() {
        return "vivo".equalsIgnoreCase(Build.BRAND);
    }

    public static boolean isHuaWei() {
        return "HUAWEI".equalsIgnoreCase(Build.MANUFACTURER) || "HONOR".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isMIUI() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    public static boolean isSamsung() {
        return "samsung".equals(Build.MANUFACTURER);
    }

    public static boolean isMeiZu() {
        return "Meizu".equals(Build.MANUFACTURER);
    }


}
