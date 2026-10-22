
package com.qycx.base;

public class ctxQmThread {

    public static class CTX_qm_thread {
        public int iType;
        public int dwTickCnt_lastGot;
        public int dwThreadId;
        public int reserved;

        //
        public com.qycx.base.ctxCaller.CTX_caller ctxCaller;

        //
        public CTX_qm_thread() {
            this.ctxCaller = new ctxCaller.CTX_caller();
        }

    }

    ;

}
