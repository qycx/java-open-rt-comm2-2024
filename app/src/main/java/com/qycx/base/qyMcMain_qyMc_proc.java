package com.qycx.base;

import static com.qycx.base.ctxQyMc.qyFuncs.exitQyMc;
import static com.qycx.base.ctxQyMc.qyFuncs.initQyMc;

public class qyMcMain_qyMc_proc {
    public static class qyFuncs_Droid {
        public static boolean InitInstance(long hInstance, int nCmdShow) {
            boolean bRet = false;
            String str;

            qyMcMain_global.g.g_pQyMc = new ctxQyMc.CCtxQyMc();
            if (qyMcMain_global.g.g_pQyMc == null) return bRet;

            //
            guiShare.GuiShare.pf_getSystmInfo.call();

            //  2016/08/12
            ctxQyMc.PARAM_initQyMc param = new ctxQyMc.PARAM_initQyMc();
            //memset(param, 0, sizeof(param));
            param.pfNewVar = ctxQmc_gui.CCtxQmc_gui.newVar_isCli_gui;
            param.pfFreeVar = ctxQmc_gui.CCtxQmc_gui.freeVar_isCli_gui;
            //
            if (initQyMc(0, param, qyMcMain_global.g.g_pQyMc) !=0) return bRet;
            
            ctxQmc.CCtxQmc pProcInfo = qyMcMain_global.g.g_pQyMc.get_pProcInfo();
            //
            bRet = true;
            

            return bRet;
        }

        public static int ExitInstance(long hInstance) {
            int iErr = -1;
            exitQyMc(qyMcMain_global.g.g_pQyMc);
            //
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if  (  pQyMc!=null) {
                pQyMc.gui.hMainWnd = null;
            }


            //  2015/07/08
            qyMcMain_global.g.g_pQyMc = null;



            //
            iErr = 0;
            
            return iErr;
        }


    }
}
