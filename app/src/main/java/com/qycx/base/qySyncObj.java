
package com.qycx.base;

import com.qycx.base.util.Log;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class qySyncObj
{
	private static final Object lock=new Object();

	public static class qyFuncs
	{

		public static boolean bSyncQmObjQ(String mutexName, Lock hMutex, int uiTimeoutInMilliSeconds)
		{
			boolean iErr = false;
			if (hMutex == null)
			{
				return iErr;
			}
			try {
				synchronized (hMutex){
					hMutex.wait(uiTimeoutInMilliSeconds);
				}
				iErr=true;
			} catch (InterruptedException e) {
				iErr=false;
			}finally {

			}
			return iErr;
		}

		public static boolean bSyncQmObjQ(String mutexName, Semaphore hMutex, int uiTimeoutInMilliSeconds)
		{
			boolean bRet = false;
			if (hMutex == null)
			{
				return bRet;
			}
			try {
				bRet = hMutex.tryAcquire(1,uiTimeoutInMilliSeconds, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return bRet;
		}

		public static void cancelSyncQmObjQ(Semaphore hMutex)
		{

			if (hMutex != null)
			{
				hMutex.release();
			}
		}

	};


	public  static class SYNC_OBJ_PARAM
	{
		public long lpCriticalSection;
		public String mutexName;//[cntof_mutexName];
	}
	;


	public static class CQySyncObj implements AutoCloseable {
		//public static class  m_var 
		//{ 
		public long m_var_lpCriticalSection;
//		public Lock m_var_hMutex;
		public volatile Semaphore m_var_hMutex;
		public boolean m_var_bLocked;
		private volatile boolean IsDisposed = false;

		public CQySyncObj() { }

		public int sync(String mutexName, Semaphore hMutex, int uiMilliSeconds, String hint)
		{

			if (m_var_hMutex != null) {
				return -1;
			}
			if (!qyFuncs.bSyncQmObjQ(mutexName, hMutex, uiMilliSeconds)) {
				return -1;
			}
			m_var_hMutex = hMutex;

			//
			if  (  false  ) {
				Log.d("tn_msgInput", "syncObj.sync ok " + hint);
			}

			//
			return 0;
		}

		public int sync(String mutexName, Semaphore hMutex, String hint){
			return sync(mutexName,hMutex, 1000, hint);
		}

		public void Dispose()
		{
			Dispose(true);
			//GC.SuppressFinalize(this);
		}
		protected void Dispose(boolean Disposing)
		{
			if (!IsDisposed) {
				if (Disposing) {
					//Clean Up managed resources
				}
				//Clean up unmanaged resources  
				if (m_var_hMutex != null) {
					qyFuncs.cancelSyncQmObjQ(m_var_hMutex);
					//
					if  (  m_var_hMutex.availablePermits()  ==  2)  {
						Log.d("qy","CQySyncObj.Dispose error");
					}
					//
					m_var_hMutex = null;
				}

				//
				//Log.d("tn_msgInput",  "syncObj.Disposed");

			}
			IsDisposed = true;
		}

		@Override
		public void close() throws Exception {
			//
			if  (  true ) {
				//Log.d("qy", "CQySyncObj.close called");
			}
			//
			Dispose();
		}
	};

}
