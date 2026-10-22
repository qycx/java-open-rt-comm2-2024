package com.qycx.base;

import static com.qycx.base.DlgNotification.qyFuncs_Droid.*;
import static com.qycx.base.commonSock_android.qyFuncs_Droid.*;
import static com.qycx.base.dlgPolicyIsClient.qyFuncs_Droid.*;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.*;
import static com.qycx.base.dumpAudio.qyFuncs_Droid.*;
import static com.qycx.base.getSystem.qyFuncs_Droid.*;
import static com.qycx.base.policyIsClientFunc.qyFuncs_Droid.getPolicyAvLevel;
import static com.qycx.base.tmpFunc_Droid.qyFuncs_Droid.*;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.Fragment_dlgTalk_msgList;
import com.qycx.MainActivity;

import java.lang.ref.WeakReference;

public class GuiShareProc
{
    public static class qyFuncs_Droid
    {
        public static  int GuiShare_init(Object hMainWnd)
        {
            MainActivity mainWnd = (MainActivity)hMainWnd;

            guiShare.GuiShare.pf_myGetTickCount =myGetTickCount_android;
            guiShare.GuiShare.pf_myGetPid =myGetPid;
            guiShare.GuiShare.pf_myGetTid =myGetTid;
            
            guiShare.GuiShare.pf_startThread1 =startThread1;
            guiShare.GuiShare.pf_waitForThread1 =waitForThread1;
            
            guiShare.GuiShare.pf_getSystmInfo =getSystemInfo;
            guiShare.GuiShare.pf_customSystem =customSystem;

            guiShare.GuiShare.pf_bQySockOpen =bQySockOpen_android;
            guiShare.GuiShare.pf_qyConnect =qyConnect_android;
            guiShare.GuiShare.pf_qyDisconnect =qyDisconnect_android;
            guiShare.GuiShare.pf_qySend =qySend_android;
            guiShare.GuiShare.pf_qyRecv =qyRecv_android;
            guiShare.GuiShare.pf_qyWaitForRead =qyWaitForRead_android;
            guiShare.GuiShare.pf_qyWaitForWrite =qyWaitForWrite_android;
            
            guiShare.GuiShare.pf_PostMessage = mainWnd.gui_PostMessage;
            guiShare.GuiShare.pf_IsWindow = mainWnd.gui_IsWindow;
            //
            guiShare.GuiShare.pf_PostMessage_new = new WeakReference<>(mainWnd.gui_PostMessage_new);
            //
            guiShare.GuiShare.pf_mainWnd_getVar = mainWnd.mainWnd_getVar;
            //
            guiShare.GuiShare.pf_gui_procMsg = mainWnd.gui_procMsg;
            //
            guiShare.GuiShare.pf_gui_dlgTalk_procMsg = mainWnd.gui_dlgTalk_procMsg;
            //
            guiShare.GuiShare.pf_gui_notify_cancel = mainWnd.gui_notify_cancel;
            //
            guiShare.GuiShare.pf_gui_notify_chk = mainWnd.gui_notify_chk;
            //
            guiShare.GuiShare.pf_gui_notify_doCmd = mainWnd.gui_notify_doCmd;

            guiShare.GuiShare.pf_mainWnd_setTimer = mainWnd.mainWnd_setTimer;
            guiShare.GuiShare.pf_mainWnd_killTimer = mainWnd.mainWnd_killTimer;

            guiShare.GuiShare.pf_findTalker =findTalker;
            guiShare.GuiShare.pf_dlgTalk_qPostMsg =dlgTalk_qPostMsg;

            guiShare.GuiShare.pf_gui_displayNotification = gui_displayNotification;

            //
            guiShare.GuiShare.pf_testInMain = test.testCls_Droid.testInMain;
            //
            guiShare.GuiShare.pf_dumpData =dumpData;
            guiShare.GuiShare.pf_dumpData1 =dumpData1;
            guiShare.GuiShare.pf_dumpRgb32 =dumpRgb32;
            guiShare.GuiShare.pf_dumpRgb32_1 =dumpRgb32_1;
            guiShare.GuiShare.pf_dumpBmp =dumpBmp;
            //
            guiShare.GuiShare.pf_dumpAudio_init =dumpAudio_init;
            guiShare.GuiShare.pf_dumpAudio_exit =dumpAudio_exit;
            guiShare.GuiShare.pf_dumpAudio =dumpAudio;

            guiShare.GuiShare.pf_logd =logd;

            guiShare.GuiShare.pf_initPolicyAvParams = initPolicyAvParams;
            guiShare.GuiShare.pf_getPolicyIsClient = getPolicyIsClient.qyFuncs_Droid.getPolicyIsClient;
            guiShare.GuiShare.pf_getPolicyAvLevel = getPolicyAvLevel;

            guiShare.GuiShare.pf_initVideoCapDev = Activity_dlgTalk_av.initVideoCapDev;
            guiShare.GuiShare.pf_exitVideoCapDev = Activity_dlgTalk_av.exitVideoCapDev;
            guiShare.GuiShare.pf_bGetCapturePara = Activity_dlgTalk_av.bGetCapturePara;
            guiShare.GuiShare.pf_startCapture = Activity_dlgTalk_av.startCapture;
            guiShare.GuiShare.pf_stopCapture = Activity_dlgTalk_av.stopCapture;

            //
            guiShare.GuiShare.pf_i420ToRgb = tmpJniFunc.qyFuncs_Droid.i420ToRgb;

            //
            guiShare.GuiShare.pf_talk_updatePlayerInfo = Activity_dlgTalk.talk_updatePlayerInfo;
            guiShare.GuiShare.pf_talk_addNewMsg = Fragment_dlgTalk_msgList.talk_addNewMsg;
            guiShare.GuiShare.pf_talk_displayTaskStatus = Fragment_dlgTalk_msgList.talk_displayTaskStatus;

            //
            guiShare.GuiShare.pf_loadCusModule_android =loadCusModule_android.qyFuncs_Droid.loadCusModule_android;

            //
            guiShare.GuiShare.pf_doDecodeVideo_mediaCodec = qdcFunc_mediaCodec.qyFuncs_Droid.doDecodeVideo_mediaCodec;
            guiShare.GuiShare.pf_doEncodeVideo_mediaCodec = qdcFunc_mediaCodec.qyFuncs_Droid.doEncodeVideo_mediaCodec;
            guiShare.GuiShare.pf_doDecodeAudio_MediaCodec = qdcFunc_mediaCodec.qyFuncs_Droid.doDecodeAudio_mediaCodec;

            //
            guiShare.GuiShare.pf_stopLocalAudioRecorder = audioRecorder_dsound.qyFuncs_Droid.stopLocalAudioRecorder;
            //
            guiShare.GuiShare.pf_startPlayAudio_android = playAudio_android.qyFuncs_Droid.startPlayAudio_android;
            guiShare.GuiShare.pf_stopPlayAudio_android = playAudio_android.qyFuncs_Droid.stopPlayAudio_android;
            //
            guiShare.GuiShare.pf_startPlayVideo_android = playVideo_android.qyFuncs_Droid.startPlayVideo_android;
            guiShare.GuiShare.pf_stopPlayVideo_android = playVideo_android.qyFuncs_Droid.stopPlayVideo_android;

            //

            //
            guiShare.GuiShare.pf_readDbgCfg = mySaveSecret.qyFuncs_Droid.readDbgCfg;

            //
            guiShare.GuiShare.pf_newIndex_DLG_TALK_videoConference = taskDataFunc_android.qyFuncs_Droid.newIndex_DLG_TALK_videoConference;
            guiShare.GuiShare.pf_freeIndex_DLG_TALK_videoConference = taskDataFunc_android.qyFuncs_Droid.freeIndex_DLG_TALK_videoConference;
            guiShare.GuiShare.pf_getVideoConference =taskDataFunc_android.qyFuncs_Droid.getVideoConference;

            //
            guiShare.GuiShare.pf_new_QMC_taskData = taskDataFunc_android.qyFuncs_Droid.new_QMC_taskData;
            guiShare.GuiShare.pf_free_QMC_taskData = taskDataFunc_android.qyFuncs_Droid.free_QMC_taskData;
            guiShare.GuiShare.pf_getTaskData = taskDataFunc_android.qyFuncs_Droid.getTaskData;

            //
            guiShare.GuiShare.pf_tfopen = file_android.qyFuncs_Droid._tfopen_android;
            guiShare.GuiShare.pf_fclose = file_android.qyFuncs_Droid.fclose_android;
            guiShare.GuiShare.pf_fseeki64 = file_android.qyFuncs_Droid.fseeki64_android;
            guiShare.GuiShare.pf_fread = file_android.qyFuncs_Droid.fread_android;
            guiShare.GuiShare.pf_fwrite = file_android.qyFuncs_Droid.fwrite_android;
            //
            guiShare.GuiShare.pf_CopyFile = file_android.qyFuncs_Droid.CopyFile_android;
            guiShare.GuiShare.pf_DeleteFile = file_android.qyFuncs_Droid.DeleteFile_android;
            //
            guiShare.GuiShare.pf_newIndex_qisTrans= prepare.qyFuncs_Droid.newIndex_qisTrans;
            guiShare.GuiShare.pf_freeIndex_qisTrans= prepare.qyFuncs_Droid.freeIndex_qisTrans;
            guiShare.GuiShare.pf_getQisTransByIndex = prepare.qyFuncs_Droid.getQisTransByIndex; ;



            //
            return 0;
        }

        //以下的全局变量，会持有窗体的引用，导致窗体无法回收。此函数做清理工作。
        public static void GuiShare_exit(){
            guiShare.GuiShare.pf_PostMessage = null;
            guiShare.GuiShare.pf_IsWindow = null;
            //
            guiShare.GuiShare.pf_PostMessage_new = null;
            //
            guiShare.GuiShare.pf_mainWnd_getVar = null;
            //
            guiShare.GuiShare.pf_gui_procMsg = null;
            //
            guiShare.GuiShare.pf_gui_dlgTalk_procMsg = null;
            //
            guiShare.GuiShare.pf_gui_notify_cancel = null;
            //
            guiShare.GuiShare.pf_gui_notify_chk = null;
            //
            guiShare.GuiShare.pf_gui_notify_doCmd = null;

            guiShare.GuiShare.pf_mainWnd_setTimer = null;
            guiShare.GuiShare.pf_mainWnd_killTimer = null;

        }

    }
}
