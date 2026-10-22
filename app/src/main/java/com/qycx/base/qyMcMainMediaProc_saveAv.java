


package com.qycx.base;


import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_vp80;

public class qyMcMainMediaProc_saveAv {
    public static class qyFuncs {
        public static boolean canSave_fourcc(int iFourcc) {
            switch (iFourcc) {
                case CONST_fourcc_h264:
                case CONST_fourcc_vp80:
                    return true;
                default:
                    break;
            }
            return false;
        }

        public static qmcFunc.PF_threadProc mcThreadProc_saveAv = (Object obj) ->
        {
            int iErr = -1;


            return;
        };

    }
}
