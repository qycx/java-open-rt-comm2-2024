package com.qycx.base;

import static com.qycx.base.prepare.qyFuncs_Droid.m_tmp_videoConferences;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_transferFile;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

public class taskDataFunc_android {
    public static class qyFuncs_Droid {


        public static guiShare.PF_newIndex_DLG_TALK_videoConference newIndex_DLG_TALK_videoConference=(int index_taskInfo)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            int index = -1;
            if(m_tmp_videoConferences == null){
                showInfo_open.qyFuncs.showInfo_open("newIndex_DLG_TALK_videoConference error: line_19 m_tmp_videoConferences is null ");
                return -1;
            }

            if (index_taskInfo < 0 || index_taskInfo >= m_tmp_videoConferences.length) return -1;
            if (m_tmp_videoConferences[index_taskInfo] == null) return -1;
            index = index_taskInfo;

            return index;
        };

        public static guiShare.PF_getVideoConference getVideoConference=(int ind_videoConference)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            //
            qmcTaskInfo.DLG_TALK_videoConference videoConference = null;
            int index = (int) ind_videoConference;

            if (index < 0 || index >= m_tmp_videoConferences.length) return null;

            videoConference = m_tmp_videoConferences[index];

            return videoConference;

        };

        public static guiShare.PF_freeIndex_DLG_TALK_videoConference freeIndex_DLG_TALK_videoConference=(int ind_videoConference)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            int index = (int) ind_videoConference;

            ind_videoConference = -1;

            return;
        };


        public static guiShare.PF_new_QMC_taskData new_QMC_taskData=(int index_taskInfo, int taskDataType)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

                //
                int index = -1;
            try {
                if(mainWnd.m_tmp_taskDatas == null){
                    Log.e("m_tmp_taskDatas is null!!!!!");
                    return -1;
                }
                //
                if (index_taskInfo < 0 || index_taskInfo >= mainWnd.m_tmp_taskDatas.length)
                    return -1;

                //
                if (mainWnd.m_tmp_taskDatas[index_taskInfo] != null) return -1;

                //
                switch (taskDataType) {
                    case CONST_taskDataType_conf:

                        mainWnd.m_tmp_taskDatas[index_taskInfo] = new qmcTaskInfo.QMC_taskData_conf(index_taskInfo);
                        break;
                    case CONST_taskDataType_transferFile:
                        mainWnd.m_tmp_taskDatas[index_taskInfo] = new qmcTaskInfo.QMC_taskData_transferFile();
                        break;
                    default:
                        //Log.d(TAG,"new_QMC_taskData failed: unprocessed taskDataType");
                        return -1;
                }

                qmcTaskInfo.QMC_taskData_common taskData = mainWnd.m_tmp_taskDatas[index_taskInfo];// new qmcTaskInfo.DLG_TALK_videoConference();
                //
                if (taskData.uiType != (int) taskDataType) {
                    int ii = 0;
                }
                //
                taskData.tmpStatus = new qmcTaskInfo.QmcTaskDataCommon_tmpStatus();  //  clear
                //
                index = index_taskInfo;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                return index;
            }


        };

        public static guiShare.PF_getTaskData getTaskData=(int ind_taskData)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            //
            qmcTaskInfo.QMC_taskData_common taskData = null;
            int index = (int) ind_taskData;

            if (index < 0 || index >= mainWnd.m_tmp_taskDatas.length) return null;

            taskData = mainWnd.m_tmp_taskDatas[index];

            return taskData;

        };

        public static guiShare.PF_free_QMC_taskData free_QMC_taskData=(int ind_taskData)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            int index = (int) ind_taskData;
            if (index >= 0 && index < mainWnd.m_tmp_taskDatas.length) {

                if (mainWnd.m_tmp_taskDatas[index] != null) {
                    //
                    mainWnd.m_tmp_taskDatas[index].free();

                    //
                    mainWnd.m_tmp_taskDatas[index] = null;
                }
            }
            //
            ind_taskData = -1;

            return;
        };

        ////////////////////////////////
        ///
        public static boolean bIndexValid_myThread(int index) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            if (index < 1 || index >= mainWnd.m_tmp_threads.length) return false;
            return true;

        }

        public static int newMyThread(Object p0, qmcFunc.PF_threadProc pf, Object param, int index_o) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                //
                if (syncObj.sync("", mainWnd.hMutex_syncThreads,"newMyThread.167") != 0) {
                    return iErr;
                }

                //
                int index = -1;

                for (index = 1; index < mainWnd.m_tmp_threads.length; index++) {
                    if (mainWnd.m_tmp_threads[index] == null) break;
                }
                if (index == mainWnd.m_tmp_threads.length) {
                    Log.d("new_androidThread failed: too many android threads");
                    return iErr;
                }
                //              
                mainWnd.m_tmp_threads[index] = new MyThread(p0, pf, param);


                index_o = index;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }


            //
            String str = String.format("newMyThread ok, %s", index_o);


            //
            iErr = 0;

            

            //
            return index_o;
        }

        public static MyThread getMyThread(int obj_myThread) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            //
            MyThread mt = null;
            int index = (int) obj_myThread;

            if (index < 1 || index >= mainWnd.m_tmp_threads.length) return null;

            mt = mainWnd.m_tmp_threads[index];

            return mt;

        }

        public static int freeMyThread(int obj_myThread) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;

            int index = (int) obj_myThread;

            try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
            {

                //
                if (syncObj.sync("", mainWnd.hMutex_syncThreads,"freeMyThread.231") != 0) {
                    return -1;
                }

                if (mainWnd.m_tmp_threads[index] != null) {
                    //
                    mainWnd.m_tmp_threads[index] = null;
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }

            //
            String str;
            str = String.format("freeMyThread %s", obj_myThread);
                    //
            obj_myThread = -1;
            

            return obj_myThread;
        }


    }


}
