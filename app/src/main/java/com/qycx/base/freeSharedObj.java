


package com.qycx.base;

import static com.qycx.base.compressAudio.qyFuncs.exitCompressAudio;
import static com.qycx.base.qmcFunc.qyFuncs.CONST_sharedObjTypeTable;
import static com.qycx.base.qmcFunc.qyFuncs.stopQThread;
import static com.qycx.base.qmcMasterSlave.qyFuncs.bExists_master;
import static com.qycx.base.qmcMasterSlave.qyFuncs.removeSlaveFromMaster;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoCapture.qyFuncs.closeLocalVideo;
import static com.qycx.base.qmcVideoCapture.qyFuncs.exitOrg2IntBuf;
import static com.qycx.base.qmcVideoCapture.qyFuncs.stopCapture;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qySyncCommProc.qyFuncs.qyExitProcess;
import static com.qycx.base.qyq2.qyFuncs.exitQyQ2;

public class freeSharedObj {
    public static class qyFuncs {


        public static int freeSharedObjByIndex(Object hWnd, int iWndContentType, int index) {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            //TCHAR tHint[128] = "";      //  2014/05/27
            String tHint = "";

            //
            boolean bDbg = true;
            String str;

            if (index < 0 || index >= pQmcCfg.usMaxCnt_pSharedObjs) return -1;
            //
            pSharedObj = getSharedObjByIndex(pProcInfo, index); //  &pProcInfo.pSharedObjs[index];

            //
            //  2014/05/28
            str = String.format("freeSharedObjByIndex %s %s enters", index, qyGetDesByType1(CONST_sharedObjTypeTable, pSharedObj.var.uiType));
            showInfo_open.qyFuncs.showInfo_open(str);
            //  2012/11/06


            //  2014/09/29
            if (pSharedObj.var.bSlave) {
                removeSlaveFromMaster(pProcInfo, index, pSharedObj.var.slave.masterKey.iIndex_sharedObj);
                if (bExists_master(pProcInfo, index)) return iErr;
            }

            //
            showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 61 before stopLocalAudioRecorder");

            //  2013/08/23
            if (pSharedObj.var.bSoundRecorder) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("freeSharedObj: try to stopLocalAudioRecorder"));
                pProcInfo.stopLocalAudioRecorder(index, 100);
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("stopLocalAudioRecorder ends"));

                //
                qmcSharedObj.CAP_procInfo_audioCommon pU = getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
                if (pU != null) {

                    //

                    if (pU.compressAudio.bCompressInited) {
                        qyExitProcess(("freeSharedObjByIndex failed: can't wait for audio recorder."));
                    }


                    //  2013/03/21
                    //memset(pU.recordSound, 0, sizeof(pU.recordSound)  );
                    pU.memset0();

                }
            }

            //
            showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 88 after stopLocalAudioRecorder");


            //
            if (!pSharedObj.var.bCapDev) {

                int i;


                //
                for (i = 0; i < pQmcCfg.usMaxCnt_capProcInfoBmps; i++) {
                    qmcSharedObj.CAP_procInfo_bmpCommon pU = getCapBmpBySth(pProcInfo, i, 0);
                    if (null != pU && pU.uiTranNo_sharedObj == pSharedObj.var.uiTranNo) {

                        //  2011/12/04
                        stopQThread(pU.vppThread, tHint);
                        //
                        stopQThread(pU.thread, tHint);
                        stopQThread(pU.postThread, tHint);
                        //
                        if (pU.vppThread.bQInited) {
                            exitQyQ2(pU.vppThread.q2);
                            pU.vppThread.bQInited = false;
                        }
                        //  2009/12/07
                        if (pU.thread.bQInited) {
                            exitQyQ2(pU.thread.q2);
                            pU.thread.bQInited = false;
                        }
                        //  2011/12/06
                        if (pU.postThread.bQInited) {
                            exitQyQ2(pU.postThread.q2);
                            pU.postThread.bQInited = false;
                        }
                        //
                        pU.postThread.qBuf2.exitQyQ2();

                        //
                        //  2009/04/28
                        //
                        //  2015/05/15. 这个清理应该是通用的吧
                        exitOrg2IntBuf(pProcInfo, pU);  //  2014/09/23
                        pProcInfo.exitCompressVideo(pSharedObj.var.bCapDev, pU.compressVideo);

                        //  2013/03/21
                        //memset(pU, 0, sizeof(pU[0]));
                        pU.memset0();
                    }
                }
                for (i = 0; i < pQmcCfg.usMaxCnt_capProcInfoAudios; i++) {
                    qmcSharedObj.CAP_procInfo_audioCommon pU = getCapAudioBySth(pProcInfo, i, 0);
                    if (null != pU && pU.uiTranNo_sharedObj == pSharedObj.var.uiTranNo) {

                        //  2017/01/29
                        stopQThread(pU.appThread, tHint);
                        //  2009/12/07
                        stopQThread(pU.thread, tHint);
                        //
                        //  2017/01/29
                        if (pU.appThread.bQInited) {
                            exitQyQ2(pU.appThread.q2);
                            pU.appThread.bQInited = false;
                        }
                        //
                        if (pU.thread.bQInited) {
                            exitQyQ2(pU.thread.q2);
                            pU.thread.bQInited = false;
                        }
                        //
                        exitCompressAudio(pProcInfo, pU.compressAudio);

                        //  2013/03/21
                        //memset(pU, 0, sizeof(pU[0]));
                        pU.memset0();
                    }
                }


            } else {

                //
                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 169 to closeLocalVideo");

                //
                qmcSharedObj.CAP_procInfo_video pU = (qmcSharedObj.CAP_procInfo_video) getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                int uiCapType = pU.uiType;

                stopCapture(hWnd, iWndContentType, pU); //  2011/07/29

                //
                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 178 after stopCapture");


                //  2011/12/04
                stopQThread(pU.vppThread, tHint);

                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 184 after stop vpp");


                //
                stopQThread(pU.thread, tHint);

                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 190 after stop pU.thread");

                stopQThread(pU.postThread, tHint);

                //
                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 195 after stop post. before closeLocalVideo");

                //
                closeLocalVideo(hWnd, iWndContentType, index);

                //
                showInfo_open.qyFuncs.showInfo_open("freeSharedObjByIndex: 201 after closeLocalVideo");

                //
                if (pU.vppThread.bQInited) {
                    exitQyQ2(pU.vppThread.q2);
                    pU.vppThread.bQInited = false;
                }
                //
                if (pU.thread.bQInited) {
                    exitQyQ2(pU.thread.q2);
                    pU.thread.bQInited = false;
                }
                //  2011/12/06
                if (pU.postThread.bQInited) {
                    exitQyQ2(pU.postThread.q2);
                    pU.postThread.bQInited = false;
                }

                //
                if  (  pU.postThread.qBuf2!=null) {
                    pU.postThread.qBuf2.exitQyQ2();
                }


                //  2013/03/21
                //memset(pU, 0, sizeof(pU[0]));
                pU.memset0();
            }
            //
            if (!pSharedObj.var.bSlave) {
				
            }



            //
            //memset(pSharedObj, 0, sizeof(pSharedObj[0]));
            pSharedObj.memset0();

            iErr = 0;

            

            str = String.format("freeSharedObjByIndex %s leaves 225 with %s", index, 0 != iErr ? ("Failed") : ("OK"));
            showInfo_open.qyFuncs.showInfo_open(str);


            return iErr;
        }

    }
}
