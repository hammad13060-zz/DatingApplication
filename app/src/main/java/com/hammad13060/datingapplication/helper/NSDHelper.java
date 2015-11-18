package com.hammad13060.datingapplication.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.facebook.AccessToken;
import com.hammad13060.datingapplication.DBEntity.Person;
import com.hammad13060.datingapplication.DBEntity.User;
import com.hammad13060.datingapplication.DBHandlers.PeopleDBHandler;
import com.hammad13060.datingapplication.DBHandlers.UserDBHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Hammad on 15-10-2015.
 */
public class NSDHelper {

    HashMap<String, Boolean> personConnected = null;

    SharedPreferences me_data = null;

    private static NSDHelper instance = null;

    private  NsdServiceInfo mService = null;
    private NsdManager mNsdManager = null;
    private ServerSocket mServerSocket = null;
    private int mLocalPort;

    private static String mServiceName = "dating_application";
    private static final String SERVICE_NAME = "dating_application";
    private static String TAG = "NSDHelper";
    private static final String SERVICE_TYPE = "_http._tcp.";

    Context context = null;

    private NsdManager.RegistrationListener mRegistrationListener = null;
    private NsdManager.DiscoveryListener mDiscoveryListener = null;
    private  NsdManager.ResolveListener mResolveListener = null;

    private NSDHelper(Context context) {

        this.context = context;

    }

    public int getNSDPort() {
        return mLocalPort;
    }

    public ServerSocket getmServerSocket() {
        return mServerSocket;
    }

    public static NSDHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NSDHelper(context);
            instance.registerService();
        }

        return instance;
    }

    public void registerService() {

        personConnected = new HashMap<>();

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        // Create the NsdServiceInfo object, and populate it.
            initializeServerSocket();
            initializeRegistrationListener();
            //initializeResolveListener();
            initializeDiscoveryListener();

            mService = new NsdServiceInfo();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            mService.setServiceName(mServiceName);
            mService.setServiceType(SERVICE_TYPE);
            mService.setPort(mLocalPort);

            mNsdManager.registerService(
                    mService, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
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
                if (mServerSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    Log.d(TAG, "gonna resolve service now");
                    mNsdManager.resolveService(service, initializeResolveListener());
                   //mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "fut" + service);
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

    public NsdManager.ResolveListener initializeResolveListener() {
        return new NsdManager.ResolveListener() {

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

                connectHost(host, AppServer.SERVER_PORT);


            }
        };
    }

    public void tearDown() {
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mNsdManager.unregisterService(mRegistrationListener);
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        mResolveListener = null;

        instance = null;

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

    private void connectHost(InetAddress hostAddress, int port) {
        if (hostAddress == null) {
            Log.e(TAG, "Host Address is null");
            return;
        }

        Log.d(TAG, "FORMING JSON OBJECT");

        //UserDBHandler handler= UserDBHandler.getInstance(context);
        //try {
            UserDBHandler handler = new UserDBHandler(context, null, null, 1);

            User meUser = handler.getUser(AccessToken.getCurrentAccessToken().getUserId());
            JSONObject meJSON = Constants.constructUserJson(meUser);

            //new Thread(new SendDataToServer(jsonData, hostAddress, port)).start();

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                socket = new Socket(hostAddress, port);

                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                // transfer JSONObject as String to the server
                dataOutputStream.writeUTF(meJSON.toString());
                Log.i(TAG, "waiting for response from host");

                // Thread will wait till server replies
                boolean success;

                String response = dataInputStream.readUTF();
                if (response != null) {
                    Person person = Constants.personJsonToUser(new JSONObject(response));
                    PeopleDBHandler peopleDBHandler = new PeopleDBHandler(context, null, null, 1);
                    peopleDBHandler.addUser(person);

                    Intent peopleAroundIntent = new Intent();
                    peopleAroundIntent.setAction("com.hammad13060.datingapplication.PEOPLE_AROUND_RECEIVER");
                    context.sendBroadcast(peopleAroundIntent);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                // close socket
                if (socket != null) {
                    try {
                        Log.e(TAG, "closing the socket");
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

        //}
        /*catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "can't put request");
            return;
        }*/
    }
}
