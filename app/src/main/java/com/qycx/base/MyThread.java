package com.qycx.base;

public class MyThread extends Thread {
    Object m_p0;
    qmcFunc.PF_threadProc m_pf;
    Object m_param;

    public MyThread(Object p0, qmcFunc.PF_threadProc pf, Object param) {
        m_p0 = p0;
        m_pf = pf;
        m_param = param;
    }


    @Override
    public void run() {
        //base.Run();
        if (m_pf != null) {
            m_pf.call(m_param);
        }

        return;
    }
}
