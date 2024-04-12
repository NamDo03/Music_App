package com.example.music_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.music_app.PlayerActivity;
import com.example.music_app.R;
import com.example.music_app.models.SongModel;

public class NotificationUtils {
    private static final String CHANNEL_ID = "MyMusicApp";
    private static final int NOTIFICATION_ID = 101;

    public static void showNotification(Context context, SongModel songModel) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Music App", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent for launching PlayerActivity when notification is tapped
        Intent intent = new Intent(context, PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Create action intents
        Intent previousIntent = new Intent(context, NotificationActionReceiver.class);
        previousIntent.setAction("PREVIOUS_ACTION");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 0, previousIntent, PendingIntent.FLAG_MUTABLE);

        Intent pauseIntent = new Intent(context, NotificationActionReceiver.class);
        pauseIntent.setAction("PAUSE_ACTION");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(context, NotificationActionReceiver.class);
        nextIntent.setAction("NEXT_ACTION");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_MUTABLE);

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(songModel.getTitle())
                .setContentText(songModel.getSubtitle())
                .setSmallIcon(R.drawable.ic_music_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        // Add playback controls (play, pause, next, previous) using MediaStyle
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(null));

        // Add action buttons for playback controls
        builder.addAction(R.drawable.ic_prev, "Previous", previousPendingIntent);
        builder.addAction(R.drawable.ic_pause, "Pause", pausePendingIntent);
        builder.addAction(R.drawable.ic_next, "Next", nextPendingIntent);

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID); // Hủy thông báo với Notification ID cụ thể
    }


}
