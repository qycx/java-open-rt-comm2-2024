


package com.qycx.base;

import static com.qycx.base.qySqlFunc.Consts.CONST_dbType_myDb;

public class qyMcMainDb {
    public static class qyFuncs {
        public static int initQyMcDb(Object p0, Object p1, qnmCommProc1.QY_OBJ_DB pObj) {
            int iErr = -1;

            //if (null==pObj) return -1;
            if (pObj.ucbLoaded) return -1;

            //
            if (pObj.cfg.iDbType == CONST_dbType_myDb) {  //  2012/07/14
                try {
                    pObj.pDb = new myDb.CMyDb();
                    myDb.CMyDb pDb = (myDb.CMyDb) pObj.pDb;
                    String pConnectStr = pObj.cfg.connectStr;

                    if (!pDb.bQyOpenDb(pConnectStr)){
                        exitQyMcDb(p0, p1, pObj);
                        return iErr;
                    }

                    //
                } catch (Exception e) {
                    e.printStackTrace();
                    exitQyMcDb(p0, p1, pObj);
                    return iErr;
                }

                //
                if (qnmDb.CQnmDb.makeDbPool_myDb((myDb.CMyDb) pObj.pDb, 8, pObj.iDsnIndex) != 0)
                {
                    exitQyMcDb(p0, p1, pObj);
                    return iErr;
                }

            } else {

            }


            iErr = 0;


            if (0 != iErr) {

                exitQyMcDb(p0, p1, pObj);

                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("qyMc"), 0, "", "", ("initQyMcDb failed"));
            }

            if (0 == iErr) {
                pObj.ucbLoaded = true;

                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("qyMc"), 0, "", "", ("initQyMcDb ok"));
            }

            return iErr;

        }


        public static void exitQyMcDb(Object p0, Object p1, qnmCommProc1.QY_OBJ_DB pObj) {
            qnmDb.CQnmDb.freeDbPool(pObj.iDsnIndex);    //  2007/01/02

            if (pObj.pDb != null) {
                if (pObj.cfg.iDbType == CONST_dbType_myDb) {
                    myDb.CMyDb pDb = (myDb.CMyDb) pObj.pDb;
                    pDb.qyCloseDb();
                    //delete pDb;
                    pObj.pDb = null;
                } else {
                    //qyFreeDb(pObj->pDb);
                }
            }
            pObj.ucbLoaded = false;

        }

    }
}
