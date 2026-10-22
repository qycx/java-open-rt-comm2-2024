package com.qycx;


import static com.qycx.base.qyWmComm.Consts.CONST_do_1080P_AV_Call;
import static com.qycx.base.systemDefs.Consts.model_huaWei_TAHAN00m;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qycx.base.audioRecorder_dsound;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;
import com.qycx.base.dlgTalk_videoConference;
import com.qycx.base.guiShare;
import com.qycx.base.network.NetworkChange;
import com.qycx.base.network.NetworkWatcher;
import com.qycx.base.network.Network;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qmcTransform;
import com.qycx.base.qmcTransform_start;
import com.qycx.base.qmcVideoCapture;
import com.qycx.base.qmcVideoFunc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMainProc;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showInfo_open;
import com.qycx.base.statuscenter.MeetingStatusCenter;
import com.qycx.base.systemDefs;
import com.qycx.base.taskAv;
import com.qycx.base.taskCommon;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;
import java.util.Observable;

import me.jessyan.autosize.AutoSizeCompat;

//    [Activity(Label = "Activity_dlgTalk_av", ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.Keyboard)]
public class Activity_dlgTalk_av extends BaseActivity implements PopupWindow.OnDismissListener {

    private static String TAG = "Activity_dlgTalk_av";

    public WeakReference<Activity_dlgTalk> mOwner_hTalk;
    public boolean showBackSheetDialog = true;
    public Fragment_dlgTalk_av2 m_fg_dlgTalk_av2;
    BottomSheetDialog m_bsd_askToStopAv;

    public int m_iCmd;
    public int m_Level;
    public int m_index_taskInfo1;

    //
    public long uiLastTickCnt_touch;

    //
    public MyHandler mHandler;
    public Handler bottomBarHandler = new Handler(Looper.getMainLooper());
    public Runnable runnableHideBottomBar;
    public Runnable runnableShowBottomBar;

    //
    public int tn_dlg;

    //
    public boolean m_bSurfaceTextureSizeChanged;
    public boolean menuInScreen = true;
    public boolean firstStartMenuInScreen = true;
    //
    public boolean bCmdStart;
    //
    public boolean m_bTalkDraw_needRestart;
    public boolean m_bDlgTalk_av_readyFor_talkDrawRestart;

    //
    public int m_nErrs_hTalk_av_not_matched;

    private long lastRotationTime = 0;
    private static final long MIN_ROTATION_INTERVAL = 1000;
    private MeetingStatusCenter messageCenter;
    boolean up = true;

    private NetworkWatcher watcher = new NetworkWatcher() {
        @Override
        public void update(Observable observable, Object data) {
            super.update(observable, data);
            //观察者接受到被观察者的通知，来更新自己的数据操作。
            Network network = (Network) data;
            Log.d("***************network status changed ！sigb2828 ***********************");
            if(network.isConnected()){
                //Glide.with(MainActivity.this).load("url").into(iv);
    //            iv.setText("网络连接上了");
                Log.d("***************network is connected！sigb2828 ***********************");
            }
            else{
    //            iv.setText("网络断了");
                Log.d("***************network is disconnected！sigb2828 ***********************");
            }
        }
    };

    /**
     * up 为true 则为portrait模式，为false则是landscpe模式
     */
    public void upsetPortraitOrLandscapeLayout() {
        Log.d("qy","rotate screen: " + up);
        up = !up;
        if(up){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    //
    public class MyHandler extends Handler {
        Activity_dlgTalk_av mOwner;

        //
        public MyHandler(Activity_dlgTalk_av owner) {
            mOwner = owner;
        }

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);

            Log.d("Activity_dlgTalk_av: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            switch (msg.what) {
                case qyWmComm.Consts.CONST_qyWm_postComm:

                    //
                    switch (msg.arg1) {
                        case qyWmComm.Consts.CONST_qyWmParam_closeWnd:
                            Log.d("get a msg: closeWnd. ### CONST_qyWmParam_closeWnd ");
                            Log.d("收到退出消息");
                            mOwner.m_fg_dlgTalk_av2.do_request(false);
                            Log.d("发送取消发言");
                            //
                            if (mOwner.mOwner_hTalk.get().m_var.av.bAutoCancel_lostVideo) {
                                mOwner.mOwner_hTalk.get().m_var.av.bDoneOnce_autoCancel_lostVideo = true;
                            }
                            //
                            if (mOwner.mOwner_hTalk.get().m_var.av.taskInfo.bTaskExists) {
                                //
                                this.mOwner.pauseTaskAv("dlgTalk_av.handler.HandleMessage, get a msg closeWnd");
                                //
                            }
                            Log.d("发送Activity_dlgTalk_av  clean 消息");

                            guiShare.GuiShare.pf_PostMessage_new.get().call(mOwner, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_doClean, 0);
                            break;
                        case qyWmComm.Consts.CONST_qyWmParam_doClean: {
                            //
                            showInfo_open.qyFuncs.showInfo_open("dlgTalk_av.handleMsg doClean");
                            //
                            if (this.mOwner.mOwner_hTalk.get().m_var.av.taskInfo.bTaskExists) {

                                //qyFuncs_Droid.dlgTalk_cancelTasks(mOwner_hTalk, mOwner_hTalk.m_var, mOwner_hTalk.m_var.av.taskInfo.iTaskId, true);
                                short usOp = 0;
                                if (this.mOwner.mOwner_hTalk.get().m_var.av.taskInfo.ucbStarter)
                                    usOp = qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
                                else usOp = qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
                                //
                                dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
                                if (this.mOwner.mOwner_hTalk.get().m_var.av.bAutoCancel_lostVideo) {
                                    param.bAutoCancel = true;
                                }
                                Log.d("调用 dlgTalk_procTask1 开始");
                                dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(this.mOwner.mOwner_hTalk.get(), this.mOwner.mOwner_hTalk.get().m_var.av.taskInfo.iTaskId, usOp, "", param);
                                Log.d("调用 dlgTalk_procTask1 结束");
                            }

                            Log.d("收到Activity_dlgTalk_av  doFinish 消息");
                            this.mOwner.doFinish("dlgTalk_av.doClean");

                        }
                        break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;

            }

            return;
        }

        //
    }


    //
    public qyDefs.PF_commonHandler gui_onTimer1 = (Object p0, Object p1, Object p2) -> {
        String str;

        if (m_fg_dlgTalk_av2 != null) {
            m_fg_dlgTalk_av2.gui_onTimer(p0, p1, p2);
        }
        //
        return 0;
    };

// 当用户进行任何操作时，重置计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity_dlgTalk_av  OnCreate");
        int iErr = -1;

        messageCenter = MeetingStatusCenter.getInstance();

        this.mHandler = new MyHandler(this);
        this.tn_dlg = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        //
        Intent intent = getIntent();
        int iCmd = intent.getIntExtra("iCmd", 0);
        long ui64Id = intent.getLongExtra("ui64Id", 0);
        int level = intent.getIntExtra("level", CONST_do_1080P_AV_Call);
        int index_task = intent.getIntExtra("index_qmcTaskInfo", 0);

        Log.d("ui64Id " + ui64Id);
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = ui64Id;
        Activity_dlgTalk hTalk = null;
        if (null == (hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo))) {
            Log.e("pf_findTalker return null");
            if (0 != iErr)
            {
                doFinish("dlgTalk_av.OnCreate failed");
            }
            return;
        }
        mOwner_hTalk = new WeakReference<>(hTalk);
        dlgTalkProc.DLG_TALK_var pMgrVar = mOwner_hTalk.get().m_var;
        //
        if (0 != addTo_tmpTools(pMgrVar, "dlgTalk_av.OnCreate")) {
            Log.e("addTo_tmpTools failed");
            if (0 != iErr)
            {
                doFinish("dlgTalk_av.OnCreate failed");
            }
            return;
        }

        pQyMc.fake_bFolder = false;
        //
        if (pQyMc.iManufacturer == systemDefs.Consts.manufacturer_huaWei && pQyMc.iModel == model_huaWei_TAHAN00m) {
            DisplayMetrics dm = new DisplayMetrics();

            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //
            if (0 != dm.widthPixels && 0 != dm.heightPixels) {
                float fAspect = 0.0f;
                if (dm.widthPixels > dm.heightPixels) {
                    fAspect = dm.widthPixels / (dm.heightPixels * 1.0f);
                } else {
                    fAspect = dm.heightPixels / (dm.widthPixels * 1.0f);
                }
                //
                int ii = 0;
                if (fAspect > 1.6) {
                    pQyMc.fake_bFolder = true;
                }
            }
        }

        m_iCmd = iCmd;
        m_Level = level;
        m_index_taskInfo1 = index_task;

//        AutoSizeCompat.autoConvertDensityOfGlobal(getResources());
        //  create your application here
        setContentView(R.layout.layout_activity_dlgtalk_av);
        m_fg_dlgTalk_av2 = Fragment_dlgTalk_av2.NewInstance();
        runnableHideBottomBar = () -> {
            if (null != m_fg_dlgTalk_av2 && menuInScreen) {
                m_fg_dlgTalk_av2.more_tv.setActivated(false);
                m_fg_dlgTalk_av2.more_tv.setText("更多");
                m_fg_dlgTalk_av2.more_info_menu.setVisibility(View.INVISIBLE);
                animatorHideMenu();
                m_fg_dlgTalk_av2.showOperationRelativeLayout(false);
                m_fg_dlgTalk_av2.nvr_controlSetNormal();
            }
        };
        runnableShowBottomBar = () -> {
            if (null != m_fg_dlgTalk_av2 && !menuInScreen) {
                menuInScreen = true;
                if (firstStartMenuInScreen) {
                    animatorShowMenu(0);
                } else {
                    animatorShowMenu(500);
                }
                firstStartMenuInScreen = false;

            }
        };

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (m_fg_dlgTalk_av2 != null) {
            ft.replace(R.id.container, m_fg_dlgTalk_av2);
        }
        //              
        ft.commit();

        this.m_tn_dbg = 999;
        //
        setTimer(1000, gui_onTimer1);

        m_nErrs_hTalk_av_not_matched = 0;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        NetworkChange.getInstance().addObserver(watcher);//zzq
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        MeetingStatusCenter.getInstance().registerEventBus(this);
    }

    private void animatorHideMenu() {
        if (m_fg_dlgTalk_av2.popupWindow != null) {
            m_fg_dlgTalk_av2.popupWindow.dismiss();
        }
        Log.d("bottomMenu隐藏");
        menuInScreen = false;
        ObjectAnimator animatorTopMenu = ObjectAnimator.ofFloat(m_fg_dlgTalk_av2.topMenu, "translationY", m_fg_dlgTalk_av2.topMenu.getHeight(), 0);
        animatorTopMenu.setDuration(500);    // 动画持续时间，单位是毫秒
        //
        animatorTopMenu.start();

        //
        ObjectAnimator animatorBottomMenu = ObjectAnimator.ofFloat(m_fg_dlgTalk_av2.bottomMenu, "translationY", -m_fg_dlgTalk_av2.bottomMenu.getHeight(), 0);
        animatorBottomMenu.setDuration(500);    // 动画持续时间，单位是毫秒
        animatorBottomMenu.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
        Log.e("openRtComm", "onConfigurationChanged");
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastRotationTime < MIN_ROTATION_INTERVAL) {
//            setRequestedOrientation(this.getResources().getConfiguration().orientation);
//            return;
//        }
//        lastRotationTime = currentTime;
//        AutoSizeCompat.autoConvertDensityOfGlobal(getResources());
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity_dlgTalk_av  OnResume");

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        if(pQyMc==null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        pProcInfo.av.m_bInSleepMode = false;

        //
        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().m_var;

        //
        if (m_var.m_bNeedResumeTaskAv) {
            resumeTaskAv("OnResume");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        bottomBarHandler.removeCallbacks(runnableHideBottomBar);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            Log.d("DispatchTouchEvent");
            bottomBarHandler.removeCallbacks(runnableHideBottomBar);
            bottomBarHandler.postDelayed(runnableHideBottomBar, 8000);
            if (null != m_fg_dlgTalk_av2 && !menuInScreen) {
                Log.d("bottomMenu显示");

                animatorShowMenu(500);
//                return true;
            }
        }
        uiLastTickCnt_touch = tmpFunc.qyFuncs.myGetTickCount(null);
        return super.dispatchTouchEvent(e);

    }

    public void animatorShowMenu(int animiatorDuration) {
        Log.d("bottomMenu显示");
        menuInScreen = true;
        ObjectAnimator animatorbottomMenu = ObjectAnimator.ofFloat(m_fg_dlgTalk_av2.bottomMenu, "translationY", 0, -m_fg_dlgTalk_av2.bottomMenu.getHeight());
        animatorbottomMenu.setDuration(animiatorDuration);    // 动画持续时间，单位是毫秒
        //
        animatorbottomMenu.start();

        //
        ObjectAnimator animatorTopMenu = ObjectAnimator.ofFloat(m_fg_dlgTalk_av2.topMenu, "translationY", 0, m_fg_dlgTalk_av2.topMenu.getHeight());
        animatorTopMenu.setDuration(animiatorDuration);    // 动画持续时间，单位是毫秒
        animatorTopMenu.start();
    }

    //
    int doClean(String str) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        killTimer("Act_dlgTalk_av.doClean");
        if (m_fg_dlgTalk_av2 != null) {
            m_fg_dlgTalk_av2.doClean_ifActivityIsFinishing();
        }
        return 0;
    }

    @Override
    protected void onStop() {
        /**/

        //  手工取消要清除状态
        this.mOwner_hTalk.get().m_var.av.m_bState_canSpeak = false;
        if (null != mHandler) {
            //  mHandler.removeCallbacksAndMessages(null);

        }

        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        Log.d("activity_dlgTalk_av 进入后台 发送退会消息");
        Log.d(TAG, "Activity_dlgTalk_av  OnStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("Activity_dlgTalk_av  OnPause");
        //
        String str;
        str = String.format("dlgTalk_av: onPause, %s", isFinishing() ? "isFinishing()" : "");
        Log.d(str);
        if (isFinishing()) {
            //
            doClean("OnPause,isFinishing");
        }

        //
        super.onPause();


    }

    protected void onDestroy() {
        bottomBarHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Activity_dlgTalk_av  OnDestroy");
        Log.d("dlgTalk_av.OnDestroy enters");

        //
        doClean("onDestroy");
        MeetingStatusCenter.getInstance().unRegisterEventBus(this);

        //
        if (mOwner_hTalk != null) {
            removeFrom_tmpTools(mOwner_hTalk.get().m_var, "OnDestroy");
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            Log.d("mHandler.removeCallbacksAndMessages(null);");
            mHandler = null;
        }

        Log.d("dlgTalk_av.OnDestroy leaves");
        //
        super.onDestroy();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return;
        }
        ((MainActivity) pQyMc.gui.hMainWnd).canReceiveMessage = true;
        NetworkChange.getInstance().deleteObserver(watcher);
    }

    //
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        //
        //横竖屏切换前调用，保存用户想要保存的数据，以下是样例
        outState.putString("name", "yoosir");
        int ii = 0;

        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc == null)//for once crash, zzq
            return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        pProcInfo.av.m_bInSleepMode = true;

        //
        //pauseTaskAv("OnSaveInstanceState");

    }


    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString("name");
            //
            int ii = 0;

            //
        }
    }


    /***
     * 关闭窗口！
     * @return
     */
    public int doAutoCancel() {

        dlgTalkProc.DLG_TALK_var m_var = this.mOwner_hTalk.get().get_pm_var();
        String str;

        //
        str = "dlgTalk_av.doAutoCancel, " + this.tn_dlg;
        Log.d(str);

        //  自动关闭视频窗口
        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);

        return 0;
    }


    //
    public void onBackPressed() {
        Log.d("onBackPressed" + m_fg_dlgTalk_av2.currentVideoStatus);

        if (m_fg_dlgTalk_av2.currentVideoStatus == 0) {
            //
            exitDlgTalkAv();


            // super.onBackPressed();
        } else {
            m_fg_dlgTalk_av2.resetCurrentVideoChanel();
        }

    }

    public void exitDlgTalkAv() {
        if (!showBackSheetDialog) {
            BtnYes_Click.onClick(null);
            return;
        }

        dlgTalkProc.DLG_TALK_var m_var = this.mOwner_hTalk.get().get_pm_var();
        String str;

        Log.d("exitDlgTalkAv()点击退出");

        //
        // if (m_var.av.taskInfo.bTaskExists)
        //    {
        Log.d("m_bsd_askToStopAv.setContentView(view)点击退出");

        View view = this.getLayoutInflater().inflate(R.layout.layout_yescancel, null);
        this.m_bsd_askToStopAv = new BottomSheetDialog(this);
        m_bsd_askToStopAv.setContentView(view);
        //
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText("清扬视频");
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        tvContent.setText("要停止视频通话吗？");
        //
        Button btnYes = view.findViewById(R.id.yes);
        btnYes.setOnClickListener(BtnYes_Click);
        Button btnCancel = view.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(BtnCancel_Click);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc != null) {
            btnYes.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_yes));
            btnCancel.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_cancel));
            m_bsd_askToStopAv.show();
        }
    }


    //
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //
        if (hasFocus) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            /*     View decorView = this.Window.DecorView;
                 decorView.SystemUiVisibility=(StatusBarVisibility)(
                         SystemUiFlags.Immersive
                         // Set the content to appear under the system bars so that the
                         // content doesn't resize when the system bars hide and show.
                         | SystemUiFlags.LayoutStable
                         | SystemUiFlags.LayoutHideNavigation
                         | SystemUiFlags.LayoutFullscreen
                         // Hide the nav bar and status bar
                         | SystemUiFlags.HideNavigation
                         | SystemUiFlags.Fullscreen);*/
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public void showSystemUI() {
            /*  View decorView = Window.DecorView;
              decorView.SystemUiVisibility=(StatusBarVisibility)(
                      SystemUiFlags.LayoutStable
                      | SystemUiFlags.LayoutHideNavigation
                      | SystemUiFlags.LayoutFullscreen);*/
    }


    //
    private View.OnClickListener BtnCancel_Click = (View view) -> {
        m_bsd_askToStopAv.dismiss();
    };

    private View.OnClickListener BtnYes_Click = (View view) -> {
        //throw new NotImplementedException();

        //
        if (showBackSheetDialog) {
            m_bsd_askToStopAv.dismiss();
        }
        //  手工取消要清除状态
        this.mOwner_hTalk.get().m_var.av.m_bState_canSpeak = false;
        if (null != mHandler) {
            //  mHandler.removeCallbacksAndMessages(null);
        }
        //
        Log.d("BtnYes_Click");
        showInfo_open.qyFuncs.showInfo_open("dlgTalk_av.BtnYes_Click...AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        //
        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        Log.d("发送点击退出msg");
    };

    //
    int addTo_tmpTools(dlgTalkProc.DLG_TALK_var pMgrVar, String hint) {
        int iErr = -1;
        String str;

        if (hint == null) hint = "";

        int i;
        for (i = 0; i < pMgrVar.tmp_hTools_dlgTalk_av.length; i++) {
            if (pMgrVar.tmp_hTools_dlgTalk_av[i] == null) {
                pMgrVar.tmp_hTools_dlgTalk_av[i] = this;
                break;
            }
        }
        if (i == pMgrVar.tmp_hTools_dlgTalk_av.length) {
            str = String.format("dlgTalk_av.OnCreate failed, too many tmp_hTools");
            Log.d(str);
            //
            return iErr;
        }


        iErr = 0;

        return iErr;
    }

    int removeFrom_tmpTools(dlgTalkProc.DLG_TALK_var pMgrVar, String hint) {
        int iErr = -1;

        String str;

        if (hint == null) hint = "";

        int i;
        for (i = 0; i < pMgrVar.tmp_hTools_dlgTalk_av.length; i++) {
            if (pMgrVar.tmp_hTools_dlgTalk_av[i] == this) {
                pMgrVar.tmp_hTools_dlgTalk_av[i] = null;
                pMgrVar.tmp_share_eglContexts[i] = null;
                //
                break;
            }
        }

        //
        //mOwner_hTalk.gui_PostMessage(Consts.CONST_qyWm_postComm, Consts.CONST_qyWmParam_chkTalkDraw, 0);
        mOwner_hTalk.get().doChkTalkDraw();


        //
        iErr = 0;

        return iErr;
    }

    //
    public static int tmpHandler_pauseTaskAv(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, Object p2) {
        int iErr = -1;
        int iTaskId = (int) p1.iTaskId;
        Log.e("tmpHandler_pauseTaskAv start");
        //p1
        taskCommon.TASK_common taskMem = (taskCommon.TASK_common) p2;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskMem.m_index_taskInfo, "tmpHandler_pauseTaskAv");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        if (pTaskData.msgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_task && ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).iTaskId == iTaskId) {
            taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskMem;
            //
            Log.e("tmpHandler_pauseTaskAv stopTransformThreads 1");

            qmcTransform.qyFuncs.stopTransformThreads(pProcInfo, task.myTask, "tmpHandler_pauseTaskAv");
            Log.e("tmpHandler_pauseTaskAv stopTransformThreads 2");

            iErr = 1;
            return iErr;

        }

        iErr = 0;


        return iErr;
    }


    //
    public int pauseTaskAv(String hint) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        String str;

        //
        if (hint == null) hint = "";

        //
        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().m_var;
        Object hTalk = mOwner_hTalk;
        int iWndContentType = qyWndContentType.Consts.CONST_qyWndContentType_talker;

        if (!m_var.av.taskInfo.bTaskExists) {
            Log.d("pauseTaskAv returns when no meeting");
            return -1;
        }
        //
        if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
            if (pSharedObj == null) return iErr;
            if (pSharedObj.var.bCapDev) {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                if (pCapBmp == null) return iErr;

                qmcVideoCapture.qyFuncs.stopCapture(hTalk, iWndContentType, (qmcSharedObj.CAP_procInfo_video) pCapBmp);
                qmcVideoCapture.qyFuncs.closeLocalVideo(hTalk, iWndContentType, m_var.av.iIndex_sharedObj_localAv);

            }

            // audio
            pProcInfo.stopLocalAudioRecorder(m_var.av.iIndex_sharedObj_localAv, 100);

        }

        Log.e("pauseTaskAv--start");
        qnmCommProc_mis.MIS_MSGU p0 = new qnmCommProc_mis.MIS_MSGU();
        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
        param.iTaskId = m_var.av.taskInfo.iTaskId;
        pProcInfo.processQ_media.qTraverse_mono(null, Activity_dlgTalk_av::tmpHandler_pauseTaskAv, p0, param);
        m_var.m_bNeedResumeTaskAv = true;
        Log.e("pauseTaskAv--end");



        //
        iErr = 0;
        return iErr;
    }


    //
    public static int tmpHandler_resumeTaskAv(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, Object p2) {
        int iErr = -1;
        int iTaskId = (int) p1.iTaskId;
        //p1
        taskCommon.TASK_common taskMem = (taskCommon.TASK_common) p2;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskMem.m_index_taskInfo, "tmpHandler_resumeTaskAv");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        if (pTaskData.msgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_task && ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).iTaskId == iTaskId) {
            taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskMem;
            qmcTransform_start.qyFuncs.startTransformThreads(pProcInfo, task.myTask.uiTaskType, task.myTask, "tmpHandler_resumeTaskAv");
            iErr = 1;
            return iErr;

        }

        iErr = 0;


        return iErr;
    }


    public int resumeTaskAv(String hint) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().m_var;
        Activity_dlgTalk hTalk = mOwner_hTalk.get();
        int iWndContentType = qyWndContentType.Consts.CONST_qyWndContentType_talker;

        String str;

        if (hint == null) hint = "";

        m_var.m_bNeedResumeTaskAv = false;

        //
        if (!m_var.av.taskInfo.bTaskExists) {
            Log.d("resumeTaskAv returns when no meeting ");
            return -1;
        }

        //
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;
        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

        //
        pTc.shared_eglContext = m_var.m_share_eglContext;

        //
        //
        if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
            if (pSharedObj == null) return iErr;

            //
            int iIndex_sharedObjUsr = 0;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, iIndex_sharedObjUsr);
            if (null == pSharedObjUsr) return iErr;
            pSharedObjUsr.uiTranNo_openAvDev_v = 0;

            //
            if (pSharedObj.var.bCapDev) {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                if (pCapBmp == null) return iErr;
                //
                int uiCamCapType = qmcSharedObj.Consts.CONST_camCapType_android;
                qnmCommProc_mis.VIDEO_COMPRESSOR_CFG compressor = pCapBmp.compressVideo.compressor;
                compressor.shared_eglContext = pTc.shared_eglContext;

                if (0 != qmcVideoCapture.qyFuncs.openLocalVideo(pProcInfo, uiCamCapType, "", hTalk, iWndContentType, compressor, m_var.av.iIndex_sharedObj_localAv)) {
                    str = "resumeTaskAv failed, openLocalVideo failed";
                    Log.e(str);
                    return iErr;
                }
                //
                qmcVideoCapture.qyFuncs.startCapture(hTalk, iWndContentType, (qmcSharedObj.CAP_procInfo_video) pCapBmp);

                //
                qmcSharedObj.COMPRESS_VIDEO pCompressVideo = pCapBmp.compressVideo;

                if (0 != pCompressVideo.uiTranNo_openAvDev_org) {   //  
                    pSharedObjUsr.uiTranNo_openAvDev_v = (int) (pCompressVideo.uiTranNo_openAvDev_org + iIndex_sharedObjUsr); //  pSharedObj->iIndex_curUsr;
                }

                //
                m_var.av.taskInfo.local_avStream.tranInfo.video.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;
                m_var.av.taskInfo.local_avStream.tranInfo.video.compressor = pCompressVideo.compressor.common.head;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_decompress = pCompressVideo.vh_decompress;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_compress = pCompressVideo.vh_compress;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_stream = pCompressVideo.vh_stream;

                //
            }

            // audio                  
            qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = qmcSharedObj.qyFuncs.getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
            qnmCommProc_mis.AUDIO_COMPRESSOR_CFG aCompressor = pCapAudio.compressAudio.compressor;
            if (0 == audioRecorder_dsound.qyFuncs_Droid.startLocalAudioRecorder(pProcInfo.cfg.policy.audio.uiRecordType, null, "", m_var.av.iIndex_sharedObj_localAv, aCompressor)) {
            }


        }


        //
        qnmCommProc_mis.CMIS_MSGU p0 = new qnmCommProc_mis.CMIS_MSGU();
        qyMcMainProc.PARAM_findOrgMsg p1 = new qyMcMainProc.PARAM_findOrgMsg();
        p1.iTaskId = m_var.av.taskInfo.iTaskId;
        pProcInfo.processQ_media.qTraverse_mono(null, Activity_dlgTalk_av::tmpHandler_resumeTaskAv, p0.msgU, p1);


        //
        Activity_dlgTalk dlgTalk = mOwner_hTalk.get();
        boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);

        if (canSpeak) {
            //
            dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak(dlgTalk, canSpeak);

            boolean bState_canSpeak = canSpeak;

            //
            this.m_fg_dlgTalk_av2.updateRequestButtonStatus();

        }


        //
        iErr = 0;


        Log.d("resumeTaskAv leaves with " + iErr);

        //
        return iErr;
    }

    //
    public int save_dlgTalk_share_eglContext(android.opengl.EGLContext share_eglContent, String hint) {
        int iErr = -1;
        String str;

        if (hint == null) hint = "";

        //
        Activity_dlgTalk dlgTalk = null;
        Activity_dlgTalk_av act = (Activity_dlgTalk_av) this;// mOwner.getActivity();
        if (act != null) {
            dlgTalk = act.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (0 != syncObj.sync("", pMisCnt.hMutex_syncShareEglContext,"save_dlgTalk_share_eglContext.1012")) return iErr;
                int i;
                for (i = 0; i < dlgTalk.m_var.tmp_hTools_dlgTalk_av.length; i++) {
                    if (dlgTalk.m_var.tmp_hTools_dlgTalk_av[i] == this) {
                        dlgTalk.m_var.tmp_share_eglContexts[i] = share_eglContent;
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
        }
        //
        iErr = 0;


        str = String.format("save_dlgTalk_share_eglContext called, %s %s %s", (share_eglContent != null) ? "egl" : "null", (iErr == 0) ? "ok" : "failed", hint);
        Log.d(str);

        return iErr;
    }


    //
    public void changeCamera() {
        Activity_dlgTalk dlgTalk = this.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;

        //如果是竖屏切换到全屏
        if (m_var.av.taskInfo.bTaskExists) {
            //
            pauseTaskAv("changeCamera");
            //

            m_var.av.taskInfo.bUse_backCamera = !m_var.av.taskInfo.bUse_backCamera;
            //

            resumeTaskAv("changeCamera");

            if (m_var.av.taskInfo.bUse_backCamera) {
                m_fg_dlgTalk_av2.btnChangeCamera.setTextColor(getResources().getColor(R.color.av_button_active_text_color));
                m_fg_dlgTalk_av2.btnChangeCamera.setBackgroundColor(getResources().getColor(R.color.av_button_active_color));
            } else {
                m_fg_dlgTalk_av2.btnChangeCamera.setBackgroundColor(getResources().getColor(R.color.av_button_normal_color));
                m_fg_dlgTalk_av2.btnChangeCamera.setTextColor(getResources().getColor(R.color.av_button_normal_text_color));

            }
        }

        return;
    }


    //
    public void changeLocalVideo() {
        Activity_dlgTalk dlgTalk = this.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;

        //如果是竖屏切换到全屏
        if (m_var.av.taskInfo.bTaskExists) {
            m_var.av.taskInfo.bShow_localVideo = !m_var.av.taskInfo.bShow_localVideo;
        }
        if (m_var.av.taskInfo.bShow_localVideo) {
            m_fg_dlgTalk_av2.btnLocalVideo.setText("关闭本地视频");
            m_fg_dlgTalk_av2.btnLocalVideo.setTextColor(getResources().getColor(R.color.av_button_active_text_color));
            m_fg_dlgTalk_av2.btnLocalVideo.setBackgroundColor(getResources().getColor(R.color.av_button_active_color));
        } else {
            m_fg_dlgTalk_av2.btnLocalVideo.setText("本地视频");
            m_fg_dlgTalk_av2.btnLocalVideo.setBackgroundColor(getResources().getColor(R.color.av_button_normal_color));
            m_fg_dlgTalk_av2.btnLocalVideo.setTextColor(getResources().getColor(R.color.av_button_normal_text_color));
        }
    }

    //
    public static guiShare.PF_initVideoCapDev initVideoCapDev = (Object hTalk, int iWndContentType, String displayName_selected, qyCommProc_open.BITMAPINFOHEADER pBih_suggested, qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp) -> {
        if (iWndContentType != qyWndContentType.Consts.CONST_qyWndContentType_talker) return -1;
//        Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hTalk;
        Activity_dlgTalk dlgTalk;
        if (hTalk instanceof Activity_dlgTalk) {
            dlgTalk = (Activity_dlgTalk) hTalk;
        } else {
            dlgTalk = (Activity_dlgTalk) ((WeakReference<?>) hTalk).get();
        }
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;

        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return Fragment_dlgTalk_av2.initVideoCapDev(dlgTalk, iWndContentType, displayName_selected, pBih_suggested, pCapBmp);
        }
        return -1;
    };

    public static guiShare.PF_exitVideoCapDev exitVideoCapDev = (Object hTalk, int iWndContentType) -> {
        int iErr = -1;

        if (iWndContentType != qyWndContentType.Consts.CONST_qyWndContentType_talker) return -1;
        Activity_dlgTalk dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (null == dlgTalk.m_var.m_hTool_dlgTalk_av) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;

        //

        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return Fragment_dlgTalk_av2.exitVideoCapDev(hTalk, iWndContentType);
        }

        return -1;

    };

    public static guiShare.PF_bGetCapturePara bGetCapturePara = (Object hTalk, int iWndContentType, qyCommProc_open.QY_VIDEO_HEADER pVh_org) -> {
        boolean bRet = false;

        if (iWndContentType != qyWndContentType.Consts.CONST_qyWndContentType_talker) return false;
        Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hTalk;//((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return false;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;


        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return Fragment_dlgTalk_av2.bGetCapturePara(dlgTalk, iWndContentType, pVh_org);
        }

        return false;

    };

    public static guiShare.PF_startCapture startCapture = (Object hTalk, int iWndContentType) -> {
        if (iWndContentType != qyWndContentType.Consts.CONST_qyWndContentType_talker) return -1;
        Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hTalk;//((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;


        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return Fragment_dlgTalk_av2.startCapture(hTalk, iWndContentType);
        }

        return 0;
    };

    public static guiShare.PF_stopCapture stopCapture = (Object hTalk, int iWndContentType) -> {
        if (iWndContentType != qyWndContentType.Consts.CONST_qyWndContentType_talker) return -1;
        Activity_dlgTalk dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
        if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_var.m_hTool_dlgTalk_av)) return -1;
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_var.m_hTool_dlgTalk_av;

        //

        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return Fragment_dlgTalk_av2.stopCapture(hTalk, iWndContentType);
        }

        return 0;
    };

    //
    public int doTask_av(int iCmd, int iTaskId) {
        Activity_dlgTalk_av dlgTalk_av = this;
        //

        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return dlgTalk_av.m_fg_dlgTalk_av2.doTask_av(iCmd, iTaskId);
        }

        return -1;
    }

    public void onDismiss() {
        m_fg_dlgTalk_av2.onDismiss();
    }
}


