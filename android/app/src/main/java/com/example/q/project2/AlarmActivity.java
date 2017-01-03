package com.example.q.project2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by q on 2017-01-02.
 */

public class AlarmActivity extends AppCompatActivity {
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private Button setAlarmBtn;
    static final int SET_ALARM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmBtn);
        setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time;
                Toast.makeText(AlarmActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, 0);

                time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                if(System.currentTimeMillis()>time)
                {
                    if (calendar.AM_PM == 0)
                        time = time + (1000*60*60*12);
                    else
                        time = time + (1000*60*60*24);
                }
                Log.d("TIMETIMETIME", Long.toString(time));
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);

                String result = postAlarm(time);

                finish();
            }
        });
    }

    public void OnToggleClicked(View view)
    {
        long time;
        if (((ToggleButton) view).isChecked())
        {

        }
        else
        {
            alarmManager.cancel(pendingIntent);
            Intent stopIntent = new Intent(this, RingtonePlayingService.class);
            this.stopService(stopIntent);
            Toast.makeText(AlarmActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

    private String postAlarm(Long time) {
        String url = "http://52.78.52.132:3000" + "/set_time";
        OkHttpHandler handler = new OkHttpHandler();
        String result = null;
        JSONArray arr = new JSONArray();
        arr.put(Long.toString(time));
        try {
            result = handler.execute(url, arr.toString()).get();
            JSONObject obj = new JSONObject(result);
            Log.d("GET RESULT", obj.getString("result"));
            if (obj.getString("result") != "OK") {
                Toast.makeText(AlarmActivity.this, "FRIENDS SLEEPING. FAILED TO CHANGE TIME.", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}