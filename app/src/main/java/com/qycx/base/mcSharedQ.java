package com.qycx.base;

import static com.qycx.base.Consts.CONST_qyRet_qFull;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class mcSharedQ {
    public static class Consts {
        public final static int cntof_qMem_mcTask_tDesc = 16;
    }

    //
    public static class QELEM_mcTask {
        public Object m_pBuf;
        //public Object m_obj;
    }

    public static class QMEM_mcTask {
        //public SYNC_mt_cnt syncMtCnt;

        //
        public boolean bUsed;                         //  赋值true表明new

        public QELEM_mcTask qElemMemory;
        public int size;


        //
        public boolean ucbQElemInited;                   //  表明是否初始化

        //
        public boolean bNeedRemoved;                  //  是否准备放弃

        //
        public String tDesc;//[cntof_qMem_mcTask_tDesc + 1];

    }

    ;

    public interface PF_qElemNewEx_mcTask {
        Object call(int iAllocType, int size, QELEM_mcTask p);
    }

    public interface PF_qElemFreeEx_mcTask {
        void call(QELEM_mcTask p);
    }

    public interface PF_qElemInit_mcTask {
        int call(Object p);
    }

    public interface PF_qElemExit_mcTask {
        void call(Object p);
    }

    public interface PF_qElemRemoveEx_mcTask {
        void call(Object p, int size, String hint);
    }

    public interface PF_enumTask {
        int call(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, taskCommon.TASK_common taskElem);
    }

    public interface PF_bEnumTask {
        boolean call(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, taskCommon.TASK_common taskElem);
    }


    public static class MC_sharedQ_var {

        public genericQueue.GENERIC_Q_CFG cfg;

        //
        //
        public static PF_qElemNewEx_mcTask pfQElemNewEx;                //  2010/04/24
        public static PF_qElemFreeEx_mcTask pfQElemFreeEx;            //  2010/04/24
        //
        public static PF_qElemInit_mcTask pfQElemInit;                //  2007/06/10
        public static PF_qElemExit_mcTask pfQElemExit;                //

        //														   //
        public static PF_qElemRemoveEx_mcTask pfqElemRemoveEx;           //  2012/11/10

        //
        public qySyncCommProc.RW_lock_param rwLockParam_sync;

        //
        public qySyncCommProc.CMutexRW pRw_sync;

        //
        public QMEM_mcTask[] pMems;
        public Semaphore[] hMutexs;
        //
        public int uiQNodes;

        //
        public int dwThreadId_mgr;


    }

    ;


    public static class CMcSharedQ {

        public MC_sharedQ_var m_var = new MC_sharedQ_var();

        public boolean bSetQInfo(String mutexName_sync, int uiMaxCnt_sema, int uiMilliSeconds_mutex_r, int uiMilliSeconds_sema_r, int uiMilliSeconds_mutex_w, int uiMilliSeconds_sema_w) {
            return true;
        }

        public int initQ(genericQueue.GENERIC_Q_CFG pCfg, qySyncCommProc.RW_lock_param pRwLockParam_sync,
                         PF_qElemNewEx_mcTask pfQElemNewEx,
                         PF_qElemInit_mcTask pfQElemInit,
                         PF_qElemExit_mcTask pfQElemExit,
                         PF_qElemFreeEx_mcTask pfQElemFreeEx) {
            int iErr = -1;

            //memcpy(m_var.cfg, pCfg, sizeof(m_var.cfg));
            m_var.cfg = pCfg;
            //
            m_var.pfQElemNewEx = pfQElemNewEx;
            m_var.pfQElemFreeEx = pfQElemFreeEx;
            //
            m_var.pfQElemInit = pfQElemInit;
            m_var.pfQElemExit = pfQElemExit;

            m_var.rwLockParam_sync = pRwLockParam_sync;
            m_var.pRw_sync = new qySyncCommProc.CMutexRW();
            m_var.pMems = new QMEM_mcTask[m_var.cfg.uiMaxQNodes];
            m_var.hMutexs = new Semaphore[m_var.cfg.uiMaxQNodes];

            for (int i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                m_var.pMems[i] = new QMEM_mcTask();
                m_var.hMutexs[i] = new Semaphore(1);
            }
            iErr = 0;
            return iErr;
        }


        String getMutexName(int i) {
            return "mcSharedObj"+ i;
        }


        void qNodeFree(QMEM_mcTask pMem, boolean bElemRemove) {
            MC_sharedQ_var pQ = m_var;

            //if (pQ !=m_var) return;
            QMEM_mcTask pQNode = pMem;

            if (pQNode.qElemMemory.m_pBuf != null) {
                if (pQNode.ucbQElemInited) {
                    if (pQ.pfQElemExit != null) pQ.pfQElemExit.call(pQNode.qElemMemory.m_pBuf);
                    pQNode.ucbQElemInited = false;
                }
                if (bElemRemove) {   //  2009/12/08
                    String str = pQ.cfg.name;
                    if (pQ.pfqElemRemoveEx != null)
                        pQ.pfqElemRemoveEx.call(pQNode.qElemMemory.m_pBuf, (int) pQNode.size, str); //  2012/11/10
                }
                if (pQ.pfQElemFreeEx != null) pQ.pfQElemFreeEx.call(pQNode.qElemMemory);
                //
                pQNode.qElemMemory.m_pBuf = 0;
            }
            //
            pQNode.ucbQElemInited = false;
            pQNode.bNeedRemoved = false;
            pQNode.bUsed = false;
        }


        //
        int recalc_uiQNodes() {
            int iErr = -1;
            int i;
            int iQNodes = 0;

            for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                QMEM_mcTask pMem = m_var.pMems[i];
                if(pMem==null) continue;
                //
                if (!pMem.bUsed) continue;
                //
                iQNodes++;
            }

            if (m_var.uiQNodes != iQNodes) {
                m_var.uiQNodes = (int) iQNodes;
            }

            iErr = 0;

            return iErr;
        }


        //
        public void exitQ() {
            int i;

            //

            for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                QMEM_mcTask pMem = m_var.pMems[i];
                if(pMem==null) return;
                if (!pMem.bUsed) continue;
                {
                    try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
                    {
                        String mutexName;

                        //
                        pMem.bNeedRemoved = true;

                        //
                        mutexName = getMutexName(i);
                        //
                        //
                        this.qNodeFree(pMem, true);
                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }
                }
            }
            //
            this.recalc_uiQNodes();

            //
            if (isEmpty()) {
                //MACRO_safeFree(m_var.pMems);
                if (m_var.pMems != null) {
                    //qyMemoryFunc.qyFuncs.myfree(m_var.pMems);
                    m_var.pMems = null;
                }
                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                    if (m_var.hMutexs[i] != null) {
                        m_var.hMutexs[i].release();
                        m_var.hMutexs[i] = null;
                    }
                }
                m_var.hMutexs = null;
            }

            return;
        }


        public int qPostMsg(Object pQElem, int size_unused) {
            int iErr = -1;
            int i;
            MC_sharedQ_var pQ = m_var;
            int iAllocType = 0;
            int size = size_unused;


            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()) {
                String hint = "mcSharedQ.qPostMsg";
                //
                if (0 != rwLock.wLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_sema_w, hint))
                    return -1;

                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                    if (m_var.pMems[i].bUsed) continue;
                    break;
                }
                if (i == m_var.cfg.uiMaxQNodes) {
                    Log.e("mcSharedQ qPostMsg failed, full");
                    rwLock.unlock();
                    return CONST_qyRet_qFull;
                }

                //
                QMEM_mcTask pQNode = m_var.pMems[i];

                //
                {
                    String mutexName;
                    mutexName = getMutexName(i);
                    if (mutexName == null) {
                        rwLock.unlock();
                        return -1;
                    }

                    pQNode.bUsed = true;

                    //
                    String str = String.format("sharedQ.mems%d", i);
                    pQNode.tDesc= str;

                    pQNode.qElemMemory = new QELEM_mcTask();
                    pQNode.qElemMemory.m_pBuf = pQElem;
                    pQNode.size = size;

                    if (MC_sharedQ_var.pfQElemInit != null && MC_sharedQ_var.pfQElemInit.call(pQNode.qElemMemory.m_pBuf) != 0)
                    {
                        rwLock.unlock();
                        return iErr;
                    }
                    pQNode.ucbQElemInited = true;
                }


                //pQ->uiQNodes  ++  ;
                this.recalc_uiQNodes();


                iErr = 0;



                //
                if (iErr != 0) {
                    qNodeFree(pQNode, false);
                }

            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            //
            return iErr;
        }

        int qGetMsg(long pQElem, int puiSize) {

            return -1;
        }

        public int qRemoveMsg(ctxCaller.CTX_caller ctxCaller, PF_bEnumTask pf_bRemoveCond, Object p0, Object p1) {
            int iErr = -1;
            int i;
            boolean tmpbRet;
            boolean bExists_needRemoved = false;

            //

            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()) {

                if (0 != rwLock.rLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_sema_r,"qRemoveMsg.350"))
                    return -1;

                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                    QMEM_mcTask pMem = m_var.pMems[i];
                    if (!pMem.bUsed) continue;
                    //
                    if (pMem.bNeedRemoved) {
                        bExists_needRemoved = true;
                        continue;

                    }
                    //
                    tmpbRet = pf_bRemoveCond.call(ctxCaller, (qnmCommProc_mis.MIS_MSGU) p0, (qyMcMainProc.PARAM_findOrgMsg) p1, (taskCommon.TASK_common) pMem.qElemMemory.m_pBuf);
                    if (!tmpbRet) continue;
                    pMem.bNeedRemoved = true;
                    bExists_needRemoved = true;
                    continue;
                }


            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            if (bExists_needRemoved) {
                //
                this.chkMsgs_toBeRemoved();
            }


            iErr = 0;


            return iErr;
        }


        public void emptyQ() {
            int iErr = -1;
            int i;

            int tmpbRet;


            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()) {
                //
                String w_hint = "mcSharedQ.emptyQ";

                //
                if (0 != rwLock.wLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_mutex_w, w_hint))
                    return;

                for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                QMEM_mcTask pMem = m_var.pMems[i];
                if(pMem==null) continue;
                if (!pMem.bUsed) continue;
                //

                pMem.bNeedRemoved = true;


                //
                qNodeFree(pMem, true);


            }
                //
                this.recalc_uiQNodes();

            } catch (Exception e) {
                Log.e(e.getMessage());
                return;
            }
            iErr = 0;


            return;
        }


        public int qTraverse(ctxCaller.CTX_caller ctxCaller, PF_enumTask pf_visit, Object p0, Object p1) {
            int iErr = -1;
            int i;
            int tmpiRet;

            //
            int nNodes = (int) m_var.uiQNodes;


            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()){
                if (0 != rwLock.rLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_sema_r,"sharedQ.qTraverse.443")) {
                    String str = "mcSharedQ.qTraverse: rLock failed:" + m_var.pRw_sync.w_hint;
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null, str);
                    return -1;
                }
                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes && nNodes != 0; i++) {
                    QMEM_mcTask pMem = m_var.pMems[i];
                    if (!pMem.bUsed) continue;

                    //
                    if (!pMem.ucbQElemInited || pMem.bNeedRemoved) continue;

                    //
                    nNodes--;

                    /**********debug start**********/
//                    qnmCommProc_mis.MIS_MSGU p3 = (qnmCommProc_mis.MIS_MSGU) p0;
//                    if(p3.debugAudio == 4){
//                        Log.d("this is audio");
//                    }
                    /**********debug end **********/

                   // Log.d("pf_visit.call:  stage2 2 e53535");
                    tmpiRet = pf_visit.call(ctxCaller, (qnmCommProc_mis.MIS_MSGU) p0, (qyMcMainProc.PARAM_findOrgMsg) p1, (taskCommon.TASK_common) pMem.qElemMemory.m_pBuf);
                    if (tmpiRet != 0) {
                        iErr = tmpiRet;
//                        rwLock.unlock();
                        return iErr;
                    }
                }
//                rwLock.unlock();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            if (nNodes != 0) {
            }

            //
            iErr = 0;


            return iErr;
        }


        public int qTraverse_mono(ctxCaller.CTX_caller ctxCaller, PF_enumTask pf_visit, Object p0, Object p1) {
            //return qTraverse(pf_visit, p0, p1);

            //
            int iErr = -1;
            int i;
            int tmpiRet;

            //
            long dwTickCnt0 = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            int nNodes = (int) m_var.uiQNodes;


            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()){
                String w_hint = "mcSharedQ.qTraverse_mono";

                //
                if (0 != rwLock.wLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_sema_w, w_hint)) {
                    String str = "mcSharedQ.qTraverse_mono: wLock failed";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return -1;
                }
                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes && nNodes != 0; i++) {
                    QMEM_mcTask pMem = m_var.pMems[i];
                    if (!pMem.bUsed) continue;

                    //
                    if (!pMem.ucbQElemInited || pMem.bNeedRemoved) continue;

                    //
                    nNodes--;

                    //
                    tmpiRet = pf_visit.call(ctxCaller, (qnmCommProc_mis.MIS_MSGU) p0,p1 == null ? null : (qyMcMainProc.PARAM_findOrgMsg) p1, (taskCommon.TASK_common) pMem.qElemMemory.m_pBuf);
                    if (tmpiRet != 0) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                }

            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            if (nNodes != 0) {
            }

            //
            iErr = 0;
            


            /*int dwTickCnt1 = tmpFunc.qyFuncs.myGetTickCount(null);
            int iDiffInMs = (int) (dwTickCnt1 - dwTickCnt0);
            if (iDiffInMs > 500) {
                int ii = 0;
            }
*/

            return iErr;


        }


        boolean isFull() {
            if (m_var.uiQNodes >= m_var.cfg.uiMaxQNodes) return true;
            return false;
        }

        public boolean isWarning(int uiQNodes_warningInterval)      //  判断是否队列的成员空闲空间〈uiQNodes_interval
        {
            if (m_var.uiQNodes + uiQNodes_warningInterval >= m_var.cfg.uiMaxQNodes) return true;

            return false;
        }


        public boolean isEmpty() {
            if (0 == m_var.uiQNodes) return true;

            return false;
        }


        int chkMsgs_toBeRemoved() {
            int iErr = -1;
            int i;

            for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                QMEM_mcTask pMem = m_var.pMems[i];
                if (!pMem.bUsed) continue;
                if (pMem.bNeedRemoved) break;
            }
            if (i == m_var.cfg.uiMaxQNodes) return 0;



            try(qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()){
                //
                String w_hint = "mcSharedQ.chkMsgs_toBeRemoved";
                //
                if (0 != rwLock.wLock(m_var.pRw_sync, (int) m_var.rwLockParam_sync.uiMilliSeconds_sema_w, w_hint)) {
                    showInfo_open.qyFuncs.showInfo_open("chkMsgs_toBeRemoved, wLock failed");
                    return -1;
                }

                //
                for (i = 0; i < m_var.cfg.uiMaxQNodes; i++) {
                    QMEM_mcTask pMem = m_var.pMems[i];
                    if (!pMem.bUsed) continue;
                    //
                    if (pMem.ucbQElemInited
                            && !pMem.bNeedRemoved
                    ) {
                        continue;
                    }
                    //
                    pMem.bNeedRemoved = true;

                    //
                    qNodeFree(pMem, true);

                }
                //
                this.recalc_uiQNodes();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            iErr = 0;

            return iErr;

        }


    }


}
