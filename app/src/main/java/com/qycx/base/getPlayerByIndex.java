
package com.qycx.base;

public class getPlayerByIndex
{
    public static class qyFuncs
{

		public static qmcCommFunc_is_open.QY_PLAYER getPlayerByIndex(ctxQmc.CCtxQmc pProcInfo, int index)
		{
			if (index < 0 || index >= pProcInfo.av.usCnt_players) return null;
			//  if  (  !index  )  return  &pProcInfo->av.localAv.player;	//  2009/06/02
			return pProcInfo.av.pPlayers[index];
		}

	}
}
