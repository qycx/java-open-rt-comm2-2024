package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.Consts.CONST_qyRet_qFull;
import static com.qycx.base.Consts.IO_TAG;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.qmCommonFunc_mis.Consts.MIN_uiSizePerSnd_media;
import static com.qycx.base.qmCrypt.Consts.CONST_cspContainer_qmc;
import static com.qycx.base.qmCrypt.Consts.DEFAULT_cspProvider;
import static com.qycx.base.qmCrypt.qyFuncs.decryptData;
import static com.qycx.base.qmCrypt.qyFuncs.encryptData;
import static com.qycx.base.qmObjQProc.qyFuncs.procMisMsg_moreData;
import static com.qycx.base.qmcCommFunc_mis.Consts.CONST_maxTriesToStartSession_mis;
import static com.qycx.base.qmcCrypt.qyFuncs.exitCrypt_qmc;
import static com.qycx.base.qmcCrypt.qyFuncs.initCrypt_qmc;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_getCfgs;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_qyNullThread;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.setSessionErr;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_moreData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgMoreData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_warning;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.bChkMessengerChannelsReady;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.tryToStartChannel;
import static com.qycx.base.qyMsgFunc.qyFuncs.qnmCloseSession_msg;
import static com.qycx.base.qyMsgFunc.qyFuncs.qyRecvMsg;
import static com.qycx.base.qyMsgFunc.qyFuncs.qySendMsg;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount64;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;

import java.nio.ByteBuffer;

public class mcThread_io {

    public static class qyFuncs {
        public static class mcThreadProc_mis_snd implements Runnable {
            qmCommFunc_mis.MIS_CHANNEL pChannel;
            private static int video_count = 0;
            private static int audio_count = 0;

            public mcThreadProc_mis_snd(Object lpParameter) {
                pChannel = (qmCommFunc_mis.MIS_CHANNEL) lpParameter;
            }


            /*
            //@Override
            public void run_old() {

                Log.d(IO_TAG," mcThreadProc_mis_snd enter");
                if (null == pChannel) {
                    return;
                }

                qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
                if (null == pMisCnt) {
                    return;
                }
                ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
               if (null == pProcInfo) {
                   return;
               }
                ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
                if (null == pQyMc) {
                    return;
                }

                int loopCtrl = 0;

                int len;

                qnmCommProc_mis.MIS_MSGU pMsg = null;// = new qnmCommProc_mis.MIS_MSGU();

                qyTypes.QY_BUF buf_help = new qyTypes.QY_BUF();

                int maxEncBufSize = 1500000;
                byte[] encBuf = new byte[maxEncBufSize];
                RecyclableMemoryStreamManager recyclableMemoryStreamManager = new RecyclableMemoryStreamManager();
                int iRet;
                //  BOOL							bThreadIdFound				=	FALSE;

                //TCHAR tBuf[256];
                boolean bChkTimeoutInSToSwitch = false;
                int usTimeoutInMsToSwitch = 0;
                int iElapse = 0;

                long dwTickCnt_toGetMsg;
                long dwTickCnt_startToSend;
                long dwTickCnt_lastEndSending_totalMsg = 0;

                String str;

                String where_showInfo = String.format("%s_S", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "starts");
                buf_help.len = 64;
                if (null == (buf_help.data = new byte[buf_help.len])){
                    return;
                }
                String type ="";
                switch (pChannel.uiType) {       //  2008/11/20
                    case CONST_channelType_media:
                        usTimeoutInMsToSwitch = pProcInfo.cfg.policy.usTimeoutInMsToSwitch;
                        bChkTimeoutInSToSwitch = true;
                        type = "video";
                        break;
                    case CONST_channelType_realTimeMedia:
                        usTimeoutInMsToSwitch = pProcInfo.cfg.policy.usTimeoutInMsToSwitch_rt;
                        bChkTimeoutInSToSwitch = true;
                        type = "audio";
                        break;
                    case CONST_channelType_talking:
                        type = "talk";
                        break;
                    default:
                        break;
                }
                Log.d(IO_TAG,type+" mcThreadProc_mis_snd run");
                //  pChannel->status.nMsgSent  =  0;

                genericQueue.GENERIC_Q pOutputQ = null; //  2011/01/23

                for (; bChannelOk(pChannel,false); loopCtrl++) {
                    //
                    boolean  b_q2Empty  =  false;
                    if  (  pChannel.m_b_useQ2_toOutput)  {
                        b_q2Empty=isQ2Empty(pChannel.outputQ21);
                    }
                    else  {
                          b_q2Empty  =  pChannel.outputQBuf2.isQ2Empty();
                    }
                    //
                    if (b_q2Empty) {
                        //
                        if(pChannel.uiType == 4)
                        {
                            if  (  false  ) {
                                Log.d(VIDEO_UPLOAD, "video outputQ2 is empty");
                            }
                        }
                        //
                        q2PostMsgAndTrigger(null, pChannel.toSendQ2,"snd.146");

                        if  (  pChannel.m_b_useQ2_toOutput) {
                            myWaitOne(pChannel.outputQ21.hEvent0, 3500);
                        }
                        else  {
                              myWaitOne(pChannel.outputQBuf2.hEvent0,3500);
                        }

                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucbViewIoStatus) {
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, ("after waitForSingleObject"));
                        }

                    }

                    for (; bChannelOk(pChannel,false); ) {

//                        q2AvoidTriggerTooHigh(null, pChannel.outputQ2);


                        long dwTickCnt_toWaitForSock = myGetTickCount(null);

                        iRet = guiShare.GuiShare.pf_qyWaitForWrite.call(pChannel.sock, pQyMc.cfg.to.iSelect);
                        if (iRet < 0) {
                            if (null != pMisCnt){
                                pChannel.status.ulbSessionErr = true;
                                pChannel.status.ulbSndQuit = true;
                            }
                            if(pChannel.uiType == 4)
                            {
                                Log.d(VIDEO_UPLOAD,"pf_qyWaitForWrite failed");
                            }
                            return;      //  ³ö´í
                        }
                        if (0 == iRet) {
                            long tmp_tickCnt = myGetTickCount(null);
                            int iDiffInMs = (int) (tmp_tickCnt - dwTickCnt_toWaitForSock);

                            str = "qyWaitForWrite returns 0. iDiffInMs " + iDiffInMs + "ms, continue";
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, str);

                            continue;
                        }

                        //
                        int nQNodes = 0;
                        if  (  pChannel.m_b_useQ2_toOutput) {
                            nQNodes = getQ2Nodes(pChannel.outputQ21);
                        }
                        else  {
                              nQNodes=pChannel.outputQBuf2.getQ2Nodes();
                        }
                        //if  (  nQNodes  <  MAX_outputQ2Nodes_toPostQ  )
                        if(pChannel.uiType == 4){
                            if  (  false  ) {
                                int bufSize = pChannel.getOutQ2Size();
                                Log.d(IO_TAG, "video data size: " + bufSize);
                            }
                        }
                        int nQNodes_toTrigger = 2;
                        //
                        //
                        if (nQNodes < nQNodes_toTrigger) {
                            q2PostMsgAndTrigger(null, pChannel.toSendQ2,"snd.197");
                        }

                        if  (  pChannel.m_b_useQ2_toOutput) {
                            if ((pMsg = q2GetMsg(pChannel.outputQ21)) == null) break;

                            //
                            if  (  true  ) {
                                if (pMsg.uiType == CONST_misMsgType_req)  {
                                    qnmCommProc_mis.MIS_MSG_REQ req  =  (  qnmCommProc_mis.MIS_MSG_REQ) pMsg;
                                    //
                                    if  (  req.usCode  ==  CONST_qyCmd_getCfgs)  {
                                        Log.d("qy","getCfgs");
                                    }
                                }
                            }
                        }
                        else {

                            //
                            QY_qBuf2.HeadData_snd head = new QY_qBuf2.HeadData_snd();

                            byte[] buf = new byte[bufSize_transferVideoData];
                            int bufSize = buf.length;
                            //
                            int tmp_len = pChannel.outputQBuf2.q2GetMsg(head, buf, 0,  bufSize);
                            if (tmp_len <= 0) {
                                //  没读到
                                break;
                            }
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
                            byteBuffer.position(tmp_len);
                            //
                            switch  (  head.msg_uiType) {
                                case CONST_misMsgType_req:
                                    qnmCommProc_mis.MIS_MSG_REQ req  =  new qnmCommProc_mis.MIS_MSG_REQ();
                                    req.uiType = head.msg_uiType;
                                    req.buffer = byteBuffer;
                                    req.lenInBytes=tmp_len;
                                    //
                                    req.ucFlg= head.ucFlg;;
                                    req.usCode  =  head.usCode;
                                    req.tStartTime=head.tStartTime;
                                    req.uiTranNo  =  head.uiTranNo;
                                    req.usSeqNo  =  head.usSeqNo;

                                    //
                                    if  (  req.usCode  ==  CONST_qyCmd_getCfgs)  {
                                        Log.d("qy","getCfgs");
                                    }

                                    //
                                    pMsg  =  req;
                                    break;
                                case CONST_misMsgType_talk:
                                    qnmCommProc_mis.MIS_MSG_TALK  talk = new qnmCommProc_mis.MIS_MSG_TALK();
                                    talk.uiType = head.msg_uiType;
                                    talk.buffer=byteBuffer;
                                    talk.lenInBytes=tmp_len;
                                    //
                                    talk.ucFlg= head.ucFlg;;
                                    talk.usCode  =  head.usCode;
                                    talk.tStartTime=head.tStartTime;
                                    talk.uiTranNo  =  head.uiTranNo;
                                    talk.usSeqNo  =  head.usSeqNo;

                                    //
                                    pMsg=talk;
                                    break;
                                case CONST_misMsgType_task:
                                case  CONST_misMsgType_outputTask:
                                    qnmCommProc_mis.MIS_MSG_TASK task  =  new qnmCommProc_mis.MIS_MSG_TASK();
                                    task.uiType = head.msg_uiType;
                                    task.buffer=byteBuffer;
                                    task.lenInBytes=tmp_len;
                                    //
                                    //
                                    task.ucFlg= head.ucFlg;;
                                    task.usCode  =  head.usCode;
                                    task.tStartTime=head.tStartTime;
                                    task.uiTranNo  =  head.uiTranNo;
                                    task.usSeqNo  =  head.usSeqNo;

                                    //
                                    pMsg = task;
                                    break;
                                default:
                                    pMsg=null;

                                    break;
                            }


                            //
                            if  (  pMsg  ==  null  )  {
                                break;
                            }
                        }


                        //
                        if(pChannel.uiType == 4){
                            //Log.d(VIDEO_UPLOAD,"2.12 q2GetMsg(pChannel.outputQ2)");
                        }
                        switch (pMsg.uiType) {
                            case CONST_misMsgType_req:
                            {
                                str = "";
                                int step = 0;
                                showInfo_open.qyFuncs.showInfo_open( str);

                                qnmCommProc_mis.MIS_MSG_REQ req = (qnmCommProc_mis.MIS_MSG_REQ) pMsg;
                                if (null == (req.buffer = encryptData(pChannel.commEncCtx, req.buffer, req.lenInBytes))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd req: encryptData failed"));
                                    if (null != pMisCnt){
                                        pChannel.status.ulbSessionErr = true;
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                // send data zzq
                                if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, req.ucFlg, req.usCode, req.tStartTime, req.uiTranNo, req.usSeqNo, req.buffer.array(), req.buffer.limit())) {
                                    if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                        int bufSize = pChannel.getOutQ2Size();
                                    }
                                    else {
                                        Log.i(IO_TAG,"qySendMsg failed");
                                    }
                                    if (null != pMisCnt){
                                        pChannel.status.ulbSessionErr = true;
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                //  2009/10/11
                                pChannel.status.netStat.ui64MsgDataLenSent += req.lenInBytes;
                                break;
                            }
                            case CONST_misMsgType_talk: {
                                qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;

                                str = "";
                                if (!isUcFlgResp(talk.ucFlg)) {
                                    //str = String.format("snd: tn %d, %s: len %d"), pMsg->task.uiTranNo, qyGetDesByType1(CONST_qyCmdTable, pMsg->task.usCode), pMsg->task.lenInBytes);
                                    showInfo_open.qyFuncs.showInfo_open( str);
                                } else {
                                    //str = String.format("snd: tn %d, resp: %s "), pMsg->task.uiTranNo, qyGetDesByType1(CONST_qyRcTable, pMsg->task.usCode));
                                    showInfo_open.qyFuncs.showInfo_open( str);
                                }

                                if (null == (talk.buffer = encryptData(pChannel.commEncCtx, talk.buffer, talk.lenInBytes))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd talk: encryptData failed"));
                                    if (null != pMisCnt){
                                        pChannel.status.ulbSessionErr = true;
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }
                                 // send video or audio data zzq
                                if(pChannel.uiType == 4){
                                    Log.d(VIDEO_UPLOAD,"2.13 qySendMsg");
                                }
                                if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, talk.ucFlg, talk.usCode, talk.tStartTime, talk.uiTranNo, talk.usSeqNo, talk.buffer.array(), talk.buffer.limit())) {
                                    Log.i("qySendMsg failed");
                                    if (null != pMisCnt){
                                        pChannel.status.ulbSessionErr = true;
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }
                                //  2009/10/11
                                pChannel.status.netStat.ui64MsgDataLenSent += talk.lenInBytes;
                            break;
                            }
                            case CONST_misMsgType_task:
                            case CONST_misMsgType_outputTask: {
                                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                                //  2008/11/20
                                if (bChkTimeoutInSToSwitch) {
                                    if (task.dwTickCnt_start != 0) {
                                        iElapse = (int) (myGetTickCount(null) - task.dwTickCnt_start);
                                        showInfo_open.qyFuncs.showInfo_open(0, "", "snd task elapse " + iElapse +"ms");
                                        if (iElapse > usTimeoutInMsToSwitch) {
                                            boolean bTaskFound = false;
                                            str = String.format("snd: %dms elapsed, skipped. tn %d", iElapse, task.uiTranNo);
                                            showInfo_open.qyFuncs.showInfo_open( str);
                                            break;
                                        }
                                    }
                                }
                                if (null == (task.buffer = encryptData(pChannel.commEncCtx, task.buffer, task.lenInBytes))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd req: encryptData failed"));
                                    if (null != pMisCnt){
                                        pChannel.status.ulbSessionErr = true;
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                int uiSizePerSnd_media = pMisCnt.cfg.uiSizePerSnd_media;     //  2011/01/30
                                if (0 == uiSizePerSnd_media)
                                    uiSizePerSnd_media = MIN_uiSizePerSnd_media;

                                {       //
                                    byte[] p = null;
                                    int nLeft = 0;
                                    int uiSeqNo = 0;
                                    int nSend = uiSizePerSnd_media;
                                    byte ucFlg = 0;
                                    //  TCHAR				tBuf[255  +  1]		=	(  ""  );
//                                    task.buffer.flip();
                                    p = task.buffer.array();
                                    nLeft = task.buffer.limit();

                                    for (uiSeqNo = 0; nLeft != 0 && bChannelOk(pChannel,false); uiSeqNo++) {
                                        nSend = Math.min(nSend, nLeft);
                                        //
                                        ucFlg = (byte) ((nLeft > nSend) ? (task.ucFlg | CONST_commFlg_moreData) : task.ucFlg);

                                        //


                                        int size = pChannel.getOutQ2Size();
                                        if(pChannel.uiType == CONST_channelType_realTimeMedia ){ //如果是音频 zzq
                                            //Log.d(AUDIO_UPLOAD ,"<stage4> pChannel size is " + size + " thread " + Thread.currentThread().getId() + " size: " +nSend );
                                            //EventBus.getDefault().post(new AudioOutBufSizeEvent(size, 3, Thread.currentThread().getId()));
                                        }else if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                            //Log.d(VIDEO_UPLOAD ,"<stage4> pChannel size is " + size + " thread " + Thread.currentThread().getId() + " size: " +nSend );
                                        }
                                        //real send video and audio data zzq
                                        if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, ucFlg, task.usCode, task.tStartTime, task.uiTranNo, (int) uiSeqNo, p, nSend)) { //zzq发送视频数据！！！！！
                                            if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                                Log.d(VIDEO_UPLOAD, "<stage5> qySendMsg failed" +Thread.currentThread().getName() + Thread.currentThread().getId());
                                            }
                                            else if(pChannel.uiType == CONST_channelType_realTimeMedia) { //如果是视频 zzq
                                                Log.d(AUDIO_UPLOAD, "<stage5> qySendMsg failed" + Thread.currentThread().getName( )+ Thread.currentThread().getId());
                                            }
                                            else {
                                                Log.i("qySendMsg failed");
                                            }
                                            if (null != pMisCnt){
                                                pChannel.status.ulbSessionErr = true;
                                                pChannel.status.ulbSndQuit = true;
                                            }
                                            return;
                                        }
                                        //  2009/10/11
                                        pChannel.status.netStat.ui64MsgDataLenSent += nSend;
                                        //
                                        //
                                        pChannel.status.dwTickCnt_lastEndSending = myGetTickCount64(null);   //  2011/01/23

                                        //
                                        nLeft -= nSend;
//                                            p += nSend;
                                        continue;
                                    }
                                }

                                //  2011/01/22
                                long dwTickCnt_endSending = myGetTickCount(null);
                            }
                            break;
                            default:
                                if (null != pMisCnt){
                                    pChannel.status.ulbSessionErr = true;
                                    pChannel.status.ulbSndQuit = true;
                                }
                                return;
                        }

                        //  pChannel->status.netStat.nMsgSent  ++  ;
                        pChannel.status.dwTickCnt_lastEndSending = myGetTickCount64(null);   //  2011/01/23

                        dwTickCnt_lastEndSending_totalMsg = pChannel.status.dwTickCnt_lastEndSending;
                    }

                }

                Log.d(IO_TAG,type+" mcThreadProc_mis_snd exit");
                showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "ends");

                if (null != pMisCnt) {
                    pChannel.status.ulbSessionErr = true;
                    pChannel.status.ulbSndQuit = true;
                }
            }

             */



            //
            @Override
            public void run() {

                Log.d("qy"," mcThreadProc_mis_snd enter");

                if (null == pChannel) {
                    return;
                }

                qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
                if (null == pMisCnt) {
                    return;
                }
                ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
                if (null == pProcInfo) {
                    return;
                }
                ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
                if (null == pQyMc) {
                    return;
                }

                int loopCtrl = 0;

                int len;


                qnmCommProc_mis.MIS_MSGU pMsg = null;// = new qnmCommProc_mis.MIS_MSGU();

                //qyTypes.QY_BUF buf_help = new qyTypes.QY_BUF();

                //int maxEncBufSize = 15;
                //byte[] encBuf = new byte[maxEncBufSize];
                //RecyclableMemoryStreamManager recyclableMemoryStreamManager = new RecyclableMemoryStreamManager();
                int iRet;
                //  BOOL							bThreadIdFound				=	FALSE;

                //TCHAR tBuf[256];
                boolean bChkTimeoutInSToSwitch = false;
                int usTimeoutInMsToSwitch = 0;
                int iElapse = 0;

                long dwTickCnt_toGetMsg;
                long dwTickCnt_startToSend;
                long dwTickCnt_lastEndSending_totalMsg = 0;

                String str;

                String where_showInfo = String.format("%s_S", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "starts");
                /*
                buf_help.len = 64;
                if (null == (buf_help.data = new byte[buf_help.len])){
                    return;
                }

                 */

                //
                String type ="";
                switch (pChannel.uiType) {       //  2008/11/20
                    case CONST_channelType_media:
                        usTimeoutInMsToSwitch = pProcInfo.cfg.policy.usTimeoutInMsToSwitch;
                        bChkTimeoutInSToSwitch = true;
                        type = "video";
                        break;
                    case CONST_channelType_realTimeMedia:
                        usTimeoutInMsToSwitch = pProcInfo.cfg.policy.usTimeoutInMsToSwitch_rt;
                        bChkTimeoutInSToSwitch = true;
                        type = "audio";
                        break;
                    case CONST_channelType_talking:
                        type = "talk";
                        break;
                    default:
                        break;
                }

                //
                Log.d("qy",type+" mcThreadProc_mis_snd run haha");
                //  pChannel->status.nMsgSent  =  0;

                genericQueue.GENERIC_Q pOutputQ = null; //  2011/01/23


                //
                byte[] tmp_buf = new byte[pChannel.getOutBufferLength()];
                QY_qBuf2.HeadData_snd  tmp_head  =  new QY_qBuf2.HeadData_snd();

                //
                //byte  [] p1= new byte[2048];//[nSend];


                //
                for (; ; loopCtrl++) {
                    //
                    if  (  !bChannelOk(pChannel,false) )  {
                        Log.d("qy","mcThreadProc_mis_snd: bChannelOk is false, break;612");
                        break;
                    }

                    //
                    boolean  b_q2Empty  =  false;
                    if  (  pChannel.m_b_useQ2_toOutput)  {
                        b_q2Empty=isQ2Empty(pChannel.outputQ21);
                    }
                    else  {
                        b_q2Empty  =  pChannel.outputQBuf2.isQ2Empty();
                    }
                    //
                    if (b_q2Empty) {
                        //
                        if(pChannel.uiType == 4)
                        {
                            if  (  false  ) {
                                Log.d(VIDEO_UPLOAD, "video outputQ2 is empty");
                            }
                        }
                        //
                        q2PostMsgAndTrigger(null, pChannel.toSendQ2,"snd.146");

                        if  (  pChannel.m_b_useQ2_toOutput) {
                            myWaitOne(pChannel.outputQ21.hEvent0, 3500);
                        }
                        else  {
                            myWaitOne(pChannel.outputQBuf2.hEvent0,3500);
                        }

                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucbViewIoStatus) {
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, ("after waitForSingleObject"));
                        }

                    }

                    //
                    for (; ; ) {

                        //
                        if  (  !bChannelOk(pChannel,false)  ) {
                            Log.d("qy", "mcThreadProc_mis_snd: bChannelOk is false, break;653");
                            bChannelOk(pChannel,true);
                            break;
                        }

                        //
                        //  q2AvoidTriggerTooHigh(null, pChannel.outputQ2);

                        //
                        long dwTickCnt_toWaitForSock = myGetTickCount(null);

                        iRet = guiShare.GuiShare.pf_qyWaitForWrite.call(pChannel.sock, pQyMc.cfg.to.iSelect);
                        if (iRet < 0) {
                            if (null != pMisCnt){
                                //pChannel.status.ulbSessionErr = true;
                                setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.674");
                                //
                                pChannel.status.ulbSndQuit = true;
                            }
                            if(pChannel.uiType == 4)
                            {
                                Log.d(VIDEO_UPLOAD,"pf_qyWaitForWrite failed");
                            }
                            return;      //  ³ö´í
                        }
                        if (0 == iRet) {
                            long tmp_tickCnt = myGetTickCount(null);
                            int iDiffInMs = (int) (tmp_tickCnt - dwTickCnt_toWaitForSock);

                            str = "qyWaitForWrite returns 0. iDiffInMs " + iDiffInMs + "ms, continue";
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, str);

                            continue;
                        }

                        //
                        int nQNodes = 0;
                        if  (  pChannel.m_b_useQ2_toOutput) {
                            nQNodes = getQ2Nodes(pChannel.outputQ21);
                        }
                        else  {
                            nQNodes=pChannel.outputQBuf2.getQ2Nodes();
                        }
                        //if  (  nQNodes  <  MAX_outputQ2Nodes_toPostQ  )
                        if(pChannel.uiType == 4){
                            if  (  false  ) {
                                int bufSize = pChannel.getOutQ2Size();
                                Log.d(IO_TAG, "video data size: " + bufSize);
                            }
                        }
                        int nQNodes_toTrigger = 2;
                        //
                        //
                        if (nQNodes < nQNodes_toTrigger) {
                            q2PostMsgAndTrigger(null, pChannel.toSendQ2,"snd.197");
                        }

                        //
                        QY_qBuf2.HeadData_snd head = null;
                        byte [] buf=null;
                        ByteBuffer byteBuffer  =  null;

                        //
                        if  (  pChannel.m_b_useQ2_toOutput) {
                            if ((pMsg = q2GetMsg(pChannel.outputQ21)) == null) break;

                            //
                            if  (  true  ) {
                                if (pMsg.uiType == CONST_misMsgType_req)  {
                                    qnmCommProc_mis.MIS_MSG_REQ req  =  (  qnmCommProc_mis.MIS_MSG_REQ) pMsg;
                                    //
                                    if  (  req.usCode  ==  CONST_qyCmd_getCfgs)  {
                                        Log.d("qy","getCfgs");
                                    }
                                }
                            }
                        }
                        else {

                            //
                            head  =  tmp_head;//new QY_qBuf2.HeadData_snd();
                            buf=tmp_buf;

                           // byte[] buf = new byte[bufSize_transferVideoData];
                            int bufSize = buf.length;
                            //
                            int tmp_len = pChannel.outputQBuf2.q2GetMsg(head, buf, 0,  bufSize);
                            if (tmp_len <= 0) {
                                //  没读到
                                break;
                            }
                            pChannel.totalSize += tmp_len;
                            pChannel.totalTime++;
                            if(tmp_len > pChannel.maxSize){
                                pChannel.maxSize = tmp_len;
                            }

                            if(pChannel.totalTime % 10 ==0){
                                //Log.d("qy","pchannel uitype:"+pChannel.uiType +" max:" + pChannel.maxSize + " totalsize:" + pChannel.totalSize + " time:" + pChannel.totalTime + "average:" + pChannel.totalSize/pChannel.totalTime);

                            }

                            //
                            byteBuffer  = ByteBuffer.wrap(buf);
                            byteBuffer.position(tmp_len);
                            //
                            /*
                            switch  (  head.msg_uiType) {
                                case CONST_misMsgType_req:
                                    qnmCommProc_mis.MIS_MSG_REQ req  =  new qnmCommProc_mis.MIS_MSG_REQ();
                                    req.uiType = head.msg_uiType;
                                    req.buffer = byteBuffer;
                                    req.lenInBytes=tmp_len;
                                    //
                                    req.ucFlg= head.ucFlg;;
                                    req.usCode  =  head.usCode;
                                    req.tStartTime=head.tStartTime;
                                    req.uiTranNo  =  head.uiTranNo;
                                    req.usSeqNo  =  head.usSeqNo;

                                    //
                                    if  (  req.usCode  ==  CONST_qyCmd_getCfgs)  {
                                        Log.d("qy","getCfgs");
                                    }

                                    //
                                    pMsg  =  req;
                                    break;
                                case CONST_misMsgType_talk:
                                    qnmCommProc_mis.MIS_MSG_TALK  talk = new qnmCommProc_mis.MIS_MSG_TALK();
                                    talk.uiType = head.msg_uiType;
                                    talk.buffer=byteBuffer;
                                    talk.lenInBytes=tmp_len;
                                    //
                                    talk.ucFlg= head.ucFlg;;
                                    talk.usCode  =  head.usCode;
                                    talk.tStartTime=head.tStartTime;
                                    talk.uiTranNo  =  head.uiTranNo;
                                    talk.usSeqNo  =  head.usSeqNo;

                                    //
                                    pMsg=talk;
                                    break;
                                case CONST_misMsgType_task:
                                case  CONST_misMsgType_outputTask:
                                    qnmCommProc_mis.MIS_MSG_TASK task  =  new qnmCommProc_mis.MIS_MSG_TASK();
                                    task.uiType = head.msg_uiType;
                                    task.buffer=byteBuffer;
                                    task.lenInBytes=tmp_len;
                                    //
                                    //
                                    task.ucFlg= head.ucFlg;;
                                    task.usCode  =  head.usCode;
                                    task.tStartTime=head.tStartTime;
                                    task.uiTranNo  =  head.uiTranNo;
                                    task.usSeqNo  =  head.usSeqNo;

                                    //
                                    pMsg = task;
                                    break;
                                default:
                                    pMsg=null;

                                    break;
                            }


                            //
                            if  (  pMsg  ==  null  )  {
                                break;
                            }

                             */
                        }


                        //
                        if(pChannel.uiType == 4){
                            //Log.d(VIDEO_UPLOAD,"2.12 q2GetMsg(pChannel.outputQ2)");
                        }
                        switch (head.msg_uiType) {
                            case CONST_misMsgType_req:
                            {
                                str = "";
                                int step = 0;
                                //showInfo_open.qyFuncs.showInfo_open( str);

                                //
                                //head.

                                //
                                //qnmCommProc_mis.MIS_MSG_REQ req = (qnmCommProc_mis.MIS_MSG_REQ) pMsg;
                                if (null == (byteBuffer = encryptData(pChannel.commEncCtx, byteBuffer, head.dataLen))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd req: encryptData failed"));
                                    if (null != pMisCnt){
                                        //
                                        //pChannel.status.ulbSessionErr = true;
                                        setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.856");
                                        //
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                // send data zzq
                                if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, head.ucFlg, head.usCode, head.tStartTime, head.uiTranNo, head.usSeqNo, byteBuffer.array(), 0,  byteBuffer.limit())) {
                                    if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                        int bufSize = pChannel.getOutQ2Size();
                                    }
                                    else {
                                        Log.i(IO_TAG,"qySendMsg failed");
                                    }
                                    if (null != pMisCnt){
                                        //
                                        //pChannel.status.ulbSessionErr = true;
                                        setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.874");
                                        //
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                //  这里有点错，因为head.dataLen是未加密的数据长度，加密后数据长度可能有变化
                                pChannel.status.netStat.ui64MsgDataLenSent += head.dataLen;
                                break;
                            }
                            case CONST_misMsgType_talk: {
                                //qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;

                                str = "";
                                /*
                                if (!isUcFlgResp(talk.ucFlg)) {
                                    //str = String.format("snd: tn %d, %s: len %d"), pMsg->task.uiTranNo, qyGetDesByType1(CONST_qyCmdTable, pMsg->task.usCode), pMsg->task.lenInBytes);
                                    showInfo_open.qyFuncs.showInfo_open( str);
                                } else {
                                    //str = String.format("snd: tn %d, resp: %s "), pMsg->task.uiTranNo, qyGetDesByType1(CONST_qyRcTable, pMsg->task.usCode));
                                    showInfo_open.qyFuncs.showInfo_open( str);
                                }

                                 */

                                //
                                if (null == (byteBuffer = encryptData(pChannel.commEncCtx, byteBuffer, head.dataLen))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd talk: encryptData failed"));
                                    if (null != pMisCnt){
                                        //
                                        //pChannel.status.ulbSessionErr = true;
                                        setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.906");
                                        //
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }
                                // send video or audio data zzq
                                if(pChannel.uiType == 4){
                                    Log.d(VIDEO_UPLOAD,"2.13 qySendMsg");
                                }
                                if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, head.ucFlg, head.usCode, head.tStartTime, head.uiTranNo, head.usSeqNo, byteBuffer.array(), 0,  byteBuffer.limit())) {
                                    Log.i("qySendMsg failed");
                                    if (null != pMisCnt){
                                        //
                                        //pChannel.status.ulbSessionErr = true;
                                        setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.921");
                                        //
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }
                                //  2009/10/11
                                pChannel.status.netStat.ui64MsgDataLenSent += head.dataLen;
                                break;
                            }
                            case CONST_misMsgType_task:
                            case CONST_misMsgType_outputTask: {
                                //qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                                //  2008/11/20
                                if (bChkTimeoutInSToSwitch) {
                                    if (head.dwTickCnt_start != 0) {
                                        iElapse = (int) (myGetTickCount(null) - head.dwTickCnt_start);
                                        showInfo_open.qyFuncs.showInfo_open(0, "", "snd task elapse " + iElapse +"ms");
                                        if (iElapse > usTimeoutInMsToSwitch) {
                                            boolean bTaskFound = false;
                                            str = String.format("snd: %dms elapsed, skipped. tn %d", iElapse, head.uiTranNo);
                                            showInfo_open.qyFuncs.showInfo_open( str);
                                            break;
                                        }
                                    }
                                }
                                if (null == (byteBuffer = encryptData(pChannel.commEncCtx, byteBuffer, head.dataLen))) {
                                    showInfo_open.qyFuncs.showInfo_open( ("snd req: encryptData failed"));
                                    if (null != pMisCnt){
                                        //pChannel.status.ulbSessionErr = true;
                                        setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.951");
                                        //
                                        pChannel.status.ulbSndQuit = true;
                                    }
                                    return;
                                }

                                int uiSizePerSnd_media = pMisCnt.cfg.uiSizePerSnd_media;     //  2011/01/30
                                if (0 == uiSizePerSnd_media)
                                    uiSizePerSnd_media = MIN_uiSizePerSnd_media;

                                //
                                if  ( pProcInfo.bUse100k) {
                                    if  (  pProcInfo.use100k_onlyV)  {
                                        if  (  pChannel.uiType==CONST_channelType_realTimeMedia)  {
                                            continue;
                                        }
                                    }
                                    else  {
                                          if  (  pChannel.uiType  ==  CONST_channelType_media)  {
                                              continue;
                                          }
                                    }
                                }

                                //
                                if  (  pChannel.uiType  ==  CONST_channelType_media) {
                                    if (pProcInfo.bUse100k) {

                                        //
                                        int nQNodes1  =  pChannel.outputQBuf2.getQ2Nodes();
                                        if  (  nQNodes1  >  1  )  {
                                            Log.d("qy","snd: 100k: video: too many nodes "  +  nQNodes1  +  "" );
                                        }

                                        //
                                        uiSizePerSnd_media = 512;//96;  //  1024

                                    }
                                }
                                //

                                int  nTotal  =  0;
                                {       //
                                    byte[] p = null;
                                    int nLeft = 0;
                                    int uiSeqNo = 0;
                                    int nSend = uiSizePerSnd_media;
                                    byte ucFlg = 0;
                                    //  TCHAR				tBuf[255  +  1]		=	(  ""  );
//                                    task.buffer.flip();
                                    p = byteBuffer.array();
                                    nLeft = byteBuffer.limit();

                                    //
                                    nTotal  =  nLeft;

                                    //  一下的代码有错误，没有完成分包发送。
                                    if  (  nLeft  >  nSend  )  {
                                        //Log.e("qy", "mcThreadProc_mis_snd: for msgTask, not finished");
                                    }

                                    //
                                    for (uiSeqNo = 0; nLeft != 0 && bChannelOk(pChannel,false); uiSeqNo++) {
                                        nSend = Math.min(nSend, nLeft);
                                        //
                                        ucFlg = (byte) ((nLeft > nSend) ? (head.ucFlg | CONST_commFlg_moreData) : head.ucFlg);

                                        //
                                        int  offset  =  0;
                                        //
                                        int size = pChannel.getOutQ2Size();
                                        if(pChannel.uiType == CONST_channelType_realTimeMedia ){ //如果是音频 zzq
                                            //Log.d(AUDIO_UPLOAD ,"<stage4> pChannel size is " + size + " thread " + Thread.currentThread().getId() + " size: " +nSend );
                                            //EventBus.getDefault().post(new AudioOutBufSizeEvent(size, 3, Thread.currentThread().getId()));
                                        }else if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                            //Log.d(VIDEO_UPLOAD ,"<stage4> pChannel size is " + size + " thread " + Thread.currentThread().getId() + " size: " +nSend );

                                            //
                                            if  (  pProcInfo.bUse100k) {
                                                //System.arraycopy(byteBuffer.array(), nTotal - nLeft, p1, 0, nSend);
                                                //p = p1;

                                                //
                                                //p=byteBuffer.array();
                                                //offset=nTotal-nLeft;


                                                //
                                                if  (  pProcInfo.cfgInfo100k.use100k_v_perSnd_sleepInMs!=0) {
                                                    //mySleep(80);
                                                    mySleep(pProcInfo.cfgInfo100k.use100k_v_perSnd_sleepInMs);
                                                }
                                            }



                                        }
                                        //
                                        offset  =  nTotal  -  nLeft;

                                        //real send video and audio data zzq
                                        if (0 != qySendMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, ucFlg, head.usCode, head.tStartTime, head.uiTranNo, (int) uiSeqNo, p, offset,  nSend)) { //zzq发送视频数据！！！！！
                                            if(pChannel.uiType == CONST_channelType_media) { //如果是视频 zzq
                                                Log.d(VIDEO_UPLOAD, "<stage5> qySendMsg failed" +Thread.currentThread().getName() + Thread.currentThread().getId());
                                            }
                                            else if(pChannel.uiType == CONST_channelType_realTimeMedia) { //如果是视频 zzq
                                                Log.d(AUDIO_UPLOAD, "<stage5> qySendMsg failed" + Thread.currentThread().getName( )+ Thread.currentThread().getId());
                                            }
                                            else {
                                                Log.i("qySendMsg failed");
                                            }
                                            if (null != pMisCnt){
                                                //
                                                //pChannel.status.ulbSessionErr = true;
                                                setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.1006");
                                                //
                                                pChannel.status.ulbSndQuit = true;
                                            }
                                            return;
                                        }
                                        //
                                        if  (  true  )  {
                                            //if  (  pChannel.uiType  ==  CONST_channelType_media)
                                            //Log.d("qy",  "channel "  +  pChannel.uiType  +  " send "  + nSend  +  " code "  +  head.usCode);
                                        }
                                        //  2009/10/11
                                        pChannel.status.netStat.ui64MsgDataLenSent += nSend;
                                        //
                                        //
                                        pChannel.status.dwTickCnt_lastEndSending = myGetTickCount64(null);   //  2011/01/23

                                        //
                                        nLeft -= nSend;
//                                            p += nSend;
                                        continue;
                                    }
                                }

                                //
                                if  (  true  )  {
                                    if  (  pChannel.uiType==CONST_channelType_media) {
                                        //Log.d("qy", "snd: nTotal "  +  nTotal  +  " qNodes "  +  pChannel.outputQBuf2.size());
                                    }
                                }

                                //  2011/01/22
                                long dwTickCnt_endSending = myGetTickCount(null);
                            }
                            break;
                            default:
                                if (null != pMisCnt){
                                    //
                                    //pChannel.status.ulbSessionErr = true;
                                    setSessionErr(pChannel,true,"mcThreadProc_mis_snd.1033");
                                    //
                                    pChannel.status.ulbSndQuit = true;
                                }
                                return;
                        }

                        //  pChannel->status.netStat.nMsgSent  ++  ;
                        pChannel.status.dwTickCnt_lastEndSending = myGetTickCount64(null);   //  2011/01/23

                        dwTickCnt_lastEndSending_totalMsg = pChannel.status.dwTickCnt_lastEndSending;

                        //
                        continue;
                    }

                    //
                    continue;
                }

                //
                Log.d("qy",type+" mcThreadProc_mis_snd exit mmmmmmmm");
                showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "ends");

                if (null != pMisCnt) {
                    //
                    //pChannel.status.ulbSessionErr = true;
                    setSessionErr(pChannel,true,"mcThreadProc_mis_snd.run.1060");
                    //
                    pChannel.status.ulbSndQuit = true;
                }
            }


        }

        public static class CTX_thread_io extends ctxQmThread.CTX_qm_thread {
            //public CTX_qm_thread common;

            //
            public Thread hThread_toSend;                                  //  2008/05/23
            //DWORD dwThreadId_toSend;

            //  socket机制使用
            public Thread hThread_snd;
            //DWORD dwThreadId_snd;
        }

        /**
         * 音视频IO上传线程
         */
        public static class mcThreadProc_mis_io implements Runnable {
            qmCommFunc_mis.MIS_CHANNEL pChannel;

            public mcThreadProc_mis_io(qmCommFunc_mis.MIS_CHANNEL channel) {
                this.pChannel = channel;
            }
            private void threadLog(String str){
                if(pChannel.uiType == CONST_channelType_realTimeMedia){
                    Log.d(AUDIO_UPLOAD, "threadLog is "+  str);
                }
            }

            @Override
            public void run() {

                //
                Log.d( "qy", "mcThreadProc_mis_io thread enter");

                //
                int iErr = -1;
                if (null == pChannel) {
                    return;
                }

                //
                pChannel.status.iIoStep=1;

                //
                qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
                if (null == pMisCnt) {
                    threadLog("pMisCnt");
                    return;
                }

                //
                pChannel.status.iIoStep=2;


                ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
                if (null == pProcInfo) {
                    threadLog("pProcInfo");
                    return;
                }

                ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
                if (null == pQyMc) {
                    threadLog("pQyMc");
                    return;
                }

                int i;
                int iRet;
                qyMsgFunc.QY_MSG_HEAD msgHead = new qyMsgFunc.QY_MSG_HEAD();

                //qyTypes.QY_BUF buf_help1 = new qyTypes.QY_BUF();

                //RecyclableMemoryStreamManager recyclableMemoryStreamManager = new RecyclableMemoryStreamManager();

                int tmpiRet = -1;
                boolean bCryptInited = false;

                CTX_thread_io ctx = new CTX_thread_io();

                String where_showInfo = qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType) + "_R";

                qnmCommProc_mis.CMIS_MSGU pMsgBuf = new qnmCommProc_mis.CMIS_MSGU();

                //buf_help1.len = 956880;//(int) sizeof(qnmCommProc_mis);
                //buf_help1.data = new byte[buf_help1.len];

                //
                pChannel.status.iIoStep=10;


                if (pChannel.bSessionOpen.get()) {
                    threadLog("bSessionOpen");
                    exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1160");
                    return;
                }
                pChannel.commEncCtx = new qmCrypt.ENC_CTX_session();
                pChannel.commEncCtx.common = pMisCnt.commEncCtx.common;
                if (0 != initCrypt_qmc(pProcInfo, pMisCnt, CONST_cspContainer_qmc, DEFAULT_cspProvider, (pChannel.uiType == CONST_channelType_talking), pChannel.commEncCtx)) {
                    threadLog("initCrypt_qmc");
                    Log.e("initCrypt_qmc failed");
                    exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1168");
                    return;
                }

                bCryptInited = true;

                pChannel.commEncCtx.m_var.mutexName_syncCrypt = String.format("qmcCrypt%s", getuiNextTranNo.call());

                //
                if  (  pChannel.uiType==CONST_channelType_media) {
                    Log.d("qy", "video_R, starts");
                }

                //
                Log.d("qy", "before tryToStartChannel");

                //
                qyMcMainDaemon_isCli.qyFuncs.RESULT_startChannel result = new qyMcMainDaemon_isCli.qyFuncs.RESULT_startChannel();
                for (i = 0; i < CONST_maxTriesToStartSession_mis; i++) {
                    result.memset0();
                    if (0 == tryToStartChannel(pMisCnt, pChannel, result))
                        break;
                }
                if (i == CONST_maxTriesToStartSession_mis) {
                    String str;
                    str  =  "can't start session, channelType "  +  pChannel.uiType;
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open( str);
                    exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1196");
                    return;
                }
                //pChannel.status.ulbSessionErr = false;
                setSessionErr(pChannel,false,"mcThreadProc_mis_io.run.1200");
                pChannel.status.dwTickCnt_lastStartToRecv = pChannel.status.dwTickCnt_lastEndSending = myGetTickCount64(null);        //  2009/02/25

                bChkMessengerChannelsReady(pQyMc);

                //
                Log.d("qy","auth OK");
                showInfo_open.qyFuncs.showInfo_open( ("auth OK"));

                //
                pChannel.status.iIoStep=1136;


                /*
                ctx.hThread_toSend = new Thread(new mcThread_toSend.qyFuncs.mcThreadProc_mis_toSend(pChannel));
                ctx.hThread_toSend.setName("ThreadProc_mis_snd");  //zzq
                ctx.hThread_toSend.start();

                 */

                //
                showInfo_open.qyFuncs.showInfo_open( ("Function toSend is ready"));
                ctx.hThread_snd = new Thread(new mcThreadProc_mis_snd(pChannel));
                ctx.hThread_snd.setName("ThreadProc_mis_toSend"); //zzq
                ctx.hThread_snd.start();
                threadLog("ThreadProc_mis_toSend is created");
                showInfo_open.qyFuncs.showInfo_open( ("Function send is ready"));
                String thr_type;
                if(pChannel.uiType==CONST_channelType_media)
                {
                    thr_type = "video";
                }else if(pChannel.uiType == CONST_channelType_realTimeMedia)
                {
                    thr_type = "audio";
                }else if (pChannel.uiType == CONST_channelType_talking)
                {
                    thr_type = "talk";
                }
                else{
                    thr_type = "other";
                }
                Log.d(IO_TAG,"run "+thr_type+" io thread  uiType="+pChannel.uiType);

                //
                boolean  bDbg  =  false;
                long tick_wait0 = 0;
                long tick_wait1=0;
                long tick_recv =  0;
                long tick_beforePost = 0;
                long tick_post=0;
                String str;

                //
                if  (  pChannel.uiType==CONST_channelType_media) {
                    //bDbg = true;
                    pChannel.inCache_buf  =  new byte[bufSize_transferVideoData];
                }
                else  if  (  pChannel.uiType  ==  CONST_channelType_realTimeMedia)  {
                    pChannel.inCache_buf  =  new byte[bufSize_transferAudioData];
                }
                else  {
                      pChannel.inCache_buf  =  new byte[32  *  1024];
                }

                //
                pChannel.status.iIoStep=1184;

                //
                for (; bChannelOk(pChannel,false); ) {

                    //
                    pChannel.status.iIoStep=1190;

                    //
                    if  (  false) {
                        //
                        if  (  pChannel.uiType  ==  CONST_channelType_talking) {
                            tick_wait0 = myGetTickCount(null);
                            long iDiffInMs = tick_wait0 - pChannel.status.dwTickCnt_lastStartToRecv;
                            str =  "talk: elapse_fromRecv "  +  iDiffInMs  +  "ms";
                            //
                            showInfo_open.qyFuncs.showInfo_open(str);
                            //Log.d("tn_msgInput", "before wait");
                        }
                    }


                    //     threadLog("run");
                    iRet = guiShare.GuiShare.pf_qyWaitForRead.call(pChannel.sock, 2500);
                    if (iRet < 0) {
                        if(pChannel.uiType==5)
                        {
                            Log.i(AUDIO_UPLOAD,"break 1");
                        }
                        exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1281");
                        return;
                    }
                    if (0 == iRet) {
                        //
                        if  (  bDbg) {
                            Log.d("tn_msgInput", "wait returns 0");
                        }

                        //
                        continue;
                    }

                    //
                    if  (  bDbg) {
                        tick_wait1 = myGetTickCount(null);
                        //
                        str  =  "wait costs " + (tick_wait1  -  tick_wait0  ) + "ms";
                        showInfo_open.qyFuncs.showInfo_open(str);
                        //Log.d("tn_msgInput","wait costs " + (tick_wait1  -  tick_wait0  ) + "ms" );
                    }

                    //
                    pChannel.status.dwTickCnt_lastStartToRecv = myGetTickCount64(null);
                    ByteBuffer buffer;
                    if (null == (buffer = qyRecvMsg(pChannel.session, pChannel.sock, pQyMc.cfg.to, msgHead))) { //receive 音视频数据
                        Log.e("qy","mcThreadProc_mis_io: qyRecvMsg(  ) failed");
                        if(pChannel.uiType==5)
                        {
                            Log.i(AUDIO_UPLOAD,"break 2");
                        }
                        exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1312");
                        return;
                    }
                    pChannel.status.dwTickCnt_lastRecvd=myGetTickCount(null);

                    //
                    if  (  bDbg) {
                        tick_recv  =  myGetTickCount(null);
                        //
                        //Log.d("tn_msgInput", "recv consts "  +  (tick_recv  - tick_wait1)  +  "ms");
                        str= "recv consts "  +  (tick_recv  - tick_wait1)  +  "ms";
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }

                    //
                    if  (  pChannel.uiType==CONST_channelType_media) {
                        if  (  false  ) {
                            //Log.d("tn_msgInput", "video_R ...: msgHead.tn " + msgHead.uiTranNo);
                            str = "video_R ...: tn " + msgHead.uiTranNo  +  " len "  +  msgHead.uiLen;
                            showInfo_open.qyFuncs.showInfo_open(str);
                        }
                    }

                    //
                    qnmCommProc_mis.MIS_MSG_INPUT pMsg = new qnmCommProc_mis.MIS_MSG_INPUT();
                    pMsg.tStartTime = msgHead.i64StartTime;
                    pMsg.uiTranNo = msgHead.uiTranNo;
                    pMsg.usSeqNo = msgHead.usSeqNo;
                    pMsg.ucFlg = msgHead.ucFlg;
                    pMsg.usCode = msgHead.usCode;
                    pMsg.uiChannelType = pChannel.uiType;     //  2008/04/17
                    pMsg.lenInBytes = msgHead.uiLen;
                    pMsg.buffer = buffer;

                    if (isUcFlgMoreData(pMsg.ucFlg)) {
                        //
                        if  (  false  ) {
                            showInfo_open.qyFuncs.showInfo_open("io: get moreData. tn " + msgHead.uiTranNo + " sn " + msgHead.usSeqNo + " len " + msgHead.uiLen);
                        }
                        //
                        if  (  msgHead.uiTranNo  !=  pChannel.inCache_tn)  {
                            if  (  msgHead.usSeqNo!=0)  {
                                showInfo_open.qyFuncs.showInfo_open("io: err: first seqNo is not 0. skipped");
                                continue;
                            }
                            pChannel.inCache_tn  =  msgHead.uiTranNo;
                            pChannel.inCache_lastSeqNo=0;
                            pChannel.inCache_len  =  0;
                            //
                            if  (  msgHead.uiLen  >  pChannel.inCache_buf.length)  {
                                showInfo_open.qyFuncs.showInfo_open("io: err: 1376. msgHead.uiLen too big");
                                continue;
                            }
                            //
                            System.arraycopy(buffer.array(),0,pChannel.inCache_buf,0,msgHead.uiLen);
                            pChannel.inCache_len=msgHead.uiLen;
                            continue;
                        }
                        if  (  msgHead.usSeqNo!=pChannel.inCache_lastSeqNo  +  1)  {
                            str  =  "io:err: channel "  +  pChannel.uiType  +  ": seqNo "  +  msgHead.usSeqNo  +  " != inCache_lastSeqNo ("  +  pChannel.inCache_lastSeqNo  +  ") + 1";
                            showInfo_open.qyFuncs.showInfo_open(str);
                            continue;
                        }
                        if  (  msgHead.uiLen  +  pChannel.inCache_len  >  pChannel.inCache_buf.length  )  {
                            showInfo_open.qyFuncs.showInfo_open("io:err: 1389. msgHead.uiLen too big");
                            continue;
                        }
                        System.arraycopy(buffer.array(),0,pChannel.inCache_buf,pChannel.inCache_len,msgHead.uiLen);
                        pChannel.inCache_len  +=  msgHead.uiLen;
                        //
                        pChannel.inCache_lastSeqNo  =  msgHead.usSeqNo;
                        //
                        continue;
                    }
                    if (pMsg.usSeqNo != 0) {
                        /*
                        boolean bMsgSkipped = false;
                        Pair<Integer, Boolean> pair = procMisMsg_moreData(pChannel.inCacheQ, pMsg, bMsgSkipped);
                        if (0 != pair.getValue0()) {
                            if(pChannel.uiType==5)
                            {
                                Log.i(AUDIO_UPLOAD,"break 4");
                            }
                            exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1366");
                            return;
                        }
                        bMsgSkipped = pair.getValue1();
                        if (bMsgSkipped) continue;

                         */
                        if  (  pChannel.inCache_tn  !=  msgHead.uiTranNo)  {
                            showInfo_open.qyFuncs.showInfo_open("io:err:inCache_tn!=msgHead.tn");
                            continue;
                        }
                        if  (  pChannel.inCache_lastSeqNo  +  1  !=  msgHead.usSeqNo)  {
                            showInfo_open.qyFuncs.showInfo_open("io:err:inCache_lastSeqNo!=msgHead.seqNo");
                            continue;
                        }
                        if  (  msgHead.uiLen  +  pChannel.inCache_len  >  pChannel.inCache_buf.length)  {
                            showInfo_open.qyFuncs.showInfo_open("io:err: 1422. msgHead.len too big");
                            continue;
                        }
                        System.arraycopy(buffer.array(),0,pChannel.inCache_buf,pChannel.inCache_len,msgHead.uiLen);
                        pChannel.inCache_len  +=  msgHead.uiLen;
                        //
                        buffer=ByteBuffer.wrap(pChannel.inCache_buf);
                        buffer.position(0);
                        buffer.limit(pChannel.inCache_len);
                        pMsg.buffer=buffer;

                        //
                        pMsg.lenInBytes  =  pChannel.inCache_len;
                    }

                    int head_len = 24;//sizeof(QY_MSG_HEAD)
                    pChannel.status.netStat.ui64MsgDataLenRecvd += head_len + pMsg.lenInBytes;     //  2009/10/10

                    if (null == (pMsg.buffer = decryptData(pChannel.commEncCtx,  pMsg.buffer))) {
                        //
                        showInfo_open.qyFuncs.showInfo_open( ("io: decryptData failed."));
                        //
                        if(pChannel.uiType==5)
                        {
                            Log.i(AUDIO_UPLOAD,"break 5");
                        }
                        exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1382");
                        return;
                    }

                    // 把音视频数据发送到队列,zzq ,中转线程
                    if(pChannel.uiType == CONST_channelType_media)
                    {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, pProcInfo.str_who_showInfo, null, where_showInfo, " msgHead.uiTranNo " + msgHead.uiTranNo);
                        //Log.d("tn_msgInput","video_R: msgHead.tn "  +  msgHead.uiTranNo);
                        //
                    }else if (pChannel.uiType == CONST_channelType_realTimeMedia)
                    {
                        //Log.d(Consts.AUDIO_DOWNLOAD,"0 receive audio data");
                    }

                    //
                    pChannel.dbg_sn  ++  ;
                    pMsg.dbg_sn  =  pChannel.dbg_sn;


                    //
                    if  (  bDbg) {
                        tick_beforePost = myGetTickCount(null);
                        //Log.d("tn_msgInput", "recv -> before post costs " +  (tick_beforePost - tick_wait0) + "ms" );
                        str  =  "recv -> before post costs " +  (tick_beforePost - tick_wait0) + "ms" ;
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }


                    //
                    if (0 > (tmpiRet = pProcInfo.postReciveMsg2Mgr_mc(ctx.ctxCaller, pMisCnt, null, CONST_misMsgType_input, msgHead, 0, pMsg, 0, 0, pChannel.uiType, pMsgBuf, false))) {
                        if (tmpiRet == CONST_qyRet_qFull) {
                            switch (pChannel.uiType) {
                                case CONST_channelType_media:
                                case CONST_channelType_realTimeMedia:
                                    Log.d("postMsg2Mgr_mc returns qFull, skipped.");
                                    showInfo_open.qyFuncs.showInfo_open(("postMsg2Mgr_mc returns qFull, skipped."));
                                    continue;
                                default:
                                    break;
                            }
                        }
                        Log.e("postMsg2Mgr_mc failed.");
                        showInfo_open.qyFuncs.showInfo_open(("postMsg2Mgr_mc failed."));
                        if(pChannel.uiType==5)
                        {
                            Log.i(AUDIO_UPLOAD,"break 6");
                        }
                        exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1425");
                        return;
                    }

                    //
                    if  (  bDbg  )  {
                        tick_post  =  myGetTickCount(null);
                        //Log.d("tn_msgInput","post consts "  +  (tick_post  -  tick_beforePost)  +  "ms");
                        str = "post consts "  +  (tick_post  -  tick_beforePost)  +  "ms";
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }

                    //
                    continue;

                }   //  end of for(;;)

                Log.d("qy","exit "+ thr_type + " io thread run");
                //
                exit_thread(pChannel, pProcInfo, ctx, where_showInfo, pQyMc, bCryptInited, pMisCnt,"io.run.1444");

                //
                /*
                Log.d(Consts.AUDIO_DOWNLOAD,"io thread exit");
                Log.d(Consts.VIDEO_DOWNLOAD,"io thread exit");
                Log.d(AUDIO_UPLOAD,"io thread exit");
                Log.d(VIDEO_UPLOAD,"io thread exit");

                 */
                Log.d("qy","io thread exit, after exit_thread. channelType "  +  pChannel.uiType);

                return;
            }
        }

        private static void exit_thread(qmCommFunc_mis.MIS_CHANNEL pChannel, ctxQmc.CCtxQmc pProcInfo, CTX_thread_io ctx, String where_showInfo, ctxQyMc.CCtxQyMc pQyMc, boolean bCryptInited, qmCommFunc_mis.MIS_CNT pMisCnt, String hint) {

            //
            pChannel.status.iIoStep=1383;

            //
            Log.d("qy","io.exit_thread enters "  +  hint);


            //
            //pChannel.status.ulbSessionErr = true;
            setSessionErr(pChannel,true,"mcThreadProc_io.exit_thread.1472");

            //
            /*
            MACRO_qyNullThread(ctx.hThread_toSend);
            showInfo_open.qyFuncs.showInfo_open(("Function toSend is closed"));
             */

            //
            MACRO_qyNullThread(ctx.hThread_snd);
            showInfo_open.qyFuncs.showInfo_open(("Function snd is closed. channelType ")  +  pChannel.uiType);

            //
            pChannel.status.iIoStep=1404;

            //
            if (pChannel.bSessionOpen.get()) {
                //
                pChannel.status.iIoStep  =  1409;
                //
                /*
                commonSock.SOCK_TIMEOUT to_close = new commonSock.SOCK_TIMEOUT();
                to_close.iRead  =  500;
                to_close.iSelect  = 500;
                to_close.iWrite  =  500;
                 */
                commonSock.SOCK_TIMEOUT to_close  =  pQyMc.cfg.to;
                qnmCloseSession_msg(pChannel.session, pChannel.sock, to_close);
                //
                pChannel.status.iIoStep  =  1413;

                //
                pChannel.bSessionOpen.set(false);
                Log.d("Session is closed");
                showInfo_open.qyFuncs.showInfo_open(("Session is closed"));
            }
            if (bCryptInited) exitCrypt_qmc(pProcInfo, pMisCnt, pChannel.commEncCtx);

            //
            pChannel.status.iIoStep  =  1423;


            //
            pChannel.status.ulbIoQuit1 = true;
        }
    }
}
