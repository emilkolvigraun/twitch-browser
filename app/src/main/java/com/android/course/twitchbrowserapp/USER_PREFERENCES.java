package com.android.course.twitchbrowserapp;

/**
 * Created by Emil S. Kolvig-Raun on 20-04-2018.
 */

public class USER_PREFERENCES {


    private static String current_selection = null;
    private static String current_name = null;

    public static void setSelection(String name){
        current_selection = name;
    }

    public static String getSelection(){
        return current_selection;
    }

    public static void setName(String name){
        current_name = name;
    }

    public static String getName(){
        return current_name;
    }
}
