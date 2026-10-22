package com.qycx.base;

import static com.qycx.base.Consts.MAX_PATH;
import static com.qycx.base.qmcSharedObj.Consts.CONST_camCapType_android;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_android;
import static com.qycx.base.qnmCommProc_mis.Consts.DEFAULT_usTimeoutInMsToSwitch_isClient;
import static com.qycx.base.qnmCommProc_mis.Consts.DEFAULT_usTimeoutInMsToSwitch_rt_isClient;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_maxusCnt_players;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_maxusCnt_players_gps;


import android.os.Environment;

import java.io.File;

public class getPolicyIsClient {
    public static class qyFuncs_Droid {

        public static guiShare.PF_getPolicyIsClient getPolicyIsClient = (ctxQmc.CCtxQmc pProcInfo, ctxQmc.POLICY_isClient p) ->
        {
            File path;
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String str1 = path.getAbsolutePath();
            p.dirToSaveFile=str1;
            p.usMax_nPlayers = DEFAULT_maxusCnt_players; //  64;
            p.usMax_nPlayers_gps = DEFAULT_maxusCnt_players_gps; //  2012/05/07

            p.usTimeoutInMsToSwitch = DEFAULT_usTimeoutInMsToSwitch_isClient;                          //  2008/10/28,
            p.usTimeoutInMsToSwitch_rt = DEFAULT_usTimeoutInMsToSwitch_rt_isClient;                            //  2008/10/28,

            p.video.uiCamCapType = (int) getCamCapType(pProcInfo);
            p.audio.uiRecordType = (int) getRecordType_default(pProcInfo);       //  2010/08/02

            return 0;
        };


        public static int getCamCapType(ctxQmc.CCtxQmc pProcInfo) {
            return CONST_camCapType_android;
        }

        public static int getRecordType_default(ctxQmc.CCtxQmc pProcInfo) {
            return CONST_recordType_android;
        }


        public static int getVideoConferenceCfg(Object hKeyRoot0, String pRootKey_qnmScheduler, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.VIDEO_CONFERENCE_CFG pCfg) {
            qnmCommProc_mis.VIDEO_CONFERENCE_CFG p = pCfg;
            {
                //qyFuncs.mymemset(p, 0, sizeof(VIDEO_CONFERENCE_CFG));

            }

            return 0;
        }

    }
}
