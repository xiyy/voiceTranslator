package com.xi.liuliu.voicetranslator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.bean.Language;
import com.xi.liuliu.voicetranslator.utils.GsonUtil;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

public class LanguageInitActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_LANGUAGE_INIT_ACTIVITY = 1000;
    private Language mSrcLanguage;
    private Language mDestLanguage;
    private TextView mSrcLanguageBtn;
    private TextView mDestLanguageBtn;
    private TextView mFinishBtn;
    private TextView mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_language_init);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        mSrcLanguage = new Language();
        mSrcLanguage.setChineseName("中文");
        mSrcLanguage.setLocalName("中文");
        mSrcLanguage.setAsrCode("zh-cmn-Hans-CN");
        mSrcLanguage.setTranslateCode("zh-cmn-Hans-CN");
        mDestLanguage = new Language();
        mDestLanguage.setChineseName("英文");
        mDestLanguage.setLocalName("English");
        mDestLanguage.setAsrCode("en-US");
        mDestLanguage.setTranslateCode("en");

    }

    private void initView() {
        mSrcLanguageBtn = findViewById(R.id.text_view_src_language_language_init_activity);
        mDestLanguageBtn = findViewById(R.id.text_view_dest_language_language_init_activity);
        mFinishBtn = findViewById(R.id.text_view_finish_language_init_activity);
        mCancelBtn = findViewById(R.id.text_view_cancel_language_init_activity);
        mSrcLanguageBtn.setOnClickListener(this);
        mDestLanguageBtn.setOnClickListener(this);
        mFinishBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_src_language_language_init_activity:
                Intent srcIntent = new Intent(this, LanguageListActivity.class);
                srcIntent.putExtra("view_page_current_item", 0);
                startActivityForResult(srcIntent, REQUEST_CODE_LANGUAGE_INIT_ACTIVITY);
                overridePendingTransition(R.anim.in_from_bottom, 0);
                break;

            case R.id.text_view_dest_language_language_init_activity:
                Intent destIntent = new Intent(this, LanguageListActivity.class);
                destIntent.putExtra("view_page_current_item", 1);
                startActivityForResult(destIntent, REQUEST_CODE_LANGUAGE_INIT_ACTIVITY);
                overridePendingTransition(R.anim.in_from_bottom, 0);
                break;

            case R.id.text_view_finish_language_init_activity:
            case R.id.text_view_cancel_language_init_activity:
                String srcLanguage = GsonUtil.getInstance().objectToStr(mSrcLanguage);
                String destLanguage = GsonUtil.getInstance().objectToStr(mDestLanguage);
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_SRC_LANGUAGE, srcLanguage);
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_DEST_LANGUAGE, destLanguage);
                SharedPrefUtil.putBoolean(getApplicationContext(), SharedPrefUtil.KEY_LANGUAGE_INIT, true);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LANGUAGE_INIT_ACTIVITY && resultCode == RESULT_OK) {
            Language srcLanguage = data.getParcelableExtra(SharedPrefUtil.KEY_SRC_LANGUAGE);
            Language destLanguage = data.getParcelableExtra(SharedPrefUtil.KEY_DEST_LANGUAGE);
            if (srcLanguage != null) {
                mSrcLanguage = srcLanguage;
                mSrcLanguageBtn.setText(mSrcLanguage.getChineseName());
            }
            if (destLanguage != null) {
                mDestLanguage = destLanguage;
                mDestLanguageBtn.setText(mDestLanguage.getChineseName());
            }

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.zoomout);
    }
}
