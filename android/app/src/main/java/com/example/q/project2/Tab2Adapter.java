package com.example.q.project2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-01.
 */

public class Tab2Adapter extends BaseAdapter {
    private ArrayList<Tab2Item> items = new ArrayList<>();

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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tab2_item, parent, false);
        }

        Picasso.with(parent.getContext()).load(items.get(position).getUrl())
                .into((ImageView) convertView.findViewById(R.id.gallery_item));
        return convertView;
    }

    public void clearData() {
        items.clear();
    }

    public void addData(String name, String url, String last_update, boolean on_server) {
        for (int i=0 ; i<items.size() ; i++) {
            if(items.get(i).getName().equals(name)) { return; }
        }

        items.add(new Tab2Item(name, url, last_update, on_server));
        notifyDataSetChanged();
    }
}
