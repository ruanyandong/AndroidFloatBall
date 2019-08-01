package com.example.floatballv2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.example.floatballv2.anager.ViewManager;
import com.example.floatballv2.utils.SharePrerencesUtil;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.floatballv2.utils.SharePrerencesUtil.KEY_FLOAT_WINDOW_TIP_CLEAN;
import static com.example.floatballv2.utils.SharePrerencesUtil.KEY_FLOAT_WINDOW_TIP_VIDEO;
import static com.example.floatballv2.utils.SharePrerencesUtil.KEY_IS_ALREADY_TIP_CLEAN;

public class StartFloatBallService extends Service {

    Timer timer ;
    private long mLastVideoTimeTamp, mLastRubbishTimeTamp;

    public static final int TIME_DAY = 24 * 60 * 60 * 1000;

    public StartFloatBallService() {
        try {
            mLastVideoTimeTamp = SharePrerencesUtil.getData(this,SharePrerencesUtil.KEY_FLOAT_WINDOW_TIP_VIDEO, 0L);
            mLastRubbishTimeTamp = SharePrerencesUtil.getData(this, KEY_FLOAT_WINDOW_TIP_CLEAN, 0L);
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
        ViewManager manager = ViewManager.getInstance(this);
        manager.showFloatBall();

        if (timer != null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long timeTamp = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                if (timeTamp - mLastRubbishTimeTamp > TIME_DAY){
                    if (hour >= 12 && hour <= 18) {
                        mLastRubbishTimeTamp = System.currentTimeMillis();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).showFloatTips("垃圾可清理");
                            }
                        });
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).removeFloatTips();
                            }
                        }, 10000);
                        SharePrerencesUtil.putData(StartFloatBallService.this,KEY_IS_ALREADY_TIP_CLEAN,true);
                        SharePrerencesUtil.putData(StartFloatBallService.this,KEY_FLOAT_WINDOW_TIP_CLEAN, mLastRubbishTimeTamp);
                    }
                }
                if (timeTamp - mLastVideoTimeTamp > TIME_DAY){
                    if (hour >= 19 && hour <= 22){
                        mLastVideoTimeTamp = System.currentTimeMillis();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).showFloatTips("视频更新啦");
                            }
                        });
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewManager.getInstance(StartFloatBallService.this).removeFloatTips();
                            }
                        },10000);
                        SharePrerencesUtil.putData(StartFloatBallService.this,KEY_FLOAT_WINDOW_TIP_VIDEO, mLastVideoTimeTamp);
                    }
                }

            }
        };
        timer.schedule(timerTask,5000,30000);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


}