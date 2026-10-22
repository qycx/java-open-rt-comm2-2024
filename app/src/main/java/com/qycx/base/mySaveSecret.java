package com.qycx.base;

import static com.qycx.base.guiShare.Consts.CONST_cfgFile_dbg_isCli;
import static com.qycx.base.guiShare.Consts.CONST_cfgFile_login_isCli;
import static com.qycx.base.guiShare.Consts.CONST_cfgName_passwd;
import static com.qycx.base.guiShare.Consts.CONST_cfgName_server;
import static com.qycx.base.guiShare.Consts.CONST_cfgName_statusServer;
import static com.qycx.base.guiShare.Consts.CONST_cfgName_usr;

import android.content.Context;

import com.qycx.MyApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class mySaveSecret
{
    public static class qyFuncs_Droid
    {
        public static void writeToFile(String message, String cfgFileName)
        {
            Context context = MyApplication.getContext();
            try
            {
                var outputStream = context.openFileOutput(cfgFileName,Context.MODE_PRIVATE);
                if (outputStream == null)
                    return;

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(message);
                outputStreamWriter.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public static String readFromFile(String cfgFileName)
        {
            String result = "";

            Context context = MyApplication.getContext();
            try
            {
                var inputStream = context.openFileInput(cfgFileName);
                if (inputStream != null)
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String temp = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((temp = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(temp);
                        stringBuilder.append("\n");
                    }

                    inputStream.close();
                    result = stringBuilder.toString();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            
            return result;
        }


        public static int saveLoginInfo(String m_server, String m_name, String m_passwd)
        {
            int iErr = -1;
            //String dir = CONST_subDir_isCli;

            Context context = MyApplication.getContext();
            //String directoryPath = context.FilesDir + File.Separator + dir;

            String str_login = "";
            str_login += CONST_cfgName_server + "=" + m_server + "\n";
            str_login += CONST_cfgName_usr + "=" + m_name + "\n";
            str_login += CONST_cfgName_passwd + "=" + m_passwd + "\n";

            writeToFile(str_login, CONST_cfgFile_login_isCli);

            iErr = 0;
        

            return iErr;
        }



        public static String getCfgValByName(String cfgInfo, String cfgName)
        {
            if(cfgInfo==null) return null;
            String str = "";
            qyMcMainDaemon_isCli_mgr.TMP_tBuf1024 tBuf1024=new qyMcMainDaemon_isCli_mgr.TMP_tBuf1024();
            char[] tmp_tBuf = new char[1024];

            int i;
            int nStart = 0;
            int nEnd = 0;
            for (i = 0; i < cfgInfo.length(); i++)
            {
                if (cfgInfo.getBytes()[i] == '\n')
                {
                    nEnd = i;
                    //
                    // nStart->nEnd;
                    int nSep = 0;
                    for (nSep = nStart; nSep < nEnd; nSep++)
                    {
                        if (cfgInfo.getBytes()[nSep] == '=')
                        {
                            if (nSep == nStart) break;
                            if (nSep - nStart + 1 > tmp_tBuf.length) return null;
//                            cfgInfo.CopyTo(nStart, tmp_tBuf, 0, nSep - nStart);
                            System.arraycopy(cfgInfo.getBytes(),0,tmp_tBuf,0,nSep - nStart);
                            tmp_tBuf[nSep - nStart] = '\0';

                            StringBuilder sb = new StringBuilder();
                            for (char c : tmp_tBuf)
                            {
                                if (c == '\0') break;
                                sb.append(c);
                            }
                            String tmp_str = sb.toString();
                            //
                            int len = tmp_str.length();
                            //
                            if (0 != tmpFunc.qyFuncs.mytcsicmp(tmp_str, cfgName))
                            {
                                break;
                            }
                            //
                            if (nEnd - nSep > tmp_tBuf.length) return null;
//                            cfgInfo.CopyTo(nSep + 1, tmp_tBuf, 0, nEnd - nSep - 1);
                            System.arraycopy(cfgInfo.getBytes(),0,tmp_tBuf,0,nEnd - nSep - 1);
                            tmp_tBuf[nEnd - nSep - 1] = '\0';
                            //
//                            sb.clear();
                            for (char c : tmp_tBuf)
                            {
                                if (c == '\0') break;
                                sb.append(c);
                            }
                            str = sb.toString();
                            return null;
                        }
                    }

                    //
                    nStart = i + 1;
                    continue;

                }

            }


        

            return str;
        }

        public static int readLoginInfo( String m_server,  String m_name,  String m_passwd)
        {
            int iErr = -1;

            m_server = "";
            m_name = "";
            m_passwd = "";

            String cfgInfo = readFromFile(CONST_cfgFile_login_isCli);

            m_server = getCfgValByName(cfgInfo, CONST_cfgName_server);
            m_name = getCfgValByName(cfgInfo, CONST_cfgName_usr);
            m_passwd = getCfgValByName(cfgInfo, CONST_cfgName_passwd);

            iErr = 0;
        
            return iErr;
        }

        //
        public static int saveDbgCfg(String m_statusServer)
        {
            int iErr = -1;
            //
            String str_cfg = "";
            str_cfg += CONST_cfgName_statusServer + "=" + m_statusServer + "\n";

            //
            writeToFile(str_cfg, CONST_cfgFile_dbg_isCli);


            //
            iErr = 0;
        

            return iErr;
        }



        //
        public static guiShare.PF_readDbgCfg readDbgCfg=(String m_statusServer)->
        {
            int iErr = -1;
            String cfgInfo = readFromFile(CONST_cfgFile_dbg_isCli);
//            m_statusServer = getCfgValByName(cfgInfo, CONST_cfgName_statusServer);
       //     m_statusServer = "10.0.10.84";
            m_statusServer = "10.0.1.106";
            return m_statusServer;
        };

    }
}


