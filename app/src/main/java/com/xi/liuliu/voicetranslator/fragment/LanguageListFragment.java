package com.xi.liuliu.voicetranslator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.adapter.LanguageRecyclerAdapter;
import com.xi.liuliu.voicetranslator.bean.Language;

import java.util.List;

/**
 * Date:2019/7/30
 * Author:zhangxiaobei
 * Describe:
 */
public class LanguageListFragment extends Fragment {
    private static final String TAG = LanguageListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private List<Language> mLanguageList;
    private LanguageRecyclerAdapter mLanguageSelAdapter;
    private LanguageRecyclerAdapter.OnItemClickListener mOnItemClickListener;

    public void setData(List<Language> list, LanguageRecyclerAdapter.OnItemClickListener onItemClickListener) {
        mLanguageList = list;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_language_selector_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mLanguageSelAdapter = new LanguageRecyclerAdapter(mLanguageList);
        mLanguageSelAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mLanguageSelAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
