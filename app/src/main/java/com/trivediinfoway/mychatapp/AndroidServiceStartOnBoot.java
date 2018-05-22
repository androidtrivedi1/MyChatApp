package com.trivediinfoway.mychatapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.trivediinfoway.mychatapp.fcm.FCMService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TI A1 on 16-05-2018.
 */

public class AndroidServiceStartOnBoot extends Service {
    public static final long NOTIFY_INTERVAL = 3 * 1000; // 10 seconds  1000*60*60*24
    //static  Activity activity;
    String login_username = "";
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    int counter_notificaiotn = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("start...service..", "start...service...");
        FCMService.notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            StatusBarNotification[] notifications =
                    FCMService.notificationManager.getActiveNotifications();

            Log.e("notifications..Length", notifications.length + "");

            if (notifications.length > 0) {
                for (StatusBarNotification notification : notifications) {
                    if (notification.getNotification().extras.getString("android.title").equals(UserDetails.chatwith)) {
                        Log.e("GET..ID...Activity", notification.getId() + "");
                        counter_notificaiotn = counter_notificaiotn + 1;
                        Log.e("Length111", counter_notificaiotn + "");
                        //  FCMService.notificationManager.cancel(notification.getId());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error...", e.getMessage() + "");
        }
        return START_STICKY;

    }

    /*public AndroidServiceStartOnBoot(Context context) {
        super();
        this.context = context;
    }

    public AndroidServiceStartOnBoot() {
        super();
    }*/

    @Override
    public void onCreate() {

        //  activity = (Activity) getApplicationContext();
        Log.e("Create...Service...", "Create...Service...");
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                  /* Toast.makeText(getApplicationContext(), getDateTime(),
                           Toast.LENGTH_SHORT).show();*/


                    //     Log.e("TOAST.....service..", getDateTime() + "....service");

                    new LoginAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users.json");
                }

            });
        }
    }

    class LoginAsycTask extends AsyncTask<String, Void, List<DataClass>> {

        @Override
        protected void onPreExecute() {
            //  pd = ProgressDialog.show(UsersListActivity.this, "", "Loading...");
            super.onPreExecute();
        }

        @Override
        protected List<DataClass> doInBackground(String... voids) {
            ArrayList<DataClass> arraylist = new ArrayList<DataClass>();
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(voids[0]);
            HttpParams httpParameters = httpget.getParams();
            HttpResponse response = null;

            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String s = EntityUtils.toString(entity);
                    Log.e("API response ", s + ">>>API");
                    //     JSONObject obj = new JSONObject(s);

                    JSONObject json = new JSONObject(s);

                    Log.e("size", json.length() + ">>>>>>>>>>>>>>");

                    Iterator i = json.keys();
                    String key = "";
                    DataClass data = null;
                    for (int j = 0; j < json.length(); j++) {
                        if (j == 2) {
                            //    Log.e("connections", json.getJSONArray("connections") + ">>>>>>>>>>>>>>");
                        }
                        key = i.next().toString();
                        Log.e("key", key + ":::key");

                        JSONObject json2 = json.getJSONObject(key);
                        // if(json.getJSONObject("connections").isNull())
                        // if(j==1) {
                        //     Log.e("connections", json2.getJSONObject("connections") + ">>>>>");
                        //  }

                        //      JSONObject json_obj = new JSONObject((Map) json2.getJSONObject("connections"));

                        //  String labelDataString=json2.getString("connections");
                        //   JSONObject labelDataJson= null;
                        //  labelDataJson= new JSONObject(labelDataString);
                        //   if (json2.has("connections")) {
                        //    String status = json.getString("connections");
                        //   optString
                        //     Log.e("status",json2.getJSONObject("connections")+")))))))))))))))");
                        //  }

                        data = new DataClass();
                        if (json2.has("connections")) {
                            Log.e("status__ON", json2.getString("username") + "=");
                            data.setFlag_online(true);
                        /*new UsersListActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.VISIBLE);
                            }
                        });*/
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    CustomAdapter.imgonlinestatus.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            Log.e("status__OFF", "OFFLINE");
                            data.setFlag_online(false);
                       /* new UsersListActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });*/
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    CustomAdapter.imgonlinestatus.setVisibility(View.GONE);
                                }
                            });
                            // CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.GONE);
                        }
                        if (json2.has("lastOnline")) {
                            data.setLast_seen(json2.getString("lastOnline"));
                        } else {
                            data.setLast_seen("0");
                        }

                        String val1 = json2.getString("token");
                        //   String val2 = json2.getString("password");
                        String val3 = json2.getString("username");
                        Log.e("key", val1 + ">>>>>>>");
                        // Log.e("receiver", val2 + ">>>>>>>");
                        Log.e("receiver", val3 + ">>>>>>>");

                /*  SharedPreferences settings_username = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
                   String login_username = settings_username.getString("username", "");
                    Log.e("login_username...", login_username + ":login_username");*/

                  if (!val3.equals("mm")) {
                        data.setUsername(json2.getString("username"));
                        data.setToken(json2.getString("token"));
                        data.setImage_url(json2.getString("profile_image"));
                        data.setLast_seen(json2.getString("lastOnline"));
                        arraylist.add(data);
                    }
                    }
                } else {
                }
            } catch (Exception e) {
                Log.e("Error...", e.getMessage() + "");
            }
            Log.e("size...", arraylist.size() + "...size");
            for (int i = 0; i < arraylist.size(); i++) {
                Log.e("NAME.....", arraylist.get(i).getToken() + "GET>>>>>>>>>>>>>>");
                Log.e("NAME.....", arraylist.get(i).getUsername() + "USER>>>>>>>>>>>>>>");
            }
            return arraylist;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(final List<DataClass> result) {
            super.onPostExecute(result);
            //    if ((pd != null) && pd.isShowing()) {
            //         pd.dismiss();
            /*swipe_container.setRefreshing(false);

            int[] arraycolor = {getResources().getColor(R.color.color1), getResources().getColor(R.color.color2),
                    getResources().getColor(R.color.color3), getResources().getColor(R.color.color4),
                    getResources().getColor(R.color.color5),
                    getResources().getColor(R.color.color6), getResources().getColor(R.color.color7),
                    getResources().getColor(R.color.color8)};
*/
           /* CustomAdapter adapter = new CustomAdapter(result,);//, arraycolor);
        UsersListActivity.listUsers.setAdapter(adapter);
         //   listUsers.setSelection(listUsers.getAdapter().getCount() - 1);
        UsersListActivity.listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                       *//* FCMService.notificationManager =
                                (NotificationManager) UsersListActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                        StatusBarNotification[] notifications =
                                FCMService.notificationManager.getActiveNotifications();

                        Log.e("..................", notifications.length + "");

                        if (notifications.length > 0) {
                            for (StatusBarNotification notification : notifications) {
                                if (notification.getNotification().extras.getString("android.title").equals(result.get(position).getUsername())) {

                                    Log.e("GET..ID..LISt..", notification.getId() + "");
                                    FCMService.notificationManager.cancel(notification.getId());
                                }
                            }
                        }*//*

                    FCMService.notificationManager =
                            (NotificationManager) AndroidServiceStartOnBoot.activity.getSystemService(Context.NOTIFICATION_SERVICE);

                    try {
                        StatusBarNotification[] notifications =
                                FCMService.notificationManager.getActiveNotifications();

                        Log.e("notifications..Length", notifications.length + "");

                        if (notifications.length > 0) {
                            for (StatusBarNotification notification : notifications) {
                                if (notification.getNotification().extras.getString("android.title").equals("ee")) {

                                    Log.e("GET..ID...Activity", notification.getId() + "");
                                    FCMService.notificationManager.cancel(notification.getId());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error...", e.getMessage() + "");
                    }
                    UserDetails.chatwith = result.get(position).getUsername();
                    receiverTOken = result.get(position).getToken();
                    Log.e("UserDetails.chatwith", UserDetails.chatwith + ">>>>>>>");
                    Intent intent = new Intent(AndroidServiceStartOnBoot.activity, ChatActivity.class);
                    intent.putExtra("title", UserDetails.chatwith + "");
                    intent.putExtra("url", result.get(position).getImage_url() + "");
                    intent.putExtra("online", result.get(position).isFlag_online());
                    //      intent.putExtra("last_seen", result.get(position).getLast_seen());
                    Log.e("Iamges", result.get(position).getImage_url() + "?????");
                    startActivity(intent);
                    //startActivity(new Intent(UsersListActivity.this, ChatActivity.class));
                }
            });*/
            //   }
        }
    }
}
