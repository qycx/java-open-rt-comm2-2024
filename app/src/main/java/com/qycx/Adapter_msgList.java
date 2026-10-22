package com.qycx;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qycx.base.TableItem;
import com.qycx.base.displayShortTime;
import com.qycx.base.qyDefs;

import java.util.List;

public class Adapter_msgList extends BaseAdapter {
    List<TableItem.TableItem_msg> items;
    LayoutInflater context;


    public Adapter_msgList(LayoutInflater context, List<TableItem.TableItem_msg> items) {
        super();
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > getCount()) return null;
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        var item = items.get(position);
        String str;

        if (item.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            view = context.inflate(R.layout.layout_grpmsgrow, null);

            ImageView iv = view.findViewById(R.id.Image);
            iv.setImageResource(item.ImageResourceId);

            String displayName = item.grpName;
            //
            TextView tv1 = view.findViewById(R.id.Text2);
            tv1.setText(displayName);

            str = displayShortTime.qyFuncs_Droid.displayShortTime(item.tRecvTime);
            //
            TextView tv2 = view.findViewById(R.id.Time);
            tv2.setText(str);

            //
            str = String.format("%s：%s", item.speaker, item.msg);
            //
            TextView tv3 = view.findViewById(R.id.Text3);
            tv3.setText(str);
        } else {
            view = context.inflate(R.layout.layout_contactmsgrow, null);

            ImageView iv1 = view.findViewById(R.id.Image);
            iv1.setImageResource(item.ImageResourceId);

            TextView tv1 = view.findViewById(R.id.Text1);
            tv1.setText(item.dw + " " + item.bm);

            String displayName = item.syr;
            //
            TextView tv2 = view.findViewById(R.id.Text2);
            tv2.setText(displayName);

            str = displayShortTime.qyFuncs_Droid.displayShortTime(item.tRecvTime);
            TextView tv3 = view.findViewById(R.id.Time);
            tv3.setText(str);

            str = String.format("%s：%s", item.speaker, item.msg);
            //
            TextView tv4 = view.findViewById(R.id.Text3);
            tv4.setText(str);
        }

        return view;
    }
}
