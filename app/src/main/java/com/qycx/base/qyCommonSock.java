


package com.qycx.base;

import static com.qycx.base.qyCommonFunc9.qyFuncs.qyntohi;
import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;
import static com.qycx.base.tmpFunc.qyFuncs.safeStrnCpy;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class qyCommonSock {
    private static final String TAG = qyCommonSock.class.getSimpleName();

    public static class Consts {

        public final static int INADDR_NONE = 0xffffffff;


    }

    public static class qyFuncs {

        public static void clearQySock(commonSock.QY_SOCK pSock) {

        }

        public static boolean bQySockOpen(commonSock.QY_SOCK pSock) {
            //  return  (  pSock->sockFd  >=  0  );
            //return pSock.sockFd != -1;
            return pSock.socket == null ? false : true;
        }

        public static byte MACRO_byte0(int l) {
            return (byte) ((l & 0x000000ff));
        }

        public static byte MACRO_byte1(int l) {
            return (byte) ((l & 0x0000ff00) >> 8);
        }

        public static byte MACRO_byte2(int l) {
            return (byte) ((l & 0x00ff0000) >> 16);
        }

        public static byte MACRO_byte3(int l) {
            return (byte) ((l & 0xff000000) >> 24);
        }


        public static boolean bulIpValid(int ulIp) {
            ulIp = qyntohi(ulIp);
            if (MACRO_byte0(ulIp) == 0 || MACRO_byte0(ulIp) == 255) return false;
            if (MACRO_byte3(ulIp) == 0 || MACRO_byte3(ulIp) == 255) return false;
            return true;
        }


        public static boolean isValidIPAddress(String ip) {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        public static boolean bIpValid(String ip) {
            long ulIp = 0;

            if (ip == null || ip.isEmpty()) {
                return false;
            }

            try {
                InetAddress ipa = InetAddress.getByName(ip);
                byte[] addrBytes = ipa.getAddress();
                if (addrBytes.length == 4) {
                    for (byte b : addrBytes) {
                        ulIp = (ulIp << 8) | (b & 0xFF);
                    }
                } else {
                    // IPv6 address
                    return false;
                }
            } catch (UnknownHostException e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
                return false;
            }

            return true;// bulIpValid((int) ulIp);
        }


        public static int myinet_addr(String ip) {
            int ulIp = 0;

            if (ip == null) {
                return Consts.INADDR_NONE;
            }

            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                byte[] addrBytes = ipAddress.getAddress();
                if (addrBytes.length == 4) {
                    for (byte b : addrBytes) {
                        ulIp = (ulIp << 8) | (b & 0xFF);
                    }
                } else {
                    // IPv6 address
                    return Consts.INADDR_NONE;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return Consts.INADDR_NONE;
            }

            return ulIp;
        }

        // 辅助方法：将 int 转换为 byte 数组
        private static byte[] intToBytes(int value) {
            return new byte[] {
                    (byte) ((value >> 24) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) (value & 0xFF)
            };
        }

        public static byte[] ulIp2Str(int lVal, byte[] ip, int size) {
            if (ip == null || size < CONST_qyMaxIpLen + 1) {
                return null;
            }

            try {
                InetAddress inetAddress = InetAddress.getByAddress(intToBytes(lVal));
                String ipStr = inetAddress.getHostAddress();
                byte[] ipBytes = ipStr.getBytes();

                // 拷贝 IP 字符串到指定大小的数组中
                System.arraycopy(ipBytes, 0, ip, 0, Math.min(ipBytes.length, size));
            } catch (UnknownHostException e) {
                return null;
            }

            return ip;
        }


        //int getLocalIpBySocket(SOCKET s, String ip)
        public static int getLocalIpBySocket(commonSock.QY_SOCK pSock, byte[] ip) {
            String str_ip = "";

            safeStrnCpy(str_ip, ip, CONST_qyMaxIpLen + 1);

            return 0;

        }


        //int getRemoteIpBySocket(SOCKET s, String ip)
        public static int getRemoteIpBySocket(int s, byte[] ip) {
            String str_ip = "";

            safeStrnCpy(str_ip, ip, CONST_qyMaxIpLen + 1);

            return 0;

        }

    }
}
