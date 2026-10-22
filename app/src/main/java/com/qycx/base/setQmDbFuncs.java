


package com.qycx.base;

import static com.qycx.base.qySqlFunc.Consts.CONST_dbType_myDb;

public class setQmDbFuncs {
    public static class qyFuncs {

        //  2012/01/30
        public static int setQmDbFuncs_qm(int iDbType, myDb.QM_dbFuncs pDbFuncs) {
            int iErr = -1;

            //memset(pDbFuncs, 0, sizeof(pDbFuncs[0]));

            //
            //setQmDb_default_Funcs(pDbFuncs);

            //
            switch (iDbType) {
                case CONST_dbType_myDb:
                    //
                    myDb.QM_dbFuncs.pf_bGetImAmRcdBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetImAmRcdBySth_myDb;
                    myDb.QM_dbFuncs.pf_bGetMessengerRegInfoBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetMessengerRegInfoBySth_myDb;

                    myDb.QM_dbFuncs.pf_bGetImOtherRegInfoBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetImOtherRegInfoBySth_myDb;
                    myDb.QM_dbFuncs.pf_bGetMeInfoBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetMeInfoBySth_myDb;

                    myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetMessengerInfoBySth_myDb;

                    myDb.QM_dbFuncs.pf_insertImObj_recoverMessenger = qmCommonDbFunc_myDb_mis.qyFuncs.insertImObj_recoverMessenger_myDb;
                    myDb.QM_dbFuncs.pf_updateImObj_recoverMessenger = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_recoverMessenger_myDb;
                    myDb.QM_dbFuncs.pf_updateImObj_recoverMessenger2 = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_recoverMessenger2_myDb;


                    myDb.QM_dbFuncs.pf_insertImObj_recoverImGrp = qmCommonDbFunc_myDb_mis.qyFuncs.insertImObj_recoverImGrp_myDb;
                    myDb.QM_dbFuncs.pf_updateImObj_recoverImGrp = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_recoverImGrp_myDb;
                    myDb.QM_dbFuncs.pf_updateImObj_recoverImGrp2 = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_recoverImGrp2_myDb;

                    myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetImGrpInfoBySth_myDb;
                    myDb.QM_dbFuncs.pf_insertImGrp_recoverImGrp = qmCommonDbFunc_myDb_mis.qyFuncs.insertImGrp_recoverImGrp_myDb;
                    myDb.QM_dbFuncs.pf_updateImGrp_recoverImGrp = qmCommonDbFunc_myDb_mis.qyFuncs.updateImGrp_recoverImGrp_myDb;
                    //
                    myDb.QM_dbFuncs.pf_bGetImGrpMemBySth = qmCommonDbFunc_myDb_mis.qyFuncs.bGetImGrpMemBySth_myDb;

                    myDb.QM_dbFuncs.pf_insertImGrpMem_recoverImGrpMem = qmCommonDbFunc_myDb_mis.qyFuncs.insertImGrpMem_recoverImGrpMem_myDb;
                    myDb.QM_dbFuncs.pf_updateImGrpMem_recoverImGrpMem = qmCommonDbFunc_myDb_mis.qyFuncs.updateImGrpMem_recoverImGrpMem_myDb;
                    myDb.QM_dbFuncs.pf_updateImGrpMem_recoverImGrpMem2 = qmCommonDbFunc_myDb_mis.qyFuncs.updateImGrpMem_recoverImGrpMem2_myDb;
                    //
                    myDb.QM_dbFuncs.pf_bIdInfoExistedInImObjRegInfoTab = qmCommonDbFunc_myDb_mis.qyFuncs.bIdInfoExistedInImObjRegInfoTab_myDb;
                    myDb.QM_dbFuncs.pf_insertImObjRegInfo_qisRegUsrInfoInTab = qmCommonDbFunc_myDb_mis.qyFuncs.insertImObjRegInfo_qisRegUsrInfoInTab_myDb;
                    myDb.QM_dbFuncs.pf_updateImObjRegInfo_qisRegUsrInfoInTab = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObjRegInfo_qisRegUsrInfoInTab_myDb;
				 
                    myDb.QM_dbFuncs.pf_qisRegUsrInfo = qmCommonDbFunc_myDb_mis.qyFuncs.qisRegUsrInfo_myDb;
				 
                    myDb.QM_dbFuncs.pf_updateImObj_me = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_me_myDb;
					 
                    myDb.QM_dbFuncs.pf_updateImObj_refreshImGrps_end = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_refreshImGrps_end_myDb;
                    //
                    myDb.QM_dbFuncs.pf_bNeedRetrieveImGrpMems = qmCommonDbFunc_myDb_isCli.qyFuncs.bNeedRetrieveImGrpMems_myDb;
					 
                    myDb.QM_dbFuncs.pf_updateImGrpMem_refreshImGrpMems_end = qmCommonDbFunc_myDb_mis.qyFuncs.updateImGrpMem_refreshImGrpMems_end_myDb;

                    myDb.QM_dbFuncs.pf_updateImObj_refreshContacts_end = qmCommonDbFunc_myDb_mis.qyFuncs.updateImObj_refreshContacts_end_myDb;
					 
                    myDb.QM_dbFuncs.pf_bSelectImTask_storeTaskInDb = qmCommonDbFunc_myDb_isCli.qyFuncs.bSelectImTask_storeTaskInDb_myDb;
                    myDb.QM_dbFuncs.pf_insertImTask_storeTaskInDb = qmCommonDbFunc_myDb_isCli.qyFuncs.insertImTask_storeTaskInDb_myDb;
                    myDb.QM_dbFuncs.pf_updateTaskStatus = qmCommonDbFunc_myDb_isCli.qyFuncs.updateTaskStatus_myDb;
					 
                    myDb.QM_dbFuncs.pf_bGetImTaskRcdBySth = qmCommonDbFunc_myDb_isCli.qyFuncs.bGetImTaskRcdBySth_myDb;
                    
                    int ii = 0;
                    //
                    break;
                default:
					 

                    break;
            }


            iErr = 0;
            
            return iErr;
        }


    }
}
