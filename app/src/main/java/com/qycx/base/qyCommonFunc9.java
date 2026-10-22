

//
package com.qycx.base;

import static com.qycx.base.qyCommonFunc9.Consts.cntof_buf1024;
import static com.qycx.base.qyCommonFunc9.Consts.cntof_buf256;
import static com.qycx.base.qyMemoryFunc.qyFuncs.mymemcpy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class qyCommonFunc9 {


    public static class Consts {
        public final static int cntof_buf1024 = 1024;
        public final static int cntof_buf256 = 256;
    }


    static class TMP_buf4 {
        public byte buf[] = new byte[4];
    }

    static class TMP_buf2 {
        public byte[] buf = new byte[2];
    }

    static class TMP_buf8 {
        public byte[] buf = new byte[8];
    }

    public static class TMP_buf256 {
        public byte[] buf = new byte[cntof_buf256];
    }

    public static class TMP_buf1024 {
        public byte[] buf = new byte[cntof_buf1024];
    }


    //
    public static class qyFuncs {

        public static int qyhtoni(int x) {
            byte[] buf = new byte[4];

            buf[0] = (byte) ((x & 0xff000000) >>> 24);
            buf[1] = (byte) ((x & 0xff0000) >>> 16);
            buf[2] = (byte) ((x & 0xff00) >>> 8);
            buf[3] = (byte) (x & 0xff);

            return ByteBuffer.wrap(buf).getInt();
        }


        public static int qyntohi(int x) {
            int iRet;
            //unsigned  char buf[4];
            TMP_buf4 buf4 = new TMP_buf4();
            ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES);
            buffer.putInt(x);
            //qyMemoryFunc.qyFuncs.mymemcpy(buf4.buf, buffer.array(), 4);
            iRet = (buf4.buf[0] << 24) + (buf4.buf[1] << 16) + (buf4.buf[2] << 8) + buf4.buf[3];
            return iRet;
        }

        public static short qyhtons(short x) {
            byte[] buf = new byte[2];

            buf[0] = (byte) ((x >>> 8) & 0xff);
            buf[1] = (byte) (x & 0xff);

            return (short) (((buf[0] & 0xff) << 8) | (buf[1] & 0xff));
        }

        public static short qyntohs(short x) {
            byte[] buf = new byte[2];

            buf[0] = (byte) ((x >>> 8) & 0xff);
            buf[1] = (byte) (x & 0xff);

            return (short) (((buf[0] & 0xff) << 8) | (buf[1] & 0xff));
        }

        public static long qyhtonl64(long x) {
            byte[] buf = new byte[8];

            buf[0] = (byte) ((x >>> 56) & 0xff);
            buf[1] = (byte) ((x >>> 48) & 0xff);
            buf[2] = (byte) ((x >>> 40) & 0xff);
            buf[3] = (byte) ((x >>> 32) & 0xff);
            buf[4] = (byte) ((x >>> 24) & 0xff);
            buf[5] = (byte) ((x >>> 16) & 0xff);
            buf[6] = (byte) ((x >>> 8) & 0xff);
            buf[7] = (byte) (x & 0xff);

            return ByteBuffer.wrap(buf).getLong();
        }

        public static long qyntohl64(long x) {
            byte[] buf = new byte[8];
            long l64Ret = 0;

            for (int i = 0; i < 8; i++) {
                buf[i] = (byte) ((x >>> ((7 - i) * 8)) & 0xff);
                l64Ret += ( (buf[i] & 0xff) << ((7 - i) * 8));
            }

            return l64Ret;
        }

        public static void MACRO_htons(int s) {
            s = qyhtons((short) s);
        }

        public static void MACRO_htoni(int l) {
            l = qyhtoni(l);
        }

        public static void MACRO_htonl64(long l64) {
            l64 = qyhtonl64(l64);
        }

        public static void MACRO_ntohs(int s) {
            s = qyntohs((short) s);
        }

        public static void MACRO_ntohi(int l) {
            l = qyntohi(l);
        }

        public static void MACRO_ntohl64(long l64) {
            l64 = qyntohl64(l64);
        }
    }
}




