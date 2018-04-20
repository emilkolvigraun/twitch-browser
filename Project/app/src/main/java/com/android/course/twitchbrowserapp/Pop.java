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

        // STRUCTURE OF JSON FOR Streams
        //_total
        //streams:
//                {"_id":28397511456,
//                "game":"Fortnite",
//                "broadcast_platform":"live",
//                "community_id":"558ffafb-6f75-44ff-8cb7-a4f045d2399f",
//                "community_ids":["558ffafb-6f75-44ff-8cb7-a4f045d2399f", "8ea1e94f-4015-4f38-bd7e-745d8f42539a"],
//                "viewers":15493,
//                "video_height":1080,
//                "average_fps":60,
//                "delay":0,
//                "created_at":"2018-04-20T02:42:40Z",
//                "is_playlist":false,
//                "stream_type":"live",
//                "preview":{
//                    "small":"https:\/\/static-cdn.jtvnw.net\/previews-ttv\/live_user_highdistortion-80x45.jpg",
//                    "medium":"https:\/\/static-cdn.jtvnw.net\/previews-ttv\/live_user_highdistortion-320x180.jpg",
//                    "large":"https:\/\/static-cdn.jtvnw.net\/previews-ttv\/live_user_highdistortion-640x360.jpg",
//                    "template":"https:\/\/static-cdn.jtvnw.net\/previews-ttv\/live_user_highdistortion-{width}x{height}.jpg"},
//                "channel":{
//                    "mature":true,
//                    "status":"TSM HighDistortion | 500 BITS FOR 99 cents with !Prime | #2 Kills on PC | ▶️ Youtube.com\/HighDistortion",
//                    "broadcaster_language":"en",
//                    "display_name":"HighDistortion",
//                    "game":"Fortnite",
//                    "language":"en",
//                    "_id":84752541,
//                    "name":"highdistortion",
//                    "created_at":"2015-03-08T21:23:04.274013Z",
//                    "updated_at":"2018-04-20T16:07:22.832027Z",
//                    "partner":true,
//                    "logo":"https:\/\/static-cdn.jtvnw.net\/jtv_user_pictures\/a86c03dc-a682-4069-b6b1-592847a8d078-profile_image-300x300.png",
//                    "video_banner":"https:\/\/static-cdn.jtvnw.net\/jtv_user_pictures\/385231343fd2ef63-channel_offline_image-1920x1080.png",
//                    "profile_banner":"https:\/\/static-cdn.jtvnw.net\/jtv_user_pictures\/ecc65383-4237-494b-bb0e-20a1f0f6eaa9-profile_banner-480.png",
//                    "profile_banner_background_color":"","url":"https:\/\/www.twitch.tv\/highdistortion",
//                    "views":4087967,
//                    "followers":364575,
//                    "broadcaster_type":"",
//                    "description":"Thanks for stumbling on in this channel. You are watching Jimmy A.K.A. HighDistortion. Once a Pro gamer on the MLG circuit, now just an amateur bodybuilder who likes to lift and play a lot of video games.",
//                    "private_video":false,
//                    "privacy_options_enabled":false}
//                }



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
                try {
                    String gamesJSONString = response.getString("streams");
                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    for (int i = 0; i < 1; i++) {
                        // For every entry in the JSONArray make an object and get its name as well as box art.
                        JSONObject gameObject = jsonArr.getJSONObject(0);
                        Log.d("Prints", gameObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
