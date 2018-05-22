package com.trivediinfoway.mychatapp;

import android.app.Application;

/**
 * Created by TI A1 on 22-03-2018.
 */

public class FirebaseChatMainApp extends Application {
    private static boolean sIsChatActivityOpen = false;
    public static final String Login_PREFS = "Login_Preference";
    public static final String Login_Username = "username";
    public static final String Login_UserProfile = "profile_url";

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}