package com.qycx.base;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class qyFuncs {

    public static class Consts {
        public final static double Pi = 3.14159;
        public final static int SpeedOfLight = 300000; // km per sec.
        public final static int CONST_qyMaxIpLen = 15;

    }

    static class MyBuffer {
        public ByteBuffer fixedBuffer;//[128];
        public ByteBuffer fixedBuffer1;//[CONST_qyMaxIpLen];

    }


    public static class testCls {
        int i;
        qnmCommProc.QWM_DYN_CFG cnt;

        public testCls() {
            cnt.cnt.usCntPort = 0;

        }
    }

    ;


    public static class MyClass implements AutoCloseable {
        private boolean IsDisposed = false;

        //
        testCls a;

        //
        public int init() {
            a = new testCls();
            return 0;
        }

        public void Dispose() {
            Dispose(true);
            //GC.SuppressFinalize(this);
        }

        protected void Dispose(boolean Disposing) {
            if (!IsDisposed) {
                if (Disposing) {
                    //Clean Up managed resources
                    a = null;
                }
                //Clean up unmanaged resources
            }
            IsDisposed = true;
        }

        @Override
        public void close() throws Exception {
            Dispose(false);
        }
    }


    public static long ArrayToIntptr(byte[] source) {
        if (source == null)
            return 0;


//        byte[] point = source;
//        {
//            long ptr = new long (point);
//            return ptr;
//        }
        return 0;
    }

    //
    static class testChar {
        public String buf;//[128];

    }

    public static String createMD5(String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] inputBytes = input.getBytes();
            byte[] hashBytes = md5.digest(inputBytes);

            // Convert the byte array to hexadecimal String
            StringBuilder sb = new StringBuilder();
            for (byte hashByte : hashBytes) {
                sb.append(String.format("%02x", hashByte));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocalIPAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);

            if (networkInterface != null) {
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int test() {
        /*
        //qnmCommProc_mis kk;
        qnmCommProc_mis.MIS_MSGU msg;

        //
        qySyncCommProc.CQyMalloc_array mallocObj_array = new qySyncCommProc.CQyMalloc_array(5);
        {
            mallocObj_array.mems[0].mallocf(20);
            mallocObj_array.mems[3].mallocf(30);

        }


        //
        int ii = sizeof(MyBuffer);
        int jj = sizeof( char);

        ii = sizeof(QY_CFGITEM_L32);

        long p = 0;
        byte[] pb =  p;
        boolean b = pb == null;
        String s0 = CreateMD5("123");
        Log.d(TAG, s0);

        String str = "127.0.0.1";
        ii = (int) myinet_addr("127.0.0.1");
        Log.d(TAG, "%s", ii);

        str = GetLocalIPAddress();

        GENERIC_Q test_q = new genericQueue.GENERIC_Q();
        initGenericQ("test", "test", 0, 100,  qyMemoryFunc.qyFuncs. test_q);
        int iii = 0;
        int pi = iii;
        long p_pi =  pi;
        qPostMsg(p_pi, Integer.BYTES, test_q);
        exitGenericQ(test_q);


        QY_qBuf2.CQyQ2Help q2Help = new QY_qBuf2.CQyQ2Help();
        {
            int immmm = 9;
        }


        QWM_DYN_CFG a;
        a.cnt.ucbAutoCntIp = true;
        a.cnt.usCntPort = 1;

        //ii = sizeof(testChar.buf);


        long offset = 0;

        int[] iis = {1, 1};
        int x = 100;
        int pi1;
        //int  pi = (int )iis;
        //pi++;

        x = sizeof(QY_MEMORY);


        //b = sizeof(GENERIC_Q_cfgEx);
        x = sizeof(genericQueue.GENERIC_Q_CFG);

        GENERIC_Q mm;
        testCls nn = null;
        if (nn == null) {
        }
        nn = new testCls();
        nn.toString();

        Q_NODE qn;

        String s = "kkkk";


        char[] cs = s.toCharArray();
        x = cs.length;


        //
        MyClass objCls = new MyClass())
        {
            int tii = 0;
            objCls.init();

        }

        //
        MyClass obj = new MyClass();
        testChar tt;
        {
        }


        //testCls b = new testCls();
        return iErr;

        

*/
        return 0;
    }

    public static testCls get_cls() {
        testCls b = new testCls();
        b = null;
        return b;

    }
}
