package com.qycx.base;

import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_qwm;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_rsa1;


import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class qmCrypt {
    
    public static class Consts {
        public final static String CONST_cspContainer_qmd = ("QmdKeyContainer");           //  name of the key container to be used

        public final static String CONST_cspContainer_qmc = ("QmcKeyContainer");           //  name of the key container to be used
        public final static String CONST_mutexName_syncKey_qmc = ("QmcSyncKey");

        public final static String DEFAULT_cspProvider = "Microsoft Enhanced Cryptographic Provider v1.0";

        public final static int CONST_encryptBlockSize = 8;
    }


    public static class ENC_CTX_session extends qyEncFunc.QY_ENC_CTX {

        public Aes m_aes;

        //
        public static class ENC_CTX_session_var {
            public String mutexName_syncCrypt;//[cntof_mutexName];                  //  2008/10/12, 对每个会话密钥，要保证线程同步。微软的api不支持在2个线程里同时用一个密钥
        }

        ;
        public ENC_CTX_session_var m_var = new ENC_CTX_session_var();

    }

    ;

    public static class qyFuncs {


        public static ByteBuffer encryptData(qmCrypt.ENC_CTX_session pEncCtx, ByteBuffer data, int puiDataLen) {
            int iErr = -1;
            int uiDataLen = 0;
            ByteBuffer encrypt = null;

            if (pEncCtx.common.type == 0) {
                data.flip();
                return data;
            }
            if (null == data || 0 == puiDataLen) return null;
            uiDataLen = puiDataLen;

            // 2015/08/28
            if (pEncCtx.common.type == CONST_qyEncType_qwm) {
                Log.d( "not supported");
                return null;
            }

            // 2015/08/28
            if (pEncCtx.common.type != CONST_qyEncType_rsa1) {
                return null;
            }

            try {
                // 调用AES加密函数
                data.flip();
                encrypt = Aes.encrypt(data, pEncCtx.m_aes.secretKey, pEncCtx.m_aes.iv);

            } catch (Exception e) {
                Log.e( "qy","encryptData exception " + e.getMessage());
                return null;
            }

            return encrypt;
        }

        //解密
        public static ByteBuffer decryptData(qmCrypt.ENC_CTX_session pEncCtx, ByteBuffer data) {
            if (pEncCtx.common.type == 0) return data;

            if (null == data || data.limit() == 0) return null;

            if (pEncCtx.common.type == CONST_qyEncType_qwm) {
                Log.d( "not supported");
                return null;
            }

            if (null == pEncCtx.m_aes) {
                return null;
            }

            try {
                return Aes.decrypt(data, pEncCtx.m_aes.secretKey, pEncCtx.m_aes.iv);
            } catch (Exception e) {
                Log.d( "qy","decryptData. exception"  +  e.getMessage());
                return null;
            }

        }

    }

}



