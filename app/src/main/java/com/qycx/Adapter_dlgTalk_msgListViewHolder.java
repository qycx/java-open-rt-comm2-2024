package com.qycx;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.TableItem;
import com.qycx.base.displayShortTime;
import com.qycx.base.qwmNetworkStatusCommProc;
import com.qycx.base.qyDefs;
import com.qycx.base.qyDmItemCommProc;
import com.qycx.base.tmpProc;

public class Adapter_dlgTalk_msgListViewHolder extends RecyclerView.ViewHolder {
    public ImageView iv;
    public TextView tv_speaker;
    public TextView tv_msg;


    public Adapter_dlgTalk_msgListViewHolder(View itemView, final Adapter_dlgTalk_msgList.OnItemClickListener listener) {
        super(itemView);
        iv = itemView.findViewById(R.id.iv);
        tv_speaker = itemView.findViewById(R.id.tv_speaker);
        tv_msg = itemView.findViewById(R.id.tv_msg);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
        });

        itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(itemView, position);
                }
            }
            return true;
        });
    }

    public void bindData(TableItem.TableItem_dlgTalk_msg item, Activity_dlgTalk mActivity) {
        // Bind data to views
        // ...

        String str;
        if (mActivity.m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            str = String.format("%s %s %s", item.dw, item.bm, item.syr);
            str += " " + displayShortTime.qyFuncs_Droid.displayShortTime(item.tStartTime);
            tv_speaker.setText(str);
        } else {
            str = item.syr;
            str += " " + displayShortTime.qyFuncs_Droid.displayShortTime(item.tStartTime);
            tv_speaker.setText(str);
        }
        iv.setImageResource(item.ImageResourceId);

        if (item.iTaskId == 0) {
            str = item.msg;
        } else {
            int iStatus = tmpProc.qyFuncs.getStatusByTaskId(item.iTaskId);
            str = item.msg + " " + qyDmItemCommProc.qyFuncs.qyGetDesByType1(qwmNetworkStatusCommProc.qyFuncs.CONST_qyStatusTable_cn, iStatus) + " " + item.statusStr;
        }
        tv_msg.setText(str);
    }
}
