



package com.qycx.base;
public class qyMcMainRealTimeMediaProc
{
public static class Consts {
    // #define DEFAULT_usMaxCnt_taskInfos									500
    public final static int DEFAULT_usMaxCnt_taskInfos = 5;     //  10;   //100;

    public final static int DEFAULT_maxusCnt_players = 20;     //16;

    //  2012/05/07
    public final static int DEFAULT_maxusCnt_players_gps = 1;   //  10;

    public final static int DEFAULT_usMaxCnt_pSharedObjs = 1;   //  5;  //9;

    //
    public final static int DEFAULT_usMaxCnt_androidThreads = 30;  //300;


    //  音量缓存，表明每次读取多少字节发送单元。这个是程序根据采样测试的结果自动调整。
    public final static int MIN_uiBytesRecorded_perBlockAlign = 320;                       //  2009/05/11
    public final static int MAX_uiBytesRecorded_perBlockAlign = 44100;

    public final static int MIN_audio_uiSample = 8000;                 //  2014/05/24
    public final static int MAX_audio_uiSample = 48000;                    //  2014/05/24

    //
    public final static int DEFAULT_iHertz_pcm_av = 16000;     //  2013/05/25. aec's internal nSamples
    public final static int DEFAULT_nChannels_pcm_av = 1;
    public final static int DEFAULT_wBitsPerSample_pcm_av = 16;



}


    public static class qyFuncs
{
}
}
