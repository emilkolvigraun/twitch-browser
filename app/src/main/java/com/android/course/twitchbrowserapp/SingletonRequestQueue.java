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

/**
 *  The Volley request queue.
 *  Allows for automatic scheduling of network requests.
 *  Can run multiple concurrent network requests.
 */
public class SingletonRequestQueue {
    private static SingletonRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    //Internal constructor
    private SingletonRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }


    //Singleton getInstance()
    public static synchronized SingletonRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonRequestQueue(context);
        }
        return mInstance;
    }

    // Returns the instantiated request-queue
    // If the request-queue is null, a new request-queue is added with the
    // corresponding context
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() returns the context
            // or in this case the activity, as an activity extends context.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
