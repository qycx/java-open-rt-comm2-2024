


package com.qycx.base;

public class colorSpaceFunc {
    public static class qyFuncs {

        public static int M_sizeImage(int bits, int biWidth, int biHeight) {
            return (((biWidth) * (bits) + 31) / 32 * 4 * (biHeight));
        }

        //  #define		M_rgb24SizeImage(  biWidth,  biHeight  )	  (  (  (  biWidth  )  *  24  +  31  )  /  32  *  4  *  (  biHeight  )  )
        public static int M_rgb24SizeImage(int biWidth, int biHeight) {
            return M_sizeImage(24, biWidth, biHeight);
        }

    }
}
