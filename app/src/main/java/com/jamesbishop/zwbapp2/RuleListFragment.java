package com.jamesbishop.zwbapp2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.jamesbishop.zwbapp2.dummy.DummyContent;
import com.jamesbishop.zwbapp2.getdata.RulesDBAdapter;

import java.sql.SQLException;
import java.util.ArrayList;


public class RuleListFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String RULE_ID;


    // TODO: Rename and change types of parameters
    private String mRule;

    private RulesDBAdapter db;
    ArrayList<String> rules = new ArrayList<String>();




    // TODO: Rename and change types of parameters
    public static RuleListFragment newInstance(String rule_id) {
        RuleListFragment fragment = new RuleListFragment();
        Bundle args = new Bundle();
        args.putString(RULE_ID, rule_id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RuleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mRule = getArguments().getString(RULE_ID);

        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fillData(mRule);
        db.close();

        return super.onCreateView(inflator, container, savedInstanceState);
    }

    private void fillData(String rule) {
        Cursor c = null;
        try {
            c = db.getRules(rule);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (c.moveToNext()) {
            // TODO: Yep
        }
    }





    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // TODO: Rule Detail fragment, probably a new activity.

    }
}
