package com.jamesbishop.zwbapp2.getdata;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by bishopj on 27/08/2014.
 */
public class getLists extends AsyncTask<String, Void, String> {

    String[] topLevel;
    String[] secLevel;

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
            // Connect to the page and get the page title. Confirm connection
            Log.d("getLists", "Connecting to " + strings[0]);
            Document doc = Jsoup.connect(strings[0]).get();

            Boolean gotPage = doc.title() != null ? true:false;
            String title = gotPage ? "Page is " + doc.title(): "Connection failed";
            Log.d("getLists", title);

            if (gotPage) {
                Elements listElems = doc.select("div.ruleSection");

                for (Element rule : listElems) {
                    // TODO: Not required once logging is done.
                    String ruleText = rule.text();

                    // Get the rule and split it into useable values for the DB.
                    String[] ruleSplit = rule.text().split(" ");
                    String ruleNum = ruleSplit[0];
                    String ruleTitle = ruleSplit[1];


                    /*
                    Plan: Have 3 fields in the menu DB. Number (ruleNum), Title(ruleTitle) and index(int 0 or 1).

                    When retrieving the menu, run a query like
                    String query = "SELECT ruleNum, ruleTitle FROM 20140301 WHERE index = 0;"
                    String query = "SELECT ruleNum, ruleTitle FROM 20140301 WHERE index = 1 AND ruleNum LIKE '" + selectedRule + "%';"

                    Preferably, these will be added as parents and children in an Expandable Listview
                     */
                    if (!ruleNum.contains(".")) {
                        Log.d("getLists", "Top " + ruleText);
                        // TODO: Populate the top level entries
                    } else {
                        Log.d("getLists", "Second " + ruleText);
                        // TODO: Populate the 2nd level entries
                    }
                }
            }

        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
