package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.qyWmComm.Consts.CONST_Status_Window_Function_Open;

import android.graphics.Paint;

import com.qycx.base.statuscenter.messagebean.NetConnectEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectStatus;
import com.qycx.base.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class commonSock_android {

    public static class qyFuncs_Droid {

        public static boolean isSocketInvalid_android(commonSock.QY_SOCK pSock) {
            if (pSock == null) return false;
            if (pSock.socketChannel == null) return true;
            return false;
        }

        public static guiShare.PF_bQySockOpen bQySockOpen_android = (commonSock.QY_SOCK pSock) ->
        {
            if (pSock == null) return false;
            return pSock.socketChannel != null;
        };


        public static guiShare.PF_qyDisconnect qyDisconnect_android = (commonSock.QY_SOCK pSock) ->
        {
            //Log.d("qyDisconnect_android");
            //
            if (!isSocketInvalid_android(pSock)) {
                SocketChannel socketChannel = pSock.socketChannel;
                try {
                    socketChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pSock.socketChannel = null;
            }
            if (pSock.selector_r != null) {
                try {
                    pSock.selector_r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pSock.selector_r = null;
            }
            if (pSock.selector_w != null) {
                try {
                    pSock.selector_w.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pSock.selector_w = null;
            }

            if (pSock.bb_buf_r != null) {
                pSock.bb_buf_r = null;
            }

            if (pSock.bb_buf_w != null) {
                pSock.bb_buf_w = null;
            }
        };

        public static guiShare.PF_qyConnect qyConnect_android = (String ipaddr, int port, commonSock.SOCK_PARAM pSockParam, commonSock.QY_SOCK pSock) -> {

            //
            //Log.d("qyConnect_android");

            //
            int iErr = -1;

            if (!isSocketInvalid_android(pSock)) return -1;

            try {
                pSock.socketChannel = SocketChannel.open();
                if (pSock.socketChannel == null) {
                    qyDisconnect_android.call(pSock);
                    return iErr;
                }

                SocketChannel socketChannel = pSock.socketChannel;
                // Connect to the server
                socketChannel.socket().connect(new InetSocketAddress(ipaddr, port),pSockParam.to.iConnect);

                pSock.selector_r = Selector.open();
                pSock.selector_w = Selector.open();
                // Configure the SocketChannel
                socketChannel.configureBlocking(false);
                socketChannel.register(pSock.selector_r, SelectionKey.OP_READ);
                socketChannel.register(pSock.selector_w, SelectionKey.OP_WRITE);
                while (!socketChannel.finishConnect())
                {
                    Log.d(".");
                }
                iErr = 0;
            } catch (IOException e) {
              //  e.printStackTrace();
                //Log.e("connection failed!!!!!!!!!!");
                //
                qyDisconnect_android.call(pSock);
                return iErr;
            }
            return iErr;
        };

        public static guiShare.PF_qySend qySend_android = (commonSock.QY_SOCK pSock, byte[] buf, int offset,  int len, int timeoutVal) ->
        {
            int nWrite = 0;

            SocketChannel socketChannel = pSock.socketChannel;
            Selector selector = pSock.selector_w;
            if(selector == null) return -1;
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isWritable()) {

                        //
                        //buf.position(offset);
                        //buf.limit(offset  +  len);
                        ByteBuffer buf1=ByteBuffer.wrap(buf,offset,len);
                        nWrite = socketChannel.write(buf1);

                        //
                        if  (  false  ) {
                            if (nWrite > 200 && nWrite < 1000) {
                                Log.d(AUDIO_UPLOAD, "<stage6> qySend_android nWrite: " + nWrite + " len: " + len);
                            } else if (nWrite >= 1000) {
                                Log.d(VIDEO_UPLOAD, "<stage6> qySend_android nWrite: " + nWrite + " len: " + len);
                            } else {
                                Log.d("qySend_android: " + nWrite + " len " + len);
                            }
                        }

                        //
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }

            } catch (Exception e) {
                Log.e( "qyWrite_android exception " + e);
                Log.d(VIDEO_UPLOAD + " " +AUDIO_UPLOAD,  "<stage6> qySend_android nWrite: exception ");
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                qyDisconnect_android.call(pSock);
            }
            return nWrite;
        };

        public static guiShare.PF_qyRecv qyRecv_android = (commonSock.QY_SOCK pSock, ByteBuffer buffer, int offset, int len,  int timeoutVal) ->
        {
            int nRead = -1;

            //
            if (null == buffer || buffer.capacity() == 0) {
                Log.e("qy","qyRecv_android failed, buffer is null or cap is 0");
                return -1;
            }

            if  (  offset  <  0  )  {
                Log.e("qy","qyRecv_android failed, offset < 0");
                return  -1;
            }
            if  (  offset  +  len  >  buffer.capacity()) {
                Log.e("qy","qyRecv_android failed, offset + len > cap");
                return  -1;
            }

            SocketChannel channel = pSock.socketChannel;
            Selector selector = pSock.selector_r;
            if(selector == null) {
                Log.e("qy","qyRecv_android failed, selector_r is null");
                return -1;
            }
            //
            try {
                int nReady = selector.select(timeoutVal);
                if ( nReady != 1) {
                    nRead = 0;
                }else{
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (key.isValid() && key.isReadable()) {
                            buffer.position(offset);
                            buffer.limit(offset  +  len);
                            nRead = channel.read(buffer);
                            //
                            if  (  nRead  <  0)  {
                                Log.e("qy","qyRecv_android failed， socketchannel.read returns "  +  nRead);
                            }
                            //
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e( "qy","qyRecv_android exception: " + e);
                //EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                //qyDisconnect_android.call(pSock);
            }
            return nRead;
        };

        //
        // Use select() to determine when
        // data is waiting to be read
        //  返回值：>  0  为有数据
        //			== 0  为超时
        //			<  0  为失败
        public static guiShare.PF_qyWaitForRead qyWaitForRead_android = (commonSock.QY_SOCK pSock, int timeoutInMs) ->
        {
            int iErr = -1;

            try {
                Selector selector = pSock.selector_r;
                if(selector == null) return -1;

                int nReady = selector.select(timeoutInMs);
                if(nReady != 1)
                {
                    iErr = 0;
                }else{
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();

                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isValid() && key.isReadable()) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                    iErr = 1;
                }
            } catch (Exception e) {
                Log.e( "qyWaitForRead_android exception " + e);
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                qyDisconnect_android.call(pSock);
                e.printStackTrace();
                return iErr;
            }


            return iErr;
        };


        public static guiShare.PF_qyWaitForWrite qyWaitForWrite_android = (commonSock.QY_SOCK pSock, int timeoutInMs) ->
        {
            int iErr = -1;
            try {
                Selector selector = pSock.selector_w;
                if(selector == null) return -1;
                int nReady = selector.select( timeoutInMs);
                if (nReady != 1)
                {
                    iErr  =  0;
                }else{
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();

                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isValid() && key.isWritable()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    }
                    iErr = 1;
                }
            } catch (Exception e) {
                Log.e( "qyWaitForWrite_android exception " + e);
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                qyDisconnect_android.call(pSock);
                Log.e( e.getMessage());
                return iErr;
            }

            return iErr;
        };
    }
}
