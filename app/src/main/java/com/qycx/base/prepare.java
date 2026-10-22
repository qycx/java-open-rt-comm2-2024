package com.qycx.base;


import com.qycx.base.util.Log;

import static com.qycx.base.qnmCommProc_mis.Consts.MAX_activeMems_from;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_usMaxCnt_taskInfos;

public class prepare
{
    public static class qyFuncs_Droid
    {
        //
        public static qmcTransform.QisTrans[] g_transes = null;
        public static qmcTaskInfo.DLG_TALK_videoConference[] m_tmp_videoConferences = null;
        //public static qmcTaskInfo.QMC_taskData_conf[] m_tmp_taskData_confs = new qmcTaskInfo.QMC_taskData_conf[DEFAULT_usMaxCnt_taskInfos];

        public static int perpare_init()
        {
            int iErr = -1;
            Log.d("prepare_init, initTransforms. 21");

            do
            {
                g_transes = new qmcTransform.QisTrans[5];
                m_tmp_videoConferences = new qmcTaskInfo.DLG_TALK_videoConference[DEFAULT_usMaxCnt_taskInfos];//[];
                int i;

                if(g_transes == null) return iErr;
                for  (  i  =  0;  i  <  g_transes.length;i++)
                {
                    g_transes[i] = new qmcTransform.QisTrans();
                    g_transes[i].usMaxCntLimit_transforms = MAX_activeMems_from;
                    g_transes[i].pTransforms = new qmcTransform.QY_TRANSFORM[g_transes[i].usMaxCntLimit_transforms];
                    //Log.d(TAG,"prepare_init, initTransforms.643");
                    int j;
                    for (j = 0; j < g_transes[i].usMaxCntLimit_transforms; j++)
                    {
                        //Log.d(TAG,"initTransforms.647  " + j);
                        g_transes[i].pTransforms[j] = new qmcTransform.QY_TRANSFORM();
                    }
                        
                    //Log.d(TAG,"prepare_init, initTransforms.647");

                    //
                    continue;
                }

                //
                for  (  i  =0;i<m_tmp_videoConferences.length;i++)
                {
                    m_tmp_videoConferences[i] = new qmcTaskInfo.DLG_TALK_videoConference();
                }

            } while (false);


            return iErr;
        }


        public static int  prepare_exit()
        {
            int iErr = -1;

            do
            {
                int i;
                if(g_transes==null) return iErr;
                for (i  =0; i< g_transes.length; i++)
                {
                    g_transes[i] = null;
                }
                g_transes = null;

                //
                for  (  i=0;i<m_tmp_videoConferences.length;i++)
                {
                    m_tmp_videoConferences[i] = null;
                }
                m_tmp_videoConferences = null;
            } while (false);

            return iErr;
        }

        public static guiShare.PF_newIndex_qisTrans newIndex_qisTrans=()->
        {
            int iErr = -1;
            int index = -1;
            do
            {
                int i;
                for  (  i = 1; i < g_transes.length; i++)
                {
                    if (g_transes[i].bUsed) continue;
                    break;
                }
                if (i == g_transes.length) break;
                g_transes[i].bUsed = true;
                index = i;

                iErr = 0;
            } while (false);

            if (iErr != 0) return iErr;
            return index;

        };

        public static guiShare.PF_freeIndex_qisTrans freeIndex_qisTrans=(int index)->
        {
            if  (  index  >=  1  &&  index  < g_transes.length)
            {
                if (!g_transes[index].bUsed) return 0;
                //
                int i=index;
                int j;
                for (j = 0; j < g_transes[i].usMaxCntLimit_transforms; j++)
                {
                    //Log.d(TAG,"initTransforms.647  " + j);
                    g_transes[i].pTransforms[j].pTask=null;
                }
                //
                g_transes[index].bUsed = false;
                return 0;            
            }
            return -1;
        };

        public static guiShare.PF_getQisTransByIndex getQisTransByIndex=(int index)->
        {
            if (index >= 1 && index < g_transes.length) return g_transes[index];
            return null;
        };
    }
}

