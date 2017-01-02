package com.example.q.project2;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by q on 2016-12-30.
 */

public class TabA2Fragment extends Fragment {
    private View view;
    private AccessToken accessToken =  AccessToken.getCurrentAccessToken();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> contactDataSet = new ArrayList<String>();
    private List<Bitmap> profileDataSet = new ArrayList<Bitmap>();

    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab1, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        getFriends("/" + accessToken.getUserId() + "/taggable_friends?limit=50");

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
                            // specify an adapter (see also next example)

                            for (int i = 0; i < friendsArray.length(); i++) {
                                JSONObject object = friendsArray.getJSONObject(i);
                                object.put("is_facebook", true);
                                object.put("last_update", new Date());
                                object.put("user", accessToken.getUserId());
                                contactDataSet.add(object.getString("name"));

                                if (object.getJSONObject("picture").getJSONObject("data").getBoolean("is_silhouette") == false) {
                                    Bitmap bmp = getProfileImage(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    profileDataSet.add(bmp);
                                }
                                else
                                    profileDataSet.add(((BitmapDrawable) getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square)).getBitmap());

                            }

                            uploadContacts(friendsArray);

                            String[] nameArr = new String[contactDataSet.size()];
                            Bitmap[] profileImageArr = new Bitmap[contactDataSet.size()];

                            nameArr = contactDataSet.toArray(nameArr);
                            profileImageArr = profileDataSet.toArray(profileImageArr);

                            Log.d("CONTACT ARRAY", nameArr.toString());
                            Log.d("Profile Array", profileImageArr.toString());

                            mAdapter = new RecyclerViewAdapter(getActivity(), nameArr, profileImageArr);

                            // Sectioned Adapter
                            /*
                            List<SectionedRecyclerViewAdapter.Section> sections =
                                    new ArrayList<SectionedRecyclerViewAdapter.Section>();

                            sections.add(new SectionedRecyclerViewAdapter.Section(0, "Section 1"));
                            sections.add(new SectionedRecyclerViewAdapter.Section(5, "Section 2"));

                            SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[sections.size()];
                            SectionedRecyclerViewAdapter mSectionedAdapter = new
                                    SectionedRecyclerViewAdapter(getActivity(), R.layout.section, R.id.section_text, mAdapter);
                            mSectionedAdapter.setSections(sections.toArray(dummy));
                            */
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void uploadContacts(JSONArray contacts) {
        String url = "http://52.78.52.132:3000" + "/upload_contacts";
        OkHttpHandler handler = new OkHttpHandler();
        String result = null;
        try {
            result = handler.execute(url, contacts.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getProfileImage(String imagePath) {
        ProfileImageLoader profileImageLoader = new ProfileImageLoader();
        Bitmap result = ((BitmapDrawable) getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square)).getBitmap();
        try {
            result = profileImageLoader.execute(imagePath).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
