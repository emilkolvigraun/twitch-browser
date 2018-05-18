package com.android.course.twitchbrowserapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Creating a RequestQueue as a singleton, which makes the RequestQueue last the lifetime of the app.
 *
 * Created by Sahin on 18-05-2018.
 */


public class SingletonRequestQueue {
    private static SingletonRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private SingletonRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized SingletonRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
