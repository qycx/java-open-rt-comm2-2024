package com.qycx;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;



public class Fragment_dlgTalk_msgr_detail extends Fragment {
    Activity_dlgTalk_detail mOwner;
    //
    TextView tv_back;
    TextView tv_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your fragment here
        mOwner = (Activity_dlgTalk_detail) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use this to return your custom view for this Fragment
        // return inflater.inflate(R.layout.YourFragment, container, false);

        //return super.onCreateView(inflater, container, savedInstanceState);

        //
        View view = inflater.inflate(R.layout.layout_fg_dlgtalk_msgr_detail, container, false);

        tv_back = (TextView) view.findViewById(R.id.txt_back);
        tv_back.setOnClickListener(Tv_back_Click);

        tv_title = (TextView) view.findViewById(R.id.txt_title);

        Activity_dlgTalk_detail owner = (Activity_dlgTalk_detail) getActivity();
        Activity_dlgTalk hTalk = owner.mOwner_hTalk;

        tv_title.setText(hTalk.m_str_title);


        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        String str;

        //
        TextView tv_staticUsrName = (TextView) view.findViewById(R.id.staticUsrName);
        tv_staticUsrName.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_staticUsrName));

        //
        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();

        qyDefs.QY_MESSENGER_ID idInfo = hTalk.m_var.addr.idInfo;
        if (!pDbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, msgrInfo, regInfo, null, null)) {
//                qyFuncs.mymemset((Object)(byte)regInfo, 0, sizeof(QY_MESSENGER_REGINFO));
        }

        TextView tv_dwmc = (TextView) view.findViewById(R.id.dwmc);
        TextView tv_usrName = (TextView) view.findViewById(R.id.usrName);
        TextView tv_idInfo = (TextView) view.findViewById(R.id.idInfo);

        //
        str = String.format("%s %s", regInfo.dw, regInfo.bm);
        tv_dwmc.setText(str);
        tv_usrName.setText(regInfo.syr);
        tv_idInfo.setText(String.valueOf(idInfo.ui64Id));

        return view;

    }

    private View.OnClickListener Tv_back_Click = (View view) ->
    {
        //throw new NotImplementedException();
        mOwner.onBackPressed();
    };
}
