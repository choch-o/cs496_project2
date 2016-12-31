package com.example.q.project2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by q on 2016-12-30.
 */

public class Tab1Fragment extends Fragment {
    private View view;
    private TextView resultLog;
    private AccessToken accessToken =  AccessToken.getCurrentAccessToken();
    private String friendListID;
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab1, container, false);
        resultLog = (TextView) view.findViewById(R.id.result_log);

        getFriends("/" + accessToken.getUserId() + "/taggable_friends");
        return view;
    }

    private void getFriends(String nextLink) {
        GraphRequest request = new GraphRequest(
                accessToken,
                nextLink,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            // Friends array (name, id)
                            JSONArray friendsArray = response.getJSONObject().getJSONArray("data");
                            JSONObject paging = response.getJSONObject().getJSONObject("paging");
                            JSONObject cursors = paging.getJSONObject("cursors");
                            String next = paging.getString("next");
                            Log.d("Friends array", friendsArray.toString());
                            Log.d("Num of friends", Integer.toString(friendsArray.length()));
                            Log.d("Cursors", cursors.toString());
                            Log.d("Next friends", next);

                            JSONObject friend = friendsArray.getJSONObject(0);
                            String friendID = friend.getString("id");

                            Log.d("Friend 1", friend.toString());
                            Log.d("Friend 1 ID", friendID);
                            // friendsGraphRequest(friendID);
                        } catch (Exception e) {
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
