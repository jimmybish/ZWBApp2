package com.jamesbishop.zwbapp2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jamesbishop.zwbapp2.common.AutoUpdateApk;

public class BaseActivity extends ActionBarActivity {

    public static Toolbar toolbar;
    public static Menu optionsMenu;
    public static boolean menuRefreshing;
    public static boolean rulesRefreshing;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        // Create and configure the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

    }




    /*
* This one changes the refresh icon to a spinner. If either AsyncTask is running, the spinner will show. When
* both have finished, it should go back to the refresh icon.
 */
    public static void setRefreshActionButtonState() {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.menuRefresh);
            if (refreshItem != null) {
                if (!menuRefreshing && !rulesRefreshing) {
                    refreshItem.setActionView(null);
                } else {
                    refreshItem.setActionView(R.layout.progress_refreshing);
                }
            }
        }
    }
}