package com.example.music_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationAction";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PREVIOUS_ACTION":
                    Log.d(TAG, "Previous action clicked");
                    MyExoPlayer.skipToPrevSong(context);
                    break;
                case "PAUSE_ACTION":
                    Log.d(TAG, "Pause action clicked");
                    break;
                case "NEXT_ACTION":
                    Log.d(TAG, "Next action clicked");
                    MyExoPlayer.skipToNextSong(context);
                    break;
            }
        }
    }
}
