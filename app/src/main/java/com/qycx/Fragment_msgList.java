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
import com.qycx.base.getTalkerDesc;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.isCliHelpPublic;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMemoryFunc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.showPopupMenu;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Fragment_msgList extends Fragment {
    //    private static final String TAG = Fragment_msgList.class.getSimpleName();
    List<TableItem.TableItem_msg> mTableItems = new ArrayList<TableItem.TableItem_msg>();
    ListView listView;
    TextView tv_topMenu;
    TextView m_tv_txtTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_msglist, container, false);

        tv_topMenu = view.findViewById(R.id.txt_topMenu);
        tv_topMenu.setOnClickListener(Tv_topMenu_Click);

        MainActivity mainWnd = (MainActivity) getActivity();

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return view;
        m_tv_txtTitle = view.findViewById(R.id.txt_title);
        m_tv_txtTitle.setText(mainWnd.m_str_title);

        listView = view.findViewById(R.id.list);
        listView.setAdapter(new Adapter_msgList(inflater, mTableItems));
        listView.setOnItemClickListener(onItemClickListener);
        return view;
    }

    private showPopupMenu.qyFuncs_Droid.PF_procMenu procMenu = (Context context, int iMenuCmd) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (iMenuCmd == R.id.quit) {
            guiShare.GuiShare.pf_PostMessage_new.get().call(pQyMc.gui.hMainWnd, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        }
        return 0;
    };

    private View.OnClickListener Tv_topMenu_Click = (view) -> {
        int ii = 0;
        Context context = getActivity();
        showPopupMenu.qyFuncs_Droid.showPopupMenu(context, view, R.menu.layout_msglist_topmenu, procMenu);
    };

    String fileName_recentContacts = "recentContacts.txt";

    //
    public int saveRecentContacts() {
        Context context = MyApplication.getContext();
        String str;
        String fileName = fileName_recentContacts;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if(pProcInfo==null) return -1;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        fileName = fileName_recentContacts + pMisCnt.idInfo.ui64Id;


        //
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for (var e : mTableItems) {
                str = String.format("peer={%s}|speaker={%s}|t={%s}|txt={%s}|\n", e.ui64Id, e.speaker, e.tRecvTime, e.msg);
                outputStreamWriter.write(str);
            }

            //
            outputStreamWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public int loadRecentContacts() {
        int iErr = -1;
        Context context = MyApplication.getContext();
        String fileName = fileName_recentContacts;
        String str;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        fileName = fileName_recentContacts + pMisCnt.idInfo.ui64Id;

        try {
            var inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String temp = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {
                    //stringBuilder.append(temp);
                    //stringBuilder.append("\n");
                    qyDefs.QY_MESSENGER_ID idInfo_peer = new qyDefs.QY_MESSENGER_ID();
                    String speaker;
                    long tSendTime;
                    String txt;

                    String key;
                    int pos0;
                    int pos1;

                    //
                    key = "peer=";
                    pos0 = temp.indexOf(key);
                    if (pos0 < 0) return iErr;
                    pos1 = temp.indexOf('|', pos0);
                    if (pos1 < 0) return iErr;
                    str = temp.substring(pos0 + key.length(), pos1 - (pos0 + key.length()));
                    idInfo_peer.ui64Id = Long.parseLong(str);
                    //
                    key = "speaker=";
                    pos0 = temp.indexOf(key);
                    if (pos0 < 0) return iErr;
                    pos1 = temp.indexOf('|', pos0);
                    if (pos1 < 0) return iErr;
                    str = temp.substring(pos0 + key.length(), pos1 - (pos0 + key.length()));
                    speaker = str;
                    //
                    key = "t=";
                    pos0 = temp.indexOf(key);
                    if (pos0 < 0) return iErr;
                    pos1 = temp.indexOf('|', pos0);
                    if (pos1 < 0) return iErr;
                    str = temp.substring(pos0 + key.length(), pos1 - (pos0 + key.length()));
                    tSendTime = Long.parseLong(str);
                    //
                    key = "txt=";
                    pos0 = temp.indexOf(key);
                    if (pos0 < 0) return iErr;
                    pos1 = temp.indexOf('|', pos0);
                    if (pos1 < 0) return iErr;
                    str = temp.substring(pos0 + key.length(), pos1 - (pos0 + key.length()));
                    txt = (str);

                    //
                    TableItem.TableItem_msg item = new TableItem.TableItem_msg();

                    //
                    qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
                    idInfo_from.ui64Id = 0;
                    TableItem_msg_fillInfo(idInfo_peer, idInfo_from, item);
                    //
                    if (item.uiObjType == 0) continue;
                    //
                    item.speaker = speaker;
                    item.msg = txt;// new String(pContent->html.wBuf);
                    item.tRecvTime = tSendTime;
                    //
                    TableItem.TableItem_msg item1 = mTableItems.stream().filter(x -> x.ui64Id == item.ui64Id).findFirst().get();
                    if (item1 == null) {
                        mTableItems.add(item);
                    }

                    //
                    continue;
                }

                inputStream.close();
            }
        } catch (Exception e) {
            return iErr;
        }

        Collections.sort(mTableItems, (x, y) -> Long.compare(y.tRecvTime, x.tRecvTime));
        //
        Log.d("异步Notify msg_list adapter");
        if (listView != null) {
            Adapter_msgList adapter = (Adapter_msgList) listView.getAdapter();
            adapter.notifyDataSetChanged();
        }

        iErr = 0;
        return iErr;
    }


    //
    public static int TableItem_msg_fillInfo(qyDefs.QY_MESSENGER_ID addr_logicalPeer_idInfo, qyDefs.QY_MESSENGER_ID idInfo_from, TableItem.TableItem_msg item) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

        item.ui64Id = addr_logicalPeer_idInfo.ui64Id;
        //
        qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", addr_logicalPeer_idInfo, 0, messengerInfo, regInfo, null, null)) {
            return iErr;
        }
        item.uiObjType = messengerInfo.uiType;
        if (messengerInfo.uiType == qyDefs.Consts.CONST_objType_imGrp) {
            qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
            if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "", addr_logicalPeer_idInfo, grpInfo)) {
//                qyMemoryFunc.qyFuncs.mymemset((Object) ( byte)grpInfo, 0, sizeof(IM_GRP_INFO));
            }
            item.grpName = grpInfo.name;
            //
            if (grpInfo.idInfo_creator.ui64Id != 0) item.ImageResourceId = R.drawable.tmpgrp;
            else item.ImageResourceId = R.drawable.grp;
        } else {
            item.dw = regInfo.dw;
            item.bm = regInfo.bm;
            item.syr = regInfo.syr;
            //
            item.ImageResourceId = R.drawable.contact;
        }

        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = idInfo_from.ui64Id;
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, messengerInfo, regInfo, null, null)) {
            return iErr;
        }
        item.speaker = regInfo.syr;
        
        iErr = 0;
        return iErr;
    }

    //
    public static String get_syr_by_idInfo(qyDefs.QY_MESSENGER_ID idInfo) {
        String str = "";
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

        qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        //QY_MESSENGER_ID idInfo;
        //idInfo.ui64Id = pMsg->data.route.idInfo_from.ui64Id;
        if (!pDbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo,
                0, messengerInfo, regInfo, null, null)) {
            return null;
        }

        str = new String(regInfo.syr);


        return str;
    }

    public int gui_procMsg(qnmCommProc_mis.MIS_MSGU pMsgU) {
        int iErr = -1;
        String str;
        boolean bDbg = false;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

        //
        qyDefs.QY_MESSENGER_ID addr_logicalPeer_idInfo = new qyDefs.QY_MESSENGER_ID();
        addr_logicalPeer_idInfo.ui64Id = 0;
        qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
        idInfo_from.ui64Id = 0;
        String msg = "";
        long tRecvTime = 0;

        try {

            //
            int lenInBytes_msg = 0;
            //
            switch (pMsgU.uiType) {
                case qnmCommProc_mis.Consts.CONST_misMsgType_talk: {
                    qnmCommProc_mis.MIS_MSG_TALK pMsg = (qnmCommProc_mis.MIS_MSG_TALK) pMsgU;
                    qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;
                    //
                    addr_logicalPeer_idInfo = pMsg.addr_logicalPeer.idInfo;
                    idInfo_from = pMsg.data.route.idInfo_from;
                    tRecvTime = pMsg.tRecvTime;
                    //
                    lenInBytes_msg = pMsg.lenInBytes;//(int) ((int) System.Runtime.InteropServices.Marshal.OffsetOf < MIS_MSG_TALK > ("data") + pMsg.lenInBytes);
                    //
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_htmlContent: {
                            msg = ((qnmCommProc_mis.IM_HTML_CONTENT) pContent).wBuf;
                        }
                        break;
                    }


                }
                break;
                case qnmCommProc_mis.Consts.CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgU;
                    qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
                    addr_logicalPeer_idInfo = pMsg.addr_logicalPeer.idInfo;
                    idInfo_from.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                    tRecvTime = pMsg.tRecvTime;
                    //
                    lenInBytes_msg = pMsg.lenInBytes;// (int) ((int) System.Runtime.InteropServices.Marshal.OffsetOf < MIS_MSG_INPUT > ("data") + pMsg.lenInBytes);
                    //
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                            qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                            if (transferAvInfo.confCfg.idInfo_initiator.ui64Id != 0) {
                                idInfo_from.ui64Id = transferAvInfo.confCfg.idInfo_initiator.ui64Id;
                            }
                            //
                            msg = getTalkerDesc.qyFuncs.getMsgDesc(pMsgU);
                        }
                        break;
                        case imCommType_defs.Consts.CONST_imCommType_transferFileReq:
                            msg = getTalkerDesc.qyFuncs.getMsgDesc(pMsgU);
                            break;
                        default:
                            break;
                    }

                }
                break;
                case qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus: {
                    qnmCommProc_mis.MIS_MSG_taskStatus pMsg = (qnmCommProc_mis.MIS_MSG_taskStatus) pMsgU;
                    int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, pMsg.iTaskId);
                    qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                    if (pTaskInfo == null) return iErr;
                    pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = tmpFunc.qyFuncs.myGetTickCount(null);
                    //
                }
                iErr = 0;
                return iErr;
                default:
                    return iErr;
            }

            pProcInfo.postImMsg2Log_isClient(pMsgU, lenInBytes_msg);

            Log.d("fg_msgList.gui_procMsg, recvd " + msg);


            qyDefs.QY_MESSENGER_ID finalAddr_logicalPeer_idInfo = addr_logicalPeer_idInfo;
            TableItem.TableItem_msg item = this.mTableItems.stream().filter(x -> x.ui64Id == finalAddr_logicalPeer_idInfo.ui64Id).findFirst().orElse(null);
            if (item == null) {
                int kk = 0;
                item = new TableItem.TableItem_msg();
                //
                TableItem_msg_fillInfo(addr_logicalPeer_idInfo, idInfo_from, item);
                //
                item.msg = msg;// new String(pContent->html.wBuf);
                item.tRecvTime = tRecvTime;
                mTableItems.add(item);
                Collections.sort(mTableItems, (x, y) -> Long.compare(y.tRecvTime, x.tRecvTime));

                Adapter_msgList adapter = (Adapter_msgList) listView.getAdapter();
                adapter.notifyDataSetChanged();
            } else {
                //
                if (item.uiObjType == 0) {
                    TableItem.TableItem_msg tmp_tableItem_msg = new TableItem.TableItem_msg();
                    TableItem_msg_fillInfo(addr_logicalPeer_idInfo, idInfo_from, tmp_tableItem_msg);
                    item.uiObjType = tmp_tableItem_msg.uiObjType;
                    item.grpName = tmp_tableItem_msg.grpName;
                    item.dw = tmp_tableItem_msg.dw;
                    item.bm = tmp_tableItem_msg.bm;
                    item.syr = tmp_tableItem_msg.syr;
                    item.ImageResourceId = tmp_tableItem_msg.ImageResourceId;
                }
                //
                item.speaker = get_syr_by_idInfo(idInfo_from);
                item.msg = msg;// new String(pContent->html.wBuf);
                item.tRecvTime = tRecvTime;

                Collections.sort(mTableItems, (x, y) -> Long.compare(y.tRecvTime, x.tRecvTime));

                Adapter_msgList adapter = (Adapter_msgList) listView.getAdapter();
                adapter.notifyDataSetChanged();
                //
                int kk = 0;
            }
        } catch (Exception e) {
            Log.d("fg_msgList.gui_procMsg, exception");
            return iErr;
        }

        iErr = 0;
        return iErr;

    }

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

    private AdapterView.OnItemClickListener onItemClickListener = (parent, view, position, id) -> {
        TableItem.TableItem_msg item_sel = mTableItems.get(position);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = item_sel.ui64Id;

        qnmCommProc_mis.MSGR_ADDR addr = new qnmCommProc_mis.MSGR_ADDR();
        addr.idInfo.ui64Id = item_sel.ui64Id;
        isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();

        dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, addr, false, true, param);
    };

}


