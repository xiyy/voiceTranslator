package com.sogou.speech.audiosource;

import com.sogou.speech.audiosource.fileaudiosource.FileAudioSource;
import com.sogou.speech.audiosource.fileaudiosource.FileDataProviderFactory;
import com.sogou.speech.listener.AudioRecordListener;


public class AudioSourceManager {

    public static class AudioSourceType{
        static final int DEFAULT_SOURCE = 0;
        static final int FILE_SOURCE = 1;
    }

    AbstractAudioSource mSource;
    int mType;

    public AudioSourceManager(int sourceType, String audioPath, AudioRecordListener audioRecordListener){
        mType = sourceType;
        switch(sourceType){
            case AudioSourceType.DEFAULT_SOURCE:
                final IAudioDataProviderFactory defaultAudioFactory = new AudioRecordDataProviderFactory(audioRecordListener);
                mSource = new DefaultAudioSource(defaultAudioFactory);
                break;
            case AudioSourceType.FILE_SOURCE:
                final IAudioDataProviderFactory fileAudioFactory = new FileDataProviderFactory(audioPath);
                mSource = new FileAudioSource(fileAudioFactory);
                break;
            default:
                throw new IllegalArgumentException("unknown audio source type");
        }
    }

    public void start(boolean firstStart){
        if(firstStart){
            switch(mType){
                case AudioSourceType.DEFAULT_SOURCE:
                    new Thread((DefaultAudioSource)mSource).start();
                    break;
                case AudioSourceType.FILE_SOURCE:
                    new Thread((FileAudioSource)mSource).start();
                    break;
            }
        }else{
            mSource.start();
        }
    }

    public void pause(){
        mSource.pause();
    }

    public void stop(){
        mSource.stop();
    }


    public void addAudioSourceListener(IAudioSourceListener listener){
        if(mSource != null){
            mSource.addAudioSourceListener(listener);
        }
    }

    public void removeAudioSourceListener(IAudioSourceListener listener){
        if(mSource != null){
            mSource.removeAudioSourceListener(listener);
        }
    }



//    private final DefaultAudioSource mAudioSource;
//    private final FileAudioSource mAudioSource;
}
