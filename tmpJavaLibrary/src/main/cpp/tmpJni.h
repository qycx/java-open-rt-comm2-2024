//
// Created by qycx1 on 2020/12/6.
//

#ifndef __TMPJNI_H__
#define __TMPJNI_H__

int hi(int *pResult);
//
int sayHello(char * msg,char * buf,int bufSize);

int procByteArray(char * buf,int bufSize);

///////////////////////////
#define BOOL    bool
typedef unsigned char BYTE;

//
int  i420ToRgb( unsigned  char  *  yuv,  unsigned  char  *  rgb,  int rgbBits, bool bBgr,  int width,  int  height,  BOOL  bFlipImg  );
int  rgbToI420(  unsigned  char  *  rgb,  int rgbBits,bool bBgr, unsigned  char  *  yuv,  int  width,  int  height,  BOOL  bFlipImg  );
int rgb32To24(char * rgb32,bool bBgr_32, char *rgb,bool bBgr_24, int w,int h);

//
int rotateYUV420P(unsigned char *src, unsigned char *dst, int width_src, int height_src, int degree,BOOL bFlipImg);



#endif //MY_APPLICATION_MAX_H
