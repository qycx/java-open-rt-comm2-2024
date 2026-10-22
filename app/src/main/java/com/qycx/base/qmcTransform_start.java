


package com.qycx.base;

import static com.qycx.base.qmcDecAudio.qyFuncs.mcThreadProc_decAudio;
import static com.qycx.base.qmcTransform.qyFuncs.startTransformThread_v;
import static com.qycx.base.qyMcMainMediaProc_saveAv.qyFuncs.mcThreadProc_saveAv;
import static com.qycx.base.qyq2.qyFuncs.emptyQ2;

import android.util.Log;

public class qmcTransform_start {
    private static final String TAG=qmcTransform_start.class.getSimpleName();

    public static class qyFuncs {
        public static boolean isTooManyThreads() {
            return false;
        }

        //


        //
        public static int startTransformThread(ctxQmc.CCtxQmc pProcInfo, int uiTaskType, taskAv.PROC_TASK_AV pTask, qmcTransform.QY_TRANSFORM p) {
            int iErr = -1;
            int dwThreadDaemonId;
            //MC_VAR_isCli	*	pProcInfo	=	QY_GET_procInfo_isCli(  );

            ctxQmcTmpl.QMC_status pStatus = pProcInfo.status;//->get_qmc_status();
            //if (!pStatus) return -1;

            //TCHAR tHintBuf[128] = "";

            //  QY_TRANSFORM	*	p	=	&pTask.pTransforms[i];

            String str;
            str = String.format("startTransformThread called, {%s}", p.index_activeMems_from);
            showInfo_open.qyFuncs.showInfo_open(str);


            //  2014/08/04
            if (pTask.saveVideo.ucbSaveVideo_req) {
                if (null == p.saveThread.hThread) {
                    //
                    p.saveThread.bQuit = false;

                    p.saveThread.hThread=guiShare.GuiShare.pf_startThread1.call( mcThreadProc_saveAv, p);

                    //  2012/03/25
                    //InterlockedIncrement(pStatus->nThreads_dec);
//                    Interlocked.Increment(pStatus.nThreads_dec);
                    synchronized (TAG){
                        pStatus.nThreads_dec--;
                    }
                }
            }

            //if  (  !bVideoOnly  )
            {
                if (null == p.audio.hThread) {
                    //
                    p.audio.bQuit = false;

                    p.audio.hThread=guiShare.GuiShare.pf_startThread1.call(mcThreadProc_decAudio, p);
//                    emptyQ2(p.audio.q2);

                    //  2012/03/25
                    //InterlockedIncrement(pStatus->nThreads_dec);
//                    Interlocked.Increment(pStatus.nThreads_dec);
                    synchronized (TAG){
                        pStatus.nThreads_dec--;
                    }

                }
                if (p.bNeed_decV) {

                    //
                    startTransformThread_v(pProcInfo, p, "called in startTrans");
                }

            }


            iErr = 0;

            
            return iErr;

        }

        public static int startTransformThreads(ctxQmc.CCtxQmc pProcInfo, int uiTaskType, taskAv.PROC_TASK_AV pTask, String hint) {
            int iErr = -1;
            int i;
            int dwThreadDaemonId;

            //TCHAR tHintBuf[128] = "";

            //BOOL				bVideoOnly		=	false;
            if (hint == null) hint = "";

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                //Log.e(TAG,"For test, startTransformThread returns 0");
                return 0;
            }

            //
            String str;
            str = String.format("startTransformThreads called, %s", hint);
            showInfo_open.qyFuncs.showInfo_open(0,null,str);

            //
            if (isTooManyThreads()) {
                //_sntprintf(tHintBuf, mycountof(tHintBuf), ("too many dec threads"));
                return iErr;
            }

            //
            for (i = 0; i < pTask.trans.usCntLimit_transforms; i++) {
                qmcTransform.QY_TRANSFORM p = pTask.trans.pTransforms[i];

                //
                if (0 != startTransformThread(pProcInfo, uiTaskType, pTask, p)) return iErr;

            }

                    iErr = 0;
            

            Log.d(TAG,"startTransformTrheads leaves with ");

            if (0 != iErr) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("startTransformThreads failed, %s"), tHintBuf);
            }

            return iErr;
        }


    }
}
