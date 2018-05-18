package com.android.course.twitchbrowserapp;

/**
 * Created by Emil S. Kolvig-Raun on 20-04-2018.
 */

/**
 * A static class that allows for easy retrieval of information.
 *  Holds all URLS used to retrieve responses from the Twitch API.
 *
 *  Responsible for keeping the user selections.
 */
public class SETTINGS {

    //If twitch accepts our GET we ask to get the data returned in a JSON format.
    private static final String HEADER_CLIENT_ID = "hf6aoclq1ddt9w5tfa5o6qzybqs3g1";
    private static final String HEADER_ACCEPT = "application/vnd.twitchtv.v5+json";

    private static final String REQUEST_GAME_URL = "https://api.twitch.tv/kraken/streams/?game=";
    private static final String REQUEST_NAME_URL = "http://tmi.twitch.tv/group/user/";
    private static final String REQUEST_TOP_URL = "https://api.twitch.tv/helix/games/top";

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

    public static String getNameRequestURL(){
        return REQUEST_NAME_URL;
    }
    public static String getGameRequestURL(){
        return REQUEST_GAME_URL;
    }
    public static String getTopRequestURL(){
        return REQUEST_TOP_URL;
    }

    public static String getHeaderClientId(){ return HEADER_CLIENT_ID; }
    public static String getHeaderAccept(){ return HEADER_ACCEPT; }
}
