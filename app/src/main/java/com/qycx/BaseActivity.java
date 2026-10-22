package com.qycx;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.qycx.base.qyDefs;
import com.qycx.base.showInfo_open;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;

public class BaseActivity extends AppCompatActivity {
    Handler m_timerHandler;
    WeakReference<myTimerRunnable> m_timerRunnable;
    int m_delayInMs;
    qyDefs.PF_commonHandler m_pf;

    //
    public String mTag;

    //
    public int m_tn_dbg = 0;

    //       
    class myTimerRunnable implements Runnable {
        public WeakReference<BaseActivity> mOwner;
            public myTimerRunnable(BaseActivity owner)
        {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void run()
        {
            //throw new NotImplementedException();
            mOwner.get().m_pf.call(null, null, null);
            if (mOwner.get().m_timerHandler != null) {
                mOwner.get().m_timerHandler.postDelayed(mOwner.get().m_timerRunnable.get(), mOwner.get().m_delayInMs);
            }
        }
    }


    public int setTimer(int delayInMs, qyDefs.PF_commonHandler pf) {
        if (delayInMs == 0) return -1;
        if (pf == null) return -1;

        if (m_timerHandler != null || m_timerRunnable != null) return -1;

        //
        this.m_timerHandler = new Handler();
        this.m_timerRunnable = new WeakReference<>(new myTimerRunnable(this));
        this.m_delayInMs = delayInMs;
        m_pf = pf;

        this.m_timerHandler.postDelayed(this.m_timerRunnable.get(), delayInMs);
        return 0;
    }

    public void killTimer(String str) {
        if (this.m_timerHandler == null || this.m_timerRunnable == null) return;
        //
        this.m_timerHandler.removeCallbacks(this.m_timerRunnable.get());
        this.m_timerRunnable = null;
        this.m_timerHandler = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity: " + getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int doFinish(String hint) {
        //
        Log.d("doFinish: " + hint);
        showInfo_open.qyFuncs.showInfo_open("doFinish "  +  hint);
        //
        finish();
        return 0;
    }
}
