package com.hammad13060.datingapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Hammad on 22-10-2015.
 */
public class Constants {
    public static final String SHARED_PREFERENCE = "com.hammad13060.datingapplication.USER_PREFERENCE";
    public static final String USER_DATA = "com.hammad13060.datingapplication.USER_DATA";
    public static final String WEB_SERVER_URL = "http://192.168.51.125/DatingApplication";


    public static List<User> peopleAround = null;

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
}
