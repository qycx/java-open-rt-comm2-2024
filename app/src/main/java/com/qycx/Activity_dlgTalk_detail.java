package com.qycx;


import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.guiShare;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;

//    [Activity(Label = "Activity_dlgTalk_detail")]
public class Activity_dlgTalk_detail extends qmcBaseActivity {
    //
    public Activity_dlgTalk mOwner_hTalk;

    //
    public Fragment_dlgTalk_msgr_detail m_fg_dlgTalk_msgr_detail;
    public Fragment_dlgTalk_imGrp_detail m_fg_dlgTalk_imGrp_detail;


    //
    //
    public MyHandler mHandler;


    //
    public class MyHandler extends Handler {
        Activity_dlgTalk_detail mOwner;

        //
        public MyHandler(Activity_dlgTalk_detail owner) {
            mOwner = owner;
        }

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);

            switch (msg.what) {
                case CONST_qyWm_postComm:

                    //
                    switch (msg.arg1) {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your application here

        //
        this.mHandler = new MyHandler(this);

        int iErr = -1;
        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        //
        Intent intent = getIntent();
        long ui64Id =  intent.getLongExtra("ui64Id", 0);
        //
        qyDefs.QY_MESSENGER_ID idInfo=new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = ui64Id;
        Activity_dlgTalk hTalk = null;
        if (null == (hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo))) {
            if (0 != iErr)
            {
                doFinish("dlgTalk_av.OnCreate failed");
            }
            return;
        }
        mOwner_hTalk = hTalk;
        dlgTalkProc.DLG_TALK_var pMgrVar = mOwner_hTalk.m_var;

        setContentView(R.layout.layout_activity_dlgtalk_detail);

        if (pMgrVar.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            m_fg_dlgTalk_imGrp_detail = new Fragment_dlgTalk_imGrp_detail();
        } else {
            m_fg_dlgTalk_msgr_detail = new Fragment_dlgTalk_msgr_detail();
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //
        if (m_fg_dlgTalk_imGrp_detail != null) {
            ft.replace(R.id.container, m_fg_dlgTalk_imGrp_detail);
        }
        if (m_fg_dlgTalk_msgr_detail != null) {
            ft.replace(R.id.container, m_fg_dlgTalk_msgr_detail);
        }
        //
        ft.commit();

        //
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        iErr = 0;
        
        if (0 != iErr) {
            doFinish("dlgTalk_av.OnCreate failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            //   mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}

