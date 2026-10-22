


package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;

import com.qycx.base.statuscenter.MeetingStatusCenter;
import com.qycx.base.util.Log;

public class qnmMainFunc_isCli {
    public static class qyFuncs {
        //  2014/10/29
        public static int newTaskId(Object pDb, int iDbType) {
            return (int) getuiNextTranNo.call();
        }

        public static qmCommFunc_mis.MIS_CHANNEL getChannelByType(qmCommFunc_mis.MIS_CNT pMisCnt, int uiChannelType) {
            int i;

            if (null == pMisCnt) return null;

            if (0 == uiChannelType) return pMisCnt.channels[0];

            if (pMisCnt.channels == null) return null;

            for (i = 0; i < pMisCnt.channels.length; i++) {
                if (pMisCnt.channels[i] != null && pMisCnt.channels[i].uiType == uiChannelType) {
                    return pMisCnt.channels[i];
                }
            }

            return pMisCnt.channels[0];
        }

        /**
         * output channel err log
         * @param pChannel
         * @param log
         * zzq
         */
        public static void channelLog(qmCommFunc_mis.MIS_CHANNEL pChannel, String log) {
            if (null == pChannel) {
                Log.d("channelLog",VIDEO_DOWNLOAD + " " +AUDIO_DOWNLOAD + " " + VIDEO_UPLOAD + " " + AUDIO_UPLOAD+ " "+ log);
                return;
            }
            if (pChannel.uiType != CONST_channelType_media && pChannel.uiType != CONST_channelType_realTimeMedia) {
                return;
            }

            Log.d("channelLog",VIDEO_DOWNLOAD + " " +AUDIO_DOWNLOAD + " " + VIDEO_UPLOAD + " " + AUDIO_UPLOAD+ " "+ log);
        }

        //
        public static int setSessionErr(qmCommFunc_mis.MIS_CHANNEL pChannel,  boolean bErr, String hint)
        {
            if  (  pChannel==null)  return  -1;
            pChannel.status.ulbSessionErr1=bErr;
            //
            if  (  true  )  {
                Log.d("qy","setSessionErr: "  +  bErr  +  " "  + hint);
            }

            //
            return  0;
        }

        //
        public static boolean bChannelOk(qmCommFunc_mis.MIS_CHANNEL pChannel, boolean bDbgParam) {
            if (null == pChannel) {
                channelLog(pChannel , "channel pChannel is not ok");
                return false;
            }
            qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
            if (null == pMisCnt) {
                channelLog(pChannel , "channel pMisCnt is not ok");
                return false;
            }
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            if (null == pProcInfo) {
                channelLog(pChannel , "channel pProcInfo is not ok");
                return false;
            }
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            if (null == pQyMc) {
                channelLog(pChannel , "channel pQyMc is not ok");
                return false;
            }

            //
            boolean bDbg = false;

            //
            if (pChannel.bSessionOpen.get()) {
                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount64(null);
                //
                if (pChannel.uiType != CONST_channelType_talking) {

                    //
                    if (dwTickCnt - pChannel.status.dwTickCnt_lastStartToRecv > pQyMc.cfg.iTimeoutInMs_channelIdle_others
                            && dwTickCnt - pChannel.status.dwTickCnt_lastEndSending > pQyMc.cfg.iTimeoutInMs_channelIdle_others) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("Channel %s timeout, idle too long, iTimeoutInMs_channelsIdle_others %d"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType), pQyMc->cfg.iTimeoutInMs_channelIdle_others);
                        channelLog(pChannel , "channel 1 is not ok");
                        return false;
                    }

                } else {
                    if (dwTickCnt - pChannel.status.dwTickCnt_lastStartToRecv > pQyMc.cfg.iTimeoutInMs_channelIdle_recv_talk) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("Channel %s timeout, idle too long, iTimeoutInMs_channelsIdle_recv_talk %d"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType), pQyMc->cfg.iTimeoutInMs_channelIdle_recv_talk);
                        channelLog(pChannel , "channel 2 is not ok");
                        return false;
                    }
                    //  2017/10/03. 当talk通道堵塞时，应尽快重新连接
                    if (dwTickCnt - pProcInfo.status.dwTickCnt_sendTo_applyForOrgReq < 5000
                            && pProcInfo.status.dwTickCnt_sendTo_applyForOrgReq > pChannel.status.dwTickCnt_lastStartToRecv + 10000) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("Channel %s timeout, idle too long, dwTickCnt_sendTo_applyForOrgReq > dwTickCnt_lastStartToRecv + 10000"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType));
                        channelLog(pChannel , "channel 3 is not ok");
                        return false;
                    }
                    //  2012/05/21.以下为了防止误判,用另一个变量做了一个辅助. 这样让判断至少在超时2次后才生效
                    boolean tmp_bOk = true;
                    if (!isQ2Empty(pChannel.toSendQ2)) {   //  2012/05/21
                        if (dwTickCnt - pChannel.status.dwTickCnt_lastEndSending > pQyMc.cfg.iTimeoutInMs_channelIdle_others) {
                            tmp_bOk = false;
                        }
                    }
                    if (tmp_bOk) pChannel.status.dwTickCnt_noSending_start = 0;
                    if (pChannel.status.dwTickCnt_noSending_start != 0) {
                        if (dwTickCnt - pChannel.status.dwTickCnt_noSending_start > pQyMc.cfg.iTimeoutInMs_channelIdle_others + 5000) {
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("Channel %s timeout, idle too long, iTimeoutInMs_channelsIdle_others %d"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType), pQyMc->cfg.iTimeoutInMs_channelIdle_others);
                            channelLog(pChannel , "channel 4 is not ok");
                            return false;
                        }
                    }
                    if (!tmp_bOk) {
                        if (0 == pChannel.status.dwTickCnt_noSending_start) {
                            pChannel.status.dwTickCnt_noSending_start = dwTickCnt;
                        }
                    }
                }
            }

            if (pQyMc.b_notifyPanic) {
                channelLog(pChannel , "channel 5 is not ok");
                return false;
            }

            //
            //boolean result = (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli && !pChannel.status.ulbSessionErr);
            boolean result = (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli && pChannel.bSessionOpen.get() && !pChannel.status.ulbSessionErr1);
            if(!result){
                if  (  false  ) {
                }
            }
            //
            return result;
            //return (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli && pChannel.bSessionOpen && !pChannel.status.ulbSessionErr);

        }


        //
        public static boolean bThread_toSendOk(qmCommFunc_mis.MIS_CHANNEL pChannel) {
            if (null == pChannel) {
                channelLog(pChannel , "channel pChannel is not ok");
                return false;
            }
            qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
            if (null == pMisCnt) {
                channelLog(pChannel , "channel pMisCnt is not ok");
                return false;
            }
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            if (null == pProcInfo) {
                channelLog(pChannel , "channel pProcInfo is not ok");
                return false;
            }
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            if (null == pQyMc) {
                channelLog(pChannel , "channel pQyMc is not ok");
                return false;
            }

            //
            boolean bDbg = false;


            if (pQyMc.b_notifyPanic) {
                channelLog(pChannel , "channel 5 is not ok");
                return false;
            }

            //
            //boolean result = (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli && !pChannel.status.ulbSessionErr);
            boolean result = (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli);
            if(!result){
                if  (  false  ) {
                }
            }
            //
            return result;
            //return (!pQyMc.bQuit && !pProcInfo.bQuit_threadProc_isCli && pChannel.bSessionOpen && !pChannel.status.ulbSessionErr);

        }



        //
        //  2014/06/04
        public static boolean isEmpty_ROUTE_sendLocalAv(qmcSharedObj.ROUTE_sendLocalAv pRoute) {
            boolean iErr = false;

            if (0 != pRoute.videoConference_idInfo_to.ui64Id) return iErr;
            if (0 != pRoute.route.idInfo_to.ui64Id) return iErr;
            int i;
            for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {
                if (0 != pRoute.route.mems_to_idInfo_ui64Id[i]) return iErr;
            }

            iErr = true;


            return iErr;
        }

        /**
         *检查音频视频通道是否启动，如果没启动，发一个消息到中转线程，由中转线程启动, zzq
         * @param pProcInfo
         * @param pChannel
         * @param channelType
         * @return
         */

        public static int chkToApplyForChannel(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CHANNEL pChannel, int channelType) {
            int iErr = -1;
            String str;

            boolean bChannelReady = false;
            qyq2.QY_Q2 pQ2 = null;

            if (null == pChannel) {
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                if (null == pMisCnt) return -1;
                pChannel = getChannelByType(pMisCnt, (int) channelType);
                if (null == pChannel) return -1;
            }
            channelType = (int) pChannel.uiType;

            //
            switch (channelType) {
                case CONST_channelType_media:
                    bChannelReady = pProcInfo.av.video.bChannelReady;
                    pQ2 = pProcInfo.mediaQ2;
                    break;

                case CONST_channelType_realTimeMedia:
                    bChannelReady = pProcInfo.av.audio.bChannelReady;
                    pQ2 = pProcInfo.realTimeMediaQ2;
                    break;
                default:
                    break;
            }
            if (null == pQ2) return iErr;

            //
            if (bChannelReady) {
                iErr = 0;
                return iErr;
            }

            //
            qnmCommProc_mis.MIS_MSG_applyForChkChannels_qmc msg_applyForChkChannels = new qnmCommProc_mis.MIS_MSG_applyForChkChannels_qmc();
            long dwTickCnt;
            dwTickCnt = (int) tmpFunc.qyFuncs.myGetTickCount(null);
            int i;

            //
            if (Math.abs((int) (dwTickCnt - pChannel.status.dwLastTickCnt_applyForChkChannels)) > 2000) {


                //  Log.e(TAG,  "toShareBmp: applyForChkChannels"  );
                str = String.format("Channel {%s} not Ready, applyForChkChannels", qyGetDesByType1(CONST_channelTypeTable, channelType));
                Log.e(str);

                //
                //mymemset( ( byte[])msg_applyForChkChannels, 0, sizeof(MIS_MSG_applyForChkChannels_qmc));
                msg_applyForChkChannels.uiType = CONST_misMsgType_applyForChkChannels_qmc;
                //msg_applyForChkChannels.pMisCnt = pChannel->pMisCnt;

                //
                if (0 != q2PostMsgAndTrigger(msg_applyForChkChannels, pQ2,"chkToApplyForChannel.235")) {
                    showNotification(0, null, null, 0, 0, 0, ("chkToApplyForChannels: applyForChkChannels failed"));
                }

                //
                pChannel.status.dwLastTickCnt_applyForChkChannels = (int) dwTickCnt;
            }

            iErr = 0;


            return iErr;
        }


    }
}
