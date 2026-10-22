


package com.qycx.base;

import static com.qycx.base.tmpFunc.qyFuncs.safeStrnCpy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class qwmMd5 {
    public static class qyFuncs {

        public static String bQyGetStrMd5_x(String str) {
            try {
                // Use input string to calculate MD5 hash
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] inputBytes = str.getBytes();
                byte[] hashBytes = md5.digest(inputBytes);

                // Convert the byte array to hexadecimal string
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hashBytes.length; i++) {
                    sb.append(String.format("%02x", hashBytes[i]));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
