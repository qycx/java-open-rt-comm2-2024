


package com.qycx.base;

import static com.qycx.base.tmpFunc.qyFuncs.mystrncpy;

public class videoFunc_open {

    public static class Consts {
        public final static int BI_RGB = 0;
        public final static int BI_RLE8 = 1;
        public final static int BI_RLE4 = 2;
        public final static int BI_BITFIELDS = 3;
        public final static int BI_JPEG = 4;
        public final static int BI_PNG = 5;

        //
        public final static int cntof_buf5 = 5;

    }

    public static class TMP_buf5 {
        public byte buf[] = new byte[Consts.cntof_buf5];//[];
    }

    public static class qyFuncs {


        /// <summary>
        ///
        /// </summary>
        /// <param name="fourcc"></param>
        /// <param name="fourccStr"></param>
        /// <param name="size"></param>
        /// <returns></returns>
        public static int makeBmpInfoHeader_rgb(int bits, int iWidth, int iHeight, qyCommProc_open.BITMAPINFOHEADER p) {
            if (null == p) return -1;

            //mymemset(p, 0, sizeof(qyCommProc_open.BITMAPINFOHEADER));

            p.biSize = 40;//sizeof(qyCommProc_open.BITMAPINFOHEADER);
            p.biWidth = iWidth;
            p.biHeight = iHeight;
            p.biPlanes = 1;
            p.biBitCount = (short) bits;
            p.biCompression = Consts.BI_RGB;
            p.biSizeImage = (p.biWidth * p.biBitCount + 31) / 32 * 4 * p.biHeight;
            p.biClrUsed = (bits <= 8) ? 1 << (int) bits : 0;

            return 0;
        }

        public static int makeBmpInfoHeader_yuy2(int bits, int iWidth, int iHeight, qyCommProc_open.BITMAPINFOHEADER p) {
            if (null == p) return -1;

            p.biSize = 40;//sizeof(qyCommProc_open.BITMAPINFOHEADER);
            p.biWidth = iWidth;
            p.biHeight = iHeight;
            p.biPlanes = 1;
            p.biBitCount = 16; //  (  WORD  )bits;
            p.biCompression = qyCommProc_open.Consts.CONST_fourcc_YUY2;
            p.biSizeImage = (p.biWidth * p.biBitCount + 31) / 32 * 4 * p.biHeight;
            p.biClrUsed = (bits <= 8) ? 1 << (int) bits : 0;
            return 0;
        }


        public static int makeBmpInfoHeader_yv12(int bits, int iWidth, int iHeight, qyCommProc_open.BITMAPINFOHEADER p) {
            if (null == p) return -1;

            //  2014/07/20
            bits = 12;

            p.biSize = 40;//sizeof(qyCommProc_open.BITMAPINFOHEADER);
            p.biWidth = iWidth;
            p.biHeight = iHeight;
            p.biPlanes = 1;
            p.biBitCount = 12;
            p.biCompression = qyCommProc_open.Consts.CONST_fourcc_YV12;
            p.biSizeImage = (p.biWidth * p.biBitCount + 31) / 32 * 4 * p.biHeight;
            p.biClrUsed = (bits <= 8) ? 1 << (int) bits : 0;

            return 0;
        }

        public static int makeBmpInfoHeader_i420(int bits, int iWidth, int iHeight, qyCommProc_open.BITMAPINFOHEADER p) {
            if (null == p) return -1;

            //  2014/07/20
            bits = 12;

            //
            //mymemset(p, 0, sizeof(qyCommProc_open.BITMAPINFOHEADER));

            p.biSize = 40;//sizeof(qyCommProc_open.BITMAPINFOHEADER);
            p.biWidth = iWidth;
            p.biHeight = iHeight;
            p.biPlanes = 1;
            p.biBitCount = 12;
            p.biCompression = qyCommProc_open.Consts.CONST_fourcc_I420;
            p.biSizeImage = (p.biWidth * p.biBitCount + 31) / 32 * 4 * p.biHeight;
            p.biClrUsed = (bits <= 8) ? 1 << (int) bits : 0;
            return 0;
        }

        //  2014/07/23
        public static int makeBmpInfoHeader_yuv(int iFourcc, int bits, int iWidth, int iHeight, qyCommProc_open.BITMAPINFOHEADER p) {
            switch (iFourcc) {
                case qyCommProc_open.Consts.CONST_fourcc_YV12:
                    return makeBmpInfoHeader_yv12(bits, iWidth, iHeight, p);
                case qyCommProc_open.Consts.CONST_fourcc_I420:
                    return makeBmpInfoHeader_i420(bits, iWidth, iHeight, p);
                default:
                    break;
            }

            return -1;
        }

        public static byte[] iFourcc2Str(int fourcc, byte[] fourccStr, int size) {
            //byte chs[5] = "";
            TMP_buf5 buf5 = new TMP_buf5();
            int i = 0;

            i = 0;
            buf5.buf[i] = (byte) fourcc;
            i++;
            buf5.buf[i] = (byte) (fourcc >> 8);
            i++;
            buf5.buf[i] = (byte) (fourcc >> 16);
            i++;
            buf5.buf[i] = (byte) (fourcc >> 24);
            i++;
            buf5.buf[i] = 0;

            //
            if (null == fourccStr) {
                //OutputDebugString(  CQyString(  (  char  *  )chs  )  +  (  "\n"  )  );
            }

            //
            if (null != fourccStr && size > 4) {
                //
                //safeStrnCpy(  (  char  *  )chs,  fourccStr,  size  );
                mystrncpy(fourccStr, buf5.buf, (int) size);
                //
                return fourccStr;
            }

            return null;
        }

        //
        public static int myMAKEFOURCC(byte ch0, byte ch1, byte ch2, byte ch3) {
            return ((int) (byte) (ch0) | ((int) (byte) (ch1) << 8) | ((int) (byte) (ch2) << 16) | ((int) (byte) (ch3) << 24));
        }

        public static int myMAKEFOURCC(char ch0, char ch1, char ch2, char ch3) {
            return myMAKEFOURCC((byte) ch0, (byte) ch1, (byte) ch2, (byte) ch3);
        }


        //
        public static int fourccStr2i(byte[] fourccStr) {
            if (null == fourccStr) return -1;

            //return  mmioFOURCC(  fourccStr[0],  fourccStr[1],  fourccStr[2],  fourccStr[3]  );
            return (int) myMAKEFOURCC(fourccStr[0], fourccStr[1], fourccStr[2], fourccStr[3]);
        }
    }
}
