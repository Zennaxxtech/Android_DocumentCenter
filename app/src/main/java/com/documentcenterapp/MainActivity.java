package com.documentcenterapp;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.home.HomeFragment;
import com.documentcenterapp.login.LoginActivity;
import com.documentcenterapp.setting.SettingFragment;
import com.documentcenterapp.uploadDownload.UploadDownloadFragment;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "[MainActivity] : ";

    private Context context = MainActivity.this;

    private Toolbar toolbar;

    private FrameLayout frame_container;

    private NavigationView navigationView;

    private LayoutInflater inflater = null;

    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private ImageView iv_navigation_icon;
    private TextView tv_title, tv_app_title, tv_logout;
    private CharSequence mTitle;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed = 0;

    private int position = Constants.VALUE_NOT_PROVIDED;

    private ImageView imageback;
    private ImageView iv_upload, iv_search;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = getIntent().getIntExtra(Constants.Intent.INTENT_MENU_INDEX, 0);
        Log.i(TAG, "onCreate(), position : " + position);

        tinyDB = new TinyDB(this);

        initData();
        initActionBar();
        initDrawer();
        disableNavigationViewScrollbars(navigationView);

        displayView(R.id.nav_home);
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        try {
            if (navigationView != null) {
                NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
                if (navigationMenuView != null) {
                    navigationMenuView.setVerticalScrollBarEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        Log.i(TAG, "initData() called ");

        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();

            iv_navigation_icon = (ImageView) findViewById(R.id.iv_navigation_icon);
            tv_app_title = (TextView) findViewById(R.id.tv_app_title);

            frame_container = (FrameLayout) findViewById(R.id.frame);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            View navHeaderView = navigationView.getHeaderView(0);
            imageback = (ImageView) navHeaderView.findViewById(R.id.imageback);
            imageback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            iv_navigation_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });

            iv_upload = (ImageView) findViewById(R.id.iv_upload);
            iv_search = (ImageView) findViewById(R.id.iv_search);

            tv_logout = (TextView) findViewById(R.id.tv_logout);
            tv_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tinyDB.remove(Constants.Preferences.PREF_LOGIN_DONE);
                    tinyDB.remove(Constants.Preferences.PREF_LOGIN_TYPE);
                    tinyDB.remove(Constants.Preferences.PREF_USER_NAME);
                    tinyDB.remove(Constants.Preferences.PREF_USER_PASSWORD);

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initActionBar() {
        Log.i(TAG, "initActionBar(), called : ");
        try {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDrawer() {
        Log.i(TAG, "initDrawer() called");

        try {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                    tv_app_title.setText("DocumentCenter");

                    //actionBar.setCustomView(null);
                    // calling onPrepareOptionsMenu() to hide action bar icons
                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);

                    tv_app_title.setText(mTitle);
                    //actionBar.setIcon(mIcon);

                    /*ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
                    actionBar.setCustomView(actionBarCustomView, layout);*/

                    // calling onPrepareOptionsMenu() to show action bar icons
                    invalidateOptionsMenu();

                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    //toolbar.setAlpha(1 - slideOffset / 2);
                }
            };
            mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    actionBarDrawerToggle.syncState();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleAndIcon(CharSequence title, int resId) {
        try {
            mTitle = title;
            actionBar.setTitle("");
            tv_app_title.setText(mTitle);

           /* ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(actionBarCustomView, layout);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent e) {
        Log.i(TAG, "onKeyDown() called");

        if (code == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "onKeyDown(), KeyEvent.KEYCODE_BACK");
            Log.i(TAG, "onKeyDown(), position : " + position);

            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
            } else if (position == 0 /*Constants.SlidingMenuAdminIndex.HOME_INDEX*/) {
                Log.i(TAG, "onKeyDown(), R.id.nav_home");
                MainActivity.this.finish();
            } else {
                displayView(R.id.nav_home);
                navigationView.setCheckedItem(R.id.nav_home);

                Log.i(TAG, "onKeyDown(), else");

                int fragments = getFragmentManager().getBackStackEntryCount();
                Log.i(TAG, "onKeyDown(), fragments : " + fragments);
                if (fragments > 0) {
                    super.onBackPressed();
                } else {
                    Log.i(TAG, "onKeyDown(), back_pressed : " + back_pressed);
                    if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                        super.onBackPressed();
                    } else {
                    }
                    back_pressed = System.currentTimeMillis();
                }
            }
            return true;
        } else {
            return super.onKeyDown(code, e);
        }
    }

    private void updateDrawer() {
        Log.i(TAG, "updateDrawer(), called : ");

        try {
            setTitleAndIcon(mTitle, 0);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Log.i(TAG, "onNavigationItemSelected(), Called");

        try {
            // Handle navigation view item clicks here.

            mTitle = item.getTitle();

            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }

            displayView(item.getItemId());
            item.setChecked(true);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void displayView(int itemId) {
        Log.i(TAG, "displayView(), Called");

        try {
            //creating fragment object
            Fragment fragment = null;
            //initializing the fragment object which is selected

            fragment = displayAdminView(itemId);

            if (fragment != null) {
                frame_container.removeAllViews();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

                updateDrawer();
            } else {
                // error in creating fragment
                Log.e(TAG, "displayView(), Error in creating fragment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Fragment displayAdminView(int itemId) {
        Log.i(TAG, "displayAdminView(), Called");

        //creating fragment object
        Fragment fragment = null;

        try {
            //initializing the fragment object which is selected

            if (itemId == R.id.nav_home) {
                position = 0;
                mTitle = context.getResources().getString(R.string.home);
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_upload_download) {
                position = 1;
                mTitle = context.getResources().getString(R.string.upload_download);
                fragment = new UploadDownloadFragment(0);
            } else if (itemId == R.id.nav_setting) {
                position = 2;
                mTitle = context.getResources().getString(R.string.setting);
                fragment = new SettingFragment();
            } else {
                fragment = new Fragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
