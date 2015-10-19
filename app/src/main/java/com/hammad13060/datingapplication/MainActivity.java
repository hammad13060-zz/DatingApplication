package com.hammad13060.datingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

//this class serves as base class for all other classes which require user login
public class MainActivity extends AppCompatActivity {

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;

    GraphRequest request;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing facebook sdk
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        //setContentView(R.layout.activity_main);

        //creating a callback manager
        callbackManager = CallbackManager.Factory.create();

        //registering a tracker for access token
        registerTracker();

        if (!isLoggedin()) {
            showSignedOutUI();
        }

        if (AccessToken.getCurrentAccessToken().isExpired()) {
            refreshToken();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        //fetchPersonalData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_discovery_preference) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedin() {
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return true;
        }

        return false;
    }

    //token tracker
    private void registerTracker() {
        this.accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    showSignedOutUI();
                } else if (currentAccessToken.isExpired()) {
                    refreshToken();
                } else {
                    AccessToken.setCurrentAccessToken(currentAccessToken);
                    accessToken = currentAccessToken;
                }
                //setting the current access token for use
            }
        };
    }

    //refreshes a token and assigns it to access token variable
    private void refreshToken() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("user_photos", "user_about_me", "user_birthday", "user_location", "user_posts"));
        accessToken = AccessToken.getCurrentAccessToken();
    }

    // if user is not logged in then it redirects the user to login activity
    private void showSignedOutUI() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    /*private void fetchPersonalData() {
        runOnUiThread(new Runnable(){
            public void run() {

                request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.d("response", "data received");
                                try {

                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String birthday = object.getString("birthday");
                                    String gender = object.getString("gender");

                                    TextView view;

                                    view = (TextView)findViewById(R.id.id_view);
                                    view.setText(id);

                                    view = (TextView)findViewById(R.id.name_view);
                                    view.setText(name);

                                    view = (TextView)findViewById(R.id.gender_view);
                                    view.setText(gender);

                                    view = (TextView)findViewById(R.id.birthday_view);
                                    view.setText(birthday);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Application code
                            }
                        });

                //If there are stories, add them to the table
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,birthday,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });
    }

    void getPermanentToken(){
        new Runnable() {
            public void run(){
                JSONObject object = new JSONObject();
                try {
                    object.accumulate("token", AccessToken.getCurrentAccessToken());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }*/


}