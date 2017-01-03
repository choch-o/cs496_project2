package com.example.q.project2;

import android.app.AlarmManager;
import android.support.v4.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.socketio.ExceptionCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by q on 2016-12-30.
 */

public class TabCFragment extends Fragment {
    public static String serverURL = "http://52.78.52.132:3000";
    final AccessToken accessToken =  AccessToken.getCurrentAccessToken();
    public static String userID = "";
    public static String userName = "";
    public static String userPhone = "";
    private View rootView;
    public static TabCAdapter adapter = new TabCAdapter();
    private Button alarmBtn;
    public static String ALARM_TIME = "";
    long time;

    static final int SET_ALARM_REQUEST = 1;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.tab3, container, false);
        alarmBtn = (Button) rootView.findViewById(R.id.call_alarm_btn);
        String formattedTime = getAlarmTime();
        alarmBtn.setText(formattedTime);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "ALARM ON", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                MainActivity.pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                MainActivity.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, MainActivity.pendingIntent);
            }
        });
        alarmBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(getActivity(), AlarmActivity.class);
                startActivityForResult(i, SET_ALARM_REQUEST);
                return true;
            }
        });
        GridView gridView = (GridView)rootView.findViewById(R.id.alarm_view);
        adapter = new TabCAdapter();
        gridView.setAdapter(adapter);

        checkUser();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.alarm_sync);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeUp();
                ALARM_TIME = getAlarmTime();
                alarmBtn.setText(ALARM_TIME);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                gotoSleep();
                ALARM_TIME = getAlarmTime();
                alarmBtn.setText(ALARM_TIME);
                Intent intent = new Intent(rootView.getContext(), Sleepmode.class);
                startActivityForResult(intent, 1001);
                return true;
            }
        });

        return rootView;
    }

    public void tt (String msg) {
        Toast.makeText(rootView.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // Get facebook information of me
    public void checkUser() {
        // TODO : Async ID fetching - need to be executed before onCreateView
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            userID = object.getString("id");
                            userName = object.getString("name");
                            checkUserFromServer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        request.executeAsync();
    }

    public void checkUserFromServer() {
        Ion.with(rootView.getContext()).load(serverURL + "/check_me/" + userID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // If the user is not on the server
                        if (result.get("result").getAsInt() == 0) {
//                            DialogFragment dialog = new PhoneDialogFragment();
//                            dialog.show(getActivity().getSupportFragmentManager(), "PhoneDialogFragment");
                            PhoneDialogFragment dialog = new PhoneDialogFragment();
                            dialog.show(getActivity().getSupportFragmentManager(), "Phone Dialog");
                            Log.d("User is not on server", "Going to add");
                            Log.d("User phone", userPhone);
                            // enrollUser();
                        } else {
                            wakeUp();
                        }
                    }
                });
    }
    public void onUserEnterPhone(String phoneNumber) {
        userPhone = phoneNumber;
        enrollUser();
    }

    // Fetch image URL from facebook
    public void enrollUser() {
         GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            JSONObject profileJSON = response.getJSONObject().getJSONObject("picture")
                                                            .getJSONObject("data");
                            if((Boolean)profileJSON.get("is_silhouette")) {
                                enrollUserToServer("http://dismagazine.com/uploads/2011/08/notw_silhouette-1.jpg");
                            } else {
                                enrollUserToServer(profileJSON.getString("url"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void enrollUserToServer(String profileURL) {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("userName", URLEncoder.encode(userName,"utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        json.addProperty("userID", userID);
        json.addProperty("profileURL", profileURL);
        json.addProperty("phone", userPhone);
        json.addProperty("keycode", (new Random()).nextInt(100));

        Ion.with(rootView.getContext()).load(serverURL + "/enroll_me")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        loadFromServer();
                    }
                });

    }

    public void loadFromServer() {
        adapter.clear();

        Ion.with(rootView.getContext()).load(serverURL+"/profile_list").asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(e != null) {
                            e.printStackTrace();
                            return;
                        }

                        JsonObject record;
                        String userNameDecoded = "Anony";
                        for (int i=0 ; i<result.size() ; i++) {
                            record = result.get(i).getAsJsonObject();
                            try {
                                userNameDecoded = URLDecoder.decode(record.get("userName").getAsString(), "utf-8");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            adapter.addData(record.get("userID").getAsString(),
                                    userNameDecoded,
                                    record.get("profileURL").getAsString(),
                                    record.get("awake").getAsBoolean(),
                                    record.get("phone").getAsString(),
                                    record.get("keycode").getAsInt());
                        }
                    }
                });

    }

    public void gotoSleep() {
        Ion.with(rootView.getContext()).load(serverURL + "/good/night/" + userID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("Everything will be OK", result.toString());
                        loadFromServer();
                    }
                });
    }

    private String getAlarmTime() {
        String url = "http://52.78.52.132:3000" + "/get_time";
        OkHttpHandler handler = new OkHttpHandler();
        String result = null;
        String timeFormatted = null;
        try {
            result = handler.execute(url).get();
            JSONObject obj = new JSONObject(result);
            Log.d("GET RESULT", Long.toString(obj.getLong("time")));
            time = obj.getLong("time");
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            timeFormatted = formatter.format(date);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeFormatted;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_ALARM_REQUEST) {
            String formattedTime = getAlarmTime();
            alarmBtn.setText(formattedTime);
        }
        else if (requestCode == 1001) {
            wakeUp();
            tt("Good morning! :)");
        }
    }

    public void wakeUp() {
        Ion.with(rootView.getContext()).load(serverURL + "/good/morning/" + userID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("Everything will be OK", result.toString());
                        loadFromServer();
                    }
                });
    }
}
