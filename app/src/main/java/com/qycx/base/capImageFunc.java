


package com.qycx.base;

public class capImageFunc {
    public static class qyFuncs {
        //  dar  =  m  /  n
        public static int tmp_getDar(int iW_org, int iH_org, int iW, int iH, int m_o, int n_o) {
            int maxI = iW_org / 16;
            //
            int iW_real;
            int iH_real;
            int m, n;
            //
            int iW_real1 = 0;
            int m1 = 0, n1 = 0;
            //

            int k, kk;

            if (maxI <= 0) return -1;

            for (n = 1; n <= maxI; n++) {

                if (n != 1) {
                    //  要求都是2的n次方
                    int t;
                    for (t = n; ; ) {
                        if (0 != (t % 2)) break;
                        t = t / 2;
                    }
                    if (t != 1) continue;
                }

                if (0 != (iW_org % n)
                        || 0 != (iH_org % n)) {
                    continue;
                }

                //
                k = iW / (iW_org / n);
                if (0 != ((k * iW_org / n) % 2)
                        || 0 != ((k * iH_org / n) % 2)) {
                    k = k / 2 * 2;
                }
                kk = iH / (iH_org / n);
                if (0 != ((kk * iH_org / n) % 2)
                        || 0 != ((kk * iW_org / n) % 2)) {
                    kk = kk / 2 * 2;
                }
                //
                m = Math.min(k, kk);
                if (0 == m) continue;

                iW_real = iW_org / n * m;
                iH_real = iH_org / n * m;

                //
                if (iW_real > iW_real1) {
                    iW_real1 = iW_real;
                    m1 = m;
                    n1 = n;
                }
                continue;
            }

            if (0 == m1 || 0 == n1) return -1;

            m_o = m1;
            n_o = n1;
            return 0;
        }

        //  dar  =  m  /  n
        public static int tmp_getDar_autoClip(int iW_org, int iH_org, int iW, int iH, int m_o, int n_o) {
            //  int	maxI  =  iW_org  /  16;
            int maxI = iW_org / 64;
            //
            int iW_real;
            int iH_real;
            int m, n;
            //
            int iW_real1 = 0;
            int m1 = 0, n1 = 0;
            //

            //

            //
            int k, kk;

            if (maxI <= 0) return -1;

            for (n = 1; n <= maxI; n++) {

                if (n != 1) {
                    //  要求都是2的n次方
                    int t;
                    for (t = n; ; ) {
                        if (0 != (t % 2)) break;
                        t = t / 2;
                    }
                    if (t != 1) continue;
                }

                if (0 != (iW_org % n)
                        || 0 != (iH_org % n)) {
                    continue;
                }

                //
                k = iW / (iW_org / n);
                if (k * iW_org / n < iW) k++;
                if (0 != ((k * iW_org / n) % 2)
                        || 0 != ((k * iH_org / n) % 2)) {
                    k = (k + 1) / 2 * 2;
                }
                kk = iH / (iH_org / n);
                if (kk * iH_org / n < iH) kk++;
                if (0 != ((kk * iH_org / n) % 2)
                        || 0 != ((kk * iW_org / n) % 2)) {
                    kk = (kk + 1) / 2 * 2;
                }
                //
                m = Math.max(k, kk);
                if (0 == m) continue;

                iW_real = iW_org / n * m;
                iH_real = iH_org / n * m;

                //
                if (0 == iW_real1
                        || iW_real < iW_real1) {
                    iW_real1 = iW_real;
                    m1 = m;
                    n1 = n;
                }
                continue;
            }

            if (0 == m1 || 0 == n1) return -1;

            m_o = m1;
            n_o = n1;
            return 0;
        }


        public static int myGetImageInfo(boolean ucbFixedSize,            //
                                         boolean ucbAutoClip,                                            //  根据目标矩形自动裁剪，使目标矩形被充满
                                         boolean ucb3d,                                                  //  3d显示用， 2014/11/14
                                         int iW_org, int iH_org,
                                         int iX, int iY, int iW, int iH,
                                         int piW_i, int piH_i,
                                         int piX_src_i, int piY_src_i,
                                         int piX_dst, int piY_dst, int piW_dst, int piH_dst) {
            //TCHAR tBuf[256];

            if (0 == piX_dst || 0 == piY_dst || 0 == piW_dst || 0 == piH_dst) return -1;
            if (0 == iW_org || 0 == iH_org) return -1;
            if (0 == iW || 0 == iH) {
                piW_i = piH_i = 0;
                piX_src_i = piY_src_i = 0;
                piX_dst = piY_dst = piW_dst = piH_dst = 0;
                return 0;
            }

            if (ucbFixedSize) {                   //  2??ü·?′ó??D?
                //
                piW_i = iW_org;
                piH_i = iH_org;
                //
                if (iW_org > (int) iW) piX_dst = 0;
                else piX_dst = iX + (iW - iW_org) / 2;

                if (iH_org > (int) iH) piY_dst = 0;
                else piY_dst = iY + (iH - iH_org) / 2;
                //
                piX_src_i = piY_src_i = 0;
                //
                piW_dst = iW_org;
                piH_dst = iH_org;

                return 0;
            }

            boolean b16X9 = false;
            if (!ucb3d) {           //  3d不需要整数比例就可以缩放自如。2014/11/14
                if (iH_org * 16 == iW_org * 9) b16X9 = true;
            }

            //
            int m = 0, n = 0;
            int nUnit;

            if (true && ucbAutoClip) {

                //  2014/04/11
                if (b16X9) {
                    nUnit = 8;

                    if (0 == tmp_getDar_autoClip(iW_org, iH_org, iW, iH, m, n)) {
                        //
                        int iW_i, iH_i;
                        iW_i = iW_org / n * m;
                        iH_i = iH_org / n * m;
                        //
                        piW_i = iW_i;
                        piH_i = iH_i;
                        //
                        piW_dst = iW / nUnit * nUnit;
                        piH_dst = iH / nUnit * nUnit;
                        piX_dst = iX + (iW - piW_dst) / 2;
                        piY_dst = iY + (iH - piH_dst) / 2;
                        //
                        piX_src_i = (iW_i - piW_dst) / 2;
                        piY_src_i = (iH_i - piH_dst) / 2;

                        return 0;
                    }
                }

                //
                double ff;
                nUnit = 8;  //  16;

                //
                piW_dst = iW;
                piH_dst = iH;
                //
                if (iW >= piW_dst) piX_dst = iX + (iW - piW_dst) / 2;
                else piX_dst = iX;
                if (iH >= piH_dst) piY_dst = iY + (iH - piH_dst) / 2;
                else piY_dst = iY;

                //
                if (0 == piH_dst) return -1;

                //
                ff = piW_dst / (piH_dst * 1.0f);
                if (iW_org / (iH_org * 1.0f) > ff) {
                    //
                    piH_i = piH_dst;
                    piW_i = (int) (piH_i * iW_org / (1.0f * iH_org));

                    //
                    piX_src_i = (piW_i - piW_dst) / 2;
                    piY_src_i = 0;

                    //
                    return 0;
                }
                //
                piW_i = piW_dst;
                piH_i = (int) (piW_i * iH_org / (1.0 * iW_org));
                //
                piX_src_i = 0;
                piY_src_i = (piH_i - piH_dst) / 2;

                //
                //
                return 0;
            }

            double f;

            if (b16X9) {

                if (0 == tmp_getDar(iW_org, iH_org, iW, iH, m, n)) {
                    //
                    piX_src_i = piY_src_i = 0;
                    piW_i = iW_org / n * m;
                    piH_i = iH_org / n * m;
                    //
                    piW_dst = iW_org / n * m;
                    piH_dst = iH_org / n * m;
                    piX_dst = iX + (iW - piW_dst) / 2;
                    piY_dst = iY + (iH - piH_dst) / 2;

                    return 0;
                }
                //  qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (  ""  ),  (  "IsClient"  ),  0,  (  ""  ),  (  ""  ),  (  "tmp_getDar failed"  )  );
            }

            //
            f = iW_org / (iH_org * 1.0f);
            if (f * iH > iW) {                //  ì??-
                int tmp_iW = iW;
                if (!ucb3d) {
                    tmp_iW = tmp_iW / 4 * 4;
                }
//
                piX_src_i = piY_src_i = 0;
                piW_i = tmp_iW;
                piH_i = (int) (tmp_iW / f);            //  iW  /  f;
                //
                piW_dst = piW_i;  //  iW;
                piH_dst = piH_i;  //  (  int  )(  iW  /  f  );
                piX_dst = iX;
                piY_dst = (int) (iY + (iH - piH_dst) / 2.0f);
                return 0;
            }

            //
            piX_src_i = piY_src_i = 0;
            piH_i = iH;
            piW_i = (int) (iH * f);
//  ì??í
            piH_dst = iH;
            piW_dst = (int) (iH * f);
            piY_dst = iY;
            piX_dst = (int) (iX + (iW - piW_dst) / 2.0f);

            return 0;
        }


    }
}
