
package com.qycx.base;

import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.qnmCommProc.qyFuncs.CONST_fieldIdTable_en;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmSqlScripts.Consts.CONST_tabName_qyImObjRegInfoTab;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;

import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;

public class getTalkerDesc
{

	public static class Consts
	{
		public final static int cntof_desc = 32;
		public final static int CONST_maxConfMgrMems = 20;

	}


	public static class qyFuncs
	{
		public static int getTalkerDesc(qyCommProc_open.getTalkerDescParams getTalkerDescParams)
		{
			if (null == getTalkerDescParams.pMisCnt) return -1;
			qyDefs.QY_MESSENGER_ID pIdInfo =getTalkerDescParams.idInfo;

			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

			funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = (funcsForIsCliHelp.FUNCS_for_isCliHelp)pProcInfo.pFuncs_for_isCliHelp;//  QY_GET_FUNCS_for_isCliHelp(  );
			myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;//
			if (null == pDbFuncs) return -1;
			myDb.QM_dbFuncs g_dbFuncs = pDbFuncs=new myDb.QM_dbFuncs();

			qyMcMainDaemon_isCli_mgr.TMP_tBuf128 talkerDescBuf=new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
			int cntof_talkerDescBuf = cntof_tBuf128;
			qyMcMainDaemon_isCli_mgr.TMP_tBuf128 displayNameBuf=new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
			int cntof_displayNameBuf = cntof_tBuf128;

			String str;
			String talkerDesc;

			if (null == getTalkerDescParams.pMisCnt || null == pIdInfo) return -1;

			if (false)
				//if (pIdInfo.ui64Id == getTalkerDescParams.pMisCnt.idInfo.ui64Id)
			{
				String pTalkerDesc = getTalkerDescParams.pMisCnt.talkerDesc.talkerDesc;
				{
					String pDisplayName = getTalkerDescParams.pMisCnt.displayName.displayName;
					{
						talkerDescBuf.tBuf=pTalkerDesc;//, cntof_talkerDescBuf);
						displayNameBuf.tBuf=pDisplayName;//, cntof_displayNameBuf);
					}
				}
			}
			else
			{

				//  2014/12/23		  		
				qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo=new qnmCommProc_mis.QY_MESSENGER_INFO();
				Object pDb = null;
				int i;

				qnmDb.CQnmDb tmpObjDb = new qnmDb.CQnmDb();

				pDb = tmpObjDb.getAvailableDb(pQyMc.iDsnIndex_mainSys);

				//
				//mymemset((byte[])messengerInfo, 0, sizeof(QY_MESSENGER_INFO));
				//
				//messengerInfo.misServName, "", mycountof(messengerInfo.misServName));
				messengerInfo.idInfo.ui64Id = pIdInfo.ui64Id;

				//

				//  2014/12/23
				qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo=new qnmCommProc_mis.QY_MESSENGER_REGINFO();
				if (true)
				{
					//if (!pQMem->talkerDesc[0])
					{

						if (!g_dbFuncs.pf_bGetMessengerRegInfoBySth.call(pDb, pQyMc.cfg.db.iDbType,  CONST_fieldIdTable_en, CONST_tabName_qyImObjRegInfoTab, "", pIdInfo, 0,regInfo)){
							qnmCommProc_mis.MY_REG_DESC desc = new qnmCommProc_mis.MY_REG_DESC();
						}
						talkerDesc = regInfo.dw +" "+ regInfo.bm + " "+ regInfo.syr;
					}
				}


				//
				if (true)
				{
					//talkerDescBuf, pQMem->talkerDesc, mycountof(talkerDescBuf));
					//displayNameBuf, pQMem->displayName, mycountof(displayNameBuf));
				}
			}

			if (null == talkerDescBuf.tBuf)
			{
				str = String.format("(%s)", pIdInfo.ui64Id);
				talkerDescBuf.tBuf=str;
			}
			if (null == displayNameBuf.tBuf) {
				str = String.format("(%s)", pIdInfo.ui64Id);
				displayNameBuf.tBuf=str;
			}

			if (0 != getTalkerDescParams.talkerDescCnt)
			{
				if(talkerDesc.isEmpty()){
					getTalkerDescParams.talkerDesc= talkerDescBuf.tBuf;//, (int)talkerDescCnt);
				}else{
					getTalkerDescParams.talkerDesc = talkerDesc;//, (int)talkerDescCnt);
				}
			}
			if (0 != getTalkerDescParams.shortNameCnt){
				getTalkerDescParams.shortName= displayNameBuf.tBuf;//, (int)shortNameCnt);
			}

			return 0;
		}

		public static Pair<Integer,Object> getTalkerDesc(qyDefs.QY_MESSENGER_ID idInfo)
		{
			int iErr = -1;
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			if (pQyMc == null) return null;
			myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

			//
		 	int	uiObjType = 0;

			qnmCommProc_mis.QY_MESSENGER_INFO messengerInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
			qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
            if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType,  CONST_fieldIdTable_en, "",idInfo,
                0,messengerInfo,regInfo, null, null))
            {
                return null;
            }
			uiObjType = messengerInfo.uiType;
			if (messengerInfo.uiType == CONST_objType_imGrp)
			{
				qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
				if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "",idInfo,grpInfo))
				{
					Log.e("pf_bGetImGrpInfoBySth failed");
					return null;
				}
				return new Pair<>(uiObjType,grpInfo);
			}
			return new Pair<>(uiObjType,regInfo);
		}

		public static  String getMsgDesc(qnmCommProc_mis.MIS_MSGU pMsg)
		{
			String str = "";
			qnmCommProc_mis.IM_CONTENTU pContent = null;

			switch (pMsg.uiType)
			{
				case qnmCommProc_mis.Consts.CONST_misMsgType_input:
					{
						qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
						if (!isUcFlgTalkData(input.ucFlg) && !isUcFlgRouteTalkData(input.ucFlg)) break;
						pContent=M_getMsgContent(input.uiType, input.ucFlg, input.data);
					}
					break;
				case CONST_misMsgType_task:
					qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
					if (!isUcFlgTalkData(task.ucFlg) && !isUcFlgRouteTalkData(task.ucFlg)) break;
					pContent = M_getMsgContent(task.uiType, task.ucFlg, task.data);
					break;
				default:
					break;
			}

			if (pContent != null)
			{
				switch (pContent.uiType)
				{
					case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
						str = "视频通话";
						break;
					case CONST_imCommType_transferFileReq:
					{
						qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ)pContent;
						str = "发送文件 " + transferFileReq.fileName + " " + transferFileReq.ui64FileLen;
						break;
					}
					default:
						break;
				}
			}
			return str;
		}
	}
}
