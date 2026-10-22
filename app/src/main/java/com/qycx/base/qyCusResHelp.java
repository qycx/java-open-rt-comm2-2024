package com.qycx.base;

import static com.qycx.base.qnmCustomStr_ts.qyFuncs.CONST_strTable_ts;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;

public class qyCusResHelp
{
    public static class qyFuncs
    {
        public static String getResStr(Object p0, Object pQnmCusResInfo, int iResId)
        {
            String p = null;

            p= qyGetDesByType1(CONST_strTable_ts, iResId);



            return p!=null ? p : "";
        }

    }
}
