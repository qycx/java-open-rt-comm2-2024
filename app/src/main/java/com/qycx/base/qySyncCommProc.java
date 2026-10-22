

package com.qycx.base;

import static com.qycx.base.qySyncCommProc.Consts.CONST_uiMilliSeconds_sema_r;
import static com.qycx.base.qyq2.Consts.CONST_uiInitCnt_sema_q2SyncFlg;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class qySyncCommProc {
    private static final String lock = qySyncCommProc.class.getSimpleName();

    public static class Consts {
        public final static int CONST_uiMilliSeconds_sema_r = 10000;
        public final static int CONST_iAllocType_null = 0;     //  don't used.
        //  #define		CONST_iAllocType_tmpMemory							10		//  2010/09/18
        public final static int CONST_iAllocType_user = 1000;    //  2010/09/18
    }

    public static class CQySyncCnt {
        //  2013/07/03. 多线程同步计数用
        boolean IsDisposed = false;

        static class syncCnt_m_var {
            public int[] plCnt;
            //
            public qyMcMainDaemon_isCli_mgr.TMP_tHintBuf tHint;
            //public String tHint[32];        //  2013/08/06. fot debug
            public boolean bDebug;

        }

        syncCnt_m_var m_var;


        public CQySyncCnt() {
            m_var = new syncCnt_m_var();
        }

        public void Dispose() {
            Dispose(true);
            //GC.SuppressFinalize(this);
        }

        protected void Dispose(boolean Disposing) {
            if (!IsDisposed) {
                if (Disposing) {
                    //Clean Up managed resources
                }
                //Clean up unmanaged resources
                cancelSync();

            }
            IsDisposed = true;
        }


        public int sync(int[] plCnt, String hint) {
            if (null == plCnt || m_var.plCnt != null) {
                return -1;
            }
            if (plCnt.length <= 0) return -1;
            synchronized (lock) {
                m_var.plCnt = plCnt;
                m_var.plCnt[0] += 1;
            }
            return 0;
        }

        //
        void cancelSync() {
            if (m_var.plCnt != null) {
                synchronized (lock) {
                    m_var.plCnt[0] -= 1;
                }
            }
        }
    }

    ;

    public static class RW_lock_param {
        public int uiMaxCnt_sema;
        public int uiInitCnt_sema;                                                //  2009/05/10
        public int uiMilliSeconds_mutex_r;
        public int uiMilliSeconds_sema_r;
        public int uiMilliSeconds_mutex_w;
        public int uiMilliSeconds_sema_w;
    }

    ;


    public static class qyFuncs {

        public static int RW_lock_param_init(qySyncCommProc.RW_lock_param p) {

            qySyncCommProc.RW_lock_param rwLockParam = p;

            //
            rwLockParam.uiMaxCnt_sema = CONST_uiInitCnt_sema_q2SyncFlg;
            rwLockParam.uiInitCnt_sema = rwLockParam.uiMaxCnt_sema - 1;
            rwLockParam.uiMilliSeconds_mutex_r = 10000;
            rwLockParam.uiMilliSeconds_sema_r = 10000;
            rwLockParam.uiMilliSeconds_mutex_w = 10000;
            rwLockParam.uiMilliSeconds_sema_w = 10000;


            return 0;
        }


        public static void qyExitProcess(String hint) {
            if (null == hint) hint = ("qyExitProcess called");

            //MessageBox(NULL, hint, ("Serious error."), MB_OK | MB_TASKMODAL);
            //ExitProcess(0);
            Thread.currentThread().stop();

            return;
        }

    }


    public static class CMutexRW {
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        public String w_hint;

        public int Lock_DataRead(int uiMilliSeconds_sema) {
            try {
                if (rwLock.readLock().tryLock(uiMilliSeconds_sema, TimeUnit.MILLISECONDS)) {
                    return 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return -1;
        }

        public void Unlock_DataRead() {
            if (rwLock.getReadHoldCount() > 0) {
                rwLock.readLock().unlock();
            }
        }

        public int Lock_DataWrite(int uiMilliSeconds_sema) {
            try {
                if (rwLock.writeLock().tryLock(uiMilliSeconds_sema, TimeUnit.MILLISECONDS)) {
                    return 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return -1;
        }

        public void Unlock_DataWrite() {
            if (rwLock.isWriteLockedByCurrentThread()) {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static class CQyRWLock implements AutoCloseable {
        private CMutexRW m_pMutexRW;
        private volatile boolean m_bIsLocked;
        private volatile boolean m_bIsRlocked;
        private volatile boolean isDisposed = false;

        //
        public boolean m_bDbg=false;
        public String m_str_lockHint  =  "";


        //
        public CQyRWLock() {
            // No need to memset in Java
        }

        @Override
        public void close() {
            dispose();
        }

        public void dispose() {
            dispose(true);
            // No need to call GC.SuppressFinalize in Java
        }

        protected void dispose(boolean disposing) {
            if (!isDisposed) {
                if (disposing) {
                    // Clean up managed resources if any
                }
                // Clean up unmanaged resources
                unlock();
                isDisposed = true;
            }
        }

        public int rLock(CMutexRW p, int uiMilliSeconds_sema,String hint) {
            if (p == null || m_bIsLocked) {
                return -1;
            }

            if (p.Lock_DataRead(uiMilliSeconds_sema) != 0) {
                return -1;
            }

            //
            if  (  m_bDbg)  {

                //Log.d("tn_msgInput",  "rLock "  +  tmpFunc.qyFuncs.myGetCurrentProcessId()  +  " "  + hint);
                //
                m_str_lockHint  =  hint;
            }


            //
            m_pMutexRW = p;
            m_bIsLocked = true;
            m_bIsRlocked = true;
            return 0;
        }

        public int wLock(CMutexRW p, int uiMilliSeconds_sema, String hint) {
            if (p == null || m_bIsLocked) {
                return -1;
            }

            if (p.Lock_DataWrite(uiMilliSeconds_sema) != 0) {
                return -1;
            }

            //
            if  (  m_bDbg  )  {
                Log.d("tn_msgInput","wLock "  +  tmpFunc.qyFuncs.myGetCurrentProcessId());
            }



            //
            p.w_hint = hint;
            m_pMutexRW = p;
            m_bIsLocked = true;
            m_bIsRlocked = false;
            return 0;
        }

        public void unlock() {
            if (m_bIsLocked) {
                if (m_bIsRlocked) {
                    m_pMutexRW.Unlock_DataRead();
                } else {
                    m_pMutexRW.Unlock_DataWrite();
                    m_pMutexRW.w_hint = "";
                }

                //
                if  ( m_bDbg  )  {
                    //
                    if  (  false  ) {
                        if (m_bIsRlocked) {
                            Log.d("tn_msgInput", "r.unlock " + tmpFunc.qyFuncs.myGetCurrentProcessId()  +  " "  + m_str_lockHint);
                        } else {
                            Log.d("tn_msgInput", "w.unlock " + tmpFunc.qyFuncs.myGetCurrentProcessId()  +  " "  +  m_str_lockHint);

                        }
                    }
                }

                //
                m_pMutexRW = null;
                m_bIsLocked = false;
                m_bIsRlocked = false;
            }
        }
    }


    public static class CQyMalloc {                                   //  2008/03/29, ¿ÉÒÔ×Ô¶¯ÊÍ·ÅµÄ¶¯Ì¬·ÖÅäÀà¡£
        boolean IsDisposed = false;

        private qyMemoryFunc.QY_MEMORY m_var;

        public CQyMalloc() {
            m_var = new qyMemoryFunc.QY_MEMORY();
        }


        public void Dispose() {
            Dispose(true);
            //GC.SuppressFinalize(this);
        }

        protected void Dispose(boolean Disposing) {
            if (!IsDisposed) {
                if (Disposing) {
                    //Clean Up managed resources
                }
            }
            IsDisposed = true;
        }

        public qyMemoryFunc.QY_MEMORY get()       //  2010/04/23
        {
            if (null == m_var.m_pBuf) return null;
            return m_var;
        }

        public int detach()  //  2009/05/03
        {
            if (null == m_var.m_pBuf) return -1;
            //memset(m_var, 0, sizeof(m_var));
            qyMemoryFunc.QY_MEMORY pm_var = m_var;
            {
                //qyFuncs.mymemset(pm_var, 0, sizeof(QY_MEMORY));
            }
            return 0;
        }

        public int attach(qyMemoryFunc.QY_MEMORY p)  //  2009/12/07
        {
            //if (!p) return -1;
            if (m_var.m_pBuf != null || m_var.m_pVirtualData != null) return -1;
            //memcpy(m_var, p, sizeof(m_var));
            m_var = p;
            return 0;
        }
    }


    /// <summary>
    /// /////////////////////////////////////////////////////////
    /// </summary>
    /// <param name="p0"></param>
    /// <param name="val"></param>
    /// <param name="puiTranNo"></param>
    /// <returns></returns>

    public static class CQyMalloc_array {                                   //  2008/03/29, ¿ÉÒÔ×Ô¶¯ÊÍ·ÅµÄ¶¯Ì¬·ÖÅäÀà¡£
        boolean IsDisposed = false;

        //
        public CQyMalloc[] mems;
        public int m_n;

        public CQyMalloc_array(int n) {
            if (n == 0) return;
            m_n = n;
            this.mems = new CQyMalloc[m_n];
            for (int i = 0; i < m_n; i++) {
                mems[i] = new CQyMalloc();
            }

        }

        public void Dispose() {
            Dispose(true);
            //GC.SuppressFinalize(this);
        }

        protected void Dispose(boolean Disposing) {
            if (!IsDisposed) {
                if (Disposing) {
                    //Clean Up managed resources
                }
                //Clean up unmanaged resources
                for (int i = 0; i < m_n; i++) {
                    mems[i].Dispose();
                }
            }
            IsDisposed = true;
        }
    }

    public interface PF_getuiNextTranNo {
        int call();
    } //  2014/11/28

}
