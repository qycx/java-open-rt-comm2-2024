


package com.qycx.base;

public class qmcSoundFunc {
    public static class Consts {
        public final static int WAVE_FORMAT_PCM = 1;

    }


    public static class qyFuncs {
        public static int makeWaveFormat_pcm(int nChannels, int iHertz, int wBitsPerSample, qyCommProc_open.WAVEFORMATEX p) {
            {
                p.wFormatTag = Consts.WAVE_FORMAT_PCM;
                p.nChannels = (short) nChannels;                                       //  DEFAULT_audio_nChannels;		//  2;
                //p->wBitsPerSample = (wBitsPerSample!=0) ? wBitsPerSample : 16;       //  DEFAULT_audio_wBitsPerSample;	//  16;
                if (wBitsPerSample != 0) p.wBitsPerSample = (short) wBitsPerSample;
                else p.wBitsPerSample = 16;

                p.cbSize = 0;
                p.nSamplesPerSec = iHertz;  //  22.05 KHz

                p.nBlockAlign = (short) ((p.wBitsPerSample / 8) * p.nChannels);
                p.nAvgBytesPerSec = p.nSamplesPerSec * p.nBlockAlign;
            }

            return 0;
        }

    }
}
