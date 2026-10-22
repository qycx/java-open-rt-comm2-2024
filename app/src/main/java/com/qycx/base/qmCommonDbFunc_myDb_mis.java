


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.genericQueue.qyFuncs.qTraverse;
import static com.qycx.base.genericQueue.qyFuncs.qTraverseEx;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_messengerRegInfo;
import static com.qycx.base.qmCommonDbFunc_mis.qyFuncs.qisRegUsrInfoInTab;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxNameLen;
import static com.qycx.base.qnmSqlScripts.Consts.CONST_tabName_qyImObjRegInfoTab;
import static com.qycx.base.qyCustom.qyFuncs.qyGetCustomId;
import static com.qycx.base.qyDefs.Consts.CONST_objType_me;
import static com.qycx.base.qyDefs.Consts.CONST_objType_messenger;
import static com.qycx.base.qyDefs_open.Consts.CONST_qyStatus_ok;
import static com.qycx.base.qyWmComm.Consts.CONST_Status_Window_Function_Open;
import static com.qycx.base.tmpFunc.qyFuncs.mytcsicmp;

import static com.qycx.base.tmpFunc.qyFuncs.safeTcsnCpy;


import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.util.Objects;

public class qmCommonDbFunc_myDb_mis {
//    private static final String TAG = qmCommonDbFunc_myDb_mis.class.getSimpleName();

    public static class qyFuncs {

        /////////////////////////////////////////
        /// <summary>
        /// 
        /// </summary>
        /// <param name="p0"></param>
        /// <param name="p1"></param>
        /// <param name="p2"></param>
        /// <returns></returns>
        /// 
        public static myDb.PF_bGetImAmRcdBySth bGetImAmRcdBySth_myDb = (Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, String messengerName, int id, qnmCommProc_mis.QY_imAm_RCD pObj) -> {
            return false;
        };


        public static myDb.PF_bGetImOtherRegInfoBySth bGetImOtherRegInfoBySth_myDb = (Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, String pTabName, qnmCommProc_mis.MSGR_ADDR pAddr, int iRcdId, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo) -> {
            return false;
        };

        public static qyDefs.PF_commonHandler tmpHandler_bGetMeInfoBySth_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iErr = -1;

            //  p0
            qnmCommProc_mis.QY_MESSENGER_INFO pObj = (qnmCommProc_mis.QY_MESSENGER_INFO) p1;
            myDb.QMEM_qyImObj pQMem = (myDb.QMEM_qyImObj) p2;

            if (pQMem.messengerInfo.uiType == CONST_objType_me) {

                //qyMemoryFunc.qyFuncs.mymemcpy(pObj,  pQMem.messengerInfo, (int) sizeof(QY_MESSENGER_INFO));
                iErr = 1;
                return iErr;
            }

            iErr = 0;
            return iErr;
        };

        //  2013/02/05
        public static myDb.PF_bGetMeInfoBySth bGetMeInfoBySth_myDb = (Object pDbParam, int iDbType, String misServName, qnmCommProc_mis.QY_MESSENGER_INFO pObj) -> {
            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (qTraverse(pDb.m_var_pQ_qyImObjTab, tmpHandler_bGetMeInfoBySth_myDb, null, pObj) <= 0)
                return bRet;

            bRet = true;
            return bRet;
        };

        public static qyDefs.PF_commonHandler tmpHandler_bGetMessengerInfoBySth_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iErr = -1;
            qyDefs.QY_MESSENGER_ID pIdInfo = (qyDefs.QY_MESSENGER_ID) p0;
            myDb.QMEM_qyImObj pMem = (myDb.QMEM_qyImObj) p2;

            if (pIdInfo.ui64Id == pMem.messengerInfo.idInfo.ui64Id) {
                ((myDb.QMEM_qyImObj) p1).messengerInfo = pMem.messengerInfo;
                ((myDb.QMEM_qyImObj) p1).desc = pMem.desc;
                iErr = 1;
                return iErr;
            }

            iErr = 0;
            return iErr;
        };


        public static qyDefs.PF_commonHandler tmpHandler_bGetMessengerRegInfoBySth_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iRet = -1;
            int i;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            qyDefs.QY_MESSENGER_ID pIdInfo = (qyDefs.QY_MESSENGER_ID) p0;
            qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo = (qnmCommProc_mis.QY_MESSENGER_REGINFO) p1;
            myDb.QMEM_qyImObjRegInfo pQMem = (myDb.QMEM_qyImObjRegInfo) p2;

            if (pQMem.addr.idInfo.ui64Id == pIdInfo.ui64Id) {

                pRegInfo.uiType = CONST_imCommType_messengerRegInfo;
                pRegInfo.iCustomId = qyGetCustomId();

                pRegInfo.addr = pQMem.addr;
                pRegInfo.dw = pQMem.dw;
                pRegInfo.bm = pQMem.bm;
                pRegInfo.syr = pQMem.syr;

                iRet = 1;
                return iRet;
            }

            iRet = 0;

            return iRet;
        };


        //  2013/02/04
        public static myDb.PF_bGetMessengerRegInfoBySth bGetMessengerRegInfoBySth_myDb = (Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, String pTabName, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, int iRcdId, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo) -> {
            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pTabName) return false;
            if (null == misServName || null == pIdInfo || pIdInfo.ui64Id == 0) {
                Log.e(("bGetMessengerRegInfoBySth_myDb failed, param err"));
                return false;
            }

            if (0 == pTabName.compareTo(CONST_tabName_qyImObjRegInfoTab)) {
                if (qTraverseEx(pDb.m_var_pQ_qyImObjRegInfoTab, tmpHandler_bGetMessengerRegInfoBySth_myDb, pIdInfo, pRegInfo) <= 0)
                    return bRet;
            } else
                return bRet;

            bRet = true;
            return bRet;
        };

        public static myDb.PF_bGetMessengerInfoBySth bGetMessengerInfoBySth_myDb = (myDb.QM_dbFuncs pDbFuncs, Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable,
                                                                                    String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, int iRcdId,
                                                                                    qnmCommProc_mis.QY_MESSENGER_INFO pObjParam, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo,
                                                                                    qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfoInTab1,
                                                                                    qnmCommProc_mis.QY_MESSENGER_phoneInfo pPhoneInfo) ->
        {

            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            myDb.QMEM_qyImObj qMem = new myDb.QMEM_qyImObj();
            //Log.d("myDb   qMemqMem ");
            qnmCommProc_mis.QY_MESSENGER_INFO tmpObjInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();

            if (null == misServName || null == pIdInfo || pIdInfo.ui64Id == 0) {
                Log.e(("bGetMessengerInfoBySth_myDb failed, param err"));
                return false;
            }

            if (qTraverseEx(pDb.m_var_pQ_qyImObjTab, tmpHandler_bGetMessengerInfoBySth_myDb, pIdInfo, qMem) <= 0) {
                return bRet;
            }
            if(pObjParam!=null)
            {
                pObjParam.idInfo = qMem.messengerInfo.idInfo;
                pObjParam.uiType = qMem.messengerInfo.uiType;
                pObjParam.ip = qMem.messengerInfo.ip;
                pObjParam.iRole = qMem.messengerInfo.iRole;
                pObjParam.iStatus = qMem.messengerInfo.iStatus;
                pObjParam.clientVer = qMem.messengerInfo.clientVer;
                pObjParam.b_mantissa_clientVer = qMem.messengerInfo.b_mantissa_clientVer;
                pObjParam.detectedIp = qMem.messengerInfo.detectedIp;
                pObjParam.iRcdId = qMem.messengerInfo.iRcdId;
                pObjParam.messengerName = qMem.messengerInfo.messengerName;
                pObjParam.misServName = qMem.messengerInfo.misServName;
                pObjParam.shClientVer = qMem.messengerInfo.shClientVer;
                pObjParam.startTime = qMem.messengerInfo.startTime;
                pObjParam.tLastModifiedTime = qMem.messengerInfo.tLastModifiedTime;
                pObjParam.tTime_logon = qMem.messengerInfo.tTime_logon;
                pObjParam.uiDevType = qMem.messengerInfo.uiDevType;
                pObjParam.uiLogonId = qMem.messengerInfo.uiLogonId;
                pObjParam.usLangId = qMem.messengerInfo.usLangId;
                pObjParam.usRunningStatus = qMem.messengerInfo.usRunningStatus;
                pObjParam.wLocation = qMem.messengerInfo.wLocation;
            }

            if (pRegInfo != null) {
                if (bGetMessengerRegInfoBySth_myDb.call(pDbParam, iDbType, pFieldIdTable, CONST_tabName_qyImObjRegInfoTab, misServName, pIdInfo, 0, pRegInfo)) {
                    pRegInfo.addr.uiObjType = qMem.messengerInfo.uiType;
                    pRegInfo.addr.idInfo.ui64Id = pIdInfo.ui64Id;
                }
            }

            bRet = true;
            return bRet;
        };

        public static myDb.PF_insertImObj_recoverImGrp insertImObj_recoverImGrp_myDb = (Object pDbParam, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime) -> {

            int iErr = -1;

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            myDb.QMEM_qyImObj mem = new myDb.QMEM_qyImObj();
            mem.messengerInfo.uiType = uiObjType;
            mem.messengerInfo.idInfo.ui64Id = pGrpInfo.idInfo.ui64Id;
            mem.messengerInfo.iStatus = CONST_qyStatus_ok;
            if (0 != qPostMsg(mem, pDb.m_var_pQ_qyImObjTab))
                return iErr;

            iErr = 0;


            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateImObj_recoverImGrp_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iErr = -1;
            qnmCommProc_mis.IM_GRP_INFO pGrpInfo = (qnmCommProc_mis.IM_GRP_INFO) p0;
            int uiObjType = (int) p1;
            myDb.QMEM_qyImObj pMem = (myDb.QMEM_qyImObj) p2;

            if (pMem.messengerInfo.idInfo.ui64Id == pGrpInfo.idInfo.ui64Id) {
                pMem.messengerInfo.uiType = uiObjType;
                pMem.messengerInfo.iStatus = CONST_qyStatus_ok;
                iErr = 1;
                return iErr;
            }
            iErr = 0;

            return iErr;
        };


        public static myDb.PF_updateImObj_recoverImGrp updateImObj_recoverImGrp_myDb = (Object pDbParam, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, int iRcdId) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pGrpInfo || 0 == pGrpInfo.idInfo.ui64Id) {
                Log.e(("updateImObj_recoverImGrp_myDb failed, param err"));
                return -1;
            }

            if (qTraverse(pDb.m_var_pQ_qyImObjTab, tmpHandler_updateImObj_recoverImGrp_myDb, pGrpInfo, uiObjType) < 0)
                return iErr;
            iErr = 0;

            return iErr;
        };

        public static myDb.PF_updateImObj_recoverImGrp2 updateImObj_recoverImGrp2_myDb = (Object pDbParam, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, int iRcdId) -> {
            return 0;
        };

        public static qyDefs.PF_commonHandler tmpHandler_bGetImGrpInfoBySth_myDb = (Object p0, Object p1, Object p2) ->
        {
            int iErr = -1;
            qyDefs.QY_MESSENGER_ID pIdInfo = (qyDefs.QY_MESSENGER_ID) p0;
            qnmCommProc_mis.IM_GRP_INFO pMem = (qnmCommProc_mis.IM_GRP_INFO) p2;

            if (pMem.idInfo.ui64Id == pIdInfo.ui64Id) {
                ((qnmCommProc_mis.IM_GRP_INFO) p1).id = pMem.id;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).idInfo = pMem.idInfo;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).idInfo_creator = pMem.idInfo_creator;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).uiType = pMem.uiType;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).name = pMem.name;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).misServName = pMem.misServName;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).startTime = pMem.startTime;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).tLastModifiedTime = pMem.tLastModifiedTime;
                ((qnmCommProc_mis.IM_GRP_INFO) p1).usSubtype = pMem.usSubtype;
                iErr = 1;
                return iErr;
            }
            iErr = 0;
            return iErr;
        };

        public static myDb.PF_bGetImGrpInfoBySth bGetImGrpInfoBySth_myDb = (Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.IM_GRP_INFO pRcd) -> {
            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == misServName || null == pIdInfo || pIdInfo.ui64Id == 0) {
                Log.e("bGetImGrpInfoBySth_myDb invalid param");
                return false;
            }

            if (qTraverse(pDb.m_var_pQ_qyImGrpInfoTab, tmpHandler_bGetImGrpInfoBySth_myDb, pIdInfo, pRcd) <= 0) {
                //Log.e("Traverse ImGrpInfoTab failed");
                return bRet;
            }
            bRet = true;
            return bRet;
        };

        public static myDb.PF_insertImGrp_recoverImGrp insertImGrp_recoverImGrp_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            if (null == pGrpInfo) return -1;
            if (0 != qPostMsg(pGrpInfo, pDb.m_var_pQ_qyImGrpInfoTab)) {
                Log.e("post pGrpInfo to qyImGrpInfoTab failed");
                return iErr;
            }
            iErr = 0;
            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateImGrp_recoverImGrp_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;
            qnmCommProc_mis.IM_GRP_INFO pGrpInfo = (qnmCommProc_mis.IM_GRP_INFO) p0;
            //  p1
            qnmCommProc_mis.IM_GRP_INFO pMem = (qnmCommProc_mis.IM_GRP_INFO) p2;

            if (pGrpInfo.idInfo.ui64Id == pMem.idInfo.ui64Id) {
                safeTcsnCpy(pGrpInfo.name, pMem.name, CONST_maxNameLen + 1);
                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_updateImGrp_recoverImGrp updateImGrp_recoverImGrp_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, int iRcdId) -> {

            int iErr = -1;

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pGrpInfo || 0 == pGrpInfo.idInfo.ui64Id) {
                Log.e(("updateImGrp_recoverImGrp_myDb failed, param err"));
                return -1;
            }

            if (qTraverse(pDb.m_var_pQ_qyImGrpInfoTab, tmpHandler_updateImGrp_recoverImGrp_myDb, pGrpInfo, 0) < 0)
                return iErr;

            iErr = 0;

            return iErr;
        };

        //
        public static myDb.PF_updateImObj_refreshImGrps_end updateImObj_refreshImGrps_end_myDb = (Object pDb, int iDbType, String misServName, int uiObjType, long tLastModifiedTime) -> {
            return 0;

        };

        public static myDb.PF_updateImGrpMem_refreshImGrpMems_end updateImGrpMem_refreshImGrpMems_end_myDb = (Object pDb, int iDbType, String misServName, long tLastModifiedTime) -> {
            return 0;
        };


        public static myDb.PF_updateImObj_refreshContacts_end updateImObj_refreshContacts_end_myDb = (Object pDb, int iDbType, String misServName, long tLastModifiedTime) -> {
            return 0;
        };

        public static qyDefs.PF_commonHandler tmpHandler_bGetImGrpMemBySth_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;
            qyDefs.COMMON_PARAM pCommonParam0 = (qyDefs.COMMON_PARAM) p0;
            qnmCommProc_mis.IM_GRP_MEM pRcd = (qnmCommProc_mis.IM_GRP_MEM) p1;
            qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;

            qyDefs.QY_MESSENGER_ID pIdInfo_grp = (qyDefs.QY_MESSENGER_ID) pCommonParam0.p0;
            qyDefs.QY_MESSENGER_ID pIdInfo_mem = (qyDefs.QY_MESSENGER_ID) pCommonParam0.p1;
            int iRcdId = (int) pCommonParam0.p2;
            //

            if (null != pIdInfo_grp && null != pIdInfo_mem) {
                if (pQMem.idInfo_grp.ui64Id == pIdInfo_grp.ui64Id
                        && pQMem.idInfo_mem.ui64Id == pIdInfo_mem.ui64Id) {
                    pRcd = pQMem;
                    iErr = 1;
                    return iErr;
                }
            } else if (pQMem.id == iRcdId) {
                pRcd = pQMem;
                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_bGetImGrpMemBySth bGetImGrpMemBySth_myDb = (Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo_grp, qyDefs.QY_MESSENGER_ID pIdInfo_mem, int iRcdId, qnmCommProc_mis.IM_GRP_MEM pRcd) -> {
            boolean bRet = false;

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            qyDefs.COMMON_PARAM commonParam = new qyDefs.COMMON_PARAM();


            qyDefs.qyFuncs.MACRO_makeCommonParam3(pIdInfo_grp, pIdInfo_mem, iRcdId, commonParam);
            if (qTraverse(pDb.m_var_pQ_qyImGrpMemTab, tmpHandler_bGetImGrpMemBySth_myDb, commonParam, pRcd) <= 0)
                return bRet;


            bRet = true;

            return bRet;
        };

        public static myDb.PF_insertImGrpMem_recoverImGrpMem insertImGrpMem_recoverImGrpMem_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime) -> {
            int iErr = -1;

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;


            if (null == pGrpMem) return -1;

            //  2014/04/20
            pGrpMem.id = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            if (0 != qPostMsg(pGrpMem, pDb.m_var_pQ_qyImGrpMemTab))
                return iErr;

            iErr = 0;

            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateImGrpMem_recoverImGrpMem_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;
            qnmCommProc_mis.IM_GRP_MEM pGrpMem = (qnmCommProc_mis.IM_GRP_MEM) p0;
            //  p1
            qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;

            if (pGrpMem.idInfo_grp.ui64Id == pQMem.idInfo_grp.ui64Id && pGrpMem.idInfo_mem.ui64Id == pQMem.idInfo_mem.ui64Id) {
                pQMem.iRole = pGrpMem.iRole;
                pQMem.iStatus = pGrpMem.iStatus;
                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_updateImGrpMem_recoverImGrpMem updateImGrpMem_recoverImGrpMem_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime, int iRcdId) -> {
            int iErr = -1;
            //TCHAR sqlBuf[CONST_maxSqlBufLen + 1] = "";
            //char timeBuf[CONST_qyTimeLen + 1];

            if (null == pGrpMem) return -1;
            if (0 == pGrpMem.idInfo_grp.ui64Id || 0 == pGrpMem.idInfo_mem.ui64Id) {
                Log.e(("updateImGrpMem_recoverImGrpMem_myDb failed, param err"));
                return -1;
            }

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (qTraverse(pDb.m_var_pQ_qyImGrpMemTab, tmpHandler_updateImGrpMem_recoverImGrpMem_myDb, pGrpMem, 0) < 0)
                return iErr;

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_updateImGrpMem_recoverImGrpMem2 updateImGrpMem_recoverImGrpMem2_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime, int iRcdId) -> {
            return 0;
        };

        //
        public static myDb.PF_insertImObj_recoverMessenger insertImObj_recoverMessenger_myDb = (myDb.QM_dbFuncs pDbFuncs, Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime) -> {
            int iErr = -1;

            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            myDb.QMEM_qyImObj qMem = new myDb.QMEM_qyImObj();
            qMem.messengerInfo = pObj;

            if (0 != qPostMsg(qMem, pDb.m_var_pQ_qyImObjTab)) {
                Log.e("insertImObj_recoverMessenger_myDb qPostMsg failed");
                return iErr;
            }

            iErr = 0;
            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateImObj_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;
            qnmCommProc_mis.QY_MESSENGER_INFO pObj = (qnmCommProc_mis.QY_MESSENGER_INFO) p0;
            //  p1
            myDb.QMEM_qyImObj pMem = (myDb.QMEM_qyImObj) p2;

            if (pMem.messengerInfo.idInfo.ui64Id == pObj.idInfo.ui64Id) {
                pMem.messengerInfo.uiType = pObj.uiType;
                //
                pMem.messengerInfo.iRole = pObj.iRole;    //  2017/08/14
                //
                pMem.messengerInfo.iStatus = pObj.iStatus;
                iErr = 1;
                return iErr;
            }

            iErr = 0;


            return iErr;
        };

        public static myDb.PF_updateImObj_recoverMessenger updateImObj_recoverMessenger_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime, int iRcdId) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pObj || 0 == pObj.idInfo.ui64Id) {
                Log.e(("updateImObj_recoverMessenger_myDb failed, param err"));
                return -1;
            }

            if (qTraverse(pDb.m_var_pQ_qyImObjTab, tmpHandler_updateImObj_myDb, pObj, 0) < 0)
                return iErr;

            iErr = 0;


            return iErr;
        };

        public static myDb.PF_updateImObj_recoverMessenger2 updateImObj_recoverMessenger2_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime, int iRcdId) -> {
            return 0;
        };

        public static myDb.PF_qisRegUsrInfo qisRegUsrInfo_myDb = (myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pMessengerInfo, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, boolean bLogReRegEvent) -> {
            int iErr = -1;

            if (0 != qisRegUsrInfoInTab(pDbFuncs, pDb, iDbType, pFieldIdTable, pMessengerInfo, pRegInfo, CONST_tabName_qyImObjRegInfoTab))
                return iErr;

            iErr = 0;


            if (CONST_Status_Window_Function_Open == 0) {
                Log.i("qisRegUsrInfo leaves");
            }
            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_idInfoExistedInImObjRegInfoTab_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;

            qyDefs.QY_MESSENGER_ID pIdInfo = (qyDefs.QY_MESSENGER_ID) p0;
            // p1
            myDb.QMEM_qyImObjRegInfo pQMem = (myDb.QMEM_qyImObjRegInfo) p2;

            if (pQMem.addr.idInfo.ui64Id == pIdInfo.ui64Id) {

                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_bIdInfoExistedInImObjRegInfoTab bIdInfoExistedInImObjRegInfoTab_myDb = (Object pDbParam, int iDbType, qyDefs.QY_MESSENGER_ID pIdInfo, String tabName) -> {
            boolean bRet = false;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pIdInfo || pIdInfo.ui64Id == 0) return false;

            String str_name0 = pDb.m_var_pQ_qyImObjRegInfoTab.cfg.name, str_name1 = pDb.m_var_pQ_qyImObjRegInfoTab1.cfg.name;

            if (0 == mytcsicmp(str_name0, tabName)) {
                if (qTraverse(pDb.m_var_pQ_qyImObjRegInfoTab, tmpHandler_idInfoExistedInImObjRegInfoTab_myDb, pIdInfo, 0) <= 0)
                    return bRet;
            } else if (0 == mytcsicmp(str_name1, tabName)) {
                if (qTraverse(pDb.m_var_pQ_qyImObjRegInfoTab1, tmpHandler_idInfoExistedInImObjRegInfoTab_myDb, pIdInfo, 0) <= 0)
                    return bRet;
            } else {
                return bRet;
            }

            bRet = true;

            return bRet;
        };


        public static myDb.PF_insertImObjRegInfo_qisRegUsrInfoInTab insertImObjRegInfo_qisRegUsrInfoInTab_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String tabName) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;
            myDb.QMEM_qyImObjRegInfo qMem = new myDb.QMEM_qyImObjRegInfo();

            if (null == pRegInfo) return -1;

            qMem.uiType = pRegInfo.uiType;
            qMem.iCustomId = pRegInfo.iCustomId;
            qMem.addr = pRegInfo.addr;
            qMem.dw = pRegInfo.dw;
            qMem.bm = pRegInfo.bm;
            qMem.syr = pRegInfo.syr;

            String str_name0 = pDb.m_var_pQ_qyImObjRegInfoTab.cfg.name, str_name1 = pDb.m_var_pQ_qyImObjRegInfoTab1.cfg.name;

            if (0 == mytcsicmp(str_name0, tabName)) {
                if (0 != qPostMsg(qMem, pDb.m_var_pQ_qyImObjRegInfoTab))
                    return iErr;
            } else if (0 == mytcsicmp(str_name1, tabName)) {
                if (0 != qPostMsg(qMem, pDb.m_var_pQ_qyImObjRegInfoTab1))
                    return iErr;
            } else return iErr;

            iErr = 0;
            return iErr;
        };

        public static qyDefs.PF_commonHandler tmpHandler_updateImObjRegInfo_qisRegUsrInfoInTab_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;

            qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo = (qnmCommProc_mis.QY_MESSENGER_REGINFO) p0;
            // p1
            myDb.QMEM_qyImObjRegInfo pQMem = (myDb.QMEM_qyImObjRegInfo) p2;

            if (pQMem.addr.idInfo.ui64Id == pRegInfo.addr.idInfo.ui64Id) {

                //memcpy(pQMem->myRegInfo.uiFieldIds, pRegInfo->uiFieldIds, sizeof(pQMem->myRegInfo.uiFieldIds)  );
                //memcpy(pQMem->myRegInfo.u.cols, pRegInfo->u.cols, sizeof(pQMem->myRegInfo.u.cols)  );
                pQMem.dw = pRegInfo.dw;//, CONST_maxNameLen + 1);
                pQMem.bm = pRegInfo.bm;//, CONST_maxNameLen + 1);
                pQMem.syr = pRegInfo.syr;//, CONST_maxNameLen + 1);

                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        };

        public static myDb.PF_updateImObjRegInfo_qisRegUsrInfoInTab updateImObjRegInfo_qisRegUsrInfoInTab_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String tabName) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (null == pRegInfo) return -1;

            String str_name0 = pDb.m_var_pQ_qyImObjRegInfoTab.cfg.name, str_name1 = pDb.m_var_pQ_qyImObjRegInfoTab1.cfg.name;

            if (0 == mytcsicmp(str_name0, tabName)) {
                if (qTraverse(pDb.m_var_pQ_qyImObjRegInfoTab, tmpHandler_updateImObjRegInfo_qisRegUsrInfoInTab_myDb, pRegInfo, 0) < 0)
                    return iErr;
            } else if (0 == mytcsicmp(str_name1, tabName)) {
                if (qTraverse(pDb.m_var_pQ_qyImObjRegInfoTab1, tmpHandler_updateImObjRegInfo_qisRegUsrInfoInTab_myDb, pRegInfo, 0) < 0)
                    return iErr;
            } else return iErr;

            iErr = 0;

            return iErr;
        };


        //
        /////////////
        public static qyDefs.PF_commonHandler tmpHandler_updateImObj_me_myDb = (Object p0, Object p1, Object p2) -> {
            int iErr = -1;
            qnmCommProc_mis.QY_MESSENGER_INFO pObj_me = (qnmCommProc_mis.QY_MESSENGER_INFO) p0;
            //  p1
            myDb.QMEM_qyImObj pMem = (myDb.QMEM_qyImObj) p2;

            if (pObj_me.idInfo.ui64Id == pMem.messengerInfo.idInfo.ui64Id) {
                if (pMem.messengerInfo.uiType != CONST_objType_me) {
                    pMem.messengerInfo.uiType = CONST_objType_me;
                }
            } else {
                if (pMem.messengerInfo.uiType == CONST_objType_me) {
                    pMem.messengerInfo.uiType = CONST_objType_messenger;
                }
            }

            iErr = 0;
            return iErr;
        };

        public static myDb.PF_updateImObj_me updateImObj_me_myDb = (Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj_me) -> {
            int iErr = -1;
            myDb.CMyDb pDb = (myDb.CMyDb) pDbParam;

            if (qTraverseEx(pDb.m_var_pQ_qyImObjTab, tmpHandler_updateImObj_me_myDb, pObj_me, 0) < 0)
                return iErr;

            iErr = 0;

            return iErr;
        };
    }
}
