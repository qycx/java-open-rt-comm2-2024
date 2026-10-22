
package com.qycx.base;


import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.VIDEO_DOWNLOAD;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qisChannel.qyFuncs.startThread_toSend;
import static com.qycx.base.qisChannel.qyFuncs.waitForChannel;
import static com.qycx.base.qmcCommFunc_mis.Consts.CONST_iAllocType_longTimeMsg;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForChkChannels_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_toSend;
import static com.qycx.base.qyMcMainProc_isCli_media.qyFuncs.procMsgTask_mc_media;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.procMsgInput_other_req;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.procMsgInput_other_resp;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.util.Log;


public class qyMcMainProc_isCli_realTimeMedia {

    public static class qyFuncs {

        //
        public static Runnable mcThreadProc_mis_realTimeMedia = () ->
        {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qyq2.QY_Q2 pTriggerQ = pProcInfo.realTimeMediaQ2;
            int uiChannelType = CONST_channelType_realTimeMedia;

            boolean bDbg = false;
            String str;
            String where_showInfo = "audioMgr";

            //
            int loopCtrl = 0;
            int i;

            //int dwThreadId = myGetCurrentThreadId();
            pProcInfo.dwThreadId_realTimeMedia = tmpFunc.qyFuncs.myGetCurrentThreadId();


            qnmCommProc_mis.MIS_MSGU pMsg = null;
            int len;
            ctxMcThread.CTX_MC_thread ctx;

            str = String.format("mcThreadProc_mis_realTimeMedia:  enters");
            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, str);

            //memset(ctx, 0, sizeof(ctx));
            ctx = new ctxMcThread.CTX_MC_thread();
            ctx.pProcessQ = pProcInfo.processQ_media;
            ctx.pMsgBuf_forInput = new qnmCommProc_mis.CMIS_MSGU();
            ctx.pMsgBuf =new qnmCommProc_mis.CMIS_MSGU();

            for (; !pQyMc.bQuit; loopCtrl++) {

                myWaitOne(pTriggerQ.hEvent0, 5000);

                len =1024;//sizeof(qnmCommProc_mis.MIS_MSGU);

                for (; !pQyMc.bQuit; ) {
                    //
                    //long tickCnt_beforeGetMsg  =  myGetTickCount(null);
                    //
                    try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                    {
                        genericQueue.GENERIC_Q  pQ_toGetMsg  =  pTriggerQ.qs0;
                        if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pQ_toGetMsg)))
                            break;
                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        break;
                    }
                    //
                    //long tickCnt_afterGetMsg  =  myGetTickCount(null);


                    //mymemset((((byte[])pMsg) + len), 0, (int)Math.min(256, sizeof(qnmCommProc_mis.MIS_MSGU) - len));

                    switch (pMsg.uiType) {
                        case CONST_misMsgType_input: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// (MIS_CNT)pMsg->input.pMisCnt;
                            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                            if (null == pMisCnt) break;
                            //  if  (  !(  pChannel  =  getChannelByType(  pMisCnt,  pMsg->input.uiChannelType  )  )  )  break;
                            if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                break;
                            qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                            if (isUcFlgResp(input.ucFlg)) {
                                if (0 != procMsgInput_other_resp(pQyMc, pProcInfo, input, len, ctx, pChannel)) {
                                    //  Log.e(  "mcThreadProc_mis_realTimeMedia: procMsgInput_other_resp failed."  );
                                }
                            } else {
                                //Log.d(Consts.AUDIO_DOWNLOAD,"2 realTimeMediaQ2.qs0 " + pProcInfo.realTimeMediaQ2.qs0.uiQNodes + " realTimeMediaQ2.qs1 "+ pProcInfo.realTimeMediaQ2.qs1.uiQNodes);

                                long tick_before  =  myGetTickCount(null);

                                //
                                if (0 != procMsgInput_other_req(pQyMc, pProcInfo, input, len, ctx, pChannel)) {
                                    //  Log.e(  "mcThreadProc_mis_realTimeMedia: procMsgInput_other_req failed."  );
                                }

                                //
                                if  (  false ) {
                                    long tick_after = myGetTickCount(null);
                                    str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " find costs " + (tick_after - tick_before);
                                    //str  +=  " getMsg consts "  +  (tickCnt_afterGetMsg  -  tickCnt_beforeGetMsg);
                                    showInfo_open.qyFuncs.showInfo_open(str);
                                }

                            }

                            //
                        }
                        break;
                        case CONST_misMsgType_talk:
                            Log.i( "mcThreadProc_mis_realTimeMedia: msg_talk ÔÝÊ±²»´¦Àí");
                            break;
                        case CONST_misMsgType_task: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// (MIS_CNT)pMsg->task.pMisCnt;
                            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                            if (null == pMisCnt) break;
                            if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                break;

                            if (0 != procMsgTask_mc_media(pQyMc, pProcInfo, (qnmCommProc_mis.MIS_MSG_TASK) pMsg, len, false, ctx, pChannel))
                                break;

                            //  printMisMsg(  0,  0,  pMsg  );

                        }
                        break;
                        case CONST_misMsgType_applyForChkChannels_qmc: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// (MIS_CNT)pMsg->applyForChkChannels_qmc.pMisCnt;
                            if (null == pMisCnt) break;

                            Log.d("qy","rtMedia: applyForChkChannels");
                            startChannel(pProcInfo, pMisCnt, getChannelByType(pMisCnt, uiChannelType), ("rtMedia: applyForChannels"));


                        }
                        break;

                        case CONST_misMsgType_outputTask:

                        default:
                            str = String.format("mcThreadProc_mis_realTimeMedia failed, msgType %s", pMsg.uiType);

                            break;
                    }

                    //  dwRet  =  WaitForSingleObject(  pTriggerQ->hSemaTrigger,  50  );
                    //  if  (  dwRet  ==  WAIT_FAILED  )  goto  errLabel;
                }


                if (0 == (loopCtrl % 5)) {

                    //  for  (  i  =  0;  i  <  mycountof(  pProcInfo.misCnts  );  i  ++  )
                    //for (i = 0; i < pProcInfo.ucCnt_misCnts; i++)
                    {
                        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// getMisCntByIndex(0, pProcInfo, i);  //&pProcInfo.misCnts[i];
                        //if (!pMisCnt) continue;
                        //
                        //if (!pMisCnt->cfg.dynCfg.cnt.usCntPort) continue;

                        waitForChannel(pProcInfo, pMisCnt, uiChannelType, false);
                    }
                }

                //
                {
                    qmCommFunc_mis.MIS_CHANNEL pChannel=getChannelByType(pProcInfo.pMisCnt,uiChannelType);
                    if (pChannel.hThread_toSend != null) {
                        waitForThread_toSend(pChannel, false);
                    }
                    if (pChannel.hThread_toSend == null) {
                        startThread_toSend(pProcInfo, pProcInfo.pMisCnt, pChannel, "mcThreadProc_rtMedia.195");
                    }
                }

                //
                continue;
            }


            //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt; // getMisCntByIndex(0, pProcInfo, i);  //&pProcInfo.misCnts[i];
            waitForChannel(pProcInfo, pMisCnt, uiChannelType, true);

            {
                qmCommFunc_mis.MIS_CHANNEL pChannel=getChannelByType(pMisCnt,uiChannelType);

                if  (  pChannel.hThread_toSend!=null) {
                    waitForThread_toSend(pChannel, true);
                    if  (  pChannel.hThread_toSend!=null) {
                        showInfo_open.qyFuncs.showInfo_open("Err: audio.toSend not closed");
                    }
                    else  {
                        showInfo_open.qyFuncs.showInfo_open("audio.toSend closed");
                    }
                }
                else  {
                    showInfo_open.qyFuncs.showInfo_open("audio.toSend already closed");
                }
            }


            //
            pProcInfo.status.threadsStatus.ulbRealTimeMediaQuit = true;
            Log.i( "mcThreadProc_mis_realTimeMedia:  leaves");
        };
    }
}
