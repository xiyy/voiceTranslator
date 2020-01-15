package com.sogou.speech.utils;

import android.media.MediaPlayer;


public class AudioPlayer {

    private MediaPlayer mMediaPlayer = new MediaPlayer();


    public void start(String filePath) {
        if (mMediaPlayer.isPlaying()) {
            return;
        }
        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(1.0f,1.0f);
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

    }


}
