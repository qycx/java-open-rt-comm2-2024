


package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_rtOp;
import static com.qycx.base.Consts.VIDEO_DOWNLOAD;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.setSessionErr;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_io;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_toSend;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyq2.qyFuncs.emptyQ2;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;

public class qisChannel {

    public static class qyFuncs { //io thread zzq

        public static int startChannel(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qmCommFunc_mis.MIS_CHANNEL pChannel, String hint) {
            int iErr = -1;

            if (hint == null) hint = "";

            if (null == pProcInfo || null == pChannel) {
                Log.e("startChannel: pProcInfo or pChannel is null");
                return -1;
            }

            if (pChannel.hThread_io != null) {
                if (bChannelOk(pChannel,false)) {
                    iErr = 0;
                    return iErr;
                }

                waitForThread_io(pChannel,false);
                if (null == pChannel.hThread_io) {
                    String str =String.format("%s is closed", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                    Log.e("qy",str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                }

                //
                /*
                if  (  null!=pChannel.hThread_io)  {
                    if  (  pChannel.status.ulbIoQuit1) {
                        try {
                            pChannel.hThread_io.join();
                            pChannel.hThread_io=null;
                        }
                        catch (Exception e ) {
                            showInfo_open.qyFuncs.showInfo_open("startChannel. hThread_io.join exception: "  +  e.getMessage());
                            return  -1;
                        }
                    }
                }

                 */

            }

            //if (pMisCnt.commEncCtx.ucbPublicKeyVerified)
            //  2008/06/09, 需要确认后，才可以启动收发线程。

            if (null == pChannel.hThread_io) {

                //
                /*
                if(myGetTickCount(null) - pChannel.endPoint < 5000) {
                    Log.d("startChannel","time is not up, io thread can't be started!!!!!!" + pChannel.uiType);
                    return iErr;
                }
                Log.d("startChannel","time is not up, io thread is started!!!!!!" + pChannel.uiType );
                pChannel.endPoint = myGetTickCount(null);

*/
                //
                String str = String.format("Try to start channel " + qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType) + " " + hint);
                Log.i(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                switch (pChannel.uiType) {                   //  2008/05/14. 对实时数据来说，最重要是当前数据。为了避免未发出的数据积累，先在这里清除了。
                    case CONST_channelType_media:
                    case CONST_channelType_realTimeMedia:
                    case CONST_channelType_rtOp:
                        emptyQ2(pChannel.toSendQ2);
                        if  (  pChannel.m_b_useQ2_toOutput) {
                            emptyQ2(pChannel.outputQ21);
                        }
                        else  {
                               Log.d("qy", "not finished,emptyQ2(pChannel.outputQBuf2);"  );
                        }
                        Log.d("startChannel",VIDEO_DOWNLOAD + " " +AUDIO_DOWNLOAD + " " + VIDEO_UPLOAD + " " + AUDIO_UPLOAD+ " "+ "start media thread:" + pChannel.uiType);
                        break;
                    default:
                        break;
                }

                //
                pChannel.status.iIoStep=0;
                pChannel.status.ulbIoQuit1=false;

                //
                pChannel.hThread_io = new Thread(new mcThread_io.qyFuncs.mcThreadProc_mis_io(pChannel));//音视频上传IO线程
                pChannel.hThread_io.setName("Thread_io_" + qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));

                try{
                    pChannel.hThread_io.start();
                } catch (IllegalThreadStateException e) {
                   // System.err.println("已尝试启动一个已经启动的线程，操作非法。");
                    showInfo_open.qyFuncs.showInfo_open(null, "startChannel", "error line 74 hThread_io start fail");
                }

            }

            iErr = 0;
            return iErr;
        }


        //
        public static int startThread_toSend(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qmCommFunc_mis.MIS_CHANNEL pChannel, String hint) {
            int iErr = -1;

            if (hint == null) hint = "";

            if (null == pProcInfo || null == pChannel) {
                Log.e("startChannel: pProcInfo or pChannel is null");
                return -1;
            }
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if  (  pQyMc==null)  {
                return  -1;
            }
            if  (  pQyMc.bQuit  )  {
                return  -1;
            }

            //
            if (pChannel.hThread_toSend != null) {
                waitForThread_toSend(pChannel, false);
                if (null == pChannel.hThread_toSend) {
                    String str =String.format("startThread_toSend: %s is closed", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                    Log.e("qy",str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                }
            }

            //if (pMisCnt.commEncCtx.ucbPublicKeyVerified)
            //  2008/06/09, 需要确认后，才可以启动收发线程。

            if (null == pChannel.hThread_toSend) {

                //
                /*
                if(myGetTickCount(null) - pChannel.endPoint < 5000) {
                    Log.d("startChannel","time is not up, io thread can't be started!!!!!!" + pChannel.uiType);
                    return iErr;
                }
                Log.d("startChannel","time is not up, io thread is started!!!!!!" + pChannel.uiType );
                pChannel.endPoint = myGetTickCount(null);

*/
                //
                String str = String.format("startThread_toSend: Try to start " + qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType) + " " + hint);
                Log.i(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);

                //
                pChannel.hThread_toSend = new Thread(new mcThread_toSend.qyFuncs.mcThreadProc_mis_toSend(pChannel));//音视频上传IO线程
                pChannel.hThread_toSend.setName("Thread_toSend_" + qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));

                try{
                    pChannel.hThread_toSend.start();
                } catch (IllegalThreadStateException e) {
                    // System.err.println("已尝试启动一个已经启动的线程，操作非法。");
                    showInfo_open.qyFuncs.showInfo_open(null, "startChannel_toSend", "error line 74 hThread_io start fail");
                }

            }

            iErr = 0;
            return iErr;
        }





        //
        public static int waitForChannel(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, int uiChannelType, boolean bForceQuit) {
            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

            if (null != (pChannel = getChannelByType(pMisCnt, uiChannelType))
                    && pChannel.hThread_io != null) {
                if (!bForceQuit) {
                    //waitForObject(pChannel->hThread_io, 0);
                    waitForThread_io(pChannel, false);
                } else {
                    //pChannel.status.ulbSessionErr = true;
                    setSessionErr(pChannel,true,"waitForChannel.209");

                    //waitForObject(pChannel->hThread_io, 20000);
                    waitForThread_io(pChannel, true);
                    if (pChannel.hThread_io != null) {
                        //Log.e("waitForChannel failed.");
                        String str = String.format("waitForChannel failed. %s", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                        showInfo_open.qyFuncs.showInfo_open(null, "waitForChannel", str);
                    }
                }

                if (null == pChannel.hThread_io) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("waitForChannel ok. %s"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType));
                    String str = String.format("waitForchannel ok. %s", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                    showInfo_open.qyFuncs.showInfo_open(null, "waitForChannel", str);
                }
            }

            return 0;
        }

    }
}
