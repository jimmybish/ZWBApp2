package com.jamesbishop.zwbapp2.getdata;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jamesbishop.zwbapp2.RuleMenuActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.jamesbishop.zwbapp2.RuleMenuActivity.setRefreshActionButtonState;

/**
 * Created by bishopj on 27/08/2014.
 */
public class getRules extends AsyncTask<String, Void, String> {

    private interfaces.getRulesListener mListener;
    private final Context mCtx;
    private static final String TAG = "getRules";

    public getRules(Context context) {
        this.mCtx = context;
    }

    public void setListener(interfaces.getRulesListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        RuleMenuActivity.rulesRefreshing = true;
        setRefreshActionButtonState();
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
            // Connect to the page and get the page title. Confirm connection
            // Log.d("getLists", "Connecting to " + strings[0]);
            Document doc = Jsoup.connect(strings[0]).get();

            Boolean gotPage = doc.title() != null ? true:false;
            // String title = gotPage ? "Page is " + doc.title(): "Connection failed";
            // Log.d("getLists", title);

            if (gotPage) {
                Elements listElems = doc.select("div.rule");

                // We've got a connection. Drop the rules table, ready to populate!
                RulesDBAdapter db;
                db = new RulesDBAdapter(mCtx);
                db.open();
                db.dropRules();

                // Gets a rule ID, as set by each element. If no id is supplied, this will
                // hold the previous value. Not perfect, but the best solution I can come up with.
                String rule_id;

                for (Element rule : listElems) {

                    String ruleContent = rule.html();
                    String split[] = rule.text().split(" ");
                    rule_id = split[0];
                    Log.d(TAG, rule_id + "|" + ruleContent);
                    db.insertRule(rule_id, ruleContent);

                    // Get the rule's full HTML. Not interested in titles.
                    /*
                    * Well, maybe I am. This will remain commented out for now.
                    if (rule.hasClass("ruleSection")) {
                        Log.d("getRules", "Don't want no titles");
                    } else {
                        String ruleContent = rule.html();
                        Log.d("getRules", ruleContent);

                    }
                    */

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
        RuleMenuActivity.rulesRefreshing = false;
        setRefreshActionButtonState();
        mListener.callback();
    }
}
