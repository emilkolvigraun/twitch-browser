package com.android.course.twitchbrowserapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emil S. Kolvig-Raun on 20-04-2018.
 */

public class Pop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        int width = display.widthPixels;
        int height = display.heightPixels;


        TextView text = findViewById(R.id.textView_pop);
        text.setText(User_Preferences.getSelection());

        getWindow().setLayout((int)(width*0.7), (int)(height*0.5));

        requestWithSomeHttpHeaders();
    }

    public void requestWithSomeHttpHeaders() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.twitch.tv/kraken/streams/?game=Fortnite";

        //"Kraken" is deprecated
        // a query for pokemon streams
        // https://api.twitch.tv/kraken/search/streams?query=poker&client_id=CLIENTID
        // https://api.twitch.tv/kraken/streams/featured

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Prints", "length of response: " + response.length() + response.toString());

                // "data" is the name of the TwitchAPI JSON Array
                // This array gets divided into objects (gameObject)
//                    String gamesJSONString = response.getString("streams");
//                    JSONArray jsonArr = new JSONArray(gamesJSONString);
//                    for (int i = 0; i < jsonArr.length(); i++) {
//                        // For every entry in the JSONArray make an object and get its name as well as box art.
//                        JSONObject gameObject = jsonArr.getJSONObject(i);
//                    }
                Log.d("Prints", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-ID", "hf6aoclq1ddt9w5tfa5o6qzybqs3g1");
                params.put("Accept", "application/vnd.twitchtv.v5+json");

                return params;
            }

        };
        queue.add(jsonObjReq);
        System.out.println(queue);
    }
}
