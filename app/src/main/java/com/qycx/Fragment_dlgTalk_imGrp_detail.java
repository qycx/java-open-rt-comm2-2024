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
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;

public class Fragment_dlgTalk_imGrp_detail extends Fragment {
    Activity_dlgTalk_detail mOwner;
    //
    TextView tv_back;
    TextView tv_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOwner = (Activity_dlgTalk_detail) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_dlgtalk_imgrp_detail, container, false);

        tv_back = (TextView) view.findViewById(R.id.txt_back);
        tv_back.setOnClickListener(Tv_back_Click);

        tv_title = (TextView) view.findViewById(R.id.txt_title);

        Activity_dlgTalk_detail owner = (Activity_dlgTalk_detail) getActivity();
        Activity_dlgTalk hTalk = owner.mOwner_hTalk;

        tv_title.setText(hTalk.m_str_title);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        myDb.QM_dbFuncs g_dbFuncs = pQyMc.p_g_dbFuncs;
        Object pDb = pQyMc.gui.pDb;
        int iDbType = pQyMc.cfg.db.iDbType;


        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        //
        TextView tv_staticGreatorIdInfo = (TextView) view.findViewById(R.id.staticCreatorIdInfo);
        tv_staticGreatorIdInfo.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_staticCreatorIdInfo));


        //
        qnmCommProc_mis.IM_GRP_INFO grpInfo=new qnmCommProc_mis.IM_GRP_INFO();
        qyDefs.QY_MESSENGER_ID idInfo = hTalk.m_var.addr.idInfo;

        //  再在qyImGrpInfoTab留下组名
        if (!g_dbFuncs.pf_bGetImGrpInfoBySth.call(pDb, iDbType, "", idInfo, grpInfo)) {
//            qyFuncs.mymemset((Object) (byte) grpInfo, 0, sizeof(IM_GRP_INFO));
        }

        TextView tv_idInfo = (TextView) view.findViewById(R.id.idInfo);
        tv_idInfo.setText(String.valueOf(idInfo.ui64Id));

        TextView tv_creatorIdInfo = (TextView) view.findViewById(R.id.creatorIdInfo);
        tv_creatorIdInfo.setText(String.valueOf(grpInfo.idInfo_creator.ui64Id));

        return view;
    }


    private android.view.View.OnClickListener Tv_back_Click = (View view) ->
    {
        mOwner.onBackPressed();
    };
}
