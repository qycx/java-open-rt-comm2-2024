


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.isQEmpty;
import static com.qycx.base.genericQueue.qyFuncs.isQWarning;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

public class qyq2 {

    public static class Consts {
        public final static int cntof_q2Cfg_name = 16;
        public final static int cntof_q2Cfg_evt0Name = 24;
        public final static int CONST_uiInitCnt_sema_q2SyncFlg = 20;                                    //  2009/05/13
    }

    public static class QY_Q2_cfg {
        public String name;//[cntof_q2Cfg_name];
        public int uiMaxQNodes;
        //
        public boolean m_bUse2Buf;
        //
        public qySyncCommProc.RW_lock_param rwLockParam_syncFlg;
        public int uiMaxCnt_semaTrigger;
        public int usCnt_hEvents;
        public String evt0Name;//[cntof_q2Cfg_evt0Name];                     //  2009/12/22
    }

    /*
    public static class CQyQ2Help_unused extends qySyncCommProc.CQyRWLock {

        public qyq2.QY_Q2 m_pQ2;
        //
        public genericQueue.GENERIC_Q m_pQ_toGetMsg;
        public genericQueue.GENERIC_Q m_pQ_toPostMsg;

        public CQyQ2Help_unused() {
            m_pQ2 = null;
        }

        public genericQueue.GENERIC_Q getQ_toPostMsg(qyq2.QY_Q2 pQ2,String hint) {
            if (null == pQ2) return null;

            //
            if  (  !pQ2.cfg.m_bUse2Buf)  {
                return  pQ2.qs0;
            }

            //
            if (m_pQ2 != null) {
                if (m_pQ2 != pQ2) return null;
            } else
                m_pQ2 = pQ2;

            if (m_pQ_toPostMsg != null) return m_pQ_toPostMsg;

            if (this.rLock(pQ2.pRw_syncFlg, (int) pQ2.cfg.rwLockParam_syncFlg.uiMilliSeconds_sema_r,hint) != 0)
                return null;

            m_pQ_toPostMsg = pQ2.bFlag_toPostMsg_q1 ? pQ2.qs1 : pQ2.qs0;
            m_pQ_toGetMsg = pQ2.bFlag_toPostMsg_q1 ? pQ2.qs0 : pQ2.qs1;
            
            return m_pQ_toPostMsg;
        }

        public genericQueue.GENERIC_Q getQ_toGetMsg(qyq2.QY_Q2 pQ2) {
            if (null == pQ2) {
                return null;
            }

            //
            if  (   !pQ2.cfg.m_bUse2Buf )  {
                return  pQ2.qs0;
            }

            if (m_pQ2 != null) {
                if (m_pQ2 != pQ2) {
                    return null;
                }
            } else
                m_pQ2 = pQ2;

            if (m_pQ_toGetMsg != null) return m_pQ_toGetMsg;

            genericQueue.GENERIC_Q pQ_toGetMsg;
            genericQueue.GENERIC_Q pQ_toPostMsg;
            if (pQ2.bFlag_toPostMsg_q1) {
                pQ_toGetMsg = pQ2.qs0;
                pQ_toPostMsg = pQ2.qs1;
            } else {
                pQ_toGetMsg = pQ2.qs1;
                pQ_toPostMsg = pQ2.qs0;
            }

            if (isQEmpty(pQ_toGetMsg)) {
                if (!isQEmpty(pQ_toPostMsg)) {
                    //
                    String hint = "qyq2Help.getQ_toGetMsg";

                    //  try to switch outputQ
                    if (this.wLock(pQ2.pRw_syncFlg, (int) pQ2.cfg.rwLockParam_syncFlg.uiMilliSeconds_sema_w, hint) != 0) {
                        return null;
                    }
                    pQ2.bFlag_toPostMsg_q1 = !pQ2.bFlag_toPostMsg_q1;
                    this.unlock();
                }
            }

            if (pQ2.bFlag_toPostMsg_q1) {
                m_pQ_toGetMsg = pQ2.qs0;
                m_pQ_toPostMsg = pQ2.qs1;
            } else {
                m_pQ_toGetMsg = pQ2.qs1;
                m_pQ_toPostMsg = pQ2.qs0;
            }

            return m_pQ_toGetMsg;

        }




        public int qPostMsgAndTrigger(Object pQElem, qyq2.QY_Q2 pQ2, String hint) {
//            Log.e("qyQ2Help::qPostMsgAndTrigger: enter f73636 "  );

            int tmpiRet = 0;
            boolean ucbTriggerErr = false;

            if (pQ2 == null) return -1;

            if (pQElem != null) {
                if ((tmpiRet = genericQueue.qyFuncs.qPostMsg(pQElem, getQ_toPostMsg(pQ2,hint))) != 0) {
                    Log.e("qyQ2Help::qPostMsgAndTrigger: qPostMsg returns " + tmpiRet  + " pQ2.name:"  + pQ2.cfg.name  +  " "  + hint);
                    return tmpiRet;
                }
            }
            try {
                if (pQ2.hSemaTrigger1 != null) {
                    tmpFunc.qyFuncs.mySemaRelease(pQ2.hSemaTrigger1, 1);
                } else {
                }
                if (pQ2.hEvent0 != null) {
                    tmpFunc.qyFuncs.myEventSet(pQ2.hEvent0);
                }
            } catch (Exception e) {
                Log.d(e.toString());
            }

            //  2008/11/21
            if (ucbTriggerErr != pQ2.ucbTriggerErr) {
                pQ2.ucbTriggerErr = ucbTriggerErr;
            }
            return tmpiRet;
        }

    }

     */


    public static class QY_Q2 {                                   //  2009/05/13

        public QY_Q2_cfg cfg = new QY_Q2_cfg();
        public boolean bFlag_toPostMsg_q1;
        public String qName;
        public qySyncCommProc.CMutexRW pRw_syncFlg;
        public Semaphore hSemaTrigger1;               //  2007/05/08
        public long lPrevCnt_semaTrigger;      //  2007/05/08, 没有严格意义，用来debug用。
        //fixed long hEvents[1 + 1];
        public EventWaitHandle hEvent0;
        public EventWaitHandle hEvent1;

        public boolean ucbTriggerErr;                //  2008/11/21. 在使用触发机制的队列中，当触发器满了，设置此位。有利于及时调用相关函数将触发器恢复

        //GENERIC_Q qs[2];
        public genericQueue.GENERIC_Q qs0;
        public genericQueue.GENERIC_Q qs1;
        public void clearQueue(){
            emptyGenericQ(qs0);
            emptyGenericQ(qs1);
        }

        //
        public long dwTickCnt_lastData;

    }


    public static class qyFuncs {
        //  int  initQyQ2(  genericQueue.GENERIC_Q_CFG  *  pCfg,  qySyncCommProc.RW_lock_param  *  pRwLockParam_syncFlg,  unsigned  short  usCnt_hEvents,  LPCTSTR  evt0Name,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  QY_Q2  *  pQ2  )
        public static int initQyQ2(genericQueue.GENERIC_Q_CFG pCfg,
                                   boolean bUse2Buf,
                                   qySyncCommProc.RW_lock_param pRwLockParam_syncFlg,
                                   int usCnt_hEvents,
                                   String evt0Name,
                                   genericQueue.PF_qElemInit pfQElemInit,
                                   genericQueue.PF_qElemExit pfQElemExit,
                                   genericQueue.PF_qElemRemove pfQElemRemove,
                                   qyq2.QY_Q2 pQ2) {
            //if (pCfg == null ) return -1;

            genericQueue.GENERIC_Q_cfgEx cfgEx = new genericQueue.GENERIC_Q_cfgEx();
            //memset(cfgEx, 0, sizeof(cfgEx));
            //memcpy(cfgEx.common, pCfg, sizeof(cfgEx.common));
            cfgEx.common = pCfg;

            cfgEx.pfQElemInit = pfQElemInit;
            cfgEx.pfQElemExit = pfQElemExit;
            cfgEx.pfQElemRemove = pfQElemRemove;
            return initQyQ2(cfgEx, bUse2Buf,pRwLockParam_syncFlg, usCnt_hEvents, evt0Name, pQ2);
        }

        //  2012/11/10
        public static int initQyQ2(genericQueue.GENERIC_Q_cfgEx pCfgEx, boolean bUse2Buf,  qySyncCommProc.RW_lock_param pRwLockParam_syncFlg,
                                   int usCnt_hEvents, String evt0Name, qyq2.QY_Q2 pQ2) {
            int iErr = -1;
            int j;


            //if (null == pRwLockParam_syncFlg) return -1;
            if (pQ2 == null) return -1;

            genericQueue.GENERIC_Q_CFG pCfg = pCfgEx.common;
            pQ2.cfg.name = pCfg.name;
            pQ2.cfg.uiMaxQNodes = pCfgEx.common.uiMaxQNodes;

            //
            if  (  true  )  {
                if  (  bUse2Buf  ==  false  )
                {
                    //Log.d("qy","initQyQ2: for dbg: ");
                }
                //Log.e("qy","for dbg: bUse2Buf set to true");
                //bUse2Buf=true;
            }

            //
            pQ2.cfg.m_bUse2Buf  =  bUse2Buf;
            if  (  bUse2Buf)  {
                Log.d("qy","initQyQ2: "  +  pQ2.cfg.name  +  ": bUse2Buf is true");
            }

            //mymemcpy(pQ2.cfg.rwLockParam_syncFlg, pRwLockParam_syncFlg, sizeof(pQ2->cfg.rwLockParam_syncFlg)  );
            pQ2.cfg.rwLockParam_syncFlg = pRwLockParam_syncFlg;

            pQ2.pRw_syncFlg = new qySyncCommProc.CMutexRW();

            if (pCfg.uiMaxCnt_semaTrigger != 0) {
                pQ2.cfg.uiMaxCnt_semaTrigger = pCfg.uiMaxCnt_semaTrigger;
                //if (!(pQ2->hSemaTrigger1 = CreateSemaphore(NULL, 0, pQ2->cfg.uiMaxCnt_semaTrigger, NULL))) return iErr;
                pQ2.hSemaTrigger1 = new Semaphore(0, pQ2.cfg.uiMaxCnt_semaTrigger == 1);
                //Log.e("%S creates sema", pCfg->name);
            }


            if (usCnt_hEvents != 0) {
                if (usCnt_hEvents > 2) {
                    exitQyQ2(pQ2);
                    return iErr;
                }
                pQ2.cfg.usCnt_hEvents = usCnt_hEvents;
                if (evt0Name != null) {
                    {
                        pQ2.cfg.evt0Name = evt0Name;//, cntof_q2Cfg_evt0Name);
                    }
                }

                pQ2.hEvent0 = new EventWaitHandle(false, EventResetMode.AutoReset, pQ2.cfg.evt0Name);

                //for (j = 1; j < pQ2->cfg.usCnt_hEvents; j++)
                if (pQ2.cfg.usCnt_hEvents > 1) {
                    //pQ2->hEvents[j] = CreateEvent(NULL, FALSE, FALSE, NULL);
                    pQ2.hEvent1 = new EventWaitHandle(false, EventResetMode.AutoReset);
                    //if (NULL == pQ2->hEvents[j]) return iErr;
                    if (pQ2.hEvent1 == null) return iErr;
                }
            }


            //char mutexName[64] = "";
            String str_mutexName;
            //for (j = 0; j < mycountof(pQ2->qs); j++)
            {
                j = 0;
                //_sntprintf(mutexName, mycountof(mutexName), ("%s-%u"), pCfg->mutexName_prefix, j);
                str_mutexName = String.format("%s-%d", pCfg.mutexName_prefix, j);
                pQ2.qs0 = new genericQueue.GENERIC_Q();
                if (genericQueue.qyFuncs.initGenericQ(str_mutexName, str_mutexName, 0, pCfg.uiMaxQNodes,
//                        pCfgEx.pfQElemNewEx, pCfgEx.pfQElemNew,
                        pCfgEx.pfQElemInit, pCfgEx.pfQElemExit,
//                        pCfgEx.pfQElemFreeEx, pCfgEx.pfQElemFree,
                        pCfgEx.pf_QElemRemoveEx, pCfgEx.pfQElemRemove, pQ2.qs0) != 0)
                {
                    exitQyQ2(pQ2);
                    return iErr;
                }
                //
                if  (  pQ2.cfg.m_bUse2Buf) {
                    j = 1;
                    str_mutexName = String.format("%s-%d", pCfg.mutexName_prefix, j);
                    pQ2.qs1 = new genericQueue.GENERIC_Q();
                    if (genericQueue.qyFuncs.initGenericQ(str_mutexName, str_mutexName, 0, pCfg.uiMaxQNodes,
//                        pCfgEx.pfQElemNewEx, pCfgEx.pfQElemNew,
                            pCfgEx.pfQElemInit, pCfgEx.pfQElemExit,
//                        pCfgEx.pfQElemFreeEx, pCfgEx.pfQElemFree,
                            pCfgEx.pf_QElemRemoveEx, pCfgEx.pfQElemRemove, pQ2.qs1) != 0) {
                        exitQyQ2(pQ2);
                        return iErr;
                    }
                }
            }


            iErr = 0;


            if (iErr != 0) exitQyQ2(pQ2);

            return iErr;
        }

        public static int exitQyQ2(qyq2.QY_Q2 pQ2) {
            int j;
            if (pQ2 == null) return 0;


            //for (j = 0; j < mycountof(pQ2->qs); j++)
            {
                genericQueue.qyFuncs.exitGenericQ(pQ2.qs0);
                pQ2.qs0 = null;
                genericQueue.qyFuncs.exitGenericQ(pQ2.qs1);
                pQ2.qs1 = null;
            }

            //for (j = 0; j < mycountof(pQ2->hEvents); j++)
            {
                if (pQ2.hEvent0 != null) {
                    //CloseHandle(pQ2->hEvents[j]); pQ2->hEvents[j] = NULL;
                    pQ2.hEvent0.Close();
                    pQ2.hEvent0 = null;

                }
                if (pQ2.hEvent1 != null) {
                    pQ2.hEvent1.Close();
                    pQ2.hEvent1 = null;
                }

            }

            if (pQ2.hSemaTrigger1 != null) {
                //CloseHandle(pQ2->hSemaTrigger1); pQ2->hSemaTrigger1 = NULL;
                pQ2.hSemaTrigger1.release();
                pQ2.hSemaTrigger1 = null;
            }

            //MACRO_safeDelete(pQ2->pRw_syncFlg);
            if (pQ2.pRw_syncFlg != null) {
                pQ2.pRw_syncFlg = null;
            }
            return 0;
        }

        public static void emptyQ2(QY_Q2 pQ) {
            if (pQ == null) return;
            emptyGenericQ(pQ.qs0);
            emptyGenericQ(pQ.qs1);
        }

        public static int q2AvoidTriggerTooHigh(String mutexName_toAvoidTriggerTooHigh, qyq2.QY_Q2 pQ) {

            return 0;
        }

        public static int q2PostMsg(qnmCommProc_mis.MIS_MSGU pQElem, qyq2.QY_Q2 pQ2,String hint) {
            int iErr = -1;

            if  (  pQ2==null)  return  -1;

            //
            pQ2.dwTickCnt_lastData = tmpFunc.qyFuncs.myGetTickCount(null);

            /*
            try(CQyQ2Help q2Help = new CQyQ2Help()){
                if (q2Help.getQ_toPostMsg(pQ2,hint) == null) {
                    return -1;
                }
                return genericQueue.qyFuncs.qPostMsg(pQElem, q2Help.m_pQ_toPostMsg);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
             */

            try{
                genericQueue.GENERIC_Q pQ_toPostMsg  = pQ2.qs0;;
                if (pQ_toPostMsg == null) {
                    return -1;
                }
                return genericQueue.qyFuncs.qPostMsg(pQElem, pQ_toPostMsg);
                //
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }



        }


        public static int q2PostMsgAndTrigger(Object pQElem, qyq2.QY_Q2 pQ2,String hint) {
            if(pQ2 == null) return -1;
            pQ2.dwTickCnt_lastData = tmpFunc.qyFuncs.myGetTickCount(null);
            /*
            try(CQyQ2Help q2Help = new CQyQ2Help()) {
                return q2Help.qPostMsgAndTrigger(pQElem, pQ2,hint);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

             */
            int  tmpiRet;

            genericQueue.GENERIC_Q pQ_toPostMsg  =  pQ2.qs0;
            if (pQElem != null) {
                if ((tmpiRet = genericQueue.qyFuncs.qPostMsg(pQElem, pQ_toPostMsg)) != 0) {
                    Log.e("qyQ2Help::qPostMsgAndTrigger: qPostMsg returns " + tmpiRet  + " pQ2.name:"  + pQ2.cfg.name  +  " "  + hint);
                    return tmpiRet;
                }
            }
            try {
                if (pQ2.hSemaTrigger1 != null) {
                    tmpFunc.qyFuncs.mySemaRelease(pQ2.hSemaTrigger1, 1);
                } else {
                }
                if (pQ2.hEvent0 != null) {
                    tmpFunc.qyFuncs.myEventSet(pQ2.hEvent0);
                }
            } catch (Exception e) {
                Log.d(e.toString());
            }

            return  0;

        }

        public static int q2TriggerToProcess(qyq2.QY_Q2 pQ2) {

            if (pQ2.hEvent0 != null) {
                tmpFunc.qyFuncs.myEventSet(pQ2.hEvent0);
            }
            return 0;
        }

        //  2010/05/03
        public static int q2GetMsg(qyq2.QY_Q2 pQ2, byte[] pQElem, int puiSize) {

            /*
            try(CQyQ2Help q2Help = new CQyQ2Help())
            {
                return genericQueue.qyFuncs.qGetMsg(q2Help.getQ_toGetMsg(pQ2), pQElem, puiSize);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

             */

            try
            {
                genericQueue.GENERIC_Q pQ_toGetMsg  =  pQ2.qs0;
                return genericQueue.qyFuncs.qGetMsg(pQ_toGetMsg, pQElem, puiSize);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }


        }

        public static qnmCommProc_mis.MIS_MSGU q2GetMsg(qyq2.QY_Q2 pQ2) {
            /*
            try(CQyQ2Help q2Help = new CQyQ2Help()) {
                return (qnmCommProc_mis.MIS_MSGU) genericQueue.qyFuncs.qGetMsg(q2Help.getQ_toGetMsg(pQ2));
            } catch (Exception e) {
                Log.e(e.getMessage());
                return null;
            }

             */
            try{
                genericQueue.GENERIC_Q pQ_toGetMsg  =  pQ2.qs0;
                return (qnmCommProc_mis.MIS_MSGU) genericQueue.qyFuncs.qGetMsg(pQ_toGetMsg);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return null;
            }


        }

        //  2014/03/25
        public static int q2GetMsgPkg(qyq2.QY_Q2 pQ2, ByteBuffer pPkg, int uiSize_pkg, int[] uiLens, int pnLens) {
            /*
            try(CQyQ2Help q2Help = new CQyQ2Help()) {
                IntBuffer i_buf = IntBuffer.allocate(Integer.BYTES);
                i_buf.put(pnLens);
                return genericQueue.qyFuncs.qGetMsgPkg(q2Help.getQ_toGetMsg(pQ2), pPkg, uiSize_pkg, uiLens, i_buf.get());
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

             */
            try {
                genericQueue.GENERIC_Q pQ_toGetMsg  =  pQ2.qs0;
                //
                IntBuffer i_buf = IntBuffer.allocate(Integer.BYTES);
                i_buf.put(pnLens);
                return genericQueue.qyFuncs.qGetMsgPkg(pQ_toGetMsg, pPkg, uiSize_pkg, uiLens, i_buf.get());
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

        }


        //  2014/04/10
        public static int q2GetMsgs(qyq2.QY_Q2 pQ2, byte[] pQElems, int[] puiSizes, int pnQElems) {
            /*
            try(CQyQ2Help q2Help = new CQyQ2Help()) {
                return genericQueue.qyFuncs.qGetMsgs(q2Help.getQ_toGetMsg(pQ2), pQElems, puiSizes, pnQElems);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

             */

            try {
                genericQueue.GENERIC_Q pQ_toGetMsg  =  pQ2.qs0;
                return genericQueue.qyFuncs.qGetMsgs(pQ_toGetMsg, pQElems, puiSizes, pnQElems);
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }


        }

        public static boolean isQ2Warning(qyq2.QY_Q2 pQ2, int uiQNodes_warningInterval) {
            if (null == pQ2) return false;

            //
            if  (  !pQ2.cfg.m_bUse2Buf)  {
                return  isQWarning(pQ2.qs0,uiQNodes_warningInterval);
            }

            //
            if (pQ2.bFlag_toPostMsg_q1)
                return genericQueue.qyFuncs.isQWarning(pQ2.qs1, uiQNodes_warningInterval);
            else return genericQueue.qyFuncs.isQWarning(pQ2.qs0, uiQNodes_warningInterval);
        }

        public static boolean isQ2Empty(qyq2.QY_Q2 pQ2) {
            if (null == pQ2) return false;
            //
            if  (  !pQ2.cfg.m_bUse2Buf)  {
                return  isQEmpty(pQ2.qs0);
            }

            //
            return (isQEmpty(pQ2.qs0) && isQEmpty(pQ2.qs1));
        }


        public static int getQ2Nodes_toPostMsg(qyq2.QY_Q2 pQ2) {
            if (null == (pQ2)) return -1;

            //
            if  (  !pQ2.cfg.m_bUse2Buf)  {
                if  (  pQ2.qs0==null)return  -1;
                return  pQ2.qs0.uiQNodes;
            }

            return (int) ((pQ2).bFlag_toPostMsg_q1 ? (pQ2).qs1.uiQNodes : (pQ2).qs0.uiQNodes);
        }

        public static int getQ2Nodes_toGetMsg(qyq2.QY_Q2 pQ2) {
            if (null == (pQ2)) return -1;

            //
            if  (  !pQ2.cfg.m_bUse2Buf  )  {

                if  (  pQ2.qs0==null) return  -1;

                return  pQ2.qs0.uiQNodes;
            }

            //
            if  (  pQ2.qs0==null||pQ2.qs1==null) return  -1;

            return (int) ((pQ2).bFlag_toPostMsg_q1 ? (pQ2).qs0.uiQNodes : (pQ2).qs1.uiQNodes);
        }

        //  2015/02/18
        public static int getQ2Nodes(qyq2.QY_Q2 pQ2) {
            if (null == (pQ2)) return -1;
            if (null == pQ2.qs0 || null == pQ2.qs1) return -1;
            return (int) ((pQ2).qs0.uiQNodes + (pQ2).qs1.uiQNodes);
        }


    }


}

