package com.qycx;


import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.qycx.base.TableItem;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.genericQueue;
import com.qycx.base.guiShare;
import com.qycx.base.imObj_proc;
import com.qycx.base.isCliHelpPublic;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.showPopupMenu;
import com.qycx.base.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Fragment_imObjList extends Fragment {
    MainActivity mainWnd;
    ListView listView;
    TextView tv_topMenu;
    TextView m_tv_txtTitle;

    public List<TableItem.TableItem_imObj> mTableItems_imObj = new ArrayList<>();


    int th_fillList_imObj(imObj_proc.QMem_imObj_intQ pMem) {
        int iErr = -1;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", pMem.idInfo, 0, msgrInfo, regInfo, null, null)) {
            Log.e("th_fillList_imObj pf_bGetMessengerInfoBySth failed");
            return iErr;
        }

        TableItem.TableItem_imObj item = new TableItem.TableItem_imObj();
        item.ui64Id = msgrInfo.idInfo.ui64Id;
        item.uiObjType = msgrInfo.uiType;

        if (msgrInfo.uiType == qyDefs.Consts.CONST_objType_imGrp) {
            qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
            if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "", msgrInfo.idInfo, grpInfo)) {
                Log.e("th_fillList_imObj pf_bGetImGrpInfoBySth failed");
            }
            item.grpName = grpInfo.name;
            Log.i(item.grpName);

            item.ImageResourceId = grpInfo.idInfo_creator.ui64Id == 0 ? R.drawable.grp : R.drawable.tmpgrp;
        } else {
            item.dw = regInfo.dw;
            item.bm = regInfo.bm;
            item.syr = regInfo.syr;
            item.ImageResourceId = R.drawable.contact;
        }
        mTableItems_imObj.add(item);

        iErr = 0;
        return iErr;
    }

    public int fillList_imObj() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        imObj_proc.QMem_imObj_intQ mem;
        while (pMisCnt.imObj_intQ.uiQNodes > 0){
//            Log.i("fillList_imObj: "+pMisCnt.imObj_intQ.uiQNodes);
            mem = (imObj_proc.QMem_imObj_intQ) genericQueue.qyFuncs.qGetMsg(pMisCnt.imObj_intQ);
            if (mem == null) {
                Log.i("get QMem_imObj_intQ finish");
                break;
            }
            th_fillList_imObj(mem);
        }
        return 0;
    }


    //
    public int display_imObjList() {
        if (listView != null) {
            Adapter_imObjList adapter = (Adapter_imObjList) listView.getAdapter();
            adapter.notifyDataSetChanged();
        }
        return 0;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainWnd = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_imobjlist, container, false);
        tv_topMenu = view.findViewById(R.id.txt_topMenu);
        tv_topMenu.setOnClickListener(Tv_topMenu_Click);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return view;

        m_tv_txtTitle = view.findViewById(R.id.txt_title);
        m_tv_txtTitle.setText(mainWnd.m_str_title);

        listView = view.findViewById(R.id.list);
        listView.setAdapter(new Adapter_imObjList(inflater, mTableItems_imObj));
        listView.setOnItemClickListener(ListView_ItemClick);
        return view;
    }

    showPopupMenu.qyFuncs_Droid.PF_procMenu procMenu = (Context context, int menuId) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (menuId == R.id.quit) {
            guiShare.GuiShare.pf_PostMessage_new.get().call(pQyMc.gui.hMainWnd, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        }
        return 0;
    };

    //
    public int gui_onTimer(Object p0, Object p1, Object p2) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainWnd = (MainActivity) getActivity();
        if (mainWnd == null) return 0;

        if (pProcInfo.av.talk.bChannelReady) {
            m_tv_txtTitle.setText(mainWnd.m_str_title);
        } else {
            m_tv_txtTitle.setText(mainWnd.m_str_title + "(离线)");
        }
        return 0;
    }


    //
    private View.OnClickListener Tv_topMenu_Click = (View view) -> {
        Context context = getActivity();
        showPopupMenu.qyFuncs_Droid.showPopupMenu(context, view, R.menu.layout_imobjlist_topmenu, procMenu);
    };

    private AdapterView.OnItemClickListener ListView_ItemClick = (AdapterView<?> parent, View view, int position, long id) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        {
            if (mTableItems_imObj.size() <= position) {
                return;
            }
            Log.i(mTableItems_imObj.get(position).ui64Id + " clicked ");

            TableItem.TableItem_imObj item_sel = mTableItems_imObj.get(position);

//            Log.d("这里应该提示一下，如果有视频任务在进行，就不能激活新窗口");

            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = item_sel.ui64Id;

            qnmCommProc_mis.MSGR_ADDR addr = new qnmCommProc_mis.MSGR_ADDR();
            addr.idInfo.ui64Id = item_sel.ui64Id;
            isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();

            dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, addr, false, true, param);
        }
    };
}
