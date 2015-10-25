package com.hammad13060.datingapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class PreferenceActivity extends MainActivity {


    private static final String TAG = "PreferenceActivity";
    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        setPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preference, menu);
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

    public void maleBoxSelected(View view) {
        Log.d(TAG, "Male checkbox clicked");
        CheckBox box = (CheckBox) view;

        if (box.isChecked()) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("male", true);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("male", false);
            editor.commit();
        }
    }

    public void femaleBoxSelected(View view) {
        Log.d(TAG, "Female checkbox clicked");
        CheckBox box = (CheckBox) view;

        if (box.isChecked()) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("female", true);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("female", false);
            editor.commit();
        }
    }

    private void setPreference() {

        boolean male_boolean = pref.getBoolean("male", true);
        boolean female_boolean = pref.getBoolean("female", true);

        CheckBox male_check_box = (CheckBox)findViewById(R.id.male_check_box);
        CheckBox female_check_box = (CheckBox)findViewById(R.id.female_check_box);

        male_check_box.setChecked(male_boolean);
        female_check_box.setChecked(female_boolean);

    }

}
