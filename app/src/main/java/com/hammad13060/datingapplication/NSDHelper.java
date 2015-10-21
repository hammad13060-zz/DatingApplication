package com.hammad13060.datingapplication;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Hammad on 15-10-2015.
 */
public class NSDHelper {

    private NsdServiceInfo mService = null;
    private NsdManager mNsdManager = null;
    private ServerSocket mServerSocket = null;
    private int mLocalPort;

    private String mServiceName = "dating_application";
    private static final String SERVICE_NAME = "dating_application";
    private String TAG = "NSDHelper";
    private String SERVICE_TYPE = "_http._tcp";

    Context context = null;

    NsdManager.RegistrationListener mRegistrationListener = null;
    NsdManager.DiscoveryListener mDiscoveryListener = null;
    NsdManager.ResolveListener mResolveListener = null;

    public NSDHelper(Context context) {

        this.context = context;
        initializeRegistrationListener();
        initializeDiscoveryListener();
        initializeResolveListener();
        initializeServerSocket();

    }

    public int getNSDPort() {
        return mLocalPort;
    }

    public ServerSocket getmServerSocket() {
        return mServerSocket;
    }

    public void registerService() {
        // Create the NsdServiceInfo object, and populate it.
        mService  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        mService.setServiceName(mServiceName);
        mService.setServiceType(SERVICE_TYPE);
        mService.setPort(mLocalPort);

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                mService, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        mNsdManager.discoverServices(String.valueOf(mService), NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();

                Log.d(TAG, "service registration successful");

                new SocketServerThread().start();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d(TAG, "service registration successful " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "service unregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.d(TAG, "service unregistration failed");
            }
        };
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(SERVICE_NAME)){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();

                connectHost(host, port);


            }
        };
    }

    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {
            mServerSocket = new ServerSocket(0);
            // Store the chosen port.
            mLocalPort =  mServerSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLocalIpAddress() {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    private void connectHost(InetAddress hostAddress, int port) {
        if (hostAddress == null) {
            Log.e(TAG, "Host Address is null");
            return;
        }

        //my ip address
        String ipAddress = getLocalIpAddress();
        JSONObject jsonData = new JSONObject();

        JSONObject serverData = new JSONObject();

        UserDBHandler handler= new UserDBHandler(context);
        User me = handler.getUser(AccessToken.getCurrentAccessToken().getUserId());
        try {
            jsonData.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            jsonData.put("name", me.get_name());
            jsonData.put("gender", me.is_gender());
            jsonData.put("age", me.get_age());
            jsonData.put("url", me.get_url());
            jsonData.put("ipAddress", ipAddress);

            serverData.put("ipAddress", hostAddress);
            serverData.put("port", port);

            new SocketServerTask().execute(jsonData, serverData);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "can't put request");
            return;
        }
    }

    private class SocketServerTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            JSONObject userData = params[0];
            JSONObject serverData = params[1];

            InetAddress serverAddress;
            int serverPort;
            try {
                serverAddress = (InetAddress)serverData.get("ipAddress");
                serverPort = serverData.getInt("port");
                try {
                    socket = new Socket(serverAddress, serverPort);

                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());

                    // transfer JSONObject as String to the server
                    dataOutputStream.writeUTF(userData.toString());
                    Log.i(TAG, "waiting for response from host");

                    // Thread will wait till server replies
                    boolean success;

                    String response = dataInputStream.readUTF();
                    if (response != null && response.equals("Connection Accepted")) {
                        success = true;
                    } else {
                        success = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    // close socket
                    if (socket != null) {
                        try {
                            Log.i(TAG, "closing the socket");
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // close input stream
                    if (dataInputStream != null) {
                        try {
                            dataInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // close output stream
                    if (dataOutputStream != null) {
                        try {
                            dataOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



    //multi threaded server code
    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            ServerSocket serverSocket = mServerSocket;

            while (true) {
                try {
                    new SavePeopleThread().execute(serverSocket.accept());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    private class SavePeopleThread extends AsyncTask<Socket, Void, Void> {
        @Override
        protected Void doInBackground(Socket... params) {
            Socket socket = params[0];

            DataInputStream dataInputStream  = null;
            DataOutputStream dataOutputStream = null;
            try {
                dataInputStream = new DataInputStream(
                        socket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());

                String messageFromClient, messageToClient, request;

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                final JSONObject jsondata;
                    jsondata = new JSONObject(messageFromClient);

                User user = new User(
                        jsondata.getString("user_id"),
                        jsondata.getString("name"),
                        jsondata.getBoolean("gender"),
                        jsondata.getInt("age"),
                        jsondata.getString("url")
                );
                PeopleDBHandler handler = new PeopleDBHandler(context);
                handler.addUser(user);

                messageToClient = "Connection Accepted";
                dataOutputStream.writeUTF(messageToClient);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

}
