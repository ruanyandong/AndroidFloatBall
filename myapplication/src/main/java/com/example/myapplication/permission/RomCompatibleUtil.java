package com.example.myapplication.permission;

import android.os.Build;
import android.text.TextUtils;

import com.qukandian.util.DeviceUtil;

public class RomCompatibleUtil {

    public static boolean isOppoR9s() {
        String phoneMode = DeviceUtil.getPhoneMode();
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && DeviceUtil.isOPPO() && !TextUtils.isEmpty(phoneMode) && phoneMode.contains("R9s");
    }

    public static boolean isMiOs7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && DeviceUtil.isMIUI();
    }
}
