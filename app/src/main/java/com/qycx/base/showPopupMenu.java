package com.qycx.base;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

public class showPopupMenu {
    public static class qyFuncs_Droid {

        public interface PF_procMenu {
            int call(Context context, int menuId);
        }

        public static class Tmp_menu implements PopupMenu.OnMenuItemClickListener {
            Context m_context;
            PF_procMenu m_pf_procMenu;

            public Tmp_menu(Context context, PF_procMenu pf) {
                m_context = context;
                m_pf_procMenu = pf;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Context context = m_context;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                //Toast.makeText(context, item.ItemId, Toast.LENGTH_SHORT).show();
                int ii = item.getItemId();

                //
                if (m_pf_procMenu != null) {
                    m_pf_procMenu.call(context, item.getItemId());
                }
                int iii = 0;
                //throw new NotImplementedException();
                return true;
            }
        }

        public static void showPopupMenu(Context context, View view, int menuRes, PF_procMenu pf) {
            // View当前PopupMenu显示的相对View的位置
            //Context context = getActivity();
            PopupMenu popupMenu = new PopupMenu(context, view);
            // menu布局
            popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
            // menu的item点击事件
            popupMenu.setOnMenuItemClickListener(new Tmp_menu(context, pf));
            popupMenu.show();
        }
    }
}
