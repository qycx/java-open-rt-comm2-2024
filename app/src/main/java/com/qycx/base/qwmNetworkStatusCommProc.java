

package com.qycx.base;

import static com.qycx.base.qyDefs_open.Consts.*;

public class qwmNetworkStatusCommProc {

    public static class qyFuncs {

        public static qyTypes.QY_DMITEM[] CONST_qyStatusTable_en = new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(CONST_qyStatus_ok, ("OK")),
                        //
                        new qyTypes.QY_DMITEM(CONST_qyStatus_del, "Del"),
                        new qyTypes.QY_DMITEM(CONST_qyStatus_update, "Update"),
                        //
                        new qyTypes.QY_DMITEM(CONST_qyStatus_err, "Error"),        //  ???-1???Error?????
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_req, "Req"),        //  2007/10/07
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_resp, "Resp"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_applyToSend, "applyToSend"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_waitToSend, "waitToSend"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_applyToRecv, "applyToRecv"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_waitToRecv, "waitToRecv"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sending, "sending"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_receiving, "receiving"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sendBreak, "sendBreak"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_recvBreak, "recvBreak"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_recvFinished, "recvFinished"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sendFinished, "sendFinished"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_err_missingOrgReq, "err_missingOrgReq"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_canceledBySender, "canceledBySender"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_canceledByReceiver, "canceledByReceiver"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_deniedByReceiver, "deniedByReceiver"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_autoCanceledByReceiver, "autoCanceledByReceiver"),        //  2014/07/30
                        //
                        //
                        new qyTypes.QY_DMITEM(-1, ""),
                };


        //
        public static qyTypes.QY_DMITEM[] CONST_qyStatusTable_cn = new qyTypes.QY_DMITEM[]

                {
                        new qyTypes.QY_DMITEM(CONST_qyStatus_ok, ("成功")),
                        //
                        new qyTypes.QY_DMITEM(CONST_qyStatus_del, "删除"),
                        new qyTypes.QY_DMITEM(CONST_qyStatus_update, "升级"),
                        //
                        new qyTypes.QY_DMITEM(CONST_qyStatus_err, "错误"),        //  ???-1???Error?????
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_req, "请求"),        //  2007/10/07
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_resp, "响应"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_applyToSend, "申请发送"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_waitToSend, "等待发送"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_applyToRecv, "申请接收"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_waitToRecv, "等待接收"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sending, "发送中"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_receiving, "接收中"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sendBreak, "发送中断"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_recvBreak, "接收中断"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_recvFinished, "接收完成"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_sendFinished, "发送完成"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_err_missingOrgReq, "丢失原始包"),
                        //
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_canceledBySender, "发送取消"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_canceledByReceiver, "接收取消"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_deniedByReceiver, "拒绝接收"),
                        new qyTypes.QY_DMITEM(CONST_imTaskStatus_autoCanceledByReceiver, "自动取消接收"),        //  2014/07/30
                        //
                        //
                        new qyTypes.QY_DMITEM(-1, ""),
                };


    }


}
