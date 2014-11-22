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
    private String ruleId;

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


                for (Element rule : listElems) {

                    // Edit links with a handler for Intents
                    Elements listLinks = rule.select("a");
                    for (Element link : listLinks) {
                        String oldLink = link.attr("href");
                        link.attr("href", "http://www.wftda.com" + oldLink);
                        Log.d(TAG, ruleId + ": " + link.attr("href"));
                    }

                    // Make headings bold
                    if (rule.hasClass("ruleSubHeader") || rule.hasClass("single")) {
                        //ruleContent = "<strong>" + ruleContent + "</strong>";
                        rule.wrap("<strong></strong>");
                    }

                    String ruleContent = rule.html();

                    String split[] = rule.text().split(" ", 2);
                    // If there's a numbered rule ID, use it. Otherwise, use the previous one (for ordering purposes).
                    if (split[0].length() > 0 && Character.isDigit(split[0].charAt(0)))
                        ruleId = split[0];

                    db.insertRule(ruleId, ruleContent);

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
