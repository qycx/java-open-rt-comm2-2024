


package com.qycx.base;

import com.qycx.base.util.javatuples.Octet;

public class capImageProc {
    public static class qyFuncs {
        public static Octet<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer> new_myGetImageInfo(boolean ucbFixedSize,            //
                                             boolean ucbAutoClip,                                          //  根据目标矩形自动裁剪，使目标矩形被充满
                                             boolean ucb3d,                                                    //  3d显示用， 2014/11/14
                                             int iW_org, int iH_org,
                                             int iX, int iY, int iW, int iH) {

            if (0 == iW_org || 0 == iH_org) return null;
            if (0 == iW || 0 == iH) {
                return null;
            }
			int piW_i,piH_i,piX_dst,piY_dst,piX_src_i,piY_src_i,piW_dst,piH_dst;
            if (ucbFixedSize) {
				piW_i = (int) iW_org;
				piH_i = (int) iH_org;

                if (iW_org > (int) iW) piX_dst = 0;
				else piX_dst = (int) (iX + (iW - iW_org) / 2);

                if (iH_org > (int) iH) piY_dst = 0;
				else piY_dst = (int) (iY + (iH - iH_org) / 2);
                //
				piX_src_i = piY_src_i = 0;
                //
				piW_dst = (int) iW_org;
				piH_dst = (int) iH_org;

				return new Octet<>(piW_i, piH_i, piX_src_i, piY_src_i, piX_dst, piY_dst, piW_dst, piH_dst);
            }

            double f;

            //
            f = iW_org / (iH_org * 1.0f);
            if (f * iH > iW) {               //  ì??-
                //
                int tmp_iW = (int) iW;
                if (!ucb3d) {
                    //tmp_iW  =  tmp_iW  /  4  *  4;
                    //tmp_iW  =  tmp_iW  /  4  *  4;
                }
                //
				piX_src_i = piY_src_i = 0;
				piW_i = (int) tmp_iW;
				piH_i = (int) (tmp_iW / f);            //  iW  /  f;
                //
				piW_dst = piW_i;  //  iW;
				piH_dst = piH_i;  //  (  int  )(  iW  /  f  );
				piX_dst = (int) (iX + (iW -  piW_dst) / 2.0f  );
				piY_dst = (int) (iY + (iH -  piH_dst) / 2.0f  );
				return new Octet<>(piW_i, piH_i, piX_src_i, piY_src_i, piX_dst, piY_dst, piW_dst, piH_dst);
            }

            //
			piX_src_i = piY_src_i = 0;
			piH_i = iH;
			piW_i = (int) (iH * f);
            //  ì??í
			piH_dst = iH;
			piW_dst = (int) (iH * f);
			piY_dst = iY;
			piX_dst = (int) (iX + (iW -  piW_dst) / 2.0f  );

			return new Octet<>(piW_i, piH_i, piX_src_i, piY_src_i, piX_dst, piY_dst, piW_dst, piH_dst);
        }

    }
}
