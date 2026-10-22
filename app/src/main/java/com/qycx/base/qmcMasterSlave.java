



package com.qycx.base;

import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjSyncByIndex;
import static com.qycx.base.slaveStreamProc.qyFuncs.isSlaveOfMaster;

public class qmcMasterSlave
{
    public static class qyFuncs
{
		//
		public static  boolean bExists_slave(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj_master)
		{
			boolean bRet = false;
			//  QY_MC					*	pQyMc					=	QY_GET_GBUF(  );
			//  MC_VAR_isCli			*	pProcInfo				=	QY_GET_procInfo_isCli(  );


			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_master = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_master = null;
			pSharedObjInfo_master = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_master);
			if (null==pSharedObjInfo_master) return bRet;

			pSharedObj_master = pSharedObjInfo_master.sharedObj;
			if (!pSharedObj_master.bUsed) return bRet;

			if (pSharedObj_master.var.bSlave) return bRet;

			int iIndex_sharedObj_slave = pSharedObj_master.var.master.slaveKey.iIndex_sharedObj;

			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_slave = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_slave = null;
			pSharedObjInfo_slave = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_slave);
			if (null==pSharedObjInfo_slave) return bRet;

			pSharedObj_slave = pSharedObjInfo_slave.sharedObj;
			if (!pSharedObj_slave.bUsed) return bRet;

			if (!pSharedObj_slave.var.bSlave) return bRet;

			//
			if (!isSlaveOfMaster(iIndex_sharedObj_slave, pSharedObj_slave, iIndex_sharedObj_master, pSharedObj_master))
			{
				return bRet;
			}

			bRet = true;



			return bRet;
		}

		public static  boolean bExists_master(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj_slave)
		{
			boolean bRet = false;
			//  QY_MC						*	pQyMc					=	QY_GET_GBUF(  );
			//  MC_VAR_isCli				*	pProcInfo				=	QY_GET_procInfo_isCli(  );

			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_slave = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_slave = null;
			pSharedObjInfo_slave = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_slave);
			if (null==pSharedObjInfo_slave) return bRet;

			pSharedObj_slave = pSharedObjInfo_slave.sharedObj;
			if (!pSharedObj_slave.bUsed) return bRet;

			if (!pSharedObj_slave.var.bSlave) return bRet;

			//
			int iIndex_sharedObj_master = pSharedObj_slave.var.slave.masterKey.iIndex_sharedObj;

			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_master = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_master = null;
			pSharedObjInfo_master = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_master);
			if (null==pSharedObjInfo_master) return bRet;

			pSharedObj_master = pSharedObjInfo_master.sharedObj;
			if (!pSharedObj_master.bUsed) return bRet;

			if (pSharedObj_master.var.bSlave) return bRet;


			//
			if (!isSlaveOfMaster(iIndex_sharedObj_slave, pSharedObj_slave, iIndex_sharedObj_master, pSharedObj_master))
			{
				return bRet;
			}

			bRet = true;



			return bRet;
		}


		public static  int removeSlaveFromMaster(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj_slave, int iIndex_sharedObj_master)
		{
			//
			int iErr = -1;

			boolean bInited = false;
			int tn_master = 0;
			int tn_slave = 0;
			//
			qySyncObj.CQySyncObj syncObj_master=new qySyncObj.CQySyncObj();
			qySyncObj.CQySyncObj syncObj_slave=new qySyncObj.CQySyncObj();
			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_master = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_master = null;
			pSharedObjInfo_master = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_master);
			if (null==pSharedObjInfo_master) return iErr;

			pSharedObj_master = pSharedObjInfo_master.sharedObj;

			//
			qmcSharedObj.QY_sharedObj_sync pSharedObjInfo_slave = null;
			qmcSharedObj.QY_SHARED_OBJ pSharedObj_slave = null;
			pSharedObjInfo_slave = getSharedObjSyncByIndex(pProcInfo, iIndex_sharedObj_slave);
			if (null==pSharedObjInfo_slave) return iErr;

			pSharedObj_slave = pSharedObjInfo_slave.sharedObj;
			bInited = true;

			if (isSlaveOfMaster(iIndex_sharedObj_slave, pSharedObj_slave, iIndex_sharedObj_master, pSharedObj_master))
			{
				//memset(pSharedObj_master->master.slaveKey, 0, sizeof(pSharedObj_master->master.slaveKey)  );
				pSharedObj_master.var.master.slaveKey = new qmcSharedObj.SHARED_obj_key();
				//memset(pSharedObj_slave->slave.masterKey, 0, sizeof(pSharedObj_slave->slave.masterKey)  );
				pSharedObj_slave.var.slave.masterKey = new qmcSharedObj.SHARED_obj_key();
			}

			iErr = 0;

			if (bInited)
			{
	
			}
			return iErr;

		}

	}
}
