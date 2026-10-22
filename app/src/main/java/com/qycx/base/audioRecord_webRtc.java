
package com.qycx.base;

public class audioRecord_webRtc
{
	public static class qyFuncs
	{
		//
		static boolean g_bInited_webRtc = false;

		public static int init_webRtc()
		{
			if (!g_bInited_webRtc)
			{
				g_bInited_webRtc = true;
			}

			return 0;
		}

		//
		public static int exit_webRtc()
		{
			if (g_bInited_webRtc)
			{
				g_bInited_webRtc = false;
			}

			return 0;
		}

	}
}
