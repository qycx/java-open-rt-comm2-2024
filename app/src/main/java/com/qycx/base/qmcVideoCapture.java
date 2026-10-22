


//
package com.qycx.base;

import static com.qycx.base.qmcMasterSlave.qyFuncs.bExists_slave;
import static com.qycx.base.qmcSharedObj.Consts.CONST_camCapType_android;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capType_av;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.sharedObjUsr.qyFuncs.resetCurSharedObjUsr;
import static com.qycx.base.sharedObjUsr.qyFuncs.resetCurSharedObjUsr_localAv;
import static com.qycx.base.tmpFunc.qyFuncs.MACRO_mysafeFree;
import static com.qycx.base.videoFunc_open.qyFuncs.makeBmpInfoHeader_i420;

import android.widget.Toast;

import com.qycx.Activity_dlgTalk;
import com.qycx.MyApplication;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

public class qmcVideoCapture {

    public static class Consts {
        public final static int CONST_videoCompressors_null = 0;
        public final static int CONST_videoCompressors_vcm = 1;    //  "vcm"
        public final static int CONST_videoCompressors_dmo = 2;    //  "dmo"
        public final static int CONST_audioCompressors_acm = 3;    //  "acm"					//  2009/05/16
        public final static int CONST_audioCompressors_dmo = 4;    //  "dmo"					//  2009/05/16
        public final static int CONST_videoCompressors_custom = 5; //  "custom"				//  2010/04/28
        public final static int CONST_audioCompressors_custom = 6; //  "custom"				//  2010/04/28
        //
        public final static int CONST_audioCompressors_ipp = 7;    //
        public final static int CONST_videoCompressors_ipp = 8;    //	"h264"					//  2011/07/30
        //
        public final static int CONST_audioCompressors_hwAccl = 9; //
        public final static int CONST_videoCompressors_hwAccl = 10;    //	"h264"					//  2011/11/25
        //
        //
        //  13
        public final static int CONST_videoCompressors_stream = 14;    //  "h264"					//  for rtsp. 2014/02/25
        //
        public final static int CONST_audioCompressors_vorbis = 15;    //
        public final static int CONST_videoCompressors_vp8 = 16;   //	"vp8"					//  2014/07/19

        //
        public final static int CONST_audioCompressors_ffmpeg = 21;
        public final static int CONST_videoCompressors_ffmpeg = 22;

        // 10 21 22

    }


    public static class SMPL_bsRead_param {
        public int dwTickCnt_doPretrans;
    }

    ;


    public static class qyFuncs {
        public static int qmcChkTasks_gui(ctxQmc.CCtxQmc pProcInfo)           //  2009/09/10
        {

            return 0;
        }


        public static int qmcApplyForRemovingInvalidTasks(ctxQmc.CCtxQmc pProcInfo, int uiChannelType) {

            return 0;
        }

        public static int org2Rgb(ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.CAP_procInfo_bmpCommon pVc, byte[] pInput, byte[] pOrg2RgbBuf) {
            return -1;
        }

        //
        public static int refreshWebCamSettings(qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg, qmcSharedObj.CAP_procInfo_video pVc) {
            //QY_MC pQyMc = QY_GET_GBUF();

            //memset(pVc->policy, 0, sizeof(pVc->policy)  );
            qmcSharedObj.CAP_procInfo_video_policy p = pVc.policy;
            {
                //qyFuncs.mymemset(p, 0, sizeof(CAP_procInfo_video_policy));
            }


            {
                pVc.policy.bih.biWidth = pVideoCompressorCfg.common.head.iWidth_pic;
                pVc.policy.bih.biHeight = pVideoCompressorCfg.common.head.iHeight_pic;
            }

            return 0;
        }


        //
        public static int openLocalVideo(ctxQmc.CCtxQmc pProcInfoCommon, int uiCamCapType, String displayName, Object hTalk, int iWndContentType, qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressor, int iIndex_sharedObj) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            policyAvParams.PolicyAvParams pAvParams = (policyAvParams.PolicyAvParams) pProcInfoCommon.p_gAvParams;  //  QY_GET_PolicyAvParams(  );
            if (null == pAvParams) return -1;

            int usCapDriverIndex = 0;
            int iIndex_capBmp = -1;
            int uiTranNo_sharedObj = 0;

            if (null == pProcInfoCommon) return -1;
            ctxQmc.CCtxQmc pProcInfo = pProcInfoCommon;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            qmcSharedObj.CAP_procInfo_video pVc = null;


            pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return iErr;
            pSharedObj.var.bCapDev = true;

            int iIndex_sharedObj_used = -1;

            //
            iIndex_capBmp = pSharedObj.var.iIndex_capBmp;
            uiTranNo_sharedObj = pSharedObj.var.uiTranNo;      //  2011/05/31

            //
            pVc = (qmcSharedObj.CAP_procInfo_video) getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
            if (null == pVc) return iErr;


            if (pVc.bCapDevConnected) {
                return iErr;
            }

            //
            if  (  pProcInfo.bUse100k)  {
                Toast.makeText(MyApplication.getContext(),"输出带宽限制为100k", Toast.LENGTH_SHORT).show();
            }

            //
            pSharedObj.var.hMutex_syncEncFrame = new Semaphore(1);
            String evtName = "encFrame" + iIndex_sharedObj;
            pSharedObj.var.hEvt_notifyFrameAvail = new EventWaitHandle(false, EventResetMode.AutoReset, evtName, 1);

            pVc.uiType = CONST_capType_av;
            pVc.iIndex_sharedObj = iIndex_sharedObj;
            pVc.uiCamCapType = uiCamCapType;
            pVc.displayName = displayName;
            pVc.bCaptureStarted_openLocalVideo = false;

            //
            if  (pProcInfo.bUse100k)  {
                {
                    pVideoCompressor.common.head.iAvgBitrate = pProcInfo.cfgInfo100k.use100k_v_biterate;
                    //pVideoCompressor.common.head.iAvgBitrate  =  20000;
                }

            }

            //
            refreshWebCamSettings(pVideoCompressor, pVc);
            switch (uiCamCapType) {

                case CONST_camCapType_android:
                    if (0 != guiShare.GuiShare.pf_initVideoCapDev.call(hTalk, iWndContentType, "", pVc.policy.bih, pVc)) {
                        Log.e("openLocalVideo.254, initVideoCapDev failed");
                        return iErr;
                    }
                    pVc.bCapDevConnected = true;
                    if (!guiShare.GuiShare.pf_bGetCapturePara.call(hTalk, iWndContentType, pVc.vh_org))
                        return iErr;

                    break;
                default:

                    return iErr;
            }
            if (!pVc.bCapDevConnected) return iErr;

            boolean bOk_org = false;
            if (!bOk_org) {
                if (0 != initOrg2IntBuf(pProcInfoCommon, pVideoCompressor, (qmcSharedObj.CAP_procInfo_bmpCommon) pVc)) {
                    Log.e("initOrg2IntBuf failed");
                    return iErr;
                }
            }

            //
            qyCommProc_open.QY_VIDEO_HEADER pVh_dec = pVc.bOrg2IntBufInited ? pVc.vh_int : pVc.vh_org;

            //  2014/04/03
            pVideoCompressor.common.pVideoQ2 = pVc.thread.q2;
            pVideoCompressor.common.pParent_transform = pVc;
            pVideoCompressor.index_sharedObj = iIndex_sharedObj;
            pVideoCompressor.m_bFront = pVc.m_bFront;

            if (0 != pProcInfoCommon.initCompressVideo(pVh_dec.bih, CONST_capType_av, pVideoCompressor, true, 0, pVc.compressVideo)) {
                Log.e("initCompressVideo failed");
                return iErr;
            }

            iErr = 0;
            return iErr;
        }


        //
        public static int closeLocalVideo(Object hWnd, int iWndContentType, int iIndex_sharedObj) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;//QY_GET_GBUF(  );
            if (null == pQyMc) return -1;

            ctxQmc.CCtxQmc pProcInfoCommon = pQyMc.get_pProcInfo();
            if (null == pProcInfoCommon) return -1;
            int iIndex_capBmp = -1;

            ctxQmc.CCtxQmc pProcInfo = pProcInfoCommon;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            qmcSharedObj.CAP_procInfo_video pVc = null;

            pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return iErr;
            //
            iIndex_capBmp = pSharedObj.var.iIndex_capBmp;

            //
            pVc = (qmcSharedObj.CAP_procInfo_video) getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
            if (null == pVc) return iErr;

            //  2017/09/13
            pVc.bCaptureStarted_openLocalVideo = false;

            //
            stopCapture(hWnd, iWndContentType, pVc);
            if (pVc.bCaptureStarted) {

                return iErr;
            }
            if (pVc.bCapDevConnected) {
                // Finally disconnect the driver
                switch (pVc.uiCamCapType) {
                    case CONST_camCapType_android:
                        guiShare.GuiShare.pf_exitVideoCapDev.call(hWnd, iWndContentType);
                        pVc.bCapDevConnected = false;
                        break;
                    default:
                        return iErr;
                }
                //memset(pVc.capDev, 0, sizeof(pVc.capDev)  );
                pVc.capDev.memset0();

            }
            if (pVc.bCapDevConnected) {
                return iErr;
            }

            //
            if (0 != pProcInfoCommon.exitCompressVideo(true, pVc.compressVideo)) return iErr;


            //  2009/05/30
            exitOrg2IntBuf(pProcInfoCommon, (qmcSharedObj.CAP_procInfo_bmpCommon) pVc);


            //
            if (pSharedObj.var.hMutex_syncEncFrame != null) {
                if (pSharedObj.var.hMutex_syncEncFrame.tryAcquire()) {
                    pSharedObj.var.hMutex_syncEncFrame.release();
                }
                pSharedObj.var.hMutex_syncEncFrame = null;
            }
            if (pSharedObj.var.hEvt_notifyFrameAvail != null) {
                pSharedObj.var.hEvt_notifyFrameAvail.Close();
                pSharedObj.var.hEvt_notifyFrameAvail = null;
            }


            //
            iErr = 0;


            return iErr;
        }


        public static void exitOrg2IntBuf(ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.CAP_procInfo_bmpCommon pVc) {


            MACRO_mysafeFree(pVc.pOrg2IntBuf);
            pVc.iOrg2IntBufSize = 0;

            pVc.bOrg2IntBufInited = false;
        }


        // to intermediate buf
        public static int initOrg2IntBuf(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressor, qmcSharedObj.CAP_procInfo_bmpCommon pVc) {
            int iErr = -1;
            //BITMAPINFOHEADER pBmpInfo = null;
            int iFormatSize = 0;
            int biSizeImage = 0;

            //
            if (pVc.bOrg2IntBufInited) return -1;

            //memset(pVc.vh_dec, 0, sizeof(pVc.vh_dec)  );
            pVc.vh_int = new qyCommProc_open.QY_VIDEO_HEADER();

            qyCommProc_open.QY_VIDEO_HEADER pVh_dec = pVc.vh_int;
            {
                switch (pVc.mSensorOrientation) {
                    case 90:
                    case 270:
                        makeBmpInfoHeader_i420(12, pVc.vh_org.bih.biHeight, pVc.vh_org.bih.biWidth, pVh_dec.bih);
                        break;
                    case 0:
                    case 180:
                    case 360:
                        makeBmpInfoHeader_i420(12, pVc.vh_org.bih.biWidth, pVc.vh_org.bih.biHeight, pVh_dec.bih);
                        break;
                    default:
                        exitOrg2IntBuf(pProcInfo, pVc);
                        return iErr;
                }
            }

            if (pVc.vh_int.bih.biWidth == pVc.vh_org.bih.biWidth
                    && pVc.vh_int.bih.biHeight == pVc.vh_org.bih.biHeight
                    && pVc.mSensorOrientation == 0
                    && pVc.vh_int.bih.biCompression == pVc.vh_org.bih.biCompression
            ) {
                iErr = 0;
                exitOrg2IntBuf(pProcInfo, pVc);
                return iErr;
            }


            //
            pVc.iOrg2IntBufSize = Math.max(pVc.vh_int.bih.biSizeImage, biSizeImage) + iFormatSize + 4096;
            if (pVc.iOrg2IntBufSize <= 0) {
                exitOrg2IntBuf(pProcInfo, pVc);
                return iErr;
            }

            //  
            pVc.pOrg2IntBuf = new byte[pVc.iOrg2IntBufSize];

            pVc.bOrg2IntBufInited = true;

            iErr = 0;


            //if (pBmpInfo!=null) { qyMemoryFunc.qyFuncs.myfree(pBmpInfo); pBmpInfo = NULL; }
            if (0 != iErr) {
                exitOrg2IntBuf(pProcInfo, pVc);
                //
                //char buf[128] = "";
                //iFourcc2Str(pVc.vh_org.bih.biCompression, buf, mycountof(buf));
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("initOrg2RgbBuf failed, fourcc %s"), CQyString(buf));
            }

            return iErr;

        }

        public static int startCapture(Object hTalk, int iWndContentType, qmcSharedObj.CAP_procInfo_video pVc) {
            int iErr = -1;


            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            if (!pVc.bCapDevConnected) return iErr;

            if (pVc.bCaptureStarted) {       //  ÒÑ¾­Æô¶¯ÁË
                iErr = 0;
                return iErr;
            }

            switch (pVc.uiCamCapType) {

                case CONST_camCapType_android:
                    if (0 != guiShare.GuiShare.pf_startCapture.call(hTalk, iWndContentType))
                        return iErr;
                    break;
                default:
                    return iErr;
            }
            pVc.bCaptureStarted = true;
            pVc.dwTickCnt_captureStarted = tmpFunc.qyFuncs.myGetTickCount(null);  //  2017/09/19

            /**/

            //
            iErr = 0;

            return iErr;

        }

        public static int stopCapture(Object hTalk, int iWndContentType, qmcSharedObj.CAP_procInfo_video pVc) {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            if (pVc.bCaptureStarted) {

                switch (pVc.uiCamCapType) {

                    case CONST_camCapType_android:
                        guiShare.GuiShare.pf_stopCapture.call(hTalk, iWndContentType);
                        break;

                    default:
                        return iErr;
                }

                pVc.bCaptureStarted = false;

            }

            iErr = 0;


            return iErr;
        }


        public static int newSharedObjIndex(ctxQmc.CCtxQmc pProcInfo, Object hWnd, int iSharedObjType, int piIndex_sharedObjUsr, boolean pbFirstUsr) {
            int iErr = -1;
            int index = 0;

            return -1;

        }


        public static int setCurSharedObjUsrFunc(ctxQmc.CCtxQmc pMC_VAR_isCli, int index_sharedObj, boolean bLocalAv, Object hWnd) {

            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pMC_VAR_isCli;

            //  int								index				=	0;
            //MC_VAR_isCli				*		pProcInfo			=	QY_GET_procInfo_isCli(  );
            //
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;

            if (!guiShare.GuiShare.pf_IsWindow.call(hWnd)) return -1;

            //  pSharedObj  =  &pProcInfo->pSharedObjs[index];
            pSharedObj = getSharedObjByIndex(pProcInfo, index_sharedObj);
            if (null == pSharedObj) return -1;
            if (pSharedObj.var.bQuit) return -1;
            if (!pSharedObj.bUsed) return -1;

            int i;
            for (i = 0; i < (pSharedObj.usrs.length); i++) {
                Activity_dlgTalk starter = null;
                Activity_dlgTalk wnd = null;
                if (pSharedObj.usrs[i].hWnd_starter instanceof WeakReference<?>)
                    starter = (Activity_dlgTalk) ((WeakReference<?>) pSharedObj.usrs[i].hWnd_starter).get();
                else
                    starter = (Activity_dlgTalk) pSharedObj.usrs[i].hWnd_starter;
                if (hWnd instanceof WeakReference<?>)
                    wnd = (Activity_dlgTalk) ((WeakReference<?>) hWnd).get();
                else
                    wnd = (Activity_dlgTalk) hWnd;

                if (starter == wnd) break;
//                if (null == starter || null == wnd) return -1;
//                if (pSharedObj.usrs[i].hWnd_starter == hWnd) break;
            }
            if (i == (pSharedObj.usrs.length)) return -1;

            if (pSharedObj.var.iIndex_curUsr != i) {
                //
                qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

                pSharedObj.var.iIndex_curUsr = i;
                //
                pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
                //
                //setROUTE_sendLocalAv(pProcInfo, pMisCnt, pSharedObjUsr.idInfo_to, pSharedObjUsr.ucbVideoConferenceStarter,  pSharedObj.curRoute_sendLocalAv);

            }


            return 0;

        }

        //
        public static int setCurSharedObjUsr_localAv(ctxQmc.CCtxQmc pMC_VAR_isCli, int index_sharedObj, Object hWnd) {

            if (null == pMC_VAR_isCli) return -1;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pMC_VAR_isCli;

            //
            //Log.d("setCurSharedObjUsr_localAv called");


            //
            boolean bLocalAv = false;
            if (0 != setCurSharedObjUsrFunc(pMC_VAR_isCli, index_sharedObj, bLocalAv, hWnd)) {
                resetCurSharedObjUsr_localAv(pProcInfo, index_sharedObj);
                //
                //pProcInfo->set_enlarged_gZone(hWnd);
            }

            //
            if (bExists_slave(pProcInfo, index_sharedObj)) {
                qmcSharedObj.QY_SHARED_OBJ pSharedObj_master = getSharedObjByIndex(pProcInfo, index_sharedObj);
                if (pSharedObj_master != null) {
                    int iIndex_sharedObj_slave = pSharedObj_master.var.master.slaveKey.iIndex_sharedObj;
                    //
                    if (0 != setCurSharedObjUsr(pProcInfo, iIndex_sharedObj_slave, hWnd)) {
                        resetCurSharedObjUsr(pProcInfo, iIndex_sharedObj_slave);
                    }
                }
            }


            //
            return 0;
        }


        //
        public static int setCurSharedObjUsr(ctxQmc.CCtxQmc pProcInfo, int index_sharedObj, Object hWnd) {
            boolean bLocalAv = false;
            return setCurSharedObjUsrFunc(pProcInfo, index_sharedObj, bLocalAv, hWnd);
        }


    }


}
