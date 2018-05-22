package com.trivediinfoway.mychatapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TI A1 on 16-05-2018.
 */

public class MyService extends Service {
    Timer timer = new Timer();
    MyTimerTask timerTask;
    ResultReceiver resultReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        resultReceiver = intent.getParcelableExtra("receiver");

        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Bundle bundle = new Bundle();
        bundle.putString("end", "Timer Stopped....");
        resultReceiver.send(200, bundle);
    }


    class MyTimerTask extends TimerTask {
        public MyTimerTask() {
            Bundle bundle = new Bundle();
            bundle.putString("start", "Timer Started....");
            resultReceiver.send(100, bundle);
        }

        @Override
        public void run() {
            new LoginAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users.json");
            // SimpleDateFormat dateFormat = new SimpleDateFormat("s");
            // resultReceiver.send(Integer.parseInt(dateFormat.format(System.currentTimeMillis())), null);
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
                            resultReceiver.send(1, null);
                         //   CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("status__OFF", "OFFLINE");
                            data.setFlag_online(false);
                            resultReceiver.send(0, null);
                        //    CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.GONE);
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
                    Log.e("login_username...", login_username + ":login_username");
*/
                  /*  if (!val3.equals("rr")) {
                        data.setUsername(json2.getString("username"));
                        data.setToken(json2.getString("token"));
                        data.setImage_url(json2.getString("profile_image"));
                        data.setLast_seen(json2.getString("lastOnline"));
                        arraylist.add(data);
                    }*/
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

        }
    }
}

