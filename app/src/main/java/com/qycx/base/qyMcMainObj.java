


package com.qycx.base;

public class qyMcMainObj {


    public static class Consts {
        public final static int QY_MC_TOTAL_DAEMON_THREADS = 10;            // ºóÌ¨Ïß³ÌÊý


    }

    public static class QY_MC_GUI {

        //  2015/12/08
        public Object pDb;


        //
        public genericQueue.GENERIC_Q processQ;                               //
        public boolean ucbProcessQInited;                          //  2007/06/01

        //
        public Object hMainWnd;                              //  2009/03/25

        //
        public boolean bForeground;
        //
        public Object hActiveWnd;
        //
        public Object hLastActiveTalk;

    }

    public static class QY_SERVICEGUI_CFG {
        public int iServiceId;

        //  2015/01/20
        public static ctxQyMc.PF_newVar pfNewVar;                          //  2015/01/20
        public static ctxQyMc.PF_freeVar pfFreeVar;

        //
        public int uiVarSize;                         //
        public static ctxQyMc.PF_initVar pfInitVar;
        public static ctxQyMc.PF_init_afterLogon pfInit_afterLogon;                 //  201003/10
        public static ctxQyMc.PF_exitVar pfExitVar;                         //

        //
        public int iDbType;                            //  Obsolete

    }

    ;


    public static class QY_SERVICEGUI_INFO {
        public QY_SERVICEGUI_CFG cfg=new QY_SERVICEGUI_CFG();
        //
        public String name;//[32+1];
        //
        public ctxQyTmpl.CCtxQyTmpl pParent;          //  2016/09/19Ö¸Ïòdaemon
        //
        public ctxQmcTmpl.CCtxQmcTmpl pVar;
        public boolean ucbVarInited;
        //

    }

    ;


}
