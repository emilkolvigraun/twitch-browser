package com.android.course.twitchbrowserapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * Initialising DisplayMetrics
     * To scale game_images to screen size
     */
    final DisplayMetrics metrics = new DisplayMetrics();
    private int width;
    private int height;

    //Declaring elements
    private LinearLayout container;
    private LinearLayout loadingLayout;
    private ScrollView scrollView;

    // See AsyncTask "fillLayoutAsyncTask" for more info
    boolean firstLayout = true;

    // Hardcoded rowcount = 3, as image sizes are height/4 so there's room for title logo.
    private int rowcount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the content layout to activity_main.xml
        setContentView(R.layout.activity_main);

        // Get Device Dimensions (Height and Width)
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        //Setting the top logo image from drawable resource
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

        //Enabling us to manipulate the appearance of the loading indicator
        loadingLayout = findViewById(R.id.loadingLayout);

        // Request Twitch JSON
        requestWithSomeHttpHeaders();
    }

    /**
     * requestWithSomeHttpHeaders()
     * Standard webservice call framework.
     * Uses the VOLLEY HTTP Library.
     * Not great with large download or streaming operations, as it holds all responses in memory while parsing.
     * Great tool for a limited number of images and strings. Faster than DownloadManager..
     */
    public void requestWithSomeHttpHeaders() {
        //Retrieving the request URL from SETTINGS.
        String url = SETTINGS.getTopRequestURL();

        //Initiates response listener from Volley library
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Retrieves the "data" key from the response and recurse through array
                    String gamesJSONString = response.getString("data");
                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    for (int i = 0; i < jsonArr.length(); i++) {
                        // For every entry in the JSONArray make an object and get its name as well as box art.
                        JSONObject gameObject = jsonArr.getJSONObject(i);

                        // Construct layouts with images.
                        String name = gameObject.get("name").toString();
                        String imageURL = convertToReadableURL(gameObject.get("box_art_url").toString(), (width/rowcount), (int) (height/(rowcount*1.33)));
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
                params.put("Client-ID", SETTINGS.getHeaderClientId());
                params.put("Accept", SETTINGS.getHeaderAccept());
                return params;
            }
        };
        //Adds the request to the Queue
        SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    //AsyncTask for loading the images into bitmap from URL.
    //When images are loaded the view is filled with the views
    private class fillLayoutAsyncTask extends AsyncTask<Void, Integer, String> {
        private int currentRowCount;
        private int containerChildCount;
        private String game_name;
        private String URL;
        private Bitmap image;
        private ImageView currentGameImage;

        public fillLayoutAsyncTask(int currentGameCount, String game_name, String URL) {
            this.game_name = game_name;
            this.URL = URL;

            // Calculate the amount of rows added the the current layout via % of the game counter
            // flooring the value to avoid layout confusion
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
            currentGameImage.setOnClickListener(new Click_Listener(game_name));
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

    /**
     * Creates the cusstom click listener class, to set the "name" selection.
     */
    private class Click_Listener implements View.OnClickListener {
        private String name;
        //Constructor assigns the name to the variable
        public Click_Listener(String name){
            this.name = name;
        }

        @Override
        public void onClick(View view) {
            // 1. The name is assigned as selection
            // 2. The "popup window" activity is started as intent.
            SETTINGS.setSelection(this.name);
            startActivity(new Intent(MainActivity.this, PopUpActivity.class));
        }
    }

    /**
     * Used to convert the box_art_url string to a valid URL with the width
     * and height declared with DisplayMetrics
     * @param box_art_url
     * @param width
     * @param height
     * @return
     */
    private String convertToReadableURL(String box_art_url, int width, int height) {
        return box_art_url.replaceAll("\\{width\\}x\\{height\\}", width + "x" + height);
    }
}
