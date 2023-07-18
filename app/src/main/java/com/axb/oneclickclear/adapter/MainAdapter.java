package com.axb.oneclickclear.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axb.oneclickclear.R;

public class MainAdapter extends BaseAdapter {

    private Context ctx;
    private String[] titles;
    private int[] ids;

    public MainAdapter(Context ctx, String[] titles, int[] ids) {
        this.ctx = ctx;
        this.titles = titles;
        this.ids = ids;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_home, parent, false);
            holder = new ViewHolder();
            holder.iv_item = (ImageView) view.findViewById(R.id.iv_item);
            holder.tv_item = (TextView) view.findViewById(R.id.tv_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_item.setText(titles[position]);
        holder.iv_item.setImageResource(ids[position]);
        return view;
    }

    public String getTitle(int position) {
        return titles[position];
    }

    public int geImg(int position) {
        return ids[position];
    }

    class ViewHolder {
        TextView tv_item;
        ImageView iv_item;
    }
}
