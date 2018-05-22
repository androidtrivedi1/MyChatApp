package com.trivediinfoway.mychatapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.trivediinfoway.mychatapp.fcm.Config;
import com.trivediinfoway.mychatapp.fcm.NotificationUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView autousername, autopwd, autoemail;
    TextView tvcreateaccount, txtLogin, txtlo;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String username = "", password = "", email = "";
    String token = "";
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    private Uri filePath = null;
    private final int PICK_IMAGE_REQUEST = 71;
    private DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rtmchat-370f7.appspot.com");    //change the url according to your firebase app
    static String profile_url = "";
    ArrayList<String> list_email_check;
    boolean flag_email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        tvcreateaccount = (TextView) findViewById(R.id.tvcreateaccount);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtlo = (TextView) findViewById(R.id.txtlo);

        autousername = (AutoCompleteTextView) findViewById(R.id.autousername);
        autopwd = (AutoCompleteTextView) findViewById(R.id.autopwd);
        autoemail = (AutoCompleteTextView) findViewById(R.id.autoemail);

        //  btnsubmit.setEnabled(false);

        profile_image = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //  txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        final DatabaseReference mdata = FirebaseDatabase.getInstance().getReference("users");
      /*  mdata.addChildEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Child...",dataSnapshot.getKey()+">>>>>?>?>?>?");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        list_email_check = new ArrayList<String>();
        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                Log.i("Child...", dataSnapshot.child("email").getValue() + ">>>>>?>?>?>?");
                list_email_check.add(dataSnapshot.child("email").getValue() + "");
                //  Message message = dataSnapshot.getValue("email");
                //    messageList.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mdata.addChildEventListener(childEventListener);
        //DatabaseReference reference = new DatabaseReference("https://rtmchat-370f7.firebaseio.com/users.json");
        // String alphabet = username.substring(0, 1);
        /*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference demoRef = rootRef.child("users");
        Log.e("Value",demoRef.getKey()+">>>>>>>>>>>>>>>>>>>>>>>>");
        Log.e("Value",demoRef.child("email").getKey()+">>>>>>>>>>>>>>>>>>>>>>>>");
        demoRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
               Log.e("Value",value+"asdasdasdasd");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autousername.getText().toString().trim().equals("")) {
                    if (!autoemail.getText().toString().trim().equals("")) {
                        chooseImage();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter email.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter username.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvcreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = autousername.getText().toString();
                password = autopwd.getText().toString();
                email = autoemail.getText().toString();

                String url = "https://rtmchat-370f7.firebaseio.com/users.json";

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                token = pref.getString("regId", null);

                if (!username.trim().equals("") && !password.trim().equals("") && !token.trim().equals("") && filePath!=null && !email.trim().equals("")) {

                    for (int i = 0; i < list_email_check.size(); i++) {
                        if (list_email_check.get(i).equals(autoemail.getText().toString())) {
                            flag_email = true;
                            break;
                        } else
                            flag_email = false;
                    }
                    if (flag_email == false) {
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("Loading...");
                        progressDialog.show();
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    Log.e("postSnapshot", postSnapshot.getValue() + "");
                                    Log.e("Image_", "Image_" + autousername.getText().toString() + "_" + autoemail.getText().toString().replace("@", "_") + "");

                                    Log.e("Value", mDatabase.getKey() + ">>>>>>>>>>>>>>>>>>>>>>>>");

                                    if (postSnapshot.getValue().toString().contains("Image_" + autousername.getText().toString() + "_" + autoemail.getText().toString().replace("@", "_"))) {
                                        profile_url = postSnapshot.getValue() + "";

                                        Log.e("profile_url>>>>>>", profile_url + "=>>>>>>>>>>>");
                                        Firebase reference = new Firebase("https://rtmchat-370f7.firebaseio.com/users");

                                        Log.e("profile_url...", profile_url + "=PPPPPPPPPP");

                                        reference.child(username).child("username").setValue(username);
                                        reference.child(username).child("password").setValue(password);
                                        reference.child(username).child("token").setValue(token);
                                        reference.child(username).child("profile_image").setValue(profile_url);
                                        reference.child(username).child("email").setValue(email);

                                        SharedPreferences settings_username = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
                                        SharedPreferences.Editor editor_username = settings_username.edit();
                                        editor_username.putString("username", username);
                                        editor_username.commit();

                                        SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putBoolean("Login_Preference", true);
                                        editor.commit();

                                        SharedPreferences settings_Profile = getSharedPreferences(FirebaseChatMainApp.Login_UserProfile, 0);
                                        SharedPreferences.Editor editor_profile = settings_Profile.edit();
                                        editor_profile.putString("profile_url", profile_url+"");
                                        editor_profile.commit();

                                        Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressDialog.dismiss();

                                    } else {
                                        //      Toast.makeText(MainActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                                        //       progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });

                    } else {
                        ChildEventListener childEventListener = new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                // A new data item has been added, add it to the list
                                Log.i("Images...", dataSnapshot.getChildrenCount() + ">>>>>?>?>?>?");

                        //        list_email_check.add(dataSnapshot.child("email").getValue() + "");
                                //  Message message = dataSnapshot.getValue("email");
                                //    messageList.add(message);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        filePath = null;
                        autoemail.setText("");
                        profile_image.setImageResource(R.drawable.user);
                        mDatabase.addChildEventListener(childEventListener);
                        Toast.makeText(MainActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter all fields.", Toast.LENGTH_LONG).show();
                }
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //   btnsubmit.setEnabled(false);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
                Log.e("bitmap", filePath + ":bitmap");

                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Loading...");
                progressDialog.show();

                StorageReference sRef = storageRef.child("Image_" + autousername.getText().toString() + "_" + autoemail.getText().toString().replace("@", "_") + "." + getFileExtension(filePath));
                sRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //dismissing the progress dialog
                                progressDialog.dismiss();
                                String new_url = "Image_" + autousername.getText().toString() + "." + getFileExtension(filePath);
                                Log.e("FILEEEEEE", "Image_" + autousername.getText().toString() + "_" + autoemail.getText().toString().replace("@", "_") + "." + getFileExtension(filePath) + ":::::::::::");
                                //displaying success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                //   btnsubmit.setEnabled(true);
                                String uploadId = mDatabase.push().getKey();
                                mDatabase.child(uploadId).setValue(taskSnapshot.getDownloadUrl().toString());

                               /* Firebase reference = new Firebase("https://rtmchat-370f7.firebaseio.com/users");
                                // String alphabet = username.substring(0, 1);
                                Log.e("profile_url...",profile_url+"=PPPPPPPPPP");
                                reference.child(username).child("username").setValue(username);
                                reference.child(username).child("password").setValue(password);
                                reference.child(username).child("token").setValue(token);
                                reference.child(username).child("profile_image").setValue(new_url);

                                SharedPreferences settings_username = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
                                SharedPreferences.Editor editor_username = settings_username.edit();
                                editor_username.putString("username", username);
                                editor_username.commit();

                                SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("Login_Preference", true);
                                editor.commit();

                                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                                startActivity(intent);
                                finish();*/

                                //creating the upload object to store uploaded image details
                                //      Upload upload = new Upload(editTextName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                                //adding an upload to firebase database
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //displaying the upload progress
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //      progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayFirebaseRegId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("MainActivity", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //  txtRegId.setText("Firebase Reg Id: " + regId);
            Log.e("Firebase Reg Id: ", regId);
        } else {
            // txtRegId.setText("Firebase Reg Id is not received yet!");
            Log.e("Firebase Reg Id not: ", "Firebase Reg Id is not received yet!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
