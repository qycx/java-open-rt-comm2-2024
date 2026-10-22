#include <jni.h>
#include <string>
#include "tmpJni.h"

//
//
extern "C" JNIEXPORT jstring JNICALL
Java_introduction_android_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_hiFromJni(JNIEnv *env, jclass clazz, jintArray p_result) {
    // TODO: implement hiFromJni()

    int bb[9];
    hi(bb);

    return 653;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_sayHello(JNIEnv *env, jclass clazz, jstring inStr) {
    // TODO: implement sayHello()
    char buf[128]="initStr";

    const char * inCStr=env->GetStringUTFChars(inStr,NULL);
    //LOGI("-------------- %s", inCStr);
    sayHello((char*)inCStr,buf,sizeof(buf));
    //
    env->ReleaseStringUTFChars(inStr,inCStr);

    //
    return env->NewStringUTF(buf);
}


extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_sumAvg(JNIEnv *env, jclass clazz, jintArray nums) {
    // TODO: implement sumAvg()
    jint * inCArray=env->GetIntArrayElements(nums,NULL);
    if ( inCArray==NULL)return NULL;
    jsize length=env->GetArrayLength(nums);

    jint sum=0;
    int i;
    for (i=0;i<length;i++) {
        sum+=inCArray[i];
    }
    jdouble avg=(jdouble)sum/length;
    env->ReleaseIntArrayElements(nums,inCArray,0);

    jdouble outArray[]={(jdouble )sum,avg};

    jdoubleArray outJArray=env->NewDoubleArray(2);
    if ( NULL==outJArray) return NULL;
    env->SetDoubleArrayRegion(outJArray,0,2,outArray);
    return outJArray;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_procByteArray(JNIEnv *env, jclass clazz, jbyteArray bb,
                                                      jint buf_size) {
    // TODO: implement procByteArray()
    jbyte * bufferIn;
    int len=env->GetArrayLength(bb);
    bufferIn=(jbyte*)env->GetPrimitiveArrayCritical(bb,NULL);

    //
    procByteArray((char*)bufferIn, buf_size);

    //


    env->ReleasePrimitiveArrayCritical(bb,bufferIn,0);

    return 0;

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_allocNativeByteBuffer(JNIEnv *env, jclass clazz,
                                                              jint size) {
    // TODO: implement allocNativeByteBuffer()
    if ( size==0) return NULL;
    void * buffer=new char [size];
    if ( buffer==NULL) return NULL;

    //
    if(size > 10) {
        strncpy((char*)buffer,"hello",size);
    }

    //
    jobject directBuffer=env->NewDirectByteBuffer(buffer,size);



    return directBuffer;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_freeNativeByteBuffer(JNIEnv *env, jclass clazz,
                                                             jobject bb) {
    // TODO: implement freeNativeByteBuffer()
    if ( bb==NULL) return;


    void * buffer=env->GetDirectBufferAddress(bb);

    //
    if ( buffer!=NULL ) {
        free(buffer);
    }
    return;
}


//

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_i420ToRgb(JNIEnv *env, jclass clazz, jobject yuv,
                                                  jobject rgb, jint rgb_bits, jboolean b_bgr,
                                                  jint width, jint height, jboolean b_flip_img) {
    // TODO: implement i420ToRgb()
    int iErr = -1;
    char * pRgb=NULL;
    jbyte * pYuv=NULL;
    int rgbSize;
    int yuvSize;

    if ( 0!=env->PushLocalFrame(2) ) {
        return -1;
    }

    //pYuv=env->GetByteArrayElements(yuv,NULL);
    yuvSize=env->GetDirectBufferCapacity(yuv);
    pYuv=(jbyte *)env->GetDirectBufferAddress(yuv);
    if ( pYuv==NULL) goto errLabel;

    rgbSize=env->GetDirectBufferCapacity(rgb);
    pRgb=(char*)env->GetDirectBufferAddress(rgb);
    if ( pRgb==NULL)goto errLabel;

    //
    i420ToRgb((unsigned char *)pYuv,(unsigned char *)pRgb,rgb_bits, b_bgr, (int)width,(int)height,(BOOL)b_flip_img);

    //
    //
    iErr = 0;
    errLabel:

    /*
    if ( pRgb!=NULL) {
        env->ReleasePrimitiveArrayCritical(rgb, pRgb, 0);
        //env->DeleteLocalRef(rgb);
    }

    if(pYuv!=NULL) {
        //env->ReleaseByteArrayElements(yuv, pYuv, 0);
        env->ReleasePrimitiveArrayCritical(yuv,pYuv,0);
        env->DeleteLocalRef(yuv);
    }
     */

    env->PopLocalFrame(NULL);

    return iErr;



}







extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rgbToI420(JNIEnv *env, jclass clazz, jobject rgb,
                                                  jint rgb_bits, jboolean b_bgr, jobject yuv,
                                                  jint width, jint height, jboolean b_flip_img) {
    // TODO: implement rgbToI420()
    int iErr = -1;
    jbyte * pRgb=NULL;
    char * pYuv=NULL;

    pRgb=(jbyte*)env->GetDirectBufferAddress(rgb);
    if ( pRgb==NULL)return -1;
    pYuv=(char *)env->GetDirectBufferAddress(yuv);
    if ( pYuv==NULL) goto errLabel;


    rgbToI420((unsigned char * )pRgb,rgb_bits,b_bgr, (unsigned char * )pYuv,width,height,b_flip_img);

    iErr = 0;
    errLabel:


    return iErr;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rgb32To24(JNIEnv *env, jclass clazz, jobject rgb32,
                                                  jboolean b_bgr_32, jobject rgb, jboolean b_bgr_24,
                                                  jint w, jint h) {
    // TODO: implement rgb32To24()
    int iErr = -1;
    jbyte * pRgb32=NULL;
    jbyte *pRgb=NULL;

    pRgb32=(jbyte*)env->GetDirectBufferAddress(rgb32);
    if(pRgb32==NULL) goto errLabel;
    pRgb=(jbyte*)env->GetDirectBufferAddress(rgb);
    if(pRgb==NULL)goto errLabel;

    //
    rgb32To24((char*)pRgb32,b_bgr_32,(char*)pRgb,b_bgr_24,w,h);

    iErr =0;
    errLabel:


    return iErr;

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rotateI420(JNIEnv *env, jclass clazz, jobject src,
                                                   jobject dst, jint width_src, jint height_src,
                                                   jint degree,jboolean b_flip_img) {
    // TODO: implement rotateI420()
    int iErr = -1;
    jbyte * pSrc=NULL;
    jbyte * pDst=NULL;

    pSrc=(jbyte*)env->GetDirectBufferAddress(src);
    if ( pSrc==NULL) goto errLabel;
    pDst=(jbyte *)env->GetDirectBufferAddress(dst);
    if ( pDst==NULL) goto errLabel;

    //
    if ( 0!=rotateYUV420P((unsigned char*)pSrc,(unsigned char *)pDst,(int)width_src,(int)height_src,(int)degree,b_flip_img) ) {
        goto errLabel;
    }

    iErr = 0;
    errLabel:
    return iErr;
}

