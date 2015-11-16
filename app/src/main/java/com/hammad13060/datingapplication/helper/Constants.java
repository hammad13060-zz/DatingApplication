package com.hammad13060.datingapplication.helper;

import android.util.Log;

import com.hammad13060.datingapplication.DBEntity.Person;
import com.hammad13060.datingapplication.DBEntity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hammad on 22-10-2015.
 */
public class Constants {
    public static final String SHARED_PREFERENCE = "com.hammad13060.datingapplication.USER_PREFERENCE";
    public static final String USER_DATA = "com.hammad13060.datingapplication.USER_DATA";
    public static List<User> peopleAround = null;


    //public static final String WEB_SERVER_URL = "http://192.168.54.96/DatingAppServer";
    public static final String WEB_SERVER_URL = "http://10.0.0.7/DatingAppServer";
    public static void addPerson(User person) {
        if (peopleAround == null) {
            peopleAround = new ArrayList<>(0);
        }
        peopleAround.add(person);
        Log.e("Constants", "person added");
    }

    public static int totalPeopleAround() {
        if (peopleAround == null || peopleAround.size() <= 0) {
            return 0;
        }
        return peopleAround.size();
    }

    public static JSONObject constructUserJson(User user) {
        JSONObject userJSON = new JSONObject();
        try {
            userJSON.put("user_id", user.get_user_id());
            userJSON.put("name", user.get_name());
            userJSON.put("gender", user.is_gender());
            userJSON.put("age", user.get_age());
            userJSON.put("url", user.get_url());
            return userJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userJSON;
    }

    public static User jsonToUser(JSONObject userJSON) {
        try {
            return new User(
                    userJSON.getString("user_id"),
                    userJSON.getString("name"),
                    userJSON.getBoolean("gender"),
                    userJSON.getInt("age"),
                    userJSON.getString("url")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new User();
    }

    public static Person personJsonToUser(JSONObject userJSON) {
        try {
            return new Person(
                    userJSON.getString("user_id"),
                    userJSON.getString("name"),
                    userJSON.getBoolean("gender"),
                    userJSON.getInt("age"),
                    userJSON.getString("url")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Person();
    }

    public static JSONObject personconstructUserJson(Person user) {
        JSONObject userJSON = new JSONObject();
        try {
            userJSON.put("user_id", user.get_user_id());
            userJSON.put("name", user.get_name());
            userJSON.put("gender", user.is_gender());
            userJSON.put("age", user.get_age());
            userJSON.put("url", user.get_url());
            return userJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userJSON;
    }
}
