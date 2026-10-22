



package com.qycx.base;
public class qyMtSock
{
	public  static class MT_SUBTHREADINFO
	{
		public int iServiceId;             //  2007/03/12
		public long hMutex;
		public Object pDb;
		public int cnt;
		public commonSock.SOCK_TIMEOUT pTo;
		public Object pParentParam;         //  指向全局或生成此线程的父对象
		public Object pCfg;                 //  指向线程的配置数据区
		public Object pThreadBuf;               //  指向线程的存储数据区
		public Object pTaskBuf;             //  指向一次任务的存储数据区
										   //  void					*			pLogHelp;
		public Object pLogCtx;
		//
		public int uiBufSize_pMsgBuff;        //  2015/09/30
		public Object pMsgBuff;             //  2015/09/04
	}
	;

 


}
