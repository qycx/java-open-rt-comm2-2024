package com.qycx;

import static com.qycx.base.qyWmComm.Consts.CONST_do_1080P_AV_Call;
import static com.qycx.base.qyWmComm.Consts.CONST_do_480P_AV_Call;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.DlgTalk;
import com.qycx.base.TableItem;
import com.qycx.base.audioRecorder_android;
import com.qycx.base.clipboardFunc;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;
import com.qycx.base.findOrgMsgAndProcess_is;
import com.qycx.base.getTalkerDesc;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.myDb;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qwmNetworkStatusCommProc;
import com.qycx.base.qyCommProc;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyDefs;
import com.qycx.base.qyDefs_open;
import com.qycx.base.qyDmItemCommProc;
import com.qycx.base.qyMcMainProc;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMemoryFunc;
import com.qycx.base.qyMsgFunc;
import com.qycx.base.qyShowInfo;
import com.qycx.base.qySyncCommProc;
import com.qycx.base.qyTypes;
import com.qycx.base.showInfo_open;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;
import com.qycx.tmpjavalibrary.FileUtils;

import java.lang.ref.WeakReference;

public class Fragment_dlgTalk_msgList extends Fragment implements View.OnTouchListener, Adapter_dlgTalk_msgList.OnItemClickListener {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Adapter_dlgTalk_msgList mAdapter;
    //
    TextView m_txt_back;
    TextView m_txt_title;
    TextView m_txt_more;
    //
    EditText m_editText_input;
    Button m_button_snd;
    Button m_button_avCall;
    Button m_button_avCall_HD;
    Button m_button_file;
    //
    Activity_dlgTalk mhTalk;
    //
    RelativeLayout m_relativeLayout_topMenu;

    private TMP_sel m_curSel;

    //
    class TMP_sel {

        public long ui64Id_from;

        public String msg;
        public long tStartTime;
        public int uiTranNo;
        public int iTaskId;

    }

    public static guiShare.PF_talk_addNewMsg talk_addNewMsg = (Object hTalkParam, qyDefs.QY_MESSENGER_ID pIdInfo_from, long tSendTime, int uiTranNo, int usCode, int uiContentType, String msg, int iTaskId) -> {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        if (null == hTalkParam) return -1;
        if (null == msg || msg.isEmpty()) return -1;

        Activity_dlgTalk hTalk;
        if (hTalkParam instanceof WeakReference) {
            hTalk = ((WeakReference<Activity_dlgTalk>) hTalkParam).get();
        } else {
            hTalk = (Activity_dlgTalk) hTalkParam;
        }
        TableItem.TableItem_dlgTalk_msg item = null;
        item = hTalk.m_var.m_pProcessQ.stream().filter(x -> x.ui64Id_from == pIdInfo_from.ui64Id && x.tStartTime == tSendTime && x.uiTranNo == uiTranNo).findFirst().orElse(null);
        if (item == null) {
            int kk = 0;
            item = new TableItem.TableItem_dlgTalk_msg();
            item.ui64Id_from = pIdInfo_from.ui64Id;

            Pair<Integer, Object> pair = getTalkerDesc.qyFuncs.getTalkerDesc(pIdInfo_from);
            if(pair==null) return -1;
            qnmCommProc_mis.QY_MESSENGER_REGINFO reginfo = (qnmCommProc_mis.QY_MESSENGER_REGINFO) pair.getValue1();
            item.dw = reginfo.dw;
            item.bm = reginfo.bm;
            item.syr = reginfo.syr;
            item.ImageResourceId = R.drawable.contact;
            item.tStartTime = tSendTime;
            item.uiTranNo = uiTranNo;
            item.usCode = usCode;
            item.uiContentType = uiContentType;
            item.msg = (msg);
            item.iTaskId = iTaskId;
            hTalk.m_var.m_pProcessQ.add(item);

            if (hTalk.mAdapter.mFragment_dlgTalk_msgList1 == null) return 0;
            Fragment_dlgTalk_msgList fg = hTalk.mAdapter.mFragment_dlgTalk_msgList1;

            Adapter_dlgTalk_msgList adapter = fg.mAdapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (fg.mLayoutManager != null) {
                fg.mLayoutManager.scrollToPosition(hTalk.m_var.m_pProcessQ.size() - 1);
            }
        }
        return 0;
    };

    public static guiShare.PF_talk_displayTaskStatus talk_displayTaskStatus = (Object hDlgTalkParam, qnmCommProc_mis.MIS_MSG_taskStatus pMsgTaskStatus) -> {
        int iErr = -1;
        Activity_dlgTalk hTalk = (Activity_dlgTalk) hDlgTalkParam;
        dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        if (pMsgTaskStatus.iTaskId == 0) return -1;
        TableItem.TableItem_dlgTalk_msg item = m_var.m_pProcessQ.stream().filter(x -> x.iTaskId == pMsgTaskStatus.iTaskId).findFirst().orElse(null);
        if (item == null) return -1;

        Fragment_dlgTalk_msgList fg = hTalk.mAdapter.mFragment_dlgTalk_msgList1;

        Adapter_dlgTalk_msgList adapter = fg.mAdapter;
        if (adapter == null) return -1;

        switch (pMsgTaskStatus.iStatus) {
            case qyDefs_open.Consts.CONST_imTaskStatus_sending:
            case qyDefs_open.Consts.CONST_imTaskStatus_receiving:
                item.statusStr = String.valueOf(pMsgTaskStatus.u_transferFileStatus.ucPercent);
                adapter.notifyDataSetChanged();
                break;
            case qyDefs_open.Consts.CONST_imTaskStatus_sendFinished:
            case qyDefs_open.Consts.CONST_imTaskStatus_recvFinished:
            case qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender:
            case qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver:
            case qyDefs_open.Consts.CONST_imTaskStatus_autoCanceledByReceiver: {

                int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, pMsgTaskStatus.iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                if (pTaskInfo == null) return 0;
                qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
                if (taskData == null) return 0;
                qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) taskData.msgU;
                {
                    if (pMsg.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return 0;
                    if (m_var.addr.uiObjType != qyDefs.Consts.CONST_objType_imGrp
                            || pMisCnt.idInfo.ui64Id != pMsg.data.route.idInfo_from.ui64Id) {
                        pMsg.iStatus = pMsgTaskStatus.iStatus;
                    }
                }
                item.statusStr = qyDmItemCommProc.qyFuncs.qyGetDesByType1(qwmNetworkStatusCommProc.qyFuncs.CONST_qyStatusTable_cn, pMsgTaskStatus.iStatus);
                adapter.notifyDataSetChanged();
            }

            break;
            default:
                break;
        }
        return 0;
    };

    //
    public int gui_procMsg(qnmCommProc_mis.MIS_MSGU pMsgU) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        boolean bDbg = false;
        String str;

        //
        Activity_dlgTalk hTalk = this.mhTalk;
        if (hTalk == null) return iErr;
        dlgTalkProc.DLG_TALK_var m_var = hTalk.get_pm_var();
        if (null == pMsgU) {
            return iErr;
        }

        //
        if (pMsgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_input) {
            qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgU;
            if (pMsg == null) {
                Log.d("pMsg is null ");
                return iErr;
            }
            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;
            if (pContent == null) {
                Log.d("pContent is null ");
                return iErr;
            }
            switch (pContent.uiType) {

                case imCommType_defs.Consts.CONST_imCommType_confLayout: {
                    if (!m_var.av.taskInfo.bTaskExists) return iErr;
                    if (m_var.av.taskInfo.ucbStarter) break;

                    //  以下为参会人
                    qnmCommProc_mis.CONF_LAYOUT confLayout = (qnmCommProc_mis.CONF_LAYOUT) pContent;
                    if (0 != confLayout.ucbResp) {
                        //
                        qnmCommProc_mis.IM_CONTENTU tmpContent = pContent;
                        if (confLayout.usOp == qnmCommProc_mis.Consts.CONST_imOp_phone_respCurr) {

                            qyDefs.QY_MESSENGER_ID idInfo_curr = new qyDefs.QY_MESSENGER_ID();
                            idInfo_curr.ui64Id = confLayout.confLayoutParam.resp.curr_idinfo;
                            int ii = 0;
                        }

                        if (confLayout.usOp == qnmCommProc_mis.Consts.CONST_imOp_respChannel) {
                            int ichannel = confLayout.confLayoutParam.ipcParam.iChannel;
                            str = confLayout.confLayoutParam.ipcParam.channelName;
                            int ii = 0;
                        }
                    }
                }
                break;
                case imCommType_defs.Consts.CONST_imCommType_statusConfLayoutParam: {
                    //这里接收 放大缩小的改变变化
                    qnmCommProc_mis.StatusConfLayoutParam statusConfLayoutParam = (qnmCommProc_mis.StatusConfLayoutParam) pContent;
                    int bAmplification = statusConfLayoutParam.confLayoutParam.respVideoStatus.bAmplification;
                    Log.d("通用通道放大缩小变化 1 为放大:" + bAmplification);
                    int qiuji = statusConfLayoutParam.confLayoutParam.ipcParam.iChannel;
                    String str1 = statusConfLayoutParam.confLayoutParam.ipcParam.channelName;
                    int i = 0;


                    if (null != hTalk.m_var.m_hTool_dlgTalk_av) {
                        Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) hTalk.m_var.m_hTool_dlgTalk_av;
                        hTalk_av.m_fg_dlgTalk_av2.changeCurrentVideoStatus(bAmplification);
                        Log.d("##球机放大缩小变化通道数从>=1开始,其他为非球机通道画面,当前通道:" + qiuji);
                        hTalk.m_var.currentSelectQiuJiChannel = qiuji;
                        long qiuji_ScaleId = ((qnmCommProc_mis.StatusConfLayoutParam) pContent).confLayoutParam.enlargeParam.ui64Id;
                        //这里拿到那个ID号了哈
                        Log.d("##球机qiuji_ScaleId  qiuji" + qiuji_ScaleId + "    " + qiuji);
                        hTalk.m_var.currentSelectQiuJiIdInfo = qiuji_ScaleId;

                        hTalk_av.m_fg_dlgTalk_av2.changeNvrControlStatus(qiuji);

                    }


                }
                break;
                case imCommType_defs.Consts.CONST_imCommType_confNvrInfo: {
                    //if (!m_var.av.taskInfo.bTaskExists) return iErr;
                    if (!m_var.av.taskInfo.bTaskExists) return iErr;
                    //
                    if (m_var.av.taskInfo.ucbStarter) break;

                    if (0 != ((qnmCommProc_mis.ConfNvrInfo) pContent).ucbResp) { //  发给requester

                        qnmCommProc_mis.ConfNvrInfo confNvrInfo = (qnmCommProc_mis.ConfNvrInfo) pContent;
                        qyDefs.QY_MESSENGER_ID idInfo_to = confNvrInfo.idInfo_requester;
//                        idInfo_to = confNvrInfo.idInfo_requester;
                        qyDefs.QY_MESSENGER_ID idInfo_requester = confNvrInfo.idInfo_requester;
//                        idInfo_requester = confNvrInfo.idInfo_requester;
                        //

                        pProcInfo.m_ipcProc.op.nvrInfo = confNvrInfo.nvrInfo;

                        Log.d("pProcInfo.m_ipcProc.op.nvrInfo:" + pProcInfo.m_ipcProc.op.nvrInfo);

                        //Activity_dlgTalk hTalk = this.mhTalk;
                        if (null != hTalk.m_var.m_hTool_dlgTalk_av) {
                            Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) hTalk.m_var.m_hTool_dlgTalk_av;
                            hTalk_av.m_fg_dlgTalk_av2.showNvrList();

                        }

                    }
                }
                break;
                case imCommType_defs.Consts.CONST_imCommType_taskProcReq:
                    qnmCommProc_mis.TASK_PROC_REQ taskProcReq = (qnmCommProc_mis.TASK_PROC_REQ) pContent;
                    if (taskProcReq.usOp == qnmCommProc_mis.Consts.CONST_imOp_recv_applyForOrgReq) {
                        qySyncCommProc.CQyMalloc malloc_pMsgTask_o = new qySyncCommProc.CQyMalloc();
                        {
                            int size = 128;//sizeof(MIS_MSG_TASK);
                            qnmCommProc_mis.CMIS_MSGU pMsgTask_o = new qnmCommProc_mis.CMIS_MSGU();// (qnmCommProc_mis.MIS_MSG_TASK) malloc_pMsgTask_o.mallocf(size);
                            if (null == pMsgTask_o) return iErr;

                            if (pMsg.iTaskId == 0) return iErr;
                            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, pMsg.iTaskId);
                            if (index_taskInfo < 0) return iErr;
                            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "fg_dlgTalk_msgList.gui_procMsg");
                            if (pTaskInfo == null) return iErr;
                            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                            if (pTaskData == null) return iErr;

                            //MIS_MSG_TASK pMsgTask_i = findTaskInProcessQ(&m_var, pMsg->iTaskId);
                            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_i = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                            {
                                if (null == pMsgTask_i) return iErr;
                                if (pMsgTask_i.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task)
                                    return iErr;
                                //
                                qnmCommProc_mis.IM_CONTENTU pContentTask_i = pMsgTask_i.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask_i.data.buf;
                                if (pContentTask_i.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                    return iErr;
                                //
                                if (true) {   //  2012/05/25
                                    boolean bPermitted = false;
                                    bPermitted = true;
                                    //
                                    if (!bPermitted) {
                                        return iErr;
                                    }
                                }
                                //
                                qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
//                                    qyFuncs.mymemset((Object) (byte) param, 0, sizeof(PARAM_findOrgMsg));
                                param.pMsgTask_o = pMsgTask_o;
                                if (pProcInfo.processQ_media.qTraverse(mainActivity.var_common.ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsgTask_i, param) <= 0) {
                                    return iErr;
                                }

                                if (pMsgTask_o.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) {
                                    return iErr;
                                }
                                qnmCommProc_mis.IM_CONTENTU pContentTask_o = ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).data.buf;
                                if (pContentTask_o.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                    return iErr;

                                int lenInBytes = ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).lenInBytes;// (int) (pMsgTask_o.lenInBytes Marshal.OffsetOf < MIS_MSG_routeTalkData > ("buf"));
                                if (lenInBytes < 0) return iErr;
                                //
                                qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
                                idInfo_dst.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                                //  set idInfo_logicalPeer temporiary.
                                if (m_var.addr.idInfo.ui64Id != idInfo_dst.ui64Id) {
                                    ((qnmCommProc_mis.TRANSFER_AV_INFO) pContent).idInfo_logicalPeer.ui64Id = m_var.addr.idInfo.ui64Id;
                                }
                                qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
//                                postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, qnmCommProc_mis.Consts.CONST_misMsgType_outputTask, (byte) 0, pMsgTask_o.usCode, pMsgTask_o.tStartTime, pMsgTask_o.uiTranNo, (short) 0, pMsgTask_o.iTaskId, pMsgTask_o.uiTaskType, pMsgTask_o.data.buf.array(), (int) lenInBytes, idInfo_logicalPeer, pMsgTask_o.idInfo_taskSender, pMsgTask_o.idInfo_taskReceiver, idInfo_dst, 0, (qnmCommProc_mis.MIS_MSGU) null, false);
                                //pContentTask_o->transferAvInfo.idInfo_logicalPeer.ui64Id  =  0;
                                //
                                str = String.format("dlgTalk: send transferAvInfo again to %s. tn %s", idInfo_dst.ui64Id, ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).uiTranNo);
                                showInfo_open.qyFuncs.showInfo_open( str);

                                //
                                break;
                            }
                        }
                    }
                    //  2014/10/09
                    break;
                case imCommType_defs.Consts.CONST_imCommType_taskProcResp:
                    if (((qnmCommProc_mis.TASK_PROC_RESP) pContent).usOp_org == qnmCommProc_mis.Consts.CONST_imOp_recv_applyForChkTaskAlive) {
                        /**/
                    }
                    break;
                default:
                    break;
            }

            test.testCls_Droid.testChk("talk_msgList.gui_procMsg.402");
        }

        //
        test.testCls_Droid.testChk("talk_msgList.gui_procMsg.406");

        dlgTalkProc.PARAM_PROC_ROW ppr = new dlgTalkProc.PARAM_PROC_ROW();
        dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg(mhTalk, ppr, (Object) pMsgU);

        //
        test.testCls_Droid.testChk("talk_msgList.gui_procMsg.407");


        //
        iErr = 0;

        return iErr;

    }


    //
    public int chkIfOldAvTaskExists() {
        int iErr = -1;
        //
        if (mhTalk == null) return -1;
        if (mhTalk.m_var == null) return -1;
        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
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
                if (pMsgTask.addr_logicalPeer.idInfo.ui64Id == mhTalk.m_var.addr.idInfo.ui64Id) {
                    qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;
                    if (pContent.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                        continue;
                    qyDefs.QY_MESSENGER_ID idInfo_initiator;
                    idInfo_initiator = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContent).confCfg.idInfo_initiator;
                    if (idInfo_initiator.ui64Id == 0)
                        idInfo_initiator.ui64Id = pMsgTask.data.route.idInfo_from.ui64Id;
                    int tmp_iTaskId = pTaskInfo.var.iTaskId;

                    //
                    myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
                    qyDefs.QY_MESSENGER_ID idInfo = idInfo_initiator;


                    qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
                    qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
                    if (!pDbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en,
                            "", idInfo, 0, msgrInfo, regInfo, null, null)) {
//                        qyMemoryFunc.qyFuncs.mymemset((Object) (byte) regInfo, 0, sizeof(QY_MESSENGER_REGINFO));
                    }
                    TableItem.TableItem_dlgTalk_msg item = new TableItem.TableItem_dlgTalk_msg();
                    item.ui64Id_from = idInfo_initiator.ui64Id;
                    item.dw = regInfo.dw;
                    item.bm = regInfo.bm;
                    item.syr = regInfo.syr;
                    item.ImageResourceId = R.drawable.contact;
                    item.msg = "视频呼叫";
                    item.iTaskId = tmp_iTaskId;
                    item.ImageResourceId = R.drawable.contact;
                    item.tStartTime = pMsgTask.tStartTime;
                    item.uiTranNo = pMsgTask.uiTranNo;
                    mhTalk.m_var.m_pProcessQ.add(item);
                }
            }
        }
        iErr = 0;
        return iErr;
    }


    public int gui_onTimer(Object p0, Object p1, Object p2) {
        Activity_dlgTalk hTalk = this.mhTalk;
        if (hTalk == null) return 0;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        if (this.mhTalk.m_var.m_hTool_dlgTalk_av == null) {
            qyCommProc_open.findOldAvTaskActiveParams findOldAvTaskActiveParams = new qyCommProc_open.findOldAvTaskActiveParams();
//            int iTaskId = 0;
//            int iElapseInMs = 0;
            if (hTalk.findOldAvTaskActive(findOldAvTaskActiveParams) == 0) {
                Log.d( "qy","任务活跃中. elapse: " + findOldAvTaskActiveParams.piElapseInMs);
                this.m_button_avCall_HD.setText("接收");
            } else {
                this.m_button_avCall_HD.setText("视频通话");
            }

            if (!pProcInfo.av.talk.bChannelReady) {
                m_txt_title.setText(hTalk.m_str_title + "(离线)");
            } else {
                m_txt_title.setText(hTalk.m_str_title);
            }
        }
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your fragment here
        mhTalk = (Activity_dlgTalk) getActivity();

        //
        if (mhTalk.m_var != null) {
            chkIfOldAvTaskExists();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_dlgtalk_msglist, container, false);

        m_txt_back = view.findViewById(R.id.txt_back);
        m_txt_title = view.findViewById(R.id.txt_title);
        m_txt_more = view.findViewById(R.id.txt_more);

        m_txt_back.setOnClickListener(M_txt_back_Click);
        m_txt_more.setOnClickListener(M_txt_more_Click);

        mRecyclerView = view.findViewById(R.id.recyclerView);

        //
        mLayoutManager = new LinearLayoutManager(inflater.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Adapter_dlgTalk_msgList(getActivity(), mhTalk.m_var.m_pProcessQ);

        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        m_editText_input = view.findViewById(R.id.editText_input);
        m_button_snd = view.findViewById(R.id.button_snd);
        m_button_avCall = view.findViewById(R.id.btn_av);
        m_button_avCall_HD = view.findViewById(R.id.btn_av_hd);
        m_button_file = view.findViewById(R.id.btn_file);

        m_button_snd.setOnClickListener(M_button_snd_Click);
        m_button_avCall.setOnClickListener(M_button_avCall_Click);
        m_button_file.setOnClickListener(M_button_file_Click);
        m_button_avCall_HD.setOnClickListener(M_button_avCall_HD_Click);

        Activity_dlgTalk hTalk = (Activity_dlgTalk) getActivity();
        m_txt_title.setText(hTalk.m_str_title);

        m_relativeLayout_topMenu = view.findViewById(R.id.topMenu);

        return view;
    }

    private View.OnClickListener M_button_file_Click = (View view) -> {
        //throw new NotImplementedException();
        int ii = 0;
        openSystemFile();
    };

    //
    public void openSystemFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //Toast.makeText(this, "请 安装文件管理器", Toast.LENGTH_SHORT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (requestCode == 1) {
            //Get the Uri of the selected file


            if (data.getClipData() != null) {//有选择多个文件

            } else if (data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String path = null;// uri.Path;

                try {
                    FileUtils fu = new FileUtils(this.mhTalk);
                    path = fu.getPath(uri);
                    //path = PathUtil.getPath(this, uri);
                    if (path == null) return;
                    //
                    int iTaskId = 0;
                    DlgTalk.qyFuncs_Droid.procSendFile(path, null, null, mhTalk, mhTalk.m_var, iTaskId);
                } catch (Exception e) {
                    return;
                }
                int i;
                i = 9;

                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }


    //
    private View.OnClickListener M_txt_more_Click = (View view) -> {
        //throw new NotImplementedException();
        mhTalk.startActivity_dlgTalk_detail();
    };

    private View.OnClickListener M_txt_back_Click = (View view) -> {
        //throw new NotImplementedException();
        mhTalk.onBackPressed();
    };


    @Override
    public void onItemClick(View view, int position) {
        TableItem.TableItem_dlgTalk_msg item_sel = mhTalk.m_var.m_pProcessQ.get(position);
        this.m_curSel = new TMP_sel();
        m_curSel.ui64Id_from = item_sel.ui64Id_from;
        m_curSel.tStartTime = item_sel.tStartTime;
        m_curSel.uiTranNo = item_sel.uiTranNo;
        m_curSel.msg = item_sel.msg;
        m_curSel.iTaskId = item_sel.iTaskId;
        showListDialog();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private View.OnClickListener M_button_avCall_Click = (View view) -> {
        if (mhTalk.hasAllPermission()) {
            startActivityDlgTalk(CONST_do_480P_AV_Call);
        } else {
            mhTalk.isPermissionOk();
        }
    };

    private View.OnClickListener M_button_avCall_HD_Click = (View view) -> {
        //
        audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);

        //
        if (mhTalk.hasAllPermission()) {
            startActivityDlgTalk(CONST_do_1080P_AV_Call);
        } else {
            mhTalk.isPermissionOk();
        }
    };

    public void startActivityDlgTalk(int level) {
        Activity_dlgTalk dlgTalk = (Activity_dlgTalk) getActivity();

        //
        qyCommProc_open.findOldAvTaskActiveParams findOldAvTaskActiveParams = new qyCommProc_open.findOldAvTaskActiveParams();
//        int iTaskId = 0;
//        int iElapseInMs = 0;
        if (0 == dlgTalk.findOldAvTaskActive(findOldAvTaskActiveParams)) {
            String str;
            str = "任务 " + findOldAvTaskActiveParams.piTaskId + " 仍在活跃中";
            Log.d(str);
            doAvRecover(findOldAvTaskActiveParams.piTaskId);
            return;
        }
        dlgTalk.doTask_startAvCall(level);
    }

    private View.OnClickListener M_button_snd_Click = (View view) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        String str = m_editText_input.getText().toString();
        str = str.trim();
        if (str.isEmpty()) return;
        //
        qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
        idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
        //
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = (int) tStartTran;

        m_editText_input.setText("");
        m_editText_input.requestFocus();

        byte ucFlg;
        int lenInBytes;
        qnmCommProc_mis.CMIS_MSGU pMsgBuf = null;
        ucFlg = 0;

        qnmCommProc_mis.IM_HTML_CONTENT content = new qnmCommProc_mis.IM_HTML_CONTENT();
        content.uiType = imCommType_defs.Consts.CONST_imCommType_htmlContent;
        content.wBuf = str;//, qnmCommProc_mis.Consts.cntof_imHtmlContent_wBuf);
        lenInBytes = 20 + str.length() * Character.BYTES;//  (tmpFunc.qyFuncs.mylstrlen(content.wBuf) + 1) * sizeof( char);

        qnmCommProc_mis.MSG_ROUTE route = new qnmCommProc_mis.MSG_ROUTE();
        route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
        route.idInfo_to.ui64Id = mhTalk.m_var.addr.idInfo.ui64Id;
        route.uiDevType_to = mhTalk.m_var.addr.uiDevType;

        qyDefs.QY_MESSENGER_ID talk_idInfo = new qyDefs.QY_MESSENGER_ID();
        talk_idInfo.ui64Id = mhTalk.m_var.addr.idInfo.ui64Id;

        if (mhTalk.m_var.addr.uiObjType != qyDefs.Consts.CONST_objType_imGrp) {
            if (0 != postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, route,
                    qnmCommProc_mis.Consts.CONST_misMsgType_talk, ucFlg, qnmCommProc.Consts.CONST_qyCmd_talkTo, tStartTran, uiTranNo, (short) 0, content, lenInBytes, talk_idInfo.ui64Id, talk_idInfo.ui64Id, 0, pMsgBuf, true))
                return;
        } else {
            qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
            idInfo_dst.ui64Id = mhTalk.m_var.addr.idInfo.ui64Id;

            if (idInfo_dst.ui64Id != mhTalk.m_var.addr.idInfo.ui64Id)
                content.idInfo_logicalPeer.ui64Id = mhTalk.m_var.addr.idInfo.ui64Id;

//            byte[] data = content.toByteArray();
            if (0 != postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, route,
                    qnmCommProc_mis.Consts.CONST_misMsgType_talk, ucFlg, (short) qnmCommProc.Consts.CONST_qyCmd_talkTo, tStartTran, uiTranNo, (short) 0, content, lenInBytes, talk_idInfo.ui64Id, idInfo_dst.ui64Id, 0, pMsgBuf, true))
                return;
        }

        talk_addNewMsg.call(this.mhTalk, idInfo_from, tStartTran, uiTranNo, qnmCommProc.Consts.CONST_qyCmd_talkTo, content.uiType, str, 0);
    };

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        //throw new NotImplementedException();
        // MotionEvent Object holds X-Y values
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return false;
    }

    //
    public final static int iCmd_copy = 1;
    public final static int iCmd_av_alive = 10;
    public final static int iCmd_av_recover = 11;
    public final static int iCmd_task_gone = 12;
    public final static int iCmd_file_accept = 20;
    public final static int iCmd_file_cancel = 21;

    //
    qyTypes.QY_DMITEM[] menu_conf_alive = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_av_alive, "会议正进行"),
                    new qyTypes.QY_DMITEM(-1, "")
            };


    qyTypes.QY_DMITEM[] menu_conf_bClosed = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_av_recover, "重新连接"),
                    new qyTypes.QY_DMITEM(-1, "")
            };

    //
    qyTypes.QY_DMITEM[] menu_file_wait = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_file_accept, "接收"),
                    new qyTypes.QY_DMITEM(iCmd_file_cancel, "取消"),
                    new qyTypes.QY_DMITEM(-1, "")
            };

    //
    qyTypes.QY_DMITEM[] menu_file_alive = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_file_cancel, "取消"),
                    new qyTypes.QY_DMITEM(-1, "")
            };


    //
    qyTypes.QY_DMITEM[] menu_task_gone = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_task_gone, "任务已终止"),
                    new qyTypes.QY_DMITEM(-1, "")
            };


    //
    qyTypes.QY_DMITEM[] menu_msg = new qyTypes.QY_DMITEM[]
            {
                    new qyTypes.QY_DMITEM(iCmd_copy, "复制"),
                    new qyTypes.QY_DMITEM(-1, ""),
            };


    //
    int getCnt(qyTypes.QY_DMITEM[] items) {
        if (items == null) return 0;
        int i;
        int cnt = 0;
        for (i = 0; i < items.length; i++) {
            if (items[i].type == -1) break;
            cnt++;
        }
        //
        return cnt;
    }

    //
    String[] getMenuList(qyTypes.QY_DMITEM[] menu) {
        if (menu == null) return null;

        //
        String[] items = null;
        int n = getCnt(menu);
        if (n == 0) return null;
        items = new String[n];
        int i;
        for (i = 0; i < n; i++) {
            items[i] = new String(menu[i].des);
        }
        return items;
    }


    //
    qyTypes.QY_DMITEM[] getTaskMenu(int iTaskId) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qyTypes.QY_DMITEM[] menu = null;

        if (iTaskId == 0) return null;

        //
        int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
        if (pTaskInfo == null){
            if (menu == null)
            {
                menu = menu_task_gone;
            }
            return menu;
        }
        qmcTaskInfo.QMC_taskData_common pTaskData = (pTaskInfo.getTaskData());
        if (pTaskData == null) {
            if (menu == null)
            {
                menu = menu_task_gone;
            }
            return menu;
        }
        qnmCommProc_mis.MIS_MSGU pMsgU = pTaskData.msgU;
        {
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pMsgU;
            if (pMsgTask.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task){
                if (menu == null)
                {
                    menu = menu_task_gone;
                }
                return menu;
            }

            if (!pTaskInfo.var.bClosed && qyCommProc.qyFuncs.bTaskAlive(pMsgTask.iStatus)) {
                qnmCommProc_mis.IM_CONTENTU pContent = qnmCommProc_mis.qyFuncs.M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
                if (pContent == null){
                    if (menu == null)
                    {
                        menu = menu_task_gone;
                    }
                    return menu;
                }

                switch (pContent.uiType) {
                    case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                        menu = menu_conf_alive;
                        break;
                    case imCommType_defs.Consts.CONST_imCommType_transferFileReq: {
                        if (qyCommProc.qyFuncs.bTaskNeedAcception(pMsgTask.iStatus)) {
                            menu = menu_file_wait;
                        } else {
                            menu = menu_file_alive;
                        }
                    }
                    break;
                    default:
                        break;
                }
            } else {
                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                int iDiffInMs = (int) (dwTickCnt - pTaskInfo.var.dwTickCnt_recv_lastRefreshed1);
                if (iDiffInMs < 500) {
                    menu = menu_conf_bClosed;
                } else {
                    menu = menu_task_gone;
                }
            }
        }

        iErr = 0;
        if (menu == null) {
            menu = menu_task_gone;
        }
        return menu;
    }

    qyTypes.QY_DMITEM[] m_curMenu = null;

    //
    void showListDialog() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        String[] items = null;// new String[]{ "我是1","我是2","我是3"};
        //
        m_curMenu = null;
        if (m_curSel.iTaskId == 0) {
            m_curMenu = menu_msg;

        } else {
            m_curMenu = getTaskMenu(m_curSel.iTaskId);
        }
        items = getMenuList(m_curMenu);
        if (items == null) return;


        //
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
        listDialog.setIcon(R.drawable.icon);//图标
        listDialog.setTitle("菜单");
        listDialog.setItems(items, ListClicked);
        listDialog.show();
    }

    //
    int doAvRecover(int iTaskId) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
        if (pTaskInfo == null) return iErr;

        //
        pTaskInfo.var.bClosed = false;

        //
        Log.d("doAvRecover: bNeedRemoved_toRecover set to true");

        //
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        qnmCommProc_mis.MIS_MSGU tmp_pMsg = pTaskData.msgU;
        if (tmp_pMsg.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return iErr;
        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) tmp_pMsg;
        task.iStatus = qyDefs_open.Consts.CONST_imTaskStatus_resp;
        Log.d("##1347 " + task.iStatus);


        //
        dlgTalkProc.DLG_TALK_var m_var = this.mhTalk.m_var;


        //
        m_var.av.bNeedDoTaskAv = true;

        //
        //mainActivity.var_common.processingAvTask
        if (this.mhTalk.hasAllPermission()) {
            this.mhTalk.startActivity_dlgTalk_av(tmpResource.IDC_BUTTON_av_accept, index_taskInfo, CONST_do_1080P_AV_Call, "doAvRecover");

        } else {
            this.mhTalk.isPermissionOk();
        }


        iErr = 0;
        return iErr;
    }

    //
    private DialogInterface.OnClickListener ListClicked = (DialogInterface dialog, int which) -> {
        int ii = 0;

        Activity_dlgTalk hTalk = (Activity_dlgTalk) getActivity();
        if (m_curMenu == null) return;
        if (which >= m_curMenu.length) return;

        if (m_curSel.iTaskId == 0) {
            switch (m_curMenu[which].type) {
                case iCmd_copy:
                    clipboardFunc.qyFuncs_Droid.copyToClipboard(m_curSel.msg);
                    break;
                default:
                    break;
            }
        } else {
            switch (m_curMenu[which].type) {
                case iCmd_av_recover:
                    doAvRecover(m_curSel.iTaskId);
                    break;
                case iCmd_file_accept: {
                    dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
                    dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(hTalk, m_curSel.iTaskId, (short) qnmCommProc_mis.Consts.CONST_imOp_recv_accept, "", param);
                }
                break;
                default:
                    break;
            }
        }
    };
}

