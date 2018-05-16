package com.android.course.twitchbrowserapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Emil S. Kolvig-Raun on 20-04-2018.
 */

public class Pop extends Activity {


    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        this.width = display.widthPixels;
        this.height = display.heightPixels;

        getWindow().setLayout((int)(this.width*0.7), (int)(this.height*0.5));

        requestWithSomeHttpHeaders();

    }

    private void setPopUpContent(ArrayList<String> info){
        TextView text = findViewById(R.id.textView_pop);
        Log.d("DEBUGGING: ", info.toString());


        LinearLayout loading = findViewById(R.id.loadingLayout);


        text.setAlpha(0f);
        text.setText(info.toString());
        loading.setAlpha(0f);
        text.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);


    }

    private boolean indexExists(final JSONArray list, final int index) {
        return index >= 0 && index < list.length();
    }


    public void requestWithSomeHttpHeaders() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Constructing URL
        String game_chosen = User_Preferences.getSelection().replaceAll("\\s","%20");
        String url = "https://api.twitch.tv/kraken/streams/?game=" + game_chosen;

        // DEBUGGING
        Log.d("STREAM URL", url);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Prints", "length of response: " + response.length() + response.toString());
                try {
                // "data" is the name of the TwitchAPI JSON Array
                // This array gets divided into objects (gameObject)
                    ArrayList<String> info = new ArrayList<>();
                    String gamesJSONString = response.getString("streams");

                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    if (indexExists(jsonArr, 0)){
                        JSONObject jsonObj = jsonArr.getJSONObject(0);

                        //Retrieve all values
                        String game = recurseKeys(jsonObj, "game");
                        String viewers = recurseKeys(jsonObj, "viewers");
                        String name = recurseKeys(jsonObj, "name");
                        String status = recurseKeys(jsonObj, "status");
                        String updated_last = recurseKeys(jsonObj, "updated_at");
                        String followers = recurseKeys(jsonObj, "followers");
                        String description = recurseKeys(jsonObj, "description");
                        String large_image = recurseKeys(jsonObj, "medium");

                        info.add(game);
                        info.add(name);
                        info.add(viewers);
                        info.add(followers);
                        info.add(large_image);
                        info.add("Last updated: " + updated_last);
                        info.add(status);
                        info.add(description);


                        // For every entry in the JSONArray make an object and get its name as well as box art.
                        Log.d("Prints ID", recurseKeys(jsonObj, "name"));

                    } else {
                        info.add("No information available from Twitch.");
                    }
                    setPopUpContent(info);

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

    }

    public static String recurseKeys(JSONObject jObj, String findKey) throws JSONException {
        String finalValue = "";
        boolean _stop = false;
        if (jObj == null) {
            return "";
        }

        Iterator<String> keyItr = jObj.keys();
        Map<String, String> map = new HashMap<>();

        while(keyItr.hasNext() || _stop == true) {
            String key = keyItr.next();
            map.put(key, jObj.getString(key));
        }

        for (Map.Entry<String, String> e : (map).entrySet()) {
            String key = e.getKey();
            if (key.equalsIgnoreCase(findKey)) {
                _stop = true;
                return jObj.getString(key);
            }

            // read value
            Object value = jObj.get(key);

            if (value instanceof JSONObject) {
                finalValue = recurseKeys((JSONObject)value, findKey);
            }
        }

        // key is not found
        return finalValue;
    }
}
