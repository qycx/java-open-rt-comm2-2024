



package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.isQFull;
import static com.qycx.base.genericQueue.qyFuncs.qRemoveMsg;
import static com.qycx.base.genericQueue.qyFuncs.qTraverse;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgMoreData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;

import com.qycx.base.util.javatuples.Pair;

public class qmObjQProc
{

public static class Consts
{
    public final static int CONST_objQType_null = 0;
    public final static int CONST_objQType_netMc = 1;
    public final static int CONST_objQType_mis = 2;
    public final static int CONST_objQType_misClient = 3;						//  对在messenger的管理端和客户端由于使用的不同，存储方式也不同。故用不同的类型来区分

}


 public static class qyFuncs
    {
		public static qyDefs.PF_commonHandler tmpHandler_procMisMsg_moreData=(Object p0, Object p1, Object pMsgElemParam)->
		{
			int iErr = -1;
			boolean bFindOnly = p0 != null ? true :false;
			qnmCommProc_mis.MIS_MSG_INPUT pMsgInput = (qnmCommProc_mis.MIS_MSG_INPUT)p1;
			qnmCommProc_mis.MIS_MSG_INPUT pMsgElem = null;// (qnmCommProc_mis.MIS_MSG_INPUT)pMsgElemParam;
			//

			if (pMsgInput.tStartTime == pMsgElem.tStartTime
				&& pMsgInput.uiTranNo == pMsgElem.uiTranNo)
			{
				//  found

				if (bFindOnly)
				{           //  这里用来寻找某类包。可以用来删除包等
					iErr = 1; return iErr;
				}

				if (pMsgInput.usSeqNo != pMsgElem.usSeqNo + 1)
				{   //  
					//Log.e(TAG,"tmpHandler_procMisMsg_moreData: usSeqNo error");
					return iErr;
				}
				if (pMsgInput.lenInBytes + pMsgElem.lenInBytes > 10)
				{
					String str =String.format(("procMoreData failed: 存储空间不够，数据太长了， tranNo [%d], seqNo [%d]"), pMsgInput.uiTranNo, (int)pMsgInput.usSeqNo);
					showInfo_open.qyFuncs.showInfo_open( str);
						
					//Log.e(TAG,"tmpHandler_procMisMsg_moreData: failed");
					return iErr;
				}
				//qyMemoryFunc.qyFuncs.mymemcpy((((byte[])pMsgElem.data) + pMsgElem.lenInBytes), pMsgInput.data, pMsgInput.lenInBytes);
				pMsgElem.lenInBytes += pMsgInput.lenInBytes;
				pMsgElem.usSeqNo = pMsgInput.usSeqNo; //  

				//  Log.e(TAG,  "uiSeqNo [%d] ",  pMsgInput->uiSeqNo  );

				if (!isUcFlgMoreData(pMsgInput.ucFlg))
				{   //  
					//qyMemoryFunc.qyFuncs.mymemcpy((byte[])pMsgInput.data, pMsgElem.data, pMsgElem.lenInBytes);
					pMsgInput.lenInBytes = pMsgElem.lenInBytes;
					pMsgInput.usSeqNo = 0;

					//  Log.e(TAG,  "tmpHandler_procMisMsg_moreData;  "  );
				}

				iErr = 1; return iErr;
			}

			iErr = 0;
		
			return iErr;
		};

		public static qyDefs.PF_bCommonHandler b_tmpHandler_procMisMsg_moreData=(Object p0, Object p1, Object pMsgElemParam)->
        {
			int iRet = tmpHandler_procMisMsg_moreData.call(p0, p1, pMsgElemParam);
			return iRet != 0 ? true : false;
        };

		//  当ucFlg的CONST_commFlg_moreData为0时，表明包发完了
		//  当不为0时，放在cacheQ里
		//  当为0时，如uiSeqNo为0，则是独立包。如不为0，则从cacheQ里取出所有的tStartTime和tranNo相同的包，按uiSeqNo组成一个完整包。
		// 
		//  这里的包，限制在一个MIS_MSG_TALK的buf里。
		//  对更大的包，也许要用队列或其他来表示了。暂不考虑. 或者，应该在客户端自己在内容数据里来分拆合并了
		public static Pair<Integer,Boolean> procMisMsg_moreData(genericQueue.GENERIC_Q pCacheQ, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, boolean pbMsgSkipped)
		{
			int iErr = -1;
			boolean bMsgSkipped = false;

			int tmpiRet;

			//  当Q满时，简单的处理是将队列清空
			if (isQFull(pCacheQ)) emptyGenericQ(pCacheQ);

			if (isUcFlgMoreData(pMsgInput.ucFlg))
			{       //  找到前面的包加进去

				tmpiRet = qTraverse(pCacheQ, tmpHandler_procMisMsg_moreData, 0,pMsgInput);
				if (tmpiRet < 0)
				{
					qRemoveMsg(pCacheQ, b_tmpHandler_procMisMsg_moreData,1,pMsgInput);
				}
				if (0==tmpiRet)
				{  //  没找到就要加进去. 注意，在这里要将整个MIS_MSG_INPUT都放进去，是因为需要将后面的数据都跟在msg的data里
//					if (0!=qPostMsg(pMsgInput, (int)sizeof(qnmCommProc_mis.MIS_MSG_INPUT), pCacheQ)) return iErr;
				}
				iErr = 0;
				return new Pair<>(iErr,bMsgSkipped);
			}

			if (0==pMsgInput.usSeqNo)
			{   //  这是指不需要在这里处理的
				iErr = 0;
				return new Pair<>(iErr,bMsgSkipped);
			}

			//  要准备把数据取出来，放到pMsgInput中

			tmpiRet = qTraverse(pCacheQ, tmpHandler_procMisMsg_moreData,0,pMsgInput);
			if (tmpiRet!=0) qRemoveMsg(pCacheQ, b_tmpHandler_procMisMsg_moreData,1,pMsgInput);
			if (tmpiRet <= 0)
			{
				bMsgSkipped = true;
				iErr = 0;
				return new Pair<>(iErr,bMsgSkipped);
			}

			iErr = 0;

		

			if (0==iErr)
			{
				if (pbMsgSkipped != true) {
					pbMsgSkipped = bMsgSkipped;
				}
			}

			if (iErr!=0)
			{   //  2011/01/24
				//Log.e(TAG,("procMisMsg_moreData failed"));
			}

			return new Pair<>(iErr,bMsgSkipped);
		}



	}
}
