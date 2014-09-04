package com.jamesbishop.zwbapp2;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jamesbishop.zwbapp2.getdata.LinkifiedTextView;
import com.jamesbishop.zwbapp2.getdata.RuleArray;
import com.jamesbishop.zwbapp2.getdata.RulesDBAdapter;
import com.jamesbishop.zwbapp2.getdata.WftdaTagHandler;

import java.sql.SQLException;
import java.util.ArrayList;


public class RuleListFragment extends Fragment {

    private RulesDBAdapter db;
    private static String mRuleId;
    private ArrayList<RuleArray> mRules;
    private RulesAdapter mRulesAdapter;
    private ListView listview;
    private static final String TAG = "RuleListFragment";


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule_list, null);

        // Get the rule ID handed to it and set it to a String value.
        mRuleId = getArguments().getString("RULE_ID");
        Log.d(TAG, mRuleId);
        fillData(mRuleId);
        mRulesAdapter = new RulesAdapter(getActivity(), 0, mRules);
        listview = (ListView) v.findViewById(R.id.rule_list);
        listview.setAdapter(mRulesAdapter);


        return v;
    }

    private void fillData(String rule_id) {
        db = new RulesDBAdapter(getActivity());
        mRules = new ArrayList<RuleArray>();
        try {
            db.open();
            Cursor c = db.getRules(rule_id);
            while (c.moveToNext()) {
                RuleArray rule = new RuleArray();
                rule.setRuleId(c.getString(0));
                rule.setRuleContent(c.getString(1));
                mRules.add(rule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class RulesAdapter extends ArrayAdapter<RuleArray> {
        private ArrayList<RuleArray> rules;
        private LayoutInflater inflater;

        public RulesAdapter(Context context, int resource, ArrayList<RuleArray> rules) {
            super(context, resource);
            this.rules = rules;
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return rules.size();
        }

        // Not required, at this stage, but there for expandability
        public int getViewTypeCount() {
            return 1;
        }

        // As above. Just the one viewtype, but it's here in case I need more.
        public int getViewType() {
            return 0;
        }

        public class ViewHolder {
            public LinkifiedTextView rule_content;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            try {
                if (convertView == null) {
                    v = inflater.inflate(R.layout.rule_list_item, null);
                    holder = new ViewHolder();
                    holder.rule_content = (LinkifiedTextView) v.findViewById(R.id.list_item);
                    v.setTag(holder);
                } else {
                    holder = (ViewHolder) v.getTag();
                }
                String content = rules.get(position).getRuleContent();
                // TODO: Remove trailing whitespace added by fromHTML.
                holder.rule_content.setText(Html.fromHtml(content, null, new WftdaTagHandler()));
            } catch (Exception e) {

            }
            return v;
        }
    }
}
