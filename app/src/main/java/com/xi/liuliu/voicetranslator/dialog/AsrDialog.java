package com.xi.liuliu.voicetranslator.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sogou.speech.framework.GrpcAsrManager;
import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.bean.Language;


public class AsrDialog implements View.OnClickListener {
    private static final String TAG = AsrDialog.class.getSimpleName();
    private Context mContext;
    private DialogView mDialogView;
    private TextView mFinish;
    private TextView mCancel;
    private View mDivideLine;
    private TextView mAsrStatusTv;
    private TextView mAsrResult;
    private Language mSrcLanguage;
    private Language mDestLanguage;
    private GrpcAsrManager mGrpcAsrManager;

    public AsrDialog(Context context, Language srcLanguage, Language destLanguage, GrpcAsrManager grpcAsrManager) {
        mContext = context;
        mSrcLanguage = srcLanguage;
        mDestLanguage = destLanguage;
        mGrpcAsrManager = grpcAsrManager;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_asr, null);
        mFinish = view.findViewById(R.id.finish_asr_dialog);
        mFinish.setOnClickListener(this);
        mCancel = view.findViewById(R.id.cancel_asr_dialog);
        mCancel.setOnClickListener(this);
        mAsrStatusTv = view.findViewById(R.id.status_asr_dialog);
        if (mSrcLanguage != null) {
            mAsrStatusTv.setText("请说" + mSrcLanguage.getChineseName());
        }
        mAsrResult = view.findViewById(R.id.asr_result_asr_dialog);
        mAsrResult.setText("");
        mDivideLine = view.findViewById(R.id.view_divide_asr_dialog);
        mDialogView = new DialogView(mContext, view);
        mDialogView.setGravity(Gravity.CENTER);
        mDialogView.setCanceledOnTouchOutside(false);
        mDialogView.setCancelable(false);
        mDialogView.setDimBehind(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_asr_dialog:
                if (mGrpcAsrManager != null) mGrpcAsrManager.stop();
                mAsrStatusTv.setText("正在识别");
                mFinish.setVisibility(View.GONE);
                mDivideLine.setVisibility(View.GONE);
                break;
            case R.id.cancel_asr_dialog:
                if (mGrpcAsrManager != null) mGrpcAsrManager.release();
                dismiss();
                break;
        }
    }

    public void show() {
        if (mDialogView != null) {
            mDialogView.showDialog();
        }
        if (mGrpcAsrManager != null) {
            mGrpcAsrManager.start();
        }
    }

    public void dismiss() {
        if (mDialogView != null) {
            mDialogView.dismissDialog();
        }
    }

    public void showAsrResult(final String text) {
        mAsrResult.post(new Runnable() {
            @Override
            public void run() {
                mAsrResult.setText(text);
            }
        });

    }

}
