package com.qycx.base;

import com.qycx.base.util.Log;

import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_pcm_enc;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.canSend;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

public class qmcSound_dx
{
	//
	public   static class myPLAY_SOUND_dx
	{
		public static class PlayADev_android
		{
			public Object m_audioTrack;
		}
		public PlayADev_android m_playDev_android = new PlayADev_android();

		//
		public int minBufferSize;

		//
		public int dwOffset_notify_unit;
		public int dwNotifyCount;
		//LPDSBPOSITIONNOTIFY lpdsbpns;

		//
		public int dwMyWriteCursor;  //		=  0xFFFFFFFF;


		public int uiMaxMsgWaiting;

		//
		public boolean bPlayInited;
		public boolean bPlayStarted;

	}
	;


	public static class qyFuncs_Droid
{
	public static  int processInput_post(ctxQmc.CCtxQmc pProcInfo, int nAvgBytesPerSec, byte[] pbInput1, int dwNumBytes, qnmCommProc_mis.MIS_MSGU pMsgBuf)
	{
		//showInfo_open.qyFuncs.showInfo_open("zzq ******* , processInput_post enter " );
		//
		int iErr = -1;


		//
		if  (  dwNumBytes  <=0  )  {
			Log.e("qy","processInput_post failed, dwNumBytes <=0");
			return  -1;
		}



		//
		boolean bHaveMsgrs_sendLocalAv = true;
		int  uiSampleTimeInMs = (int)(tmpFunc.qyFuncs.mytimeGetTime()  -  pProcInfo.tgt_start);
		if (0==nAvgBytesPerSec)
		{

		}
		else
		{
			int nDataInMs = (int)(dwNumBytes * 1000) / nAvgBytesPerSec;
			uiSampleTimeInMs -= (int)nDataInMs;

		}

		//
		pProcInfo.av.localAv.dwLastTickCnt_aCap  =  myGetTickCount(null);

		//
		if  (isMicrophoneMuted())  {
			return  -1;
		}

		//
		if  (  pProcInfo.bUse100k)  {
			if  (  pProcInfo.use100k_onlyV)  {
				return  -1;
			}
		}


		//
		{
				//qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
				{
					;

					qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = (qmcSharedObj.CAP_procInfo_audioCommon)pProcInfo.av.localAv.recordSoundProcInfo;

					qmcSharedObj.QY_SHARED_OBJ pSharedObj  =  getSharedObjByIndex(pProcInfo,pCapAudio.iIndex_sharedObj);
					if  (  pSharedObj  ==  null)  return  -1;

					boolean bSend=true;
					bSend =canSend(pSharedObj);
					if  (  !bSend)  {
						return -1;
					}


					//
					if  (  false  ) {
						String fn = "processInput_post.pcm";
						//GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
						guiShare.GuiShare.pf_dumpData1.call(pbInput1, (int)dwNumBytes, 0, 0, true, false, fn);

					}


					//
					myPlayAudioData.myPLAY_AUDIO_DATA p;
//					int len = sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);

					myPlayAudioData.myPLAY_AUDIO_DATA tmp_myPLAY_AUDIO_DATA = new myPlayAudioData.myPLAY_AUDIO_DATA();
					p =tmp_myPLAY_AUDIO_DATA; //  (  myPLAY_AUDIO_DATA  *  )m_var.pBuf;
					if (null==p) return iErr;
					//qyFuncs.mymemset(p, 0, len);

					//  2016/12/10
					int uiSize_dec = dwNumBytes;

					//  2011/12/06		
					int uiSize_enc = 0;   //  sizeof(  qnmCommProc_mis.TRANSFER_AUDIO_DATA  );

					//
					//  p->head.uiLen  =  dwNumBytes;
					p.head.uiLen = uiSize_dec + uiSize_enc;    //  2016/12/10
					p.uiSize_dec = uiSize_dec;                 //  2016/12/10
																//
					p.head.uiSampleTimeInMs = uiSampleTimeInMs;
					//  
//					if (null==mallocObj.mallocf((int)p.head.uiLen)) return iErr;
//					if (null == (p.memory = mallocObj.get())) return iErr;

					//
					byte[] tmp_audio  =  new byte[dwNumBytes];
					System.arraycopy(pbInput1,0,tmp_audio,0,dwNumBytes);
					//
					p.memory.m_pBuf = tmp_audio;//pbInput1;
					//memcpy(  p->memory.m_pBuf,  pbInput1,  p->head.uiLen  );
					//qyMemoryFunc.qyFuncs.mymemcpy(p.memory.m_pBuf,pbInput1, p.uiSize_dec);  //  2016/12/14

					//
					//guiShare.GuiShare.pf_dumpData1.call(p.memory.m_pBuf, (int) p.uiSize_dec, 0, 0, true, false, "dump_deced_pcm.pcm");


					//
					if (0!= q2PostMsgAndTrigger(p, pCapAudio.thread.q2,"processInput_post.100")) return iErr;
					//mallocObj.detach();
				}
		}

		if (bHaveMsgrs_sendLocalAv || pProcInfo.av.localAv.recordSoundProcInfo.bLocalAudioPlaying) pProcInfo.av.localAv.recordSoundProcInfo.dwTickCnt_sendLocalAv = myGetTickCount(null);
		else
		{
		}


		iErr = 0;

	

		return iErr;
	}

}
}
