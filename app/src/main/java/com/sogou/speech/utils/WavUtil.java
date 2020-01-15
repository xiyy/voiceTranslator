package com.sogou.speech.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class WavUtil {
    private static final String TAG = "WavUtil";

    public static void constructWav(OutputStream os, ByteOrder bo,
            byte[] byteData, int targetSampleRate, int targetChannels)
            throws IOException {
        SpeechLogUtil.log("construct wav, bytedata.length:"+byteData.length+",os != null:"+(os!=null));
        WavUtil.addWavHeadChars(os, "RIFF".toCharArray());
        int file_size0 = byteData.length + 44 - 8;
        WavUtil.addWavHeadInt(os, bo, file_size0);
        WavUtil.addWavHeadChars(os, "WAVEfmt".toCharArray());
        WavUtil.addWavHeadByte(os, (byte) 0x20);
        WavUtil.addWavHeadInt(os, bo, 0x10);
        WavUtil.addWavHeadShort(os, bo, (short) 0x01);
        // replace 1 with targetChannels, 2015-05-17
        WavUtil.addWavHeadShort(os, bo, (short) targetChannels);
        // replace 16000 with targetSampleRate, 2015-05-12
        WavUtil.addWavHeadInt(os, bo, targetSampleRate);
        WavUtil.addWavHeadInt(os, bo, targetSampleRate * 2);
        WavUtil.addWavHeadShort(os, bo, (short) 2);
        WavUtil.addWavHeadShort(os, bo, (short) 16);
        WavUtil.addWavHeadChars(os, "data".toCharArray());
        WavUtil.addWavHeadInt(os, bo, byteData.length);

        os.write(byteData);
    }

    public static void constructPcm(OutputStream os, ByteOrder bo, byte[] byteData, int targetSampleRate, int targetChannels) throws IOException {
        if (targetSampleRate == 8000) {
            byteData = convert16KTo8K(byteData);
        }
        os.write(byteData);
    }

    public static void addWavHeadInt(OutputStream os, ByteOrder bo, int addone)
            throws IOException {
            os.write((addone >> 0) & 0x000000ff);
            os.write((addone >> 8) & 0x000000ff);
            os.write((addone >> 16) & 0x000000ff);
            os.write((addone >> 24) & 0x000000ff);
    }

    public static void addWavHeadByte(OutputStream os, byte addone)
            throws IOException {
        os.write(addone);
    }

    public static void addWavHeadChar(OutputStream os, char addone)
            throws IOException {
        os.write(addone);
    }

    public static void addWavHeadChars(OutputStream os, char[] addone)
            throws IOException {
        for (char c : addone) {
            os.write(c);
        }
    }

    public static void addWavHeadShort(OutputStream os, ByteOrder bo,
            short addone) throws IOException {
            os.write((addone >> 0) & 0x000000ff);
            os.write((addone >> 8) & 0x000000ff);
    }

    /**
     * 计算出的分贝值正常值域为0 dB 到90.3 dB
     *
     * @param buffer
     * @return
     */
    public static double getVoiceDecibel(short[] buffer) {
        if (buffer != null) {
            int length = buffer.length;
            if (length > 0) {
                long sum = 0;
                for (int i = 0; i < length; i++) {
                    sum += buffer[i] * buffer[i];
                }
                // 平方和除以数据总长度，得到音量大小。
                double mean = sum / (double) length;
                if (mean > 0) {
                    double volume = 10 * Math.log10(mean);
                    return volume;
                }

            }

        }
        return 0;
    }

    public static byte[] convert16KTo8K(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        byte[] resultBytes = new byte[bytes.length / 2];
        int i = 0;
        while (i + 4 < bytes.length) {
            for (int j = 0; j < 2; j++) {
                resultBytes[2 * (i / 4) + j] = bytes[i + j];
            }
            i = i + 4;
        }
        return resultBytes;
    }

}
