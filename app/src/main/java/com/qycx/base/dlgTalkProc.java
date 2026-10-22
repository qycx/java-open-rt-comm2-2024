package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.askToRefreshLayout_mgr;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.dlgTalk_get_usLayoutType_local;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.findTalker;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.removeTalker;
import static com.qycx.base.dlgTalk_capImgProc.qyFuncs_Droid.dlgTalk_freeCapImageBySth_mgr;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.confStarter_requestToSpeak;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.isMemFromRequesting;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.refreshTaskAv_videoConferenceOthers;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.sendConfKey;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isMemFromActive;
import static com.qycx.base.dyn_isCliD3d.qyFuncs.dyn_d3dWall_refreshImgs;
import static com.qycx.base.getTalkerDesc.Consts.CONST_maxConfMgrMems;
import static com.qycx.base.getTalkerDesc.qyFuncs.getMsgDesc;
import static com.qycx.base.get_funcs_for_isCliHelp.qyFuncs.QY_GET_FUNCS_for_isCliHelp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confKey;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_htmlContent;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskInteractionReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.mcThreadProc_bgWall.qyFuncs_Droid.exitTalkerThread;
import static com.qycx.base.qmcCommFunc_mis.qyFuncs.M_getActiveMemDesc;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcVideoCapture.qyFuncs.setCurSharedObjUsr_localAv;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_permitToSpeak;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForOrgReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_stopSpeaking;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForTalkerShadow_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForWork;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talkingFriend_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_CONF_requesting_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_videoConferenceMem_mems;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_acceptedByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_applyToRecv;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_applyToSend;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_autoCanceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_dualByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_req;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_resp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToRecv;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToSend;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_curSharedObjUsrChanged_mgr;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmRc_ok;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;
import static com.qycx.base.sharedObjUsr.qyFuncs.freeSharedObjUsr;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.tmpFunc.qyFuncs.byte2bool;
import static com.qycx.base.tmpFunc.qyFuncs.mytime;

import android.opengl.EGLContext;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class dlgTalkProc {

    public static class PARAM_PROC_ROW {
        public int lRowIndex;
        //  unsigned  int					uiTranNo;
        public int iTaskId;
        public qyDefs.QY_MESSENGER_ID idInfo_talker;
        public int nItem;

        // 
        public boolean bScrollIntoView;                                   //  
        public int lColor;                                                //  

        public boolean bNewMsg;                                           //  2007/08/13

        public boolean ucbNeedAsk;                                           //  cancelTasks(  )
        public boolean ucbAsked;                                         //  
        public boolean bAutoCancel;                                       //  2014/07/30


        public boolean bMsgNeedRemoved;                                   //  2009/09/07

        //
        public int nMsgTalks_processed_inProcessQ;                        //  2012/05/20

        //
        public Object hListCtrl;                                         //  2017/06/25

        //  2017/08/08
        public boolean ucbNoMe;

    }

    ;


    public static class CONF_mgr_info {
        public short usCnt;
        public long mems_idInfo_ui64Id[] = new long[CONST_maxConfMgrMems];//[getTalkerDesc.Consts.CONST_maxConfMgrMems];//[];
    }

    ;


    //  2016/03/15
//  2014/08/04
    public class TALKER_threadProcInfo extends qnmCommProc_mis.QY_qThreadProcInfo_common {
        //
        Object hDlgTalk_mgr;

        Object pMgrVarParam;

    }

    ;


    public static class DlgTalkVar_av_taskInfo_mosaicTrans {
        public boolean bExists_confMosaic;
        //
        public int uiTranNo_openAvDev_confMosaic_video;
        public int uiTranNo_openAvDev_confMosaic_resource;

        //  2014/11/16
        //DLG_talk_mosaicTransStatus status;
    }

    ;

    public static class DlgTalkVar_av_taskInfo_confState {

        //
        public int uiTranNo_retrieveAll;
        public int usSeqNo_retrievePart;

        //
        public int dwTickCnt_sent;
        //
        public boolean ucbNeed_sendAll;

    }

    ;


    //
    public static class DlgTalkVar_av_taskInfo {
        public boolean bTaskExists;
        public boolean bPrepare;
        public int iTaskId;
        //
        public qmcMosaic.QMC_mosaic_maker mosaicMaker = new qmcMosaic.QMC_mosaic_maker();                                           //  2014/11/01
        //
        public DlgTalkVar_av_taskInfo_mosaicTrans mosaicTrans = new DlgTalkVar_av_taskInfo_mosaicTrans();

        //
        public qyDefs.QY_MESSENGER_ID idInfo_starter = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_initiator = new qyDefs.QY_MESSENGER_ID();
        public boolean ucbStarter;                                       //  2010/08/31
        //  2010/08/31
        public boolean ucbVideoConference;                               //  2010/08/31
        //
        public int usConfType;                                      //  2017/08/25

//
//INFO_consult_yz info_consult_yz;                                //  2017/08/27

        //
        public int uiInitW;
        public int uiInitH;

        public long tStartTime_org;                                  //  
        public int uiTranNo_org;                                  //
        //  unsigned  int									uiSerialNo_org;									//  
        public int uiContentType_org;

        //  					
        public qnmCommProc_mis.AV_stream_simple local_avStream = new qnmCommProc_mis.AV_stream_simple();                                    //  2015/02/17public

        //					
        public int iIndex_taskInfo;                                //  2014/09/25

//					
//public boolean bEnableToSpeak;                                    //  2010/12/29

        //
        public boolean bTaskInited;                                   //  2010/08/31

        //  
        public int usLayoutType_starter;                            //  2010/12/21

        //
        public int dwLastTickCnt_confStarter_sendReq_toStopSending;              //  2014/10/09

        //  2017/07/03
        public boolean ucbConfCtrl;

        //
        public qmcCommFunc_mis.ConfMem_confCtrl confMem_confCtrl = new qmcCommFunc_mis.ConfMem_confCtrl();

        //  2017/09/17
        public DlgTalkVar_av_taskInfo_confState confState = new DlgTalkVar_av_taskInfo_confState();


        //
        public boolean bUse_backCamera;
        public boolean bShow_localVideo;


        //
        public void memset0() {

            //this = new DlgTalkVar_av_taskInfo();

            bTaskExists=false;
            bPrepare=false;
            iTaskId =0;
            //
            //public qmcMosaic.QMC_mosaic_maker mosaicMaker = new qmcMosaic.QMC_mosaic_maker();                                           //  2014/11/01
            //
            //public DlgTalkVar_av_taskInfo_mosaicTrans mosaicTrans = new DlgTalkVar_av_taskInfo_mosaicTrans();

            //
            idInfo_starter.ui64Id=0;
            idInfo_initiator.ui64Id=0;
            ucbStarter=false;                                       //  2010/08/31
            //  2010/08/31
            ucbVideoConference=false;                               //  2010/08/31
            //
            usConfType  =  0;                                      //  2017/08/25

//
//INFO_consult_yz info_consult_yz;                                //  2017/08/27

            //
            uiInitW  =  0;
            uiInitH  =  0;

            tStartTime_org  =  0;                                  //
            uiTranNo_org  =  0;                                  //
            //  unsigned  int									uiSerialNo_org;									//
            uiContentType_org  =  0;

            //
            //public qnmCommProc_mis.AV_stream_simple local_avStream = new qnmCommProc_mis.AV_stream_simple();                                    //  2015/02/17public

            //
            iIndex_taskInfo  =  0;                                //  2014/09/25

//
//public boolean bEnableToSpeak;                                    //  2010/12/29

            //
            bTaskInited=false;                                   //  2010/08/31

            //
            usLayoutType_starter  =  0;                            //  2010/12/21

            //
            dwLastTickCnt_confStarter_sendReq_toStopSending  =  0;              //  2014/10/09

            //  2017/07/03
            ucbConfCtrl=false;

            //
            //public qmcCommFunc_mis.ConfMem_confCtrl confMem_confCtrl = new qmcCommFunc_mis.ConfMem_confCtrl();

            //  2017/09/17
            //public DlgTalkVar_av_taskInfo_confState confState = new DlgTalkVar_av_taskInfo_confState();


            //
            bUse_backCamera=false;
            bShow_localVideo=false;


        }
    }

    ;


    //
    public static class DlgTalkVar_av {
        //
        int idc_peer, idc_other, idc_me, idc_bgWall_conf;                  //  2013/02/24
        Object hDc_peer, hDc_other, hDc_me, hDc_bgWall_conf;                   //  2013/02/22

        public Object hDc_av_rect;               //  2009/12/28
        public int iIndex_sharedObj_localAv;               //  2009/12/28
        public int iIndex_usr_localAv;                     //  2010/09/06
        public boolean ucbGot_iIndex_sharedObj_localAv;      //  2009/08/16


        //  2016/03/15
        public syncMtCntDefs.SYNC_mt_cnt syncMtCnt_taskInfo;


//  2009/02/20

        public DlgTalkVar_av_taskInfo taskInfo = new DlgTalkVar_av_taskInfo();

        //
        public boolean bNeedDoTaskAv;

        //
        public boolean bNeedAvCall;
        //
        public boolean bAutoCancel_lostVideo;
        public long dwTickCnt_autoCancel_lostVideo;
        public boolean bDoneOnce_autoCancel_lostVideo;

        //
        public boolean m_bState_canSpeak;


        //
        public long dwTickCnt_closeTaskAv;   //  2017/10/03

        //  2019/02/23
        Object hWall;

        //
//	2017/08/30			 
        public qnmCommProc_mis.CONF_KEY videoConferenceStatus;


    }

    ;

    //
    public static class TALK_drawMem_param {
        public qnmCommProc_mis.PLAYER_ID playerId = new qnmCommProc_mis.PLAYER_ID();
    }

    public static class TALK_drawMem {
        public TALK_drawMem_param param = new TALK_drawMem_param();
        public int tn_modified;
    }

    public static class TALK_drawLayout_param {
        public TALK_drawMem me = new TALK_drawMem();
        public TALK_drawMem peer = new TALK_drawMem();
    }

    //
    public static class TALK_drawLayout {
        public TALK_drawLayout_param param = new TALK_drawLayout_param();
        //
        public int tn_modified;
    }

    ;


    //
    public static class DLG_TALK_var {
        public int currentSelectQiuJiChannel;
        public long currentSelectQiuJiIdInfo;
        public boolean is4k;
        public long tickCountOnCreate;
        /**/
        public int loopCtrl_timer;             //  2009/10/18


        //
        public qnmCommProc_mis.MSGR_ADDR addr = new qnmCommProc_mis.MSGR_ADDR();

        //
        public boolean bActivityStarted_toDoTask;
        public int toDoTask_usOp;


        //
        public boolean ucbGuest;                 //  			
        //
        public boolean ucbTmpMsgr;                   //  2012/05/23. 是否临时msgr, 是的话主要为非同分组成员.但是有授权的vieeviewDynBmp服务.
        //
        public String displayName;
        public String talkerDesc;
        //
        //WCHAR wLocation[64 + 1];        //  2007/12/23

        //
        public int iTalkerSubType;             //  2012/04/30

        //  		
        public Object hFore;                     //  2007/09/09, 


        //	
        //GENERIC_Q* m_pProcessQ;                 //  
        public List<TableItem.TableItem_dlgTalk_msg> m_pProcessQ = new ArrayList<>();

        //
        public Object pCapStuff;                                //  2009/08/19

        //  2012/05/20
        public int nMsgTalks_processed_inProcessQ;

        //  2013/02/26
        public dlgTalkPublic.DLG_talk_layout m_layout = new dlgTalkPublic.DLG_talk_layout();

        //

        public DlgTalkVar_av av = new DlgTalkVar_av();


        //				 
        //  2016/03/15
        public TALKER_threadProcInfo talkerThreadInfo;
        //  2009/02/22
        //public qmcTaskInfo.DLG_TALK_videoConference videoConference;

        //
        public qmShellPublic.DLG_guiData_talk guiData;                               //  2010/07/14


        //  2012/05/18
        public int dwTickCnt_start_noShadow;

        //
        public boolean bCancelTasks;                          //  2011/02/03

        //  2017/09/23
        public boolean bShouldQuit;


        //
        public qyGuiCommProc.DLG_syncFlgs syncFlgs = new qyGuiCommProc.DLG_syncFlgs();

        //
        public TALK_drawLayout m_drawLayout = new TALK_drawLayout();

        //
        public Object m_hTool_dlgTalk_av = null;
        public EGLContext m_share_eglContext;

        //
        public Object[] tmp_hTools_dlgTalk_av = new Object[3];
        public EGLContext[] tmp_share_eglContexts = new EGLContext[3];
        public int cnt_tmpTools_unused;

        //
        public long dwTickCnt_waitForOk_dlgTalk_av1 = 0;

        //
        //public boolean m_tool_bOrientation_landscape;

        //
        public boolean m_bNeedResumeTaskAv;


        //
        public DLG_TALK_var() {
            this.syncFlgs.lCnt_getDlgVar = new int[1];
            //

        }

    }

    ;

    public static class qyFuncs_Droid {

        //
        public static int dlgTalk_OnInitDialog(Object hDlgTalk, dlgTalkProc.DLG_TALK_var m_var) {
            // TODO: Add extra initialization here
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

//            Activity_dlgTalk hWnd = findTalker.call(pQyMc, pMisCnt, m_var.addr.idInfo);
//            if (null == ( )) {
//                Log.e( "dlgTalk_OnInitDialog failed, talker already exists");
//                return iErr;
//            }

            //  2013/07/22. 移到函数起始处
            qmCommFunc_mis.MIS_MSG_TALKINGFRIEND_QMC pMsg = new qmCommFunc_mis.MIS_MSG_TALKINGFRIEND_QMC();
            pMsg.uiType = CONST_misMsgType_talkingFriend_qmc;
            pMsg.addr = m_var.addr;
            pMsg.hWnd = hDlgTalk;
            pMisCnt.talkingFriendQ.add(pMsg);

            dlgTalk_loadRecentMsgs(hDlgTalk, m_var);

            iErr = 0;
            return iErr;
        }

        public static qyDefs.PF_commonHandler tmpHandler_loadRecentMsgs = (Object p0, Object p1, Object p2) -> {
            Activity_dlgTalk hDlgTalk = (Activity_dlgTalk) p0;
            //p1
            qnmCommProc_mis.IM_MSG_RCD pRcd = (qnmCommProc_mis.IM_MSG_RCD) p2;
            //p2

            //help_getDlgTalkVar.CHelp_getDlgTalkVar getVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            dlgTalkProc.DLG_TALK_var m_var = hDlgTalk.m_var;//getVar.getVar(hDlgTalk, "");
            if (m_var == null) return -1;

            if (pRcd.idInfo_logicalPeer.ui64Id == m_var.addr.idInfo.ui64Id) {
                guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, pRcd.idInfo_send, pRcd.tSendTime, pRcd.uiTranNo, 0, pRcd.uiType, new String(pRcd.logMsgContent), pRcd.iTaskId);
            }
            return 0;
        };


        //
        public static int dlgTalk_loadRecentMsgs(Object hDlgTalk, dlgTalkProc.DLG_TALK_var m_var) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            myDb.CMyDb myDb = (com.qycx.base.myDb.CMyDb) pQyMc.gui.pDb;

            genericQueue.qyFuncs.qTraverse(myDb.m_var_pQ_qyImMsgTab, tmpHandler_loadRecentMsgs, hDlgTalk, 0);
            return 0;
        }


        //
        public static int dlgTalk_OnDestroy(Object hDlgTalk, dlgTalkProc.DLG_TALK_var m_var) {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            removeTalker(pQyMc, hDlgTalk, 0);

            iErr = 0;

            return iErr;

        }

        //
        public static boolean isTalkerShadowMgr(qnmCommProc_mis.MSGR_ADDR addr) {
            return (0 == (addr).uiTranNo_shadow);
        }

        //  2012/05/14
        public static boolean isInvisibleMgr(dlgTalkProc.DLG_TALK_var m_var) {
            return (isTalkerShadowMgr((m_var).addr) && (m_var).guiData.param.bNeedNotShowWnd);
        }


        //
        public static int dlgTalk_closeTaskAv(Object hDlgTalk, dlgTalkProc.DLG_TALK_var m_var, String hint) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
            boolean bDbg = false;
            String str;

            //
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;


            if (null == hint) hint = "";

            try {


                //
                if (isTalkerShadowMgr(m_var.addr)) {
                    if (m_var.av.taskInfo.bTaskExists) {
                        m_var.av.taskInfo.bPrepare = true;
                    }
                }


                //  2016/03/15
                exitTalkerThread(pProcInfo, m_var.talkerThreadInfo);

                //  2009/08/16
                if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {

                    //  2017/09/11
                    if (isTalkerShadowMgr(m_var.addr)) {
                        get_conf_state.qyFuncs_Droid.get_conf_stat(hDlgTalk);
                    }

                    //  2016/03/28
                    int iIndex_sharedObj = m_var.av.iIndex_sharedObj_localAv;
                    if (qmcMasterSlave.qyFuncs.bExists_slave(pProcInfo, iIndex_sharedObj)) {
                        qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
                        if (pSharedObj != null) {
                            int iIndex_sharedObj_slave;
                            iIndex_sharedObj_slave = pSharedObj.var.master.slaveKey.iIndex_sharedObj;
                            freeSharedObjUsr(pProcInfo, iIndex_sharedObj_slave, hDlgTalk, CONST_qyWndContentType_talker);
                        }
                    }

                    //  askSharedObjToStop(  pProcInfo,  getSharedObjByIndex(  pProcInfo,  m_var.av.iIndex_sharedObj_localAv  ),  pQyMc->gui.hMainWnd  );
                    sharedObjUsr.qyFuncs.freeSharedObjUsr_localAv(pProcInfo, m_var.av.iIndex_sharedObj_localAv, hDlgTalk, CONST_qyWndContentType_talker);
                    m_var.av.ucbGot_iIndex_sharedObj_localAv = false;

                }
                //
                //freeSharedObjUsr(  pProcInfo,  


                //  2011/03/14

                Object hMgr = null;
                hMgr = hDlgTalk;
                /**/


                //
                //closeInstantAssistant(hDlgTalk);

                //  2014/11/01
                if (isTalkerShadowMgr(m_var.addr)) {
                    if (m_var.av.taskInfo.bTaskExists) {
                        //
                        qmcMosaic.qyFuncs.stopMosaicThreads(m_var.av.taskInfo.mosaicMaker);
                        //
                        shareDynBmp_closeTaskAv.qyFuncs.shareDynBmp_closeTaskAv(m_var.av.taskInfo.mosaicMaker.video);
                        shareDynBmp_closeTaskAv.qyFuncs.shareDynBmp_closeTaskAv(m_var.av.taskInfo.mosaicMaker.resource);
                        //
                        tmpProc.qyFuncs.removeTask(mainActivity.var_common.ctxCaller, m_var.av.taskInfo.iTaskId);
                        //
                        qmcTaskInfo.qyFuncs.qmcTaskInfo_setClosed(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "dlgTalk_closeTaskAv "  +  hint);
                    }
                }

                //memset(m_var.av.taskInfo, 0, sizeof(m_var.av.taskInfo));
                m_var.av.taskInfo.memset0();
                //  2017/10/03
                m_var.av.dwTickCnt_closeTaskAv = tmpFunc.qyFuncs.myGetTickCount(null);


                //  2017/09/06
                refreshTalkerList.qyFuncs.resetTalkerList_mgr(hDlgTalk);

                //  2017/10/02
                if (isTalkerShadowMgr(m_var.addr)) {
                }


                //  2010/09/03
                //  InvalidateRect(  hDlgTalk,  NULL,  true  );
                guiShare.GuiShare.pf_PostMessage.call(hMgr, CONST_qyWm_postComm, CONST_qyWmParam_curSharedObjUsrChanged_mgr, 0);


            } catch (Exception e) {
                Log.e("dlgTalk_closeTaskAv exception");
            }

            return 0;
        }

        //
        public static int dlgTalk_init_respInfo(Object hDlgTalk_mgr, DLG_TALK_var pMgrVar, boolean ucbVideoConference) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (!isTalkerShadowMgr(pMgrVar.addr)) return -1;
            if (!pMgrVar.av.ucbGot_iIndex_sharedObj_localAv) return -1;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, pMgrVar.av.iIndex_sharedObj_localAv);
            if (null == pSharedObj) return -1;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, pMgrVar.av.iIndex_usr_localAv);
            if (null == pSharedObjUsr) return -1;
            /**/

            return 0;

        }


        //
        public static int tmpHandler_cancelTask(Object hDlgTalkParam, Object p1, Object p2) {

            int iErr = -1;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            //MIS_MSGU  pMsgElem = (qnmCommProc_mis.MIS_MSGU )p2;
            TableItem.TableItem_dlgTalk_msg pMsgElem = (TableItem.TableItem_dlgTalk_msg) p2;

            //  2012/05/09
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk);
                if (null == pm_var) return -1;

                Object hMgr = hDlgTalk;
                //CHelp_getDlgTalkVar help_getDlgTalkVar_mgr;
                dlgTalkProc.DLG_TALK_var pMgrVar = pm_var;

                //
                //  DLG_TALK_var	&	m_var		=	*getDlgTalkVar(  hDlg  );
                //
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
                //TCHAR tBuf[256] = "";
                //char buf[256] = "";
                qySyncCommProc.CQyMalloc mallocObj;

                if (null == hDlgTalk || null == pParam || null == pMsgElem) return -1;
                myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
                if (null == pDbFuncs) return iErr;
                myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
                long tStartTran = tmpFunc.qyFuncs.mytime();
                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

                qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
                idInfo_dst.ui64Id = 0;
                qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
                idInfo_logicalPeer.ui64Id = 0;

                //
                //
                int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, pMsgElem.iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "tmpHandler_cancelTask");
                if (pTaskInfo == null) {
                    iErr = 0;
                    return iErr;
                }
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) {
                    iErr = 0;
                    return iErr;
                }

                //if (pMsgElem->uiType == CONST_misMsgType_task)
                //qnmCommProc_mis.MIS_MSG_TASK pMsg =pMsgElem->task;
                //
                qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                {
                    if (pMsg.uiType != CONST_misMsgType_task)
                        return iErr;

                    qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
                    int iStatus;
                    int lenInBytes;

                    if (pParam.iTaskId != 0) {
                        if (pMsg.iTaskId != pParam.iTaskId) {
                            iErr = 0;
                            return iErr;
                        }
                    }

                    Object pDb = null;
                    qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                    {
                        ;
                        if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                        pDb = db.m_pDbMem.pDb;

                        switch (pContent.uiType) {
                            case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                            case CONST_imCommType_transferFileReq:

                                //
                                switch (pMsg.iStatus) {
                                    case CONST_imTaskStatus_req:
                                    case CONST_imTaskStatus_applyToSend:
                                    case CONST_imTaskStatus_waitToSend:
                                    case CONST_imTaskStatus_acceptedByReceiver:
                                    case CONST_imTaskStatus_dualByReceiver: {

                                        iStatus = CONST_imTaskStatus_canceledBySender;
                                        g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                        pMsg.iStatus = iStatus;        //  2012/05/15

                                        //  °ÑÏÔÊ¾×´Ì¬ÐÞ¸ÄÏÂ.2008/11/14
                                        //qyFuncs.showTaskStatus(pMisCnt, pMgrVar.addr.idInfo, pMsg.idInfo_taskSender.ui64Id,pMsg->idInfo_taskReceiver, false, pContent->uiType, pMsg->iTaskId, iStatus, 0, 0, "", (""));

                                        //  Í¨Öª¶Ô·½
                                        qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
//                                        qyFuncs.mymemset( taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                        taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                        taskProcReq.usOp = CONST_imOp_send_cancel;
                                        taskProcReq.tStartTime_org = pMsg.tStartTime;
                                        taskProcReq.uiTranNo_org = pMsg.uiTranNo;
                                        taskProcReq.uiContentType_org = pContent.uiType;
                                        //
                                        lenInBytes = 128;//sizeof(TASK_PROC_REQ);


                                        int uiChannelType = CONST_channelType_talking;// pMsg->uiChannelType;
                                        //
                                        uiChannelType = 0;
                                        idInfo_dst.ui64Id = pMgrVar.addr.idInfo.ui64Id;
                                        idInfo_logicalPeer.ui64Id = pMgrVar.addr.idInfo.ui64Id;
                                        //
                                        //if (postMsgTask2Mgr_mc(pMisCnt, CONST_misMsgType_task, 0, pMsg->usCode, tStartTran, uiTranNo, 0, pMsg->iTaskId, pMsg->uiTaskType, (char)taskProcReq, lenInBytes,pMgrVar.addr.idInfo,pMsg->idInfo_taskSender,pMsg->idInfo_taskReceiver,pMsg->idInfo_taskReceiver, uiChannelType, NULL, false)) return iErr;

//                                        byte[] data = taskProcReq.toByteArray();
                                        int len = 0;
                                        if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsg.usCode, tStartTran, uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType, taskProcReq, len,
                                                idInfo_logicalPeer.ui64Id, 0, 0, idInfo_dst.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                                            return iErr;
                                    }
                                    break;
                                    case CONST_imTaskStatus_resp:
                                    case CONST_imTaskStatus_applyToRecv:
                                    case CONST_imTaskStatus_waitToRecv: {
                                        iStatus = pParam.bAutoCancel ? CONST_imTaskStatus_autoCanceledByReceiver : CONST_imTaskStatus_canceledByReceiver;

                                        g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                        pMsg.iStatus = iStatus;        //  2012/05/15

                                        qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
                                        taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                        taskProcReq.usOp = CONST_imOp_recv_cancel;
                                        taskProcReq.tStartTime_org = pMsg.tStartTime;
                                        taskProcReq.uiTranNo_org = pMsg.uiTranNo;
                                        taskProcReq.uiContentType_org = pContent.uiType;// pContent->uiType;
                                        //
                                        lenInBytes = 128;//sizeof(TASK_PROC_REQ);
                                        //  2015/09/08
                                        int uiChannelType = 0;// pMsg->uiChannelType;
                                        //
                                        uiChannelType = 0;
                                        idInfo_dst.ui64Id = pMsg.idInfo_taskSender.ui64Id;
                                        idInfo_logicalPeer.ui64Id = pMgrVar.addr.idInfo.ui64Id;

//                                        byte[] data = taskProcReq.toByteArray();
                                        int len = 0;
                                        if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsg.usCode, tStartTran, uiTranNo, (short) 0, pMsg.iTaskId, 0, taskProcReq, len,
                                                idInfo_logicalPeer.ui64Id, 0, 0, idInfo_dst.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                                            return iErr;
                                    }
                                    break;
                                    default:
                                        break;
                                }
                                //
                                if (pMgrVar.av.taskInfo.bTaskExists && pMgrVar.av.taskInfo.iTaskId == pMsg.iTaskId) {
                                    dlgTalk_closeTaskAv(hMgr, pMgrVar, "tmpHandler_cancelTask");
                                }
                                //
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            iErr = 0;


            return iErr;
        }


        //
        public static int dlgTalk_cancelTasks(Object hCurTalk, DLG_TALK_var pCurVar, int iTaskId, boolean bAutoCancel) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //
            //char idStr[CONST_qyMessengerIdStrLen + 1] = "";
            //char idStr_me[CONST_qyMessengerIdStrLen + 1] = "";
            int tmpiRet = -1;
            //  CListCtrl		*	pListCtrl									=	(  CListCtrl  *  )CWnd::FromHandle(  GetDlgItem(  hDlg,  m_var.idcList_task  )  );
            PARAM_PROC_ROW param;
            qySyncObj.CQySyncObj syncObj;

            if (null == pCurVar) return -1;

            //
            Object hMgr = hCurTalk;
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar_mgr;
            dlgTalkProc.DLG_TALK_var pMgrVar = pCurVar;

            //  2011/02/03
            if (pMgrVar.bCancelTasks) return -1;
            pMgrVar.bCancelTasks = true;

            //memset(param, 0, sizeof(param));
            param = new PARAM_PROC_ROW();
            if (!pQyMc.bQuit) param.ucbNeedAsk = true;

            //  2013/09/08
            //  2015/08/04
            boolean bNeedClose = false;

            //
            if (0 != iTaskId) {
                param.iTaskId = iTaskId;
                param.ucbNeedAsk = false;           //  
                param.bAutoCancel = bAutoCancel;    //  2014/07/30
            }
            //tmpiRet = qTraverse(m_processQ, (PF_commonHandler)tmpHandler_cancelTask, hCurTalk,param);
            for (var item : pMgrVar.m_pProcessQ) {
                tmpiRet = tmpHandler_cancelTask(hMgr, param, item);
                if (tmpiRet < 0) return iErr;

            }

            iErr = 0;


            pMgrVar.bCancelTasks = false;  //  2011/02/03

            return iErr;
        }

        public static boolean bReady_toShowMsg(Object hCurTalk) {
            boolean bRet = false;
            // 
            bRet = true;


            return bRet;

        }

        public static int activateWnd(Object hDlgTalk, dlgTalkProc.DLG_TALK_var m_var) {
            return 0;
        }


        public static int tmpHandler_showMsg_talk(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {
            int iErr = -1;

            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_TALK pMsg = (qnmCommProc_mis.MIS_MSG_TALK) pMsgParam;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;

            if (null == hDlgTalk || null == pParam || null == pMsg) return -1;

            qnmCommProc_mis.MIS_MSG_routeTalkData pTalkData = pMsg.data;
            qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pTalkData.buf;

            activateWnd(hDlgTalk, m_var);
            dlgTalkProc_help.qyFuncs_Droid.dlgTalk_set_newMsgArrives(hDlgTalk, m_var, true);

            switch (pContent.uiType) {
                case CONST_imCommType_htmlContent: {
                    guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, pMsg.data.route.idInfo_from, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usCode, pContent.uiType, ((qnmCommProc_mis.IM_HTML_CONTENT) pContent).wBuf, pMsg.iTaskId);
                }
                break;
                default:
                    break;
            }

            iErr = 0;

            return iErr;
        }


        //  2013/09/11
        public static int tmpHandler_showMsg_task(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {
            int iErr = -1;

            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pMsgParam;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;

            int i;
            //TCHAR tBuf[128] = "";

            //
            //CHelp_getDlgTalkVar help_getDlgTalkVar_mgr;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;

            String str;

            //
            switch (pMsg.uiType) {
                case CONST_misMsgType_task: {
                    qnmCommProc_mis.MIS_MSG_routeTalkData pTalkData = pMsg.data;
                    qnmCommProc_mis.IM_CONTENTU pContent = null;// (qnmCommProc_mis.IM_CONTENTU) pTalkData.buf;
                    qyDefs.QY_MESSENGER_ID idInfo_talker;
                    //
                    //char timeBuf[CONST_qyTimeLen + 1] = "";
                    //TASK_DISPLAY taskDisplay;
                    //char buf[255 + 1] = "";
                    int nItem;
                    int iStatus;

                    //
                    if (pContent.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                        str = getMsgDesc((qnmCommProc_mis.MIS_MSGU) pMsg);
                        guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, pMsg.data.route.idInfo_from, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usCode, pContent.uiType, str, pMsg.iTaskId);
                        break;

                    } else if (pContent.uiType == CONST_imCommType_transferFileReq) {
                        str = null;//getMsgDesc((qnmCommProc_mis.MIS_MSGU) pMsg);
                        guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, pMsg.data.route.idInfo_from, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usCode, pContent.uiType, str, pMsg.iTaskId);
                        break;

                    }


                }
                break;

                default:
                    break;
            }

            iErr = 0;


            //  2013/09/09	
            //dyn_d3dWall_refreshWallMemByTalker(hDlg);

            return iErr;

        }

        //
        public static int tmpHandler_showMsg_input_taskInteractionReq(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam,
                                                                      qnmCommProc_mis.IM_CONTENTU pContent) {
            int iErr = -1;
            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgParam;
            if (pMsg.uiType != CONST_misMsgType_input) {
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int i;
            //TCHAR tBuf[128];

            boolean bDbg = true;
            String str;


            if (!isTalkerShadowMgr(m_var.addr)) return -1;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;


            //
            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                ;
                //TCHAR mutexName[128];

                //
                if (0 != syncObj.sync("", pMisCnt.hMutex_syncActiveMem, 1000,"tmp_input_taskInteractionReq.1092")){
                    if (m_var.av.taskInfo.bTaskExists)
                    {
                        refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                    }
                    return iErr;
                }


                //
                switch (pContent.uiType) {
                    case CONST_imCommType_taskInteractionReq: {
                        if (!m_var.av.taskInfo.bTaskExists) {
                            syncObj.Dispose();
                            if (m_var.av.taskInfo.bTaskExists)
                            {
                                refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "tmpHandler_showMsg_input_taskInteractionReq");
                        if (null == pTaskInfo) {
                            syncObj.Dispose();
                            if (m_var.av.taskInfo.bTaskExists)
                            {
                                refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                        if (pTaskData == null) {
                            syncObj.Dispose();
                            if (m_var.av.taskInfo.bTaskExists)
                            {
                                refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        if (pTaskData.uiType != CONST_taskDataType_conf) {
                            syncObj.Dispose();
                            if (m_var.av.taskInfo.bTaskExists)
                            {
                                refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                        qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                        //qmcTaskInfo.DLG_TALK_videoConference pVc = pTc.videoConference;
                        boolean bBroadcasted = false;
                        qnmCommProc_mis.TASK_INTERACTION_REQ taskInteractionReq = (qnmCommProc_mis.TASK_INTERACTION_REQ) pContent;
                        if (taskInteractionReq.usOp == CONST_imOp_stopSpeaking) {
                            qyDefs.QY_MESSENGER_ID idInfo_speaker = new qyDefs.QY_MESSENGER_ID();
                            idInfo_speaker.ui64Id = 0;
                            qyDefs.QY_MESSENGER_ID idInfo_mgr = new qyDefs.QY_MESSENGER_ID();
                            idInfo_mgr.ui64Id = 0;
                            //
                            if (taskInteractionReq.avStream.idInfo.ui64Id == 0)
                                idInfo_speaker.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                            else {
                                idInfo_speaker.ui64Id = taskInteractionReq.avStream.idInfo.ui64Id;
                                idInfo_mgr.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                            }

                            //
                            boolean bActive = isMemFromActive(pTc, idInfo_speaker, taskInteractionReq.avStream.obj.resObj, 0,0);
                            if (bActive) {
                                confStarter_requestToSpeak(hDlgTalk, idInfo_speaker, taskInteractionReq.avStream.obj, "", false);
                                boolean bModified = false;
                                dlgTalk_refresh_usLayoutType_cur(hDlgTalk, bModified);
                                if (bModified) {
                                    askToRefreshLayout_mgr(hDlgTalk);
                                }

                            }
                            //

                            if (idInfo_mgr.ui64Id == 0)
                                str = idInfo_speaker.ui64Id + " request to stop speaking";
                            else
                                str = idInfo_mgr.ui64Id + " let " + idInfo_speaker.ui64Id + " stop speaking";
                            Log.d(str);

                            //
                            if (bActive && !isMemFromActive(pTc, idInfo_speaker, taskInteractionReq.avStream.obj.resObj, 0,0)) {
                                //Log.e("broadcast all mems info in group when %I64u stop speaking.", idInfo_speaker.ui64Id);
                                str = "broadcast all mems info in group when " + idInfo_speaker.ui64Id + " stop speaking.";
                                sendConfKey(hDlgTalk, m_var.addr.idInfo, str);
                                bBroadcasted = true;
                            }
                        } else if (taskInteractionReq.usOp == qnmCommProc_mis.Consts.CONST_imOp_requestToSpeak) {

                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("%I64u request to speak"), pMsg->task.data.route.idInfo_from.ui64Id);

                            //  2015/02/17
                            if (!m_var.av.taskInfo.bTaskExists
                                    || !m_var.av.taskInfo.ucbStarter) {
                                Log.e(("failed, not the conf starter"));
                                syncObj.Dispose();
                                if (m_var.av.taskInfo.bTaskExists)
                                {
                                    refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }

                            //  2015/07/30													
                            qyDefs.QY_MESSENGER_ID idInfo_speaker = new qyDefs.QY_MESSENGER_ID();
                            idInfo_speaker.ui64Id = 0;
                            qyDefs.QY_MESSENGER_ID idInfo_mgr = new qyDefs.QY_MESSENGER_ID();
                            idInfo_mgr.ui64Id = 0;
                            //
                            if (taskInteractionReq.avStream.idInfo.ui64Id == 0)
                                idInfo_speaker.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                            else {
                                idInfo_speaker.ui64Id = taskInteractionReq.avStream.idInfo.ui64Id;
                                idInfo_mgr.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                            }

                            //  first, add to the requestingMems
                            int index_active = 0;
                            if (!isMemFromActive(pTc, idInfo_speaker, taskInteractionReq.avStream.obj.resObj,
                                    taskInteractionReq.avStream.obj.tranInfo.audio.uiTranNo_openAvDev, taskInteractionReq.avStream.obj.tranInfo.video.uiTranNo_openAvDev ))
                            {
                                //
                                boolean ucbManual = pProcInfo.cfg.policy.ucbManullyPermitToSpeakOnVideoConference;
                                //
                                if (!isTalkerShadowMgr(m_var.addr)) {
                                    syncObj.Dispose();
                                    if (m_var.av.taskInfo.bTaskExists)
                                    {
                                        refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                                    }
                                    return iErr;
                                }
                                //TALKER_shadow_mgr pShadowMgr = (TALKER_shadow_mgr)m_var.pShadowInfo;
                                //if (!pShadowMgr) return iErr;
                                //
                                CONF_mgr_info confMgrInfo = new CONF_mgr_info(); // pShdowMgr->av.confMgrInfo

                                //
                                boolean bVal = false;
                                if (0 == getVal_bMsgrConfMgr(pMsg.data.route.idInfo_from, confMgrInfo, bVal) && bVal) {
                                    ucbManual = false;
                                }
                                //
                                if (ucbManual) {
                                    if (!isMemFromRequesting(pTc, pMsg.data.route.idInfo_from)) {
                                        if (0 == dlgTalk_videoConference.qyFuncs_Droid.addTo_requestingMems_from(pMisCnt, pTc, pMsg.data.route.idInfo_from)) {
                                            //  trigger the instantAssistant to refresh
                                            //
                                            refreshTalkerList.qyFuncs.resetTalkerList_mgr(hDlgTalk);
                                        }
                                    }
                                    //
                                } else {
                                    confStarter_requestToSpeak(hDlgTalk, idInfo_speaker, taskInteractionReq.avStream.obj, "", true)
                                    ;
                                }
                                //
                                //
                                if (0 != taskInteractionReq.avStream.obj.resObj.uiObjType) {
                                    boolean bModified = false;
                                    dlgTalk_refresh_usLayoutType_cur(hDlgTalk, bModified);
                                    if (bModified) {
                                        askToRefreshLayout_mgr(hDlgTalk);
                                    }
                                }
                                //
                                if (isMemFromActive(pTc, idInfo_speaker, taskInteractionReq.avStream.obj.resObj, 0,0)
                                        ||
                                        isMemFromRequesting(pTc, idInfo_speaker)     //  2015/07/30. 因为需要发给管理员来启停发言，所以，即使是手动允许，也要发送一遍
                                ) {
                                    str = String.format("broadcast all mems info in group when %d is speaking.", idInfo_speaker.ui64Id);
                                    //
                                    //
                                    sendConfKey(hDlgTalk, m_var.addr.idInfo, str);

                                    bBroadcasted = true;
                                }
                            } else {
                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActive = videoConference.activeMems_from[index_active];
                                if (taskInteractionReq.avStream.obj.tranInfo.audio.uiTranNo_openAvDev == 0
                                        && taskInteractionReq.avStream.obj.tranInfo.video.uiTranNo_openAvDev == 0) {
                                    Log.d(("requestToSpeak failed, a_tn and v_tn are 0"));
                                    syncObj.Dispose();
                                    if (m_var.av.taskInfo.bTaskExists)
                                    {
                                        refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                                    }
                                    return iErr;
                                }
                                if (pActive.avStream.obj.tranInfo.audio.uiTranNo_openAvDev != taskInteractionReq.avStream.obj.tranInfo.audio.uiTranNo_openAvDev
                                        || pActive.avStream.obj.tranInfo.video.uiTranNo_openAvDev != taskInteractionReq.avStream.obj.tranInfo.video.uiTranNo_openAvDev) {
                                    //  update avStream
                                    pActive.avStream.obj.tranInfo = taskInteractionReq.avStream.obj.tranInfo;
                                    //
                                    sendConfKey(hMgr, m_var.addr.idInfo, ("talk_msgTask_taskInteraction, update activeMem"));
                                    iErr = 0;
                                    syncObj.Dispose();
                                    if (m_var.av.taskInfo.bTaskExists)
                                    {
                                        refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);
                                    }
                                    return iErr;
                                }

                            }
                        }
                        //								
                        else if (taskInteractionReq.usOp == qnmCommProc_mis.Consts.CONST_imOp_prohibitSpeaking) {
                            i = 0;
                            //
                            pMgrVar.av.taskInfo.confMem_confCtrl.ucbPermitToSpeak = byte2bool((byte) 0);
                            //
                            pMgrVar.av.taskInfo.confMem_confCtrl.idInfo_starter.ui64Id = pMgrVar.av.taskInfo.idInfo_starter.ui64Id;
                            pMgrVar.av.taskInfo.confMem_confCtrl.tStartTime = pMgrVar.av.taskInfo.tStartTime_org;
                            pMgrVar.av.taskInfo.confMem_confCtrl.uiTranNo = pMgrVar.av.taskInfo.uiTranNo_org;
                            //
                            store_ConfMem_confCtrl(pMgrVar.av.taskInfo.confMem_confCtrl, pMgrVar.addr.idInfo);
                            //
                            dlgTalk_requestToSpeak(hMgr, false);
                            //
                            refreshTalkerList.qyFuncs.resetTalkerList_mgr(hMgr);
                            //
                            showNotification(0, pMsg.data.route.idInfo_from, null, 0, 0, 0, ("主持人停止了你的发言"))
                            ;
                            //
                            break;
                        } else if (taskInteractionReq.usOp == CONST_imOp_permitToSpeak) {
                            //
                            pMgrVar.av.taskInfo.confMem_confCtrl.ucbPermitToSpeak = true;
                            //
                            //
                            pMgrVar.av.taskInfo.confMem_confCtrl.idInfo_starter.ui64Id = pMgrVar.av.taskInfo.idInfo_starter.ui64Id;
                            pMgrVar.av.taskInfo.confMem_confCtrl.tStartTime = pMgrVar.av.taskInfo.tStartTime_org;
                            pMgrVar.av.taskInfo.confMem_confCtrl.uiTranNo = pMgrVar.av.taskInfo.uiTranNo_org;
                            //
                            store_ConfMem_confCtrl(pMgrVar.av.taskInfo.confMem_confCtrl, pMgrVar.addr.idInfo);
                            //
                            if (pProcInfo.cfg.policy.avRules.ucbLetConfMgrSetMicOn) {

                                //  2017/07/13
                                setCurSharedObjUsr_localAv(pProcInfo, pMgrVar.av.iIndex_sharedObj_localAv, hMgr);

                                dlgTalk_requestToSpeak(hMgr, true);
                            }
                            //
                            refreshTalkerList.qyFuncs.resetTalkerList_mgr(hMgr);
                            //
                            showNotification(0, pMsg.data.route.idInfo_from, null, 0, 0, 0, ("主持人请你发言"))
                            ;
                            //
                            break;
                        }
                        //
                        /**/
                        else {
                        }

                        //
                        if (!videoConference.bNeedRefresh_activeMems_from || !bBroadcasted) {
                            sendConfKey(hDlgTalk, pMsg.data.route.idInfo_from, "");
                            //
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("Send back all mems info to %I64u"), pMsg->task.data.route.idInfo_from.ui64Id);
                        }

                        //  2013/07/18
                        dyn_d3dWall_refreshImgs(hDlgTalk);

                    }
                    break;
                    default:
                        break;
                }
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            iErr = 0;


            //
            if (m_var.av.taskInfo.bTaskExists) {
                //chk_index_activeMems_from_decV(pProcInfo, m_var.av.taskInfo.iTaskId);
                //
                refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);

            }

            //
            return iErr;
        }

        //
        public static int tmpHandler_showMsg_input_confKey(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam,
                                                           qnmCommProc_mis.IM_CONTENTU pContent) {
            int iErr = -1;

            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgParam;
            if (pMsg.uiType != CONST_misMsgType_input) {
                Log.d("tmpHandler_showMsg_input_confKey failed, msg.type is not input");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //
            boolean bDbg = true;
            String str;

            //
            if (!isTalkerShadowMgr(m_var.addr)) return -1;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;

            //这个值拿到了哈     1就是 放大了    0就是没放大
            qnmCommProc_mis.CONF_KEY confKey = (qnmCommProc_mis.CONF_KEY) pContent;

            //
            int i, j;
            if  (  false  ) {
                int bAmplification = confKey.confLayoutParam.respVideoStatus.bAmplification;
                Log.d("qy", "初始视频状态：" + bAmplification);
            }

            //
            str = String.format("th_showMsg_input_confKey enters: talker%d: recv confKey from %s. tn %d", m_var.addr.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id,  pMsg.uiTranNo  );
            {
                for (i = 0; i < confKey.usCnt; i++) {
                    qnmCommProc_mis.VIDEO_CONFERENCE_MEM pMem = ((qnmCommProc_mis.CONF_KEY) pContent).buf_VIDEO_CONFERENCE_MEM_mems[i];
                    if  (  pMem.avStream.idInfo.ui64Id  ==0  )  continue;;
                    str  +=  " mem"  +  i  +  ":"  +  pMem.avStream.idInfo.ui64Id  +  " v "  +  pMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                }
            }
            showInfo_open.qyFuncs.showInfo_open(0,0,test.tag_100k,str);

            //
            test.testCls_Droid.testPrintTasks(true,  test.tag_100k,"th_showMsg_input_confKey before");

            //
            try {

                try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                    if (0 != syncObj.sync("", pMisCnt.hMutex_syncActiveMem, 1000,"tmp_input_confKey.1432")) return iErr;

                    //
                    switch (pContent.uiType) {
                        case CONST_imCommType_confKey: {
                            boolean bNeedRefresh_activeMems_from = false;  //  2010/09/12

                            str = String.format("showMsg_input_confKey: talker %d : recv confKey from %d", m_var.addr.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id);
                            Log.d("qy",str);

                            if (!m_var.av.taskInfo.bTaskExists) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            if (m_var.av.taskInfo.ucbStarter) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "tmpHandler_showMsg_input_confKey");
                            if (null == pTaskInfo) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                            if (pTaskData == null) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            if (pTaskData.uiType != CONST_taskDataType_conf) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                            int a = 0;
                            int bufSize = bufSize_videoConferenceMem_mems;
                            for (i = 0; i < confKey.usCnt; i++) {
                                qnmCommProc_mis.VIDEO_CONFERENCE_MEM pMem = ((qnmCommProc_mis.CONF_KEY) pContent).buf_VIDEO_CONFERENCE_MEM_mems[i];

                                if (pMem != null && pMem.avStream.idInfo.ui64Id == 0) break;
                                //if (0 == pContent_videoConferenceStatus_mems[i].ucbActive) break;
                                if (i >= (videoConference.activeMems_from.length)) break;

                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[a];
                                if (pMem != null && (pActiveMem_from.avStream.idInfo.ui64Id != pMem.avStream.idInfo.ui64Id
                                        || pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev != pMem.avStream.obj.tranInfo.audio.uiTranNo_openAvDev
                                        || pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev != pMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev)) {
                                    pActiveMem_from.avStream = pMem.avStream;
                                    //  getTalkerDesc(  m_var.pMisCnt,  &pVc.activeMems_from[i].mem.idInfo,  pVc.activeMems_from[i].desc,  mycountof(  pVc.activeMems_from[i].desc  ),  0,  0  );
                                    qyMcMainDaemon_isCli_mgr.TMP_tBuf128 tBuf_desc = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
                                    int cntof_desc = cntof_tBuf128;
                                    M_getActiveMemDesc(pMisCnt, pActiveMem_from.avStream.idInfo, tBuf_desc.tBuf, cntof_desc);
                                    pActiveMem_from.desc.desc = tBuf_desc.tBuf;

                                    bNeedRefresh_activeMems_from = true;            //  2010/09/12

                                    //
                                    str = String.format("Conference status changed, %d modified.", pActiveMem_from.avStream.idInfo.ui64Id);
                                    Log.d(str);
                                    showInfo_open.qyFuncs.showInfo_open( str);
                                }
                                a++;
                            }
                            //  j  =  i;
                            for (; a < (videoConference.activeMems_from.length); a++) {
                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[a];
                                if (0 != pActiveMem_from.avStream.idInfo.ui64Id) {
                                    pActiveMem_from.avStream.idInfo.ui64Id = 0;
                                    //
                                    bNeedRefresh_activeMems_from = true;            //  2010/09/12
                                }
                            }
                            //  i  =  j;

                            //
                            for (j = 0; j < confKey.requestingMems.usCnt; j++) {
                                bufSize = bufSize_CONF_requesting_mems;
                                qnmCommProc_mis.CONF_requesting_mem pMem = getMem.qyFuncs.getMem(((qnmCommProc_mis.CONF_KEY) pContent).requestingMems.mems, bufSize, j);
                                if (pMem == null) {
                                    syncObj.Dispose();
                                    return iErr;
                                }
                                if (pMem.idInfo.ui64Id == 0) break;
                                if (j >= (videoConference.requestingMems_from.length)) break;
                            }

                            if (!guiShare.GuiShare.pf_IsWindow.call(hDlgTalk)) {
                                syncObj.Dispose();
                                return iErr;
                            }
                            Activity_dlgTalk pDlgTalk = (Activity_dlgTalk) (hDlgTalk);
                            Log.d("pDlgTalk.respVideoStatusIsenlargement:" + confKey.confLayoutParam.respVideoStatus.bAmplification);
                            pDlgTalk.respVideoStatusIsenlargement = confKey.confLayoutParam.respVideoStatus.bAmplification;

                            //球机号选中的
                            int qiuji = confKey.confLayoutParam.ipcParam.iChannel;
                            long qiuji_ScaleId = confKey.confLayoutParam.enlargeParam.ui64Id;
                            //这里拿到那个ID号了哈
                            pDlgTalk.m_var.currentSelectQiuJiIdInfo = qiuji_ScaleId;
                            Log.d("##球机qiuji_ScaleId:" + qiuji_ScaleId);
                            pDlgTalk.m_var.currentSelectQiuJiChannel = qiuji;

                            ((Activity_dlgTalk_av) (pDlgTalk.m_var.m_hTool_dlgTalk_av)).m_fg_dlgTalk_av2.changeNvrControlStatus(qiuji);
                            Log.d("##球机号选中的" + qiuji);
                            int ii = 0;


                            //  2009/05/31
                            refreshTaskAv_videoConferenceOthers(hDlgTalk);

                            //
                            refreshTaskAvTexes(m_var.av.taskInfo.iTaskId);

                            if (bNeedRefresh_activeMems_from) {
                                dlgTalk_freeCapImageBySth_mgr(hDlgTalk, m_var.av.taskInfo.iTaskId, null);       //  2010/09/12
                            }

                            refreshTalkerList.qyFuncs.resetTalkerList_mgr(hDlgTalk);
                            m_var.av.taskInfo.usLayoutType_starter = confKey.usLayoutType_starter;

                            if (0 == dlgTalk_get_usLayoutType_local(hDlgTalk)) {
                                askToRefreshLayout_mgr(hDlgTalk);
                            }

                            break;
                        }
                        default:
                            break;
                    }
                    syncObj.Dispose();
                }

            } catch (Exception e) {
                Log.d("tmpHandler_showMsg_input_confKey, exception");
            }
            iErr = 0;

            //
            test.testCls_Droid.testPrintTasks(true,  test.tag_100k, "th_showMsg_input_confKey after");

            //
            str = String.format("tmpHandler_showMsg_input_confKey leaves: talker%d", m_var.addr.idInfo.ui64Id);
            Log.d("qy",str);

            return iErr;
        }


        public static int refreshTaskAvTexes(int iTaskId) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            int i;

            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
            if (taskData == null) return iErr;
            if (taskData.uiType != CONST_taskDataType_conf) return iErr;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) taskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
            if (videoConference == null) return iErr;


            //
            ctxQmc.TMP_task_texes ttt = pProcInfo.av.texes.taskAvTexes;

            if (videoConference.activeMems_from.length > ttt.tti_peers.length) {
                return iErr;
            }
            if (ttt.iTaskId != iTaskId) {
                ttt.iTaskId = iTaskId;
            }
            if (ttt.usCnt_activeMems != videoConference.usCntLimit_activeMems_from) {
                ttt.usCnt_activeMems = videoConference.usCntLimit_activeMems_from;
            }
            //
            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                if (ttt.tti_peers[i].idInfo.ui64Id != videoConference.activeMems_from[i].avStream.idInfo.ui64Id) {
                    ttt.tti_peers[i].idInfo = videoConference.activeMems_from[i].avStream.idInfo;
                }
                if (ttt.tti_peers[i].uiObjType != videoConference.activeMems_from[i].avStream.obj.resObj.uiObjType) {
                    ttt.tti_peers[i].uiObjType = videoConference.activeMems_from[i].avStream.obj.resObj.uiObjType;
                }
            }

            //
            iErr = 0;


            return iErr;
        }


        //
        public static int tmpHandler_showMsg_input_transferAvInfo(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam,
                                                                  qnmCommProc_mis.IM_CONTENTU pContent) {
            int iErr = -1;

            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgParam;
            if (pMsg.uiType != CONST_misMsgType_input) {
                Log.d("tmpHandler_showMsg_input_transferAvInfo failed, msg.type is not input");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //
            boolean bDbg = true;
            String str;

            //
            if (!isTalkerShadowMgr(m_var.addr)) return -1;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;


            //			
            int i, j;

            //
            str = String.format("talker%d: recv transferAvInfo from %s", m_var.addr.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id);
            Log.d(str);

            switch (pContent.uiType) {
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    //
                    qyDefs.QY_MESSENGER_ID idInfo_from  = new qyDefs.QY_MESSENGER_ID();
                    idInfo_from.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    //
                    test.testCls_Droid.testChk("th_showMsg_input_transferAvInfo.1672");

                    //
                    if (transferAvInfo.confCfg.idInfo_initiator.ui64Id != 0) {
                        idInfo_from.ui64Id = transferAvInfo.confCfg.idInfo_initiator.ui64Id;
                    }

                    //
                    test.testCls_Droid.testChk("th_showMsg_input_transferAvInfo.1676");

                    //
                    str = "视频呼叫";
                    //
                    guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, idInfo_from, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usCode, pContent.uiType, str, pMsg.iTaskId);

                    //
                    test.testCls_Droid.testChk("th_showMsg_input_transferAvInfo.1684");

                    //
                    if (pMgrVar.av.taskInfo.bTaskExists) break;//return iErr;
                    //
                    if (pMgrVar.m_hTool_dlgTalk_av == null) break;//return iErr;
                    Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) pMgrVar.m_hTool_dlgTalk_av;
                    //
                    dlgTalk_av.doTask_av(tmpResource.IDC_BUTTON_av_accept, pMsg.iTaskId);

                    //
                    break;
                default:
                    break;
            }


            iErr = 0;


            return iErr;
        }


        //
        public static int tmpHandler_showMsg_input_transferFileReq(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam,
                                                                   qnmCommProc_mis.IM_CONTENTU pContent) {
            int iErr = -1;

            Object hDlgTalk = (Object) hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgParam;
            if (pMsg.uiType != CONST_misMsgType_input) {
                Log.d("tmpHandler_showMsg_input_transferFileReq failed, msg.type is not input");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //
            boolean bDbg = true;
            String str;

            //
            if (!isTalkerShadowMgr(m_var.addr)) return -1;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;


            //			
            int i, j;

            //
            str = String.format("talker%d: recv transferFileReq from %s", m_var.addr.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id);
            Log.d(str);

            switch (pContent.uiType) {
                case CONST_imCommType_transferFileReq:
                    //
                    qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
                    idInfo_from = pMsg.data.route.idInfo_from;
                    //
//                    str = getMsgDesc((qnmCommProc_mis.MIS_MSGU) pMsg);
                    //
                    guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, idInfo_from, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usCode, pContent.uiType, str, pMsg.iTaskId);


                    //
                    break;
                default:
                    break;
            }


            iErr = 0;


            return iErr;
        }


        //  2014/09/05
        public static int tmpHandler_showMsg_input(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {
            int iErr = -1;

            Object hDlgTalk = hDlgTalkParam;

            PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int i;
            //TCHAR tBuf[128];

            boolean bDbg = true;
            String str;


            if (!isTalkerShadowMgr(m_var.addr)) return -1;
            Object hMgr = hDlgTalk;
            dlgTalkProc.DLG_TALK_var pMgrVar = m_var;


            switch (pMsg.uiType) {
                case CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                    qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(input.uiType, input.ucFlg, input.data);


                    switch (pContent.uiType) {
                        case CONST_imCommType_taskInteractionReq:

                            tmpHandler_showMsg_input_taskInteractionReq(hDlgTalkParam, m_var, p1, pMsgParam, pContent);

                            break;

                        case CONST_imCommType_confKey:

                            tmpHandler_showMsg_input_confKey(hDlgTalkParam, m_var, p1, pMsgParam, pContent);

                            break;

                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:

                            test.testCls_Droid.testChk("th_showMsg_input.1821");

                            tmpHandler_showMsg_input_transferAvInfo(hDlgTalkParam, m_var, p1, pMsgParam, pContent);

                            break;
                        case CONST_imCommType_transferFileReq:

                            tmpHandler_showMsg_input_transferFileReq(hDlgTalkParam, m_var, p1, pMsgParam, pContent);

                            break;

                        default:
                            break;
                    }

                    //
                    test.testCls_Droid.testChk("th_showMsg_input.1828");

                }
                break;

                default:
                    break;
            }

            iErr = 0;


            return iErr;

        }


        //
        public static int tmpHandler_showMsg_taskStatus(Object hDlgTalkParam, dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {
            int iErr = -1;
            qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;

            if (pMsg.uiType != CONST_misMsgType_taskStatus) return -1;

            qnmCommProc_mis.MIS_MSG_taskStatus pMsgTaskStatus = (qnmCommProc_mis.MIS_MSG_taskStatus) pMsg;            //  ÓÐÊ±¾ÍÓÃÕâ¸ö½á¹¹£¬ 2007/07/17

            //MIS_MSG_TASK pMsgTask = null;

            guiShare.GuiShare.pf_talk_displayTaskStatus.call(hDlgTalkParam, pMsgTaskStatus);
            return 0;

        }


        //
        public static int tmpHandler_showMsg(Object hDlgTalkParam, Object p1, Object pMsgParam) {

            int iErr = -1;
            Activity_dlgTalk hDlg = (Activity_dlgTalk)  hDlgTalkParam;
            boolean bDbg = false;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                ;
                dlgTalkProc.DLG_TALK_var pm_var = hDlg.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlg);
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;

                PARAM_PROC_ROW pParam = (PARAM_PROC_ROW) p1;
                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;

                long t = 0;
                int tmpiRet = 0;
                //
                //TCHAR tBuf[1024] = "";
                int i;
                //

                if (null == hDlg || null == pParam || null == pMsg) return -1;

                if (!isTalkerShadowMgr(m_var.addr)) return iErr;
                //TALKER_shadow_mgr pShadowMgr = (TALKER_shadow_mgr)m_var.pShadowInfo;
                //if (!pShadowMgr) return iErr;

                if (!bReady_toShowMsg(hDlg)) return iErr;

                t = mytime();

                switch (pMsg.uiType) {
                    case CONST_misMsgType_talk:
                        qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                        if (0 != talk.tStartTime_processed) break;
                        talk.tStartTime_processed = mytime();   //  2007/10/13
                        m_var.nMsgTalks_processed_inProcessQ++;             //  2012/05/20

                        pParam.bMsgNeedRemoved = true;     //  2009/10/18

                        //  2011/03/15
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("show msg_talk"));

                        //
                        if (0 != tmpHandler_showMsg_talk(hDlgTalkParam, m_var, p1, pMsgParam)) {

                            return iErr;
                        }


                        break;
                    case CONST_misMsgType_task:
                        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                        if (0 != task.tStartTime_processed) break;
                        task.tStartTime_processed = mytime();  //  2007/10/13, ´Ë¸³ÖµÊÇÎªÁË±ÜÃâÏûÏ¢±»ÖØ¸´´¦Àí¡£Ô­À´ÊÇÔÚ´ËcaseµÄ×îÏÂÃæ£¬ÏÖÔÚÒÆµ½ÕâÀï¡£ÊÇÒòÎª£¬Èç¹ûÖÐ¼ä´¦Àí³ö´í£¬break»ògoto µÄ»°£¬¾ÍÎÞ·¨¸³ÖµÁË£¬ÓÚÊÇ¾ÍÒªÒ»ÔÙÖØ¸´´¦Àí´ËÏûÏ¢£¬ÓÖÖØ¸´Ìø³ö£¬Ôì³ÉºóÐøÏûÏ¢ÎÞ·¨´¦ÀíµÄÏÖÏó¡£

                        //
                        if (0 == task.iTaskId)
                            pParam.bMsgNeedRemoved = true;        //  2009/10/18

                        //
                        if (0 != tmpHandler_showMsg_task(hDlgTalkParam, m_var, p1, pMsgParam))
                            return iErr;

                        break;

                    case CONST_misMsgType_input:    //  2014/09/05
                        qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                        if (0 != input.tStartTime_processed) break;
                        input.tStartTime_processed = mytime();

                        //
                        pParam.bMsgNeedRemoved = true;

                        //
                        test.testCls_Droid.testChk("tmpHandler_showMsg.1941");

                        //
                        if (0 != tmpHandler_showMsg_input(hDlgTalkParam, m_var, p1, pMsgParam)) {
                            //
                            test.testCls_Droid.testChk("th_showMsg.1946");
                            //
                            return iErr;
                        }

                        test.testCls_Droid.testChk("tmpHandler_showMsg.1951");

                        //
                        break;

                    case CONST_misMsgType_taskStatus: {
                        qnmCommProc_mis.MIS_MSG_taskStatus taskStatus = (qnmCommProc_mis.MIS_MSG_taskStatus) pMsg;
                        if (0 != taskStatus.tStartTime_processed) break;
                        taskStatus.tStartTime_processed = mytime(); //  2007/10/13,
                        pParam.bMsgNeedRemoved = true;     //  2009/10/18
                        if (0 != tmpHandler_showMsg_taskStatus(hDlgTalkParam, m_var, p1, pMsgParam))
                            return iErr;
                    }
                    break;

                    case qnmCommProc_mis.Consts.CONST_misMsgType_notifyTaskEnd_qmc:
                        /**/
                        break;

                    case CONST_misMsgType_applyForWork:

                        break;

                    case CONST_misMsgType_applyForTalkerShadow_qmc: {  //  2012/04/23

                    }
                    break;
                    default:
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("tmpHandler_showMsg failed, unprocessed msgType %d"), pMsg->uiType);
                        return iErr;
                }

            }

            iErr = 0;
            return iErr;
        }

        public static int getVal_bMsgrConfMgr(qyDefs.QY_MESSENGER_ID idInfo, CONF_mgr_info pConfMgrInfo, boolean pbRet) {
            int iErr = -1;
            boolean bRet = false;

            qyDefs.QY_MESSENGER_ID pIdInfo = idInfo;

            if (null == pIdInfo || pIdInfo.ui64Id == 0) return -1;
            //if (!pConfMgrInfo) return -1;

            int i;
            //
            for (i = 0; i < pConfMgrInfo.usCnt; i++) {
//                if (pConfMgrInfo.mems_idInfo_ui64Id[i] == pIdInfo.ui64Id)
                {
                    bRet = true;
                    break;
                }
            }


            iErr = 0;


            if (0 == iErr) {
                if (pbRet) pbRet = bRet;
            }

            return iErr;
        }

        public static int store_ConfMem_confCtrl(qmcCommFunc_mis.ConfMem_confCtrl pConfCtrl, qyDefs.QY_MESSENGER_ID pIdInfo) {
            int iErr = -1;

            iErr = 0;


            return iErr;
        }

        public static int dlgTalk_displayAvStatus(Object hDlg, dlgTalkProc.DLG_TALK_var m_var, int iTaskId, int iStatus, String statusDesc) {
            return 0;
        }

        public static int chkResources(Object hDlgTalk, boolean bFreeCapImages) {

            return 0;
        }

        //
        public static int dlgTalk_OnQyComm(Object hTalk, int wParam, qnmCommProc_mis.MIS_MSGU lParam) {
            int iErr = -1;

            if (hTalk == null) return -1;
            Activity_dlgTalk hDlg;
            if (hTalk instanceof WeakReference) {
                hDlg = ((WeakReference<Activity_dlgTalk>) hTalk).get();
            } else {
                hDlg = (Activity_dlgTalk) hTalk;
            }
            dlgTalkProc.DLG_TALK_var m_var = hDlg.m_var;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            //
            qnmCommProc_mis.MIS_MSGU pComm = (qnmCommProc_mis.MIS_MSGU) lParam;
            //TCHAR tBuf[512] = "";

            //
            if (null == pComm) return -1;


            //
            //
            switch (pComm.uiType) {


                case CONST_misMsgType_taskStatus:
                    /**/
                    break;
                case CONST_misMsgType_task: {
                    qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pComm;
                    qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsg.uiType, pMsg.ucFlg, pMsg.data);
                    int i;
                    //
                    //
                }
                break;
                case CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pComm;
                    qnmCommProc_mis.IM_CONTENTU pContent = null;
                    int i;
                    //
                    if (isUcFlgRouteTalkData(pMsg.ucFlg) || qyComm.qyFuncs.isUcFlgTalkData(pMsg.ucFlg))
                        pContent = (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
                    else pContent = null;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data;
                    //
                    switch (pContent.uiType) {
                        case CONST_imCommType_taskProcReq:
                            if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForOrgReq) {
                                qySyncCommProc.CQyMalloc malloc_pMsgTask_o = new qySyncCommProc.CQyMalloc();
                                {
                                    ;
                                    int size = 128;//sizeof(qnmCommProc_mis.MIS_MSG_TASK);
                                    qnmCommProc_mis.CMIS_MSGU pMsgTask_o = new qnmCommProc_mis.CMIS_MSGU();

                                    int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, pMsg.iTaskId);
                                    if (index_taskInfo < 0) return iErr;
                                    qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "dlgTalk_onQyComm");
                                    if (pTaskInfo == null) return iErr;
                                    qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                                    if (pTaskData == null) return iErr;

                                    //
                                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask_i = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                                    {
                                        ;// findTaskInProcessQ(m_var, pMsg->iTaskId);
                                        if (null == pMsgTask_i) return iErr;
                                        if (pMsgTask_i.uiType != CONST_misMsgType_task) return iErr;

                                        qnmCommProc_mis.IM_CONTENTU pContentTask_i = pMsgTask_i.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgTask_i.data.buf;
                                        if (pContentTask_i.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                            return iErr;
                                        //
                                        if (true) {   //  2012/05/25
                                            boolean bPermitted = false;

                                            if (m_var.addr.uiObjType == CONST_objType_imGrp) {
                                                //  要检查是否是组成员
                                                /**/
                                                bPermitted = true;
                                            } else {
                                                if (pMsg.data.route.idInfo_from.ui64Id == m_var.addr.idInfo.ui64Id) {
                                                    bPermitted = true;
                                                }
                                            }
                                            //
                                            if (!bPermitted) {
                                                return iErr;
                                            }
                                        }
                                        //
                                        if (m_var.addr.uiObjType != CONST_objType_imGrp
                                                && pTaskInfo.var.m_bReplyTask) {
                                            //
                                            if (dlgTalk_canSpeak(hTalk)) {
                                                dlgTalk_requestToSpeak(hTalk, true);
                                            }

                                            break;
                                        }
                                        //
                                        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                                        //memset(param, 0, sizeof(param));
                                        param.pMsgTask_o = pMsgTask_o;
                                        if (pProcInfo.processQ_media.qTraverse(mainActivity.var_common.ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsgTask_i, param) <= 0) {
                                            return iErr;
                                        }

                                        if (pMsgTask_o.msgU.uiType != CONST_misMsgType_task) {

                                            return iErr;
                                        }
                                        qnmCommProc_mis.IM_CONTENTU pContentTask_o = ((qnmCommProc_mis.MIS_MSG_TASK)pMsgTask_o.msgU).data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask_o.data.buf;
                                        if (pContentTask_o.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                            return iErr;

                                        //
                                        int lenInBytes = (int) ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).lenInBytes;// System.Runtime.InteropServices.;
                                        if (lenInBytes < 0) return iErr;
                                        //
                                        qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
                                        idInfo_dst.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                                        //  set idInfo_logicalPeer temporiary.
                                        if (m_var.addr.idInfo.ui64Id != idInfo_dst.ui64Id) {
                                            ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentTask_o).idInfo_logicalPeer.ui64Id = m_var.addr.idInfo.ui64Id;
                                        }
                                        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU;
                                        qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
                                        postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(
                                                mainActivity.var_common.ctxCaller,
                                                pMisCnt,
                                                CONST_misMsgType_outputTask,
                                                (byte) 0, task.usCode,
                                                task.tStartTime, task.uiTranNo,
                                                (short) 0,
                                                task.iTaskId,
                                                task.uiTaskType,
                                                task.data.buf,
                                                (int) lenInBytes,
                                                idInfo_logicalPeer.ui64Id,
                                                task.idInfo_taskSender.ui64Id,
                                                task.idInfo_taskReceiver.ui64Id,
                                                idInfo_dst.ui64Id,
                                                0,
                                                (qnmCommProc_mis.MIS_MSGU) null, false);
//
                                        if (isTalkerShadowMgr(m_var.addr)) {
                                            if (m_var.av.taskInfo.bTaskExists
                                                    && m_var.av.taskInfo.iTaskId == pMsg.iTaskId) {
                                                sendConfKey(hDlg, idInfo_dst, ("send orgReq"));
                                            }
                                        }

                                        //
                                        String str;
                                        str = String.format("dlgTalk: send transferAvInfo again to %d. tn %d", idInfo_dst.ui64Id, ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).uiTranNo);
                                        showInfo_open.qyFuncs.showInfo_open(str);

                                        //
                                        break;
                                    }
                                }
                            }
                            //  2014/10/09
                            /**/
                            break;
                        case CONST_imCommType_taskProcResp:
                            /**/
                            break;
                        default:
                            break;
                    }
                    break;
                }
                default:
                    return iErr;
            }


            iErr = CONST_qyWmRc_ok;


            return iErr;

        }

        //
        public static int dlgTalk_refresh_usLayoutType_cur(Object hDlgTalk, boolean pbModified) {
            int iErr = -1;

            return iErr;
        }


        //
        public static void dlgTalk_myTimerProc(Activity_dlgTalk hDlgTalk, int uMsg, int idEvent, int dwTime) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return;
            }
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hDlgTalk;

            int len = 0;

            boolean bDbg = false;
            String str;

            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                ;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk, ("dlgTalk_mytimeProc"));
                if (null == pm_var) {
                    return;
                }
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
                if (null == pProcInfo) return;

                long dwTickCnt_start_myTimerProc = tmpFunc.qyFuncs.myGetTickCount(null);


                m_var.loopCtrl_timer++;


                //
                if (isTalkerShadowMgr(m_var.addr)) {
                    if (pProcInfo.cfg.debugStatusInfo.ucbShowDlgTalkStatus) {
                        long dwTickCnt_end_myTimerProc = tmpFunc.qyFuncs.myGetTickCount(null);
                        long iDiffInMs = (dwTickCnt_end_myTimerProc - dwTickCnt_start_myTimerProc);
                        str = String.format("dlgTalk_myTimeProc: talker%d, nElapseInMs %sms", m_var.addr.idInfo.ui64Id, iDiffInMs);
                        Log.d(str);
                    }
                }
            }
        }


    }

}





