package com.qycx;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.isProtocolFunc;
import com.qycx.base.myDb;
import com.qycx.base.mySaveSecret;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qwmMd5;
import com.qycx.base.qyCommonFunc9;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyCustom;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMsgFunc;
import com.qycx.base.qyShowInfoCommProc;
import com.qycx.base.qySyncCommProc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showPopupMenu;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;
import com.qycx.base.util.VersionUtils;

import java.lang.ref.WeakReference;

public class Fragment_me extends Fragment {
//    private static final String TAG = Fragment_me.class.getSimpleName();
    boolean m_bInited;
    //
    WeakReference<TextView> tv_topMenu;
    //
    TextView tv_staticUsrName;
    //   TextView tv_dwmc;
    TextView tv_usrName;
    TextView tv_idInfo;
    TextView tv_staticIo;
    TextView tv_io;
    TextView tv_staticServer;
    TextView tv_server;
    //
    TextView tv_staticStatusServer;
    EditText edt_statusServer;
    TextView btn_apply;
    TextView tv_statusQInfo;
    TextView tv_ver;
    TextView tv_staticSupport;
    TextView tv_support;
    CheckBox use100k;
    EditText pw_edit;
    AlertDialog m_alert;
    //
    boolean m_bUpdated_afterLogon;
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_fg_me, container, false);
        sp = inflater.getContext().getSharedPreferences("config",MODE_PRIVATE);
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        String str;

        tv_topMenu = new WeakReference<>(view.findViewById(R.id.txt_topMenu));
        tv_topMenu.get().setOnClickListener(Tv_topMenu_Click);

        TextView tv_txtTitle = view.findViewById(R.id.txt_title);
        tv_txtTitle.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_appName));
   //     SharedPreferences sp = getContext().getSharedPreferences("SP", MODE_PRIVATE);
//存入数据


        tv_usrName = view.findViewById(R.id.usrName);
        tv_idInfo = view.findViewById(R.id.idInfo);
        // tv_staticIo = view.findViewById(R.id.staticIo);
        tv_io = view.findViewById(R.id.io);
        //   tv_staticServer = view.findViewById(R.id.staticServer);
        tv_server = view.findViewById(R.id.server);
        //
        //   tv_staticStatusServer = view.findViewById(R.id.staticStatusServer);
        edt_statusServer = (EditText) view.findViewById(R.id.edt_statusServer);
        btn_apply = view.findViewById(R.id.btn_apply);
        tv_statusQInfo = view.findViewById(R.id.statusQInfo);

        btn_apply.setOnClickListener(Btn_apply_Click);
        btn_apply.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_apply));
        TextView change_password = view.findViewById(R.id.change_password);
        pw_edit = (EditText) view.findViewById(R.id.pw_edit);

        change_password.setOnClickListener(v -> {
            String new_pw = pw_edit.getText().toString();
            if (new_pw == null || new_pw.isEmpty()) {
//                Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                ToastUtils.showToast("密码不能为空");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view1 = getLayoutInflater().inflate(R.layout.layout_yescancel, null);
                builder.setView(view1);
                builder.setCancelable(false);
                m_alert = builder.create();
                m_alert.show();
                Window wnd = m_alert.getWindow();
                wnd.setGravity(Gravity.CENTER);
                TextView tvTitle = view1.findViewById(R.id.title);
                tvTitle.setText("清扬视频");
                TextView tvContent = view1.findViewById(R.id.content);
                tvContent.setText("确定要修改密码吗？");
                Button btnYes = (Button) view1.findViewById(R.id.yes);
                btnYes.setText("确定");
                btnYes.setOnClickListener(BtnYes_Click);
                Button btnCancel = (Button) view1.findViewById(R.id.cancel);
                btnCancel.setText("取消");
                btnCancel.setOnClickListener(BtnCancel_Click);
            }
        });

        //
        // tv_dwmc.setText( "";
        tv_usrName.setText("");

        //
//        String statusServer = qyShowInfoCommProc.qyFuncs.showInfo_get_cfgServer("");
        String statusServer = sp.getString("statusServer", "10.0.1.108");
        edt_statusServer.setText(statusServer);

        tv_statusQInfo.setText("");

        String verBuf = qyCustom.qyFuncs.qnmVerStr(pQyMc.iServiceId);
        boolean bDebug = false;

        int iVer = Integer.parseInt(verBuf);
        String versionName = VersionUtils.getVersionName(getContext());
        str = String.format("V%d.%d.%d.%d %s", iVer / 1000000, (iVer / 10000) % 100, (  iVer  /  100  )  %  100,   iVer  %  100,  bDebug ? "Debug" : "");

        //
        tv_ver = view.findViewById(R.id.ver);
        use100k = view.findViewById(R.id.use100k);
        use100k.setChecked(pProcInfo.bUse100k);

        //
        use100k.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pProcInfo.bUse100k = true;
                    ToastUtils.showToast("视频出口带宽限制为100k");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("USE100k_KEY", true);
                    editor.commit();
                } else {
                    pProcInfo.bUse100k = false;
                    ToastUtils.showToast("视频出口带宽不限制");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("USE100k_KEY", false);
                    editor.commit();
                }
            }
        });


        tv_ver.setText(str);

        //   tv_staticSupport = view.findViewById(R.id.staticSupport);
        //
        //    tv_staticSupport.setText( "支持网站:";
        //
        //   tv_support = view.findViewById(R.id.support);
        //  tv_support.setText( "www.qycx.com";

        //
        updateCtrls();
        m_bInited = true;
        return view;
    }


    //
    private View.OnClickListener BtnYes_Click = (View view) -> {
        m_alert.dismiss();
        String new_pw = pw_edit.getText().toString();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        String str;
        qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
        {
            qnmCommProc_mis.IM_L_STREAM pStream = new qnmCommProc_mis.IM_L_STREAM();//(IM_L_STREAM) mallocObj.mallocf(sizeof(IM_L_STREAM));
            if (pStream == null) {
                Log.d( "sndDevInfo_qmc . malloc failed.");
            }

            qnmCommProc_mis.QY_MESSENGER_ACCOUNT reqPw = new qnmCommProc_mis.QY_MESSENGER_ACCOUNT();
            qyCommonFunc9.TMP_buf256 buf = new qyCommonFunc9.TMP_buf256();

//            qyFuncs.mymemset((Object) ( byte)reqPw, 0, sizeof(QY_MESSENGER_ACCOUNT));

            //  memset(&req, 0, sizeof(req));
            reqPw.uiType = imCommType_defs.Consts.CONST_imCommType_messengerAccount;
            int len;
            //我要做的：

            len = qnmCommProc_mis.Consts.CONST_maxMessengerPasswdLen + 1;
            reqPw.messengerPasswd = qwmMd5.qyFuncs.bQyGetStrMd5_x(new_pw);


            pStream.uiType = imCommType_defs.Consts.CONST_imCommType_lStream;
            pStream.uiContentType = (int) reqPw.uiType;
            len = qnmCommProc_mis.Consts.bufSize_imLStream + 1;


            len = isProtocolFunc.qyFuncs.messengerAccount2Stream((int) pStream.uiContentType, reqPw, pStream.buf);
            if (len > 0);
            pStream.ulStreamLen = (int) len;
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
//            byte[] data = pStream.toByteArray();
            postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(null, pMisCnt, null, qnmCommProc_mis.Consts.CONST_misMsgType_req, (byte) 0, qnmCommProc.Consts.CONST_qyCmd_refreshImObjListReq, tStartTran, uiTranNo, (short) 0,pStream, 0, 0, 0, 0, null, false);
        }
    };

    private View.OnClickListener BtnCancel_Click = (View view) -> {
        m_alert.dismiss();
    };

    showPopupMenu.qyFuncs_Droid.PF_procMenu procMenu = (Context context, int menuId) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (menuId == R.id.quit) {
            guiShare.GuiShare.pf_PostMessage_new.get().call(pQyMc.gui.hMainWnd, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        }
        return 0;
    };

    private View.OnClickListener Tv_topMenu_Click = (View view) -> {
        Context context = getActivity();
        showPopupMenu.qyFuncs_Droid.showPopupMenu(context, view, R.menu.layout_msglist_topmenu, procMenu);
    };

    //
    int updateCtrls() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        String str;

        if (pMisCnt == null) return -1;
        if (pDbFuncs == null) return -1;

        if (pQyMc == null) return -1;

        if (null == pQyMc.gui.pDb) {
            Log.d( "pQyMc.gui.pDb  null");
            return -1;
        }

        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();

        qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;
        
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, msgrInfo, regInfo, null, null)) {
            return iErr;
        }
        
        tv_usrName.setText(regInfo.dw +" "+  regInfo.bm +" "+ regInfo.syr);
        tv_idInfo.setText(String.valueOf(idInfo.ui64Id));
        tv_server.setText(pMisCnt.server.ip +":"+ pMisCnt.server.port);
        
        if (!this.m_bUpdated_afterLogon) {
            if (pProcInfo.status.retrieveImObjList.ulbAllContactsRetrieved) {
                this.m_bUpdated_afterLogon = true;
            }
        }
        
        iErr = 0;
        return iErr;
    }


    private View.OnClickListener Btn_apply_Click = (View view) -> {
        String statusServer = edt_statusServer.getText().toString();
        mySaveSecret.qyFuncs_Droid.saveDbgCfg(statusServer);
        qyShowInfoCommProc.qyFuncs.showInfo_cfgServer(statusServer);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("statusServer",statusServer);
        edit.apply();
    };

    //
    public int gui_onTimer(Object p0, Object p1, Object p2) {
        if (!m_bInited) return -1;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        String status = "";

        //
        if (!this.m_bUpdated_afterLogon) {
            updateCtrls();
        }
        String strOut = String.format("↑ %d Kbps", pProcInfo.status.netStat.ins.uiOutSpeedInKbps);
        String strIn = String.format("↓ %d Kbps", pProcInfo.status.netStat.ins.uiInSpeedInKbps);

        tv_io.setText(strOut + "  " + strIn);

        qyShowInfoCommProc.qyFuncs.showInfo_getStatus(status);

        tv_statusQInfo.setText(status);
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d( "Fragment_me  OnDestroy");
    }
}


