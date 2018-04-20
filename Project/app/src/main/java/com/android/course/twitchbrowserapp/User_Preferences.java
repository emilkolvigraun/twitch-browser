package com.android.course.twitchbrowserapp;

/**
 * Created by Emil S. Kolvig-Raun on 20-04-2018.
 */

public class User_Preferences {


    private static String current_selection = null;


    public static void setSelection(String name){
        current_selection = name;
    }

    public static String getSelection(){
        return current_selection;
    }
}
