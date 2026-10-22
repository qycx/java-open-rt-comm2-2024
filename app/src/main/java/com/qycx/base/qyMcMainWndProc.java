



package com.qycx.base;

import java.lang.ref.WeakReference;

public class qyMcMainWndProc
{
 		//
		public static class NotifyTaskStatus_curNotify
        {
			public boolean bShown;
			public boolean bForegrounded;
			public WeakReference<Object> hParent;


		};



		//
		public  static class NOTIFY_task_status
        {
			public boolean bExists_task;
			public boolean bAvCall;
			//
			public int index_taskInfo;

			//
			public NotifyTaskStatus_curNotify curNotify=new NotifyTaskStatus_curNotify();

		};

		public  static class PROCESSING_avTask
        {
			public boolean bTaskExists;
			public int index_taskInfo;
			public int iTaskId;
        };

		//
		public static class QY_MC_mainWndVar
		{                               //  2010/04/13
			//CCtxQyMc pQyMc;                           //  2016/09/21
													//  void						*				pProcInfo;

			//
			public int uiTimerId;
				
			//
			public qnmCommProc_mis.MIS_MSGU pMsgBuf_msgArrive;                //  2009/12/10


			//WND_guiData_main guiData;

			//
			//
			public NOTIFY_task_status notifyTaskStatus = new NOTIFY_task_status();

			//
			public PROCESSING_avTask processingAvTask = new PROCESSING_avTask();

			//
			//unsigned char ucbVisible;                       //  2012/07/17

			public int loopCtrl;

			//

		//
		public ctxCaller.CTX_caller ctxCaller;

		}
		;

	public static class qyFuncs
	{

	}
}

