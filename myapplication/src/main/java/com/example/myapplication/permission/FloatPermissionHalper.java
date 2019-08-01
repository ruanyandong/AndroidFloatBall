package com.example.myapplication.permission;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.qukandian.video.qkdbase.permission.rom.HuaweiUtils;
import com.qukandian.video.qkdbase.permission.rom.MeizuUtils;
import com.qukandian.video.qkdbase.permission.rom.MiuiUtils;
import com.qukandian.video.qkdbase.permission.rom.OppoUtils;
import com.qukandian.video.qkdbase.permission.rom.QikuUtils;
import com.qukandian.video.qkdbase.permission.rom.RomUtils;
import com.qukandian.video.qkdbase.permission.rom.VivoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class FloatPermissionHalper {

    public static boolean checkWindowPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (RomUtils.checkIsHuaweiRom()) {
                    return HuaweiUtils.checkFloatWindowPermission(context);
                } else if (RomUtils.checkIs360Rom()) {
                    return QikuUtils.checkFloatWindowPermission(context);
                } else if (RomUtils.checkIsOppoRom()) {
                    return OppoUtils.checkFloatWindowPermission(context);
                } else if (RomUtils.checkIsVivoRom()) {
                    return VivoUtils.checkFloatWindowPermission(context);
                } else if (RomUtils.checkIsMiuiRom()) {
                    return MiuiUtils.checkFloatWindowPermission(context);
                } else if (RomUtils.checkIsMeizuRom()) {
                    return MeizuUtils.checkFloatWindowPermission(context);
                }
            } else {
                if (RomUtils.checkIsVivoRom()) {
                    return VivoUtils.checkFloatWindowPermission(context);
                }
            }
            return commonROMPermissionCheck(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtils.checkIsMeizuRom()) {
            return MeizuUtils.checkFloatWindowPermission(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    public static boolean applyWindowPermission(Context context) {
        boolean isApply = false;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (RomUtils.checkIsHuaweiRom()) {
                    HuaweiUtils.applyPermission(context);
                } else if (RomUtils.checkIs360Rom()) {
                    QikuUtils.applyPermission(context);
                } else if (RomUtils.checkIsOppoRom()) {
                    OppoUtils.applyOppoPermission(context);
                } else if (RomUtils.checkIsVivoRom()) {
                    VivoUtils.applyPermission(context);
                } else if (RomUtils.checkIsMiuiRom()) {
                    MiuiUtils.applyMiuiPermission(context);
                } else if (RomUtils.checkIsMeizuRom()) {
                    MeizuUtils.applyPermission(context);
                }
            } else {
                if (RomUtils.checkIsVivoRom()) {
                    VivoUtils.applyPermission(context);
                } else if (RomUtils.checkIsOppoRom()) {
                    OppoUtils.applyOppoPermission(context);
                } else if (RomUtils.checkIsMeizuRom()) {
                    MeizuUtils.applyPermission(context);
                } else {
                    try {
                        commonROMPermissionApplyInternal(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            isApply = true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return isApply;
    }

    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static boolean checkUsagePermission(Context context) {
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) {
                return true;
            }
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else {
            return true;
        }
    }

    public static boolean applyUsedPermission(Context context) {
        boolean isApplyUsed = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
            isApplyUsed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isApplyUsed;
    }


}
