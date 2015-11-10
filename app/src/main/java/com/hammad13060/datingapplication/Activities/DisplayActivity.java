package com.hammad13060.datingapplication.Activities;

import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.hammad13060.datingapplication.Adapters.DisplayActivityPagerAdapter;
import com.hammad13060.datingapplication.helper.NSDHelper;
import com.hammad13060.datingapplication.R;
import com.hammad13060.datingapplication.helper.AppServer;

public class DisplayActivity extends MainActivity {

    private DisplayActivityPagerAdapter mAdapter;
    private ViewPager mPager;
    private ActionBar actionBar = null;
    private AppServer myServer = null;
    private NSDHelper mNSDHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();


        // Specify that tabs should be displayed in the action bar.
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_display);


        mAdapter = new DisplayActivityPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        myServer = new AppServer(this);
        myServer.initializeServer();
        myServer.startServer();
        mNSDHelper = NSDHelper.getInstance(this);
    }

    protected void onResume() {

        super.onResume();

        if (myServer == null) {
            myServer = new AppServer(this);
            myServer.initializeServer();
            myServer.startServer();
        }

        if (mNSDHelper == null) {
            mNSDHelper = NSDHelper.getInstance(this);
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
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
    }*/

    @Override
    public void onStop() {
        super.onStop();
        myServer.killServer();
        mNSDHelper.tearDown();
    }
}
