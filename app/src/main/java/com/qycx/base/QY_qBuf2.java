package com.qycx.base;


import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.Consts.CONST_qyRet_qFull;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decV;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encV;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_playA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_postEncV;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_showInfo;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_snd;


import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

// not used
public class QY_qBuf2 {

    public static class Consts {

        //
        public final static int CONST_qType_encA  =  1;

        public final static int CONST_qType_decA = 5;
        public final static int CONST_qType_playA = 6;
        //
        //
        public final static int CONST_qType_encV  =  21;
        public final static int CONST_qType_postEncV  =  22;

        //
        public final static int CONST_qType_decV = 25;



        //
        public final static int CONST_qType_snd  =  50;


        //
        public final static int CONST_qType_showInfo = 100;


    };


    //
    public static class QY_qBuf2_cfg {
        public int m_qType;

        //
        public int uiMaxQNodes;

        //  为false时，采用一个buf. true是用双buf
        public boolean m_bUse2Buf;


        //
        public qySyncCommProc.RW_lock_param rwLockParam_syncFlg;
    };
    public QY_qBuf2_cfg cfg = new QY_qBuf2_cfg();
    public String name;

    //
    boolean bFlag_toPostMsg_q1 = false;
    public qySyncCommProc.CMutexRW pRw_syncFlg=new qySyncCommProc.CMutexRW();

    //
    public long dwTickCnt_lastData;

    //
    public boolean  m_bDbg  =  false;

    //
    public static class QY_qBuf {

        public static class Tmp_qBuf_cfg {
            public int m_qType;

            public int uiMaxQNodes;
            public int maxDataBufLen;        //

            //
            public boolean b_showInfoQ;
        }
        ;

        Tmp_qBuf_cfg cfg=new Tmp_qBuf_cfg();
        String name;

        public Tmp_qBuf_memHead[] memHeads;
        public byte[] pBuf;
        //
        public int startIndex_memHead;
        public int uiQNodes;
        public int startPos_data;
        public int dataLen;
        //
        public boolean bNeedSync;
        public qySyncObj.SYNC_OBJ_PARAM syncObjParam = new qySyncObj.SYNC_OBJ_PARAM();
        public Semaphore hMutex;
        public Queue<Object> queue;

        //
        public boolean m_bDbg;

        //
        public boolean isQFull() {
            QY_qBuf pQ = this;
            if (pQ == null) return false;
            if (pQ.cfg.uiMaxQNodes != 0 && pQ.uiQNodes >= pQ.cfg.uiMaxQNodes) return true;
            return false;
        }

        public boolean isQWarning(int uiQNodes_warningInterval) {
            QY_qBuf pQ = this;
            if (pQ == null) return false;
            if (pQ.cfg.uiMaxQNodes != 0 && pQ.uiQNodes + uiQNodes_warningInterval >= pQ.cfg.uiMaxQNodes)
                return true;
            return false;
        }
        
        //
        public boolean isQEmpty() {
            if (this.uiQNodes == 0) return true;
            return false;

        }

        //
        public int init(int qType, int uiMaxQNodes, int maxDataBufLen, boolean bAlloc, String name) {
            int iErr = -1;

            QY_qBuf bufInfo = this;
            QY_qBuf pQ = this;

            if (maxDataBufLen == 0) return -1;
            if (bufInfo.pBuf != null) return -1;

            //
            if (name == null) name = "";
            pQ.name = name;
            pQ.cfg.m_qType = qType;

            bufInfo.cfg.uiMaxQNodes = uiMaxQNodes;
            pQ.bNeedSync = true;

            //
            pQ.hMutex = new Semaphore(1);

            queue = new ArrayDeque<>(uiMaxQNodes);


            //
            if (qType == CONST_qType_showInfo) {
                pQ.cfg.b_showInfoQ = true;
            }

            //
            if (bAlloc) {
                if (0 != alloc(maxDataBufLen)) {
                        return iErr;
                }
            }


            iErr = 0;


            
            if (iErr != 0) {
                exit();
            }
            return iErr;
        }

        public void exit() {
            QY_qBuf qBuf = this;
            QY_qBuf pQ = this;

            //
            free();

            //
            if (pQ.hMutex != null) {
                if(pQ.hMutex.tryAcquire()){
                    pQ.hMutex.release();
                }
                pQ.hMutex = null;
            }
        }

        //
        public int alloc(int maxDataBufLen) {
            //Log.d(AUDIO_DOWNLOAD,"qBuf.alloc enter" + maxDataBufLen);
            //
            int iErr = -1;
            QY_qBuf pQ = this;
            QY_qBuf bufInfo = this;

            //
            bufInfo.cfg.maxDataBufLen = maxDataBufLen;

            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.alloc.185") != 0) {
                    Log.d(AUDIO_DOWNLOAD, "qBuf.alloc  182 failed" );
                    //qyShowInfo_internal(CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, "", ("qPostMsg failed: sync failed"));
                        return iErr;
                }

                //
                if (pQ.pBuf != null || pQ.memHeads != null) {
                    if(pQ.pBuf != null )    Log.d(AUDIO_DOWNLOAD, "qBuf.alloc 190 failed" );
                    if(pQ.memHeads != null) Log.d(AUDIO_DOWNLOAD, "qBuf.alloc 191 failed" );
                    return -2;
                }

                //
                bufInfo.memHeads = new Tmp_qBuf_memHead[bufInfo.cfg.uiMaxQNodes];

                int i;
                for (i = 0; i < bufInfo.cfg.uiMaxQNodes; i++) {
                    switch (bufInfo.cfg.m_qType) {
                        case  CONST_qType_encA:
                              bufInfo.memHeads[i]=new Tmp_qBuf_memHead_encA();
                            ((Tmp_qBuf_memHead_encA)bufInfo.memHeads[i]).dataMemHead=new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                              break;
                        case CONST_qType_decA:
                            bufInfo.memHeads[i] = new Tmp_qBuf_memHead_decA();
                            ((Tmp_qBuf_memHead_decA)bufInfo.memHeads[i]).dataMemHead=new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                            break;
                        case CONST_qType_playA:
                             bufInfo.memHeads[i] = new Tmp_qBuf_memHead_playA();
                             ((Tmp_qBuf_memHead_playA)bufInfo.memHeads[i]).headData=new HeadData_playA();
                             break;
                        case  CONST_qType_encV:
                            bufInfo.memHeads[i]=new Tmp_qBuf_memHead_encV();
                            ((Tmp_qBuf_memHead_encV)bufInfo.memHeads[i]).dataMemHead = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                            break;

                        case  CONST_qType_postEncV:
                            bufInfo.memHeads[i]=new Tmp_qBuf_memHead_encV();
                            ((Tmp_qBuf_memHead_encV)bufInfo.memHeads[i]).dataMemHead = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                            break;

                        case CONST_qType_decV:
                             bufInfo.memHeads[i] = new Tmp_qBuf_memHead_decV();
                            ((Tmp_qBuf_memHead_decV)bufInfo.memHeads[i]).dataMemHead=new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                             //  这里没做完
                             break;
                        case  CONST_qType_snd:
                              bufInfo.memHeads[i]=new Tmp_qBuf_memHead_snd();
                              ((Tmp_qBuf_memHead_snd)bufInfo.memHeads[i]).headData=new HeadData_snd();
                              break;

                              //
                        case CONST_qType_showInfo:
                            bufInfo.memHeads[i] = new Tmp_qBuf_memHead_showInfo();
                            ((Tmp_qBuf_memHead_showInfo)bufInfo.memHeads[i]).headData = new HeadData_showInfo();
                            break;

                        default:
                            Log.d(AUDIO_DOWNLOAD, "qBuf.alloc failed, type error");
                                return iErr;
                    }
                }

                bufInfo.pBuf = new byte[bufInfo.cfg.maxDataBufLen];
            } catch (Exception e) {
                Log.e(e.getMessage());
                Log.d(AUDIO_DOWNLOAD,"qBuf.alloc  220 failed" );
                return -1;
            }

            iErr = 0;
            
            if (0 != iErr) {
                free();
            }
            return iErr;
        }

        public void free() {
            QY_qBuf pQ = this;
            QY_qBuf qBuf = this;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.free") != 0) {
                    //qyShowInfo_internal(CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, "", ("qPostMsg failed: sync failed"));
                        return;
                }

                //
                qBuf.memHeads = null;
                if (qBuf.pBuf != null) {
//                    qyMemoryFunc.qyFuncs.myfree( qBuf.pBuf);
                    qBuf.pBuf = null;
                }


            } catch (Exception e) {
                Log.e(e.getMessage());
                return;
            }


            return;
        }

        public void emptyQ()
        {
            QY_qBuf pQ = this;
            QY_qBuf qBuf = this;

            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.emptyQ") != 0) {
                    //qyShowInfo_internal(CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, "", ("qPostMsg failed: sync failed"));
                    return;
                }

                //
                pQ.startIndex_memHead  =  0;
                pQ.uiQNodes  =  0;
                pQ.startPos_data  =  0;
                pQ.dataLen  =  0;

                //

            } catch (Exception e) {
                Log.e(e.getMessage());
                return;
            }

            //
            return;

        }

        //
        /*
        public int qPostMsg_unused(Object pQElem) {
            int iErr = -1;
            QY_qBuf pQ = this;
            String str;

            //
            if (pQElem == null) return -1;

            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.q") != 0) {
                    //qyShowInfo_internal(CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, "", ("qPostMsg failed: sync failed"));
                        return iErr;
                }

                if (pQ.queue.size() >= pQ.cfg.uiMaxQNodes) {
                    str = String.format(pQ.name + " qFull");
                    if (pQ.cfg.b_showInfoQ) {

                    } else {
                        Log.d( str);
                    }
                    //
                    iErr = CONST_qyRet_qFull;  return iErr;
                }

                pQ.queue.add(pQElem);

                //
//                if (pQ.dataLen + size > pQ.cfg.maxDataBufLen) {
//                    str = String.format(pQ.name + " qFull");
//                    if (pQ.cfg.b_showInfoQ) {
//
//                    } else {
//                        Log.d( str);
//                    }
//                    //
//                        return iErr;
//                }

                //
//                switch (pQ.cfg.m_qType) {
//                    case CONST_qType_showInfo: {
//                        Tmp_qBuf_memHead_showInfo head = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[pQ.uiQNodes];
//                        //
//                        head.headData.len_showInfo = (int) size;
//                        //
//
//                    }
//                    break;
//                    case CONST_qType_decA: {
//                        Tmp_qBuf_memHead_decA head = (Tmp_qBuf_memHead_decA) pQ.memHeads[pQ.uiQNodes];
//                        //
//                        head.dataMemHead = (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pHeadData;
//
//                    }
//                    break;
//                    case CONST_qType_playA: {
//                        Tmp_qBuf_memHead_playA head = (Tmp_qBuf_memHead_playA) pQ.memHeads[pQ.uiQNodes];
//                        //
//                        head.headData = (HeadData_playA) pHeadData;
//
//                    }
//                    break;
//                    case CONST_qType_decV: {
//                        Tmp_qBuf_memHead_decV head = (Tmp_qBuf_memHead_decV) pQ.memHeads[pQ.uiQNodes];
//                        //
//                        head.dataMemHead = (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead)pHeadData;
//                    }
//                    break;
//                    default:
//                        //Log.d("qPostMsg failed: unprocessed qType");
//                            return iErr;
//                }
//                pQ.uiQNodes++;
//                if (pQElem0 != null) {
//                    System.arraycopy(pQElem1, 0, pQ.pBuf , pQ.dataLen,size);
//                } else {
//                    System.arraycopy(pQElem1, 0, pQ.pBuf , pQ.dataLen, size);
//                }
//                pQ.dataLen += (int) size;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            iErr = 0;
            

            return iErr;
        }

         */


        //
        public int qPostMsg(Object pHeadData, byte[] pQElem0, byte[]pQElem1, int srcPos, int size,String hint) {
            int iErr = -1;
            QY_qBuf pQ = this;
            String str;

            //
            if (pHeadData == null) return -1;
            if (pQElem0 == null && pQElem1 == null) return -1;
            if ( size == 0) return -1;
            //

            //
            //Log.d("tn_msgInput","qBuf.qPostMsg enters "  + hint);

            //
            long tick_beforeSync  =  tmpFunc.qyFuncs.myGetTickCount(null);


            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {
                //
                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.qPostMsg.387") != 0) {
                    //qyShowInfo_internal(CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, "", ("qPostMsg failed: sync failed"));

                    //
                    Log.d("tn_msgInput","qBuf "  + this.name  +  " qPostMsg: sync failed "  + hint);

                    //
                    return iErr;
                }

                //
                //Log.d("tn_msgInput","qBuf "  +  this.name  +  " qPostMsg: after sync "  + hint);


                //
                if (pQ.pBuf == null)
                {
                    if (!pQ.cfg.b_showInfoQ)
                    {
                        str ="qbuf.qPostMsg failed, pBuf is null. qName "  +  pQ.name;
                        Log.e(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    }
                    return iErr;
                }

                if (pQ.uiQNodes >= pQ.cfg.uiMaxQNodes) {
                    str = String.format(pQ.name + " qFull 499 "+ Thread.currentThread().getName());
                    if (!pQ.cfg.b_showInfoQ) {
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    } else {
                        Log.d( str);
                    }
                    iErr = CONST_qyRet_qFull;
                    return iErr;
                }

                //
                if  (  pQ.startPos_data!=0)  {
                    chkQBuf_noSync("qPostMsg.503");
                    //Log.d("chkQBuf","kkk");
                }


                //
                if  (  pQ.startPos_data  +  pQ.dataLen  +  size  >  pQ.cfg.maxDataBufLen  )  {
                    if  (  pQ.startPos_data  !=0) {
                        System.arraycopy(pQ.pBuf, pQ.startPos_data, pQ.pBuf, 0, pQ.dataLen);
                        pQ.startPos_data = 0;
                    }
                }
                if (pQ.startPos_data  +  pQ.dataLen + size > pQ.cfg.maxDataBufLen) {
                    str = String.format(pQ.name + " qFull 524 " + Thread.currentThread().getName() );
                    if (!pQ.cfg.b_showInfoQ) {
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    } else {
                        Log.d( str);
                    }
                    return iErr;
                }
                int headIndex  =  0;
                int i;
                if  (  pQ.startIndex_memHead  +  pQ.uiQNodes  >=  pQ.memHeads.length  )  {
                    if  (  pQ.startIndex_memHead!=0)  {
                        for  (  i  =  0;  i  <  pQ.uiQNodes;i++  )  {
                            switch (pQ.cfg.m_qType)  {
                                case  CONST_qType_encA: {
                                    Tmp_qBuf_memHead_encA head_i = (Tmp_qBuf_memHead_encA) pQ.memHeads[pQ.startIndex_memHead + i];
                                    Tmp_qBuf_memHead_encA head_o = (Tmp_qBuf_memHead_encA) pQ.memHeads[i];
                                    head_o.dataMemHead.copyFrom(head_i.dataMemHead);
                                }
                                break;

                                case CONST_qType_decA: {
                                    Tmp_qBuf_memHead_decA head_i = (Tmp_qBuf_memHead_decA) pQ.memHeads[pQ.startIndex_memHead  +  i];
                                    Tmp_qBuf_memHead_decA head_o = (Tmp_qBuf_memHead_decA) pQ.memHeads[i];
                                    head_o.dataMemHead.copyFrom(head_i.dataMemHead);
                                }
                                break;
                                case CONST_qType_playA: {
                                    Tmp_qBuf_memHead_playA head_i = (Tmp_qBuf_memHead_playA) pQ.memHeads[pQ.startIndex_memHead+i];
                                    Tmp_qBuf_memHead_playA head_o = (Tmp_qBuf_memHead_playA) pQ.memHeads[i];
                                    head_o.headData.copyFrom(head_i.headData);
                                }
                                break;
                                case  CONST_qType_encV:
                                case  CONST_qType_postEncV:
                                {
                                    Tmp_qBuf_memHead_encV head_i  =(Tmp_qBuf_memHead_encV) pQ.memHeads[pQ.startIndex_memHead+i];
                                    Tmp_qBuf_memHead_encV head_o  =(Tmp_qBuf_memHead_encV) pQ.memHeads[i];
                                    head_o.dataMemHead.copyFrom(head_i.dataMemHead);
                                }
                                break;
                                case CONST_qType_decV: {
                                    Tmp_qBuf_memHead_decV head_i = (Tmp_qBuf_memHead_decV) pQ.memHeads[pQ.startIndex_memHead+i];
                                    Tmp_qBuf_memHead_decV head_o = (Tmp_qBuf_memHead_decV) pQ.memHeads[i];
                                    head_o.dataMemHead.copyFrom(head_i.dataMemHead);
                                    //
                                    //Log.e("");
                                }
                                break;
                                case  CONST_qType_snd:  {
                                    Tmp_qBuf_memHead_snd head_i = (Tmp_qBuf_memHead_snd) pQ.memHeads[pQ.startIndex_memHead+i];
                                    Tmp_qBuf_memHead_snd head_o = (Tmp_qBuf_memHead_snd) pQ.memHeads[i];
                                    head_o.headData.copyFrom(head_i.headData);
                                }
                                break;


                                case  CONST_qType_showInfo:  {
                                    Tmp_qBuf_memHead_showInfo head_i = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[pQ.startIndex_memHead  +  i];
                                    Tmp_qBuf_memHead_showInfo head_o = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[i];
                                    head_o.headData.copyFrom(head_i.headData);
                                }
                                break;
                                default:  {
                                         Log.e("qy","qBuf.qPostMsg failed, qType err");
                                }
                                        break;
                            }
                        }
                        //
                        pQ.startIndex_memHead=0;

                    }
                }
                if  (  pQ.startIndex_memHead  +  pQ.uiQNodes  >=  pQ.memHeads.length  )  {
                    str = String.format(pQ.name + " qFull, startIndex_memHead + uiQNodes  >= mems.length");
                    if (!pQ.cfg.b_showInfoQ) {
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    } else {
                        Log.d( str);
                    }
                    return  -1;

                }

                //
                headIndex  =  pQ.startIndex_memHead  +  pQ.uiQNodes;
                switch (pQ.cfg.m_qType) {
                    case  CONST_qType_encA: {
                          qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head_i  =  (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead)pHeadData;
                          Tmp_qBuf_memHead_encA head = (Tmp_qBuf_memHead_encA) pQ.memHeads[headIndex];
                          head.dataMemHead.copyFrom(head_i);
                    }
                    break;
                    case CONST_qType_decA: {
                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head_i  =  (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pHeadData;
                        Tmp_qBuf_memHead_decA head = (Tmp_qBuf_memHead_decA) pQ.memHeads[headIndex];
                        head.dataMemHead.copyFrom(head_i);
                    }
                    break;
                    case CONST_qType_playA: {
                         HeadData_playA head_i  =  (HeadData_playA)pHeadData;
                        Tmp_qBuf_memHead_playA head = (Tmp_qBuf_memHead_playA) pQ.memHeads[headIndex];
                        head.headData.copyFrom(head_i);
                    }
                    break;
                    case  CONST_qType_encV:
                    case  CONST_qType_postEncV:
                    {
                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead head_i = (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead)pHeadData;
                          Tmp_qBuf_memHead_encV head  =(Tmp_qBuf_memHead_encV) pQ.memHeads[headIndex];
                          head.dataMemHead.copyFrom(head_i);
                    }
                    break;
                    case CONST_qType_decV: {
                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead head_i=(qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead)pHeadData;
                        Tmp_qBuf_memHead_decV head = (Tmp_qBuf_memHead_decV) pQ.memHeads[headIndex];
                        head.dataMemHead.copyFrom(head_i);// = (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead)pHeadData;
                        //
                        //Log.e("");
                    }
                    break;
                    case  CONST_qType_snd:  {
                          HeadData_snd head_i  =  (  HeadData_snd)pHeadData;
                          Tmp_qBuf_memHead_snd head = (Tmp_qBuf_memHead_snd) pQ.memHeads[headIndex];
                          head.headData.copyFrom(head_i);
                         }
                    break;

                    //
                    case CONST_qType_showInfo: {
                        HeadData_showInfo head_i  =  ( HeadData_showInfo)pHeadData;
                        Tmp_qBuf_memHead_showInfo head = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[headIndex];
                        head.headData.len_showInfo = (int) size;
                        head.headData.tick_post  =  head_i.tick_post;
                    }
                    break;

                    default:
                        Log.e("qPostMsg failed: unprocessed qType");
                        return iErr;
                }
                //
                if  (  pQ.startPos_data  !=  0  )  {
                    //Log.d("qy","mmmm");
                }
                //

                //
                pQ.uiQNodes++;

                //
                if (pQElem0 != null) {
                    //
                    if  (  m_bDbg  )  {
                        //Log.e("qy",this.name  + " qBuf.qPostMsg: pQElem0: dataLen  "  + pQ.dataLen  + " size "  + size);

                    }
                    //
                    System.arraycopy(pQElem0, srcPos, pQ.pBuf , pQ.startPos_data  +  pQ.dataLen,size);
                } else {
                    //
                    if  (  m_bDbg)  {
                        //Log.e("qy",this.name  +  " qBuf.qPostMsg: pQElem1: dataLen  "  + pQ.dataLen  + " size "  + size);
                    }
                    //
                    System.arraycopy(pQElem1, srcPos, pQ.pBuf , pQ.startPos_data  +  pQ.dataLen, size);
                }
                //
                pQ.dataLen += (int) size;
                //
                if  (  m_bDbg)  {
                    if  (  (  pQ.dataLen  %  1152  )  !=  0  )  {
                        //Log.d("qy","pQ.dataLen error: kk.489");
                    }
                }

                //
                //
                chkQBuf_noSync("qPostMsg");

                //
            } catch (Exception e) {
                Log.e("qy",this.name  +  " qBuf.qPostMsg exception: "  + e.getMessage());
                return -1;
            }

            //
            if  (  false  ) {
                long tick_afterSync = tmpFunc.qyFuncs.myGetTickCount(null);
                Log.d("tn_msgInput",  "qBuf.qPostMsg, sync costs "  +  (  tick_afterSync  -  tick_beforeSync  )  +  "ms "  +  hint   );
            }


            //
            iErr = 0;


            return iErr;
        }

        //  返回数据长度
        int tmp_qGetMsg(Object pHeadData, byte[] pQElem, int dstPos,  int puiSize, boolean bNotShowInfo) {
            int iErr = -1;
            QY_qBuf pQ = this;

            if (pQ.uiQNodes == 0) {
                //Log.d("qy","tmp_qGetMsg failed, pQ.uiQNodes == 0");
                return -1;
            }
            if (pQ.uiQNodes + pQ.startIndex_memHead > pQ.cfg.uiMaxQNodes) {
                //Log.d("qBuf.tmp_qGetMsg failed, err");
                return -1;
            }

            int dataLen = 0;
            switch (pQ.cfg.m_qType) {
                case  CONST_qType_encA: {
                      Tmp_qBuf_memHead_encA head = (Tmp_qBuf_memHead_encA) pQ.memHeads[pQ.startIndex_memHead];
                      dataLen=(int)head.dataMemHead.uiLen;
                      qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead memHead=(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead)pHeadData;
                      memHead.copyFrom(head.dataMemHead);
                }
                break;
                case CONST_qType_decA: {
                    Tmp_qBuf_memHead_decA head = (Tmp_qBuf_memHead_decA) pQ.memHeads[pQ.startIndex_memHead];
                    dataLen = (int) head.dataMemHead.uiLen;
                    //pHeadData = head.dataMemHead;
                    qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead memHead  =  (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead)pHeadData;
                    memHead.copyFrom(head.dataMemHead);
                }
                break;
                case CONST_qType_playA: { //todo need to add
                    Tmp_qBuf_memHead_playA head = (Tmp_qBuf_memHead_playA) pQ.memHeads[pQ.startIndex_memHead];
                    dataLen = (int) head.headData.uiSize_dec;
                    //
                    //pHeadData = head.headData;
                    HeadData_playA memHead = ( HeadData_playA)pHeadData;
                    memHead.copyFrom(head.headData);
                }
                break;
                case  CONST_qType_encV:
                case  CONST_qType_postEncV:{
                      Tmp_qBuf_memHead_encV head = (Tmp_qBuf_memHead_encV) pQ.memHeads[pQ.startIndex_memHead];
                      dataLen=(int)head.dataMemHead.uiLen;
                      //
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead memHead  = (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead) pHeadData;
                    memHead.copyFrom(head.dataMemHead);
                }
                break;

                case CONST_qType_decV: {
                    Tmp_qBuf_memHead_decV head = (Tmp_qBuf_memHead_decV) pQ.memHeads[pQ.startIndex_memHead];
                    dataLen = (int) head.dataMemHead.uiLen;
                    //pHeadData = head.dataMemHead;
                    //
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead memHead  =  (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead)pHeadData;
                    memHead.copyFrom(head.dataMemHead);

                    //
                    //Log.e("tn_msgInput","qBuf2.tmp_qGetMsg");
                }
                break;
                case  CONST_qType_snd:  {
                      Tmp_qBuf_memHead_snd head  =(Tmp_qBuf_memHead_snd) pQ.memHeads[pQ.startIndex_memHead];
                      dataLen  =  head.headData.dataLen;
                      //
                      HeadData_snd memHead  =  (  HeadData_snd) pHeadData;
                      memHead.copyFrom(head.headData);
                      //

                }
                          break;
                case CONST_qType_showInfo: {
                    Tmp_qBuf_memHead_showInfo head = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[pQ.startIndex_memHead];
                    dataLen = head.headData.len_showInfo;
                    //
                    HeadData_showInfo memHead  =  ( HeadData_showInfo) pHeadData;
                    memHead.len_showInfo  = head.headData.len_showInfo;
                    memHead.tick_post  =  head.headData.tick_post;
                }
                break;

                default:
                        //
                        Log.d("qy", "qBuf.tmp_qGetMsg failed: unprocessed qType");
                        return iErr;
            }
            if (dataLen > puiSize) {
                String str = "qBuf.tmp_qGetMsg failed: " + pQ.name + " dataLen " + dataLen + " > puiSize " + puiSize;
                Log.e("qy",str );
                showInfo_open.qyFuncs.showInfo_open(str);
                return iErr;
            }
            if  (  false  ) {
                if (!pQ.cfg.b_showInfoQ) {
                    String str = "qBuf.tmp_qGetMsg failed: " + pQ.name + " dataLen " + dataLen + " > puiSize " + puiSize;
                    Log.e("qy", str);
                    showInfo_open.qyFuncs.showInfo_open(str);
                }
            }

            //
            if (dataLen + pQ.startPos_data > pQ.cfg.maxDataBufLen) {
                Log.e("qy","qBuf.tmp_qGetMsg failed, dataLen + pQ.startPos_data > pQ.cfg.maxDataBufLen");
                    return iErr;
            }

            //
            if  (  m_bDbg  )  {
                if  (  dataLen  ==  0)  {
                    Log.e("qy","tmp_qGetMsg failed: dataLen is 0");
                }
                else {
                    if ((pQ.dataLen % dataLen) != 0) {
                        //Log.d("qy", "error.574");
                    }
                }
            }


            //
            System.arraycopy(pQ.pBuf, pQ.startPos_data,pQElem,dstPos,dataLen);
            puiSize = (int) dataLen;
            pQ.startIndex_memHead++;
            pQ.startPos_data += dataLen;
            pQ.dataLen -= dataLen;
            pQ.uiQNodes--;

            //
            if  (  m_bDbg)  {
                //Log.d("qy",this.name  +  " qBuf.tmp_qGetMsg: dataLen "  +  dataLen  +  " pQ.dataLen "  +  pQ.dataLen);
                //
                if  (  (  pQ.dataLen  %  dataLen  )  !=  0  ) {
                    //Log.d("qy", "pQ.dataLen error.600");
                }

            }

            //
            if (pQ.uiQNodes == 0) {
                if (pQ.dataLen != 0) {
                    int ii = 0;
                    Log.e("qy","qBuf "  +  pQ.name  +  " tmp_qGetMsg serious err: pQ.dataLen is not 0");
                }
                //
                pQ.startIndex_memHead = 0;
                pQ.startPos_data = 0;
            }

            //
            chkQBuf_noSync("qGetMsg");

            //
            return dataLen;

        }

        //
        public int chkQBuf_noSync(String hint )
        {
            int i;
            QY_qBuf pQ = this;
            int dataLen  =  0;

            if  (  false  ) {
                switch (cfg.m_qType) {
                    case CONST_qType_showInfo:
                        for (i = 0; i < pQ.uiQNodes; i++) {
                            Tmp_qBuf_memHead_showInfo head = (Tmp_qBuf_memHead_showInfo) pQ.memHeads[pQ.startIndex_memHead + i];
                            dataLen += head.headData.len_showInfo;
                        }
                        //
                        String str1;
                        str1 = "qNodes " + pQ.uiQNodes + " startIndex " + pQ.startIndex_memHead + " startPos " + pQ.startPos_data + " dataLen " + pQ.dataLen;
                        if (dataLen != pQ.dataLen) {
                            Log.e("chkQBuf", "Err: qBuf dataLen not correct " + hint + ":" + str1);
                        } else {

                            Log.d("chkQBuf", "chkQBuf: dataLen ok. " + hint + ":" + str1);

                        }
                        //
                        break;
                    default:
                        break;
                }
            }

            return  0;
        }


        //
        public int qGetMsg(Object pHeadData, byte[] pQElem, int dstPos,  int puiSize) {
            QY_qBuf pQ = this;

            if (pQ == null) return -1;
            if (null == pQElem || 0 == puiSize) return -1;

            //
            if  (  false  ) {
                Log.d("tn_msgInput", "qBuf.qGetMsg");
            }


            //
            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                if (pQ.bNeedSync && syncObj.sync(pQ.syncObjParam.mutexName, pQ.hMutex,"qBuf.qGetMsg.576") != 0) return -1;

                if (pQ.pBuf == null) {
                    if (!pQ.cfg.b_showInfoQ) {
                        String str = "qBuf.getMsg failed, pBuf is null";
                        Log.e( str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    }
                    syncObj.Dispose();
                    return -1;
                }

                int i = tmp_qGetMsg(pHeadData, pQElem, dstPos,  puiSize, false);
                syncObj.Dispose();
                //
                //
                return i;
            } catch (Exception e) {
                Log.e("qy", "qGetMsg exception:"  +  e.getMessage());
                return -1;
            }
        }


    };

    //
    QY_qBuf qs0, qs1;
    public int size(){
        if  ( cfg.m_bUse2Buf) {
            if  (  qs0!=null  &&  qs1!=null  ) {
                return qs0.uiQNodes + qs1.uiQNodes;
            }
            //
            return  0;
        }
        else  {
              if  (  qs0!=null) {
                  return qs0.uiQNodes;
              }
              //
              return  0;
        }
    }

    //
    public EventWaitHandle hEvent0;


    //
    public static class Tmp_qBuf_memHead {
    };


    //
    public static class Tmp_qBuf_memHead_encA extends Tmp_qBuf_memHead {
        public qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead dataMemHead;
    };

    //
    public static class Tmp_qBuf_memHead_decA extends Tmp_qBuf_memHead {
        public qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead dataMemHead;
    };




    public static class HeadData_playA {
        public int uiPts;         //  2017/10/13
        public int uiSize_dec;     //  2017/10/13


        /*
        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
            buffer.putInt(uiPts);
            buffer.putInt(uiSize_dec);
            return buffer.array();
        }

         */

        public void memset0()
        {
            uiPts=0;
            uiSize_dec=0;
        }

        public int copyFrom(HeadData_playA head_i)
        {
            if  (  head_i  ==  null )  {
                return  -1;
            }
            //
            uiPts  =  head_i.uiPts;
            uiSize_dec  =  head_i.uiSize_dec;
            //
            return  0;
        }

    };

    //
    public static class Tmp_qBuf_memHead_playA extends Tmp_qBuf_memHead {
        public HeadData_playA headData;
    };

    //
    public static class Tmp_qBuf_memHead_encV extends  Tmp_qBuf_memHead {
        public qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead dataMemHead;
    };



    public static class Tmp_qBuf_memHead_decV extends Tmp_qBuf_memHead {
        public qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead dataMemHead;
    };



    //
    public static class HeadData_snd  {
        public int msg_uiType;      // msg

        //
        public int dataLen;

        //
        public byte ucFlg;
        //
        public short usCode;
        //public short usCode_org;
        public long tStartTime;
        public int uiTranNo;
        public short usSeqNo;

        //
        public long dwTickCnt_start;


        //
        public void memset0()
        {
            msg_uiType=0;

            //
            dataLen=0;
        }

        public int copyFrom(HeadData_snd head_i)
        {
            msg_uiType= head_i.msg_uiType;;

            //
            dataLen=head_i.dataLen;

            //]
            ucFlg  = head_i.ucFlg;
            //
            usCode  =  head_i.usCode;
            //public short usCode_org;
            tStartTime  = head_i.tStartTime;
            uiTranNo  = head_i.uiTranNo;
            usSeqNo  = head_i.usSeqNo;

            //
            dwTickCnt_start=head_i.dwTickCnt_start;


            //
            //Log.e("qy","这里未完成.745");


            return  0;
        }

    }

    public static class Tmp_qBuf_memHead_snd extends  Tmp_qBuf_memHead {
        public HeadData_snd headData;


    };



    //
    public static class HeadData_showInfo {
        public int len_showInfo;
        public  long tick_post;     //  showInfo的时刻

        //
        public static int s_length = Integer.BYTES  +  Long.BYTES;

        //
        public void memset0()
        {
            len_showInfo=0;
            tick_post=0;
        }

        //
        public int copyFrom(HeadData_showInfo head_i)
        {
            if  (  head_i==null) return  -1;

            len_showInfo=head_i.len_showInfo;
            tick_post=head_i.tick_post;
            return  0;
        }

    }

    //
    public static class Tmp_qBuf_memHead_showInfo extends Tmp_qBuf_memHead {
        public HeadData_showInfo headData;
    };
    
    public int initQyQ2(int qType, int uiMaxQNodes, int maxDataBufLen, boolean bUse2Buf,  qySyncCommProc.RW_lock_param pRwLockParam_syncFlg, boolean bAlloc, String name) {
        int iErr = -1;

        QY_qBuf2 pQ2 = this;

        //
        if (0 == uiMaxQNodes) return -1;

        //
        this.cfg.m_qType = qType;
        this.cfg.uiMaxQNodes = uiMaxQNodes;
        //
        this.cfg.m_bUse2Buf=bUse2Buf;
        if  (  bUse2Buf)  {
            Log.e("qy","err: bUse2Buf is true");
        }
        //
        this.cfg.rwLockParam_syncFlg = pRwLockParam_syncFlg;

        if (name == null) name = "";
        this.name = name;
        //
        this.pRw_syncFlg = new qySyncCommProc.CMutexRW();
        if (null == this.pRw_syncFlg) return iErr;

        //
        qs0 = new QY_qBuf();
        if  (  cfg.m_bUse2Buf) {
            qs1 = new QY_qBuf();
        }

        //
        if  (  m_bDbg  )  {
            Log.d("qy","haha");
        }


        //
        String str;
        str = name + "-0";
        if (qs0.init(qType, uiMaxQNodes, maxDataBufLen, bAlloc, str) != 0) return iErr;
        if  (  cfg.m_bUse2Buf) {
            str = name + "-1";
            if (qs1.init(qType, uiMaxQNodes, maxDataBufLen, bAlloc, str) != 0) return iErr;
        }

        //
        if  (  m_bDbg)  {
            Log.d("qy","haha...");
        }

        //
        pQ2.hEvent0 = new EventWaitHandle(false, EventResetMode.AutoReset);
        if (pQ2.hEvent0 == null) return iErr;
        
        iErr = 0;

        
        if (iErr != 0) {
            exitQyQ2();
        }
        return iErr;
    }

    public void exitQyQ2() {
        QY_qBuf2 pQ2 = this;

        if (qs0 != null) {
            qs0.exit();
            qs0 = null;
        }
        if (qs1 != null) {
            qs1.exit();
            qs1 = null;
        }

        if (pQ2.hEvent0 != null) {
            //CloseHandle(pQ2->hEvents[j]); pQ2->hEvents[j] = NULL;
            pQ2.hEvent0.Close();
            pQ2.hEvent0 = null;

        }
        
        if (this.pRw_syncFlg != null) {
            this.pRw_syncFlg = null;
        }

        return;
    }

    //
    public int alloc(int maxDataBufLen) {
        Log.d(AUDIO_DOWNLOAD,"qBuf2 alloc enter maxDataBufLen " + maxDataBufLen);
        int iErr = -1;
        int result = -1;

        if (0 != (result = this.qs0.alloc(maxDataBufLen))) return result;
        if  (  cfg.m_bUse2Buf) {
            if (0 != (result = this.qs1.alloc(maxDataBufLen))) return result;
        }

        iErr = 0;
        
        if (0 != iErr) {
            free();
        }
        return iErr;
    }

    public void free() {
        this.qs0.free();
        if  (cfg.m_bUse2Buf) {
            this.qs1.free();
        }
    }

    //
    public boolean isQ2Warning(int uiQNodes_warningInterval) {
        QY_qBuf2 pQ2 = this;

        if (null == pQ2) return false;

        if  (cfg.m_bUse2Buf) {

            if (pQ2.bFlag_toPostMsg_q1) return pQ2.qs1.isQWarning(uiQNodes_warningInterval);
            else return pQ2.qs0.isQWarning(uiQNodes_warningInterval);
        }
        else  {
              return pQ2.qs0.isQWarning(uiQNodes_warningInterval);
        }
    }

    public boolean isQ2Empty() {
        QY_qBuf2 pQ2 = this;

        if (null == pQ2) return false;

        if  (cfg.m_bUse2Buf) {

            if (  pQ2.qs0==null||pQ2.qs1==null)return false;

            return (pQ2.qs0.isQEmpty() && pQ2.qs1.isQEmpty());
        }
        else  {
            if  (  pQ2.qs0==null) return false;
            //
            return (pQ2.qs0.isQEmpty() );

        }
    }

    //
    public int q2PostMsg(Object pHeadData, byte[] pQElem, int srcPos,  int size,String hint) {
        int iErr = -1;

        QY_qBuf2 pQ2 = this;

        //
        pQ2.dwTickCnt_lastData = tmpFunc.qyFuncs.myGetTickCount(null);


        try// (CQyQ2Help q2Help = new CQyQ2Help())
        {
            //
            //
            QY_qBuf pQ_toPostMsg  =  pQ2.qs0;//q2Help.getQ_toPostMsg(pQ2,hint);
            if  (  pQ_toPostMsg== null) {
                return -1;
            }
            return pQ_toPostMsg.qPostMsg(pHeadData,null,pQElem,srcPos,  size,hint);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  -1;
    }






    public int q2PostMsgAndTrigger(Object pHeadData, byte[] pQElem, int srcPos, int size,String hint) {
        int  iErr  =  -1;

        QY_qBuf2 pQ2 = this;
        //
        pQ2.dwTickCnt_lastData = tmpFunc.qyFuncs.myGetTickCount(null);

        do {

            try
            //(QY_qBuf2.CQyQ2Help q2Help = new QY_qBuf2.CQyQ2Help())
            {
                //return   q2Help.qPostMsgAndTrigger(pHeadData,pQElem,srcPos,  size,pQ2,hint);

                //

                int tmpiRet;

                QY_qBuf q_toPostMsg = pQ2.qs0;

                tmpiRet = q_toPostMsg.qPostMsg(pHeadData, pQElem, null, srcPos, size, hint);
                if (tmpiRet != 0) {
                    //
                    if (pHeadData != null) {
                        String str;
                        str = "qBuf2.q2PostMsgAndTrigger: q.name " + q_toPostMsg.name + " qPostMsg returns " + tmpiRet;
                        Log.e("qy", str);
                        showInfo_open.qyFuncs.showInfo_open(str);

                    }
                }


                //
                if (pQ2.hEvent0 != null) {
                    //SetEvent(pQ2->hEvents[0]);
                    tmpFunc.qyFuncs.myEventSet(pQ2.hEvent0);
                }


            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            iErr  =  0;


        } while (false);


        //
        if  (  true  )  {
            //Log.d("tn_msgInput",  "qBuf2.q2PostMsgAndTrigger().818, out of q2Help."  );
        }
        return iErr;
    }

    public int q2GetMsg(Object pHeadData, byte[] pQElem, int dstPos,  int puiSize) {
        QY_qBuf2 pQ2 = this;

        //
        //Log.d("tn_msgInput","qBuf2.q2GetMsg 1");

        //
        try //(CQyQ2Help q2Help = new CQyQ2Help())
        {
            QY_qBuf q_toGetMsg  =  pQ2.qs0;

            if (q_toGetMsg == null) {
                Log.d("tn_msgInput","qBuf2.q2GetMsg: q2Help.getQ_toGetMsg failed, returns null");
                return -1;
            }

            return q_toGetMsg.qGetMsg(pHeadData, pQElem, dstPos,  puiSize);
        }
        catch ( Exception e)
        {
            e.printStackTrace();
        }
        return  -1;
    }

    public int getQ2Nodes() {
        QY_qBuf2 pQ2 = this;

        if (null == (pQ2)) return -1;
        if  (cfg.m_bUse2Buf) {
            if (null == pQ2.qs0 || null == pQ2.qs1) return -1;
            return (int) ((pQ2).qs0.uiQNodes + (pQ2).qs1.uiQNodes);
        }
        else  {
            if (null == pQ2.qs0 ) return -1;
            return (int) ((pQ2).qs0.uiQNodes );

        }
    }

    //
    public int getQ2Nodes_toPostMsg() {
        QY_qBuf2 pQ2 = this;
        if (null == (pQ2)) return -1;
        if  (cfg.m_bUse2Buf) {
            return (int) ((pQ2).bFlag_toPostMsg_q1 ? (pQ2).qs1.uiQNodes : (pQ2).qs0.uiQNodes);
        }
        else  {
              return pQ2.qs0.uiQNodes;
        }
    }

    //
    public void emptyQ2(){

        this.qs0.emptyQ();
        if  (  this.qs1!=null) {
            this.qs1.emptyQ();
        }

    }



    //
    /*
    public static class CQyQ2Help_unused extends qySyncCommProc.CQyRWLock {


        public QY_qBuf2 m_pQ2;
        //
        public QY_qBuf m_pQ_toGetMsg;
        public QY_qBuf m_pQ_toPostMsg;

        public CQyQ2Help_unused()
        {
            m_pQ2 = null;
        }
        
        public QY_qBuf getQ_toPostMsg (QY_qBuf2 pQ2,String hint)
        {


            //
            if (null == pQ2) return null;
            if (m_pQ2 != null) {
                if (m_pQ2 != pQ2) return null;
            } else
                m_pQ2 = pQ2;

            //
            if  (!pQ2.cfg.m_bUse2Buf )  {
                return  pQ2.qs0;
            }

            //
            if (m_pQ_toPostMsg != null) return m_pQ_toPostMsg;

            //
            this.m_bDbg  =  pQ2.m_bDbg;

            //
            if (this.rLock(pQ2.pRw_syncFlg, (int) pQ2.cfg.rwLockParam_syncFlg.uiMilliSeconds_sema_r,hint) != 0)
                return null;

            m_pQ_toPostMsg = pQ2.bFlag_toPostMsg_q1 ? pQ2.qs1 : pQ2.qs0;
            m_pQ_toGetMsg = pQ2.bFlag_toPostMsg_q1 ? pQ2.qs0 : pQ2.qs1;

            return m_pQ_toPostMsg;
        }
        public QY_qBuf getQ_toGetMsg (QY_qBuf2 pQ2)
        {
            if (null == pQ2) {
                return null;
            }

            if (m_pQ2 != null) {
                if (m_pQ2 != pQ2) {
                    return null;
                }
            } else
                m_pQ2 = pQ2;

            //
            if  (  !pQ2.cfg.m_bUse2Buf)  {
                return pQ2.qs0;
            }

            //
            if (m_pQ_toGetMsg != null) return m_pQ_toGetMsg;

            QY_qBuf pQ_toGetMsg;
            QY_qBuf pQ_toPostMsg;
            if (pQ2.bFlag_toPostMsg_q1) {
                pQ_toGetMsg = pQ2.qs0;
                pQ_toPostMsg = pQ2.qs1;
            } else {
                pQ_toGetMsg = pQ2.qs1;
                pQ_toPostMsg = pQ2.qs0;
            }

            //
            //Log.d("tn_msgInput","q2Help.getQ_toGetMsg 1");

            //
            if ((pQ_toGetMsg.isQEmpty())) {
                if (!(pQ_toPostMsg.isQEmpty())) {
                    //
                    String hint = "qBuf2.qyq2Help.getQ_toGetMsg";

                    //
                    //Log.d("tn_msgInput","q2Help.getQ_toGetMsg: before wLock"  +  " "  +  hint  );


                    //  try to switch outputQ
                    if (this.wLock(pQ2.pRw_syncFlg, (int) pQ2.cfg.rwLockParam_syncFlg.uiMilliSeconds_sema_w, hint) != 0) {
                        //
                        Log.d("tn_msgInput","q2Help.getQ_toGetMsg: wLock failed");
                        //
                        return null;
                    }

                    //
                    //Log.d("tn_msgInput","q2Help.getQ_toGetMsg: wLock ok"  +  " "  +  hint  );

                    //
                    pQ2.bFlag_toPostMsg_q1 = !pQ2.bFlag_toPostMsg_q1;
                    this.unlock();
                }
            }


            //
            if (pQ2.bFlag_toPostMsg_q1) {
                m_pQ_toGetMsg = pQ2.qs0;
                m_pQ_toPostMsg = pQ2.qs1;
            } else {
                m_pQ_toGetMsg = pQ2.qs1;
                m_pQ_toPostMsg = pQ2.qs0;
            }

            return m_pQ_toGetMsg;

        }



        public int qPostMsgAndTrigger (Object pHeadData,byte[] pQElem, int srcPos,  int size, QY_qBuf2 pQ2,String hint)
        {
            int  iErr =  -1;
            int tmpiRet = 0;
            boolean ucbTriggerErr = false;

            if (pQ2 == null) return -1;

            //
            long tick_beforePostMsg  =  tmpFunc.qyFuncs.myGetTickCount(null);
            long tick_beforePostMsg1  =  0;

            do {

                //
                if (pQElem != null) {
                    QY_qBuf q_toPostMsg = getQ_toPostMsg(pQ2, hint);


                    //
                    if (q_toPostMsg != null) {

                        //
                        if (false) {
                            tick_beforePostMsg1 = tmpFunc.qyFuncs.myGetTickCount(null);
                            Log.d("tn_msgInput", "before q_toPostMsg.qPostMsg " + hint);
                        }

                        //
                        tmpiRet = q_toPostMsg.qPostMsg(pHeadData, pQElem, null, srcPos, size, hint);
                        if (tmpiRet != 0) {
                            //
                            String str;
                            str  =  "qyQ2Help::qPostMsgAndTrigger: q.name "  +  q_toPostMsg.name  +  " qPostMsg returns "  + tmpiRet;
                            Log.e("qy",str);
                            showInfo_open.qyFuncs.showInfo_open(str);
                            //
                            break;
                        }


                        //
                        if (false) {
                            long tick_afterPostMsg = tmpFunc.qyFuncs.myGetTickCount(null);
                            if (tick_afterPostMsg - tick_beforePostMsg1 > 1000) {
                                Log.d("tn_msgInput", "");
                            }
                            Log.d("tn_msgInput", "qBuf2.q2Help.qPostMsgAndTrigger.992" + " getQ_toPostMsg costs " + (tick_beforePostMsg1 - tick_beforePostMsg) + "ms qPostMsg costs " + (tick_afterPostMsg - tick_beforePostMsg1) + "ms " + hint);

                        }


                        //
                    }
                }


                //
                try {

                    if (pQ2.hEvent0 != null) {
                        //SetEvent(pQ2->hEvents[0]);
                        tmpFunc.qyFuncs.myEventSet(pQ2.hEvent0);
                    }
                } catch (Exception e) {
                    Log.d(e.toString());
                }

                iErr  =  0;

            } while ( false );

            return iErr;

        }


    };
    */
    
}
