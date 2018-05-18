package com.android.course.twitchbrowserapp;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sahin on 16-04-2018.
 */

public class UserListFragment extends Fragment{
    private static TextView user_list_text;
    private static TextView hint_left;
    private static LinearLayout loading;
    private static Activity get_activity;
    private static String user_list = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the view with the corresponding fragment layout.
        // and inserts it into the container.
        View view = inflater.inflate(R.layout.fragment_userlist_layout,container,false);

        // Instantiating get_activity, for later use.
        get_activity = getActivity();

        // Locates all elements.
        hint_left = view.findViewById(R.id.hint_left);
        user_list_text = view.findViewById(R.id.user_list);
        loading = view.findViewById(R.id.loadingLayout2);
        hint_left.setAlpha(0f);
        user_list_text.setAlpha(0f);

        // Returns the view, the new root of the layout.
        return view;
    }

    private static void setPageContent(){
        // Assigns all values to their respective elements.
        hint_left.setText("\n" + "\n" + "you can swipe left..");
        user_list_text.setText(user_list);

        // Turns the loading element invisible
        loading.setAlpha(0f);

        // Animates all finished elements to show after 200 milliseconds.
        hint_left.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
        user_list_text.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
    }

    @Override
    public void onPause() {
        //DO NOTHING
        super.onPause();
    }

    public static void requestWithSomeHttpHeaders() {
        // Constructing URL by retrieving the user selection and setting
        // it to lower_case, as the twitch API demands just that.
        // and putting it together with the request URL.
        String name_chosen = SETTINGS.getName().toLowerCase();
        String url = SETTINGS.getNameRequestURL() + name_chosen + "/chatters";

        //Initiates response listener from Volley library
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // On response, get the "viewers" array.
                    JSONArray viewers = new JSONObject(response.toString()).getJSONObject("chatters").getJSONArray("viewers");

                    // Loading first 200 viewers...
                    // and add them to the user_list.
                    user_list += "\n";
                    for(int i = 0; i < 200; i++){
                        user_list += (i+1) + ". " + viewers.get(i).toString() + "\n";
                    }
                    user_list += "\n" + " ...and " + (viewers.length()-200) + " more." ;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // When finished, call the setPageContent() method and reset the user_list.
                setPageContent();
                user_list = "";
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // For handling errors, but none so far.
            }
        }) {
            /**
             * Setting Custom headers in Volley request
             * @return header parameters
             */
            @Override
            public Map<String, String> getHeaders() {
                // Retrieves both parameters from SETTINGS.
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-ID", SETTINGS.getHeaderClientId());
                params.put("Accept", SETTINGS.getHeaderAccept());
                return params;
            }
        };
     // Use a singleton approach for request queue
     SingletonRequestQueue.getInstance(get_activity).addToRequestQueue(jsonObjReq);
    }
}
