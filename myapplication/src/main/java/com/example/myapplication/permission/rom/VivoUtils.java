package com.example.myapplication.permission.rom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class VivoUtils {

    public static boolean checkFloatWindowPermission(Context context) {
        return getFloatPermissionStatus(context) == 0;
    }

    public static void applyPermission(Context context) {
        Intent intent = applyIntent(context);
        if (intent != null && context != null) {
            context.startActivity(intent);
        }
    }

    public static void applyPermission(Activity activity, int requestCode) {
        Intent intent = applyIntent(activity);
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void applyPermission(Fragment fragment, int requestCode) {
        Intent intent = applyIntent(fragment.getContext());
        if (intent != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 获取悬浮窗权限状态
     *
     * @param context 上下文
     * @return 1或其他是没有打开，0是打开，该状态的定义和{@link android.app.AppOpsManager#MODE_ALLOWED}，MODE_IGNORED等值差不多，自行查阅源码
     */
    private static int getFloatPermissionStatus(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = context
                .getContentResolver()
                .query(uri, null, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.getColumnNames();
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentlmode"));
                cursor.close();
                return currentmode;
            } else {
                cursor.close();
                return getFloatPermissionStatus2(context);
            }

        } else {
            return getFloatPermissionStatus2(context);
        }
    }


    /**
     * vivo比较新的系统获取方法
     *
     * @param context 上下文
     * @return 1或其他是没有打开，0是打开
     */
    private static int getFloatPermissionStatus2(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                cursor.close();
                return currentmode;
            } else {
                cursor.close();
                return 1;
            }
        }
        return 1;
    }

    private static List<Intent> intentList(Context paramContext) {
        List<Intent> intents = new ArrayList<>();
        Intent localIntent = new Intent();
        localIntent.putExtra("packagename", paramContext.getPackageName());
        localIntent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
        intents.add(localIntent);

//        localIntent = new Intent();
//        localIntent.putExtra("packagename", paramContext.getPackageName());
//        localIntent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager"));
//        intents.add(localIntent);

        localIntent = new Intent();
        localIntent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity"));
        intents.add(localIntent);

        localIntent = new Intent();
        localIntent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.MainActivity"));
        intents.add(localIntent);

        localIntent = new Intent();
        localIntent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.MainGuideActivity"));
        intents.add(localIntent);
        return intents;
    }

    @SuppressLint("WrongConstant")
    private static boolean canQueryIntent(Context paramContext, Intent paramIntent) {
        boolean bool;
        if (paramContext.getPackageManager().queryIntentActivities(paramIntent, PackageManager.GET_PERMISSIONS).size() > 0)
            bool = true;
        else
            bool = false;
        return bool;
    }

    private static Intent applyIntent(Context paramContext) {
        List<Intent> intentList = intentList(paramContext);
        Intent destIntent = null;
        if (intentList.isEmpty()) {
            return null;
        }
        for (Intent intent : intentList) {
            if (canQueryIntent(paramContext, intent)) {
                destIntent = intent;
                break;
            }
        }
        return destIntent;
    }
}
