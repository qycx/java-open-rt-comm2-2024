package com.qycx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qycx.base.TableItem;
import com.qycx.base.qyDefs;

import java.util.List;

public class Adapter_imObjList extends BaseAdapter {
    List<TableItem.TableItem_imObj> items;
    LayoutInflater context;

    public Adapter_imObjList(LayoutInflater context, List<TableItem.TableItem_imObj> items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var item = items.get(position);

        var view = convertView;
        String str;

        if (item.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            view = context.inflate(R.layout.layout_grprow, null);
            //
            ImageView iv1 = view.findViewById(R.id.Image);
            iv1.setImageResource(item.ImageResourceId);
            str = String.format("%s %s", "(分组)", item.ui64Id);
            TextView tv1 = view.findViewById(R.id.Text1);
            tv1.setText(str);
            String displayName = item.grpName;
            TextView tv2 = view.findViewById(R.id.Text2);
            tv2.setText(displayName);
        } else {
            view = context.inflate(R.layout.layout_contactrow, null);
            ImageView iv1 = view.findViewById(R.id.Image);
            iv1.setImageResource(item.ImageResourceId);
            str = String.format("%s %s %s", item.dw, item.bm, item.ui64Id);
            TextView tv1 = view.findViewById(R.id.Text1);
            tv1.setText(str);
            String displayName = item.syr;
            TextView tv2 = view.findViewById(R.id.Text2);
            tv2.setText(displayName);
        }
        return view;
    }
}


