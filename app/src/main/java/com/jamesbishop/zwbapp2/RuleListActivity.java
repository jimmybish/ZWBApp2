package com.jamesbishop.zwbapp2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RuleListActivity extends BaseActivity {

    private String title;
    private String rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntents();

        if (title != null) {
            getSupportActionBar().setTitle(title);
        } else {
            getSupportActionBar().setTitle("Section " + rule);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RuleListFragment list = new RuleListFragment();
        Bundle args = new Bundle();
        args.putString("RULE_ID", rule);
        list.setArguments(args);
        ft.replace(R.id.menu_frame, list);
        ft.addToBackStack(null);
        ft.commit();

    }


    private void getIntents() {
        // Receive a full URL and take the ID from it
        Intent i = getIntent();
        Uri data = i.getData();
        String url;
        if (data != null) {
            url = data.toString();
        } else {
            url = i.getStringExtra("RULE");
        }

        title = i.getStringExtra("TITLE");
        String[] split = url.split("#", 2);
        rule = split[1];

    }



}
