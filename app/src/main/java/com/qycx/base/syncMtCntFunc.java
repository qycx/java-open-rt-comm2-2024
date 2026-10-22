package com.qycx.base;


import static com.qycx.base.syncMtCntFunc.Consts.CONST_mtxName_syncMtCnt_wLock;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class syncMtCntFunc {


    public static class Consts {
        public final static String CONST_mtxName_syncMtCnt_wLock = ("syncMtCnt_wLock");

    }

    public static class qyFuncs {

        public static int syncMtCnt_start(syncMtCntDefs.SYNC_mt_cnt pMtCnt, qySyncCommProc.PF_getuiNextTranNo pf_getuiNextTranNo) {
            if (null == pf_getuiNextTranNo) return -1;

            pMtCnt.ulTranNo = pf_getuiNextTranNo.call();
            if (0 == pMtCnt.ulTranNo) {
                pMtCnt.ulTranNo = pf_getuiNextTranNo.call();
            }
            if (0 == pMtCnt.ulTranNo) return -1;
            return 0;
        }

        public static int syncMtCnt_rLock(syncMtCntDefs.SYNC_mt_cnt pMtCnt, qySyncCommProc.CQySyncCnt psyncCnt, String hint) {
            int iErr = -1;

            if (null == pMtCnt) return -1;
            if (null == psyncCnt) return -1;

            long tn_taskInfo = pMtCnt.ulTranNo;
            if (0 == tn_taskInfo) return iErr;

            if (psyncCnt.sync(pMtCnt.lCnt_used, hint) != 0) return iErr;

            if (tn_taskInfo != pMtCnt.ulTranNo) return iErr;

            iErr = 0;


            return iErr;
        }


        //  2015/08/31
        //
        static int tmpFunc_syncMtCnt_wLock(syncMtCntDefs.SYNC_mt_cnt pMtCnt) {
            qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj();

            //
            if (null == pMtCnt) return -1;
            //if  (  !ptn  )  return  -1;

            //
            pMtCnt.ulTranNo = 0;
            if (pMtCnt.lCnt_used[0] != 0) return -1;
            return 0;
        }


        //  2015/08/31
        //
        public static int syncMtCnt_wLock_noWait(syncMtCntDefs.SYNC_mt_cnt pMtCnt, String mutexName, Semaphore hMutex, qySyncObj.CQySyncObj pSyncObj, long ptn) {
            //CQySyncObj	syncObj;

            //
            if (null == pMtCnt) return -1;
            if (null == pSyncObj) return -1;
            //if  (  !ptn  )  return  -1;

            //
            if (null == mutexName)
                mutexName = CONST_mtxName_syncMtCnt_wLock;  //  (  "syncMtCnt_wLock"  );

            if (pSyncObj.sync(mutexName, hMutex,"syncMtCnt_wLock_noWait.82") != 0) return -1;

            //
            if (ptn != 0) {
                ptn = pMtCnt.ulTranNo;
            }


            return tmpFunc_syncMtCnt_wLock(pMtCnt);
        }


        //
        public static int syncMtCnt_wLock_wait(syncMtCntDefs.SYNC_mt_cnt pMtCnt, String mutexName, Semaphore hMutex, qySyncObj.CQySyncObj pSyncObj, int ptn_unused, String hint) {
            int iErr = -1;

            if (null == pSyncObj) return -1;

            if (null == hint) hint = "";


            //
            if (null == mutexName)
                mutexName = CONST_mtxName_syncMtCnt_wLock;  //  (  "syncMtCnt_wLock"  );

            if (pSyncObj.sync(mutexName, hMutex,"syncMtCnt_wLock_wait.107") != 0) return -1;


            //
            for (; ; ) {
                if (0 == tmpFunc_syncMtCnt_wLock(pMtCnt)) break;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e(e.getMessage());
                    return -1;
                }
                //
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsCli"), 0, ("syncMt"), "", ("%s"), hint);
            }

            iErr = 0;

            return iErr;
        }
    }


}
