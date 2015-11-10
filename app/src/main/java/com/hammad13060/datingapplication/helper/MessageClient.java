package com.hammad13060.datingapplication.helper;

import android.content.Context;

import java.util.Objects;

/**
 * Created by Hammad on 11-11-2015.
 */

//class made singleton b'coz we need only one message client per app user
public class MessageClient {
    private Context context = null;
    private static MessageClient instance = null;

    private MessageClient() {

    }

    private MessageClient(Context context) {
        this.context = context;
    }

    public MessageClient getInstance(Context context) {
        if (instance == null) {
            instance = new MessageClient(context);
        }
        return instance;
    }

}
