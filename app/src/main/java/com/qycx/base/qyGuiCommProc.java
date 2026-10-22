


package com.qycx.base;

public class qyGuiCommProc {

    public static class DLG_syncFlgs {
        public int[] lCnt_getDlgVar;                                    //  2013/07/15
        //
        //  BOOL										bSubWndExists;									//  2011/03/28
        public boolean bInSequence;                                   //  2013/07/15
        //

        //  BOOL										bLocked_bQuitDlg;								//  2012/05/09
    }

    ;


    public static class BG_wall_layout_cfg {
        //
        public int usRows_bg;                                       //  2014/10/01
        public int usCols_bg;                                       //  2014/10/01

        //int											iW_bgWallMem;									//  2014/10/03
        //int											iH_bgWallMem;									//  2014/10/03

    }

    ;


    //  2014/10/02
    public static class BG_wall_layout {
        //
        //unsigned  short								usRows_bg;										//  2014/10/01
        //unsigned  short								usCols_bg;										//  2014/10/01

        int iW_bgWallMem;                                   //  2014/10/03
        int iH_bgWallMem;                                   //  2014/10/03

    }

    ;

    public static class TALKER_policy {


        //  2017/06/25
        //
        public boolean ucbConfCtrl_starter;


    }

    ;


    public static class qyFuncs {
    }
}
