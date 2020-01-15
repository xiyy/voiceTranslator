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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sogou.speech.framework.GrpcAsrManager;
import com.sogou.speech.http.GrpcTranslateRequestPro;
import com.sogou.speech.http.GrpcTtsRequestProtocol;
import com.sogou.speech.listener.AudioRecordListener;
import com.sogou.speech.listener.GrpcAsrListener;
import com.sogou.speech.listener.GrpcTranslateListener;
import com.sogou.speech.listener.GrpcTtsListener;
import com.sogou.speech.listener.PreprocessListener;
import com.sogou.speech.tts.v1.AudioConfig;
import com.sogou.speech.utils.AudioPlayer;
import com.sogou.speech.utils.AudioSaver;
import com.sogou.speech.utils.FileUtils;
import com.sogou.speech.utils.Settings;
import com.xi.liuliu.voicetranslator.R;
import com.xi.liuliu.voicetranslator.adapter.VoiceTranslateRecyclerAdapter;
import com.xi.liuliu.voicetranslator.bean.Language;
import com.xi.liuliu.voicetranslator.bean.VoiceTranslateResult;
import com.xi.liuliu.voicetranslator.dialog.AsrDialog;
import com.xi.liuliu.voicetranslator.utils.GsonUtil;
import com.xi.liuliu.voicetranslator.utils.LogUtil;
import com.xi.liuliu.voicetranslator.utils.SerializeUtil;
import com.xi.liuliu.voicetranslator.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/7/30
 * Author:zhangxiaobei
 * Describe:
 */
public class VoiceTranslateFragment extends Fragment implements View.OnClickListener, AudioRecordListener, PreprocessListener, GrpcAsrListener, GrpcTranslateListener, GrpcTtsListener {
    private static final String TAG = VoiceTranslateFragment.class.getSimpleName();
    private static final int TRANSLATE_TYPE_SRC = 0;
    private static final int TRANSLATE_TYPE_DEST = 1;
    private RelativeLayout mVoiceGuideRl;
    private RecyclerView mChatListRv;
    private TextView mStartVoiceSrcTv;
    private TextView mStartVoiceDestTv;
    private List<VoiceTranslateResult> voiceTranslateResultList;
    private VoiceTranslateRecyclerAdapter mVoiceTranslateRecyclerAdapter;
    private AsrDialog mAsrDialog;
    private GrpcAsrManager mGrpcAsrManager;
    private Language mSrcLanguage;
    private Language mDestLanguage;
    private int mTranslateType = -1;
    private String mTtsFileName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.log(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.log(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.log(TAG, "onCreateView");
        AudioSaver.init(Settings.SPEECH_FILE_PATH_RAW_AUDIO);
        View view = inflater.inflate(R.layout.fragment_voice_translate, container, false);
        mVoiceGuideRl = view.findViewById(R.id.voice_guide_rl_translate_fragment);
        mChatListRv = view.findViewById(R.id.chat_list_rv_translate_fragment);
        mStartVoiceSrcTv = view.findViewById(R.id.start_voice_src_translate_fragment);
        mStartVoiceDestTv = view.findViewById(R.id.start_voice_dest_translate_fragment);
        mStartVoiceSrcTv.setOnClickListener(this);
        mStartVoiceDestTv.setOnClickListener(this);
        Language srcLanguage = GsonUtil.getInstance().strToObject(SharedPrefUtil.getString(getContext(), SharedPrefUtil.KEY_SRC_LANGUAGE), Language.class);
        Language destLanguage = GsonUtil.getInstance().strToObject(SharedPrefUtil.getString(getContext(), SharedPrefUtil.KEY_DEST_LANGUAGE), Language.class);
        if (srcLanguage != null) {
            mStartVoiceSrcTv.setText(srcLanguage.getLocalName());
            mSrcLanguage = srcLanguage;
        }
        if (destLanguage != null) {
            mStartVoiceDestTv.setText(destLanguage.getLocalName());
            mDestLanguage = destLanguage;
        }
        voiceTranslateResultList = SerializeUtil.unSerializeVoiceTranslateResultList(getContext());
        if (voiceTranslateResultList == null) {
            voiceTranslateResultList = new ArrayList<>();
            mChatListRv.setVisibility(View.GONE);
            mVoiceGuideRl.setVisibility(View.VISIBLE);

        } else {
            mVoiceGuideRl.setVisibility(View.GONE);
            mChatListRv.setVisibility(View.VISIBLE);
        }
        int size = voiceTranslateResultList.size();
        mChatListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatListRv.setHasFixedSize(true);
        mVoiceTranslateRecyclerAdapter = new VoiceTranslateRecyclerAdapter();
        mVoiceTranslateRecyclerAdapter.setData(voiceTranslateResultList);
        mChatListRv.setAdapter(mVoiceTranslateRecyclerAdapter);
        //滑动到最底部
        if (size>=1) {
            mChatListRv.scrollToPosition(size-1);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.log(TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.log(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.log(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.log(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.log(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.log(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.log(TAG, "onDestroyView");
        SerializeUtil.SerializeVoiceTranslateResultList(getContext(), voiceTranslateResultList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.log(TAG, "onDetach");
    }

    @Override
    public void onClick(View view) {
        Language srcLanguage = GsonUtil.getInstance().strToObject(SharedPrefUtil.getString(getContext(), SharedPrefUtil.KEY_SRC_LANGUAGE), Language.class);
        if (srcLanguage != null) {
            mSrcLanguage = srcLanguage;
        }
        Language destLanguage = GsonUtil.getInstance().strToObject(SharedPrefUtil.getString(getContext(), SharedPrefUtil.KEY_DEST_LANGUAGE), Language.class);
        if (destLanguage != null) {
            mDestLanguage = destLanguage;
        }
        switch (view.getId()) {
            case R.id.start_voice_src_translate_fragment:
                mTranslateType = TRANSLATE_TYPE_SRC;
                mGrpcAsrManager = new GrpcAsrManager(getActivity().getApplicationContext(), this, this, this, mSrcLanguage);
                mAsrDialog = new AsrDialog(getActivity(), mSrcLanguage, mDestLanguage, mGrpcAsrManager);
                if (mAsrDialog != null) {
                    mAsrDialog.show();
                }
                break;

            case R.id.start_voice_dest_translate_fragment:
                mTranslateType = TRANSLATE_TYPE_DEST;
                mGrpcAsrManager = new GrpcAsrManager(getActivity().getApplicationContext(), this, this, this, mDestLanguage);
                mAsrDialog = new AsrDialog(getActivity(), mDestLanguage, mSrcLanguage, mGrpcAsrManager);
                if (mAsrDialog != null) {
                    mAsrDialog.show();
                }
                break;
        }
    }

    public void setSrcAndDestBtnText(String srcLocalName, String destLocalName) {
        mStartVoiceSrcTv.setText(srcLocalName);
        mStartVoiceDestTv.setText(destLocalName);
    }

    @Override
    public void onAudioRecordStart() {
        LogUtil.log(TAG, "onAudioRecordStart");
    }

    @Override
    public void onAudioDataReceived(short[] audioDataArray) {
        LogUtil.log(TAG, "onAudioDataReceived");
        AudioSaver.storeDataToStream(audioDataArray);
    }

    @Override
    public void onAudioRecordError(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onAudioRecordError,errorCode:" + errorCode + " errorMeg:" + errorMessage);
    }

    @Override
    public void onVoiceDecibelChanged(double decibel) {
        LogUtil.log(TAG, "onVoiceDecibelChanged,decibel:" + decibel);
    }

    @Override
    public void onAudioRecordStop() {
        LogUtil.log(TAG, "onAudioRecordStop");
        AudioSaver.storeWav();
    }

    @Override
    public void onAudioRecordRelease() {
        LogUtil.log(TAG, "onAudioRecordRelease");
    }

    @Override
    public void onVadFirstDetected() {
        LogUtil.log(TAG, "onVadFirstDetected");
    }

    @Override
    public void onVadError(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onVadError,errorCode:" + errorCode + " errorMeg:" + errorMessage);
    }

    @Override
    public void onSpeexError(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onSpeexError,errorCode:" + errorCode + " errorMeg:" + errorMessage);
    }

    @Override
    public void onGrpcAsrResult(final String result, boolean isLast) {
        LogUtil.log(TAG, "onGrpcAsrResult,result:" + result + " isLast:" + isLast);
        if (mAsrDialog != null) {
            mAsrDialog.showAsrResult(result);
        }

        if (isLast) {
            if (mTranslateType == TRANSLATE_TYPE_SRC) {
                new GrpcTranslateRequestPro(getContext(), this).translate(result, mSrcLanguage.getAsrCode(), mDestLanguage.getAsrCode());
            } else if (mTranslateType == TRANSLATE_TYPE_DEST) {
                new GrpcTranslateRequestPro(getContext(), this).translate(result, mDestLanguage.getAsrCode(), mSrcLanguage.getAsrCode());
            }


        }

    }

    @Override
    public void onGrpcAsrError(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onGrpcAsrError,errorCode:" + errorCode + " errorMeg:" + errorMessage);
        if (mGrpcAsrManager != null) mGrpcAsrManager.release();
    }

    @Override
    public void onGrpcTranslateResult(String srcText, String translateResult) {
        LogUtil.log(TAG, "onGrpcTranslateResult,srcText:" + srcText + "   translateResult:" + translateResult);
        //tts合成的音频路径
        mTtsFileName = System.currentTimeMillis() + ".mp3";
        String ttsFilePath = Settings.SPEECH_FILE_PATH_TTS_AUDIO + mTtsFileName;
        VoiceTranslateResult voiceTranslateResult = new VoiceTranslateResult(srcText, translateResult, ttsFilePath);
        Language ttsLanguage = null;
        if (mTranslateType == TRANSLATE_TYPE_SRC) {
            voiceTranslateResult.setType(VoiceTranslateResult.TYPE_SRC);
            ttsLanguage = mDestLanguage;
        } else if (mTranslateType == TRANSLATE_TYPE_DEST) {
            voiceTranslateResult.setType(VoiceTranslateResult.TYPE_DEST);
            ttsLanguage = mSrcLanguage;
        }
        voiceTranslateResultList.add(voiceTranslateResult);
        final int size = voiceTranslateResultList.size();
        GrpcTtsRequestProtocol grpcTtsRequestProtocol = new GrpcTtsRequestProtocol(getContext(), VoiceTranslateFragment.this);
        grpcTtsRequestProtocol.tts(translateResult, ttsLanguage.getAsrCode(), "female", 1, 1, 1, AudioConfig.AudioEncoding.MP3);
        //在主线程中刷新数据
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mChatListRv.getVisibility() == View.GONE) {
                    mVoiceGuideRl.setVisibility(View.GONE);
                    mChatListRv.setVisibility(View.VISIBLE);
                }
                mVoiceTranslateRecyclerAdapter.setData(voiceTranslateResultList);
                mVoiceTranslateRecyclerAdapter.notifyDataSetChanged();
                //滑动到最底部
                if (size >= 1) {
                    mChatListRv.scrollToPosition(size - 1);
                }
            }
        });
        if (mGrpcAsrManager != null) mGrpcAsrManager.release();
        if (mAsrDialog != null) {
            mAsrDialog.dismiss();
        }

    }

    @Override
    public void onGrpcTranslateError(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onGrpcTranslateError,errorMessage:" + errorMessage);
        if (mGrpcAsrManager != null) mGrpcAsrManager.release();
        if (mAsrDialog != null) {
            mAsrDialog.dismiss();
        }
    }

    @Override
    public void onTtsSuccess(byte[] data, boolean isLast) {
        String path = Settings.SPEECH_FILE_PATH_TTS_AUDIO + mTtsFileName;
        LogUtil.log(TAG, "onTtsSuccess,isLast:" + isLast + "  ttsFilePath:" + path);
        FileUtils.writeByteArray2SDCard(Settings.SPEECH_FILE_PATH_TTS_AUDIO, mTtsFileName, data, true);
        //合成完成，播放
        if (isLast) {
            AudioPlayer audioPlayer = new AudioPlayer();
            audioPlayer.start(path);
        }

    }

    @Override
    public void onTtsFailed(int errorCode, String errorMessage) {
        LogUtil.loge(TAG, "onTtsFailed,errorCode:" + errorCode + "   errorMessage:" + errorMessage);
    }
}
