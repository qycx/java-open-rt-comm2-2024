package com.qycx;
//    [Activity(Label = "Activity_dlgTalk", ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.Keyboard)]

import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_OnDestroy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.opengl.EGLContext;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.Manifest.permission;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.qycx.base.TableItem;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.getPlayerByIndex;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.mainWnd_procMsg;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcCommFunc_is_open;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMemoryFunc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showTaskStatusFunc;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;
import com.qycx.base.util.Log;

import org.aviran.cookiebar2.CookieBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSizeCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class Activity_dlgTalk extends qmcBaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, EasyPermissions.PermissionCallbacks {
    //
    public dlgTalkProc.DLG_TALK_var m_var;

    //
    //private TextView txt_msg;
    //private TextView txt_detail;

    private ViewPager vPager;

    public FragmentPagerAdapter_dlgTalk mAdapter;

    public final static int PAGE_ONE = 0;
    //public final static int PAGE_TWO = 1;

    public String m_str_title;

    //
    public MyHandler mHandler = null;

    public int respVideoStatusIsenlargement = -1;
    private AppSettingsDialog appSettingsDialog;

    String[] mPermissionList = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};
    private static int REQUEST_CODE_CAMERA_RECORDAUDIO_PERMISSION = 11;
    int currentSelectQiuJiChnal;

    public void isPermissionOk() {

        // boolean bRet = false;
        //int ii = 0;
        //
        if ((int) Build.VERSION.SDK_INT >= 23) {
            if (!hasAllPermission()) {
                // Ask for one permission
                EasyPermissions.requestPermissions(this, getResources().getString(R.string.permission_content_camera_mic), REQUEST_CODE_CAMERA_RECORDAUDIO_PERMISSION, mPermissionList);
            }
        }
    }

    public boolean hasAllPermission() {
        return EasyPermissions.hasPermissions(this, mPermissionList);
    }

    @Override
    //授权结果，分发下去
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        int[] gr = new int[grantResults.length];
        for (int i = 0; i < gr.length; i++) {
            gr[i] = (int) grantResults[i];
        }
        //
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, gr, this);
    }

    @Override
    public void onPermissionsGranted(int p0, List<String> p1) {


    }

    //拒绝授权
    public void onPermissionsDenied(int p0, List<String> p1) {
        //Log.i( "onPermissionsDenied:" + requestCode + ":" + perms.size());
        //    ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        //   GuiShare.pf_PostMessage(pQyMc.gui.hMainWnd, Consts.CONST_qyWm_postComm, Consts.CONST_qyWmParam_login_canceled, 0);

        //
        if (EasyPermissions.somePermissionPermanentlyDenied(this, p1)) {
            AppSettingsDialog.Builder buider = new AppSettingsDialog.Builder(this);

            for (String permission : p1) {
                switch (permission) {
                    case android.Manifest.permission.CAMERA:
                        buider.setRationale(getResources().getString(R.string.permission_content_camera));
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        buider.setRationale(getResources().getString(R.string.permission_content_mic));
                        break;
                }
            }
            appSettingsDialog = buider.build();
            appSettingsDialog.show();
        }
    }

    //
    public static dlgTalkProc.DLG_TALK_var tmp_getDlgTalkVar(Object hDlgTalk) {

        if (!guiShare.GuiShare.pf_IsWindow.call(hDlgTalk)) return null;
        if (hDlgTalk instanceof Activity_dlgTalk) {
            return ((Activity_dlgTalk) (hDlgTalk)).get_pm_var();
        }
        if (hDlgTalk instanceof WeakReference<?>) {
            return ((Activity_dlgTalk) ((WeakReference) hDlgTalk).get()).get_pm_var();
        }
        return null;
    }

    public dlgTalkProc.DLG_TALK_var get_pm_var() {
        return m_var;
    }


    public class MyHandler extends Handler {
        Activity_dlgTalk m_hDlgTalk = null;

        public MyHandler(Activity_dlgTalk hDlgTalk) {
            m_hDlgTalk = hDlgTalk;
        }

        @Override
        public void handleMessage(Message msg) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return;
            }

            String str;

            //
            Log.d("Activity_dlgTalk: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);

            switch (msg.what) {
                case qyWmComm.Consts.CONST_qyWm_postComm:

                    //
                    switch (msg.arg1) {
                        case qyWmComm.Consts.CONST_qyWmParam_doTask:
                            m_hDlgTalk.doTask_av_accept();
                            break;
                        case qyWmComm.Consts.CONST_qyWmParam_chkTalkDraw: {
                            m_hDlgTalk.doChkTalkDraw();
                            m_hDlgTalk.m_var.dwTickCnt_waitForOk_dlgTalk_av1 = 0;
                        }
                        break;
                        case qyWmComm.Consts.CONST_qyWmParam_closeWnd: {
                            Log.d("get a msg: closeWnd. to doFinish m_hDlgTalk");
                            m_hDlgTalk.doFinish("talk.closeWnd");
                        }
                        break;
                        case qyWmComm.Consts.CONST_qyWmParam_msgArrive:
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int gui_PostMessage(int msgId, int wParam, int lParam) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        {
            if  (  this.mHandler  ==  null  )  return  -1;

            Message msg = this.mHandler.obtainMessage();
            msg.what = msgId;
            msg.arg1 = wParam;
            msg.arg2 = lParam;
            this.mHandler.sendMessage(msg);
            return 0;
        }
    }

    public guiShare.PF_gui_procMsg gui_procMsg = (Object pMsgParam) -> {
        int iErr = -1;
        qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        if (mAdapter.mFragment_dlgTalk_msgList1 != null) {
            mAdapter.mFragment_dlgTalk_msgList1.gui_procMsg(pMsg);
        }
        return iErr;
    };

    public int startActivity_dlgTalk_av(int iCmd, int index_taskInfo, int level, String hint) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        String str;

        int cnt = 0;
        int i;
        for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
            if (m_var.tmp_hTools_dlgTalk_av[i] != null) {
                cnt++;
            }
        }
        if (cnt != 0) return -1;

        //
        long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
        int iDiffInMs = (int) (dwTickCnt - m_var.dwTickCnt_waitForOk_dlgTalk_av1);
        if (Math.abs(iDiffInMs) < 1500) {
            str = "startActivity_dlgTalk_av failed,iDiffInMs " + iDiffInMs + ", too quickly";
            Log.e("startActivity_dlgTalk_av: " + str);
            return -1;
        }
        m_var.dwTickCnt_waitForOk_dlgTalk_av1 = dwTickCnt;

        if (index_taskInfo >= 0) {
            if (!qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, index_taskInfo)) {
                return -1;
            }
        }

        Log.d("startActivity_dlgTalk_av " + hint);
        mainActivity.canReceiveMessage = false;
        Intent intent = new Intent(this, Activity_dlgTalk_av.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("iCmd", iCmd);
        intent.putExtra("ui64Id", m_var.addr.idInfo.ui64Id);
        intent.putExtra("level", level);
        intent.putExtra("index_qmcTaskInfo", index_taskInfo);
        startActivity(intent);

        iErr = 0;
        return iErr;
    }

    //
    public int startActivity_dlgTalk_detail() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        Intent intent = new Intent(this, Activity_dlgTalk_detail.class);
        intent.putExtra("ui64Id", m_var.addr.idInfo.ui64Id);
        startActivity(intent);

        iErr = 0;
        return iErr;
    }


    //
    public int doTask_av_accept() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        if (!(mainActivity.var_common.notifyTaskStatus.bExists_task && mainActivity.var_common.notifyTaskStatus.bAvCall)) {
            Log.d("Can't start a avTask, no task needs be accepted.");
            return iErr;
        }

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, mainActivity.var_common.notifyTaskStatus.index_taskInfo, "doTask_av_accept");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        //            
        if (pTaskData.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) {
            return iErr;
        }
        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
        if (task.addr_logicalPeer.idInfo.ui64Id != this.m_var.addr.idInfo.ui64Id) {
            Log.d("Can't start a avTask, because logicalPeer != talker.");
            return iErr;
        }

        m_var.av.bNeedDoTaskAv = true;

        if (hasAllPermission()) {
            startActivity_dlgTalk_av(tmpResource.IDC_BUTTON_av_accept, mainActivity.var_common.notifyTaskStatus.index_taskInfo, qyWmComm.Consts.CONST_do_1080P_AV_Call, "doTask_av_accept");

        } else {
            isPermissionOk();
        }

        iErr = 0;

        return iErr;
    }

    public int doChkTalkDraw() {
        int iErr = -1;
        boolean bDbg = false;
        String str;

        int i;
        int cnt = 0;
        for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
            if (m_var.tmp_hTools_dlgTalk_av[i] != null) {
                cnt++;
            }
        }
        //
        if (cnt > 1) {
            int ii = 0;
        }
        //
        if (cnt == 0) {
            m_var.m_hTool_dlgTalk_av = null;
            m_var.m_share_eglContext = null;
            iErr = 0;
            return iErr;
        }
        //
        if (cnt == 1) {
            for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
                if (m_var.tmp_hTools_dlgTalk_av[i] != null && m_var.tmp_share_eglContexts[i] != null) {
                    //
                    Object tmp_hTool_dlgTalk_av = m_var.tmp_hTools_dlgTalk_av[i];
                    EGLContext tmp_share_eglContext = m_var.tmp_share_eglContexts[i];
                    //
                    if (tmp_hTool_dlgTalk_av != m_var.m_hTool_dlgTalk_av || tmp_share_eglContext != m_var.m_share_eglContext) {
                        m_var.m_hTool_dlgTalk_av = tmp_hTool_dlgTalk_av;
                        m_var.m_share_eglContext = tmp_share_eglContext;
                        //
                        Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) m_var.m_hTool_dlgTalk_av;
                        if (hTalk_av.m_fg_dlgTalk_av2 != null) {
                            hTalk_av.m_fg_dlgTalk_av2.waitTextureViewAvailable(null, null, null);
                        }
                    }
                    //
                    break;
                }
            }
            iErr = 0;
            return iErr;
        }

        iErr = 0;
        return iErr;
    }


    //
    public int doTask_startAvCall(int level) {
        int iErr = -1;

        //
        m_var.av.bNeedDoTaskAv = true;
        if (hasAllPermission()) {
            startActivity_dlgTalk_av(tmpResource.IDC_BUTTON_av, -1, level, "doTask_startAvCall");
        } else {
            isPermissionOk();
        }

        iErr = 0;
        return iErr;

    }


    //
    public qyDefs.PF_commonHandler gui_onTimer = (Object p0, Object p1, Object p2) -> {
        String str;

        ctxQyMc.CCtxQyMc  pQyMc  =  qyMcMain_global.g.g_pQyMc;
        //
        if  (  pQyMc  == null   )  {
            Log.e("qy", "dlgTalk.gui_onTimer: pQyMc is null, serious error");
            doFinish("dlgTalk.gui_onTimer: pQyMc is null, serious error");
            return  -1;
        }


        //
        dlgTalkProc.qyFuncs_Droid.dlgTalk_myTimerProc(this, 0, 0, 0);

        if (this.mAdapter.mFragment_dlgTalk_msgList1 != null) {
            mAdapter.mFragment_dlgTalk_msgList1.gui_onTimer(0, 0, 0);
        }
        return 0;
    };

    //
    public static guiShare.PF_talk_updatePlayerInfo talk_updatePlayerInfo = (Object hTalk, qnmCommProc_mis.MIS_MSGU pMIS_MSG_applyForPlayer_qmc, qnmCommProc_mis.PLAYER_ID playerId) -> {
        int iErr = -1;
        Activity_dlgTalk dlgTalk;
        if (hTalk instanceof WeakReference) {
            dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
        } else {
            dlgTalk = (Activity_dlgTalk) hTalk;
        }
        qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc pMsg = (qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc) pMIS_MSG_applyForPlayer_qmc;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;


        dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
        boolean bChanged = false;

        if (!m_var.av.taskInfo.bTaskExists) {
            if (iErr == 0 && bChanged) {
                m_var.m_drawLayout.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            }
            return iErr;
        }
        if (pMsg.iTaskId != m_var.av.taskInfo.iTaskId) {
            if (iErr == 0 && bChanged) {
                m_var.m_drawLayout.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            }
            return iErr;
        }

        //
        int index_taskInfo = m_var.av.taskInfo.iIndex_taskInfo;
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "do_closeTask");
        if (pTaskInfo == null){
            if (iErr == 0 && bChanged) {
                m_var.m_drawLayout.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            }
            return iErr;
        }
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null){
            if (iErr == 0 && bChanged) {
                m_var.m_drawLayout.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            }
            return iErr;
        }
        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
        qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

        //
        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem = null;
        int i;
        if (pMsg.idInfo_recorder.ui64Id != 0) {
            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                pActiveMem = videoConference.activeMems_from[i];
                if (pActiveMem.avStream.idInfo.ui64Id == pMsg.idInfo_recorder.ui64Id && pActiveMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev == pMsg.video.uiTranNo) {
                    break;
                }
            }
            if (i == videoConference.usCntLimit_activeMems_from) return iErr;
            //
            if (m_var.av.taskInfo.ucbVideoConference) {
                if (pActiveMem != null && 0 == pActiveMem.avStream.obj.resObj.uiObjType)
                    return iErr;
            }
        }

        if (pMsg.idInfo_recorder.ui64Id == 0) {
            if (m_var.m_drawLayout.param.me.param.playerId.index_player != playerId.index_player || m_var.m_drawLayout.param.me.param.playerId.uiTranNo_player != playerId.uiTranNo_player) {
                m_var.m_drawLayout.param.me.param.playerId = playerId;
                m_var.m_drawLayout.param.me.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                bChanged = true;
            }
        } else {
            if (m_var.m_drawLayout.param.peer.param.playerId.index_player != playerId.index_player || m_var.m_drawLayout.param.peer.param.playerId.uiTranNo_player != playerId.uiTranNo_player) {
                qmcCommFunc_is_open.QY_PLAYER pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, playerId.index_player);
                //
                m_var.m_drawLayout.param.peer.param.playerId = playerId;
                m_var.m_drawLayout.param.peer.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                bChanged = true;
            }
        }


        iErr = 0;

        if (iErr == 0 && bChanged) {
            m_var.m_drawLayout.tn_modified = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        }
        return iErr;
    };

    //
    public int do_closeTask(int index_taskInfo) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        String str;

        Log.d("dlgTalk.do_closeTask enters");

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "do_closeTask");
        if (pTaskInfo == null) return -1;

        if (m_var.av.taskInfo.bTaskExists && m_var.av.taskInfo.iIndex_taskInfo == index_taskInfo) {
            if (m_var.m_hTool_dlgTalk_av != null) {
                Log.d("trigger to close taskAv");
                guiShare.GuiShare.pf_PostMessage_new.get().call(m_var.m_hTool_dlgTalk_av, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
            }
            iErr = 0;
            return iErr;
        }

        mainWnd_procMsg.qyFuncs_Droid.mainWnd_do_closeTask(index_taskInfo);

        iErr = 0;

        Log.d("dlgTalk.do_closeTask leaves");
        return iErr;
    }


    //
    public int findOldAvTaskActive(qyCommProc_open.findOldAvTaskActiveParams findOldAvTaskActiveParams) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc == null) return -1;
        Activity_dlgTalk mhTalk = this;
        //
        if (mhTalk == null) return -1;
        if (mhTalk.m_var == null) return -1;
        //
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        int i;
        String str;

        for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, i, "");
            if (pTaskInfo == null) return iErr;
            if (!pTaskInfo.bUsed) continue;
            qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
            if (taskData.uiType != qmcTaskInfo.Consts.CONST_taskDataType_conf) continue;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) taskData;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTc.msgU;
            {
                if (pMsgTask.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) continue;
                //
                if (pMsgTask.addr_logicalPeer.idInfo.ui64Id == mhTalk.m_var.addr.idInfo.ui64Id && pMsgTask.data.route.idInfo_from.ui64Id != pMisCnt.idInfo.ui64Id) {
                    qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;
                    if (pContent.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                        continue;
                    long uiTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                    long iDissInMs = (uiTickCnt - pTaskInfo.var.dwTickCnt_recv_lastRefreshed1);
                    if (Math.abs(iDissInMs) < 600) {
                        //str = "任务 " + pTaskInfo.var.iTaskId + " 仍在活跃中";
                        //qyFuncs.showNotification_open(0, 0, 0, str);

                        //
                        findOldAvTaskActiveParams.piTaskId = pTaskInfo.var.iTaskId;
                        findOldAvTaskActiveParams.piElapseInMs = iDissInMs;

                        //
                        break;
                    }


                }
            }
        }
        if (i == pProcInfo.cfg.usMaxCnt_taskInfos) return iErr;

        iErr = 0;

        return iErr;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity_dlgTalk   OnCreate ");
        //  create your application here
        int iErr = -1;
        String str;

        this.mTag = "dlgTalk";

        Intent intent = getIntent();
        long ui64Id = intent.getLongExtra("ui64Id", 0);
        int wParam = intent.getIntExtra("wParam", 0);
        int lParam = intent.getIntExtra("lParam", 0);
        //
        Log.e("ui64Id  " + ui64Id);

        mHandler = new MyHandler(this);

        m_var = new dlgTalkProc.DLG_TALK_var();
        m_var.tickCountOnCreate = tmpFunc.qyFuncs.myGetTickCount(null);
        m_var.m_pProcessQ = new ArrayList<>();
        m_var.addr.idInfo.ui64Id = ui64Id;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            if (iErr != 0)
            {
                doFinish("dlgTalk.OnCreate failed");
            }
            return;
        }

        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        if (m_var.addr.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
            str = qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_cantTalkToMySelf) + m_var.addr.idInfo.ui64Id;
            Log.d(str);
            showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, str);
            if (iErr != 0)
            {
                doFinish("dlgTalk.OnCreate failed");
            }
            return;
        }

        if (wParam == qyWmComm.Consts.CONST_qyWmParam_doTask) {
            m_var.bActivityStarted_toDoTask = true;
            m_var.toDoTask_usOp = lParam;
        }

        setContentView(R.layout.layout_activity_dlgtalk);
        mAdapter = new FragmentPagerAdapter_dlgTalk(getSupportFragmentManager());

        bindViews();

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qyDefs.QY_MESSENGER_ID idInfo = m_var.addr.idInfo;

        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, msgrInfo, regInfo, null, null)) {
            str = "dlgTalk.OnCreate failed, bGetMessengerInfoBySth failed, " + idInfo.ui64Id;
            Log.e(str);
            if (iErr != 0)
            {
                doFinish("dlgTalk.OnCreate failed");
            }
            return;
        }

        m_var.addr.uiObjType = msgrInfo.uiType;

        if (m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
            if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "", idInfo, grpInfo)) {

            }
            m_str_title = grpInfo.name;
        } else {
            str = regInfo.syr;
            m_str_title = str;
        }

        if (0 != dlgTalkProc.qyFuncs_Droid.dlgTalk_OnInitDialog(this, m_var)) {
            Log.e("dlgTalk_OnInitDialog failed");
            if (iErr != 0)
            {
                doFinish("dlgTalk.OnCreate failed");
            }
            return;
        }

        setTimer(1000, gui_onTimer);

        if (m_var.bActivityStarted_toDoTask) {
            doTask_av_accept();
        }
    }

    protected void onResume() {
        super.onResume();
        //
        Log.d("dlgTalk: onResume");

        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        if (pQyMc != null && pQyMc.gui != null) {
            pQyMc.gui.hLastActiveTalk = this;
            pQyMc.gui.hActiveWnd = this;
        }
        if (guiShare.GuiShare.pf_gui_notify_chk != null) {
            guiShare.GuiShare.pf_gui_notify_chk.call();
        }
    }


    protected void onPause() {
        super.onPause();
        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc==null) return;
        pQyMc.gui.hLastActiveTalk = null;
        MyApplication myApplication = (MyApplication) getApplication();
        String str;
        if (null != myApplication) {
            if (myApplication.mediaPlayer != null) {
                if (myApplication.mediaPlayer.isPlaying()) {
                    Log.d("停止铃声");
                    myApplication.mediaPlayer.pause();
                }
            }
        }
        //
        Log.d("dlgTalk: onPause");

        //
        if (pQyMc != null && pQyMc.gui.hActiveWnd == this) {
            pQyMc.gui.hActiveWnd = null;
        }

        if (isFinishing()) {
            //
            str = String.format("dlgTalk.OnPause,isFinishing()");
            if (m_var != null)
                str = String.format("dlgTalk%s.OnPause,isFinishing()", m_var.addr.idInfo.ui64Id);
            str += " enters";
            Log.d(str);

            killTimer("Act_dlgTalk.OnPause.947");
            dlgTalk_OnDestroy(this, m_var);
            Log.d("dlgTalk.OnPause leaves,isFinishing()");
        }

    }

    public void onBackPressed() {
        Log.e("dlgTalk.onBackPressed enters");
        if(guiShare.GuiShare.pf_PostMessage_new != null)
        {
            guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_talker, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        }

        Log.e("dlgTalk.onBackPressed leave");
    }

    private void bindViews() {
        vPager = findViewById(R.id.vpager);
        vPager.setAdapter(mAdapter);
        vPager.addOnPageChangeListener(this);
        vPager.setCurrentItem(PAGE_ONE, false);
    }

    private void clearSelected() {
    }

    public void onClick(View v) {
        if (v.getId() == R.id.txt_msg) {
            clearSelected();
            vPager.setCurrentItem(PAGE_ONE, false);
        }
    }

    protected void onDestroy() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        if (pQyMc == null) {
            Log.e("pQyMc == null");
            super.onDestroy();
            return;
        }
        {
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            if(mainActivity!=null)
            {
                CookieBar.dismiss(mainActivity);
            }
            Log.d("Activity_dlgTalk  OnDestroy");

            if (this.vPager != null) {
                vPager.setAdapter(null);
            }

            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //throw new NotImplementedException();
        if (state == 2) {
            switch (vPager.getCurrentItem()) {
                case PAGE_ONE:
                    clearSelected();
                    //txt_msg.setSelected(true);
                    break;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //throw new NotImplementedException();
    }

    @Override
    public void onPageSelected(int position) {
        //throw new NotImplementedException();
    }
}
