package com.trivediinfoway.mychatapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by TI A1 on 09-05-2018.
 */

public class CustomAdapter extends BaseAdapter {

    List<DataClass> arraDetails;
    LayoutInflater inflater;
    Activity activity;
    int[] colors;
    static public ImageView imgonlinestatus;

    public CustomAdapter(List<DataClass> arraDetails, Activity activity) {
        this.arraDetails = arraDetails;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CustomAdapter(List<DataClass> arraDetails, Activity activity, int[] colors) {
        this.arraDetails = arraDetails;
        this.activity = activity;
        this.colors = colors;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arraDetails.size();
    }

    @Override
    public Object getItem(int i) {
        return arraDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder {
        public TextView tvreceivername;
        public de.hdodenhof.circleimageview.CircleImageView image_listing;
        public RelativeLayout ll1;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.chat_row, null);
            imgonlinestatus = (ImageView) view.findViewById(R.id.imgonlinestatus);
            holder.tvreceivername = (TextView) view.findViewById(R.id.tvreceivername);
            holder.image_listing = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.image_listing);
            holder.ll1 = (RelativeLayout) view.findViewById(R.id.ll1);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        final DataClass data = arraDetails.get(i);
        holder.tvreceivername.setText(data.getUsername() + "");
        //    holder.ll1.setBackgroundColor(colors[i]);
        Log.e("data.getChatwith()", data.getUsername() + "<<<<<<<<<");

        if (!data.getImage_url().equals(" ")) {
            holder.image_listing.setEnabled(true);
            Picasso.with(activity).load(data.getImage_url()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.image_listing);
        } else {
            holder.image_listing.setEnabled(false);
            holder.image_listing.setImageResource(R.mipmap.ic_launcher);
            //Picasso.with(activity).load(data.getImage_url()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.image_listing);
        }

        holder.image_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image);
                ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                if (!data.getImage_url().equals(" "))
                    Picasso.with(activity).load(data.getImage_url()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                else
                    holder.image_listing.setImageResource(R.mipmap.ic_launcher);

                dialog.show();
            }
        });
        if (data.isFlag_online() == true) {
            imgonlinestatus.setVisibility(View.VISIBLE);
        }
        else
            imgonlinestatus.setVisibility(View.GONE);

        return view;
    }
}

