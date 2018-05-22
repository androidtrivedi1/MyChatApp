package com.trivediinfoway.mychatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Admin on 11-05-2018.
 */

class CustomAdaptermsgs extends BaseAdapter {
    ArrayList<DataClassMsg> arraDetails;
    Activity activity;
    LayoutInflater inflater;
    String key = "";

    public void updateResults(ArrayList<DataClassMsg> arraDetails) {
        this.arraDetails = arraDetails;
        //Triggers the list update
        notifyDataSetChanged();
    }
    public CustomAdaptermsgs(Activity activity, int design_chat_msg, int item_message_left, ArrayList<DataClassMsg> arraDetails) {
        this.arraDetails = arraDetails;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

        public EmojiconTextView tvreceivername;
        public ImageView img_chat;
        public TextView tvLocation;
        public me.himanshusoni.chatmessageview.ChatMessageView contentMessageChat;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        final DataClassMsg data = arraDetails.get(i);

        if (view == null) {
            holder = new ViewHolder();

            if (data.getType().equals("1")) {
                Log.e("TYPR.....", data.getType() + "...111");
                view = inflater.inflate(R.layout.right, null);
                holder.tvreceivername = (EmojiconTextView) view.findViewById(R.id.right);

            } else {
                Log.e("TYPR.....", data.getType() + "...222");
                view = inflater.inflate(R.layout.left, null);
                holder.tvreceivername = (EmojiconTextView) view.findViewById(R.id.left);
            }

            holder.img_chat = (ImageView) view.findViewById(R.id.img_chat);
            holder.tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            holder.contentMessageChat = (me.himanshusoni.chatmessageview.ChatMessageView) view.findViewById(R.id.contentMessageChat);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        if (!data.getMsg().equals("")) {
            holder.tvreceivername.setVisibility(View.VISIBLE);
            holder.tvreceivername.setText(data.getMsg() + "");
        } else {
            holder.tvreceivername.setVisibility(View.GONE);
        }

        if (!data.getImage().equals("")) {
            holder.tvreceivername.setVisibility(View.GONE);
            holder.img_chat.setVisibility(View.VISIBLE);
            Picasso.with(activity).load(data.getImage()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat);
        } else {
            holder.img_chat.setVisibility(View.GONE);
        }

        if (!data.getMap_lat().equals("") && !data.getMap_long().equals("")) {
            String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + data.map_lat + "," + data.map_long + "&zoom=18&size=280x280&markers=color:red|" + data.map_lat + "," + data.map_long;
            holder.img_chat.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvreceivername.setVisibility(View.GONE);
            Picasso.with(activity).load(url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat);
        } else {
            if (!data.getImage().equals("")) {
                holder.img_chat.setVisibility(View.VISIBLE);
                holder.tvLocation.setVisibility(View.GONE);
                holder.tvreceivername.setVisibility(View.GONE);
            } else {
                holder.img_chat.setVisibility(View.GONE);
                holder.tvLocation.setVisibility(View.GONE);
            }
        }

        Log.e("data.getImage()", data.getImage() + "<<<<<<<<<");

        holder.tvreceivername.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Want to delete this message?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.e("TYPE...",data.getType()+"::::::::");

                           //    Firebase reference1 = new Firebase("https://rtmchat-370f7.firebaseio.com/messages/" + "rr" + "_" + "shree");
                                ChatActivity.reference1.child(data.getKey() + "").removeValue();

                          //      arraDetails = new ArrayList<DataClassMsg>();
                               // notifyDataSetChanged();
                      //          String pos = view.getTag().toString();
                       //         int _posicion = Integer.parseInt(pos);
                                arraDetails.remove(i);
                                notifyDataSetChanged();
                       //         updateResults(arraDetails);
                                /*reference1.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                                        Map map = dataSnapshot.getValue(Map.class);
                                        String message = map.get("message").toString();
                                        String userName = map.get("user").toString();
                                        String image = map.get("image").toString();
                                        String latitude = map.get("map_lat").toString();
                                        String longitude = map.get("map_long").toString();
                                        Log.e("image...", image + "");

                                        key = dataSnapshot.getKey();
                                        DataClassMsg msg = new DataClassMsg();
                                        if (userName.equals("rr")) {
                                            msg.setMsg("You:-\n" + message);
                                            msg.setType("1");
                                            msg.setKey(dataSnapshot.getKey() + "");
                                            msg.setImage(image);
                                            msg.setMap_lat(latitude);
                                            msg.setMap_long(longitude);

                                        } else {
                                            msg.setMsg("shree" + ":-\n" + message);
                                            msg.setType("2");
                                            msg.setKey(dataSnapshot.getKey() + "");
                                            msg.setImage(image);
                                            msg.setMap_lat(latitude);
                                            msg.setMap_long(longitude);
                                        }
                                        arraDetails.add(msg);
                                      notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onChildRemoved(final DataSnapshot dataSnapshot) {
                                        Log.e("Removed...", dataSnapshot.getKey() + "");
                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }

                                });*/
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }
        });
        holder.img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!data.getImage().equals(" ") && data.getMap_lat().equals("") && data.getMap_long().equals("")) {
                    Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_image);
                    ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                    Picasso.with(activity).load(data.getImage()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                }
                else {
                    if(!data.getMap_lat().equals("") && !data.getMap_long().equals(""))
                    {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://maps.google.com/?q=" + data.map_lat + "," + data.map_long));
                        activity.startActivity(i);
                      //  Intent intent = new Intent(activity);
                    }
                    else
                    holder.img_chat.setImageResource(R.mipmap.ic_launcher);
                }
            }
        });
        return view;
    }
}
