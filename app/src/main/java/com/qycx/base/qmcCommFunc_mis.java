



package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.cntof_talkerDesc;
import static com.qycx.base.qySyncCommProc.Consts.CONST_iAllocType_user;

import android.opengl.EGLContext;

public class qmcCommFunc_mis
{
public static class Consts
{
    public final static int CONST_maxTriesToStartSession_mis = 5;

    //
    public final static int CONST_iAllocType_msg = (CONST_iAllocType_user + 1);
    public final static int CONST_iAllocType_audio = (CONST_iAllocType_user + 2);
    public final static int CONST_iAllocType_video = (CONST_iAllocType_user + 3);
    public final static int CONST_iAllocType_longTimeMsg = (CONST_iAllocType_user + 4);

	//
	//  2014/09/03
	public final static int CONST_talkerObjType_null = 0;
	public final static int CONST_talkerObjType_dynBmp = 1;


}

	public static class P_sendAvInfo_taskInfo_i
	{
		public boolean ucbVideoConference;      //  2017/08/02
											 //
		public boolean ucbAvConsole;         //   2017/07/17
										  //
		public int usConfType;              //  2017/08/25

		//
		public EGLContext shared_eglContext;
	};

	public static class P_sendAvInfo_taskInfo_o
	{

		//
		public int iTaskId;
		//  
		public qyDefs.QY_MESSENGER_ID idInfo_starter = new qyDefs.QY_MESSENGER_ID();         //.ui64Id  =  rcd.idInfo_send.ui64Id;

		//
		public long tStartTime_org;          //  =  rcd.tSendTime;
		public int uiTranNo_org;          //  =  rcd.uiTranNo;
		public int uiContentType_org;     //  =  rcd.uiContentType;

		//
		public boolean ucbStarter;               //  =  pContent->myTask.ucbStarter;									//  2010/08/31
		public boolean ucbVideoConference;       //  =  pContent->myTask.ucbVideoConference;					//  2010/08/31

		//
		public int uiInitW;               //  =  pContent->tranInfo.video.vh_decompress.bih.biWidth;
		public int uiInitH;               //  =  pContent->tranInfo.video.vh_decompress.bih.biHeight;

		//  2014/09/25
		public int iIndex_taskInfo;        //  =  pContent->myTask.iIndex_taskInfo;

		//
		public qnmCommProc_mis.AV_stream_simple local_avStream = new qnmCommProc_mis.AV_stream_simple();


	};



	public static class P_sendAvInfo_taskInfo
	{

		//  i
		public P_sendAvInfo_taskInfo_i i = new P_sendAvInfo_taskInfo_i();

				 //  o
		public P_sendAvInfo_taskInfo_o o = new P_sendAvInfo_taskInfo_o();

}		 ;



	public static class TALKER_dynBmp_taskInfo_retrieveOne
	{
		int uiTranNo=0;
		//
		int dwLastTickCnt_retrieveOne;
	};

	public static class TALKER_dynBmp_taskInfo_srcInfo
	{
		int iW, iH;
		boolean ucbUnresizable;
	};


	public static class TALKER_dynBmp_taskInfo
	{
		public int iTaskId;
		//  2013/02/12
		public long tStartTran;
		public int uiTranNo;

		//  2013/02/24
		public long tStartTime_processed;

		//  2013/02/05
		//long										tLastTime_active;

		//
		public boolean bNeedProgress;

		//  2014/09/04
		public TALKER_dynBmp_taskInfo_retrieveOne retrieveOne;

		//  2015/10/23
		public TALKER_dynBmp_taskInfo_srcInfo srcInfo;

				 };


//  2013/02/12
public  static class TALKER_dynBmp
	{
		public int uiType1;

		public qnmCommProc_mis.DYN_BMP_RULE dynBmpRule;

		//				 
		public TALKER_dynBmp_taskInfo taskInfo;

}		 ;


	public static class ConfMem_confCtrl
	{
		public 		 boolean ucbPermitToSpeak;

		//
		public qyDefs.QY_MESSENGER_ID idInfo_starter = new qyDefs.QY_MESSENGER_ID();
		public long tStartTime;
		public int uiTranNo;
}
;


public static class qyFuncs
{
		public static boolean isVideoNormalState( qmcSharedObj.COMPRESS_VIDEO pCompressVideo)
		{
			return (0!=pCompressVideo.usFps_ok && pCompressVideo.bTopFrames && pCompressVideo.usFps_ok == pCompressVideo.usFrames_perSecond_expected);
		}

		public static  void M_getActiveMemDesc(qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, String desc, int descCnt)
		{
			String str;
			qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
			getTalkerDescParams.pMisCnt = pMisCnt;
			getTalkerDescParams.idInfo = pMisCnt.idInfo;
			getTalkerDescParams.talkerDesc = null;
			getTalkerDescParams.talkerDescCnt = 0;
			getTalkerDescParams.shortName = desc;
			getTalkerDescParams.shortNameCnt = descCnt;
			getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);
			str = String.format("%s(%s)",desc, pIdInfo.ui64Id);
			desc = str;
			return;
		}

		//
		public static long MACRO_getnWaitingInMs_audio_syncRecv(qmcCommFunc_is_open.PLAY_AUDIO_procInfo player_audio, long dwTickCnt_now  ) {
			return ((player_audio).m_var.uiSampleTimeInMs_newRecvd - (player_audio).m_var.uiSampleTimeInMs + (player_audio).m_var.dwTickCnt_uiSampleTimeInMs_syncRecv - dwTickCnt_now);
				
		}

	}
}
