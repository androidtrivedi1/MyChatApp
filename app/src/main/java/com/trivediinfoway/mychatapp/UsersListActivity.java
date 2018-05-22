package com.trivediinfoway.mychatapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trivediinfoway.mychatapp.fcm.FCMService;

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
import java.util.Iterator;
import java.util.List;

public class UsersListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    static ListView listUsers;
    ProgressDialog pd;
    static String receiverTOken = "";
    TextView tvusername;
    Button btnlogout;
    String login_username;
    SwipeRefreshLayout swipe_container;
    Firebase amOnline, userRef;
    boolean flag_online = false;
    FirebaseDatabase database;
    DatabaseReference myConnectionsRef;
    DatabaseReference lastOnlineRef;
    Intent intent;
   // MyResultReceiver resultReceiver;
    DataClass data1;
    CustomAdapter adapter;
    ArrayList<DataClass> result;
    Intent serviceIntent;
    int counter_notificaiotn = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        listUsers = (ListView) findViewById(R.id.listUsers);
        btnlogout = (Button) findViewById(R.id.btnlogout);
        tvusername = (TextView) findViewById(R.id.tvusername);
        swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(UsersListActivity.this);
        swipe_container.setColorScheme(android.R.color.black,
                android.R.color.black,
                android.R.color.black,
                android.R.color.black);

        SharedPreferences settings_username = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
        login_username = settings_username.getString("username", "");
        Log.e("login_username...", login_username + ":login_username");

       /* String a1 = "9";
        String b1 = "5";
        Log.e("Fraction...",Float.parseFloat(a1)/Float.parseFloat(b1)+"=value");*/

        tvusername.setText(login_username + "");
        Log.e("tvusername", login_username + ":::NAME");

        new LoginAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users.json");

        Firebase.setAndroidContext(this);
        database = FirebaseDatabase.getInstance();
        myConnectionsRef = database.getReference("/users/" + login_username + "/connections");
        lastOnlineRef = database.getReference("/users/" + login_username + "/lastOnline");
        //   amOnline = new Firebase("https://rtmchat-370f7.firebaseio.com/.info/connected");
        //  userRef = new Firebase("https://rtmchat-370f7.firebaseio.com/presence/"+login_username);
      /*  result = new ArrayList<DataClass>();
        adapter = new CustomAdapter(result, UsersListActivity.this);//, arraycolor);
        listUsers.setAdapter(adapter);*/
        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //   DatabaseReference con = myConnectionsRef.child(login_username).push();
                        DatabaseReference con = myConnectionsRef.push();
                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();
                    Calendar c = Calendar.getInstance();

                    //      cal.setTimeInMillis(Long.parseLong(String.valueOf(ServerValue.TIMESTAMP)));
                    //   SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy", Locale.US);
                    //  fmt.format(cal.getTime());
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    // when I disconnect, update the last time I was seen online
                 //   lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                       lastOnlineRef.onDisconnect().setValue(formattedDate);

                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });

       /* DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                double estimatedServerTimeMs = System.currentTimeMillis() + offset;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        DatabaseReference connectedRef1 = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                } else {
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });*/
       /* myConnectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map map = snapshot.getValue(Map.class);
                boolean online = map.get("message");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });*/
       /* amOnline.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e("ONLINE....",dataSnapshot.getChildrenCount()+">>>ONLINE");
                } else {
                    Log.e("OFFLINE....",dataSnapshot.getChildrenCount()+">>>OFFLINE");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                SharedPreferences settings = UsersListActivity.this.getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
                                settings.edit().remove("Login_Preference").commit();

                                SharedPreferences settings_username = UsersListActivity.this.getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
                                settings_username.edit().remove("Login_Username").commit();

                                SharedPreferences settings_userProfile = UsersListActivity.this.getSharedPreferences(FirebaseChatMainApp.Login_UserProfile, 0);
                                settings_userProfile.edit().remove("profile_url").commit();

                                Intent intent = new Intent(UsersListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(UsersListActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure want to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
      /*  serviceIntent = new Intent(UsersListActivity.this, AndroidServiceStartOnBoot.class);

        startService(serviceIntent);*/
        /*resultReceiver = new MyResultReceiver(null);

        intent = new Intent(this, MyService.class);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);*/
    }
    /*class UpdateUI implements Runnable
    {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }
        public void run() {
            if(updateString.equals("0")){
                Log.e("status__ON", "ONLINE");
            }
            else {
                Log.e("status__OFF", "OFFLINE");
              //  data1.setFlag_online(false);
            }
            *//*data1 = new DataClass();
            if(updateString.equals("0")){
                data1.setFlag_online(true);
            } else {
                Log.e("status__OFF", "OFFLINE");
                data1.setFlag_online(false);
            }

            adapter.notifyDataSetChanged();*//*
           //     CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.VISIBLE);
          //  else
       //         CustomAdapter.ViewHolder.imgonlinestatus.setVisibility(View.GONE);
            Log.e("updateString",updateString+":::updateString");
           //txtview.setText(updateString);
        }
    }*/

   /* class MyResultReceiver extends ResultReceiver
    {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 100){
                runOnUiThread(new UpdateUI(resultData.getString("start")));
            }
            else if(resultCode == 200){
                runOnUiThread(new UpdateUI(resultData.getString("end")));
            }
            else{
                runOnUiThread(new UpdateUI("Result Received "+resultCode));
            }
        }
    }*/

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(serviceIntent);
    }*/

    @Override
    public void onRefresh() {
       /* if (isNetworkAvailable()) {
            new DownloadXML().execute(URL2);
        } else {
            Toast.makeText(ScoreActivity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
        }*/
        new LoginAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users.json");
        //       Firebase.setAndroidContext(this);
        //      final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //     final DatabaseReference myConnectionsRef = database.getReference("/users/" + login_username + "/connections");
        //    final DatabaseReference lastOnlineRef = database.getReference("/users/" + login_username + "/lastOnline");
        //   amOnline = new Firebase("https://rtmchat-370f7.firebaseio.com/.info/connected");
        //  userRef = new Firebase("https://rtmchat-370f7.firebaseio.com/presence/"+login_username);

       /* final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //   DatabaseReference con = myConnectionsRef.child(login_username).push();
                    DatabaseReference con = myConnectionsRef.push();
                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();
                    //    Calendar cal = Calendar.getInstance();

                    //      cal.setTimeInMillis(Long.parseLong(String.valueOf(ServerValue.TIMESTAMP)));
                    //   SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy", Locale.US);
                    //  fmt.format(cal.getTime());
                   *//* System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());*//*
                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    //    lastOnlineRef.onDisconnect().setValue(formattedDate);

                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });*/
    }

    class LoginAsycTask extends AsyncTask<String, Void, List<DataClass>> {

        @Override
        protected void onPreExecute() {
//            pd = ProgressDialog.show(UsersListActivity.this, "", "Loading...");
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
                        } else {
                            Log.e("status__OFF", "OFFLINE");
                            data.setFlag_online(false);
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

                        if (!val3.equals(login_username)) {
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

            /*if ((pd != null) && pd.isShowing()) {
                pd.dismiss();*/

                swipe_container.setRefreshing(false);

                int[] arraycolor = {getResources().getColor(R.color.color1), getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color3), getResources().getColor(R.color.color4),
                        getResources().getColor(R.color.color5),
                        getResources().getColor(R.color.color6), getResources().getColor(R.color.color7),
                        getResources().getColor(R.color.color8)};

                adapter = new CustomAdapter(result, UsersListActivity.this);//, arraycolor);
                listUsers.setAdapter(adapter);
                listUsers.setSelection(listUsers.getAdapter().getCount() - 1);
                listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                       /* FCMService.notificationManager =
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
                        }*/

                        FCMService.notificationManager =
                                (NotificationManager) UsersListActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                        try {
                            StatusBarNotification[] notifications =
                                    FCMService.notificationManager.getActiveNotifications();

                            Log.e("notifications..Length", notifications.length + "");

                            if (notifications.length > 0) {
                                for (StatusBarNotification notification : notifications) {
                                    if (notification.getNotification().extras.getString("android.title").equals(UserDetails.chatwith)) {
                                        Log.e("GET..ID...Activity", notification.getId() + "");
                                        counter_notificaiotn = counter_notificaiotn+1;
                                        Log.e("Length", counter_notificaiotn + "");
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
                        Intent intent = new Intent(UsersListActivity.this, ChatActivity.class);
                        intent.putExtra("title", result.get(position).getUsername() + "");
                        intent.putExtra("url", result.get(position).getImage_url() + "");
                       // intent.putExtra("online", result.get(position).isFlag_online());
                        Log.e("online>>>>", result.get(position).isFlag_online() + ":Status?????");
                        //      intent.putExtra("last_seen", result.get(position).getLast_seen());
                        Log.e("Images", result.get(position).getImage_url() + "?????");
                        startActivity(intent);
                        //startActivity(new Intent(UsersListActivity.this, ChatActivity.class));
                    }
                });
            }
        }
    }

