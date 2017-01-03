package com.example.q.project2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        startIntent.putExtra("ringtone-uri", alarmUri.toString());
        context.startService(startIntent);

        Intent wakeupActivity = new Intent(context, WakeupActivity.class);
        wakeupActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(wakeupActivity);
        /*
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        */
    }
}