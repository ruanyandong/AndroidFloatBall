package com.example.myapplication.floatball.widget;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.qukandian.util.ContextUtil;
import com.qukandian.util.DensityUtil;
import com.qukandian.video.qkdbase.R;
import com.qukandian.video.qkdbase.floatball.utils.AppUtil;
import com.qukandian.video.qkdbase.floatball.widget.textcounter.CounterView;
import com.qukandian.video.qkdbase.floatball.widget.textcounter.formatters.IntegerFormatter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 抖动动画 view
 * Created by weiqi on 2018/12/3
 */
public class CleanAnimView extends FrameLayout {

    ObjectAnimator mFlyAnim;
    AnimatorSet mAnimatorSet;
    AnimationDrawable mRocketBgDrawable;

    ImageView mRocketView;
    ImageView mRocketBgView;
    CounterView mTextCounterView;
    Roll3DView mCleanStatusView;

    View mLayoutCounter;

    private boolean mIsFly;
    private long mLastCleanTimeTamp = 0;

    public CleanAnimView(@NonNull Context context) {
        this(context, null);
    }

    public CleanAnimView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanAnimView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.view_clean_circle, this, true);
        mRocketView = findViewById(R.id.iv_small_rocket);
        mRocketBgView = findViewById(R.id.iv_small_rocket_bg);
        mTextCounterView = findViewById(R.id.tv_counter);
        mLayoutCounter = findViewById(R.id.layout_counter);
        mCleanStatusView = findViewById(R.id.iv_clean_3d_view);

        mTextCounterView.setAutoFormat(false);
        mTextCounterView.setFormatter(new IntegerFormatter());
        mTextCounterView.setAutoStart(false);
        mTextCounterView.setStartValue(0f);
        mTextCounterView.setTimeInterval(50);
        mTextCounterView.setSuffix("M");

        mRocketBgDrawable = (AnimationDrawable) mRocketBgView.getDrawable();

        mCleanStatusView.addImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_clean_status_over));
        mCleanStatusView.addImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_clean_status_best));
        mCleanStatusView.setRollMode(Roll3DView.RollMode.RollInTurn);
        mCleanStatusView.setPartNumber(8);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelFlyAnim();
        cancelFlyOutAnim();
        cancelFlyBgAnim();
        mIsFly = false;
    }

    /**
     * 1分钟内不再重新清理
     *
     * @return
     */
    public boolean isCleanOutTime() {
        return mLastCleanTimeTamp == 0 || ((System.currentTimeMillis() - mLastCleanTimeTamp) > 60000);
    }

    public boolean isFly() {
        return mIsFly;
    }

    public void showPrecent(boolean isAfterClear) {
        mIsFly = false;
        cancelFlyAnim();
        cancelFlyOutAnim();
        cancelFlyBgAnim();
        if (mLayoutCounter.getVisibility() != View.VISIBLE) {
            mLayoutCounter.setVisibility(View.VISIBLE);
        }
        if (mCleanStatusView.getVisibility() != View.GONE) {
            mCleanStatusView.setVisibility(View.GONE);
        }
        if (mRocketBgView.getVisibility() != View.GONE) {
            mRocketBgView.setVisibility(View.GONE);
        }
        if (mRocketView.getVisibility() != View.GONE) {
            mRocketView.setVisibility(View.GONE);
        }
        int precent = getUsedPrecent(isAfterClear);
        int increment = precent / 20;
        if (increment < 1) {
            increment = 1;
        }
        mTextCounterView.setEndValue(precent);
        mTextCounterView.setIncrement(increment); // the amount the number increments at each time interval
        mTextCounterView.start(300);
//        startPrecentAnim(precent);
    }

    /**
     * 清理内存 为了效果手动修正
     *
     * @param isAfterClear
     * @return
     */
    private int getUsedPrecent(boolean isAfterClear) {
        int usedSize = AppUtil.getUsedSize(ContextUtil.getContext());
        if (!isAfterClear) {
            //最大提示用户需要清理5xxM
            if (usedSize >= 500) {
                return 500 + usedSize / 300;
            } else {
                return (usedSize >= 200) ? usedSize : (180 + usedSize / 10);
            }
        }
        return (int) (usedSize * 0.68);
    }

    public void showRocketFly() {
        mIsFly = true;
        mLastCleanTimeTamp = System.currentTimeMillis();
        cancelFlyAnim();
        cancelFlyOutAnim();
        if (mLayoutCounter.getVisibility() != View.GONE) {
            mLayoutCounter.setVisibility(View.GONE);
        }
        if (mRocketView.getVisibility() != View.VISIBLE) {
            mRocketView.setVisibility(View.VISIBLE);
        }
        if (mRocketBgView.getVisibility() != View.VISIBLE) {
            mRocketBgView.setVisibility(View.VISIBLE);
        }
        startFlyAnim();
        startFlyBgAnim();
    }

    private void startFlyAnim() {
        if (mRocketView.getVisibility() != View.VISIBLE) {
            return;
        }
        if (mFlyAnim == null) {
            int tranX = DensityUtil.dip2px(2);
            mFlyAnim = ObjectAnimator.ofFloat(mRocketView, "translationX", 0, -tranX, 0, tranX, 0);
            mFlyAnim.setRepeatCount(ValueAnimator.INFINITE);
            mFlyAnim.setDuration(300);
        }

        if (!mFlyAnim.isStarted()) {
            mFlyAnim.start();
        }
    }

    private void cancelFlyAnim() {
        if (mFlyAnim != null && mFlyAnim.isRunning()) {
            mFlyAnim.cancel();
        }
    }

    public void showRocketFlyOut() {
        cancelFlyAnim();
        cancelFlyOutAnim();
        if (mLayoutCounter.getVisibility() != View.GONE) {
            mLayoutCounter.setVisibility(View.GONE);
        }
        if (mRocketView.getVisibility() != View.VISIBLE) {
            mRocketView.setVisibility(View.VISIBLE);
        }
        startFlyOutAnim();
    }

    private void startFlyOutAnim() {
        if (mRocketView.getVisibility() != View.VISIBLE) {
            return;
        }
        if (mAnimatorSet == null) {
            ObjectAnimator transY = ObjectAnimator.ofFloat(mRocketView, "translationY", 0, -mRocketView.getHeight());
//            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mRocketView, "scaleX", 1.0f, 1.0f, 1.0f, 1.0f, 0.2f);
//            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mRocketView, "scaleY", 1.0f, 1.0f, 1.0f, 1.0f, 0.2f);
//            ObjectAnimator alpha = ObjectAnimator.ofFloat(mRocketView, "alpha", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0f);
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.setDuration(600);
            mAnimatorSet.play(transY);//.with(scaleX).with(scaleY).with(alpha);
            mAnimatorSet.setInterpolator(new AccelerateInterpolator());
            mAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mRocketView.getVisibility() != View.VISIBLE) {
                        mRocketView.setVisibility(View.VISIBLE);
                    }
                    mRocketView.setTranslationX(0);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mRocketView.getVisibility() != View.GONE) {
                        mRocketView.setVisibility(View.GONE);
                    }
                    mRocketView.setTranslationY(0);
                    mRocketView.setAlpha(1.0f);
                    mRocketView.setScaleX(1);
                    mRocketView.setScaleY(1);
                    mRocketView.post(new Runnable() {
                        @Override
                        public void run() {
                            showCleanOver();
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (mRocketView.getVisibility() != View.GONE) {
                        mRocketView.setVisibility(View.GONE);
                    }
                    mRocketView.setTranslationY(0);
                    mRocketView.setAlpha(1.0f);
                    mRocketView.setScaleX(1);
                    mRocketView.setScaleY(1);
                    mRocketView.post(new Runnable() {
                        @Override
                        public void run() {
                            showCleanOver();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        if (!mAnimatorSet.isStarted()) {
            mAnimatorSet.start();
        }
    }

    private void cancelFlyOutAnim() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    private void startFlyBgAnim() {
        if (!mRocketBgDrawable.isRunning()) {
            mRocketBgDrawable.start();
        }
    }

    private void cancelFlyBgAnim() {
        if (mRocketBgDrawable != null && mRocketBgDrawable.isRunning()) {
            mRocketBgDrawable.stop();
        }
    }

    public void showCleanOver() {
        if (mRocketBgView.getVisibility() != View.VISIBLE) {
            mRocketBgView.setVisibility(View.VISIBLE);
        }
        if (mCleanStatusView.getVisibility() != View.VISIBLE) {
            mCleanStatusView.setVisibility(View.VISIBLE);
        }
        cancelFlyBgAnim();
        startCleanOverAnim();
    }

    private void startCleanOverAnim() {
        if (mCleanStatusView.getVisibility() != View.VISIBLE) {
            return;
        }

        mCleanStatusView.setRollDirection(0);
        mCleanStatusView.toNextFromStart();
    }

    protected void cancelCleanOverAnim() {

    }

}
