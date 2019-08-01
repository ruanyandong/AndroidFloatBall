package com.example.myapplication.permission.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qukandian.util.ContextUtil;
import com.qukandian.util.DensityUtil;
import com.qukandian.video.qkdbase.R;
import com.qukandian.video.qkdbase.util.StatusBarUtil;


public class FixToastTransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_anim, 0);
        StatusBarUtil.setTranslucentForSwipeBack(this);
        setContentView(R.layout.activity_fix_toast_transparent);
        findViewById(R.id.layout_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FixToastTransparentActivity.this.finish();
            }
        });
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FixToastTransparentActivity.this.finish();
            }
        });

        String title = null;
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
        }
        ((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(String.format(ContextUtil.getContext().getString(R
                .string.tip_find_app), ContextUtil.getContext().getString(R.string.app_name), TextUtils.isEmpty(title) ? "" : title)));

        SimpleDraweeView gif = (SimpleDraweeView) this.findViewById(R.id.iv_gif);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(Uri.parse("res://" + getPackageName() + "/" + R.drawable.icon_permission_need))//设置uri
                .build();
        gif.setController(mDraweeController);

        ViewGroup.LayoutParams imgParams = gif.getLayoutParams();
        int height = (DensityUtil.getScreenWith(ContextUtil.getContext()) - DensityUtil.dip2px(136)) * 5 / 12;
        if (height <= 0) {
            height = DensityUtil.dip2px(98);
        }
        if (imgParams != null) {
            if (imgParams.height != height) {
                imgParams.height = height;
            }
        } else {
            gif.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.no_anim);
    }

}
