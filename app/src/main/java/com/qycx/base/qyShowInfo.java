


package com.qycx.base;

import com.qycx.base.util.Log;
import com.qycx.base.qyCommProc.QY_SHOW_SMALL;

import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.isFunc.qyFuncs.idStr2Id;
import static com.qycx.base.qnmCommProc.qyFuncs.myWChar2Utf8;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_debug;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmState;
import static com.qycx.base.qyCommProc.Consts.QyShowSmall_cntof_doStr;
import static com.qycx.base.qyCommProc.Consts.QyShowSmall_cntof_what;
import static com.qycx.base.qyCommProc.Consts.QyShowSmall_cntof_whereBuf;
import static com.qycx.base.qyCommonFunc9.Consts.cntof_buf256;
import static com.qycx.base.qyCommonSock.qyFuncs.myinet_addr;
import static com.qycx.base.qyShowInfoCommProc.qyFuncs.g_pShowInfoStruct;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsg;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentProcessId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentThreadId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetLocalTime;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;


public class qyShowInfo {
    //
    public static class PARAM_showInfo {
        public int usStateType;
        public int usStateSubtype;
        //
        public qyCommProc.mySYSTEMTIME when;
        //
        public int iPos_toDisplay;
        //
        public int iTaskId;
    }

    ;


    //
    public static class qyFuncs {
        //
        /*
        public static int qyShowInfo(Object pCtx_showInfo, int iShowType, int puiStep, String clientIp, String who_from, String who_to, String where, String doStr, String what) {
           // qyCommProc.QY_SHOW_SMALL showStruct = new qyCommProc.QY_SHOW_SMALL();
           // qyCommonFunc9.TMP_buf256 buf256 = new qyCommonFunc9.TMP_buf256();
            //
            qyCommProc.QY_SHOW_SMALL showStruct ;//= new qyCommProc.QY_SHOW_SMALL();
            qyCommonFunc9.TMP_buf256 buf256 ;   //= new qyCommonFunc9.TMP_buf256();
            qyShowInfoCommProc.Ctx_thread_tmpVar var = getCtxThreadTmpVar(0);
            if  (  var  ==  null  )  {

                return  -1;
            }

            showStruct = var.m_ctxThreadShowInfo.showStruct;
            buf256 = var.m_ctxThreadShowInfo.buf256;
            //

            if (iShowType == CONST_qyShowType_debug) return 0;

            //mymemset((byte[])showStruct, 0, sizeof(QY_SHOW_SMALL));
            showStruct.memset0();
            showStruct.iType = iShowType;
            if (null != clientIp && 0 != clientIp.length())
                showStruct.ulClientIp = myinet_addr(clientIp);
            if (who_from != null) {
                //_snwprintf_s(showStruct.who_from, mycountof(showStruct.who_from), _TRUNCATE, L"%s", who_from);
                //showStruct.who_from, who_from, QyShowStruct_cntof_who_from);
                myWChar2Utf8(who_from, buf256.buf, cntof_buf256);
                showStruct.who_from  =  idStr2Id(buf256.buf);
            }
            if (who_to != null) {
                //_snwprintf_s(showStruct.who_to, mycountof(showStruct.who_to), _TRUNCATE, L"%s", who_to);
                //showStruct.who_to, who_to, QyShowStruct_cntof_who_to);
                myWChar2Utf8(who_to, buf256.buf, cntof_buf256);
                showStruct.who_to=idStr2Id(buf256.buf);

            }
            if (where != null) {
                //_snwprintf_s(showStruct.whereBuf, mycountof(showStruct.whereBuf), _TRUNCATE, L"%s", where);
                showStruct.whereBuf = where;//, QyShowSmall_cntof_whereBuf);
            }
            //  when
            myGetLocalTime(showStruct.when); //  2007/06/01
            if (0 != puiStep) {               //  2008/04/28
                (puiStep)++;
                showStruct.usStep = (short) puiStep;
            }
            //  201508/20
            showStruct.dwProcessId = myGetCurrentProcessId();  //  2015/08/30
            showStruct.dwThreadId = myGetCurrentThreadId();
            //
            if (null != doStr) {
                //_snwprintf_s(showStruct.doStr, mycountof(showStruct.doStr), _TRUNCATE, L"%s", doStr);
                showStruct.doStr = doStr;//, QyShowSmall_cntof_doStr);
            }
            //_vsnwprintf_s(showStruct.what, mycountof(showStruct.what) - 1, _TRUNCATE, fmt, args);
            showStruct.what = what;//, QyShowSmall_cntof_what);

            if (null != pCtx_showInfo) {
                qyShowInfoCommProc.qyFuncs.ShowInfo_struct sShowInfoStruct = (qyShowInfoCommProc.qyFuncs.ShowInfo_struct) pCtx_showInfo;   //  g_pShowInfoStruct;

                //  调用显示信息用的汇调函数
                //if (sShowInfoStruct.errHintPf) sShowInfoStruct.errHintPf(showStruct, 0);

                if (sShowInfoStruct.bInited) {

                    switch (showStruct.iType) {
                        case CONST_qyShowType_qwmState:
                            if (!sShowInfoStruct.state_q2.isQ2Warning(50)) {
                                //sShowInfoStruct.state_q2.q2PostMsg(showStruct);
                                // sShowInfoStruct.q2.q2PostMsgAndTrigger(IntPtr.Zero,IntPtr.Zero, 0)

                                QY_qBuf2.HeadData_showInfo headData = var.m_ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();
                                //Log.d("qbuffer","q2PostMsgAndTrigger1 size:" + sShowInfoStruct.q2.size()+ " " + showStruct.what );
                                //int nQNodes = (sShowInfoStruct.state_q2.getQ2Nodes());
                                //byte ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.state_q2.cfg.uiMaxQNodes * 2));
                                //showStruct.ucPercent_showInfoQ = ucPercent;
                                byte [] byteArray  = showStruct.toByteArray(var.m_ctxThreadShowInfo, var.m_ctxThreadShowInfo.tmp_Var_QY_SHOW_SMALL);
                                headData.len_showInfo = byteArray.length;
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);
                                sShowInfoStruct.state_q2.q2PostMsg(headData ,byteArray, 0,byteArray.length,"qyShowInfo.105");
                                sShowInfoStruct.q2.q2PostMsgAndTrigger(null ,null, 0,0,"qyShowInfo.117");
                            }
                            break;
                        default:
                            //  2014/05/11
                            if (!sShowInfoStruct.q2.isQ2Warning(100)) {
                                //Log.d("qbuffer","q2PostMsgAndTrigger2 size: " + sShowInfoStruct.q2.size() + " " + showStruct.what);
                                //int nQNodes = (sShowInfoStruct.q2.getQ2Nodes());
                                //byte ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.q2.cfg.uiMaxQNodes * 2));
                                //showStruct.ucPercent_showInfoQ = ucPercent;
                                QY_qBuf2.HeadData_showInfo headData = var.m_ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();
                                byte []byteArray = showStruct.toByteArray(var.m_ctxThreadShowInfo, var.m_ctxThreadShowInfo.tmp_Var_QY_SHOW_SMALL);
                                headData.len_showInfo = byteArray.length;
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);

                                //
                                //Log.d("tn_msgInput","before qyShowInfo.114");

                                //
                                sShowInfoStruct.q2.q2PostMsgAndTrigger(headData ,byteArray, 0,byteArray.length,"qyShowInfo.114");
                            }
                            break;
                    }
                }
            }


            return 0;
        }
        */


        /*
        public static int qyShowInfo1(int iShowType, String who_from, String who_to, String where, String doStr) {
            String str;

            if (where == null) where = "";
            if (doStr == null) doStr = "";

            //
            if (who_from == null) {
                if (who_to == null) who_to = "";
                str = String.format("%s:%s,%s", who_to, where, doStr);
            } else if (who_to == null) {
                str = String.format("%s:%s,%s", who_from, where, doStr);

            } else {
                str = String.format("%s.%s:%s,%s", who_from, who_to, where, doStr);
            }

            //
            qyShowInfo(g_pShowInfoStruct, iShowType, 0, null, who_from, who_to, where, "", doStr);

            //
//            System.out.println(str);

            //Log.e(TAG,str);

            //
            return 0;

        }

        public static int qyShowInfo1(int iShowType, int puiStep, String clientIp, String who_from, String who_to, String where, String doStr, String fmtStr) {
            return qyShowInfo(g_pShowInfoStruct,iShowType,puiStep,"", who_from, who_to, where, "",doStr + fmtStr);
        }

         */


        //
        public static int qyShowInfo2(qyShowInfoCommProc.Ctx_thread_showInfo ctxThreadShowInfo,PARAM_showInfo pParam, int iShowType, int puiStep, String clientIp, String who_from, String who_to, String where, String doStrWhat) {

            if  (  ctxThreadShowInfo  ==  null  )  {
                Log.d("qy","qyShowInfo2 failed, ctxThreadShowInfo is null");
                return  -1;
            }

            qyCommProc.QY_SHOW_SMALL showStruct;//= new qyCommProc.QY_SHOW_SMALL();
            qyCommonFunc9.TMP_buf256 buf256;   //= new qyCommonFunc9.TMP_buf256();
            //qyShowInfoCommProc.Ctx_thread_tmpVar var = getCtxThreadTmpVar();

            showStruct = ctxThreadShowInfo.showStruct;
            buf256 = ctxThreadShowInfo.buf256;

            //
            //PARAM_showInfo param = new PARAM_showInfo();
            //if (null != pParam) param = pParam;

            //
            //mymemset((byte[])showStruct, 0, sizeof(QY_SHOW_SMALL));
            showStruct.memset0();
            showStruct.iType = iShowType;
            //
            if (pParam!=null) {
                showStruct.usStateType = (short) pParam.usStateType;
                showStruct.usStateSubtype = (short) pParam.usStateSubtype;
                //
                showStruct.when.copyFreom( pParam.when);
                //
                showStruct.iPos_toDisplay = pParam.iPos_toDisplay;
                //
                showStruct.iTaskId = pParam.iTaskId;
            }
            //
            if (null != clientIp && 0 != clientIp.length())
                showStruct.ulClientIp = myinet_addr(clientIp);
            if (null != who_from) {
                //_snwprintf_s(showStruct.who_from, mycountof(showStruct.who_from), _TRUNCATE, L"%s", who_from);
                //showStruct.who_from, who_from, QyShowStruct_cntof_who_from);
                myWChar2Utf8(who_from, buf256.buf, cntof_buf256);
                showStruct.who_from  =  idStr2Id(buf256.buf);
            }
            if (null != who_to) {
                //_snwprintf_s(showStruct.who_to, mycountof(showStruct.who_to), _TRUNCATE, L"%s", who_to);
                //showStruct.who_to, who_to, QyShowStruct_cntof_who_to);
                myWChar2Utf8(who_to, buf256.buf, cntof_buf256);
                showStruct.who_to  =  idStr2Id(buf256.buf);

            }
            if (null != where) {
                //_snwprintf_s(showStruct.whereBuf, mycountof(showStruct.whereBuf), _TRUNCATE, L"%s", where);
                showStruct.whereBuf = where;//, QyShowSmall_cntof_whereBuf);
            }
            //  when
            if (showStruct.when.wYear == 0 && showStruct.when.wMonth == 0) {
                myGetLocalTime(showStruct.when); //  2007/06/01
            }
            //
            if (0 != puiStep) {               //  2008/04/28
                (puiStep)++;
                showStruct.usStep = (short) puiStep;
            }
            //  201508/20
            showStruct.dwProcessId = myGetCurrentProcessId();  //  2015/08/30
            showStruct.dwThreadId = myGetCurrentThreadId();
            //
            if (null != doStrWhat) {
                //_snwprintf_s(showStruct.doStr, mycountof(showStruct.doStr), _TRUNCATE, L"%s", doStr);
                showStruct.what = doStrWhat;//, QyShowSmall_cntof_what);
            }

            //  MACRO_qyAssert(  g_pShowInfoStruct,  (  "qyShowInfo1: g_pShowInfoStruct is null"  )  );
            if (null != g_pShowInfoStruct) {
                qyShowInfoCommProc.qyFuncs.ShowInfo_struct sShowInfoStruct = g_pShowInfoStruct;

                //  调用显示信息用的汇调函数
                //if (sShowInfoStruct.errHintPf) sShowInfoStruct.errHintPf(showStruct, 0);

                if (sShowInfoStruct.bInited) {

                    switch (showStruct.iType) {
                        case CONST_qyShowType_qwmState:
                            if (!sShowInfoStruct.state_q2.isQ2Warning(10)) {

                                //
                                //byte[] kk  =  showStruct.toByteArray();
                                //QY_SHOW_SMALL kkk  =  new QY_SHOW_SMALL();
                                //kkk.fromBytes(kk);


                                //
                                QY_qBuf2.HeadData_showInfo headData = ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();
                                //Log.d("qbuffer","q2PostMsgAndTrigger3 state size is " + sShowInfoStruct.state_q2.size()+ " " + showStruct.what );
                                //int nQNodes = (sShowInfoStruct.state_q2.getQ2Nodes());
                                //byte ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.state_q2.cfg.uiMaxQNodes * 2));
                                //showStruct.ucPercent_showInfoQ = ucPercent;
                                headData.memset0();
                                byte[]byteArray= showStruct.toByteArray(ctxThreadShowInfo, ctxThreadShowInfo.tmp_Var_QY_SHOW_SMALL);
                                headData.len_showInfo = byteArray.length;
                                headData.tick_post=myGetTickCount(null);
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);
                                sShowInfoStruct.state_q2.q2PostMsg(headData ,byteArray, 0,byteArray.length,"qyShowInfo2.238");
                                sShowInfoStruct.q2.q2PostMsgAndTrigger(null,null, 0,0,"qyShowInfo2.280");
                            }
                            else  {
                                  if  (  false  ) {
                                      Log.d("qy", "qyShowInfo2: state_q warning, size " + sShowInfoStruct.state_q2.size());
                                  }
                            }
                            break;
                        default:
                            //  2014/05/11
                            if (!sShowInfoStruct.q2.isQ2Warning(10)) {
                                QY_qBuf2.HeadData_showInfo headData = ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();
                                headData.memset0();
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);
                                //Log.d("qbuffer","q2PostMsgAndTrigger4 size is " + sShowInfoStruct.q2.size() + " " + showStruct.what );
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);
                                //int nQNodes = (sShowInfoStruct.q2.getQ2Nodes());
                                //byte ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.q2.cfg.uiMaxQNodes * 2));
                                //showStruct.ucPercent_showInfoQ = ucPercent;
                                byte [] byteArray  =  showStruct.toByteArray(ctxThreadShowInfo, ctxThreadShowInfo.tmp_Var_QY_SHOW_SMALL);
                                headData.len_showInfo = byteArray.length;
                                headData.tick_post  =  myGetTickCount(null);

                                //
                                int nQNodes = (sShowInfoStruct.q2.getQ2Nodes());
                                byte ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.q2.cfg.uiMaxQNodes * 2));
                                //
                                showStruct.ucPercent_showInfoQ = ucPercent;

                                //
                                if  (  nQNodes  >  80  )  {

                                    //Log.d("qy","qyShowInfo2: q2.size "  + nQNodes);
                                }

                                //
                                if  ( false  )  {
                                    QY_SHOW_SMALL tmp_s  =  new QY_SHOW_SMALL();
                                    tmp_s.fromBytes(byteArray);
                                    //
                                    Log.d("qyShow", "doStr:"  +  tmp_s.doStr);
                                    Log.d("qyShow", "what:"  +  tmp_s.what);

                                }


                                //
                                //sShowInfoStruct.q2.q2PostMsgAndTrigger(showStruct);
                                sShowInfoStruct.q2.q2PostMsgAndTrigger(headData ,byteArray, 0,byteArray.length,"qyShowInfo2.249");
                            }
                            break;
                    }
                }
            }


            return 0;
        }


    }
}
