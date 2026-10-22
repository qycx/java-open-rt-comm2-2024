
package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.genericQueue.qyFuncs.isQEmpty;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.getTalkerDesc.qyFuncs.getMsgDesc;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_getCfgsReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_htmlContent;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_lStream;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_messengerAccount;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveContactListReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveContactListResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImGrpListReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImGrpListResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImGrpMemListReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImGrpMemListResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImObjListEndReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveImObjListEndResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_retrieveToPaths;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_simpleResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.imObj_proc.qyFuncs.postImObjToIntQ;
import static com.qycx.base.imObj_proc.qyFuncs.triggerToDisplayImObjIntQ;
import static com.qycx.base.isTmpFunc.qyFuncs.printRetrieveContactListResp;
import static com.qycx.base.isTmpFunc.qyFuncs.printRetrieveImGrpListResp;
import static com.qycx.base.isTmpFunc.qyFuncs.printRetrieveImGrpMemListResp;
import static com.qycx.base.logImMsg_isCli.qyFuncs.logImMsg;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_getCfgs;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_refreshImObjListReq;
import static com.qycx.base.qnmCommProc.qyFuncs.CONST_fieldIdTable_en;
import static com.qycx.base.qnmCommProc1.qyFuncs.bObjDbAvail;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_contact;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_grp;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_grpMem;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_imGrpMem_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_imLStream;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_retrieveContaceMem_mems;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.retrieveImObjListReq2Stream;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs.Consts.CONST_objType_me;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.updateMeDisplayInfo;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_allContactsRetrieved;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_showHint;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.util.Log;

public class qyMcMainDaemon_isCli_scheduler {
//    private static final String TAG = qyMcMainDaemon_isCli_scheduler.class.getSimpleName();

    public static class qyFuncs {


        public static boolean bRefreshAllContacts(qmCommFunc_mis.MIS_CNT pMisCnt) {
            return 0 == pMisCnt.refreshContactList.tLastRefreshedTime_misServ;
        }

        public static boolean bMeInfoNeedReg(Object pDb, int iDbType, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo) {
            return false;

        }


        static int mcProcResp_getCfgs(qyMtSock.MT_SUBTHREADINFO pSubThreadInfoParam, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            qnmCommProc_mis.IM_CONTENTU pContent = null;

            //TCHAR tBuf[255 + 1] = "";

            if (isUcFlgRouteTalkData(pMsgInput.ucFlg)) return iErr;
            if (isUcFlgTalkData(pMsgInput.ucFlg)) return iErr;
            pContent = pMsgInput.data.buf;

            if (null == pContent) return iErr;

            switch (pContent.uiType) {
                //  case  CONST_imCommType_intervalParams:
                case CONST_imCommType_getCfgsReq:       //  2011/01/30
                    qnmCommProc_mis.QIS_getCfgs_req getCfgsReq = (qnmCommProc_mis.QIS_getCfgs_req) pContent;
                    if (getCfgsReq.uiSizePerSnd_media != 0
                            && getCfgsReq.uiSizePerSnd_media != pMisCnt.cfg.uiSizePerSnd_media) {
                        pMisCnt.cfg.uiSizePerSnd_media = Math.min(pProcInfo.cfg.uiSizePerSnd_media, getCfgsReq.uiSizePerSnd_media);
                    }

                    pMisCnt.cfg.usIntervalInS_refreshRecentFriends = (short) Math.max((int) 2, getCfgsReq.intervalParams.usIntervalInS_refreshRecentFriends);
                    pMisCnt.cfg.usIntervalInS_refreshContactList = (short) Math.max((int) 2, getCfgsReq.intervalParams.usIntervalInS_refreshContactList);

                    //  2015/07/28

                    //
                    pMisCnt.bIntervalParamsGot = true;
                    break;
                default: {
                    String str = "mcProcResp_getCfgs err: unprocessed uiContentType ( " + pContent.uiType + " )";
                    Log.e(str);
                }
                return iErr;
            }

            iErr = 0;


            return iErr;

        }


        public static int mcProcResp_refreshImGrps(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;

            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            if (null == pQyMc) return -1;

            for (int i = 0; i < pResp.usCnt; i++) {
                if (0 != pProcInfo.recoverImGrp(pQyMc.p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, CONST_objType_imGrp, pResp.mems[i], pMisCnt.refreshContactList.tStartTime, false)) {
                    Log.e("recoverImGrp( " + i + " ) failed");
                    return iErr;
                }
                postImObjToIntQ(pResp.mems[i].idInfo);
            }
            triggerToDisplayImObjIntQ();

            iErr = 0;
            return iErr;

        }

        public static int mcProcResp_refreshImGrps_end(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (bRefreshAllContacts(pMisCnt)) {
                if (0 != myDb.QM_dbFuncs.pf_updateImObj_refreshImGrps_end.call(pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType,
                        "", 0, pMisCnt.refreshContactList.tStartTime)) {
                    Log.e("updateImObj_refreshImGrps_end failed");
                    return iErr;
                }
            }
            iErr = 0;
            return iErr;
        }

        public static int mcProcResp_refreshImGrpMems(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtxParam) {
            int iErr = -1;

            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            for (int i = 0; i < pResp.usCnt; i++) {
                qnmCommProc_mis.IM_GRP_MEM pMem = getMem.qyFuncs.getMem(pResp.mems, bufSize_imGrpMem_mems, i);
                if (0 != pProcInfo.recoverImGrpMem(pQyMc.p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, pMem, pMisCnt.refreshContactList.tStartTime)) {
                    Log.e("recoverImGrpMem(  ) failed");
                    return iErr;
                }
            }


            iErr = 0;

            return iErr;
        }

        public static int mcProcResp_refreshImGrpMems_end(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;

            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;

            if (bRefreshAllContacts(pMisCnt)) {
                if (0 != myDb.QM_dbFuncs.pf_updateImGrpMem_refreshImGrpMems_end.call(pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, "", pMisCnt.refreshContactList.tStartTime)) {
                    Log.e("pf_updateImGrpMem_refreshImGrpMems_end failed");
                    return iErr;
                }
            }

            iErr = 0;
            return iErr;
        }


        // 	
        public static int mcProcResp_refreshContacts(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            myDb.QM_dbFuncs p_g_dbFuncs = pQyMc.p_g_dbFuncs;

            for (int i = 0; i < pResp.usCnt; i++) {

                qnmCommProc_mis.RETRIEVE_contact_mem pMem = getMem.qyFuncs.getMem(pResp.mems, bufSize_retrieveContaceMem_mems, i);
                if (pMem == null) return iErr;

                qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
                messengerInfo.misServName = pMem.regInfo.addr.misServName;
                messengerInfo.idInfo.ui64Id = pMem.regInfo.addr.idInfo.ui64Id;
                messengerInfo.uiType = pMem.regInfo.addr.uiObjType;
                messengerInfo.iStatus = 0;
                messengerInfo.iRole = pMem.iRole;

                if (pMem.regInfo.addr.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                    messengerInfo.uiType = CONST_objType_me;
                }

                if (0 != pProcInfo.recoverMessenger(p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, messengerInfo, pMem.regInfo, pMisCnt.refreshContactList.tStartTime, false, pProcInfo.schedulerQ)) {
                    Log.e("recoverMessenger(  ) failed");
                    return iErr;
                }

                if (pMem.regInfo.addr.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                    updateMeDisplayInfo(pProcInfo, pMisCnt, pMem.regInfo, null, null);
                }
                postImObjToIntQ(pMem.regInfo.addr.idInfo);
            }
            triggerToDisplayImObjIntQ();

            iErr = 0;
            return iErr;
        }

        //  2013/02/02. 要注意,要清理的时候要把me排除在外.因为me的更新在startchannel里也有.
        public static int mcProcResp_refreshContacts_end(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP pResp, ctxMcThread.CTX_MC_thread pCtxParam) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            //
            if (bRefreshAllContacts(pMisCnt)) {
                if (0 != myDb.QM_dbFuncs.pf_updateImObj_refreshContacts_end.call(pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, "", pMisCnt.refreshContactList.tStartTime))
                    return iErr;
            }
            iErr = 0;
            return iErr;
        }


        static int mcProcResp_retrieveImObjListReq(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            //  
            qnmCommProc_mis.IM_CONTENTU pContent = null;

            qnmCommProc_mis.RETRIEVE_IMOBJLIST_REQ req = new qnmCommProc_mis.RETRIEVE_IMOBJLIST_REQ();
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;

            qnmCommProc_mis.IM_L_STREAM pStream = new qnmCommProc_mis.IM_L_STREAM();
            int len;

            if (isUcFlgRouteTalkData(pMsgInput.ucFlg)) return -1;
            pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
            if (null == pContent) return -1;

            pMisCnt.refreshContactList.dwLastTickCnt_respRecvd = tmpFunc.qyFuncs.myGetTickCount(null);

            if (pContent.uiType == CONST_imCommType_retrieveImGrpListResp) {

                qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP pResp = (qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP) pContent;
                int uiObjType = CONST_objType_imGrp;

                if (pResp.usCnt > CONST_maxContactUpdateInfos_grp) return iErr;

                printRetrieveImGrpListResp(pResp);

                if (pResp.usCnt != 0) {
                    String str = pResp.usCnt + " groups retrieved, tn " + pMsgInput.uiTranNo;
                    Log.i(str);
                    showInfo_open.qyFuncs.showInfo_open(str);

                    //
                    if (0 != mcProcResp_refreshImGrps(pSubThreadInfo, pMisCnt, pResp, pCtx)) {
                        Log.e("mcProcResp_refreshImGrps failed");
                        return iErr;
                    }
                    pProcInfo.status.retrieveImObjList.ulbImGrpExists = true;
                }

                if (0 == pResp.ucbEnd) {

                    if (0 == pResp.usCnt) return iErr;

                    //  发送请求以获取剩余的分组信息
                    //
                    req.uiType = CONST_imCommType_retrieveImGrpListReq;
                    req.ucbNext = 1;
                    req.idInfo_lastRefreshed.ui64Id = pResp.mems[pResp.usCnt - 1].idInfo.ui64Id;

                    pStream.uiType = CONST_imCommType_lStream;
                    pStream.uiContentType = req.uiType;
                    len = bufSize_imLStream;   // sizeof(pStream->buf  );
                    if (0 != retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf)) {
                        Log.e("retrieveImObjListReq2Stream failed");
                        return iErr;
                    }
                    pStream.buf.flip();
                    pStream.ulStreamLen = len;
                    len += 12; //(int)Marshal.OffsetOf<IM_L_STREAM>("buf");

                    if (0 != pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false))
                        return iErr;

                    iErr = 0;
                    return iErr;
                }

                if (0 != mcProcResp_refreshImGrps_end(pSubThreadInfo, pMisCnt, pResp, pCtx))
                    return iErr;

                String str = "    retrieveGrps end,  " + pMsgInput.uiTranNo;
                showInfo_open.qyFuncs.showInfo_open( str);

                boolean bNeedRetrieveImGrpMems = false;
                //
                bNeedRetrieveImGrpMems = myDb.QM_dbFuncs.pf_bNeedRetrieveImGrpMems.call(pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, "");
                if (!bNeedRetrieveImGrpMems) {
                    req.uiType = CONST_imCommType_retrieveContactListReq;
                    req.tLastRefreshedTime_contactList_misServ = pMisCnt.refreshContactList.tLastRefreshedTime_misServ;
                    req.usMaxContactUpdateInfosPerSnd = pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_contact;         //  2014/02/06
                } else {
                    req.uiType = CONST_imCommType_retrieveImGrpMemListReq;
                    req.tLastRefreshedTime_contactList_misServ = pMisCnt.refreshContactList.tLastRefreshedTime_misServ;
                    req.usMaxContactUpdateInfosPerSnd = pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_grpMem;          //  2014/02/06
                }

                pStream.uiType = CONST_imCommType_lStream;
                pStream.uiContentType = req.uiType;
                len = bufSize_imLStream;// sizeof(pStream->buf  );
                if (0 > (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf)))
                    return iErr;
                pStream.buf.flip();
                pStream.ulStreamLen = (int) len;
                len += 12;//(int)Marshal.OffsetOf<IM_L_STREAM>("buf");

                if (0 != pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false))
                    return iErr;

                iErr = 0;
                return iErr;
            }

            if (pContent.uiType == CONST_imCommType_retrieveImGrpMemListResp) {

                qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP pResp = (qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP) pContent;
                qnmCommProc_mis.IM_GRP_MEM[] pMems = pResp.mems;

                if (pResp.usCnt > CONST_maxContactUpdateInfos_grpMem) return iErr;

//                printRetrieveImGrpMemListResp(pResp);

                if (0 != pResp.usCnt) {
                    String str = pResp.usCnt + " grpMems retrieved, tn " + pMsgInput.uiTranNo;
                    Log.i(str);
                    showInfo_open.qyFuncs.showInfo_open(str);

                    //
                    if (0 == pResp.usCnt) {
                        Log.e("no grpMem, resp error");
                        return iErr;
                    }
                    if (0 != mcProcResp_refreshImGrpMems(pSubThreadInfo, pMisCnt, pResp, pCtx)) {
                        Log.e("mcProcResp_refreshImGrpMems failed");
                        return iErr;
                    }
                }

                if (0 == pResp.ucbEnd) {
                    if (0 == pResp.usCnt) return iErr;
                    //  发送请求以获取剩余的分组成员信息
                    req.uiType = CONST_imCommType_retrieveImGrpMemListReq;
                    req.ucbNext = 1;
                    req.idInfo_grp_lastRefreshed.ui64Id = pMems[pResp.usCnt - 1].idInfo_grp.ui64Id;
                    req.idInfo_lastRefreshed.ui64Id = pMems[pResp.usCnt - 1].idInfo_mem.ui64Id;
                    //
                    pStream.uiType = CONST_imCommType_lStream;
                    pStream.uiContentType = req.uiType;
                    len = bufSize_imLStream;// sizeof(pStream->buf  );
                    if (0 != (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf))) {
                        Log.e("retrieveImObjListReq2Stream failed");
                        return iErr;
                    }
                    pStream.buf.flip();
                    pStream.ulStreamLen = len;
                    len += 0;

                    if (0 != pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, (qnmCommProc_mis.MSG_ROUTE) null, CONST_misMsgType_req, (byte) 0, (short) CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, (int) len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false))
                        return iErr;

                    iErr = 0;
                    return iErr;
                }

                if (0 != mcProcResp_refreshImGrpMems_end(pSubThreadInfo, pMisCnt, pResp, pCtx)) {
                    Log.e("mcProcResp_refreshImGrpMems_end faliled");
                    return iErr;
                }

                String str = "retrieveGrpMems end, " + pMsgInput.uiTranNo;
                Log.i(str);
                showInfo_open.qyFuncs.showInfo_open(str);

                //
                req.uiType = CONST_imCommType_retrieveContactListReq;
                req.tLastRefreshedTime_contactList_misServ = pMisCnt.refreshContactList.tLastRefreshedTime_misServ;
                req.usMaxContactUpdateInfosPerSnd = pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_contact;     //  2014/02/06

                pStream.uiType = CONST_imCommType_lStream;
                pStream.uiContentType = req.uiType;
                len = bufSize_imLStream; // sizeof(pStream->buf  );
                if (0 > (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf))) {
                    Log.e("retrieveImObjListReq2Stream failed");
                    return iErr;
                }
                pStream.buf.flip();
                pStream.ulStreamLen = len;
                len += 12;//offsetof(  IM_L_STREAM,  buf  ); 

                if (0 != pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false)) {
                    return iErr;
                }

                iErr = 0;
                return iErr;
            }

            if (pContent.uiType == CONST_imCommType_retrieveContactListResp) {

                qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP pResp = (qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP) pContent;

                if (pResp.usCnt > CONST_maxContactUpdateInfos_contact) return iErr;

//                printRetrieveContactListResp(pResp);

                if (0 != pResp.usCnt) {
                    {
                        String str = pResp.usCnt + " contacts retrieved, tn " + pMsgInput.uiTranNo;
                        Log.e(str);
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }

                    //
                    if (0 != mcProcResp_refreshContacts(pSubThreadInfo, pMisCnt, pResp, pCtx))
                        return iErr;

                    pProcInfo.status.retrieveImObjList.ulbContactExists = true;
                }

                if (0 == pResp.ucbEnd) {

                    if (0 == pResp.usCnt) return iErr;

                    //  发送请求以获取剩余的contact
                    //
                    req.uiType = CONST_imCommType_retrieveContactListReq;
                    req.ucbNext = 1;
                    req.idInfo_lastRefreshed.ui64Id = pResp.mems[pResp.usCnt - 1].regInfo.addr.idInfo.ui64Id;

                    pStream.uiType = CONST_imCommType_lStream;
                    pStream.uiContentType = req.uiType;
                    len = bufSize_imLStream; // sizeof(pStream->buf  );
                    if (0 > (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf)))
                        return iErr;
                    pStream.buf.flip();
                    pStream.ulStreamLen = len;
                    len += 0;

                    if (0 > pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false))
                        return iErr;

                    iErr = 0;
                    return iErr;
                }

                if (0 > mcProcResp_refreshContacts_end(pSubThreadInfo, pMisCnt, pResp, pCtx))
                    return iErr;

                //
                String str = "retrieveContaces end, " + pMsgInput.uiTranNo;
                Log.i(str);
                showInfo_open.qyFuncs.showInfo_open(str);

                //
                req.uiType = CONST_imCommType_retrieveImObjListEndReq;

                pStream.uiType = CONST_imCommType_lStream;
                pStream.uiContentType = req.uiType;
                len = bufSize_imLStream; // sizeof(pStream->buf  );
                if (0 > (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf)))
                    return iErr;
                pStream.buf.flip();
                pStream.ulStreamLen = len;
                len += 12;

                if (0 > pProcInfo.postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, pMsgInput.tStartTime, pMsgInput.uiTranNo, 0, pStream, len, 0, 0, CONST_channelType_talking, pCtx.pMsgBuf, false))
                    return iErr;

                iErr = 0;
                return iErr;
            }

            if (pContent.uiType == CONST_imCommType_retrieveImObjListEndResp) {
                qnmCommProc_mis.RETRIEVE_IMOBJLISTEND_RESP pResp = (qnmCommProc_mis.RETRIEVE_IMOBJLISTEND_RESP) pContent;
                //  2014/02/10
                boolean bRetrieveAll = bRefreshAllContacts(pMisCnt);

                //  tLastRefreshedTime_misServ, 
                pMisCnt.refreshContactList.tLastRefreshedTime_misServ = pResp.tLastRefreshedTime_contactList_misServ;
                {
                    String str = bRetrieveAll ? ("All im objects retrieved. ") : ("ImObjList retrieved");
                    showInfo_open.qyFuncs.showInfo_open( str);
                }
                pProcInfo.status.retrieveImObjList.ulbAllContactsRetrieved = true;
                if (bRetrieveAll) {
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_allContactsRetrieved, 0);
                }
                iErr = 0;
                return iErr;
            }

            String str = "mcProcResp_retrieveImObjListReq: unprocessed uiContentType ( " + ((qnmCommProc_mis.IM_L_STREAM) pContent).uiContentType + " )";
            Log.i(str);
            return iErr;

        }


        public static int procMsgInput_scheduler_req(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_input, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;

            iErr = 0;


            if (0 != iErr) {
            }


            return iErr;
        }

        //
        public static int procMsgInput_scheduler_resp(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_input, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;

            qnmCommProc_mis.IM_CONTENTU pContent = null;
            //TCHAR sqlBuf[CONST_maxSqlBufLen + 1] = "";
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            //
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pMisCnt) return -1;

            if (!isUcFlgResp(pMsgInput.ucFlg)) {
                Log.i("procMsgInput_scheduler_resp failed, not resp");
                return iErr;
            }
            if (!pMsgInput.ucbConvrted) {
                Log.i("procMsgInput_scheduler_resp: not converted");
                return iErr;
            }
            if (isUcFlgRouteTalkData(pMsgInput.ucFlg) || isUcFlgTalkData(pMsgInput.ucFlg)) {
                Log.i("procMsgInput_scheduler_resp: not addrTalkData or talkData");
                return iErr;
            }
            pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgInput.data.buf;

            switch (pContent.uiType) {
                case CONST_imCommType_simpleResp:
                    switch (((qnmCommProc_mis.IM_SIMPLE_RESP) pContent).uiContentType_org) {
                        case CONST_imCommType_messengerAccount:
                            Log.i("procMsgInput_scheduler: resp, messengerAccount");

                            //notifyProgressEnd(pMsgInput->uiTranNo, 0);
                            guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_showHint, 0);
                            //
                            break;
                    }
                    break;
                case CONST_imCommType_retrieveImGrpListResp:
                case CONST_imCommType_retrieveImGrpMemListResp:
                case CONST_imCommType_retrieveContactListResp:
                case CONST_imCommType_retrieveImObjListEndResp:
                    if (0 != mcProcResp_retrieveImObjListReq(pSubThreadInfo, pMisCnt, pMsgInput, pCtx))
                        return iErr;
                    break;

                //  case  CONST_imCommType_intervalParams:
                case CONST_imCommType_getCfgsReq:
                case CONST_imCommType_retrieveToPaths:
                    if (0 != mcProcResp_getCfgs(pSubThreadInfo, pMisCnt, pMsgInput, pCtx))
                        return iErr;
                    break;
                /**/

                //
                default: {
                    String str;
                    str = String.format("procMsgInput_scheduler_resp: unprocessed contentType, %s", pContent.uiType);

                }
                break;

            }

            iErr = 0;


            if (0 != iErr) {
            }


            return iErr;
        }

        public static int testSendMsg(ctxQyMc.CCtxQyMc pQyMc) {
            int loopCtrl = 0;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

            return 0;       //  2007/05/12


//            pChannel = getChannelByType(pMisCnt, CONST_channelType_talking);
//            if (pChannel == null) return -1;
//
//            for (; loopCtrl < 3000;)
//            {
//
//                if (pProcInfo.mgrQ.uiQNodes > 90)
//                {
//                    Log.i("¶ÔÁÐÖÐÊý¾ÝÌ«¶àÁË£¬ÒªµÄµÈÒ»»áÔÙ¼Ó");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    continue;
//                }
//
//                /**/
//
//                {
//
//                    String str;
//                    str = String.format("hehe %s times", loopCtrl);
//                    qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096 buf4096=new qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096();
//                    myWChar2Utf8(str, buf4096.buf.array(), 4096);
//
//
//                    loopCtrl++;
//
//                    //  ÔÚÕâÀï,¿ÉÒÔ³é¿Õ×öÐ©ÐèÒªÖÜÆÚÐÔ×öµÄÊÂÇé, ±ÈÈçÒªÇó·þÎñÆ÷¸üÐÂÓÃ»§ÐÅÏ¢
//                    //Log.e("testSendMsg: ", str);
//
//                    //  test,  ÕâÀïÓ¦¸ÃÏÈÅÐ¶ÏÊÇ·ñ
//                    qyDefs.QY_MESSENGER_ID dst=new qyDefs.QY_MESSENGER_ID();
//                    dst.ui64Id = 105;
//                    int uiTranNo=0;
//                    uiTranNo = getuiNextTranNo(null, 0, null);
//                    if (uiTranNo != -1)
//                    {
//                        //postMsg2Mgr_mc(  pMisCnt,  null,  CONST_misMsgType_req,  0,  CONST_qyCmd_talkTo,  0,  uiTranNo,  0,  buf4096.buf,  (int)mystrlen(  buf4096.buf  ),  null,  &dst,  0, null, false  );  //  test
//                    }
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//
//
//                }
//
//            }
//
//
//            //
//            return 0;

        }

        public static int procImMsgLogQ_scheduler(qyMtSock.MT_SUBTHREADINFO pSubThreadInfo, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSGU pMsg) {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo->pQyMc;
            int i;
            int len;

            qnmCommProc_mis.IM_MSG_RCD rcd = new qnmCommProc_mis.IM_MSG_RCD();
            qnmCommProc_mis.IM_CONTENTU pContent;

            for (i = 0; i < 3; i++) {

                if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pProcInfo.imMsgLogQ))) break;

                switch (pMsg.uiType) {
                    case CONST_misMsgType_talk:
                        pContent = null;//(qnmCommProc_mis.IM_CONTENTU)talk.data.buf;

                        if (pContent.uiType == CONST_imCommType_htmlContent) {
                            qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                            rcd.idInfo_send.ui64Id = talk.data.route.idInfo_from.ui64Id;
                            rcd.tSendTime = talk.tStartTime;
                            rcd.uiTranNo = talk.uiTranNo;
                            //  uiSeqNo
                            //
                            rcd.idInfo_logicalPeer = talk.addr_logicalPeer.idInfo;
                            //
                            rcd.idInfo_recv.ui64Id = talk.data.route.idInfo_to.ui64Id;
                            //
                            rcd.uiType = pContent.uiType;
                            rcd.logMsgContent = ((qnmCommProc_mis.IM_HTML_CONTENT) pContent).wBuf;//, cntof_logMsgContent);
                            rcd.tRecvTime = talk.tRecvTime;
                            rcd.firstTime = tmpFunc.qyFuncs.mytime();
                            rcd.lastTime = tmpFunc.qyFuncs.mytime();
                            rcd.tLastModifiedTime = tmpFunc.qyFuncs.mytime();

                            logImMsg(pQyMc.p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, rcd);

                        }

                        break;
                    case CONST_misMsgType_task:
                        pContent = null;// (qnmCommProc_mis.IM_CONTENTU)task.data.buf;

                        if (true) {
                            qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                            rcd.idInfo_send.ui64Id = task.data.route.idInfo_from.ui64Id;
                            rcd.tSendTime = task.tStartTime;
                            rcd.uiTranNo = task.uiTranNo;
                            //  uiSeqNo
                            //
                            rcd.idInfo_logicalPeer = task.addr_logicalPeer.idInfo;
                            rcd.iTaskId = task.iTaskId;
                            //
                            rcd.idInfo_recv.ui64Id = task.data.route.idInfo_to.ui64Id;
                            //
                            rcd.uiType = pContent.uiType;
                            //  lstrcpyn(  rcd.content,  pContent->html.wBuf,  mycountof(  rcd.content  )  );
                            switch (rcd.uiType) {
                                case CONST_imCommType_transferFileReq:
                                    qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                                    rcd.logMsgContent = transferFileReq.fileName;// cntof_logMsgContent);
                                    break;
                                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    if (0 != transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth && 0 != transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight) {
                                        //_sntprintf(rcd.content, mycountof(rcd.content), ("%d * %d"), pContent->transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth, pContent->transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            rcd.tRecvTime = task.tRecvTime;
                            rcd.firstTime = tmpFunc.qyFuncs.mytime();
                            rcd.lastTime = tmpFunc.qyFuncs.mytime();
                            rcd.tLastModifiedTime = tmpFunc.qyFuncs.mytime();

                            logImMsg(pQyMc.p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, rcd);
                        }
                        break;
                    //
                    case CONST_misMsgType_input:
                        qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                        if (!isUcFlgTalkData(input.ucFlg) && !isUcFlgRouteTalkData(input.ucFlg))
                            break;
                        //
                        pContent = null;// (qnmCommProc_mis.IM_CONTENTU)M_getMsgContent(input.ucFlg, input.data);


                        if (true) {
                            //mymemset((byte[])rcd, 0, sizeof(IM_MSG_RCD));
                            rcd.idInfo_send.ui64Id = input.data.route.idInfo_from.ui64Id;
                            rcd.tSendTime = input.tStartTime;
                            rcd.uiTranNo = input.uiTranNo;
                            //  uiSeqNo
                            //
                            rcd.idInfo_logicalPeer = input.addr_logicalPeer.idInfo;
                            rcd.iTaskId = input.iTaskId;
                            //
                            rcd.idInfo_recv.ui64Id = input.data.route.idInfo_to.ui64Id;
                            //
                            rcd.uiType = pContent.uiType;
                            //  lstrcpyn(  rcd.content,  pContent->html.wBuf,  mycountof(  rcd.content  )  );
                            switch (rcd.uiType) {
                                case CONST_imCommType_transferFileReq:
                                    //rcd.content, pContent->transferFileReq.fileName, cntof_log_msgContentLen);
                                    break;
                                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    if (0 != transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth && 0 != transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight) {
                                        //_sntprintf(rcd.content, mycountof(rcd.content), ("%d * %d"), pContent->transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth, pContent->transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            rcd.logMsgContent = getMsgDesc(pMsg);//, cntof_logMsgContent);
                            //
                            rcd.tRecvTime = input.tRecvTime;
                            rcd.firstTime = tmpFunc.qyFuncs.mytime();
                            rcd.lastTime = tmpFunc.qyFuncs.mytime();
                            rcd.tLastModifiedTime = tmpFunc.qyFuncs.mytime();

                            logImMsg(pQyMc.p_g_dbFuncs, pSubThreadInfo.pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, rcd);
                        }
                        break;


                    default:
                        //Log.e(("scheduler: unprocessed msgType %d"), pMsg->uiType);
                        break;

                }

            }


            iErr = 0;

            return iErr;
        }


        //
        public static Runnable mcThreadProc_mis_scheduler = () ->
        {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            qnmCommProc_mis.MIS_MSGU pMsg = null;
            int len = 0;

            qyMtSock.MT_SUBTHREADINFO subThreadInfo;
            long dwCurTickCnt = 0;

            ctxMcThread.CTX_MC_scheduler ctx;

            long tStartTran = 0;
            int uiTranNo = 0;

            //
            ctxCaller.CTX_caller tmp_ctxCaller  =  new ctxCaller.CTX_caller();


            //
            Log.d("mcThreadProc_mis_scheduler:  enters");



            //
            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
            {
                subThreadInfo = new qyMtSock.MT_SUBTHREADINFO();
                ctx = new ctxMcThread.CTX_MC_scheduler();
                ctx.pMsgBuf_forInput = new qnmCommProc_mis.CMIS_MSGU();
                ctx.pMsgBuf = new qnmCommProc_mis.CMIS_MSGU();


                if (!bObjDbAvail(pQyMc.procedObjDb)) {
                    pProcInfo.status.threadsStatus.ulbSchedulerQuit = true;
                    Log.i("mcThreadProc_mis_scheduler:  leaves");
                    return;
                }
                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) {
                    pProcInfo.status.threadsStatus.ulbSchedulerQuit = true;
                    Log.i("mcThreadProc_mis_scheduler:  leaves");
                    return;
                }

                subThreadInfo.pParentParam = pQyMc;
                subThreadInfo.iServiceId = pQyMc.iServiceId;
                subThreadInfo.pDb = pQyMc.gui.pDb;  // db.m_pDbMem->pDb;


                for (; !pProcInfo.bQuit_threadProc_isCli; ) {

//                    myWaitOne(pProcInfo.schedulerQ.hEvent, 1000);
                    myWaitOne(pProcInfo.schedulerQ.hSemaTrigger, 1000);

                    for (; !pProcInfo.bQuit_threadProc_isCli; ) {
                        pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pProcInfo.schedulerQ);
                        if (pMsg == null) {
                            break;
                        }
                        switch (pMsg.uiType) {
                            case CONST_misMsgType_input:
                                qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                                if (!isUcFlgResp(input.ucFlg)) {
                                    procMsgInput_scheduler_req(subThreadInfo, pProcInfo, pMisCnt, input, len, ctx);
                                } else {
                                    procMsgInput_scheduler_resp(subThreadInfo, pProcInfo, pMisCnt, input, len, ctx);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    //  to log imMsg in db
                    if (!isQEmpty(pProcInfo.imMsgLogQ)) {
                        procImMsgLogQ_scheduler(subThreadInfo, pProcInfo, pMsg);
                    }
                    dwCurTickCnt = tmpFunc.qyFuncs.myGetTickCount64(null);

                    qmCommFunc_mis.MIS_CHANNEL pChannel = null;
                    if (null == (pChannel = getChannelByType(pMisCnt, CONST_channelType_talking)))
                        continue;

                    if (!bChannelOk(pChannel,false)) continue;

                    if (!pMisCnt.bIntervalParamsGot && dwCurTickCnt > pMisCnt.dwLastTickCnt_getIntervalParams + pMisCnt.cfg.usIntervalInS_getIntervalParams * 1000) {

                        tStartTran = tmpFunc.qyFuncs.mytime();
                        uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();


                        //
                        tmp_ctxCaller.m_bDbg  = true;


                        //
                        qnmCommProc_mis.QIS_getCfgs_req req = new qnmCommProc_mis.QIS_getCfgs_req();
                        req.uiType = CONST_imCommType_getCfgsReq;
                        req.uiSizePerSnd_media = pProcInfo.cfg.uiSizePerSnd_media;
                        len = 16;// (int)sizeof(QIS_getCfgs_req);
//                        byte[] data = req.toByteArray();
                        pProcInfo.postMsg2Mgr_mc(tmp_ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_getCfgs, tStartTran, uiTranNo, 0, req, len, 0, 0, 0, null, false);
                        //
                        pMisCnt.dwLastTickCnt_getIntervalParams = dwCurTickCnt;
                    }


                    int usIntervalInS_refreshContactList;

                    if (0 == pMisCnt.refreshContactList.tLastRefreshedTime_misServ)
                        usIntervalInS_refreshContactList = 20;
                    else
                        usIntervalInS_refreshContactList = pMisCnt.cfg.usIntervalInS_refreshContactList;
                    //

                    //
                    //  2014/02/11. 需要考虑正在处理中的retrieve, 当下载的messenger非常多时，就下载过程会很长。判断下dwLastTickCnt_respRecvd可以避免重复发起请求
                    if (dwCurTickCnt - pMisCnt.refreshContactList.dwLastTickCnt_respRecvd > 20000
                            && dwCurTickCnt > pMisCnt.refreshContactList.dwLastTickCnt_startToRetrieve + usIntervalInS_refreshContactList * 1000) {
                        qnmCommProc_mis.IM_L_STREAM pStream = null;

                        tStartTran = tmpFunc.qyFuncs.mytime();
                        uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

                        String str = "start to retrieveImObjList, tn " + uiTranNo + " " + (bRefreshAllContacts(pMisCnt) ? ("全面") : ("增量"));
                        Log.d(str);
                        showInfo_open.qyFuncs.showInfo_open(str);

                        pMisCnt.refreshContactList.tStartTime = tStartTran;
                        pMisCnt.refreshContactList.uiTranNo = uiTranNo;
                        pMisCnt.refreshContactList.idInfo_lastRefreshed.ui64Id = 0;

                        qnmCommProc_mis.RETRIEVE_IMOBJLIST_REQ req = new qnmCommProc_mis.RETRIEVE_IMOBJLIST_REQ();
                        req.uiType = CONST_imCommType_retrieveImGrpListReq;
                        req.tLastRefreshedTime_contactList_misServ = pMisCnt.refreshContactList.tLastRefreshedTime_misServ;
                        req.usMaxContactUpdateInfosPerSnd = pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_grp;

                        pStream = new qnmCommProc_mis.IM_L_STREAM();
                        pStream.uiType = CONST_imCommType_lStream;
                        pStream.uiContentType = req.uiType;
                        if (0 > (len = retrieveImObjListReq2Stream(pStream.uiContentType, req, pStream.buf))) {
                            Log.e("retrieveImObjListReq2Stream failed");
                            pProcInfo.status.threadsStatus.ulbSchedulerQuit = true;
                            Log.i("mcThreadProc_mis_scheduler:  leaves");
                            return;
                        }
                        pStream.buf.flip();
                        pStream.ulStreamLen = len;
                        len += 12;// (uint)Marshal.OffsetOf<IM_L_STREAM>("buf");
//                        byte[] data = pStream.toByteArray();
                        pProcInfo.postMsg2Mgr_mc(ctx.ctxCaller, pMisCnt, null, CONST_misMsgType_req, (byte) 0, (short) CONST_qyCmd_refreshImObjListReq, tStartTran, uiTranNo, 0, pStream, len, 0, 0, 0, ctx.pMsgBuf, false);

                        pMisCnt.refreshContactList.dwLastTickCnt_startToRetrieve = dwCurTickCnt;
                    }
                    //  for cpu usage. 2009/10/11
                    if (dwCurTickCnt > pProcInfo.status.dwTickCnt_toStat + 1000) {
                        getCpuUsage(pQyMc.env, pProcInfo);
                        getNetStat_isCli(pProcInfo, (int) (dwCurTickCnt - pProcInfo.status.dwTickCnt_toStat));

                        pProcInfo.status.dwTickCnt_toStat = dwCurTickCnt;
                    }
                }
            }

            pProcInfo.status.threadsStatus.ulbSchedulerQuit = true;

            Log.d("mcThreadProc_mis_scheduler:  leaves");

            return;
        };

        //
        public static int getCpuUsage(qnmCommProc.QY_ENV pEnv, ctxQmc.CCtxQmc pProcInfo) {
            return 0;
        }

        public static int getNetStat_isCli(ctxQmc.CCtxQmc pProcInfo, int nIntervalInMs) {
            int iErr = -1;

            if (null == pProcInfo) return -1;
            //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;
            //
            qyCommProc.IM_NET_STAT netStat = new qyCommProc.IM_NET_STAT();
            int i;
            long i64InOctets;
            long i64OutOctets;

            if (0 == nIntervalInMs) return -1;

            //mymemset((byte[])netStat, 0, sizeof(IM_NET_STAT));
            for (i = 0; i < (pMisCnt.channels.length); i++) {
                qmCommFunc_mis.MIS_CHANNEL pChannel = pMisCnt.channels[i];
                //
                i64InOctets = (pChannel.status.netStat.ui64MsgDataLenRecvd - pChannel.status.oldNetStat.ui64MsgDataLenRecvd);
                i64OutOctets = (pChannel.status.netStat.ui64MsgDataLenSent - pChannel.status.oldNetStat.ui64MsgDataLenSent);
                if (i64InOctets < 0 || i64OutOctets < 0) {
                    //memcpy(pChannel->status.oldNetStat,pChannel->status.netStat, sizeof(pChannel->status.oldNetStat)  );
                    pChannel.status.oldNetStat = pChannel.status.netStat;
                    return iErr;
                }

                pChannel.status.netStat.uiInSpeedInKbps = (int) (i64InOctets * 8.0f / nIntervalInMs);
                pChannel.status.netStat.uiOutSpeedInKbps = (int) (i64OutOctets * 8.0f / nIntervalInMs);
                //
                //memcpy(  &pChannel->status.oldNetStat,  &pChannel->status.netStat,  sizeof(pChannel->status.oldNetStat  )  );
                pChannel.status.oldNetStat = pChannel.status.netStat;

                //
                netStat.ui64MsgDataLenRecvd += pChannel.status.netStat.ui64MsgDataLenRecvd;
                netStat.ui64MsgDataLenSent += pChannel.status.netStat.ui64MsgDataLenSent;

                //
                switch (pChannel.uiType) {
                    case CONST_channelType_media:
                        pProcInfo.status.netStat.uiInSpeedInKbps_v = pChannel.status.netStat.uiInSpeedInKbps;
                        pProcInfo.status.netStat.uiOutSpeedInKbps_v = pChannel.status.netStat.uiOutSpeedInKbps;
                        break;
                    case CONST_channelType_realTimeMedia:
                        pProcInfo.status.netStat.uiInSpeedInKbps_a = pChannel.status.netStat.uiInSpeedInKbps;
                        pProcInfo.status.netStat.uiOutSpeedInKbps_a = pChannel.status.netStat.uiOutSpeedInKbps;
                        break;
                    case CONST_channelType_robot:
                        pProcInfo.status.netStat.uiInSpeedInKbps_f = pChannel.status.netStat.uiInSpeedInKbps;
                        pProcInfo.status.netStat.uiOutSpeedInKbps_f = pChannel.status.netStat.uiOutSpeedInKbps;
                        break;
                    default:
                        break;
                }

            }

            i64InOctets = (netStat.ui64MsgDataLenRecvd - pProcInfo.status.netStat.ins.ui64MsgDataLenRecvd);
            i64OutOctets = (netStat.ui64MsgDataLenSent - pProcInfo.status.netStat.ins.ui64MsgDataLenSent);
            if (i64InOctets < 0 || i64OutOctets < 0) {
                //memcpy(pProcInfo.status.netStat,netStat, sizeof(pProcInfo.status.netStat)  );
                pProcInfo.status.netStat.ins = netStat;
                return iErr;
            }
            netStat.uiInSpeedInKbps = (int) (i64InOctets * 8.0f / nIntervalInMs);
            netStat.uiOutSpeedInKbps = (int) (i64OutOctets * 8.0f / nIntervalInMs);

            pProcInfo.status.netStat.ins = netStat;

            //
            iErr = 0;

            return iErr;
        }


    }
}

