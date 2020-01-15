package com.xi.liuliu.voicetranslator.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Date:2019/7/30
 * Author:zhangxiaobei
 * Describe:
 */
public class TranslateFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> mTitles;
    private List<Fragment> mFragments;

    public TranslateFragmentPagerAdapter(FragmentManager fragmentManager, List<String> titles, List<Fragment> fragments) {
        super(fragmentManager);
        mTitles = titles;
        mFragments = fragments;
    }


    @Override
    public int getCount() {
        if (mTitles != null) {
            return mTitles.size();
        }
        return 0;
    }


    @Override
    public Fragment getItem(int i) {
        if (mFragments != null) {
            return mFragments.get(i);
        }
        return null;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }

}
