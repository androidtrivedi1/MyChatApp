package com.trivediinfoway.mychatapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    ImageView imgView;
    private static final int SPLASH_DISPLAY_TIME = 1000;
    static boolean flag_logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences settings = getSharedPreferences(FirebaseChatMainApp.Login_PREFS, 0);
        final boolean login_preference = settings.getBoolean("Login_Preference", false);
        Log.e("login_preference...",login_preference+":Value");

        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (login_preference == true) {

                    try {
                        Intent mainIntent = new Intent(SplashActivity.this,
                                UsersListActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        SplashActivity.this.startActivity(mainIntent);

                        SplashActivity.this.finish();
                    } catch (ActivityNotFoundException e) {
                        Log.e("Error...", e.getMessage() + "");
                    }
                }else {
                    Intent mainIntent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    SplashActivity.this.startActivity(mainIntent);

                    SplashActivity.this.finish();
                }
                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
            }
        }, SPLASH_DISPLAY_TIME);
    }
}
