

package com.qycx.base;


import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;

public class DlgInstantAssistant
{
    public static class qyFuncs_Droid
    {

        public static  int closeInstantAssistant(Object hDlgTalk)
        {
            return -1;
        }

        public static  int viewInstantAssistant(Object hWndEvent, int iWndContentType, int xPos_screen, int yPos_screen, Object hCurTalk, boolean bNoDelayToView)
        {
            if (hCurTalk == null) return -1;
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk)hCurTalk;
            if (dlgTalk.m_var.m_hTool_dlgTalk_av == null) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av)dlgTalk.m_var.m_hTool_dlgTalk_av;

            return 0;
        }

    }
}
