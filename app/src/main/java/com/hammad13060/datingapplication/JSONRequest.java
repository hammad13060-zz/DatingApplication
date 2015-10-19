package com.hammad13060.datingapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Hammad on 19-10-2015.
 */
public class JSONRequest extends JsonObjectRequest{

    private JSONObject object = null;

    public JSONRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, JSONObject object) {
        super(method, url, jsonRequest, listener, errorListener);
        this.object = object;
    }

    @Override
    public byte[] getBody() {
        return object.toString().getBytes();
    }


}
