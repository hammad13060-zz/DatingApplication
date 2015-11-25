package com.hammad13060.datingapplication.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.hammad13060.datingapplication.DBHandlers.LikedUserDBHandler;
import com.hammad13060.datingapplication.DBHandlers.PeopleDBHandler;
import com.hammad13060.datingapplication.DBHandlers.UserDBHandler;
import com.hammad13060.datingapplication.R;
import com.hammad13060.datingapplication.helper.AppServer;
import com.hammad13060.datingapplication.helper.NSDHelper;

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
        } else if (id == R.id.action_logout) {
            LoginManager.getInstance().logOut();

            LikedUserDBHandler likedUserDBHandler = new LikedUserDBHandler(this, null, null, 1);
            likedUserDBHandler.deleteAllData();

            UserDBHandler userDBHandler = new UserDBHandler(this, null, null, 1);
            userDBHandler.deleteAllData();

            PeopleDBHandler peopleDBHandler = new PeopleDBHandler(this, null, null, 1);
            peopleDBHandler.deleteAllData();

            NSDHelper nsdHelper = NSDHelper.getInstance(this);
            nsdHelper.tearDown();

            AppServer myServer = AppServer.getInstance(this);
            myServer.killServer();

            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);

            finish();
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


    @Override
    protected void onStop() {
        super.onStop();
    }
}