package com.trivediinfoway.mychatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.trivediinfoway.mychatapp.fcm.Config;
import com.trivediinfoway.mychatapp.fcm.FCMService;
import com.trivediinfoway.mychatapp.fcm.FcmNotificationBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class ChatActivity extends AppCompatActivity {

    static Firebase reference1, reference2, reference1_delete;
    ImageView sendButton;
    EmojiconEditText messageArea;
    String message = "";
    static ScrollView scrollView;
    // LinearLayout layout1;
    static TextView chatuser;
    DatabaseReference myRef;
    FirebaseDatabase database;
    ProgressDialog pd;
    String login_username;
    Bundle bn;
    static String image_url = "";
    static String chat_with = "";
    String profile_image = "";
    Intent intent;
    ListView lst;
    String key = "";
    CustomAdaptermsgsDummy adt;
    ArrayList<DataClassMsg> msgs;
    de.hdodenhof.circleimageview.CircleImageView image_receiver;
    ImageView imgsmiley, imgfgallery, imgcamera, imgmore;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private File filePathImageCamera;
    private View contentRoot;
    private EmojIconActions emojIcon;
    String last_seen = "";
    private Handler mHandler = new Handler();
    String profile_url = "";
    boolean online;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rtmchat-370f7.appspot.com");

    String message1;
    String userName;
    String image;
    String latitude;
    String longitude;
    DatabaseReference typingRef;
    boolean typing = false;
    boolean typingStarted;
    private Timer mTimer = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EmojiconEditText) findViewById(R.id.messageArea);
        //  layout1 = (LinearLayout) findViewById(R.id.layout1);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        chatuser = (TextView) findViewById(R.id.chatuser);
        lst = (ListView) findViewById(R.id.lst);
        image_receiver = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.image_receiver);
        imgsmiley = (ImageView) findViewById(R.id.imgsmiley);
        imgfgallery = (ImageView) findViewById(R.id.imgfgallery);
        imgcamera = (ImageView) findViewById(R.id.imgcamera);
        imgmore = (ImageView) findViewById(R.id.imgmore);
        contentRoot = findViewById(R.id.contentRoot);

        emojIcon = new EmojIconActions(this, contentRoot, messageArea, imgsmiley);
        emojIcon.ShowEmojIcon();

        /*pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Loading..");
        pd.show();*/

        SharedPreferences settings_username = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
        login_username = settings_username.getString("username", "");
        Log.e("login_username...", login_username + ":login_username");

        SharedPreferences settings_userProfile = getSharedPreferences(FirebaseChatMainApp.Login_UserProfile, 0);
        profile_url = settings_userProfile.getString("profile_url", "");
        Log.e("profile_url...", profile_url + ":profile_url");

        bn = getIntent().getExtras();
        if (bn != null) {
            chat_with = bn.getString("title");
            image_url = bn.getString("url");
            Log.e("image_url", image_url + "::::::::image_url");
            //     online = bn.getBoolean("online");
            //     last_seen = bn.getString("last_seen");
        }
        chatuser.setText(chat_with + "");
        Log.e("chat_with", chat_with + "::::::::chat_with");

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://rtmchat-370f7.firebaseio.com/messages/" + login_username + "_" + chat_with);
        reference2 = new Firebase("https://rtmchat-370f7.firebaseio.com/messages/" + chat_with + "_" + login_username);
        database = FirebaseDatabase.getInstance();
     //   typingRef = database.getReference("/messages/" + login_username + "_" + chat_with + "/typingStatus");
        typingRef = database.getReference("/users/" + login_username + "/typingStatus");
        msgs = new ArrayList<DataClassMsg>();
        Log.e("UserDetails.chatwith", chat_with + "...");

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new UserInfoAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users/" + chat_with + ".json");
            }
        }, 3000);
*/

       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //your code here
                        new UserInfoAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users/" + chat_with + ".json");
                    }
                }, 3000);
            }
        });*/
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, 2000);
     /*  Timer timer = new CountDownTimer(2000, 2000)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                displayData();
            }
        };
        timer.start();
*/
        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

              //      DatabaseReference con = typingRef.push();

                    messageArea.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //   if (imm.isAcceptingText()) {
                            //  writeToLog("Software Keyboard was shown");
                            /*}
                            else {
                                Log.e("Typing...","Typing...Stop");
                                typingStarted = Boolean.FALSE;
                                typingRef.setValue(typingStarted);
                                typingRef.onDisconnect().setValue(typingStarted);
                                //  writeToLog("Software Keyboard was not shown");
                            }*/
                        }

                        public void afterTextChanged(Editable s) {
                            if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
                                //Log.i(TAG, “typing started event…”);
                                typingStarted = Boolean.TRUE;
                                typingRef.setValue(typingStarted);
                                Log.e("Typing...", "Typing...");
                                //send typing started status
                            } else if (s.toString().trim().length() == 0 && typingStarted) {
                                //Log.i(TAG, “typing stopped event…”);
                                Log.e("Typing...", "Typing...Stop");
                                typingStarted = Boolean.FALSE;
                                typingRef.setValue(typingStarted);
                                typingRef.onDisconnect().setValue(typingStarted);
                                //send typing stopped status
                            }

                           /* else if(!TextUtils.isEmpty(s) && typingStarted)
                            {
                                typingStarted = Boolean.FALSE;
                                typingRef.setValue(typingStarted);
                                typingRef.onDisconnect().setValue(typingStarted);
                            }*/
                        }
                    });
                   /* TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
*/
                    // after you enter 4 characters into the EditText the soft keyboard must hide
                    //     if (charSequence.length()!=0){
                    // HIDE the keyboard
                    //      hideTheKeyboard(ChatActivity.this, messageArea);
                    //           Log.e("Typing...","Typing...Stop");
                              /*  typingStarted = Boolean.FALSE;
                                typingRef.setValue(typingStarted);
                                typingRef.onDisconnect().setValue(typingStarted);*/
                    //  }
                    //     }

                       /* @Override
                        public void afterTextChanged(Editable editable) {
                            //To change body of implemented methods use File | Settings | File Templates.
                            if (!TextUtils.isEmpty(editable.toString()) && editable.toString().trim().length() == 1) {
                                //Log.i(TAG, “typing started event…”);
                                typingStarted = Boolean.TRUE;
                                typingRef.setValue(typingStarted);
                                Log.e("Typing...","Typing...");
                                //send typing started status
                            }
                            else if (editable.toString().trim().length() == 0 && typingStarted) {
                                //Log.i(TAG, “typing stopped event…”);
                                Log.e("Typing...","Typing...Stop");
                                typingStarted = Boolean.FALSE;
                                typingRef.setValue(typingStarted);
                                typingRef.onDisconnect().setValue(typingStarted);
                                //send typing stopped status
                            }
                        }
                    };

                    messageArea.addTextChangedListener(textWatcher);*/
                    //     con.setValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
       /* InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                messageArea.getWindowToken(), 0);
        */

        if (!image_url.equals("")) {
            image_receiver.setEnabled(true);
            Picasso.with(ChatActivity.this).load(image_url).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(image_receiver);
        } else {
            image_receiver.setEnabled(false);
            image_receiver.setImageResource(R.mipmap.ic_launcher);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message = messageArea.getText().toString();
                if (!message.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", message);
                    map.put("user", login_username);
                    map.put("image", "");
                    map.put("map_lat", "");
                    map.put("map_long", "");

                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                    Log.e("UserDetails.profile", UserDetails.profile + "");

                    FcmNotificationBuilder.initialize()
                            .title(login_username)
                            .message(message)
                            .username(login_username)
                            .uid(login_username)
                            .firebaseToken(regId)
                            .url(UserDetails.profile + "")
                            .receiverFirebaseToken(UsersListActivity.receiverTOken)
                            .send();
                }
            }
        });
     /*   Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (pd.isShowing() && pd != null)
                    pd.dismiss();
            }
        }, 3000);
*/
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
            //    typing = dataSnapshot.getKey();
            //    Log.e("typing...", dataSnapshot.getValue(Map.class)  + "VALUE???");

                Map map = dataSnapshot.getValue(Map.class);

                message1 = map.get("message").toString();
                userName = map.get("user").toString();
                image = map.get("image").toString();
                latitude = map.get("map_lat").toString();
                longitude = map.get("map_long").toString();


                key = dataSnapshot.getKey();
                DataClassMsg msg = new DataClassMsg();
                if (userName.equals(login_username)) {
                    msg.setMsg("You:-\n" + message1);
                    msg.setType("1");
                    msg.setKey(dataSnapshot.getKey() + "");
                    msg.setImage(image);
                    msg.setMap_lat(latitude);
                    msg.setMap_long(longitude);
               //     msg.setTypingStatus(typing);
                    //      addMessageBox("You:-\n" + message, 1,dataSnapshot.getKey());

                } else {
                    msg.setMsg(chat_with + ":-\n" + message1);
                    msg.setType("2");
                    msg.setKey(dataSnapshot.getKey() + "");
                    msg.setImage(image);
                    msg.setMap_lat(latitude);
                    msg.setMap_long(longitude);
              //      msg.setTypingStatus(typing);
                    //      addMessageBox(chat_with+":-\n" + message, 2,dataSnapshot.getKey());
                }
                msgs.add(msg);
                adt = new CustomAdaptermsgsDummy(ChatActivity.this, R.layout.right, R.layout.left, msgs);
                adt.updateResults(msgs);
                lst.setAdapter(adt);
                /*lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setMessage("Want to delete this message?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reference1.child(msgs.get(i).getKey() + "").removeValue();
                                        adt.notifyDataSetChanged();
                                //        startActivity(getIntent());
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
                    }
                });*/

                //   pd.dismiss();
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                reference1.child(dataSnapshot.getKey()).removeValue();
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

        });
        image_receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ChatActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image);
                ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                if (!image_url.equals(" "))
                    Picasso.with(ChatActivity.this).load(image_url).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                else
                    image_receiver.setImageResource(R.mipmap.ic_launcher);

                dialog.show();
            }
        });
        imgfgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoGalleryIntent();
            }
        });
        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions();
            }
        });
        imgmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPlacesIntent();
            }
        });

       /* intent = new Intent(ChatActivity.this, ChatService.class);
        //     intent.putExtra("chatwith", chat_with + "");
        startService(intent);*/

        FCMService.notificationManager =
                (NotificationManager) ChatActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            StatusBarNotification[] notifications =
                    FCMService.notificationManager.getActiveNotifications();

            Log.e("notifications..Length", notifications.length + "");

            if (notifications.length > 0) {
                for (StatusBarNotification notification : notifications) {
                    if (notification.getNotification().extras.getString("android.title").equals(chat_with)) {
                        Log.e("GET..ID...Activity", notification.getId() + "");
                        FCMService.notificationManager.cancel(notification.getId());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error...", e.getMessage() + "");
        }
    }

    /*  @Override
      protected void onDestroy() {
          super.onDestroy();
          stopService(new Intent(getApplicationContext(), ChatService.class));
          chat_with = "";
          finish();
      }*/
    public void hideTheKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        typingStarted = Boolean.FALSE;
        typingRef.setValue(typingStarted);
        typingRef.onDisconnect().setValue(typingStarted);
        // Log.e("Typing...","Typing...Stop");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  stopService(new Intent(getApplicationContext(), ChatService.class));
        hideTheKeyboard(ChatActivity.this, messageArea);
       /* typingStarted = Boolean.FALSE;
        typingRef.setValue(typingStarted);
        typingRef.onDisconnect().setValue(typingStarted);*/
        chat_with = "";
        finish();
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    new UserInfoAsycTask().execute("https://rtmchat-370f7.firebaseio.com/users/" + chat_with + ".json");
                }

            });
        }
    }

    private void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
// we already have permission, lets go ahead and call camera intent
            photoCameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted
                    photoCameraIntent();
                }
                break;
        }
    }

    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        Log.e("photoURI", photoURI + "::::::::photoURI");
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, 2);
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideTheKeyboard(ChatActivity.this,messageArea);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == 1) {
            storageRef = storage.getReferenceFromUrl("gs://rtmchat-370f7.appspot.com").child("Images");
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                selectedImage = getResizedBitmap(selectedImage, 400);

                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, getImageUri(ChatActivity.this, selectedImage));
                } else {
//URI IS NULL
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
//IS NULL
                }
            }
        } else if (requestCode == 3) {
            //  if (resultCode == 2) {
            Place place = PlacePicker.getPlace(ChatActivity.this, data);
            if (place != null) {
                LatLng latLng = place.getLatLng();

                Map<String, String> map = new HashMap<String, String>();
                map.put("message", "");
                map.put("user", login_username);
                map.put("image", "");
                map.put("map_lat", latLng.latitude + "");
                map.put("map_long", latLng.longitude + "");
                reference1.push().setValue(map);
                reference2.push().setValue(map);

                Log.e("lat", latLng.latitude + "");

            } else {
//PLACE IS NULL
            }
        }

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void sendFileFirebase(StorageReference storageReference, final File file) {
        if (storageReference != null) {
            Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(photoURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            selectedImage = getResizedBitmap(selectedImage, 400);
            UploadTask uploadTask = storageReference.putFile(getImageUri(ChatActivity.this, selectedImage));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("TAG", "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", "");
                    map.put("user", login_username);
                    map.put("image", downloadUrl + "");
                    map.put("map_lat", "");
                    map.put("map_long", "");
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    Log.e("downloadUrl", downloadUrl + ":LLLLLLLLLLL");
                }
            });
        } else {
//IS NULL
        }

    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("TAG", "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", "");
                    map.put("user", login_username);
                    map.put("image", downloadUrl + "");
                    map.put("map_lat", "");
                    map.put("map_long", "");
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
            });
        } else {
//IS NULL
        }
    }

    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), 3);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.e("ERRRRRR", e.getMessage() + ">>");
        }
    }

    public void addMessageBox(String message, int type, final String key) {

        LayoutInflater inflater = getLayoutInflater();
        if (type == 1) {
            View view = inflater.inflate(R.layout.right, null);
            final EmojiconTextView left = (EmojiconTextView) view.findViewById(R.id.right);
            final ImageView img_chat = (ImageView) view.findViewById(R.id.img_chat);
            final TextView tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            /*if (!message1.equals("")) {
                left.setVisibility(View.VISIBLE);
                left.setText(message1 + "");
                Log.e("message1...>>",message1+"}{}{}{}");
            } else {
                left.setVisibility(View.GONE);
            }

            if (!image.equals("")) {
                left.setVisibility(View.GONE);
                img_chat.setVisibility(View.VISIBLE);
                Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
            } else {
                img_chat.setVisibility(View.GONE);
            }

            if (!latitude.equals("") && !longitude.equals("")) {
                String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=280x280&markers=color:red|" + latitude + "," + longitude;
                img_chat.setVisibility(View.VISIBLE);
                tvLocation.setVisibility(View.VISIBLE);
                left.setVisibility(View.GONE);
                Picasso.with(ChatActivity.this).load(url).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
            } else {
                if (!image.equals("")) {
                    img_chat.setVisibility(View.VISIBLE);
                    tvLocation.setVisibility(View.GONE);
                    left.setVisibility(View.GONE);
                } else {
                    img_chat.setVisibility(View.GONE);
                    tvLocation.setVisibility(View.GONE);
                }
            }
            img_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!image.equals(" ") && latitude.equals("") && longitude.equals("")) {
                        Dialog dialog = new Dialog(ChatActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_image);
                        ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                        Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    else {
                        if(!latitude.equals("") && !longitude.equals(""))
                        {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("http://maps.google.com/?q=" + latitude + "," + longitude));
                            startActivity(i);
                            //  Intent intent = new Intent(activity);
                        }
                        else
                            img_chat.setImageResource(R.mipmap.ic_launcher);
                    }
                }
            });*/
            left.setText(message);
            left.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Want to delete this message?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    reference1.child(key).removeValue();

                                    //   reference1 = null;
                                    //    reference1_delete = new Firebase("https://rtmchat-370f7.firebaseio.com/messages/" + login_username + "_" + chat_with);
                                    /*reference1.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                                            Map map = dataSnapshot.getValue(Map.class);
                                            message1 = map.get("message").toString();
                                            userName = map.get("user").toString();
                                            image = map.get("image").toString();
                                            latitude = map.get("map_lat").toString();
                                            longitude = map.get("map_long").toString();
                                            Log.e("message1...", message1 + "");

                                           // key = dataSnapshot.getKey();
                                            DataClassMsg msg = new DataClassMsg();
                                            if (userName.equals(login_username)) {
                                                msg.setMsg("You:-\n" + message1);
                                                msg.setType("1");
                                                msg.setKey(dataSnapshot.getKey() + "");
                                                msg.setImage(image);
                                                msg.setMap_lat(latitude);
                                                msg.setMap_long(longitude);
                                                addMessageBox("You:-\n" + message1, 1,dataSnapshot.getKey());

                                            } else {
                                                msg.setMsg(chat_with + ":-\n" + message1);
                                                msg.setType("2");
                                                msg.setKey(dataSnapshot.getKey() + "");
                                                msg.setImage(image);
                                                msg.setMap_lat(latitude);
                                                msg.setMap_long(longitude);
                                                addMessageBox(chat_with+":-\n" + message1, 2,dataSnapshot.getKey());
                                            }*/
                                            /*if (message1.equals("")) {
                                                left.setVisibility(View.VISIBLE);
                                                left.setText(message1 + "");
                                            } else {
                                                left.setVisibility(View.GONE);
                                            }

                                            if (!image.equals("")) {
                                                left.setVisibility(View.GONE);
                                                img_chat.setVisibility(View.VISIBLE);
                                                Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
                                            } else {
                                                img_chat.setVisibility(View.GONE);
                                            }
                                            if (latitude.equals("") && !longitude.equals("")) {
                                                String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=280x280&markers=color:red|" + latitude + "," + longitude;
                                                img_chat.setVisibility(View.VISIBLE);
                                                tvLocation.setVisibility(View.VISIBLE);
                                                left.setVisibility(View.GONE);
                                                Picasso.with(ChatActivity.this).load(url).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
                                            } else {
                                                if (!image.equals("")) {
                                                    img_chat.setVisibility(View.VISIBLE);
                                                    tvLocation.setVisibility(View.GONE);
                                                    left.setVisibility(View.GONE);
                                                } else {
                                                    img_chat.setVisibility(View.GONE);
                                                    tvLocation.setVisibility(View.GONE);
                                                }
                                            }
                                            img_chat.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    if (!image.equals(" ") && latitude.equals("") && longitude.equals("")) {
                                                        Dialog dialog = new Dialog(ChatActivity.this);
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.dialog_image);
                                                        ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                                                        Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                                                        dialog.show();
                                                        Window window = dialog.getWindow();
                                                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                                    }
                                                    else {
                                                        if(!latitude.equals("") && !longitude.equals(""))
                                                        {
                                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                                            i.setData(Uri.parse("http://maps.google.com/?q=" + latitude + "," + longitude));
                                                            startActivity(i);
                                                            //  Intent intent = new Intent(activity);
                                                        }
                                                        else
                                                            img_chat.setImageResource(R.mipmap.ic_launcher);
                                                    }
                                                }
                                            });*/
                                       /* }

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
                    //    reference2.child(key).removeValue();
                    return false;
                }
            });

            //   layout1.addView(view);
        } else {
            View view = inflater.inflate(R.layout.left, null);
            final EmojiconTextView left = (EmojiconTextView) view.findViewById(R.id.left);
            // final ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat_l);
            //  final TextView tvLocation = (TextView)view.findViewById(R.id.tvLocation_l);
            left.setText(message);
            /*if (!message1.equals("")) {
                left.setVisibility(View.VISIBLE);
                left.setText(message1 + "");
                Log.e("message1...>>",message1+"}{}{}{}");
            } else {
                left.setVisibility(View.GONE);
            }

            if (!image.equals("")) {
                left.setVisibility(View.GONE);
                img_chat.setVisibility(View.VISIBLE);
                Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
            } else {
                img_chat.setVisibility(View.GONE);
            }

            if (!latitude.equals("") && !longitude.equals("")) {
                String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=280x280&markers=color:red|" + latitude + "," + longitude;
                img_chat.setVisibility(View.VISIBLE);
                tvLocation.setVisibility(View.VISIBLE);
                left.setVisibility(View.GONE);
                Picasso.with(ChatActivity.this).load(url).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(img_chat);
            } else {
                if (!image.equals("")) {
                    img_chat.setVisibility(View.VISIBLE);
                    tvLocation.setVisibility(View.GONE);
                    left.setVisibility(View.GONE);
                } else {
                    img_chat.setVisibility(View.GONE);
                    tvLocation.setVisibility(View.GONE);
                }
            }
            img_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!image.equals(" ") && latitude.equals("") && longitude.equals("")) {
                        Dialog dialog = new Dialog(ChatActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_image);
                        ImageView imgbigdp = (ImageView) dialog.findViewById(R.id.imgbigdp);
                        Picasso.with(ChatActivity.this).load(image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(imgbigdp);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    else {
                        if(!latitude.equals("") && !longitude.equals(""))
                        {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("http://maps.google.com/?q=" + latitude + "," + longitude));
                            startActivity(i);
                            //  Intent intent = new Intent(activity);
                        }
                        else
                            img_chat.setImageResource(R.mipmap.ic_launcher);
                    }
                }
            });*/
            left.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Want to delete this message?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    reference1.child(key).removeValue();
                                    //   reference1_delete = new Firebase("https://rtmchat-370f7.firebaseio.com/messages/" + login_username + "_" + chat_with);
                                    /*reference1.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                                            Map map = dataSnapshot.getValue(Map.class);
                                            message1 = map.get("message").toString();
                                            userName = map.get("user").toString();
                                            image = map.get("image").toString();
                                            latitude = map.get("map_lat").toString();
                                            longitude = map.get("map_long").toString();
                                            Log.e("image...", image + "");

                                            // key = dataSnapshot.getKey();
                                            DataClassMsg msg = new DataClassMsg();
                                            if (userName.equals(login_username)) {
                                                msg.setMsg("You:-\n" + message1);
                                                msg.setType("1");
                                                msg.setKey(dataSnapshot.getKey() + "");
                                                msg.setImage(image);
                                                msg.setMap_lat(latitude);
                                                msg.setMap_long(longitude);
                                                addMessageBox("You:-\n" + message1, 1,dataSnapshot.getKey());
                                            } else {
                                                msg.setMsg(chat_with + ":-\n" + message1);
                                                msg.setType("2");
                                                msg.setKey(dataSnapshot.getKey() + "");
                                                msg.setImage(image);
                                                msg.setMap_lat(latitude);
                                                msg.setMap_long(longitude);
                                                addMessageBox(chat_with+":-\n" + message1, 2,dataSnapshot.getKey());
                                            }
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

            //   layout1.addView(view);
        }

       /* scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });*/
        //  scrollView.fullScroll(View.FOCUS_DOWN);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (pd.isShowing() && pd != null)
            pd.dismiss();
*/
        FirebaseChatMainApp.setChatActivityOpen(false);
    }

    class UserInfoAsycTask extends AsyncTask<String, Void, Integer> {

        String lastseen = "";
        String username = "";


        @Override
        protected void onPreExecute() {
            //      pd = ProgressDialog.show(ChatActivity.this, "", "Loading...");
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
                    if (json.has("lastOnline")) {
                        lastseen = json.getString("lastOnline");
                    } else
                        lastseen = "0";

                    if (json.has("connections")) {
                        online = true;
                    } else {
                        online = false;
                    }
                    if (json.has("username")) {
                        username = json.getString("username");
                    } else {
                        username = "";
                    }
                    if (json.has("profile_image")) {
                        profile_image = json.getString("profile_image");
                    } else {
                        profile_image = "";
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
           /* if ((pd != null) && pd.isShowing())
                pd.dismiss();*/

            if (result == 0) {

                if (!profile_image.equals(""))
                    Picasso.with(ChatActivity.this).load(profile_image).fit().centerInside().placeholder(getResources().getDrawable(R.mipmap.ic_launcher)).error(getResources().getDrawable(R.mipmap.ic_launcher)).into(image_receiver);

                Calendar c = Calendar.getInstance();

                //      cal.setTimeInMillis(Long.parseLong(String.valueOf(ServerValue.TIMESTAMP)));
                //   SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy", Locale.US);
                //  fmt.format(cal.getTime());
                Log.e("online or not...", online + "::::::");
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
                if(online == false)// && !formattedDate.equals(lastseen))
                    chatuser.setText(chat_with + "\n" + "Last seen at \n" + lastseen);
                else
                    chatuser.setText(chat_with + "\n" + "Online");


                if (typingStarted == true) {
                    chatuser.setText(chat_with + "\n" + "Typing...");
                } else {

                }
            }
        }
    }
}