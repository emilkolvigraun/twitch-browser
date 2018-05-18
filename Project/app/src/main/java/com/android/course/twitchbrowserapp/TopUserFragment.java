package com.android.course.twitchbrowserapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TopUserFragment extends Fragment {
    private TextView text;
    private TextView name_text;
    private TextView status_text;
    private ImageView logo_view;
    private ImageView preview;
    private TextView hint_right;
    private Thread thread;

    private URL image_url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popupwindow,container,false);

        text = view.findViewById(R.id.textView_pop);
        name_text = view.findViewById(R.id.text_title_view);
        status_text = view.findViewById(R.id.status_description_view);
        hint_right = view.findViewById(R.id.hint_right);
        text.setAlpha(0f);
        name_text.setAlpha(0f);
        status_text.setAlpha(0f);
        hint_right.setAlpha(0f);

        logo_view = view.findViewById(R.id.stream_top_title);
        preview = view.findViewById(R.id.preview);

        logo_view.setAlpha(0f);
        preview.setAlpha(0f);

        thread = new Thread() {
            @Override
            public void run() {
                requestWithSomeHttpHeaders();
            }
        };
        thread.start();

        return view;
    }

    @Override
    public void onPause() {
        //DO NOTHING
        super.onPause();
    }

    private void setPopUpContent(ArrayList<String> info){
        new DownloadImagesFromURI().execute();

        text.setText("Viewers: " + info.get(1).toString() + "\n" + "Followers: " + info.get(2).toString() + "\n");
        name_text.setText(info.get(0).toString());
        status_text.setText("\n" + info.get(4).toString() + "\n" + "\n" + info.get(5).toString());

        Bitmap title_logo = BitmapFactory.decodeResource(getResources(), R.drawable.top_title);
        logo_view.setImageBitmap(title_logo);

        LinearLayout loading = getView().findViewById(R.id.loadingLayout);
        hint_right.setText("\n" + "\n" + "you can swipe right..");

        loading.setAlpha(0f);

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

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean indexExists(final JSONArray list, final int index) {
        return index >= 0 && index < list.length();
    }


    public void requestWithSomeHttpHeaders() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Constructing URL
        String game_chosen = USER_PREFERENCES.getSelection().replaceAll("\\s","%20");
        String url = "https://api.twitch.tv/kraken/streams/?game=" + game_chosen;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Prints", "length of response: " + response.length() + response.toString());
                try {
                    ArrayList<String> info = new ArrayList<>();
                    String gamesJSONString = response.getString("streams");

                    JSONArray jsonArr = new JSONArray(gamesJSONString);
                    if (indexExists(jsonArr, 0)){
                        JSONObject jsonObj = jsonArr.getJSONObject(0);

                        //Retrieve all values
                        String viewers = recurseKeys(jsonObj, "viewers");
                        String name = recurseKeys(jsonObj, "name");
                        String status = recurseKeys(jsonObj, "status");
                        String updated_last = recurseKeys(jsonObj, "updated_at");
                        String followers = recurseKeys(jsonObj, "followers");
                        String description = recurseKeys(jsonObj, "description");

                        info.add(name);
                        USER_PREFERENCES.setName(name);
                        UserListFragment.requestWithSomeHttpHeaders();

                        info.add(viewers);
                        info.add(followers);
                        info.add(updated_last);
                        info.add(status);
                        info.add(description);

                        JSONObject preview_image = new JSONObject(recurseKeys(jsonObj, "preview"));
                        URL url_preview_image = new URL(preview_image.get("large").toString());
                        image_url = url_preview_image;

                    } else {
                        info.add("No information available from Twitch.");
                    }
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

    private class DownloadImagesFromURI extends AsyncTask<URI, Integer, Long> {
        private Bitmap image;

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

        protected void onPostExecute(Long result) {
            preview.setImageBitmap(image);
        }
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
