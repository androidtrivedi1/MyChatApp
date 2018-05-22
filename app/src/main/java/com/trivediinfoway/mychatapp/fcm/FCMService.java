package com.trivediinfoway.mychatapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trivediinfoway.mychatapp.ChatActivity;
import com.trivediinfoway.mychatapp.FirebaseChatMainApp;
import com.trivediinfoway.mychatapp.R;
import com.trivediinfoway.mychatapp.utils.Constants;

/**
 * Created by TI A1 on 08-05-2018.
 */

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    NotificationCompat.Builder notificationBuilder;
    public static NotificationManager notificationManager;
    int Counter = 0;
    String title;
    String message;
    String username;
    String uid;
    String fcmToken;
    public static String url;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /* There are two types of messages data messages and notification messages. Data messages are handled here in onMessageReceived whether the app is in the foreground or background. Data messages are the type traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app is in the foreground. When the app is in the background an automatically generated notification is displayed. */
        String notificationTitle = null, notificationBody = null;
        String dataTitle = null, dataMessage = null;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
            dataTitle = remoteMessage.getData().get("title");
            dataMessage = remoteMessage.getData().get("text");

            Log.e("IF...",dataTitle+"...Title");

            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("text");
            username = remoteMessage.getData().get("username");
            uid = remoteMessage.getData().get("username");
            fcmToken = remoteMessage.getData().get("fcm_token");
            url = remoteMessage.getData().get("url");
            Log.e("URLLLLLLLLLL@@@@@@@@@",url+"URL>>>>>>>>");

            /*if (!FirebaseChatMainApp.isChatActivityOpen()) {
                sendNotification(title,
                        message,
                        username,
                        uid,
                        url,
                        fcmToken);
            }*/
           /*else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        username,
                        uid,
                        url,
                        fcmToken));
            }*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();//remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("text");
            username = remoteMessage.getData().get("username");
            uid = remoteMessage.getData().get("username");
            fcmToken = remoteMessage.getData().get("fcm_token");
            url = remoteMessage.getData().get("url");
            Log.e("URLLLLLLLLLL!!!!!!!!!",url+"URL>>>>>>>>");

            Log.e("IF...",notificationTitle+"...notificationTitle");
        }

        if (!FirebaseChatMainApp.isChatActivityOpen()) {
            sendNotification(notificationTitle, notificationBody, dataTitle, dataMessage);
        }
        /*if (!FirebaseChatMainApp.isChatActivityOpen()) {
            sendNotification(title,
                    message,
                    username,
                    uid,
                    url,
                    fcmToken);
        }
         else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        username,
                        uid,
                        url,
                        fcmToken));
            }*/
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    /**
     //     * Create and show a simple notification containing the received FCM message.
     //     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification(String notificationTitle, String notificationBody, String dataTitle, String dataMessage) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.putExtra("url",url);
        Log.e("URLLLLLLLLLL",url+"URL>>>>>>>>");
        Log.e("dataTitle",dataTitle+"::dataTitle");
        Log.e("dataMessage",dataMessage+"::dataMessage");
        Log.e("notificationTitle",notificationTitle+"::notificationTitle");
        Log.e("notificationBody",notificationBody+"::notificationBody");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(dataTitle)
                .setContentText(dataMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        /*NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


      //  notificationManager =
         //       (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationManager.getActiveNotifications();


       /* StatusBarNotification[] notifications =
                notificationManager.getActiveNotifications();
*/
   //     Log.e("notifications..Length", notifications.length + "");

        StatusBarNotification[] notifications =
                notificationManager.getActiveNotifications();

       // Log.e("notifications..Length", notifications.length + "");

        /*if (notifications.length > 0) {
            for (StatusBarNotification notification : notifications) {
                if (notification.getNotification().extras.getString("android.title").equals("ee")) {

                    Log.e("GET..ID..LISt..", notification.getId() + "");
                    FCMService.notificationManager.cancel(notification.getId());
                }
            }
        }*/


        Counter = notifications.length;
        Log.e("COunter..", Counter + "");
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == Counter) {
                Log.e("Exists", notification.getPackageName() + "\n" + notification.getNotification().extras.getString("android.title")
                        + "\n" + notification.getNotification().extras.getString("android.text") + "");
            }
        }

        notificationManager.notify(Counter /* ID of notification */, notificationBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String url,
                                  String firebaseToken)
    {

// int ID=Integer.parseInt(receiverUid);
// Log.e("INT... ID...",ID+"");


        Log.e("Title..", title + "..." + url);
        Log.e("Message...", message + "..");
        Log.e("Receiver...", receiver + "....");

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (message.equals("")) {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
/* .setLargeIcon(bitmap)*/
                //    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        } else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
/* .setAutoCancel(true)*/
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationManager.getActiveNotifications();


        StatusBarNotification[] notifications =
                notificationManager.getActiveNotifications();

    //    Log.e("notifications..Length", notifications.length + "");

        if (notifications.length > 0) {
            for (StatusBarNotification notification : notifications) {
                if (notification.getNotification().extras.getString("android.title").equals("dd")) {

                    Log.e("GET..ID..LISt..", notification.getId() + "");
                    FCMService.notificationManager.cancel(notification.getId());
                }
            }
        }

        Counter = notifications.length;
        Log.e("COunter..", Counter + "");
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == Counter) {
                Log.e("Exists", notification.getPackageName() + "\n" + notification.getNotification().extras.getString("android.title")
                        + "\n" + notification.getNotification().extras.getString("android.text") + "");
            }
        }
        notificationManager.notify(Counter, notificationBuilder.build());

/* notificationManager =
(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

// notificationManager.notify(1, notificationBuilder.build());
    }
}
