package com.qycx.base;

import static com.qycx.base.colorSpaceFunc.qyFuncs.M_sizeImage;
import static com.qycx.base.qmcCommFunc_mis.Consts.CONST_iAllocType_video;
import static com.qycx.base.qmcFunc.qyFuncs.setPktInputInfo;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_set_pktId_alloc;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.videoFunc_open.qyFuncs.makeBmpInfoHeader_i420;

public class qmcVideoCapture_dx
{
	public static class qyFuncs_Droid
{

		public static  int BufferCB_av(ctxQmc.CCtxQmc pProcInfoParam, int iIndex_capProcInfo, Object pSampleGrabberCbVar, int iSampleTimeInMs, byte[] pBuffer, int BufferLen,int w,int h,int iTestNo)
		{
			int iErr = -1;
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
			boolean bDbg = false;
			String str;

			qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;

			//if (!pSampleGrabberCbVar) return -1;
			//SAMPLE_grabberCb_var & m_var = (SAMPLE_grabberCb_var)pSampleGrabberCbVar;

			//  CAP_procInfo_video	*	pVc;
			qmcSharedObj.CAP_procInfo_bmpCommon pVc=new qmcSharedObj.CAP_procInfo_bmpCommon();

			pVc = (qmcSharedObj.CAP_procInfo_bmpCommon)qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, iIndex_capProcInfo, 0);
			if (null==pVc) return iErr;
			pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, pVc.iIndex_sharedObj);     //  ÕâÀïÒªÓÃindexÀ´»ñÈ¡
			if (null==pSharedObj) return iErr;

			qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
			if (null==pSharedObjUsr) return iErr;



			//  

			//
			qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo = pVc;
			
			//
			qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc(); {

				qnmCommProc_mis.myDRAW_VIDEO_DATA p=new qnmCommProc_mis.myDRAW_VIDEO_DATA();
				int len;

				//  len  =  offsetof(  myDRAW_VIDEO_DATA,  buf  )  +  pCapProcInfo->vh_org.bih.biSizeImage;
				len=128;//sizeof(myDRAW_VIDEO_DATA);
				qnmCommProc_mis.myDRAW_VIDEO_DATA tmp_myDRAW_VIDEO_DATA=new qnmCommProc_mis.myDRAW_VIDEO_DATA();
				p =tmp_myDRAW_VIDEO_DATA; //  (  myDRAW_VIDEO_DATA  *  )m_var.pBuf;
				if (null == p) return iErr;
				//qyFuncs.mymemset(p, 0, sizeof(myDRAW_VIDEO_DATA));    //  memset(  p,  0,  m_var.uiBufSize_pBuf  );

				
				if (0!=setPktInputInfo(pProcInfo,  pVc.compressVideo.input, pVc.compressVideo.usFrames_perSecond_expected, false, p))
				{
					return iErr;
				}
				
				//
				//byte[] pInput = pBuffer;
				int uiSize_dec = (int)BufferLen;
		
				uiSize_dec  =  (int)Math.max(  uiSize_dec, M_sizeImage(  32,  w,  h  )  );
				

				//  2011/12/06
				int uiSize_enc = (int)0;

				//
				p.head.uiLen = uiSize_dec + uiSize_enc;        //  pBmpInfo_input->bmiHeader.biSizeImage;	
				p.head.uiSampleTimeInMs = (int)iSampleTimeInMs;
				//
				p.uiSize_dec = uiSize_dec;
				//
				if (null== new byte[uiSize_dec + uiSize_enc]) return iErr;
				if (null == (p.memory = mallocObj.get())) return iErr;

				//  2011/12/04
				//memcpy(p->bih,pVc->vh_org.bih, sizeof(p->bih)  );
				makeBmpInfoHeader_i420(12, w, h, p.bih);
				//
				//qyMemoryFunc.qyFuncs.mymemcpy(p.memory.m_pBuf,pBuffer, (int)BufferLen);

				p.debugInfo.lPktNo = iTestNo;

				//
				M_set_pktId_alloc(p);

				//
				//GuiShare.pf_dumpData1.call(p->memory.m_pBuf, p->bih.biSizeImage, p->bih.biWidth, p->bih.biHeight, "bufferCb_avkk.yuv");


				//
				if (0!=q2PostMsgAndTrigger(p,pCapProcInfo.vppThread.q2,"BufferCB_av.100")) return iErr;
				mallocObj.detach();
			}
			


			iErr = 0;

		

			return iErr;
		}

	}
}



