




//
package com.qycx.base;

public class ctxCaller
{
	public static class Consts
	{
		//
		public final static int CONST_ctxCallerType_null = 0;
		public final static int CONST_ctxCallerType_thread = 1;
		public final static int CONST_ctxCallerType_task = 2;

		//
		public final static int cntof_ctxCaller_tNameBuf = 16;
		public final static int cntof_ctxCaller_tHintBuf = 24;


		//
	}
	public  static class TMP_ctxCaller_tNameBuf
	{
		public String tNameBuf;//[cntof_ctxCaller_tNameBuf];
	}

	public  static class  TMP_ctxCaller_tHintBuf
    {
		public String tHintBuf;//[cntof_ctxCaller_tHintBuf];
    }




	//
	public static class CTX_caller
	{
		//
		public int							type;

		// thread
		public int							dwThreadId;
		public boolean							bTmp;

		//
		public boolean  m_bDbg;

		//
		public TMP_ctxCaller_tNameBuf		tNameBuf=new TMP_ctxCaller_tNameBuf();
		public TMP_ctxCaller_tHintBuf		tHintBuf=new TMP_ctxCaller_tHintBuf();

		//
		public long tick_before;
		///public qyShowInfoCommProc.Ctx_thread_tmpVar ctx_tmpvar;

	};





}
