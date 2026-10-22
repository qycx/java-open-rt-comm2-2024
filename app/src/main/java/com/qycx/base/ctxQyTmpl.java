
package com.qycx.base;

public class ctxQyTmpl
{

	//  2015/02/08
	public static class Consts
	{
		public final static int CONST_ctxType_null = 0;
		//
		public final static int CONST_ctxType_qmc = 100;
		public final static int CONST_ctxType_dvt = 101;
		public final static int CONST_ctxType_evt = 102;
		//
		public final static int CONST_ctxType_qmd = 150;


	}



	public static class CCtxQyTmpl
	{

		public int m_iCtxType;   //上下文类型
		public String str_who_showInfo;

		//		
		public  boolean bGetLongProperty(int propId, long pVal) { return false; }
		public  boolean bSetLongProperty(int propId, long val) { return false; }
		//
		public  boolean bGetStrProperty(int propId, String tBuf, int tBufCnt) { return false; }
		public  boolean bSetStrProperty(int propId, String  val) { return false; }


		};

	}

