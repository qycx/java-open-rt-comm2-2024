


package com.qycx.base;

public class qyMcMainCusModule {

    public static class CUS_MODULES {
        public CUS_MODULE_compress[] mems;
    }

    public static class CUS_MODULE_common {
        //#define MACRO_cusModuleCommon
        public int uiType;
        //TCHAR name[32 + 1];
        public boolean bLoaded;
    }

    public static class CUS_MODULE_compress {                                           //  2010/04/28
        public CUS_MODULE_common common=new CUS_MODULE_common();

        //
        public qdcCommProc_open.QDC_MGR mgr=new qdcCommProc_open.QDC_MGR();

        //
        public qdcCommProc_open.PF_qdcInit pf_qdcInit;
        public qdcCommProc_open.PF_qdcExit pf_qdcExit;

        public qdcCommProc_open.PF_qdcInitCompressAudio pf_qdcInitCompressAudio;
        public qdcCommProc_open.PF_qdcExitCompressAudio pf_qdcExitCompressAudio;
        public qdcCommProc_open.PF_qdcCompressAudio pf_qdcCompressAudio;
        public qdcCommProc_open.PF_qdcCompressAudioRun pf_qdcCompressAudioRun;                      //  2016/12/05

        //
        public qdcCommProc_open.PF_qdcInitDecompressAudio pf_qdcInitDecompressAudio;
        public qdcCommProc_open.PF_qdcExitDecompressAudio pf_qdcExitDecompressAudio;
        public qdcCommProc_open.PF_qdcDecompressAudio pf_qdcDecompressAudio;
        public qdcCommProc_open.PF_qdcDecompressAudioRun pf_qdcDecompressAudioRun;                  //  2016/12/05

        //
        public qdcCommProc_open.PF_qdcInitCompressVideo pf_qdcInitCompressVideo;
        public qdcCommProc_open.PF_qdcExitCompressVideo pf_qdcExitCompressVideo;
        //  2014/03/30
        public qdcCommProc_open.PF_qdcGetSpsPps pf_qdcGetSpsPps;

        public qdcCommProc_open.PF_qdcCompressVideo pf_qdcCompressVideo;
        public qdcCommProc_open.PF_qdcCompressVideoRun pf_qdcCompressVideoRun;                      //  2014/04/02


        //
        public qdcCommProc_open.PF_qdcInitDecompressVideo pf_qdcInitDecompressVideo;
        public qdcCommProc_open.PF_qdcExitDecompressVideo pf_qdcExitDecompressVideo;
        public qdcCommProc_open.PF_qdcDecompressVideo pf_qdcDecompressVideo;

        //  2011/12/10
        public qdcCommProc_open.PF_qdcDecompressVideoAndOutput pf_qdcDecompressVideoAndOutput;

        //  2014/03/28
        public qdcCommProc_open.PF_qdcDecompressVideoRun pf_qdcDecompressVideoRun;


        //
        public qdcCommProc_open.PF_qdcGetOutputByIndex pf_qdcGetOutputByIndex;
        public qdcCommProc_open.PF_qdcClearOutputs pf_qdcClearOutputs;

        //
        public boolean ucbInited;

    }

    ;


    public static class qyFuncs {
        public static CUS_MODULE_compress M_getCompressModule(CUS_MODULES pCusModules, int uiModuleType) {
            return getCusModule.qyFuncs.getCusModule((pCusModules), uiModuleType, 0, 0, null);
        }

        public static int loadCusModules(ctxQyMc.CCtxQyMc pQyMc) {
            int iErr = -1;
            int index = 0;
            CUS_MODULE_compress pModule = null;
            //TCHAR tBuf[256] = "";
            //unsigned  int uiVDevId = DEFAULT_vDevId_base;
            //
            qySyncCommProc.CQyRWLock syncObj;

            index = 0;

            pQyMc.cusModules.mems = new CUS_MODULE_compress[2];

            guiShare.GuiShare.pf_loadCusModule_android.call();


            //  2014/06/19
            for (index = 0; index < pQyMc.cusModules.mems.length; index++) {
                if (pQyMc.cusModules.mems[index] != null
                        && pQyMc.cusModules.mems[index].common.bLoaded) {
                    pModule = pQyMc.cusModules.mems[index];
                    pModule.pf_qdcInit.call(pModule.mgr);
                }
            }

            //
            iErr = 0;



            return iErr;
        }


        //
        public static int unloadCusModules(ctxQyMc.CCtxQyMc pQyMc) {
            return 0;
        }

    }
}



