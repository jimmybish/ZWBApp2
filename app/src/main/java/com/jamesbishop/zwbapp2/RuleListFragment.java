package com.jamesbishop.zwbapp2;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jamesbishop.zwbapp2.getdata.LinkifiedTextView;
import com.jamesbishop.zwbapp2.getdata.RuleArray;
import com.jamesbishop.zwbapp2.getdata.RulesDBAdapter;
import com.jamesbishop.zwbapp2.getdata.WftdaTagHandler;

import java.sql.SQLException;
import java.util.ArrayList;


public class RuleListFragment extends Fragment {

    private ArrayList<RuleArray> mRules;
    private static final String TAG = "RuleListFragment";
    private ListView listview;
    private RulesAdapter mRulesAdapter;
    public int selectedItem = -1;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_rule_list, null);


        // Get the rule ID handed to it and set it to a String value.
        String mRuleId = getArguments().getString("RULE_ID");
        Log.d(TAG, mRuleId);
        fillData(mRuleId);
        mRulesAdapter = new RulesAdapter(getActivity(), 0, mRules);

        // Set all the ListView things
        listview = (ListView) v.findViewById(R.id.rule_list);
        listview.setAdapter(mRulesAdapter);
        listview.setDividerHeight(0);

        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Make a rule detail thingy
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Action bar menu

                listview.setItemChecked(position, true);

                RuleContextMenu menu = new RuleContextMenu();
                ActionBarActivity activity = (ActionBarActivity) getActivity();
                ActionMode mode = activity.startSupportActionMode(menu);
                Object[] tags = new Object[3];
                tags[0] = view.getRootView().getContext();
                tags[1] = position;
                tags[2] = mRulesAdapter.rules.get(position).getRuleText();
                mode.setTag(tags);

                return false;
            }
        });

        return v;
    }




    private void fillData(String rule_id) {
        RulesDBAdapter db = new RulesDBAdapter(getActivity());
        mRules = new ArrayList<RuleArray>();
        try {
            db.open();
            Cursor c = db.getRules(rule_id);
            while (c.moveToNext()) {
                RuleArray rule = new RuleArray();
                rule.setRuleId(c.getString(0));
                rule.setRuleContent(c.getString(1));
                rule.setRuleText(c.getString(2));
                mRules.add(rule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
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

        public CharSequence removeWhitespace(CharSequence source) {
            if(source == null)
                return "";
            int i = source.length();

            // loop back to the first non-whitespace character
            while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
            }
            return source.subSequence(0, i+1);
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
                Spanned html = Html.fromHtml(content, null, new WftdaTagHandler());
                CharSequence trimmed = removeWhitespace(html);
                holder.rule_content.setText(trimmed);
                holder.rule_content.setLongClickable(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return v;
        }
    }
}
