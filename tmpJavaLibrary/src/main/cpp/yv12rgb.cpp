// yv12.cpp : Defines the entry point for the console application.

//#include	"stdafx.h"

//
//#include	<windows.h>
//
//#include	<tchar.h>
//#include	"imgFunc_open.h"
#include "tmpJni.h"



#if  10

void  colorspace_init(  void  );

class  CColorSpace  {
public:
	CColorSpace(  ) {
		colorspace_init(  );
	}
	~CColorSpace(  )  {
		int jj = 0;
	}
};

CColorSpace  tmp_colorSpace;


#define		uint8_t			unsigned char
#define		uint16_t		unsigned short
#define		uint32_t		unsigned int
#define		int32_t			int

int32_t		RGB_Y_tab[256];
int32_t		B_U_tab[256];
int32_t		G_U_tab[256];
int32_t		G_V_tab[256];
int32_t		R_V_tab[256];

#define	MIN(A,B)			(  (  A )  <  (  B  )  ?  (  A  )  :  (  B  )  )
#define	MAX(A,B)			(  (  A )  >  (  B  )  ?  (  A  )  :  (  B  )  )

/*  yuv  ->  rgb def's  */

#define		RGB_Y_OUT		1.164
#define		B_U_OUT			2.018
#define		Y_ADD_OUT		16

#define		G_U_OUT			0.391
#define		G_V_OUT			0.813
#define		U_ADD_OUT		128

#define		R_V_OUT			1.596
#define		V_ADD_OUT		128


#define		SCALEBITS_OUT	13
#define		FIX_OUT(x)		(  (  uint16_t  )  (  (  x  )  *  (  1L  <<  SCALEBITS_OUT  )  +  0.5  )  )

/*  initialize rgb  lookup  tables  */

void  colorspace_init(  void  )
{
	int32_t		i;

	for (  i  =  0; i  <  256;  i  ++  )  {
		RGB_Y_tab[i]  =  FIX_OUT( RGB_Y_OUT ) * (  i - Y_ADD_OUT  );
		B_U_tab[i]  =  FIX_OUT( B_U_OUT ) * (  i - U_ADD_OUT  );
		G_U_tab[i]  =  FIX_OUT( G_U_OUT ) * (  i - U_ADD_OUT  );
		G_V_tab[i]  =  FIX_OUT( G_V_OUT ) * (  i - V_ADD_OUT  );
		R_V_tab[i]  =  FIX_OUT( R_V_OUT ) * (  i - V_ADD_OUT  );
	}
}

#endif

/*  yuv 4:2:0 planar -> rgb24  */

#if  0

void  yv12_to_rgb24_c( unsigned  char  *  dst,
					  //  int  dst_stride,
					  unsigned  char  *  y_src,
					  unsigned  char  *  u_src,
					  unsigned  char  *  v_src,
					  int	y_stride,
					  int	uv_stride,
					  int	width,
					  int	height  )

{
	int					nBytesPerLine_rgb24	=	(  width  *  24  +  31  )  /  32  *  4;
	//  const  uint32_t  dst_dif  =  6  *  dst_stride  -  3  *  width;
	const  uint32_t  dst_dif  =  nBytesPerLine_rgb24  -  3  *  width;
	int32_t  y_dif  =  2  *  y_stride  -width;

	//  uint8_t  *  dst2  =  dst  +  3  *  dst_stride;
	uint8_t  *  dst2  =  dst  +  nBytesPerLine_rgb24;
	uint8_t  *  y_src2  =  y_src  +  y_stride;
	uint32_t	x,  y;

	if  (  height  <  0  )  {		/*  flip  image?  */
		height  =  -height;
		y_src  +=  (  height  -  1  )  *  y_stride;
		y_src2  =  y_src  -  y_stride;
		u_src  +=  (  height  /  2  -  1  )  *  uv_stride;
		v_src  +=  (  height  /  2  -  1  )  *  uv_stride;
		y_dif	=  -width  - 2 * y_stride;
		uv_stride  =  -uv_stride;
	}

	for  (  y = height /2; y; y --  )  {
		/*  process one  2* 2 block per iteration	*/
		for  (  x  =  0;  x  <  (  uint32_t  ) width  /2;  x++  )  {
			int  u,  v;
			int  b_u,  g_uv,  r_v,  rgb_y;
			int  r,  g,  b;

			u  =  u_src[x];
			v  =  v_src[x];

			b_u	 =  B_U_tab[u];
			g_uv  =  G_U_tab[u]  +  G_V_tab[v];
			r_v	 =  R_V_tab[v];

			rgb_y  =  RGB_Y_tab[*y_src ];
			b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			dst[0]  =  MAX(  0,  MIN(  255,  b  )  );
			dst[1]  =  MAX(  0,  MIN(  255,  g  )  );
			dst[2]  =  MAX(  0,  MIN(  255,  r  )  );

			y_src ++;
			rgb_y  =  RGB_Y_tab[*y_src ];
			b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			dst[3]  =  MAX(  0,  MIN(  255,  b  )  );
			dst[4]  =  MAX(  0,  MIN(  255,  g  )  );
			dst[5]  =  MAX(  0,  MIN(  255,  r  )  );
			y_src ++;

			rgb_y  =  RGB_Y_tab[*y_src2 ];
			b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			dst2[0]  =  MAX(  0,  MIN(  255,  b  )  );
			dst2[1]  =  MAX(  0,  MIN(  255,  g  )  );
			dst2[2]  =  MAX(  0,  MIN(  255,  r  )  );
			y_src2++;

			rgb_y  =  RGB_Y_tab[*y_src2 ];
			b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			dst2[3]  =  MAX(  0,  MIN(  255,  b  )  );
			dst2[4]  =  MAX(  0,  MIN(  255,  g  )  );
			dst2[5]  =  MAX(  0,  MIN(  255,  r  )  );
			y_src2++;

			dst  +=  6;
			dst2  +=  6;
		}
		dst  +=  dst_dif;
		dst2  +=  dst_dif;

		y_src  +=  y_dif;
		y_src2  +=  y_dif;

		u_src  +=  uv_stride;
		v_src  +=  uv_stride;
	}
}

#endif


#if  0
 int  yuv420ToRgb24( unsigned  char  *  y_src,  unsigned  char  *  u_src,  unsigned  char  *  v_src,  unsigned  char  *  rgb,  int  width,  int  height  )
{
	int  y,  u,  v;
	int	 i,  j;
	unsigned  char  *  py1,  *  py2;
	int					nBytesPerLine_rgb24	=	(  width  *  24  +  31  )  /  32  *  4;
	unsigned  int		out;
	unsigned  char		pixel_24[3];
	unsigned  int		pixel32;

	py1  =  y_src;
	py2  =  y_src  +  width;

	for  (  j  =  0;  j  <  height;  j  +=  2  )  {
		 for  (  i  =  0;  i  <  width;  i  +=  2  )  {
			  u  =  *u_src  ++  ;
			  v  =  *v_src  ++  ;
			  
			  //  upper left
			  y  =  *py1  ++  ;
			  pixel32  =  convert_yuv_to_rgb_pixel(  y,  u,  v  );

			  pixel_24[0]  =  (  pixel32  &  0x000000ff  );
			  pixel_24[1]  =  (  pixel32  &  0x0000ff00  )  >>  8;
			  pixel_24[2]  =  (  pixel32  &  0x00ff0000  )  >>  16;

			  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb24  +  i  *  3;  

			  rgb[out]  =  pixel_24[0];
			  rgb[out+1]  =  pixel_24[1];
			  rgb[out+2]  =  pixel_24[2];

			  //  upper right;
			  y  =  *py1  ++  ;
			  pixel32  =  convert_yuv_to_rgb_pixel(  y,  u,  v  );

			  pixel_24[0]  =  (  pixel32  &  0x000000ff  );
			  pixel_24[1]  =  (  pixel32  &  0x0000ff00  )  >>  8;
			  pixel_24[2]  =  (  pixel32  &  0x00ff0000  )  >>  16;

			  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb24  +  (  i  +  1  )  *  3;  

			  rgb[out]  =  pixel_24[0];
			  rgb[out+1]  =  pixel_24[1];
			  rgb[out+2]  =  pixel_24[2];

			  //  down left
			  y  =  *py2  ++  ;
			  
			  pixel32  =  convert_yuv_to_rgb_pixel(  y,  u,  v  );

			  pixel_24[0]  =  (  pixel32  &  0x000000ff  );
			  pixel_24[1]  =  (  pixel32  &  0x0000ff00  )  >>  8;
			  pixel_24[2]  =  (  pixel32  &  0x00ff0000  )  >>  16;

			  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb24  +  i  *  3;  

			  rgb[out]  =  pixel_24[0];
			  rgb[out+1]  =  pixel_24[1];
			  rgb[out+2]  =  pixel_24[2];

			  //  down right;
			  y  =  *py2  ++  ;
			  pixel32  =  convert_yuv_to_rgb_pixel(  y,  u,  v  );

			  pixel_24[0]  =  (  pixel32  &  0x000000ff  );
			  pixel_24[1]  =  (  pixel32  &  0x0000ff00  )  >>  8;
			  pixel_24[2]  =  (  pixel32  &  0x00ff0000  )  >>  16;

			  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb24  +  (  i  +  1  )  *  3;  

			  rgb[out]  =  pixel_24[0];
			  rgb[out+1]  =  pixel_24[1];
			  rgb[out+2]  =  pixel_24[2];
		
		 }
		 py1  +=  width;
		 py2  +=  width;
	}

	return  0;
}
#endif

 int  yuv420ToRgb( unsigned  char  *  y_src,  unsigned  char  *  u_src,  unsigned  char  *  v_src,  unsigned  char  *  rgb,  int rgbBits, bool bBgr, int  width,  int  height,  BOOL  bFlipImg  )
{
	int  y,  u,  v;
	
	int  b_u,  g_uv,  r_v,  rgb_y;
	int  r,  g,  b;

	// 这里错了。所以取个反.
	bFlipImg= !bFlipImg;


	//
	switch(rgbBits){
		case 24:
		case 32:
			   break;
		default:
				return -1;
				break;
	}
	int pixBytes=rgbBits/8;
	bool b32=rgbBits==32;
	int a=255;


	//
	int	 i,  j;
	unsigned  char  *  py1,  *  py2;
	//int					nBytesPerLine_rgb24	=	(  width  *  24  +  31  )  /  32  *  4;
	int					nBytesPerLine_rgb	=	(  width  *  rgbBits  +  31  )  /  32  *  4;
	unsigned  int		out;

	py1  =  y_src;
	py2  =  y_src  +  width;

	for  (  j  =  0;  j  <  height;  j  +=  2  )  {
		 for  (  i  =  0;  i  <  width;  i  +=  2  )  {
			  u  =  *u_src  ++  ;
			  v  =  *v_src  ++  ;
			  
			  //  upper left
			  y  =  *py1  ++  ;

			  b_u	 =  B_U_tab[u];
			  g_uv  =  G_U_tab[u]  +  G_V_tab[v];
			  r_v	 =  R_V_tab[v];

			  rgb_y  =  RGB_Y_tab[y];
			  b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			  g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			  r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;

			  //  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb24  +  i  *  3;  
			  if  (  !bFlipImg  )  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb  +  i  *  pixBytes;
			  else  out  =  (  j  )  *  nBytesPerLine_rgb  +  i  *  pixBytes;

			  //
			  if ( !bBgr) {
                  rgb[out] = MAX(0, MIN(255, b));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out + 2] = MAX(0, MIN(255, r));
              }
			  else {
                  rgb[out] = MAX(0, MIN(255, r));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out+2] = MAX(0, MIN(255, b));
			  }
			  if ( b32)rgb[out+3]=a;

			  //  upper right;
			  y  =  *py1  ++  ;

			  rgb_y  =  RGB_Y_tab[y];
			  b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			  g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			  r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			  
			  //  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb24  +  (  i  +  1  )  *  3;  
			  if  (  !bFlipImg  )  out  =  (  height  -  j  -  1  )  *  nBytesPerLine_rgb  +  (  i  +  1  )  *  pixBytes;
			  else  out  =  (  j  )  *  nBytesPerLine_rgb  +  (  i  +  1  )  *  pixBytes;

			  //
			  if ( !bBgr) {
                  rgb[out] = MAX(0, MIN(255, b));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out + 2] = MAX(0, MIN(255, r));
              }
              else {
                  rgb[out] = MAX(0, MIN(255, r));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out+2] = MAX(0, MIN(255, b));
              }
             if ( b32)rgb[out+3]=a;

			  //  down left
			  y  =  *py2  ++  ;
			  
			  rgb_y  =  RGB_Y_tab[y];
			  b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			  g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			  r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			  
			  //  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb24  +  i  *  3;  
			  if  (  !bFlipImg  )  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb  +  i  *  pixBytes;
			  else  out  =  (  j  +  1  )  *  nBytesPerLine_rgb  +  i  *  pixBytes;

			  //
			  if( !bBgr) {
                  rgb[out] = MAX(0, MIN(255, b));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out + 2] = MAX(0, MIN(255, r));
              }
              else {
                  rgb[out] = MAX(0, MIN(255, r));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out+2] = MAX(0, MIN(255, b));
              }
             if ( b32)rgb[out+3]=a;

			  //  down right;
			  y  =  *py2  ++  ;

			  rgb_y  =  RGB_Y_tab[y];
			  b  =  (  rgb_y  +  b_u  )  >>  SCALEBITS_OUT;
			  g  =  (  rgb_y  -  g_uv  )  >>  SCALEBITS_OUT;
			  r  =  (  rgb_y  +  r_v  )  >>  SCALEBITS_OUT;
			  
			  //  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb24  +  (  i  +  1  )  *  3;  
			  if  (  !bFlipImg  )  out  =  (  height  -  (  j  +  1  )  -  1  )  *  nBytesPerLine_rgb  +  (  i  +  1  )  *  pixBytes;
			  else  out  =  (  j  +  1  )  *  nBytesPerLine_rgb  +  (  i  +  1  )  *  pixBytes;  ;

			  //
			  if(!bBgr) {
                  rgb[out] = MAX(0, MIN(255, b));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out + 2] = MAX(0, MIN(255, r));
              }
              else {
                  rgb[out] = MAX(0, MIN(255, r));
                  rgb[out + 1] = MAX(0, MIN(255, g));
                  rgb[out+2] = MAX(0, MIN(255, b));
              }
             if ( b32)rgb[out+3]=a;

		 }
		 py1  +=  width;
		 py2  +=  width;
	}

	return  0;
}


 int  i420ToRgb( unsigned  char  *  yuv,  unsigned  char  *  rgb,  int rgbBits, bool bBgr, int  width,  int  height,  BOOL  bFlipImg  )
{
	if  (  height  <=  0  )  return  -1;
	unsigned  char  *  y_src  =  yuv;
	unsigned  char  *  u_src  =  yuv  +  width  *  height;
	unsigned  char  *  v_src  =  yuv  +  width  *  height  *  5  /  4;
	
	yuv420ToRgb(  y_src,  u_src,  v_src,  rgb,  rgbBits, bBgr, width,  height,  bFlipImg  );

	return 0;
}


 //
 int  yv12ToRgb24( unsigned  char  *  yuv,  unsigned  char  *  rgb,  int  width,  int  height,  BOOL  bFlipImg  )
{
		
	//
	if  (  height  <=  0  )  return  -1;
	unsigned  char  *  y_src  =  yuv;
	unsigned  char  *  v_src  =  yuv  +  width  *  height;
	unsigned  char  *  u_src  =  yuv  +  width  *  height  *  5  /  4;
	
	yuv420ToRgb(  y_src,  u_src,  v_src,  rgb,  24, false, width,  height,  bFlipImg  );

	return 0;
}


#if  0
 //  2011/06/21
#define	rgbtoyuv(b,g,r,y,u,v)	\
	y  =  (  BYTE  )(((  int  )30  *  r  +  (  int  )59  *  g  +  (  int  )11  *  b  )  /  100  );	\
	u  =  (  BYTE  )(((  int  )-17  *  r  -  (  int  )33  *  g  +  (  int  )50  *  b  +  12800  )  /  100  );	\
	v  =  (  BYTE  )(((  int  )50  *  r  -  (  int  )42  *  g  -  (  int  )8  *  b  +  12800  )  /  100  );
#else		//  2011/11/24

#define rgbtoyuv(b,g,r,y,u,v)  \
	y  =  (  BYTE  )((((  int  )66  *  r  +  (  int  )129  *  g  +  (  int  )25  *  b  +  128  )  >>  8  )  +  16  );  \
	u  =  (  BYTE  )((((  int  )-38  *  r  -  (  int  )74  *  g  +  (  int  )112  *  b  +  128  )  >>  8  )  +  128  );  \
	v  =  (  BYTE  )((((  int  )112  *  r  -  (  int  )94  *  g  -  (  int  )18  *  b  +  128  )  >>  8  )  +128  );

#endif

 //
 int  rgb24ToYv12(  unsigned  char  *  rgb,  unsigned  char  *  yuv,  int  width,  int  height,  BOOL  bFlipImg  )
{

	//  BOOL	bFlip  =  !bFlipImg;		//  因为yuv和rgb的表示方法是倒的

	//
	unsigned  int  rgbIncrement  =  3;

	unsigned  int  planeSize;
	unsigned  int  halfWidth;

	planeSize  =  width  *  height;
	halfWidth  =  width  >>  1;

	unsigned  char  *  y_dst  =  yuv;
	unsigned  char  *  v_dst  =  yuv  +  width  *  height;
	unsigned  char  *  u_dst  =  yuv  +  width  *  height  *  5  /  4;

	unsigned  char  *  rgbIndex	=	rgb;

	int  x,  y;
	unsigned  char  *  yLine;
	unsigned  char  *  uLine;
	unsigned  char  *  vLine;

	for  (  y  =  0;  y  <  height;  y  ++  )  {
		 yLine  =  y_dst  +  y  *  width;
		 uLine  =  u_dst  +  (  y  >>  1  )  *  halfWidth;
		 vLine  =  v_dst  +  (  y  >>  1  )  *  halfWidth;

		 if  (  !bFlipImg  )  {
			 rgbIndex  =  rgb  +  width  *  (  height  -  1  -  y  )  *  rgbIncrement;
		 }

		 for  (  x  =  0;  x  <  width;  x  +=  2  )  {
			  rgbtoyuv(  rgbIndex[0],  rgbIndex[1],  rgbIndex[2],  *yLine,  *uLine,  *vLine  );
			  rgbIndex  +=  rgbIncrement;
			  yLine  ++  ;
			  rgbtoyuv(  rgbIndex[0],  rgbIndex[1],  rgbIndex[2],  *yLine,  *uLine,  *vLine  );
			  rgbIndex  +=  rgbIncrement;
			  yLine  ++  ;
			  uLine  ++  ;
			  vLine  ++  ;			  
		 }
	}

	 return  0;
}


 //
 int  rgbToI420(  unsigned  char  *  rgb,  int rgbBits,bool bBgr, unsigned  char  *  yuv,  int  width,  int  height,  BOOL  bFlipImg  )
{

	//  BOOL	bFlip  =  !bFlipImg;		//  因为yuv和rgb的表示方法是倒的

	switch (rgbBits) {
		case 24:
		case 32:
			  break;
		default:
			return -1;

	}

	//
	unsigned  int  rgbIncrement  =  rgbBits/8;	//3;

	unsigned  int  planeSize;
	unsigned  int  halfWidth;

	planeSize  =  width  *  height;
	halfWidth  =  width  >>  1;

	unsigned  char  *  y_dst  =  yuv;
	unsigned  char  *  u_dst  =  yuv  +  width  *  height;
	unsigned  char  *  v_dst  =  yuv  +  width  *  height  *  5  /  4;

	unsigned  char  *  rgbIndex	=	rgb;

	int  x,  y;
	unsigned  char  *  yLine;
	unsigned  char  *  uLine;
	unsigned  char  *  vLine;

	for  (  y  =  0;  y  <  height;  y  ++  )  {
		 yLine  =  y_dst  +  y  *  width;
		 uLine  =  u_dst  +  (  y  >>  1  )  *  halfWidth;
		 vLine  =  v_dst  +  (  y  >>  1  )  *  halfWidth;

		 if  (  !bFlipImg  )  {
			 rgbIndex  =  rgb  +  width  *  (  height  -  1  -  y  )  *  rgbIncrement;
		 }

		 for  (  x  =  0;  x  <  width;  x  +=  2  )  {
		 	  if ( !bBgr) {
				  rgbtoyuv(rgbIndex[0], rgbIndex[1], rgbIndex[2], *yLine, *uLine, *vLine);
			  }
		 	  else {
				  rgbtoyuv(rgbIndex[2], rgbIndex[1], rgbIndex[0], *yLine, *uLine, *vLine);
		 	  }
			  rgbIndex  +=  rgbIncrement;
			  yLine  ++  ;
			  if ( !bBgr) {
				  rgbtoyuv(rgbIndex[0], rgbIndex[1], rgbIndex[2], *yLine, *uLine, *vLine);
			  }
			  else {
				  rgbtoyuv(rgbIndex[2], rgbIndex[1], rgbIndex[0], *yLine, *uLine, *vLine);

			  }
			  rgbIndex  +=  rgbIncrement;
			  yLine  ++  ;
			  uLine  ++  ;
			  vLine  ++  ;			  
		 }
	}

	 return  0;
}


int rgb32To24(char * rgb32,bool bBgr_32, char *rgb,bool bBgr_24, int w,int h)
{
	int i,j;
	int line_32=w*4;
	int line_24=w*3;

	if ( !bBgr_32)return -1;
	if(bBgr_24) return -1;

	int iIn,iOut;

	for ( j=0;j<h;j++){
		for ( i=0;i<w;i++){
			iIn  = j * line_32 + i * 4;
			iOut  = j * line_24 + i * 3;

			if (bBgr_32)
			{
				rgb[iOut] = rgb32[iIn + 2];
				rgb[iOut + 1] = rgb32[iIn + 1];
				rgb[iOut + 2] = rgb32[iIn];
			}

		}
	}

	return 0;
}


 /////////////////

