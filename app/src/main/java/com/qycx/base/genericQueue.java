package com.qycx.base;


import static com.qycx.base.Consts.CONST_qyRet_bufTooSmall;
import static com.qycx.base.Consts.CONST_qyRet_qFull;
import static com.qycx.base.EventResetMode.AutoReset;
import static com.qycx.base.tmpFunc.qyFuncs.mySemaRelease;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;

public class genericQueue {
    public static class Consts {
        public final static int cntof_qCfg_name = 24;


    }

    public static class Q_NODE {

        //  Q_ELEM_T						*	pQElem;
        public qyMemoryFunc.QY_MEMORY qElemMemory = new qyMemoryFunc.QY_MEMORY();
        public int size;
        //
        public boolean ucbQElemInited;
        public Q_NODE pPrev;
        public Q_NODE pNext;

        //  unsigned  int						uiSeqNo;				//  下次加入连表的节点的序号；


    }

    ;

    public interface PF_qElemInit {
        int call(Object p);

    }

    public interface PF_qElemExit {
        void call(Object p);

    }

    public interface PF_qElemRemove {
        void call(Object p, String hint);

    }

    public interface PF_qElemRemoveEx {
        void call(Object p, int size, String hint);

    } //  2012/11/10

    public static class GENERIC_Q_CFG {
        public String name = "";//[] = new char[Consts.cntof_qCfg_name];                 //  队列名称, 用在调试等工作用
        public int uiMaxQNodes;               //  对队列中的节点数做限制，为0时不限制

        public String mutexName_prefix = "";//[]=new char[cntof_mutexName_prefix];     //  队列中的增删处理节点同步用

        //
        public int uiMaxCnt_semaTrigger;      //  对用在线程处理做触发用的semaphore, 为0时不用

        //
        public boolean bUseEvt;
        public String evt0Name = "";             //  2009/12/22

        //
        public boolean b_showInfoQ;


    }

    ;

    //  2012/11/10
    public static class GENERIC_Q_cfgEx {
        public GENERIC_Q_CFG common = new GENERIC_Q_CFG();
        //
        public PF_qElemInit pfQElemInit;
        public PF_qElemExit pfQElemExit;
        public PF_qElemRemoveEx pf_QElemRemoveEx;
        public PF_qElemRemove pfQElemRemove;


    }

    ;


    public static class GENERIC_Q {

        public GENERIC_Q_CFG cfg = new GENERIC_Q_CFG();
        public Q_NODE pFront;
        public Q_NODE pRear;
        public PF_qElemInit pfQElemInit;               //  2007/06/10
        public PF_qElemExit pfQElemExit;               //  
        public PF_qElemRemove pfQElemRemove;               //  2009/12/08
        public PF_qElemRemoveEx pfqElemRemoveEx;           //  2012/11/10
        public int uiQNodes;
        public Semaphore hSemaTrigger;                //  2007/05/08
        public long lPrevCnt_semaTrigger;      //  2007/05/08, 没有严格意义，用来debug用。

        //
        public EventWaitHandle hEvent;
        public boolean ucbTriggerErr;                //  2008/11/21. 在使用触发机制的队列中，当触发器满了，设置此位。有利于及时调用相关函数将触发器恢复
        public boolean bNeedSync;

        public qySyncObj.SYNC_OBJ_PARAM syncObjParam = new qySyncObj.SYNC_OBJ_PARAM();
        public Semaphore hMutex;

        //  2012/07/14
        public boolean bInited;

        //  2009/09/20 
        public Q_NODE pLastQNode;


    }

    ;


    public interface PF_qPostMsg {
        int call(Object pQElem, int size, GENERIC_Q pQ);
    }

    public interface PF_qGetMsg {
        int call(GENERIC_Q pQ, Object pQElem, int puiSize);
    }

    public static class qyFuncs {

        //
        public static boolean isQFull(GENERIC_Q pQ) {
            if (pQ == null) return false;
            if (pQ.cfg.uiMaxQNodes != 0 && pQ.uiQNodes >= pQ.cfg.uiMaxQNodes) {
                return true;
            }
            return false;
        }

        public static boolean isQWarning(GENERIC_Q pQ, int uiQNodes_warningInterval) {
            if (pQ == null) return false;
            if (pQ.cfg.uiMaxQNodes != 0 && pQ.uiQNodes + uiQNodes_warningInterval >= pQ.cfg.uiMaxQNodes)
                return true;
            return false;
        }

        public static boolean isQEmpty(GENERIC_Q pQ) {
            if (pQ == null) return false;
            if (pQ.pFront == null) return false;
            return pQ.pFront.pNext == null;
        }

        static Q_NODE

        qNodeNew() {
            return new Q_NODE();
        }


        static void qNodeFree(GENERIC_Q pQ, Q_NODE pQNode, boolean bElemRemove) {

            if (null == pQNode) return;

            if (pQNode.qElemMemory.m_pBuf != null) {
                if (pQNode.ucbQElemInited) {
                    if (pQ.pfQElemExit != null) pQ.pfQElemExit.call(pQNode.qElemMemory.m_pBuf);
                    pQNode.ucbQElemInited = false;
                }
                if (bElemRemove) {   //  2009/12/08
                    String hint = pQ.cfg.name;
                    if (pQ.pfqElemRemoveEx != null)
                        pQ.pfqElemRemoveEx.call(pQNode.qElemMemory.m_pBuf, (int) pQNode.size, hint); //  2012/11/10
                    else {
                        if (pQ.pfQElemRemove != null)
                            pQ.pfQElemRemove.call(pQNode.qElemMemory.m_pBuf, hint);
                    }
                }
            }
        }


        public static int initGenericQ(String qName, String mutexName_prefix, int uiMaxCnt_semaTrigger, int uiMaxQNodes,
                                       PF_qElemInit pfQElemInit, PF_qElemExit pfQElemExit,
                                       PF_qElemRemoveEx pfQElemRemoveEx, PF_qElemRemove pfQElemRemove, GENERIC_Q pQ) {
            int iErr = -1;
            if (pQ == null) return -1;

            if (qName != null) {
                pQ.cfg.name = qName;
            }
            if (mutexName_prefix != null) {

                pQ.cfg.mutexName_prefix = mutexName_prefix;
                pQ.bNeedSync = true;
                pQ.syncObjParam.mutexName = pQ.cfg.mutexName_prefix;
                pQ.hMutex = new Semaphore(1);
            }
            if (uiMaxCnt_semaTrigger != 0) {
                pQ.cfg.uiMaxCnt_semaTrigger = uiMaxCnt_semaTrigger;
                pQ.hSemaTrigger = new Semaphore(1);
                Log.d("CreateSemaphore: " + pQ.cfg.name + " sema");
            }
            pQ.cfg.uiMaxQNodes = uiMaxQNodes;

            pQ.pfQElemInit = pfQElemInit;
            pQ.pfQElemExit = pfQElemExit;

            pQ.pfqElemRemoveEx = pfQElemRemoveEx;  //  2012/11/10
            pQ.pfQElemRemove = pfQElemRemove;

            pQ.pFront = qNodeNew();
            pQ.pRear = pQ.pFront;
            pQ.uiQNodes = 0;
            pQ.bInited = true;

            iErr = 0;
            return iErr;
        }

        public static int initGenericQ(String qName, String mutexName_prefix, int uiMaxCnt_semaTrigger, int uiMaxQNodes,
                                       PF_qElemInit pfQElemInit, PF_qElemExit pfQElemExit,
                                       PF_qElemRemove pfQElemRemove, GENERIC_Q pQ) {
            PF_qElemRemoveEx pfQElemRemoveEx = null;

            return initGenericQ(qName, mutexName_prefix, uiMaxCnt_semaTrigger, uiMaxQNodes,
                    pfQElemInit, pfQElemExit, pfQElemRemoveEx, pfQElemRemove, pQ);
        }

        public static int initGenericQ(String qName, String mutexName_prefix, int uiMaxCnt_semaTrigger, int uiMaxQNodes, GENERIC_Q pQ) {
            return initGenericQ(qName, mutexName_prefix, uiMaxCnt_semaTrigger, uiMaxQNodes, null, null, null, null, pQ);
        }

        public static int initGenericQ(GENERIC_Q_CFG pCfg, PF_qElemInit pfQElemInit, PF_qElemExit pfQElemExit, PF_qElemRemove pfQElemRemove, GENERIC_Q pQ) {
            return initGenericQ(pCfg.name, pCfg.mutexName_prefix, pCfg.uiMaxCnt_semaTrigger, pCfg.uiMaxQNodes, pfQElemInit, pfQElemExit, pfQElemRemove, pQ);
        }


        public static int initGenericQ(GENERIC_Q_cfgEx pCfg, GENERIC_Q pQ) {
            int iErr = -1;

            String qName = pCfg.common.name;
            String mutexName_prefix = pCfg.common.mutexName_prefix;
            int uiMaxCnt_semaTrigger = pCfg.common.uiMaxCnt_semaTrigger;
            int uiMaxQNodes = pCfg.common.uiMaxQNodes;

            PF_qElemInit pfQElemInit = pCfg.pfQElemInit;
            PF_qElemExit pfQElemExit = pCfg.pfQElemExit;

            PF_qElemRemoveEx pfQElemRemoveEx = pCfg.pf_QElemRemoveEx;
            PF_qElemRemove pfQElemRemove = pCfg.pfQElemRemove;

            char[] a_qName = qName.toCharArray();
            String str_mutexName_prefix = mutexName_prefix;
            char[] a_mutexName_prefix = str_mutexName_prefix.toCharArray();

            if (pQ == null) return -1;

            pQ.cfg.name = qName;
            pQ.cfg.uiMaxQNodes = uiMaxQNodes;
            pQ.cfg.mutexName_prefix = str_mutexName_prefix;//, cntof_mutexName_prefix);
            pQ.bNeedSync = true;
            pQ.syncObjParam.mutexName = pQ.cfg.mutexName_prefix;
            pQ.hMutex = new Semaphore(1);

            if (uiMaxCnt_semaTrigger != 0) {
                pQ.cfg.uiMaxCnt_semaTrigger = uiMaxCnt_semaTrigger;
                pQ.hSemaTrigger = new Semaphore(1);
            }

            //  2015/08/16
            if (pCfg.common.bUseEvt) {
                pQ.cfg.bUseEvt = pCfg.common.bUseEvt;
                pQ.hEvent = new EventWaitHandle(false, AutoReset, pQ.cfg.evt0Name);
            }

            pQ.pfQElemInit = pfQElemInit;
            pQ.pfQElemExit = pfQElemExit;
            pQ.pfQElemRemove = pfQElemRemove;
            pQ.pfqElemRemoveEx = pCfg.pf_QElemRemoveEx;
            pQ.pFront = qNodeNew();
            pQ.pRear = pQ.pFront;
            pQ.uiQNodes = 0;
            pQ.bInited = true;

            //
            iErr = 0;

            return iErr;

        }


        //
        public static void exitGenericQ(GENERIC_Q pQ) {
            Q_NODE pQNode;
            Q_NODE tmppQNode;

            if (pQ == null) return;
            if (pQ.pFront == null) return;

            for (pQNode = pQ.pFront.pNext;
                 pQNode != null; ) {
                tmppQNode = pQNode.pNext;
                qNodeFree(pQ, pQNode, true);
                pQNode = tmppQNode;
            }
            pQ.pFront.pNext = null;
            qNodeFree(pQ, pQ.pFront, true);
            pQ.pFront = null;

            //  2015/08/16
            if (pQ.hEvent != null) {
                //CloseHandle(pQ.hEvent); pQ.hEvent = NULL;
                pQ.hEvent.Close();
                pQ.hEvent = null;
            }

            //
            if (pQ.hSemaTrigger != null) {
                //CloseHandle(pQ.hSemaTrigger); pQ.hSemaTrigger = NULL;
                //Log.e(_T("CloseHandle: %s sema"), pQ.cfg.name);
                pQ.hSemaTrigger.release();
                pQ.hSemaTrigger = null;
            }

            if (pQ.hMutex != null) {
                pQ.hMutex.release();

                pQ.hMutex = null;
            }
        }

        public static int qPostMsg(Object pQElem, GENERIC_Q pQ) {
            return qPostMsgEx(pQElem, 0, pQ);
        }

        public static int qPostMsgEx(Object pQElem, int iAllocType, GENERIC_Q pQ) {
            int iErr = -1;
            Q_NODE pQNode;
            int uiSeqNo = 0;

            if (pQ == null) {
                Log.d("qPostMsgEx failed, pQ is null");
                return -1;
            }

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
				//if(pQ.cfg.uiMaxQNodes != 0 && pQ.uiQNodes >= 15){
                if (isQFull(pQ)) {
                    if (!pQ.cfg.b_showInfoQ) {
                        String str ="qPostMsg failed: " + pQ.cfg.name + " is full, qNodes " + pQ.uiQNodes;
                        Log.e(str);
//                        showInfo_open.qyFuncs.showInfo_open(0, null, str);
                    }
                    return CONST_qyRet_qFull;
                }

                pQNode = new Q_NODE();
                pQNode.qElemMemory = new qyMemoryFunc.QY_MEMORY();
                pQNode.qElemMemory.m_obj = pQElem;

                if (pQ.pfQElemInit != null && pQ.pfQElemInit.call(pQNode.qElemMemory.m_pBuf) != 0) {
                    qNodeFree(pQ, pQNode, false);
                    return iErr;
                }
                pQNode.ucbQElemInited = true;

                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex, 10000, "qPostMsgEx.374") != 0) {
                    qNodeFree(pQ, pQNode, false);
                    return iErr;
                }

                if (isQFull(pQ)) {
                    iErr = CONST_qyRet_qFull;

                    syncObj.Dispose();
                    qNodeFree(pQ, pQNode, false);
                    return iErr;
                }
                pQNode.pNext = null;

                pQ.pRear.pNext = pQNode;
                pQ.pRear = pQNode;
                pQ.uiQNodes++;
                syncObj.Dispose();
                return 0;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return iErr;
            }
        }


        public static int qPostMsgAndTrigger(qnmCommProc_mis.MIS_MSGU pQElem, GENERIC_Q pQ) {
            int tmpiRet = -1;
            boolean ucbTriggerErr = false;

            if (pQElem != null) {
                if ((tmpiRet = qPostMsg(pQElem, pQ)) < 0) return tmpiRet;
            }
            if (pQ.hSemaTrigger != null) {
                mySemaRelease(pQ.hSemaTrigger, 1);
            }
            //  2015/08/16
            if (pQ.hEvent != null) {
                //SetEvent(pQ.hEvent);
                tmpFunc.qyFuncs.myEventSet(pQ.hEvent);
            }

            //  2008/11/21
            if (ucbTriggerErr != pQ.ucbTriggerErr) {
                pQ.ucbTriggerErr = ucbTriggerErr;
            }

            return 0;
        }


        static int tmp_qGetMsg(GENERIC_Q pQ, byte[] pQElem, int puiSize, boolean bNotShowInfo) {
            Q_NODE pQNode = null;
            if (null == pQElem || 0 == puiSize) return -1;
            {
                if (null == pQ.pFront.pNext) return -1;
                pQNode = pQ.pFront.pNext;
                if (puiSize < pQNode.size) {
                    return CONST_qyRet_bufTooSmall; //  -1;
                }
                if (pQ.pFront.pNext.pNext != null) {
                    pQ.pFront.pNext = pQ.pFront.pNext.pNext;
                } else {
                    pQ.pFront.pNext = null;
                    pQ.pRear = pQ.pFront;
                }
                pQ.uiQNodes--;
            }
//            mymemcpy(pQElem, pQNode.qElemMemory.m_pBuf, pQNode.size);
            puiSize = pQNode.size;
            pQNode.ucbQElemInited = false;
            qNodeFree(pQ, pQNode, false);
            return 0;

        }

        static Object tmp_qGetMsgEx(GENERIC_Q pQ) {
            Q_NODE pQNode = null;

            if (null == pQ || null == pQ.pFront || null == pQ.pFront.pNext) return null;
            pQNode = pQ.pFront.pNext;
//                if (puiSize < pQNode.size) {
//                    return CONST_qyRet_bufTooSmall; //  -1;
//                }
            if (pQ.pFront.pNext.pNext != null) {
                pQ.pFront.pNext = pQ.pFront.pNext.pNext;
            } else {
                pQ.pFront.pNext = null;
                pQ.pRear = pQ.pFront;
            }
            pQ.uiQNodes--;


            pQNode.ucbQElemInited = false;
//            mymemcpy(pQElem, pQNode.qElemMemory.m_pBuf, pQNode.size);
//            puiSize = pQNode.size;
            qNodeFree(pQ, pQNode, false);

            //
            return pQNode.qElemMemory.m_obj;
        }

        public static int qGetMsg(GENERIC_Q pQ, byte[] pQElem, int puiSize) {
            if (pQ == null) return -1;
            if (null == pQElem || 0 == puiSize) return -1;
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qGetMsg.480") != 0)
                    return -1;
                int i = tmp_qGetMsg(pQ, pQElem, puiSize, false);
                syncObj.Dispose();
                return i;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
        }

        public static Object qGetMsg(GENERIC_Q pQ) {
            if (pQ == null) return null;
//            if (null == pQElem) return -1;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())

            {

                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex, 1000,"qGetMsg.499") != 0)
                    return null;

                Object oRet = tmp_qGetMsgEx(pQ);

                if (pQ.bNeedSync) {
                    syncObj.Dispose();
                }

                return oRet;

            } catch (Exception e) {
                Log.e(e.getMessage());
                return null;
            }

        }

        //  2011/07/15. 一次性读取多个包。
        //  pQElems[i]为buf
        //  *puiSizes[i]为size,输出为长度
        //  *pnQElems为最多一次性读取多少个包，输出真实读取的包数
        public static int qGetMsgs(GENERIC_Q pQ, byte[] pQElems, int[] puiSizes, int pnQElems) {
            if (pQ == null) return -1;
            if (null == pQElems || null == puiSizes || 0 == pnQElems) return -1;

            int i;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qGetMsgs.530") != 0)
                    return -1;

                for (i = 0; i < pnQElems;
                     i++) {
                    if (tmp_qGetMsg(pQ, pQElems, puiSizes[i], false) != 0) break;
                }
                pnQElems = (int) i;
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            //  OutputDebugString(  _T(  "qGetMsgs: Not finished\n"  )  );
            return (i != 0) ? 0 : -1;
        }


        //  2011/12/10, 一次性读取多个包，并存放在一个pkg里
        //  *puiSizes[i]输出为长度
        //  *pnQElems为puiSizes的成员数，也因此表示最多一次性读取多少个包，输出真实读取的包数. 
        public static int qGetMsgPkg(GENERIC_Q pQ, Object pPkg, int uiSize_pkg, int[] uiLens, int pnLens) {
            if (null == pQ) return -1;
            if (null == pPkg || 0 == uiSize_pkg || null == uiLens || 0 == pnLens) return -1;

            int i;
            boolean bNotShowInfo = false;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qGetMsgPkg.560") != 0)
                    return -1;

                for (i = 0; i < pnLens && uiSize_pkg != 0;
                     i++) {
                    uiLens[i] = uiSize_pkg;
//                    if (tmp_qGetMsg(pQ, pPkg,  uiLens[i], bNotShowInfo) != 0) break;
//                    pPkg = Object.add(pPkg, (int) uiLens[i]);
                    uiSize_pkg -= uiLens[i];
                    bNotShowInfo = true;
                }
                pnLens = (int) i;
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            //  OutputDebugString(  _T(  "qGetMsgs: Not finished\n"  )  );
            return (i != 0) ? 0 : -1;
        }


        //
        static void tmpRemoveAQNode(GENERIC_Q pQ, Q_NODE pQNode) {
            if (null == pQNode) return;

            if (pQNode == pQ.pFront) return;       //  ¶Ôfront½Úµã£¬²»ÄÜÒÆ³ý

            if (pQNode.pNext != null) {
                pQNode.pNext.pPrev = pQNode.pPrev;
                if (pQNode.pPrev != null) pQNode.pPrev.pNext = pQNode.pNext;
                else pQ.pFront.pNext = pQNode.pNext;
            } else {
                if (pQNode.pPrev != null) {
                    pQNode.pPrev.pNext = null;
                    pQ.pRear = pQNode.pPrev;
                } else {
                    pQ.pFront.pNext = null;
                    pQ.pRear = pQ.pFront;
                }
            }

            pQNode.pNext = null;
            qNodeFree(pQ, pQNode, true);

            pQ.uiQNodes--;

            return;

        }


        public static int qRemoveMsg(GENERIC_Q pQ, qyDefs.PF_bCommonHandler pf_bRemoveCond, Object p0, Object p1) {
            int iErr = -1;
            Q_NODE pQNode = null;

            boolean tmpbRet = false;
            Q_NODE tmppQNode = null;

            if (null == pf_bRemoveCond) return -1;


            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qRemoveMsg.825") != 0)
                    return -1;

                for (pQNode = pQ.pFront.pNext;
                     pQNode != null; ) {
                    tmpbRet = pf_bRemoveCond.call(p0, p1, pQNode.qElemMemory.m_obj);
                    if (!tmpbRet) {
                        pQNode = pQNode.pNext;
                        continue;
                    }
                    tmppQNode = pQNode;
                    pQNode = pQNode.pNext;
                    tmpRemoveAQNode(pQ, tmppQNode);
                }
                syncObj.Dispose();
                iErr = 0;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            //  errLabel:
            return iErr;

        }

        public static void emptyGenericQ(GENERIC_Q pQ) {
            Q_NODE pQNode;
            Q_NODE tmppQNode;

            //if (!pQ) return;
            if  (  pQ==null) return;


            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                //  if  (  pQ.cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ.cfg.mutexName_prefix  )  )  goto  errLabel;
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"emptyGenericQ.662") != 0) {
                    return;
                }

                for (pQNode = pQ.pFront.pNext;
                     pQNode != null; ) {
                    //
                    //if (pQ.hSemaTrigger) WaitForSingleObject(pQ.hSemaTrigger, 0);     //  ¶ÔÓÚÊ¹ÓÃ´¥·¢»úÖÆµÄ¶ÓÁÐ£¬ÏÈÒª°Ñ´¥·¢¸ø¼õÉÙÁË
                    if (pQ.hSemaTrigger != null) {
                        try {
                            pQ.hSemaTrigger.wait(0);     //  ¶ÔÓÚÊ¹ÓÃ´¥·¢»úÖÆµÄ¶ÓÁÐ£¬ÏÈÒª°Ñ´¥·¢¸ø¼õÉÙÁË
                        } catch (InterruptedException e) {
                            Log.e(e.getMessage());
                            return;
                        }
                    }
                    //
                    tmppQNode = pQNode.pNext;
                    qNodeFree(pQ, pQNode, true);
                    pQNode = tmppQNode;
                }

                pQ.pFront.pNext = null;
                pQ.pRear = pQ.pFront;
                pQ.uiQNodes = 0;
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }


        public static int qTraverse(GENERIC_Q pQ, qyDefs.PF_commonHandler pf_visit, Object p0, Object p1) {
            int iErr = -1;
            Q_NODE pQNode = null;
            int tmpiRet = -1;


            if (null == pf_visit) return -1;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex, 100,"qTraverse.703") != 0) {
                    return -1;
                }

                for (pQNode = pQ.pFront.pNext; pQNode != null; pQNode = pQNode.pNext) {
                    tmpiRet = pf_visit.call(p0, p1, pQNode.qElemMemory.m_obj);
                    if (tmpiRet != 0) {
                        iErr = tmpiRet;
                        syncObj.Dispose();
                        return iErr;
                    }
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            iErr = 0;
            return iErr;
        }

        public static int qTraverseEx(GENERIC_Q pQ, qyDefs.PF_commonHandler pf_visit, Object p0, Object p1) {
            int iErr = -1;
            Q_NODE pQNode = null;
            int tmpiRet = -1;


            if (null == pf_visit) return -1;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex, 100,"qTraverseEx.733") != 0)
                    return -1;

                for (pQNode = pQ.pFront.pNext; pQNode != null; pQNode = pQNode.pNext) {
                    tmpiRet = pf_visit.call(p0, p1, pQNode.qElemMemory.m_obj);
                    if (tmpiRet != 0) {
                        iErr = tmpiRet;
                        syncObj.Dispose();
                        return iErr;
                    }
                }
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            iErr = 0;
            return iErr;
        }


        public static void genericQFree(GENERIC_Q pQ) {
            if (null == pQ) return;
            exitGenericQ(pQ);
        }
    }
}
