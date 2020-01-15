package com.xi.liuliu.voicetranslator.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xi.liuliu.voicetranslator.R;

/**
 * Date:2019/7/30
 * Author:zhangxiaobei
 * Describe:
 */
public class TextTranslateFragment extends Fragment implements View.OnClickListener {
    private TextView mSrcPlayTv;
    private TextView mDestPlayTv;
    private EditText mSrcInputEt;
    private TextView mClearTv;
    private TextView mDestOutputTv;
    private TextView mDestFinishTv;
    private TextView mUploadDocumentTv;
    private TextView mMoreHistoryTv;
    private RecyclerView mHistoryListRc;
    private LinearLayout mDestPartLl;
    private View mDivideLineView;

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
        View view = inflater.inflate(R.layout.fragment_text_translate, container, false);
        mSrcPlayTv = view.findViewById(R.id.play_tv_src_text_translate_fragment);
        mSrcPlayTv.setOnClickListener(this);
        mSrcInputEt = view.findViewById(R.id.input_et_src_text_translate_fragment);
        mSrcInputEt.setOnClickListener(this);
        mClearTv = view.findViewById(R.id.close_src_tv_text_translate_fragment);
        mClearTv.setOnClickListener(this);
        mDestPartLl = view.findViewById(R.id.dest_ll_text_translate_fragment);
        mDestPlayTv = view.findViewById(R.id.play_tv_dest_text_translate_fragment);
        mDestPlayTv.setOnClickListener(this);
        mDestOutputTv = view.findViewById(R.id.output_tv_dest_text_translate_fragment);
        mDestFinishTv = view.findViewById(R.id.finish_dest_tv_text_translate_fragment);
        mDestFinishTv.setOnClickListener(this);
        mDivideLineView = view.findViewById(R.id.divine_src_and_dest_text_translate_fragment);
        mMoreHistoryTv = view.findViewById(R.id.more_history_tv_text_translate_fragment);
        mMoreHistoryTv.setOnClickListener(this);
        mHistoryListRc = view.findViewById(R.id.history_rc_text_translate_fragment);
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

    /**
     * https://blog.csdn.net/androidsj/article/details/82592464
     *
     * @return
     */
    private boolean isSoftMethodShowing() {
        // 获取当前屏幕内容的高度
        int screenHeight = getActivity().getWindow().getDecorView().getHeight();
        // 获取View可见区域的bottom
        Rect rect = new Rect();
        // DecorView即为activity的顶级view
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        // 考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        // 选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_et_src_text_translate_fragment:

                break;

            case R.id.play_tv_dest_text_translate_fragment:

                break;
            case R.id.close_src_tv_text_translate_fragment:

                break;

            case R.id.finish_dest_tv_text_translate_fragment:

                break;

            case R.id.more_history_tv_text_translate_fragment:

                break;
        }
    }
}
