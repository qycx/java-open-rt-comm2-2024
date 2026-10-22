package com.qycx.base;

import static com.qycx.base.qmcFunc_android.qyFuncs_Droid.stopQThread_android;

public class playVideo_android
{
	public static class qyFuncs_Droid
{


		public static guiShare.PF_startPlayVideo_android startPlayVideo_android=(qmcCommFunc_is_open.QY_PLAYER pPlayerParam)->
		{
			int iErr = -1;
			qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER)pPlayerParam;
			qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo = pPlayer.video;
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

			iErr = 0;

			return iErr;
		};

		public static guiShare.PF_stopPlayVideo_android stopPlayVideo_android=(qmcCommFunc_is_open.QY_PLAYER pPlayerParam)->
		{
			int iErr = -1;
			qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER)pPlayerParam;
			qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo = pPlayer.video;

			String str;

			//
			String pName = pPlayer.video.q2.cfg.name;
			{
				str = String.format("stopPlayVideo_android called, %s", new String(pName));
			}

			stopQThread_android(pPlayVideo, "");

			iErr = 0;
			return iErr;
		};
	}
}
