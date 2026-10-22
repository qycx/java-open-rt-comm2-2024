


package com.qycx.base;

public class qmcShareDynBmp {

    public static class SHARE_dyn_bmp_var {
        public boolean bUsing;
        //boolean					bShared;
        //
        public int iIndex_sharedObj;
        public int iIndex_sharedObjUsr;                    //  2014/09/11
        public int uiTaskType;
        public boolean ucbUsed_iIndex_sharedObj;             //  2014/09/11.
        //
        public boolean bSupported_pktResType_sharedTex;       //  2015/10/20

        //
        public qnmCommProc_mis.TASK_av_props taskAvProps;                          //  2015/10/23

        //
        public int dwTickCnt_start;                      //  2014/09/12

        //
        public boolean ucbLocalVideoOpen;
        public int iTaskId;                                //  2009/09/13

        //
        //  2012/05/06
        //ZONE_PARAM zoneParam;

        //
        public static class SHARE_dyn_bmp_var_recvdReq {
            int uiTranNo_storeRecvdReq;                    //  2014/09/11
            int index_recvdReq;
        }

        ;
        public SHARE_dyn_bmp_var_recvdReq recvdReq;

        //  2014/09/11
        public static class SHARE_dyn_bmp_var_internalProcess {
            int dwTickCnt_start;
            boolean bInProcess;
            int nStep;
            //
            boolean bTest;
        }

        ;
        public SHARE_dyn_bmp_var_internalProcess internalProcess;

    }

    ;

    public static class SHARE_dyn_bmp {
        //
        //  2016/07/24
        public qnmCommProc_mis.RES_obj_simple resObj = new qnmCommProc_mis.RES_obj_simple();

        //
        int iMenuId;
        //String name[MAX_rtspUrlLen + 1];
        String cusName;//[32];

        // 2017/06/12
        int usW, usH;                                //  2017/06/12


        //
        boolean bAudioExists;                          //  2015/05/18. ÓÐÒôÆµ²¿·Ö
        boolean bVideoExists;                          //  2015/05/18. ÓÐÊÓÆµ²¿·Ö

        //
        int iIndex_sharedObj_master;                //  2014/09/29. ÓÃ×÷makeSlaveStreamÊ±ÓÐÐèÒª
        //
        qyCommProc_open.BITMAPINFOHEADER bih_mosaic;                                //  2014/10/31. ÓÃ×÷makePhotomosaicµÄÐèÒª


        //
        public SHARE_dyn_bmp_var var = new SHARE_dyn_bmp_var();

//
//QIS_ptz_wnd ptzWnd;


    }

    ;

}



