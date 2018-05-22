package com.trivediinfoway.mychatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trivediinfoway.mychatapp.fcm.FCMService;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by Admin on 11-05-2018.
 */

class CustomAdaptermsgsDummy extends BaseAdapter {
    ArrayList<DataClassMsg> arraDetails;
    Activity activity;
    LayoutInflater inflater;
    String key = "";

    public void updateResults(ArrayList<DataClassMsg> arraDetails) {
        this.arraDetails = arraDetails;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public CustomAdaptermsgsDummy(Activity activity, int design_chat_msg, int item_message_left, ArrayList<DataClassMsg> arraDetails) {
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

        public EmojiconTextView l1;
        public EmojiconTextView r1;
        ImageView img_chat, img_chat_r;
        TextView tvLocation, tvLocation_r;
        ChatMessageView contentMessageChat, contentMessageChat1;
     //   CircleImageView image_receiver_small;
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

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.dummy, null);

            holder.l1 = (EmojiconTextView) view.findViewById(R.id.left1);
            holder.r1 = (EmojiconTextView) view.findViewById(R.id.right1);
            holder.img_chat = (ImageView) view.findViewById(R.id.img_chat);
            holder.img_chat_r = (ImageView) view.findViewById(R.id.img_chat_r);
            holder.tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            holder.tvLocation_r = (TextView) view.findViewById(R.id.tvLocation_r);
        //    holder.image_receiver_small = (CircleImageView) view.findViewById(R.id.image_receiver_small);
            holder.contentMessageChat = (ChatMessageView) view.findViewById(R.id.contentMessageChat);
            holder.contentMessageChat1 = (ChatMessageView) view.findViewById(R.id.contentMessageChat1);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        final DataClassMsg data = arraDetails.get(i);
        Log.e("1", FCMService.url + ">>>>>>>>");
        //  if(FCMService.url.equals(""))
        //   else
        //      Picasso.with(activity).load(FCMService.url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.image_receiver_small);
        if (data.getType() == "2") {
            //    holder.l1.setText(data.getMsg() + "");
            holder.r1.setVisibility(View.GONE);
            holder.l1.setVisibility(View.VISIBLE);
            holder.img_chat_r.setVisibility(View.GONE);
            holder.img_chat.setVisibility(View.VISIBLE);
            holder.contentMessageChat.setVisibility(View.VISIBLE);
            holder.contentMessageChat1.setVisibility(View.GONE);
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvLocation_r.setVisibility(View.GONE);
      //      holder.image_receiver_small.setVisibility(View.VISIBLE);
     //      if (FCMService.url==null)
     //           Picasso.with(activity).load(ChatActivity.image_url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.image_receiver_small);
     //      else
     //           Picasso.with(activity).load(FCMService.url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.image_receiver_small);
           if (!data.getMsg().equals("")) {
                //    holder.l1.setVisibility(View.VISIBLE);
                holder.l1.setText(data.getMsg() + "");
            } else {
                holder.l1.setVisibility(View.GONE);
            }

            if (!data.getImage().equals("")) {
                holder.l1.setVisibility(View.GONE);
                holder.img_chat.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(data.getImage()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat);
            } else {
                holder.img_chat.setVisibility(View.GONE);
            }
            if (!data.getMap_lat().equals("") && !data.getMap_long().equals("")) {
                String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + data.map_lat + "," + data.map_long + "&zoom=18&size=280x280&markers=color:red|" + data.map_lat + "," + data.map_long;
                holder.img_chat.setVisibility(View.VISIBLE);
                holder.tvLocation.setVisibility(View.VISIBLE);
                holder.l1.setVisibility(View.GONE);
                Picasso.with(activity).load(url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat);
            } else {
                if (!data.getImage().equals("")) {
                    holder.img_chat.setVisibility(View.VISIBLE);
                    holder.tvLocation.setVisibility(View.GONE);
                    holder.l1.setVisibility(View.GONE);
                } else {
                    holder.img_chat.setVisibility(View.GONE);
                    holder.tvLocation.setVisibility(View.GONE);
                }
            }
        } else {
            //  holder.r1.setText(data.getMsg() + "");
            holder.l1.setVisibility(View.GONE);
            holder.r1.setVisibility(View.VISIBLE);
            holder.img_chat_r.setVisibility(View.VISIBLE);
            holder.img_chat.setVisibility(View.GONE);
            holder.contentMessageChat.setVisibility(View.GONE);
            holder.contentMessageChat1.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.GONE);
            holder.tvLocation_r.setVisibility(View.VISIBLE);
          //  holder.image_receiver_small.setVisibility(View.GONE);
            if (!data.getMsg().equals("")) {
                //      holder.r1.setVisibility(View.VISIBLE);
                holder.r1.setText(data.getMsg() + "");
            } else {
                holder.r1.setVisibility(View.GONE);
            }

            if (!data.getImage().equals("")) {
                holder.r1.setVisibility(View.GONE);
                holder.img_chat_r.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(data.getImage()).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat_r);
            } else {
                holder.img_chat_r.setVisibility(View.GONE);
            }
            if (!data.getMap_lat().equals("") && !data.getMap_long().equals("")) {
                String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + data.map_lat + "," + data.map_long + "&zoom=18&size=280x280&markers=color:red|" + data.map_lat + "," + data.map_long;
                holder.img_chat_r.setVisibility(View.VISIBLE);
                holder.tvLocation_r.setVisibility(View.VISIBLE);
                holder.r1.setVisibility(View.GONE);
                Picasso.with(activity).load(url).fit().centerInside().placeholder(activity.getResources().getDrawable(R.mipmap.ic_launcher)).error(activity.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.img_chat_r);
            } else {
                if (!data.getImage().equals("")) {
                    holder.img_chat_r.setVisibility(View.VISIBLE);
                    holder.tvLocation_r.setVisibility(View.GONE);
                    holder.r1.setVisibility(View.GONE);
                } else {
                    holder.img_chat_r.setVisibility(View.GONE);
                    holder.tvLocation_r.setVisibility(View.GONE);
                }
            }
        }
        /*
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
        }*/

        Log.e("data.getImage()", data.getImage() + "<<<<<<<<<");

        holder.l1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Want to delete this message?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.e("TYPE...", data.getType() + "::::::::");

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
        holder.r1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Want to delete this message?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.e("TYPE...", data.getType() + "::::::::");

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
        /*holder.img_chat.setOnClickListener(new View.OnClickListener() {
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
        });*/
        return view;
    }
}
