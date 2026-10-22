package com.qycx.base;

import static com.qycx.base.isProtocolFunc.qyFuncs.bMessengerIdValid;
import static com.qycx.base.qyDefs_open.Consts.CONST_qyStatus_ok;

import com.qycx.base.util.Log;


public class qmCommonDbFunc_mis {

    public static class qyFuncs {

        public static int recoverImGrp(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, boolean bNoGrpName) {
            int iErr = -1;

            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pGrpInfo) return -1;

            //  再在qyImObjTab中记录
            qnmCommProc_mis.QY_MESSENGER_INFO objInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
            //
            if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pDb, iDbType, pFieldIdTable, pGrpInfo.misServName, pGrpInfo.idInfo, 0, objInfo, null, null, null)) {

                if (0 != myDb.QM_dbFuncs.pf_insertImObj_recoverImGrp.call(pDb, iDbType, uiObjType, pGrpInfo, tLastModifiedTime))
                    return iErr;
            } else {
                if (objInfo.uiType != uiObjType || objInfo.iStatus != CONST_qyStatus_ok) {

                    if (0 != myDb.QM_dbFuncs.pf_updateImObj_recoverImGrp.call(pDb, iDbType, uiObjType, pGrpInfo, tLastModifiedTime, objInfo.iRcdId))
                        return iErr;
                } else {
                    if (0 != tLastModifiedTime) {
                        if (0 != myDb.QM_dbFuncs.pf_updateImObj_recoverImGrp2.call(pDb, iDbType, uiObjType, pGrpInfo, tLastModifiedTime, objInfo.iRcdId))
                            return iErr;
                    }
                }
            }

            //	 如果没有组名，那就只修复上面的imObjTab. 2014/02/03
            if (bNoGrpName) {
                Log.e("recoverImGrp: no grpInfo, skipped");
            } else {

                qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();

                //  再在qyImGrpInfoTab留下组名
                if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pDb, iDbType, pGrpInfo.misServName, pGrpInfo.idInfo, grpInfo)) {

                    myDb.QM_dbFuncs.pf_insertImGrp_recoverImGrp.call(pDb, iDbType, pGrpInfo);
                } else {
                    if ((null != pGrpInfo.name && 0 != tmpFunc.qyFuncs.mylstrcmpi(pGrpInfo.name, grpInfo.name))
                            || pGrpInfo.usSubtype != grpInfo.usSubtype
                            || pGrpInfo.idInfo_creator.ui64Id != grpInfo.idInfo_creator.ui64Id) {
                        myDb.QM_dbFuncs.pf_updateImGrp_recoverImGrp.call(pDb, iDbType, pGrpInfo, grpInfo.id);
                    }
                }
            }

            iErr = 0;
            
            return iErr;
        }

        //
        public static int recoverImGrpMem(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime) {
            int iErr = -1;

            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            //
            //TCHAR sqlBuf[CONST_maxSqlBufLen + 1] = "";
            //byte timeBuf[CONST_qyTimeLen + 1];

            if (null == pGrpMem) return -1;

            //getCurTime(timeBuf);

            //
            qnmCommProc_mis.IM_GRP_MEM objInfo = new qnmCommProc_mis.IM_GRP_MEM();
            //
            if (!myDb.QM_dbFuncs.pf_bGetImGrpMemBySth.call(pDb, iDbType, pGrpMem.misServName, pGrpMem.idInfo_grp, pGrpMem.idInfo_mem, 0, objInfo)) {

                //  if  (  g_dbFuncs.pf_insertImGrpMem_recoverImGrpMem(  pDb,  iDbType,  pGrpMem  )  )  goto  errLabel;
                if (0 != myDb.QM_dbFuncs.pf_insertImGrpMem_recoverImGrpMem.call(pDb, iDbType, pGrpMem, tLastModifiedTime))
                    return iErr;
            } else {
                if (objInfo.iRole != pGrpMem.iRole || objInfo.iStatus != pGrpMem.iStatus) {

                    //  if  (  g_dbFuncs.pf_updateImGrpMem_recoverImGrpMem(  pDb,  iDbType,  pGrpMem,  objInfo.id  )  )  goto  errLabel;
                    if (0 != myDb.QM_dbFuncs.pf_updateImGrpMem_recoverImGrpMem.call(pDb, iDbType, pGrpMem, tLastModifiedTime, objInfo.id))
                        return iErr;
                } else {
                    if (0 != tLastModifiedTime) {
                        if (0 != myDb.QM_dbFuncs.pf_updateImGrpMem_recoverImGrpMem2.call(pDb, iDbType, pGrpMem, tLastModifiedTime, objInfo.id))
                            return iErr;
                    }
                }
            }

            iErr = 0;
            
            return iErr;
        }


        public static int recoverMessenger(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pObj, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, long tLastModifiedTime, boolean bLog, genericQueue.GENERIC_Q pLogQ) {
            int iErr = -1;
            //
            //
            if (null == pDbFuncs) {
                //Log.e(  (  "recoverMessenger failed, p_g_dbFuncs is null"  )  );
                return -1;
            }
            myDb.QM_dbFuncs g_dbFuncs = (myDb.QM_dbFuncs) pDbFuncs;
            //
            qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();

            if (null == pObj && null == pRegInfo) return -1;


            //
            if (null == pObj) {
                //mymemset(  messengerInfo,  0,  sizeof(QY_MESSENGER_INFO  )  );
                //
                //lstrcpyn(messengerInfo.misServName, pRegInfo->addr.misServName, mycountof(messengerInfo.misServName  )  );
                messengerInfo.idInfo.ui64Id = pRegInfo.addr.idInfo.ui64Id;
                messengerInfo.uiType = pRegInfo.addr.uiObjType;        //  CONST_objType_messenger;
                messengerInfo.iStatus = 0;
                //
                pObj = messengerInfo;
            }

            if (null == pObj || !bMessengerIdValid(pObj.idInfo)){
                return iErr;
            }

            if (0 == pObj.startTime) {
                pObj.startTime = tmpFunc.qyFuncs.mytime();
            }
            if (0 == pObj.tLastModifiedTime) {
                pObj.tLastModifiedTime = tmpFunc.qyFuncs.mytime();
            }


            qnmCommProc_mis.QY_MESSENGER_INFO rcd = new qnmCommProc_mis.QY_MESSENGER_INFO();

            if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pDb, iDbType, pFieldIdTable, pObj.misServName == null ? "" : pObj.misServName, pObj.idInfo,
                    0, rcd, null, null, null)) {

                if (0 != myDb.QM_dbFuncs.pf_insertImObj_recoverMessenger.call(pDbFuncs, pDb, iDbType, pObj, tLastModifiedTime)) {
                    return iErr;
                }
            } else {
                //if  (  rcd.uiType  !=  CONST_objType_me  )
                {
                    if (rcd.uiType != pObj.uiType || rcd.iRole != pObj.iRole || pObj.iStatus != rcd.iStatus) {

                        if (0 != myDb.QM_dbFuncs.pf_updateImObj_recoverMessenger.call(pDb, iDbType, pObj, tLastModifiedTime, rcd.iRcdId))
                            return iErr;
                    } else {
                        if (0 != tLastModifiedTime) {
                            if (0 != myDb.QM_dbFuncs.pf_updateImObj_recoverMessenger2.call(pDb, iDbType, pObj, tLastModifiedTime, rcd.iRcdId))
                                return iErr;
                        }
                    }
                }
            }



            if (pRegInfo != null) {

                myDb.QM_dbFuncs.pf_qisRegUsrInfo.call(pDbFuncs, pDb, iDbType, pFieldIdTable, pObj, pRegInfo, bLog);

                //g_dbFuncs.pf_recoverMessengerPhoneInfo(pDbFuncs, pDb, iDbType,pRegInfo->addr, 0);

            }

            iErr = 0;

            return iErr;

        }

        public static int qisRegUsrInfoInTab(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pMessengerInfo, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, String tabName) {
            int iErr = -1;

            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pRegInfo) return iErr;
            if (null == pMessengerInfo) {
                if (!bMessengerIdValid(pRegInfo.addr.idInfo)) return iErr;
            } else {
                if (!bMessengerIdValid(pMessengerInfo.idInfo)) return iErr;
                pRegInfo.addr.idInfo.ui64Id = pMessengerInfo.idInfo.ui64Id;
            }

            pRegInfo.tLastModifiedTime = System.currentTimeMillis();

            if (!myDb.QM_dbFuncs.pf_bIdInfoExistedInImObjRegInfoTab.call(pDb, iDbType, pRegInfo.addr.idInfo, tabName)) {
                if (0 != myDb.QM_dbFuncs.pf_insertImObjRegInfo_qisRegUsrInfoInTab.call(pDb, iDbType, pRegInfo, tabName))
                    return iErr;
                iErr = 0;
                return iErr;
            }

            if (0 != myDb.QM_dbFuncs.pf_updateImObjRegInfo_qisRegUsrInfoInTab.call(pDb, iDbType, pRegInfo, tabName))
                return iErr;

            iErr = 0;
            return iErr;
        }


        public static int postEvent2Log_is(String misServName, int lEventType, byte[] detectedIp, byte[] ip, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.QIS_EVENT pEventInput, genericQueue.GENERIC_Q pLogQ) {
            return 0;
        }


    }
}
