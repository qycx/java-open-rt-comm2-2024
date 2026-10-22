



package com.qycx.base;

import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;

import java.nio.ByteBuffer;

public class qyObjs
{

    public  static class QY_LOGICAL_SERVER
    {
        public String ip;  //当前要是用的服务器ip
        public int port;  //当前要使用的端口
    }
    ;

    public  static class QWM_MACS_INFO
    {
        //unsigned char macs[CONST_qnmMaxMacs][CONST_qyMacLen  +  1];
				 public byte nMacs;
    }
    ;


}
