package com.android.course.twitchbrowserapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //final ArrayList<String> gameNames = new ArrayList<>();
    final ArrayList<Bitmap> gameImage = new ArrayList<>();
    final ArrayList<String> gameImageURLs = new ArrayList<>();
    final DisplayMetrics metrics = new DisplayMetrics();
    private int width;
    private int height;


    LinearLayout container;
    LinearLayout loadingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;

        setContentView(R.layout.activity_main);

        ImageView logo_view = findViewById(R.id.title_logo);

        logo_view.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo));

        container = findViewById(R.id.container);
        container.setVisibility(View.GONE);
        loadingLayout = findViewById(R.id.loadingLayout);

        requestWithSomeHttpHeaders();
    }


    public void requestWithSomeHttpHeaders() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.twitch.tv/helix/games/top";

        //"Kraken" is deprecated
        // a query for pokemon streams
        // https://api.twitch.tv/kraken/search/streams?query=poker&client_id=CLIENTID
        // https://api.twitch.tv/kraken/streams/featured

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Prints", "length of response: " + response.length() + response.toString());



                try {
                    // "data" is the name of the TwitchAPI JSON Array
                    // This array gets divided into objects (gameObject)
                    String gamesJSONString = response.getString("data");
                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    for (int i = 0; i < jsonArr.length(); i++) {
                        // For every entry in the JSONArray make an object and get its name as well as box art.
                        JSONObject gameObject = jsonArr.getJSONObject(i);
                        String name = gameObject.get("name").toString();
                        String image = convertToReadableURL(gameObject.get("box_art_url").toString(), (width/3), (height/4));

                        // Cuts the game titles to be under 20 chars to prevent multiline TextView.
                        String tempName;
                        if (name.length() > 20) {
                            tempName = name.substring(0, 20);
                        } else {
                            tempName = name;
                        }

                        //Add names and images to arrays so they can be added in the layout.
                        //gameNames.add(tempName);
                        gameImageURLs.add(image);

                    }
                    new DownloadImagesFromURI().execute();
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

    //Fills the container with images as well as matching text.
    //Gets every child of every layout in the container and sets its text and image.
    private void fillContainer() {
        // Fills the scroll
        int fillCounter = 0;
        for (int i = 0; i < container.getChildCount(); i++) {
            for (int j = 0; j < ((LinearLayout)container.getChildAt(i)).getChildCount(); j++) {
                ((ImageView)((LinearLayout)((LinearLayout)container.getChildAt(i)).getChildAt(j)).getChildAt(0)).setImageBitmap(gameImage.get(fillCounter));
                //((TextView)((LinearLayout)((LinearLayout)container.getChildAt(i)).getChildAt(j)).getChildAt(1)).setText(gameNames.get(fillCounter));
                //container.getChildAt(i).setOnClickListener(new Click_Listener());
                ((ImageView)((LinearLayout)((LinearLayout)container.getChildAt(i)).getChildAt(j)).getChildAt(0)).setOnClickListener(new Click_Listener());
                fillCounter++;
            }
        }
        container.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);

        // Animation for fade when loading is done
        container.setAlpha(0f);
        container.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);

    }

    private String convertToReadableURL(String box_art_url, int width, int height) {
        return box_art_url
                .replaceAll("\\{width\\}x\\{height\\}", width + "x" + height);
    }


    //AsyncTask for loading the images into bitmap from URL.
    //When images are loaded the view is filled with the views
    private class DownloadImagesFromURI extends AsyncTask<URI, Integer, Long> {
        @Override
        protected Long doInBackground(URI... uris) {
            try {
                for (int i = 0; i < gameImageURLs.size(); i++) {
                    URL url = new URL(gameImageURLs.get(i));
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    gameImage.add(image);
            }} catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(Long result) {
            fillContainer();
        }
    }

    private class Click_Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d("BUTTON", "button clicked...");
            startActivity(new Intent(MainActivity.this, Pop.class));
        }
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
