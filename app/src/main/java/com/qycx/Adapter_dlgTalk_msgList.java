package com.qycx;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.TableItem;
import com.qycx.base.displayShortTime;
import com.qycx.base.qwmNetworkStatusCommProc;
import com.qycx.base.qyDefs;
import com.qycx.base.qyDmItemCommProc;
import com.qycx.base.tmpProc;

import java.util.List;

public class Adapter_dlgTalk_msgList extends RecyclerView.Adapter<Adapter_dlgTalk_msgListViewHolder>  {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener;
    List<TableItem.TableItem_dlgTalk_msg> items;
    Activity_dlgTalk mActivity;

    public Adapter_dlgTalk_msgList(Activity activity, List<TableItem.TableItem_dlgTalk_msg> data) {
        mActivity = (Activity_dlgTalk) activity;
        items = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public Adapter_dlgTalk_msgListViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dlgtalk_msgrow, parent, false);
        itemView.setOnTouchListener(mActivity.mAdapter.mFragment_dlgTalk_msgList1);

        return new Adapter_dlgTalk_msgListViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(Adapter_dlgTalk_msgListViewHolder holder, int position) {
        var item = items.get(position);

        //holder.TextView.setText( items[position];
        String str;
        if (mActivity.m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            str = String.format("%s %s %s", item.dw, item.bm, item.syr);
            //
            str += " " + displayShortTime.qyFuncs_Droid.displayShortTime(item.tStartTime);
            //
            holder.tv_speaker.setText(str);
        } else {
            str = item.syr;
            //
            str += " " + displayShortTime.qyFuncs_Droid.displayShortTime(item.tStartTime);
            //
            holder.tv_speaker.setText(str);
        }
        holder.iv.setImageResource(item.ImageResourceId);
        //
        if (item.iTaskId == 0) str = item.msg;
        else {
            int iStatus = tmpProc.qyFuncs.getStatusByTaskId(item.iTaskId);
            str = item.msg + " " + qyDmItemCommProc.qyFuncs.qyGetDesByType1(qwmNetworkStatusCommProc.qyFuncs.CONST_qyStatusTable_cn, iStatus) + " " + item.statusStr;
        }
        //
        holder.tv_msg.setText(str);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
