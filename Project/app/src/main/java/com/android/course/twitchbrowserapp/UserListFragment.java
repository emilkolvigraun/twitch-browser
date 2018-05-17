package com.android.course.twitchbrowserapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sahin on 16-04-2018.
 */

public class UserListFragment extends Fragment{
    private TextView hint_left;
    private LinearLayout loading;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlist_layout,container,false);

        hint_left = view.findViewById(R.id.hint_left);
        loading = view.findViewById(R.id.loadingLayout2);
        hint_left.setAlpha(0f);

        requestWithSomeHttpHeaders();
        return view;
    }

    private void setPageContent(){
        hint_left.setText("\n" + "\n" + "you can swipe left..");

        loading.setAlpha(0f);

        hint_left.animate()
                .setDuration(200)
                .alpha(1f)
                .setListener(null);
    }

    @Override
    public void onPause() {
        //DO NOTHING
        super.onPause();
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

                    //yet to be implemented
                    setPageContent();


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
}
