package com.example.floatballv2.anager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.floatballv2.R;
import com.example.floatballv2.utils.DensityUtil;
import com.example.floatballv2.utils.SharePrerencesUtil;
import com.example.floatballv2.view.FloatBall;
import com.example.floatballv2.view.FloatMenu;

import java.lang.reflect.Field;

/**
 * Created by ZY on 2016/8/10.
 * 管理者，单例模式
 */
public class ViewManager {

    private FloatBall floatBall;

    private FloatMenu floatMenu;

    private WindowManager windowManager;

    private static ViewManager manager;

    private LayoutParams floatBallParams;

    private LayoutParams floatMenuParams;

    private Context context;

    private TextView floatTips;

    private static boolean isBeingDrag = false;
    private static boolean isBeingShowTip = false;

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
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        floatBall = new FloatBall(context);
        floatMenu = new FloatMenu(context);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            float startX;
            float startY;
            float tempX;
            float tempY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        tempX = event.getRawX();
                        tempY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() - startX;
                        float y = event.getRawY() - startY;
                        Log.d("TAG", "onTouch: "+x+" "+y);
                        Log.d("TAG", "=======: "+floatBallParams.x+" "+floatBallParams.y);
                        //计算偏移量，刷新视图
                        floatBallParams.x -= x;
                        floatBallParams.y += y;

                        Log.d("TAG", "---------: "+floatBallParams.x+" "+floatBallParams.y);
                        isBeingDrag = true;
                        removeFloatTips();
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (endX > getScreenWidth() / 2) {
                            endX = 0;
                        } else {
                            endX = getScreenWidth() - floatBall.width;
                        }
                        floatBallParams.x = (int) endX;

                        isBeingDrag = false;
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                        //否则继续传递，将事件交给OnClickListener函数处理
                        if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                            return true;
                        }
                        break;
                }
                return false;
            }
        };
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideFloatBall();
                removeFloatTips();
                showFloatMenu();
                floatMenu.startAnimation();
            }
        };
        floatBall.setOnTouchListener(touchListener);
        floatBall.setOnClickListener(clickListener);
    }

    //显示浮动小球
    public void showFloatBall() {
        if (floatBallParams == null) {
            floatBallParams = new LayoutParams();
            floatBallParams.width = floatBall.width;
            floatBallParams.height = floatBall.height;
            floatBallParams.gravity = Gravity.END;
            floatBallParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatBallParams.format = PixelFormat.RGBA_8888;
            Log.d("TAG", "showFloatBall: 屏幕高度"+getScreenHeight());
            floatBallParams.y = getScreenHeight()/2-DensityUtil.dp2px(context,250);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                floatBallParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                floatBallParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
            }
        }
        Log.d("TAG", "showFloatBall: "+floatBallParams.x+" "+floatBallParams.y);
        windowManager.addView(floatBall, floatBallParams);
    }

    //显示底部菜单
    private void showFloatMenu() {
        if (floatMenuParams == null) {
            floatMenuParams = new LayoutParams();
            floatMenuParams.width = getScreenWidth();
            floatMenuParams.height = getScreenHeight() - getStatusHeight();
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

        // 设置按钮
        floatMenu.setOnClickIconSetListener(new FloatMenu.OnClickIconSetListener() {
            @Override
            public void clickIconSet() {
                // 在这里跳转到设置页面,去关闭悬浮窗
            }
        });

        // 判断是否是提示垃圾清理了
        boolean isTip = SharePrerencesUtil.getData(context,SharePrerencesUtil.KEY_IS_ALREADY_TIP_CLEAN);
        // 获取垃圾，1、先获取应用内垃圾，如果没有，自己虚拟垃圾
        // 获取真垃圾
        //TODO :floatMenu.setRealGarbage();

        // 虚拟提示垃圾
        if (isTip){
            floatMenu.setVirtualGarbage();
        }else {
            floatMenu.setVirtualGarbageLittle();
        }
        SharePrerencesUtil.putData(context,SharePrerencesUtil.KEY_IS_ALREADY_TIP_CLEAN,false);
        //设置垃圾球背景
        floatMenu.setGarbageBallBg(R.drawable.floatball_float_menu_garbage_ball_red_bg);
        //重置刷子和风扇的见性
        floatMenu.setShuaziVisible(View.GONE);
        floatMenu.setFengShanVisible(View.VISIBLE);
        // 重置垃圾清理文字
        floatMenu.setBarbageCLeanText("垃圾清理");
    }

    //显示提示
    public void showFloatTips(String text) {
        if (isBeingDrag || floatBallParams == null || floatBall == null || floatBall.getParent() == null || TextUtils.isEmpty(text) ||
                (floatTips != null && floatTips.getParent() != null)) {
            return;
        }
        try {
            LayoutParams tipsParams = new LayoutParams();
            tipsParams.width = DensityUtil.dp2px(context,120);
            tipsParams.height = DensityUtil.dp2px(context,36);
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
                tipsParams.x = floatBallParams.x;
                // 设置提示文字padding
                floatTips.setPadding(20,0,0,0);
            } else {
                int width = 0;
                if (floatTips.getPaint() != null) {
                    width = (int) floatTips.getPaint().measureText(text, 0, text.length());
                }
                if (width <= 0) {
                    width = DensityUtil.dp2px(context,14 * text.length());
                }
                tipsParams.x = floatBallParams.x - DensityUtil.dp2px(context,86);
                // 设置提示文字padding
                floatTips.setPadding(0,0,20,0);
            }
            tipsParams.y = floatBallParams.y-DensityUtil.dp2px(context,2);

            floatTips.setText(text);
            windowManager.addView(floatTips, tipsParams);
            windowManager.updateViewLayout(floatTips, tipsParams);
            // 避免提示覆盖在悬浮球上
            hideFloatBall();
            showFloatBall();
            isBeingShowTip = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //隐藏提示
    public void removeFloatTips() {
        if (windowManager != null && floatTips != null && floatTips.getParent() != null) {
            windowManager.removeView(floatTips);
            isBeingShowTip = false;
            floatTips = null;
        }
    }

    private TextView getFloatTips() {
        floatTips = new TextView(context);
        floatTips.setTextSize(12);
        floatTips.setGravity(Gravity.CENTER);
        floatTips.setTextColor(Color.parseColor("#ffffffff"));
        floatTips.setSingleLine(true);
        floatTips.setBackgroundResource(R.drawable.floatball_float_tip_bg);
        return floatTips;
    }

    // 隐藏悬浮球
    public void hideFloatBall(){
        if (floatBall != null){
            windowManager.removeView(floatBall);
        }
    }


    //隐藏底部菜单
    public void hideFloatMenu() {
        if (floatMenu != null) {
            windowManager.removeView(floatMenu);
        }
    }

    //获取屏幕宽度
    private int getScreenWidth() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //获取屏幕高度
    private int getScreenHeight() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    //获取状态栏高度
    private int getStatusHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }
    }

}