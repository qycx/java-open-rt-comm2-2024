package com.qycx.base;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class EventWaitHandle extends Semaphore {
    private final Object lock = new Object();

    public EventWaitHandle(boolean b, EventResetMode eventResetMode) {
        super(100);
    }

    public EventWaitHandle(boolean b, EventResetMode eventResetMode, String str, int permit) {
        super(permit);
    }

    public EventWaitHandle(boolean b, EventResetMode eventResetMode, String str) {
        super(100);
    }

    public void Close() {
        trigger();
    }

    public boolean WaitOne(int millseconds) {
        boolean ret = false;
        try {
            ret = this.tryAcquire(1, millseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
        return ret;
    }

    public boolean Set() {
        synchronized (lock) {
            lock.notify();
        }
        return true;
    }

    public void trigger() {
        this.release();
    }
}
