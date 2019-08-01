package com.example.myapplication.floatball;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.myapplication.floatball.utils.AppUtil;
import com.example.myapplication.floatball.widget.FloatBall;
import com.example.myapplication.floatball.widget.FloatMenu;


/**
 * Created by weiqi on 2019/2/16
 * 管理者，单例模式
 */
public class ViewManager {

    private final int TIP_STATUS_NORMAL = 0;
    private final int TIP_STATUS_CLEAN = 1;
    private final int TIP_STATUS_VIDEO = 2;

    private static ViewManager manager;
    MyOnClickListener mClickListener;

    private FloatBall floatBall;
    private FloatMenu floatMenu;
    private TextView floatTips;

    private WindowManager windowManager;
    private LayoutParams floatBallParams;
    private LayoutParams floatMenuParams;

    private Context context;
    private int mScreenW, mScreenH;

    private boolean mIsStatusBall = true;
    private int mTipStatus = TIP_STATUS_NORMAL;//0无提示 1清理提示 2视频提示

    //私有化构造函数
    private ViewManager(Context context) {
        this.context = context;
        init();
    }

    //获取ViewManager实例
    public static ViewManager getInstance(Context context) {
        if (manager == null) {
            synchronized (ViewManager.class) {
                if (manager == null) {
                    manager = new ViewManager(context);
                }
            }
        }
        return manager;
    }

    private void init() {
        int screenH = AppUtil.getScreenHeight(ContextUtil.getContext());
        mScreenW = AppUtil.getScreenWith(ContextUtil.getContext());
        Activity activity = ActivityTaskManager.getActivityTop();
        try {
            if (activity != null && NotchScreenUtil.hasNotchScreen(activity)) {
                mScreenH = DensityUtil.getHasVirtualKey(ContextUtil.getContext());
            }
            if (mScreenH < screenH) {
                mScreenH = screenH;
            }
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            floatBall = new FloatBall(context);
            floatMenu = new FloatMenu(context);

            mClickListener = new MyOnClickListener();
            floatBall.setOnTouchListener(touchListener);
            floatBall.setOnClickListener(mClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //隐藏悬浮球和桌面
    public void dismissFloatWindow() {
        removeFloatBall();
        removeFloatTips();
        hideFloatMenu();
    }

    public void startFloatWindow() {
        if ((floatBall != null && floatBall.getParent() != null) || (floatMenu != null && floatMenu.getParent() != null)) {
            return;
        }

        if (windowManager == null && context != null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        Point ballPoint = new Point();
        try {
            String pointStr = SpUtil.getData(KEY_FLOAT_WINDOW_BALL_POINT, "");
            if (!TextUtils.isEmpty(pointStr)) {
                String[] points = pointStr.split("#");
                if (!TextUtils.isEmpty(pointStr) && points.length == 2) {
                    ballPoint.x = Integer.parseInt(points[0]);
                    ballPoint.y = Integer.parseInt(points[1]);
                    floatBall.setLeftOrRight(Integer.parseInt(points[0]) == 0);
                }
            }
        } catch (Exception ignore) {
        }
        showFloatBall(ballPoint);
        ReportUtil.onFloatWindowReport(new ReportInfo().setAction(ParamsManager.Cmd243.ACTION_BALL));
    }

    //显示浮动小球
    public void showFloatBall(Point point) {
        if (floatBall != null && floatBall.getParent() != null) {
            return;
        }
        try {
            if (floatBallParams == null) {
                floatBallParams = new LayoutParams();
                floatBallParams.x = (point != null && point.x > 0) ? point.x : 0;
                floatBallParams.y = (point != null && point.y > 0) ? point.y : DensityUtil.getScreenHeight(ContextUtil.getContext()) / 3;
                floatBallParams.width = floatBall.width;
                floatBallParams.height = floatBall.width;
                floatBallParams.gravity = Gravity.TOP | Gravity.START;
                floatBallParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
                floatBallParams.format = PixelFormat.RGBA_8888;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    floatBallParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    floatBallParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
                }
            }
            windowManager.addView(floatBall, floatBallParams);
            mIsStatusBall = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //隐藏球
    public void removeFloatBall() {
        if (windowManager != null && floatBall != null && floatBall.getParent() != null) {
            windowManager.removeView(floatBall);
        }
    }

    //显示底部菜单
    private void showFloatMenu(int tipStatus) {
        if (floatMenu != null && floatMenu.getParent() != null) {
            return;
        }
        try {
            mIsStatusBall = false;
            if (floatMenuParams == null) {
                floatMenuParams = new LayoutParams();
                floatMenuParams.width = mScreenW;
                floatMenuParams.height = mScreenH - AppUtil.getStatusHeight(ContextUtil.getContext());
                floatMenuParams.gravity = Gravity.BOTTOM;
                floatMenuParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
                floatMenuParams.format = PixelFormat.RGBA_8888;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    floatMenuParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    floatMenuParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
                }
            }
            windowManager.addView(floatMenu, floatMenuParams);
            String action = ParamsManager.Cmd243.ACTION_MENU;
            switch (tipStatus) {
                case TIP_STATUS_CLEAN:
                    action = ParamsManager.Cmd243.ACTION_MENU_CLEAN;
                    break;
                case TIP_STATUS_VIDEO:
                    action = ParamsManager.Cmd243.ACTION_MENU_VIDEO;
                    break;
            }
            ReportUtil.onFloatWindowReport(new ReportInfo().setAction(action));
            mTipStatus = TIP_STATUS_NORMAL;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //隐藏底部菜单
    public void hideFloatMenu() {
        if (windowManager != null && floatMenu != null && floatMenu.getParent() != null) {
            windowManager.removeView(floatMenu);
        }
    }

    //显示提示
    public void showFloatTips(String text, boolean isVideo) {
        if (!mIsStatusBall || floatBallParams == null || floatBall == null || floatBall.getParent() == null || TextUtils.isEmpty(text) ||
                (floatTips != null && floatTips.getParent() != null)) {
            return;
        }
        try {
            LayoutParams tipsParams = new LayoutParams();
            tipsParams.width = LayoutParams.WRAP_CONTENT;
            tipsParams.height = LayoutParams.WRAP_CONTENT;
            tipsParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            tipsParams.format = PixelFormat.RGBA_8888;
            tipsParams.gravity = Gravity.LEFT | Gravity.TOP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tipsParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                tipsParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
            }
            if (floatTips == null) {
                floatTips = getFloatTips();
            }

            if (floatBallParams.x == 0) {
                tipsParams.x = floatBallParams.x + floatBall.width + DensityUtil.dip2px(5);
                floatTips.setBackgroundResource(R.drawable.icon_clock_tips_left_bg);
            } else {
                int width = 0;
                if (floatTips.getPaint() != null) {
                    width = (int) floatTips.getPaint().measureText(text, 0, text.length());
                }
                if (width <= 0) {
                    width = DensityUtil.dip2px(14 * text.length());
                }
                tipsParams.x = floatBallParams.x - width - DensityUtil.dip2px(30);
                floatTips.setBackgroundResource(R.drawable.icon_clock_tips_right_bg);
            }
            tipsParams.y = floatBallParams.y;

            floatTips.setText(text);
            windowManager.addView(floatTips, tipsParams);
            windowManager.updateViewLayout(floatTips, tipsParams);

            if (isVideo) {
                mTipStatus = TIP_STATUS_VIDEO;
            } else {
                mTipStatus = TIP_STATUS_CLEAN;
            }
            ReportUtil.onFloatWindowReport(new ReportInfo().setAction(isVideo ? ParamsManager.Cmd243.ACTION_TIP_VIDEO :
                    ParamsManager.Cmd243.ACTION_TIP_CLEAN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //隐藏提示
    public void removeFloatTips() {
        if (windowManager != null && floatTips != null && floatTips.getParent() != null) {
            windowManager.removeView(floatTips);
            floatTips = null;
        }
        mTipStatus = TIP_STATUS_NORMAL;
    }

    public void showBlueCenterTips(String text) {
        if (!mIsStatusBall || floatBallParams == null || floatBall == null || TextUtils.isEmpty(text) ||
                (floatTips != null && floatTips.getParent() != null)) {
            return;
        }
        floatBall.setCenterTips(text);
    }

    private TextView getFloatTips() {
        floatTips = new TextView(context);
        floatTips.setTextSize(14);
        floatTips.setTextColor(ContextCompat.getColor(context, R.color.color_FFFCF0));
        floatTips.setSingleLine(true);
        return floatTips;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        float startX;
        float startY;
        float tempX;
        float tempY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        tempX = event.getRawX();
                        tempY = event.getRawY();
                        if (mClickListener != null) {
                            mClickListener.setTipStatus(mTipStatus);
                        }
                        removeFloatTips();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() - startX;
                        float y = event.getRawY() - startY;
                        //计算偏移量，刷新视图
                        floatBallParams.x += x;
                        floatBallParams.y += y;
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (endX < mScreenW / 2) {
                            endX = 0;
                            floatBall.setLeftOrRight(true);
                        } else {
                            endX = mScreenW - floatBall.width;
                            floatBall.setLeftOrRight(false);
                        }
                        floatBallParams.x = (int) endX;
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                        //否则继续传递，将事件交给OnClickListener函数处理
                        if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                            SpUtil.putData(KEY_FLOAT_WINDOW_BALL_POINT, String.format("%s#%s", (int) endX, (int) endY));
                            return true;
                        }
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    };


    public class MyOnClickListener implements OnClickListener {

        private int tipStatus = TIP_STATUS_NORMAL;

        public void setTipStatus(int tipStatus) {
            this.tipStatus = tipStatus;
        }

        @Override
        public void onClick(View v) {
            SpUtil.putData(KEY_FLOAT_WINDOW_CENTER_TIP_CLICK, true);
            if (floatBall != null && floatMenu != null) {
                floatBall.setCenterTips(null);
                removeFloatBall();
                showFloatMenu(tipStatus);
                floatMenu.startAnimation();
            }
        }
    }

}