package com.trivediinfoway.mychatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by TI A1 on 16-05-2018.
 */

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //  if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

        Intent serviceIntent = new Intent(context, AndroidServiceStartOnBoot.class);

        context.startService(serviceIntent);

        //  }
    }

}