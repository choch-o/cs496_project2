package com.example.q.project2;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by q on 2017-01-03.
 */

public class RingtonePlayingService extends Service {
    private Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();
        Log.d("ringring", "ring");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
    }
}
