package com.grvmishra788.pay_track;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + MainActivity.class.getName();

    //tabLayout & viewPager variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    //
    private DrawerLayout drawer;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init tabLayout and viewPager
        mTabLayout = findViewById(R.id.layoutBottomTabs);
        mViewPager = findViewById(R.id.viewPager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new AccountsFragment(), getString(R.string.tab_1_name));
        mViewPagerAdapter.addFragment(new TransactionsFragment(), getString(R.string.tab_2_name));
        mViewPagerAdapter.addFragment(new DebtsFragment(), getString(R.string.tab_3_name));
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //set Transactions tab as default
        mViewPager.setCurrentItem(1);

        //setup action bar and navigation drawer
        setUpToolBarAndNavDrawer();
        Log.i(TAG, "onCreate() ends!");
    }

    //function to setup action bar and navigation drawer
    private void setUpToolBarAndNavDrawer() {
        //set action bar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup drawer toggle
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUpNavigationView();
    }

    private void setUpNavigationView() {
        Log.i(TAG,"setUpNavigationView()");
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "onNavigationItemSelected() called ");

                //get ID of menu item
                int id = item.getItemId();

                // Handle navigation view item clicks here.
                if (id == R.id.nav_about) {
                    //TODO::create AboutActivity
                } else if (id == R.id.nav_setting) {
                    //TODO::create SettingsActivity
                } else if (id == R.id.nav_categories){
                    Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_all){
                    //just close drawers
                } else {
                    Log.e(TAG,"No match for navigation menu click!");
                }

                drawer.closeDrawer(GravityCompat.START);
                Log.d(TAG, "onNavigationItemSelected() completed ");
                return false; //false returned to keep 'ALL' menu item selected
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): resultCode - " + resultCode + " requestCode - "+ requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
