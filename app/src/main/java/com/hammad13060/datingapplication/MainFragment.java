package com.hammad13060.datingapplication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;



public class MainFragment extends MainActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private AppServer myServer = null;
    private NSDHelper mNSDHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);




    }

    @Override
    protected void onStart() {
        super.onStart();
        myServer = new AppServer(this);
        myServer.initializeServer();
        myServer.startServer();
        mNSDHelper = NSDHelper.getInstance(this);
    }

    @Override
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

    @Override
    public void onStop() {
        super.onStop();
        myServer.killServer();
        mNSDHelper.tearDown();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DisplayProfileFragment(), "PROFILE");
        adapter.addFragment(new DisplayPeopleFragment(), "DISCOVER");
        adapter.addFragment(new DisplayMatchFragment(), "MATCHES");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
