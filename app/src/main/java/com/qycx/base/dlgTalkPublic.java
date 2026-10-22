package com.qycx.base;

public class dlgTalkPublic
{
	public static class DLG_talk_layout
	{

		//
		public int usCnt_row;                   //  
		public int usCnt_col;                   //  

		//
		public int uiW_pic;
		public int uiH_pic;

		//				
		public short usLayoutType_cur;            //  2010/12/29

		//
		public boolean ucbExists_localScreenSharing;         //  2015/11/14

		//
		public boolean ucbShowDlgDesc;
		public boolean ucbShowPeerDesc;
		public boolean ucbShowMeDesc;
		public boolean ucbShowOtherDesc;
		public boolean ucbShow_peer_tool;            //  2017/08/21

		//
		public int iH_dlgDesc;
		public int iW_memberList;
		public int iW_taskList;
		public int iW_ptz;
		public int iW_subtitles;               //  2013/04/05
		public int iW_offlineRes;              //  2015/08/01
											   //
		public int iW_peerInfo;                //  2017/08/21
		public int iH_staticPeerInfo;
		public int iH_peerInfo;
		public int iH_shareScreen;

		//
		public int iW_meDesc;                  //  2008/03/27
		public int iH_meDesc;

		//
		//  2017/11/03
		public boolean ucbShowBgWall;
		//

		public qyGuiCommProc.BG_wall_layout_cfg bgWallLayoutCfg;         //  2017/11/03
														   //
		public qyGuiCommProc.BG_wall_layout bgWallLayout;                //  2014/10/01
													//unsigned  short									usPollingIntervalInS1;		//  2014/10/06

	}
	;

	public  static class PARAM_dlgTalk_procTask
	{
		public boolean av_autoAnswer_av_task_bTaskNeedAutoAnswered;
		//
		public boolean bManualCmd;            //  2017/02/12
		public Object hParent;

		//
		public boolean bAutoCancel;
	}
	;

	//
	public  static class PARAM_startAvCall
	{
		public boolean ucbNoLocalAv;
		//
		public qyDefs.QY_MESSENGER_ID idInfo_initiator;
		//
	}
	;

}
