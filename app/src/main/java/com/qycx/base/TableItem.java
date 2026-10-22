package com.qycx.base;

public class TableItem
{
    public static class TableItem_imObj
    {
        //
        public long ui64Id;

        //
        public int uiObjType;

        public int ImageResourceId;

        public String dw;
        public String bm;
        public String syr;

        //
        public String grpName;

        //
        public TableItem_imObj()
        {
            ui64Id = 0;
            uiObjType = 0;
            ImageResourceId = 0;
            dw = "";
            bm = "";
            syr = "";
            grpName = "";
        }


    }

    public static class TableItem_msg
    {
        public long ui64Id;
        
        //
        public int uiObjType;

        //
        public int ImageResourceId;

        public String dw;
        public String bm;
        public String syr;
        public String grpName;

        //
        public String speaker;
        public String msg;
        public long tRecvTime;

        //
        public TableItem_msg()
        {
            ui64Id = 0;
            uiObjType = 0;
            ImageResourceId = 0;
            dw = "";
            bm = "";
            syr = "";
            grpName = "";
            //
            speaker = "";
            msg = "";
            tRecvTime = 0;

        }


    }


    public static class TableItem_dlgTalk_msg
    {
        // speaker
        public long ui64Id_from;
                
        //
        public int ImageResourceId;

        public String dw;
        public String bm;
        public String syr;
        public String msg;
        public long tStartTime;
        public int uiTranNo;
        //
        public int uiMsgType;
        public int usCode;
        public int uiContentType;
        //
        public int uiTaskType;
        public int iTaskId;
        public String statusStr;

        //
        public TableItem_dlgTalk_msg()
        {
            ui64Id_from = 0;
            ImageResourceId = 0;
            dw = "";
            bm = "";
            syr = "";
            //
            msg = "";
            tStartTime = 0;
            uiTranNo = 0;
            //
            uiMsgType = 0;
            usCode = 0;
            uiContentType = 0;
            //
            uiTaskType = 0;
            iTaskId = 0;
            //iStatus = 0;
            statusStr = "";


        }


    }



}

