


package com.qycx.base;

public class qyTypes {
    public static class QY_BUF {
        public int resourceType;     // 资源类型
        public int len;
        public byte[] data;
    }

    ;

    public static class QY_STR {
        public int len;
        public int resourceType;
        public byte[] szData;
    }

    ;

    public static class QY_DMITEM {
        public int type;
        public String des;
        public String pData;
        //  long				flg;
        //  long				data;

        public QY_DMITEM(int t1) {
            type = t1;
            des = null;
            pData = null;
        }

        public QY_DMITEM(int t1, String d1) {
            type = t1;
            des = d1;
            pData = null;
        }

        public QY_DMITEM(int t1, String d1, String p1) {
            type = t1;
            des = d1;
            pData = p1;
        }
    }

    ;


}
