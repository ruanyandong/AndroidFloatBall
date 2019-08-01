package com.example.myapplication.floatball;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.qukandian.util.ListUtils;
import com.qukandian.video.qkdbase.BuildConfig;
import com.qukandian.video.qkdbase.floatball.service.StartFloatBallService;
import com.qukandian.video.qkdbase.floatball.utils.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by weiqi on 2019/2/16
 */
public class ClearMemoryHelper {

    private static ArrayList<String> ignoreProcesses = new ArrayList<String>() {
        {
            add(BuildConfig.APPLICATION_ID);
        }
    };

    private static void startClearOld(Context context, @NonNull List<String> list) {
        if (context == null) {
            return;
        }
        //To change body of implemented methods use File | Settings | File Templates.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return;
        }
        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);

        if (infoList != null) {
            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                if (appProcessInfo == null) {
                    continue;
                }

                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                String[] pkgList;
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                        && (pkgList = appProcessInfo.pkgList) != null) {
                    for (String aPkgList : pkgList) {//pkgList 得到该进程下运行的包名
                        if (!TextUtils.isEmpty(aPkgList)) {
                            list.add(aPkgList);
                        }
                    }
//                    Log.e(StartFloatBallService.TAG, "It will be killed, processName:" + appProcessInfo.processName + " importance:" +
// appProcessInfo.importance
//                            + " list:" + Arrays.toString(pkgList));
                } else {
//                    Log.d(StartFloatBallService.TAG, "processName:" + appProcessInfo.processName + " importance:" + appProcessInfo.importance);
                }
            }
        }

        killProcessByPackage(am, list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void startClearLolp(Context context, @NonNull List<String> list) {
        if (context == null) {
            return;
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return;
        }
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 100, time);
        // Sort the stats by the last time used
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                list.add(usageStats.getPackageName());
            }
//            if (!mySortedMap.isEmpty()) {
//                String topPackageName;
//                topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//            }

            killProcessByPackage((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE), list);

        }
    }

    private static void killProcessByPackage(ActivityManager am, List<String> list) {
        if (am == null || list == null) {
            return;
        }
        for (String item : list) {
            if (!TextUtils.isEmpty(item) && !BuildConfig.APPLICATION_ID.contains(item)) {
                Log.d(StartFloatBallService.TAG, "It will be killed, processName:" + item);
                am.killBackgroundProcesses(item);
            } else {
                Log.e(StartFloatBallService.TAG, "It ignore, processName:" + item);
            }
        }
    }

    public static void startMemoryClean(Context context, OnMemoryCleanCallback callback) {
        if (context == null) {
            if (callback != null) {
                callback.onMemoryclean(false, 0);
            }
            return;
        }
        long beforeMem = 0L, afterMem = 0L;
        List<String> list = new ArrayList<>();
        try {
            beforeMem = AppUtil.getAvailMemory(context);
            Log.d(StartFloatBallService.TAG, "before 可用内存: " + beforeMem + " 百分比:" + AppUtil.getAvailPrecent(context));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startClearLolp(context, list);
            } else {
                startClearOld(context, list);
            }
            afterMem = AppUtil.getAvailMemory(context);
            Log.d(StartFloatBallService.TAG, "after 可用内存:" + afterMem + " 百分比:" + AppUtil.getAvailPrecent(context)
                    + " clearCount:" + list.size() + " DM:" + (afterMem - beforeMem) + "M");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (callback != null) {
            callback.onMemoryclean(!ListUtils.isListEmpty(list), (int) (afterMem - beforeMem));
        }
    }

    public interface OnMemoryCleanCallback {
        void onMemoryclean(boolean result, int clearSize);
    }
}
