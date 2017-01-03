package com.example.q.project2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by q on 2017-01-02.
 */

public class TabCAdapter extends BaseAdapter {
    private final ArrayList<TabCProfile> items = new ArrayList<>();

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
            convertView = inflater.inflate(R.layout.tabc_profile, parent, false);
        }

        Picasso.with(parent.getContext()).load(items.get(position).getProfileURL())
                .into((ImageView) convertView.findViewById(R.id.profile_item));

        String display = items.get(position).getUserName() + "\n" + items.get(position).getPhone();
        ((TextView)convertView.findViewById(R.id.profile_name)).setText(display);

        if(items.get(position).getAwake()) {
            ((FrameLayout)convertView.findViewById(R.id.profile_shadow)).setBackgroundColor(0x00000000);
        } else {
            ((FrameLayout)convertView.findViewById(R.id.profile_shadow)).setBackgroundColor(0xbb000000);
        }

        return convertView;
    }

    public void addData(String userID, String userName, String profileURL, Boolean awake,
                          String phone, Integer keycode) {
        items.add(new TabCProfile(userID, userName, profileURL, awake, phone, keycode));
        notifyDataSetChanged();
    }

    public void clear() { items.clear(); }
}
