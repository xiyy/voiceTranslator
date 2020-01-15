package com.sogou.speech.vad;

import java.util.Arrays;



public class VadAlgorithm {
    public static class VadResult{
        public int nonVoiceFrameCount;
        public int voiceFrameCount;
        public long samplesPerFrame;
    }

    /**
     * omit vars below, set them as local vars or param vars , or put thenm elsewhere
     int m_max_wav_len;
     short *m_raw_wav;
     int m_reserve_len;
     int m_wav_len;

     short *m_out_wav;
     int m_out_wav_len;

     int m_pre_reserve_len;
     short *m_out_wav_pre;
     int m_out_wav_pre_len;

     int m_frame_sum;
     */
    private static final double pi = 3.1415926;
    private static final double eps = 2.2204e-16;

    private final int m_win_size;
    private final int m_shift_size;
    private final double[] m_ana_win;

    private final double m_alfa_ff;
    private final double m_alfa_sf;
    private final double m_beta_sf;
    private final double m_alfa_snr;

    private final int m_fft_size; // fft size
    private final int m_log_fft_size; // log of fft size
    private final int[] m_rev; // reverse data for fft
    private final double[] m_sin_fft; // sine data for fft
    private final double[] m_cos_fft; // cosine data for fft

    private final double[] m_win_wav;
    private final double[] m_v_re;
    private final double[] m_v_im;
    private final int m_sp_size;
    private final double[] m_sp;
    private final double[] m_sp_smooth;
    private final double[] m_sp_ff;
    private final double[] m_sp_sf;
    private final double[] m_sp_ff_pre;
    private final double[] m_sp_snr;
    private final int m_freq_win_len;
    private final double[] m_freq_win;

    private final int m_fs;
    private final int m_ind_2k;
    private final int m_ind_4k;
    private final int m_ind_6k;
    private final double m_thres_02;
    private final double m_thres_24;
    private final double m_thres_46;
    private final double m_thres_68;

    private final int[] mNonAndSoundFrameCount = {0, 0};
    private final long mSamplesPerFrame;
    private final double[] mNumSub = { 0, 0, 0, 0 };

    /**
     *  add two member variables
     *  m_initial_fnum,  initial frame number
     *  m_db_thres, mean square energy threshold
     *  2016.3.3
     *  @liukeang
     */
    private final int m_initial_fnum;
    private final double m_db_thres;

    /** add two members to constructor of Vad
     *  2016.3.3
     *  @liukeang
     */

    public VadAlgorithm(int fs, int win_size, int shift_size, double alfa_ff,
                        double alfa_sf, double beta_sf, double alfa_snr, double thres_02, double thres_24, double thres_46,
                        double thres_68, int fft_size, int freq_win_len, int intial_fnum, double db_thres) {
        m_fs = fs;
        m_win_size = win_size;
        m_shift_size = shift_size;
        // add new line, set shift_size to samplePerFrame
        mSamplesPerFrame = shift_size;

        m_alfa_ff = alfa_ff;
        m_alfa_sf = alfa_sf;
        m_beta_sf = beta_sf;
        m_alfa_snr = alfa_snr;

		/*
		 * 		initialize two new members
		 * 		2016.3.3
		 * 		@liukeang
		 * */

        this.m_initial_fnum = intial_fnum;
        this.m_db_thres = db_thres;


        // Analysis window
        m_ana_win = new double[m_win_size];
        for (int i = 0; i < m_win_size; ++i) {
            m_ana_win[i] = 0.54 - 0.46 * Math.cos((2 * i + 1) * pi / (m_win_size));
        }

        // FFT Related
        m_fft_size = fft_size;
        m_sp_size = m_fft_size / 2 + 1;
        m_ind_2k = 2000 * m_fft_size / m_fs;
        m_ind_4k = 4000 * m_fft_size / m_fs;
        m_ind_6k = 6000 * m_fft_size / m_fs;
        m_thres_02 = thres_02;
        m_thres_24 = thres_24;
        m_thres_46 = thres_46;
        m_thres_68 = thres_68;

        // FFT Related Array
        m_rev = new int[m_fft_size];
        m_sin_fft = new double[m_fft_size / 2];
        m_cos_fft = new double[m_fft_size / 2];

        // Windowed Data
        m_win_wav = new double[m_fft_size];
        m_v_re = new double[m_fft_size];
        m_v_im = new double[m_fft_size];

        // Spectral Power
        m_sp = new double[m_sp_size];
        m_sp_smooth = new double[m_sp_size];
        m_sp_ff = new double[m_sp_size];
        m_sp_sf = new double[m_sp_size];
        m_sp_ff_pre = new double[m_sp_size];
        m_sp_snr = new double[m_sp_size];

        for (int i = 0; i < m_sp_size; ++i) {
            m_sp_snr[i] = 1;
        }

        // Frequency Smoothed Window
        m_freq_win_len = freq_win_len;
        m_freq_win = new double[2 * m_freq_win_len + 1];

        double tmp = 1.0 / (m_freq_win_len + 1);
        for (int i = 0; i < m_freq_win_len; ++i) {
            m_freq_win[i] = (i + 1) * tmp;
            m_freq_win[2 * m_freq_win_len - i] = (i + 1) * tmp;
        }
        m_freq_win[m_freq_win_len] = 1.0;

        m_log_fft_size = init_fft(fft_size);
    }

    //make fft_size from global to formal args(形参),
    // make log_fft_size from global to local, return log_fft_size,
    private int init_fft(int fft_size) {
        int i, j;
        int tmp;
        int log_fft_size = 0;
        tmp = 1;
        while (tmp < fft_size) {
            ++log_fft_size;
            tmp *= 2;
        }
        for (i = 0; i < fft_size; ++i) {
            m_rev[i] = 0;
            tmp = i;
            for (j = 0; j < log_fft_size; ++j) {
                m_rev[i] = (m_rev[i] << 1) | (tmp & 1); // rev_i = rev_i*2+tmp%2
                tmp = tmp >> 1;
            }
        }

        final int half_fft = fft_size / 2;
        final double two_pi_div_fft = 2 * pi / fft_size;
        for (i = 0; i < half_fft; ++i) {
            m_sin_fft[i] = Math.sin(two_pi_div_fft * i);
            m_cos_fft[i] = Math.cos(two_pi_div_fft * i);
        }
        return log_fft_size;
    }

    private void fft_dit(final double[] x, double[] v_re, double[] v_im) {
        int i, j, k;
        int p, q, m, n;
        double tmp1, tmp2;

        for (i = 0; i < m_fft_size; ++i) {
            v_re[m_rev[i]] = x[i];
            v_im[m_rev[i]] = 0;
        }
        p = m_fft_size / 2;
        q = 1;

        for (i = 1; i <= m_log_fft_size; ++i) {
            m = 0;
            n = m + q;
            for (j = 0; j < p; ++j) {
                for (k = 0; k < q; ++k) {
                    tmp1 = v_re[n] * m_cos_fft[k * p] + v_im[n] * m_sin_fft[k * p];
                    tmp2 = v_im[n] * m_cos_fft[k * p] - v_re[n] * m_sin_fft[k * p];
                    v_re[n] = v_re[m] - tmp1;
                    v_im[n] = v_im[m] - tmp2;
                    v_re[m] = v_re[m] + tmp1;
                    v_im[m] = v_im[m] + tmp2;
                    ++m;
                    ++n;
                }
                m = n;
                n = m + q;
            }
            p = p >> 1;
            q = q << 1;
        }
    }

    /*
    Qilin make modifications:
	1.make m_frame_sum from global to local variable
	2.make raw_wav , total_raw_len as input param
	3.return processed raw buffer length
    */

    private int detect_sp_ratio(boolean isFirstPack, short[] raw_wav, int total_raw_len, int[] nonAndSoundFrameCount) {
        int speech_frame_num = 0;
        int non_speech_frame_num = 0;
        int sta;
        int i, j;
        double tmp, energy_db;
        int frame_sum = 0;
        for (sta = 0; sta + m_win_size < total_raw_len; sta += m_shift_size) {
            ++frame_sum;
            energy_db = 0;
            // add window
            for (i = 0; i < m_win_size; ++i) {
                final short raw_i = raw_wav[i + sta];
                m_win_wav[i] = raw_i * m_ana_win[i];
                energy_db += raw_i * raw_i;
            }
            energy_db /= m_win_size;
            energy_db = 10 * Math.log10(energy_db + eps);
            // fft
            fft_dit(m_win_wav, m_v_re, m_v_im);
            m_sp[0] = 0.0;
            for (i = 1; i < m_sp_size; ++i) {
                m_sp[i] = m_v_re[i] * m_v_re[i] + m_v_im[i] * m_v_im[i];
            }
            // smooth in frequency domain

            for (i = 1; i < m_freq_win_len; ++i) {
                m_sp_smooth[i] = 0;
                tmp = 0;
                for (j = 0; j <= i + m_freq_win_len; ++j) {
                    m_sp_smooth[i] += m_sp[j] * m_freq_win[j - i + m_freq_win_len];
                    tmp += m_freq_win[j - i + m_freq_win_len];
                }
                m_sp_smooth[i] /= tmp;
            }

            for (i = m_freq_win_len; i < m_sp_size - 1 - m_freq_win_len; ++i) {
                m_sp_smooth[i] = 0;
                tmp = 0;
                for (j = i - m_freq_win_len; j <= i + m_freq_win_len; ++j) {
                    m_sp_smooth[i] += m_sp[j] * m_freq_win[j - i + m_freq_win_len];
                    tmp += m_freq_win[j - i + m_freq_win_len];
                }
                m_sp_smooth[i] /= tmp;
            }

            for (i = m_sp_size - 1 - m_freq_win_len; i < m_sp_size - 1; ++i) {
                m_sp_smooth[i] = 0;
                tmp = 0;
                for (j = i - m_freq_win_len; j < m_sp_size; ++j) {
                    m_sp_smooth[i] += m_sp[j] * m_freq_win[j - i + m_freq_win_len];
                    tmp += m_freq_win[j - i + m_freq_win_len];
                }
                m_sp_smooth[i] /= tmp;
            }

            // initialize for first frame
			/* 	replace inital_frame_num with global variable m_initial_fnum,
			 * 	and comment the local var inital_frame_num , change if condition
			 * 	2016.3.3
			 * 	@liukeang
			 * */

            // isFirstPack <==> pack_id == 1
            if (isFirstPack && frame_sum <= m_initial_fnum) {
                for (i = 0; i < m_sp_size; ++i) {
                    final double delta = m_sp_smooth[i] / m_initial_fnum;
                    m_sp_ff[i] += delta;
                    m_sp_sf[i] += delta;
                    m_sp_ff_pre[i] += delta;
                }
                continue;
            }

            // ff smooth
            for (i = 0; i < m_sp_size; ++i) {
                m_sp_ff[i] = m_alfa_ff * m_sp_ff[i] + (1 - m_alfa_ff) * m_sp_smooth[i];
            }
            // sf smooth
            for (i = 0; i < m_sp_size; ++i) {
                if (m_sp_sf[i] < m_sp_ff[i]) {
                    m_sp_sf[i] = m_alfa_sf * m_sp_sf[i]
                            + (1 - m_alfa_sf) * (m_sp_ff[i] - m_beta_sf * m_sp_ff_pre[i]) / (1 - m_beta_sf);
                } else {
                    m_sp_sf[i] = m_sp_ff[i];
                }
            }
            for (i = 0; i < m_sp_size; ++i) {
                m_sp_snr[i] = m_alfa_snr * m_sp_snr[i] + (1 - m_alfa_snr) * (m_sp_ff[i] / (m_sp_sf[i] + eps));
            }

            Arrays.fill(mNumSub, 0);

            for (i = 1; i < m_ind_2k; ++i) {
                if (m_sp_snr[i] >= m_thres_02) {
                    mNumSub[0] += 1;
                }
            }
            for (i = m_ind_2k; i < m_ind_4k; ++i) {
                if (m_sp_snr[i] >= m_thres_24) {
                    mNumSub[1] += 1;
                }
            }
            for (i = m_ind_4k; i < m_ind_6k; ++i) {
                if (m_sp_snr[i] >= m_thres_46) {
                    mNumSub[2] += 1;
                }
            }
            for (i = m_ind_6k; i <= m_sp_size - 2; ++i) {
                if (m_sp_snr[i] >= m_thres_68) {
                    mNumSub[3] += 1;
                }
            }
            int unvoice = 0;
            if ((mNumSub[2] + mNumSub[3]) / (m_sp_size - 1 - m_ind_4k) >= 0.5) {
                unvoice = 1;
            }
            mNumSub[0] = mNumSub[0] / (m_ind_2k - 1);
            mNumSub[1] = mNumSub[1] / (m_ind_4k - m_ind_2k);
            mNumSub[2] = mNumSub[2] / (m_ind_6k - m_ind_4k);
            mNumSub[3] = mNumSub[3] / (m_sp_size - 1 - m_ind_4k);
            // printf( "%lf\t%lf\t%lf\t%lf\t%lf\n", mNumSub[0], mNumSub[1],
            // mNumSub[2], mNumSub[3], ( mNumSub[2] + mNumSub[3] ) / (
            // m_sp_size-1-128) );
            int num = 0;
            for (i = 0; i < 4; ++i) {
                if (mNumSub[i] >= 0.3) {
                    ++num;
                }
            }

            // update on 2015-12-30
            // repace 55 with m_db_thres , 2016.3.3
            if (energy_db < m_db_thres) {
                non_speech_frame_num++;
            } else if (num >= 1 || unvoice == 1) {
                speech_frame_num++;
            } else {
                non_speech_frame_num++;
            }
            System.arraycopy(m_sp_ff, 0, m_sp_ff_pre, 0, m_sp_size);
        } // end for

        nonAndSoundFrameCount[0] = non_speech_frame_num;
        nonAndSoundFrameCount[1] = speech_frame_num;

        //omit this line:
        //m_reserve_len = m_wav_len - sta;

        // why return sta?
        return sta;
    }

    public int detectVoice(boolean isFirstPack, short[] raw_wav, int total_raw_len, VadResult vadResult) {
        // 1. Input arguments check
        if (raw_wav == null || total_raw_len == 0) {
            return 0;
        }

        // 2. voice activity detection
        mNonAndSoundFrameCount[0] = mNonAndSoundFrameCount[1] = 0;
        int processedLen = detect_sp_ratio(isFirstPack, raw_wav, total_raw_len, mNonAndSoundFrameCount);

        // 3. Decide speech exists or not
        final int non_speech_frame_num = mNonAndSoundFrameCount[0];
        final int speech_frame_num = mNonAndSoundFrameCount[1];
        vadResult.nonVoiceFrameCount = non_speech_frame_num;
        vadResult.voiceFrameCount = speech_frame_num;
        vadResult.samplesPerFrame = mSamplesPerFrame;

        return processedLen;
    }
}