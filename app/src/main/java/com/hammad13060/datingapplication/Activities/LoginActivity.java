package com.hammad13060.datingapplication.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hammad13060.datingapplication.Activities.MainFragment;
import com.hammad13060.datingapplication.DBEntity.User;
import com.hammad13060.datingapplication.DBHandlers.UserDBHandler;
import com.hammad13060.datingapplication.R;
import com.hammad13060.datingapplication.helper.Constants;
import com.hammad13060.datingapplication.helper.JSONRequest;
import com.hammad13060.datingapplication.helper.MessageClientHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends FragmentActivity {
    private static final String WEB_URL = Constants.WEB_SERVER_URL + "/register_user.php";


    private static final String TAG = "loginActivity";


    //user profile temp variables
    private String name;
    private int age;
    private Boolean gender;
    private String url;

    private CallbackManager callbackManager;
    LoginButton loginButton;
    LoginManager loginManager;
    AccessToken accessToken;


    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for finding key hash of dev environment
        //Log.i(TAG, printKeyHash(this));
        pref = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        //initializing facebook sdk
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        //setting the layout
        setContentView(R.layout.activity_login);

        /*Button clickButton = (Button) findViewById(R.id.inst);
        final TypedValue typedValue = new TypedValue();
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String mssg = "1.Login with facebook.\n2.This app will work only if other users are also on the same wifi network.";
                AlertDialog.Builder myAlert = new AlertDialog.Builder(LoginActivity.this);

                myAlert.setMessage(mssg)
                        .setNeutralButton("Continue..", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("!Instructions For You!")
                        .setIcon(typedValue.resourceId)
                        .create();
                myAlert.show();

            }
        });*/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //System.setProperty("http.keepAlive", "false");

        //login button reference
        loginButton = (LoginButton) findViewById(R.id.login_button);

        //registering callback manager
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, loginCallback());

        //setting permissions of login button
        loginButton.setReadPermissions("user_photos", "user_about_me", "user_birthday", "user_location", "user_posts");

        //getting current access token
        accessToken = AccessToken.getCurrentAccessToken();

        //
        if (accessToken != null && accessToken.isExpired()) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("user_photos", "user_about_me", "user_birthday", "user_location", "user_posts"));
        } else if (accessToken != null && !accessToken.isExpired()) {
            //getPermanentToken();
            requestProfileInfo();
            enterMainApp();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //returns a callback for facebook login events
    private FacebookCallback<LoginResult> loginCallback() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "LOGGING IN");
                accessToken = loginResult.getAccessToken();
                requestProfileInfo();
                enterMainApp();

            }

            @Override
            public void onCancel() {
                //app code
                LoginManager.getInstance().logOut();
                /*PeopleDBHandler.getInstance(getApplicationContext()).close();
                UserDBHandler.getInstance(getApplicationContext()).close();*/
                Log.d(TAG, "LOGGING OUT");
            }

            @Override
            public void onError(FacebookException e) {
                //app code
                Log.d(TAG, "error");
            }
        };
    }





    //function to enter main app
    private void enterMainApp() {
        Intent intent = new Intent(this, MainFragment.class);
        startActivity(intent);
        //finish();
    }

    private void requestProfileInfo() {
        String request_string = "/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture";
        Bundle parameters = new Bundle();
        parameters.putBoolean("redirect", false);
        parameters.putInt("height", 300);
        parameters.putInt("width", 300);

        GraphRequest dp_request = new GraphRequest(AccessToken.getCurrentAccessToken(), request_string, parameters, HttpMethod.GET,  new GraphRequest.Callback() {

            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject obj = graphResponse.getJSONObject();
                try {
                    JSONObject profile_data = (JSONObject)obj.get("data");
                    String current_dp_url = (String) profile_data.get("url");
                    url = current_dp_url;

                } catch (JSONException e) {
                    Log.d(TAG, "something went wrong with profile picture request");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.d(TAG, graphResponse.getError().toString());
                }
            }
        });


        //dp_request.setParameters(parameters);

        // request for name, gender, age_range
        GraphRequest meRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            name = jsonObject.getString("first_name");
                            String gender_string = jsonObject.getString("gender");
                            if (gender_string.equals("male")) {
                                gender = false;
                            } else {
                                gender = true;
                            }
                            age = 21;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        //saveUserToDB();

        parameters = new Bundle();
        parameters.putString("fields", "id,first_name, gender, age_range");

        meRequest.setParameters(parameters);

        final GraphRequestBatch request;
        request = new GraphRequestBatch(
                meRequest,
                dp_request
        );
        meRequest.executeAndWait();
        dp_request.executeAndWait();
        saveUserToDB();
        registerUser();
    }

    //registering user on php server
    private void registerUser() {
        //volley request object
        RequestQueue volleyRequest = Volley.newRequestQueue(this);

        //creating user data for user registeration
        final JSONObject object = new JSONObject();
        try {
            object.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            object.put("name", name);
            object.put("age", age);
            object.put("gender", gender);
            object.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //response listener for http request
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                boolean registered  = false;
                try {
                    registered = response.getBoolean("registered");
                    if (registered == false) {
                        Log.d(TAG, "USER REGISTRATION COMPLETE");
                    } else {
                        Log.d(TAG, "USER ALREADY REGISTERED");
                    }

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("male", true);
                    editor.putBoolean("female", true);
                    editor.commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        //error response listener for http request
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "POST FAILED");
            }
        };

        JSONRequest request = new JSONRequest(
                Request.Method.POST, WEB_URL, null,
                responseListener, errorListener, object
        );
        volleyRequest.add(request);

    }

    private void saveUserToDB() {
        User user = new User(
                AccessToken.getCurrentAccessToken().getUserId(),
                name,
                gender,
                age,
                url
        );
        UserDBHandler handler = new UserDBHandler(this, null, null, 1);
        handler.addUser(user);
    }
}
