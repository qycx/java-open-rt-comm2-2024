


package com.qycx.base;

import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_h264_enc;
import static com.qycx.base.isProtocolFunc.qyFuncs.bSupported_sendVhDec;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_compress;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_dec_amf;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_dec_nv;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_dec_vp8;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ipp;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaCodec;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaSdk_dec;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_custom;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_dmo;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_ffmpeg;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_hwAccl;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_ipp;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_stream;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_vcm;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_vp8;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setMyStep;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_I420;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_YV12;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyDefs.Consts.CONST_objType_mosaicStream_resource;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.smplFunc_v.qyFuncs.smplBitstreamReader_ReadNextFrame_func_v;
import static com.qycx.base.taskAv.qyFuncs.M_getMutexName_syncActiveMem;
import static com.qycx.base.tmpFunc.qyFuncs.MACRO_mysafeFree;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;
import static com.qycx.base.tmpFunc.qyFuncs.mytoString;
import static com.qycx.base.videoFunc_open.Consts.BI_RGB;
import static com.qycx.base.videoFunc_open.Consts.cntof_buf5;
import static com.qycx.base.videoFunc_open.qyFuncs.iFourcc2Str;
import static com.qycx.base.videoFunc_open.qyFuncs.makeBmpInfoHeader_i420;

import com.qycx.base.util.Log;


public class taskAvProc {

    public static class qyFuncs {
        public static int adjustModuleType_decV(int uiModuleType) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            //
            switch (uiModuleType) {
                case CONST_moduleType_mediaSdk_dec:
                case CONST_moduleType_ipp:
                    //
                    if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                        return CONST_moduleType_ffmpeg;
                    } else {
                        return CONST_moduleType_mediaCodec;
                    }
                default:
                    break;
            }

            return 0;
        }

        public static int adjustModuleType_encV(int uiModuleType) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            switch (uiModuleType) {
                case CONST_moduleType_ffmpeg:
                    if (!pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                        uiModuleType = CONST_moduleType_mediaCodec;
                    }
                    break;
                default:
                    break;
            }

            return uiModuleType;
        }


        //

        public static int adjustModuleType_decA(int uiModuleType) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            switch (uiModuleType) {
                case CONST_moduleType_ffmpeg: {
                    return CONST_moduleType_mediaCodec;
                }
                default:
                    break;
            }

            return 0;
        }


        //
        public static int initTaskAvFrom_video(ctxQmc.CCtxQmc pProcInfo, qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from, int index_activeMems_from, int iTaskId, int uiModuleType_suggested, boolean bUseDecTool_suggested, qmcTransform.QY_TRANSFORM pTransform) {
            int iErr = -1;
            String str_i = "initTaskAvFrom_video start";
            showInfo_open.qyFuncs.showInfo_open(str_i);
            com.qycx.base.util.Log.e(str_i);

            //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            //TCHAR tHintBuf[256] = "";
            int tmpiRet = -1;
            int i;
            //TCHAR tBuf[128];

            String str;

            //
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;

            //
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) return -1;

            //
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) {  //  2014/08/04
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("initTaskAvFrom_video failed: pTask is null."));
                return -1;
            }
            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;

            //  2012/03/19
            M_setMyStep(pTransformVideo.debugStep, 31);

            //
            if (pTransformVideo.ucbInited) {   //  2009/05/29
                Log.e( "initTaskAvFrom_video failed: already inited.");
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("initTaskAvFrom_video failed: already inited."));
                if (0 != iErr) {
                    exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                }
                return iErr;
            }

            if (!pTransformVideo.ucbInited) {

                try {

                    //  2009/05/28
                    {
                        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                            ;
                            //TCHAR mutexName[128];
                            qyMcMainDaemon_isCli_mgr.TMP_tBuf128 mutexName = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
                            int cntof_mutexName = cntof_tBuf128;
                            //  2011/07/03
                            //  _sntprintf(  mutexName,  mycountof(  mutexName  ),  (  "%s%d"  ),  pProcInfo->cfg_mutexName_syncActiveMem_prefix,  iTaskId  );
                            M_getMutexName_syncActiveMem(pQmcCfg, iTaskId, mutexName.tBuf, cntof_mutexName);

                            if (0 != syncObj.sync(mutexName.tBuf, pMisCnt.hMutex_syncActiveMem, 1000,"initTaskAvFrom_video.171"))
                            {
                                if (0 != iErr) {
                                    exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                                }
                                return iErr;
                            }

                            pTransformVideo.idInfo.ui64Id = pActiveMem_from.avStream.idInfo.ui64Id;
                            pTransformVideo.tv.uiTranNo_openAvDev = pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                            //
                            //pTransformVideo.tv.compressor.ucCompressors = pFrom.video.tv_recvd.compressor.ucCompressors;
                            //pTransformVideo.tv.compressor.usMaxFps_toShareBmp = pFrom.video.tv_recvd.compressor.usMaxFps_toShareBmp;        //  2014/08/07
                            pTransformVideo.tv.compressor = pActiveMem_from.avStream.obj.tranInfo.video.compressor;
                            //pTransformVideo.tv.compressor.common.ucbOutputFlippedImg  =  pFrom.video.tv_recvd.compressor.common.ucbOutputFlippedImg;		//  2011/12/11
                            //
                            //memcpy(pTransformVideo.tv.vh_compress,pFrom.video.tv_recvd.vh_compress, sizeof(pTransformVideo.tv.vh_compress)  );
                            pTransformVideo.tv.vh_compress = pActiveMem_from.avStream.obj.tranInfo.video.vh_compress;

                            //  2014/02/25
                            //memcpy(pTransformVideo.tv.vh_stream,pFrom.video.tv_recvd.vh_stream, sizeof(pTransformVideo.tv.vh_stream)  );
                            pTransformVideo.tv.vh_stream = pActiveMem_from.avStream.obj.tranInfo.video.vh_stream;

                            if (0 == pTransformVideo.tv.vh_stream.dwFourcc) {
                                pTransformVideo.tv.vh_stream.dwFourcc = (int) pActiveMem_from.avStream.obj.tranInfo.video.vh_compress.bih.biCompression;

                            }

                            //  2014/09/23
                            if (bSupported_sendVhDec()) {
                                //memcpy(pTransformVideo.tv.vh_decompress,pFrom.video.tv_recvd.vh_decompress, sizeof(pTransformVideo.tv.vh_decompress));
                                pTransformVideo.tv.vh_decompress = pActiveMem_from.avStream.obj.tranInfo.video.vh_decompress;
                            }

                            {  //  2014/09/23. 用vh_compress的信息来填充vh_decompress
                                switch (pTransformVideo.tv.compressor.ucCompressors) {
                                    case CONST_videoCompressors_vcm:
                                    case CONST_videoCompressors_dmo: {
                                        qyCommProc_open.BITMAPINFOHEADER pbih = pTransformVideo.tv.vh_decompress.bih;
                                        videoFunc_open.qyFuncs.makeBmpInfoHeader_rgb(24, pTransformVideo.tv.vh_compress.bih.biWidth, pTransformVideo.tv.vh_compress.bih.biHeight, pbih);
                                    }
                                    break;
                                    default:
                                        switch (pTransformVideo.tv.compressor.iFourcc) {
                                            case CONST_fourcc_h264:
                                            case CONST_fourcc_HEVC: {
                                                qyCommProc_open.BITMAPINFOHEADER pbih = pTransformVideo.tv.vh_decompress.bih;
                                                makeBmpInfoHeader_i420(12, pTransformVideo.tv.vh_decompress.bih.biWidth, pTransformVideo.tv.vh_decompress.bih.biHeight, pbih);
                                            }
                                            break;
                                            default:
                                                break;
                                        }
                                        break;
                                }
                            }

                            //  2014/08/05
                            pTransformVideo.dwLastmodifiedTickCnt_keyParams = tmpFunc.qyFuncs.myGetTickCount(null);
                            syncObj.Dispose();
                        }
                    }

                } catch (Exception e) {
                    //e->Delete();
                    Log.e(e.getMessage());
                    e.printStackTrace();
                    if (0 != iErr) {
                        exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                    }
                    return iErr;
                }

                //
                if  (  pActiveMem_from.avStream.obj.resObj.uiObjType  ==  CONST_objType_mosaicStream_resource)  {
                    mySleep(1000);
                    showInfo_open.qyFuncs.showInfo_open(  0,  "",  "辅流暂时不支持");
                    return  iErr;
                }

                //
                //memset(pTransformVideo.debugStep, 0, sizeof(pTransformVideo.debugStep)  );   //  2014/08/11
                qnmCommProc_is_open.MY_DEBUG_STEP pStep = pTransformVideo.debugStep;
                {
                    //mymemset(pStep, 0, sizeof(MY_DEBUG_STEP));
                }
                //memset(pTransformVideo.status, 0, sizeof(pTransformVideo.status)  );
                qmcTransform.TRANSFORM_VIDEO_procInfo_status pstatus = pTransformVideo.status;
                {
                    //mymemset(pstatus, 0, sizeof(TRANSFORM_VIDEO_procInfo_status));
                    pTransformVideo.status.clearData();

                }
                //  2015/01/15
                //memset(pTransformVideo.decInfo, 0, sizeof(pTransformVideo.decInfo)  );
                qmcTransform.TRANSFORM_VIDEO_procInfo_decInfo pdecInfo = pTransformVideo.decInfo;
                {
                    pdecInfo.clearData();
                    //mymemset(pdecInfo, 0, sizeof(TRANSFORM_VIDEO_procInfo_decInfo));
                }

                //  2012/03/19
                M_setMyStep(pTransformVideo.debugStep, 33);


                //
                if (0 == pActiveMem_from.avStream.obj.tranInfo.video.compressor.ucCompressors)
                    pTransformVideo.tv.compressor.ucCompressors = CONST_videoCompressors_vcm;       //  2009/03/30

                //  2014/0325
                //memset(pTransformVideo.u, 0, sizeof(pTransformVideo.u)  );
                pTransformVideo.u_qoi.myQoi.common.memset0();

                if (pTransformVideo.tv.compressor.ucCompressors == CONST_videoCompressors_vcm) {
                    /**/
                    Log.e(("vcm is not supported in winCe"));
                    if (0 != iErr) {
                        exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                    }
                    return iErr;
                } else if (pTransformVideo.tv.compressor.ucCompressors == CONST_videoCompressors_dmo) {


                    {
                        //if  (  initDecompressVideo_filterGraph(  &pFrom.video.tv_recvd.compressor,  &pTransformVideo.tv.vh_compress,  &pTransformVideo.tv.vh_decompress,  &pTransformVideo.debugStep,  &pTransformVideo.u.dmo  )  )
                        {
                            str = "initTaskAvFrom_video: initDecVideo_dmo not supported.";

                            showNotification(0, null, null, 0, 0, 0, str);
                            //_sntprintf(tHintBuf, mycountof(tHintBuf), ("%s initDecompressVideo_filterGraph failed."), tHintBuf);
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;
                        }
                    }

                } else {
                    qyMcMainCusModule.CUS_MODULE_compress pModule = null;
                    int uiModuleType = 0;             //  2012/09/09

                    switch (pTransformVideo.tv.compressor.ucCompressors) {
                        case CONST_videoCompressors_ipp:                //  2011/07/31
                            //  pModule	=	pQyMc->cusModules.pIpp;
                            uiModuleType = CONST_moduleType_ipp;    //  2012/09/09
                            break;
                        case CONST_videoCompressors_hwAccl:     //  2011/11/25
                        case CONST_videoCompressors_ffmpeg:
                            //
                        {
                            //uiModuleType  =  CONST_moduleType_ipp;
                            uiModuleType = CONST_moduleType_mediaSdk_dec;
                        }
                        break;
                        case CONST_videoCompressors_stream:
                            switch (pTransformVideo.tv.vh_stream.dwFourcc) {
                                case CONST_fourcc_h264:
                                case CONST_fourcc_HEVC: //  2017/10/08
                                    break;
                                default: {
                                    if (0 != iErr) {
                                        exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                                    }
                                    return iErr;
                                }
                            }
                            uiModuleType = CONST_moduleType_ipp;
                            //
                            break;
                        case CONST_videoCompressors_vp8:        //  2014/07/21
                            uiModuleType = CONST_moduleType_dec_vp8;
                            break;
                        case CONST_videoCompressors_custom:
                            //  pModule	=	getCusModule(  pProcInfo->pQyMc,  CONST_moduleType_compress  );
                            uiModuleType = CONST_moduleType_compress;
                            break;
                        default:
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            //_sntprintf(tHintBuf, mycountof(tHintBuf), ("%s ucCompressors error, %d"), tHintBuf, (int)pTransformVideo.tv.compressor.common.ucCompressors);
                            return iErr;
                    }
                    //
                    switch (uiModuleType_suggested) {
                        case CONST_moduleType_mediaSdk_dec:
                        case CONST_moduleType_dec_nv:
                        case CONST_moduleType_dec_amf:  //
                            switch (uiModuleType) {
                                case CONST_moduleType_ipp:
                                case CONST_moduleType_mediaSdk_dec:
                                    uiModuleType = uiModuleType_suggested;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }

                    uiModuleType = adjustModuleType_decV(uiModuleType);

                    pModule = M_getCompressModule(pCusModules, uiModuleType);    //  2012/09/09
                    if (null == pModule || null == pModule.pf_qdcInitDecompressVideo) {

                        //_sntprintf(tHintBuf, mycountof(tHintBuf), ("%s pModule or initDecV is null. moduleType %d"), tHintBuf, uiModuleType);
                        str = String.format("pModule or initDecV is null. moduleType %s", uiModuleType);
                        //
                        videoFunc_open.TMP_buf5 fourcc = new videoFunc_open.TMP_buf5();
                        int cntof_fourcc = 5;
                        //char fourcc[4 + 1];
                        iFourcc2Str((int) pTransformVideo.tv.vh_stream.dwFourcc, fourcc.buf, cntof_fourcc);
                        str = String.format("decV: %s not supported. %s", mytoString(fourcc.buf), str);
                        showNotification(0, null, null, 0, 0, 0, str);
                        if (0 != iErr) {
                            exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                        }
                        return iErr;
                    }
                    pTransformVideo.uiModuleType_toDec = uiModuleType; //  2012/09/09
                    pTransformVideo.bUseDecTool = bUseDecTool_suggested;   //  2015/01/19

                    //  2017/10/11
                    int iFourcc = (int) pTransformVideo.tv.vh_stream.dwFourcc;
                    if (0 == iFourcc) {
                        //fixed (byte[] fourccStr1 = pTransformVideo.tv.compressor.common.fourccStr)
                        {
                            iFourcc = pTransformVideo.tv.compressor.iFourcc;// fourccStr2i(fourccStr1);
                        }
                    }

                    //  2016/04/28
                    int real_usPktResType = 0;  //  pProcInfo->get_pktResType_suggested(  CONST_pktUsage_dec,  uiModuleType  );
                    if (0 > (real_usPktResType = pProcInfo.get_deced_pktResType(uiModuleType, iFourcc, real_usPktResType)))
                    {
                        if (0 != iErr) {
                            exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                        }
                        return iErr;
                    }
                    //
                    //  2017/10/13
                    videoFunc_open.TMP_buf5 fourccStr = new videoFunc_open.TMP_buf5();
                    //byte fourccStr[4 + 1] = "";
                    iFourcc2Str(iFourcc, fourccStr.buf, cntof_buf5);
                    str = String.format("initTaskAvFrom_video: fourcc %s, real_usPktResType %d", mytoString(fourccStr.buf), real_usPktResType);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    //
                    pTransformVideo.usPktResType = real_usPktResType;

                    //  2014/03/25
                    qnmCommProc_mis.VIDEO_COMPRESSOR_CFG compressor = new qnmCommProc_mis.VIDEO_COMPRESSOR_CFG();
                    //
                    compressor = new qnmCommProc_mis.VIDEO_COMPRESSOR_CFG();
                    //memcpy(compressor,pFrom.video.tv_recvd.compressor, sizeof(compressor));
                    compressor.common.head = pActiveMem_from.avStream.obj.tranInfo.video.compressor;
                    //
                    compressor.common.iTaskId = iTaskId;
                    {
                        int index_taskInfo = getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
                        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                        if (pTaskInfo == null) {
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;
                        }
                        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                        if (pTaskData == null) {
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;
                        }
                        if (pTaskData.uiType != CONST_taskDataType_conf) {
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;
                        }
                        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                        compressor.shared_eglContext = pTc.shared_eglContext;

                        //
                    }

                    //

                    compressor.common.pCurPkt = pTransformVideo.pCurPkt;
                    compressor.common.size_pCurPkt = 128;//sizeof(myDRAW_VIDEO_DATA);
                    compressor.common.pInDataCacheQ = pTransformVideo.pInDataCacheQ;
                    compressor.common.pVideoQ2 = pTransformVideo.q2;
                    compressor.common.pTmpDataQ = pTransformVideo.pTmpDataQ;
                    //
                    compressor.common.pParent_transform = pTransform;
                    compressor.common.uiTransformType_pParent = CONST_uiTransformType_dec;
                    //
                    compressor.common.pf_bTransformVideoQuit = qyMcMainMediaProc_decVideo_rtsp.qyFuncs.bTransformVideoQuit;
                    compressor.common.pf_transformGetSpsPps = qyMcMainMediaProc_decVideo_rtsp.qyFuncs.transformGetSpsPps;
                    compressor.common.pf_transformGetBih = qyMcMainMediaProc_decVideo_rtsp.qyFuncs.transformGetBih;
                    compressor.common.pf_getNextVPkt = getNextVPkt.qyFuncs.getNextVPkt;
                    //compressor.common.pf_parseVPkt_i  =  dec_parseEncVPkt_i;
                    compressor.common.pf_parseDecVPkt_o = getNextVPkt.qyFuncs.parseDecVPkt_o;

                    compressor.common.pf_detachPkt = getNextVPkt.qyFuncs.detachVPkt;
                    // fixme to change , 看这个赋值的字段没地方引用，就直接注释了
//                    compressor.common.pf_clean_myDRAW_VIDEO_DATA = (qyCommProc_open.PF_clean_myDRAW_VIDEO_DATA) clean_myDRAW_VIDEO_DATA;

                    //  2014/09/07
                    compressor.common.iDecParam_index_pMems_from = -1;// index_pMems_from;
                    //  2014/07/09
                    compressor.common.dec.pf_smplBitstreamReader_ReadNextFrame_func = smplBitstreamReader_ReadNextFrame_func_v;
                    //  2014/03/29
                    //compressor.common.ucHardwareAccl = pQmcCfg->policy.ucHardwareAccl;
                    //  2015/10/04
                    compressor.common.usPktResType_suggested = real_usPktResType;   //  2016/04/28	//  pProcInfo->get_pktResType_suggested(  CONST_pktUsage_dec  );
                    //  2014/07/17
                    //pTransformVideo.u.qoi.myQoi.common.var.pDebugStep =pTransform->video.debugStep;


                    //  2015/01/19
                    if (!pTransformVideo.bUseDecTool) {

                        if (0 != pModule.pf_qdcInitDecompressVideo.call(compressor, pTransformVideo.tv.vh_compress, pTransformVideo.tv.vh_decompress, pTransformVideo.u_qoi.myQoi.common)) {
                            //_sntprintf(tHintBuf, mycountof(tHintBuf), ("%s qdcInitDecVideo failed."), tHintBuf);
                            Log.e( "initTaskAvFrom_video: initQdcDecVideo failed.");
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;
                        }

                    } else {  //  2015/01/28
                        qdcCommProc_open.QOI_common pQdcObjInfo = pTransformVideo.u_qoi.myQoi.common;
                        //memcpy(pQdcObjInfo->cfg.v,compressor.common, sizeof(pQdcObjInfo->cfg.v)  );
                        pQdcObjInfo.cfg.v = compressor.common;
                        pQdcObjInfo.var.ucbTransformFuncsOk = true;
                    }
                }

                //  2014/08/04
                if (pTask.saveVideo.ucbSaveVideo_req) {
                }

//                guiShare.GuiShare.pf_dumpData1.call(pTransformVideo.tv.vh_stream.mem0.spsPps.array(), pTransformVideo.tv.vh_stream.mem0.usLen, 0, 0, true, false, CONST_fn_dump_h264_enc);
//                guiShare.GuiShare.pf_dumpData1.call(pTransformVideo.tv.vh_stream.mem1.spsPps.array(), pTransformVideo.tv.vh_stream.mem1.usLen, 0, 0, true, false, CONST_fn_dump_h264_enc);
//                guiShare.GuiShare.pf_dumpData1.call(pTransformVideo.tv.vh_stream.mem2.spsPps.array(), pTransformVideo.tv.vh_stream.mem2.usLen, 0, 0, true, false, CONST_fn_dump_h264_enc);

                //
                if (pTransformVideo.tv.compressor.ucCompressors != CONST_videoCompressors_stream) {
                    switch (pTransformVideo.tv.vh_decompress.bih.biCompression) {
                        case BI_RGB:
                            if (pTransformVideo.tv.vh_decompress.bih.biPlanes != 1) {
                                //_sntprintf(tHintBuf, mycountof(tHintBuf), (" vh_dec.biPlanes %d"), (int)pTransformVideo.tv.vh_decompress.bih.biPlanes);
                                //goto  errLabel;
                            }
                            if (pTransformVideo.tv.vh_decompress.bih.biBitCount != 24) {
                                //_sntprintf(tHintBuf, mycountof(tHintBuf), ("%s bitCount %d"), tHintBuf, (int)pTransformVideo.tv.vh_decompress.bih.biBitCount);
                            }
                            break;
                        case CONST_fourcc_YV12:
                        case CONST_fourcc_I420:
                            break;

                        default:
                            //_sntprintf(tHintBuf, mycountof(tHintBuf), (" vh_dec not RGB"), pTransformVideo.tv.vh_decompress.bih.biCompression);
                            Log.i( "not rgb.");
                            if (0 != iErr) {
                                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
                            }
                            return iErr;

                    }
                }

                pTransformVideo.index_activeMems_from = index_activeMems_from;

                //
                pTransform.v_preTransThread.qBuf2.emptyQ2();

                //
                pTransformVideo.ucbInited = true;
            }

            iErr = 0;


            //  2012/03/19
            M_setMyStep(pTransformVideo.debugStep, 41);


            //
            if (0 != iErr) {
                exitTaskAvFrom_video(pProcInfo, pTransform, "initTaskAvFrom_video err");
            }


            //  2012/03/19
            M_setMyStep(pTransformVideo.debugStep, 42);

            //  if  (  iErr  ||  tHintBuf[0]  )
            {
                //int index_activeMems_from = 0;  //  pTransform->index_activeMems_from;
                str = String.format("initTaskAvFrom_video leaves. %s: %s %s", index_activeMems_from, pActiveMem_from.avStream.idInfo.ui64Id, (0 != iErr ? ("failed") : ("")));
                showInfo_open.qyFuncs.showInfo_open(0, null,str);

            }

            //Log.e("initTaskAvFrom_video: %I64u leaves with %s", pFrom.idInfo.ui64Id, iErr ? "error" : "ok");
            String str_ii = "initTaskAvFrom_video end";
            showInfo_open.qyFuncs.showInfo_open(str_ii);
            com.qycx.base.util.Log.e(str_ii);
            return iErr;
        }


        public static void exitTaskAvFrom_video(ctxQmc.CCtxQmc pProcInfo, qmcTransform.QY_TRANSFORM pTransform, String hint) {

            int i;
            String str = "exitTaskAvFrom_video start";
            showInfo_open.qyFuncs.showInfo_open(str);
            com.qycx.base.util.Log.e(str);

            //
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) {
                //MACRO_qyAssert(0, ("exitTaskAvFrom_video: pCusModules is null"));
                return;
            }

            //
            if (hint == null) hint = "";

            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;

            M_setMyStep(pTransformVideo.debugStep, 102);


            //  if  (  pFrom.video.ucbInited  )
            {
                if (0 == pTransformVideo.tv.compressor.ucCompressors) ;
                else if (pTransformVideo.tv.compressor.ucCompressors == CONST_videoCompressors_vcm) {
                } else if (pTransformVideo.tv.compressor.ucCompressors == CONST_videoCompressors_dmo) {

                    if (pQmcCfg.ucb__SUPPORT_dmoToDec__) {
                        //exitDecompressVideo_dmo(pTransformVideo.u.dmo);
                    } else {
                        //exitDecompress_filterGraph(  &pTransformVideo.u.dmo,  &pTransformVideo.debugStep  );
                    }
                } else {
                    qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransformVideo.uiModuleType_toDec); //  2012/09/09

                    //  2015/01/19
                    if (!pTransformVideo.bUseDecTool) {
                        if (pModule != null) {
                            if (null != pModule.pf_qdcExitDecompressVideo) {
                                pModule.pf_qdcExitDecompressVideo.call(pTransformVideo.u_qoi.myQoi.common);
                            }

                        }


                    }
                }
                //
                //  2014/08/04
                byte[] pPkt = null;//pTransformVideo.saveVideo.pSaveVideoPkt;
                MACRO_mysafeFree(pPkt);
                pTransformVideo.saveVideo.pSaveVideoPkt = pPkt;

                //  2012/09/09
                pTransformVideo.uiModuleType_toDec = 0;
                pTransformVideo.bUseDecTool = false;   //  2015/01/19

                //  2010/05/04
                //memset(pTransformVideo.u, 0, sizeof(pTransformVideo.u)  );
                pTransformVideo.u_qoi.myQoi.common.memset0();

                //  if  (  pTransformVideo.pDecompressBuf  )  M_free(  pTransformVideo.pDecompressBuf  );

                //  2012/03/19
                qmcTransform.TRANSFORM_VIDEO_procInfo_status pstatus = pTransformVideo.status;
                {
                    //mymemset(pstatus, 0, sizeof(TRANSFORM_VIDEO_procInfo_status));
                }

                //
                pTransformVideo.ucbInited = false;
            }
            M_setMyStep(pTransformVideo.debugStep, 108);

            int index_activeMems_from = pTransform.index_activeMems_from;
            str = String.format("exitTaskAvFrom_video end {%s}. {%s}", index_activeMems_from, hint);
            showInfo_open.qyFuncs.showInfo_open(str);
            com.qycx.base.util.Log.e(str);
            return;
        }


    }
}
