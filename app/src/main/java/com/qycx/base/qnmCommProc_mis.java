
package com.qycx.base;

import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isProtocolFunc.qyFuncs.commAuthInfo2Stream_mis;
import static com.qycx.base.isProtocolFunc.qyFuncs.data2Stream;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_byte;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_l64;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_lData;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_long;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_short;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_str;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_wStr;
import static com.qycx.base.isProtocolFunc.qyFuncs.tmp_htonAvStream;
import static com.qycx.base.isProtocolFunc.qyFuncs.tmp_ntohAvStream;
import static com.qycx.base.qnmCommProc.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_contact;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_grp;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxContactUpdateInfos_grpMem;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxMisServNameLen;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxTransferAvDataMems;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_usTaskProcObjType_vStream;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_ipcInfos;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_AUDIO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_VIDEO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_imGrpInfo_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_imGrpMem_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_imLStream;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_ipcInfos;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_retrieveContaceMem_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_logMsgContent;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_videoConferenceMems;
import static com.qycx.base.qyComm.*;
import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;
import static com.qycx.base.qyCommProc.Consts.*;
import static com.qycx.base.qyCommonFunc9.qyFuncs.*;
import static com.qycx.base.qyCustom.qyFuncs.qnmVerStr;
import static com.qycx.base.qyDefs.Consts.CONST_objType_android;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_rsa1;
import static com.qycx.base.qyMcMainDaemon_isCli.Consts.*;
import static com.qycx.base.qyMsgFunc.qyFuncs.qyRecvMsg;
import static com.qycx.base.qyMsgFunc.qyFuncs.qyTellService_msg;
import static com.qycx.base.qyWmComm.Consts.*;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.tmpFunc.qyFuncs.mylstrlen;

import static com.qycx.base.tmpFunc.qyFuncs.safeStrnCpy;

import android.opengl.EGLContext;

import com.qycx.base.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class qnmCommProc_mis {

    public static class Consts {
        public final static int CONST_misMsgType_null = 0;
        public final static int CONST_misMsgType_base = CONST_qyWmParam_misMsg_base;
        public final static int CONST_misMsgType_resp_onlySock = (CONST_misMsgType_base + 1);  //
        public final static int CONST_misMsgType_quit = (CONST_misMsgType_base + 2);
        public final static int CONST_misMsgType_closeSession = (CONST_misMsgType_base + 3);
        public final static int CONST_misMsgType_input = (CONST_misMsgType_base + 4);   //服务器端到客户端：
        public final static int CONST_misMsgType_send = (CONST_misMsgType_base + 5);   //
        public final static int CONST_misMsgType_processSelf = (CONST_misMsgType_base + 6);    //
        public final static int CONST_misMsgType_processBt = (CONST_misMsgType_base + 7);  //  2011/04/18
        //public final static int		CONST_misMsgType_processSelf_sendFinished			(  CONST_misMsgType_base  +  8	)	//  2015/08/18
        public final static int CONST_misMsgType_processSelf_new = (CONST_misMsgType_base + 9);    //  2015/08/21
        public final static int CONST_misMsgType_inputArrive = (CONST_misMsgType_base + 10);   //  2015/08/24
        //public final static int		CONST_misMsgType_resp1								(  CONST_misMsgType_base  +  11	 )	//  2015/08/25. 暂时用来替换resp,以帮助发现未加密的resp
        public final static int CONST_misMsgType_resp2 = (CONST_misMsgType_base + 12); //  2015/08/25. resp的处理标识已经是网络字节流顺序，resp2表明不是网络顺序


        //
        //  public final static int		CONST_misMsgType_resumeToSend					(  CONST_misMsgType_base  +  16  )	//
        //
        public final static int CONST_misMsgType_req = (CONST_misMsgType_base + 30);   //  messenger·
        public final static int CONST_misMsgType_resp = (CONST_misMsgType_base + 31);  //  iocp·
        public final static int CONST_misMsgType_talk = (CONST_misMsgType_base + 32);  //
        public final static int CONST_misMsgType_task = (CONST_misMsgType_base + 33);  //
        //
        //
        public final static int CONST_misMsgType_isSendOk = (CONST_misMsgType_base + 50);  //  iocp iocp tranNo. messenger outputQ iocp,tranNo starttime
        public final static int CONST_misMsgType_sendOk = (CONST_misMsgType_base + 51);    //  MGR tranNo. messenger iocp, tranNoºÍstarttime
        //  mgr·
        //
        public final static int CONST_misMsgType_sendFinished = (CONST_misMsgType_base + 52);  //  mgr
        //
        //  public final static int		CONST_misMsgType_closeChannel					(  CONST_misMsgType_base  +  53  )	//  2007/12/20, QMemMessenge
        public final static int CONST_misMsgType_closeChannelByCliIndex = (CONST_misMsgType_base + 53);    //  2007/12/20,
        public final static int CONST_misMsgType_chkChannelByType_mgr = (CONST_misMsgType_base + 54);  //  2015/08/26,
        //
        public final static int CONST_misMsgType_applyForWork = (CONST_misMsgType_base + 55);  //
        //////
        public final static int CONST_misMsgType_taskStatus = (CONST_misMsgType_base + 60);    //
        public final static int CONST_misMsgType_imMsgRcd = (CONST_misMsgType_base + 61);  //
        public final static int CONST_misMsgType_event = (CONST_misMsgType_base + 62); //

        //  ÒÔÉÏÊÇ°üÀàÐÍ
        public final static int CONST_misMsgType_talkingFriend_qmc = (CONST_misMsgType_base + 70); //	talkingFriendQ
        public final static int CONST_misMsgType_recentFriend_qmc = (CONST_misMsgType_base + 71);  //	recentFriendQ
        public final static int CONST_misMsgType_displayedElem_qmc = (CONST_misMsgType_base + 72); //
        public final static int CONST_misMsgType_refreshContactsStatus_qmc = (CONST_misMsgType_base + 73); //  messenger display
        public final static int CONST_misMsgType_refreshMeStatus_qmc = (CONST_misMsgType_base + 74);   //  diplay
        public final static int CONST_misMsgType_applyForPlayer_qmc = (CONST_misMsgType_base + 75);    //
        public final static int CONST_misMsgType_applyForChkChannels_qmc = (CONST_misMsgType_base + 76);   //
        public final static int CONST_misMsgType_applyForRemovingInvalidTasks_qmc = (CONST_misMsgType_base + 77);  //  2009/09/10
        public final static int CONST_misMsgType_applyForTalkerShadow_qmc = (CONST_misMsgType_base + 78);  //  2012/04/23. 由墙发起,请求dlgTalk生成一个影子窗口

        //
        public final static int CONST_misMsgType_procVideo_qmc = (CONST_misMsgType_base + 80); //  2009/03/24
        public final static int CONST_misMsgType_notifyTaskEnd_qmc = (CONST_misMsgType_base + 81); //  2009/04/28
        public final static int CONST_misMsgType_procGps_qmc = (CONST_misMsgType_base + 82);   //  2012/04/20
        //
        public final static int CONST_misMsgType_getDaemonStatus = (CONST_misMsgType_base + 85);   //  2011/01/12
        //
        public final static int CONST_misMsgType_outputTask = (CONST_misMsgType_base + 90);    //  robot
        public final static int CONST_misMsgType_outputTask_toMix = (CONST_misMsgType_base + 91);  //  2011/01/23


        //  2008/05/26
        public final static int CONST_misMsgType_grp = (CONST_misMsgType_base + 200);  //  ×éºÏ°ü¡£×éºÏ°üÓÐÊ±ºÜ´ó.²»Òª½«MIS_MSGUºÍ×éºÏ°ü»ìÆðÀ´¡£·ñÔò£¬Ó°ÏìÍ¨³£µÄMIS_MSGUµÄÄÚ´æ·ÖÅä¡£

        public final static int CONST_maxContactUpdateInfos_grp = 128;                                         //  2014/02/06
        public final static int CONST_maxContactUpdateInfos_grpMem = 2048;                                     //  2014/02/06
        public final static int CONST_maxContactUpdateInfos_contact = 128 * 3;                                 //  2014/02/09

        public final static int CONST_maxMessengerNameLen = 255;
        public final static int CONST_maxMessengerPasswdLen = 255;

        public final static int CONST_maxMisServNameLen = 32;                  //  2010/08/05

        //
        public final static int CONST_qyMessengerIdStrLen = 18;                    //
        //
        public final static long CONST_invalidMessengerId = -1;                   //  2007/07/28

        public final static int CONST_cntof_wRndName_serv = 255 + 1;
        public final static int cntof_talkerDesc = 32;
        public final static int cntof_displayName = 20;
        public final static int CONST_cntof_clientWelcomMsg = 128;
        public final static int cntof_hint = 255 + 1;

        //
        public final static int CONST_maxMsgrs_sendTo = 32;                    //

        public final static int MAX_mems_taskAv = 512;                 //
        //  public final static int 		MAX_mems_taskAv										32					//
        //  public final static int 		MAX_activeMems_taskAv_xp							4					//
        //
        public final static int MAX_speakers_taskAv = 25;              //  16;                   //

        //
        public final static int DEFAULT_usMaxSpeakers = 9;


        //
        public final static int MAX_mosaicMems_taskAv = 2;             //1;
        public final static int MAX_resourceMems_taskAv_conf = 4;
        //
        public final static int MAX_resourceMems_taskAv_p2p = 2;

        //
        public final static int MAX_conf_activeMems = MAX_speakers_taskAv + MAX_mosaicMems_taskAv + MAX_resourceMems_taskAv_conf;
        //
        public final static int MAX_conf_requestingMems = 10;                   //


        //
        public final static int MAX_activeMems_from = MAX_conf_activeMems;//20;


        //  public final static int 		CONST_maxTaskAvMixerPlayers							32					//  2009/02/16
        //public final static int CONST_maxTransferAvDataMems = 32;                  //  2009/02/16
        public final static int CONST_maxTransferAvDataMems = 160;     //  32;                  //  2009/02/16


        //  public final static int 			DEFAULT_mems_taskAv									128
        public final static int DEFAULT_mems_taskAv = 256;             //  2012/02/21

        //public final static int 		DEFAULT_usCntLimit_activeMems_from					3					//
        public final static int DEFAULT_usCntLimit_activeMems_from = 4;                    //  2014/12/05

        //
        public final static int MAX_usCntLimit_activeMems_from_3d_win32 = 4;


        //
        public final static int bufSize_imDataContent = 63 * 1024;
        public final static int bufSize_imStreamContent = 63 * 1024;                     //  2008/03/22,
        //public final static int bufSize_imLStream = (900 + 32) * 1024;       //  2008/03/22, 这个包应该比别的包略大一点，所以+32
        public final static int bufSize_imLStream = (300 + 32) * 1024;       //  2008/03/22, 这个包应该比别的包略大一点，所以+32


        //
        public final static int bufSize_talkData = bufSize_imLStream + 2 * 1024;//pc 2443304;
        public final static int bufSize_routeTalkData = bufSize_imLStream + 2 * 1024;


        //
        //  public final static int 		bufSize_transferFileDataResp				32  *  1024
        //  public final static int 		bufSize_transferFileDataResp				64  *  1024						//  2010/12/05
        public final static int bufSize_transferFileDataResp = 128 * 1024;                   //  2014/07/13
        //
        public final static int bufSize_transferVideoData = 900 * 1024;                  //  2012/06/28. mediaSdk压缩出来的一个包就可能600k.cuda压缩出来的一个包可能700多k
        public final static int bufSize_transferAudioData = 512 * 1024;                  //  2009/05/20

        //  public final static int 		bufSize_myDrawVideoData					8  *  1024  *  1024				//  2009/05/03
        public final static int bufSize_myDrawVideoData_2k = 8 * 1024 * 1024;                //  2009/05/03
        public final static int bufSize_myDrawVideoData_4k = 36 * 1024 * 1024;           //  2014/06/01. 4096X2304, 3840X2160

        public final static int bufSize_myPlayAudioData1 = 48000 * 2 / 10; //100ms //1 * 1024 * 1024;

        //
        public final static int cntof_imHtmlContent_wBuf = 16 * 1024;

        //
        public final static int CONST_maxContactUpdateInfos_025909 = 10;                                           //

        //
        public final static int CONST_sizeof_imGrpInfo = 248;
        //IM_GRP_INFO mems[CONST_maxContactUpdateInfos_grp];          //  ·Ç½áÊø°ü
        public final static int bufSize_imGrpInfo_mems = CONST_sizeof_imGrpInfo * CONST_maxContactUpdateInfos_grp;
        //
        public final static int CONST_sizeof_imGrpMem = 128;
        //IM_GRP_MEM mems[CONST_maxContactUpdateInfos_grpMem];            //  ·Ç½áÊø°ü
        public final static int bufSize_imGrpMem_mems = CONST_sizeof_imGrpMem * CONST_maxContactUpdateInfos_grpMem;

        //
        public final static int CONST_sizeof_retrieveContaceMem = 976;
        //RETRIEVE_contact_mem mems[CONST_maxContactUpdateInfos_contact];         //  ·Ç½áÊø°ü
        public final static int bufSize_retrieveContaceMem_mems = CONST_sizeof_retrieveContaceMem * CONST_maxContactUpdateInfos_contact;

        //
        public final static int CONST_maxNameLen = 64;

        //
        public final static int MAX_imObjRule_reqMems = 10;
        //
        public final static int DEFAULT_maxImObjRule_reqMems = 10;                 //  当req.usMaxMemsPerSnd为0时，取此值. 不能修改. 2011/11/13


        //
        public final static int CONST_sizeof_refreshImObjRuleReqMem = 280;
        //REFRESH_imObjRule_reqMem mems[MAX_imObjRule_reqMems];
        public final static int bufSize_REFRESH_imObjRule_reqMem_mems = CONST_sizeof_refreshImObjRuleReqMem * MAX_imObjRule_reqMems;

        public final static int CONST_sizeof_TRANSFER_VIDEO_dataMemHead = 56;
        //TRANSFER_VIDEO_dataMemHead mems[CONST_maxTransferAvDataMems];
        public final static int bufSize_TRANSFER_VIDEO_dataMemHead_mems = CONST_sizeof_TRANSFER_VIDEO_dataMemHead * CONST_maxTransferAvDataMems;

        public final static int CONST_sizeof_TRANSFER_AUDIO_dataMemHead = 48;
        //TRANSFER_AUDIO_dataMemHead mems[CONST_maxTransferAvDataMems];
        public final static int bufSize_TRANSFER_AUDIO_dataMemHead_mems = CONST_sizeof_TRANSFER_AUDIO_dataMemHead * CONST_maxTransferAvDataMems;

        //
        public final static int sizeof_videoConferenceMem = 4720;
        public final static int bufSize_videoConferenceMem_mems = sizeof_videoConferenceMem * (MAX_conf_activeMems);
        public final static int cntof_videoConferenceMems = MAX_conf_activeMems;

        //
        public final static int sizeof_CONF_requesting_mem = 80;
        public final static int bufSize_CONF_requesting_mems = sizeof_CONF_requesting_mem * MAX_conf_requestingMems;


        //
        public final static int MAX_ipcInfos = 128;
        public final static int sizeof_ipcInfo = 320;
        public final static int bufSize_ipcInfos = sizeof_ipcInfo * MAX_ipcInfos;


        public final static int cntof_name = 128;
        public final static int cntof_messengerPasswd = 255;
        public final static int cntof_ip = 16;

        //
        //
        public final static int CONST_pkts_playVideo = 20;                                     //  处理线程中一次性读取的最大包数，要保证下面的具体线程中的取值小于此数. 2011/12/06
        //
        public final static int CONST_pkts_vpp = 20;
        public final static int CONST_pkts_encVideo = 20;
        public final static int CONST_pkts_toShareVideo_post = 20;
        //
        public final static int CONST_pMsgs_dispatchVideo = 20;
        //
        public final static int CONST_pMsgs_preTransformVideo = 10;
        public final static int CONST_pkts_preTransformVideo = (CONST_maxTransferAvDataMems + CONST_pkts_playVideo);
        public final static int CONST_pkts_out_preTransformVideo = 20;

        public final static int CONST_pkts_decVideo = 20;
        public final static int CONST_pkts_out_decVideo = 20;

        //
        public final static int CONST_pMsgs_transformVideo = 1;
        public final static int CONST_pkts_transformVideo = (CONST_maxTransferAvDataMems + CONST_pkts_playVideo);


        //  2015/02/13
        public final static int CONST_pkts_decAudio = 20;
        public final static int CONST_pkts_encAudio = 20;


        //
        public final static int CONST_sizeof_myDRAW_VIDEO_DATA = 280;
        //sizeof(myDRAW_VIDEO_DATA) * CONST_pkts_playVideo;
        public final static int bufSize_myDRAW_VIDEO_DATA_pkts_playVideo = (CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_playVideo);

        public final static int bufSize_myDRAW_VIDEO_DATA_pkts_toShareVideo_post = CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_toShareVideo_post;
        public final static int bufSize_myDRAW_VIDEO_DATA_pkts_encVideo = CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_encVideo;
        public final static int bufSize_myDRAW_VIDEO_DATA_pkts_vpp = CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_vpp;
        //public final static int bufSize_myDRAW_VIDEO_DATA_pkts_preTransformVideo = CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_preTransformVideo;
        //public final static int bufSize_myDRAW_VIDEO_DATA_pkts_decVideo = CONST_sizeof_myDRAW_VIDEO_DATA * CONST_pkts_decVideo;

        //
        public final static int CONST_sizeof_myPLAY_AUDIO_DATA = 104;
        public final static int bufSize_myPLAY_AUDIO_DATA_pkts_encAudio = CONST_sizeof_myPLAY_AUDIO_DATA * CONST_pkts_encAudio;

        public final static int CONST_enlargeType_null = 0;
        public final static int CONST_enlargeType_img = 1;
        //
        //  2015/08/02
        public final static int CONST_procOfflineResSubtype_null = 0;
        public final static int CONST_procOfflineResSubtype_getCfgs = 1;
        public final static int CONST_procOfflineResSubtype_retrieve = 2;
        public final static int CONST_procOfflineResSubtype_upload = 3;
        public final static int CONST_procOfflineResSubtype_download = 4;
        public final static int CONST_procOfflineResSubtype_del = 5;

        //
        public final static int CONST_transferAvInfo_subtype_null = 0;
        public final static int CONST_transferAvInfo_subtype_task = 2;
        //
        public final static int CONST_transferAvReplyInfo_subtype_task = 3;

        //
        //  2015/03/12
        public final static int CONST_avFlg_null = 0x0;
        public final static int CONST_avFlg_unresizable = 0x1;

        //
        //  2014/05/31
        public final static int CONST_usIndex_screen0 = 1;
        public final static int CONST_usIndex_webcam0 = 1;
        public final static int CONST_usIndex_webcam1 = 2;
        public final static int CONST_usIndex_webcam2 = 3;
        //
        public final static int CONST_usIndex_avStream_slave = 2;                                  //  2014/09/28
        //
        public final static int CONST_usIndex_photomosaic_video = 3;                                   //  2014/11/91
        public final static int CONST_usIndex_photomosaic_resource = 4;                                    //  2014/11/01

        //
        public final static int CONST_usTaskProcObjType_null = 0;
        public final static int CONST_usTaskProcObjType_vStream = 1;

        //
        //
        public final static int CONST_imOp_null = 0;
        public final static int CONST_imOp_says = 1;       //
        public final static int CONST_imOp_tryToSendFile = 2;      //
        //
        public final static int CONST_imOp_recv_accept = 101;      //
        public final static int CONST_imOp_recv_deny = 102;        //

        public final static int CONST_imOp_recvFile_saveas = 103;
        //
        public final static int CONST_imOp_recv_cancel = 104;      //
        public final static int CONST_imOp_send_cancel = 106;      //
        public final static int CONST_imOp_send_permit = 107;      //
        //
        public final static int CONST_imOp_dual_permit = 120;      //  2008/05/29
        //
        public final static int CONST_imOp_recv_applyForOrgReq = 123;      //  2009/10/18
        public final static int CONST_imOp_recv_applyForChkTaskAlive = 124;        //  2009/10/19
        public final static int CONST_imOp_recv_applyForKeyFrame = 125;        //  2014/08/26

        //
        public final static int CONST_imOp_requestToSpeak = 130;       //  2009/02/21
        public final static int CONST_imOp_stopSpeaking = 131;     //  2009/02/21.
        //
        public final static int CONST_imOp_permitToSpeak = 132;        //  2017/07/07
        public final static int CONST_imOp_prohibitSpeaking = 133;     //  2017/07/07.
        //
        public final static int CONST_imOp_requestToControlDesktop_yz = 134;       //  2017/08/26
        public final static int CONST_imOp_stopControlDesktop_yz = 135;

        public final static int CONST_imOp_sendStreamInfo = 141;

        //
        public final static int CONST_imOp_requestToSend = 180;        //  2014/10/09
        public final static int CONST_imOp_stopSending = 181;        //  2014/10/09

        //
        public final static int CONST_imOp_new = 190;      //  2017/07/29
        public final static int CONST_imOp_refresh = 191;
        public final static int CONST_imOp_del = 192;
        public final static int CONST_imOp_modify = 193;       //  2018/11/20
        //


        //
        public final static int CONST_imOp_enlargeImg = 200;
        public final static int CONST_imOp_enlargeChannel = 201;
        public final static int CONST_imOp_setOneBig = 202;

        //
        public final static int CONST_imOp_videoTop = 210;
        public final static int CONST_imOp_videoBottom = 211;
        public final static int CONST_imOp_videoLeft = 212;
        public final static int CONST_imOp_videoRight = 213;


        public final static int CONST_imOp_loseSel = 214;

        public final static int CONST_imOp_phone_enlargeImg = 220;
        public final static int CONST_imOp_phone_enlargeChannel = 221;
        public final static int CONST_imOp_phone_respCurr = 225;
        public final static int CONST_imOp_respChannel = 230;
        //
        public final static int CONST_imOp_openFile = 301;     //
        //
        public final static int CONST_imOp_myMax = 10000;  //
        //
        public final static int cntof_subject = 128;
        public final static int cntof_content = 256;
        public final static int cntof_propStr = 32;
        public final static int cntof_senderDesc = 64 + 1;
        public final static int cntof_receiverDesc = 64 + 1;

        //
        public final static int DEFAULT_usTimeoutInMsToSwitch_isClient = 2000;                             //  2008/10/28
        public final static int DEFAULT_usTimeoutInMsToSwitch_rt_isClient = 2000;                              //	2009/06/07从100调整为200，//  2008/10/28

        //
        public final static int CONST_threadStep_null = 0;
        public final static int CONST_threadStep_start = 1;
        public final static int CONST_threadStep_toWait = 3;
        public final static int CONST_threadStep_toGetMsg = 5;
        public final static int CONST_threadStep_msgGot = 9;
        //
        public final static int CONST_nStep_enter = 30;                //
        public final static int CONST_nStep_leave = 39;
        //
        //  user defined step 100.1000
        //
        public final static int CONST_threadStep_msgProced = 1001;
        public final static int CONST_threadStep_toExit = 1011;
        public final static int CONST_threadStep_end = 1019;

        //
        public final static int bufSize_tmpPktEncA = 16000;

        //
        public final static int bufSize_tmpPktEncV = 900000;

        //
        public final static int cntof_logMsgContent = 256;

        //
        public final static int CONST_ptzCmd_null = 0;
        public final static int CONST_ptzCmd_up = 1;
        public final static int CONST_ptzCmd_down = 2;
        public final static int CONST_ptzCmd_left = 3;
        public final static int CONST_ptzCmd_right = 4;
        public final static int CONST_ptzCmd_topLeft = 5;
        public final static int CONST_ptzCmd_topRight = 6;
        public final static int CONST_ptzCmd_bottomLeft = 7;
        public final static int CONST_ptzCmd_bottomRight = 8;
        public final static int CONST_ptzCmd_noop = 9;
        public final static int CONST_ptzCmd_focusIn = 10;
        public final static int CONST_ptzCmd_focusOut = 11;
        public final static int CONST_ptzCmd_irisIn = 12;
        public final static int CONST_ptzCmd_irisOut = 13;
        public final static int CONST_ptzCmd_zoomIn = 14;
        public final static int CONST_ptzCmd_zoomOut = 15;

        //

        public final static int IDC_BUTTON_UP = 2280;
        public final static int IDC_BUTTON_DOWN = 2281;
        public final static int IDC_BUTTON_LEFT = 2282;
        public final static int IDC_BUTTON_RIGHT = 2283;
        public final static int IDC_BUTTON_ZOOM_OUT = 2272;
        public final static int IDC_BUTTON_ZOOM_IN = 2273;
        //
        public final static int CONST_mouseStatus_lBtnDown = 1;
        public final static int CONST_mouseStatus_lBtnUp = 2;

        public final static int hEvent_notifyToQuit_Permit = 1;

        //
        public final static int bufSize_decObj_a_mediaCodec_tmpDataBuf  =  10000;
        public final static int bufSize_decObj_mediaCodec_tmpDataBuf  =   500000;//300000;
        public final  static int bufSize_encV_tmpDataBuf  =  300000;



    }

    public static class PLAYER_ID {
        public int index_player;
        public int uiTranNo_player;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putInt(index_player);
            buffer.putInt(uiTranNo_player);
            return buffer.array();
        }
    }

    ;

    public static class MSG_SIMPLE_ROUTE {                                       //
        public qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
    }

    ;

    public static class MSG_DEBUG_INFO {
        long tTime_serv_startToRecv;                 //  iocpÆðÊ¼ÊÕ°üÊ±¼ä( s )


        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(tTime_serv_startToRecv);
            return buffer.array();
        }
    }

    ;


    public static class ROUTE_DEBUG_INFO {
        public long dwTickCnt_me_start;
        public long tTime_start;
        public int usElapsed_toSend;
        public int index;
        public int dwTickCnt_serv_startToRecv;
        public int dwTickCnt_processQ;
        public int dwTickCnt_processQ2Grp;
        public int dwTickCnt_postMsg2OutputQ_imGrp;
        public int dwTickCnt_doMisMgr_processSelf_imGrp;
        public int dwTickCnt_tmpHandler_talkToGrpMem_bt;
        public int dwTickCnt_postMsg2OutputQ_messenger;
        public MSG_DEBUG_INFO req = new MSG_DEBUG_INFO();
        public long dwTickCnt_peer_recved;
        public long tTime_peer_recved;
        public int usElapsed_peer_resp_toSend;
        public MSG_DEBUG_INFO resp = new MSG_DEBUG_INFO();
        public int dwTickCnt_resp_recved;
        public int usElapsed_resp_end;
        public DEBUG_myDRAW_VIDEO_DATA debug_myDRAW_VIDEO_DATA = new DEBUG_myDRAW_VIDEO_DATA();
        public int uiStep_showInfo;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(4096);


            buffer.putLong(dwTickCnt_me_start);
            buffer.putLong(tTime_start);
            buffer.putInt(usElapsed_toSend);
            buffer.putInt(index);
            buffer.putInt(dwTickCnt_serv_startToRecv);
            buffer.putInt(dwTickCnt_processQ);
            buffer.putInt(dwTickCnt_processQ2Grp);
            buffer.putInt(dwTickCnt_postMsg2OutputQ_imGrp);
            buffer.putInt(dwTickCnt_doMisMgr_processSelf_imGrp);
            buffer.putInt(dwTickCnt_tmpHandler_talkToGrpMem_bt);
            buffer.putInt(dwTickCnt_postMsg2OutputQ_messenger);
            if (req != null) {
                byte[] buf1 = req.toByteArray();
                buffer.put(buf1);
            }
            buffer.putLong(dwTickCnt_peer_recved);
            buffer.putLong(tTime_peer_recved);
            buffer.putInt(usElapsed_peer_resp_toSend);
            if (resp != null) {
                byte[] buf2 = resp.toByteArray();
                buffer.put(buf2);
            }
            buffer.putInt(dwTickCnt_resp_recved);
            buffer.putInt(usElapsed_resp_end);
            if (debug_myDRAW_VIDEO_DATA != null) {
                byte[] buf3 = debug_myDRAW_VIDEO_DATA.toByteArray();
                buffer.put(buf3);
            }
            buffer.putInt(uiStep_showInfo);
            buffer.flip();

            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            buf.put(buffer.array(), 0, buffer.limit());
            buffer.clear();
            buffer = null;
            return buf.array();
        }
    }

    ;


    public static class MSG_ROUTE {
        public qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
        public int uiDevType_to;
        public int uiDevType_from;
        public long[] mems_to_idInfo_ui64Id = new long[Consts.CONST_maxMsgrs_sendTo];
        public int uiMsgRouteId;
        public int uiLogonId;
        public int uiObjType_src;
        public short usLangId_from;
        public int ulDetectedIp_from;
        //public ROUTE_DEBUG_INFO debugInfo = new ROUTE_DEBUG_INFO();
        public int tmpInternal_usCnt_mems_to;

        public byte[] toByteArray() {
            //
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            if (idInfo_to != null) {
                byte[] buf1 = idInfo_to.toByteArray();
                buffer.put(buf1);
            }
            if (idInfo_from != null) {
                byte[] buf2 = idInfo_from.toByteArray();
                buffer.put(buf2);
            }
            buffer.putInt(uiDevType_to);
            buffer.putInt(uiDevType_from);

            for (long ui64Id : mems_to_idInfo_ui64Id) {
                buffer.putLong(ui64Id);
            }


            buffer.putInt(uiMsgRouteId);
            buffer.putInt(uiLogonId);
            buffer.putInt(uiObjType_src);
            buffer.putShort(usLangId_from);
            buffer.putInt(ulDetectedIp_from);
            /*
            if (debugInfo != null) {
                byte[] buf3 = debugInfo.toByteArray();
                buffer.put(buf3);
            }
             */
            buffer.putInt(tmpInternal_usCnt_mems_to);
            buffer.flip();
            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            buf.put(buffer);
            return buf.array();
        }
    }


    ;

    public static class MSGR_ADDR {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public String misServName;
        public int uiDevType;
        public int uiLogonId;
        public int uiObjType;
        public String displayName;
        public boolean bInNoticeMode;
        public long tStartTime_notice;
        public int uiTranNo_notice;
        public int uiTranNo_shadow;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            if (idInfo != null) {
                byte[] idInfoBytes = idInfo.toByteArray();
                buffer.put(idInfoBytes);
            }
            if (misServName != null) {
                byte[] misServNameBytes = misServName.getBytes();
                buffer.put(misServNameBytes);
            }
            buffer.putInt(uiDevType);
            buffer.putInt(uiLogonId);
            buffer.putInt(uiObjType);

            if (displayName != null) {
                byte[] displayNameBytes = displayName.getBytes();
                buffer.put(displayNameBytes);
            }
//            int size = idInfoBytes.length + misServNameBytes.length + displayNameBytes.length + Integer.BYTES * 8 + Long.BYTES;
            buffer.put((byte) (bInNoticeMode ? 1 : 0));
            buffer.putLong(tStartTime_notice);
            buffer.putInt(uiTranNo_notice);
            buffer.putInt(uiTranNo_shadow);
            buffer.flip();
            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            buf.put(buffer.array(), 0, buffer.limit());
            return buf.array();
        }
    }

    public static class QY_COMM_AUTHINFO_MIS {

        public short usAuthCmd;

        public int iAppType;                               //  2011/03/27
        public int iCustemId;
        public int uiChannelType;                         //  2007/07/08

        public String ver;
        //

        public short usLangId;
        public int uiDevType = CONST_objType_android;
        //
        public short usAuthType;                              //  2011/03/22
        //

        public String ip;// = new byte[CONST_qyMaxIpLen + 1];

        public String messengerName;//[CONST_maxMessengerNameLen + 1];               //  messenger
        public String messengerPasswd;//[CONST_maxMessengerPasswdLen + 1];
        public String messengerNewPasswd;//[CONST_maxMessengerPasswdLen + 1];
        //
        public String wRndName_serv;//[255 + 1];               //
        public int uiRnd_cli;                             //  web 2007/11/22
        //
        //  int								uiWebSessId;							//  web2007/11/22
        public int uiLogonId;                             //  2011/02/02 唯一登录标识
        //


        // ----------------------------------------
        public short usSessionEncType;                //会话加密类型

        public int uiBlobLen_sessionKey;                  //  会话秘钥长度
//        public ByteBuffer sessionKeyBlob = ByteBuffer.allocateDirect(bufSize_sessionKeyBlob);//[bufSize_sessionKeyBlob]; 会话内容
        public byte[] sessionKeyBlob;//[bufSize_sessionKeyBlob]; 会话内容

        public int usLen_sessionIv;                     // aes向量长度
//        public ByteBuffer sessionIv = ByteBuffer.allocateDirect(bufSize_sessionIv);//[bufSize_sessionIv]; //会话秘钥向量内容
        public byte[] sessionIv;//[bufSize_sessionIv]; //会话秘钥向量内容

        //
        public int copyFrom(QY_COMM_AUTHINFO_MIS src)
        {
            usAuthCmd = src.usAuthCmd;

            iAppType  = src.iAppType;                               //  2011/03/27
            iCustemId  =  src.iCustemId;
            uiChannelType  =  src.uiChannelType;                         //  2007/07/08

            ver  =  src.ver;
            //

            usLangId  =  src.usLangId;
            uiDevType = src.uiDevType;
            //
            usAuthType  =  src.usAuthType;                              //  2011/03/22
            //

            ip  =  src.ip;// = new byte[CONST_qyMaxIpLen + 1];

            messengerName  =  src.messengerName;//[CONST_maxMessengerNameLen + 1];               //  messenger
            messengerPasswd  =  src.messengerPasswd;//[CONST_maxMessengerPasswdLen + 1];
            messengerNewPasswd  =  src.messengerNewPasswd;//[CONST_maxMessengerPasswdLen + 1];
            //
            wRndName_serv  =  src.wRndName_serv;//[255 + 1];               //
            uiRnd_cli  =  src.uiRnd_cli;                             //  web 2007/11/22
            //
            //  int								uiWebSessId;							//  web2007/11/22
            uiLogonId  =  src.uiLogonId;                             //  2011/02/02 唯一登录标识
            //


            // ----------------------------------------
            usSessionEncType  =  src.usSessionEncType;                //会话加密类型

            //
            if  (  src.sessionKeyBlob!=null  &&  src.uiBlobLen_sessionKey  !=0) {
                sessionKeyBlob = new byte[src.sessionKeyBlob.length];
                System.arraycopy(src.sessionKeyBlob,0,sessionKeyBlob,0,sessionKeyBlob.length);
                uiBlobLen_sessionKey=src.uiBlobLen_sessionKey;
            }

            //
            if  (  src.usLen_sessionIv  !=  0  &&  src.sessionIv!=null) {
                sessionIv  =  new byte[src.sessionIv.length];
                System.arraycopy(src.sessionIv,0,sessionIv,0,sessionIv.length);
                usLen_sessionIv  =  src.usLen_sessionIv;
            }

            //
            return  0;
        }

    }

    ;

    //  ÈÏÖ¤ÏìÓ¦
    public static class AUTH_RESP_MIS {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();//账户ID
        //
        public String wRndName_serv;// = ByteBuffer.allocate(Consts.CONST_cntof_wRndName_serv);//[];               //  webMessenger
        //  int								uiWebSessId;							//  webMessenger
        //
        public int uiLogonId;                             //
        //
        public int uiCommEncCtxType;                      //
        public int uiBlobLen_publicKey;
        public byte[] publicKeyBlob = new byte[bufSize_publicKeyBlob];

        //
        public byte ucbDaemonDemo;                            //  2009/10/11
        public int usMaxVideoConferenceMems;                //  2009/11/05
        public byte ucDays_left;                          //  2010/03/22
        //
        public int usAppAvLevel_daemon;                 //  2011/10/22
        //
        public int usSubSystemId_daemon;                    //  2017/06/21

        //
        public String talkerDesc;//[cntof_talkerDesc];                           //  2011/04/07

        public String wBuf_hint;//[cntof_hint];

        //
        public String clientWelcomeMsg;//[CONST_cntof_clientWelcomMsg];                    //  2016/08/10
    }

    ;

    public static class QY_imAm_RCD {
        int id;
        String misServName;//[255 + 1];
        qyDefs.QY_MESSENGER_ID idInfo;
        String messengerName;//[255 + 1];
        String messengerPasswd;//[255 + 1];
        int iPasswdType;
        String salt;//[CONTS_passwdSaltLen + 1];
        int iStatus;
        String startTime;//[14 + 1];
        String tLastModifiedTime;//[14 + 1];
    }

    //  2014/09/04


    public static class RES_obj_simple {
        public int uiObjType;     // 类型（屏幕共享、发言人）
        public int usIndex_obj;   // 索引用来区分ObjType
        public int usHelp_subIndex; // 用来区分不同的球机

        public byte[] toByteArray() {
            int totalLength = 3 * Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.putInt(uiObjType);
            buffer.putInt(usIndex_obj);
            buffer.putInt(usHelp_subIndex);

            return buffer.array();
        }
    }


    public static class QIS_RES_OBJ {
        //
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();                     //  2016/07/24
        public RES_obj_simple resObj = new RES_obj_simple();

        public byte[] toByteArray() {
            byte[] buf1 = idInfo.toByteArray();
            byte[] buf2 = resObj.toByteArray();

            int totalLength = buf1.length + buf2.length;
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.put(buf1);
            buffer.put(buf2);

            return buffer.array();
        }
    }

    //
    public static class QIS_dynBmp_info {
        //
        QIS_RES_OBJ qisResObj;                      //  2016/07/23
        //
        String name;//[32];                     //  name used to mean device id or unique name for dynBmp
        String cusName;//[32];                  //  cusName used to mean a friendly comment for dynBmp
        //
        int usW, usH;
        //
    }

    public static class TRANSFER_VIDEO_dataResp extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public int uiTranNo_openAvDev;                    //  tranNo.
        public long tSelectTime;                        //  2008/12/05
        public short usFps;                               //
        public short usCnt_pkts;
        public short usDiff_pkts;                     //  2009/04/18

        public byte[] toByteArray() {
            byte[] buf = idInfo.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + Short.BYTES * 3 + Long.BYTES + buf.length);
            buffer.putInt(uiType);
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putLong(tSelectTime);
            buffer.putShort(usFps);
            buffer.putShort(usCnt_pkts);
            buffer.putShort(usDiff_pkts);
            buffer.put(buf); // 假设QY_MESSENGER_ID类有实现toByteArray方法
            return buffer.array();
        }
    }


    public static class TRANSFER_AUDIO_dataMemHead_tmpInternal {
        public int uiTranNo_org;
        public long dwTickCnt_videoConferenceRecved;

        public byte[] toByteArray() {
            int bufferSize = Integer.BYTES + Long.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiTranNo_org);
            buffer.putLong(dwTickCnt_videoConferenceRecved);
            return buffer.array();
        }

    }

    ;


    public static class TRANSFER_AUDIO_dataMemHead {
        //unsigned  short									usIndex_ii;							//  2013/07/18. 将被逐渐弃用

        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();     //  会议时,发言人的id

        //
        public int uiTranNo_openAvDev;    //  2013/08/04. 当会议参会人,断开会议,修改策略,重新参会时,需要有tn来标识不同的视频流,所以要加这个成员


        public TRANSFER_AUDIO_dataMemHead_tmpInternal tmpInternal = new TRANSFER_AUDIO_dataMemHead_tmpInternal();  // 临时变量               //  ÁÙÊ±Ê¹ÓÃµÄ±äÁ¿£¬²»ÄÜÓÃÔÚÍ¨ÐÅÖÐ

        public int uiSampleTimeInMs;  // 这一声音帧的时间戳               //  2009/05/02
        public int ui_rtTimeLen;   //无用      //  2009/05/27

        //  2015/09/10
        public int uiPts;  // 显示时间戳                   //  2015/09/10

        public int uiLen; // 这一帧的数据包长度

        //
        //public MIS_MSG_TASK  tmp_pMsgTask;


        //
        public int copyFrom(TRANSFER_AUDIO_dataMemHead src)
        {
            if  (  src  ==  null  )  return  -1;
            this.idInfo.ui64Id = src.idInfo.ui64Id;
            this.uiTranNo_openAvDev = src.uiTranNo_openAvDev;
            this.uiSampleTimeInMs = src.uiSampleTimeInMs;
            this.uiPts = src.uiPts;
            //this.ucbKeyFrame=src.ucbKeyFrame;
            this.uiLen = src.uiLen;
            //this.tmp_tn_msgInput=src.tmp_tn_msgInput;

            //
            //tmp_pMsgTask  =  src.tmp_pMsgTask;

            //
            return  0;
        }

        public void memset0()
        {
            this.idInfo.ui64Id = 0;
            this.uiTranNo_openAvDev = 0;
            this.uiSampleTimeInMs = 0;
            this.uiPts = 0;
            //this.ucbKeyFrame=src.ucbKeyFrame;
            this.uiLen = 0;

            return;

        }

        public byte[] toByteArray() {
            int idInfoSize = idInfo.toByteArray().length;
            int tmpInternalSize = tmpInternal.toByteArray().length;
            int bufferSize = idInfoSize + Integer.BYTES + tmpInternalSize + 3 * Integer.BYTES + 2 * Short.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putShort((short) idInfoSize);
            buffer.put(idInfo.toByteArray());
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putShort((short) tmpInternalSize);
            buffer.put(tmpInternal.toByteArray());
            buffer.putLong(uiSampleTimeInMs);
            buffer.putInt(ui_rtTimeLen);
            buffer.putInt(uiPts);
            buffer.putInt(uiLen);
            return buffer.array();
        }

    }

    public static class TRANSFER_AUDIO_DATA_tmpInternal_param {                                    //  for procTaskAudio. 2009/05/12
        public int index_pMems_from; // 已弃用
        public int index_activeMems_from; // 话筒的index


        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
            buffer.putInt(index_pMems_from);
            buffer.putInt(index_activeMems_from);
            return buffer.array();
        }
    }

    ;

    public static class TRANSFER_AUDIO_DATA_tmpInternal_result {

        public short usRespCode; // 响应码
        public short usCnt_pkts;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 2);
            buffer.putShort(usRespCode);
            buffer.putShort(usCnt_pkts);
            return buffer.array();
        }
    }

    ;


    public static class TRANSFER_AUDIO_DATA_tmpInternal {
        public int usCnt_mems; // mem数量

        public TRANSFER_AUDIO_DATA_tmpInternal_param param = new TRANSFER_AUDIO_DATA_tmpInternal_param();

        //  audio´¦Àí½á¹û£¬2008/04/17
        public TRANSFER_AUDIO_DATA_tmpInternal_result result = new TRANSFER_AUDIO_DATA_tmpInternal_result();                               //  ÐèÒª·¢ËÍÖ¡ËÙÏìÓ¦Ê±£¬ÏÈ½«½á¹û±£´æÔÚÕâÀï¡£ÔÚtmpHandler_toMixerÀï×é°ü·¢ËÍ


        public byte[] toByteArray() {
            byte[] buf1 = param.toByteArray();
            byte[] buf2 = result.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + buf1.length + buf2.length);
            buffer.putInt(usCnt_mems);
            buffer.put(buf1);
            buffer.put(buf2);
            return buffer.array();
        }

    }

    /**
     * 传输音频数据包
     */
    public static class TRANSFER_AUDIO_DATA extends IM_CONTENTU {
        /**
         * 唯一值，标识打开音频设备的序号
         */
        public int uiTranNo_openAvDev;
        public long tLastTime_showFrameInfo_020534; // 已弃用
        public short usCnt_pkts; // 暂时无用

        /**
         * 临时的变量， 不是很重要
         */
        public TRANSFER_AUDIO_DATA_tmpInternal tmpInternal = new TRANSFER_AUDIO_DATA_tmpInternal();

        /**
         * 计数器：下面几个成员
         */
        public short usCnt;

        /**
         * Member的结构体
         */
        TRANSFER_AUDIO_dataMemHead[] mems = new TRANSFER_AUDIO_dataMemHead[CONST_maxTransferAvDataMems];

        /**
         * 整个数据长度（上面的memes所有的帧的数据之和）
         */
        public int uiLen;                                 //  2009/05/20
//        public ByteBuffer buf = ByteBuffer.allocate(bufSize_transferAudioData);
        public ByteBuffer buf;

        public TRANSFER_AUDIO_DATA() {
            for (int i = 0; i < CONST_maxTransferAvDataMems; i++) {
                mems[i] = new TRANSFER_AUDIO_dataMemHead();
            }
        }

        public void initHeadMems() {
            for (int i = 0; i < mems.length; i++) {
                mems[i].memset0();
            }
        }

        public void clearData() {
            this.uiType = 0;
            this.uiTranNo_openAvDev = 0;
            this.tLastTime_showFrameInfo_020534 = 0L;
            this.usCnt_pkts = 0;
            this.usCnt = 0;
            this.uiLen = 0;
            if (this.buf != null) {
                this.buf.position(0);
                this.buf.limit(this.buf.capacity());
            }
            initHeadMems();
        }

        public byte[] toByteArray() {
            byte[] buf1 = tmpInternal.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + Short.BYTES * 2 + Long.BYTES + buf1.length + buf.capacity());
            buffer.putInt(uiType);
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putLong(tLastTime_showFrameInfo_020534);
            buffer.putShort(usCnt_pkts);
            buffer.putShort(usCnt);
            buffer.put(buf1);
//            buffer.put(mems);
            buffer.putInt(uiLen);
            buffer.put(buf);
            return buffer.array();
        }

    }

    ;

    //
    public static class TRANSFER_AUDIO_dataResp extends IM_CONTENTU {
        //
        public int uiTranNo_openAvDev;
        public short usCnt_pkts;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + Short.BYTES);
            buffer.putInt(uiType);
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putInt(usCnt_pkts);
            return buffer.array();
        }

    }

    ;


    //
    //  2011/01/26
    public static class TRANSFER_videoData_stat {
        public long tLastTime_showFrameInfo_020534;     //

        //
        public long tSelectTime;                        //  2008/12/05
        public short usCnt_pkts;                          //  tSelectTime. 020534 tLastTime_showFrameInfo.tStartTime
        public short usFps;                               //

        //
        public int iVal_dbg;                           //  2017/09/04

        public byte[] toByteArray() {
            int bufferSize = Long.BYTES * 2 + Integer.BYTES * 2 + Short.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putLong(tLastTime_showFrameInfo_020534);
            buffer.putLong(tSelectTime);
            buffer.putShort(usCnt_pkts);
            buffer.putShort(usFps);
            buffer.putInt(iVal_dbg);
            return buffer.array();
        }

    }


    public static class TRANSFER_VIDEO_dataMemHead {
        //unsigned  short									usIndex_ii;							//  2013/07/18. 将被逐步弃用

        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();                             //  2009/05/31
        //
        public int uiTranNo_openAvDev;                    //  2012/01/09

        //
        public int uiSampleTimeInMs;                  //  2009/05/02
        //int									uiSampleTimeInMs_org;				//  2009/05/27

        //  2015/01/15
        public int uiPts;                             //  2015/01/15

        //
        public byte ucbKeyFrame;


        //  2016/12/26
        //int										uiEventId_lastRecvd_unused;				//  2013/11/15, 表示从remoteAssist收到了事件的id

        //
        public int uiLen;                             //  ¶ÔÓ¦µÄÊý¾Ý³¤¶È

        //
        private byte tmpInternal;
        public int  tmp_tn_msgInput;

        //
        public TRANSFER_videoData_stat stat = new TRANSFER_videoData_stat();                               //  2017/09/05

        //
        //public myDRAW_VIDEO_DATA tmp_pkt;

        //
        public int copyFrom(TRANSFER_VIDEO_dataMemHead src)
        {
            if  (  src  ==  null  )  return  -1;
            this.idInfo.ui64Id=src.idInfo.ui64Id;
            this.uiTranNo_openAvDev=src.uiTranNo_openAvDev;
            this.uiSampleTimeInMs=src.uiSampleTimeInMs;
            this.uiPts=src.uiPts;
            this.ucbKeyFrame=src.ucbKeyFrame;
            this.uiLen=src.uiLen;
            this.tmp_tn_msgInput=src.tmp_tn_msgInput;

            //
            //tmp_pkt  =  src.tmp_pkt;

            //
            return  0;
        }

        //
        public void memset0()
        {

            this.idInfo.ui64Id=0;
            this.uiTranNo_openAvDev=0;
            this.uiSampleTimeInMs=0;
            this.uiPts=0;
            this.ucbKeyFrame=0;
            this.uiLen=0;
            this.tmp_tn_msgInput=0;

        }


        //
        public byte[] toByteArray() {
            byte[] buf1 = idInfo.toByteArray();
            byte[] buf2 = stat.toByteArray();
            int bufferSize = buf1.length + Integer.BYTES * 3 + Byte.BYTES + buf2.length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put(buf1);
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putInt(uiSampleTimeInMs);
            buffer.putInt(uiPts);
            buffer.put(ucbKeyFrame);
            buffer.putInt(uiLen);
            buffer.put(buf2);
            return buffer.array();
        }
    }

    ;

    public static class TRANSFER_VIDEO_DATA_tmpInternal_param {                                    //  for procTaskVideo. 2009/05/12

        //
        public int index_pMems_from;
        public int index_activeMems_from;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
            buffer.putInt(index_pMems_from);
            buffer.putInt(index_activeMems_from);
            return buffer.array();
        }

    }

    ;

    public static class TRANSFER_VIDEO_DATA_tmpInternal_result {
        public short usRespCode;
        public float fFps;
        public long tSelectTime;
        public short usCnt_pkts;
        public short usDiff_pkts;                     //  2009/04/18

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 3 + Float.BYTES);
            buffer.putShort(usRespCode);
            buffer.putFloat(fFps);
            buffer.putLong(tSelectTime);
            buffer.putShort(usCnt_pkts);
            buffer.putShort(usDiff_pkts);
            return buffer.array();
        }

    }

    ;

    public static class TRANSFER_VIDEO_DATA_tmpInternal {
        public int usCnt_mems;

        public TRANSFER_VIDEO_DATA_tmpInternal_param param = new TRANSFER_VIDEO_DATA_tmpInternal_param();

        //  ´¦Àí½á¹û

        public TRANSFER_VIDEO_DATA_tmpInternal_result result = new TRANSFER_VIDEO_DATA_tmpInternal_result();


        public byte[] toByteArray() {
            byte[] buf1 = param.toByteArray();
            byte[] buf2 = result.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + buf1.length + buf2.length);
            buffer.putInt(usCnt_mems);
            buffer.put(buf1);
            buffer.put(buf2);
            return buffer.array();
        }

    }

    /**
     * 传输视频数据包，同音频
     */
    public static class TRANSFER_VIDEO_DATA extends IM_CONTENTU {
        /**
         * 唯一值，标识打开视频设备的序号
         */
        public int uiTranNo_openAvDev;
        public TRANSFER_VIDEO_DATA_tmpInternal tmpInternal = new TRANSFER_VIDEO_DATA_tmpInternal(); // 临时的变量，不重要
        /**
         * 计数器：Members的数量（视频的每一帧）
         */
        public short usCnt;
        /**
         * 视频每一帧的头信息
         */
        public TRANSFER_VIDEO_dataMemHead[] mems = new TRANSFER_VIDEO_dataMemHead[CONST_maxTransferAvDataMems];
        /**
         * 数据总长度（上面mems之和）
         */
        public int uiLen;
        public ByteBuffer buf;// = ByteBuffer.allocateDirect(bufSize_transferVideoData);

        public TRANSFER_VIDEO_DATA() {
            for (int i = 0; i < CONST_maxTransferAvDataMems; i++) {
                mems[i] = new TRANSFER_VIDEO_dataMemHead();
            }
        }

        public void initHeadMems() {
            for (int i = 0; i < mems.length; i++) {
                mems[i].memset0();
            }
        }

        public void clearDataTo0() {
            this.uiTranNo_openAvDev = 0;
            this.usCnt = 0;
            this.uiLen = 0;
            this.uiType = 0;
            initHeadMems();
//            this.buf.position(0);
        }

        public byte[] toByteArray() {
            byte[] buf1 = tmpInternal.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 4 + buf1.length + buf.capacity());
            buffer.putInt(uiType);
            buffer.putInt(uiTranNo_openAvDev);
            buffer.put(buf1);
            buffer.putShort(usCnt);
//            buffer.put(buf_TRANSFER_VIDEO_dataMemHead_mems);
            buffer.putInt(uiLen);
            buffer.put(buf);
            return buffer.array();
        }

    }


    public static class myDRAW_VIDEO_DATA_input {
        //
        public boolean bRtsp;                                    //  2015/03/14

        //
        public long tStartTran;                              //  2011/08/30

        public byte[] toByteArray() {
            int bufferSize = 1 + Long.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put((byte) (bRtsp ? 1 : 0));
            buffer.putLong(tStartTran);
            return buffer.array();
        }

    }

    ;

    public static class myDRAW_VIDEO_DATA_vpp {
        int uiCnt_vpp0;

        public byte[] toByteArray() {
            int bufferSize = Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiCnt_vpp0);
            return buffer.array();
        }

    }

    ;

    public static class myDRAW_VIDEO_DATA_decInfo {
        public int index_pMems_from;
        //  2016/12/27
        public int uiEventId_lastRecvd_unused;                    //  2008/11/22

        //  2014/09/04
        public QIS_RES_OBJ dynBmp = new QIS_RES_OBJ();                                 //  2014/10/12

        //
        public boolean ucbUnresizable;                            //  2015/10/23

        public byte[] toByteArray() {
            byte[] buf = dynBmp.toByteArray();
            int bufferSize = Integer.BYTES * 2 + buf.length + Byte.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(index_pMems_from);
            buffer.putInt(uiEventId_lastRecvd_unused);
            buffer.put(ucbUnresizable ? (byte) 1 : (byte) 0);
            buffer.put(buf);
            return buffer.array();
        }

    }

    ;


    public static class DEBUG_myDRAW_VIDEO_DATA {
        public int dwTickCnt_start;
        public int usToEncInMs;
        public int usEncedInMs;
        public long usPostInMs;
        public int usToDispatchInMs;
        public int usOutputInMs;
        public int usPreTransInMs;
        public int usTransInMs;

        public long dwTickCnt_proc_io_read;
        public long dwTickCnt_doPretrans;
        public long dwTickCnt_drawVideoData;
        public long dwTickCnt_playPkt_start;

        public int uiTranNo_msgTask;
        public int lPktNo;

        public static int getSize() {
            // Calculate the total size of the fields in bytes
            return 16 * Integer.BYTES;
        }

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(getSize());
            buffer.putInt(dwTickCnt_start);
            buffer.putInt(usToEncInMs);
            buffer.putInt(usEncedInMs);
            buffer.putLong(usPostInMs);
            buffer.putInt(usToDispatchInMs);
            buffer.putInt(usOutputInMs);
            buffer.putInt(usPreTransInMs);
            buffer.putInt(usTransInMs);
            buffer.putLong(dwTickCnt_proc_io_read);
            buffer.putLong(dwTickCnt_doPretrans);
            buffer.putLong(dwTickCnt_drawVideoData);
            buffer.putLong(dwTickCnt_playPkt_start);
            buffer.putInt(uiTranNo_msgTask);
            buffer.putInt(lPktNo);
            return buffer.array();
        }
    }


    public static class myDRAW_VIDEO_DATA {
        public int uiType_unused;
        public int uiCapType;
        public int iIndex_capBmp;

        public myDRAW_VIDEO_DATA_input input = new myDRAW_VIDEO_DATA_input();
        public myDRAW_VIDEO_DATA_vpp vpp = new myDRAW_VIDEO_DATA_vpp();
        public int uiEventId_lastRecvd_unused;
        public int iTaskId;
        public int uiTaskType;                                //  2014/10/01
        public qyDefs.QY_MESSENGER_ID idInfo_peer = new qyDefs.QY_MESSENGER_ID();
        public myDRAW_VIDEO_DATA_decInfo decInfo = new myDRAW_VIDEO_DATA_decInfo();                                //
        public DEBUG_myDRAW_VIDEO_DATA debugInfo = new DEBUG_myDRAW_VIDEO_DATA();
        public int lPktId_alloc;                          //  2014/03/26.一个不重的序号. 分配内存的标识
        public TRANSFER_VIDEO_dataMemHead head = new TRANSFER_VIDEO_dataMemHead();                                    //  head.len表示pBuf中的缓冲长度, head.len和bih.biSizeImage不一定相等
        public int usIndex_activeMems_from;             //  2013/07/18.
        public int usPktResType;                            //  2015/09/30
        public int uiSize_dec;                                //  pBuf中分为2个区，一个为未压缩数据区，大小为uiSize_dec. uiSize_dec应<=head.len
        public qyCommProc_open.BITMAPINFOHEADER bih = new qyCommProc_open.BITMAPINFOHEADER();                                   //  2011/12/04. 此bih表明了未压缩区中的图像格式, bih.sizeImage应该<=uiSize_dec
        //
        public int uiOffset_enc;
        public int uiLen_enc;                             //  另一个为压缩数据区，大小为head.len - uiSize_dec，包含一个包头uiOffset_enc，紧跟一个压缩数据. 真实大小为uiLen_enc
        //
        //  2014/02/24. 当bRtsp为true, p.uiLen_enc  =  offsetof(  TRANSFER_VIDEO_DATA,  buf  )  +  BufferLen;


        public qyMemoryFunc.QY_MEMORY memory = new qyMemoryFunc.QY_MEMORY();                                    //  2010/04/23

        public byte[] toByteArray2() {
            byte[] buf1 = input.toByteArray();
            byte[] buf2 = vpp.toByteArray();
            byte[] buf3 = idInfo_peer.toByteArray();
            byte[] buf4 = decInfo.toByteArray();
            byte[] buf5 = debugInfo.toByteArray();
            byte[] buf6 = head.toByteArray();
            byte[] buf7 = bih.toByteArray();
            byte[] buf8 = memory.toByteArray1();

            int bufferSize = 15 * Integer.BYTES + buf1.length + buf2.length + buf3.length + buf4.length + buf5.length + buf6.length + buf7.length + buf8.length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType_unused);
            buffer.putInt(uiCapType);
            buffer.putInt(iIndex_capBmp);
            buffer.put(buf1);
            buffer.put(buf2);
            buffer.putInt(uiEventId_lastRecvd_unused);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            buffer.put(buf3);
            buffer.put(buf4);
            buffer.put(buf5);
            buffer.putInt(lPktId_alloc);
            buffer.put(buf6);
            buffer.putInt(usIndex_activeMems_from);
            buffer.putInt(usPktResType);
            buffer.putInt(uiSize_dec);
            buffer.put(buf7);
            buffer.putInt(uiOffset_enc);
            buffer.putInt(uiLen_enc);
            buffer.put(buf8);
            return buffer.array();
        }
    }

    ;


    //////////////////////////////////////////////////////////////////////
    ///
    public static class IM_GRP_INFO {
        public int uiType;
        public int id;
        public String misServName = "";
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public String name = "";
        public short usSubtype;
        public qyDefs.QY_MESSENGER_ID idInfo_creator = new qyDefs.QY_MESSENGER_ID();
        public long startTime;
        public long tLastModifiedTime;
    }

    public static class IM_GRP_MEM {
        public int uiType;
        public int id;
        public String misServName;//[CONST_maxMisServNameLen + 1];
        public qyDefs.QY_MESSENGER_ID idInfo_grp = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_mem = new qyDefs.QY_MESSENGER_ID();
        public int iRole;
        public long startTime;
        public long tLastModifiedTime;
        public int iStatus;
    }

    ;


    public static class MY_REG_DESC {
        public String pZb = "";
        public String pBb = "";
        public String pDw = "";
        public String pBm = "";
        public String pSyr = "";
    }

    ;


    public static class QY_MESSENGER_PCINFO {
        int id;

        String misServName;//[CONST_maxMisServNameLen + 1];
        qyDefs.QY_MESSENGER_ID idInfo;
        //
        //
        int uiType;
        //
        int iPlatformId;
        //
        String pcName;//[255 + 1];
        String domainName;//[255 + 1];
        String osUsrName;//[255 + 1];
        //
        long tLastModifiedTime;

    }

    ;

    public static class QY_MESSENGER_phoneInfo {

        int id;
        String misServName;//[CONST_maxMisServNameLen + 1];
        int uiDevType;
        //TCHAR wDevIdStr[128 + 1];
        //
        String messengerPasswd;//[255 + 1];
        //
        qyDefs.QY_MESSENGER_ID idInfo;
        //
        int iStatus;
        //
        long startTime;
        long tLastModifiedTime;

    }

    ;


    public static class QY_MESSENGER_INFO {

        public int uiType;                                        //  messengerType, me, messenger, group
        public int iRcdId;                                     //  qyMessengerTab id

        public String misServName = "";//[CONST_maxMisServNameLen + 1];
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();

        public int uiLogonId;                                 //  2007/12/22
        public long tTime_logon;                             //  2022/02/02
        public String wLocation;//[64 + 1];

        public String ip;//[CONST_qyMaxIpLen + 1];
        public String detectedIp;//[CONST_qyMaxIpLen + 1];

        public short usLangId;
        public int uiDevType;

        public String messengerName = "";//[64 + 1];

        public int iRole;                                      //  2017/08/14

        public long startTime;

        public short usRunningStatus;                         //  2007/06/07

        public long tLastModifiedTime;

        public ByteBuffer clientVer;//[CONST_qyMaxVerLen + 1];

        public short shClientVer;             // clientVer[0-3];
        public byte blClientVer;               // clientVer[4-5];		//  2005/05/23, lhQwmVer  =  atol(  qwmVer  )  /  100;  llQwmVer  =  atol(  qwmVer  )  %  100;
        public byte b_mantissa_clientVer;      // clientVer[6-7]

        public int iStatus;

        public ByteBuffer toByteBuffer() {
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            buffer.putInt(uiType);
            buffer.putInt(iRcdId);
            if (misServName != null) {
                byte[] bytes = misServName.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            if (idInfo != null) {
                buffer.put(idInfo.toByteBuffer());
            }
            buffer.putInt(uiLogonId);
            buffer.putLong(tTime_logon);
            if (wLocation != null) {
                byte[] bytes = wLocation.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            if (ip != null) {
                byte[] bytes = ip.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            if (detectedIp != null) {
                byte[] bytes = detectedIp.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            buffer.putInt(uiDevType);
            if (messengerName != null) {
                byte[] bytes = messengerName.getBytes();
                buffer.put(bytes, 0, bytes.length);
            }
            buffer.putInt(iRole);
            buffer.putLong(startTime);
            buffer.putShort(usRunningStatus);
            buffer.putLong(tLastModifiedTime);
            if (clientVer != null) {
                buffer.put(clientVer);
            }
            buffer.putShort(shClientVer);
            buffer.put(blClientVer);
            buffer.put(b_mantissa_clientVer);
            buffer.putInt(iStatus);
            buffer.flip();
            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            buf.put(buffer.array(), 0, buffer.limit());
            return buf;
        }
    }

    //  ´Ë½á¹¹²»ÓÃÀ´´«Êä£¬Òª×ª»»³ÉÁ÷²Å½øÐÐ´«Êä
    public static class QY_MESSENGER_REGINFO {
        public int uiType;
        //
        public int id;

        public int iCustomId;

        //  TCHAR												misServName[CONST_maxMisServNameLen  +  1];
        //  QY_MESSENGER_ID									idInfo;
        public MSGR_ADDR addr = new MSGR_ADDR();

        public long regTime;//[CONST_qyTimeLen + 1];
        public long auditTime;//[CONST_qyTimeLen + 1];
        public long tLastModifiedTime;//[CONST_qyTimeLen + 1];
        public long tCommitTime;//[CONST_qyTimeLen + 1];


        public int[] uiFieldIds = new int[CONST_qnmMaxRegFields];
        public String dw = "";//[CONST_maxNameLen + 1];
        public String bm = "";//[CONST_maxNameLen + 1];
        public String syr = "";//[CONST_maxNameLen + 1];

        //
        int index;
        public String tmp_col;//[CONST_maxNameLen + 1];

    }

    ;


    public static class REFRESH_imObjRule_reqMem {
        qyDefs.QY_MESSENGER_ID idInfo_related;
        int uiRuleType;
        int usIndex;
        int uiCmd;
        //
        String wDesc;//[128];           //  2011/11/13
    }

    ;

    public static class REFRESH_imObjRules_req {
        int uiType;

        byte ucbResp;
        byte ucbRetrieveAll;

        int usMaxMemsPerSnd;         //  2011/11/13


        //
        byte req_ucbNext;
        //
        qyDefs.QY_MESSENGER_ID req_idInfo_related;
        int req_uiRuleType;
        int req_usIndex;


        //
        byte ucbEnd;


        int usCnt_mems_tmpInternal;

        //
        int usCnt;
        //REFRESH_imObjRule_reqMem mems[MAX_imObjRule_reqMems];
        ByteBuffer buf_REFRESH_imObjRule_reqMem;//[bufSize_REFRESH_imObjRule_reqMem_mems];
    }

    ;


    //  2011/11/13. qm的全局策略，也是存在数据库里
    public static class QY_im_rules {
        //  2011/10/15
        //  unsigned  char										ucbShareWebcamInConference;						//  是否在会议中共享摄像机. 如果选是，则在会议自动接收时，要弹出共享窗口
        qyDefs.QY_MESSENGER_ID conferenceId_shareWebcam;
        byte ucbNoLocalVideoInThisConference;              //  是否在这个会议中启动视频. 2011/10/22
        //
        byte ucbDistinguishSharedWebcams;                  //  是否区分被共享的摄像机，（是否使用序号）
        //
    }

    ;

    //
    public static class QY_imObj_rules {
        int uiCmd_autoAnswer_av;
        int uiCmd_autoAnswer_screen;   //  2010/12/18
        int uiCmd_autoAnswer_mediaFile;    //  2010/12/18
    }


    //  2010/08/13
    public static class QY_imObjRule_rcd {
        int id;

        String misServName;//[CONST_maxMisServNameLen + 1];
        qyDefs.QY_MESSENGER_ID idInfo;
        //
        int uiRuleType;
        //
        int usIndex;
        qyDefs.QY_MESSENGER_ID idInfo_related;
        int uiCmd;
        //
        long startTime;
        long tLastModifiedTime;
        //
        int iStatus;
        //
        String col0;//[255 + 1];


    }

    public static class IM_SIMPLE_RESP extends IM_CONTENTU {
        public int usCmd_org;
        public short uiContentType_org;
        public int iStatus;

        public byte[] toByteArray() {
            int bufferSize = 4 * Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType);
            buffer.putInt(usCmd_org);
            buffer.putInt(uiContentType_org);
            buffer.putInt(iStatus);
            return buffer.array();
        }

        //
    }

    public static class IM_HTML_CONTENT extends IM_CONTENTU {
        public byte ucbResp;
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
        public int uiSerialNo;
        public String wBuf;

        public byte[] toByteArray() {
            byte[] buf1 = idInfo_logicalPeer.toByteArray();
            // 计算所有字段所需的总字节数
            int size = Integer.BYTES + Byte.BYTES + buf1.length + Integer.BYTES + wBuf.length() * Character.BYTES;

            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.putInt(uiType);
            buffer.put(ucbResp);
            buffer.put(buf1);
            buffer.putInt(uiSerialNo);
            buffer.put(wBuf.getBytes(StandardCharsets.UTF_8));
            return buffer.array();
        }
    }

    public static class PROC_TASK_FILE {                                           //  video

        public Object hWnd_task;                                 //  2009/09/09


        //
        public static class procTaskFile_reply_info {
            public long tStartTime_reply;
            public int uiTranNo_reply;
            //
            public TRANSFER_FILE_REPLYREQ replyReq;

            //
            public long tStartTime_lastFileDataReq;
            public int uiTranNo_lastFileDataReq;

        }

        ;
        public procTaskFile_reply_info reply_info;

        //
        public Object fp;

        //
        public boolean bNeedRemoved;


        public long ui64FileLen;
        //
        public String fileName_transferred; //
        public String fullFilePath_save;    //
        public int uiSizePerSnd_internal;              //

        //
        public int uiLastDataBlockIndex;               //
        public int usErrors;                         //

        //
        public byte ucPercent;                          //

        //
        public int nTimes_toRestart;
    }

    public static class TRANSFER_FILE_REQ extends IM_CONTENTU {
        public long ui64FileLen;
        public String fileName;
        public int uiSizePerSnd_suggest;
        public short usSubtype;
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer;
        public Object pInternal;
        public String fullFilePath;
        public boolean bGrpTask;
        public long dwStartTickCnt;
        public long dwLastReportTickCnt;
        public byte ucPercent;
        public int usErrors;
        public Object hTool_offlineRes;
        public boolean bNeedCalcel;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES + fileName.length() + Integer.BYTES +
                    Integer.BYTES + idInfo_logicalPeer.toByteArray().length + Integer.BYTES + fullFilePath.length() +
                    Integer.BYTES + Integer.BYTES + Byte.BYTES + Integer.BYTES + Integer.BYTES +
                    Integer.BYTES);
            buffer.putInt(uiType);
            buffer.putLong(ui64FileLen);
            buffer.put(fileName.getBytes());
            buffer.putInt(uiSizePerSnd_suggest);
            buffer.putShort(usSubtype);
            buffer.put(idInfo_logicalPeer.toByteArray());
            buffer.put(fullFilePath.getBytes());
            buffer.put((byte) (bGrpTask ? 1 : 0));
            buffer.putLong(dwStartTickCnt);
            buffer.putLong(dwLastReportTickCnt);
            buffer.put(ucPercent);
            buffer.putInt(usErrors);
            buffer.put((byte) (bNeedCalcel ? 1 : 0));
            return buffer.array();
        }
    }

    public static class TRANSFER_FILE_REPLYREQ extends IM_CONTENTU {
        public long tStartTime_org;
        public int uiTranNo_org;
        public int uiSerialNo_org;
        public short usOp;

        public byte[] toByteArray() {
            int size = Short.BYTES + Long.BYTES + 3 * Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);

            buffer.putInt(uiType);
            buffer.putLong(tStartTime_org);
            buffer.putInt(uiTranNo_org);
            buffer.putInt(uiSerialNo_org);
            buffer.putShort(usOp);

            return buffer.array();
        }
    }


    public static class TaskProcReq_vStream {
        //  2014/08/26. 也许这里应该引入一个objType，这样，就可以把清晰的指定要对什么做操作了.在要操作的对象多起来的时候，需要objType来区分下
        public int uiTranNo_openAvDev;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES);
            buffer.putInt(uiTranNo_openAvDev);
            return buffer.array();
        }
    }

    ;


    public static class TaskProcReq_viewDynBmp {
        public int uiObjType;
        public short usIndex_obj;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES + Short.BYTES);
            buffer.putInt(uiObjType);
            buffer.putShort(usIndex_obj);
            return buffer.array();
        }
    }


    public static class QY_MESSENGER_ACCOUNT {
        public int uiType;
        // public String name[cntof_name];
        public String messengerPasswd;//[cntof_messengerPasswd + 1];


    }


    /**
     * 修改任务的包
     */
    public static class TASK_PROC_REQ extends IM_CONTENTU {
        /**
         * 开始的时间戳, 搭配uiTranNo_org 用来标识唯一的请求
         */
        public long tStartTime_org;
        public int uiTranNo_org;
        public int uiSerialNo_org; // 没用
        /**
         * 用来标识原始请求时干啥的
         */
        public int uiContentType_org;
        /**
         * 表明操作: 接受、拒绝、取消等等
         */
        public short usOp;
        public short usTaskProcObjType; // 已弃用
        public TaskProcReq_vStream vStream = new TaskProcReq_vStream(); // 应该无用
        public int uiTaskType; // 应该无用
        public TaskProcReq_viewDynBmp viewDynBmp = new TaskProcReq_viewDynBmp(); // 应该无用
        public Object pInternal = new Object(); // 应该无用
        public Object hWnd_task = new Object(); // 应该无用

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 6 + Short.BYTES * 2 + Long.BYTES + vStream.toByteArray().length +
                    viewDynBmp.toByteArray().length);
            buffer.putInt(uiType);
            buffer.putLong(tStartTime_org);
            buffer.putInt(uiTranNo_org);
            buffer.putInt(uiSerialNo_org);
            buffer.putInt(uiContentType_org);
            buffer.putShort(usOp);
            buffer.putShort(usTaskProcObjType);
            buffer.put(vStream.toByteArray());
            buffer.putInt(uiTaskType);
            buffer.put(viewDynBmp.toByteArray());
            return buffer.array();
        }
    }

    public static class TASK_PROC_RESP extends IM_CONTENTU {
        //
        public long tStartTime_org;                     //  2009/10/19
        public int uiTranNo_org;                      //
        //
        public short usCmd_org;
        //  int										uiContentType_org;
        //
        public short usOp_org;
        //
        public int iStatus;                            //  2009/10/19

        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();                 //  2009/10/18

        //
        public Object pInternal = new Object();

        public boolean bNeedRemoved;

        public byte[] toByteArray() {
            byte[] buf = idInfo_logicalPeer.toByteArray();
            int bufferSize = Integer.BYTES * 3 + Long.BYTES + 3 * Short.BYTES * 2 + buf.length + 1;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType);
            buffer.putLong(tStartTime_org);
            buffer.putInt(uiTranNo_org);
            buffer.putShort(usCmd_org);
            buffer.putShort(usOp_org);
            buffer.putInt(iStatus);
            buffer.put(buf);
            buffer.put((byte) (bNeedRemoved ? 1 : 0));
            return buffer.array();
        }


    }

    public static class TRANSFER_FILEDATA_REQ extends IM_CONTENTU {                                   //  2007/07/15
        //
        public long tStartTime_org;                     //  ÇëÇó·¢ÆðµÄÊ±¼ä, ÕâÊÇÎªÁËÈ·¶¨ÊÇÄÄ¸öÈÎÎñ±»µÃµ½ÁËÔÊÐí
        public int uiTranNo_org;                      //
        public int uiSerialNo_org;                        //  Ã»Ê²Ã´ÓÃ
        //
        public int usOp;
        //
        public int uiSizePerSnd;                      //  Õâ´Î×î¶à·¢ËÍµÄ×Ö½ÚÊý, Ó¦¸ÃºÍReplyReqÖÐµÄÒ»Ñù
        //
        public int uiDataBlockIndex;                  //  µÚ¼¸¿éÊý¾Ý£¬´Ó0¿ªÊ¼

        //
        public Object pInternal = new Object();

        //
        public static class TransferFileDataReq_tmpInternal {

            public long tStartTime_new;
            public int uiTranNo_new;

            //
            public boolean ucbFileEnd;

            public byte[] toByteArray() {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Integer.BYTES + Byte.BYTES);
                buffer.putLong(tStartTime_new);
                buffer.putInt(uiTranNo_new);
                buffer.put((byte) (ucbFileEnd ? 1 : 0));
                return buffer.array();
            }


        }

        ;
        public TransferFileDataReq_tmpInternal tmpInternal = new TransferFileDataReq_tmpInternal();

        public byte[] toByteArray() {
            byte[] buf = tmpInternal.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Integer.BYTES * 6 + buf.length);
            buffer.putInt(uiType);
            buffer.putLong(tStartTime_org);
            buffer.putInt(uiTranNo_org);
            buffer.putInt(uiSerialNo_org);
            buffer.putInt(usOp);
            buffer.putInt(uiSizePerSnd);
            buffer.putInt(uiDataBlockIndex);
            buffer.put(buf);
            return buffer.array();
        }
    }

    public static class TRANSFER_FILEDATA_RESP extends IM_CONTENTU {                                   //  2007/07/15
        public int uiDataBlockIndex;                  //  buf pInternal,
        public int uiLen;
        public ByteBuffer buf = ByteBuffer.allocateDirect(Consts.bufSize_transferFileDataResp);

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + buf.capacity());
            buffer.putInt(uiType);
            buffer.putInt(uiDataBlockIndex);
            buffer.putInt(uiLen);
            buffer.put(buf);
            return buffer.array();
        }

    }

    ;


    public static class TRANSFER_FILEEND_REQ_tmpInternal {
        //  2015/09/07
        public boolean bChkToRemove_i;                       //  2015/09/07
        //
        public boolean bNeedRemoved_o;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES * 2);
            buffer.put((byte) (bChkToRemove_i ? 1 : 0));
            buffer.put((byte) (bNeedRemoved_o ? 1 : 0));
            return buffer.array();
        }

    }

    ;


    public static class TRANSFER_FILEEND_REQ extends IM_CONTENTU {                                   //  2007/07/15
        public long tStartTime_org;
        public int uiTranNo_org;
        public int uiSerialNo_org;
        public TRANSFER_FILEEND_REQ_tmpInternal tmpInternal = new TRANSFER_FILEEND_REQ_tmpInternal();

        public byte[] toByteArray() {
            byte[] buf = tmpInternal.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + Long.BYTES + buf.length);
            buffer.putInt(uiType);
            buffer.putLong(tStartTime_org);
            buffer.putInt(uiTranNo_org);
            buffer.putInt(uiSerialNo_org);
            buffer.put(buf);
            return buffer.array();
        }

    }

    ;

    public static class TRANSFER_FILEEND_RESP_tmpInternal {
        public boolean bChkToRemove_i;
        //
        public boolean bNeedRemoved_o;
    }

    ;


    public static class TRANSFER_FILEEND_RESP extends IM_CONTENTU {                                   //  2007/07/15
        public int uiVerifyMethod;                        //
        public String buf;//[2048];

        //  2015/09/07
        //
        public TRANSFER_FILEEND_RESP_tmpInternal tmpInternal;


    }

    ;


    public static class AEC_cfg {
        boolean bUseAec;                                   //  2013/05/25

        //
        boolean bFeatrModeOn;                              //  2014/08/17
        boolean bNoiseSup;                                 //  2014/08/17
        boolean bAGC;                                      //  2014/08/17
        boolean bCntrClip;                                 //  2014/08/17

        //
        int uiEchoLengthInMs;

    }

    ;

    //
    public static class VIDEO_CONFERENCE_CFG {
        public int iRows;                                      //  ·ÖÊÓÆµ×é³ÉÒ»¸ö¾ØÕó¡£ÐÐÊý
        public int iCols;                                      //  ÁÐÊý

        //
        public int usMaxSpeakers;                               //  2009/02/19


        //
        public AEC_cfg aecCfg;

    }

    ;

    public static class VIDEO_COMPRESSOR_CFG {
        public qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon common = new qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon();
        public EGLContext shared_eglContext;
        public int index_sharedObj;
        public boolean m_bFront;
    }

    ;


    public static class AUDIO_COMPRESSOR_CFG {
        //  unsigned  char										ucCompressors;								//  acm or dmo
        public qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon common = new qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon();
        //TCHAR compressorName[64];                           //  2009/05/16

        //
        public qyq2.QY_Q2 pAudioQ2;
        public Object pParent_transform;


        //
        public qyCommProc_open.WAVEFORMATEX wf_compress = new qyCommProc_open.WAVEFORMATEX();
        public qyCommProc_open.WAVEFORMATEX wf_decompress_default = new qyCommProc_open.WAVEFORMATEX();

        //
        //  GUID												dmoClsid;

        //AUDIO_ENC_PARAMS									params;

        //
        public int uiBytesRecorded_perBlockAlign;                //  ÒôÆµµÄ¶ÔÆëµ¥ÔªµÄ»º³å´óÐ¡


        //
        public qnmCommProc_is_open.AUDIO_PLAY_CFG playCfg;                                    //  2010/03/11

        //
        //  int										uiRecordType;								//  2010/08/02

    }


    public static class AV_COMPRESSOR_CFG {                                           //  2009/05/21
        public byte ucSeconds_perFrame_videoConference;            //
        public int usFrames_perSecond_videoConference;          //

        public AUDIO_COMPRESSOR_CFG audio = new AUDIO_COMPRESSOR_CFG();
        public VIDEO_COMPRESSOR_CFG video = new VIDEO_COMPRESSOR_CFG();
    }

    public static class AV_tranInfo_head {
        public short shClientVer;            // 客户端版本号的高4位  //  2014/09/23. 客户端版本号的高位

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Short.BYTES);
            buffer.putShort(shClientVer);
            return buffer.array();
        }

    }

    public static class CONF_hg_info {
        public long ui64MeetingId_hg;  // 汇款系统的会议ID      //  2023/04/04
        public int iMeetingType_hg;    // 会议类型

        public int iMeetingLevel_hg;   // 会议等级
        public int iMeetingDurationInMin;      // 会议时长（分钟）

        public long ui64_meetingStartTime;  // 会议起始时间
        //TCHAR meetingCompere[64];

        public boolean bRecord; // 是否录制会议
        public boolean bShare_2ndStream; // 是否双流

        public boolean bFlow512k; // 是否采用512K


        //
        public static class Hg_tmpInternal {
            public int cnt_shareMems;

            public byte[] toByteArray() {
                ByteBuffer buf = ByteBuffer.allocateDirect(Integer.BYTES);
                buf.putInt(cnt_shareMems);
                return buf.array();
            }
        }

        public Hg_tmpInternal tmpInternal = new Hg_tmpInternal();


        public byte[] toByteArray() {
            ByteBuffer buf = ByteBuffer.allocateDirect(Long.BYTES + Integer.BYTES * 4 + 3);
            buf.putLong(ui64MeetingId_hg);
            buf.putInt(iMeetingType_hg);
            buf.putInt(iMeetingLevel_hg);
            buf.putInt(iMeetingDurationInMin);
            buf.putLong(ui64_meetingStartTime);
            buf.put((byte) (bRecord ? 1 : 0));
            buf.put((byte) (bShare_2ndStream ? 1 : 0));
            buf.put((byte) (bFlow512k ? 1 : 0));
            buf.put(tmpInternal.toByteArray());
            return buf.array();
        }
    }

    ;


    //
    public static class TRAN_conf_cfg_conf {
        public int usMaxFps_toShareBmp_conf;     // 视频的最大帧率                      //  2009/04/19
        public int iWidth_pic_conf;              // 视频的宽                      //  2014/08/15
        public int iHeight_pic_conf;             // 视频的高                      //  2014/08/15
        public int conf_iFourcc;                 // 视频的编码，例如 265、264
        //int											conf_bitrateInKbps_dl;
        public int conf_bitrateInKbps_ul;								//  对参会方，只需要知道上传码流


        public static class TRAN_conf_cfg_conf_confCtrl {
            //
            public byte ucbConfCtrl_starter; // 可能是发起方

            public byte[] toByteArray() {
                return new byte[]{ucbConfCtrl_starter};
            }
        }

        public TRAN_conf_cfg_conf_confCtrl confCtrl = new TRAN_conf_cfg_conf_confCtrl();

        public byte[] toByteArray() {
            ByteBuffer buf = ByteBuffer.allocateDirect(Integer.BYTES * 4 + confCtrl.toByteArray().length);
            buf.putInt(usMaxFps_toShareBmp_conf);
            buf.putInt(iWidth_pic_conf);
            buf.putInt(iHeight_pic_conf);
            buf.putInt(conf_iFourcc);
            buf.put(confCtrl.toByteArray());
            return buf.array();
        }
    }

    public static class TRAN_conf_cfg {

        public int usMaxSpeakers; // 最多几个发言人,发言前做判断

        public TRAN_conf_cfg_conf conf = new TRAN_conf_cfg_conf(); // 视频的一些信息（帧率、宽高、编码）

        public byte ucbVideoConference; // 是否是组会议

        //  2017/07/17
        //public byte ucbAvConsole;

        //  2017/08/25
        public int usConfType; // 暂时没用

        public qyDefs.QY_MESSENGER_ID idInfo_initiator = new qyDefs.QY_MESSENGER_ID(); // 会议发起方（召集人）的id

        public CONF_hg_info hgInfo = new CONF_hg_info();  // 汇款信息

        public byte[] toByteArray() {
            ByteBuffer buf = ByteBuffer.allocateDirect(Integer.BYTES + Byte.BYTES + Integer.BYTES + Long.BYTES + hgInfo.toByteArray().length);
            buf.putInt(usMaxSpeakers);
            buf.put(conf.toByteArray());
            buf.put(ucbVideoConference);
            buf.putInt(usConfType);
            buf.put(idInfo_initiator.toByteArray());
            buf.put(hgInfo.toByteArray());
            return buf.array();
        }
    }

    public static class IpcProc {
        public qyCommProc_open.VH_stream vh_stream = new qyCommProc_open.VH_stream();                                            //  2014/02/25
        public Op op = new Op();                                                                   //
        public qyCommProc_open.QY_VIDEO_HEADER vh_decompress = new qyCommProc_open.QY_VIDEO_HEADER();
    }

    ;

    public static class dev {
        public NvrInfo nvrInfo;
    }

    ;

    public static class Op {
        public qyDefs.QY_MESSENGER_ID idInfo;
        public NvrInfo nvrInfo;
        public int iChannel;
        public int nID;
        public byte ucMouseStatus;
    }

    public static class TRAN_video {
        public int uiTranNo_openAvDev; // 唯一值，用来标识打开视频设备的序号
        public qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon_head compressor = new qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon_head(); // 视频压缩信息
        public qyCommProc_open.QY_VIDEO_HEADER vh_compress = new qyCommProc_open.QY_VIDEO_HEADER(); // 视频压缩格式
        public qyCommProc_open.VH_stream vh_stream = new qyCommProc_open.VH_stream(); // 编码格式的一些信息
        public qyCommProc_open.QY_VIDEO_HEADER vh_decompress = new qyCommProc_open.QY_VIDEO_HEADER(); // 原始格式（压缩前）
    }

    public static class TRAN_audio {
        public int uiTranNo_openAvDev; // 唯一性的值，用来标识打开音频设备的序号
        public qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon_head compressor = new qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon_head(); // 音频压缩信息
        public qyCommProc_open.QY_AUDIO_HEADER ah_compress = new qyCommProc_open.QY_AUDIO_HEADER(); // 音频压缩格式
        public qyCommProc_open.AH_stream ah_stream = new qyCommProc_open.AH_stream(); // 应该暂时弃用
        public qyCommProc_open.QY_AUDIO_HEADER ah_decompress = new qyCommProc_open.QY_AUDIO_HEADER(); // 原始格式

        public byte[] toByteArray() {

            byte[] ahCompressBytes = ah_compress.toByteArray();
            byte[] ahDecompressBytes = ah_decompress.toByteArray();

            int totalLength = Integer.BYTES + ahCompressBytes.length + ah_stream.toByteArray().length + ahDecompressBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.putInt(uiTranNo_openAvDev);

            buffer.put(ahCompressBytes);
            buffer.put(ah_stream.toByteArray());
            buffer.put(ahDecompressBytes);

            return buffer.array();
        }
    }

    public static class TASK_v_props {
        public byte ucAvFlg; // 标志位，代表 是否无缩放 例如屏幕共享不能缩放,

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES);
            buffer.put(ucAvFlg);
            return buffer.array();
        }
    }

    public static class TASK_av_props {
        public TASK_v_props v = new TASK_v_props();

        public byte[] toByteArray() {
            byte[] buf = v.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(buf.length);
            buffer.put(buf);
            return buffer.array();
        }
    }

    public static class AV_TRAN_INFO {
        public AV_tranInfo_head head = new AV_tranInfo_head(); // 视频头信息
        public TRAN_audio audio = new TRAN_audio(); // 音频格式
        public TRAN_video video = new TRAN_video(); // 视频格式
        public TASK_av_props taskAvProps = new TASK_av_props(); // 视频的（扩展）属性
    }

    public static class MOSAIC_TRAN_INFO {

        public RES_obj_simple resObj = new RES_obj_simple(); // 资源类型
        //
        public TRAN_video video = new TRAN_video();
    }

    public static class TRANSFER_AV_INFO_viewDynBmp {
        //  2016/07/24
        //public RES_obj_simple resObj;
        //
        public byte ucbSaveVideo;  // 是否存储                //  2014/08/04
    }

    public static class PROXIED_TRAN_INFO {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public int uiObjType;
        public AV_TRAN_INFO ti = new AV_TRAN_INFO();
    }

    public static class TRANSFER_AV_INFO_tmpInternal_streamInfo {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public int uiObjType;
        public int usIndex_obj;
    }

    /**
     * 发起的包
     */
    public static class TRANSFER_AV_INFO extends IM_CONTENTU {
        //
        public byte ucbResp;
        /**
         * 任务类型：
         * public final static int CONST_imTaskType_transferAvInfo = 25;  // 会议类型（点对点、组）
         * public final static int CONST_imTaskType_shareScreen = 26;     //  共享屏幕
         * public final static int CONST_imTaskType_remoteAssist = 27;    // 远程协助 (操作屏幕)
         * public final static int CONST_imTaskType_shareMediaFile = 28;  // 共享媒体文件
         * public final static int CONST_imTaskType_shareDynBmp = 29;     // 共享视频源
         * public final static int CONST_imTaskType_viewDynBmp = 30;      // 查看视频源
         * public final static int CONST_imTaskType_queryCustomerServiceOfficer = 31;  // 视频客服（已弃用）
         * public final static int CONST_imTaskType_shareGps = 40;        // 共享Gps
         * public final static int CONST_imTaskType_viewGps = 41;         // 查看Gps
         */
        public int uiTaskType;                            //

        //
        public int												shareDevice_grp_index;				//  查看设备分组

        /**
         * 汇款信息
         */
        public TRAN_conf_cfg confCfg = new TRAN_conf_cfg();

        //
        //public AV_TRAN_INFO	tranInfo;
        /**
         * 音视频信息（格式）
         */
        public AV_stream_simple ass = new AV_stream_simple();

        //  2014/11/07
        /**
         * 视频组合的信息
         * 1. video -> 发言人（视频）
         * 2. resource -> 屏幕共享
         */
        public MOSAIC_TRAN_INFO confMosaicTranInfo_video = new MOSAIC_TRAN_INFO();

        public MOSAIC_TRAN_INFO confMosaicTranInfo_resource = new MOSAIC_TRAN_INFO();

        /**
         * 属于TRANSFER_AV_INFO的一个子集，查看远程视频源的一些扩展信息
         */
        public TRANSFER_AV_INFO_viewDynBmp viewDynBmp = new TRANSFER_AV_INFO_viewDynBmp();                          //  2009/09/06

        /**
         * Talk的号，IdInfo
         */
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();                    //  2009/10/18

        public int uiTranNo_avTask_starter; // 暂时弃用          //  2014/11/22. 这个tranNo用来在会议和共享资料等任务里建立关联。

        //  2014/11/20
        public PROXIED_TRAN_INFO proxiedTranInfo = new PROXIED_TRAN_INFO(); // 弃用

        /**
         * 无用，用来标志上面的信息是发送的，下面的字段是临时（不发送的）
         */
        public Object pInternal = new Object();

        /**
         * 区分这个实时任务是不是组的还是联系人的
         */
        public boolean bGrpTask;

        public int tmp_usSubtype; // 已弃用

        // 记录一些信息
        public TRANSFER_AV_INFO_tmpInternal_streamInfo tmp_internal_streamInfo = new TRANSFER_AV_INFO_tmpInternal_streamInfo();

        public int tmp_internal_usCnt_consult_mems; // 暂时没用到,已弃用

        public AV_stream tmp_internal_tmpAs = new AV_stream(); // 临时记录的一些信息
    }

    public static class TransferAvReplyInfo_viewDynBmpReply {
        public byte ucbSaveVideo; // 是否存储视频
    }

    public static class TransferAvReplyInfo_tmp_internal {
        public AV_stream tmpAs;
    }

    /**
     * 响应的包
     */
    public static class TRANSFER_AV_replyInfo extends IM_CONTENTU {                                   //  2007/07/06
        /**
         * tStartTime_org 和 uiTranNo_org 唯一表示一个包
         */
        public long tStartTime_org; // 发送的时间(秒级时间戳)
        public int uiTranNo_org; // 发送的序号
        public short usOp; // 表明会议（我）拒绝、允许
        public AV_TRAN_INFO tranInfo = new AV_TRAN_INFO();
        public TransferAvReplyInfo_viewDynBmpReply viewDynBmpReply = new TransferAvReplyInfo_viewDynBmpReply(); // 远程查看视频的时候响应包的一个辅助结构
        public Object pInternal = new Object(); // 标志位,上面的发送，下面的不发送，无用
        public int tmp_usSubtype;
        public TransferAvReplyInfo_tmp_internal tmp_internal = new TransferAvReplyInfo_tmp_internal();

    }

    public static class AV_stream_simple {
        public RES_obj_simple resObj = new RES_obj_simple(); // 视频源类型
        public AV_TRAN_INFO tranInfo = new AV_TRAN_INFO(); // 音视频格式
    }

    public static class AV_stream {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public AV_stream_simple obj = new AV_stream_simple();
    }

    /**
     * 申请发言的包
     */
    public static class TASK_INTERACTION_REQ extends IM_CONTENTU {
        /**
         * tStartTime_org（开始时间戳） + uiTranNo_org 用来标识唯一包
         */
        public long tStartTime_org;
        public int uiTranNo_org;
        /**
         * 申请发言操作：申请、取消
         */
        public short usOp;
        /**
         * 发言人的音视频参数
         */
        public AV_stream avStream = new AV_stream();
        /**
         * 发言人的显示信息(会场名称)
         */
        public String talkerDesc;

        public int iHkStatus = 0;

        /**
         * 一个请求里发送的时候，需要告诉这个请求时哪个组的
         * 用于标识这个发言请求时归哪个组的
         */
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        public Object pInternal = new Object(); // 无用
    }


    public static class IM_TASK_RCD {

        public int id;

        public String misServName;
        public qyDefs.QY_MESSENGER_ID idInfo_send = new qyDefs.QY_MESSENGER_ID();
        public String senderDesc;

        public long tSendTime;
        public int uiTranNo;
        public int usSeqNo;

        public int uiContentType;
        public long tStartTime_serv;
        public int uiSerialNo;

        public qyDefs.QY_MESSENGER_ID idInfo_recv = new qyDefs.QY_MESSENGER_ID();
        public String receiverDesc;
        public int iRole;

        public long tRecvTime;
        public int usRespCode;

        public int ibReply;

        public int uiType;
        public int uiMode;

        public String subTypeStr;
        public String subject;
        public String content;
        public String txtContent;

        public int ibNeedReply;
        public long tEndTime;

        public String issuer;
        public long tIssueTime;

        public long tStartTime_req;
        public int uiTranNo_req;

        public long firstTime;
        public long lastTime;
        public int iStatus;
        public long tLastModifiedTime;

        public qyDefs.QY_MESSENGER_ID idInfo_auditor = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_replyTo = new qyDefs.QY_MESSENGER_ID();

        public int iProp0;
        public String propStr0;

        public void copyData(IM_TASK_RCD rcd) {
            rcd.id = this.id;
            rcd.misServName = this.misServName;
            rcd.idInfo_send.ui64Id = this.idInfo_send.ui64Id;
            rcd.senderDesc = this.senderDesc;
            rcd.tSendTime = this.tSendTime;
            rcd.uiTranNo = this.uiTranNo;
            rcd.usSeqNo = this.usSeqNo;
            rcd.uiContentType = this.uiContentType;
            rcd.tStartTime_serv = this.tStartTime_serv;
            rcd.uiSerialNo = this.uiSerialNo;
            rcd.idInfo_recv.ui64Id = this.idInfo_recv.ui64Id;
            rcd.receiverDesc = this.receiverDesc;
            rcd.iRole = this.iRole;
            rcd.tRecvTime = this.tRecvTime;
            rcd.usRespCode = this.usRespCode;
            rcd.ibReply = this.ibReply;
            rcd.uiType = this.uiType;
            rcd.uiMode = this.uiMode;
            rcd.subTypeStr = this.subTypeStr;
            rcd.subject = this.subject;
            rcd.content = this.content;
            rcd.txtContent = this.txtContent;
            rcd.ibNeedReply = this.ibNeedReply;
            rcd.tEndTime = this.tEndTime;
            rcd.issuer = this.issuer;
            rcd.tIssueTime = this.tIssueTime;
            rcd.tStartTime_req = this.tStartTime_req;
            rcd.uiTranNo_req = this.uiTranNo_req;
            rcd.firstTime = this.firstTime;
            rcd.lastTime = this.lastTime;
            rcd.iStatus = this.iStatus;
            rcd.tLastModifiedTime = this.tLastModifiedTime;
            rcd.idInfo_auditor.ui64Id = this.idInfo_auditor.ui64Id;
            rcd.idInfo_replyTo.ui64Id = this.idInfo_replyTo.ui64Id;
            rcd.iProp0 = this.iProp0;
            rcd.propStr0 = this.propStr0;
        }

    }

    public static class IM_L_STREAM extends IM_CONTENTU {

        public int uiContentType;
        public int ulStreamLen;
        public ByteBuffer buf = ByteBuffer.allocate(bufSize_imLStream + 1);
    }

    //public static class IM_L_STREAM_nh extends IM_L_STREAM
    public static class IM_L_STREAM_nh extends IM_CONTENTU
    {
        public int uiContentType;
        public int ulStreamLen;
        //  buf[1]
        public ByteBuffer p;
    }

    public static class QIS_INTERVAL_PARAMS {                                       //  2005/06/10
        public short usIntervalInS_refreshRecentFriends;
        public short usIntervalInS_refreshContactList;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 2);
            buffer.putShort(usIntervalInS_refreshRecentFriends);
            buffer.putShort(usIntervalInS_refreshContactList);
            return buffer.array();
        }

    }

    public static class QIS_getCfgs_req extends IM_CONTENTU {

        public byte ucbResp;

        public int uiSizePerSnd_media;

        public QIS_INTERVAL_PARAMS intervalParams = new QIS_INTERVAL_PARAMS();


        public byte[] toByteArray() {
            int offset = 3;
            byte[] buf = intervalParams.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + 1 + buf.length + offset);

            buffer.putInt(uiType);
            buffer.put(ucbResp);
            buffer.putInt(uiSizePerSnd_media);
            buffer.put(buf);
            return buffer.array();
        }

    }

    public static class RETRIEVE_IMOBJLIST_REQ extends IM_CONTENTU {
        public short usMaxContactUpdateInfosPerSnd;               //  Ò»´Î¿ÉÒÔÏÂÔØ×î¶à¶àÉÙ¸öÁªÏµÈË¸üÐÂÐÅÏ¢
        public long tLastRefreshedTime_contactList_misServ;     //  ÉÏ´ÎµÄË¢ÐÂÊ±¼ä£¬Îª0Ê±£¬±íÃ÷È«ÃæË¢ÐÂ
        //
        public byte ucbNext;                                  //  ÊÇ·ñ¼ÌÐøÇëÇó
        public qyDefs.QY_MESSENGER_ID idInfo_grp_lastRefreshed = new qyDefs.QY_MESSENGER_ID();                   //  ÉÏ´ÎË¢ÐÂµÄ×éID£¬½öÓÃÔÚË¢ÐÂ³ÉÔ±¹ØÏµ¡£
        public qyDefs.QY_MESSENGER_ID idInfo_lastRefreshed = new qyDefs.QY_MESSENGER_ID();                       //  ÉÏ´ÎµÄË¢ÐÂ½Úµã
    }

    public static class RETRIEVE_IMGRPLIST_RESP extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo_lastRefreshed;
        public byte ucbEnd;
        public short usCnt;
        public IM_GRP_INFO[] mems = new IM_GRP_INFO[CONST_maxContactUpdateInfos_grp];
        Object pInternal;
        public int index;

    }

    public static class RETRIEVE_IMGRPMEMLIST_RESP extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo_grp_lastRefreshed;
        public qyDefs.QY_MESSENGER_ID idInfo_lastRefreshed;
        public byte ucbEnd;

        public short usCnt;
        public qnmCommProc_mis.IM_GRP_MEM[] mems = new IM_GRP_MEM[CONST_maxContactUpdateInfos_grpMem];

        Object pInternal;
        public int index;

    }

    public static class RETRIEVE_contact_mem {
        public QY_MESSENGER_REGINFO regInfo = new QY_MESSENGER_REGINFO();
        public int iRole;
    }

    public static class RETRIEVE_CONTACTLIST_RESP extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo_lastRefreshed;
        public byte ucbEnd;
        public short usCnt;
        public RETRIEVE_contact_mem[] mems = new RETRIEVE_contact_mem[CONST_maxContactUpdateInfos_contact];
        Object pInternal;
        public int index;
        public int iFiledIndex;

    }

    public static class RETRIEVE_IMOBJLISTEND_RESP extends IM_CONTENTU {                                           //  ½áÊø°ü
        //
        public long tLastRefreshedTime_contactList_misServ;
        //
    }

    public static class IM_CONTENTU {
        public int uiType; // 类型，视频包、音频包
    }

    public static class CIM_CONTENTU {
        public IM_CONTENTU imContentU;

    }

    public static class IM_CONTENTU_nh {                                   //  2010/07/29
        public int uiType;                             //
        public IM_SIMPLE_RESP simpleResp;
        public IM_L_STREAM_nh lStream;
    }

    ;

    public static class QIS_EVENT {
        int id;
        int lHelpRand;                                     //	2003/11/15
        //
        String misServName;//[CONST_maxMisServNameLen + 1];
        qyDefs.QY_MESSENGER_ID idInfo;
        //
        ByteBuffer detectedIp = ByteBuffer.allocateDirect(16);//[15 + 1];                            //  Í¨ÐÅ¼ì²âµ½µÄ¿Í»§¶ËIP
        ByteBuffer ip = ByteBuffer.allocateDirect(16);//[15 + 1];                                    //  ip must be filled before qnmLogPcEvent
        int lEventType;                                        //  eventType must be filled before qnmLogPcEvent
        int iCustomId;                                      //  iCustomId must be filled before qnmLogPcEvent
        ByteBuffer localEventTime = ByteBuffer.allocateDirect(15);//[14 + 1];                                    //  localEventTime must be filled before qnmLogPcEvent
        ByteBuffer localCntEventTime = ByteBuffer.allocateDirect(15);//[14 + 1];                             //  ´«Èë¹ÜÀíÖÐÐÄµÄ±¾µØµÄÊ±¼ä
        ByteBuffer cntEventTime = ByteBuffer.allocateDirect(15);//[14 + 1];                                  //  ´«Èë¹ÜÀíÖÐÐÄµÄ¹ÜÀíÖÐÐÄµÄÊ±¼ä
        String comment;//[255 + 1];
        //char cols[CONST_qnmMaxEventRegFields][255  +  1];	//  ÊÂ¼þ´¥·¢ÕßµÄÐÅÏ¢10ÁÐ
        String usrName;//[255 + 1];
        //
        //TCHAR deses[CONST_qnmMaxEventDesFields][255  +  1];	//  ÊÂ¼þÃèÊö±¾ÉíµÄÐÅÏ¢10ÁÐ
        String dsnName;//[128 + 1];                                //  2007/01/02
        int iStatus;                                        //  2006/06/02, Ôö¼Ó¶ÔÊÂ¼þµÄ´¦Àí×´Ì¬µÄÃèÊö
    }


    public static class MIS_MSG_TALKDATA {
        public MSG_SIMPLE_ROUTE route; //  TALKDATA_ADDR									addr;
        //
        //String buf[sizeof(IM_CONTENTU) + 1024];   //  ×¢Òâ£¬ÔÚ·ÅÈë¶ÓÁÐÀï£¬²»ÒªÓÃsizeof(  data  ),  Ó¦¸ÃÊ¹ÓÃoffsetof(  ,  data  )  +  dataLen, ÒÔ½ÚÔ¼ÄÚ´æ
        public ByteBuffer buf;//[bufSize_talkData];
    }

    public static class MIS_MSG_routeTalkData {
        public MSG_ROUTE route = new MSG_ROUTE();
        public IM_CONTENTU buf = new IM_CONTENTU();

        public byte[] toByteArray() {
            byte[] routeBytes = route.toByteArray();

            int size = routeBytes.length;// + buf.capacity();
            ByteBuffer buffer = ByteBuffer.allocate(size);

            buffer.put(routeBytes);
//            buf.flip();
//            buffer.put(buf);

            return buffer.array();
        }
    }

    public static class MIS_MSG_TALK extends MIS_MSGU {                                   //  2007/11/29, addressable talk
        //
        public int uiCliIndex;                           //  input daemon
        //  2008/01/05, vDev_coms uiCliIndex

        public int uiSessionId;
        public int uiStep;                                //  2008/04/28

        public boolean ucbConvrted;                      //  input 2007/7/14

        public boolean ucbConvrted_msgRoute;             //  daemon input
        public int lenInBytes_msgRouteInDataBuf;        //  daemon input data.route route data.buf
        //  lenInBytes_msgRouteInDataBuf data.buf route

        //  mc MisCnt qmd uiCliIndex, 2007/06/10
        //Object pMisCnt;                          //  misCntµÄ, 2007/06/02
        public MSGR_ADDR addr_logicalPeer = new MSGR_ADDR();                 //  messenger.

        //
        public byte ucFlg;
        //
        public short usCode;
        public int usCode_org;                          //
        public long tStartTime;
        public int uiTranNo;
        public short usSeqNo;

        public long tStartTime_processed;                //  iocp uiTranNo_mgr iocp
        public int uiTranNo_processed;
        public int nTimes_tryToSend;                  //  mgr iocp
        //
        public long tTime_alive;                     //  2014/12/09. 当没有找到新的视频数据时，刷新此变量，也可以表明任务是活跃的
        //
        public long tTime_applyForChkTaskAlive;          //  2010/10/30
        public byte nTimes_applyForChkTaskAlive;      //  2014/12/09

        //
        public int uiTaskMode_unused;                 //  2009/08/23

        //
        public int iTaskId;                            //
        public int uiTaskType;                            //
        public qyDefs.QY_MESSENGER_ID idInfo_taskSender = new qyDefs.QY_MESSENGER_ID();                  //
        public qyDefs.QY_MESSENGER_ID idInfo_taskReceiver = new qyDefs.QY_MESSENGER_ID();                //
        public int iStatus;                            //  2007/07/18
        public long tLastModifiedTime;                   //  2014/05/01

        //
        public long tRecvTime;                           //
        public int dwTickCnt_start;                  //  ( ms ).
        public int dwTickCnt_toSend;                 //  2011/01/22. start.toSendQ
        //
        public int usElapsed_recved;                    //
        public int usElapsed_serv_startToProcess;       //  mtMgrThreadProc_is
        public int index_debugInfo;                    //  debugInfo index, debugInfo 2008/11/19

        //
        public int uiChannelType;                     //  2008/04/17,
        //
        public int uiChannelType_input;               //  2017/10/03

        //
        public int lenInBytes;
        //
        public MIS_MSG_routeTalkData data = new MIS_MSG_routeTalkData();                             //  sizeof(  data  ),  offsetof(  ,  data  )  +  offsetof(  MIS_MSG_TALKDATA,  buf  )  +  bufLen

        /*
        public byte[] toByteArray() {
            int bufferSize = Integer.BYTES * 26 + 1 * 5 + Long.BYTES * 8 +
                    addr_logicalPeer.toByteArray().length +
                    idInfo_taskSender.toByteArray().length +
                    idInfo_taskReceiver.toByteArray().length +
                    data.toByteArray().length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType);
            buffer.putInt(uiCliIndex);
            buffer.putInt(uiSessionId);
            buffer.putInt(uiStep);
            buffer.put((byte) (ucbConvrted ? 1 : 0));
            buffer.put((byte) (ucbConvrted_msgRoute ? 1 : 0));
            buffer.putInt(lenInBytes_msgRouteInDataBuf);
            buffer.put(addr_logicalPeer.toByteArray());
            buffer.put(ucFlg);
            buffer.putInt(usCode);
            buffer.putInt(usCode_org);
            buffer.putLong(tStartTime);
            buffer.putInt(uiTranNo);
            buffer.putInt(usSeqNo);
            buffer.putLong(tStartTime_processed);
            buffer.putInt(uiTranNo_processed);
            buffer.putInt(nTimes_tryToSend);
            buffer.putLong(tTime_alive);
            buffer.putLong(tTime_applyForChkTaskAlive);
            buffer.put(nTimes_applyForChkTaskAlive);
            buffer.putInt(uiTaskMode_unused);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            buffer.put(idInfo_taskSender.toByteArray());
            buffer.put(idInfo_taskReceiver.toByteArray());
            buffer.putInt(iStatus);
            buffer.putLong(tLastModifiedTime);
            buffer.putLong(tRecvTime);
            buffer.putInt(dwTickCnt_start);
            buffer.putInt(dwTickCnt_toSend);
            buffer.putInt(usElapsed_recved);
            buffer.putInt(usElapsed_serv_startToProcess);
            buffer.putInt(index_debugInfo);
            buffer.putInt(uiChannelType);
            buffer.putInt(uiChannelType_input);
            buffer.putInt(lenInBytes);
            buffer.put(data.toByteArray());
            return buffer.array();
        }

         */

    }

    public static class MIS_MSG_INPUT extends MIS_MSGU {
        public int uiCliIndex;
        public int uiSessionId;
        public int uiStep;
        public boolean ucbConvrted;
        public boolean ucbConvrted_msgRoute;
        public short lenInBytes_msgRouteInDataBuf;
        public Object pMisCnt;
        public MSGR_ADDR addr_logicalPeer = new MSGR_ADDR();
        public byte ucFlg;
        public short usCode;
        public short usCode_org;
        public long tStartTime;
        public int uiTranNo;
        public short usSeqNo;
        public long tStartTime_processed;
        public int uiTranNo_processed;
        public int nTimes_tryToSend;
        public long tTime_alive;
        public long tTime_applyForChkTaskAlive;
        public byte nTimes_applyForChkTaskAlive;
        public int uiTaskMode_unused;
        public int iTaskId;
        public int uiTaskType;
        public qyDefs.QY_MESSENGER_ID idInfo_taskSender = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_taskReceiver = new qyDefs.QY_MESSENGER_ID();
        public int iStatus;
        public long tLastModifiedTime;
        public boolean bFrom_sharedObj;
        public int index_sharedObj;

        public long tRecvTime;

        public long dwTickCnt_start;
        public long dwTickCnt_toSend;
        public short usElapsed_recved;
        public short usElapsed_serv_startToProcess;
        public int index_debugInfo;
        public int uiChannelType;
        public int uiChannelType_input;
        public int lenInBytes;
        public MIS_MSG_routeTalkData data = new MIS_MSG_routeTalkData();
//        public ByteBuffer buffer = ByteBuffer.allocate(409600);

        //
        public int dbg_sn;


        //
        public void copyDataUntilData(MIS_MSG_INPUT input) {
            // parent
            input.uiType = this.uiType;
            input.tmpID = this.tmpID;
            // self
            input.uiCliIndex = this.uiCliIndex;
            input.uiSessionId = this.uiSessionId;
            input.uiStep = this.uiStep;
            input.ucbConvrted = this.ucbConvrted;
            input.ucbConvrted_msgRoute = this.ucbConvrted_msgRoute;
            input.lenInBytes_msgRouteInDataBuf = this.lenInBytes_msgRouteInDataBuf;
            input.pMisCnt = this.pMisCnt;
            input.addr_logicalPeer = this.addr_logicalPeer;
            input.ucFlg = this.ucFlg;
            input.usCode = this.usCode;
            input.usCode_org = this.usCode_org;
            input.tStartTime = this.tStartTime;
            input.uiTranNo = this.uiTranNo;
            input.usSeqNo = this.usSeqNo;
            input.tStartTime_processed = this.tStartTime_processed;
            input.uiTranNo_processed = this.uiTranNo_processed;
            input.nTimes_tryToSend = this.nTimes_tryToSend;
            input.tTime_alive = this.tTime_alive;
            input.tTime_applyForChkTaskAlive = this.tTime_applyForChkTaskAlive;
            input.nTimes_applyForChkTaskAlive = this.nTimes_applyForChkTaskAlive;
            input.uiTaskMode_unused = this.uiTaskMode_unused;
            input.iTaskId = this.iTaskId;
            input.uiTaskType = this.uiTaskType;
            input.idInfo_taskSender = this.idInfo_taskSender;
            input.idInfo_taskReceiver = this.idInfo_taskReceiver;
            input.iStatus = this.iStatus;
            input.tLastModifiedTime = this.tLastModifiedTime;
            input.bFrom_sharedObj = this.bFrom_sharedObj;
            input.index_sharedObj = this.index_sharedObj;
            input.tRecvTime = this.tRecvTime;
            input.dwTickCnt_start = this.dwTickCnt_start;
            input.dwTickCnt_toSend = this.dwTickCnt_toSend;
            input.usElapsed_recved = this.usElapsed_recved;
            input.usElapsed_serv_startToProcess = this.usElapsed_serv_startToProcess;
            input.index_debugInfo = this.index_debugInfo;
            input.uiChannelType = this.uiChannelType;
            input.uiChannelType_input = this.uiChannelType_input;
            input.lenInBytes = this.lenInBytes;

            //
            input.dbg_sn=this.dbg_sn;
        }

        /*
        public byte[] toByteArray1() {
            byte[] buf4 = null;
            int data_size = 0;
            if (data != null) {
                buf4 = data.toByteArray();
                data_size = buf4.length;
            }
            ByteBuffer buffer = ByteBuffer.allocate(409600 + data_size);
            buffer.putInt(uiType);
            buffer.putInt(uiCliIndex);
            buffer.putInt(uiSessionId);
            buffer.putInt(uiStep);
            buffer.put((byte) (ucbConvrted ? 1 : 0));
            buffer.put((byte) (ucbConvrted_msgRoute ? 1 : 0));
            buffer.putInt(lenInBytes_msgRouteInDataBuf);
            if (addr_logicalPeer != null) {
                byte[] buf1 = addr_logicalPeer.toByteArray();
                buffer.put(buf1);
            }
            buffer.put(ucFlg);
            buffer.putShort(usCode);
            buffer.putShort(usCode_org);
            buffer.putLong(tStartTime);
            buffer.putInt(uiTranNo);
            buffer.putShort(usSeqNo);
            buffer.putLong(tStartTime_processed);
            buffer.putInt(uiTranNo_processed);
            buffer.putInt(nTimes_tryToSend);
            buffer.putLong(tTime_alive);
            buffer.putLong(tTime_applyForChkTaskAlive);
            buffer.put(nTimes_applyForChkTaskAlive);
            buffer.putInt(uiTaskMode_unused);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            if (idInfo_taskSender != null) {
                byte[] buf2 = idInfo_taskSender.toByteArray();
                buffer.put(buf2);
            }
            if (idInfo_taskReceiver != null) {
                byte[] buf3 = idInfo_taskReceiver.toByteArray();
                buffer.put(buf3);
            }
            buffer.putInt(iStatus);
            buffer.putLong(tLastModifiedTime);
            buffer.putLong(tRecvTime);
            buffer.putLong(dwTickCnt_start);
            buffer.putLong(dwTickCnt_toSend);
            buffer.putInt(usElapsed_recved);
            buffer.putInt(usElapsed_serv_startToProcess);
            buffer.putInt(index_debugInfo);
            buffer.putInt(uiChannelType);
            buffer.putInt(uiChannelType_input);
            buffer.putInt(lenInBytes);
            if (buf4 != null) {
                buffer.put(buf4);
            }
            buffer.flip();
            ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
            return buf.array();
        }

         */
    }

    public static class MIS_MSG_TASK extends MIS_MSGU {
        public int uiCliIndex;
        public int uiSessionId;
        public int uiStep;
        public boolean ucbConvrted;
        public boolean ucbConvrted_msgRoute;
        public int lenInBytes_msgRouteInDataBuf;
        public MSGR_ADDR addr_logicalPeer = new MSGR_ADDR();
        public byte ucFlg;
        public short usCode;
        public short usCode_org;
        public long tStartTime;
        public int uiTranNo;
        public short usSeqNo;
        public long tStartTime_processed;
        public int uiTranNo_processed;
        public int nTimes_tryToSend;
        public long tTime_alive;
        public long tTime_applyForChkTaskAlive;
        public byte nTimes_applyForChkTaskAlive;
        public int uiTaskMode_unused;
        public int iTaskId;
        public int uiTaskType;
        public qyDefs.QY_MESSENGER_ID idInfo_taskSender = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_taskReceiver = new qyDefs.QY_MESSENGER_ID();
        public int iStatus;
        public long tLastModifiedTime;
        public boolean bFrom_sharedObj;
        public int index_sharedObj;
        public long tRecvTime;
        public long dwTickCnt_start;
        public long dwTickCnt_toSend;
        public int usElapsed_recved;
        public int usElapsed_serv_startToProcess;
        public int index_debugInfo;
        public int uiChannelType;
        public int uiChannelType_input;
        public int lenInBytes;
        public MIS_MSG_routeTalkData data = new MIS_MSG_routeTalkData();

        public void memset0() {
            this.uiType = 0;
            this.uiCliIndex = 0;
            this.uiSessionId = 0;
            this.uiStep = 0;
            this.ucbConvrted = false;
            this.ucbConvrted_msgRoute = false;
            this.lenInBytes_msgRouteInDataBuf = 0;
            this.addr_logicalPeer = new MSGR_ADDR();
            this.ucFlg = 0;
            this.usCode = 0;
            this.usCode_org = 0;
            this.tStartTime = 0;
            this.uiTranNo = 0;
            this.usSeqNo = 0;
            this.tStartTime_processed = 0;
            this.uiTranNo_processed = 0;
            this.nTimes_tryToSend = 0;
            this.tTime_alive = 0;
            this.tTime_applyForChkTaskAlive = 0;
            this.nTimes_applyForChkTaskAlive = 0;
            this.uiTaskMode_unused = 0;
            this.iTaskId = 0;
            this.uiTaskType = 0;
            this.idInfo_taskSender.ui64Id = 0;
            this.idInfo_taskReceiver.ui64Id = 0;
            this.iStatus = 0;
            this.tLastModifiedTime = 0;
            this.bFrom_sharedObj = false;
            this.index_sharedObj = 0;
            this.tRecvTime = 0;
            this.dwTickCnt_start = 0;
            this.dwTickCnt_toSend = 0;
            this.usElapsed_recved = 0;
            this.usElapsed_serv_startToProcess = 0;
            this.index_debugInfo = 0;
            this.uiChannelType = 0;
            this.uiChannelType_input = 0;
            this.lenInBytes = 0;
        }

        public static void copyHeadInfo(MIS_MSG_TASK task, MIS_MSG_TASK misMsgTask) {
            //MIS_MSG_TASK misMsgTask = new MIS_MSG_TASK();
            misMsgTask.uiType = task.uiType;
            misMsgTask.uiCliIndex = task.uiCliIndex;
            misMsgTask.uiSessionId = task.uiSessionId;
            misMsgTask.uiStep = task.uiStep;
            misMsgTask.ucbConvrted = task.ucbConvrted;
            misMsgTask.ucbConvrted_msgRoute = task.ucbConvrted_msgRoute;
            misMsgTask.lenInBytes_msgRouteInDataBuf = task.lenInBytes_msgRouteInDataBuf;
            misMsgTask.addr_logicalPeer = task.addr_logicalPeer;
            misMsgTask.ucFlg = task.ucFlg;
            misMsgTask.usCode = task.usCode;
            misMsgTask.usCode_org = task.usCode_org;
            misMsgTask.tStartTime = task.tStartTime;
            misMsgTask.uiTranNo = task.uiTranNo;
            misMsgTask.usSeqNo = task.usSeqNo;
            misMsgTask.tStartTime_processed = task.tStartTime_processed;
            misMsgTask.uiTranNo_processed = task.uiTranNo_processed;
            misMsgTask.nTimes_tryToSend = task.nTimes_tryToSend;
            misMsgTask.tTime_alive = task.tTime_alive;
            misMsgTask.tTime_applyForChkTaskAlive = task.tTime_applyForChkTaskAlive;
            misMsgTask.nTimes_applyForChkTaskAlive = task.nTimes_applyForChkTaskAlive;
            misMsgTask.uiTaskMode_unused = task.uiTaskMode_unused;
            misMsgTask.iTaskId = task.iTaskId;
            misMsgTask.uiTaskType = task.uiTaskType;
            misMsgTask.idInfo_taskSender = task.idInfo_taskSender;
            misMsgTask.idInfo_taskReceiver = task.idInfo_taskReceiver;
            misMsgTask.iStatus = task.iStatus;
            misMsgTask.tLastModifiedTime = task.tLastModifiedTime;
            misMsgTask.bFrom_sharedObj = task.bFrom_sharedObj;
            misMsgTask.index_sharedObj = task.index_sharedObj;
            misMsgTask.tRecvTime = task.tRecvTime;
            misMsgTask.dwTickCnt_start = task.dwTickCnt_start;
            misMsgTask.dwTickCnt_toSend = task.dwTickCnt_toSend;
            misMsgTask.usElapsed_recved = task.usElapsed_recved;
            misMsgTask.usElapsed_serv_startToProcess = task.usElapsed_serv_startToProcess;
            misMsgTask.index_debugInfo = task.index_debugInfo;
            misMsgTask.uiChannelType = task.uiChannelType;
            misMsgTask.uiChannelType_input = task.uiChannelType_input;
            misMsgTask.lenInBytes = task.lenInBytes;
            //return misMsgTask;
            return;
        }

        /*
        public byte[] toByteArray1() {
            byte[] addrLogicalPeerBytes = addr_logicalPeer.toByteArray();
            byte[] idInfoTaskSenderBytes = idInfo_taskSender.toByteArray();
            byte[] idInfoTaskReceiverBytes = idInfo_taskReceiver.toByteArray();
            byte[] dataBytes = data.toByteArray();

            int size = Integer.BYTES * 21 + Short.BYTES * 3 + Long.BYTES * 7 + Byte.BYTES * 2 + 1 * 4 + addrLogicalPeerBytes.length + idInfoTaskSenderBytes.length + idInfoTaskReceiverBytes.length + dataBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(size);

            buffer.putInt(uiType);
            buffer.putInt(uiCliIndex);
            buffer.putInt(uiSessionId);
            buffer.putInt(uiStep);
            buffer.put((byte) (ucbConvrted ? 1 : 0));
            buffer.put((byte) (ucbConvrted_msgRoute ? 1 : 0));
            buffer.putInt(lenInBytes_msgRouteInDataBuf);
            buffer.put(addrLogicalPeerBytes);
            buffer.put(ucFlg);
            buffer.putInt(usCode);
            buffer.putInt(usCode_org);
            buffer.putLong(tStartTime);
            buffer.putInt(uiTranNo);
            buffer.putInt(usSeqNo);
            buffer.putLong(tStartTime_processed);
            buffer.putInt(uiTranNo_processed);
            buffer.putInt(nTimes_tryToSend);
            buffer.putLong(tTime_alive);
            buffer.putLong(tTime_applyForChkTaskAlive);
            buffer.put(nTimes_applyForChkTaskAlive);
            buffer.putInt(uiTaskMode_unused);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            buffer.put(idInfoTaskSenderBytes);
            buffer.put(idInfoTaskReceiverBytes);
            buffer.putInt(iStatus);
            buffer.putLong(tLastModifiedTime);
            buffer.put((byte) (bFrom_sharedObj ? 1 : 0));
            buffer.putInt(index_sharedObj);
            buffer.putLong(tRecvTime);
            buffer.putLong(dwTickCnt_start);
            buffer.putLong(dwTickCnt_toSend);
            buffer.putInt(usElapsed_recved);
            buffer.putInt(usElapsed_serv_startToProcess);
            buffer.putInt(index_debugInfo);
            buffer.putInt(uiChannelType);
            buffer.putInt(uiChannelType_input);
            buffer.putInt(lenInBytes);
            buffer.put(dataBytes);

            return buffer.array();
        }

         */
    }

    public static class MIS_MSG_REQ extends MIS_MSGU {                                   //  2007/11/29, addressable talk

        public int uiCliIndex;                           //  input daemon

        public int uiSessionId;
        public int uiStep;                                //  2008/04/28

        public boolean ucbConvrted;                      //  input 2007/7/14

        public boolean ucbConvrted_msgRoute;             //  daemon input
        public short lenInBytes_msgRouteInDataBuf;

        public MSGR_ADDR addr_logicalPeer = new MSGR_ADDR();

        //
        public byte ucFlg;
        //
        public short usCode;
        public short usCode_org;
        public long tStartTime;
        public int uiTranNo;
        public short usSeqNo;

        public long tStartTime_processed;                //  iocp uiTranNo_mgr iocp
        public int uiTranNo_processed;
        public int nTimes_tryToSend;                  //  mgr iocp
        //
        public long tTime_alive;                     //  2014/12/09. 当没有找到新的视频数据时，刷新此变量，也可以表明任务是活跃的
        //
        public long tTime_applyForChkTaskAlive;          //  2010/10/30
        public byte nTimes_applyForChkTaskAlive;      //  2014/12/09

        //
        public int uiTaskMode_unused;                 //  2009/08/23

        //
        public int iTaskId;                            //
        public int uiTaskType;                            //
        public qyDefs.QY_MESSENGER_ID idInfo_taskSender = new qyDefs.QY_MESSENGER_ID();                  //
        public qyDefs.QY_MESSENGER_ID idInfo_taskReceiver = new qyDefs.QY_MESSENGER_ID();                //
        public int iStatus;
        public long tLastModifiedTime;

        //
        public long tRecvTime;
        public int dwTickCnt_start;
        public int dwTickCnt_toSend;
        //
        public short usElapsed_recved;
        public short usElapsed_serv_startToProcess;
        public int index_debugInfo;

        //
        public int uiChannelType;
        //
        public int uiChannelType_input;

        //
        public int lenInBytes;
        //
        public IM_CONTENTU data = new IM_CONTENTU();

        //        public byte[] data = new byte[2444440];
        /*
        public byte[] toByteArray1() {
            byte[] addrLogicalPeerByteArray = addr_logicalPeer.toByteArray();
            byte[] idInfoTaskSenderByteArray = idInfo_taskSender.toByteArray();
            byte[] idInfoTaskReceiverByteArray = idInfo_taskReceiver.toByteArray();

            int buffersize = Integer.BYTES * 17 + Byte.BYTES * 4 + Short.BYTES * 6 + Long.BYTES * 6 +
                    addrLogicalPeerByteArray.length +
                    idInfoTaskSenderByteArray.length;
//                    + idInfoTaskReceiverByteArray.length+data.length;

            ByteBuffer buffer = ByteBuffer.allocate(buffersize);
            buffer.putInt(uiType);
            buffer.putInt(uiCliIndex);
            buffer.putInt(uiSessionId);
            buffer.putInt(uiStep);
            buffer.put((byte) (ucbConvrted ? 1 : 0));
            buffer.put((byte) (ucbConvrted_msgRoute ? 1 : 0));
            buffer.putShort(lenInBytes_msgRouteInDataBuf);
            buffer.put(addrLogicalPeerByteArray);
            buffer.put(ucFlg);
            buffer.putShort(usCode);
            buffer.putShort(usCode_org);
            buffer.putLong(tStartTime);
            buffer.putInt(uiTranNo);
            buffer.putShort(usSeqNo);
            buffer.putLong(tStartTime_processed);
            buffer.putInt(uiTranNo_processed);
            buffer.putInt(nTimes_tryToSend);
            buffer.putLong(tTime_alive);
            buffer.putLong(tTime_applyForChkTaskAlive);
            buffer.put(nTimes_applyForChkTaskAlive);
            buffer.putInt(uiTaskMode_unused);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            buffer.put(idInfoTaskSenderByteArray);
            buffer.put(idInfoTaskReceiverByteArray);
            buffer.putInt(iStatus);
            buffer.putLong(tLastModifiedTime);
            buffer.putLong(tRecvTime);
            buffer.putInt(dwTickCnt_start);
            buffer.putInt(dwTickCnt_toSend);
            buffer.putShort(usElapsed_recved);
            buffer.putShort(usElapsed_serv_startToProcess);
            buffer.putInt(index_debugInfo);
            buffer.putInt(uiChannelType);
            buffer.putInt(uiChannelType_input);
            buffer.putInt(lenInBytes);
//            buffer.put(data);

            return buffer.array();
        }

         */

    }

    public static class MIS_MSG_procVideo_qmc extends MIS_MSGU {                                   //  2008/03/18
        //
        //		Object pMisCnt;
        public qyDefs.QY_MESSENGER_ID idInfo_peer = new qyDefs.QY_MESSENGER_ID();
        //
        public qyDefs.QY_MESSENGER_ID idInfo_sender = new qyDefs.QY_MESSENGER_ID();
        //
        //
        public int uiTranNo_openAvDev;
        public int usIndex;
        //
        public int iTaskId;
        public long hWnd_task;                         //  2009/09/14
        //
        public int uiEventId_lastRecvd_unused;            //  2016/12/26	//  2008/11/22

        //
        public int uiCapType;
        public int iIndex_capProcInfo;
        public qyCommProc_open.BITMAPINFOHEADER bih = new qyCommProc_open.BITMAPINFOHEADER();

        //
        //QY_MEMORY									memory;

        //  2014/09/23
        public myDRAW_VIDEO_DATA pkt = new myDRAW_VIDEO_DATA();

        //
        //  int										index_player;						//  2009/10/03
        public PLAYER_ID playerId = new PLAYER_ID();                         //  2016/02/18

        public boolean ucbDisplayAllFrames;              //  2009/07/25

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(4 + 72 + 72 + 4 + 4 + 4 + 72 + 72 + 72 + 4 + 4 + 4 + 4 + 72 + 1);
            buffer.putInt(uiType);
            buffer.put(idInfo_peer.toByteArray());
            buffer.put(idInfo_sender.toByteArray());
            buffer.putInt(uiTranNo_openAvDev);
            buffer.putInt(usIndex);
            buffer.putInt(iTaskId);
            buffer.putLong(hWnd_task);
            buffer.putInt(uiEventId_lastRecvd_unused);
            buffer.putInt(uiCapType);
            buffer.putInt(iIndex_capProcInfo);
            buffer.put(bih.toByteArray());
            buffer.put(pkt.toByteArray2());
            buffer.put(playerId.toByteArray());
            buffer.put((byte) (ucbDisplayAllFrames ? 1 : 0));
            return buffer.array();
        }

    }

    public static class myTRANSFER_FILE_STATUS {                                   //  robotÓÃÀ´¸ø½çÃæ·¢ÏûÏ¢£¬ÈÃ½ØÃæÏÔÊ¾ÎÄ¼þ·¢ËÍµÈ¹ý³Ì, 2007/07/17
        public byte ucPercent;
        public long ui64Len_recvd;                        //  ÊÕµ½µÄ×Ö½ÚÊý¡£ 2008/10/25
        public String fileName;//[cntof_fileName];
        public String fullFilePath;//[MAX_PATH + 1];

        public byte[] toByteArray() {
            int bufferSize = Byte.BYTES + Long.BYTES;
            byte[] fileNameBytes = null;
            byte[] fullFilePathBytes = null;
            if (fileName != null) {
                fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
                bufferSize += fileNameBytes.length;
            }
            if (fullFilePath != null) {
                fullFilePathBytes = fullFilePath.getBytes(StandardCharsets.UTF_8);
                bufferSize += fullFilePathBytes.length;
            }
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put(ucPercent);
            buffer.putLong(ui64Len_recvd);
            if (fileNameBytes != null) {
                buffer.put(fileNameBytes);
            }
            if (fullFilePathBytes != null) {
                buffer.put(fullFilePathBytes);
            }
            return buffer.array();
        }

    }

    public static class myNOTIFICATION {                                   //  2010/07/21
        public qyDefs.QY_MESSENGER_ID idInfo_from;
        public String content;//[cntof_content];

        public byte[] toByteArray() {
            int buffer_size = 0;
            byte[] idInfoBytes = null;
            if (idInfo_from != null) {
                buffer_size += idInfoBytes.length;
            }
            byte[] contentBytes = null;
            if (content != null) {
                contentBytes = content.getBytes(StandardCharsets.UTF_8);
                buffer_size += contentBytes.length;
            }

            ByteBuffer buffer = ByteBuffer.allocate(buffer_size);
            if (idInfoBytes != null) {
                buffer.put(idInfoBytes);
            }
            if (contentBytes != null) {
                buffer.put(contentBytes);
            }
            return buffer.array();
        }

    }

    public static class MIS_MSG_taskStatus extends MIS_MSGU {                                   //  2007/07/17
        //
        //Object pMisCnt;
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
        //
        public qyDefs.QY_MESSENGER_ID idInfo_taskSender = new qyDefs.QY_MESSENGER_ID();
        public qyDefs.QY_MESSENGER_ID idInfo_taskReceiver = new qyDefs.QY_MESSENGER_ID();
        //
        public boolean bImGrpMem;

        public int cmdCode;
        public long tStartTime;                          //  2009/10/18
        public int uiTranNo;                          //  2009/10/18
        public int uiContentType;
        public int iTaskId;
        public int uiTaskType;                            //  2007/07/22
        public int iStatus;

        //
        public long tStartTime_processed;                //  2011/03/10

        //
        public boolean ucbNeeded_applyForOrgReq;         //  2010/07/21
        //
        public boolean ucbFound;                         //  2014/11/21
        //
        public boolean ucbNotification;                  //  2010/07/21

        //
        public myTRANSFER_FILE_STATUS u_transferFileStatus = new myTRANSFER_FILE_STATUS();
        public myNOTIFICATION u_notification = new myNOTIFICATION();                        //  2010/07/21

        public byte[] toByteArray() {
            int bufferSize = Integer.BYTES * 10 + 1 * 6 + Long.BYTES * 2 +
                    idInfo_logicalPeer.toByteArray().length +
                    idInfo_taskSender.toByteArray().length +
                    idInfo_taskReceiver.toByteArray().length +
                    u_transferFileStatus.toByteArray().length +
                    u_notification.toByteArray().length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType);
            buffer.put(idInfo_logicalPeer.toByteArray());
            buffer.put(idInfo_taskSender.toByteArray());
            buffer.put(idInfo_taskReceiver.toByteArray());
            buffer.put((byte) (bImGrpMem ? 1 : 0));
            buffer.putInt(cmdCode);
            buffer.putLong(tStartTime);
            buffer.putInt(uiTranNo);
            buffer.putInt(uiContentType);
            buffer.putInt(iTaskId);
            buffer.putInt(uiTaskType);
            buffer.putInt(iStatus);
            buffer.putLong(tStartTime_processed);
            buffer.put((byte) (ucbNeeded_applyForOrgReq ? 1 : 0));
            buffer.put((byte) (ucbFound ? 1 : 0));
            buffer.put((byte) (ucbNotification ? 1 : 0));
            buffer.put(u_transferFileStatus.toByteArray());
            buffer.put(u_notification.toByteArray());
            return buffer.array();
        }

    }

    public static class MIS_MSG_REFRESHMESTATUS_QMC extends MIS_MSGU {                               //

        public byte[] toByteArray() {
            int bufferSize = Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(uiType);
            return buffer.array();
        }

    }

    public static class IM_MSG_RCD {
        public int id;
        public String misServName;//[CONST_maxMisServNameLen + 1];
        public qyDefs.QY_MESSENGER_ID idInfo_send = new qyDefs.QY_MESSENGER_ID();
        public long tSendTime;
        public int uiTranNo;
        public int usSeqNo;
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
        public int iTaskId;
        public long tStartTime_serv;
        public int uiSerialNo;
        public qyDefs.QY_MESSENGER_ID idInfo_recv = new qyDefs.QY_MESSENGER_ID();
        public int iRole;
        public long tRecvTime;
        public int usRespCode;
        public int uiType;
        public String logMsgContent;
        public long firstTime;
        public long lastTime;
        public int iStatus;
        public long tLastModifiedTime;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(4 + (CONST_maxMisServNameLen + 1) + 72 + 12 + 8 + 8 + 4 + 2048 + 8 + 8 + 4 + 8);
            buffer.putInt(id);
            buffer.put(misServName.getBytes(StandardCharsets.UTF_8));
            buffer.position(buffer.position() + CONST_maxMisServNameLen + 1 - misServName.length()); // Fill remaining bytes with zeros
            buffer.put(idInfo_send.toByteArray());
            buffer.putLong(tSendTime);
            buffer.putInt(uiTranNo);
            buffer.putInt(usSeqNo);
            buffer.put(idInfo_logicalPeer.toByteArray());
            buffer.putInt(iTaskId);
            buffer.putLong(tStartTime_serv);
            buffer.putInt(uiSerialNo);
            buffer.put(idInfo_recv.toByteArray());
            buffer.putInt(iRole);
            buffer.putLong(tRecvTime);
            buffer.putInt(usRespCode);
            buffer.putInt(uiType);
            buffer.put(logMsgContent.getBytes(StandardCharsets.UTF_8));
            buffer.position(buffer.position() + cntof_logMsgContent - logMsgContent.length()); // Fill remaining bytes with zeros
            buffer.putLong(firstTime);
            buffer.putLong(lastTime);
            buffer.putInt(iStatus);
            buffer.putLong(tLastModifiedTime);
            return buffer.array();
        }
    }

    public static class MIS_MSG_applyForPlayer_qmc extends MIS_MSGU {

        public qyDefs.QY_MESSENGER_ID idInfo_recorder = new qyDefs.QY_MESSENGER_ID();
        public int iTaskId;
        public qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
        public int index_sharedObj;
        public boolean bGps;
        public MIS_MSG_applyForPlayer_qmc_audio audio = new MIS_MSG_applyForPlayer_qmc_audio();
        public MIS_MSG_applyForPlayer_qmc_video video = new MIS_MSG_applyForPlayer_qmc_video();

        public static class MIS_MSG_applyForPlayer_qmc_audio {
            public int uiTranNo;                     //  2009/04/27
            //  unsigned  short						usIndex;						//  2009/04/27
            //
            //  WAVEFORMATEX							wf;
            public qyCommProc_open.QY_AUDIO_HEADER ah = new qyCommProc_open.QY_AUDIO_HEADER();                             //  2010/06/17

            public qnmCommProc_is_open.AUDIO_PLAY_CFG playCfg = new qnmCommProc_is_open.AUDIO_PLAY_CFG();                     //  2009/06/14

            public byte[] toByteArray() {
                byte[] ahBytes = ah.toByteArray();
                byte[] playCfgBytes = playCfg.toByteArray();
                int bufferSize = Integer.BYTES + ahBytes.length + playCfgBytes.length;
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                buffer.putInt(uiTranNo);
                buffer.put(ahBytes);
                buffer.put(playCfgBytes);
                return buffer.array();
            }


        }

        public static class MIS_MSG_applyForPlayer_qmc_video {
            public int uiTranNo;
            //  unsigned  short						usIndex;
            public qyCommProc_open.QY_VIDEO_HEADER vh = new qyCommProc_open.QY_VIDEO_HEADER();
            //
            public int usFps;                           //  2015/02/20

            public byte[] toByteArray() {
                byte[] buf = vh.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + buf.length);
                buffer.putInt(uiTranNo);
                buffer.putInt(usFps);
                buffer.put(buf);
                return buffer.array();
            }
        }

        public byte[] toByteArray() {
            byte[] buf1 = idInfo_recorder.toByteArray();
            byte[] buf2 = idInfo_logicalPeer.toByteArray();
            byte[] buf3 = audio.toByteArray();
            byte[] buf4 = video.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + 1 + buf1.length + buf2.length + buf3.length + buf4.length);
            buffer.putInt(uiType);
            buffer.put(buf1);
            buffer.putInt(iTaskId);
            buffer.put(buf2);
            buffer.putInt(index_sharedObj);
            buffer.put((byte) (bGps ? 1 : 0));
            buffer.put(buf3);
            buffer.put(buf4);
            return buffer.array();
        }

    }

    public static class MIS_MSG_applyForChkChannels_qmc extends MIS_MSGU {                               //  2008/06/01
        boolean bTaskExists;
        //  BOOL											bTaskNeedRestart;
        boolean bTask_sendLocalAv;             //  2008/10/17, ´æÔÚÐèÒª·¢ËÍ±¾µØavµÄÈÎÎñ

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + Byte.BYTES);
            buffer.putInt(uiType);
            buffer.put((byte) (bTaskExists ? 1 : 0));
            buffer.put((byte) (bTask_sendLocalAv ? 1 : 0));
            return buffer.array();
        }

    }

    public static class CMIS_MSGU {
        public MIS_MSGU msgU;
    }

    public static class MIS_MSGU {
        public int uiType;
//        ByteBuffer buffer = ByteBuffer.allocate(bufSize_transferVideoData);
        ByteBuffer buffer;
        public int tmpID = getuiNextTranNo.call();
        public int debugAudio = 0;
       /*
        public MIS_MSG_INPUT input = new MIS_MSG_INPUT();
        public MIS_MSG_REQ req = new MIS_MSG_REQ();
        public MIS_MSG_TALK talk = new MIS_MSG_TALK();
        public MIS_MSG_TASK task = new MIS_MSG_TASK();                      //  2007/07/08

        public MIS_MSG_taskStatus taskStatus = new MIS_MSG_taskStatus();                  //  2007/07/17

        public MIS_MSG_REFRESHMESTATUS_QMC refreshMeStatus_qmc = new MIS_MSG_REFRESHMESTATUS_QMC();        //  2007/06/20

        public MIS_MSG_applyForPlayer_qmc applyForPlayer_qmc = new MIS_MSG_applyForPlayer_qmc();          //  2008/05/09

        public MIS_MSG_applyForChkChannels_qmc applyForChkChannels_qmc = new MIS_MSG_applyForChkChannels_qmc();    //  2008/06/01

        public byte[] toByteArray() {
            int buffer_size = 0;

            byte[] intput_buf = input.toByteArray();
            buffer_size += intput_buf.length;

            byte[] req_buf = req.toByteArray();
            buffer_size += req_buf.length;

            byte[] talk_buf = talk.toByteArray();
            buffer_size += talk_buf.length;

            byte[] task_buf = task.toByteArray();
            buffer_size += task_buf.length;

            byte[] taskStatus_buf = taskStatus.toByteArray();
            buffer_size += taskStatus_buf.length;

            byte[] refreshMeStatus_qmc_buf = refreshMeStatus_qmc.toByteArray();
            buffer_size += refreshMeStatus_qmc_buf.length;

            byte[] applyForPlayer_qmc_buf = applyForPlayer_qmc.toByteArray();
            buffer_size += applyForPlayer_qmc_buf.length;

            byte[] applyForChkChannels_qmc_buf = applyForChkChannels_qmc.toByteArray();
            buffer_size += applyForChkChannels_qmc_buf.length;

            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + buffer_size);
            buffer.putInt(uiType);
            buffer.put(intput_buf);
            buffer.put(req_buf);
            buffer.put(talk_buf);
            buffer.put(task_buf);
            buffer.put(taskStatus_buf);
            buffer.put(refreshMeStatus_qmc_buf);
            buffer.put(applyForPlayer_qmc_buf);
            buffer.put(applyForChkChannels_qmc_buf);
            return buffer.array();
        }
            */
    }


    public static class TASK_AV_FROM_video {

        //
        public TRAN_video tv_recvd = new TRAN_video();                                   //  2014/11/10

        //  2014/11/10
        public boolean bGot_vh_stream;

        //
        public int usFrames_perSecond_expected;             //  TRANSFER_VIDEO_DATA
        //
        public long tLastTime_showFrameInfo_020534;
        public int usCnt_pkts;
        public long tSelectTime;
        public int dwTickCnt_selectTime;

    }

    public static class TASK_AV_FROM_audio {

        //  2014/11/10
        public TRAN_audio ta_recvd = new TRAN_audio();                                   //  2014/11/10

        //
        public long tLastTime_showFrameInfo_020534;
        public int usCnt_pkts;

    }

    public static class TASK_AV_FROM_viewDynBmp_reply {
        public boolean ucbSaveVideo_reply;
    }

    public static class TASK_AV_FROM {

        //
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();                                     //
        public long tStartTime_task;                         //
        //
        public boolean ucbVideoConferenceStarter;                    //  2009/04/02

        //  2014/09/23
        public short shClientVer;

        //
        public TASK_AV_FROM_video video = new TASK_AV_FROM_video();

        public TASK_AV_FROM_audio audio = new TASK_AV_FROM_audio();

        //
        //  2015/05/19. 2014/09/04
        public TASK_AV_FROM_viewDynBmp_reply viewDynBmp_reply = new TASK_AV_FROM_viewDynBmp_reply();

        //  2015/10/23
        public TASK_av_props taskAvProps = new TASK_av_props();


        //
        public static class TaskAvFrom_sth {
            public int iDiffInMs_tStartAudios;//[MAX_activeMems_from];                      //  2009/05/27
            public int iSampleTimeInMs_audios;//[MAX_activeMems_from];                        //  2010/06/19
        }

        ;
        public TaskAvFrom_sth sth;

        //
        public void copyTo(TASK_AV_FROM dst) {
            TASK_AV_FROM src = this;
            //
            dst.idInfo = src.idInfo;
            dst.tStartTime_task = src.tStartTime_task;
            //
            dst.ucbVideoConferenceStarter = src.ucbVideoConferenceStarter;
            //

            //  2014/09/23
            dst.shClientVer = src.shClientVer;


            //
            dst.video = src.video;
            dst.audio = src.audio;

            //
            dst.viewDynBmp_reply = src.viewDynBmp_reply;
            dst.taskAvProps = src.taskAvProps;
            dst.sth = src.sth;

            return;

        }

    }

    public static class ACTIVE_taskAvFrom {                                           //  2009/02/19

        public qyDefs.QY_MESSENGER_ID idInfo;                                     //  ·¢ËÍÕß£¨ÔÚ×é²¥Àï£¬Õâ¸öºÜÖØÒª£©

        public int usIndex_pMems_from;

    }

    public static class PKT_info_toDec_info {
        public boolean bGot_pkt;
        //
        public int index_mems;
        public int iPos;

        public void clearData() {
            this.iPos = 0;
            this.bGot_pkt = false;
            this.index_mems = 0;
        }
    }

    public static class PTZ_control_cmd extends IM_CONTENTU {
        //
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        public PTZ_cmd_common cmdInfo = new PTZ_cmd_common();

        public byte[] toByteArray() {

            byte[] buf1 = idInfo_imGrp_related.toByteArray();
            byte[] buf2 = cmdInfo.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocateDirect(buf1.length + buf2.length + Integer.BYTES);
            buffer.putInt(uiType);
            buffer.put(buf1);
            buffer.put(buf2);
            return buffer.array();
        }

    }

    public static class PTZ_cmd_common {
        public Obj obj = new Obj();
        //
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public int iChannel;
        public byte ucCmd;
        public int ucParam;
        public byte ucMouseStatus;

        public byte[] toByteArray() {
            byte[] buf1 = obj.toByteArray();
            byte[] buf2 = idInfo.toByteArray();

            ByteBuffer buffer = ByteBuffer.allocateDirect(buf1.length + buf2.length + Integer.BYTES * 3 + Byte.BYTES);
            buffer.put(buf1);
            buffer.put(buf2);
            buffer.putInt(iChannel);
            buffer.put(ucCmd);
            buffer.putInt(ucParam);
            buffer.putInt(ucMouseStatus);
            return buffer.array();
        }

    }

    public static class Obj {

        public int uiObjType;
        public short usIndex_obj;
        public short usHelp_subIndex;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES + Short.BYTES * 2);
            buffer.putInt(uiObjType);
            buffer.putShort(usIndex_obj);
            buffer.putShort(usHelp_subIndex);
            return buffer.array();
        }

    }

    //
    public static class Tmp_pkt_encA {
        public TRANSFER_AUDIO_dataMemHead memHead_a;
        public int bufSize;
        public ByteBuffer /**/buf;//[bufSize_tmpPktEncA];   //  128000/8
        public int len;
    }

    public static class Tmp_pkt_encV {
        public TRANSFER_VIDEO_dataMemHead memHead_v;
        public int bufSize;
        public ByteBuffer buf;//[bufSize_tmpPktEncV];   //  128000/8
        public int len;
    }

    //
    public static class PKT_info_toDec {
        public Object pMIS_MSGU;

        //
        public Tmp_pkt_encA pkt_encA = new Tmp_pkt_encA();
        public Tmp_pkt_encV pkt_encV = new Tmp_pkt_encV();

        //
        public Object pPkt_dec;                       //  用来做解码缓存

        //
        public boolean bGot_msg;                                      //  2015/02/14. 表明pMIS_MSGU是否有效数据
        //
        public int lPktId_getNewPkt;
        //
        public PKT_info_toDec_info info = new PKT_info_toDec_info();

        //
        public boolean bAudio_forDbg;

    }

    public static class PRETRANS_procInfo_helpInfo {
        public static class PRETRANS_procInfo_helpInfo_pts {

            public int dwTickCnt_start;         //  2015/01/15
            public int uiPts_first;
            //
            //
            public int uiPts_lastRecvd;           //  2015/01/15

        }

        ;
        public PRETRANS_procInfo_helpInfo_pts pts;

    }

    public static class PRETRANS_procInfo extends QY_qThreadProcInfo_common {
        //MACRO_qyQThreadProcInfo_common

        //
        public QY_qBuf2 qBuf2;

        //
        public PRETRANS_procInfo_helpInfo helpInfo;

    }

    //  2014/08/04
    public static class SAVE_av_procInfo extends QY_qThreadProcInfo_common {

    }

    //
    public static class TRANS_pts_info_v {

        //
        public int dwTickCnt_calc_start;                        //  每隔几秒计算一次。每次都重新清零。

        //
        public int uiCnt_ptsErr;                              //  pts不匹配的次数
        public int uiCnt_notRecvd_pts;                            //  多少pts丢了

    }

    public static class TRANS_pts_info {
        public TRANS_pts_info_v v;
    }

    //  2009/06/17
    public static class QIS_DISPATCH_procInfo extends QY_qThreadProcInfo_common {
        //MACRO_qyQThreadProcInfo_common
    }

    public static class QIS_DISPATCH {
        public Object pProcInfo;
        public MSGR_ADDR pAddr_logicalPeer;
        public int iTaskId;
        public Object pTranInfo;                                        //  AV_TRAN_INFO  *
        public Object pTask;                                            //  PROC_TASK_AV  *

        //  2011/12/08
        public int index_activeMems_from;

        //
        public QIS_DISPATCH_procInfo audio;
        public QIS_DISPATCH_procInfo video;

    }

    public static class VIDEO_CONFERENCE_MEM {
        public AV_stream avStream = new AV_stream(); // 音视频信息

        //  2013/07/18. 需要将第几个话筒的属性传给其他用户
        public short usIndex_speaker; // 索引，用于标识视频放在会议的哪个位置


        //
        public Object pInternal;

        public String desc;//[32];
    }

    public static class CONF_requesting_mem {
        //
        public qyDefs.QY_MESSENGER_ID idInfo;

        //
        public Object pInternal;

        public String desc;//[32];

    }

    public static class CONF_requesting_mems {

        public int usCnt;
        public CONF_requesting_mem[] mems = new CONF_requesting_mem[Consts.MAX_conf_requestingMems];

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES);
            buffer.putInt(usCnt);
            return buffer.array();
        }
    }

    /**
     * 会议关键信息包
     */
    public static class CONF_KEY extends IM_CONTENTU {

        /**
         * 关联组
         */
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();               //  2009/02/23
        /**
         * 发言人的相关信息
         */
        VIDEO_CONFERENCE_MEM[] buf_VIDEO_CONFERENCE_MEM_mems = new VIDEO_CONFERENCE_MEM[Consts.MAX_conf_activeMems];

        public CONF_KEY()
        {
            for(int i =0;i<buf_VIDEO_CONFERENCE_MEM_mems.length;i++)
            {
                buf_VIDEO_CONFERENCE_MEM_mems[i] = new VIDEO_CONFERENCE_MEM();
            }
        }
        //
        public static class ConfKey_tmpInternal {
            public int usCnt_mems;

            public byte[] toByteArray() {
                int size = Integer.BYTES;
                ByteBuffer buffer = ByteBuffer.allocateDirect(size);
                buffer.putInt(usCnt_mems);
                return buffer.array();
            }
        }

        public ConfKey_tmpInternal tmpInternal = new ConfKey_tmpInternal();

        public short usLayoutType_starter;               //  2010/12/29

        public short usCnt;
        public CONF_requesting_mems requestingMems = new CONF_requesting_mems();
        public ConfLayoutParam confLayoutParam = new ConfLayoutParam();


        public byte[] toByteArray() {
            int size = Integer.BYTES + idInfo_imGrp_related.toByteArray().length + Short.BYTES * 2 + requestingMems.toByteArray().length + confLayoutParam.toByteArray().length;
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);

            buffer.putInt(uiType);
            buffer.put(idInfo_imGrp_related.toByteArray());
            buffer.putInt(tmpInternal.usCnt_mems);
            buffer.putShort(usLayoutType_starter);
            buffer.putShort(usCnt);
//            buffer.put(buf_VIDEO_CONFERENCE_MEM_mems);
            buffer.put(requestingMems.toByteArray());
            buffer.put(confLayoutParam.toByteArray());

            return buffer.array();
        }
    }

    public static class StatusConfLayoutParam extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();

        //
        public ConfLayoutParam confLayoutParam = new ConfLayoutParam();
    }

    public static class ConfLayoutParam {
        //
        public OneBigLayoutParam oneBigLayoutParam = new OneBigLayoutParam();

        //
        public EnlargeParam enlargeParam = new EnlargeParam();

        //
        public IpcParam ipcParam = new IpcParam();

        //
        public PhoneEnlargeParam phoneEnlargeParam = new PhoneEnlargeParam();
        public Resp resp = new Resp();

        //
        public RespVideoStatus respVideoStatus = new RespVideoStatus();

        public byte[] toByteArray() {
            byte[] buf1 = oneBigLayoutParam.toByteArray();
            byte[] buf2 = enlargeParam.toByteArray();
            byte[] buf3 = ipcParam.toByteArray();
            byte[] buf4 = phoneEnlargeParam.toByteArray();
            byte[] buf5 = resp.toByteArray();
            byte[] buf6 = respVideoStatus.toByteArray();

            int size = buf1.length + buf2.length + buf3.length + buf4.length + buf5.length + buf6.length;
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);

            buffer.put(buf1);
            buffer.put(buf2);
            buffer.put(buf3);
            buffer.put(buf4);
            buffer.put(buf5);
            buffer.put(buf6);

            return buffer.array();
        }
    }

    ;

    public static class ConfNvrInfo extends IM_CONTENTU {
        //

        //
        public byte ucbResp;

        //
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();

        //
        public NvrInfo nvrInfo = new NvrInfo();
        public qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
        public tmpInternal tmpInternal = new tmpInternal();
        //

    }

    ;

    public static class NvrInfo {

        public long ui64Id;
        public short usCnt;
        //public IpcInfo[128] mems;
        public IpcInfo[] mems;
        public ByteBuffer buf_ipcInfo = ByteBuffer.allocateDirect(bufSize_ipcInfos);

        public NvrInfo() {
            mems = new IpcInfo[128];
            for (int i = 0; i < mems.length; i++) {
                mems[i] = new IpcInfo();
            }
        }

//        public void initMems() {
//            mems = new IpcInfo[usCnt];
//            for (int i = 0; i < mems.length; i++) {
//                mems[i] = new IpcInfo();
//            }
//        }

    }

    ;

    public static class IpcInfo {
        public int iChannel;
        public String name;//[cntof_name];
        public String ip;// = ByteBuffer.allocateDirect(cntof_ip);//[];

        public static IpcInfo fromByteArray(byte[] byteArray) {
            ByteBuffer buf = ByteBuffer.wrap(byteArray);
            buf.order(ByteOrder.LITTLE_ENDIAN);  // 设置字节序
            return fromByteArray(buf);
        }

        // 静态方法，从 byte[] 创建 IpcInfo 对象
        public static IpcInfo fromByteArray(ByteBuffer buffer) {
            IpcInfo ipcInfo = new IpcInfo();
            ipcInfo.iChannel = buffer.getInt();
            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);
            ipcInfo.name = new String(nameBytes, StandardCharsets.UTF_8);
            ipcInfo.ip = new String();
            buffer.get(ipcInfo.ip.getBytes());
            return ipcInfo;
        }
    }

    ;

    public static class tmpInternal {


        public int usCnt_mems;

    }

    ;

    public static class PhoneEnlargeParam {

        public short x;
        public short y;
        public short usEnlargeType;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Short.BYTES * 3);
            buffer.putShort(x);
            buffer.putShort(y);
            buffer.putShort(usEnlargeType);
            return buffer.array();
        }
    }              //  放大类型

    public static class EnlargeParam {

        public short usEnlargeType;
        public long ui64Id;
        public int tn_v;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES + Long.BYTES + Short.BYTES);
            buffer.putShort(usEnlargeType);
            buffer.putLong(ui64Id);
            buffer.putInt(tn_v);
            return buffer.array();
        }
    }

    public static class Resp {

        public int ii;
        public long curr_idinfo;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES + Long.BYTES);
            buffer.putInt(ii);
            buffer.putLong(curr_idinfo);
            return buffer.array();
        }
    }

    public static class RespVideoStatus {
        public int bAmplification;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES);
            buffer.putInt(bAmplification);
            return buffer.array();
        }
    }

    public static class OneBigLayoutParam {
        public byte ucbOneBigLayout;
        public long ui64Id;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Byte.BYTES + Long.BYTES);
            buffer.put(ucbOneBigLayout);
            buffer.putLong(ui64Id);
            return buffer.array();
        }
    }

    ;

    public static class IpcParam {
        public int iChannel;
        public String channelName;//[cntof_channelName];

        public byte[] toByteArray() {
            byte[] channelNameBytes = channelName.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES + channelNameBytes.length);
            buffer.putInt(iChannel);
            buffer.put(channelNameBytes);
            return buffer.array();
        }
    }

    public static class CONF_LAYOUT extends IM_CONTENTU {
        public byte ucbResp;
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();               //  2009/02/23
        public short usOp;
        public short usLayoutType;
        public ConfLayoutParam confLayoutParam = new ConfLayoutParam();

        public byte[] toByteArray() {
            byte[] idInfoBytes = idInfo_imGrp_related.toByteArray();
            byte[] confLayoutParamBytes = confLayoutParam.toByteArray();

            int size = Integer.BYTES + Byte.BYTES + idInfoBytes.length + Short.BYTES * 2 + confLayoutParamBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(size);

            buffer.putInt(uiType);
            buffer.put(ucbResp);
            buffer.put(idInfoBytes);
            buffer.putShort(usOp);
            buffer.putShort(usLayoutType);
            buffer.put(confLayoutParamBytes);

            return buffer.array();
        }

    }

    public static class CONF_req_policy {
        public short usAvLevel;
        public int conf_iFourcc;
        public short usMaxSpeakers;

        public byte[] toByteArray() {
            // 计算所有字段所需的总字节数
            int size = Integer.BYTES + Short.BYTES * 2;

            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.putShort(usAvLevel);
            buffer.putInt(conf_iFourcc);
            buffer.putShort(usMaxSpeakers);

            return buffer.array();
        }
    }

    ;


    //
    public static class CONF_req extends IM_CONTENTU {
        public qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        public short usOp;
        public CONF_req_policy policy = new CONF_req_policy();
        public qyDefs.QY_MESSENGER_ID idInfo_initiator = new qyDefs.QY_MESSENGER_ID();

        public byte[] toByteArray() {
            byte[] buf1 = idInfo_imGrp_related.toByteArray();
            byte[] buf2 = policy.toByteArray();
            byte[] buf3 = idInfo_initiator.toByteArray();


            // 计算所有字段所需的总字节数
            int size = Integer.BYTES + Short.BYTES + buf1.length + buf2.length + buf3.length;

            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.putInt(uiType);
            buffer.put(buf1);
            buffer.putShort(usOp);
            buffer.put(buf2);
            buffer.put(buf3);
            return buffer.array();
        }
    }


    public static class QY_qThreadProcInfo_common extends qnmCommProc_is_open.MACRO_qyQThreadProcInfo_common1 {
    }

    ;


    //
    public static class DYN_BMP_RULE {
        //
        public QIS_RES_OBJ dynBmp;
        //
        String name;//[32];                         //  name used to mean device id or unique name for dynBmp
        String cusName;//[32];                      //  cusName used to mean a friendly comment for dynBmp


        //
        public boolean ucbSaveVideo;

        //

    }

    ;


    public static class qyFuncs {


        public static class TMP_ip {
            public String[] ip;
        }

        public static InetAddress getLocalHostExactAddress() {
            try {
                InetAddress candidateAddress = null;

                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface iface = networkInterfaces.nextElement();
                    // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                    for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                        InetAddress inetAddr = inetAddrs.nextElement();
                        // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                        if (!inetAddr.isLoopbackAddress()) {
                            if (inetAddr.isSiteLocalAddress()) {
                                // 如果是site-local地址，就是它了 就是我们要找的
                                // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                                return inetAddr;
                            }

                            // 若不是site-local地址 那就记录下该地址当作候选
                            if (candidateAddress == null) {
                                candidateAddress = inetAddr;
                            }

                        }
                    }
                }

                // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
                return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //建立会话
        public static ByteBuffer qnmOpenSession_mis(String servIp, int usPort, QY_COMM_AUTHINFO_MIS pAuthInfo, int usAuthCmd, int uiChannelType,
                                             String clientIp, int iServiceId, String serviceVer, qmCrypt.ENC_CTX_session pCommEncCtx,
                                             QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_PARAM pSockParam) {
            Log.d("qnmOpenSession_mis: ");
            int iErr = -1;
            QY_COMM_AUTHINFO_MIS authInfo = new QY_COMM_AUTHINFO_MIS();//pAuthInfo;

            //
            authInfo.copyFrom(pAuthInfo);

            //
            if (!guiShare.GuiShare.pf_bQySockOpen.call(pSock)) {
                if (guiShare.GuiShare.pf_qyConnect.call(servIp, usPort, pSockParam, pSock) != 0) {
                    String str = String.format("qnmOpenSession_mis: connect %s failed", servIp);
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return null;
                }
                //
                Log.d("qy","qnmOpenSession_mis: connect ok"  +  qnmVerStr(0));
            }
            authInfo.usAuthCmd = (short) usAuthCmd;
            authInfo.uiChannelType = uiChannelType;
            authInfo.ip = getLocalHostExactAddress().getHostAddress();

            qmCrypt.ENC_CTX_session pEncCtx = pCommEncCtx;
            if(pEncCtx == null) return null;
            if (pEncCtx.common.type == CONST_qyEncType_rsa1
                /*&& pEncCtx.m_var.uiBlobLen_sessionKey != 0*/) {
//                if (pEncCtx.m_aes.Key.length() > bufSize_sessionKeyBlob) return iErr;
                if (pEncCtx.m_aes == null || pEncCtx.m_aes.Key == null || pEncCtx.m_aes.Key.length > bufSize_sessionKeyBlob) return null;

                authInfo.uiBlobLen_sessionKey = pEncCtx.m_aes.Key.length;
                authInfo.sessionKeyBlob = pEncCtx.m_aes.Key;

                if (pEncCtx.m_aes.IV.length > bufSize_sessionIv) return null;
                authInfo.usLen_sessionIv = (int) pEncCtx.m_aes.IV.length;
                authInfo.sessionIv = pEncCtx.m_aes.IV;

                Log.d("qy","len: iv: " + authInfo.usLen_sessionIv);
                Log.d("qy","长度: key: " + authInfo.uiBlobLen_sessionKey);
//                System.arraycopy(pEncCtx.m_aes.IV.getBytes(), 0, authInfo.sessionIv, 0, authInfo.usLen_sessionIv);
            }

            ByteBuffer tmpBuf4096 = ByteBuffer.allocate(bufSize_4096);
            //
            Log.d("qy","authInfo.uiDevType: " + authInfo.uiDevType);
            //
            int len = commAuthInfo2Stream_mis(authInfo, tmpBuf4096);
            if (len < 0) return null;

            pSession.clientIp = authInfo.ip;
            long tStartTime = System.currentTimeMillis();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
//            tmpBuf4096.flip();
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null){
                Log.e("qnmOpenSession_mis: pQyMc null");
                return null;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (0 != qyTellService_msg(iServiceId, serviceVer, pCommEncCtx, CONST_qyCmd_tellService, tStartTime, uiTranNo, tmpBuf4096, pSock, pSockParam.to, pSession, pMisCnt.commEncCtx.m_var.publicKeyBlob_base64)) {
                Log.e("qnmOpenSession_mis: qyTellService_msg failed");
                return null;
            }
            qyMsgFunc.QY_MSG_HEAD tmpResp = new qyMsgFunc.QY_MSG_HEAD();
            tmpResp.uiLen = 0;
            tmpResp.i64StartTime = tStartTime;
            tmpResp.uiTranNo = uiTranNo;
            return qyRecvMsg(pSession, pSock, pSockParam.to, tmpResp);
        }


        //
        public static MIS_MSG_routeTalkData MACRO_msgRouteTalkData(byte ucFlg, MIS_MSG_routeTalkData pMsgData) {
            //(  (  isUcFlgRouteTalkData(  ucFlg  )  ||  isUcFlgTalkData(  ucFlg  )  )  ?  pMsgBuf  :  NULL  )
            return ((isUcFlgRouteTalkData(ucFlg) || isUcFlgTalkData(ucFlg)) ? pMsgData : null);
        }

        public static IM_CONTENTU M_getMsgContent(int uiMsgType, byte ucFlg, Object pMsgData) {
            if (uiMsgType == Consts.CONST_misMsgType_input) {
                if (!((isUcFlgRouteTalkData(ucFlg) || isUcFlgTalkData(ucFlg)))) {
                    int ii = 0;
                }
                return ((MIS_MSG_routeTalkData) pMsgData).buf;
            }
            return ((isUcFlgRouteTalkData(ucFlg) || isUcFlgTalkData(ucFlg)) ? ((MIS_MSG_routeTalkData) pMsgData).buf : (IM_CONTENTU) pMsgData);
        }

        //
        public static void MACRO_qyNullThread(Thread h) {
            //if  (  h  )  {	::WaitForSingleObject(h, INFINITE  );	CloseHandle(h  ); h	=	NULL;	}
            if (h != null) {
                int i;
                int maxStep = 100000;
                for (i = 0; i < maxStep; i++) {
                    if (!h.isAlive()) break;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Log.e(e.getMessage());
                        return;
                    }
                    continue;

                }
                if (i == maxStep) {
                    int ii = 0;
                    showInfo_open.qyFuncs.showInfo_open(null, "MACRO_qyNullThread", "failed, serious err");
                }
                h = null;
            }
            return;
        }


        public static int qyhtonImSimpleResp(int lhClientVer, IM_SIMPLE_RESP p) {
            p.uiType = qyhtoni(p.uiType);
            p.usCmd_org = qyhtons((short) p.usCmd_org);
            p.uiContentType_org = (short) qyhtoni(p.uiContentType_org);
            p.iStatus = qyhtoni(p.iStatus);
            //
            //  MACRO_htonl64(  p.tStartTime_org  );		//  2009/10/17
            //  MACRO_htonl(  p.uiTranNo_org  );
            //
            return 0;//sizeof(IM_SIMPLE_RESP);
        }

        public static int qyntohImSimpleResp(int lhClientVer, IM_SIMPLE_RESP p) {
            p.uiType = qyntohi(p.uiType);
            p.usCmd_org = qyntohs((short) p.usCmd_org);
            p.uiContentType_org = (short) qyntohi(p.uiContentType_org);
            p.iStatus = qyntohi(p.iStatus);
            //
            //  MACRO_ntohl64(  p.tStartTime_org  );		//  2009/10/17
            //  MACRO_ntohl(  p.uiTranNo_org  );
            //
            return 0;//sizeof(IM_SIMPLE_RESP);
        }


        /////


        public static int qyhtonImLStream(int lhClientVer, IM_L_STREAM p) {
            int len = -1;

            int tmp_l = (int) p.ulStreamLen;
            if (tmp_l > bufSize_imLStream) return -1;

            len = tmp_l + 12;// (int)Marshal.OffsetOf<IM_L_STREAM>("buf")

            p.uiType = qyhtoni(p.uiType);
            p.uiContentType = qyhtoni(p.uiContentType);
            p.ulStreamLen = qyhtoni(p.ulStreamLen);

            return len;
        }

        public static int qyntohImLStream(long lhClientVer, ByteBuffer p, IM_L_STREAM_nh pStream) {
            int len = -1;

            if (null == p || null == pStream) {
                Log.e("qyntohImLStream failed, p or pStream is null");
                return -1;
            }
//            pStream.uiType = p.getInt();
            pStream.uiContentType = p.getInt();
            pStream.ulStreamLen = p.getInt();
            pStream.p = p;

            //  if  (  p.ulStreamLen  >  sizeof(  p.buf  )  )  return  -1;
            int tmp_ul = pStream.ulStreamLen;
            if (tmp_ul > bufSize_imLStream) return -1;

            len = tmp_ul + 12;//offsetof(IM_L_STREAM,buf);
            return len;
        }


        //
        public static int retrieveImObjListReq2Stream(int uiStreamId, RETRIEVE_IMOBJLIST_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;

            int total_len = 0;

            if (null == pReq) return iErr;
            int len = 0;
            if (0 > (len = data2Stream(CONST_qyDataType_long, (short) CONST_qnmCfgId_start, uiStreamId, 0, ptr))) {
                Log.e("CONST_qnmCfgId_start data2Stream failed");
                return iErr;
            }
            total_len += len;
            if (0 != pReq.usMaxContactUpdateInfosPerSnd) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usMaxMemsPerSnd, pReq.usMaxContactUpdateInfosPerSnd, 0, ptr))) {
                    Log.e("CONST_qnmCfgId_usMaxMemsPerSnd data2Stream failed");
                    return iErr;
                }
                total_len += len;
            }

            if (0 != pReq.tLastRefreshedTime_contactList_misServ) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, (short) CONST_qnmCfgId_tLastRefreshedTime, pReq.tLastRefreshedTime_contactList_misServ, Long.BYTES, ptr))) {
                    Log.e("CONST_qnmCfgId_tLastRefreshedTime data2Stream failed");
                    return iErr;
                }
                total_len += len;
            }
            if (0 != pReq.ucbNext) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, (short) CONST_qnmCfgId_ucbNext, pReq.ucbNext, 0, ptr))) {
                    Log.e("CONST_qnmCfgId_ucbNext data2Stream failed");
                    return iErr;
                }
                total_len += len;
            }
            if (pReq.idInfo_grp_lastRefreshed.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, (short) CONST_qnmCfgId_idInfo_grp_lastRefreshed, pReq.idInfo_grp_lastRefreshed.ui64Id, Long.BYTES, ptr))) {
                    Log.e("CONST_qnmCfgId_idInfo_grp_lastRefreshed data2Stream failed");
                    return iErr;
                }
                total_len += len;
            }
            if (pReq.idInfo_lastRefreshed.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, (short) CONST_qnmCfgId_idInfo_lastRefreshed, pReq.idInfo_lastRefreshed.ui64Id, Long.BYTES, ptr))) {
                    Log.e("CONST_qnmCfgId_idInfo_lastRefreshed data2Stream failed");
                    return iErr;
                }
                total_len += len;
            }
            if (0 > (len = data2Stream(CONST_qyDataType_long, (short) CONST_qnmCfgId_null, null, 0, ptr))) {
                Log.e("CONST_qnmCfgId_null data2Stream failed");
                return iErr;
            }
            total_len += len;
            return total_len;
        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2RetrieveImGrpListResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            RETRIEVE_IMGRPLIST_RESP pResp = (RETRIEVE_IMGRPLIST_RESP) p1;
            if (null == pResp) return iErr;

            int tmp_cfgId = pItem.cfgId;
            switch (uiStreamId) {
                case CONST_imCommType_retrieveImGrpListResp:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pResp.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_ucbEnd:
                            pResp.ucbEnd = getFieldData_byte(pItem);
                            break;
                        //
                        case CONST_qnmCfgId_idInfo_lastRefreshed: {

                            pResp.idInfo_lastRefreshed.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_usCnt:
                            pResp.usCnt = getFieldData_short(pItem);
                            if (pResp.usCnt > CONST_maxContactUpdateInfos_grp) return iErr;
                            break;
                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem: {
                    if (pResp.index >= CONST_maxContactUpdateInfos_grp) {
                        Log.i("tmpHandler_stream2RetrieveImGrpListResp: failed");
                        return iErr;
                    }

                    IM_GRP_INFO pMem = getMem.qyFuncs.getMem(pResp.mems, bufSize_imGrpInfo_mems, pResp.index);
                    if (pMem == null) return iErr;
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_messengerId: {
                            pMem.idInfo.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                            break;
                        }
                        case CONST_qnmCfgId_hint:
                            pMem.name = getFieldData_wStr(pItem);
                            break;
                        case CONST_qnmCfgId_subtype:
                            pMem.usSubtype = getFieldData_short(pItem);
                            break;
                        case CONST_qnmCfgId_idInfo_creator: {
                            pMem.idInfo_creator.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_null:
                            pResp.index++;
                            break;
                        default:
                            break;
                    }
                    //
                }
                break;
                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };

        public static int retrieveImGrpMemListResp2Stream(int uiStreamId, RETRIEVE_IMGRPMEMLIST_RESP pUpdateInfos, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pUpdateInfos) return -1;
            if (null == ptr) return -1;
            //
            if (pUpdateInfos.usCnt > CONST_maxContactUpdateInfos_grpMem) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (pUpdateInfos.idInfo_grp_lastRefreshed.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_lastRefreshed, pUpdateInfos.idInfo_grp_lastRefreshed.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }
            if (pUpdateInfos.idInfo_lastRefreshed.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_lastRefreshed, pUpdateInfos.idInfo_lastRefreshed.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }

            if (0 != pUpdateInfos.ucbEnd) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbEnd, pUpdateInfos.ucbEnd, 0, ptr)))
                    return iErr;
            }
            if (0 != pUpdateInfos.usCnt) {
                //
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pUpdateInfos.usCnt, 0, ptr)))
                    return iErr;

                //
                for (i = 0; i < pUpdateInfos.usCnt; i++) {
                    //  ÏÂÃæ½«Ò»¸öQY_MESSENGER_REGINFO×ª»»³ÉÁ÷
                    IM_GRP_MEM[] pMems = null;// (IM_GRP_MEM)pUpdateInfos.buf_imGrpMem_mems;
                    IM_GRP_MEM pRegInfo = pMems[i];

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;

                    if (pRegInfo.idInfo_grp.ui64Id != 0) {
                        if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp1, pRegInfo.idInfo_grp.ui64Id, Long.BYTES, ptr)))
                            return iErr;
                    }
                    if (pRegInfo.idInfo_mem.ui64Id != 0) {
                        if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pRegInfo.idInfo_mem.ui64Id, Long.BYTES, ptr)))
                            return iErr;
                    }
                    if (0 != pRegInfo.iRole) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iRole, pRegInfo.iRole, 0, ptr)))
                            return iErr;
                    }

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                }
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;
        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2RetrieveImGrpMemListResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            RETRIEVE_IMGRPMEMLIST_RESP pResp = (RETRIEVE_IMGRPMEMLIST_RESP) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;

            if (null == pResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (uiStreamId) {
                case CONST_imCommType_retrieveImGrpMemListResp:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pResp.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_ucbEnd:
                            pResp.ucbEnd = getFieldData_byte(pItem);
                            break;
                        case CONST_qnmCfgId_idInfo_grp_lastRefreshed: {

                            pResp.idInfo_grp_lastRefreshed.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_idInfo_lastRefreshed: {

                            pResp.idInfo_lastRefreshed.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_usCnt:
                            pResp.usCnt = getFieldData_short(pItem);
                            if (pResp.usCnt > CONST_maxContactUpdateInfos_grpMem) return iErr;
                            break;
                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem: {
                    IM_GRP_MEM pMem = null;
                    //
                    if (pResp.index >= CONST_maxContactUpdateInfos_grpMem) {
                        Log.i("tmpHandler_stream2RetrieveImGrpListResp: failed");
                        return iErr;
                    }
                    //
                    int bufSize = bufSize_imGrpMem_mems;
                    //
                    pMem = getMem.qyFuncs.getMem(pResp.mems, bufSize, pResp.index);// &pMems[pResp.index];

                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_idInfo_grp1: {

                            pMem.idInfo_grp.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_messengerId: {

                            pMem.idInfo_mem.ui64Id =  ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_iRole:
                            if (-1 == getFieldData_long(pItem)) return iErr;
                            break;
                        case CONST_qnmCfgId_null:
                            pResp.index++;
                            break;
                        default:
                            break;
                    }
                    //
                }
                break;
                default:
                    break;
            }

            iErr = 0;

            if (0 != iErr) {
                Log.i("tmpHandler_stream2RetrieveImGrpMemListResp: failed");
            }
            return iErr;
        };


        public static int retrieveContactListResp2Stream(int uiStreamId, RETRIEVE_CONTACTLIST_RESP pUpdateInfos, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int j;

            if (null == pUpdateInfos) return -1;
            if (null == ptr) return -1;
            //
            if (pUpdateInfos.usCnt > CONST_maxContactUpdateInfos_contact) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (pUpdateInfos.idInfo_lastRefreshed.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_lastRefreshed, pUpdateInfos.idInfo_lastRefreshed.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }

            if (0 != pUpdateInfos.ucbEnd) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbEnd, pUpdateInfos.ucbEnd, 0, ptr)))
                    return iErr;
            }
            if (0 != pUpdateInfos.usCnt) {
                //
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pUpdateInfos.usCnt, 0, ptr)))
                    return iErr;
                //
                for (i = 0; i < pUpdateInfos.usCnt; i++) {
                    //  ÏÂÃæ½«Ò»¸öQY_MESSENGER_REGINFO×ª»»³ÉÁ÷
                    RETRIEVE_contact_mem[] pMems = null;// (RETRIEVE_contact_mem)pUpdateInfos.buf_retrieveContactMem_mems;
                    RETRIEVE_contact_mem pMem = pMems[i];
                    QY_MESSENGER_REGINFO pRegInfo = pMem.regInfo;

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;

                    if (0 != pRegInfo.addr.uiObjType && 0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiObjType, pRegInfo.addr.uiObjType, 0, ptr)))
                        return iErr;    //  2008/01/31
                    if (pRegInfo.addr.idInfo.ui64Id != 0 && 0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pRegInfo.addr.idInfo.ui64Id, Long.BYTES, ptr)))
                        return iErr;
                    //
                    if (0 != pMem.iRole) {  //  2017/08/14
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iRole, pMem.iRole, 0, ptr)))
                            return iErr;
                    }
                    //
                    if (0 != pRegInfo.addr.uiDevType && 0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDevType_from, pRegInfo.addr.uiDevType, 0, ptr)))
                        return iErr;
                    //if (0 != pRegInfo.addr.wDevIdStr[0] && 0 >(len = data2Stream(CONST_qyDataType_wStr, CONST_qnmCfgId_wDevIdStr_from, pRegInfo.addr.wDevIdStr, lstrlen(pRegInfo.addr.wDevIdStr),ptr,len)) return iErr;   //  2008/01/31


                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                }
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;
        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2RetrieveContactListResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            RETRIEVE_CONTACTLIST_RESP pResp = (RETRIEVE_CONTACTLIST_RESP) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            int lVal = 0;

            if (null == pResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (uiStreamId) {
                case CONST_imCommType_retrieveContactListResp:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pResp.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_ucbEnd:
                            if (0 > (pResp.ucbEnd = getFieldData_byte(pItem)))
                                return iErr;
                            break;
                        //
                        case CONST_qnmCfgId_idInfo_lastRefreshed: {

                            pResp.idInfo_lastRefreshed.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_usCnt:
                            if (0 > (pResp.usCnt = getFieldData_short(pItem)))
                                return iErr;
                            //
                            if (pResp.usCnt > CONST_maxContactUpdateInfos_contact) return iErr;
                            break;
                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem: {
                    RETRIEVE_contact_mem pMem = null;
                    //
                    if (pResp.index >= CONST_maxContactUpdateInfos_contact || pResp.iFiledIndex >= CONST_qnmMaxRegFields) {
                        Log.i("tmpHandler_stream2RetrieveContactListResp: failed");
                        return iErr;
                    }
                    pMem = getMem.qyFuncs.getMem(pResp.mems, bufSize_retrieveContaceMem_mems, pResp.index);

                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_uiObjType:
                            if (-1 == (pMem.regInfo.addr.uiObjType = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_messengerId: {

                            pMem.regInfo.addr.idInfo.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_iRole:  //  2017/08.14
                            if (-1 == (pMem.iRole = getFieldData_long(pItem))) return iErr;
                            break;
                        case CONST_qnmCfgId_uiDevType_from:
                            if (-1 == (pMem.regInfo.addr.uiDevType = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        /**/
                        case CONST_qnmCfgId_uiFieldId:
                            if (-1 == (lVal = getFieldData_long(pItem))) return iErr;
                            pMem.regInfo.uiFieldIds[pResp.iFiledIndex] = lVal;
                            break;
                        case CONST_qnmCfgId_hint: {
                            //if (0 != getFieldData_wStr(pItem, pMem.regInfo.u.cols[pResp.iFiledIndex], mycountof(pMem.regInfo.u.cols[pResp.iFiledIndex]))) return iErr;
                            pMem.regInfo.tmp_col = getFieldData_wStr(pItem);

                            switch (pMem.regInfo.uiFieldIds[pResp.iFiledIndex]) {
                                case CONST_fieldId_dw:
                                    pMem.regInfo.dw = pMem.regInfo.tmp_col;//, CONST_maxNameLen + 1);
                                    break;
                                case CONST_fieldId_bm:
                                    pMem.regInfo.bm = pMem.regInfo.tmp_col;//, CONST_maxNameLen + 1);
                                    break;
                                case CONST_fieldId_syr:
                                    pMem.regInfo.syr = pMem.regInfo.tmp_col;//, CONST_maxNameLen + 1);
                                    break;
                                default:
                                    break;

                            }
                        }
                        pResp.iFiledIndex++;
                        break;
                        case CONST_qnmCfgId_null:
                            pResp.index++;
                            pResp.iFiledIndex = 0;
                            break;
                        default:
                            break;
                    }
                    //
                }
                break;
                default:
                    break;
            }

            iErr = 0;

            if (iErr != 0) {
                Log.i("tmpHandler_stream2RetrieveContactListResp: failed");
            }
            return iErr;
        };

        public static int retrieveImObjListEndResp2Stream(int uiStreamId, RETRIEVE_IMOBJLISTEND_RESP pResp, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pResp) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pResp.tLastRefreshedTime_contactList_misServ) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tLastRefreshedTime, pResp.tLastRefreshedTime_contactList_misServ, Long.BYTES, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;
        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2RetrieveImObjListEndResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            RETRIEVE_IMOBJLISTEND_RESP pResp = (RETRIEVE_IMOBJLISTEND_RESP) p1;
            //  QY_CFGITEM							*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pResp) return iErr;

            if (uiStreamId != CONST_imCommType_retrieveImObjListEndResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pResp.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_tLastRefreshedTime:
                    if (0 > (pResp.tLastRefreshedTime_contactList_misServ = getFieldData_l64(pItem)))
                        return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };

        public static int transferFileReq2Stream(int uiStreamId, TRANSFER_FILE_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.ui64FileLen) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_ui64FileLen, pReq.ui64FileLen, Long.BYTES, ptr)))
                    return iErr;
            }
            if (null != pReq.fileName) {
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_fileName, pReq.fileName, (int) mylstrlen(pReq.fileName), ptr)))
                    return iErr;
            }

            //  2014/07/13
            if (0 != pReq.uiSizePerSnd_suggest) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSizePerSnd, pReq.uiSizePerSnd_suggest, 0, ptr)))
                    return iErr;
            }

            //  2015/08/05
            if (0 != pReq.usSubtype) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_subtype, pReq.usSubtype, 0, ptr)))
                    return iErr;
            }

            if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_logicalPeer, pReq.idInfo_logicalPeer.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }


            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferFileReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_FILE_REQ pContent = (TRANSFER_FILE_REQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileReq) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_fileName:
                    pContent.fileName = getFieldData_wStr(pItem);
                    break;
                case CONST_qnmCfgId_ui64FileLen:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiSizePerSnd:   //  2014/07/13
                    if (-1 == getFieldData_long(pItem))
                        return iErr;
                    break;
                case CONST_qnmCfgId_subtype:   //  2015/08/05
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_idInfo_logicalPeer: {

                    pContent.idInfo_logicalPeer.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                }
                break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };

        public static int transferFileReplyReq2Stream(int uiStreamId, TRANSFER_FILE_REPLYREQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiSerialNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSerialNo_org, pReq.uiSerialNo_org, 0, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferFileReplyReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_FILE_REPLYREQ pContent = (TRANSFER_FILE_REPLYREQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileReplyReq) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_usOp:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_tStartTime_org:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiTranNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiSerialNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };

        public static int taskProcReq2Stream(int uiStreamId, TASK_PROC_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.uiSerialNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSerialNo_org, pReq.uiSerialNo_org, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.uiContentType_org) {   //
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiContentType_org, pReq.uiContentType_org, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //  2014/08/28
            if (0 != pReq.usTaskProcObjType) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usTaskProcObjType, pReq.usTaskProcObjType, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.usTaskProcObjType) {

                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                    return iErr;
                total_len += len;

                switch (pReq.usTaskProcObjType) {
                    case CONST_usTaskProcObjType_vStream:
                        //
                        if (0 != pReq.vStream.uiTranNo_openAvDev) {
                            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openVideoDev, pReq.vStream.uiTranNo_openAvDev, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        //
                        //
                        break;
                    default:
                        break;
                }


                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                    return iErr;
                total_len += len;
            }


            //  2009/09/11
            if (0 != pReq.uiTaskType) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTaskType, pReq.uiTaskType, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.viewDynBmp.uiObjType) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiObjType, pReq.viewDynBmp.uiObjType, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.viewDynBmp.usIndex_obj) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_index, pReq.viewDynBmp.usIndex_obj, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            if (0 == iErr) {

            }
            return total_len;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TaskProcReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TASK_PROC_REQ pContent = (TASK_PROC_REQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            //if  (  uiStreamId  !=  CONST_imCommType_taskProcReq  )  goto  errLabel;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (uiStreamId) {
                case CONST_imCommType_taskProcReq:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_usOp:
                            if (0 > (pContent.usOp = getFieldData_short(pItem))) return iErr;
                            break;
                        case CONST_qnmCfgId_tStartTime_org:
                            if (0 > ( pContent.tStartTime_org = getFieldData_l64(pItem))) return iErr;
                            break;
                        case CONST_qnmCfgId_uiTranNo_org:
                            if (-1 == (pContent.uiTranNo_org = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_uiSerialNo_org:
                            if (-1 == (pContent.uiSerialNo_org = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_uiContentType_org:
                            if (-1 == (pContent.uiContentType_org = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usTaskProcObjType:  //  2014/08/28
                            if (0 > (pContent.usTaskProcObjType = getFieldData_short(pItem)))
                                return iErr;
                            break;

                        //  2014/10/09
                        case CONST_qnmCfgId_uiTaskType: //  2009/09/11
                            if (-1 == (pContent.uiTaskType = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_uiObjType:
                            if (-1 == (pContent.viewDynBmp.uiObjType = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_index:
                            if (0 > (pContent.viewDynBmp.usIndex_obj = getFieldData_short(pItem)))
                                return iErr;
                            break;

                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem:  //  2014/08/28
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_uiTranNo_openVideoDev:
                            if (-1 == (pContent.vStream.uiTranNo_openAvDev = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        //
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            iErr = 0;

            return iErr;
        };

        public static int taskProcResp2Stream(int uiStreamId, TASK_PROC_RESP pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.usCmd_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCmd_org, pReq.usCmd_org, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.usOp_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp_org, pReq.usOp_org, 0, ptr)))
                    return iErr;
            }

            if (0 != pReq.iStatus) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_status, pReq.iStatus, 0, ptr)))
                    return iErr;
            }

            if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_logicalPeer, pReq.idInfo_logicalPeer.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TaskProcResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TASK_PROC_RESP pContent = (TASK_PROC_RESP) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_taskProcResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_tStartTime_org:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiTranNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_usCmd_org:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_usOp_org:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_status:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_idInfo_logicalPeer: {

                    pContent.idInfo_logicalPeer.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                }
                break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };


        //
        public static int transferFileDataReq2Stream(int uiStreamId, TRANSFER_FILEDATA_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiSerialNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSerialNo_org, pReq.uiSerialNo_org, 0, ptr)))
                    return iErr;
            }

            if (0 != pReq.uiSizePerSnd) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSizePerSnd, pReq.uiSizePerSnd, 0, ptr)))
                    return iErr;
            }

            if (0 != pReq.uiDataBlockIndex) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDataBlockIndex, pReq.uiDataBlockIndex, 0, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferFileDataReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_FILEDATA_REQ pContent = (TRANSFER_FILEDATA_REQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileDataReq) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_tStartTime_org:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiTranNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiSerialNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiSizePerSnd:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiDataBlockIndex:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };


        public static int transferFileDataResp2Stream(int uiStreamId, TRANSFER_FILEDATA_RESP pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.uiDataBlockIndex) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDataBlockIndex, pReq.uiDataBlockIndex, 0, ptr)))
                    return iErr;
            }
            //  2014/07/13
            if (0 != pReq.uiLen) {
                if (0 > (len = data2Stream(CONST_qyDataType_lData, CONST_qnmCfgId_rawData, pReq.buf, pReq.uiLen, ptr)))
                    return iErr;
            }


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferFileDataResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_FILEDATA_RESP pContent = (TRANSFER_FILEDATA_RESP) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileDataResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_uiDataBlockIndex:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_rawData:
                    pContent.uiLen = Consts.bufSize_transferFileDataResp;// sizeof(pContent.buf  );

                    if (null  == (pContent.buf = getFieldData_lData(pItem))) return iErr;
                    pContent.uiLen = pContent.buf.capacity();
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };

        public static int transferFileEndReq2Stream(int uiStreamId, TRANSFER_FILEEND_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.uiSerialNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSerialNo_org, pReq.uiSerialNo_org, 0, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferFileEndReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_FILEEND_REQ pContent = (TRANSFER_FILEEND_REQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileEndReq) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_tStartTime_org:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiTranNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_uiSerialNo_org:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };


        public static int transferFileEndResp2Stream(int uiStreamId, TRANSFER_FILEEND_RESP pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.uiVerifyMethod) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiVerifyMethod, pReq.uiVerifyMethod, 0, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 == iErr) {

            }
            return iErr;

        }

        public static int tmpHandler_stream2TransferFileEndResp(isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) {
            int iErr = -1;
            //  p0;
            TRANSFER_FILEEND_RESP pContent = (TRANSFER_FILEEND_RESP) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferFileEndResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_uiVerifyMethod:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        }


        //////////////////////////////////////////////////////
        public static int qisGetCfgsReq2Stream(int uiStreamId, QIS_getCfgs_req pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;
            int total_len = ptr.position();
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr))) {
                return iErr;
            }
            total_len += len;
            if (0 != pReq.ucbResp) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr))) {
                    return iErr;
                }
                total_len += len;
            }

            if (0 != pReq.uiSizePerSnd_media) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSizePerSnd, pReq.uiSizePerSnd_media, 0, ptr))) {
                    return iErr;
                }
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usIntervalInS_refreshRecentFriends, pReq.intervalParams.usIntervalInS_refreshRecentFriends, 0, ptr))) {
                return iErr;
            }
            total_len += len;

            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usIntervalInS_refreshContactList, pReq.intervalParams.usIntervalInS_refreshContactList, 0, ptr))) {
                return iErr;
            }
            total_len += len;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr))) {
                return iErr;
            }
            total_len += len;

            return total_len;
        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2QisGetCfgsReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            if (uiStreamId != CONST_imCommType_getCfgsReq) return iErr;

            QIS_getCfgs_req pContent = (QIS_getCfgs_req) p1;

            int tmp_cfgId = pItem.cfgId;
            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_ucbResp:
                    pContent.ucbResp = (byte) getFieldData_byte(pItem);
                    break;
                case CONST_qnmCfgId_uiSizePerSnd:
                    pContent.uiSizePerSnd_media = getFieldData_long(pItem);
                    break;
                case CONST_qnmCfgId_usIntervalInS_refreshRecentFriends:
                    pContent.intervalParams.usIntervalInS_refreshRecentFriends = getFieldData_short(pItem);
                    break;
                case CONST_qnmCfgId_usIntervalInS_refreshContactList:
                    pContent.intervalParams.usIntervalInS_refreshContactList = getFieldData_short(pItem);
                    break;
                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };

        public static int taskInteractionReq2Stream(int uiStreamId, TASK_INTERACTION_REQ pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            int total_len = 0;
            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //  2015/02/17
            if (0 > (len = tmp_htonAvStream(pReq.avStream, ptr)))
                return iErr;
            total_len += len;

            //
            //
            if (null != pReq.talkerDesc) {
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_talkerDesc, pReq.talkerDesc, (int) mylstrlen(pReq.talkerDesc), ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iHkStatus, pReq.iHkStatus, (int) 0, ptr)))
                return iErr;
            total_len += len;

            //
            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            if (0 == iErr) {

            }
            return total_len;

        }

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TaskInteractionReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TASK_INTERACTION_REQ pContent = (TASK_INTERACTION_REQ) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;
            boolean bProcessed = false;    //  2015/02/17


            if (null == pContent) return iErr;


            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            //
            switch (uiStreamId) {
                case CONST_imCommType_taskInteractionReq:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_usOp:
                            if (0 > getFieldData_short(pItem)) return iErr;
                            break;
                        case CONST_qnmCfgId_tStartTime_org:
                            if (0 > getFieldData_l64(pItem)) return iErr;
                            break;
                        case CONST_qnmCfgId_uiTranNo_org:
                            if (0 > getFieldData_long(pItem)) return iErr;
                            break;
                        case CONST_qnmCfgId_talkerDesc:
                            pContent.talkerDesc = getFieldData_wStr(pItem);
                            break;
                        case CONST_qnmCfgId_idInfo_grp_related: {

                            pContent.idInfo_imGrp_related.ui64Id =  ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        default:
                            //
                            break;
                    }
                    break;

                case CONST_imCommType_avStream:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            break;
                        case CONST_qnmCfgId_null:
                            break;
                        default:
                            //  2015/02/17
                            //if  (  tmp_ntohAvTranInfo(  pItem,  &pContent.tranInfo,  &bProcessed  )  )  goto  errLabel;
                            if (0 > tmp_ntohAvStream(pItem,pContent.avStream, bProcessed))
                                return iErr;

                            bProcessed = true;
                            if (bProcessed) {
                                iErr = 0;
                                return iErr;
                            }
                            //
                            break;
                    }
                    break;

                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };

        public static int confKey2Stream(int uiStreamId, CONF_KEY pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            //  2010/12/29
            if (0 != pReq.usLayoutType_starter) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usLayoutType, pReq.usLayoutType_starter, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.usCnt) {
                //
                if (pReq.usCnt > cntof_videoConferenceMems) return iErr;
                //
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pReq.usCnt, 0, ptr)))
                    return iErr;
                total_len += len;
                //
                VIDEO_CONFERENCE_MEM[] pReq_mems = pReq.buf_VIDEO_CONFERENCE_MEM_mems;
                for (i = 0; i < pReq.usCnt; i++) {
                    VIDEO_CONFERENCE_MEM pMem = pReq_mems[i];

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;
                    total_len += len;

                    //  2009/05/31
                    if (0 > (len = tmp_htonAvStream(pMem.avStream, ptr))) return iErr;
                    total_len += len;

                    //  2013/07/18
                    if (0 != pMem.usIndex_speaker) {
                        if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usIndex_speaker, pMem.usIndex_speaker, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                    total_len += len;

                }

            }

            if (0 > (len = tmp_htonConfLayoutParam(pReq.confLayoutParam, ptr))) return iErr;
            total_len += len;

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            return total_len;
        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2confKey = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            CONF_KEY pContent = (CONF_KEY) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;
            boolean bProcessed = false;

            if (null == pContent) return iErr;

            short tmp_cfgId = pItem.cfgId;
            if (uiStreamId == CONST_imCommType_confLayoutParam) {
                tmp_ntohConfLayoutParam(pCtx, (int) uiStreamId, pItem,pContent.confLayoutParam, bProcessed);
            }
            switch (uiStreamId) {
                case CONST_imCommType_confKey:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_idInfo_grp_related: {

                            pContent.idInfo_imGrp_related.ui64Id = ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_usLayoutType:
                            if (0 >(pContent.usLayoutType_starter = getFieldData_short(pItem)))
                                return iErr;
                            break;

                        case CONST_qnmCfgId_usCnt:
                            if (0 > (pContent.usCnt = getFieldData_short(pItem)))
                                return iErr;
                            //
                            if (pContent.usCnt > cntof_videoConferenceMems) return iErr;
                            break;
                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem: {
                    VIDEO_CONFERENCE_MEM pMem = null;
                    if (pContent.tmpInternal.usCnt_mems >= cntof_videoConferenceMems) {
                        return iErr;
                    }
                    pMem = pContent.buf_VIDEO_CONFERENCE_MEM_mems[pContent.tmpInternal.usCnt_mems];
                    if(pMem == null) return iErr;
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_usIndex_speaker:
                            if (0 >(pMem.usIndex_speaker = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_null:
                            pContent.tmpInternal.usCnt_mems++;
                            break;
                        default:
                            break;
                    }
                }
                break;
                case CONST_imCommType_avStream: {
                    VIDEO_CONFERENCE_MEM pMem = null;
                    if (pContent.tmpInternal.usCnt_mems >= cntof_videoConferenceMems) {
                        return iErr;
                    }
                    VIDEO_CONFERENCE_MEM[] pContent_mems = pContent.buf_VIDEO_CONFERENCE_MEM_mems;
                    pMem = pContent_mems[pContent.tmpInternal.usCnt_mems];
                    if(pMem == null) return iErr;
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            break;
                        default:
                            if (0 > tmp_ntohAvStream(pItem, pMem.avStream, bProcessed))
                                return iErr;

                            bProcessed = true;
                            if (bProcessed) {
                                iErr = 0;
                                return iErr;
                            }
                            break;
                    }

                    //
                }
                break;

                default:
                    break;

            }
            iErr = 0;

            return iErr;
        };

        public static int statusConfLayoutParam2Stream(int uiStreamId, StatusConfLayoutParam pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (pReq.idInfo_imGrp_related.ui64Id != 0) {                                                                                                       // pReq.idInfo_imGrp_related.ui64Id, sizeof,ptr,len
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            {
                if (0 > (len = tmp_htonConfLayoutParam(pReq.confLayoutParam, ptr))) return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            if (0 == iErr) {

            }
            return total_len;


        }


        //
        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2statusConfLayoutParam = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            StatusConfLayoutParam pContent = (StatusConfLayoutParam) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;
            boolean bProcessed = false;

            if (null == pContent) return iErr;

            int tmp_cfgId;
            // memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            //
            int uiStreamId_1 = 0;
            if (pCtx.index_streamId > 0) {
                uiStreamId_1 = pCtx.uiStreamIds[1];
            }


            if (uiStreamId_1 == CONST_imCommType_confLayoutParam) {
                tmp_ntohConfLayoutParam(pCtx, (int) uiStreamId, pItem,pContent.confLayoutParam, bProcessed);
            } else {
                //
                switch (uiStreamId) {
                    case CONST_imCommType_statusConfLayoutParam:
                        switch (tmp_cfgId) {
                            case CONST_qnmCfgId_start:
                                pContent.uiType = uiStreamId;
                                break;
                            case CONST_qnmCfgId_idInfo_grp_related: {
                                pContent.idInfo_imGrp_related.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                            }
                            break;
                            //
                            default:
                                break;
                        }
                        break;
                }
            }
            iErr = 0;

            return iErr;
        };

        public static int confLayout2Stream(int uiStreamId, CONF_LAYOUT pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }


            if (0 != pReq.usLayoutType) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usLayoutType, pReq.usLayoutType, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 > (len = tmp_htonConfLayoutParam(pReq.confLayoutParam, ptr))) {
                return iErr;
            }
            total_len += len;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            if (0 == iErr) {

            }
            return total_len;

        }

        public static int tmp_htonConfLayoutParam(ConfLayoutParam pReq, ByteBuffer ptr) {

            int iErr = -1;
            int i = 0;
            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            //
            int uiStreamId = CONST_imCommType_confLayoutParam;
            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            //
            if (0 != pReq.oneBigLayoutParam.ucbOneBigLayout) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbOneBigLayout, pReq.oneBigLayoutParam.ucbOneBigLayout, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.oneBigLayoutParam.ui64Id) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId_oneBig, pReq.oneBigLayoutParam.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.enlargeParam.usEnlargeType) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usEnlargeType, pReq.enlargeParam.usEnlargeType, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.enlargeParam.ui64Id) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pReq.enlargeParam.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.enlargeParam.tn_v) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_uiTranNo_openVideoDev, pReq.enlargeParam.tn_v, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.ipcParam.iChannel) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_iChannel, pReq.ipcParam.iChannel, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (null != pReq.ipcParam.channelName) {
                //  data2Stream(CONST_qyDataType_wStr, CONST_qnmCfgId_name, pMem.name, (int)mylstrlen(pMem.name),ptr,len)
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_channelName, pReq.ipcParam.channelName, (int) mylstrlen(pReq.ipcParam.channelName), ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.resp.ii) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_ii, pReq.resp.ii, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.resp.curr_idinfo) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_curr_idinfo, pReq.resp.curr_idinfo, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.phoneEnlargeParam.x) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_enlarge_x, pReq.phoneEnlargeParam.x, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.phoneEnlargeParam.y) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_enlarge_y, pReq.phoneEnlargeParam.y, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.phoneEnlargeParam.usEnlargeType) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_phone_usEnlargeType, pReq.phoneEnlargeParam.usEnlargeType, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.respVideoStatus.bAmplification) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_amplificationType, pReq.respVideoStatus.bAmplification, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;


            //
            iErr = 0;

            return total_len;

        }

        //
        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2confLayout = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            CONF_LAYOUT pContent = (CONF_LAYOUT) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;
            //
            int uiStreamId_1 = 0;
            if (pCtx.index_streamId > 0) {
                uiStreamId_1 = (int) pCtx.uiStreamIds[1];
            }

            //
            boolean bProcessed = false;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;
            //
            if (uiStreamId_1 == CONST_imCommType_confLayoutParam) {
                tmp_ntohConfLayoutParam(pCtx, (int) uiStreamId, pItem,pContent.confLayoutParam, bProcessed);
            } else {
                //
                switch (uiStreamId) {
                    case CONST_imCommType_confLayout:
                        switch (tmp_cfgId) {
                            case CONST_qnmCfgId_start:
                                pContent.uiType = uiStreamId;
                                break;
                            case CONST_qnmCfgId_idInfo_grp_related: {

                                pContent.idInfo_imGrp_related.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                            }
                            break;
                            case CONST_qnmCfgId_usLayoutType:
                                if (0 > getFieldData_short(pItem))
                                    return iErr;
                                //
                                break;
                            case CONST_qnmCfgId_ucbResp:
                                if (0 != getFieldData_byte(pItem)) return iErr;
                                break;
                            case CONST_qnmCfgId_usOp:
                                if (0 > getFieldData_short(pItem))
                                    return iErr;
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }

            iErr = 0;

            return iErr;
        };

        ///
        public static int tmp_ntohConfLayoutParam(isProtocolFunc.CTX_stream2Data pCtx, int uiStreamId, qnmCommProc.QY_CFGITEM pItem, ConfLayoutParam pContent,
                                                  boolean pbProcessed) {
            int iErr = -1;
            //
            boolean bProcessed = false;

            if (null == pContent) return iErr;

            int tmp_cfgId;


            tmp_cfgId = (int) pItem.cfgId;

            //
            bProcessed = true;
            switch (uiStreamId) {
                case CONST_imCommType_confLayoutParam:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            break;
                        case CONST_qnmCfgId_ucbOneBigLayout:
                            if (0 > (pContent.oneBigLayoutParam.ucbOneBigLayout = getFieldData_byte(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_messengerId_oneBig:
                            if (0 > (pContent.oneBigLayoutParam.ui64Id = getFieldData_l64(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usEnlargeType:
                            if (0 >(pContent.enlargeParam.usEnlargeType = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_messengerId:
                            if (0 >(pContent.enlargeParam.ui64Id= getFieldData_l64(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_uiTranNo_openVideoDev:
                            if (0 >(pContent.enlargeParam.tn_v= getFieldData_long(pItem)))
                                return iErr;
                            break;
                        //
                        case CONST_qnmCfgId_iChannel:
                            if (0 >(pContent.ipcParam.iChannel = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_channelName:
                            pContent.ipcParam.channelName = getFieldData_wStr(pItem);
                        break;
                        case CONST_qnmCfgId_ii:
                            if (0 >(pContent.resp.ii= getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_curr_idinfo:
                            if (0 >(pContent.resp.curr_idinfo= getFieldData_l64(pItem)))
                                return iErr;
                        break;
                        case CONST_qnmCfgId_enlarge_x:
                            if (0 >(pContent.phoneEnlargeParam.x= getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_enlarge_y:
                            if (0 >(pContent.phoneEnlargeParam.y= getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_phone_usEnlargeType:
                            if (0 > (pContent.phoneEnlargeParam.usEnlargeType= getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_amplificationType:
                            if (0 >(pContent.respVideoStatus.bAmplification= getFieldData_long(pItem)))
                                return iErr;
                            break;

                        default:
                            break;
                    }
                    break;
                default:
                    break;

            }
            iErr = 0;


            if (true == pbProcessed) pbProcessed = bProcessed;

            return iErr;
        }


        public static int confNvrInfo2Stream(int uiStreamId, ConfNvrInfo pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.ucbResp) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr)))
                    return iErr;
            }

            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }
            if (pReq.idInfo_requester.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_requester, pReq.idInfo_requester.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }

            //
            if (0 != pReq.nvrInfo.usCnt) {

                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_nvr, pReq.nvrInfo.ui64Id, 0, ptr)))
                    return iErr;

                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pReq.nvrInfo.usCnt, 0, ptr)))
                    return iErr;

                for (i = 0; i < pReq.nvrInfo.usCnt; i++) {

                    IpcInfo pMem = null;//getMem((IpcInfo)pReq.nvrInfo.buf_ipcInfo, bufSize_ipcInfos, i);// &pReq.nvrInfo.mems[i];
                    //
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;

                    //
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iChannel, pMem.iChannel, 0, ptr)))
                        return iErr;

                    if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_name, pMem.name, (int) mylstrlen(pMem.name), ptr)))
                        return iErr;

                    if (0 > (len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_ip, pMem.ip, pMem.ip.length(), ptr)))
                        return iErr;

                    //
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                }
            }
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;

            if (0 != iErr) {

            }
            return iErr;

        }

        //(CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, QY_CFGITEM pItem)
        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2confNvrInfo = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            ConfNvrInfo pContent = (ConfNvrInfo) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            //
            int uiStreamId_1 = 0;
            if (pCtx.index_streamId > 0) {
                uiStreamId_1 = (int) pCtx.uiStreamIds[1];
            }

            boolean bProcessed = false;

            int tmp_cfgId;
            tmp_cfgId = (int) pItem.cfgId;
            switch (uiStreamId) {
                case CONST_imCommType_confNvrInfo:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_ucbResp:
                            if (0 >(pContent.ucbResp = getFieldData_byte(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_idInfo_grp_related: {

                            pContent.idInfo_imGrp_related.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_idInfo_requester: {

                            pContent.idInfo_requester.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;

                        case CONST_qnmCfgId_idInfo_nvr:
                            if (0 > (pContent.nvrInfo.ui64Id = getFieldData_l64(pItem))) return iErr;
                            break;

                        case CONST_qnmCfgId_usCnt:
                            if (0 > (pContent.nvrInfo.usCnt = getFieldData_short(pItem)))
                                return iErr;
                            //
                            if (pContent.nvrInfo.usCnt >= MAX_ipcInfos) {
                                Log.d(("stream2ConfNvrInfo"));
                                showInfo_open.qyFuncs.showInfo_open(0, null,"stream2ConfNvrInfo");
                                return iErr;
                            }
                            //
                            break;

                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem:

                    if (pContent.tmpInternal.usCnt_mems >= MAX_ipcInfos) {
                        return iErr;
                    }
                    //
//                    IpcInfo pMem = getMem.qyFuncs.getMem((IpcInfo)pContent.nvrInfo.buf_ipcInfo, bufSize_ipcInfos, pContent.tmpInternal.usCnt_mems);
                    IpcInfo pMem = pContent.nvrInfo.mems[pContent.tmpInternal.usCnt_mems];
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_null:
                            pContent.tmpInternal.usCnt_mems++;
                            break;
                        case CONST_qnmCfgId_iChannel:
                            pMem.iChannel = getFieldData_long(pItem);
                            break;
                        case CONST_qnmCfgId_name:
                            pMem.name = getFieldData_wStr(pItem);
                            break;
                        case CONST_qnmCfgId_ip:
                            pMem.ip = getFieldData_str(pItem);
                            break;
                    }
                    break;
                default:
                    break;
            }

            //
            iErr = 0;

            return iErr;
        };


        //  2010/12/27
        public static int confReq2Stream(int uiStreamId, CONF_req pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.policy.usAvLevel) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usAvLevel, pReq.policy.usAvLevel, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.policy.conf_iFourcc) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_conf_iFourcc, pReq.policy.conf_iFourcc, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.policy.usMaxSpeakers) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usMaxSpeakers, pReq.policy.usMaxSpeakers, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (pReq.idInfo_initiator.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_initiator, pReq.idInfo_initiator.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            if (0 == iErr) {

            }
            return total_len;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2ConfReq = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            CONF_req pContent = (CONF_req) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            int tmp_cfgId;
            tmp_cfgId = pItem.cfgId;

            //
            switch (uiStreamId) {
                case CONST_imCommType_confReq:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_idInfo_grp_related: {

                            pContent.idInfo_imGrp_related.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        case CONST_qnmCfgId_usOp:
                            if (0 > (pContent.usOp = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usAvLevel:
                            if (0 >(pContent.policy.usAvLevel= getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_conf_iFourcc:
                            if (-1 == (pContent.policy.conf_iFourcc= getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usMaxSpeakers:
                            if (0 > (pContent.policy.usMaxSpeakers = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_idInfo_initiator: {

                            pContent.idInfo_initiator.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                        }
                        break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            iErr = 0;

            return iErr;
        };

        public static int transferVideoData2Stream(int uiStreamId, TRANSFER_VIDEO_DATA pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;
            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (0 != pReq.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openVideoDev, pReq.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.usCnt) {

                if (pReq.usCnt > CONST_maxTransferAvDataMems) return iErr;

                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pReq.usCnt, 0, ptr)))
                    return iErr;
                total_len += len;
                //
                TRANSFER_VIDEO_dataMemHead pMems[] = pReq.mems;//(TRANSFER_VIDEO_dataMemHead)pReq.buf_TRANSFER_VIDEO_dataMemHead_mems;

                for (i = 0; i < pReq.usCnt; i++) {
                    TRANSFER_VIDEO_dataMemHead pMem = pMems[i];
                    if (pMem == null) return -1;
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;
                    total_len += len;

                    //
                    if (pMem.idInfo.ui64Id != 0) {           //  2009/05/31
                        if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pMem.idInfo.ui64Id, Long.BYTES, ptr)))
                            return iErr;
                        total_len += len;
                    }
                    //  2012/01/09
                    if (0 != pMem.uiTranNo_openAvDev) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openVideoDev, pMem.uiTranNo_openAvDev, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    if (0 != pMem.uiSampleTimeInMs) {       //  2009/05/04
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSampleTimeInMs, pMem.uiSampleTimeInMs, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }
                    //  2015/01/15
                    if (0 != pMem.uiPts) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiPts, pMem.uiPts, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    if (0 != pMem.ucbKeyFrame) {
                        if (0 > (len = data2Stream(CONST_qyDataType_byte, CONST_qnmCfgId_ucbKeyFrame, pMem.ucbKeyFrame, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }


                    //  2013/11/15

                    //
                    if (0 != pMem.uiLen) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiLen, pMem.uiLen, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //   2017/09/05
                    if (0 != pMem.stat.usFps) {
                        if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usFps, pMem.stat.usFps, 0, ptr)))
                            return iErr;
                        total_len += len;

                    }
                    if (0 != pMem.stat.usCnt_pkts) {
                        if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt_pkts, pMem.stat.usCnt_pkts, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }
                    if (0 != pMem.stat.tSelectTime) {   //  2008/12/05
                        //
                        if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tSelectTime, pMem.stat.tSelectTime, Long.BYTES, ptr)))
                            return iErr;
                        total_len += len;
                    }
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
            }

            if (0 != pReq.uiLen) {
                if (0 > (len = data2Stream(CONST_qyDataType_lData, CONST_qnmCfgId_rawData, pReq.buf.array(), pReq.uiLen, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            return total_len;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferVideoData = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            TRANSFER_VIDEO_DATA pContent = (TRANSFER_VIDEO_DATA) p1;

            if (null == pContent) return iErr;

            int tmp_cfgId = pItem.cfgId;

            switch (uiStreamId) {
                case CONST_imCommType_transferVideoData:

                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_uiTranNo_openVideoDev:
                            if (-1 == (pContent.uiTranNo_openAvDev = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usCnt:
                            if (0 > (pContent.usCnt = getFieldData_short(pItem)))
                                return iErr;
                            if (pContent.usCnt > CONST_maxTransferAvDataMems)  //  2011/01/17
                            {
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_rawData:
                            if (null  == (pContent.buf = getFieldData_lData(pItem))) return iErr;
                            pContent.uiLen = pContent.buf.capacity();
                            break;
                        default:
                            break;
                    }
                    break;

                case CONST_imCommType_mem: {
                    TRANSFER_VIDEO_dataMemHead pMem = null;
                    if (pContent.tmpInternal.usCnt_mems >= CONST_maxTransferAvDataMems) {
                        return iErr;
                    }
                    pMem = getMem.qyFuncs.getMem(pContent.mems, bufSize_TRANSFER_VIDEO_dataMemHead_mems, pContent.tmpInternal.usCnt_mems);
                    if(pMem == null) return -1;
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            break;
                        //
                        case CONST_qnmCfgId_messengerId:            //  2009/05/31
                        {
                            if (0 > (pMem.idInfo.ui64Id = getFieldData_l64(pItem)))
                                return iErr;
                            break;
                        }
                        case CONST_qnmCfgId_uiTranNo_openVideoDev:
                            if (-1 == (pMem.uiTranNo_openAvDev = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        //
                        case CONST_qnmCfgId_uiSampleTimeInMs:
                            if (-1 == (pMem.uiSampleTimeInMs = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        //  2015/01/15
                        case CONST_qnmCfgId_uiPts:
                            if (-1 == (pMem.uiPts = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_ucbKeyFrame:
                            if (0 > (pMem.ucbKeyFrame = getFieldData_byte(pItem)))
                                return iErr;
                            break;

                        case CONST_qnmCfgId_uiLen:
                            if (-1 == (pMem.uiLen = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        //  2017/09/05
                        case CONST_qnmCfgId_usFps:
                            if (0 > (pMem.stat.usFps = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_tLastTime_showFrameInfo_020534:
                            if (0 > (pMem.stat.tLastTime_showFrameInfo_020534 = getFieldData_l64(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_usCnt_pkts:
                            if (0 > (pMem.stat.usCnt_pkts = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_tSelectTime:
                            if (0 > (pMem.stat.tSelectTime = getFieldData_l64(pItem)))
                                return iErr;
                            break;

                        //
                        case CONST_qnmCfgId_null:
                            pContent.tmpInternal.usCnt_mems++;
                            break;
                        default: {
                            int ii = 0;
                        }
                        break;
                    }
                    //

                }
                break;


                default:
                    break;
            }

            iErr = 0;
            return iErr;
        };


        public static int transferVideoDataResp2Stream(int uiStreamId, TRANSFER_VIDEO_dataResp pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;


            //  qyShowInfo1(  CONST_qyShowType_debug,  (  ""  ),  (  ""  ),  0,  (  ""  ),  (  ""  ),  (  "    transferVideoData2stream: uiTranNo_openAvDev %d"  ),  pReq.uiTranNo_openAvDev  );


            //  2017/09/05
            if (pReq.idInfo.ui64Id != 0) {           //  2009/05/31
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pReq.idInfo.ui64Id, Long.BYTES, ptr)))
                    return iErr;
            }

            //
            if (0 != pReq.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openVideoDev, pReq.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.usFps) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usFps, pReq.usFps, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.tSelectTime) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tSelectTime, pReq.tSelectTime, Long.BYTES, ptr)))
                    return iErr;
            }
            if (0 != pReq.usCnt_pkts) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt_pkts, pReq.usCnt_pkts, 0, ptr)))
                    return iErr;
            }
            if (0 != pReq.usDiff_pkts) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usDiff_pkts, pReq.usDiff_pkts, 0, ptr)))
                    return iErr;
            }


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;
            if (0 == iErr) {

            }
            return iErr;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferVideoDataResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_VIDEO_dataResp pContent = (TRANSFER_VIDEO_dataResp) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferVideoDataResp) return iErr;

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem.cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_messengerId: {

                    pContent.idInfo.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                }
                break;
                case CONST_qnmCfgId_uiTranNo_openVideoDev:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    //  qyShowInfo1(  CONST_qyShowType_debug,  (  ""  ),  (  ""  ),  0,  (  ""  ),  (  ""  ),  (  "    tmpHandler_stream2TransferVideoData: uiTranNo_openAvDev %d"  ),  pContent.uiTranNo_openAvDev  );
                    break;
                case CONST_qnmCfgId_usFps:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_tSelectTime:
                    if (0 != getFieldData_l64(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_usCnt_pkts:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_usDiff_pkts:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;

                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };


        public static int transferAudioData2Stream(int uiStreamId, TRANSFER_AUDIO_DATA pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;
            int total_len = 0;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            //  qyShowInfo1(  CONST_qyShowType_debug,  (  ""  ),  (  ""  ),  0,  (  ""  ),  (  ""  ),  (  "    transferVideoData2stream: uiTranNo_openAvDev %d"  ),  pReq.uiTranNo_openAvDev  );


            if (0 != pReq.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openAudioDev, pReq.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.tLastTime_showFrameInfo_020534) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tLastTime_showFrameInfo_020534, pReq.tLastTime_showFrameInfo_020534, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.usCnt_pkts) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt_pkts, pReq.usCnt_pkts, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.usCnt) {

                if (pReq.usCnt > CONST_maxTransferAvDataMems) return iErr;

                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pReq.usCnt, 0, ptr)))
                    return iErr;
                total_len += len;
                //
                for (i = 0; i < pReq.usCnt; i++) {
                    TRANSFER_AUDIO_dataMemHead pMems[] = pReq.mems;//(TRANSFER_AUDIO_dataMemHead)pReq.buf_TRANSFER_AUDIO_dataMemHead_mems;
                    TRANSFER_AUDIO_dataMemHead pMem = pMems[i];

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr)))
                        return iErr;
                    total_len += len;
                    //

                    //
                    if (pMem.idInfo.ui64Id != 0) {
                        if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pMem.idInfo.ui64Id, Long.BYTES, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    if (0 != pMem.uiTranNo_openAvDev) {   //  2013/08/04
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openAudioDev, pMem.uiTranNo_openAvDev, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    if (0 != pMem.uiSampleTimeInMs) {   //  2009/05/04
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSampleTimeInMs, pMem.uiSampleTimeInMs, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }
                    if (0 != pMem.ui_rtTimeLen) {       //  2009/05/27
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_ui_rtTimeLen, pMem.ui_rtTimeLen, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //  2015/09/10
                    if (0 != pMem.uiPts) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiPts, pMem.uiPts, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    //
                    //  if  (  pMem.uiLen  )  {
                    //	  if  (  data2Stream(  CONST_qyDataType_lData,  CONST_qnmCfgId_rawData,  (  void  *  )pMem.buf,  pMem.uiLen,  &ptr,  &len  )  )  goto  errLabel;
                    //  }
                    if (0 != pMem.uiLen) {
                        if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiLen, pMem.uiLen, 0, ptr)))
                            return iErr;
                        total_len += len;
                    }

                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

            }

            if (0 != pReq.uiLen) {
                if (0 > (len = data2Stream(CONST_qyDataType_lData, CONST_qnmCfgId_rawData, pReq.buf.array(), pReq.uiLen, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;
            if (0 == iErr) {

            }
            return total_len;

        }

        ;


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferAudioData = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            TRANSFER_AUDIO_DATA pContent = (TRANSFER_AUDIO_DATA) p1;
            if (null == pContent) return iErr;

            int tmp_cfgId = pItem.cfgId;

            switch (uiStreamId) {
                case CONST_imCommType_transferAudioData:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_uiTranNo_openAudioDev:
                            if (-1 == (pContent.uiTranNo_openAvDev = getFieldData_long(pItem)))
                            {
                                Log.e("pContent.uiTranNo_openAvDev < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_tLastTime_showFrameInfo_020534:
                            if (0 > (pContent.tLastTime_showFrameInfo_020534 = getFieldData_l64(pItem)))
                            {
                                Log.e("pContent.tLastTime_showFrameInfo_020534 < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_usCnt_pkts:
                            if (0 > (pContent.usCnt_pkts = getFieldData_short(pItem)))
                            {
                                Log.e("pContent.usCnt_pkts < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_usCnt:
                            if (0 > (pContent.usCnt = getFieldData_short(pItem)))
                            {
                                Log.e("pContent.usCnt < 0");
                                return iErr;
                            }
                            if (pContent.usCnt > CONST_maxTransferAvDataMems) {
                                Log.e("pContent.usCnt  > CONST_maxTransferAvDataMems");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_rawData:
                            if (null  == (pContent.buf = getFieldData_lData(pItem))) return iErr;
                            pContent.uiLen = pContent.buf.capacity();
                            break;
                        default:
                            break;
                    }
                    break;
                case CONST_imCommType_mem: {
                    TRANSFER_AUDIO_dataMemHead pMem = null;
                    //
                    if (pContent.tmpInternal.usCnt_mems >= CONST_maxTransferAvDataMems) {
                        Log.e("tmpHandler_stream2TransferAudioData failed: usCnt_mems is too big");
                        return iErr;
                    }
                    pMem = getMem.qyFuncs.getMem(pContent.mems, bufSize_TRANSFER_AUDIO_dataMemHead_mems, pContent.tmpInternal.usCnt_mems);
                    if (pMem == null) {
                        Log.e("pMem == null");
                        return iErr;
                    }
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_messengerId: {
                            if (0 > (pMem.idInfo.ui64Id = getFieldData_l64(pItem)))
                            {
                                Log.e("pContent.uiTranNo_openAvDev < 0");
                                return iErr;
                            }
                        }
                        break;
                        case CONST_qnmCfgId_uiTranNo_openAudioDev:      //  2013/08/04
                            if (-1 == (pMem.uiTranNo_openAvDev = getFieldData_long(pItem)))
                            {
                                Log.e("pMem.uiTranNo_openAvDev < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_uiSampleTimeInMs:
                            if (-1 == (pMem.uiSampleTimeInMs = getFieldData_long(pItem)))
                            {
                                Log.e("pMem.uiSampleTimeInMs < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_ui_rtTimeLen:
                            if (-1 == (pMem.ui_rtTimeLen = getFieldData_long(pItem)))
                            {
                                Log.e("pMem.ui_rtTimeLen < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_uiPts:
                            if (-1 == (pMem.uiPts = getFieldData_long(pItem)))
                            {
                                Log.e("pMem.uiPts < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_uiLen:
                            if (-1 == (pMem.uiLen = getFieldData_long(pItem)))
                            {
                                Log.e("pMem.uiLen < 0");
                                return iErr;
                            }
                            break;
                        case CONST_qnmCfgId_null:
                            pContent.tmpInternal.usCnt_mems++;
                            break;
                        default:
                            break;
                    }
                }
                break;
                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };


        public static int transferAudioDataResp2Stream(int uiStreamId, TRANSFER_AUDIO_dataResp pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;

            if (0 != pReq.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openAudioDev, pReq.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
            }

            if (0 != pReq.usCnt_pkts) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt_pkts, pReq.usCnt_pkts, 0, ptr)))
                    return iErr;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;

            iErr = 0;
            if (0 == iErr) {

            }
            return iErr;

        }


        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferAudioDataResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            TRANSFER_AUDIO_dataResp pContent = (TRANSFER_AUDIO_dataResp) p1;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_transferAudioDataResp) return iErr;

            int tmp_cfgId = pItem.cfgId;

            switch (tmp_cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_uiTranNo_openAudioDev:
                    if (-1 == getFieldData_long(pItem)) return iErr;
                    break;
                case CONST_qnmCfgId_usCnt_pkts:
                    if (0 > getFieldData_short(pItem)) return iErr;
                    break;
                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };


        public static int qyhtonImContentU(int lhClientVer, IM_CONTENTU p) {
            int len = -1;

            switch (p.uiType) {
                case CONST_imCommType_simpleResp:

                    return qyhtonImSimpleResp(lhClientVer, (IM_SIMPLE_RESP) p);

                case CONST_imCommType_lStream:
                    return qyhtonImLStream(lhClientVer, (IM_L_STREAM) p);

                case CONST_imCommType_htmlContent:
                    Log.i("qyhtonImMsgContentU failed, htmlContent");
                    break;
                default: {
                    String str;
                    str = String.format("qyhtonImMsgContentU failed, unknown uiType %s", p.uiType);

                }
                break;
            }

            //
            return len;
        }

        public static int qyntohImContentU(int lhClientVer, byte[] src, IM_CONTENTU_nh p) {
            int len = -1;
            int uiType = 0;

            //  uiType  =  qyntohl(  p.uiType  );
            //mymemcpy((byte[])uiType,src, sizeof(int));
            uiType = qyntohi(uiType);

            switch (uiType) {
                case CONST_imCommType_simpleResp:
                    //mymemcpy((byte[])p.simpleResp,src, (int)sizeof(IM_SIMPLE_RESP));
                    return qyntohImSimpleResp(lhClientVer, p.simpleResp);

                case CONST_imCommType_htmlContent:
                    Log.i("qyntohImMsgContentU failed, htmlContent");
                    break;
                case CONST_imCommType_lStream:
                    //  return  qyntohImLStream(  lhClientVer,  &p.lStream  );					//  2010/07/10. 这里应该设计一个专门的IM_CONTENTU_ntoh
                    return qyntohImLStream(lhClientVer, ByteBuffer.wrap(src), p.lStream);                  //
                default: {
                    String str;
                    str = String.format("qyntohImMsgContentU failed, unprocessed type " + uiType);
                    Log.d("qyntohImContentU: " + str);
                }
                break;
            }

            //
            return len;
        }

        public static boolean isAvFlg_unresizable(byte ucAvFlg) {
            return (0 != ((ucAvFlg) & Consts.CONST_avFlg_unresizable));
        }

        public static void MACRO_makeImSimpleResp(int usCmd_org1, int uiContentType1, int iStatus1, IM_SIMPLE_RESP respSimple) {
            IM_SIMPLE_RESP pResp = respSimple;
            respSimple.uiType = CONST_imCommType_simpleResp;
            respSimple.usCmd_org = usCmd_org1;
            respSimple.uiContentType_org = (short) uiContentType1;
            respSimple.iStatus = iStatus1;
        }

        //
        public static void M_set_pktId_alloc(myDRAW_VIDEO_DATA pPkt) {
            (pPkt).lPktId_alloc = getuiNextTranNo.call();
        }

        public static void M_set_pktId_getNewPkt(myPlayAudioData.myPLAY_AUDIO_DATA pPkt) {
            (pPkt).lPktId_getNewPkt = getuiNextTranNo.call();
        }

        public static void M_set_pktId_getNewPkt(PKT_info_toDec pPkt) {
            (pPkt).lPktId_getNewPkt = getuiNextTranNo.call();
        }


        public static boolean bQThreadExists(QY_qThreadProcInfo_common qThread) {
            if (qThread == null) return false;

            return (qThread).hThread != null;

        }

        public static void waitForQThread(QY_qThreadProcInfo_common pQThread, int dwMilliSeconds) {
            guiShare.GuiShare.pf_waitForThread1.call(pQThread.hThread, dwMilliSeconds);
            pQThread.hThread = null;
        }

        public static qmcTransform.QY_TRANSFORM MACRO_getTransform(taskAv.PROC_TASK_AV task, int index_activeMems_from) {
            return ((index_activeMems_from < 0 || index_activeMems_from >= (task).trans.usCntLimit_transforms) ? null : (task).trans.pTransforms[index_activeMems_from]);
        }


        public static QIS_DISPATCH MACRO_getDispatch(taskAv.PROC_TASK_AV task, int index_activeMems_from) {
            return ((index_activeMems_from < 0 || index_activeMems_from >= (task).usCntLimit_dispatchs) ? null : (task).pDispatchs[index_activeMems_from]);
        }

        public static void M_setStep(qnmCommProc_is_open.MY_DEBUG_STEP pDebugStep, int nStep) {
            {
//                (pDebugStep).nStep_debug = (nStep);
//                (pDebugStep).dwTickCnt_step_debug = tmpFunc.qyFuncs.myGetTickCount(null);
            }
        }
        //#define M_setStep_fg(  pDebugStep,  nStep  )			{  if  (  pDebugStep  )  {  (  pDebugStep  ).fg.FillBuffer.nStep_debug  =  (  nStep  );  (  pDebugStep  ).fg.FillBuffer.dwTickCnt_step_debug  =  GetTickCount(  );  }  }


        public static void M_setMyStep(qnmCommProc_is_open.MY_DEBUG_STEP pDebugStep, int nMyStep) {   //MACRO_qyAssert(  nMyStep  <  800,  (  "nMyStep err. must < 800"  )  );
            M_setStep(pDebugStep, 100 + nMyStep);
        }

    }


    //
    public static class CTRANSFER_FILEDATA_REQ {
        public TRANSFER_FILEDATA_REQ transferFileDataReq;

    }

    public static class CTRANSFER_FILEDATA_RESP {
        public TRANSFER_FILEDATA_RESP transferFileDataResp;

    }

    ;

    public static class CTRANSFER_FILEEND_RESP {
        public TRANSFER_FILEEND_RESP transferFileEndResp;

    }

    ;

}







