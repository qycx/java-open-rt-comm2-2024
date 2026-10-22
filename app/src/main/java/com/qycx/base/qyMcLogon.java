


package com.qycx.base;

import static com.qycx.base.Consts.CONST_qyAppType_client;
import static com.qycx.base.ctxQyTmpl.Consts.CONST_ctxType_qmc;
import static com.qycx.base.dlgMcClientLogon.qyFuncs.dlgMcClientLogon_OnOK;
import static com.qycx.base.doApplyForPlayer.qyFuncs.chkPlayers;
import static com.qycx.base.qyMcMainCommon.qyFuncs.getProcedObjsCfg;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.loadCusModules;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.unloadCusModules;
import static com.qycx.base.qyMcMainDb.qyFuncs.exitQyMcDb;
import static com.qycx.base.qyMcMainDb.qyFuncs.initQyMcDb;
import static com.qycx.base.qySqlFunc.Consts.CONST_dbType_myDb;

import android.util.Log;

public class qyMcLogon {
//    private static final String TAG = qyMcLogon.class.getSimpleName();

    public static class qyFuncs {


        //
        public static int doMcClientLogonOK(Object hDlg, String m_server, String m_name, String m_passwd) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pQyMc.bLogon) return -1;

            if (dlgMcClientLogon_OnOK(hDlg, m_server, m_name, m_passwd) != 0) return -1;

            pQyMc.bLogon = true;
            pQyMc.cfg.db.iDbType = CONST_dbType_myDb;

            if (0 != getProcedObjsCfg(pQyMc)) return iErr;

            if (0 != initQyMcDb(null, null, pQyMc.procedObjDb)) {
                return iErr;
            }
            pQyMc.gui.pDb = pQyMc.procedObjDb.pDb;
            pQyMc.setQmDbFuncs(pQyMc.cfg.db.iDbType, pQyMc.p_g_dbFuncs);

            loadCusModules(pQyMc);

            if (pQyMc.iAppType == CONST_qyAppType_client) {
                //  要在登陆后立即运行此函数. 2011/10/22
                if (guiShare.GuiShare.pf_initPolicyAvParams.call() != 0) return iErr;
            }

            if (pProcInfo.m_iCtxType != CONST_ctxType_qmc) return iErr;

            ctxQmc.POLICY_isClient policy = new ctxQmc.POLICY_isClient();
            if (0 == guiShare.GuiShare.pf_getPolicyIsClient.call(pProcInfo, policy)) {
                pProcInfo.cfg.policy = policy;
            }
            showInfo_open.qyFuncs.showInfo_open(0, null, "doMcClientLogon: before start threadProcs");

            int i;
            for (i = 0; i < pQyMc.hDaemonThreads.length; i++) {
                if (pQyMc.threadProcs[i] != null) {

                    pQyMc.hDaemonThreads[i]=guiShare.GuiShare.pf_startThread1.call( pQyMc.threadProcs[i], pQyMc);
                }
            }
            showInfo_open.qyFuncs.showInfo_open(0, null, "doMcClientLogon: after start threadProcs");
            iErr = 0;

            return iErr;
        }


        public static void qyMcLogoff() {
            showInfo_open.qyFuncs.showInfo_open(0, null, "qyMcLogoff enters");
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            pQyMc.bLogon = false;

            qyMcMainProc.qyFuncs.exitAllDaemonThreads(pQyMc);
            pProcInfo.processQ_media.emptyQ();
            chkPlayers(pProcInfo);
            exitQyMcDb(null, null, pQyMc.procedObjDb);
            unloadCusModules(pQyMc);
            showInfo_open.qyFuncs.showInfo_open(0, null, "qyMcLogoff leaves");
        }

    }
}
