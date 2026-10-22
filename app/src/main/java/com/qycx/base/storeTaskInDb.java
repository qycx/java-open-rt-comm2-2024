


package com.qycx.base;

import static com.qycx.base.isFunc.qyFuncs.idInfo2Str;
import static com.qycx.base.qnmCommProc.qyFuncs.CONST_fieldIdTable_en;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_qyMessengerIdStrLen;
import static com.qycx.base.qnmCustomComm.qyFuncs.regInfo2Desc;
import static com.qycx.base.qnmSqlScripts.Consts.CONST_tabName_qyImTaskHisTab;
import static com.qycx.base.qnmSqlScripts.Consts.CONST_tabName_qyImTaskTab;

import com.qycx.base.util.Log;

public class storeTaskInDb {
    public static class qyFuncs {

        public static class TMP_idStr {
            public byte[] idStr = new byte[CONST_qyMessengerIdStrLen + 1];
        }

        public static int storeTaskInDb(Object pDb, int iDbType, qmCommFunc_mis.MIS_CNT pMisCnt, int iTaskId_suggested, qnmCommProc_mis.IM_TASK_RCD pRcd, boolean bInHis, String hint) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            String pTabName = bInHis ? CONST_tabName_qyImTaskHisTab : CONST_tabName_qyImTaskTab;
            //byte idStr_send[CONST_qyMessengerIdStrLen + 1] = "";
            TMP_idStr idStr_send = new TMP_idStr();
            //
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            String str;

            //
            if (hint == null) hint = "";

            str = String.format("storeTaskIdDb called, {%s}", hint);
            showInfo_open.qyFuncs.showInfo_open(0, null, str);
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                if (null == idInfo2Str(pRcd.idInfo_send, idStr_send.idStr, CONST_qyMessengerIdStrLen + 1)) {
                    return iErr;
                }

                if (myDb.QM_dbFuncs.pf_bSelectImTask_storeTaskInDb.call(pDb, iDbType, pMisCnt, pRcd, bInHis)) {
//                    Log.e("storeTaskInDb: ");
                    iErr = 1;
                    return iErr;
                }
                //
                if (!bInHis) {
                    qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
                    qnmCommProc_mis.MY_REG_DESC desc = new qnmCommProc_mis.MY_REG_DESC();
                    //
                    if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pDb, iDbType, CONST_fieldIdTable_en, "", pRcd.idInfo_send, 0, null, regInfo, null, null)) //mymemset((byte[])regInfo, 0, sizeof(QY_MESSENGER_REGINFO));
                        if (0 != regInfo2Desc(null, regInfo, desc, null, 0, null, 0)) return iErr;
                    if (!desc.pSyr.isEmpty()) {
                        str = String.format("%s - %s -%s", desc.pDw, desc.pBm, desc.pSyr);
                        pRcd.senderDesc = str;
                    }
                    if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pDb, iDbType, CONST_fieldIdTable_en, "", pRcd.idInfo_recv, 0, null, regInfo, null, null)) {

                    }
                    if (0 != regInfo2Desc(null, regInfo, desc, null, 0, null, 0)) return iErr;
                    if (!desc.pSyr.isEmpty()) {
                        str = String.format("%s - %s -%s", desc.pDw, desc.pBm, desc.pSyr);
                        pRcd.receiverDesc = str;
                    }
                    //
                    pRcd.firstTime = tmpFunc.qyFuncs.mytime();
                    pRcd.lastTime = tmpFunc.qyFuncs.mytime();
                }

                String pName = pMisCnt.cfg.mutexNamePrefix_syncTask;
                if (0 != syncObj.sync(pName, pMisCnt.hMutex_syncTask,"storeTaskInDb.77")) return iErr;
                if (0 != myDb.QM_dbFuncs.pf_insertImTask_storeTaskInDb.call(pDb, iDbType, pMisCnt, iTaskId_suggested, pRcd, bInHis)) {
                    Log.e("pf_insertImTask_storeTaskInDb failed");
                    syncObj.Dispose();
                    return iErr;
                }
                if (!myDb.QM_dbFuncs.pf_bSelectImTask_storeTaskInDb.call(pDb, iDbType, pMisCnt, pRcd, bInHis)) {
                    Log.e("pf_bSelectImTask_storeTaskInDb failed");
                    syncObj.Dispose();
                    return iErr;
                }
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            iErr = 0;


            if (iErr >= 0) {

            }
            return iErr;
        }

    }
}
