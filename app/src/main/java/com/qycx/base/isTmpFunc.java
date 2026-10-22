
//#define __PRINT_imObj__


package com.qycx.base;

import com.qycx.base.util.Log;

public class isTmpFunc {
    public static class qyFuncs {

        public static void printRetrieveImGrpListResp(qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP pResp) {
            Log.d("printRetrieveImGrpListResp start");
            Log.d(String.format("GroupNums：%s", pResp.mems.length));
            for (int i = 0; i < pResp.mems.length; i++) {
                if (pResp.mems[i] != null)
                    Log.d(String.format("GroupIdInfo: %s, GroupName: %s", pResp.mems[i].idInfo.ui64Id, pResp.mems[i].name));
            }
            Log.d("printRetrieveImGrpListResp end");
        }


        public static void printRetrieveImGrpMemListResp(qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP pResp) {
            Log.d("printRetrieveImGrpMemListResp start");
            Log.d(String.format("GroupMemNums：%s", pResp.mems.length));
            for (int i = 0; i < pResp.mems.length; i++) {
                if (pResp.mems[i] != null)
                    Log.d(String.format("GroupID: %s, GroupMemID: %s", pResp.mems[i].idInfo_grp.ui64Id, pResp.mems[i].idInfo_mem.ui64Id));
            }
            Log.d("printRetrieveImGrpMemListResp end");
        }

        public static void printRetrieveContactListResp(qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP pResp) {
            Log.d("printRetrieveImGrpMemListResp start");
            Log.d(String.format("ContactsNums：%s", pResp.mems.length));
            for (int i = 0; i < pResp.mems.length; i++) {
                if (pResp.mems[i] != null)
                    Log.d(String.format("ContactID: %s, dw: %s, bm: %s, syr: %s", pResp.mems[i].regInfo.id, pResp.mems[i].regInfo.dw, pResp.mems[i].regInfo.bm, pResp.mems[i].regInfo.syr));
            }
            Log.d("printRetrieveImGrpMemListResp end");
        }

        //
        public static int printMisMsg(Object bNetworkDataParam, Object p1, qnmCommProc_mis.MIS_MSGU pMsg) {


            return 0;
        }


    }
}
