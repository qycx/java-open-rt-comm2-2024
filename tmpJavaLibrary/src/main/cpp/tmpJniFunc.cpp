
#include <stdlib.h>
#include <string.h>





int hi(int *pResult)
{
    if (pResult==NULL)return -1;

    *pResult=865;
    return 9;
}

//
int sayHello(char * msg,char * buf,int bufSize)
{
    if ( msg==NULL)return -1;
    if(buf==NULL||bufSize==0)return -1;

    //
    snprintf(buf,bufSize,"%s|%s",msg,"from sayHello");
    buf[bufSize-1]=0;

    //
    printf("sayHello called\n");

    return 0;

}


int procByteArray(char * buf,int bufSize)
{
    strncpy(buf,"变化了procByteArray",bufSize);
    return 0;
}