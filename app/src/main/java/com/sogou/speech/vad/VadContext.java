package com.sogou.speech.vad;



public class VadContext {
    /** Package count in this session */
    public long packCount;

    /** set true when voice end */
    public boolean voiceEnded;

    /** set true when have found speech */
    public boolean voiceFound;

    /** set true when current package is speech */
    public boolean isSpeech;

    /** set true only when speech first be found */
    public boolean isVoiceFirstFound;

    /** set true when waiting too long for speech begin , same meaning as m_quit_silently */
    public boolean beginWaitTimeout;

    /** record begin waiting sample count */
    public long voiceBeginWaitSamples;

    /** record end waiting sample count */
    public long voiceEndWaitSamples;

    /** total length of voice found including ahead*/
    public long foundVoiceLengthInSamples;

    /** begin sample index of one session*/
    public long sessionBeginOffsetSample;


    /** sample count before speech begins*/
    public long aheadOfSpeechLengthInSamples;

    public void reset(boolean cutOff){
        if(!cutOff){
            packCount = 0;
        }
        voiceEnded = false;
        voiceFound = false;
        isSpeech = false;
        isVoiceFirstFound = false;
        beginWaitTimeout = false;
        voiceBeginWaitSamples = 0;
        voiceEndWaitSamples = 0;
        foundVoiceLengthInSamples = 0;
        sessionBeginOffsetSample = 0;
        aheadOfSpeechLengthInSamples = 0;
    }
}
