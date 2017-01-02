package com.example.q.project2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by q on 2017-01-01.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private List<String> mNames;
    private List<Bitmap> mImages;

    public void add(String s,int position) {
        position = position == -1 ? getItemCount()  : position;
        mNames.add(position,s);
        Bitmap b = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square)).getBitmap();
        mImages.add(position, b);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            mNames.remove(position);
            mImages.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView profile_image;
        public final TextView contact_name;
        public ViewHolder(View view) {
            super(view);
            profile_image = (ImageView) view.findViewById(R.id.profile_image);
            contact_name = (TextView) view.findViewById(R.id.contact_name);
        }
    }

    public RecyclerViewAdapter(Context context, String[] names, Bitmap[] images) {
        mContext = context;
        if (names != null)
            mNames = new ArrayList<String>(Arrays.asList(names));
        else mNames = new ArrayList<String>();

        if (images != null)
            mImages = new ArrayList<Bitmap>(Arrays.asList(images));
        else mImages = new ArrayList<Bitmap>();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.contact_name.setText(mNames.get(position));
        holder.profile_image.setImageBitmap(mImages.get(position));

        holder.contact_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Position ="+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }
}
