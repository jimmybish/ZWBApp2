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
public class getRules extends AsyncTask<String, Void, String> {

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
                Elements listElems = doc.select("div.rule");

                for (Element rule : listElems) {
                    // TODO: Not required once logging is done.

                    // Get the rule's full HTML. Not interested in titles.
                    if (rule.hasClass("ruleSection")) {
                        Log.d("getRules", "Don't want no titles");
                    } else {
                        String ruleContent = rule.html();
                        Log.d("getRules", ruleContent);

                        // TODO: Store the rules in the DB, I guess!
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
