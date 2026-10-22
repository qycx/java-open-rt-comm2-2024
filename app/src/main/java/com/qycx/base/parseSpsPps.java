


package com.qycx.base;

import static com.qycx.base.parseSpsPps.Consts.CONST_h264nalType_pps;
import static com.qycx.base.parseSpsPps.Consts.CONST_h264nalType_sps;
import static com.qycx.base.parseSpsPps.Consts.CONST_h265nalType_pps;
import static com.qycx.base.parseSpsPps.Consts.CONST_h265nalType_sps;
import static com.qycx.base.parseSpsPps.Consts.CONST_h265nalType_vps;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;
import static com.qycx.base.qyCommProc_open.Consts.MAX_spsPpsLen;
import static com.qycx.base.qyCommProc_open.Consts.cntof_VH_stream_mems;
import static com.qycx.base.qyMcMainDaemon_isCli.Consts.bufSize_1024;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class parseSpsPps {

    public static class Consts {
        //
        public final static int CONST_h264nalType_idr = 5;
        public final static int CONST_h264nalType_sps = 7;
        public final static int CONST_h264nalType_pps = 8;

        //
        public final static int CONST_h265naleType_CODED_SLICE_IDR = 19;       //  NAL_UNIT_CODED_SLICE_IDR, // 19 // Current name in the spec: IDR_W_DLP
        public final static int CONST_h265nalType_CODED_SLICE_IDR_N_LP = 20;       //  NAL_UNIT_CODED_SLICE_IDR_N_LP, // 20
        //
        public final static int CONST_h265nalType_vps = 32;
        public final static int CONST_h265nalType_sps = 33;
        public final static int CONST_h265nalType_pps = 34;
    }

    public static class qyFuncs {
        public static List<Integer> findStartCodeIndex(ByteBuffer data) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < data.limit() - 3; i++) {
                if (data.get(i) == 0 && data.get(i + 1) == 0 && data.get(i + 2) == 0 && data.get(i + 3) == 1) {
                    list.add(i);
                }
            }
            return list;
        }

        public static int parseSpsPps(int iFourcc, ByteBuffer spsPps, ByteBuffer spsBuf, ByteBuffer ppsBuf, ByteBuffer vpsBuf) {
            List<Integer> list = findStartCodeIndex(spsPps);
            return switch (iFourcc) {
                case CONST_fourcc_h264 -> parseSpsPps_h264(list, spsPps, spsBuf, ppsBuf);
                case CONST_fourcc_HEVC -> parseSpsPps_h265(list, spsPps, spsBuf, ppsBuf, vpsBuf);
                default -> -1;
            };
        }

        //
        public static int parseSpsPps1(int iFourcc, ByteBuffer spsPps, qyCommProc_open.VH_stream pVh_stream) {
            int iErr = -1;
            //
            if (null == spsPps) return -1;
            if (null == pVh_stream) return -1;

            pVh_stream.dwFourcc = (int) iFourcc;

            //
            int vpsLen = 0;   //  2017/10/10
            ByteBuffer pVps = ByteBuffer.allocate(MAX_spsPpsLen);
            int spsLen = 0;
            ByteBuffer pSps = ByteBuffer.allocate(MAX_spsPpsLen);
            int ppsLen = 0;
            ByteBuffer pPps = ByteBuffer.allocate(MAX_spsPpsLen);

            //
            switch (iFourcc) {
                case CONST_fourcc_HEVC: {
                    int k = 0;
                    if (0 != parseSpsPps(iFourcc, spsPps, pSps, pPps, pVps)) return iErr;
                    if (pVps != null) {
                        vpsLen = pVps.limit();
                        pVh_stream.mem0.usLen = vpsLen;
                        pVh_stream.mem0.spsPps = pVps;
                        k++;
                    }
                    if (pPps != null) {
                        spsLen = pSps.limit();
                        pVh_stream.mem1.usLen = spsLen;
                        pVh_stream.mem1.spsPps = pSps;
                        k++;
                    }
                    if (pPps != null) {
                        ppsLen = pPps.limit();
                        pVh_stream.mem2.usLen = ppsLen;
                        pVh_stream.mem2.spsPps = pPps;
                        k++;
                    }
                    pVh_stream.usCnt = (int) k;
                }
                break;
                case CONST_fourcc_h264:
                default: {
                    int k = 0;
                    if (0 != parseSpsPps(iFourcc, spsPps, pSps, pPps, pVps)) return iErr;
                    if (pPps != null) {
                        spsLen = pSps.limit();
                        pVh_stream.mem0.usLen = spsLen;
                        pVh_stream.mem0.spsPps = pSps;
                        k++;
                    }
                    if (pPps != null) {
                        ppsLen = pPps.limit();
                        pVh_stream.mem1.usLen = ppsLen;
                        pVh_stream.mem1.spsPps = pPps;
                        k++;
                    }
                    pVh_stream.usCnt = k;
                }
                break;
            }


            iErr = 0;


            return iErr;
        }


        //
        public static int parseSpsPps_h264(List<Integer> list, ByteBuffer data, ByteBuffer sps, ByteBuffer pps) {
            int iErr = -1;
            if (null == data) return iErr;

            for (int i = 0; i < list.size(); i++) {
                int len = 0;
                if (i < list.size() - 1) {
                    len = list.get(i + 1) - list.get(i);
                } else {
                    len = data.limit() - list.get(i);
                }
                byte[] buf = new byte[len];
                data.get(buf);
//                System.arraycopy(data, list.get(i), buf, 0, len);
                int type = (buf[4] & 0x1F);
                switch (type) {
                    case CONST_h264nalType_sps:
                        sps.put(buf);
                        sps.flip();
                        break;
                    case CONST_h264nalType_pps:
                        pps.put(buf);
                        pps.flip();
                        break;
                }
            }
            if (sps.limit() == 0 || pps.limit() == 0) {
                sps.clear();
                pps.clear();
                return iErr;
            }
            iErr = 0;
            return iErr;

        }

        //
        //  2017/10/10
        public static int parseSpsPps_h265(List<Integer> list, ByteBuffer data, ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
            int iErr = -1;

            if (null == data || vps == null || sps == null || pps == null) {
                return -1;
            }
            vps.position(0);
            sps.position(0);
            pps.position(0);

            for (int i = 0; i < list.size(); i++) {
                int len = 0;
                if (i < list.size() - 1) {
                    len = list.get(i + 1) - list.get(i);
                } else {
                    len = data.limit() - list.get(i);
                }
                byte[] buf = new byte[len];
                data.get(buf);
//                System.arraycopy(data, list.get(i), buf, 0, len);
                int type = (buf[4] & 0x7E) >> 1;
                switch (type) {
                    case CONST_h265nalType_vps:
                        vps.put(buf);
                        vps.flip();
                        break;
                    case CONST_h265nalType_sps:
                        sps.put(buf);
                        sps.flip();
                        break;
                    case CONST_h265nalType_pps:
                        pps.put(buf);
                        pps.flip();
                        break;
                }
            }
            if (vps.limit() == 0 || sps.limit() == 0 || pps.limit() == 0) {
                vps.clear();
                sps.clear();
                pps.clear();
                return iErr;
            }
            iErr = 0;
            return iErr;

        }


        //
        public static int makeSpsPps(qyCommProc_open.VH_stream pVh_stream, ByteBuffer spsPpsParam) {
            int iErr = -1;
            int spsLen = 0;
            int i;

            if (null == pVh_stream || null == spsPpsParam) return -1;

            if (0 != pVh_stream.usCnt) {
                if (pVh_stream.usCnt > cntof_VH_stream_mems) return iErr;
                //
                for (i = 0; i < pVh_stream.usCnt; i++) {
                    qyCommProc_open.VH_stream_mem pMem = null;
                    switch (i) {
                        case 0:
                            pMem = pVh_stream.mem0;
                            break;
                        case 1:
                            pMem = pVh_stream.mem1;
                            break;
                        case 2:
                            pMem = pVh_stream.mem2;
                            break;
                        default:
                            return iErr;
                    }
                    spsPpsParam.put(pMem.spsPps.array(), 0, pMem.usLen);
                    spsLen += pMem.usLen;
                }
            }
            spsPpsParam.flip();
            return spsLen;
        }
    }
}
