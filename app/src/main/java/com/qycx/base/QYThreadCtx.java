package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentThreadId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;

import java.util.Map;

public class QYThreadCtx {
    private static String TAG = "QYThreadCtx";
    private static int count = 0;



    //
    public static qyShowInfoCommProc.Ctx_thread_tmpVar getCtxThreadTmpVar( int iParamType, int iChannelType ){
        qyShowInfoCommProc.Ctx_thread_tmpVar var;

        //
        if  (  qyMcMain_global.g.g_pQyMc  ==  null)  {
            //
            Log.d("qy", "getCtxThreadTmpVar.g.g_pQyMc is null");
            return null;
        }
        ctxQyMc.CCtxQyMc  pQyMc = qyMcMain_global.g.g_pQyMc;

        if  (  qyMcMain_global.g.g_pQyMc.iThreadCtxtMap  ==  null  )  {
            //
            Log.d("qy", "getCtxThreadTmpVar.g.g_pQyMc.iThreadCtxMap is null");
            return  null;
        }

        do {
            //
            var = qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.get(Thread.currentThread().getId());
            if (var == null) {
                //
                var = new qyShowInfoCommProc.Ctx_thread_tmpVar();
                var.m_threadId = myGetCurrentThreadId();
                qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.put(Thread.currentThread().getId(), var);
                //qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.put(var.threadId, var);
                //     Log.d(TAG, Thread.currentThread().getName() +" id: "+ Thread.currentThread().getId() + " didn't get ctx from hashmap, call new: " + qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.size());
            }
            if (iParamType != 0) {

                boolean  tmp_bErr  = false;

                switch (iParamType) {
                    case Consts.CONST_iParamType_main:
                         if (var.m_ctxThreadMain == null) {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadMain  ==  null   )  {
                                    var.m_ctxThreadMain=new qyShowInfoCommProc.Ctx_thread_main();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;
                    case  Consts.CONST_iParamType_decObj_a:
                          if  (  var.m_ctxThreadDecObjA  ==  null  )  {
                              try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                  if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                      tmp_bErr=true;
                                      break;
                                  }
                                  if  (  var.m_ctxThreadDecObjA  ==  null   )  {
                                      var.m_ctxThreadDecObjA=new qyShowInfoCommProc.Ctx_thread_decObj_a();
                                  }
                              } catch (Exception e) {
                                  Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                              }

                          }
                          break;
                    case  Consts.CONST_iParamType_toSend:
                        if  (  var.m_ctxThreadtoSend ==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadtoSend ==  null   )  {
                                    var.m_ctxThreadtoSend =new qyShowInfoCommProc.Ctx_thread_toSend();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        switch (iChannelType) {
                            case  CONST_channelType_realTimeMedia:
                                  if  (  var.m_ctxThreadtoSend.a==null) {
                                      try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                          if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                              tmp_bErr=true;
                                              break;
                                          }
                                          if  (  var.m_ctxThreadtoSend.a ==  null   )  {
                                              var.m_ctxThreadtoSend.a =new qyShowInfoCommProc.Ctx_thread_toSend_a();
                                          }
                                      } catch (Exception e) {
                                          Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                                      }

                                  }
                                  break;
                            case  CONST_channelType_media:
                                  if  (  var.m_ctxThreadtoSend.v==null)  {

                                      try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                          if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                              tmp_bErr=true;
                                              break;
                                          }
                                          if  (  var.m_ctxThreadtoSend.v ==  null   )  {
                                              var.m_ctxThreadtoSend.v =new qyShowInfoCommProc.Ctx_thread_toSend_v();
                                          }
                                      } catch (Exception e) {
                                          Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                                      }
                                  }
                                  break;
                            default:
                                    break;
                        }
                        //
                        //
                        break;
                    case  Consts.CONST_iParamType_toShareVideo_post:
                        if  (  var.m_ctxThreadToShareVideoPost ==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadToShareVideoPost==  null   )  {
                                    var.m_ctxThreadToShareVideoPost  =new qyShowInfoCommProc.Ctx_thread_toShareVideo_post();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;

                    case  Consts.CONST_iParamType_decv:
                        if  (  var.m_ctxThreadDecV ==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadDecV==  null   )  {
                                    var.m_ctxThreadDecV  =new qyShowInfoCommProc.Ctx_thread_decv();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;
                    case  Consts.CONST_iParamType_deca:
                        if  (  var.m_ctxThreadDecA ==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadDecA==  null   )  {
                                    var.m_ctxThreadDecA =new qyShowInfoCommProc.Ctx_thread_deca();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;

                    case  Consts.CONST_iParamType_playa:
                        if  (  var.m_ctxThreadPlayA ==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadPlayA ==  null   )  {
                                    var.m_ctxThreadPlayA =new qyShowInfoCommProc.Ctx_thread_playa();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;

                    case  Consts.CONST_iParamType_encA:
                        if  (  var.m_ctxThreadEncA==  null  )  {
                            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                if (0 != syncObj.sync("", pQyMc.hMutex_syncCtxThreadTmpVar, "")) {
                                    tmp_bErr=true;
                                    break;
                                }
                                if  (  var.m_ctxThreadEncA ==  null   )  {
                                    var.m_ctxThreadEncA =new qyShowInfoCommProc.Ctx_thread_encA();
                                }
                            } catch (Exception e) {
                                Log.e("qy","getCtxThreadTmpVar. "  +  e.getMessage());

                            }
                        }
                        break;
                    default:
                        break;
                }
                //
                if  ( tmp_bErr)  {
                    break;
                }

            }
            //
            var.m_timestamp = myGetTickCount(null);
            return var;
        } while ( false );

        return  null;
    }

    public static void chkCtxThreadTmpVar(){
        if(++count % 5 != 0)
            return;
        long currentTime = myGetTickCount(null);

      //  /Log.d(TAG, "the thread hash size: " + qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.size());

        for(Map.Entry<Long, qyShowInfoCommProc.Ctx_thread_tmpVar> var :qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.entrySet() ){
            long key = var.getKey();
            qyShowInfoCommProc.Ctx_thread_tmpVar var1 = var.getValue();
            if(currentTime - var1.m_timestamp >10000){
                qyMcMain_global.g.g_pQyMc.iThreadCtxtMap.remove(key);
                //
                //Log.d(TAG, "remove thread ctx thread: " + key );
            }
            else{
                //Log.d(TAG, "the thread can live: " + key);
            }
        }
    }
}
