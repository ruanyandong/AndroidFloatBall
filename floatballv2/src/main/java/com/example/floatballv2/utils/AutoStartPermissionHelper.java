package com.example.floatballv2.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import static com.example.floatballv2.utils.SharePrerencesUtil.KEY_IS_AUTO_START_HAD;


public class AutoStartPermissionHelper {

    private static boolean isHadAutoStart = false;

    public static void setIsHadAutoStart(Context context,boolean isAutoStart) {
        if (isHadAutoStart != isAutoStart) {
            SharePrerencesUtil.putData(context,KEY_IS_AUTO_START_HAD, isAutoStart);
        }
        isHadAutoStart = isAutoStart;
    }

    public static boolean checkAutoStartPermission(Context context) {
        try {
            isHadAutoStart = SharePrerencesUtil.getData(context,KEY_IS_AUTO_START_HAD);
        } catch (Exception ignore) {
        }
        return isHadAutoStart;
    }


    public static boolean applyPermission(Context context) {
        try {
            context.startActivity(getSettingIntent());
        } catch (Exception e) {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
        return true;
    }

    private static Intent getSettingIntent() {
        ComponentName componentName = null;
        String brand = Build.BRAND;
        switch (brand.toLowerCase()) {
            case "samsung":
                componentName = new ComponentName("com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei":
                componentName = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                break;
            case "xiaomi":
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo":
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case "oppo":
                if (RomCompatibleUtil.isOppoR9s()) {
                    componentName = new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity");
                } else {
                    componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                }
                break;
            case "360":
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu":
                componentName = new ComponentName("com.meizu.safe",
                        "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus":
                componentName = new ComponentName("com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            default:
                break;
        }
        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (componentName != null) {
            intent.setComponent(componentName);
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        return intent;
    }
}
