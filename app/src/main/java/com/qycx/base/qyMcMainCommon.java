


package com.qycx.base;

public class qyMcMainCommon {
    public static class qyFuncs {
        public static int getProcedObjsCfg(ctxQyMc.CCtxQyMc pQyMc) {
            int id;

            //
            if (null == pQyMc) return -1;

            //
            //memset(pQyMc->procedObjDbs, 0, sizeof(pQyMc->procedObjDbs)  );

            //  把基本系统的配置放入procedObjDbs中，放在第一个单元中（id=0），不能修改和删除
            id = 0;
            pQyMc.procedObjDb.cfg = pQyMc.cfg.db;
            //setProcedObjSth(pQyMc->procedObjDbs[id]);
            pQyMc.procedObjDb.ucbUsed = true;
            //
            pQyMc.iDsnIndex_mainSys = id;  //  2006/12/23


            return 0;
        }

    }
}
