


package com.qycx.base;

import static com.qycx.base.Consts.CONST_Enable_Encryption;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.ctxQyTmpl.Consts.CONST_ctxType_qmc;
import static com.qycx.base.qmCrypt.Consts.CONST_cspContainer_qmc;
import static com.qycx.base.qmCrypt.Consts.DEFAULT_cspProvider;
import static com.qycx.base.qmcCommFunc_mis.Consts.CONST_maxTriesToStartSession_mis;
import static com.qycx.base.qmcCrypt.qyFuncs.exitCrypt_qmc;
import static com.qycx.base.qmcCrypt.qyFuncs.initCrypt_qmc;
import static com.qycx.base.qnmCommProc.qyFuncs.getCorrectCnt;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.setSessionErr;
import static com.qycx.base.qwmMd5.qyFuncs.bQyGetStrMd5_x;
import static com.qycx.base.qyCommonSock.qyFuncs.clearQySock;
import static com.qycx.base.qyCustom.qyFuncs.qnmVerStr;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_aes256;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_rsa1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.tryToStartChannel;
import static com.qycx.base.qyMsgFunc.qyFuncs.qnmCloseSession_msg;

import com.qycx.base.util.Log;

public class dlgMcClientLogon {
//    private static final String TAG = dlgMcClientLogon.class.getSimpleName();

    public static class qyFuncs {

        //
        public static class PARAM_doMcClientLogon {
            public String m_server;  //服务器地址

            public int iRet = -1;  //状态码
        }

        ;


        public static int dlgMcClientLogon_OnOK(Object hDlg, String m_server, String m_name, String m_passwd) {
            int iErr = -1;

            //
            Log.i("tn_msgInput",  "dlgMcClientLogon_OnOK");
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            if (pProcInfo.m_iCtxType != CONST_ctxType_qmc) return -1;

            if (null == m_server) return iErr;
            if (null == m_name) return iErr;
            if (null == m_passwd) return iErr;

            m_server = m_server.trim();
            m_name = m_name.trim();
            m_passwd = m_passwd.trim();

            if (m_server.isEmpty()) return iErr;
            if (m_name.isEmpty()) {
                return iErr;
            }
            if (m_passwd.isEmpty()) {
                return iErr;
            }

            pProcInfo.authInfo.messengerName = m_name;
            pProcInfo.authInfo.messengerPasswd = bQyGetStrMd5_x(m_passwd);

            PARAM_doMcClientLogon param = new PARAM_doMcClientLogon();
            param.m_server = m_server;

            Thread thread = new Thread(() -> doMcClientLogon_run.call(param));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.e(e.getMessage());
                return -1;
            }
            return param.iRet;
        }

        //
        public static qmcFunc.PF_threadProc doMcClientLogon_run = (Object obj) ->
        {
            int iErr = -1;
            PARAM_doMcClientLogon param = (PARAM_doMcClientLogon) obj;
            String m_server = param.m_server;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            pMisCnt.cfg.dynCfg.cnt.cntAddr1 = m_server.getBytes(); //ip地址
            pMisCnt.cfg.dynCfg.cnt.usCntPort = 8768;

            qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_talking);
            if (null == pChannel) {
                param.iRet = iErr;
                return;
            }

            if (pChannel.bSessionOpen.get()) {
                qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                pChannel.bSessionOpen.set(false);
            }

            clearQySock(pChannel.sock);

            pMisCnt.server.ip = m_server;

            qyObjs.QY_LOGICAL_SERVER tmpCnt = new qyObjs.QY_LOGICAL_SERVER();
            if (0 != getCorrectCnt(pQyMc.env, pMisCnt.cfg.dynCfg, qyMcMainDaemon_isCli.qyFuncs.bServConnected_mis, pMisCnt, pQyMc.iServiceId,
                    qnmVerStr(pQyMc.iServiceId), pMisCnt.commEncCtx, pQyMc.cfg.sockParam, tmpCnt)) {
                {
                    Log.e("getCorrectCnt failed ");
                    param.iRet = iErr;
                    return;
                }
            } else {
                //将正确的服务器地址信息 赋值
                pMisCnt.server = tmpCnt;
            }

            if (!pMisCnt.commEncCtx.ucbPublicKeyDownloaded || !pMisCnt.commEncCtx.ucbPublicKeyVerified) {
                return;
            }

            //
            if  (  true  ) {
                //取消加密
//            pMisCnt.commEncCtx.common.type = CONST_qyEncType_rsa1;
           // pMisCnt.commEncCtx.common.type = 0;

                pMisCnt.commEncCtx.common.type = CONST_Enable_Encryption? CONST_qyEncType_rsa1: 0;
            }

            //
            pChannel.sock = new commonSock.QY_SOCK();
            pChannel.session = new qyComm.QY_COMM_SESSION();
            param.iRet = doMisCntLogon(pMisCnt, pChannel, null, null);;
        };


        //
        public static int doMisCntLogon(qmCommFunc_mis.MIS_CNT pMisCnt, qmCommFunc_mis.MIS_CHANNEL pChannel, Object hDlg, Object pm_var) {

            int iErr = -1;

            if (null == pMisCnt) {
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            if (null == pProcInfo) {
                return -1;
            }
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            boolean bCryptInited = false;
            int i;

            if (pMisCnt.commEncCtx.common.type == CONST_qyEncType_rsa1) {
                pProcInfo.authInfo.usSessionEncType = CONST_qyEncType_aes256;
            }

            pChannel.commEncCtx = new qmCrypt.ENC_CTX_session();
            pChannel.commEncCtx.common = pMisCnt.commEncCtx.common;
            if (initCrypt_qmc(pProcInfo, pMisCnt, CONST_cspContainer_qmc, DEFAULT_cspProvider, (pChannel.uiType == CONST_channelType_talking), pChannel.commEncCtx) != 0) {
                exit(pChannel, pQyMc, pProcInfo, pMisCnt, bCryptInited);
                return iErr;
            }
            bCryptInited = true;

            String str = "qmcCrypt" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            pChannel.commEncCtx.m_var.mutexName_syncCrypt = str;


            pProcInfo.authInfo.uiLogonId = 0;

            //RecyclableMemoryStreamManager recyclableMemoryStreamManager = new RecyclableMemoryStreamManager();

            for (i = 0; i < CONST_maxTriesToStartSession_mis; i++) {
                qyMcMainDaemon_isCli.qyFuncs.RESULT_startChannel result = new qyMcMainDaemon_isCli.qyFuncs.RESULT_startChannel();
                if (0 == tryToStartChannel(pMisCnt, pChannel, result))
                    break;
            }
            if (i == CONST_maxTriesToStartSession_mis) {
                exit(pChannel, pQyMc, pProcInfo, pMisCnt, bCryptInited);
                return iErr;
            }
            //pChannel.status.ulbSessionErr = false;
            setSessionErr(pChannel,false,"doMisCntLogon.194");

            //
            iErr = 0;
            exit(pChannel, pQyMc, pProcInfo, pMisCnt, bCryptInited);
            return iErr;
        }

        private static void exit(qmCommFunc_mis.MIS_CHANNEL pChannel, ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, boolean bCryptInited) {
            //
            //pChannel.status.ulbSessionErr = true;  //  此标志位通知recv和snd两线程退出
            setSessionErr(pChannel,true,"doMisClientLogon.exit.205");

            //
            if (pChannel.bSessionOpen.get()) {
                qnmCloseSession_msg(pChannel.session, pChannel.sock, pQyMc.cfg.to);
                pChannel.bSessionOpen.set(false);
            }
            if (bCryptInited) {
                exitCrypt_qmc(pProcInfo, pMisCnt, pChannel.commEncCtx);
            }
            //pChannel.status.ulbIoQuit = true;
        }
    }
}

