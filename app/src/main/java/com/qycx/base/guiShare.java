
package com.qycx.base;

import com.qycx.Activity_dlgTalk;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class guiShare {

    //
    public static class Consts {
        //
        //public final static String CONST_subDir_isCli = "isCli";
        public final static String CONST_cfgFile_login_isCli = "login_isCli.cfg";
        public final static String CONST_cfgFile_dbg_isCli = "dbg_isCli.cfg";


        //
        public final static String CONST_cfgName_server = "server";
        public final static String CONST_cfgName_usr = "usr";
        public final static String CONST_cfgName_passwd = "passwd";

        //
        public final static String CONST_cfgName_statusServer = "statusServer";


        //
        public final static int CONST_msg_cameraOpen = 10000;


        //
        public final static int CONST_iFgType_dlgTalk_av = 1;
        public final static int CONST_iFgType_dlgTalk_av2 = 2;

        public final static float CONST_fz_me = 1f - 0.000002f;
        //
        public final static float CONST_fz_z1 = CONST_fz_me;//1f - 0.0000001f;
        public final static float CONST_fz_peer = 1f - 0.000001f;


    }

    public interface PF_myGetTickCount {
        int call();
    }

    public interface PF_myShowChangePassWordSuccess {
        void call();
    }

    public interface PF_myGetPid {
        int call();
    }

    public interface PF_myGetTid {
        int call();
    }

    public interface PF_startThread1 {
        //int call(Object obj_ph, qmcFunc.PF_threadProc pf, Object param);
        Thread call( qmcFunc.PF_threadProc pf, Object param);
    }

    public interface PF_waitForThread1 {
        void call(Object obj_ph, long dwMilliseconds);
    }

    public interface PF_getSystemInfo {
        void call();        //  int iManufactor, int iModel, boolean b_isHarmony
    }

    public interface PF_customSystem {
        int call();
    }

    public interface PF_bQySockOpen {
        boolean call(commonSock.QY_SOCK pSock);
    }

    public interface PF_qyConnect {
        int call(String ipaddr, int port, commonSock.SOCK_PARAM pSockParam, commonSock.QY_SOCK pSock);
    }

    public interface PF_qyDisconnect {
        void call(commonSock.QY_SOCK pSock);
    }

    public interface PF_qySend {
        int call(commonSock.QY_SOCK pSock,byte[] buffer, int offset,  int len, int timeoutVal);
    }

    public interface PF_qyRecv {
        int call(commonSock.QY_SOCK pSock, ByteBuffer buffer, int offset, int len,  int timeoutVal);
    }

    public interface PF_qyWaitForRead {
        int call(commonSock.QY_SOCK pSock, int timeoutInMs);
    }

    public interface PF_qyWaitForWrite {
        int call(commonSock.QY_SOCK pSock, int timeoutInMs);
    }

    public interface PF_PostMessage {
        int call(Object wnd, int msgId, int wParm, int lParam);
    }

    public interface PF_IsWindow {
        boolean call(Object hWnd);
    }

    public interface PF_PostMessage_new {
        int call(Object wnd, int iWndContenType, int msgId, int wParm, int lParam);
    }

    public interface PF_mainWnd_getVar {
        Object call();
    }

    public interface PF_gui_procMsg {
        int call(Object pMsgParam);
    }

    public interface PF_gui_dlgTalk_procMsg {
        int call(Object hTalkParam, Object pMsgParam);
    }

    public interface PF_gui_notify_cancel {
        int call();
    }

    public interface PF_gui_notify_chk {
        int call();
    }

    public interface PF_gui_notify_doCmd {
        int call(int usOp);
    }

    public interface PF_mainWnd_setTimer {
        int call();
    }

    public interface PF_mainWnd_killTimer {
        void call();
    }

    public interface PF_findTalker {
        Activity_dlgTalk call(ctxQyMc.CCtxQyMc pQyMc, qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo);
    }

    public interface PF_dlgTalk_qPostMsg {
        int call(Object hDlgTalk, Object pQElem, int size);
    }

    public interface PF_gui_displayNotification {
        int call(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_taskStatus status);
    }

    public interface PF_testInMain {
        int call(ctxQyMc.CCtxQyMc pQyMc);
    }

    public interface PF_dumpData {
        int call(byte[] data, int w, int h, boolean bAppend, boolean bFileName_haveTime, String pureFileName);
    }

    public interface PF_dumpData1 {
        int call(byte[] data, int len, int w, int h, boolean bAppend, boolean bFileName_haveTime, String pureFileName);
    }

    public interface PF_dumpRgb32 {
        int call(byte[] data, int w, int h, String pureFileName);
    }

    public interface PF_dumpRgb32_1 {
        int call(byte[] data, int w, int h, String pureFileName);
    }

    public interface PF_dumpBmp {
        int call(Object bitmap, String pureFileName);
    }

    public interface PF_dumpAudio_init {
        int call();
    }

    public interface PF_dumpAudio_exit {
        int call();
    }

    public interface PF_dumpAudio {
        int call(byte[] data, int size);
    }

    public interface PF_logd {
        int call(String tag, String str);
    }

    public interface PF_initPolicyAvParams {
        int call();
    }

    public interface PF_getPolicyIsClient {
        int call(ctxQmc.CCtxQmc pProcInfo, ctxQmc.POLICY_isClient p);
    }

    public interface PF_getPolicyAvLevel {
        int call(ctxQyMc.CCtxQyMc pQyMc, int uiCapType, int uiSubCapType, int iCapUsage);
    }

    public interface PF_initVideoCapDev {
        int call(Object hTalk, int iWndContentType, String displayName_selected, qyCommProc_open.BITMAPINFOHEADER pBih_suggested, qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp);
    }

    public interface PF_exitVideoCapDev {
        int call(Object hTalk, int iWndContentType);
    }

    public interface PF_bGetCapturePara {
        boolean call(Object hTalk, int iWndContentType, qyCommProc_open.QY_VIDEO_HEADER pVh_org);
    }

    public interface PF_startCapture {
        int call(Object hTalk, int iWndContentType);
    }

    public interface PF_stopCapture {
        int call(Object hTalk, int iWndContentType);
    }

    public interface PF_i420ToRgb {
        int call(byte[] yuv, Object yri, int rgbBits, boolean bBgr, int width, int height, boolean bFlipImg);
    }

    public interface PF_talk_updatePlayerInfo {
        int call(Object hTalk, qnmCommProc_mis.MIS_MSGU pMsg, qnmCommProc_mis.PLAYER_ID playerId);
    }

    public interface PF_talk_addNewMsg {
        int call(Object hTalkParam, qyDefs.QY_MESSENGER_ID pIdInfo_from, long tSendTime, int uiTranNo, int usCode, int uiContentType, String msg, int iTaskId);
    }

    public interface PF_talk_displayTaskStatus {
        int call(Object hDlgTalk, qnmCommProc_mis.MIS_MSG_taskStatus pMsgTaskStatus);
    }

    public interface PF_loadCusModule_android {
        int call();
    }

    public interface PF_doDecodeVideo_mediaCodec {
        int call(qmcTransform.QY_TRANSFORM pTransform, int index_activeMems_from, qnmCommProc_mis.MIS_MSGU pMsgBuf);
    }

    public interface PF_doEncodeVideo_mediaCodec {
        int call(qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, byte[] pMsgBuf);
    }

    public interface PF_doDecodeAudio_mediaCodec {
        int call(qmcTransform.QY_TRANSFORM pTransform, int index_pMems_from1, int index_activeMems_from, qnmCommProc_mis.MIS_MSGU pMsgBuf);
    }

    public interface PF_stopLocalAudioRecorder {
        int call(ctxQmc.CCtxQmc pProcInfo, int index_sharedObj, int nTries);
    }

    public interface PF_startPlayAudio_android {
        int call(qmcCommFunc_is_open.QY_PLAYER pPlayerParam);
    }

    public interface PF_stopPlayAudio_android {
        int call(qmcCommFunc_is_open.QY_PLAYER pPlayerParam);
    }

    public interface PF_startPlayVideo_android {
        int call(qmcCommFunc_is_open.QY_PLAYER pPlayerParam);
    }

    public interface PF_stopPlayVideo_android {
        int call(qmcCommFunc_is_open.QY_PLAYER pPlayerParam);
    }

    public interface PF_readDbgCfg {
        String call(String m_statusServer);
    }

    public interface PF_newIndex_DLG_TALK_videoConference {
        int call(int index_taskInfo);
    }

    public interface PF_freeIndex_DLG_TALK_videoConference {
        void call(int ind_videoConference);
    }

    public interface PF_getVideoConference {
        qmcTaskInfo.DLG_TALK_videoConference call(int ind_videoConference);
    }

    public interface PF_new_QMC_taskData {
        int call(int index_taskInfo, int taskDataType);
    }

    public interface PF_free_QMC_taskData {
        void call(int ind_taskData);
    }

    public interface PF_getTaskData {
        qmcTaskInfo.QMC_taskData_common call(int ind_taskData);
    }

    public interface PF_tfopen {
        Object call(String filename, String mode);
    }

    public interface PF_fclose {
        int call(Object stream);
    }

    public interface PF_fseeki64 {
        int call(Object stream, long offset, int origin);
    }

    public interface PF_fread {
        long call(byte[] ptr, int size, int nmemb, Object stream);
    }

    public interface PF_fwrite {
        long call(byte[] buffer, long size, long count, Object stream);
    }

    public interface PF_CopyFile {
        boolean call(String lpExistingFileName, String lpNewFileName, boolean bFailIfExists);
    }

    public interface PF_DeleteFile {
        boolean call(String lpFileName);
    }

    public interface PF_newIndex_qisTrans {
        int call();
    }

    public interface PF_freeIndex_qisTrans {
        int call(int index);
    }

    public interface PF_getQisTransByIndex {
        qmcTransform.QisTrans call(int index);
    }

    public interface PF_dec_parseEncAPkt_i {
        int call(int uiTransformType, Object pParent_transform, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec_param, int p_old_lPktId_getNewPkt, int puiSize, long puiSampleTimeInMs, int puiPts, Object p0);
    }


    //
    public static class GuiShare {
        public static PF_myGetTickCount pf_myGetTickCount;
        public static PF_myGetPid pf_myGetPid;
        public static PF_myGetTid pf_myGetTid;
        public static PF_startThread1 pf_startThread1;
        public static PF_waitForThread1 pf_waitForThread1;

        //
        public static PF_getSystemInfo pf_getSystmInfo;
        public static PF_customSystem pf_customSystem;

        //
        public static PF_bQySockOpen pf_bQySockOpen;
        public static PF_qyConnect pf_qyConnect;
        public static PF_qyDisconnect pf_qyDisconnect;
        public static PF_qySend pf_qySend;
        public static PF_qyRecv pf_qyRecv;
        public static PF_qyWaitForRead pf_qyWaitForRead;
        public static PF_qyWaitForWrite pf_qyWaitForWrite;

        //        
        public static PF_PostMessage pf_PostMessage;
        public static PF_IsWindow pf_IsWindow;
        //            
        public static WeakReference<PF_PostMessage_new> pf_PostMessage_new;
        //
        public static PF_mainWnd_getVar pf_mainWnd_getVar;
        //
        public static PF_gui_procMsg pf_gui_procMsg;
        public static PF_gui_dlgTalk_procMsg pf_gui_dlgTalk_procMsg;
        //
        public static PF_gui_notify_cancel pf_gui_notify_cancel;
        public static PF_gui_notify_chk pf_gui_notify_chk;
        //
        public static PF_gui_notify_doCmd pf_gui_notify_doCmd;
        //
        public static PF_mainWnd_setTimer pf_mainWnd_setTimer;
        public static PF_mainWnd_killTimer pf_mainWnd_killTimer;

        //
        public static PF_findTalker pf_findTalker;
        public static PF_dlgTalk_qPostMsg pf_dlgTalk_qPostMsg;

        //
        public static PF_gui_displayNotification pf_gui_displayNotification;

        //
        public static PF_testInMain pf_testInMain;

        //
        public static PF_dumpData pf_dumpData;
        public static PF_dumpData1 pf_dumpData1;
        public static PF_dumpRgb32 pf_dumpRgb32;
        public static PF_dumpRgb32_1 pf_dumpRgb32_1;
        public static PF_dumpBmp pf_dumpBmp;

        //
        public static PF_dumpAudio_init pf_dumpAudio_init;
        public static PF_dumpAudio_exit pf_dumpAudio_exit;
        public static PF_dumpAudio pf_dumpAudio;

        //
        public static PF_logd pf_logd;

        //
        public static PF_initPolicyAvParams pf_initPolicyAvParams;
        public static PF_getPolicyIsClient pf_getPolicyIsClient;
        public static PF_getPolicyAvLevel pf_getPolicyAvLevel;

        //
        public static PF_initVideoCapDev pf_initVideoCapDev;
        public static PF_exitVideoCapDev pf_exitVideoCapDev;
        public static PF_bGetCapturePara pf_bGetCapturePara;
        public static PF_startCapture pf_startCapture;
        public static PF_stopCapture pf_stopCapture;


        //
        public static PF_i420ToRgb pf_i420ToRgb;

        //
        public static PF_talk_updatePlayerInfo pf_talk_updatePlayerInfo;
        public static PF_talk_addNewMsg pf_talk_addNewMsg;
        public static PF_talk_displayTaskStatus pf_talk_displayTaskStatus;


        //
        public static PF_loadCusModule_android pf_loadCusModule_android;
        public static PF_doDecodeVideo_mediaCodec pf_doDecodeVideo_mediaCodec;
        public static PF_doEncodeVideo_mediaCodec pf_doEncodeVideo_mediaCodec;
        public static PF_doDecodeAudio_mediaCodec pf_doDecodeAudio_MediaCodec;

        //
        public static PF_stopLocalAudioRecorder pf_stopLocalAudioRecorder;
        public static PF_startPlayAudio_android pf_startPlayAudio_android;
        public static PF_stopPlayAudio_android pf_stopPlayAudio_android;

        public static PF_startPlayVideo_android pf_startPlayVideo_android;
        public static PF_stopPlayVideo_android pf_stopPlayVideo_android;

        //
        public static PF_readDbgCfg pf_readDbgCfg;

        //
        public static PF_newIndex_DLG_TALK_videoConference pf_newIndex_DLG_TALK_videoConference;
        public static PF_freeIndex_DLG_TALK_videoConference pf_freeIndex_DLG_TALK_videoConference;
        public static PF_getVideoConference pf_getVideoConference;

        //
        public static PF_new_QMC_taskData pf_new_QMC_taskData;
        public static PF_free_QMC_taskData pf_free_QMC_taskData;
        public static PF_getTaskData pf_getTaskData;

        //
        public static PF_tfopen pf_tfopen;
        public static PF_fclose pf_fclose;
        public static PF_fseeki64 pf_fseeki64;
        public static PF_fread pf_fread;
        public static PF_fwrite pf_fwrite;
        //
        public static PF_CopyFile pf_CopyFile;
        public static PF_DeleteFile pf_DeleteFile;

        //
        public static PF_newIndex_qisTrans pf_newIndex_qisTrans;
        public static PF_freeIndex_qisTrans pf_freeIndex_qisTrans;
        public static PF_getQisTransByIndex pf_getQisTransByIndex;

        public static PF_dec_parseEncAPkt_i pf_dec_parseEncAPkt_i;

        //
    };


}
