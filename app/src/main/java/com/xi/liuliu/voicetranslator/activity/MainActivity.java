package com.xi.liuliu.voicetranslator.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.adapter.TranslateFragmentPagerAdapter;
import com.xi.liuliu.voicetranslator.bean.Language;
import com.xi.liuliu.voicetranslator.fragment.TextTranslateFragment;
import com.xi.liuliu.voicetranslator.fragment.VoiceTranslateFragment;
import com.xi.liuliu.voicetranslator.utils.GsonUtil;
import com.xi.liuliu.voicetranslator.utils.LogUtil;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSION_CODE = 1;
    public static final int REQUEST_CODE_LANGUAGE_lIST_ACTIVITY = 1000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mSrcLanguageLl;
    private LinearLayout mDestLanguageLl;
    private TextView mSrcTextView;
    private TextView mDestTextView;
    private ImageView mLanguageExchangeBtn;
    private TabLayout mTableLayout;
    private ViewPager mViewPager;
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;
    private Language mSrcLanguage;
    private Language mDestLanguage;
    private VoiceTranslateFragment mVoiceTranslateFragment;
    private TextTranslateFragment mTextTranslateFragment;
    private static final String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        checkPermission();
    }

    private void initData() {
        if (mTitleList == null) {
            mTitleList = new ArrayList<>(3);
            mTitleList.add("语音翻译");
            mTitleList.add("文本翻译");
        }
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<>(3);
            mVoiceTranslateFragment = new VoiceTranslateFragment();
            mTextTranslateFragment = new TextTranslateFragment();
            mFragmentList.add(mVoiceTranslateFragment);
            mFragmentList.add(mTextTranslateFragment);
        }
    }

    private void initView() {
        //实现5.0以上状态栏浅蓝色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.blue3));
        }
        mToolbar = findViewById(R.id.tool_bar_main_activity);
        setSupportActionBar(mToolbar);
        mDrawerLayout = findViewById(R.id.parent_dl_main_activity);
        //DrawerLayout监听器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mSrcLanguageLl = findViewById(R.id.src_ll_main_activity);
        mSrcLanguageLl.setOnClickListener(this);
        mDestLanguageLl = findViewById(R.id.dest_ll_main_activity);
        mDestLanguageLl.setOnClickListener(this);
        mSrcTextView = findViewById(R.id.src_tv_language_main_activity);
        mDestTextView = findViewById(R.id.dest_tv_main_activity);
        String srcLanguage = SharedPrefUtil.getString(getApplicationContext(), SharedPrefUtil.KEY_SRC_LANGUAGE);
        String destLanguage = SharedPrefUtil.getString(getApplicationContext(), SharedPrefUtil.KEY_DEST_LANGUAGE);
        if (!TextUtils.isEmpty(srcLanguage)) {
            mSrcLanguage = GsonUtil.getInstance().strToObject(srcLanguage, Language.class);
        }
        if (mSrcLanguage != null) {
            mSrcTextView.setText(mSrcLanguage.getChineseName());
        }
        if (!TextUtils.isEmpty(destLanguage)) {
            mDestLanguage = GsonUtil.getInstance().strToObject(destLanguage, Language.class);
        }
        if (mDestLanguage != null) {
            mDestTextView.setText(mDestLanguage.getChineseName());
        }
        mLanguageExchangeBtn = findViewById(R.id.exchange_iv_main_activity);
        mLanguageExchangeBtn.setOnClickListener(this);
        mTableLayout = findViewById(R.id.tabLayout_main_activity);
        mViewPager = findViewById(R.id.viewPager_main_activity);
        TranslateFragmentPagerAdapter translateAdapter = new TranslateFragmentPagerAdapter(getSupportFragmentManager(), mTitleList, mFragmentList);
        mViewPager.setAdapter(translateAdapter);//给ViewPager设置适配器
        mTableLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTableLayout.setTabsFromPagerAdapter(translateAdapter);//给Tabs设置适配器

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.src_ll_main_activity:
                Intent srcIntent = new Intent(this, LanguageListActivity.class);
                srcIntent.putExtra("view_page_current_item", 0);
                startActivityForResult(srcIntent, REQUEST_CODE_LANGUAGE_lIST_ACTIVITY);
                overridePendingTransition(R.anim.in_from_bottom, 0);
                break;
            case R.id.dest_ll_main_activity:
                Intent destIntent = new Intent(this, LanguageListActivity.class);
                destIntent.putExtra("view_page_current_item", 1);
                startActivityForResult(destIntent, REQUEST_CODE_LANGUAGE_lIST_ACTIVITY);
                overridePendingTransition(R.anim.in_from_bottom, 0);
                break;

            case R.id.exchange_iv_main_activity:
                Language temp = mDestLanguage;
                mDestLanguage = mSrcLanguage;
                mSrcLanguage = temp;
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_SRC_LANGUAGE, GsonUtil.getInstance().objectToStr(mSrcLanguage));
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_DEST_LANGUAGE, GsonUtil.getInstance().objectToStr(mDestLanguage));
                mSrcTextView.setText(mSrcLanguage.getChineseName());
                mDestTextView.setText(mDestLanguage.getChineseName());
                //设置VoiceTranslateFragment语言按钮文案
                if (mSrcLanguage != null && mDestLanguage != null) {
                    mVoiceTranslateFragment.setSrcAndDestBtnText(mSrcLanguage.getLocalName(), mDestLanguage.getLocalName());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LANGUAGE_lIST_ACTIVITY && resultCode == RESULT_OK) {
            Language srcLanguage = data.getParcelableExtra(SharedPrefUtil.KEY_SRC_LANGUAGE);
            Language destLanguage = data.getParcelableExtra(SharedPrefUtil.KEY_DEST_LANGUAGE);
            if (srcLanguage != null) {
                mSrcLanguage = srcLanguage;
                mSrcTextView.setText(mSrcLanguage.getChineseName());
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_SRC_LANGUAGE, GsonUtil.getInstance().objectToStr(mSrcLanguage));
            }
            if (destLanguage != null) {
                mDestLanguage = destLanguage;
                mDestTextView.setText(mDestLanguage.getChineseName());
                SharedPrefUtil.putString(getApplicationContext(), SharedPrefUtil.KEY_DEST_LANGUAGE, GsonUtil.getInstance().objectToStr(mDestLanguage));
            }
            //设置VoiceTranslateFragment语言按钮文案
            if (mSrcLanguage != null && mDestLanguage != null) {
                mVoiceTranslateFragment.setSrcAndDestBtnText(mSrcLanguage.getLocalName(), mDestLanguage.getLocalName());
            }

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean needApply = false;
            for (int i = 0; i < PERMISSIONS.length; i++) {
                int checkPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[i]);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    needApply = true;
                }
            }
            if (needApply) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.log(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.log(TAG, PERMISSIONS[i] + "已授权");
                } else {
                    LogUtil.log(TAG, PERMISSIONS[i] + "未授权");

                    allPermissionsGranted = false;
                }
            }
            if (allPermissionsGranted) {
                LogUtil.log(TAG, "全部授权了！");
            } else {
                Toast.makeText(getApplicationContext(), "尚未获得所需权限，请在设置中打开权限，否则app无法正常使用！", Toast.LENGTH_SHORT).show();
            }
        }
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
