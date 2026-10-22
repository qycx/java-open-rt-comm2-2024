package com.qycx.base;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class RSACryptoServiceProvider {
    private final static String TAG=RSACryptoServiceProvider.class.getSimpleName();
    public int KeySize;
    byte[] publicKey;
    Cipher cipher;
    public RSACryptoServiceProvider(){
        try {
            cipher=Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public byte[] Encrypt(byte[] tmpData, boolean b) {
        return new byte[0];
    }

    public void Dispose() {

    }

}
