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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final DisplayMetrics metrics = new DisplayMetrics();
    private int width;
    private int height;

    LinearLayout container;
    LinearLayout loadingLayout;
    ScrollView scrollView;

    // See AsyncTask "fillLayoutAsyncTask" for more info
    boolean firstLayout = true;

    // Hardcoded rowcount for testing
    int rowcount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Device Dimensions (Height and Width)
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;


        ImageView logo_view = findViewById(R.id.title_logo);
        Bitmap title_logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        logo_view.setImageBitmap(title_logo);

        // Initialize/Create the game container layout.
        container = new LinearLayout(this);
        LinearLayout.LayoutParams newRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(newRowParams);
        container.setVisibility(View.GONE);

        scrollView = findViewById(R.id.gameScrollView);
        scrollView.addView(container);

        loadingLayout = findViewById(R.id.loadingLayout);

        // Request Twitch JSON
        requestWithSomeHttpHeaders();
    }

    public void requestWithSomeHttpHeaders() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.twitch.tv/helix/games/top";
        String stream_url = "https://api.twitch.tv/kraken/streams";

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
                    String gamesJSONString = response.getString("data");
                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    for (int i = 0; i < jsonArr.length(); i++) {
                        // For every entry in the JSONArray make an object and get its name as well as box art.
                        JSONObject gameObject = jsonArr.getJSONObject(i);
                        String name = gameObject.get("name").toString();
                        String imageURL = convertToReadableURL(gameObject.get("box_art_url").toString(), (width/rowcount), (int) (height/(rowcount*1.33)));

                        // Construct layouts with images.
                        new fillLayoutAsyncTask(i, name, imageURL).execute();
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
    }

    //AsyncTask for loading the images into bitmap from URL.
    //When images are loaded the view is filled with the views
    private class fillLayoutAsyncTask extends AsyncTask<Void, Integer, String> {
        private int currentRowCount;
        private int containerChildCount;
        private String gamename;
        private String URL;
        private Bitmap image;
        private ImageView currentGameImage;

        public fillLayoutAsyncTask(int currentGameCount, String gamename, String URL) {
            this.gamename = gamename;
            this.URL = URL;

            // Calculate the amount of rows added the the current layout via % of the game counter
            this.currentRowCount = currentGameCount%rowcount;
            this.containerChildCount = (int) Math.floor(currentGameCount/rowcount) + 1;

            // If the game is in the first layout but NOT the first its index must never be 0
            // If it is the first game it will create a new layout
            if (containerChildCount == 0 && !firstLayout) {
                containerChildCount = 1;
            }
        }

        @Override
        protected void onPreExecute() {
            // On Pre Execute is the first method that is called doing the AsyncTask.
            // This is where the layouts (or columns) in the table is created if necessary.

            if (currentRowCount >= rowcount || (containerChildCount == 0 || currentRowCount == 0)) {
                // Create new row Layout and make the game container visible
                if (firstLayout) {
                    firstLayout = false;
                    container.setVisibility(View.VISIBLE);
                    loadingLayout.setVisibility(View.GONE);
                }
                // Create a new layout if the row is full
                LinearLayout newCurrentRow = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams newRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCurrentRow.setOrientation(LinearLayout.HORIZONTAL);
                newCurrentRow.setLayoutParams(newRowParams);
                container.addView(newCurrentRow);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            // doInBackground is called right after onPreExecute. This is all done in a separate thread.
            // Because this is a separate thread, we are allowed to do network calls. (URL decoding)

            // Create current ImageView
            currentGameImage = new ImageView(getApplicationContext());
            currentGameImage.setOnClickListener(new Click_Listener(gamename));
            currentGameImage.setAdjustViewBounds(true);
            currentGameImage.setAlpha(0f);
            // Decode URL
            try {
                URL url = new URL(URL);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                // Only when we know that we got the image decoded we increment the row counter.
                // If we didn't, we would risk "holes" in our layout with no images if they were not loaded.
                currentRowCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            // this method is called right after doInBackground is done, and is on the main thread.
            // Therefore we are allowed to make UI changes such as .addView() which we cant do in doInBackground().
            currentGameImage.setImageBitmap(image);
            ((LinearLayout) container.getChildAt(containerChildCount - 1)).addView(currentGameImage);

            // When image is loaded, fade in the image
            currentGameImage.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setListener(null);
        }
    }

    private class Click_Listener implements View.OnClickListener {

        private String name;

        public Click_Listener(String name){
            this.name = name;
        }

        @Override
        public void onClick(View view) {
            Log.d("BUTTON", "button clicked...");
            USER_PREFERENCES.setSelection(this.name);
            startActivity(new Intent(MainActivity.this, PopUpActivity.class));
        }
    }

    private String convertToReadableURL(String box_art_url, int width, int height) {
        return box_art_url
                .replaceAll("\\{width\\}x\\{height\\}", width + "x" + height);
    }
}
