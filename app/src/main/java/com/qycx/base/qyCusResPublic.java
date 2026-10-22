


package com.qycx.base;

public class qyCusResPublic {

    public static class Consts {
        public final static int CONST_usRunningStatus_null = 0;
        public final static int CONST_usRunningStatus_online = 1;
        public final static int CONST_usRunningStatus_offline = 2;
        public final static int CONST_usRunningStatus_away = 3;
        public final static int CONST_usRunningStatus_hide = 4;                                //  2007/06/07, ÔÚÏß£¬ÀëÏß£¬Àë¿ª£¬ÒþÉíµÈ
        public final static int CONST_usRunningStatus_neverLogon = 5;                              //  Î´ÉÏÏß¡£Ö¸´Ó·þÎñÆ÷¿ª»úºóÃ»ÓÐÉÏ¹ýÏß

        //
        public final static int CONST_imTaskType_null = 0;
        //
        public final static int CONST_imTaskType_sendFile = 11;
        public final static int CONST_imTaskType_recvFile = 12;
        //
        public final static int CONST_imTaskType_avSetup = 15;
        //  public final static int		CONST_imTaskType_listening							16
        //  public final static int		CONST_imTaskType_avCall								17			//  ºóÃæÓ¦¸Ã¸úÒ»¸öÄ¿µÄIP
        //  public final static int		CONST_imTaskType_whiteboard							18			//
        //  public final static int		CONST_imTaskType_appCall							19			//
        //

        /**
         * 可能是内部使用，不是和服务器交互的信令，任务类型：
         */
        public final static int CONST_imTaskType_transferAvInfo = 25;              // 会议类型（点对点、组）
        public final static int CONST_imTaskType_shareScreen = 26;                 // 共享屏幕
        public final static int CONST_imTaskType_remoteAssist = 27;                // 远程协助 (操作屏幕)
        public final static int CONST_imTaskType_shareMediaFile = 28;              // 共享媒体文件
        public final static int CONST_imTaskType_shareDynBmp = 29;                 // 共享视频源
        public final static int CONST_imTaskType_viewDynBmp = 30;                  // 查看视频源
        public final static int CONST_imTaskType_queryCustomerServiceOfficer = 31; // 视频客服（已弃用）
        //
        public final static int CONST_imTaskType_shareDevice_grp		=				32;			//  2024/10/13, 分组观看摄像头

        //  2012/04/19
        public final static int CONST_imTaskType_shareGps = 40;                    // 共享Gps
        public final static int CONST_imTaskType_viewGps = 41;                     // 查看Gps



        //
        //
        public final static int CONST_policyAvLevel_240p = 10;         //  2014/04/23
        //
        public final static int CONST_policyAvLevel_480p = 21;
        public final static int CONST_policyAvLevel_848x480 = 22;
        public final static int CONST_policyAvLevel_576p = 23;

        //
        public final static int CONST_policyAvLevel_720p = 32;         //  2014/04/23
        //
        public final static int CONST_policyAvLevel_1080p = 42;            //  2014/04/23

        //
        public final static int CONST_policyAvLevel_1080p60 = 45;          //  2023/12/31


        //
        public final static int CONST_policyAvLevel_2160p = 52;            //  2023/12/31

        //
        public final static int CONST_policyAvLevel_2160p60 = 55;            //  2023/12/31


        //  2012/02/14
        public final static int CONST_policyAvLevel_dev_2fps = 110;            //  2fps, dmo.		screen
        public final static int CONST_policyAvLevel_dev_5fps = 112;            //  5fps-300k, ipp. screen
        //  2012/10/29
        public final static int CONST_policyAvLevel_dev_5fps1 = 113;           //  5fps-450k, ipp. screen
        //  2015/11/06
        public final static int CONST_policyAvLevel_dev_5fps2 = 114;            //  5fps-700k, screen
        //  2012/11/3


    }

}
