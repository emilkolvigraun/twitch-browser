package com.android.course.twitchbrowserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWithSomeHttpHeaders();
    }

    public void requestWithSomeHttpHeaders() {

        final TextView mTextView = (TextView) findViewById(R.id.helloText);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.twitch.tv/helix/games/top";

        //"Kraken" is deprecated
// a query for pokemon streams
//        https://api.twitch.tv/kraken/search/streams?query=poker&client_id=CLIENTID
        //https://api.twitch.tv/kraken/streams/featured

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-ID", "hf6aoclq1ddt9w5tfa5o6qzybqs3g1");
                params.put("Accept", "application/vnd.twitchtv.v5+json");

                return params;
            }

        };
        queue.add(stringRequest);
    }
// Add the request to the RequestQueue.


//    var httpRequest = new XMLHttpRequest();
//
//    httpRequest.addEventListener('load', clipsLoaded);
//    httpRequest.open('GET', 'https://api.twitch.tv/kraken/clips/top?limit=10&game=Overwatch&trending=true');
//    httpRequest.setRequestHeader('Client-ID', 'uo6dggojyb8d6soh92zknwmi5ej1q2');
//    httpRequest.setRequestHeader('Accept', 'application/vnd.twitchtv.v5+json');
//    httpRequest.send();
}
