package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_imObj_needRefreshed;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;

import java.nio.ByteBuffer;

public class imObj_proc {
    public static class QMem_imObj_intQ {
        public qyDefs.QY_MESSENGER_ID idInfo=new qyDefs.QY_MESSENGER_ID();

        public byte[] toByteArray() {
            byte[] buf = idInfo.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocateDirect(buf.length);
            buffer.put(buf);
            return buffer.array();
        }
    }

    public static class qyFuncs {

        public static int postImObjToIntQ(qyDefs.QY_MESSENGER_ID idInfo) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            if (idInfo.ui64Id == 0) return iErr;

            QMem_imObj_intQ mem = new QMem_imObj_intQ();
            mem.idInfo = idInfo;
            qPostMsg(mem, pMisCnt.imObj_intQ);

            iErr = 0;
            return iErr;
        }
        
        public static int triggerToDisplayImObjIntQ() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_imObj_needRefreshed, 0);
            return 0;
        }
    }
}
