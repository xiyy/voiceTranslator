package com.sogou.speech.utils;


public class ErrorHint {
    //1、网络相关
    public static final String HINT_ERROR_NETWORK_UNAVAILABLE = "网络未连接,请检查";
    public static final String HINT_NETWORK_TRY_AGAIN = "网络不给力,再试一次";
    //2、录音相关
    public static final String HINT_ERROR_AUDIO_FORBIDDEN = "无录音权限,请检查";
    public static final String HINT_AUDIO_TRY_AGAIN_LATER = "录音失败,稍后重试";
    public static final String HINT_AUDIO_TRY_AGAIN = "录音失败,再试一次";
    public static final String HINT_AUDIO_SAY_AGAIN = "没听清楚,再试一次";
    //3、预处理相关
    public static final String HINT_PRETREATMENT_TRAY_AGAIN_LATER = "出错了,等等再试";
    public static final String HINT_PRETREATMENT_SAY_AGAIN = "没听清楚,再试一次";
    public static final String HINT_PRETREATMENT_SPEECH_TOO_LONG = "录音过长,请重试";
    public static final String HINT_PRETREATMENT_TRY_AGAIN = "出错了,再试一次";
    //4、离线引擎相关
    public static final String HINT_BF_FAILED = "离线语音损坏";
    public static final String HINT_BF_TRY_AGAIN = "离线语音异常,请重试";
    //5、识别服务相关
    public static final String HINT_RECOGNIZE_TRY_AGAIN = "出错了,再试一次";
    //8、后端返回状态码
    public static final String HINT_SERVER_SAY_AGAIN = "没听清楚,再试一次";
    public static final String HINT_SERVER_TRY_AGAIN = "出错了,再试一次";
    //9、其他
    public static final String HINT_OTHER_TRY_AGAIN = "出错了,再试一次";

    public static String getHint(int errorCode) {
        String hint;
        switch (errorCode) {
            case ErrorIndex.ERROR_NETWORK_UNAVAILABLE:
                hint = HINT_ERROR_NETWORK_UNAVAILABLE;
                break;
            case ErrorIndex.ERROR_NETWORK_TIMEOUT:
            case ErrorIndex.ERROR_NETWORK_IO:
            case ErrorIndex.ERROR_NETWORK_CONN:
            case ErrorIndex.ERROR_NETWORK_PROTOCOL:
            case ErrorIndex.ERROR_NETWORK_EXCEED_RETRY_TIMES:
            case ErrorIndex.ERROR_NETWORK_MALFORMED_URL:
            case ErrorIndex.ERROR_NETWORK_UNSUPPORTED_ENCODING:
            case ErrorIndex.ERROR_NETWORK_SECURITY_EXCEPTION:
            case ErrorIndex.ERROR_NETWORK_OTHER:
                hint = HINT_NETWORK_TRY_AGAIN;
                break;
            case ErrorIndex.ERROR_AUDIO_FORBIDDEN:
                hint = HINT_ERROR_AUDIO_FORBIDDEN;
                break;
            case ErrorIndex.ERROR_AUDIO_IS_NULL:
            case ErrorIndex.ERROR_AUDIO_ILLEGAL_SAMPLE_RATE:
            case ErrorIndex.ERROR_AUDIO_ILLEGAL_BUFFER_SIZE:
            case ErrorIndex.ERROR_AUDIO_ILLEGAL_ARGUMENT:
            case ErrorIndex.ERROR_AUDIO_READ_FAILED:
            case ErrorIndex.ERROR_AUDIO_INITIALIZE_FAILED:
                hint = HINT_AUDIO_TRY_AGAIN_LATER;
                break;
            case ErrorIndex.ERROR_AUDIO_END_WHEN_START_AUDIO:
                hint = HINT_AUDIO_SAY_AGAIN;
                break;
            case ErrorIndex.ERROR_AUDIO_START_FAILED:
            case ErrorIndex.ERROR_AUDIO_STOP_FAILED:
            case ErrorIndex.ERROR_AUDIO_RELEASE_FAILED:
                hint = HINT_AUDIO_TRY_AGAIN;
                break;
            case ErrorIndex.ERROR_AGC_PROCESS:
            case ErrorIndex.ERROR_AGC_INIT_FAILED:
            case ErrorIndex.ERROR_AEC_PROCESS:
            case ErrorIndex.ERROR_AEC_INIT_FAILED:
            case ErrorIndex.ERROR_VAD_AUDIO_BUFFER_OVERRUN:
                hint = HINT_PRETREATMENT_TRAY_AGAIN_LATER;
                break;
            case ErrorIndex.ERROR_VAD_SPEECH_TIMEOUT:
            case ErrorIndex.ERROR_VAD_AUDIO_TOO_SHORT:
                hint = HINT_PRETREATMENT_SAY_AGAIN;
                break;
            case ErrorIndex.ERROR_VAD_SPEECH_TOO_LONG:
            case ErrorIndex.ERROR_VAD_AUDIO_INPUT_TOO_LONG:
                hint = HINT_PRETREATMENT_SPEECH_TOO_LONG;
                break;
            case ErrorIndex.ERROR_SPEEX_ENCODE:
                hint = HINT_PRETREATMENT_TRY_AGAIN;
                break;
            case ErrorIndex.ERROR_BF_DECODER_MODEL_PATH_ERROR:
            case ErrorIndex.ERROR_BF_DECODER_AUTH_ILLEGAL:
                hint = HINT_BF_FAILED;
                break;
            case ErrorIndex.ERROR_BF_DECODER_INIT_FAILED:
            case ErrorIndex.ERROR_BF_DECODER_START_FAILED:
            case ErrorIndex.ERROR_BF_DECODER_DECODE_FAILED:
            case ErrorIndex.ERROR_BF_DECODER_ERROR:
            case ErrorIndex.ERROR_BF_DECODER_STOP_FAILED:
            case ErrorIndex.ERROR_BF_RECOGNITION_NO_MATCH:
                hint = HINT_BF_TRY_AGAIN;
                break;

            case ErrorIndex.ERROR_CORRECTING_FAILED:
                hint = HINT_RECOGNIZE_TRY_AGAIN;
                break;
            case ErrorIndex.ERROR_SERVER_NO_DECODED_RESULT:
            case ErrorIndex.ERROR_SERVER_VOICE_CONTENT_EMPTY:
                hint = HINT_SERVER_SAY_AGAIN;
                break;
            case ErrorIndex.ERROR_SERVER_DECODE_FAILED:
            case ErrorIndex.ERROR_SERVER_IS_BUSY:
            case ErrorIndex.ERROR_SERVER_SOCKET_CONNECTION_FAILED:
            case ErrorIndex.ERROR_SERVER_PARSE_REQUEST_BODY_FAILED:
            case ErrorIndex.ERROR_SERVER_REQUEST_BODY_EMPTY:
            case ErrorIndex.ERROR_SERVER_REQUEST_METHOD_ILLEGAL:
            case ErrorIndex.ERROR_SERVER_RESPONSE_NOT_200:
                hint = HINT_SERVER_TRY_AGAIN;
                break;
            case ErrorIndex.ERROR_ANALYZE_JSON:
            case ErrorIndex.ERROR_NUMBER_FORMAT:
            case ErrorIndex.ERROR_OTHER:
                hint = HINT_OTHER_TRY_AGAIN;
                break;
            default:
                hint = "未知错误";

        }
        return hint;
    }
}
