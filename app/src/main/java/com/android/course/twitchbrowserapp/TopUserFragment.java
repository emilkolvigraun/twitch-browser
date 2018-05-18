package com.android.course.twitchbrowserapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Emil S. Kolvig-Raun on 17-05-2018.
 */

/**
 * This class is responsible for the "Top Streamer" page.
 * It used Volley to retrieve the right data and adds it to the request queue.
 */
public class TopUserFragment extends Fragment {
    //Declares all layout elements the needs manipulated
    private TextView text;
    private TextView name_text;
    private TextView status_text;
    private ImageView logo_view;
    private ImageView preview;
    private TextView hint_right;

    //Declares an image url, which is used to read the preview image and place it in an image view.
    private URL image_url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the layout build for this page
        // The container parameter is the parent view group, from the activities layout.
        //      ...in which the fragment is inserted.
        View view = inflater.inflate(R.layout.popupwindow,container,false);

        // Using the view, it is possible to locate the elements
        // By declaring the variables outside of this method, we allow for later manipulation
        text = view.findViewById(R.id.textView_pop);
        name_text = view.findViewById(R.id.text_title_view);
        status_text = view.findViewById(R.id.status_description_view);
        hint_right = view.findViewById(R.id.hint_right);
        logo_view = view.findViewById(R.id.stream_top_title);
        preview = view.findViewById(R.id.preview);

        // All elements are made invisible, during loading.
        text.setAlpha(0f);
        name_text.setAlpha(0f);
        status_text.setAlpha(0f);
        hint_right.setAlpha(0f);
        logo_view.setAlpha(0f);
        preview.setAlpha(0f);

        // Calls the requestWithSomeHttpHeaders method, which works the same way as the one in the MainActivity.
        requestWithSomeHttpHeaders();

        // Returns the view, which is the root of the layout.
        return view;
    }

    @Override
    public void onPause() {
        //DO NOTHING
        super.onPause();
    }

    /**
     * This method is called by the requestWithSomeHttpHeaders method.
     * It assigns all values to the corresponding layout elements.
     *
     * @param info - An arraylist which holds all String retrieved from the response.
     */
    private void setPopUpContent(ArrayList<String> info){
        // Used to decode the preview image stream
        new DownloadImagesFromURI().execute();

        // If no information is available, set test to something else.
        if (info.size() > 1){
            text.setText("Viewers: " + info.get(1).toString() + "\n" + "Followers: " + info.get(2).toString() + "\n");
            name_text.setText(info.get(0).toString());
            status_text.setText("\n" + info.get(4).toString() + "\n" + "\n" + info.get(5).toString());

            Bitmap title_logo = BitmapFactory.decodeResource(getResources(), R.drawable.top_title);
            logo_view.setImageBitmap(title_logo);

            hint_right.setText("\n" + "\n" + "you can swipe right..");
        } else {
            status_text.setText("\n" + info.get(0).toString() + "\n" + "\n");
        }


        // When all values are assigned the loading element is set to be transparent
        // Hiding it from all visuals
        LinearLayout loading = getView().findViewById(R.id.loadingLayout);
        loading.setAlpha(0f);

        // ..and the elements are all animated to show in 200 milliseconds.
        name_text.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        preview.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        text.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        status_text.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        logo_view.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        hint_right.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);

    }

    // This method is used to propagate index errors
    // might no longer be necessary but experienced problems in the past.
    private boolean indexExists(final JSONArray list, final int index) {
        return index >= 0 && index < list.length();
    }

    public void requestWithSomeHttpHeaders() {
        // Constructing URL by retrieving it from SETTINGS.
        // Replacing all whitespace with "%20" which represent a whitespace in an encoded URL.
        // Could have used "+" but because Twitch uses "%20" we do as well.
        String game_chosen = SETTINGS.getSelection().replaceAll("\\s","%20");
        String url = SETTINGS.getGameRequestURL() + game_chosen;

        //Initiates response listener from Volley library
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Constructs the "info" arraylist which is later passed on.
                    ArrayList<String> info = new ArrayList<>();
                    // Locates the "streams" key in the JSON array
                    String streamsJSONString = response.getString("streams");

                    // Checks if the first index exists
                    JSONArray jsonArr = new JSONArray(streamsJSONString);
                    if (indexExists(jsonArr, 0)){
                        JSONObject jsonObj = jsonArr.getJSONObject(0);

                        // Retrieve all values, by recursion.
                        // Return the first result, as users streaming are sorted by number of viewers.
                        String viewers = recurseKeys(jsonObj, "viewers");
                        String name = recurseKeys(jsonObj, "name");
                        String status = recurseKeys(jsonObj, "status");
                        String updated_last = recurseKeys(jsonObj, "updated_at");
                        String followers = recurseKeys(jsonObj, "followers");
                        String description = recurseKeys(jsonObj, "description");

                        // add the "name" to the info list
                        // and set the "name" in settings, making it a user selection.
                        info.add(name);
                        SETTINGS.setName(name);

                        // Tun the static requestWithSomeHttpHeaders in neighbor fragment
                        // as it uses the "name" user selection.
                        UserListFragment.requestWithSomeHttpHeaders();

                        // Adds the rest of the retrieved information to the info list.
                        info.add(viewers);
                        info.add(followers);
                        info.add(updated_last);
                        info.add(status);
                        info.add(description);

                        // Retrieve the image URL and set the image_url variable.
                        JSONObject preview_image = new JSONObject(recurseKeys(jsonObj, "preview"));
                        URL url_preview_image = new URL(preview_image.get("large").toString());
                        image_url = url_preview_image;

                    } else {
                        // If the first index doesn't exist the response is empty.
                        // Therefore, add this String.
                        info.add("No information available from Twitch.");
                    }

                    // Execute the setPopUpContext methods with the info list.
                    setPopUpContent(info);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Used to handle errors.
                // But none so far.
            }
        })
        {
            /**
             * Setting Custom headers in Volley request
             * @return header parameters
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                // Retrieves both parameters from SETTINGS.
                params.put("Client-ID", SETTINGS.getHeaderClientId());
                params.put("Accept", SETTINGS.getHeaderAccept());
                return params;
            }

        };
        // adds the JSON object request to the queue.
        SingletonRequestQueue.getInstance(this.getContext()).addToRequestQueue(jsonObjReq);

    }

    /**
     * Used to "download" the image from the retrieved uri.
     * Uses AsyncTask to perform in the background, not obstructing or holding back the main UI thread.
     */
    private class DownloadImagesFromURI extends AsyncTask<URI, Integer, Long> {
        private Bitmap image;

        // doInBackground performs a network based operation.
        @Override
        protected Long doInBackground(URI... uris) {
            try {
                if(image_url != null){
                    image = BitmapFactory.decodeStream(image_url.openConnection().getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Is called right when doInBackground finishes, and we can safely update the UI.
        // Assigns the image to the ImageView, preview.
        protected void onPostExecute(Long result) {
            preview.setImageBitmap(image);
        }
    }


    // Returns the first matching key
    public static String recurseKeys(JSONObject jObj, String findKey) throws JSONException {
        String finalValue = "";
        boolean _stop = false;

        // First checks if the JSONObject exists.
        // If it doesn't, we return an empty string.
        if (jObj == null) {
            return "";
        }

        // Instantiates an Iterator with the keys from the JSONObject.
        Iterator<String> keyItr = jObj.keys();
        // Instantiates a hashmap.
        Map<String, String> map = new HashMap<>();

        // Loads the JSon Object into a hash-map.
        while(keyItr.hasNext() || _stop == true) {
            String key = keyItr.next();
            map.put(key, jObj.getString(key));
        }

        // Recursion in hash-map to locate the key we are looking for.
        for (Map.Entry<String, String> e : (map).entrySet()) {
            String key = e.getKey();
            // If the key is the one we are looking for, return it.
            if (key.equalsIgnoreCase(findKey)) {
                _stop = true;
                return jObj.getString(key);
            }

            // else, read the value and recurse it.
            Object value = jObj.get(key);

            if (value instanceof JSONObject) {
                finalValue = recurseKeys((JSONObject)value, findKey);
            }
        }
        // key is not found :(
        return finalValue;
    }
}
