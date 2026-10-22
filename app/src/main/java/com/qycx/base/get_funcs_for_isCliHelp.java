



package com.qycx.base;

public class get_funcs_for_isCliHelp
{
    public static class qyFuncs
{
		public static funcsForIsCliHelp.FUNCS_for_isCliHelp QY_GET_FUNCS_for_isCliHelp(ctxQmc.CCtxQmc pProcInfo)
		{
			//  (  funcsForIsCliHelp.FUNCS_for_isCliHelp  *  )(  (  (  MC_VAR_isCli  *  )  QY_GET_procInfo_isCli(  )  )->pFuncs_for_isCliHelp  )
			if (null==pProcInfo) return null;
			return (funcsForIsCliHelp.FUNCS_for_isCliHelp)pProcInfo.pFuncs_for_isCliHelp;
		}

	}
}
