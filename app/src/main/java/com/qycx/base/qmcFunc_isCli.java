


package com.qycx.base;

import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_queryCustomerServiceOfficerReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_rtcCallReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferGpsInfo;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_content;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_propStr;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_subject;

public class qmcFunc_isCli {
    public static class qyFuncs {
        public static qnmCommProc_mis.IM_TASK_RCD msg2TaskRcd(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsg, int usRespCode, qnmCommProc_mis.IM_TASK_RCD pRcd) {
            int iErr = -1;
            qnmCommProc_mis.MIS_MSG_routeTalkData pTalkData = null;
            qnmCommProc_mis.IM_CONTENTU pContent = null;
            //char buf[255 + 1] = "";
            String str;

            if (null == pMisCnt || null == pMsg || null == pRcd) return null;

            pTalkData = pMsg.data;
            pContent = (qnmCommProc_mis.IM_CONTENTU) pTalkData.buf;// (qnmCommProc_mis.IM_CONTENTU) pTalkData.buf;

            if (null == pTalkData || null == pContent) return null;

            //mymemset(pRcd, 0, sizeof(IM_TASK_RCD));
            //
            //pRcd->misServName, pMisCnt.cfg.misServName, mycountof(pRcd->misServName));
            pRcd.idInfo_send.ui64Id = pMsg.idInfo_taskSender.ui64Id;
            pRcd.tSendTime = pMsg.tStartTime;
            pRcd.uiTranNo = pMsg.uiTranNo;
            pRcd.idInfo_recv.ui64Id = pMsg.idInfo_taskReceiver.ui64Id;
            //
            pRcd.uiContentType = pContent.uiType; //  2008/05/29
            //
            pRcd.tRecvTime = tmpFunc.qyFuncs.mytime();
            pRcd.usRespCode = usRespCode;
            //
            pRcd.id = pMsg.iTaskId;                   //  2010/04/23
            pRcd.uiMode = 0;
            pRcd.uiType = pMsg.uiTaskType;
            pRcd.iStatus = pMsg.iStatus;

            switch (pContent.uiType) {
                case CONST_imCommType_transferFileReq:
                    qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                    pRcd.subject = transferFileReq.fileName;//, cntof_subject);
                    pRcd.content = transferFileReq.fullFilePath;//, cntof_content);
                    //
                    //_sntprintf(pRcd->propStrs[0], mycountof(pRcd->propStrs[0]), ("%I64u"), pContent->transferFileReq.ui64FileLen);
                    str = String.valueOf(transferFileReq.ui64FileLen);
                    pRcd.propStr0 = str;//, cntof_propStr);
                    break;
                case CONST_imCommType_rtcCallReq:

                    break;
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    //
                    //_sntprintf(pRcd->subject, mycountof(pRcd->subject), ("%d * %d"), pContent->transferAvInfo.tranInfo.video.vh_decompress.bih.biWidth, pContent->transferAvInfo.tranInfo.video.vh_decompress.bih.biHeight);
                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    str = String.format("%d*%d", transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth, transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight);
                    pRcd.subject = str;// cntof_subject);
                    //
                    break;
                case CONST_imCommType_queryCustomerServiceOfficerReq:       //  2011/04/06
                    break;
                case CONST_imCommType_transferGpsInfo:                      //  2012/04/19
                    break;
                default:
                    return null;
            }

            return pRcd;
        }

        public static qnmCommProc_mis.IM_TASK_RCD msg2TaskRcd(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_TASK pMsg, int usRespCode, qnmCommProc_mis.IM_TASK_RCD pRcd) {
            int iErr = -1;
            qnmCommProc_mis.MIS_MSG_routeTalkData pTalkData = null;
            qnmCommProc_mis.IM_CONTENTU pContent = null;
            //char buf[255 + 1] = "";
            String str;

            if (null == pMisCnt || null == pMsg || null == pRcd) return null;

            pTalkData = pMsg.data;
            pContent = (qnmCommProc_mis.IM_CONTENTU) pTalkData.buf;

            if (null == pTalkData || null == pContent) return null;

            pRcd.idInfo_send.ui64Id = pMsg.idInfo_taskSender.ui64Id;
            pRcd.tSendTime = pMsg.tStartTime;
            pRcd.uiTranNo = pMsg.uiTranNo;
            pRcd.idInfo_recv.ui64Id = pMsg.idInfo_taskReceiver.ui64Id;
            pRcd.uiContentType = pContent.uiType; //  2008/05/29
            pRcd.tRecvTime = tmpFunc.qyFuncs.mytime();
            pRcd.usRespCode = usRespCode;
            pRcd.id = pMsg.iTaskId;                   //  2010/04/23
            pRcd.uiMode = 0;
            pRcd.uiType = pMsg.uiTaskType;
            pRcd.iStatus = pMsg.iStatus;

            switch (pContent.uiType) {
                case CONST_imCommType_transferFileReq:
                    qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                    pRcd.subject = transferFileReq.fileName;//, cntof_subject;
                    pRcd.content = transferFileReq.fullFilePath;//, cntof_content;
                    pRcd.propStr0 = String.valueOf(transferFileReq.ui64FileLen);// cntof_propStr);
                    break;
                case CONST_imCommType_rtcCallReq:
                    //
                    //ulIp2Str(pContent->rtcCallReq.ulIp_caller, buf, mycountof(buf));
                    //myStr2TChar(buf, pRcd->subject, mycountof(pRcd->subject));
                    //
                    break;
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    //
                    //_sntprintf(pRcd->subject, mycountof(pRcd->subject), ("%d * %d"), pContent->transferAvInfo.tranInfo.video.vh_decompress.bih.biWidth, pContent->transferAvInfo.tranInfo.video.vh_decompress.bih.biHeight);
                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    str = String.format("%d*%d", transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth, transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight);
                    pRcd.subject = str;// cntof_subject);
                    //
                    break;
                case CONST_imCommType_queryCustomerServiceOfficerReq:       //  2011/04/06
                    break;
                case CONST_imCommType_transferGpsInfo:                      //  2012/04/19
                    break;
                default:
                    return null;
            }


            iErr = 0;

            return iErr != 0 ? null : pRcd;
        }

    }
}
