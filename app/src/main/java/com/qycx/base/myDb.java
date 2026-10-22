

package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.initGenericQ;
import static com.qycx.base.myDb.Consts.*;
import static com.qycx.base.qnmSqlScripts.Consts.*;

import java.nio.ByteBuffer;

public class myDb {
    public static class Consts {
        //  2014/05/19
        public final static int CONST_uiMaxQNodes_qyImObjTab = 3000;
        public final static int CONST_uiMaxQNodes_qyImObjRegInfoTab = 3000;
        public final static int CONST_uiMaxQNodes_qyImObjRegInfoTab1 = 3000;
        public final static int CONST_uiMaxQNodes_qyImGrpInfoTab = 3000;
        public final static int CONST_uiMaxQNodes_qyImGrpMemTab = 10000;
        public final static int CONST_uiMaxQNodes_qyImObjRuleTab = 1000;
        public final static int CONST_uiMaxQNodes_qyDynBmpTab = 1000;

        public final static int CONST_uiMaxQNodes_qyImTaskTab = 1000;
        public final static int CONST_uiMaxQNodes_qyImMsgTab = 1000;

    }

    public static class QMEM_qyImObj {
        public qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        public String desc;

        public ByteBuffer toByteBuffer() {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            buffer.put(messengerInfo.toByteBuffer());

            if (desc != null) {
                byte[] bytes = desc.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            buffer.flip();
            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            buf.put(buffer);
            return buf;
        }
    }

    ;

    public static class QMEM_qyImObjRegInfo {

        public int uiType;
        //
        public int id;

        public int iCustomId;

        public qnmCommProc_mis.MSGR_ADDR addr;

        public String regTime;//[CONST_qyTimeLen + 1];
        public String auditTime;//[CONST_qyTimeLen + 1];
        public String tLastModifiedTime;//[CONST_qyTimeLen + 1];
        public String tCommitTime;//[CONST_qyTimeLen + 1];

        public String dw;//[CONST_maxNameLen + 1];
        public String bm;//[CONST_maxNameLen + 1];
        public String syr;//[CONST_maxNameLen + 1];
    }

    public interface PF_bGetImAmRcdBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, String messengerName, int id, qnmCommProc_mis.QY_imAm_RCD pObj);
    }

    public interface PF_bGetMessengerRegInfoBySth {
        boolean call(Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, String pTabName, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, int iRcdId, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo);
    }

    public interface PF_bGetImOtherRegInfoBySth {
        boolean call(Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, String pTabName, qnmCommProc_mis.MSGR_ADDR pAddr, int iRcdId, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo);
    }

    public interface PF_bGetMeInfoBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, qnmCommProc_mis.QY_MESSENGER_INFO pObj);
    }

    public interface PF_bGetMessengerInfoBySth {
        boolean call(QM_dbFuncs pDbFuncs, Object pDbParam, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, int iRcdId, qnmCommProc_mis.QY_MESSENGER_INFO pObjParam, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfoInTab1, qnmCommProc_mis.QY_MESSENGER_phoneInfo pPhoneInfo);
    }

    public interface PF_insertImObj_recoverMessenger {
        int call(QM_dbFuncs pDbFuncs, Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime);
    }

    public interface PF_updateImObj_recoverMessenger {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_updateImObj_recoverMessenger2 {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_insertImObj_recoverImGrp {
        int call(Object pDb, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime);
    }

    public interface PF_updateImObj_recoverImGrp {
        int call(Object pDb, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_updateImObj_recoverImGrp2 {
        int call(Object pDb, int iDbType, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_bGetImGrpInfoBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.IM_GRP_INFO pRcd);
    }

    public interface PF_insertImGrp_recoverImGrp {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo);
    }

    public interface PF_updateImGrp_recoverImGrp {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, int iRcdId);
    }

    public interface PF_bGetImGrpMemBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo_grp, qyDefs.QY_MESSENGER_ID pIdInfo_mem, int iRcdId, qnmCommProc_mis.IM_GRP_MEM pRcd);
    }

    public interface PF_insertImGrpMem_recoverImGrpMem {
        int call(Object pDb, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime);
    }

    public interface PF_updateImGrpMem_recoverImGrpMem {
        int call(Object pDb, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_updateImGrpMem_recoverImGrpMem2 {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime, int iRcdId);
    }

    public interface PF_bIdInfoExistedInImObjRegInfoTab {
        boolean call(Object pDbParam, int iDbType, qyDefs.QY_MESSENGER_ID pIdInfo, String tabName);
    }

    public interface PF_insertImObjRegInfo_qisRegUsrInfoInTab {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String tabName);
    }

    public interface PF_updateImObjRegInfo_qisRegUsrInfoInTab {
        int call(Object pDbParam, int iDbType, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String tabName);
    }

    public interface PF_qisRegUsrInfo {
        int call(QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pMessengerInfo, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, boolean bLogReRegEvent);
    }

    public interface PF_bGetMessengerPhoneInfoBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, int uiDevType, String wDevIdStr, qyDefs.QY_MESSENGER_ID pIdInfo, int id, qnmCommProc_mis.QY_MESSENGER_phoneInfo pRcd);
    }

    public interface PF_recoverMessengerPhoneInfo {
        int call(QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qnmCommProc_mis.MSGR_ADDR pAddr, Object p1);
    }

    public interface PF_updateImObj_me {
        int call(Object pDb, int iDbType, qnmCommProc_mis.QY_MESSENGER_INFO pObj_me);
    }

    public interface PF_bGetImObjRulesReq {
        boolean call(Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qyDefs.QY_MESSENGER_ID pIdInfo_related, String condPart, short usMaxMemsPerSnd, qnmCommProc_mis.REFRESH_imObjRules_req pReq);
    }

    public interface PF_bGetImObjRuleRcdBySth {
        boolean call(Object pDbParam, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.REFRESH_imObjRule_reqMem pMem, int iRcdId, qnmCommProc_mis.QY_imObjRule_rcd pRcd);
    }

    public interface PF_insertImObjRule_recoverImObjRules {
        int call(Object pDb, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.REFRESH_imObjRule_reqMem pReqMem, long tLastModifiedTime);
    }

    public interface PF_deleteImObjRule_recoverImObjRules {
        int call(Object pDb, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.REFRESH_imObjRule_reqMem pReqMem, int rcdId);
    }

    public interface PF_updateImObjRule_recoverImObjRules {
        int call(Object pDb, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.REFRESH_imObjRule_reqMem pReqMem, long tLastModifiedTime, int rcdId);
    }

    public interface PF_updateImObjRule_recoverImObjRules2 {
        int call(Object pDb, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.REFRESH_imObjRule_reqMem pReqMem, long tLastModifiedTime, int rcdId);
    }

    public interface PF_updateImObj_refreshImGrps_end {
        int call(Object pDb, int iDbType, String misServName, int uiObjType, long tLastModifiedTime);
    }

    public interface PF_bNeedRetrieveImGrpMems {
        boolean call(Object pDb, int iDbType, String misServName);
    }

    public interface PF_updateImGrpMem_refreshImGrpMems_end {
        int call(Object pDb, int iDbType, String misServName, long tLastModifiedTime);
    }

    public interface PF_updateImObj_refreshContacts_end {
        int call(Object pDb, int iDbType, String misServName, long tLastModifiedTime);
    }

    public interface PF_deleteOldImObjRule_mcProcResp_refreshImObjRulesReq {
        int call(Object pSubThreadInfoParam, Object pSciParam, Object pMisCntParam, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, Object pCtxParam);
    }

    public interface PF_selectNextImObjRule_mcProcResp_refreshImObjRulesReq {
        int call(Object pSubThreadInfoParam, Object pSciParam, Object pMisCntParam, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, Object pCtxParam, qyDefs.QY_MESSENGER_ID pIdInfo_related);
    }

    public interface PF_bSelectImTask_storeTaskInDb {
        boolean call(Object pDb, int iDbType, Object pMisCntParam, qnmCommProc_mis.IM_TASK_RCD pRcd, boolean bInHis);
    }

    public interface PF_insertImTask_storeTaskInDb {
        int call(Object pDb, int iDbType, Object pMisCntParam, int iTaskId_suggested, qnmCommProc_mis.IM_TASK_RCD pRcd, boolean bInHis);
    }

    public interface PF_updateTaskStatus {
        int call(Object pDb, int iStatus, int iTaskId);
    }

    public interface PF_bGetImTaskRcdBySth {
        boolean call(Object pDb, int iDbType, boolean bHisTab, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, long tSendTime, int uiTranNo, int id, qnmCommProc_mis.IM_TASK_RCD pRcd);
    }

    public interface PF_bViewImTaskList_unproced {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImTaskList_cur {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImTaskList_his {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImObjList {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewRegInfoList {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImGrpList {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImGrpMemList_isClient {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb, String docName, String wherePart);
    }

    public interface PF_bViewImObjRuleList_is {
        boolean call(qnmCommProc1.QY_OBJ_DB pObjDb);
    }

    public interface PF_bInsertPhoneGuest_procMsgTalk_work {
        boolean call(Object pDbParam, int iDbType, Object pQyMcParam, qnmCommProc_mis.MIS_MSG_TALK pMsg, Object pGuestParam);
    }

    public interface PF_bUpdateImTask_bChkQyMcEnv_mis {
        boolean call(Object pDb, int iStatus_set, int iStatus_0, int iStatus_1);
    }

    public interface PF_deleteOldDynBmp_mcProcResp_retrieveDynBmps {
        int call(Object pDb, int iDbType, Object pTimeBuf);
    }

    public interface PF_bInsertDynBmp_mcProcResp_retrieveDynBmps {
        boolean call(Object pDb, Object pMisCntParam, qnmCommProc_mis.QIS_dynBmp_info pMem, Object pTimeBuf);
    }

    public interface PF_bUpdateDynBmp_mcProcResp_retrieveDynBmps {
        boolean call(Object pDb, qnmCommProc_mis.QIS_dynBmp_info pMem, Object pTimeBuf, int rcdId);
    }

    public interface PF_bUpdateImObjRegInfo_procMsgInput_scheduler {
        boolean call(Object pDb, Object pSciParam, Object pMisCntParam, qyDefs.QY_MESSENGER_ID pIdInfo);
    }

    public interface PF_bStatImTasks {
        boolean call(Object pProcInfoParam, Object pMisCntParam, Object pDb, int iDbType);
    }

    public interface PF_bDeleteFromTabById {
        boolean call(Object pDb, int iDbType, String pTabName, int id);
    }

    public interface PF_bDeleteFromTabByIdInfo {
        boolean call(Object pDb, int iDbType, String pTabName, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo);
    }

    public interface PF_bDeleteImOtherRegInfoByAddr {
        boolean call(Object pDb, int iDbType, String misServName, String pTabName, qnmCommProc_mis.MSGR_ADDR pAddr);
    }


    public static class QM_dbFuncs {

        public static PF_bGetImAmRcdBySth pf_bGetImAmRcdBySth;

        public static PF_bGetMessengerRegInfoBySth pf_bGetMessengerRegInfoBySth;
        public static PF_bGetImOtherRegInfoBySth pf_bGetImOtherRegInfoBySth;
        public static PF_bGetMeInfoBySth pf_bGetMeInfoBySth;
        public static PF_bGetMessengerInfoBySth pf_bGetMessengerInfoBySth;
        public static PF_insertImObj_recoverMessenger pf_insertImObj_recoverMessenger;
        public static PF_updateImObj_recoverMessenger pf_updateImObj_recoverMessenger;
        public static PF_updateImObj_recoverMessenger2 pf_updateImObj_recoverMessenger2;

        public static PF_insertImObj_recoverImGrp pf_insertImObj_recoverImGrp;
        public static PF_updateImObj_recoverImGrp pf_updateImObj_recoverImGrp;
        public static PF_updateImObj_recoverImGrp2 pf_updateImObj_recoverImGrp2;
        public static PF_bGetImGrpInfoBySth pf_bGetImGrpInfoBySth;
        public static PF_insertImGrp_recoverImGrp pf_insertImGrp_recoverImGrp;
        public static PF_updateImGrp_recoverImGrp pf_updateImGrp_recoverImGrp;
        public static PF_bGetImGrpMemBySth pf_bGetImGrpMemBySth;
        public static PF_insertImGrpMem_recoverImGrpMem pf_insertImGrpMem_recoverImGrpMem;
        public static PF_updateImGrpMem_recoverImGrpMem pf_updateImGrpMem_recoverImGrpMem;
        public static PF_updateImGrpMem_recoverImGrpMem2 pf_updateImGrpMem_recoverImGrpMem2;
        public static PF_bIdInfoExistedInImObjRegInfoTab pf_bIdInfoExistedInImObjRegInfoTab;
        public static PF_insertImObjRegInfo_qisRegUsrInfoInTab pf_insertImObjRegInfo_qisRegUsrInfoInTab;
        public static PF_updateImObjRegInfo_qisRegUsrInfoInTab pf_updateImObjRegInfo_qisRegUsrInfoInTab;


        //PF_qnmRegUsrInfo pf_qnmRegUsrInfo;
        public static PF_qisRegUsrInfo pf_qisRegUsrInfo;

        public static PF_updateImObj_me pf_updateImObj_me;

        public static PF_updateImObj_refreshImGrps_end pf_updateImObj_refreshImGrps_end;
        //
        public static PF_bNeedRetrieveImGrpMems pf_bNeedRetrieveImGrpMems;
        //
        public static PF_updateImGrpMem_refreshImGrpMems_end pf_updateImGrpMem_refreshImGrpMems_end;
        //
        public static PF_updateImObj_refreshContacts_end pf_updateImObj_refreshContacts_end;

        public static PF_bSelectImTask_storeTaskInDb pf_bSelectImTask_storeTaskInDb;
        public static PF_insertImTask_storeTaskInDb pf_insertImTask_storeTaskInDb;
        public static PF_updateTaskStatus pf_updateTaskStatus;

        public static PF_bGetImTaskRcdBySth pf_bGetImTaskRcdBySth;

    }

    ;

    public static class CMyDb {
        boolean m_var_bInited;

        genericQueue.GENERIC_Q[] m_var_q_tabs;    //[20];

        //
        public genericQueue.GENERIC_Q m_var_pQ_qyImObjTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImObjRegInfoTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImObjRegInfoTab1;
        public genericQueue.GENERIC_Q m_var_pQ_qyImGrpInfoTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImGrpMemTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImObjRuleTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImTaskTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyImMsgTab;
        public genericQueue.GENERIC_Q m_var_pQ_qyDynBmpTab;


        public boolean bQyOpenDb(String connectStr) {
            boolean iErr = false;

            String pTabName;
            int uiMaxQNodes;
            int index = 0;

            //
            if (m_var_q_tabs != null) return false;
            m_var_q_tabs = new genericQueue.GENERIC_Q[15];
            for (int i = 0; i < m_var_q_tabs.length; i++) {
                m_var_q_tabs[i] = new genericQueue.GENERIC_Q();
            }

            //
            pTabName = CONST_tabName_qyImObjTab;
            uiMaxQNodes = Consts.CONST_uiMaxQNodes_qyImObjTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImObjTab = m_var_q_tabs[index];
            index++;
            pTabName = CONST_tabName_qyImObjRegInfoTab;
            uiMaxQNodes = Consts.CONST_uiMaxQNodes_qyImObjRegInfoTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImObjRegInfoTab = m_var_q_tabs[index];
            index++;
            pTabName = CONST_tabName_qyImObjRegInfoTab1;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImObjRegInfoTab1;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImObjRegInfoTab1 = m_var_q_tabs[index];
            index++;
            pTabName = CONST_tabName_qyImGrpInfoTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImGrpInfoTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImGrpInfoTab = m_var_q_tabs[index];
            index++;
            pTabName = CONST_tabName_qyImGrpMemTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImGrpMemTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImGrpMemTab = m_var_q_tabs[index];
            index++;

            pTabName = CONST_tabName_qyImObjRuleTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImObjRuleTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImObjRuleTab = m_var_q_tabs[index];
            index++;

            pTabName = CONST_tabName_qyImTaskTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImTaskTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImTaskTab = m_var_q_tabs[index];
            index++;

            pTabName = CONST_tabName_qyImMsgTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyImMsgTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyImMsgTab = m_var_q_tabs[index];
            index++;

            pTabName = CONST_tabName_qyDynBmpTab;
            uiMaxQNodes = CONST_uiMaxQNodes_qyDynBmpTab;
            if (initGenericQ(pTabName, pTabName, 0, uiMaxQNodes, m_var_q_tabs[index]) != 0)
            {
                qyCloseDb();
                return iErr;
            }
            m_var_pQ_qyDynBmpTab = m_var_q_tabs[index];
            index++;
            //
            if (index > m_var_q_tabs.length){
                qyCloseDb();
                return iErr;
            }

            iErr = true;


            if (iErr) {
                m_var_bInited = true;
            }
            if (!iErr) {
                qyCloseDb();
            }
            return iErr;
        }


        public void qyCloseDb() {
            int i;

            for (i = 0; i < m_var_q_tabs.length; i++) {
                genericQueue.qyFuncs.exitGenericQ(m_var_q_tabs[i]);
            }

            m_var_bInited = false;
        }


    }


}
