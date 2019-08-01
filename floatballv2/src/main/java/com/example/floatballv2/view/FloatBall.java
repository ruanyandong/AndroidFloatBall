package com.example.floatballv2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.floatballv2.R;
import com.example.floatballv2.utils.DensityUtil;

public class FloatBall extends FrameLayout {
    private View root;
    public int width;
    public int height;

    private FrameLayout mFloatballLayout;
    private ImageView mFengshanImg;
    private ImageView mShuaziImg;

    public FloatBall(Context context) {
        super(context);
        init(context);
    }

    public FloatBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        width = DensityUtil.dp2px(context,32);
        height = DensityUtil.dp2px(context,32);
        root = View.inflate(context, R.layout.floatball_layout, null);
        mFloatballLayout = root.findViewById(R.id.floatball_layout);
        mFengshanImg = root.findViewById(R.id.fengshan);
        mShuaziImg = root.findViewById(R.id.shuazi);
        addView(root);
    }

    // 改变悬浮球的颜色，红色、橘色、蓝色
    public void setFloatBallBackground(int drawbleId){
        if (mFloatballLayout != null){
            mFloatballLayout.setBackgroundResource(drawbleId);
        }
    }

}
