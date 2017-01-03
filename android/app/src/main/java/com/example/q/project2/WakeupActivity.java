package com.example.q.project2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;

public class WakeupActivity extends AppCompatActivity {
    private String userID = TabCFragment.userID;
    private Integer myKeycode = TabCFragment.adapter.findKeycode(userID);
    private String counterName = "";
    private String counterPhone = "";
    private Integer counterKeycode;

    private TextView myKeycodeView;
    private TextView keycodeMsg;
    private Button callBtn;
    private EditText enterKeycode;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        myKeycodeView = (TextView)findViewById(R.id.myKeycode);
        keycodeMsg = (TextView)findViewById(R.id.keycodeMsg);
        callBtn = (Button)findViewById(R.id.callBtn);
        enterKeycode = (EditText)findViewById(R.id.enterKeycode);
        submitBtn = (Button)findViewById(R.id.submitKeycode);

        Ion.with(this).load(TabCFragment.serverURL+"/profile_counter/"+userID).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            counterName = URLDecoder.decode(result.get("userName").getAsString(),"utf-8");
                            counterPhone = result.get("phone").getAsString();
                            counterKeycode = result.get("keycode").getAsInt();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        myKeycodeView.setText(Integer.toString(myKeycode));
                        keycodeMsg.setText(counterName);
                        keycodeMsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // CALL INTENT
                                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + counterPhone.toString()));
                                WakeupActivity.this.startActivity(i);
                            }
                        });

                        callBtn.setText("Call " + counterName);
                        callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // CALL INTENT
                                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + counterPhone.toString()));
                                WakeupActivity.this.startActivity(i);
                            }
                        });
                        enterKeycode.setHint(counterName + "'s Keycode");
                        submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Compare counterKeycode with enterKeycode's Value
                                int inputKeycode = Integer.parseInt(enterKeycode.getText().toString());
                                if (inputKeycode == counterKeycode) {
                                    // Cancel alarm
                                    MainActivity.alarmManager.cancel(MainActivity.pendingIntent);
                                    Intent stopIntent = new Intent(WakeupActivity.this, RingtonePlayingService.class);
                                    WakeupActivity.this.stopService(stopIntent);
                                    Toast.makeText(WakeupActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();

                                    // Go back to tabCFragment
                                    finish();
                                } else {
                                    // Show error toast
                                    Toast.makeText(WakeupActivity.this, "WRONG KEYCODE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
}
