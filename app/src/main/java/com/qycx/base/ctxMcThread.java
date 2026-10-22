


package com.qycx.base;

public class ctxMcThread {
    public static class CTX_MC_thread extends ctxQmThread.CTX_qm_thread {
        //
        public mcSharedQ.CMcSharedQ pProcessQ;

        public long dwTickCnt_tryToResumeVideoConference; //  2009/10/17

        public qnmCommProc_mis.CMIS_MSGU pMsgBuf_forInput;                     //  2009/12/11
        public qnmCommProc_mis.CMIS_MSGU pMsgBuf;                              //  2009/12/11

        //
        public CTX_MC_thread() {
            return;
        }

    }

    ;

    public static class CTX_MC_work extends ctxMcThread.CTX_MC_thread {
        //public ctxMcThread.CTX_MC_thread common;

        //GENERIC_Q guestQ;
        //boolean bGuestQInited;
        //void* ppGuests_sortedByDevIdStr;
        //

    }

    ;


    public static class CTX_MC_scheduler extends ctxMcThread.CTX_MC_thread {       //  2009/09/05
        //public ctxMcThread.CTX_MC_thread common;

        //

    }

    ;

    public static class CTX_mc_pretrans_status_errFlgs {
        public boolean bErrExists_mallocfFailed;
    }

    ;

    public static class CTX_mc_pretrans_status {
        public CTX_mc_pretrans_status_errFlgs errFlgs;
        public byte ucMsgSkipped_ifMallocFailed;     //  2014/05/12
    }

    ;

    public static class CTX_mc_pretrans extends ctxMcThread.CTX_MC_thread {

        //  2014/05/12
        public CTX_mc_pretrans_status status;

        //
        public long pMsgBuf;
        //
    }

    ;


}
