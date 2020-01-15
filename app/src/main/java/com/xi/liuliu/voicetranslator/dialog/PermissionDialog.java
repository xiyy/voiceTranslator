package com.xi.liuliu.voicetranslator.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xi.liuliu.voicetranslator.R;


public class PermissionDialog implements View.OnClickListener {
    private static final String TAG = PermissionDialog.class.getSimpleName();
    private Context mContext;
    private DialogView mDialogView;
    private TextView mExitTv;
    private TextView mSettingTv;
    private TextView mPermissionHintTv;
    public PermissionDialog(Context context,int hintId) {
        mContext = context;
        init(hintId);
    }

    private void init(int hintId) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_permission, null);
        mPermissionHintTv = view.findViewById(R.id.hint_tv_dialog_permission);
        mPermissionHintTv.setText(hintId);
        mExitTv = view.findViewById(R.id.exit_tv_dialog_permission);
        mExitTv.setOnClickListener(this);
        mSettingTv = view.findViewById(R.id.setting_tv_dialog_permission);
        mSettingTv.setOnClickListener(this);
        mDialogView = new DialogView(mContext, view);
        mDialogView.setGravity(Gravity.CENTER);
        mDialogView.setCanceledOnTouchOutside(false);
        mDialogView.setCancelable(false);
        mDialogView.setDimBehind(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_tv_dialog_permission:
                dismiss();
                break;
            case R.id.setting_tv_dialog_permission:

                dismiss();
                break;
        }
    }

    public void show() {
        if (mDialogView != null) {
            mDialogView.showDialog();
        }

    }

    public void dismiss() {
        if (mDialogView != null) {
            mDialogView.dismissDialog();
        }
    }


}
