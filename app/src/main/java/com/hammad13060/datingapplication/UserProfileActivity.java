package com.hammad13060.datingapplication;

import android.annotation.TargetApi;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;

public class UserProfileActivity extends MainActivity {

    public static final String TAG = "UserProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

    }

    protected void onResume() {
        super.onResume();
        executeProfileRequest();
    }


    private void requestProfileInfo() {
        String request_1 = "/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture";

        ImageView imgView = (ImageView)findViewById(R.id.profile_image_view);
        Bundle parameters = new Bundle();
        //parameters.putString("fields", "data");
        parameters.putBoolean("redirect", false);
        parameters.putInt("height", 300);
        parameters.putInt("width", 300);
        //parameters.putString("type", "normal");

        GraphRequest dp_request = new GraphRequest(AccessToken.getCurrentAccessToken(), request_1, parameters, HttpMethod.GET,  new GraphRequest.Callback() {

            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject obj = graphResponse.getJSONObject();
                try {
                    Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
                    Log.d(TAG, permissions.toString());
                    JSONObject profile_data = (JSONObject)obj.get("data");
                    String current_dp_url = (String) profile_data.get("url");
                    setProfilePicture(current_dp_url);

                } catch (JSONException e) {
                    Log.d(TAG, "something went wrong with setting the profile picture");
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
                            TextView txtView = (TextView) findViewById(R.id.user_name_view);
                            TextView genderView = (TextView) findViewById(R.id.gender_view);
                            String first_name = jsonObject.getString("first_name");
                            String gender = jsonObject.getString("gender");
                            txtView.setText("name: " + first_name);
                            genderView.setText("gender: " + gender);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        parameters = new Bundle();
        parameters.putString("fields", "id,first_name, gender, age_range");

        meRequest.setParameters(parameters);

        final GraphRequestBatch request;
        request = new GraphRequestBatch(
                dp_request,
                meRequest
        );

        request.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });

        dp_request.executeAsync();
        meRequest.executeAsync();
    }

    private void executeProfileRequest() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestProfileInfo();
            }
        });

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }*/

    /*@Override
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
    }*/

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setProfilePicture(String url) {
        ImageView imgView = (ImageView)findViewById(R.id.profile_image_view);

        Picasso.with(this)
                .load(url)
                .into(imgView);
    }
}
