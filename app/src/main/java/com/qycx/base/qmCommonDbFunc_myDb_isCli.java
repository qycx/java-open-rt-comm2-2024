


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.genericQueue.qyFuncs.qTraverse;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.newTaskId;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs.qyFuncs.MACRO_makeCommonParam3;
import static com.qycx.base.qyDefs_open.Consts.CONST_qyStatus_del;

public class qmCommonDbFunc_myDb_isCli {
    public static class qyFuncs {

        public static qyDefs.PF_commonHandler tmpHandler_bNeedRetrieveImGrpMems_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iErr = -1;
            //  p0
            //  p1
            myDb.QMEM_qyImObj pMem = (myDb.QMEM_qyImObj) p2;

            if (pMem.messengerInfo.uiType == CONST_objType_imGrp
                    && pMem.messengerInfo.iStatus != CONST_qyStatus_del) {
                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_bNeedRetrieveImGrpMems bNeedRetrieveImGrpMems_myDb = (Object pDbParam, int iDbType, String misServName) ->
        {
            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;


            boolean bImGrpExists = false;
            if (qTraverse(pDb.m_var_pQ_qyImObjTab, tmpHandler_bNeedRetrieveImGrpMems_myDb, 0, 0) > 0)
                bRet = true;

            return bRet;
        };


        public static qyDefs.PF_commonHandler tmpHandle_bGetImTaskRcdBySth_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iRet = -1;

            qyDefs.COMMON_PARAM pCommonParam = (qyDefs.COMMON_PARAM) p0;
            //
            qyDefs.QY_MESSENGER_ID pIdInfo = (qyDefs.QY_MESSENGER_ID) pCommonParam.p0;
            int uiTranNo = (int) pCommonParam.p1;
            int id = (int) pCommonParam.p2;
            //
            qnmCommProc_mis.IM_TASK_RCD pRcd = (qnmCommProc_mis.IM_TASK_RCD) p1;
            qnmCommProc_mis.IM_TASK_RCD pQMem = (qnmCommProc_mis.IM_TASK_RCD) p2;

            if (null != pIdInfo && pIdInfo.ui64Id != 0) {
                if (pQMem.idInfo_send.ui64Id == pIdInfo.ui64Id
                        && pQMem.uiTranNo == uiTranNo) {
                    pQMem.copyData(pRcd);
					iRet = 1; return iRet;
                }
            } else if (pQMem.id == id) {
                if (pRcd != null) {
                    pQMem.copyData(pRcd);
                    iRet = 1;
                }
                return iRet;
            }

            iRet = 0;

            return iRet;
        };

        //  2013/02/06
        public static myDb.PF_bGetImTaskRcdBySth bGetImTaskRcdBySth_myDb = (Object pDb, int iDbType, boolean bHisTab, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, long tSendTime, int uiTranNo, int id, qnmCommProc_mis.IM_TASK_RCD pRcd) ->
        {
            boolean bRet = false;
            myDb.CMyDb pMyDb = (myDb.CMyDb) pDb;
            qyDefs.COMMON_PARAM commonParam0 = new qyDefs.COMMON_PARAM();

            if (bHisTab) return bRet;
            MACRO_makeCommonParam3(pIdInfo, uiTranNo, id, commonParam0);


            if (qTraverse(pMyDb.m_var_pQ_qyImTaskTab, tmpHandle_bGetImTaskRcdBySth_myDb, commonParam0, pRcd) <= 0)
                return bRet;


            //
            bRet = true;


            return bRet;
        };

        public static qyDefs.PF_commonHandler tmpHandle_selectImTask_storeTaskInDb_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iRet = -1;

            //  p0
            qnmCommProc_mis.IM_TASK_RCD pRcd = (qnmCommProc_mis.IM_TASK_RCD) p1;
            qnmCommProc_mis.IM_TASK_RCD pQMem = (qnmCommProc_mis.IM_TASK_RCD) p2;

            if (pQMem.idInfo_send.ui64Id == pRcd.idInfo_send.ui64Id
                    //&&  pQMem->idInfo_recv.ui64Id  ==  pRcd->idInfo_recv.ui64Id	//  2015/09/09.
                    && pQMem.tSendTime == pRcd.tSendTime
                    && pQMem.uiTranNo == pRcd.uiTranNo) {
                pRcd.id = pQMem.id;
                pRcd.iStatus = pQMem.iStatus;

                iRet = 1;
                return iRet;
            }

            iRet = 0;

            return iRet;
        };

        public static myDb.PF_bSelectImTask_storeTaskInDb bSelectImTask_storeTaskInDb_myDb = (Object pDb, int iDbType, Object pMisCntParam, qnmCommProc_mis.IM_TASK_RCD pRcd, boolean bInHis) ->
        {
            boolean bRet = false;
            myDb.CMyDb pMyDb = (myDb.CMyDb) pDb;

            if (bInHis) return bRet;

            if (qTraverse(pMyDb.m_var_pQ_qyImTaskTab, tmpHandle_selectImTask_storeTaskInDb_myDb, null, pRcd) <= 0)
                return bRet;

            bRet = true;


            return bRet;
        };

        public static myDb.PF_insertImTask_storeTaskInDb insertImTask_storeTaskInDb_myDb = (Object pDb, int iDbType, Object pMisCntParam, int iTaskId_suggested, qnmCommProc_mis.IM_TASK_RCD pRcd, boolean bInHis) ->
        {
            int iErr = -1;
            myDb.CMyDb pMyDb = (myDb.CMyDb) pDb;

            if (bInHis) return 0;
            if (null == pRcd) return -1;

            //  2013/02/05
            if (iTaskId_suggested != 0) pRcd.id = iTaskId_suggested;
            else {
                pRcd.id = newTaskId(pDb, iDbType);
            }

            if (0 != qPostMsg(pRcd, pMyDb.m_var_pQ_qyImTaskTab)) return iErr;
            iErr = 0;

            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateTaskStatus_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iRet = -1;

            int iStatus = (int) p0;
            int iTaskId = (int) p1;
            qnmCommProc_mis.IM_TASK_RCD pQMem = (qnmCommProc_mis.IM_TASK_RCD) p2;

            if (pQMem.id == iTaskId) {
                pQMem.iStatus = iStatus;
                //  2014/05/01
                pQMem.tLastModifiedTime = tmpFunc.qyFuncs.mytime();
                //
                iRet = 1;
                return iRet;
            }

            iRet = 0;

            return iRet;
        };

        //  2013/02/05
        public static myDb.PF_updateTaskStatus updateTaskStatus_myDb = (Object pDb, int iStatus, int iTaskId) ->
        {
            int iErr = -1;
            myDb.CMyDb pMyDb = (myDb.CMyDb) pDb;

            if (qTraverse(pMyDb.m_var_pQ_qyImTaskTab, tmpHandler_updateTaskStatus_myDb, iStatus, iTaskId) < 0)
                return iErr;

            iErr = 0;


            return iErr;
        };


    }
}
