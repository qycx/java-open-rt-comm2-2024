package com.qycx;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.IO_TAG;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.confOthers_sendStreamInfo;
import static com.qycx.base.getTalkerDesc.Consts.*;
import static com.qycx.base.guiShare.Consts.*;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.qmcTaskInfo.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_I420;
import static com.qycx.base.qyDefs.Consts.*;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.qyWndContentType.Consts.*;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import org.greenrobot.eventbus.EventBus;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.audioRecorder_android;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;
import com.qycx.base.dlgTalk_videoConference;
import com.qycx.base.doCmd_startAvCall;
import com.qycx.base.getTalkerDesc;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.myrecycleFunc;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qmcVideoCapture;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyWmComm;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showInfo_open;
import com.qycx.base.statuscenter.messagebean.NetConnectEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectStatus;
import com.qycx.base.talkDraw;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;
import com.qycx.base.util.javatuples.Triplet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fragment_dlgTalk_av2 extends Fragment implements PopupWindow.OnDismissListener {
    //private static String TAG = "Camera2VideoFragment";
    public static int REQUEST_VIDEO_PERMISSIONS = 1;
    public static String FRAGMENT_DIALOG = "dialog";
    public static String TAG = "Fragment_dlgTalk_av2";

    private int statusBarHeight;

    private boolean isChangeCamera = false;
    boolean canCloseCameraThread = false;
    public Activity_dlgTalk_av mOwner_wnd;

    //
    public MainHandler mMainHandler;

    //
    public boolean isFirstOpenCamera;
    public int mLoopCtrl;

    //
    public MyTextureView mTextureView;
    private dlgTalk_av_Renderer mRenderer;

    //
    public ConstraintLayout bottomMenu;
    public ConstraintLayout topMenu;
    TextView m_btn_request;
    TextView m_tv_ioStatusOut;
    TextView m_tv_ioStatusIn;
    TextView nvr_channel_name;
    TextView nvr_control;
    TextView back_main_video;
    TextView back_to_video;
    TextView up_bt;
    TextView down_bt;
    TextView left_bt;
    TextView right_bt;
    TextView long_focus_bt;
    TextView short_focus_bt;
    TextView m_btn_av;
    TextView meeting_launch;
    TextView meeting_account;
    TextView meeting_screen_info;
    TextView meeting_id;


    FrameLayout min_viewport;
    Drawable pull_downDrawable;
    Drawable pull_upDrawable;
    //
    ConstraintLayout left_menu_relative;
    public ConstraintLayout more_info_menu;
    ConstraintLayout right_menu_relative;
    public PopupWindow popupWindow;
    NvrListAdapter nvrListAdapter;
    public TextView btnLocalVideo;
    public TextView more_tv;
    public TextView btnChangeCamera;
    public TextView btnRotateScreen;

    //zzy add 2024/10/15
    //增加语音视频切换按钮
    private TextView tvTips;
    private ImageView ivVoiceVideoSwitcher;

    camera_RenderThread mCameraRenderThread;
    //
    public int mSensorOrientation;
    public NetConnectStatus mNetStatus;

    //public com.qycx.base.qdc.decObj_a_mediaCodec decObj_a_mediaCodec;

    //
    public int m_st_iW;
    public int m_st_iH;

    //
    public int m_o_iW;
    public int m_o_iH;


    // These values are passed to us by the camera/render thread, and displayed in the UI.
    // We could also just peek at the values in the RenderThread Object, but we'd need to
    // synchronize access carefully.

    private int mCameraPreviewWidth_unused, mCameraPreviewHeight_unused;
    private float mCameraPreviewFps_unused;
    //
    private int mRectWidth, mRectHeight;
    private int mZoomWidth, mZoomHeight;
    private int mRotateDeg;

    //
    public class Tmp_autoSpeak {
        public boolean m_bAutoSpeak;
        public int m_nTimes_autoSpeak;
    }

    Tmp_autoSpeak m_autoSpeak;

    public static volatile boolean sReleaseInCallback = true;

    //
    public int showBottomMenu() {
        if (this.bottomMenu.getVisibility() != android.view.View.VISIBLE) {
            this.bottomMenu.setVisibility(android.view.View.VISIBLE);
        }
        return 0;
    }

    public int gui_onTimer(Object p0, Object p1, Object p2) {
        Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        if (owner == null) return -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        {
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            mLoopCtrl++;

            @SuppressLint("DefaultLocale") String strOut = String.format("↑%.2fM", pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1000.0);
            @SuppressLint("DefaultLocale") String strIn = String.format("↓%.2fM",  pProcInfo.status.netStat.ins.uiInSpeedInKbps  / 1000.0);
//            String strOut = "↑" + pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1024.0 + "M";
//            String strIn = "↓" + pProcInfo.status.netStat.ins.uiInSpeedInKbps / 1024.0 + "M";

            int outSpeed = pProcInfo.status.netStat.ins.uiOutSpeedInKbps;
            int inSpeed  = pProcInfo.status.netStat.ins.uiInSpeedInKbps;

      //      EventBus.getDefault().post(new ChangeNetspeedEvent(inSpeed, outSpeed));

            m_tv_ioStatusOut.setText(strOut);
            m_tv_ioStatusIn.setText(strIn);
            if (owner.mOwner_hTalk.get().m_var.is4k) {
//                Toast.makeText(owner, "不支持4K会议", Toast.LENGTH_SHORT).show();
                ToastUtils.showToast("不支持4K会议");
            }

            //if (0 == (mLoopCtrl % 3))
            {
                chkIfAvAlive();
                chkAutoSpeak(mLoopCtrl);
            }
            initMinButtonStates();
        }
        return 0;
    }


    //
    int m_nLostVideo = 0;

    //
    int chkIfAvAlive() {
        //
        //
        do {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, mOwner_wnd.m_index_taskInfo1, "");
            if (pTaskInfo == null) {
                m_nLostVideo  ++  ;
            }
            else {
                qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
                if (taskData == null) break;


                //
                long dwTickCnt = myGetTickCount(null);
                long iElapseInMs_a = dwTickCnt - taskData.tmpStatus.lastTickCnt_dec_a;
                long iElapseInMs_v  =  dwTickCnt  -  taskData.tmpStatus.lastTickCnt_dec_v;
                if (Math.abs(iElapseInMs_a) > 3000  &&  Math.abs(iElapseInMs_v)>3000)
                {
                    m_nLostVideo++;
                }
                else {
                    m_nLostVideo = 0;
                }
            }

            Activity_dlgTalk_av owner = this.mOwner_wnd;
            Activity_dlgTalk talk = owner.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = talk.m_var;

            //
            int max_nLostVideo  =  10;
            if  (  pProcInfo.bUse100k)  {
                max_nLostVideo=40;
            }

            //
            if (owner.m_nErrs_hTalk_av_not_matched > 130 || m_nLostVideo > max_nLostVideo) {

                //
                if  (  true  ) {
                    //
                    m_var.av.bAutoCancel_lostVideo = true;
                    m_var.av.dwTickCnt_autoCancel_lostVideo = myGetTickCount(null);
                    m_var.av.bDoneOnce_autoCancel_lostVideo = false;

                    //
                    String str;
                    str = "m_nLostVideo (" + m_nLostVideo + ") > 10, autoCancel";
                    showInfo_open.qyFuncs.showInfo_open(str);

                    //
                    owner.doAutoCancel();
                }
                else  {
                      Log.d("qy","for test: cancel checking m_nLostVideo");
                }

                //
            }

            //
            if  (  pProcInfo.m_n_swapBuffersFailed  >  5)  {
                pProcInfo.m_n_swapBuffersFailed=0;

                //
                String str;
                str =  "fg_talk_av2: m_bSwapBuffersFailed true, autoCancel";
                Toast.makeText(MyApplication.getContext(),"swapBufferFailed, autoCancel", Toast.LENGTH_SHORT).show();
                //
                owner.doAutoCancel();
            }


        } while (false);

        //
        return 0;
    }


    //
    int chkAutoSpeak(int loopCtrl) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if(pProcInfo==null) return -1;
        boolean bDbg = false;
        String str;

        if (pProcInfo.av.m_bInSleepMode) {
            Log.d("chkAutoSpeak: bInSleep true, do nothing");
            return 0;
        }

        //
        long dwTickCnt  =  myGetTickCount(null);
        long iDiffInMs  =  dwTickCnt  -  pProcInfo.av.localAv.dwLastTickCnt_vCap;
        if  (  iDiffInMs  >  10000  )
        {
            if  (  loopCtrl  >  30) {       //  忽略掉前面的错误判断
                str = "chkAutoSpeak: 摄像头没有正常工作. 忽略";
                Context context = MyApplication.getContext();
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
            //
            return   0;
        }
        iDiffInMs  =  dwTickCnt  -  pProcInfo.av.localAv.dwLastTickCnt_aCap;
        if  (  iDiffInMs  >  10000  )
        {
            if  (  loopCtrl  >  30) {       //  忽略掉前面的错误判断
                str = "chkAutoSpeak: 麦克风没有正常工作. 忽略";
                Context context = MyApplication.getContext();
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
            //
            return   0;
        }

        //
        Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
        if (m_autoSpeak.m_bAutoSpeak) {
            if (m_autoSpeak.m_nTimes_autoSpeak > 30) {
                m_autoSpeak = new Tmp_autoSpeak();
                //
                dlgTalk.m_var.av.m_bState_canSpeak = false;
            } else {
                m_autoSpeak.m_nTimes_autoSpeak++;


                boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);
                if (!canSpeak) {
                    Log.d("主动发言");
                    do_request(true);
                } else {
                    m_autoSpeak = new Tmp_autoSpeak();
                }

            }
        } else {
            if (dlgTalk.m_var.av.m_bState_canSpeak) {
                boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);
                if (!canSpeak) {
                    m_autoSpeak.m_bAutoSpeak = true;
                }
            } else {
                if (this.mOwner_wnd.mOwner_hTalk.get().get_pm_var().addr.uiObjType != CONST_objType_imGrp) {
                    boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);
                    if  (  !canSpeak) {
                        do_request(true);
                    }
                }

            }
        }

        return 0;

    }




        //
    public int waitTextureViewAvailable(Object p0, Object p1, Object p2) {
        int iErr = -1;
        Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        Activity_dlgTalk hTalk = owner.mOwner_hTalk.get();

        String str;

        str = "fg_dlgTalk_av2, waitTextureViewAvailable called";
        Log.d(str);

        //
        if (!mTextureView.isAvailable()) {
            Log.d("fg_dlgTalk_av,gui_onTimer, textureView.isAvail is false");

            return 0;
        }
        //
        if (hTalk.m_var.m_share_eglContext == null) {
            Log.d("fg_dlgTalk_av,gui_onTimer, talk.m_share_eglContext is null");
            return 0;
        }


        if (hTalk.m_var.av.taskInfo.bTaskExists) {
            if (!hTalk.m_var.m_bNeedResumeTaskAv) {
                owner.pauseTaskAv("waitTexViewAvail");


            }

            if (hTalk.m_var.m_bNeedResumeTaskAv) {
                //
                owner.resumeTaskAv("waitTexViewAvail");
                iErr = 0;
                return iErr;
            }

            //
            iErr = 0;
            return iErr;
        }


        //
        if (hTalk.m_var.av.bNeedDoTaskAv) {
            //
            hTalk.m_var.av.bNeedDoTaskAv = false;

            //
            int iTaskId = 0;
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, mOwner_wnd.m_index_taskInfo1, "");
            if (pTaskInfo != null) {
                iTaskId = pTaskInfo.var.iTaskId;
            }


            //
            doTask_av(mOwner_wnd.m_iCmd, iTaskId);
        }

        //
        iErr = 0;

        str = String.format("fg_dlgTalk_av2, waitTextureViewAvailable leaves " + iErr);
        showInfo_open.qyFuncs.showInfo_open(str);
        //
        return iErr;
    }

    //


    public int doTask_av(int iCmd, int iTaskId) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        //boolean bDbg = false;
        String str;

        //
        //boolean b3D = false;
        //  2017/07/17
        boolean ucbAvConsole = false;
        //Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        //decObj_a_mediaCodec = new decObj_a_mediaCodec();
        //decObj_a_mediaCodec.m_bTest = true;
        //decObj_a_mediaCodec.testExtractMpegFrames(this.mOwner_wnd);

        if (!mTextureView.isAvailable()) {
            str = "fg_dlgTalk_av.doTask_av failed, textureView.isAvail is false";
            Log.d(str);

            return iErr;
        }

        //
        test.testCls_Droid.testChk("doTask_av");

        //
        switch (iCmd) {
            case tmpResource.IDC_BUTTON_av: {
                doCmd_startAvCall.qyFuncs_Droid.doCmd_startAvCall(null, mOwner_wnd.mOwner_hTalk.get(), mOwner_wnd.m_Level, false, false, null);
                if  (  mOwner_wnd.mOwner_hTalk!=null) {
                    Activity_dlgTalk hTalk  =  mOwner_wnd.mOwner_hTalk.get();
                    if  (  hTalk!=null) {
                        if  (  hTalk.m_var.av.taskInfo.bTaskExists  ) {
                            mOwner_wnd.m_index_taskInfo1  =  hTalk.m_var.av.taskInfo.iIndex_taskInfo;
                            //
                            str  =  "fg_dlgTalk_av2.doTask_av: startAvCall ok, set index_taskInfo " + mOwner_wnd.m_index_taskInfo1;
                            showInfo_open.qyFuncs.showInfo_open(str);
                        }
                    }
                }
            }
            break;
            case tmpResource.IDC_BUTTON_av_accept: {
                int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "doTask_av");
                if (pTaskInfo == null) return iErr;

                dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
                //memset(&param, 0, sizeof(param));
                param.bManualCmd = true;        //  2017/02/12
                //pFuncs->pf_dlgTalk_procTask1(  hDlgTalk,  pDlgTalkVar->guiData.av.iTaskId,  CONST_imOp_dual_permit,  NULL,  &param  );
                dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(mOwner_wnd.mOwner_hTalk.get(), pTaskInfo.var.iTaskId, (short) CONST_imOp_dual_permit, null, param);

                Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
                if (dlgTalk != null) {
                    Activity_dlgTalk hTalk  =  dlgTalk;
                    //
                    if  (  hTalk.m_var.av.taskInfo.bTaskExists  ) {
                        mOwner_wnd.m_index_taskInfo1  =  hTalk.m_var.av.taskInfo.iIndex_taskInfo;
                        //
                        str  =  "fg_dlgTalk_av2.doTask_av: dlgTalk_procTask1.dual_permit ok, set index_taskInfo " + mOwner_wnd.m_index_taskInfo1;
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }
                    //
                    if (dlgTalk.m_var.av.bAutoCancel_lostVideo) {
                        dlgTalk.m_var.av.bAutoCancel_lostVideo = false;

                        //
                        if (dlgTalk.m_var.av.m_bState_canSpeak) {
                            m_autoSpeak.m_bAutoSpeak = true;
                            m_autoSpeak.m_nTimes_autoSpeak++;

                            //
                            do_request(true);
                        }
                    }
                    if (!dlgTalk.m_var.av.m_bState_canSpeak) {
                        //
                        do_request(false);
                    }
                }
            }
            break;
            default:

                Log.d("not finished, unknown iCmd");
                break;
        }

        iErr = 0;
        return iErr;
    }


    //
    public int updateRequestButtonStatus() {
        Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();

        //this.m_btn_request
        boolean bState_canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        if (bState_canSpeak) {
            m_btn_request.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_stopSpeaking));
            m_btn_request.setTextColor(getResources().getColor(R.color.av_button_active_text_color));
            m_btn_request.setBackgroundColor(getResources().getColor(R.color.av_button_active_color));
        } else {
            m_btn_request.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_requestToSpeak));
            m_btn_request.setBackgroundColor(getResources().getColor(R.color.av_button_normal_color));
            m_btn_request.setTextColor(getResources().getColor(R.color.av_button_normal_text_color));

        }
        return 0;
    }

    public static int initVideoCapDev(Activity_dlgTalk hTalk, int iWndContentType, String displayName_selected, qyCommProc_open.BITMAPINFOHEADER pBih_suggested, qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        String str;
        if (iWndContentType != CONST_qyWndContentType_talker) return -1;
        Activity_dlgTalk dlgTalk = hTalk;//((WeakReference<Activity_dlgTalk>) hTalk).get();
        dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;
        if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return -1;
        Fragment_dlgTalk_av2 fg = dlgTalk_av.m_fg_dlgTalk_av2;

        if (!fg.mTextureView.isAvailable()) return -1;

        //
        int w, h;
        //w = fg.mTextureView.Width;
        //h = fg.mTextureView.Height;
        w = pBih_suggested.biWidth;
        h = pBih_suggested.biHeight;

        if (null == dlgTalk.m_var.m_share_eglContext) return iErr;
        int surfaceOrientation = dlgTalk_av.getWindowManager().getDefaultDisplay().getRotation();

        boolean bFront = true;
        if (m_var.av.taskInfo.bTaskExists) bFront = !m_var.av.taskInfo.bUse_backCamera;
        pCapBmp.m_bFront = bFront;

        fg.mCameraRenderThread = new camera_RenderThread(fg.mMainHandler, dlgTalk.m_var.m_share_eglContext, pCapBmp.iIndex_sharedObj, bFront, surfaceOrientation);
        fg.mCameraRenderThread.start();
        //
        if (0 != fg.mCameraRenderThread.waitUntilReady()) {
            return iErr;
        }
        //
        fg.mSensorOrientation = fg.mCameraRenderThread.myCamera.mSensorOrientation;
        //
        //pCapBmp.mSensorOrientation = fg.mSensorOrientation;


        //
        //
        Log.d("initVideoCapDev.851");


        //fg.me_tex
        //
        SurfaceTexture st = null;// fg.m_st_test;
        //st=new SurfaceTexture(CTalkDraw.me_texId);
        st = talkDraw.CTalkDraw.me_st;
        if (st == null) {
            Log.d("initVideoCapDev failed, me_st is null");
            return iErr;
        }

        //
        fg.m_st_iW = fg.mCameraRenderThread.mCameraPreviewWidth;
        fg.m_st_iH = fg.mCameraRenderThread.mCameraPreviewHeight;

        //
        int angle_z = 0;
        Triplet<Integer, Integer, Integer> o_info = myrecycleFunc.qyFuncs_Droid.get_tex_o_info(fg.mSensorOrientation, surfaceOrientation, fg.mCameraRenderThread.mCameraPreviewWidth, fg.mCameraRenderThread.mCameraPreviewHeight);
        angle_z = o_info.getValue0();
        fg.m_o_iW = o_info.getValue1();
        fg.m_o_iH = o_info.getValue2();

        //
        st.setDefaultBufferSize(fg.m_st_iW, fg.m_st_iH);

        if (bFront && fg.isFirstOpenCamera) {
            fg.mCameraRenderThread.mHandler.sendSurfaceAvailable(st, true);

        } else {

            fg.mCameraRenderThread.mHandler.sendSurfaceAvailable(st, false);

            fg.isFirstOpenCamera = false;
        }

        //
        Log.d("initVideoCapDev.910");

        //
        iErr = 0;

        //
        Log.d("initVideoCapDev leaves");

        //
        return iErr;
    }

    public static int exitVideoCapDev(Object hTalk, int iWndContentType) {
        int iErr = -1;

        if (iWndContentType != CONST_qyWndContentType_talker) return -1;
        Activity_dlgTalk dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (null == dlgTalk.m_var.m_hTool_dlgTalk_av) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;
        if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return -1;
        Fragment_dlgTalk_av2 fg = dlgTalk_av.m_fg_dlgTalk_av2;

        if (fg.mCameraRenderThread != null) {
            camera_RenderThread.camera_RenderHandler rh = fg.mCameraRenderThread.getHandler();
            if (rh != null) {
                rh.sendShutdown();
            }
            try {
                fg.mCameraRenderThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fg.mCameraRenderThread = null;

        }


        iErr = 0;
        return iErr;
    }

    public static boolean bGetCapturePara(Activity_dlgTalk hTalk, int iWndContentType, qyCommProc_open.QY_VIDEO_HEADER pVh_org) {
        boolean bRet = false;

        if (iWndContentType != CONST_qyWndContentType_talker) return false;
        Activity_dlgTalk dlgTalk = hTalk;//((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return false;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;
        if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return false;
        Fragment_dlgTalk_av2 fg = dlgTalk_av.m_fg_dlgTalk_av2;

        //
        qyCommProc_open.BITMAPINFOHEADER bih_org = new qyCommProc_open.BITMAPINFOHEADER();
//        qyMemoryFunc.qyFuncs.mymemset((Object) ( byte)bih_org, 0, sizeof(BITMAPINFOHEADER));
        /**/

        if (fg.mCameraRenderThread == null) return bRet;

        bih_org.biWidth = fg.m_o_iW;
        bih_org.biHeight = fg.m_o_iH;
        bih_org.biCompression = CONST_fourcc_I420;

        pVh_org.bih = bih_org;


        //
        bRet = true;
        return bRet;
    }

    public static int startCapture(Object hTalk, int iWndContentType) {
        return 0;
    }

    public static int stopCapture(Object hTalk, int iWndContentType) {
        return 0;
    }


    //
    public static Fragment_dlgTalk_av2 NewInstance() {
        return new Fragment_dlgTalk_av2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        isFirstOpenCamera = true;
        mOwner_wnd = (Activity_dlgTalk_av) getActivity();
    }


    private void getMeetingInfo() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        int iIndex_taskInfo = -1;
        dlgTalkProc.DLG_TALK_var m_var = mOwner_wnd.mOwner_hTalk.get().m_var;
        if (m_var.av.taskInfo.bTaskExists) {
            iIndex_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, m_var.av.taskInfo.iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, iIndex_taskInfo, "");
            if (pTaskInfo != null) {
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData.uiType == CONST_taskDataType_conf) {
                    qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

                    //  pTc.grp.idInfo_initiator;//发起人id
                    //qyFuncs.getTalkerDesc(pMisCnt, pActiveMem_from.avStream.idInfo, pDesc, Consts.cntof_desc, null, 0);

                    qmcTaskInfo.TMP_desc tmpDesc = new qmcTaskInfo.TMP_desc();
                    String desc = tmpDesc.desc;


                    qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                    getTalkerDescParams.pMisCnt = pMisCnt;
                    getTalkerDescParams.idInfo = m_var.av.taskInfo.idInfo_initiator;
                    getTalkerDescParams.talkerDesc = desc;
                    getTalkerDescParams.talkerDescCnt = cntof_desc;
                    getTalkerDescParams.shortName = null;
                    getTalkerDescParams.shortNameCnt = 0;
                    getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);
                    Log.d("会议召集人:" + getTalkerDescParams.talkerDesc);
                    String meeting_launch_people_str = "会议召集人:" + getTalkerDescParams.talkerDesc;
                    meeting_launch.setText(meeting_launch_people_str);
                    Log.d("会议召集人ID:" + m_var.av.taskInfo.idInfo_initiator.ui64Id);
                    if (m_var.av.taskInfo.idInfo_initiator.ui64Id == 0) {
                        meeting_id.setVisibility(android.view.View.INVISIBLE);
                        meeting_launch.setVisibility(android.view.View.INVISIBLE);
//                        back_main_video.setVisibility(android.view.View.INVISIBLE);
                        meeting_screen_info.setVisibility(android.view.View.INVISIBLE);
                    }
                    String meeting_id_str = "会议召集人ID:" + m_var.av.taskInfo.idInfo_initiator.ui64Id;
                    meeting_id.setText(meeting_id_str);
                    getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                    getTalkerDescParams.pMisCnt = pMisCnt;
                    getTalkerDescParams.idInfo = pMisCnt.idInfo;
                    getTalkerDescParams.talkerDesc = desc;
                    getTalkerDescParams.talkerDescCnt = cntof_desc;
                    getTalkerDescParams.shortName = null;
                    getTalkerDescParams.shortNameCnt = 0;
                    getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);


                    Log.d("当前登录账号:" + getTalkerDescParams.talkerDesc);
                    String meeting_account_str = "当前登录账号:" + getTalkerDescParams.talkerDesc;
                    meeting_account.setText(meeting_account_str);
                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTc.msgU;

                    {
                        if (pMsgTask.uiType == CONST_misMsgType_task) {
                            qnmCommProc_mis.IM_CONTENTU pContent = qnmCommProc_mis.qyFuncs.M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
                            if (null != pContent) {
                                qnmCommProc_mis.IM_CONTENTU tmp_pContent = pContent;
                                //
                                if (pContent.uiType == CONST_imCommType_transferAvInfo) {
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    int width = transferAvInfo.confMosaicTranInfo_video.video.vh_decompress.bih.biWidth;
                                    int height = transferAvInfo.confMosaicTranInfo_video.video.vh_decompress.bih.biHeight;
                                    String meeting_screen_info_str = "分辨率:" + width + "x" + height;
                                    meeting_screen_info.setText(meeting_screen_info_str);
                                    Log.d("px py 分辨率:" + width + "--" + height);
                                }
                            }
                        }

                    }

                }

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = null;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        view = inflater.inflate(R.layout.layout_fg_dlgtalk_av, container, false);
        //
        this.mMainHandler = new MainHandler(this);

        mTextureView = view.findViewById(R.id.texture);
        bottomMenu = view.findViewById(R.id.bottomMenu);
        topMenu = view.findViewById(R.id.topMenu);
        more_info_menu = view.findViewById(R.id.more_info_menu);
        m_btn_request = view.findViewById(R.id.btn_request);
        m_tv_ioStatusOut = view.findViewById(R.id.tv_ioStatus_out);
        m_tv_ioStatusIn = view.findViewById(R.id.tv_ioStatus_in);
        // m_btn_more = (Button)view.findViewById(R.id.btn_more);
        up_bt = view.findViewById(R.id.up_bt);
        down_bt = view.findViewById(R.id.down_bt);
        left_bt = view.findViewById(R.id.left_bt);
        right_bt = view.findViewById(R.id.right_bt);
        short_focus_bt = view.findViewById(R.id.short_focus_bt);
        long_focus_bt = view.findViewById(R.id.long_focus_bt);
        m_btn_av = view.findViewById(R.id.btn_av);
        nvr_channel_name = view.findViewById(R.id.nvr_channel_name_title);
        nvr_control = view.findViewById(R.id.nvr_control);
        back_main_video = view.findViewById(R.id.back_main_video);
        back_to_video = view.findViewById(R.id.back_to_video);
        nvr_control.setOnClickListener(M_Nvr_control_Click);
        back_main_video.setOnClickListener(M_Back_Main_Video_Click);
        back_to_video.setOnClickListener(M_Back_To_Video_Click);
        min_viewport = view.findViewById(R.id.min_viewport);
        btnChangeCamera = view.findViewById(R.id.changeCamera);
        btnChangeCamera.setOnClickListener(BtnChangeCamera_Click);
        view.findViewById(R.id.rotatescreen).setOnClickListener(BtnRotateScreen_Click);
        btnLocalVideo = view.findViewById(R.id.localVideo);
        more_tv = view.findViewById(R.id.more_tv);
        meeting_launch = view.findViewById(R.id.meeting_launch);
        meeting_account = view.findViewById(R.id.meeting_account);
        meeting_screen_info = view.findViewById(R.id.meeting_screen_info);
        meeting_id = view.findViewById(R.id.meeting_id);
        btnLocalVideo.setOnClickListener(BtnLocalVideo_Click);
        more_tv.setOnClickListener(BtnMoreTV_Click);
        btnChangeCamera.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_changeCamera));
        btnLocalVideo.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_localVideo));

        left_menu_relative = view.findViewById(R.id.left_menu_relative);
        right_menu_relative = view.findViewById(R.id.right_menu_relative);

        mRenderer = new dlgTalk_av_Renderer(this, CONST_iFgType_dlgTalk_av2);
        mRenderer.init();
        mRenderer.start();

        mOwner_wnd.menuInScreen = false;
        min_viewport.post(mOwner_wnd.runnableShowBottomBar);
        mOwner_wnd.bottomBarHandler.postDelayed(mOwner_wnd.runnableHideBottomBar, 8000);
        mTextureView.setSurfaceTextureListener(mRenderer);
        mTextureView.setOnTouchListener(Texture_View_OnTouchListener);
        nvr_channel_name.setOnClickListener(Channel_num_tv_ClickListener);
        up_bt.setOnTouchListener(Up_bt_OnTouchListener);
        down_bt.setOnTouchListener(Down_bt_OnTouchListener);
        left_bt.setOnTouchListener(Left_bt_OnTouchListener);
        right_bt.setOnTouchListener(Right_bt_OnTouchListener);
        short_focus_bt.setOnTouchListener(Short_focus_bt_OnTouchListener);
        long_focus_bt.setOnTouchListener(Long_focus_OnTouchListener);

        m_btn_request.setOnClickListener(M_btn_request_Click);
        m_btn_av.setOnClickListener(M_btn_av_Click);
        min_viewport.setOnClickListener(M_min_viewport_Click);

        if (pQyMc == null) return null;

        pull_downDrawable = mOwner_wnd.getDrawable(R.drawable.pull_down);
        pull_downDrawable.setBounds(0, 0, pull_downDrawable.getIntrinsicWidth(), pull_downDrawable.getIntrinsicHeight());

        pull_upDrawable = mOwner_wnd.getDrawable(R.drawable.pull_up);
        pull_upDrawable.setBounds(0, 0, pull_upDrawable.getIntrinsicWidth(), pull_upDrawable.getIntrinsicHeight());

        m_btn_request.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_requestToSpeak));
//        m_btn_av.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_cancel_AV));//"Cancel";

        bindViews(view);


        Activity_dlgTalk hTalk = (mOwner_wnd.mOwner_hTalk.get());
        dlgTalkProc.DLG_TALK_var m_var = hTalk.get_pm_var();

        if (m_var.addr.uiObjType != CONST_objType_imGrp) {
            m_btn_request.setVisibility(android.view.View.INVISIBLE);
        }
        this.m_autoSpeak = new Tmp_autoSpeak();

        //
        if  (  pQyMc!=null  )  {
            ctxQmc.CCtxQmc pProcInfo  = pQyMc.get_pProcInfo();
            if  (  pProcInfo.bUse100k  )  {
                //
                pProcInfo.use100k_onlyV=true;
//                back_main_video.setText("仅音频");
            }
        }

        //
        return view;
    }

    private void bindViews(View view) {
        bindVoiceVideoSwitcher(view);
        bindBottomMenu(view);
    }

    private void bindBottomMenu(View view) {
        if  (!isGroup()) {
            back_main_video.setVisibility(View.GONE);
        }
    }

    private boolean isGroup(){
        Activity_dlgTalk_av owner = this.mOwner_wnd;
        Activity_dlgTalk talk = owner.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = talk.m_var;
        return m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindVoiceVideoSwitcher(View view) {
        ctxQmc.CCtxQmc pProcInfo  = g_pQyMc.get_pProcInfo();



        //仅100k,才显示音视频切换按钮
        //群组不显示
        if(!pProcInfo.bUse100k || isGroup())  return;

        tvTips = view.findViewById(R.id.tvTips);
        ivVoiceVideoSwitcher = view.findViewById(R.id.ivVoiceVideoSwitcher);
        ivVoiceVideoSwitcher.setVisibility(View.VISIBLE);
        ivVoiceVideoSwitcher.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //按下 "仅音频"
                pProcInfo.use100k_onlyV=false;
                tvTips.setVisibility(View.VISIBLE);
                return true;
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                //弹起
                tvTips.setVisibility(View.GONE);
                pProcInfo.use100k_onlyV=true;
                return false;
            }
            return false;
        });
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveNetStatusEvent(NetConnectEvent bean) {
        NetConnectStatus status = bean.getNetStatus();
        Log.d(IO_TAG, "OnReceiveNetStatusEvent mNetStatus:" + mNetStatus + " status:" + status);
        if(mNetStatus == NetConnectStatus.DISCONNECTED && status ==  NetConnectStatus.CONNECTED)
        {
            Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
            if(dlgTalk !=null && dlgTalk.m_var.av.m_bState_canSpeak){
                do_request(true);
            }
        }
        mNetStatus = status;
    }

    /**
     * 回到主页
     */

    private final View.OnClickListener M_Back_To_Video_Click = (View view) -> {
        resetCurrentVideoChanel();
    };

    private final View.OnClickListener M_Back_Main_Video_Click = (View view) -> {
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        if  (  pQyMc==null) return;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

        //
        Activity_dlgTalk_av owner = this.mOwner_wnd;


        //
        if  (!isGroup())  {
            //
//            pProcInfo.use100k_onlyV=!pProcInfo.use100k_onlyV;
//            back_main_video.setText(pProcInfo.use100k_onlyV  ?  "仅音频"  :  "仅视频");

            if  (  mOwner_wnd==null)  return;
            if  (  mOwner_wnd.mOwner_hTalk==null)return;
            Activity_dlgTalk hTalk  =  mOwner_wnd.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var  =  hTalk.get_pm_var();
            //
            return;
        }


        //
        audioRecorder_android.qyFuncs_Droid.changeMicrophoneStatus(this.mOwner_wnd);
        confOthers_sendStreamInfo(owner);
        back_main_video.setText(audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted() ? "开麦" : "关麦");
       // EventBus.getDefault().post(new MicrophoneMutedEvent(audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted()));
        if (audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted()){
            back_main_video.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_text_color));
            back_main_video.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_color));
        }
        else {
            back_main_video.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_text_color));
            back_main_video.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_color));
        }
    };

    private final View.OnClickListener Channel_num_tv_ClickListener = (View view) -> {
        Log.d("Channel_num_tv_ClickListener");
        if (popupWindow.isShowing()) {

            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(nvr_channel_name);
            nvr_channel_name.setCompoundDrawables(null, null, pull_downDrawable, null);
        }
    };

    private final View.OnTouchListener Long_focus_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            long_focus_bt.setPressed(true);
            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_IN, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            long_focus_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_IN, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Short_focus_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            short_focus_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_OUT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            short_focus_bt.setPressed(false);
            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_OUT, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Right_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            right_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_RIGHT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            right_bt.setPressed(false);
            dlg_YTBtn_remote(IDC_BUTTON_RIGHT, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Left_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            left_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_LEFT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            left_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_LEFT, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };

    private final View.OnTouchListener Up_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            up_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_UP, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            up_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_UP, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };

    private final View.OnTouchListener Down_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            down_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_DOWN, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            down_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_DOWN, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };

    private final View.OnTouchListener Texture_View_OnTouchListener = (View v, MotionEvent event) -> {
        if  ( event.getAction()==MotionEvent.ACTION_DOWN) {
            //Log.d("qy" ,"down");
            if  (  mOwner_wnd.menuInScreen) {
                mOwner_wnd.bCmdStart=true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if  (  mOwner_wnd.bCmdStart) {
                mOwner_wnd.bCmdStart = false;

                //
                Rect centerSurfaceViewPortRect = (Rect) mOwner_wnd.mOwner_hTalk.get().m_var.av.hDc_av_rect;
                //
                if (centerSurfaceViewPortRect == null) {
                    return false;
                }

                //

                if (centerSurfaceViewPortRect.contains((int) (event.getX()), (int) (event.getY() - statusBarHeight))) {

                    int centerViewPortWidth = centerSurfaceViewPortRect.right - centerSurfaceViewPortRect.left;
                    int centerViewPortHeight = centerSurfaceViewPortRect.bottom - centerSurfaceViewPortRect.top;

                    Log.d("zzstar 发送原始坐标" + event.getX() + "- " + event.getY());
                    int transformVideoX = (int) ((event.getX() -
                            centerSurfaceViewPortRect.left) / centerViewPortWidth * 1920);
                    int transformVideoY = (int) ((event.getY() -
                            centerSurfaceViewPortRect.top - statusBarHeight) / centerViewPortHeight * 1080);

                    qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
                    layoutParam.phoneEnlargeParam.x = (short) transformVideoX;
                    layoutParam.phoneEnlargeParam.y = (short) transformVideoY;
                    Log.d("zzstar 发送视频映射坐标：" + layoutParam.phoneEnlargeParam.x + "- " + layoutParam.phoneEnlargeParam.y);

                    //组名
                    qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
                    idInfo_imGrp_related.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.addr.idInfo.ui64Id;

                    // 我
                    qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
                    idInfo_requester.ui64Id = 0;

                    //mcu 的
                    qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
                    idInfo_to.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.av.taskInfo.idInfo_starter.ui64Id;

                    dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(mOwner_wnd.mOwner_hTalk.get(), (byte) 0, (short) 0, (short) CONST_imOp_phone_enlargeImg, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
                }
            }
        }
        return true;
    };


    private final View.OnClickListener M_min_viewport_Click = (View view) -> {
        resetCurrentVideoChanel();
    };

    public void resetCurrentVideoChanel() {

        qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
        layoutParam.phoneEnlargeParam.x = 0;
        layoutParam.phoneEnlargeParam.y = 0;

        //组名
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.addr.idInfo.ui64Id;

        // 我
        qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
        idInfo_requester.ui64Id = 0;

        //mcu 的
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        idInfo_to.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.av.taskInfo.idInfo_starter.ui64Id;
        Log.d("缩小画面");
        dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(mOwner_wnd.mOwner_hTalk.get(), (byte) 0, (short) 0, (short) CONST_imOp_phone_enlargeChannel, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
    }

    private final View.OnClickListener BtnLocalVideo_Click = (View view) -> {
        //throw new NotImplementedException();
        Log.d("changeCamera clicked");
        mOwner_wnd.changeLocalVideo();
    };

    private final View.OnClickListener BtnMoreTV_Click = (View view) -> {

        getMeetingInfo();
        if (more_info_menu.getVisibility() == View.VISIBLE) {
            more_tv.setActivated(false);
            more_tv.setText("更多");
            more_info_menu.setVisibility(android.view.View.INVISIBLE);
        } else {
            more_tv.setActivated(true);
            more_tv.setText("关闭更多");
            more_info_menu.setVisibility(View.VISIBLE);
        }
    };


    //
    long  lastTickCnt_changeCamera  =  0;

    //
    private final View.OnClickListener BtnChangeCamera_Click = (View view) -> {
        //
        long tickCnt  =  myGetTickCount(null);
        int  iDiffInMs  =  (int)(tickCnt  -  lastTickCnt_changeCamera);
        if  (  iDiffInMs  <  500)  {
            //  不让太频繁的切换
            return;
        }
        lastTickCnt_changeCamera  =  tickCnt;

        //throw new NotImplementedException();
        Log.d("changeCamera clicked");
        if (!isChangeCamera) {
            isChangeCamera = true;
            mOwner_wnd.changeCamera();
            btnChangeCamera.postDelayed(() -> isChangeCamera = false, 1000);
        }
    };

    private final View.OnClickListener BtnRotateScreen_Click = (View view) -> {
        //
        long tickCnt  =  myGetTickCount(null);
        int  iDiffInMs  =  (int)(tickCnt  -  lastTickCnt_changeCamera);
        if  (  iDiffInMs  <  500)  {
            //  不让太频繁的切换
            return;
        }
        lastTickCnt_changeCamera  =  tickCnt;

        //throw new NotImplementedException();
        Log.d("Rotate screen clicked");
        Activity_dlgTalk_av owner = mOwner_wnd;
        owner.upsetPortraitOrLandscapeLayout();
    };



    private final View.OnClickListener M_btn_request_Click = (View view) -> {
        Activity_dlgTalk_av owner = mOwner_wnd;
        Activity_dlgTalk dlgTalk = owner.mOwner_hTalk.get();


        boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);
        do_request(!canSpeak);

        //
        dlgTalk.m_var.av.m_bState_canSpeak = !canSpeak;

        //
        m_autoSpeak = new Tmp_autoSpeak();  //  clear
    };

    private final View.OnClickListener M_Nvr_control_Click = (View view) -> {
        if (left_menu_relative.getVisibility() == View.INVISIBLE) {
            Log.d("left_menu_relative:Visible");
            left_menu_relative.setVisibility(View.VISIBLE);
            right_menu_relative.setVisibility(View.VISIBLE);
            nvr_controlSetActive();
        } else {
            Log.d("left_menu_relative:Invisible");
            left_menu_relative.setVisibility(android.view.View.INVISIBLE);
            right_menu_relative.setVisibility(android.view.View.INVISIBLE);
            nvr_controlSetNormal();
        }
    };

    public void nvr_controlSetNormal() {
        nvr_control.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_color));
        nvr_control.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_text_color));
    }

    public void nvr_controlSetActive() {
        nvr_control.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_text_color));
        nvr_control.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_color));
    }

    public void do_request(boolean bEnable) {
        //throw new NotImplementedException();
        //mIsShutter = true;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        Activity_dlgTalk_av owner = mOwner_wnd;
        Activity_dlgTalk dlgTalk = owner.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;

        //
        if (!m_var.av.taskInfo.bTaskExists) return;




        //
        qmcVideoCapture.qyFuncs.setCurSharedObjUsr_localAv(pProcInfo, m_var.av.iIndex_sharedObj_localAv, dlgTalk);

        //
        //boolean canSpeak = qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);

        //
        if (0 != dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak(dlgTalk, bEnable)) {
            return;
        }

        //
        boolean bState_canSpeak = bEnable;// !canSpeak;


        if (bState_canSpeak) {
            m_btn_request.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_stopSpeaking));
            m_btn_request.setTextColor(getResources().getColor(R.color.av_button_active_text_color));
            m_btn_request.setBackgroundColor(getResources().getColor(R.color.av_button_active_color));
        } else {
            m_btn_request.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_requestToSpeak));
            m_btn_request.setBackgroundColor(getResources().getColor(R.color.av_button_normal_color));
            m_btn_request.setTextColor(getResources().getColor(R.color.av_button_normal_text_color));
        }
    }


    private final View.OnClickListener M_btn_av_Click = (View view) -> {
        Activity_dlgTalk_av owner = this.mOwner_wnd;

        //
        owner.mOwner_hTalk.get().m_var.av.bNeedAvCall = false;

        //
        owner.mOwner_hTalk.get().m_var.av.bAutoCancel_lostVideo = false;

        //
        Log.d("M_btn_av_Click()点击退出");
        owner.exitDlgTalkAv();
    };


    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mTextureView.isAvailable()) {
            int ii = 0;
        } else {
            int jj = 0;
        }
    }
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        //
        canCloseCameraThread = true;
        doClean("fg.OnDestroyView");
        super.onDestroyView();
    }


    public int doClean(String hint) {
        String str;

        if (hint == null) hint = "";

        Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        if (owner != null) {
            //
            //owner.killTimer();

            //
            if (!owner.mOwner_hTalk.get().m_var.m_bNeedResumeTaskAv) {
                dlgTalkProc.qyFuncs_Droid.dlgTalk_closeTaskAv(owner.mOwner_hTalk, owner.mOwner_hTalk.get().m_var, "fg_dlgTalk_av.doClean_ifActivityIsFinishing");
            }
        }

        if (mRenderer != null) {
            mRenderer.halt();
            try {
                mRenderer.join();
                mRenderer.exit();
                //  mRenderer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRenderer = null;// (dlgTalk_av_Renderer)h;
        }
        return 0;
    }


    public int doClean_ifActivityIsFinishing() {
        return doClean("doClean_ifActivityIfFinishing");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        //

        super.onDestroy();
        Log.d("Fragment_dlgTalk_av2  OnDestroy");
        canCloseCameraThread = true;
        if (mMainHandler != null) {
            Log.d(" mMainHandler.removeCallbacksAndMessages(null);");
        }
    }


    private void getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

    }

    public boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    static Size chooseVideoSize(Size[] choices) {
        if (choices.length == 0) {
            return new Size(0, 0);
        }
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }

        return choices[choices.length - 1];

    }

    static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();

        for (int i = 0; i < choices.length; i++) {
            Size option = choices[i];
            if (option.getHeight() == option.getWidth() * h / w
                    && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    //
    public String selectCamera(CameraManager manager, boolean bFront) {
        String sel_id = null;

        String[] cameraIdList = new String[0];
        try {
            cameraIdList = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
        if (cameraIdList == null) return null;

        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                return null;
            }
            if (bFront) {
                if (characteristics.get(CameraCharacteristics.LENS_FACING).intValue() == CameraMetadata.LENS_FACING_FRONT) {
                    sel_id = cameraId;
                    break;
                }
            } else {
                if (characteristics.get(CameraCharacteristics.LENS_FACING).intValue() == CameraMetadata.LENS_FACING_BACK) {
                    sel_id = cameraId;
                    break;
                }
            }
        }
        if (sel_id == null) {
            if (cameraIdList.length > 0) {
                sel_id = cameraIdList[0];
            }
        }

        return sel_id;
    }


    //
    private void updateControls() {

    }

    public List<qnmCommProc_mis.IpcInfo> ipcInfoList = new ArrayList<>();

    void showNvrList() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        int i;
        qnmCommProc_mis.NvrInfo nvrInfo = pProcInfo.m_ipcProc.op.nvrInfo;

        ipcInfoList.clear();
        if (nvrInfo.usCnt != 0) {
            initPopWindows();


            for (i = 0; i < nvrInfo.usCnt; i++) {
                qnmCommProc_mis.IpcInfo pMem = nvrInfo.mems[i];
                ipcInfoList.add(pMem);
            }
            if (nvrInfo.usCnt >= 1) {
                nvr_channel_name.setVisibility(View.VISIBLE);

                qnmCommProc_mis.IpcInfo pMem = new qnmCommProc_mis.IpcInfo();
                pMem.iChannel = -1;
                pMem.name = "切换布局";//, 128);
                ipcInfoList.add(0, pMem);

                nvrListAdapter.notifyDataSetChanged();
                popupWindow.showAsDropDown(nvr_channel_name);
            }
            nvr_channel_name.setCompoundDrawables(null, null, pull_downDrawable, null);
        }
    }

    public void initPopWindows() {

        View view = LayoutInflater.from(mOwner_wnd).inflate(R.layout.popwin_layout, null);
        RecyclerView popListView = view.findViewById(R.id.pop_list_view);

        popListView.addItemDecoration(new DividerItemDecoration(mOwner_wnd, 1));
        nvrListAdapter = new NvrListAdapter(mOwner_wnd, ipcInfoList);
        popListView.setLayoutManager(new LinearLayoutManager(mOwner_wnd));
        popListView.setAdapter(nvrListAdapter);
        popupWindow = new PopupWindow(view, nvr_channel_name.getWidth(), mOwner_wnd.getWindow().getDecorView().getHeight() / 4, true);
        popupWindow.setAnimationStyle(R.anim.anim_pop);
        //popupWindow.Touchable = true;
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
//        popupWindow.setTouchInterceptor((v, event) -> true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
        popupWindow.setOnDismissListener(this);
    }

    public static class NvrListAdapter extends RecyclerView.Adapter {
        private final Activity_dlgTalk_av mOwner_wnd;
        private final List<qnmCommProc_mis.IpcInfo> ipcInfoList;

        public NvrListAdapter(Activity_dlgTalk_av mOwner_wnd, List<qnmCommProc_mis.IpcInfo> ipcInfoList) {
            this.ipcInfoList = ipcInfoList;
            this.mOwner_wnd = mOwner_wnd;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(mOwner_wnd).inflate(R.layout.popwin_item, parent, false);
            return new CloudViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder");

            qnmCommProc_mis.IpcInfo ipcInfo = ipcInfoList.get(position);

            String nvrName = ipcInfo.name;

            ((CloudViewHolder) holder).nvr_name_tv.setText(nvrName);

            Log.d("nvr_name_tv.Click");
            ((CloudViewHolder) holder).nvr_name_tv.setOnClickListener(v -> {
                qnmCommProc_mis.IpcInfo ipcInfo1 = ipcInfoList.get(position);
                int clickItemChannel = ipcInfo1.iChannel;

                Log.d(position + "position");

                mOwner_wnd.m_fg_dlgTalk_av2.sendCurrentQiuJIEnableControl(clickItemChannel);
                if (nvrName.contains("(球机)")) {
                    mOwner_wnd.m_fg_dlgTalk_av2.showOperationRelativeLayout(true);
                } else {
                    mOwner_wnd.m_fg_dlgTalk_av2.showOperationRelativeLayout(false);
                    mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(View.INVISIBLE);
                }
                mOwner_wnd.m_fg_dlgTalk_av2.popupWindow.dismiss();
                mOwner_wnd.m_fg_dlgTalk_av2.nvr_channel_name.setText(nvrName);
            });
        }

        @Override
        public int getItemCount() {
            return ipcInfoList.size();
        }
    }

    public void sendCurrentQiuJIEnableControl(int clickItemChannel) {
        //组名
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.addr.idInfo.ui64Id;

        // 我
        qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
        idInfo_requester.ui64Id = 0;

        //mcu 的
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        idInfo_to.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.av.taskInfo.idInfo_starter.ui64Id;


        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;


        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
        layoutParam.enlargeParam.usEnlargeType = CONST_enlargeType_img;

        layoutParam.enlargeParam.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;
        layoutParam.ipcParam.iChannel = clickItemChannel;

        mOwner_wnd.mOwner_hTalk.get().m_var.currentSelectQiuJiChannel = clickItemChannel;

        dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(mOwner_wnd.mOwner_hTalk.get(), (byte) 0, (short) 0, (short) CONST_imOp_enlargeChannel, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
        Log.d("##球机sendConfLayout sendCurrentQiuJIEnableControl:" + clickItemChannel);
    }

    public void showOperationRelativeLayout(boolean show) {
        if (show) {
            nvr_control.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_text_color));
            nvr_control.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_active_color));
            mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(View.VISIBLE);
            mOwner_wnd.m_fg_dlgTalk_av2.left_menu_relative.setVisibility(View.VISIBLE);
            mOwner_wnd.m_fg_dlgTalk_av2.right_menu_relative.setVisibility(View.VISIBLE);
        } else {
            //  mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(android.view.View.INVISIBLE);
            nvr_control.setBackgroundColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_color));
            nvr_control.setTextColor(mOwner_wnd.getResources().getColor(R.color.av_button_normal_text_color));
            mOwner_wnd.m_fg_dlgTalk_av2.left_menu_relative.setVisibility(android.view.View.INVISIBLE);
            mOwner_wnd.m_fg_dlgTalk_av2.right_menu_relative.setVisibility(android.view.View.INVISIBLE);
        }
    }

    public static class CloudViewHolder extends RecyclerView.ViewHolder {
        public TextView nvr_name_tv;

        public CloudViewHolder(View itemView) {
            super(itemView);
            nvr_name_tv = itemView.findViewById(R.id.nvr_name_tv);
        }
    }

    public void dlg_YTBtn_remote(int nResourceID, byte ucMouseStatus) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        int uiObjType = 0;
        short usIndex_obj = 0;
        short usHelp_subIndex = 0;
        //
        byte ucCmd = 0;
        byte ucParam = 0;

        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;

        int iChannel = mOwner_wnd.mOwner_hTalk.get().m_var.currentSelectQiuJiChannel;
        Log.d("##球机dlg_YTBtn_remote" + iChannel);
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.addr.idInfo.ui64Id;
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        if (pProcInfo.m_ipcProc.op.nvrInfo.ui64Id == 0) {
            idInfo_to.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_var.currentSelectQiuJiIdInfo;
        } else {
            idInfo_to.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;
        }

        do {
            switch (nResourceID) {
                case IDC_BUTTON_UP:
                    ucCmd = CONST_ptzCmd_up;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_DOWN:
                    ucCmd = CONST_ptzCmd_down;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_LEFT:
                    ucCmd = CONST_ptzCmd_left;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_RIGHT:
                    ucCmd = CONST_ptzCmd_right;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_ZOOM_IN:
                    ucCmd = CONST_ptzCmd_zoomIn;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_ZOOM_OUT:
                    ucCmd = CONST_ptzCmd_zoomOut;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, idInfo_imGrp_related, idInfo_to);
                    break;
                default:
                    break;
            }
        } while (false);
    }

    public int sendPtzReq(int uiObjType, short usIndex_obj, short usHelp_subIndex, qyDefs.QY_MESSENGER_ID idInfo, int iChannel, byte ucCmd, byte ucParam, byte ucMouseStatus, qyDefs.QY_MESSENGER_ID idInfo_imGrp_related, qyDefs.QY_MESSENGER_ID idInfo_to) {
        Log.d(uiObjType + " eeeee  " + usIndex_obj + "   " + usHelp_subIndex + "   " + idInfo.ui64Id + "   " + iChannel + "   " + ucCmd + "   " + ucParam + "   " + ucMouseStatus + "   " + idInfo_imGrp_related.ui64Id + "   " + idInfo_to.ui64Id);


        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        int uiChannelType = CONST_channelType_media;

        qnmCommProc_mis.PTZ_control_cmd req = new qnmCommProc_mis.PTZ_control_cmd();
        req.uiType = imCommType_defs.Consts.CONST_imCommType_ptzControlCmd;
        req.idInfo_imGrp_related = idInfo_imGrp_related;
        req.cmdInfo.obj.uiObjType = uiObjType;
        req.cmdInfo.obj.usIndex_obj = usIndex_obj;
        req.cmdInfo.obj.usHelp_subIndex = usHelp_subIndex;
        req.cmdInfo.idInfo = idInfo;
        req.cmdInfo.iChannel = iChannel;
        req.cmdInfo.ucCmd = ucCmd;
        req.cmdInfo.ucParam = ucParam;
        req.cmdInfo.ucMouseStatus = ucMouseStatus;
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
//        byte[] data = req.toByteArray();
        int len = 0;//(uint)sizeof(PTZ_control_cmd)
        if (0 != postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, (qnmCommProc_mis.MSG_ROUTE) null, qnmCommProc_mis.Consts.CONST_misMsgType_outputTask, (byte) 0, (short) qnmCommProc.Consts.CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, req, len, 0, idInfo_to.ui64Id, (int) uiChannelType, null, false))
            ;

        iErr = 0;
        return iErr;
    }

    public void onDismiss() {
        nvr_channel_name.setCompoundDrawables(null, null, pull_upDrawable, null);
    }

    public int currentVideoStatus = 0;

    void changeCurrentVideoStatus(int bAmplification) {

        Log.d("收到窗口状态:" + bAmplification);
        currentVideoStatus = bAmplification;
        if (bAmplification != 0)//response 画面放大大于0  缩小=0
        {

            min_viewport.setVisibility(android.view.View.VISIBLE);

        } else {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

            nvr_channel_name.setVisibility(android.view.View.INVISIBLE);
            nvr_channel_name.setText(mOwner_wnd.getString(R.string.nvr_channel_name));
            min_viewport.setVisibility(android.view.View.INVISIBLE);
            mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(android.view.View.INVISIBLE);
            mOwner_wnd.m_fg_dlgTalk_av2.showOperationRelativeLayout(false);
            changeNvrControlStatus(0);
        }

    }

    void initMinButtonStates() {
        if (mOwner_wnd.mOwner_hTalk.get().respVideoStatusIsenlargement != -1) {
            if (mOwner_wnd.mOwner_hTalk.get().respVideoStatusIsenlargement == qyWmComm.Consts.Video_Status_Enlargement) {
                min_viewport.setVisibility(android.view.View.VISIBLE);
            } else {
                currentVideoStatus = 0;//初始画面
                min_viewport.setVisibility(android.view.View.INVISIBLE);

            }
            mOwner_wnd.mOwner_hTalk.get().respVideoStatusIsenlargement = -1;
        }


    }

    public void changeNvrControlStatus(int qiujiChannel) {
        Log.d(" changeNvrControlStatus:" + qiujiChannel);

        if (qiujiChannel >= 1) {
            nvr_control.setVisibility(android.view.View.VISIBLE);
        } else {
            nvr_control.setVisibility(android.view.View.GONE);
            showOperationRelativeLayout(false);
        }
    }

    public class MainHandler extends Handler {
        private final static int MSG_SEND_CAMERA_PARAMS0 = 0;
        private final static int MSG_SEND_CAMERA_PARAMS1 = 1;
        private final static int MSG_SEND_RECT_SIZE = 2;
        private final static int MSG_SEND_ZOOM_AREA = 3;
        private final static int MSG_SEND_ROTATE_DEG = 4;
        //
        private final static int MSG_talkDraw_needRestart = 10;

        //private WeakReference<TextureFromCameraActivity> mWeakActivity;
        Fragment_dlgTalk_av2 m_fg;

        public MainHandler(Fragment_dlgTalk_av2 fg) {
            //mWeakActivity = new WeakReference<TextureFromCameraActivity>(activity);
            m_fg = fg;
        }

        /**
         * Sends the updated camera parameters to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendCameraParams(int width, int height, float fps) {
            // The right way to do this is to bundle them up into an Object.  The lazy
            // way is to send two messages.
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS0, width, height));
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS1, (int) (fps * 1000), 0));
        }

        /**
         * Sends the updated rect size to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRectSize(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_RECT_SIZE, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendZoomArea(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_ZOOM_AREA, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRotateDeg(int rot) {
            sendMessage(obtainMessage(MSG_SEND_ROTATE_DEG, rot, 0));
        }

        //
        public void send_MSG_talkDraw_needRestart() {
            sendMessage(obtainMessage(MSG_talkDraw_needRestart, 0, 0));
        }


        @Override
        public void handleMessage(Message msg) {
            Fragment_dlgTalk_av2 activity = m_fg;
            if (activity == null) {
                //Log.d( "Got message for dead activity");
                return;
            }

            Log.d("Fragment_dlgTalk_av2: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            switch (msg.what) {
                case MSG_SEND_CAMERA_PARAMS0: {
                    activity.mCameraPreviewWidth_unused = msg.arg1;
                    activity.mCameraPreviewHeight_unused = msg.arg2;
                    break;
                }
                case MSG_SEND_CAMERA_PARAMS1: {
                    activity.mCameraPreviewFps_unused = msg.arg1 / 1000.0f;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_RECT_SIZE: {
                    activity.mRectWidth = msg.arg1;
                    activity.mRectHeight = msg.arg2;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_ZOOM_AREA: {
                    activity.mZoomWidth = msg.arg1;
                    activity.mZoomHeight = msg.arg2;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_ROTATE_DEG: {
                    activity.mRotateDeg = msg.arg1;
                    activity.updateControls();
                    break;
                }
                case MSG_talkDraw_needRestart: {
                    Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) activity.getActivity();
                    if (dlgTalk_av != null) {
                        if (dlgTalk_av.m_bTalkDraw_needRestart) {
                            dlgTalk_av.pauseTaskAv("fg_dlgTalk_av2.Handler, MSG_talkDraw_needRestart");
                            //
                            dlgTalk_av.m_bDlgTalk_av_readyFor_talkDrawRestart = true;
                        }
                    }
                }
                break;
                default:
                    Log.e("Unknown message " + msg.what);
            }
        }
    }
}




