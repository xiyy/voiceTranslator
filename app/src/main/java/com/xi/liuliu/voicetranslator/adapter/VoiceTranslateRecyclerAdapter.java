package com.xi.liuliu.voicetranslator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sogou.speech.utils.AudioPlayer;
import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.bean.VoiceTranslateResult;
import com.xi.liuliu.voicetranslator.utils.LogUtil;

import java.util.List;

/**
 * Date:2019/7/31
 * Author:zhangxiaobei
 * Describe:
 */
public class VoiceTranslateRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private static final String TAG = VoiceTranslateRecyclerAdapter.class.getSimpleName();
    private List<VoiceTranslateResult> mVoiceTranslateResultList;
    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 1;

    public VoiceTranslateRecyclerAdapter() {
    }

    public void setData(List<VoiceTranslateResult> list) {
        mVoiceTranslateResultList = list;
    }

    public List<VoiceTranslateResult> getData() {
        return mVoiceTranslateResultList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_LEFT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_src_recycler_view_voice_translate_fragment, viewGroup, false);
            SrcViewHolder srcViewHolder = new SrcViewHolder(view);
            srcViewHolder.view = view;
            srcViewHolder.asrTv = view.findViewById(R.id.text_view_src_asr_item_recycler_view_voice_translate_fragment);
            srcViewHolder.translateTv = view.findViewById(R.id.text_view_src_translate_item_recycler_view_voice_translate_fragment);
            srcViewHolder.playBtn = view.findViewById(R.id.image_view_src_play_item_recycler_view_voice_translate_fragment);
            srcViewHolder.playBtn.setOnClickListener(this);
            return srcViewHolder;
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dest_recycler_view_voice_translate_fragment, viewGroup, false);
            DestViewHolder destViewHolder = new DestViewHolder(view);
            destViewHolder.view = view;
            destViewHolder.asrTv = view.findViewById(R.id.text_view_dest_asr_item_recycler_view_voice_translate_fragment);
            destViewHolder.translateTv = view.findViewById(R.id.text_view_dest_translate_item_recycler_view_voice_translate_fragment);
            destViewHolder.playBtn = view.findViewById(R.id.image_view_dest_play_item_recycler_view_voice_translate_fragment);
            destViewHolder.playBtn.setOnClickListener(this);
            return destViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM_LEFT) {
            SrcViewHolder srcViewHolder = (SrcViewHolder) viewHolder;
            srcViewHolder.asrTv.setText(mVoiceTranslateResultList.get(position).getAsrResult());
            srcViewHolder.translateTv.setText(mVoiceTranslateResultList.get(position).getTranslateResult());
            srcViewHolder.playBtn.setTag(position);
        } else {
            DestViewHolder destViewHolder = (DestViewHolder) viewHolder;
            destViewHolder.asrTv.setText(mVoiceTranslateResultList.get(position).getAsrResult());
            destViewHolder.translateTv.setText(mVoiceTranslateResultList.get(position).getTranslateResult());
            destViewHolder.playBtn.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mVoiceTranslateResultList != null) return mVoiceTranslateResultList.size();
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        LogUtil.log(TAG, "getItemViewType,position:" + position);
        if (mVoiceTranslateResultList != null) {
            int type = mVoiceTranslateResultList.get(position).getType();
            if (type == VoiceTranslateResult.TYPE_SRC) {
                return ITEM_LEFT;
            } else if (type == VoiceTranslateResult.TYPE_DEST) {
                return ITEM_RIGHT;
            }

        }
        return -1;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        //拿到翻译的结果，播报
        AudioPlayer audioPlayer = new AudioPlayer();
        String path = mVoiceTranslateResultList.get(position).getTtsFilePath();
        audioPlayer.start(path);
    }


    public static class SrcViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView asrTv;
        TextView translateTv;
        ImageView playBtn;

        public SrcViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }
    }

    public static class DestViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView asrTv;
        TextView translateTv;
        ImageView playBtn;

        public DestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }
    }

}
