package com.android.course.twitchbrowserapp;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static String user_list;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlist_layout,container,false);

        get_activity = getActivity();

        hint_left = view.findViewById(R.id.hint_left);
        user_list_text = view.findViewById(R.id.user_list);
        loading = view.findViewById(R.id.loadingLayout2);
        hint_left.setAlpha(0f);
        user_list_text.setAlpha(0f);

        return view;
    }

    private static void setPageContent(){
        hint_left.setText("\n" + "\n" + "you can swipe left..");
        user_list_text.setText(user_list);

        loading.setAlpha(0f);

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

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(get_activity);

            // Constructing URL
            String name_chosen = USER_PREFERENCES.getName().replaceAll("\\s", "%20").toLowerCase();
            String url = "http://tmi.twitch.tv/group/user/" + name_chosen + "/chatters";

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Prints", "length of response (chatters): length:" + response.length() + ", " + response.toString());

                    try {
                        JSONArray viewers = new JSONObject(response.toString()).getJSONObject("chatters").getJSONArray("viewers");


                        for(int i = 0; i < 1000; i++){
                            user_list += viewers.get(i).toString() + "\n";
                        }
                        user_list += "...";

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setPageContent();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
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

}
