


package com.qycx.base;

import static com.qycx.base.commonSock.Consts.*;

import com.qycx.base.util.Log;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class commonSock {

    public static class Consts {
        public final static int INVALID_SOCKET = (~0);

        public final static int DEFAULT_QY_ACCEPT_TIMEOUT = 8000;  //  5000
        public final static int DEFAULT_QY_CONNECT_TIMEOUT = 5000; //  2000
        public final static int DEFAULT_QY_WRITE_TIMEOUT = 5000;   //  2000
        public final static int DEFAULT_QY_READ_TIMEOUT = 20000;   //  10000
        public final static int DEFAULT_qyTo_select = 5000;

    }


    public static class QY_SOCK {

        //public int sockFd_unused;      //	第一个成员是asrSock的，应予以保持，包括成员与在结构中的位置	
        //SOCKET sockFd;      //	2014/02/07. 第一个成员是asrSock的，应予以保持，包括成员与在结构中的位置	
        public Socket socket;

        //
        public byte[] buf_r;
        public byte[] buf_w;

        public SocketChannel socketChannel;

        public ByteBuffer bb_buf_r;
        public ByteBuffer bb_buf_w;
        public Selector selector_r;
        public Selector selector_w;
    };


    public static class SOCK_TIMEOUT {
        public int iAccept;
        public int iConnect;
        public int iRead;
        public int iWrite;
        public int iSelect;        //  仅在某些试探状态中使用，读写数据时使用iRead, iWrite
    };

    public static class SOCK_PARAM {
        public int capacity_buf_r;   //读取buf的容量
        public int capacity_buf_w;  //写buf的容量

        //
        public commonSock.SOCK_TIMEOUT to;
    };

    //
    public static class qyFuncs {

        // 持续接收内容，将内容接收完全。
        public static int qyRecvEx(commonSock.QY_SOCK pAsrSock, ByteBuffer buffer, int uiLen, int timeoutVal) {
            if (buffer == null) {
                Log.e("qy","qyRecvEx failed, buffer is null");
                return -1;
            }
            //
            if  (uiLen  <=0  )  {
                Log.e("qy","qyRecvEx failed, uiLen <=0");
                return  -1;
            }
            if  (  buffer.capacity()  <  uiLen)  {
                Log.e("qy","qyRecvEx failed, buffer,cap < uiLen");
                return  -1;
            }

            //
            buffer.clear();

            //
            while (buffer.position() < uiLen) {
                int offset = 0;
                int tmpLen  =  0;

                offset = buffer.position();
                tmpLen = uiLen-buffer.position();

                int tmpnRead  = guiShare.GuiShare.pf_qyRecv.call(pAsrSock, buffer,offset,tmpLen, timeoutVal);
                if  (  tmpnRead  <=0  ) {
                    Log.e("qy","qyRecvEx 96 failed, pf_qyRecv returns "  +  tmpnRead);
                    return -1;
                }
            }

            //
            return uiLen;

        }

        public static int setSockTimeout(commonSock.SOCK_TIMEOUT p) {
            p.iAccept = DEFAULT_QY_ACCEPT_TIMEOUT;
            p.iConnect = DEFAULT_QY_CONNECT_TIMEOUT;
            p.iRead = DEFAULT_QY_READ_TIMEOUT;
            p.iWrite = DEFAULT_QY_WRITE_TIMEOUT;
            p.iSelect = DEFAULT_qyTo_select;

            return 0;
        }

    }
}

