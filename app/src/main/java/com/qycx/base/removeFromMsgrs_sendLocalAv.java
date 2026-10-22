


package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxMsgrs_sendTo;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.tencent.bugly.proguard.at.p;

import com.qycx.base.util.Log;

public class removeFromMsgrs_sendLocalAv {
    public static class qyFuncs {
        public static int removeFromMsgrs_sendLocalAv(ctxQmc.CCtxQmc pProcInfo,qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, qmcSharedObj.ROUTE_sendLocalAv pRoute) {
            int iErr = -1;
            int i = 0;
            //ROUTE_sendLocalAv	*	pRoute		=		NULL;


            if (pIdInfo.ui64Id == 0) return -1;

            //ctxQmc.QMC_cfg pQmcCfg = (ctxQmc.QMC_cfg)pProcInfo->get_qmc_cfg();
            //if (!pQmcCfg) return -1;


            //pRoute  =  &pSharedObj1->curRoute_sendLocalAv;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                ;
                if (0 != syncObj.sync("", pMisCnt.hMutex_syncSendAv,"removeFromMsgrs.31")) return iErr;

                if (pRoute.videoConference_idInfo_to.ui64Id == pIdInfo.ui64Id) {
                    pRoute.videoConference_idInfo_to.ui64Id = 0;
                } else {

                    if (pRoute.route.idInfo_to.ui64Id == pIdInfo.ui64Id)
                        pRoute.route.idInfo_to.ui64Id = 0;
                    else {
                        for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {
                            if (pIdInfo.ui64Id == pRoute.route.mems_to_idInfo_ui64Id[i]) {
                                pRoute.route.mems_to_idInfo_ui64Id[i] = 0;
                                break;
                            }
                        }
                    }

                    if (0 == pRoute.route.idInfo_to.ui64Id) {       //  如果idInfo_to为空,则从mems_to[]中提取一个到idInfo_to中
                        for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {
                            if (pRoute.route.mems_to_idInfo_ui64Id[i] != 0) {
                                pRoute.route.idInfo_to.ui64Id = pRoute.route.mems_to_idInfo_ui64Id[i];
                                pRoute.route.mems_to_idInfo_ui64Id[i] = 0;
                                pRoute.routeInfo.tModifiedTime = tmpFunc.qyFuncs.mytime();   //  2009/09/11

                                break;
                            }
                        }
                    }

                }

                //  更新一下修改时间戳
                pRoute.route.uiMsgRouteId = getuiNextTranNo.call();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            iErr = 0;



            if (0 == iErr) {
            }

            return iErr;
        }


    }
}
