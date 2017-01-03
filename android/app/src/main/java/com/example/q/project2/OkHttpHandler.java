package com.example.q.project2;

import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by q on 2017-01-01.
 */

public class OkHttpHandler extends AsyncTask<String, Void, String> {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    protected String doInBackground(String... params) {
        Request request;
        // POST
        if (params.length > 1) {
            try {
                JSONArray contactArray = new JSONArray(params[1]);
                Log.d("JSON ARRAY NEW", contactArray.toString());

            } catch (Exception e) {
            }

            RequestBody requestBody = RequestBody.create(JSON, params[1]);
            request = new Request.Builder()
                    .url(params[0])
                    .post(requestBody)
                    .build();

        } else {
            request = new Request.Builder()
                    .url(params[0])
                    .get()
                    .build();
        }
        // GET
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAILED";
    }

    protected void onPostExecute(String result) {
        Log.d("POST response", result);
    }
}
