
package com.qycx.base;

import static com.qycx.base.commonSock.qyFuncs.qyRecvEx;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_end;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_user;
import static com.qycx.base.qyComm.Consts.bufSize_qyCommServiceData;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommFunc.Consts.CONST_maxReqDataLen_enc_is;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_qwm;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_rsa1;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class qyMsgFunc {

    public static class QY_MSG_HEAD {
        public short usCode;
        public byte ucFlg;
        public byte reserved;
        public int uiLen;
        public long i64StartTime;
        public int uiTranNo;
        public short usSeqNo;
        public byte[] reserved1 = new byte[2];
    }


    public static class qyFuncs {

        public static ByteBuffer qyRecvMsg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo,qyMsgFunc.QY_MSG_HEAD pMsgHead) {
            int iErr = -1;
            byte[] encBuf = null;
            int encLen = CONST_maxReqDataLen_enc_is;

            ByteBuffer buf = ByteBuffer.allocate(24);
            int recv_size = qyRecvEx(pSock, buf, 24, pTo.iRead);
            if (recv_size < buf.capacity()) {
                Log.e("qy","recive_size:" + recv_size + "< " + buf.capacity());
                return null;
            }
            buf.flip();

            pMsgHead.usCode = buf.getShort();
            pMsgHead.ucFlg = buf.get();
            pMsgHead.reserved = buf.get();
            pMsgHead.uiLen = buf.getInt();
            pMsgHead.i64StartTime = buf.getLong();
            pMsgHead.uiTranNo = buf.getInt();
            pMsgHead.usSeqNo = buf.getShort();
            buf.get(pMsgHead.reserved1);

            //
            if  (  false  )  {
                Log.d("qy","qyRecvMsg: tn "  +  pMsgHead.uiTranNo  +  " msg.uiLen "  +  pMsgHead.uiLen);
            }



            //
            ByteBuffer buffer;
            if (pSession.service.encType == CONST_qyEncType_qwm) {
                Log.d("qy","qyRecvMsg failed, enc_qwm not supported");
                return null;
            } else {
                //校验数据长度的合法性
                if (pMsgHead.uiLen <= 0 || pMsgHead.uiLen > (qnmCommProc_mis.Consts.bufSize_imLStream + 1024))
                {
                    Log.e("qy","msg.uiLen = " + pMsgHead.uiLen + "  error length");
                    return null;
                }
                try{
                    buffer = ByteBuffer.allocate(pMsgHead.uiLen);
                }catch (Exception ex)
                {
                    Log.e("qy","invalid data length "+pMsgHead.uiLen);
                    return null;
                }
                if (qyRecvEx(pSock, buffer, pMsgHead.uiLen, pTo.iRead) < 0) {
                    buffer = null;
                    Log.e("qy","qyRecvEx failed");
                    return null;
                }
                buffer.flip();
            }

            if (isUcFlgResp(pMsgHead.ucFlg)) pSession.usLastRespCode_i = pMsgHead.usCode;
            else pSession.usLastReqCode_i = pMsgHead.usCode;

            return buffer;
        }

        //
        public static int qySendMsg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, byte ucFlg, int cmdCode, long lStartTime, int uiTranNo, int usSeqNo, byte[] data, int offset,  int dataLen) {
            int iErr = -1;
            QY_MSG_HEAD tmpHead = new QY_MSG_HEAD();
            byte[] encBuf = null;
            int encLen = CONST_maxReqDataLen_enc_is;
            tmpHead.usCode = (short) cmdCode;
            tmpHead.ucFlg = ucFlg;
            if (null == data) {
                dataLen = 0;
            }
            tmpHead.uiLen = dataLen;
            tmpHead.i64StartTime = lStartTime;
            tmpHead.uiTranNo = uiTranNo;
            tmpHead.usSeqNo = (short) usSeqNo;

            ByteBuffer head_buffer = ByteBuffer.allocate(24);
            head_buffer.putShort(tmpHead.usCode);
            head_buffer.put(tmpHead.ucFlg);
            head_buffer.put(tmpHead.reserved);
            head_buffer.putInt(tmpHead.uiLen);
            head_buffer.putLong(tmpHead.i64StartTime);
            head_buffer.putInt(tmpHead.uiTranNo);
            head_buffer.putShort(tmpHead.usSeqNo);
            head_buffer.put(tmpHead.reserved1);
            head_buffer.flip();
            int len = head_buffer.limit();
            if (guiShare.GuiShare.pf_qySend.call(pSock, head_buffer.array(), 0,len, pTo.iWrite) != len) {
                return iErr;
            }
            if (dataLen > 0) {
                try {
                    //ByteBuffer buffer = ByteBuffer.wrap(data, 0, dataLen);
                    if (guiShare.GuiShare.pf_qySend.call(pSock, data, offset,  dataLen, pTo.iWrite) != dataLen) {
                        return iErr;
                    }
                } catch (Exception e) {
                    Log.e(e.getLocalizedMessage());
                    return iErr;
                }
            } else {
                Log.e("data size must > 0 !");
                return -1;
            }
            iErr = 0;
            return iErr;
        }

        public static int qyTellService_msg(int serviceId, String version, qyEncFunc.QY_ENC_CTX pCommEncCtx, int usCmd, long tStartTime, int uiTranNo, ByteBuffer buf, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, qyComm.QY_COMM_SESSION pSession, String base64PublicKey) {
            Log.d("qyTellService_msg: ");
            int iErr = -1;
            int len;

            if (null == buf || buf.remaining() <= 0) return -1;
            qyComm.QY_COMM_SERVICE service = new qyComm.QY_COMM_SERVICE();

            service.serviceId = serviceId;
            service.version = version.getBytes();
            service.encType = pCommEncCtx.common.type;

            switch (service.encType) {
                case CONST_qyEncType_rsa1: {
                    qmCrypt.ENC_CTX_session pEncCtx = (qmCrypt.ENC_CTX_session) pCommEncCtx;

                    if (null == pEncCtx) return iErr;

                    buf.flip();
                    byte[] tmp_data = new byte[buf.limit()];
                    buf.get(tmp_data);
                    byte[] encData = RSAEncrypt.encrypt_byte(tmp_data, base64PublicKey);

                    service.data = encData;
                    len = encData.length;
                }
                break;
                default:
                    len = Math.min(buf.position(), bufSize_qyCommServiceData);
                    System.arraycopy(buf.array(), 0, service.data, 0, len);
                    break;
            }

            ByteBuffer service_buf = ByteBuffer.allocate(Integer.BYTES * 2 + CONST_qyMaxVerLen + len);
            service_buf.putInt(service.serviceId);
            service_buf.put(service.version);
            service_buf.putInt(service.encType);
            service_buf.put(service.data, 0, len);
            byte[] data = service_buf.array();
            Log.d(Consts.IO_TAG,"qyTellService_msg");
            if (qySendMsg(pSession, pSock, pTo, (byte) 0, usCmd, tStartTime, uiTranNo, 0, data, 0,  data.length) != 0)
                return iErr;

            // 将service赋入pSession中
            service.serviceId = serviceId;
            service.encType = pCommEncCtx.common.type;
            pSession.service = service;
            pSession.commEncCtx = pCommEncCtx;

            iErr = 0;
            return iErr;
        }


        //关闭会话
        public static void qnmCloseSession_msg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo) {
            Log.d("qnmCloseSession_msg");
            QY_MSG_HEAD tmpResp = new QY_MSG_HEAD();

            if (guiShare.GuiShare.pf_bQySockOpen.call(pSock)) {
                if (isRcOk(pSession.usLastRespCode_i) || pSession.usLastRespCode_i == CONST_qyRc_user) {
                    /*
                    if (0 == qySendMsg(pSession, pSock, pTo, (byte) 0, CONST_qyCmd_end, 0, 0, 0, null, 0)) {
                        qyRecvMsg(pSession, pSock, pTo, tmpResp);
                    }
                     */
                }
                //
                pSock.buf_r = null;
                pSock.buf_w = null;
                //
                guiShare.GuiShare.pf_qyDisconnect.call(pSock);
            }
            return;
        }
    }
}
