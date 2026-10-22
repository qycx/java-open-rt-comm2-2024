
#include "tmpJni.h"

static inline void
rotateYUV420P90(unsigned char *srcY, unsigned char *srcU, unsigned char *srcV,
                unsigned char *dstY, unsigned char *dstU, unsigned char *dstV,
                int width_src, int height_src)
                {
    int width=width_src;
    int height=height_src;

    //rotate y
    int dstIndex = 0;
    int srcIndex = 0;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            dstIndex = x * height + height - y - 1;
            dstY[dstIndex] = srcY[srcIndex++];
        }
    }

    //rotate uv
    int uvHeight = height / 2;
    int uvWidth = width / 2;
    int dstUVIndex = 0;
    int srcUVIndex = 0;
    for (int y = 0; y < uvHeight; y++) {
        for (int x = 0; x < uvWidth; x++) {
            dstUVIndex = x * uvHeight + uvHeight - y - 1;
            dstU[dstUVIndex] = srcU[srcUVIndex];
            dstV[dstUVIndex] = srcV[srcUVIndex];
            srcUVIndex++;
        }
    }
}

//
static inline int
rotateYUV420P90_new(unsigned char *srcY, unsigned char *srcU, unsigned char *srcV,
                unsigned char *dstY, unsigned char *dstU, unsigned char *dstV,
                int width_src, int height_src,
                bool bFlipImg
                )
{
    int iErr = -1;
    int width=width_src;
    int height=height_src;

    //rotate y
    int dstIndex = 0;
    int srcIndex = 0;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            if ( !bFlipImg) {
                dstIndex = x * height + height - y - 1;
            }
            else {
                dstIndex=(width-1-x)*height+height-y-1;
            }
            //
            dstY[dstIndex] = srcY[srcIndex++];
        }
    }

    //rotate uv
    int uvHeight = height / 2;
    int uvWidth = width / 2;
    int dstUVIndex = 0;
    int srcUVIndex = 0;
    for (int y = 0; y < uvHeight; y++) {
        for (int x = 0; x < uvWidth; x++) {
            if ( !bFlipImg) {
                dstUVIndex = x * uvHeight + uvHeight - y - 1;
            }
            else {
                 dstUVIndex=(uvWidth-1-x)*uvHeight+uvHeight-y-1;
            }
            //
            dstU[dstUVIndex] = srcU[srcUVIndex];
            dstV[dstUVIndex] = srcV[srcUVIndex];
            srcUVIndex++;
        }
    }

    iErr = 0;
    errLabel:
    return iErr;
}





//
static inline void
rotateYUV420P180(unsigned char *srcY, unsigned char *srcU, unsigned char *srcV,
                 unsigned char *dstY, unsigned char *dstU, unsigned char *dstV,
                 int width_src, int height_src)
                 {
    int width=width_src;
    int height=height_src;

    //rotate y
    int dstIndex = 0;
    int srcIndex = 0;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            dstIndex = (height - y - 1) * width + width - x - 1;
            dstY[dstIndex] = srcY[srcIndex++];
        }
    }

    //rotate uv
    int uvHeight = height / 2;
    int uvWidth = width / 2;
    int dstUVIndex = 0;
    int srcUVIndex = 0;
    for (int y = 0; y < uvHeight; y++) {
        for (int x = 0; x < uvWidth; x++) {
            dstUVIndex = (uvHeight - y - 1) * uvWidth + uvWidth - x - 1;
            dstU[dstUVIndex] = srcU[srcUVIndex];
            dstV[dstUVIndex] = srcV[srcUVIndex];
            srcUVIndex++;
        }
    }
}

static inline void
rotateYUV420P270(unsigned char *srcY, unsigned char *srcU, unsigned char *srcV,
                 unsigned char *dstY, unsigned char *dstU, unsigned char *dstV,
                 int width_src, int height_src)
                 {
    int width=width_src;
    int height=height_src;

    //rotate y
    int dstIndex = 0;
    int srcIndex = 0;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            dstIndex = (width - x - 1) * height + y;
            dstY[dstIndex] = srcY[srcIndex++];
        }
    }

    //rotate uv
    int uvHeight = height / 2;
    int uvWidth = width / 2;
    int dstUVIndex = 0;
    int srcUVIndex = 0;
    for (int y = 0; y < uvHeight; y++) {
        for (int x = 0; x < uvWidth; x++) {
            dstUVIndex = (uvWidth - x - 1) * uvHeight + y;
            dstU[dstUVIndex] = srcU[srcUVIndex];
            dstV[dstUVIndex] = srcV[srcUVIndex];
            srcUVIndex++;
        }
    }
}


//
int rotateYUV420P(unsigned char *src, unsigned char *dst, int width_src, int height_src, int degree,BOOL bFlipImg)
{
    int iErr = -1;
    int width=width_src;
    int height=height_src;

    //
    unsigned char *pSrcY = src;
    unsigned char *pSrcU = src + width * height;
    unsigned char *pSrcV = src + width * height / 4 * 5;

    unsigned char *pDstY = dst;
    unsigned char *pDstU = dst + width * height;
    unsigned char *pDstV = dst + width * height / 4 * 5;

    switch (degree) {
        case 90:
            if( 0!=rotateYUV420P90_new(pSrcY, pSrcU, pSrcV, pDstY, pDstU, pDstV, width, height,bFlipImg)){
                goto errLabel;
            }
            break;
        case 180:
            rotateYUV420P180(pSrcY, pSrcU, pSrcV, pDstY, pDstU, pDstV, width, height);
            break;
        case 270:
            rotateYUV420P270(pSrcY, pSrcU, pSrcV, pDstY, pDstU, pDstV, width, height);
            break;
        default:
            goto errLabel;
            break;
    }

    iErr = 0;
    errLabel:
    return iErr;
}



