


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.CONST_channelType_rtOp;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.Consts.CONST_qyRet_qFull;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsgAndTrigger;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_htmlContent;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.isProtocolFunc.qyFuncs.bMessengerIdValid;
import static com.qycx.base.isProtocolFunc.qyFuncs.stream2Data;
import static com.qycx.base.isProtocolFunc.qyFuncs.tmpHandler_stream2AuthResp_mis;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qisChannel.qyFuncs.startThread_toSend;
import static com.qycx.base.qisChannel.qyFuncs.waitForChannel;
import static com.qycx.base.qmCrypt.qyFuncs.decryptData;
import static com.qycx.base.qnmCommProc.Consts.CONST_qnmAuthCmd_clientHello;
import static com.qycx.base.qnmCommProc.qyFuncs.CONST_fieldIdTable_en;
import static com.qycx.base.qnmCommProc.qyFuncs.getCorrectCnt;
import static com.qycx.base.qnmCommProc1.qyFuncs.bObjDbAvail;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_refreshMeStatus_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_displayName;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_hint;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_talkerDesc;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.qnmOpenSession_mis;
import static com.qycx.base.qnmCustomComm.qyFuncs.regInfo2Desc;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_anotherLogonExists;
import static com.qycx.base.qyComm.Consts.CONST_qyServiceId_mis;
import static com.qycx.base.qyComm.qyFuncs.isRcNeedVerified;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_debugWarning;
import static com.qycx.base.qyCommProc.Consts.bufSize_publicKeyBlob;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_io;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_toSend;
import static com.qycx.base.qyCommonSock.qyFuncs.clearQySock;
import static com.qycx.base.qyCusResHelp.qyFuncs.getResStr;
import static com.qycx.base.qyCusResPublic.Consts.CONST_usRunningStatus_online;
import static com.qycx.base.qyCustom.qyFuncs.qnmVerStr;
import static com.qycx.base.qyDefs.Consts.CONST_objType_me;
import static com.qycx.base.qyDefs.Consts.CONST_qyTimeLen;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.Consts.bufSize_4096;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.qyFuncs.mcThreadProc_mis_mgr;
import static com.qycx.base.qyMcMainDaemon_isCli_scheduler.qyFuncs.mcThreadProc_mis_scheduler;
import static com.qycx.base.qyMcMainProc_isCli_media.qyFuncs.mcThreadProc_mis_media;
import static com.qycx.base.qyMcMainProc_isCli_realTimeMedia.qyFuncs.mcThreadProc_mis_realTimeMedia;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.mcThreadProc_mis_robot;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.procMsgInput_other_req;
import static com.qycx.base.qyMsgFunc.qyFuncs.qnmCloseSession_msg;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_threadProc_isCli_end;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyq2.qyFuncs.emptyQ2;
import static com.qycx.base.qyq2.qyFuncs.q2AvoidTriggerTooHigh;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.showInfo_open.qyFuncs.set_who_showInfo;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.tmpFunc.qyFuncs.msgToHead_snd;
import static com.qycx.base.tmpFunc.qyFuncs.myEventSet;
import static com.qycx.base.tmpFunc.qyFuncs.mySemaRelease;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;
import static com.qycx.base.tmpFunc.qyFuncs.safeTcsnCpy;
import static com.qycx.base.tmpFunc.qyFuncs.safeWcsnCpy;

import com.qycx.R;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class qyMcMainDaemon_isCli {

//    private static final String TAG = qyMcMainDaemon_isCli.class.getSimpleName();

    public static class Consts {
        public final static int bufSize_1024 = 1024;
        public final static int bufSize_2048 = 2048;
        public final static int bufSize_4096 = 4096;
        public final static int bufSize_8192 = 8192;
    }

    public static class TMP_timeBuf {
        public ByteBuffer timeBuf = ByteBuffer.allocate(CONST_qyTimeLen + 1);//[];
    }

    ;

    public static class qyFuncs {
        public static class RESULT_startChannel {
            public String respHint;//[cntof_hint];

            //
            public void memset0()
            {
                respHint=null;
            }
        }

        /*
        public static class TMP_buf1024 {
            public ByteBuffer buf = ByteBuffer.allocate(Consts.bufSize_1024);//[];
        }

        public static class TMP_buf2048 {
            public ByteBuffer buf = ByteBuffer.allocate(Consts.bufSize_2048);//[];

        }

         */

        public static class TMP_buf4096 {
            public ByteBuffer buf = ByteBuffer.allocate(bufSize_4096);//[];
        }

        /*
        public static class TMP_buf8192 {
            public ByteBuffer buf = ByteBuffer.allocate(Consts.bufSize_8192);//[];
        }

         */


        static Runnable mcThreadProc_mis_work = () -> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;

            int loopCtrl = 0;
            qnmCommProc_mis.MIS_MSGU pMsg;
            int len;
            int dwRet;
            //
            ctxMcThread.CTX_MC_work ctx;
            int uiChannelType = CONST_channelType_talking;

            pProcInfo.dwThreadId_work = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctx = new ctxMcThread.CTX_MC_work();
            ctx.pMsgBuf_forInput = new qnmCommProc_mis.CMIS_MSGU();
            {
                //  2007/06/20
                if (!bObjDbAvail(pQyMc.procedObjDb)) {
                    return;
                }

                for (; !pProcInfo.bQuit_threadProc_isCli; ) {

                    if (null == pQyMc.gui.hMainWnd) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.e(e.getMessage());
                            return;
                        }
                        continue;
                    }

//                    myWaitOne(pProcInfo.workQ.hEvent, 3500);
                    myWaitOne(pProcInfo.workQ.hSemaTrigger, 3500);

                    for (; !pProcInfo.bQuit_threadProc_isCli; ) {

                        if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pProcInfo.workQ)))
                            break;

                        switch (pMsg.uiType) {
                            case CONST_misMsgType_input: {
                                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                                qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                                if (null == pMisCnt) {
                                    Log.i("mcThreadProc_mis_work failed: pMisCnt is null");
                                    break;
                                }
                                if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                    break;
                                qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                                if (!isUcFlgResp(input.ucFlg)) {
                                    if (0 != procMsgInput_other_req(pQyMc, pProcInfo, input, input.lenInBytes, ctx, pChannel))
                                        break;
                                    break;
                                }

                                if (0 != procMsgInput_work_resp(pQyMc, pProcInfo, input, input.lenInBytes, ctx, pChannel))
                                    break;
                                //
                            }
                            break;

                            case CONST_misMsgType_talk: {
                                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                                qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                                qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                                if (null == pMisCnt) {
                                    Log.i("mcThreadProc_mis_work: pMisCnt is null");
                                    break;
                                }
                                if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                    break;

                                if (0 != getIdInfo_logicalPeer(pMisCnt, talk.data.route,
                                        talk.addr_logicalPeer)) break;

                                if (0 != procMsgTalk_work(pQyMc, pProcInfo, talk, talk.lenInBytes, ctx, pChannel))
                                    break;

                            }
                            break;


                            case CONST_misMsgType_task: {
                                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                                qnmCommProc_mis.IM_CONTENTU pContent = task.data.buf;

                                if (null == pMisCnt) {
                                    Log.i("mcThreadProc_mis_work: pMisCnt is null");
                                    break;
                                }
                            }
                            break;

                            default:
                                Log.d("mcThreadProc_mis_work failed, unknown  msgType " + pMsg.uiType);
                                break;
                        }

                        //dwRet = WaitForSingleObject(pProcInfo->workQ.hSemaTrigger, 50);
                        //if (dwRet == WAIT_FAILED) return iErr;

                    }

                }
            }


            //if (ctx.ppGuests_sortedByDevIdStr) free(ctx.ppGuests_sortedByDevIdStr);
            //if (ctx.bGuestQInited) exitGenericQ(ctx.guestQ);

            Log.i("mcThreadProc_mis_work:  leaves");

            pProcInfo.status.threadsStatus.ulbWorkQuit = true;

            return;
        };

        /// <summary>
        /// ///////////////////////////
        /// </summary>
        /// <param name="pMisCnt"></param>
        /// <param name="pChannel"></param>
        /// <param name="result"></param>
        /// <returns></returns>
        /// 更新显示信息
        public static int updateMeDisplayInfo(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String talkerDesc, String displayName) {
            int iErr = -1;

            qnmCommProc_mis.MIS_MSG_REFRESHMESTATUS_QMC msg = new qnmCommProc_mis.MIS_MSG_REFRESHMESTATUS_QMC();
            int len;
            String str;

            String pTalkerDesc = pMisCnt.talkerDesc.talkerDesc;
            {
                String pDisplayName = pMisCnt.displayName.displayName;
                {
                    String pDisplayNameWithIdInfo = pMisCnt.displayName_withIdInfo.displayName;
                    {

                        //
                        if (null != pRegInfo && pRegInfo.addr.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            //
                            qnmCommProc_mis.MY_REG_DESC desc = new qnmCommProc_mis.MY_REG_DESC();

                            //  2010/04/16
                            if (0 != regInfo2Desc(null, pRegInfo, desc, pTalkerDesc, cntof_talkerDesc, pDisplayName, cntof_displayName))
                                return iErr;

                            str = String.format("%s %d", pDisplayName, pMisCnt.idInfo.ui64Id);
                            pDisplayNameWithIdInfo = str;// cntof_displayName);


                            //  2016/04/06
                        } else if (null != talkerDesc && null != displayName) {
                            pTalkerDesc = talkerDesc;//, cntof_talkerDesc);
                            pDisplayName = displayName;//, cntof_displayName);

                            //  2016/04/06
                            safeTcsnCpy(pDisplayName, pDisplayNameWithIdInfo, cntof_displayName);
                        }

                        //
                        //mymemset((byte[])msg, 0, sizeof(MIS_MSG_REFRESHMESTATUS_QMC));
                        msg.uiType = CONST_misMsgType_refreshMeStatus_qmc;
                        //msg.pMisCnt = pMisCnt;
                        //			 
                        len = 4;//sizeof(MIS_MSG_REFRESHMESTATUS_QMC);
                        if (0 != qPostMsgAndTrigger(msg, pProcInfo.displayQ)) {
                            return iErr;
                        }
                    }
                }
            }

            iErr = 0;


            return iErr;

        }


        //
        //public static int tryToStartChannel(qmCommFunc_mis.MIS_CNT pMisCnt, qmCommFunc_mis.MIS_CHANNEL pChannel, RecyclableMemoryStreamManager p_recyclableMemoryStreamManager, RESULT_startChannel result)
        public static int tryToStartChannel(qmCommFunc_mis.MIS_CNT pMisCnt, qmCommFunc_mis.MIS_CHANNEL pChannel, RESULT_startChannel result)
        {
            int iErr = -1;

            if (null == pMisCnt) return -1;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pMisCnt.pProcInfoParam;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            String str;

            TMP_buf4096 buf4096 = new TMP_buf4096();
            int len;


            qyTypes.QY_BUF buf_help = new qyTypes.QY_BUF();
            buf_help.len = bufSize_4096;
            buf_help.data = new byte[buf_help.len];

            byte[] ptr;
            qnmCommProc_mis.AUTH_RESP_MIS authResp = new qnmCommProc_mis.AUTH_RESP_MIS();
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            qnmCommProc_mis.QY_MESSENGER_INFO obj = new qnmCommProc_mis.QY_MESSENGER_INFO();

            if (pChannel.bSessionOpen.get()) return 0;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            pChannel.status.ulbNeedVerified = false;
            pChannel.status.ulbAuthed = false;

            len = bufSize_4096;
            String str_servIp = pMisCnt.server.ip;
            commonSock.SOCK_PARAM sockParam = pQyMc.cfg.sockParam;

            //
            Log.d("qyLogonId","tryToStartChannel enters, logonId "  +  pProcInfo.authInfo.uiLogonId);

            //
            do {

                //
                if (null == (buf4096.buf = qnmOpenSession_mis(str_servIp, pMisCnt.server.port, pProcInfo.authInfo, 0, pChannel.uiType, null, pQyMc.iServiceId, qnmVerStr(pQyMc.iServiceId), pChannel.commEncCtx, pChannel.session, pChannel.sock, sockParam))) {
                    if (pChannel.bSessionOpen.get())
                    {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }
                len = buf4096.buf.limit();
                pChannel.bSessionOpen.set(true);

                //解密
                /*
                byte[] cipherText = new byte[buf4096.buf.remaining()];
                buf4096.buf.get(cipherText);
                 */
                ByteBuffer buf;
//
                buf4096.buf.position(0);
                if (null == (buf = decryptData(pChannel.commEncCtx, buf4096.buf)))
                {
                    if (pChannel.bSessionOpen.get())
                    {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }
                if (buf.capacity() == 0)
                    return iErr;
                len = stream2Data(buf, len, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2AuthResp_mis, 0, authResp);
                if (len < 0)
                {
                    if (pChannel.bSessionOpen.get())
                    {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }
                if (!isRcOk(pChannel.session.usLastRespCode_i)) {
                    //
                    //
                    if (pChannel.uiType == CONST_channelType_talking) {
                        if (result.respHint == null) {
                            result.respHint = "";
                        }
                        String respHint = result.respHint;
                        {
                            safeWcsnCpy(String.valueOf(authResp.wBuf_hint), respHint.toCharArray(), cntof_hint);
                        }
                        //检测 账号是否有重复登录
                        if (pChannel.session.usLastRespCode_i == CONST_qyRc_anotherLogonExists) {
                            if (pQyMc.bLogon) {
                                str = "另一处已登陆:"  + authResp.wBuf_hint;
                                showNotification(pMisCnt, null, null, 0, 0, 0, str);
                                showInfo_open.qyFuncs.showInfo_open(0, null, str);
                                //强制退出
                                waitToQuit_isCli(pProcInfo, 5000, str);
                            }
                            if (pChannel.bSessionOpen.get())
                            {
                                qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                                pChannel.bSessionOpen.set(false);
                            }
                            return iErr;
                        }
                    }
                    if (!isRcNeedVerified(pChannel.session.usLastRespCode_i)) {
                        //
                        Log.d("tn_msgInput","tryToStartChannel failed: access denied");
                        //
                        if (pChannel.bSessionOpen.get()) {
                            qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                            pChannel.bSessionOpen.set(false);
                        }
                        return iErr;
                    }
                    Log.d("get hint from server: " + authResp.wBuf_hint);
                    pChannel.status.ulbNeedVerified = true;
                }
                if (isRcOk(pChannel.session.usLastRespCode_i)) pChannel.status.ulbAuthed = true;

                if (!pChannel.status.ulbAuthed) {
                    Log.d("ulbAuthed is false");
                    if (pChannel.bSessionOpen.get()) {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }
                if (!bMessengerIdValid(authResp.idInfo)) {
                    if (pChannel.bSessionOpen.get())
                    {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }

                idInfo.ui64Id = authResp.idInfo.ui64Id;
                {
                    pProcInfo.str_who_showInfo = String.valueOf(idInfo.ui64Id);
                    set_who_showInfo(pProcInfo.str_who_showInfo);
                }
                Log.d("tryTostartChannel, getIdInfo " + idInfo.ui64Id);
                if (pChannel.uiType != CONST_channelType_talking) {   //  2008/03/17,
                    iErr = 0;
                    return iErr;
                }

                //  2011/02/02
                if (0 != authResp.uiLogonId) {
                    if  (  pProcInfo.authInfo.uiLogonId  ==  0  ) {
                        if (pProcInfo.authInfo.uiLogonId != authResp.uiLogonId) {
                            pProcInfo.authInfo.uiLogonId = authResp.uiLogonId;
                            //
                            showInfo_open.qyFuncs.showInfo_open("tryToStartChannel: uiLogonId is set");
                        }
                    }
                    else {
                          if (  pProcInfo.authInfo.uiLogonId  !=  authResp.uiLogonId  )  {

                              //
                              //if (pQyMc.bLogon)
                              {
                                  str = "另一处已登陆:"  + authResp.wBuf_hint;
                                  showNotification(pMisCnt, null, null, 0, 0, 0, str);
                                  showInfo_open.qyFuncs.showInfo_open(0, null, str);
                                  //强制退出
                                  waitToQuit_isCli(pProcInfo, 5000, str);
                              }
                              //
                              break;
                          }
                    }
                }
                //暂时不用了
                if (0 != authResp.usAppAvLevel_daemon) {
                    if (pQyMc.usAppAvLevel_daemon != authResp.usAppAvLevel_daemon) {
                        pQyMc.usAppAvLevel_daemon = authResp.usAppAvLevel_daemon;  //  2011/10/22
                    }
                }
                //  2009/10/11
                if (pQyMc.ucbDaemonDemo != authResp.ucbDaemonDemo) {
                    pQyMc.ucbDaemonDemo = authResp.ucbDaemonDemo;
                }
                //
                //  2017/06/21
                if (pQyMc.usSubSystemId_daemon != authResp.usSubSystemId_daemon) {
                    pQyMc.usSubSystemId_daemon = authResp.usSubSystemId_daemon;
                }

                //  2016/08/10
                if (pChannel.uiType == CONST_channelType_talking) {
//                safeWcsnCpy(authResp.clientWelcomeMsg, pProcInfo.cfg.clientWelcomeMsg, mycountof(pProcInfo.cfg.clientWelcomeMsg));
                }

                if (!pQyMc.bLogon) {       //  2010/03/10
                    iErr = 0;
                    if (pChannel.bSessionOpen.get()) {
                        qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                        pChannel.bSessionOpen.set(false);
                    }
                    return iErr;
                }

                qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                {
                    if (!bObjDbAvail(pQyMc.procedObjDb)) {
                        if (pChannel.bSessionOpen.get())
                        {
                            qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                            pChannel.bSessionOpen.set(false);
                        }
                        return iErr;
                    }
                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) {
                        if (pChannel.bSessionOpen.get())
                        {
                            qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                            pChannel.bSessionOpen.set(false);
                        }
                        return iErr;
                    }
                    Object pDb = db.m_pDbMem.pDb;

                    if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pQyMc.p_g_dbFuncs, pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, "", idInfo, 0, obj, null, null, null)) {

                        obj.uiType = CONST_objType_me;
                        obj.idInfo = idInfo;
                        //如果数据库有值就修改   没有值就插入
                        if (0 != pProcInfo.recoverMessenger(pDbFuncs, pDb, pQyMc.cfg.db.iDbType, CONST_fieldIdTable_en, obj,
                                null, 0, false, pProcInfo.schedulerQ)) {
                            Log.e("recoverMessenger failed");
                            if (pChannel.bSessionOpen.get()) {
                                qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                                pChannel.bSessionOpen.set(false);
                            }
                            return iErr;
                        }
                    }

                    if (0 != myDb.QM_dbFuncs.pf_updateImObj_me.call(pDb, pQyMc.cfg.db.iDbType, obj)) {
                        Log.e("g_dbFuncs.pf_updateImObj_me failed");
                        if (pChannel.bSessionOpen.get()) {
                            qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                            pChannel.bSessionOpen.set(false);
                        }
                        return iErr;
                    }

                    if (pQyMc.usMaxVideoConferenceMems != authResp.usMaxVideoConferenceMems)
                        pQyMc.usMaxVideoConferenceMems = authResp.usMaxVideoConferenceMems;

                    if (pQyMc.ucDays_left != authResp.ucDays_left)
                        pQyMc.ucDays_left = authResp.ucDays_left;  //  2010/03/22

                    pMisCnt.idInfo = idInfo;
                    pMisCnt.status.usRunningStatus = CONST_usRunningStatus_online; //  2007/08/31
                    pMisCnt.bIntervalParamsGot = false;

                    if (0 != updateMeDisplayInfo(pProcInfo, pMisCnt, null, null, null)) {
                        Log.e("updateMeDisplayInfo faild");
                        if (pChannel.bSessionOpen.get())
                        {
                            qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                            pChannel.bSessionOpen.set(false);
                        }
                        break;
                    }
                }


                iErr = 0;

            }
            while (false);

            if (iErr != 0) {
                if (pChannel.bSessionOpen.get()) {
                    qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                    pChannel.bSessionOpen.set(false);
                }

                //
                Log.d("tryToStartChannel failed");

            }

            return iErr;
        }


        //
        static qmcFunc.PF_threadProc mcThreadProc_isCli = (Object lpParameter) -> {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = (ctxQyMc.CCtxQyMc) lpParameter;
            int iServiceId = CONST_qyServiceId_mis;

            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int i;
            boolean bInited = false;
            int dwThreadDaemonId;
            int loopCtrl = 0;
            //  QY_LOGICAL_SERVER				tmpCnt;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (pMisCnt == null) return;

            String str = "mcThreadProc_isCli enters";
            showInfo_open.qyFuncs.showInfo_open(0, null, str);
            pProcInfo.status.ulbThreadProcInited = true;
            pProcInfo.bQuit_threadProc_isCli = false;
            pProcInfo.status.threadsStatus = new ctxQmcTmpl.QMC_threads_status();
            str = "mcThreadProc_isCli, to starts all threads";
            showInfo_open.qyFuncs.showInfo_open(0, null, str);

            pProcInfo.hThread_mgr = new Thread(mcThreadProc_mis_mgr);
            pProcInfo.hThread_mgr.start();
            pProcInfo.hThread_mgr.setName("ThreadProc_mis_mgr");

            pProcInfo.hThread_robot = new Thread(mcThreadProc_mis_robot);
            pProcInfo.hThread_robot.start();
            pProcInfo.hThread_robot.setName("ThreadProc_mis_robot");

            pProcInfo.hThread_work = new Thread(mcThreadProc_mis_work);
            pProcInfo.hThread_work.start();
            pProcInfo.hThread_work.setName("ThreadProc_mis_work");

            pProcInfo.hThread_scheduler = new Thread(mcThreadProc_mis_scheduler);
            pProcInfo.hThread_scheduler.start();
            pProcInfo.hThread_media = new Thread(mcThreadProc_mis_media);
            pProcInfo.hThread_media.start();
            pProcInfo.hThread_media.setName("ThreadProc_mis_media");

            pProcInfo.hThread_realTimeMedia = new Thread(mcThreadProc_mis_realTimeMedia);
            pProcInfo.hThread_realTimeMedia.start();
            pProcInfo.hThread_realTimeMedia.setName("ThreadProc_mis_realTimeMedia");

            //
            for (; true; loopCtrl++) {
                if  (  pQyMc.bQuit  ||  !pQyMc.bLogon  )  {
                    if  (  pQyMc.bQuit) {
                        showInfo_open.qyFuncs.showInfo_open(0, "", "isCli.650: pQyMc.bQuit is true");
                    }
                    if  (  !pQyMc.bLogon  )  {
                        showInfo_open.qyFuncs.showInfo_open(0, "", "isCli: pQyMc.bLogon is false");
                    }
                    break;
                }
                //, Thread.sleep(1000)
                myWaitOne(pProcInfo.hEvt_notify_thread_isCli, 1000);
//                Log.d("mcThreadProc_isCli: evt waited");
                if (pQyMc.bQuit) {
                    showInfo_open.qyFuncs.showInfo_open(0,"","isCli.661: pQyMc.bQuit is true");
                    break;
                }

                //
                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_talking);
                if  (  pChannel  ==  null)  continue;
                if  (  !pChannel.bSessionOpen.get()) {
                    //
                    qyObjs.QY_LOGICAL_SERVER tmpCnt = new qyObjs.QY_LOGICAL_SERVER();
                    if (0 != getCorrectCnt(pQyMc.env, pMisCnt.cfg.dynCfg, qyMcMainDaemon_isCli.qyFuncs.bServConnected_mis, pMisCnt, pQyMc.iServiceId,
                            qnmVerStr(pQyMc.iServiceId), pMisCnt.commEncCtx, pQyMc.cfg.sockParam, tmpCnt)) {
                        {
                            str  =  "mcThreadProc_isCli: getCorrectCnt failed ";
                            showInfo_open.qyFuncs.showInfo_open(str);
                            //
                            continue;
                        }
                    } else {
                        //
                        showInfo_open.qyFuncs.showInfo_open("mcThreadProc_isCli: getCorrectCnt ok");

                        //将正确的服务器地址信息 赋值
                        pMisCnt.server = tmpCnt;
                    }

                    if (!pMisCnt.commEncCtx.ucbPublicKeyDownloaded || !pMisCnt.commEncCtx.ucbPublicKeyVerified) {
                        //
                        showInfo_open.qyFuncs.showInfo_open("mcThreadProc_isCli err: publicKey not downloaded");
                        //
                        continue;
                    }
                }

                //
                int loopCtrl_chkChannel = 2; //possible turing point, zzq
                if (0 == (loopCtrl % loopCtrl_chkChannel)) {
                    if (null == pChannel) continue;

                    if (pMisCnt.cfg.dynCfg.cnt.usCntPort != 0) {

                        if (pProcInfo.status.threadsStatus.ulbMgrQuit
                                || pProcInfo.status.threadsStatus.ulbWorkQuit
                                || pProcInfo.status.threadsStatus.ulbDisplayQuit
                                || pProcInfo.status.threadsStatus.ulbRobotQuit
                                || pProcInfo.status.threadsStatus.ulbSchedulerQuit
                                || pProcInfo.status.threadsStatus.ulbMediaQuit
                                || pProcInfo.status.threadsStatus.ulbRealTimeMediaQuit
                                || pProcInfo.status.threadsStatus.ulbRtOpQuit
                                || pProcInfo.status.threadsStatus.ulbLogQuit
                        ) {
                            String msg = ("mcThreadProc_mis will quit, because sub thread has quitted.");
                            Log.e(msg);
                            showInfo_open.qyFuncs.showInfo_open(0, "mcThreadProc_isCli", msg);
                            return;
                        }

                        if (!pChannel.bSessionOpen.get()) {

                            //
                            Log.d("isCli: before startChannel");

                            Log.d("talk channel start io thread");
                            startChannel(pProcInfo, pMisCnt, getChannelByType(pMisCnt, 0), "");
                        }

                        if (pChannel.hThread_io != null) {
                            //
                            //traceLog.qyFuncs.traceLog("isCli: before waitForThread");

                            //
                            waitForThread_io(pChannel, false);
                            if (null == pChannel.hThread_io) {
                                str = String.format("channal %s closed", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                                Log.d(str);
                                showInfo_open.qyFuncs.showInfo_open(null, "mcThreadProc_isCli", str);
                            }
                        }

                        //
                        if(pChannel.hThread_toSend!=null)
                        {
                            waitForThread_toSend(pChannel,false);
                        }
                        if  (  pChannel.hThread_toSend  ==  null  )  {
                            startThread_toSend(pProcInfo,  pMisCnt,pChannel,"mcThreadProc_isCli.723");
                        }

                    }
                }

                //
                continue;
            }

            //
            iErr = 0;


            str = String.format("mcThreadProc_isCli start to quit");
            Log.d("qy", str);

            //
            pProcInfo.bQuit_threadProc_isCli = true;

            //
            {
                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pProcInfo.pMisCnt, CONST_channelType_talking);
                //Log.d("waitForChannel");
                waitForChannel(pProcInfo, pProcInfo.pMisCnt, 0, true);
                //Log.d("channel talking waited");

                //
                if  (  pChannel.hThread_toSend  !=  null) {
                    waitForThread_toSend(pChannel, true);
                    if (pChannel.hThread_toSend!=null)  {
                        Log.e("qy","Err: talk.thread_toSend not closed");
                        showInfo_open.qyFuncs.showInfo_open("Err: talk.thread_toSend not closed");
                    }
                    else  {
                          showInfo_open.qyFuncs.showInfo_open("talk.thread_toSend closed");
                    }
                }
                else  {
                      //
                      showInfo_open.qyFuncs.showInfo_open("talk.thread_toSend already closed");
                }

            }


            //
            myEventSet(pProcInfo.mgrQ.hEvent);
            mySemaRelease(pProcInfo.schedulerQ.hSemaTrigger, 1);

            try {
                if(pProcInfo.hThread_rtOp!=null)
                {
                    pProcInfo.hThread_rtOp.join();
                    pProcInfo.hThread_rtOp = null;
                    Log.i("hThread_rtOp exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "rtOp waited");
                }
                if(pProcInfo.hThread_realTimeMedia !=null) {
                    pProcInfo.hThread_realTimeMedia.join();
                    pProcInfo.hThread_realTimeMedia = null;
                    Log.i("hThread_realTimeMedia exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "rtMedia waited");
                }
                if(pProcInfo.hThread_media!=null) {
                    pProcInfo.hThread_media.join();           //  2008/03/15
                    pProcInfo.hThread_media = null;
                    Log.i("hThread_media exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "media waited");
                }
                if(pProcInfo.hThread_scheduler!=null) {
                    pProcInfo.hThread_scheduler.join();
                    pProcInfo.hThread_scheduler = null;
                    Log.i("hThread_scheduler exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "scheduler waited");
                }
                if(pProcInfo.hThread_work==null) {
                    pProcInfo.hThread_work.join();
                    pProcInfo.hThread_work = null;
                    Log.i("hThread_work exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "work waited");
                }
                if(pProcInfo.hThread_display!=null) {
                    pProcInfo.hThread_display.join();
                    pProcInfo.hThread_display = null;
                    Log.i("hThread_display exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "display waited");
                }
                if(pProcInfo.hThread_robot!=null){
                pProcInfo.hThread_robot.join();
                pProcInfo.hThread_robot = null;
                Log.i("hThread_robot exit");
                showInfo_open.qyFuncs.showInfo_open(0, null, "robot waited");
                }
                if(pProcInfo.hThread_mgr!=null) {
                    pProcInfo.hThread_mgr.join();
                    pProcInfo.hThread_mgr = null;
                    Log.i("hThread_mgr exit");
                    showInfo_open.qyFuncs.showInfo_open(0, null, "mgr waited");
                }
                pProcInfo.bEnd_threadProc_isCli = true;

                //
                str = "mcThreadProc_isCli leaves";
                Log.d("qy", str);
                showInfo_open.qyFuncs.showInfo_open(0, null, str);
                //
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_threadProc_isCli_end, 0);
        };


        public static boolean bChkMessengerChannelsReady(ctxQyMc.CCtxQyMc pQyMc) {
            boolean bRet = false;

            ctxQmc.CCtxQmc pVar = pQyMc.get_pProcInfo();
            if (null == pVar) return false;
            int i;
            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

            boolean bTalkReady = false;
            boolean bMediaReady = false;
            boolean bRtMediaReady = false;
            boolean bRobotReady = false;
            boolean bRtOpReady = false;

            qmCommFunc_mis.MIS_CNT pMisCnt = pVar.pMisCnt;
            if (null == pMisCnt) return bRet;

            if (pMisCnt.cfg.dynCfg.cnt.usCntPort != 0) {
                if (bChannelOk(getChannelByType(pMisCnt, CONST_channelType_talking),false))
                    bTalkReady = true;
                if (bChannelOk(getChannelByType(pMisCnt, CONST_channelType_media),false))
                    bMediaReady = true;
                if (bChannelOk(getChannelByType(pMisCnt, CONST_channelType_realTimeMedia),false))
                    bRtMediaReady = true;
                if (bChannelOk(getChannelByType(pMisCnt, CONST_channelType_robot),false))
                    bRobotReady = true;
                if (bChannelOk(getChannelByType(pMisCnt, CONST_channelType_rtOp),false))
                    bRtOpReady = true;
            }

            bRet = true;


            if (bRet) {
                if (pVar != null) {
                    long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                    //
                    if (pVar.av.talk.bChannelReady != bTalkReady) {
                        pVar.av.talk.bChannelReady = bTalkReady;
                        pVar.av.talk.dwLastModifiedTickCnt = dwTickCnt;
                        Log.d("online status bTalkReady:" + bTalkReady);

                    }
                    if (pVar.av.video.bChannelReady != bMediaReady) {
                        pVar.av.video.bChannelReady = bMediaReady;
                        pVar.av.video.dwLastModifiedTickCnt = dwTickCnt;
                        Log.d("online status bMediaReady:" + bMediaReady);
                    }
                    if (pVar.av.audio.bChannelReady != bRtMediaReady) {
                        pVar.av.audio.bChannelReady = bRtMediaReady;
                        pVar.av.audio.dwLastModifiedTickCnt = dwTickCnt;
                        Log.d("online status bRtMediaReady:" + bRtMediaReady);
                    }
                    if (pVar.av.robot.bChannelReady != bRobotReady) {
                        pVar.av.robot.bChannelReady = bRobotReady;
                        pVar.av.robot.dwLastModifiedTickCnt = dwTickCnt;
                        Log.d("online status bRoboteady:" + bRobotReady);
                    }
                    if (pVar.av.rtOp.bChannelReady != bRtOpReady) {
                        pVar.av.rtOp.bChannelReady = bRtOpReady;
                        pVar.av.rtOp.dwLastModifiedTickCnt = dwTickCnt;
                        Log.d("online status bRtOpReady:" + bRtOpReady);
                    }
                }
            }
            return bRet;
        }

        public static int postMsg2OutputQ_mc(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSGU pMsg, int msgLen, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;

            if (null == pMisCnt) {
                Log.e("postMsg2OutputQ_mc failed: pMisCnt is null");
                return -1;
            }

            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pMisCnt.pProcInfoParam;
            if (null == pProcInfo) return -1;

            int tmpiRet = -1;
            if(pChannel.uiType == 4)
            {
                //Log.d(VIDEO_UPLOAD,"2.11 q2PostMsgAndTrigger");
            }
            //
            if  (  pChannel.m_b_useQ2_toOutput) {
                //
                if (0 != (tmpiRet = q2PostMsgAndTrigger(pMsg, pChannel.outputQ21, "postMsg2OutputQ_mc.856"))) {

                    if (tmpiRet != CONST_qyRet_qFull) {
                        Log.e("qPostMsgAndTrigger failed.");
                        return iErr;
                    } else {
                        //
                        switch (pChannel.uiType) {
                            case CONST_channelType_media:
                            case CONST_channelType_realTimeMedia:
                                emptyQ2(pChannel.outputQ21);
                                break;
                            default:
                                break;
                        }
                        //  2015/08/22. 队列满时，不能退出，应接着触发发送. 在q2PostMsgAndTrigger已经触发
                    }
                }
                if (pChannel.uiType == 4) {
                    //Log.d(com.qycx.base.Consts.IO_TAG,"q2PostMsgAndTrigger video qs0 size:"+pChannel.outputQ2.qs0.uiQNodes+ " qs1 size:"+pChannel.outputQ2.qs1.uiQNodes);
                }
                if (pChannel.outputQ21.ucbTriggerErr) {
                    //q2AvoidTriggerTooHigh(null, pChannel.outputQ2);
                }
            }
            else  {
                  //Log.e("qy","这里还没做完.894");
                  QY_qBuf2.HeadData_snd head  =  new QY_qBuf2.HeadData_snd();
                  msgToHead_snd(pMsg,head);
                  //
                pChannel.outputQBuf2.q2PostMsgAndTrigger(head,pMsg.buffer.array(),0,head.dataLen,"postMsg2OutputQ_mc.899");
            }



            //
            iErr = 0;
            return iErr;
        }

        public static int postMsg2OutputQ_mc(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSGU pMs, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;

            if (null == pMisCnt) {
                Log.e("postMsg2OutputQ_mc failed: pMisCnt is null");
                return -1;
            }

            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            qyMcMainDaemon_isCli_mgr.TMP_tHintBuf tHintBuf = new qyMcMainDaemon_isCli_mgr.TMP_tHintBuf();
            int tmpiRet = -1;

            if  (  pChannel.m_b_useQ2_toOutput) {
                if (0 != (tmpiRet = q2PostMsgAndTrigger(pMs, pChannel.outputQ21, "postMsg2OutputQ_mc.899"))) {

                    if (tmpiRet != CONST_qyRet_qFull) {
                        Log.e("qPostMsgAndTrigger failed.");
                        return iErr;
                    } else {
                        switch (pChannel.uiType) {
                            case CONST_channelType_media:
                            case CONST_channelType_realTimeMedia:
                                emptyQ2(pChannel.outputQ21);
                                break;
                            default:
                                break;
                        }
                        //  2015/08/22. 队列满时，不能退出，应接着触发发送. 在q2PostMsgAndTrigger已经触发
                    }
                }
                if (pChannel.outputQ21.ucbTriggerErr) q2AvoidTriggerTooHigh(null, pChannel.outputQ21);
            }
            else  {
                  //
                  QY_qBuf2.HeadData_snd head = new QY_qBuf2.HeadData_snd();
                  msgToHead_snd(pMs,head);
                  //
                  pChannel.outputQBuf2.q2PostMsgAndTrigger(  head,pMs.buffer.array(),0,head.dataLen,"");

            }

            //
            iErr = 0;

            if (iErr != 0 || tHintBuf.tHintBuf != null) {
                String str = String.format(("postMsg2OutputQ_mc: %s"), tHintBuf);
                showInfo_open.qyFuncs.showInfo_open( str);
            }
            return iErr;
        }

        public static int procMsgInput_work_resp(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;

            //  qnmCommProc_mis.IM_CONTENTU		*	pContent	=	(  qnmCommProc_mis.IM_CONTENTU  *  )pMsgInput->data;
            qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);

            int i;

            if (!isUcFlgResp(pMsgInput.ucFlg)) return -1;
            if (lenInBytes_msg < 0) return -1;

            switch (pContent.uiType) {

                case CONST_imCommType_htmlContent:  //  2014/07/12

                    if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        Log.i("mcThreadProc_mis_work: qPostMsg failed.");
                        break;
                    }
                    //PostMessage(pQyMc->gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                    break;
                default:
                    break;
            }

            iErr = 0;

            return iErr;
        }


        public static int procMsgTalk_work(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TALK pMsg, int lenInBytes_msg, ctxMcThread.CTX_MC_work pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            int iDbType = pQyMc.cfg.db.iDbType;
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            String pT;

            if (isUcFlgResp(pMsg.ucFlg)) return 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            qnmCommProc_mis.MIS_MSG_routeTalkData pTalkData = pMsg.data;
            qnmCommProc_mis.IM_CONTENTU pContent = pTalkData.buf;

            switch (pContent.uiType) {
                case CONST_imCommType_htmlContent: {

                    qnmCommProc_mis.IM_HTML_CONTENT pReq = (qnmCommProc_mis.IM_HTML_CONTENT) pContent;

                    if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                        if (null == pMisCnt) return iErr;
                        if (pReq.idInfo_logicalPeer.ui64Id == pMisCnt.idInfo.ui64Id) {
                            String str = "procMsgTalk_work failed, req.idInfo_logicalPeer is me";
                            Log.e(str);
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            return iErr;
                        }
                        pMsg.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_logicalPeer.ui64Id;
                    }
                }
                break;
                default:
                    break;
            }

            if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                Log.i("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                return iErr;
            }
            guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

            iErr = 0;
            return iErr;
        }


        public static int getIdInfo_logicalPeer(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MSG_ROUTE pRoute, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;

            if (null == pMisCnt) return -1;        //  2007/12/23
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            {

                if (pRoute.idInfo_to.ui64Id == pMisCnt.idInfo.ui64Id) {
                    pAddr_logicalPeer.idInfo.ui64Id = pRoute.idInfo_from.ui64Id;
                    iErr = 0;
                    return iErr;
                } else {
                    //
                    pAddr_logicalPeer.idInfo.ui64Id = pRoute.idInfo_to.ui64Id;
                }
            }

            iErr = 0;

            return iErr;
        }


        public static int myGetPeer(String misServName, qnmCommProc_mis.MSG_ROUTE pRoute, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, qnmCommProc_mis.MSGR_ADDR pPeer) {
            int iErr = -1;

            if (null == misServName || null == pRoute || null == pAddr_logicalPeer || null == pPeer)
                return -1;

            pPeer.misServName = misServName;
            pPeer.uiObjType = pRoute.uiObjType_src;
            pPeer.idInfo.ui64Id = pAddr_logicalPeer.idInfo.ui64Id;
            pPeer.uiDevType = pAddr_logicalPeer.uiDevType;
            pPeer.uiLogonId = pRoute.uiLogonId;

            iErr = 0;
            return iErr;
        }


        //
        public static qnmCommProc.qyFuncs.PF_bServConnected bServConnected_mis = (qnmCommProc.QY_ENV pEnv, String serverIp, int serverPort, Object pParam, int serviceId, String ver, qyEncFunc.QY_ENC_CTX pCommEncCtx, commonSock.SOCK_PARAM pSockParam) -> {
            boolean bRet = false;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pParam;
            commonSock.QY_SOCK sock;
            qyComm.QY_COMM_SESSION session = new qyComm.QY_COMM_SESSION();
            boolean bSessionOpen =false;

            ByteBuffer buffer;

            qnmCommProc_mis.AUTH_RESP_MIS authResp = new qnmCommProc_mis.AUTH_RESP_MIS();

            sock = new commonSock.QY_SOCK();
            clearQySock(sock);

            qnmCommProc_mis.QY_COMM_AUTHINFO_MIS auth = new qnmCommProc_mis.QY_COMM_AUTHINFO_MIS();

            qmCrypt.ENC_CTX_session ecs = new qmCrypt.ENC_CTX_session();

            if (null == (buffer = qnmOpenSession_mis(serverIp, serverPort, auth, CONST_qnmAuthCmd_clientHello, CONST_channelType_talking, null, serviceId, ver, ecs, session, sock, pSockParam))) {
                Log.d("qnmOpenSession_mis failed ");
                if (bSessionOpen) {
                    qnmCloseSession_msg(session, sock, pSockParam.to);
                }
                return bRet;
            }
            bSessionOpen = true;
            if (!isRcOk(session.usLastRespCode_i)) {
                Log.d("isRcOk failed ");
                if (bSessionOpen) {
                    qnmCloseSession_msg(session, sock, pSockParam.to);
                }
                return bRet;
            }
            qnmCloseSession_msg(session, sock, pSockParam.to);
            bSessionOpen=false;
            int len = buffer.capacity();
            if (0 > stream2Data(buffer, len, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2AuthResp_mis, null, authResp))
            {
                if (bSessionOpen) {
                    qnmCloseSession_msg(session, sock, pSockParam.to);
                }
                return bRet;
            }

            pMisCnt.commEncCtx.ucbPublicKeyDownloaded = true;

            if (false) {
            } else {
                try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                    Log.i("publicKey verified");

                    //if (syncObj.sync(CONST_mutexName_syncKey_qmc)) return iErr;

                    //  Èç¹ûÔ­À´Ã»ÓÐÃÜÔ¿£¬Ôò´æÈë×¢²á±í¡£²¢µ¼ÈëCSP
                    //  Èç¹ûÓÐ£¬Ð£ÑéÊÇ·ñÒ»ÖÂ¡£
                    pMisCnt.commEncCtx.common.type = authResp.uiCommEncCtxType;
                    if (authResp.uiBlobLen_publicKey > bufSize_publicKeyBlob){
                        if (bSessionOpen) {
                            qnmCloseSession_msg(session, sock, pSockParam.to);
                        }
                        return bRet;
                    }
                    pMisCnt.commEncCtx.m_var.uiBlobLen_publicKey = authResp.uiBlobLen_publicKey;
                    pMisCnt.commEncCtx.m_var.publicKeyBlob_base64 = new String(authResp.publicKeyBlob);
                    System.arraycopy(authResp.publicKeyBlob, 0, pMisCnt.commEncCtx.m_var.publicKeyBlob.array(), 0, authResp.uiBlobLen_publicKey);
                    pMisCnt.commEncCtx.ucbPublicKeyVerified = true;
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    return bRet;
                }
            }

            if (!pMisCnt.commEncCtx.ucbPublicKeyVerified) {
                if (bSessionOpen) {
                    qnmCloseSession_msg(session, sock, pSockParam.to);
                }
                return bRet;
            }

            bRet = true;


            if (bSessionOpen) qnmCloseSession_msg(session, sock, pSockParam.to);

            String str = String.format("bServConnected_mis: Connecting to %s:%d %s", (serverIp), (int) serverPort, bRet ? ("OK") : ("Failed"));
            Log.d(str);
            return bRet;

        };


        public static int waitToQuit_isCli(ctxQmc.CCtxQmc pProcInfo, int dwTimeInMs, String hint) {
            if (null == pProcInfo) return -1;

            //Log.d("异地登录");
            if (0 != pProcInfo.waitToQuit.dwTickCnt_start) return 0;
            pProcInfo.waitToQuit.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
            pProcInfo.waitToQuit.dwTimeInMs = (int) dwTimeInMs;


            pProcInfo.waitToQuit.hint = hint;

            return 0;
        }

        //
        public static int notifyPanicToApp(ctxQyMc.CCtxQyMc pQyMc, int iErr, String hint) {
            Log.i("notifyPanicToApp: not supported");
            if (null != pQyMc) {
                Log.i("notifyPanicToApp: WM_CLOSE");
                //  pMainWnd->PostMessage(  WM_CLOSE,  0,  0  );
                //GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_qyQuitMainWnd, 0);
                //
                pQyMc.b_notifyPanic = true;
                //
                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                int iDiffInMs = (int) (dwTickCnt - pQyMc.dwLastTickCnt_notifyPanic);
                iDiffInMs = Math.abs(iDiffInMs);
                if (iDiffInMs > 4000) {
                    pQyMc.dwLastTickCnt_notifyPanic = dwTickCnt;
                    pQyMc.nTimes_notifyPanic++;
                    //
                    String str = "App请退出: " + hint + " " + pQyMc.nTimes_notifyPanic;
                    //
                    showNotification(null, null, null, 0, 0, 0, str);
                }
            }

            return 0;
        }


    }
}

