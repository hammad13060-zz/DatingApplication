package com.hammad13060.datingapplication;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        myServer = new AppServer(this);
        myServer.initializeServer();
        myServer.startServer();
        mNSDHelper = NSDHelper.getInstance(this);

        //addTabs();

        /*mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });*/

        //PagerTabStrip tabs = (PagerTabStrip) findViewById(R.id.pager_title_strip);


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

    private ActionBar.TabListener createActionBarListener() {
        return new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };
    }

    //adding tabs to the activity
    private void addTabs() {

        actionBar.addTab(
                actionBar.newTab()
                        .setText(mAdapter.getPageTitle(DisplayActivityPagerAdapter.DISPLAY_PEOPLE))
                        .setTabListener(createActionBarListener()));

        actionBar.addTab(
                actionBar.newTab()
                        .setText(mAdapter.getPageTitle(DisplayActivityPagerAdapter.DISPLAY_MATCH))
                        .setTabListener(createActionBarListener()));

        actionBar.addTab(
                actionBar.newTab()
                        .setText(mAdapter.getPageTitle(DisplayActivityPagerAdapter.DISPLAY_PROFILE))
                        .setTabListener(createActionBarListener()));
    }

    @Override
    public void onStop() {
        super.onStop();
        myServer.killServer();
        mNSDHelper.tearDown();
    }

    /*@Override
    public void onFragmentInteraction(Uri uri) {

    }*/
}
