package com.example.q.project2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Sleepmode extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepmode);
        final Intent intent = getIntent();

        TextView alarmText = (TextView) findViewById(R.id.wakeuptime);
        alarmText.setText(TabCFragment.ALARM_TIME);

        Button wakeupBtn = (Button) findViewById(R.id.wakeup);
        wakeupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
