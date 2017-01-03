package com.example.q.project2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
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
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
//        ringtone.play();
        Log.d("ringring", "ring");

        try {
            mediaPlayer.setDataSource(getApplicationContext(), ringtoneUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
//        ringtone.stop();
        mediaPlayer.stop();
    }
}
