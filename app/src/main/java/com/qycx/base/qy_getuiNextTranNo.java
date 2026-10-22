
package com.qycx.base;

import java.util.concurrent.atomic.AtomicInteger;

public class qy_getuiNextTranNo {
    public static class qyFuncs {
        private static final Object lock = new Object();
        private static AtomicInteger siTranNo = new AtomicInteger(0);

        public static qySyncCommProc.PF_getuiNextTranNo getuiNextTranNo = () -> {
            synchronized (lock)
            {
                return  siTranNo.addAndGet(1) + 1;
            }
        };
    }
}
