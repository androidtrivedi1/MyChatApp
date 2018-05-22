package com.trivediinfoway.mychatapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TI A1 on 17-05-2018.
 */

public class ChatService extends Service {
    public static final long NOTIFY_INTERVAL = 3 * 1000; // 10 seconds  1000*60*60*24
    //static  Activity activity;
    String login_username = "";
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    boolean online = false;
    String chatwithname = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("start...service..", "start...service...");
       /* Bundle bn = intent.getExtras();
        if(bn!=null)
        chatwithname = bn.getString("chatwith");
        Log.e("service", chatwithname + "Chatwith");*/
        return START_STICKY;
    }
    @Override
    public void onCreate() {

        //  activity = (Activity) getApplicationContext();
        Log.e("Create...Service...", "Create...Service...");
        // cancel if already existed
        chatwithname = ChatActivity.chat_with;
        ChatActivity.chatuser.setText(chatwithname+"");
        new UserInfoAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users/"+chatwithname+".json");
        Log.e("URL.....","https://rtmchat-370f7.firebaseio.com/users/"+chatwithname+".json"+":::::::");

       /* if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);*/

    }
    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    Log.e("service111", chatwithname + "Chatwith");
                    // display toast
                  /* Toast.makeText(getApplicationContext(), getDateTime(),
                           Toast.LENGTH_SHORT).show();*/


                    //     Log.e("TOAST.....service..", getDateTime() + "....service");
                  /*  ChatActivity.chatuser.setText(chatwithname+"");
                    new UserInfoAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users/"+chatwithname+".json");
                    Log.e("URL.....","https://rtmchat-370f7.firebaseio.com/users/"+chatwithname+".json"+":::::::");
              */  }

            });
        }
    }
    class UserInfoAsycTask extends AsyncTask<String, Void, Integer> {

        String lastseen = "";
        String username = "";
        boolean typingStarted = false;

        @Override
        protected void onPreExecute() {
          //  pd = ProgressDialog.show(ChatActivity.this, "", "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... voids) {
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
                    if(json.has("lastOnline")) {
                        lastseen = json.getString("lastOnline");
                    }
                    else
                        lastseen = "0";

                    if(json.has("connections"))
                    {
                        online = true;
                    }
                    else
                    {
                        online = false;
                    }
                    if (json.has("username")) {
                        username = json.getString("username");
                        Log.e("Username...",username+"UUUUUUUU");
                    } else {
                        username = "";
                    }

                    if(json.has("typingStatus"))
                    {
                        typingStarted = json.getBoolean("typingStatus");
                    }
                    else
                    {
                        typingStarted = false;
                    }

                } else {

                }

            } catch (Exception e) {
                Log.e("Error...", e.getMessage() + "");
            }
            Log.e("size...", arraylist.size() + "...size");
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if(result==0)
            {
                Calendar c = Calendar.getInstance();

                //      cal.setTimeInMillis(Long.parseLong(String.valueOf(ServerValue.TIMESTAMP)));
                //   SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy", Locale.US);
                //  fmt.format(cal.getTime());
                Log.e("online or not...",online+"::::::");
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
                if(online==false)// && !formattedDate.equals(lastseen))
                    ChatActivity.chatuser.setText(username + "\n"+"Last seen at \n"+lastseen);
                else
                    ChatActivity.chatuser.setText(username + "\n"+"Online");

                if(typingStarted==true)
                {
                    ChatActivity.chatuser.setText(username + "\n" + "Typing...");
                }
                else
                {

                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

}
