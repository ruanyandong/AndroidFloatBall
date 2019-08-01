package com.example.myapplication.floatball.widget;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.floatball.ViewManager;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.jifen.framework.router.Router;
import com.qukandian.sdk.account.AccountInstance;
import com.qukandian.sdk.config.BaseExtra;
import com.qukandian.sdk.config.BaseSPKey;
import com.qukandian.sdk.config.model.FloatConfigModel;
import com.qukandian.sdk.config.model.PushConfig;
import com.qukandian.sdk.config.model.VideoClickModel;
import com.qukandian.sdk.util.ColdStartCacheManager;
import com.qukandian.sdk.video.model.ReportInfo;
import com.qukandian.util.ActivityTaskManager;
import com.qukandian.util.ContextUtil;
import com.qukandian.util.DensityUtil;
import com.qukandian.util.ListUtils;
import com.qukandian.util.LoadImageUtil;
import com.qukandian.util.SpUtil;
import com.qukandian.video.qkdbase.BaseConstants;
import com.qukandian.video.qkdbase.R;
import com.qukandian.video.qkdbase.config.ContentExtra;
import com.qukandian.video.qkdbase.floatball.ClearMemoryHelper;
import com.qukandian.video.qkdbase.floatball.ViewManager;
import com.qukandian.video.qkdbase.router.PageIdentity;
import com.qukandian.video.qkdbase.util.AppDataUtil;
import com.qukandian.video.qkdbase.widget.AvatarLevelViewFresco;
import com.qukandian.video.qkdbase.widget.SwitchTextView;
import com.qukandian.video.qkdbase.widget.dialog.VideoListCardDialog;

import java.util.Random;

import statistic.report.ParamsManager;
import statistic.report.ReportUtil;


/**
 * Created by weiqi on 2019/2/16
 * 底部菜单栏
 */
public class FloatMenu extends LinearLayout implements View.OnClickListener {

    String[] mClickTips = {"等会再来试试吧", "已经加速，去看看精彩视频", "已经快如闪电啦"};

    private TranslateAnimation mAnimation;

    CleanAnimView mCleanBall;
    SwitchTextView calendarView;
    SimpleDraweeView mVideoImageView;
    TextView mDateView;
    TextView mClickView;
    LinearLayout mContentView;

    private int mCurrentImageIndex = 0;
    private int mCurrentVideoIndex = 0;

    private Handler mHandler;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                finishView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    public FloatMenu(final Context context) {
        super(context);
        View root = View.inflate(context, R.layout.layout_float_ball_menu, null);
        View layout = root.findViewById(R.id.layout);
        mCleanBall = root.findViewById(R.id.v_float_menu_ball);
        calendarView = root.findViewById(R.id.layout_float_menu_calendar);
        mVideoImageView = root.findViewById(R.id.iv_video);
        mDateView = root.findViewById(R.id.layout_float_menu_date);
        mClickView = root.findViewById(R.id.tv_click_tips);
        mContentView = root.findViewById(R.id.layout_float_menu_content);

        mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0);
        mAnimation.setDuration(300);
        mAnimation.setFillAfter(true);
        layout.setAnimation(mAnimation);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finishView();
                return false;
            }
        });

        ViewGroup.LayoutParams viewParams = layout.getLayoutParams();
        int height = (int) ((DensityUtil.getScreenWith(ContextUtil.getContext()) - DensityUtil.dip2px(30)) * 0.52) + DensityUtil.dip2px(15);
        if (height <= 0) {
            height = DensityUtil.dip2px(178 + 15);
        }
        if (viewParams != null) {
            if (viewParams.height != height) {
                viewParams.height = height;
            }
        } else {
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }

        ViewGroup.LayoutParams rootParams = getLayoutParams();
        int screenHeight = DensityUtil.getScreenHeight(ContextUtil.getContext());
        if (screenHeight > 0) {
            if (rootParams != null) {
                if (rootParams.height != screenHeight) {
                    rootParams.height = screenHeight;
                }
            } else {
                setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight));
            }
        }

        root.findViewById(R.id.layout_rubbish_clean).setOnClickListener(this);
        root.findViewById(R.id.layout_mobile_speed).setOnClickListener(this);
        root.findViewById(R.id.layout_mobile_game).setOnClickListener(this);
        root.findViewById(R.id.layout_recommend_video).setOnClickListener(this);
        addView(root);

        mCurrentVideoIndex = SpUtil.getData(BaseSPKey.KEY_FLOAT_WINDOW_VIDEO_INDEX, 0);
    }

    private void finishView() {
        if (getContext() != null) {
            ViewManager manager = ViewManager.getInstance(getContext());
            manager.showFloatBall(null);
            manager.hideFloatMenu();
        }
    }

    public void startAnimation() {
        mAnimation.start();
        FloatConfigModel floatConfigModel = ColdStartCacheManager.getInstance().getFloatConfigModel();
        if (floatConfigModel != null) {
            if (calendarView != null) {
                if (!ListUtils.isListEmpty(floatConfigModel.getCalendars())) {
                    calendarView.startPlay(floatConfigModel.getCalendars());
                    calendarView.setVisibility(View.VISIBLE);
                } else {
                    calendarView.setVisibility(View.GONE);
                }
            }
            if (mDateView != null && !TextUtils.isEmpty(floatConfigModel.getDate())) {
                mDateView.setText(floatConfigModel.getDate());
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mVideoImageView != null) {
            String imageUrl = getImageConfigUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                LoadImageUtil.setCoverViewGif(mVideoImageView, imageUrl, View.NO_ID, new ResizeOptions(DensityUtil.dip2px(70),
                        DensityUtil.dip2px(70)), ScalingUtils.ScaleType.CENTER_CROP, 0);
            }
        }
        if (mCleanBall != null) {
            if (mCleanBall.isCleanOutTime()) {
                mCleanBall.showPrecent(false);
            } else {
                mCleanBall.showCleanOver();
            }
        }
    }

    private String getImageConfigUrl() {
        String url = null;
        FloatConfigModel configModel = ColdStartCacheManager.getInstance().getFloatConfigModel();
        if (configModel != null && !ListUtils.isListEmpty(configModel.getBeautifulIcons())) {
            if (ListUtils.isPositionValidateInList(mCurrentImageIndex, configModel.getBeautifulIcons())) {
                url = configModel.getBeautifulIcons().get(mCurrentImageIndex);
            } else {
                url = configModel.getBeautifulIcons().get(0);
                mCurrentImageIndex = 0;
            }
            mCurrentImageIndex++;
        }
        return url;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mClickView != null && mClickView.getVisibility() != View.GONE) {
            mClickView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_mobile_speed) {
            try {
                if (mCleanBall.isCleanOutTime()) {
                    if (mCleanBall.isFly()) {
                        return;
                    }
                    mCleanBall.showRocketFly();
                    ClearMemoryHelper.startMemoryClean(ActivityTaskManager.getActivityTop(), new ClearMemoryHelper.OnMemoryCleanCallback() {
                        @Override
                        public void onMemoryclean(boolean result, int clearSize) {
                            mCleanBall.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCleanBall.showRocketFlyOut();
                                }
                            }, 2000);
                        }
                    });
                } else {
                    mCleanBall.showCleanOver();
                    if (mClickView != null && mClickView.getVisibility() != View.VISIBLE) {
                        mClickView.setText(mClickTips[new Random().nextInt(mClickTips.length)]);
                        mClickView.setVisibility(View.VISIBLE);
                        if (mHandler == null) {
                            mHandler = new Handler(Looper.getMainLooper());
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mClickView != null && mClickView.getVisibility() != View.GONE)
                                    mClickView.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ReportUtil.onFloatMenuReport(new ReportInfo().setAction(ParamsManager.Cmd244.ACTION_CLEAN));
        } else if (v.getId() == R.id.layout_mobile_game) {
            //通过 server打开本应用 游戏中心
            try {
                FloatConfigModel floatConfigModel = ColdStartCacheManager.getInstance().getFloatConfigModel();
                if (floatConfigModel != null && !TextUtils.isEmpty(floatConfigModel.getGameClick())) {
                    Router.build(PageIdentity.QU_WEB_ACTIVITY)
                            .with(BaseExtra.EXTRA_WEB_URL, floatConfigModel.getGameClick())
                            .go(getContext());
                    ReportUtil.onFloatMenuReport(new ReportInfo().setAction(ParamsManager.Cmd244.ACTION_GAME).setClickId(floatConfigModel.getGameClick()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishView();
        } else if (v.getId() == R.id.layout_recommend_video) {
            //通过 server打开本应用 的推荐视频 小视频、短视频
            try {
                VideoClickModel videoModel = getVideoClickModel();
                if (videoModel != null && !TextUtils.isEmpty(videoModel.getVideoId())) {
                    Router.build(videoModel.isSmallVideo() ? PageIdentity.SMALL_VIDEO_DETAIL : PageIdentity.VIDEO_DETAIL)
                            .with(ContentExtra.EXTRA_NEWS_ID_ONLY, true)
                            .with(ContentExtra.EXTRA_NEWS_FROM, BaseConstants.FROM_FLOAT_WINDOW)
                            .with(ContentExtra.EXTRA_NEWS_VIDEO_ID, videoModel.getVideoId())
                            .go(getContext());
                    ReportUtil.onFloatMenuReport(new ReportInfo().setAction(ParamsManager.Cmd244.ACTION_VIDEO).setClickId(videoModel.getVideoId())
                            .setIsSmall(videoModel.isSmallVideo() ? ParamsManager.Cmd244.IS_SMALL_VIDEO : ParamsManager.Cmd244.IS_VIDEO));
                } else {//无配置 跳主页
                    Router.build(PageIdentity.MAIN).go(getContext());
                    ReportUtil.onFloatMenuReport(new ReportInfo().setAction(ParamsManager.Cmd244.ACTION_VIDEO));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishView();
        }
    }

    private VideoClickModel getVideoClickModel() {
        VideoClickModel model = null;
        FloatConfigModel configModel = ColdStartCacheManager.getInstance().getFloatConfigModel();
        if (configModel != null && configModel.getVideoList() != null && !ListUtils.isListEmpty(configModel.getVideoList())) {
            if (ListUtils.isPositionValidateInList(mCurrentVideoIndex, configModel.getVideoList())) {
                model = configModel.getVideoList().get(mCurrentVideoIndex);
            } else {
                model = configModel.getVideoList().get(0);
                mCurrentVideoIndex = 0;
            }
            mCurrentVideoIndex++;
            SpUtil.putData(BaseSPKey.KEY_FLOAT_WINDOW_VIDEO_INDEX, mCurrentVideoIndex);
        }
        return model;
    }

}