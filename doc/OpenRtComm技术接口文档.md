# OpenRtComm (windows version）技术接口文档

 [OpenRtcomm](https://bbs.qycx.com/index/index/tags_search.html?tag_id=93&name=OpenRtcomm) [specification](https://bbs.qycx.com/index/index/tags_search.html?tag_id=111&name=specification)

清扬视频会议系统，是一个实时音视频信息传输交换系统。

## 1.实时性。

音视频的实时同步，是本实时应用系统的核心基础。为了减少时延保证实时性，系统把数据进行分类。

## 2.应用系统中的数据分类。

### 2.1 按内容分类

第1类，控制信息，譬如指令、用户信息、分组信息等；

第2类，普通数据，譬如文件等；

第3类，视频数据；

第4类，音频数据；

### 2.2 对应了4个通道：

```
#define CONST_channelType_talking 1 //  控制信息通道，数据量小，实时行要求高

#define CONST_channelType_robot 2 // 普通数据通道，特点是可以收发，但是实时性没有视频和音频信息要求那么高。

#define CONST_channelType_media 4 //  视频信息通道，数据量超大，实时性要求极高

#define CONST_channelType_realTimeMedia 5 //  音频信息通道，数据量大，实时性要求极高
```

### 2.3 按打包后通信分类（把上面内容打包到通信结构中了）：

1类：服务器端到客户端：CONST_misMsgType_input

2类：客户端到服务器：    CONST_misMsgType_req

3类：客户端到客户端：    CONST_misMsgType_talk // 消息

4类：客户端到客户端：    CONST_misMsgType_task // 消息之外的音视频和文件等

## 3. 应用系统中的数据处理（中转）线程分类。

上述数据需要分别处理，每种数据类型都对应一种处理线程。

```
DWORD WINAPI mcThreadProc_mis_mgr( LPVOID lpParameter ); // manager中转线程，处理控制消息的

DWORD WINAPI mcThreadProc_mis_robot( LPVOID lpParameter ); // 用于处理文件等普通数据

DWORD WINAPI mcThreadProc_mis_media( LPVOID lpParameter ); // 用于处理视频

DWORD WINAPI mcThreadProc_mis_realTimeMedia( LPVOID lpParameter ); // 用于处理音频
```

## 4. 中转后的数据终点，即中转线程对应的最终结点

### 4.1  一类是：gui界面。

  这是其中一个最终对象；在android上播放器通过GPU处理，在PC上需要区分3D播放和windows播放。

### 4.2 一类是：player_audio；

播放器player对象。 doApplyForPlayer( ... ) 这个就是播放器对象，一个视频播放器，一个音频播放器。

```
extern "C" DWORD WINAPI mcThreadProc_playAudio( LPVOID lpParameter );   // 直接可以播放audio了。

extern "C" DWORD WINAPI mcThreadProc_playVideo( LPVOID lpParameter );   //
```

playvideo不是终点，是为了同步时间戳，然后扔到GUI界面，主要是GUI界面来播放；

### 4.3 一类是：robot；普通数据

### 4.4 一类是：mcThreadProc_mis_scheduler

这个也是最终对象；慢速的信息处理，譬如:分组信息，用户信息等；

现在主要用来做联系人信息和分组信息的更新；昨天有人问日志怎么存？就可以用这个scheduler来存；

### 5.  数据处理（中转）线程的运行机制（数据怎么中转、怎么工作的？）

首先，涉及通道的概念，通道的数据结构如下：

typedef  struct  __misChannel_t {...}   MIS_CHANNEL;

线程怎么运行呢？每个中转线程都会启动一个通道，每个通道的主要功能就是socket收发功能，socket收发按理说应该是一个收线程，一个发线程，但是为了加快处理速度，发线程分为预发送线程和发送线程。即通道涉及到以下三个线程。

DWORD WINAPI mcThreadProc_mis_io( LPVOID lpParameter );          //  收线程

DWORD WINAPI mcThreadProc_mis_toSend( LPVOID lpParameter ); // 预发送线程

DWORD WINAPI mcThreadProc_mis_snd( LPVOID lpParameter );       // 发线程

————————————————————————————————

下面6、7、8分别单独介绍接收线程、预发送线程和发送线程的运行流程。

## 6. 接收线程mcThreadProc_mis_io的运行机制（怎么收取读取数据的？）

DWORD WINAPI mcThreadProc_mis_io( LPVOID lpParameter ){...} 函数实现了以下三个步骤：

1） 收取数据

qyRecvMsg(...) //从服务器上把数据收下来

2） 数据解密

decryptData(...) //把数据脱密

3） 数据进入中转线程，进行数据处理。

```
if  (  (  tmpiRet  =  pProcInfo->postMsg2Mgr_mc(  pMisCnt,  NULL,  
CONST_misMsgType_input,  msgHead.ucFlg,  msgHead.usCode,  (  time_t  
)msgHead.i64StartTime,  msgHead.uiTranNo,  0,  (  char  *  
)&pMsg->data,  pMsg->lenInBytes,  0,  0,  
pChannel->uiType,  pMsgBuf,  FALSE  )  )  )  
{
  if  (  tmpiRet  ==  CONST_qyRet_qFull  )  
  {

        switch  (  pChannel->uiType  )  {
            case  CONST_channelType_media:
            case  CONST_channelType_realTimeMedia:
    qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (  ""  ),  
pProcInfo->who_showInfo,  0,  where_showInfo,  _T(  ""  ),  _T(  
"postMsg2Mgr_mc returns qFull, skipped."  )  );    
                continue;
            default:
                break;
         }
  }

  qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (  ""  ),  
  pProcInfo->who_showInfo,  0,  where_showInfo,  _T(  ""  ),  _T(  
"postMsg2Mgr_mc failed."  )  );    
  goto  errLabel;
 }
```

## 7. 预发送线程mcThreadProc_mis_toSend的运行机制（数据在预发送线程做了什么预处理工作？）

DWORD WINAPI mcThreadProc_mis_toSend( LPVOID lpParameter )函数完成的数据的预处理。

为什么数据需要进行预处理呢？几乎所有的网络IO发送都是慢动作，为了加速，才需要做预处理。

如何进行预处理？首先针对不同的数据类型进行不同的预处理。下面以audio数据为例，看一下audio类型的数据是怎么进行预处理的。

对于audio数据，要到audio的实时任务里面去取，找到要发送的audio数据，然后打包发送；（video数据预处理的过程类似。）

怎么取得audio数据（realTimeMedia）？

```
  if  (  pChannel->uiType  ==  CONST_channelType_realTimeMedia  )  {
  tmpiRet  =  pProcInfo->processQ_media.qTraverse(tmpHandler_toSendMedia,
    &commonParam0,  &commonParam1  );
```

  其中：

      processQ_media，实时任务队列，所有的实时任务都放在这个队列里。

      processQ_media.qTraverse，枚举所有的实时任务。

      tmpHandler_toSendMedia，此函数为函数指针，通过枚举实时任务，找到音频数据。（视频数据也在这个函数里）

  该过程处理完毕，取得audio数据。（同样，也可以取得视频数据）

  下面把上面过程具体讲解一下。展开函数指针的处理过程，讲讲如果找到音频数据的。  （视频数据也在这个函数里）

  上面过程主要是通过调用函数指针，从实时任务队列中找到音频数据（或者视频数据）

   函数指针对应的函数为：

       int  tmpHandler_toSendMedia(  void  *  p0,  void  *  p1,  TASK_common  *  taskElemParam  )

   其中：

   TASK_common，实时任务队列节点里的内部数据。

   讲解：

##### 第1）实时任务分类。

        实时任务是最最核心的对象。每个实时任务都分两类数据：TASK_common，QMC_TASK_INFO，这是两个结构。

TASK_common ， 是直接放在processQ_meida 队列里面的节点；比较安全，不可以从外面访问的；只有枚举的时候才能访问的。实时任务自己处理的信息要放在其中。

QMC_TASK_INFO， 是放在processQ_meida 队列外面的；别的程序可以从外面访问的。公共信息可以存放其中。

##### 第2）如何区分 音频数据还是视频数据。

```
pChannel->uiType  ==  CONST_channelType_realTimeMedia; //这是音频数据；
pChannel->uiType  ==  CONST_channelType_media;         //这是视频数据；
```

##### 第3）如何区分是不是会议发起方？（音频需要区分，视频也需要区分）

pTaskInfo->var.m_bReplyTask， 

false，表示是会议发起方；

true，表示是会议参会方；

（注释：对于会议发起方来说，一类是用户发起方（点对点），一类是MCU发起方（会议）。这个信息用途不大）

##### 第4）把同一个任务里的数据打成一个包放入输出队列

qTraverse(  pOutputQ_audio,  tmpHandler_toMixer_audio,  &commonParam0,  p1  );

其中：

pOutputQ_audio， 从某一个实时任务的音频输出队列取得数据，送到发送队列，完成预发送，等待发送。

（注释：某一个实时任务都对应一个音频输出队列pOutputQ_audio和视频输出队列pOutputQ_video）

tmpHandler_toMixer_audio，通过该函数指针，找到音频的输出队列，然后组成包（tmpHandler_toMixer_audio），转换网络字节顺序，要加密，然后送到输出队列里面。

函数指针tmpHandler_toMixer_audio对应的函数为：

```
int  tmpHandler_toMixer_audio(  void  *  p0,  void  *  p1, 
   void  *  pMsgElemParam  )

.....

  switch  (  pContentElem->uiType  )  {
    case  CONST_imCommType_transferAudioData:  {

       if  (  !pMsgMixer->uiType  )  {
            tmpInitMsgMixer(pProcInfo,  pMisCnt,  pMsgTask_av,  pAvTran,  pTask,  
              CONST_imCommType_transferAudioData,  pMsgElem,  pMsgMixer);
       }
       if  (  pMixer->uiTranNo_openAvDev  !=  
               pContentElem->transferAudioData.uiTranNo_openAvDev  )  {
            if  (  pMixer->usCnt  )  {
                 if  (  pQyMc->env.bDebugInfo  &&  
                     ProcInfo->cfg.debugStatusInfo.ucbViewAudioStatus  )  {

//qyShowInfo1(  CONST_qyShowType_detail,  0,  "",  
           pProcInfo->who_showInfo,  NULL,  _T(  ""  ),  _T(  ""  ),  _T(  
"tmpHadler_toMixer: flush mixer data. mixer.usCnt %d, mixer.uiLen %d,  
mem.len %d."  ),  pMixer->usCnt,  pMixer->uiLen,  
pContentElem->transferAudioData.mems[i].uiLen  );

}

tmpHandler_toSend(  pCommonParam0_toSend,  p1,  pMsgMixer  );//预发送
```

讲解：

上面最后这行就是预发送；

预发送函数指针为：

> int  tmpHandler_toSend(  void  *  p0,  void  *  p1,  void  *  pMsgElemParam  )

其中：

讲解， 

##### 第1）qyhtonRouteTalkData( ... ) :转换字节顺序；

##### 第2）送到channel的outputQ里

> if  (  postMsg2OutputQ_mc(  pMisCnt,  (  MIS_MSGU  *  )pMsgTask_o,  lenInBytes,  pChannel  )  )  {

##### 第3）这是outputQ

int  postMsg2OutputQ_mc(  MIS_CNT  *  pMisCnt,  MIS_MSGU  *  pMsg,  unsigned  int  msgLen,  MIS_CHANNEL  *  pChannel  )

##### 第4）进行预发送后，然后提醒一下：

if  (  (  tmpiRet  =  q2PostMsgAndTrigger(  pMsg,  msgLen,  &pChannel->outputQ2  )  )  )  {

至此预发送toSend就 讲完了。

## 8. 发送线程mcThreadProc_mis_snd的运行机制

   extern "C" DWORD WINAPI mcThreadProc_mis_snd( LPVOID lpParameter ){...}

   上面的函数怎么实现发送的呢？发送需要从发送队列读取数据，

##### 第1）我们先从output里读取数据，

   if  (  q2GetMsg(  &pChannel->outputQ2,  pMsg,  &len  )  )  break;

##### 第2）找到客户端到服务器数据类型，数据加密，然后发送到服务器

```
case  CONST_misMsgType_req:

     qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (  ""  ),  
pProcInfo->who_showInfo,  0,  where_showInfo,  _T(  ""  ),  _T(  
"snd: %s,  tn %d"  ),  qyGetDesByType1(  CONST_qyCmdTable,  
pMsg->req.usCode  ),  pMsg->req.uiTranNo  );

if  (  encryptData(  &pChannel->commEncCtx,  &buf_help,  (  
char  *  )&pMsg->req.data,  &pMsg->req.lenInBytes,  
sizeof(  pMsg->req.data  )  )  )  {

qyShowInfo1(  CONST_qyShowType_warning,  0,  (  ""  ),  
pProcInfo->who_showInfo,  0,  where_showInfo,  _T(  ""  ),  _T(  "snd
 req: encryptData failed"  )  );

goto  errLabel;

}
```

## 9. 模型里还有哪些最重要的对象呢？

### 9.1 CCtxQyMc

也即QY_MC，这是整个应用的全局量。 

typedef class CCtxQyMc QY_MC; // 这个是一个整个应用的全局量； 

```
class CCtxQyMc: public CCtxQyMcTmpl { ... }; 
```

MC的意思：management console

### 9.2 CCtxQmc

//CCtxQmc，这个是一个整个应用的全局量，依附于上面第一个全局量CCtxQyMc；

```
class CCtxQmc:public CCtxQmcTmpl{ ... };
```

### 9.3 MIS_CNT // 服务器IP

```
struct __misCnt_t { ... };

typedef struct __misCnt_t MIS_CNT;
```

MIS_CNT，是服务中心，代表着一个服务器 (注释：最早时候，一个客户端能连多个服务器，实际上，一个程序里面只有一个mis_cnt,但是这个结构独立起来了；）

### 9.4 本地object对象，QY_SHARED_OBJ

启动视频会议，就会产生本地视频源，就需要用到QY_SHARED_OBJ这个概念了。
QY_SHARED_OBJ，定义为本地而不是远程产生的资源，可以是单audio，单video，或者av组合的，单屏幕共享。
理论上，一个obj可以同时为多个实时任务列表提供数据；
清扬的双流就是产生了2个object。
为什么叫本地产生呢？就意味着不是远程数据，而是本地数据；远程数据通过实时任务列表来管理。本地不在实时任务列表中，就通过关联，把数据灌入实时任务列表中去。
这个对象，用于模拟一个可共享的对象；
视频共享，就用这个对象模拟
音频视频共享，也是用这个对象模拟
QY_SHARED_OBJ中包含：
iIndex_capBmp：指向一个CAP_procInfo_video视频流结构，该结构内包含COMPRESS_VIDEO结构用来压缩视频。
iIndex_capAudio：指向一个CAP_procInfo_audioU音频流结构，该结构内包含COMPRESS_AUDIO结构用来压缩音频。
