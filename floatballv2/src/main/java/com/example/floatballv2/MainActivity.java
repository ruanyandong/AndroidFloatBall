package com.example.floatballv2;

import android.animation.ValueAnimator;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.floatballv2.service.StartFloatBallService;
import com.example.floatballv2.utils.FloatPermissionHalper;
import com.example.floatballv2.utils.SharePrerencesUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "is_just_apply";
    static Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.openPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!FloatPermissionHalper.checkWindowPermission(MainActivity.this)) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivityForResult(intent, 1);
                        FloatPermissionHalper.applyWindowPermission(MainActivity.this);
                        SharePrerencesUtil.putData(MainActivity.this,TAG,true);
                    } else {
                        showFloatBall();
                    }
                }else{
                    showFloatBall();
                }
            }
        });

        Log.d(TAG, "onCreate: =========");
        permissionCheck();
        Log.d(TAG, "onCreate: =========");


        // 垃圾数值动画
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(407,0);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                Log.d("ruanyandong", "onAnimationUpdate: value="+value);

                long time = animation.getCurrentPlayTime();
                Log.d("ruanyandong", "onAnimationUpdate: time="+time);

            }
        });
        valueAnimator.start();

        // 垃圾数值动画
        final ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(2.19f,0);
        valueAnimator1.setDuration(3000);
        valueAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                Log.d("ruanyandong=", "onAnimationUpdate: "+value);


                long time = animation.getCurrentPlayTime();

                Log.d("ruanyandong=", "onAnimationUpdate: "+time);

            }
        });
        valueAnimator1.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ============");
        permissionCheck();
        Log.d(TAG, "onStart: ============");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ==========");
        permissionCheck();
        Log.d(TAG, "onRestart: ==========");

    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ==========");
        permissionCheck();
        Log.d(TAG, "onStop: ==========");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ==========");
        permissionCheck();
        Log.d(TAG, "onDestroy: ==========");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: =============");
        permissionCheck();
        Log.d(TAG, "onPause: =============");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ===========");
//        permissionCheck();
//        Log.d(TAG, "onResume: ===========");
        boolean isApply = SharePrerencesUtil.getData(this,TAG);
        if (isApply){
            checkPermissionAndShowFloatBall();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Log.d(TAG, "onActivityResult: =======");
            permissionCheck();
            Log.d(TAG, "onActivityResult: =======");
        }
    }


    private void checkPermissionAndShowFloatBall(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    boolean Open1 = Settings.canDrawOverlays(MainActivity.this);
                    boolean Open2 = FloatPermissionHalper.checkFloatPermission(MainActivity.this);
                    boolean Open3 = FloatPermissionHalper.checkWindowPermission(MainActivity.this);

                    if (Open3){
                        //开启
                        showFloatBall();
                    }else {
                        //关闭
                    }
                    Log.e(TAG, "open = " + Open1+" "+Open2+" "+Open3);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    AppOpsManager appOpsMgr = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), getPackageName());
                    if (mode == 1 || mode == 0){
                        //showFloatBall();
                        //权限已开起
                    }else if (mode == 2 || mode == 3){
                        //权限已关闭
                    }
                    Log.e(TAG, "mode = " + mode);
                    //此方法不可行，亲测华为8.0手机  未申请状态时  也返回1
                }
            }
        },1000);
    }


    private void permissionCheck() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    boolean Open1 = Settings.canDrawOverlays(MainActivity.this);
                    boolean Open2 = FloatPermissionHalper.checkFloatPermission(MainActivity.this);
                    boolean Open3 = FloatPermissionHalper.checkWindowPermission(MainActivity.this);

//                    if (nOpen){
//                        //开启
//                        //showFloatBall();
//                    }else {
//                        //关闭
//                    }
                    Log.e(TAG, "open = " + Open1+" "+Open2+" "+Open3);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    AppOpsManager appOpsMgr = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), getPackageName());
                    if (mode == 1 || mode == 0){
                        //showFloatBall();
                        //权限已开起
                    }else if (mode == 2 || mode == 3){
                        //权限已关闭
                    }
                    Log.e(TAG, "mode = " + mode);
                    //此方法不可行，亲测华为8.0手机  未申请状态时  也返回1
                }
            }
        },1000);
    }


    private void showFloatBall() {
        Intent intent = new Intent(this, StartFloatBallService.class);
        startService(intent);
        //finish();
    }
}
