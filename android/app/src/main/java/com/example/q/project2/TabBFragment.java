package com.example.q.project2;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by q on 2016-12-30.
 */


public class TabBFragment extends Fragment {
    private final String serverURL = "http://52.78.52.132:3000";
    // TODO : How to set this deviceURL?
    private final String deviceURL = Environment.getExternalStorageDirectory().toString() + "/Foodie";
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.tab2, container, false);

        final AccessToken accessToken =  AccessToken.getCurrentAccessToken();
        final String userID = accessToken.getUserId();

        final GridView gridView = (GridView)rootView.findViewById(R.id.gallery_view);
        final Tab2Adapter adapter = new Tab2Adapter();

        gridView.setAdapter(adapter);

        loadFromServer(rootView, adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.gallery_sync);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromDevice(rootView, adapter);
                syncToServer(rootView, adapter);
            }
        });

        return rootView;
    }

    public void loadFromServer(final View view, final Tab2Adapter adapter) {
        // Clear the Adapter
        adapter.clearData();

        // Get the URL List from server
        // Append them to the Adapter
        Ion.with(view.getContext()).load(serverURL+"/image_list").asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(e != null) {
                            e.printStackTrace();
                            return;
                        }

                        JsonObject record;
                        for (int i=0 ; i<result.size() ; i++) {
                            record = result.get(i).getAsJsonObject();
                            adapter.addData(record.get("name").getAsString(),
                                            serverURL + "/" + record.get("url").getAsString(),
                                            record.get("last_update").getAsString(), true);
                        }
                    }
                });
    }

    public void loadFromDevice(final View view, final Tab2Adapter adapter) {
        File f = new File(deviceURL);
        File files[] = f.listFiles();
        for (int i=0 ; i<files.length ; i++) {
            adapter.addData(files[i].getName(), "file:" + files[i].getPath(), "time", false);
        }
    }

    public void syncToServer(final View view, final Tab2Adapter adapter) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String formattedDate = df.format(new Date());

        for (int i=0 ; i<adapter.getCount() ; i++) {
            if (((Tab2Item) adapter.getItem(i)).isOn_server()) {
                continue;
            }

            Tab2Item item = (Tab2Item) adapter.getItem(i);

            Ion.with(view.getContext()).load(serverURL + "/image")
                    .setHeader("name", item.getName())
                    .setHeader("last_update", formattedDate)
                    .setMultipartFile("userFile", "image/jpeg", new File(item.getUrl().substring(5)))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(e != null) {
                                Toast.makeText(view.getContext(), "Error uploading file", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                return;
                            }
                        }
                    });

            item.setOn_server(true);
        }

        Toast.makeText(view.getContext(), "Sync Completed", Toast.LENGTH_SHORT).show();
    }
}
