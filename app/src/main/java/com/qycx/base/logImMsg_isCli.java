

package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;

public class logImMsg_isCli {
    public static class qyFuncs {

        public static int logImMsg(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.IM_MSG_RCD pRcd) {
            int iErr = -1;

            //
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            return logImMsg_isCli(pDb, iDbType, pFieldIdTable, pRcd);

        }

        public static int logImMsg_isCli(Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.IM_MSG_RCD pRcdParam) {
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            qPostMsg(pRcdParam, pDb.m_var_pQ_qyImMsgTab);
            return 0;
        }
    }
}
