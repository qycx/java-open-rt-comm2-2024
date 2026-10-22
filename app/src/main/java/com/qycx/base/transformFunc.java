



package com.qycx.base;
public class transformFunc
{
    public static class qyFuncs
{
	public static  boolean bCapAudioQuit(qmcSharedObj.CAP_procInfo_audioCommon pCAP_procInfo_audioCommon, Object p1, Object p2)
	{

		//
		qmcSharedObj.CAP_procInfo_audioCommon pCapProcInfo = (qmcSharedObj.CAP_procInfo_audioCommon)pCAP_procInfo_audioCommon;
		if (null==pCapProcInfo) return false;


		return pCapProcInfo.thread.bQuit;
	}

}
}
