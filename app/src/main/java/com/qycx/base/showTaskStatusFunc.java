



package com.qycx.base;

import static com.qycx.base.Consts.MAX_PATH;
import static com.qycx.base.Consts.cntof_fileName;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_content;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;


public class showTaskStatusFunc
{
    public static class qyFuncs
    {
		//显示任务状态 将任务状态发到界面线程上去
		public static  int showTaskStatusFunc(Object pMisCnt_unused, qyDefs.QY_MESSENGER_ID pIdInfo_logicalPeer, qyDefs.QY_MESSENGER_ID pIdInfo_taskSender, qyDefs.QY_MESSENGER_ID  pIdInfo_taskReceiver_unused, boolean bImGrpMem, int cmdCode, long tStartTime, int uiTranNo, int uiContentType, int iTaskId, int iStatus, boolean ucbNotification, qyDefs.QY_MESSENGER_ID pIdInfo_from, byte ucPercent, long  ui64Len_recvd, String fileName, String fullFilePath)
		{
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			qnmCommProc_mis.MIS_MSG_taskStatus msgTaskStatus=new qnmCommProc_mis.MIS_MSG_taskStatus();
			//  报告状态
			//mymemset((byte[])msgTaskStatus, 0, sizeof(MIS_MSG_taskStatus));
			msgTaskStatus.uiType = CONST_misMsgType_taskStatus;
			if (pIdInfo_logicalPeer!=null) msgTaskStatus.idInfo_logicalPeer.ui64Id = pIdInfo_logicalPeer.ui64Id;
			//
			if (null!=pIdInfo_taskSender) msgTaskStatus.idInfo_taskSender.ui64Id = pIdInfo_taskSender.ui64Id;
			msgTaskStatus.bImGrpMem = bImGrpMem;            //
			msgTaskStatus.cmdCode = cmdCode;                //  2009/10/18
			msgTaskStatus.tStartTime = tStartTime;      //  2009/10/18
			msgTaskStatus.uiTranNo = uiTranNo;          //  2009/10/18
			msgTaskStatus.uiContentType = uiContentType;    //  
			msgTaskStatus.iTaskId = iTaskId;
			msgTaskStatus.iStatus = iStatus;
			if (ucbNotification)
			{
				msgTaskStatus.ucbNotification = true;
				if (pIdInfo_from!=null) msgTaskStatus.u_notification.idInfo_from.ui64Id = pIdInfo_from.ui64Id;
				msgTaskStatus.u_notification.content=fileName;//, cntof_content);
			}
			else
			{
				msgTaskStatus.u_transferFileStatus.ucPercent = ucPercent;
				msgTaskStatus.u_transferFileStatus.ui64Len_recvd = ui64Len_recvd;
				if (fileName!=null) {
					msgTaskStatus.u_transferFileStatus.fileName=fileName;
				}
				if (fullFilePath!=null) {
					msgTaskStatus.u_transferFileStatus.fullFilePath=fullFilePath;
				}
			}
			qPostMsg(msgTaskStatus, pQyMc.gui.processQ);
			guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

			return 0;
		}



		//
		//
		public static  int showNotification_open(long tStartTime, int uiTranNo, int uiContentType, String hint)
		{
			return showTaskStatusFunc((Object) null, (qyDefs.QY_MESSENGER_ID) null, (qyDefs.QY_MESSENGER_ID) null, (qyDefs.QY_MESSENGER_ID) null, false, 0, tStartTime, uiTranNo, uiContentType, 0, 0, true, (qyDefs.QY_MESSENGER_ID) null, (byte) 0, 0, hint, (String) null);
		}


		//

		public static  int showNotification(Object pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo_logicalPeer, qyDefs.QY_MESSENGER_ID pIdInfo_from, long tStartTime, int uiTranNo, int uiContentType, String hint)
		{
			return showTaskStatusFunc(pMisCnt, pIdInfo_logicalPeer, (qyDefs.QY_MESSENGER_ID) null, (qyDefs.QY_MESSENGER_ID) null, false, 0, tStartTime, uiTranNo, uiContentType, 0, 0, true, pIdInfo_from, (byte) 0, 0, hint, (String) null);
		}


		//


		//
		// int  showTaskStatusFunc(  void  *  pMisCnt, qyDefs.QY_MESSENGER_ID  *  pIdInfo_logicalPeer, qyDefs.QY_MESSENGER_ID  *  pIdInfo_taskSender, qyDefs.QY_MESSENGER_ID  *  pIdInfo_taskReceiver,  boolean  bImGrpMem,  unsigned  short  cmdCode,  long  tStartTime,  int  uiTranNo,  int  uiContentType,  int  iTaskId,  int  iStatus,  unsigned  char  ucbNotification, qyDefs.QY_MESSENGER_ID	*	pIdInfo_from,  unsigned  char  ucPercent,  unsigned  __int64  ui64Len_recvd,  LPCTSTR  fileName,  LPCTSTR  fullFilePath  );

		public static  int showTaskStatus(Object pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo_logicalPeer, qyDefs.QY_MESSENGER_ID pIdInfo_taskSender, qyDefs.QY_MESSENGER_ID  pIdInfo_taskReceiver_unused, boolean bImGrpMem, int uiContentType, int iTaskId, int iStatus, byte ucPercent, long  ui64Len_recvd, String fileName, String fullFilePath)
		{
			return showTaskStatusFunc(pMisCnt,pIdInfo_logicalPeer,pIdInfo_taskSender, null,bImGrpMem, 0, 0, 0, uiContentType, iTaskId, iStatus, false, null, ucPercent, ui64Len_recvd, fileName, fullFilePath);
		}

		public static  int showTaskStatus(Object pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo_logicalPeer, qyDefs.QY_MESSENGER_ID pIdInfo_taskSender, qyDefs.QY_MESSENGER_ID pIdInfo_taskReceiver_unused, boolean bImGrpMem, int cmdCode, long tStartTime, int uiTranNo, int uiContentType, int iTaskId, int iStatus)
		{
			return showTaskStatusFunc(pMisCnt,pIdInfo_logicalPeer,pIdInfo_taskSender, (qyDefs.QY_MESSENGER_ID) null, bImGrpMem, cmdCode, tStartTime, uiTranNo, uiContentType, iTaskId, iStatus, false, (qyDefs.QY_MESSENGER_ID) null, (byte) 0, 0, null, (String) null);
		}

    }
}
