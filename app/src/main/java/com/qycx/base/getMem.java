


package com.qycx.base;

import java.util.List;

public class getMem {
    public static class qyFuncs {

        public static qnmCommProc_mis.myDRAW_VIDEO_DATA getMem(qnmCommProc_mis.myDRAW_VIDEO_DATA[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.myDRAW_VIDEO_DATA info = pStart[index];
            if (info == null) {
                info = new qnmCommProc_mis.myDRAW_VIDEO_DATA();
                pStart[index] = info;
            }
            return info;
        }

        public static qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead getMem(qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead info = pStart[index];
//            if (info == null) {
//                info = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
//                pStart[index] = info;
//            }
            return info;
        }

        public static qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead getMem(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead info = pStart[index];
//            if (info == null) {
//                info = new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
//                pStart[index] = info;
//            }
            return info;
        }

        public static qnmCommProc_mis.IM_GRP_INFO getMem(qnmCommProc_mis.IM_GRP_INFO[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.IM_GRP_INFO info = pStart[index];
            if (info == null) {
                info = new qnmCommProc_mis.IM_GRP_INFO();
                pStart[index] = info;
            }
            return info;
        }

        public static qnmCommProc_mis.IM_GRP_MEM getMem(qnmCommProc_mis.IM_GRP_MEM[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.IM_GRP_MEM mem = pStart[index];
            if (mem == null) {
                mem = new qnmCommProc_mis.IM_GRP_MEM();
                pStart[index] = mem;
            }
            return mem;
        }

        public static qnmCommProc_mis.RETRIEVE_contact_mem getMem(qnmCommProc_mis.RETRIEVE_contact_mem[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.RETRIEVE_contact_mem mem = pStart[index];
            if (mem == null) {
                mem = new qnmCommProc_mis.RETRIEVE_contact_mem();
                pStart[index] = mem;
            }
            return mem;
        }

        public static qnmCommProc_mis.VIDEO_CONFERENCE_MEM getMem(qnmCommProc_mis.VIDEO_CONFERENCE_MEM[] pStart, int index) {
            if (index >= pStart.length) {
                return null;
            }
            return pStart[index];
        }


        public static qnmCommProc_mis.CONF_requesting_mem getMem(qnmCommProc_mis.CONF_requesting_mem[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.CONF_requesting_mem mem = pStart[index];
            if (mem == null) {
                mem = new qnmCommProc_mis.CONF_requesting_mem();
                pStart[index] = mem;
            }
            return mem;
        }

        public static qnmCommProc_mis.IpcInfo getMem(qnmCommProc_mis.IpcInfo[] pStart, int bufSize, int index) {
            if (index >= pStart.length) {
                return null;
            }
            qnmCommProc_mis.IpcInfo mem = pStart[index];
            if (mem == null) {
                mem = new qnmCommProc_mis.IpcInfo();
                pStart[index] = mem;
            }
            return mem;
        }
    }
}
