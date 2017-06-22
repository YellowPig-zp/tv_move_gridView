package com.yuqirong.draggridview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuqirong.draggridview.R;
import com.yuqirong.draggridview.view.DragGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anyway on 2016/2/19.
 */
public  class DragGridAdapter extends BaseAdapter {

    private static final String TAG = "DragGridAdapter";

    private boolean isMove = false;

    private int movePosition = -1;

    private final List<String> list;

    private LayoutInflater mInflater;
    private Context context;

    private DragGridView gridView;
    public DragGridAdapter(List list, Context context, DragGridView gridView) {
        this.list = list;
        this.context = context;
        this.gridView = gridView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewBean holder;
        if (view == null) {
            holder = new ViewBean();
            view = mInflater.inflate(R.layout.item,parent,false);
            holder.text = (TextView) view.findViewById(R.id.tv_text);
            view.setTag(holder);
        } else {
            holder = (ViewBean) view.getTag();
        }
        holder.text.setText(list.get(position));
        if (position == movePosition && gridView.mode!=DragGridView.MODE_FORBID) {
            holder.text.setBackgroundResource(R.color.pressedColor);
        }else{
            holder.text.setBackgroundResource(R.color.normalColor);
        }
        return view;
    }


    /**
     * 给item交换位置
     *
     * @param from item原先位置
     * @param to      item现在位置
     */
    public void exchangePosition(int from, int to, boolean isMove) {
        String temp = list.get(from);
        if(from < to){
            for(int i=from; i<to; i++){
                Collections.swap(list, i, i+1);
            }
        }else if(from > to){
            for(int i=from; i>to; i--){
                Collections.swap(list, i, i-1);
            }
        }

        list.set(to, temp);

        movePosition = to;
        this.isMove = isMove;
        notifyDataSetChanged();
    }


    private final class ViewBean {
        TextView text;
    }

}
