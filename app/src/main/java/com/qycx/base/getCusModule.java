



package com.qycx.base;
public class getCusModule
{
   public static class qyFuncs
{
		//
		public static qyMcMainCusModule.CUS_MODULE_compress getCusModule(qyMcMainCusModule.CUS_MODULES pModules, int uiModuleType, int uiVDevId, int uiDevType,
																		 String wDevIdStr)
		{
			int index = 0;
			//QY_MC			*	pQyMc	=	(  QY_MC  *  )pQyMcParam;
			qyMcMainCusModule.CUS_MODULE_compress pModule = null;

			if (null==pModules) return null;
			if (null == pModules.mems) return null;

			//
			for (index = 0; index < pModules.mems.length; index++)
			{
				pModule = pModules.mems[index];
				//
				if (pModule.common.uiType != uiModuleType) continue;

				//
				break;
			}

			if (index == pModules.mems.length) return null;

			return pModule;
		}

	}
}
