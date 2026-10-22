
package com.qycx.base;

import static com.qycx.base.qmcSharedObj.Consts.MAX_sharedObjUsrs;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_task_end;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;

public class qmcVideoFunc
{
    public static class qyFuncs
    {

        // #define		getSharedObjUsr(  pSharedObj,  iIndex_usr  )		(  (  (  pSharedObj  )  &&  (  iIndex_usr  )  >=0  &&  (  iIndex_usr  )  <  MAX_sharedObjUsrs  )  ?  &(  pSharedObj  )->usrs[iIndex_usr]  :  NULL  )

        public static qmcSharedObj.SHARED_OBJ_USR getSharedObjUsr(qmcSharedObj.QY_SHARED_OBJ pSharedObj, int iIndex_usr)
        {
            return (((pSharedObj != null) && (iIndex_usr) >= 0 && (iIndex_usr) < MAX_sharedObjUsrs) ? (pSharedObj).usrs[iIndex_usr] : null);
        }


        public static int getSharedObjIndex_localAv()
        {
            return 0;
        }

        //
        //  void  askSharedObjToStop(  MC_VAR_isCli  *  pProcInfo,  qmcSharedObj.QY_SHARED_OBJ  *  pSharedObj,  HWND  hWnd_notifyEnd  )
        public static void askSharedObjToStop(ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.QY_SHARED_OBJ pSharedObj, Object hWnd_notifyEnd, String hint)

        {
            if (null == pSharedObj) return;

    
            if (!pSharedObj.var.bQuit)
            {
                pSharedObj.var.bQuit = true;
            }

            guiShare.GuiShare.pf_PostMessage.call(hWnd_notifyEnd, CONST_qyWm_postComm, CONST_qyWmParam_task_end, 0);

            return;
        }



    }
}
