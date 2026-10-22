
package com.qycx.base;


public class qyTmpMemory
{
    public static class qyFuncs
{
		public static genericQueue.PF_qElemRemove clean_myPLAY_AUDIO_DATA=(Object p_p, String hint)->
		{
			if (null==p_p) return;
//			myPlayAudioData.myPLAY_AUDIO_DATA p = (myPlayAudioData.myPLAY_AUDIO_DATA)p_p;
			((myPlayAudioData.myPLAY_AUDIO_DATA) p_p).memory = new qyMemoryFunc.QY_MEMORY();



			return;
		};

		//
		//  2015/02/12
		public static  void cleanEx_myPLAY_AUDIO_DATA(Object p_p, int size, String hint)
		{
			myPlayAudioData.myPLAY_AUDIO_DATA p[] =null;// (myPlayAudioData.myPLAY_AUDIO_DATA)p_p;

			int nPkts = size / p.length;

			//TCHAR tBuf[128] = "";
			//_sntprintf(tBuf, mycountof(tBuf), ("%s %s"), hint ? hint : "", ("Ex"));

			int i;
			for (i = 0; i < nPkts; i++)
			{
				Object p1 =p[i];
				clean_myPLAY_AUDIO_DATA.call(p1, hint);
			}
			return;
		}



		//
		public static genericQueue.PF_qElemRemove clean_myDRAW_VIDEO_DATA=(Object p_p, String hint)->
		{
			qnmCommProc_mis.myDRAW_VIDEO_DATA p = (qnmCommProc_mis.myDRAW_VIDEO_DATA)p_p;
			if (null==p) return;
			
		};

		//  2012/11/10
		public static genericQueue.PF_qElemRemoveEx cleanEx_myDRAW_VIDEO_DATA=(Object p_p, int size, String hint)->
		{
			if(p_p == null) return;
			qnmCommProc_mis.myDRAW_VIDEO_DATA p = (qnmCommProc_mis.myDRAW_VIDEO_DATA)p_p;
			int nPkts = 10;//sizeof(myDRAW_VIDEO_DATA);

//			int i;
//			for (i = 0; i < nPkts; i++)
//			{
//				Object pv =p[i];
				clean_myDRAW_VIDEO_DATA.call(p, hint);
//			}
			return;
		};

		//  2011/12/15. player.displayQ用到了
		public static genericQueue.PF_qElemRemove clean_MIS_MSG_procVideo_qmc=(Object p_p, String hint)->
		{
			qnmCommProc_mis.MIS_MSG_procVideo_qmc p = (qnmCommProc_mis.MIS_MSG_procVideo_qmc)p_p;
			if (null==p) return;
		};

		public static genericQueue.PF_qElemRemoveEx cleanEx_MIS_MSG_procVideo_qmc=(Object p_p, int size, String hint)->
		{
			qnmCommProc_mis.MIS_MSG_procVideo_qmc p[] =null;// (qnmCommProc_mis.MIS_MSG_procVideo_qmc)p_p;
			int nPkts = size / p.length;// sizeof(MIS_MSG_procVideo_qmc);

			//TCHAR tBuf[128] = "";
			//_sntprintf(tBuf, mycountof(tBuf), ("%s %s"), hint ? hint : "", ("Ex"));

			int i;
			for (i = 0; i < nPkts; i++)
			{
				qnmCommProc_mis.MIS_MSG_procVideo_qmc p1 =p[i];
				clean_MIS_MSG_procVideo_qmc.call(p1, hint);
			}
			return;
		};

	}
}
