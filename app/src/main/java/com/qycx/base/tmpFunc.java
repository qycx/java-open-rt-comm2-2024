package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qyMsgFunc.qyFuncs.qySendMsg;

import com.qycx.base.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class tmpFunc {
//    private static final String TAG = tmpFunc.class.getSimpleName();

    public static class qyFuncs {
        public static int mystrlen(byte[] str) {
            return str.length;
        }

        public static int mywcslen(String str) {
            return str.length();
        }

        public static int mylstrlen(String lpString) {
            return lpString.getBytes().length;
        }


        //
//        public static String String dst, String src, int iMaxLength)
//        {
//            return dst, src, iMaxLength);
//        }

//        public static String String dst, String str_src, int n)
//        {
//            if (dst == null) return null;
//            if (str_src == null) return null;
//            System.arraycopy(str_src,0,dst,0,n);
//            return dst;
//        }

        public static byte[] mystrncpy(byte[] destination, byte[] source, int num) {
            if (destination == null || source == null || num == 0) return null;

            safeStrnCpy(source, destination, (int) num);
            return destination;
        }


        public static void safeStrnCpy(String strIn, byte[] strOut, int size) {
            if (size == 0 || strIn == null || strOut == null) return;
            byte[] b_strIn = strIn.getBytes(StandardCharsets.UTF_8);
            int len = Math.min(size - 1, b_strIn.length);
            System.arraycopy(b_strIn, 0, strOut, 0, len);
            strOut[len] = (byte) 0;
        }


        public static void safeStrnCpy(byte[] strIn, byte[] strOut, int size) {
            if (size == 0 || strIn == null || strOut == null) return;
            int i;
            for (i = 0; i < size - 1; i++) {
                strOut[i] = strIn[i];
                if (strIn[i] == 0) break;
            }
            if (i == size - 1) strOut[i] = (byte) 0;
            return;
        }


        public static void safeWcsnCpy(String strIn, char[] strOut, int cntof_strOut) {
            if (cntof_strOut == 0 || strIn == null || strOut == null || strOut.length < cntof_strOut)
                return;
            int i;
            if (strIn.length() == 0 || strOut.length == 0) return;
            for (i = 0; i < cntof_strOut - 1; i++) {
                strOut[i] = strIn.charAt(i);
                if (strIn.charAt(i) == 0) break;
            }
            if (i == cntof_strOut - 1) strOut[i] = (char) 0;
        }

        public static void safeTcsnCpy(String strIn, String strOut, int cntof_strOut) {
            safeWcsnCpy(strIn, strOut.toCharArray(), cntof_strOut);
        }

        public static int mytcsicmp(String string1, String string2) {
            return string1.compareTo(string2);
        }

        // 定义一个静态的起始时间，1970年1月1日
        public static Instant dt1970 = Instant.ofEpochSecond(0);

        // 获取当前时间距离1970年1月1日的秒数，并可选地将秒数存储在传入的参数中
        public static long mytime() {
            return System.currentTimeMillis() / 1000;
        }

        // 将 IPv4 地址字符串转换为整数表示
        public static int myinet_addr(byte[] cp) {
            if (cp == null) {
                return 0;
            }

            try {
                String ipAddress = new String(cp);
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                byte[] addressBytes = inetAddress.getAddress();

                // 将四个字节的地址转换为整数
                int result = 0;
                for (byte b : addressBytes) {
                    result = (result << 8) | (b & 0xFF);
                }

                return result;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return 0;
            }
        }

        // 将字节数组转换为 UTF-8 编码的字符串
        public static String mytoString(byte[] p) {
            /*
            int len = mystrlen(p);
            byte[] a = new byte[len];
            System.arraycopy(p, 0, a, 0, len);

             */
            //byte[]a=p;
            return new String(p, StandardCharsets.UTF_8);
        }

        public static boolean myWaitOne(EventWaitHandle s, int millseconds) {
            if (s == null) return false;
            return s.WaitOne(millseconds);
        }

        public static boolean myWaitOne(Semaphore s, int millseconds) {
            if (s == null) return false;
            boolean ret = false;
            try {
                ret = s.tryAcquire(1, millseconds, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
            return ret;
        }

        public static void myEventSet(EventWaitHandle evt) {
            if (evt == null) return;
            evt.trigger();
//            boolean bRet = false;
//            if(evt==null) return bRet;
//            try {
//                evt.Set();
//                bRet = true;
//            } catch (Exception e) {
//                Log.e(e.toString());
//                e.printStackTrace();
//            }
//
//            return bRet;
        }

        public static void mySetEvent(EventWaitHandle evt) {
            myEventSet(evt);
        }


        public static int mySemaRelease(Semaphore s, int releaseCount) {
            int iRet = -1;
            if (s == null) return iRet;
            try {
                s.release(releaseCount);
                iRet = 0;
            } catch (Exception e) {
                Log.d(e.toString());
                return iRet;
            }

            return iRet;
        }

        public static long myGetTickCount(ctxQmThread.CTX_qm_thread cqt) {
            if (null == guiShare.GuiShare.pf_myGetTickCount) return 0;
            return guiShare.GuiShare.pf_myGetTickCount.call();
        }

        public static long myGetTickCount64(ctxQmThread.CTX_qm_thread cqt) {
//            return (System.currentTimeMillis());
            return System.nanoTime() / 1000000;
        }

        public static long mytimeGetTime() {
            return tmpFunc.qyFuncs.myGetTickCount(null);
        }

        public static int myGetCurrentThreadId() {

            if (null == guiShare.GuiShare.pf_myGetTid) return 0;

            return (int) guiShare.GuiShare.pf_myGetTid.call();


        }

        public static int myGetCurrentProcessId() {
            //Thread.
            if (null == guiShare.GuiShare.pf_myGetPid) return 0;
            return (int) guiShare.GuiShare.pf_myGetPid.call();
        }


        public static void mySleep(int dwMilliseconds) {
            try {
                Thread.sleep((int) dwMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static boolean bEqual_byteArray(byte[] b1, byte[] b2) {
            //1：

            //2：
            if (b1.length != b2.length) {
                return false;
            }

            //3：
            boolean isEqual = true;

            for (int i = 0; i < b1.length; i++) {
                if (b1[i] != b2[i]) {
                    //System..println("different");
                    isEqual = false;
                    break;
                }
            }
            return isEqual;
        }

        //
        public static void MACRO_mysafeFree(byte[] x) {
//            if ((x != null)) { qyMemoryFunc.qyFuncs.myfree(x); (x) = null; }
        }

        //
        public static long myatol(byte[] bstr) {
            long lVal = 0;
            String str = mytoString(bstr);
            return Long.parseLong(str);

        }

        //
        public static String _T(String str) {
            if (str == null) str = "";
            return str;
        }

        public static void OutputDebugString(String str) {

        }

        public static boolean byte2bool(byte b) {
            return b != 0;
        }

        public static byte bool2byte(boolean b) {
            return (byte) (b ? 1 : 0);
        }

        public static boolean myCopyFile(String lpExistingFileName, String lpNewFileName, boolean bFailIfExists) {
            return false;
        }


        public static boolean myDeleteFile(String lpFileName) {
            return false;
        }


        public static void myGetLocalTime(qyCommProc.mySYSTEMTIME p) {
            LocalDateTime now = LocalDateTime.now();

            p.wYear = (short) now.getYear();
            p.wMonth = (short) now.getMonthValue();
            p.wDayOfWeek = (short) now.getDayOfWeek().getValue();
            p.wDay = (short) now.getDayOfMonth();
            p.wHour = (short) now.getHour();
            p.wMinute = (short) now.getMinute();
            p.wSecond = (short) now.getSecond();
            p.wMilliseconds = (short) (now.getNano() / 1_000_000);
        }

        public static int mylstrcmpi(String name1, String name2) {
            if (name1 == null || name2 == null) return -1;
            return name1.compareTo(name2);
        }

        //
        public static int msgToHead_snd(com.qycx.base.qnmCommProc_mis.MIS_MSGU  pMsg, QY_qBuf2.HeadData_snd head)
        {
            head.msg_uiType  =  pMsg.uiType;
            //
            switch  (  head.msg_uiType)  {
                case  CONST_misMsgType_req:
                    qnmCommProc_mis.MIS_MSG_REQ  req  =  (qnmCommProc_mis.MIS_MSG_REQ)pMsg;
                      head.dataLen  =  req.lenInBytes;
                      //
                      head.ucFlg  =  req.ucFlg;
                      head.usCode  =  req.usCode;
                      head.tStartTime  =  req.tStartTime;
                      head.uiTranNo=req.uiTranNo;
                      head.usSeqNo=req.usSeqNo;
                      //
                        //
                      break;
                case  CONST_misMsgType_talk:
                    qnmCommProc_mis.MIS_MSG_TALK  talk  =  (  qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                    head.dataLen=talk.lenInBytes;

                    //
                    head.ucFlg  =  talk.ucFlg;
                    head.usCode  =  talk.usCode;
                    head.tStartTime  =  talk.tStartTime;
                    head.uiTranNo=talk.uiTranNo;
                    head.usSeqNo=talk.usSeqNo;

                    //
                    break;
                case  CONST_misMsgType_task:
                case CONST_misMsgType_outputTask:
                      qnmCommProc_mis.MIS_MSG_TASK  task  =  (  qnmCommProc_mis.MIS_MSG_TASK)pMsg;
                      head.dataLen=task.lenInBytes;
                      //
                      head.ucFlg           = task.ucFlg;
                      head.usCode          = task.usCode;
                      head.tStartTime      = task.tStartTime;
                      head.uiTranNo        = task.uiTranNo;
                      head.usSeqNo         = task.usSeqNo;
                      head.dwTickCnt_start = task.dwTickCnt_start;
                    //
                    head.dwTickCnt_start=task.dwTickCnt_start;

                    //
                    break;
                default:
                        return  -1;

            }

            //


            return  0;
        }
    }
}
