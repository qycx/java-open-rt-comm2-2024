



package com.qycx.base;

import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_mp3_enc;

public class dumpAudio
{
	public static class qyFuncs_Droid
	{
		public static boolean g_bInited_dumpA = false;



		public static guiShare.PF_dumpAudio_init dumpAudio_init=()->
		{
			if (g_bInited_dumpA) return -1;

			//
			guiShare.GuiShare.pf_dumpData1.call(null, 0, 0, 0, false, false, CONST_fn_dump_mp3_enc);

			//
			g_bInited_dumpA = true;

			return 0;
		};

		//
		public static guiShare.PF_dumpAudio_exit dumpAudio_exit=()->
		{
			if (!g_bInited_dumpA) return -1;
			g_bInited_dumpA = false;
			return 0;
		};


		public static guiShare.PF_dumpAudio dumpAudio=(byte[] data, int size)->
		{
			if (null == data || 0 == size)
			{
				return -1;
			}
			guiShare.GuiShare.pf_dumpData1.call(data, size, 0, 0, true, false, CONST_fn_dump_mp3_enc);
			return 0;
		};


	}
}

