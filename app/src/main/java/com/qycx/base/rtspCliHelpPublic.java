



package com.qycx.base;

import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_stream;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;

public class rtspCliHelpPublic
{
    public static class qyFuncs
{

        public static boolean bUseSplitter(byte ucCompressors, int dwFourcc)
        {
            return ((ucCompressors) == CONST_videoCompressors_stream && (dwFourcc) != CONST_fourcc_h264);
        }

}
}
