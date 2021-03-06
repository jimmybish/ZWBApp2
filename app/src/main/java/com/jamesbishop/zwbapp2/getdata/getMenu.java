package com.jamesbishop.zwbapp2.getdata;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jamesbishop.zwbapp2.RuleMenuActivity;
import com.jamesbishop.zwbapp2.RuleMenuFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.jamesbishop.zwbapp2.RuleMenuActivity.setRefreshActionButtonState;

/**
 * Created by bishopj on 27/08/2014.
 */
public class getMenu extends AsyncTask<String, Void, String> {

    private Interfaces.getMenuListener mListener;
    private final Context mCtx;
    private static final String TAG = "getMenu";

    public getMenu (Context context) {
        this.mCtx = context;
    }

    public void setListener(RuleMenuFragment listener) {
        mListener = listener;
    }


    @Override
    protected void onPreExecute() {
        RuleMenuActivity.menuRefreshing = true;
        setRefreshActionButtonState();
        super.onPreExecute();
    }

    /*
    Get the Menu and populate the DB.
     */
    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
            // Connect to the page and get the page title. Confirm connection
            //Log.d(TAG, "Connecting to " + strings[0]);
            Document doc = Jsoup.connect(strings[0]).get();

            Boolean gotPage = doc.title() != null ? true:false;
            // String title = gotPage ? "Page is " + doc.title(): "Connection failed";
            // Log.d(TAG, title);

            if (gotPage) {
                Elements listElems = doc.select("div.ruleSection");

                RulesDBAdapter db;
                db = new RulesDBAdapter(mCtx);
                db.open();
                db.dropMenu(); // If there's an existing menu, get rid of it and make a new one.

                for (Element rule : listElems) {

                    String ruleText = rule.text();

                    // Get the rule and split it into useable values for the DB.
                    String[] ruleSplit = rule.text().split(" ");
                    String ruleNum = ruleSplit[0];

                    // Insert a parent selection (has a .), otherwise, insert a child selection
                    if (!ruleNum.contains(".")) {
                        //Log.d(TAG, "Parent " + ruleText);
                        db.insertMenu(ruleNum, ruleText, 0);
                    } else {
                        //Log.d(TAG, "Child " + ruleText);
                        db.insertMenu(ruleNum, ruleText, 1);
                    }
                }
                db.close();
            }

        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        RuleMenuActivity.menuRefreshing = false;
        setRefreshActionButtonState();
        mListener.callback();
    }
}
