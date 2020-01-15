package com.xi.liuliu.voicetranslator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;
import com.xi.liuliu.voicetranslator.utils.TokenUtil;
import com.xi.liuliu.voicetranslator.view.LineTextView;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private LineTextView mAppNameLineTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenUtil.checkToken(getApplicationContext());
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        dismiss(2000);

    }

    private void initView() {
        mAppNameLineTv = findViewById(R.id.app_name_tv_splash_activity);
        mAppNameLineTv.setTextAndSizeAndColor(getString(R.string.app_name), 17, R.color.grey17);

    }

    private void dismiss(long delayTime) {
        mAppNameLineTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (SharedPrefUtil.getBoolean(getApplicationContext(), SharedPrefUtil.KEY_LANGUAGE_INIT)) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LanguageInitActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, delayTime);
    }

    //禁止用返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
