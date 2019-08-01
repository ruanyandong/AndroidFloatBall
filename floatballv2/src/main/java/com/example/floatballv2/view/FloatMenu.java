package com.example.floatballv2.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.floatballv2.R;
import com.example.floatballv2.anager.ViewManager;


/**
 * Created by ZY on 2016/8/10.
 * 底部菜单栏
 */
public class FloatMenu extends FrameLayout{
    private Context mContext;
    private TranslateAnimation mFloatMenuAnimation;
    private View mFloatMenuRoot;
    private LinearLayout mFloatMenuBody;
    // FloatMenu上半部分控件
    private TextView mTemperatureNumberTv;
    private ImageView mWeatherSymbolImg;
    private TextView mWeatherTextTv;
    private ImageView mIconSetImg;
    // FloatMenu下半部分控件
    // 垃圾清理控件
    private FrameLayout mGarbageBallBg;
    private ImageView mFengShanImg;
    private ImageView mShuaziImg;
    private TextView mGarbageNumberTv;
    private TextView mGarbageCleanTv;


    // 签到
    private TextView mSignInTv;



    private OnClickIconSetListener onClickIconSetListener;

    // 垃圾数量
    private String realGarbage = "0M";
    private String virtualGarbage;
    private int garbageNumber = 0;

    public FloatMenu(final Context context) {
        super(context);
        initView(context);

        initFloatMenuAnim();

        initViewClickEvent();

        addView(mFloatMenuRoot);
    }

    public interface OnClickIconSetListener{
        void clickIconSet();
    }

    private void initView(Context context){
        this.mContext = context;
        mFloatMenuRoot = View.inflate(mContext, R.layout.floatball_menu, null);
        mFloatMenuBody = mFloatMenuRoot.findViewById(R.id.float_menu_layout);
        // FloatMenu上半部分控件
        mTemperatureNumberTv = mFloatMenuRoot.findViewById(R.id.temperature_number);
        mWeatherSymbolImg = mFloatMenuRoot.findViewById(R.id.weather_symbol);
        mWeatherTextTv = mFloatMenuRoot.findViewById(R.id.weather_text);
        mIconSetImg = mFloatMenuRoot.findViewById(R.id.icon_set);
        // 垃圾清理控件
        mGarbageBallBg = mFloatMenuRoot.findViewById(R.id.float_menu_garbage_ball);
        mFengShanImg = mFloatMenuRoot.findViewById(R.id.fengshan);
        mShuaziImg = mFloatMenuRoot.findViewById(R.id.shuazi);
        mGarbageNumberTv = mFloatMenuRoot.findViewById(R.id.garbage_number_text);
        mGarbageCleanTv = mFloatMenuRoot.findViewById(R.id.garbage_lean_text);

        // 签到
        mSignInTv = mFloatMenuRoot.findViewById(R.id.sign_in_text);

    }


    public void setGarbageBallBg(int drawable){
        if (mGarbageBallBg != null){
            mGarbageBallBg.setBackgroundResource(drawable);
        }
    }

    public void setFengShanVisible(int visible){
        if (mFengShanImg != null){
            mFengShanImg.setVisibility(visible);
        }
    }

    public void setShuaziVisible(int visible){
        if (mShuaziImg != null){
            mShuaziImg.setVisibility(visible);
        }
    }

    public void setBarbageCLeanText(String text){
        if (mGarbageCleanTv != null){
            mGarbageCleanTv.setText(text);
        }
    }

    public void setRealGarbage(String garbageNumber){
        if (mGarbageNumberTv != null){
            realGarbage = garbageNumber;
            mGarbageNumberTv.setText(garbageNumber);
        }
    }

    public void setVirtualGarbage(){
        if (mGarbageNumberTv != null){
            mGarbageNumberTv.setText(simulateGarbage());
        }
    }

    public void setVirtualGarbageLittle(){
        if (mGarbageNumberTv != null){
            mGarbageNumberTv.setText(simulateGarbageLittle());
        }
    }

    // 构造假垃圾:垃圾清理提示时，如果没有获取到真实垃圾，则构造1000M以内的假垃圾
    public String simulateGarbage(){
        final double d = Math.random();
        int i = (int)(d*1000);
        if (i == 0){
            i = 666;
        }
        virtualGarbage = i+"M";
        return virtualGarbage;
    }

    // 通过随机点击悬浮球显示菜单时，如果没有获取到真实垃圾，显示100M以内的加垃圾

    public String simulateGarbageLittle(){
        final double d = Math.random();
        int i = (int)(d*100);
        if (i == 0){
            i = 66;
        }
        virtualGarbage = i+"M";
        return virtualGarbage;
    }

    private void initFloatMenuAnim(){
        mFloatMenuAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0);
        mFloatMenuAnimation.setDuration(500);
        mFloatMenuAnimation.setFillAfter(true);
        mFloatMenuBody.setAnimation(mFloatMenuAnimation);
    }

    // 控件点击事件
    private void initViewClickEvent(){
        if (mFloatMenuRoot != null){
            mFloatMenuRoot.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ViewManager manager = ViewManager.getInstance(mContext);
                    manager.showFloatBall();
                    manager.hideFloatMenu();
                    return false;
                }
            });
        }

        if (mIconSetImg != null){
            mIconSetImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickIconSetListener != null){
                        onClickIconSetListener.clickIconSet();
                    }
                }
            });
        }

        mGarbageNumberTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                garbageCleanAnim();
            }
        });

    }


    // 垃圾清理
    public void garbageCleanAnim(){
        String tempGarbage = (String)mGarbageNumberTv.getText();

        if (tempGarbage.contains("M")){
            garbageNumber = Integer.parseInt(tempGarbage.substring(0,tempGarbage.length()-1));
        }else if(tempGarbage.contains("G")){
            garbageNumber = Integer.parseInt(tempGarbage.substring(0,tempGarbage.length()-1))*1024;
        }
        // 风扇动画
        final Animation rotateAnim = AnimationUtils.loadAnimation(mContext,R.anim.floatball_menu_fengshan_rotate);
        mFengShanImg.setAnimation(rotateAnim);
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mGarbageCleanTv.setText("清理中...");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotateAnim.cancel();
                mFengShanImg.clearAnimation();
                mFengShanImg.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rotateAnim.start();

        // 垃圾数值动画
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(garbageNumber,0);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                Log.d("ruanyandong", "onAnimationUpdate: value="+value);
                mGarbageNumberTv.setText(value+"M");
                long time = animation.getCurrentPlayTime();
                Log.d("ruanyandong", "onAnimationUpdate: time="+time);
                // 总时间3000，时间控制背景
                if (950 < time && time < 1050){
                    mGarbageBallBg.setBackgroundResource(R.drawable.floatball_float_menu_garbage_ball_orange_bg);
                }
                if (1950 < time && time < 2050){
                   mGarbageBallBg.setBackgroundResource(R.drawable.floatball_float_menu_grabege_ball_blue_bg);
                }
                if (value == 0){
                    mGarbageNumberTv.setText("");
                    mGarbageCleanTv.setText("清理完成");
                    mShuaziImg.setVisibility(VISIBLE);
                    valueAnimator.cancel();
                    valueAnimator.removeUpdateListener(this);
                }
            }
        });
        valueAnimator.start();
    }



    public void setTemperatureNumber(String temperatureNumber){
        if (mTemperatureNumberTv != null){
            mTemperatureNumberTv.setText(temperatureNumber);
        }
    }

    public void setWeatherSymbolImg(int weatherDrawable){
        if (mWeatherSymbolImg != null){
            mWeatherSymbolImg.setImageResource(weatherDrawable);
        }
    }

    public void setWeatherText(String weatherText){
        if (mWeatherTextTv != null){
            mWeatherTextTv.setText(weatherText);
        }
    }

    public void setOnClickIconSetListener(OnClickIconSetListener listener){
        this.onClickIconSetListener = listener;
    }


    public void startAnimation() {
        if (mFloatMenuAnimation != null){
            mFloatMenuAnimation.start();
        }
    }

}