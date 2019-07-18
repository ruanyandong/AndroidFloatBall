package leavesc.hello.floatball;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import leavesc.hello.floatball.service.StartFloatBallService;

/**
 * 作者：leavesC
 * 时间：2019/4/1 21:55
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void startService(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            } else {
                showFloatBall();
            }
        }else{
            showFloatBall();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showFloatBallV2();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RYD","onActivityResult");
        if (requestCode == 1){
            Log.d("RYD","请求码等于"+requestCode);
            Log.d("RYD","结果码等于"+resultCode);
        }
    }

    private void showFloatBall() {
        Intent intent = new Intent(this, StartFloatBallService.class);
        startService(intent);
        finish();
    }

    private void showFloatBallV2() {
        Intent intent = new Intent(this, StartFloatBallService.class);
        startService(intent);
        //finish();
    }


}