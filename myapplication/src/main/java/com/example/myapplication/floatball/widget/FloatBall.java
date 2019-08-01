package com.example.myapplication.floatball.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created by weiqi on 2019/2/16
 * 悬浮球
 */
public class FloatBall extends View implements Runnable {

    public int viewWidth = 0;
    public int width = 0;
    public int extra = 0;

    private int radius, dp2;

    //默认显示的文本
    private String text = "50%";

    //是否在拖动
    private boolean isDrag;

    private Paint tipsBitmapPaint;

    private Paint textPaint;

    private Paint tipsPaint;
    private Paint progressPaint;
    private Paint redDotPaint;

    private Bitmap bitmapBg;
    private Bitmap bitmapBrush;

    // 存放第一条水波Y值
    private float[] firstWaterLine;
    // 第二条
    private float[] secondWaterLine;
    // 画水球的画笔
    private Paint waterPaint;
    // 影响三角函数的初相
    private float move;
    // 剪切圆的半径
    private int clipRadius;
    // 水球的增长值
    private int up = 0;

    // 显示提示，则隐藏刷子
    private boolean showCenterTips;
    private String tips;
    private int tipsHeight;

    private int progressAngle;
    private int progressStrokeWidth;
    private ValueAnimator progressAnimator;
    private boolean isLeft = true;

    public FloatBall(Context context) {
        super(context);
        init();
    }

    public FloatBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        dp2 = DensityUtil.dip2px(2);
        viewWidth = DensityUtil.dip2px(38);
        width = DensityUtil.dip2px(33);
        radius = width >> 1;

        textPaint = new Paint();
        textPaint.setTextSize(DensityUtil.dip2px(7));
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        tipsBitmapPaint = new Paint();
        tipsBitmapPaint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_0096FF),
                PorterDuff.Mode.SRC_IN));

        tipsPaint = new Paint();
        tipsPaint.setTextSize(DensityUtil.dip2px(12));
        tipsPaint.setColor(Color.WHITE);
        tipsPaint.setFakeBoldText(true);
        tipsHeight = DensityUtil.dip2px(3);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.tran20_black));//进度色
        progressStrokeWidth = DensityUtil.dip2px(1.5f);
        progressPaint.setStrokeWidth(progressStrokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        redDotPaint = new Paint();
        redDotPaint.setColor(getResources().getColor(R.color.app_theme));
        redDotPaint.setStyle(Paint.Style.FILL);
        redDotPaint.setAntiAlias(true);
        redDotPaint.setDither(true);

        waterPaint = new Paint();
        waterPaint.setAntiAlias(true);
        waterPaint.setColor(getResources().getColor(R.color.app_theme_35));

        clipRadius = width / 2;
        up = width * 11 / 15;
        firstWaterLine = new float[width];
        secondWaterLine = new float[width];

        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.icon_shortcut_proc_float_bg);
        bitmapBg = Bitmap.createScaledBitmap(bg, width, width, true);
        Bitmap brush = BitmapFactory.decodeResource(getResources(), R.drawable.icon_shortcut_proc_float_brush);
        bitmapBrush = Bitmap.createScaledBitmap(brush, width * 54 / 102, width * 48 / 102, true);

        //低配手机没有波纹动画
        if (!DevicePerUtil.isLow(getContext())) {
            moveWaterLine();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewWidth, viewWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawCircle(radius, radius, radius - dp2, ballPaint);
        if (!isDrag) {

            if (showCenterTips) {
                canvas.drawBitmap(bitmapBg, dp2, dp2, tipsBitmapPaint);//背景圆,改成蓝色
                waterPaint.setColor(getResources().getColor(R.color.color_0096FF));
                drawWaterView(canvas);//波纹
                //文字
                float textWidth = tipsPaint.measureText(tips, 0, tips.length());
                canvas.drawText(tips, width / 2 - textWidth / 2 + dp2, width / 2 + tipsHeight + dp2, tipsPaint);
                //loading
                RectF rectF = new RectF(dp2 - progressStrokeWidth / 2, dp2 - progressStrokeWidth / 2,
                        width + dp2 + progressStrokeWidth / 2, width + dp2 + progressStrokeWidth / 2);
                canvas.drawArc(rectF, progressAngle, 350, false, progressPaint);
                //红点
                canvas.drawCircle(isLeft ? dp2 * 2 : viewWidth - dp2 * 2, dp2 * 2,
                        dp2 * 3 / 2, redDotPaint);
            } else {//刷子

                canvas.drawBitmap(bitmapBg, dp2, dp2, null);//背景圆
                waterPaint.setColor(getResources().getColor(R.color.app_theme_35));
                drawWaterView(canvas);//波纹

//                canvas.drawBitmap(bitmapBrush, (width - width * 54 / 102) / 2, (width - width * 48 / 102) / 2, null);
                canvas.drawBitmap(bitmapBrush, (width - width * 54 / 102) / 2 + dp2, (width - width * 48 / 102) / 2 + dp2, null);
            }
        } else {
            //正在被拖动时则显示指定图片
            float textWidth = textPaint.measureText(text);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
            canvas.drawText(text, radius - textWidth / 2, radius + dy, textPaint);
        }
    }

    /**
     * 画水球的功能
     *
     * @param canvas
     */
    private void drawWaterView(Canvas canvas) {
        // y = A*sin(wx+b)+h ，这个公式里：w影响周期，A影响振幅，h影响y位置，b为初相；
        // 将周期定为view总宽度
        float mCycleFactorW = (float) (2 * Math.PI / width);

        // 得到第一条波的y值
        for (int i = 0; i < width; i++) {
            firstWaterLine[i] = (float) (10 * Math.sin(mCycleFactorW * i + move) - up);
        }

        canvas.save();

        // 裁剪成圆形区域
        Path path = new Path();
        path.reset();
        canvas.clipPath(path);

        path.addCircle(width / 2 + dp2, width / 2 + dp2, clipRadius, Path.Direction.CCW);
        canvas.clipPath(path, android.graphics.Region.Op.REPLACE);
        // 将坐标系移到底部
        canvas.translate(dp2, width / 2 + clipRadius + dp2);

        for (int i = 0; i < width; i++) {
            canvas.drawLine(i, firstWaterLine[i], i, width, waterPaint);
        }
        for (int i = 0; i < width; i++) {
            canvas.drawLine(i, secondWaterLine[i], i, width, waterPaint);
        }
        canvas.restore();
    }

    private void moveWaterLine() {
        postDelayed(this, 500);
    }

    @Override
    public void run() {
        move += 0.2;//差值越小 速度越慢
        if (move == 100) {
            move = 0;
        }
        postInvalidate();
        postDelayed(this, 150);
    }

    public void setCenterTips(String tips) {
        showCenterTips = !TextUtils.isEmpty(tips);
        this.tips = tips;
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        if (showCenterTips) {
            //loading
            progressAnimator = ValueAnimator.ofInt(0, 359);
            progressAnimator.setDuration(2 * 1000);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            progressAnimator.addUpdateListener(animation -> {
//                progressAngle = (int) animation.getAnimatedValue();
//                invalidate();
//            });
            progressAnimator.start();
        }
    }

    public void setLeftOrRight(boolean left) {
        isLeft = left;
    }

    //设置当前移动状态
    public void setDragState(boolean isDrag) {
        this.isDrag = isDrag;
        invalidate();
    }


}
