
package com.qycx.base;

import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmState;
import static com.qycx.base.qyShowInfo.qyFuncs.qyShowInfo2;

import android.util.Log;

public class showInfo_open {

    public static class qyFuncs {

        static String sWho_showInfo = ("999");

        public static int set_who_showInfo(String name) {
            if (null == name) return -1;
            sWho_showInfo = name;
            return 0;
        }

        public static int showInfo_open(Object p0, String tWhere, String hint) {
            showInfo_open(p0, 0, tWhere, hint);
            return 0;
        }

        public static int showInfo_open(String hint) {
            showInfo_open(0 , 0, "", hint);
            return 0;
        }

        public static int showInfo_open(Object p0, int puiStep, String tWhere, String hint) {


            com.qycx.base.qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar  =  getCtxThreadTmpVar(0,0);
            if  (  tmpVar  == null )  {
                Log.e("qy","showInfo_open failed, tmpVar is null");
                return  -1;
            }



            //
            if (tWhere == null) tWhere = "";
            if (hint == null) hint = "";

            String who_from = sWho_showInfo;

            if (null == hint) return -1;
            if (null == tWhere) tWhere = "";

            int threadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            String str = hint + " ." + threadId;

            //
            if  (  true  ) {
                Log.d("qyShowInfo", tWhere+":"+str);
            }


            //
            qyShowInfo2(tmpVar.m_ctxThreadShowInfo,null,CONST_qyShowType_qwmComm, puiStep, "", who_from, null, tWhere, hint);



            return 0;

        }

        public static int showState_open(Object p0, String tWhere, String hint, int usStateType, int usStateSubtype, qyCommProc.mySYSTEMTIME when, int iPos_toDisplay, int iTaskId) {

            String who_from = sWho_showInfo;

            if (null == hint) return -1;
            if (null == tWhere) tWhere = "";

            //
            com.qycx.base.qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar  =  getCtxThreadTmpVar(0,0);
            if  (  tmpVar == null )  {
                Log.d("qy","showState_open failed, tmpVar is null");
                return  -1;
            }

            //
            qyShowInfo.PARAM_showInfo param = tmpVar.m_ctxThreadShowInfo.tmp_param;//new qyShowInfo.PARAM_showInfo();
            param.usStateType = usStateType;
            param.usStateSubtype = usStateSubtype;
            param.when = when;
            param.iPos_toDisplay = iPos_toDisplay;
            param.iTaskId = iTaskId;

            qyShowInfo2(tmpVar.m_ctxThreadShowInfo,param, CONST_qyShowType_qwmState, 0, "", who_from, null, tWhere, hint);
            return 0;
        }
    }
}
