package com.qycx.base;

import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.systemDefs.Consts.manufacturer_huaWei;
import static com.qycx.base.systemDefs.Consts.manufacturer_mbk;
import static com.qycx.base.systemDefs.Consts.manufacturer_oppo;
import static com.qycx.base.systemDefs.Consts.manufacturer_unione;
import static com.qycx.base.systemDefs.Consts.manufacturer_vivo;
import static com.qycx.base.systemDefs.Consts.manufacturer_xiaoMi;
import static com.qycx.base.systemDefs.Consts.model_huaWei_TAHAN00m;
import static com.qycx.base.systemDefs.Consts.model_mbk_t2101;
import static com.qycx.base.systemDefs.Consts.model_unione_ua100;
import static com.qycx.base.systemDefs.Consts.model_xiaoMi_redMi10;

import android.os.Build;

import com.qycx.tmpffmpeglibrary.TmpFfmpeg;

import java.lang.reflect.Method;

public class getSystem
{
    public static class qyFuncs_Droid
    {

        //
        static qyTypes.QY_DMITEM[] manufacturerTable = new qyTypes.QY_DMITEM[]
{
            new qyTypes.QY_DMITEM(manufacturer_xiaoMi,"Xiaomi"),
            new qyTypes.QY_DMITEM(manufacturer_huaWei,"HUAWEI"),
            new qyTypes.QY_DMITEM(manufacturer_mbk,"MBK"),
            new qyTypes.QY_DMITEM(manufacturer_unione,"UniOne"),
            new qyTypes.QY_DMITEM(manufacturer_vivo,"vivo"),
            new qyTypes.QY_DMITEM(manufacturer_oppo,"OPPO"),


        new qyTypes.QY_DMITEM(-1,""),


};

        static qyTypes.QY_DMITEM[] modelTable_xiaoMi = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_xiaoMi_redMi10,"M2004J7AC"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),

        };

        static qyTypes.QY_DMITEM[] modelTable_huaWei = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_huaWei_TAHAN00m,"TAH-AN00m"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),

        };

        static qyTypes.QY_DMITEM[] modelTable_mbk = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_mbk_t2101,"MBK-T2101"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
        };

        static qyTypes.QY_DMITEM[] modelTable_unione = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_unione_ua100,"UA100"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
        };

        private static String HARMONY_OS = "harmony";

        //判断是否为鸿蒙
        public static boolean isHarmonyOSa()
        {
            try
            {
                Class clz = Class.forName("com.huawei.system.BuildEx");
                Method method = clz.getMethod("getOsBrand");

                ClassLoader classLoader = clz.getClassLoader();

                //Log.d(TAG,"classLoader: " + classLoader);

                //BootClassLoader的parent为null
                if (classLoader != null && classLoader.getParent() == null)
                {


                    //return method.invoke(clz).toString();//返回获取的名称
                    String name = method.invoke(clz).toString();
                    boolean b = HARMONY_OS.equals(name);


                    //****返回是否是鸿蒙系统 自行注释并将方法返回值改为boolean
                    //return HARMONY_OS.Equals(method.Invoke(clz));
                    return b;
                }
            }
            catch (ClassNotFoundException e)
            {
            }
            catch (NoSuchMethodException e)
            {
            }
            catch (Exception e)
            {
            }
            return false;

        }
        //
        public static guiShare.PF_getSystemInfo getSystemInfo=()->
        {
            int iManufactor;
            int iModel;
            boolean b_isHarmony;

            String versionCode = "";

            String manufacturer = Build.MANUFACTURER;

            String model = Build.MODEL;

            //
            iManufactor = 0;
            iModel = 0;
            b_isHarmony = false;
            b_isHarmony = qyFuncs_Droid.isHarmonyOSa();

            if (null != manufacturer)
            {
                int iVal;
                //
                char[] m1 = manufacturer.toLowerCase().toCharArray();
                String ss = "mbk";
                char[] s1 = ss.toLowerCase().toCharArray();
                int i;
                boolean bErr = false;
                for  (  i  =  0;  i  <  ss.length() ; i++)
                {
                    if (s1[i] != m1[i])
                    {
                        bErr = true;
                        break;
                    }
                    continue;
                }
                if (!bErr)
                {
                    iManufactor = manufacturer_mbk;
                }
                else
                {
                    //
                    iVal = qyDmItemCommProc.qyFuncs.qyGetTypeByDes(manufacturerTable, manufacturer);
                    if (iVal < 0) iVal = 0;
                    iManufactor = iVal;
                }
                //
                qyTypes.QY_DMITEM[] pModelTable = null;
                switch (iManufactor)
                {
                    case manufacturer_xiaoMi:
                        pModelTable = modelTable_xiaoMi;
                        break;
                    case manufacturer_huaWei:
                        pModelTable = modelTable_huaWei;
                        break;
                    case manufacturer_mbk:
                        pModelTable = modelTable_mbk;
                        break;
                    case manufacturer_unione:
                        pModelTable = modelTable_unione;
                        break;
                    default:
                        break;
                } 
                if (pModelTable != null)
                {
                    iVal = qyDmItemCommProc.qyFuncs.qyGetTypeByDes(pModelTable, model);
                    if (iVal < 0) iVal = 0;
                    //
                    iModel = iVal;
                }

                

                //versionCode = SystemProperty("ro.miui.version.code_time");

            }

            //
            ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
            pQyMc.iManufacturer  =  iManufactor;
            pQyMc.iModel  =  iModel;
            pQyMc.b_isHarmony  =   b_isHarmony;

            //
            return;
        };

        public static guiShare.PF_customSystem customSystem=()->
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            switch(pQyMc.iManufacturer)
            {
                case manufacturer_xiaoMi:
                     switch (pQyMc.iModel)
                    {
                        case model_xiaoMi_redMi10:
                            pProcInfo.cfg.b_no_KeyLowLatency_a = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case manufacturer_mbk:
                     switch(pQyMc.iModel)
                    {
                        case model_mbk_t2101:
                            pProcInfo.cfg.b_no_KeyLowLatency_a = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case manufacturer_unione:
                    switch (pQyMc.iModel)
                    {
                        case model_unione_ua100:
                            pProcInfo.cfg.b_no_4k = true;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;

            }

            //
            set100kParam();

            //
            return 0;
        };

        //
        public static int set100kParam()
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if  (  pQyMc ==  null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if  (  pProcInfo==null)  return  -1;

            //

            //com.qycx.tmpffmpeglibrary.TmpCusSystem.cfg100k  kk  =  new TmpCusSystem.cfg100k()  ;
            //com.qycx.tmpjavalibrary.FileUtilgleslibrary.tmpffmpeglibrary..TmpFfmpegTmpCusSystem  kk;



            //
            switch ( pQyMc.iManufacturer)  {

                /*
                case  manufacturer_oppo:

                    pProcInfo.use100k_v_w  =  480;
                    pProcInfo.use100k_v_h  =  640;
                    //
                    pProcInfo.use100k_enc_sleepInMs  =  100;
                    //
                    pProcInfo.use100k_v_biterate  =  20000;
                    //
                    pProcInfo.use100k_v_keyFrameRate  =  3;

                    //
                    break;

                 */


                case  manufacturer_huaWei:
                default:

                     /*
                      pProcInfo.use100k_v_w  =  240;    //480;
                      pProcInfo.use100k_v_h  =  320;    //640;
                      //
                      pProcInfo.use100k_enc_sleepInMs  =  4000;//2000;
                      //
                      pProcInfo.use100k_v_biterate  =  10000;    //10000;
                      //
                      pProcInfo.use100k_v_frameRate  =  1;

                      pProcInfo.use100k_v_perSnd_sleepInMs  =  80;

                      //
                      pProcInfo.use100k_v_w  =  480;    //480;
                      pProcInfo.use100k_v_h  =  640;    //640;
                      //
                      pProcInfo.use100k_enc_sleepInMs  =  1000;//2000;
                      //
                      pProcInfo.use100k_v_biterate  =  25000;    //10000;
                      //
                      pProcInfo.use100k_v_perSnd_sleepInMs=1;

                    //
                    pProcInfo.use100k_enc_sleepInMs  =  333;//2000;

                      //
                      pProcInfo.use100k_v_frameRate  =  3;
                      pProcInfo.use100k_v_I_FRAME_INTERVAL  =  10;
                      //
                    pProcInfo.use100k_v_frameRate  =  20;//5;
                    pProcInfo.use100k_v_I_FRAME_INTERVAL  =  3  *  pProcInfo.use100k_v_frameRate;

                    //
                    pProcInfo.use100k_enc_sleepInMs  =  (int)(1000.  /   pProcInfo.use100k_v_frameRate);//2000;

                    //
                      pProcInfo.use100k_v_biterate  =  35000;    //10000;
                    pProcInfo.use100k_v_biterate  =  50000;    //10000;

                      //
                      if  (  false  ) {
                          pProcInfo.use100k_v_w = 144;
                          pProcInfo.use100k_v_h = 176;
                      }

                      */
                      com.qycx.tmpffmpeglibrary.TmpCusSystem.doCus100k(pProcInfo.cfgInfo100k);

                      //
                      break;
            }


            //
            return  0;
        }

    }
}


