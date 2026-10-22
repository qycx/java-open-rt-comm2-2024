


package com.qycx.base;

import static com.qycx.base.qyDefs_open.*;
import static com.qycx.base.qyDefs_open.Consts.*;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class qyCommProc {

    public static class Consts {
        public final static int CONST_maxCnt_sema_syncQmObjQ = 10;                                    //  qmObjQ

        public final static String DEFAULT_mutexName_syncQmObjQ = ("m_syncQmObjQ");
        public final static String DEFAULT_semaphoreName_syncQmObjQ = ("sema_syncQmObjQ");
        //
        public final static String DEFAULT_mutexName_syncQmObjQ_client = ("m_syncQmObjQ_cli");         //  2007/06/03
        public final static String DEFAULT_semaphoreName_syncQmObjQ_client = ("sema_syncQmObjQ_cli");      //  2007/06/03

        public final static String DEFAULT_mutexNamePrefix_syncTask = ("m_syncTask");              //  2007/06/27, mutex, prefix + misServName  +  id_send

        //
        public final static int bufSize_publicKeyBlob = 2048;
        public final static int bufSize_sessionKeyBlob = 1024;
        public final static int bufSize_sessionIv = 32;

        //
        public final static int CONST_dataBufSize_iocp = 513 * 1024;//129 * 1024;	//  2011/02/13


        public final static int CONST_qyShowType_qwmComm = 5;
        public final static int CONST_qyShowType_qwmState = 7;
        public final static int CONST_qyShowType_debug = 13;       //
        public final static int CONST_qyShowType_debugWarning = 14;        //  2007/05/07
        public final static int CONST_qyShowType_warning = 15;     //  2007/05/07

        //
        public final static int CONST_uiMaxQNodes_showInfoQ_is = 100;                             //  2014/05/12
        public final static int CONST_uiMaxQNodes_showInfoQ_state_is = 100;                               //  2015/08/22

        //
        public final static int MAX_pipeCli_idleInMs = 5000;

        public final static int QyShowSmall_cntof_whereBuf = 8;
        public final static int QyShowSmall_cntof_doStr = 8;
        public final static int QyShowSmall_cntof_what = 96;


    }


    public static class mySYSTEMTIME {
        public short wYear;
        public short wMonth;
        public short wDayOfWeek;
        public short wDay;
        public short wHour;
        public short wMinute;
        public short wSecond;
        public short wMilliseconds;

        public byte[] toByteArray(ByteBuffer buffer ) {
            //ByteBuffer buffer = ctxThreadShowInfo.mySYSTEMTIME_toByteArray_buffer;//ByteBuffer.allocate(Short.BYTES * 8);
            if  (  buffer.capacity()!= length()  )  return null;
            buffer.clear();
            //
            buffer.putShort(wYear);
            buffer.putShort(wMonth);
            buffer.putShort(wDayOfWeek);
            buffer.putShort(wDay);
            buffer.putShort(wHour);
            buffer.putShort(wMinute);
            buffer.putShort(wSecond);
            buffer.putShort(wMilliseconds);
            return buffer.array();
        }

        public int length(){
            return Short.BYTES * 8;
        }

        public void fromBytes(byte[] data){
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * 8);
            buffer.put(data);
            buffer.flip();
            wYear         = buffer.getShort();
            wMonth        = buffer.getShort();
            wDayOfWeek    = buffer.getShort();
            wDay          = buffer.getShort();
            wHour         = buffer.getShort();
            wMinute       = buffer.getShort();
            wSecond       = buffer.getShort();
            wMilliseconds = buffer.getShort();
        }

        public void memset0()
        {
            wYear  =  0;
            wMonth  =  0;
            wDayOfWeek  =  0;
            wDay  =  0;
            wHour  =  0;
            wMinute  =  0;
            wSecond  =  0;
            wMilliseconds  =  0;

        }

        public int copyFreom(mySYSTEMTIME when_i)
        {
            if  (  when_i ==  null  )  return  -1;

            wYear  =  when_i.wYear;
            wMonth  =  when_i.wMonth;
            wDayOfWeek  =  when_i.wDayOfWeek;
            wDay  =  when_i.wDay;
            wHour  =  when_i.wHour;
            wMinute  =  when_i.wMinute;
            wSecond  =  when_i.wSecond;
            wMilliseconds  =  when_i.wMilliseconds;

            return  0;
        }

    }

    //
    public  static class Var_QY_SHOW_SMALL {
        ByteBuffer  buffer_qyShowSmall;
        ByteBuffer  buffer_when;

        public Var_QY_SHOW_SMALL()
        {
            buffer_qyShowSmall  =  ByteBuffer.allocate(296);
            int length  =  Short.BYTES * 8;
            buffer_when = ByteBuffer.allocate(length);

            //
            return;
        }
    }


    
    //
    //public static class QY_SHOW_SMALL extends qnmCommProc_mis.MIS_MSGU
    public static class QY_SHOW_SMALL
    {
        public int iType;
        //
        public short usStateType;                     //  2022/01/27
        public short usStateSubtype;                      //  2022/01/27
        //
        public int iPos_toDisplay;
        //
        public int iTaskId;                            //  2022/01/27
        //
        public int ulClientIp;                           //  2007/08/21 socket clientIp
        public long who_from;                            //  2015/05/23. WCHAR						who_from[16];
        public long who_to;
        public String whereBuf;//[8];                      // utf8
        public mySYSTEMTIME when = new mySYSTEMTIME();                                //  2007/06/01,
        //
        public short usStep;                              //  2008/04/28,
        public byte ucPercent_showInfoQ;
        public byte uc_reserved;
        //													//
        public int dwProcessId;                      //  2015/08/30
        public int dwThreadId;                           //  2015/08/20
        //
        public String doStr;//[8];                         // utf8
        public String what;//[96];                         // utf8

        public String getWhereBuf() {
            return this.whereBuf;
        }
        public static final int s_Length = 296;
        //
        public static byte[] truncateOrPadToLength(String input, int targetLength,byte[] dstBytes) {
            // 转换字符串为UTF-8字节数组
            if (input == null)
                input = "";
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

            if  (  dstBytes  ==   null  )  return  null;
            if  (  dstBytes.length  <  targetLength)  return  null;

            // 如果字节数组长度小于目标长度，则填充空字节
            if (bytes.length < targetLength) {
                //byte[] paddedBytes = new byte[targetLength];

                //
                System.arraycopy(bytes, 0, dstBytes, 0, bytes.length);

                //
                int i;
                for  (  i  = bytes.length  ;  i  <  targetLength;i++ )  {
                    dstBytes[i]=0;
                }

                //
                return dstBytes;
            }

            // 如果字节数组长度大于或等于目标长度，则截取前targetLength个字节
            //return (bytes.length > targetLength) ? Arrays.copyOf(bytes, targetLength) : bytes;
            System.arraycopy(bytes,0,dstBytes,0, targetLength);
            return dstBytes;

        }
        public void fromBytes(byte[] data){
            int offset = 0;
            ByteBuffer buffer1 = ByteBuffer.allocate(s_Length);
            buffer1.put(data);
            buffer1.flip();
            String temp;
            iType          = buffer1.getInt();
            usStateType    = buffer1.getShort();
            usStateSubtype = buffer1.getShort();
            iPos_toDisplay = buffer1.getInt();
            iTaskId        = buffer1.getInt();
            ulClientIp     = buffer1.getInt();
            who_from       = buffer1.getLong();
            who_to         = buffer1.getLong();

            byte[] where = new byte[16];
            offset = buffer1.position();
            System.arraycopy(data, offset, where , 0, 16);
            temp = new String(where);
            whereBuf = temp.trim();
            buffer1.position(offset + 16);

            offset          = buffer1.position();
            int  len        = when.length();
            byte[] whenlist = new byte[len];
            System.arraycopy(data, offset, whenlist , 0, len);
            when.fromBytes(whenlist);;
            buffer1.position(offset + len);

            usStep              = buffer1.getShort();
            ucPercent_showInfoQ = buffer1.get();
            uc_reserved         = buffer1.get();
            dwProcessId         = buffer1.getInt();
            dwThreadId          = buffer1.getInt();

            byte[] buf3 = new byte[16];
            offset = buffer1.position();
            System.arraycopy(data, offset, buf3, 0, 16);
            temp = new String(buf3);
            doStr = temp.trim();
            buffer1.position(offset + 16);

            byte[] buf4 = new byte[192];
            offset = buffer1.position();
            System.arraycopy(data, offset, buf4, 0, 192);
            temp = new String (buf4) ;
            what = temp.trim();
        }


        public byte[] toByteArray(qyShowInfoCommProc.Ctx_thread_showInfo ctxThreadShowInfo,  Var_QY_SHOW_SMALL tmpVar) {

            ByteBuffer buffer = tmpVar.buffer_qyShowSmall;
            //
            //ByteBuffer buffer = ByteBuffer.allocate(296);
            if  ( buffer==null) {
                Log.d("qy","QY_SHOW_SMALL.toByteArray failed, buffer is null");
                return null;
            }
            if  (  buffer.capacity()!=296) {
                Log.d("qy","QY_SHOW_SMALL.toByteArray failed, buffer.cap != 296");
                return null;
            }
            buffer.clear();

            //
            byte[]p=null;

            //
            buffer.putInt(iType);
            buffer.putShort(usStateType);
            buffer.putShort(usStateSubtype);
            buffer.putInt(iPos_toDisplay);
            buffer.putInt(iTaskId);
            buffer.putInt(ulClientIp);
            buffer.putLong(who_from);
            buffer.putLong(who_to);
            p = truncateOrPadToLength(whereBuf, 16,ctxThreadShowInfo.tmp_byte192);
            if  (  p==null  )  {
                Log.e("qy","287");
                return null;
            }
            buffer.put(p,0,16);
            buffer.put(when.toByteArray(tmpVar.buffer_when));
            buffer.putShort(usStep);
            buffer.put(ucPercent_showInfoQ);
            buffer.put(uc_reserved);
            buffer.putInt(dwProcessId);
            buffer.putInt(dwThreadId);

            //buffer.put(truncateOrPadToLength(doStr, 16));
            p=truncateOrPadToLength(doStr, 16,ctxThreadShowInfo.tmp_byte192);
            if  (  p==null) {
                return  null;
            }
            buffer.put(p,0,16);

            //buffer.put(truncateOrPadToLength(what, 192));
            p=truncateOrPadToLength(what, 192, ctxThreadShowInfo.tmp_byte192);
            if  (  p==null) {
                return  null;
            }
            buffer.put(p,0,192);
            return buffer.array();
        }

        //
        public void memset0()
        {
            iType=0;
            usStateType=0;
            usStateSubtype=0;
            iPos_toDisplay=0;
            iTaskId=0;
            ulClientIp=0;
            who_from=0;
            who_to=0;
            //whereBuf=null;
            when.memset0();
            usStep=0;
            ucPercent_showInfoQ=0;
            uc_reserved=0;
            dwProcessId=0;
            dwThreadId=0;
            //buffer.put(truncateOrPadToLength(doStr, 16));
            //buffer.put(truncateOrPadToLength(what, 192));


        }

    };


    //
    public static class IM_NET_STAT {
        //
        public long ui64MsgDataLenRecvd;                            //  2009/10/10
        public long ui64MsgDataLenSent;
        //
        public int uiInSpeedInKbps;                              //  bps
        public int uiOutSpeedInKbps;
        //
        //  unsigned  __int64								ui64MsgDataLenRecvd_dec;
        //  unsigned  __int64								ui64MsgDataLenSent_dec;
        //
        //  unsigned  long									nMsgRecvd;
        //  unsigned  long									nMsgSent;
        //
        //
    };

    //
    public static class QMC_net_stat {
        public IM_NET_STAT ins=new IM_NET_STAT();
        //
        public int uiInSpeedInKbps_a;
        public int uiOutSpeedInKbps_a;
        //
        public int uiInSpeedInKbps_v;
        public int uiOutSpeedInKbps_v;
        //
        public int uiInSpeedInKbps_f;
        public int uiOutSpeedInKbps_f;

    };


    public static class qyFuncs {

        public static qyTypes.QY_DMITEM[] CONST_qyCmdTable = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(-1, ""),
        };


        public static qyTypes.QY_DMITEM[] CONST_qyRcTable = new qyTypes.QY_DMITEM[]

        {
            new qyTypes.QY_DMITEM(-1, ""),
        };


        /// <summary>
        /// //////////////////////
        /// </summary>
        /// <param name="ph"></param>
        /// <param name="dwMilliseconds"></param>
        /*
        public static void waitForThread(Thread ph, int dwMilliseconds) {
            int dwRet = 0;

            if (null == ph) return;
            
            if (ph != null) {
                if (dwMilliseconds == 0) {
                    if (!ph.isAlive()) {
                        ph = null;
                        return;
                    }
                    return;
                }

                int msPerStep = 100;
                msPerStep = 10;
                int maxStep = dwMilliseconds / msPerStep;
                if (maxStep == 0) maxStep = 1;
                //
                for (int i = 0; i < maxStep; i++) {
                    if (!ph.isAlive()) {
                        ph = null;
                        return;
                    }
                    try {
                        Thread.sleep((int) msPerStep);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }


            return;

        }

         */

        //public static void waitForThread_io(qmCommFunc_mis.MIS_CHANNEL pChannel, int dwMilliseconds,  boolean bForceQuit)
        public static void waitForThread_io(qmCommFunc_mis.MIS_CHANNEL pChannel, boolean bForceQuit)
        {
            int dwRet = 0;

            Thread ph = pChannel.hThread_io;

            if (null == ph) return;

            if (ph != null) {
                if (!bForceQuit) {
                    if (!ph.isAlive()) {
                        try {
                            ph.join();
                            pChannel.hThread_io = null;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return;
                    }
                    return;
                }

                /*
                int msPerStep = 100;
                msPerStep = 10;
                int maxStep = dwMilliseconds / msPerStep;
                if (maxStep == 0) maxStep = 1;
                //
                for (int i = 0; i < maxStep; i++) {
                    if (!ph.isAlive()) {
                        pChannel.hThread_io = null;
                        return;
                    }
                    try {
                        Thread.sleep((int) msPerStep);
                    } catch (InterruptedException e) {
                        Log.e(e.getMessage());
                    }
                }

                 */
                try {
                    ph.join();
                    pChannel.hThread_io=null;

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }


            return;

        }


        //
        //public static void waitForThread_toSend(qmCommFunc_mis.MIS_CHANNEL pChannel, int dwMilliseconds,boolean bForceQuit)
        public static void waitForThread_toSend(qmCommFunc_mis.MIS_CHANNEL pChannel, boolean bForceQuit)
        {
            int dwRet = 0;

            Thread ph = pChannel.hThread_toSend;

            if (null == ph) return;

            if (ph != null) {

                if (!bForceQuit) {
                    if (!ph.isAlive()) {
                        try {
                            ph.join();
                            pChannel.hThread_toSend = null;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return;
                    }
                    return;
                }

                /*
                int msPerStep = 100;
                msPerStep = 10;
                int maxStep = dwMilliseconds / msPerStep;
                if (maxStep == 0) maxStep = 1;
                //
                for (int i = 0; i < maxStep; i++) {
                    if (!ph.isAlive()) {
                        pChannel.hThread_toSend = null;
                        return;
                    }
                    try {
                        Thread.sleep((int) msPerStep);
                    } catch (InterruptedException e) {
                        Log.e(e.getMessage());
                    }
                }

                 */
                try {
                    ph.join();
                    pChannel.hThread_toSend=null;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }


            return;

        }


        //
        public static boolean bTaskAlive(int iStatus) {
            switch (iStatus) {
                case 0:
                case CONST_imTaskStatus_req:
                case CONST_imTaskStatus_resp:
                case CONST_imTaskStatus_applyToSend:
                case CONST_imTaskStatus_waitToSend:
                case CONST_imTaskStatus_applyToRecv:
                case CONST_imTaskStatus_waitToRecv:
                case CONST_imTaskStatus_acceptedByReceiver:
                case CONST_imTaskStatus_dualByReceiver:
                    return true;
                default:
                    break;
            }
            return false;

        }

        //
        public static boolean bTaskNeedAcception(int iStatus) {
            switch (iStatus) {
                case 0:
                case CONST_imTaskStatus_resp:
                case CONST_imTaskStatus_applyToRecv:
                    return true;
                default:
                    break;
            }
            return false;
        }

    }

}
