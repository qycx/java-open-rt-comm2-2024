


package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.qnmCommProc.qyFuncs.myWChar2Utf8;
import static com.qycx.base.tmpFunc.qyFuncs.mytoString;

public class isFunc {
    public static class qyFuncs {
        public static qyTypes.QY_DMITEM[] CONST_channelTypeTable = new qyTypes.QY_DMITEM[]
                {
                        //new qyTypes.QY_DMITEM(0,"",""),
                        //new qyTypes.QY_DMITEM(1,"kk",""),
                        //new qyTypes.QY_DMITEM(-1,"","")
                        new qyTypes.QY_DMITEM(CONST_channelType_null, ("")),
                        new qyTypes.QY_DMITEM(CONST_channelType_talking, ("Talk")),
                        new qyTypes.QY_DMITEM(CONST_channelType_robot, ("Robot")),
                        new qyTypes.QY_DMITEM(CONST_channelType_webSending, ("WebTalk")),
                        new qyTypes.QY_DMITEM(CONST_channelType_media, ("Video")),
                        new qyTypes.QY_DMITEM(CONST_channelType_realTimeMedia, ("Audio")),
                        new qyTypes.QY_DMITEM(CONST_channelType_rtOp, ("Op")),
                        new qyTypes.QY_DMITEM(-1),
                };


        public static long idStr2Id1(byte[] idStr) {
            int iErr = -1;

            if (null == idStr ) return -1;

            long pId  =  0;

            try {
                pId = (long) Float.parseFloat(mytoString(idStr)); //  2007/04/03, 这里暂时只取低位的数。
            } catch (Exception e) {
                pId = 0;
            }
            return pId;
        }
        public static long idStr2Id(byte[] idStr) {
            if (null == idStr ) return -1;

            long result = 0;
            int len = Math.min(idStr.length, 64);

            for(int i = 0;i < len; i ++){
                if(idStr[i] == 0) break;
                result = result *10 + idStr[i] -48;
            }
            return result;

            // pId = (long) Float.parseFloat(mytoString(idStr)); //  2007/04/03, 这里暂时只取低位的数。
        }

        public static byte[] idInfo2Str(qyDefs.QY_MESSENGER_ID pIdInfo, byte[] idStr, int size) {
            if (null == pIdInfo || null == idStr || size <= qnmCommProc_mis.Consts.CONST_qyMessengerIdStrLen)
                return null;
            //_snprintf(idStr, size, "%018I64u", pIdInfo->ui64Id);
            String str = String.format("{0:D18}", pIdInfo.ui64Id);
            myWChar2Utf8(str, idStr, size);
            return idStr;
        }
    }
};
