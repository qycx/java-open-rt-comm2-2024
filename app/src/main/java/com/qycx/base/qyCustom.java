


package com.qycx.base;

import static com.qycx.base.qyComm.Consts.CONST_qyServiceId_is;
import static com.qycx.base.tmpFunc.qyFuncs.myatol;
import static com.qycx.base.tmpFunc.qyFuncs.mystrlen;

import java.nio.ByteBuffer;

import kotlin.Triple;

public class qyCustom {
    public static class Consts {


    }


    public static class qyFuncs {
        //  #define		CONST_qnmVer_is								"024705"			//
        //  #define		CONST_qnmVer_is								"025401"				//
        public final static String CONST_qnmVer_is = "02660994";               //  2017/10/19	"02660258"				//  2017/06/15	"02650267"				//  2016/10/12	//  "02630602"				//  2015/08/21	//  "02622960"				//


        //
        public static String qnmVerStr(int iServiceId) {
            String p = CONST_qnmVer_is;

            switch (iServiceId) {
                case CONST_qyServiceId_is:
                    p = CONST_qnmVer_is;
                    break;
                default:
                    break;

            }

            return p;
        }


        public static int qyGetCustomId() {
            return 0;

        }

        //, int pshVer, byte[] pblVer, byte[] pb_mantissa_ver
        public static Triple<Short,Byte,Byte> parse_qnmVerStr(byte[] ver) {
            if (null == ver) return null;
            if (mystrlen(ver) < 6) return null;

            ByteBuffer buffer = ByteBuffer.wrap(ver);
            short lhVer = (short)buffer.getInt();
            byte blVer = (byte) buffer.getChar();
            byte b_mantissa_ver = (byte) buffer.getChar();
            return new Triple<>(lhVer,blVer,b_mantissa_ver);
        }
    }

}



