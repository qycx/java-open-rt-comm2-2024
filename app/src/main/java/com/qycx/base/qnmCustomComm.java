
package com.qycx.base;



public class qnmCustomComm
{
    public static class qyFuncs
{

		public static  int regInfo2Desc(Object p0, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, qnmCommProc_mis.MY_REG_DESC pDesc, String talkerDesc, int uiCnt_talkerDesc, String  displayName, int uiCnt_displayName)
		{
			int iErr = -1;
			int index;
			//  unsigned  int			uiFieldId;

			pDesc.pZb = "";
			pDesc.pBb = "";
			pDesc.pDw = "";
			pDesc.pBm = "";
			pDesc.pSyr = "";
			//
			if  (  pRegInfo.dw==null) pRegInfo.dw="";
			if(pRegInfo.bm==null)pRegInfo.bm="";
			if(pRegInfo.syr==null)pRegInfo.syr="";


			//
			pDesc.pDw = new String(pRegInfo.dw);
			pDesc.pBm = new String(pRegInfo.bm);
			pDesc.pSyr = new String(pRegInfo.syr);
			//			
			String str;
			//
			if (pDesc.pSyr.length()!=0)
			{
				if (talkerDesc != null)
				{
					str = String.format("%s - %s - %s",
						//_sntprintf(talkerDesc, uiCnt_talkerDesc, ("%s - %s - %s"), 
						pDesc.pDw, pDesc.pBm, pDesc.pSyr);
					talkerDesc=str;// (int)uiCnt_talkerDesc);
				}

				if (displayName != null)
				{
					//_sntprintf(displayName, uiCnt_displayName, ("%s"), pDesc->pSyr);
					displayName= pDesc.pSyr;//, (int)uiCnt_displayName);
				}
			}
			else
			{
				if (talkerDesc != null)
				{
					//_sntprintf(talkerDesc, uiCnt_talkerDesc, ("(%I64u)"), pRegInfo->addr.idInfo.ui64Id);
					str = String.format("%s", pRegInfo.addr.idInfo.ui64Id);
					talkerDesc=str;// (int)uiCnt_talkerDesc);
				}
				if (displayName != null)
				{
					//_sntprintf(displayName, uiCnt_displayName, ("(%I64u)"), pRegInfo->addr.idInfo.ui64Id);
					str = String.format("%s", pRegInfo.addr.idInfo.ui64Id);
					displayName=str;// (int)uiCnt_displayName);

				}
			}
			//if (talkerDesc) tTrim(talkerDesc);
			//if (displayName) tTrim(displayName);

			iErr = 0;
		

			if (iErr!=0)
			{
			
			}

			return iErr;
		}



		public static  int grpInfo2Desc(Object p0, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, String talkerDesc, int uiCnt_talkerDesc, String displayName, int uiCnt_displayName)
		{
			int iErr = -1;
			String str;

			if (pGrpInfo.name != null)
			{
				if (talkerDesc != null)
				{
					talkerDesc= pGrpInfo.name;//, (int)uiCnt_talkerDesc);
				}
				if (displayName != null)
				{
					displayName= pGrpInfo.name;//, (int)uiCnt_displayName);
				}
			}
			else
			{
				if (talkerDesc != null)
				{
					str = String.format("(%s)", pGrpInfo.idInfo.ui64Id);
					talkerDesc=str;// (int)uiCnt_talkerDesc);
				}
				if (displayName != null)
				{
					str = String.format("(%s)", pGrpInfo.idInfo.ui64Id);
					displayName=str;// (int)uiCnt_displayName);
				}
			}

			iErr = 0;

			return iErr;
		}
	}
}
