package com.qycx.base;

import static com.qycx.base.qyCommProc.Consts.bufSize_publicKeyBlob;

import android.util.Log;

public class qmcCrypt {
    private static final String TAG = qmcCrypt.class.getSimpleName();
    public static class qyFuncs {
        //加密初始化
        public static int initCrypt_qmc(ctxQmc.CCtxQmc pVar, qmCommFunc_mis.MIS_CNT pMisCnt, String szContainer, String szProvider, boolean bNewKeySetIfNotExists, qmCrypt.ENC_CTX_session pEncCtx) {
            int iErr = -1;
            try {
                if(pMisCnt.commEncCtx.common.type == 0) return 0;
                if (pMisCnt.commEncCtx.m_var.uiBlobLen_publicKey == 0) return 0;

                //
                byte[] publicKey = new byte[bufSize_publicKeyBlob];
                System.arraycopy(pMisCnt.commEncCtx.m_var.publicKeyBlob.array(),0,publicKey,0,pMisCnt.commEncCtx.m_var.uiBlobLen_publicKey);

                //
                if (pEncCtx.m_aes == null) {
                    pEncCtx.m_aes = Aes.Create();
                    pEncCtx.m_aes.GenerateKey();
                    pEncCtx.m_aes.GenerateIV();

                    //
                    Log.d("qy","initCrypt_qmc. aes.key generated");
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                return iErr;
            }

            iErr = 0;

            

            return iErr;
        }

        //加密的清理动作
        public static void exitCrypt_qmc(ctxQmc.CCtxQmc pVar, qmCommFunc_mis.MIS_CNT pMisCnt, qmCrypt.ENC_CTX_session pEncCtx) {
            int iErr = -1;

            try {
                if (pEncCtx.m_aes != null) {
                    pEncCtx.m_aes.Dispose();
                    pEncCtx.m_aes = null;
                }
            } catch (Exception e) {

            }

            iErr = 0;
            
            return;
        }


    }
}
