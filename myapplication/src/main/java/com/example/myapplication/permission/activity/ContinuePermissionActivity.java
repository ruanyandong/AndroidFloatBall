package com.example.myapplication.permission.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.myapplication.permission.AutoStartPermissionHelper;
import com.example.myapplication.permission.ContinueConstants;
import com.example.myapplication.permission.FloatPermissionHalper;
import com.qukandian.util.DeviceUtil;
import com.qukandian.video.qkdbase.R;
import com.qukandian.video.qkdbase.config.AbTestManager;
import com.qukandian.video.qkdbase.model.LockScreenAlertConfigModel;
import com.qukandian.video.qkdbase.permission.AutoStartPermissionHelper;
import com.qukandian.video.qkdbase.permission.ContinueConstants;
import com.qukandian.video.qkdbase.permission.FloatPermissionHalper;
import com.qukandian.video.qkdbase.permission.RomCompatibleUtil;
import com.qukandian.video.qkdbase.permission.rom.RomUtils;
import com.qukandian.video.qkdbase.util.OpenPermissionPageUtils;

public class ContinuePermissionActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FROM_MIAN = 2801;//主页打开权限

    String[] checkPermissions;
    String needPausePermission = null;

    private int index = 0;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_permission);
        mHandler = new Handler(Looper.getMainLooper());
        String permissionsConfig = null;
        if (getIntent() != null) {
            permissionsConfig = getIntent().getStringExtra("permissions");
        }
        if (TextUtils.isEmpty(permissionsConfig)) {
            permissionsConfig = AbTestManager.getInstance().getContinuePermissions();
        }
        if (!TextUtils.isEmpty(permissionsConfig)) {
            checkPermissions = permissionsConfig.split("#");
        }
        if (checkPermissions == null || checkPermissions.length == 0) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        needPausePermission = null;
        boolean isGotoSetting = false;
        if (checkPermissions != null) {
            for (; index < checkPermissions.length; ) {
                if (!isHadPermission(index)) {
                    needPausePermission = checkPermissions[index];
                    boolean isApplySuccess = applyPermissionTips(needPausePermission, true);
                    index++;
                    if (!isApplySuccess) {
                        continue;
                    }
                    isGotoSetting = true;
                    break;
                } else {
                    index++;
                }
            }
        }

        if (!isGotoSetting) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(needPausePermission)) {
            applyPermissionTips(needPausePermission, false);
            needPausePermission = null;
        }
    }

    private boolean applyPermissionTips(String permission, boolean isResume) {
        boolean isApplySuccess = false;
        switch (permission) {
            case ContinueConstants.FLOAT_WINDOW:
                // 跳转到悬浮窗权限设置页
                if (isResume) {
                    isApplySuccess = FloatPermissionHalper.applyWindowPermission(ContinuePermissionActivity.this);
                } else if (!isVivoLittelSix()) {
                    handlerFixToastTransparent(true, true, "-悬浮窗", 800);
                }
                break;
            case ContinueConstants.USED_STATS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isResume) {
                        isApplySuccess = FloatPermissionHalper.applyUsedPermission(this);
                    } else {
                        handlerFixToastTransparent(true, true, null, 1000);
                    }
                }
                break;
            case ContinueConstants.AUTO_START:
                if (isResume) {
                    isApplySuccess = AutoStartPermissionHelper.applyPermission(ContinuePermissionActivity.this);
                } else if (!isVivoLittelSix()) {
                    handlerFixToastTransparent(true, true, "-自启动", 800);
                }
                break;
            case ContinueConstants.LOCK_SCREEN:
                // 跳转到悬浮窗权限设置页
                if (isResume) {
                    OpenPermissionPageUtils.openLockScreenPermissionPage(this);
                    isApplySuccess = true;
                } else if (!isVivoLittelSix()) {
                    handlerFixToastTransparent(true, true, "-锁屏显示和悬浮窗", 800);
                }
                break;
        }

        return isApplySuccess;
    }

    private void handlerFixToastTransparent(boolean isCheckHandler, boolean isAddNewTask, String title, int delay) {
//        if (isCheckHandler && (RomCompatibleUtil.isOppoR9s() || RomCompatibleUtil.isMiOs7())) {
//            startFixToastTransparent(isAddNewTask, title);
//        } else {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startFixToastTransparent(isAddNewTask, title);
//                }
//            }, delay);
//        }
    }

    private void startFixToastTransparent(boolean isAddNewTask, String title) {
        Intent intent = new Intent(ContinuePermissionActivity.this, FixToastTransparentActivity.class);
        if (isAddNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra("title", title);
        }
        startActivity(intent);
    }

    private boolean isVivoLittelSix() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M && RomUtils.checkIsVivoRom();
    }

    private boolean isHadPermission(int index) {
        if (index < 0 || index >= checkPermissions.length) {
            return true;
        }
        String permission = checkPermissions[index];
        switch (permission) {
            case ContinueConstants.FLOAT_WINDOW:
                return FloatPermissionHalper.checkWindowPermission(this);
            case ContinueConstants.USED_STATS:
                return FloatPermissionHalper.checkUsagePermission(this);
            case ContinueConstants.AUTO_START:
                return AutoStartPermissionHelper.checkAutoStartPermission();
            case ContinueConstants.LOCK_SCREEN:
                return LockScreenAlertConfigModel.getModelFromSp().isLockScreenOpen();
//                return false;
        }
        return true;
    }

}