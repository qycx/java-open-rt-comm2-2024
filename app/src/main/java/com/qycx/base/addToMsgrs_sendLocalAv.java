package com.qycx.base;

import com.qycx.base.ctxQmc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qyDefs;
import com.qycx.base.qySyncObj;
import com.qycx.base.tmpFunc;

import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxMsgrs_sendTo;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;

public class addToMsgrs_sendLocalAv {
    public static class qyFuncs {
        public static int setROUTE_sendLocalAv(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo,
                                               boolean ucbVideoConferenceStarter, qmcSharedObj.ROUTE_sendLocalAv pRouteParam) {
            int iErr = -1;
            qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj();
            
            int i = 0;
            //ROUTE_sendLocalAv	*	pRoute		=		NULL;


            if (null == pProcInfo) return -1;
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;

            if (null == pMisCnt)
                return -1;    //  这里允许pIdInfo或者pIdInfo->ui64Id为0，表示将不是在处理dlgTalk.不需要发送数据.

            qmcSharedObj.ROUTE_sendLocalAv pRoute = pRouteParam;
            {

                {
                    if (ucbVideoConferenceStarter) {
                        pRoute.videoConference_idInfo_to.ui64Id = pIdInfo.ui64Id;
                    } else {
                        pRoute.route.idInfo_to.ui64Id = pIdInfo.ui64Id;
                        pRoute.routeInfo.tModifiedTime = tmpFunc.qyFuncs.mytime();                                           //  2009/09/11
                    }
                }
                pRoute.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                pRoute.route.uiMsgRouteId = getuiNextTranNo.call();
            }


            iErr = 0;



            if (0 == iErr) {
            }
            return iErr;
        }


        //

        public static int addToMsgrs_sendLocalAv(ctxQmc.CCtxQmc pProcInfo,qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID idInfo, boolean ucbVideoConferenceStarter, qmcSharedObj.ROUTE_sendLocalAv route) {
            int iErr = -1;
            qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj();
            int i = 0;
            //ROUTE_sendLocalAv	*	pRoute		=		NULL;

            qyDefs.QY_MESSENGER_ID pIdInfo =idInfo;
            qmcSharedObj.ROUTE_sendLocalAv pRoute = route;
            {
                if (null == pMisCnt || null == pIdInfo || pIdInfo.ui64Id == 0) return -1;
                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
                if (ucbVideoConferenceStarter) {
                    if (0 == pRoute.videoConference_idInfo_to.ui64Id)
                        pRoute.videoConference_idInfo_to.ui64Id = pIdInfo.ui64Id;
                    else if (pRoute.videoConference_idInfo_to.ui64Id != pIdInfo.ui64Id) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("addToMsgrs_sendLocalAv failed: ±¾µØÊÓÆµÒÑ¾­¼ÓÈëÊÓÆµ»áÒéÁË£¬Ö»ÄÜ¼ÓÈëÒ»¸ö."));
						return iErr;
                    }
                    //
                } else {
                    if (pRoute.videoConference_idInfo_to.ui64Id == pIdInfo.ui64Id) {               //  ÒÑÔÚ·¢ËÍÕßÖÐÁË
                        iErr = 0; return iErr;
                    }

                    if (pRoute.route.idInfo_to.ui64Id == pIdInfo.ui64Id) {                           //  ÒÑÔÚ·¢ËÍÕßÖÐÁË
                        iErr = 0; return iErr;
                    }
                    for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {
                        if (pIdInfo.ui64Id == pRoute.route.mems_to_idInfo_ui64Id[i]) {               //  ÒÑÔÚ·¢ËÍÕßÖÐÁË
                            iErr = 0; return iErr;
                        }
                    }

                    //  ÏÂÃæÒªÕÒ¸ö¿ÕÎ»ÖÃ´æ½øÈ¥
                    if (0 == pRoute.route.idInfo_to.ui64Id) {                                               //  ÕÒµ½·¢ËÍµÄÎ»ÖÃ
                        pRoute.route.idInfo_to.ui64Id = pIdInfo.ui64Id;
                        pRoute.routeInfo.tModifiedTime = tmpFunc.qyFuncs.mytime();
                        ;                                       //  2009/09/11
                    } else {
                        for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {           //  
                            if (0 == pRoute.route.mems_to_idInfo_ui64Id[i]) break;
                        }
                        if (i == CONST_maxMsgrs_sendTo) {

							return iErr;
                        }
//                        pRoute.route.mems_to_idInfo_ui64Id[i] = pIdInfo.ui64Id;
                        //mytime(pRoute->routeInfo.mems[i].tModifiedTime);                           //  2009/
                    }
                }
                pRoute.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                pRoute.route.uiMsgRouteId = getuiNextTranNo.call();
            }

            iErr = 0;



            if (0 == iErr) {
            }
            return iErr;
        }

    }
}
