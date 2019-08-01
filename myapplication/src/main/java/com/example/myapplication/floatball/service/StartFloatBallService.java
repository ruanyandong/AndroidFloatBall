package com.example.myapplication.floatball.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.myapplication.floatball.ViewManager;
import com.qukandian.sdk.account.AccountInstance;
import com.qukandian.sdk.config.Constants;
import com.qukandian.util.SpUtil;
import com.qukandian.video.qkdbase.config.AbTestManager;
import com.qukandian.video.qkdbase.floatball.ViewManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.qukandian.sdk.config.BaseSPKey.KEY_FLOAT_WINDOW_CENTER_TIP_CLICK;
import static com.qukandian.sdk.config.BaseSPKey.KEY_FLOAT_WINDOW_TIP_CLEAN;
import static com.qukandian.sdk.config.BaseSPKey.KEY_FLOAT_WINDOW_TIP_VIDEO;


public class StartFloatBallService extends Service {

    public static final String TAG = "FloatBall";

    public static boolean sIsUserFloatWindowEnable = true;

    private Timer mTimer;
    private long mLastVideoTimeTamp, mLastRubbishTimeTamp;

    public StartFloatBallService() {
        try {
            mLastVideoTimeTamp = SpUtil.getData(KEY_FLOAT_WINDOW_TIP_VIDEO, 0L);
            mLastRubbishTimeTamp = SpUtil.getData(KEY_FLOAT_WINDOW_TIP_CLEAN, 0L);
            sIsUserFloatWindowEnable = SpUtil.getData(AccountInstance.FLOAT_WINDOW, true);
        } catch (Exception ignore) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long timeTamp = System.currentTimeMillis();
                if (AbTestManager.getInstance().isFloatWindowTip() && timeTamp - mLastVideoTimeTamp > Constants.ONE_DAY_TIME_UNIT) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour >= 19 && hour <= 22) {
                        mLastVideoTimeTamp = System.currentTimeMillis();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).showFloatTips("视频更新啦", true);
                            }
                        });
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).removeFloatTips();
                            }
                        }, 10000);
                        SpUtil.putData(KEY_FLOAT_WINDOW_TIP_VIDEO, mLastVideoTimeTamp);
                        Log.d(TAG, "Date获取当前日期时间 video hour:" + hour);
                    }
                }

                if (AbTestManager.getInstance().isFloatWindowTip() && timeTamp - mLastRubbishTimeTamp > Constants.ONE_DAY_TIME_UNIT) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour >= 12 && hour <= 18) {
                        mLastRubbishTimeTamp = System.currentTimeMillis();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).showFloatTips("垃圾可清理", false);
                            }
                        });
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).removeFloatTips();
                            }
                        }, 10000);
                        SpUtil.putData(KEY_FLOAT_WINDOW_TIP_CLEAN, mLastRubbishTimeTamp);
                        Log.d(TAG, "Date获取当前日期时间 rubbish hour:" + hour);
                    }
                }


            }
        };

        mTimer.schedule(task, 5000, 1800000);//10000

        //变蓝“点我”提示，5分钟每点击过 就展示
//        if (!SpUtil.getData(KEY_FLOAT_WINDOW_CENTER_TIP_CLICK, false)) {
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                if (AbTestManager.getInstance().isFloatWindowTip() && !SpUtil.getData(KEY_FLOAT_WINDOW_CENTER_TIP_CLICK, false)) {
//                    ViewManager.getInstance(StartFloatBallService.this).showBlueCenterTips("点我");
//                }
//            }, 5 * 60 * 1000);
//        }

        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AbTestManager.getInstance().isFloatWindow()) {
            String isFloatStart;
            if (intent != null && !TextUtils.isEmpty(isFloatStart = intent.getStringExtra("isFloatStart"))) {
                if (TextUtils.equals(isFloatStart, "2")) {
                    ViewManager.getInstance(this).dismissFloatWindow();
                } else if (TextUtils.equals(isFloatStart, "1")) {
                    ViewManager.getInstance(this).startFloatWindow();
                }
            } else {
                if (sIsUserFloatWindowEnable) {
                    ViewManager.getInstance(this).startFloatWindow();
                }
            }
        }
        return START_STICKY;
    }

}