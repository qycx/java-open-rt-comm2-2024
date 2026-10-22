


package com.qycx.base;

import static com.qycx.base.qnmDb.Consts.CONST_mutexName_qnmDb;
import static com.qycx.base.qnmDb.Consts.CONST_qnmMaxDbs;
import static com.qycx.base.qySqlFunc.Consts.CONST_dbType_myDb;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;

public class qnmDb {


    public static class Consts {
        public final static int CONST_qnmMaxDbs = 32;  //  128
        public final static String CONST_mutexName_qnmDb = "qnmDbMutex";

    }


    public static class QNM_DB_MEM {
        public Object pDb;      //数据库对象地址
        public boolean ucbUsed; //是否这个数据库被使用
        //  int			iStatus;
        public int uiThreadId;
        public int iDsnIndex;      //  2006/12/22 数据库对象的索引
        public int iDbType;        //  2012/07/14 数据库对象的类型
    }

    ;


    //  2013/07/28
    public static class QNM_db_pool {
        public QNM_DB_MEM[] m_dbMems;    //[CONST_qnmMaxDbs];
        public int m_g_iCnt_dbMems;
    }

    ;

    public static class CQnmDb implements AutoCloseable {
        public CQnmDb() {
            m_pDbMem = null;
        }

        //
        public QNM_DB_MEM m_pDbMem;
        boolean IsDisposed = false;

        //
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
                if (null != m_pDbMem) {
                    try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
                    {
                        if (syncObj.sync(CONST_mutexName_qnmDb, g_m_hMutex_forPool,"qnmDb.Dispose.68") != 0)
                            return;

                        //
                        m_pDbMem.ucbUsed = false;
                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }
                }

            }
            IsDisposed = true;
        }


        //
        static Semaphore g_m_hMutex_forPool = null;
        static QNM_db_pool g_pQnmDbPool = null;

        //
        public static int initDbPool() {
            int iErr = -1;

            //  if  (  m_ucbInited  )  return  0;
            if (null != g_pQnmDbPool) return 0;

            //int size = sizeof(QNM_db_pool);
            g_pQnmDbPool = new QNM_db_pool();    // (QNM_db_pool)mymalloc(size);
            //memset(g_pQnmDbPool, 0, size);

            g_pQnmDbPool.m_dbMems = new QNM_DB_MEM[CONST_qnmMaxDbs];
            g_pQnmDbPool.m_g_iCnt_dbMems = g_pQnmDbPool.m_dbMems.length;    // mycountof(g_pQnmDbPool->m_dbMems);
            for (int i = 0; i < g_pQnmDbPool.m_g_iCnt_dbMems; i++) {
                g_pQnmDbPool.m_dbMems[i] = new QNM_DB_MEM();
            }

            //
            g_m_hMutex_forPool = new Semaphore(1);

            //  memset(  m_dbMems,  0,  sizeof(  m_dbMems  )  );
            //  m_ucbInited  =  TRUE;

            iErr = 0;


            if (iErr != 0) {
                exitDbPool();
            }

            return 0;
        }

        public static void exitDbPool() {
            int i;
            //  CDatabase	*	pDb		=	NULL;
            Object pDb = null;

            //  if  (  !m_ucbInited  )  return;
            if (null == g_pQnmDbPool) return;

            g_pQnmDbPool = null;

            //
            if (g_m_hMutex_forPool != null) {
                if (g_m_hMutex_forPool.tryAcquire()) {
                    try {

                    } finally {
                        g_m_hMutex_forPool.release();
                    }
                }
                g_m_hMutex_forPool = null;
            }
        }


        public static int makeDbPool_myDb(myDb.CMyDb pDb, int cnt, int iDsnIndex) {
            int iErr = -1;


            int i;
            int index = 0;


            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
//                if (0 != syncObj.sync(CONST_mutexName_qnmDb, g_m_hMutex_forPool)) return -1;

                for (i = 0; i < cnt; i++) {

                    //  取空闲的dbMems
                    for (; index < g_pQnmDbPool.m_dbMems.length; index++) {
                        if (null == g_pQnmDbPool.m_dbMems[index].pDb) break;
                    }
                    if (index == g_pQnmDbPool.m_dbMems.length) {
                        //Log.e(TAG,"dbPool已满，makeDbPool失败");
                        return iErr;
                    }

                    g_pQnmDbPool.m_dbMems[index].pDb = pDb;
                    g_pQnmDbPool.m_dbMems[index].uiThreadId = (int) Thread.currentThread().getId();// GetCurrentThreadId();
                    g_pQnmDbPool.m_dbMems[index].iDsnIndex = iDsnIndex;                    //  2006/12/22
                    g_pQnmDbPool.m_dbMems[index].iDbType = CONST_dbType_myDb;

                }

            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }


            iErr = 0;


            return iErr;

        }

        public static void freeDbPool(int iDsnIndex)  //  2006/12/22, 这个函数用来卸载某个数据源时用的
        {
            int i;


            //  if  (  !m_ucbInited  )  return;
            if (null == g_pQnmDbPool) return;

            for (i = 0; i < g_pQnmDbPool.m_dbMems.length; i++) {
                if (g_pQnmDbPool.m_dbMems[i].iDsnIndex != iDsnIndex) continue;
                if (null == g_pQnmDbPool.m_dbMems[i].pDb) continue;
                if (g_pQnmDbPool.m_dbMems[i].iDbType == CONST_dbType_myDb) {
                    //  不需要操作. 2012/07/14
                    ;
                } else {

                }
                //memset(g_pQnmDbPool->m_dbMems[i], 0, sizeof(g_pQnmDbPool->m_dbMems[i])  );
                g_pQnmDbPool.m_dbMems[i].pDb = null;
                g_pQnmDbPool.m_dbMems[i].iDbType = 0;

            }

            return;

        }


        public Object getAvailableDb(int iDsnIndex) {
            int index = -1;

            if (null != m_pDbMem) return m_pDbMem.pDb;

            //
            //  if  (  !m_g_dbMems  ||  m_g_iCnt_dbMems  <=  0  )  return  NULL;
            if (null == g_pQnmDbPool) return null;


            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (0 != syncObj.sync((CONST_mutexName_qnmDb), g_m_hMutex_forPool,"qnmDb.GetAvailDb.229")) return null;
                if(g_pQnmDbPool==null) return null;
//                for (index = 0; index < g_pQnmDbPool.m_g_iCnt_dbMems; index++) {
////                    if (null == g_pQnmDbPool.m_dbMems[index].pDb) continue;
////                    if (g_pQnmDbPool.m_dbMems[index].iDsnIndex != iDsnIndex) continue;
//                    //  if  (  m_dbMems[index].uiThreadId  !=  GetCurrentThreadId(  )  )  continue;
//                    //  Log.e(TAG,  "2007/05/09, CDatabase应该可以支持多线程的调用，只要有同步就可以了, 这里把对threadId的考虑暂时去掉了"  );
////                    if (!g_pQnmDbPool.m_dbMems[index].ucbUsed) break;
//                }

                if (index < g_pQnmDbPool.m_g_iCnt_dbMems) {
                    m_pDbMem = g_pQnmDbPool.m_dbMems[0];
                    m_pDbMem.ucbUsed = true;
                }
                syncObj.Dispose();

                //  

            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            if (null == m_pDbMem) {
                //Log.e(TAG,"CQnmDb::getAvailableDb failed, return  NULL.");
            }

            return m_pDbMem != null ? m_pDbMem.pDb : null;
        }


        void showQnmDbInfo() {
            int i = 0;
            //  for  (  i  =  0;  i  <  m_
        }


        @Override
        public void close() throws Exception {

        }
    }
}
