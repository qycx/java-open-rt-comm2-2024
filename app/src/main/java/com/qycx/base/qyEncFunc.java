

package com.qycx.base;
public class qyEncFunc
{

public static class Consts
{
    public final static int CONST_qyEncType_null = 0;
    public final static int CONST_qyEncType_des = 1;
    public final static int CONST_qyEncType_qnm = 2;       //  不用
    public final static int CONST_qyEncType_qwm = 3;       //  2004/01/02
    public final static int CONST_qyEncType_rsa = 4;		//  2008/06/09
    public final static int CONST_qyEncType_rsa1 = 5;		//  2008/06/09

    //
    public final static int CONST_qyEncType_aes256 = 100;

}


    public static class QY_ENC_CTX
    {
        public static class QY_ENC_CTX_var
        {
            public int type;            //通信加密类型
        };

        public QY_ENC_CTX_var common=new QY_ENC_CTX_var();
};


public static class qyFuncs
{
}
}
