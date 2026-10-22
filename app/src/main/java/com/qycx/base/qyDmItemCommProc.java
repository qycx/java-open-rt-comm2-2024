



package com.qycx.base;
public class qyDmItemCommProc
{
    public static class qyFuncs
{
		public static String qyGetDesByType(qyTypes.QY_DMITEM[] pTable, long type)
		{
			int i;

			if (null==pTable) return null;           //  2005/02/08

			for (i = 0; pTable[i].type != -1; i++)
			{
				if (pTable[i].type == type) return pTable[i].des;
			}
			return null;

		}

		//  该函数专门为提供显示用，防止返回NULL导致指针错误
		public static String qyGetDesByType1(qyTypes.QY_DMITEM[] pTable, long type)
		{
			String p = null;
			return (null!=(p = qyGetDesByType(pTable, type)) ? p : (""));
		}

		//
		public static int qyGetTypeByDes(qyTypes.QY_DMITEM[] pTable,String des)
        {
			int i;
			
			if (null == pTable) return -1;
			if (null == des) return -1;

			//
			for (i = 0; pTable[i].type != -1; i++)
            {
				if  (  pTable[i].des ==null) continue;;
				if (pTable[i].des.equals( des)) {
					return pTable[i].type;
				}
            }
			return -1;
        }


	}
}
