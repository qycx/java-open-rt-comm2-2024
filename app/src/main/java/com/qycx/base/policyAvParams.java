


//
package com.qycx.base;

import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;

public class policyAvParams {

    //
//
    public static class Consts {

//		public final static int DEFAULT_conf_fourcc_confServ = CONST_fourcc_HEVC;  //h264
//		public final static int DEFAULT_conf_fourcc_others = CONST_fourcc_HEVC;    //h264

        public final static int DEFAULT_conf_fourcc_confServ = CONST_fourcc_h264;
        public final static int DEFAULT_conf_fourcc_others = CONST_fourcc_HEVC;


    }

    public static class PolicyAvParams {

        //  2013/06/09
        qnmCommProc_is_open.AUDIO_PLAY_CFG CONST_audioPlayCfg_1s, CONST_audioPlayCfg_07s, CONST_audioPlayCfg_05s;

        //  ld
        qnmCommProc_is_open.AUDIO_PLAY_CFG audioPlayCfg_240p;               //  2014/04/23

    }

    ;

}
