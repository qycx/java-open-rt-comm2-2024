


package com.qycx.base;

import static com.qycx.base.qyComm.Consts.CONST_qyCmd_tellService;
import static com.qycx.base.qyComm.Consts.bufSize_qyCommServiceData;
import static com.qycx.base.qyCommFunc.Consts.CONST_maxReqDataLen_enc_netMc;
import static com.qycx.base.qyCommonFunc9.qyFuncs.*;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyDefs.qyFuncs.MACRO_safeStrMemCpy;
import static com.qycx.base.tmpFunc.qyFuncs.mytoString;

import java.nio.ByteBuffer;

public class qyCommFunc {
	public static final String TAG = qyCommFunc.class.getSimpleName();

    public static class Consts {
        public final static int CONST_maxReqDataLen_enc_netMc = 16384;         //  2004/07/26,
        public final static int CONST_maxReqDataLen_enc_is = 1024 * 1024;    //  2008/03/20,
        //
        public final static int CONST_qnmReqBufSize_netMc = 32768;            //  netMc


    }

    public static class qyFuncs {
        //
        public static int qyTellService(int serviceId, byte[] version, Object pCommEncCtx, byte[] data, int dataLen, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, qyComm.QY_COMM_SESSION pSession, qyComm.QY_COMM_HEAD tmp_head,  ByteBuffer tmp_bb) {
            int iErr = -1;
            qyComm.QY_COMM_SERVICE service=new qyComm.QY_COMM_SERVICE();
            int len;

            //  Log.e(TAG,  "qyTellService(  ) enters."  );

            //  清空pSession->service
            qyComm.QY_COMM_SERVICE p = pSession.service;
            {
                //mymemset((byte[])p, 0, sizeof(qyComm.QY_COMM_SERVICE));
            }

            //  重新组织service
            //mymemset((byte[])service, ' ', sizeof(qyComm.QY_COMM_SERVICE));
//            service.serviceId = qyhtoni(serviceId);
//            MACRO_safeStrMemCpy(mytoString(version), service.version, CONST_qyMaxVerLen);
            //if (pCommEncCtx) service.encType = qyhtonl(((QY_ENC_CTX)pCommEncCtx)->type);
            //qyMemoryFunc.qyFuncs.mymemcpy(service.data.array(), data, (int) Math.min(dataLen, bufSize_qyCommServiceData));
            service.serviceId = serviceId;
            service.data = data;
//            service.version = 0;
            service.encType = 0;
            len = (int) ( Math.min(dataLen, bufSize_qyCommServiceData)); //
            byte[] dataByteArr = service.toArrayBuffer();
            if (0 != qySendReq(pSession, pSock, pTo, CONST_qyCmd_tellService,dataByteArr, len,tmp_head,  tmp_bb))
                return iErr;

            //  将service赋入pSession中
            service.serviceId = serviceId;          //

            //mymemcpy((byte[])pSession.service,service, sizeof(service));
            pSession.service = service;


            iErr = 0;

            

            //  Log.e(TAG,  "qyTellService(  ) leaves, %s",  iErr  ?  "失败"  :  "成功"  );

            return iErr;

        }


        public static int tmpqySendReq(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, boolean bResp, int cmdCode, byte[] data, int dataLen, qyComm.QY_COMM_HEAD tmp_head,  ByteBuffer tmp_bb) {
            int iErr = -1;
            qyComm.QY_COMM_HEAD tmpHead=tmp_head;//new qyComm.QY_COMM_HEAD();
            tmpHead.memset0();

            byte[] encBuf = null;
            int encLen = CONST_maxReqDataLen_enc_netMc;

            //mymemset((byte[])tmpHead, 0, sizeof(QY_COMM_HEAD));
//            tmpHead.usCode = qyhtons((short) cmdCode);
            if (null == data) dataLen = 0;

            if (0 != dataLen) {       //  2004/01/02里加入了加密的处理

            }

//            tmpHead.len = qyhtoni(dataLen);

            tmpHead.usCode = (short) cmdCode;
            tmpHead.len = dataLen;
            ByteBuffer buffer = tmpHead.toArrayBuffer(tmp_bb);
            if (0 >= guiShare.GuiShare.pf_qySend.call(pSock,buffer.array(), 0,  8,  pTo.iWrite))
                return iErr;
            if (0 != dataLen) {
                if (0 >= guiShare.GuiShare.pf_qySend.call(pSock, (data) , 0,  dataLen,  pTo.iWrite))
                    return iErr;
            }

            //  2007/04/25
            if (!bResp) pSession.usLastReqCode_o = cmdCode;
            else pSession.usLastRespCode_o = cmdCode;


            //
            iErr = 0;

            

//            if (null != encBuf) qyMemoryFunc.qyFuncs.myfree( encBuf);

            return iErr;

        }


        public static int qySendReq(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, int cmdCode, byte[] data, int dataLen, qyComm.QY_COMM_HEAD tmp_head,  ByteBuffer tmp_bb) {
            return tmpqySendReq(pSession, pSock, pTo, false, cmdCode, data, dataLen, tmp_head,  tmp_bb);
        }

    }
}
