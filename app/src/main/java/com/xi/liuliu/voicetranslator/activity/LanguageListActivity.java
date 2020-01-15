package com.xi.liuliu.voicetranslator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.adapter.LanguagePagerAdapter;
import com.xi.liuliu.voicetranslator.adapter.LanguageRecyclerAdapter;
import com.xi.liuliu.voicetranslator.bean.Language;
import com.xi.liuliu.voicetranslator.fragment.LanguageListFragment;
import com.xi.liuliu.voicetranslator.utils.AssetsUtil;
import com.xi.liuliu.voicetranslator.utils.GsonUtil;
import com.xi.liuliu.voicetranslator.utils.IOUtil;
import com.xi.liuliu.voicetranslator.utils.LogUtil;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LanguageListActivity extends AppCompatActivity implements View.OnClickListener, LanguageRecyclerAdapter.OnItemClickListener {
    private static final String TAG = LanguageListActivity.class.getSimpleName();
    private ImageView mCloseBtn;
    private TabLayout mTableLayout;
    private ViewPager mViewPager;
    private List<String> mTitles;
    private List<Fragment> mFragments;
    private List<Language> mLanguageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_language_list);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initData();
        initView();
    }

    private void initData() {
        InputStream inputStream = AssetsUtil.getAssetsInputStreamByPath(getApplicationContext(), "language.json");
        String languageJson = IOUtil.convertStreamToString(inputStream);
        LogUtil.log(TAG, "languageJson:" + languageJson);
        mLanguageList = GsonUtil.getInstance().getObjectList(languageJson, Language.class);
        if (mTitles == null) {
            mTitles = new ArrayList<>(2);
            mTitles.add("源语言");
            mTitles.add("目标语言");
        }
        if (mFragments == null) {
            mFragments = new ArrayList<>(2);
            LanguageListFragment srcFragment = new LanguageListFragment();
            srcFragment.setData(mLanguageList, this);
            LanguageListFragment destFragment = new LanguageListFragment();
            destFragment.setData(mLanguageList, this);
            mFragments.add(srcFragment);
            mFragments.add(destFragment);
        }

    }

    private void initView() {
        mCloseBtn = findViewById(R.id.close_language_selector_activity);
        mCloseBtn.setOnClickListener(this);
        mTableLayout = findViewById(R.id.table_layout_language_selector_activity);
        mViewPager = findViewById(R.id.viewPager_language_selector_activity);
        LanguagePagerAdapter languageSelectorAdapter = new LanguagePagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(languageSelectorAdapter);//给ViewPager设置适配器
        mViewPager.setCurrentItem(getIntent().getIntExtra("view_page_current_item", 0));
        mTableLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTableLayout.setTabsFromPagerAdapter(languageSelectorAdapter);//给Tabs设置适配器
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        if (mViewPager.getCurrentItem() == 0) {
            intent.putExtra(SharedPrefUtil.KEY_SRC_LANGUAGE, mLanguageList.get(position));
        } else if (mViewPager.getCurrentItem() == 1) {
            intent.putExtra(SharedPrefUtil.KEY_DEST_LANGUAGE, mLanguageList.get(position));
        }
        setResult(RESULT_OK, intent);
        LogUtil.log(TAG, "code:" + mLanguageList.get(position).getTranslateCode() + " name:" + mLanguageList.get(position).getTranslateCode());
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_language_selector_activity:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.out_to_bottom);
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
