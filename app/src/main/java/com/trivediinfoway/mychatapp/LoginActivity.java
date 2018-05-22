package com.trivediinfoway.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    AutoCompleteTextView autousernamelogin;
    AutoCompleteTextView autopasswordlogin;
    TextView tvloginmain;
    String username = "", password = "";
    ProgressDialog pd;
    boolean flag_login = false;
    String username_new = "";
    String profile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autousernamelogin = (AutoCompleteTextView) findViewById(R.id.autousernamelogin);
        autopasswordlogin = (AutoCompleteTextView) findViewById(R.id.autopasswordlogin);

        tvloginmain = (TextView) findViewById(R.id.tvloginmain);
        tvloginmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = autousernamelogin.getText().toString();
                password = autopasswordlogin.getText().toString();

                String url = "https://rtmchat-370f7.firebaseio.com/users.json";
                new LoginAsycTask().execute(url);
            }
        });
        SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
        final boolean login_preference = settings.getBoolean("Login_Preference", false);
        Log.e("login_preference...", login_preference + ":Value");

    }

    class LoginAsycTask extends AsyncTask<String, Void, List<DataClassLogin>> {
        ArrayList<DataClassLogin> arraylist = new ArrayList<DataClassLogin>();

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(LoginActivity.this, "", "Loading...");
            super.onPreExecute();
        }

        @Override
        protected List<DataClassLogin> doInBackground(String... voids) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(voids[0]);
            HttpParams httpParameters = httpget.getParams();
            HttpResponse response = null;

            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String s = EntityUtils.toString(entity);
                    Log.e(": API response ", s + "");
                    //     JSONObject obj = new JSONObject(s);

                    JSONObject json = new JSONObject(s);

                    Iterator i = json.keys();
                    String key = "";

                   /* while(i.hasNext()){
                        key = i.next().toString();

                        if(!key.equals("email")) {
                            arraylist.add(key);
                        }
                    }*/

                    for (int j = 0; j < json.length(); j++) {
                        key = i.next().toString();
                        Log.e("key", key + ":::key");
                        JSONObject json2 = json.getJSONObject(key);
                        String val1 = json2.getString("email");
                        Log.e("Val1",val1+"HHHHHHHHHH");
                        DataClassLogin data = new DataClassLogin();
                        data.setEmail(val1);
                        data.setPassword(json2.getString("password"));
                        data.setProfile_image(json2.getString("profile_image"));
                        data.setToken(json2.getString("token"));
                        data.setUsername(json2.getString("username"));

                        arraylist.add(data);
                    }

                } else {

                }

            } catch (Exception e) {
                Log.e("Error...", e.getMessage() + "");
            }
            Log.e("size...", arraylist.size() + "...size");
            return arraylist;
        }

        @Override
        protected void onPostExecute(List<DataClassLogin> result) {
            super.onPostExecute(result);
            if ((pd != null) && pd.isShowing()) {
                pd.dismiss();

                if(result.size()>0)
                {
                    for(int i = 0;i<result.size();i++)
                    {
                        if(result.get(i).getEmail().equals(autousernamelogin.getText().toString())&& result.get(i).getPassword().equals(autopasswordlogin.getText().toString()))
                        {
                            username_new = result.get(i).getUsername();
                            profile = result.get(i).getProfile_image();
                            flag_login = true;
                            break;
                        }
                        else
                        {
                            flag_login = false;
                       //     Toast.makeText(LoginActivity.this,"Email or password incorrect.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if(flag_login==true)
                {
                    SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Login_Preference", true);
                    editor.commit();

                    SharedPreferences settings_U = getSharedPreferences(FirebaseChatMainApp.Login_Username, 0);
                    SharedPreferences.Editor editor_U = settings_U.edit();
                    editor_U.putString("username", username_new+"");
                    editor_U.commit();

                    SharedPreferences settings_P = getSharedPreferences(FirebaseChatMainApp.Login_UserProfile, 0);
                    SharedPreferences.Editor editor_P = settings_P.edit();
                    editor_P.putString("profile_url", profile+"");
                    editor_P.commit();

                    UserDetails.username = username_new;
                    UserDetails.password = password;
                    UserDetails.profile = profile;
                    Intent intent = new Intent(LoginActivity.this, UsersListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Email or password incorrect.",Toast.LENGTH_SHORT).show();
                }

              //  if (result.contains(autousernamelogin.getText().toString())) {
/*
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword("aa@gmail.com", password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("TAG", "performFirebaseLogin:onComplete:" + task.isSuccessful());

                                    Log.e("NEWWWWWW",task.getResult().getUser().getUid()+">>>>>ID");*/
// If sign in fails, display a message to the user. If sign in succeeds
// the auth state listener will be notified and logic to handle the
// signed in user can be handled in the listener.
                                   /* if (task.isSuccessful()) {
                                        mOnLoginListener.onSuccess(task.getResult().toString());
                                        updateFirebaseToken(task.getResult().getUser().getUid(),
                                                new SharedPrefUtil(activity.getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));
                                    } else {
                                        mOnLoginListener.onFailure(task.getException().getMessage());
                                    }*/
                    //          }
                    //     });
/*
                    SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Login_Preference", true);
                    editor.commit();

                    UserDetails.username = username;
                    UserDetails.password = password;
                    Intent intent = new Intent(LoginActivity.this, UsersListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    return;
                }*/
            }
        }
    }
}
