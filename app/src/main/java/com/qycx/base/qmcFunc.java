
package com.qycx.base;

import static com.qycx.base.qmcSharedObj.Consts.*;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.tmpFunc.qyFuncs.myEventSet;

import com.qycx.base.util.Log;

public class qmcFunc {

    public interface PF_threadProc {
        void call(Object obj);
    }

    public static class qyFuncs {
        //
        public static qyTypes.QY_DMITEM[] CONST_sharedObjTypeTable = new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(CONST_sharedObjType_av, ("av")),
                        new qyTypes.QY_DMITEM(CONST_sharedObjType_mediaFile, ("mediaFile")),
                        new qyTypes.QY_DMITEM(CONST_sharedObjType_mediaDevice, ("mediaDevice")),
                        new qyTypes.QY_DMITEM(CONST_sharedObjType_webcam_vOnly, ("webcam_vOnly")),
                        new qyTypes.QY_DMITEM(-1),
                };

        public static int setPktInputInfo(ctxQmc.CCtxQmc pProcInfoCommon, qyCommProc_open.VIDEO_input_info pInput, int usFrames_perSecond_expected, boolean bNoSkip, Object pmyDRAW_VIDEO_DATA) {
            int iErr = -1;
            String str;

            //
            if (null == pProcInfoCommon) {
                Log.e("setPktInputInfo failed, pProcInfo is null");
                return -1;
            }

            //
            long tStartTran = tmpFunc.qyFuncs.mytime();

            if (pInput.tStartTran != tStartTran) {
                pInput.usFps = pInput.usFramesThisSecond;
                pInput.usFramesThisSecond = 1;
                pInput.tStartTran = tStartTran;
            } else {
                pInput.usFramesThisSecond++;
            }

            //
            int index = pInput.usFramesThisSecond - 1;

            //  2011/08/31
            if (!bNoSkip) {
                int tmpusFps_input = pInput.usFps; //  _input;
                int tmpuiFps_expected = usFrames_perSecond_expected;
                if (0 == tmpuiFps_expected) tmpuiFps_expected = 1;

                if (tmpusFps_input > tmpuiFps_expected) {

                    boolean bSkipped = false;
                    int k = 0;
                    //
                    if (tmpuiFps_expected > 2) k = (tmpusFps_input + 2) / tmpuiFps_expected;
                    else k = tmpusFps_input / tmpuiFps_expected;
                    if (k > 1) {   //  如果要发送的帧数是小部分，则选择数要发送的帧
                        if (0 != (index % k)) {
                            bSkipped = true;
                        } else {   //  2012/03/16
                            int fps_real = 0;
                            //
                            if (k > 2) fps_real = (tmpusFps_input + 2) / k;
                            else fps_real = tmpusFps_input / k;

                            int index_real = index / k;
                            if (fps_real > tmpuiFps_expected) {
                                int nThrows = fps_real - tmpuiFps_expected;
                                k = fps_real / nThrows;
                                if (fps_real > nThrows * k) k++;
                                if (0 == (index_real % k)) {
                                    bSkipped = true;
                                }
                            }
                        }
                    } else {   //  如果要发送的帧数是大多数，则选择数丢弃帧
                        int nThrows = tmpusFps_input - tmpuiFps_expected;
                        k = tmpusFps_input / nThrows;
                        if (tmpusFps_input > nThrows * k) k++;
                        if (0 == (index % k)) {
                            bSkipped = true;
                        }
                    }

                    //
                    if (bSkipped) {


                        return iErr;
                    }
                }
            }

            //
            //
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = (qnmCommProc_mis.myDRAW_VIDEO_DATA) pmyDRAW_VIDEO_DATA;
            if (null != pPkt) {
                pPkt.input.tStartTran = tStartTran;                                                    //  2011/08/30
            }


            iErr = 0;
            


//			boolean bNoShow = false;
//
//			 ctxQmcTmpl.QMC_cfg_debugStatusInfo pCfg_debugStatusInfo =  pProcInfoCommon.cfg.debugStatusInfo;
//
//			//
//			if (0!=iErr)
//			{
//				if ( pCfg_debugStatusInfo.ucbShowPktInputInfo && !bNoShow)
//				{
//					//_sntprintf(tBuf, mycountof(tBuf), ("setPktInputInfo:  pkt[%d] skipped"), index);
//					str=String.format("setPktInputInfo:  pkt[%s] skipped", index);
//					//
//					//_sntprintf(  tBuf,  mycountof(  tBuf  ),  (  "%s, usFramesThisSecond %d"  ),  tBuf,  (  int  )pInput.usFramesThisSecond  );
//					str+=String.format(  " usFramesThisSecond %s",  (  int  )pInput.usFramesThisSecond  );
//
//					qyShowInfo1(CONST_qyShowType_qwmComm, null, "", ("IsClient"), null, "", "", str);
//				}
//			}

            return iErr;
        }

        //
        public static int stopQThread(qnmCommProc_mis.QY_qThreadProcInfo_common pThread, String hint) {
            //TCHAR tBuf[256];

            if (null == hint) hint = "";

            if (pThread.hThread != null) {
                int nTries = 200;
                int i;
                for (i = 0; i < nTries; i++) {
                    if (null == pThread.hThread) break;

                    //
                    pThread.bQuit = true;
                    myEventSet(pThread.q2.hEvent0);
                    if  (  pThread.qBuf2!=null)  {
                        myEventSet(pThread.qBuf2.hEvent0);
                    }
                    //waitForThread( pThread.hThread, 100);
                    guiShare.GuiShare.pf_waitForThread1.call(pThread.hThread, 100);
                    pThread.hThread = null;
                }

                if (null != pThread.hThread) {
                    //qyExitProcess(tBuf);
                    int ii = 0;
                }
            }

            return 0;
        }


        //
        public static int startQThread(PF_threadProc lpStartAddress, Object lpParameter, Object pThreadClass, qnmCommProc_mis.QY_qThreadProcInfo_common pQThread) {
            int iErr = -1;
            int dwThreadDaemonId = 0;

            //
            if (null != lpStartAddress) {

                pQThread.hThread=guiShare.GuiShare.pf_startThread1.call( lpStartAddress, lpParameter);

            } else {
            }

            iErr = 0;

            

            return iErr;
        }


    }
}
