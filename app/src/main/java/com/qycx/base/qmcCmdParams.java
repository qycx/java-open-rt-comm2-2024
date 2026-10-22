


package com.qycx.base;

public class qmcCmdParams {
    public static class QMC_APP_PARAMS {

        //  appObjPrefix= "qm"  +  iSeqNoSelected_appObjPrefix. 2014/02/10
        public String appObjPrefix;//[32];
        //
        int iSeqNoSelected_appObjPrefix;    //
        //  2015/02/01
        int tn_cliPipe;                     //  2015/02/02

        public String serverAddr;//[64];
        public int port;

        //
        public String user;//[32];                     //  2014/02/10
        public String passwd;//[32];
        //
        public String rtspUrl;//[128];                 //  2014/12/12
        //
        //  2015/04/28
        boolean ucbNoDvt;                     //  2015/04/28
        boolean ucbNoEvt;                     //  2015/10/28

        //  2016/04/26
        boolean ucbDbgDvt;                        //  2016/04/26
        boolean ucbDbgEvt;                        //  2016/05/05
        //
        boolean ucbDbgOnvif;                  //  2016/0


        //  2015/07/18
        int iTestNo;

        //
        boolean bMcu;
        //
        qyDefs.QY_MESSENGER_ID idInfo_mcu;

        //
        boolean bService;

        //
        boolean bConfServer;
        //
        boolean bFileServer;


        //
        boolean bUseMachineKey;
        boolean bUseAuthTypeMcu;
        boolean ucbNotNeedLocked;                         //

        //
        public boolean canStartConf;
        public boolean bNoPrompt;
        boolean bNoAcceptAv;

        //  2015/10/04
        int usPktResType_suggested;
    }
}
