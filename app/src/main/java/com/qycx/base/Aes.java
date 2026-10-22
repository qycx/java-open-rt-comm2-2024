package com.qycx.base;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static final int KEY_SIZE = 256;
    public byte[] Key;
    public byte[] IV;

    public SecretKey secretKey;
    public IvParameterSpec iv;

//    public static String base64Key = "NdlcpVagugt+IJCsCse3Cc3lHJYPEFETGmLDHD/y+kg=";
//    public static String base64IV = "e6c81Zokxs2WWKcId/pAxQ==";

    public static Aes Create() {

        return new Aes();
    }

    public void GenerateKey() {
        try {
            SecretKey secretKey = generateAESKey(KEY_SIZE);
            this.secretKey = secretKey;
            Key = secretKey.getEncoded();
        } catch (Exception e) {
            Log.e(e.getMessage());
            return;
        }
    }

    public void GenerateIV() {
        IvParameterSpec ivParameterSpec = generateIV();
        this.iv = ivParameterSpec;
        IV = ivParameterSpec.getIV();
    }

    public void Dispose() {

    }

    // 生成AES密钥
    public static SecretKey generateAESKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(keySize);
        return keyGen.generateKey();
    }

    // 生成初始化向量（IV）
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // AES块大小为16字节
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }


    public static SecretKey getSecretKeyFromBase64(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public static IvParameterSpec getIvParameterSpecFromBase64(String base64Iv) {
        byte[] decodedIv = Base64.getDecoder().decode(base64Iv);
        return new IvParameterSpec(decodedIv);
    }
    public ByteBuffer encrypt(ByteBuffer plainBuffer) throws Exception {
        return encrypt(plainBuffer, secretKey, iv);
    }

    public static ByteBuffer encrypt(ByteBuffer plainBuffer, SecretKey keySpec, IvParameterSpec ivSpec) throws Exception {
        /*
        byte[] plainText = new byte[plainBuffer.remaining()];
        plainBuffer.get(plainText);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText);

        return ByteBuffer.wrap(encryptedBytes);

         */

        if  (  plainBuffer.position()  !=  0  )  {
            Log.d("qy", "encrypt error");
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(plainBuffer.array(),0,  plainBuffer.remaining());

        return ByteBuffer.wrap(encryptedBytes);


    }

    public ByteBuffer decrypt(ByteBuffer cipherTextBuffer) {
        return decrypt(cipherTextBuffer, secretKey, iv);
    }


    public static ByteBuffer decrypt(ByteBuffer cipherTextBuffer, SecretKey keySpec, IvParameterSpec ivSpec) {
        try {
            /*
            byte[] cipherText = new byte[cipherTextBuffer.remaining()];
            cipherTextBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherText);

            return ByteBuffer.wrap(decryptedBytes);

             */
            if  (  cipherTextBuffer.position()!=0) {
                Log.d("qy","decrypt err");
            }

            //
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherTextBuffer.array(),0,cipherTextBuffer.remaining());

            return ByteBuffer.wrap(decryptedBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Base64编码
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // Base64解码
    public static byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }

}
