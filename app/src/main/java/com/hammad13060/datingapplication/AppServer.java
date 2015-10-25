package com.hammad13060.datingapplication;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Hammad on 25-10-2015.
 */
public class AppServer {

    private static final String TAG = "AppServer";
    public static final int SERVER_PORT = 6000;
    public static final String SERVER_REPLY = "Hey this is server!!!";

    private ServerSocket mServerSocket = null;
    private Context  context;
    private Thread serverThread = null;

    public AppServer(Context context) {
        this.context = context;
    }

    public void initializeServer() {
        try {
            mServerSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            Log.e(TAG, "problem in intializing server");
        }
    }

    public void startServer() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Socket helperSocket = null;
                    DataOutputStream outputStream = null;
                    DataInputStream inputStream = null;
                    if (Thread.interrupted()) {
                        return;
                    }
                    try {
                        helperSocket = mServerSocket.accept();
                        outputStream = new DataOutputStream(helperSocket.getOutputStream());

                        //reding response
                        inputStream = new DataInputStream(helperSocket.getInputStream());

                        String response = inputStream.readUTF();

                        try {
                            JSONObject user_data = new JSONObject(response);
                            User person = new User(
                                    user_data.getString("user_id"),
                                    user_data.getString("name"),
                                    user_data.getBoolean("gender"),
                                    user_data.getInt("age"),
                                    user_data.getString("url")
                            );
                            Log.e(TAG, "received data of ==> " + person.toString());
                            outputStream.writeUTF(SERVER_REPLY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (helperSocket != null) {
                            try {
                                helperSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        serverThread.start();
    }

    public void killServer() {
        if (serverThread != null) {
            serverThread.interrupt();
        }

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
