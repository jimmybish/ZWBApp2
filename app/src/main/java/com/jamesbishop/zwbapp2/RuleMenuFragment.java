package com.jamesbishop.zwbapp2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesbishop.zwbapp2.getdata.RulesDBAdapter;
import com.jamesbishop.zwbapp2.getdata.getMenu;
import com.jamesbishop.zwbapp2.getdata.getRules;
import com.jamesbishop.zwbapp2.getdata.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bishopj on 28/08/2014.
 *
 * This class uses AnimatedExpandableListView. Credits for that are in AnimatedExpandableListView.java.
 *
 * I'm not sure I like how much code is actually in this class (Hard to navigate), but whatever. I think
 * I prefer the fragment loading the data, rather than the activity. Best practices, be damned!
 */



public class RuleMenuFragment extends Fragment implements interfaces.getMenuListener, interfaces.getRulesListener {

    private AnimatedExpandableListView listView;
    public RulesMenuAdapter adapter;
    private RulesDBAdapter db;
    private List<GroupItem> items = null;
    Button emptyButton;
    onMenuSelectedListener mListener;

    private static final String TAG = "RuleMenuFragment";

    public RuleMenuFragment() {    }

    // The interface the Activity must implement
    public interface onMenuSelectedListener {
        public void onMenuSelected(String rule_id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onMenuSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement the listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule_menu, null);

        setHasOptionsMenu(true);


        // Read the DB and populate the list
        db = new RulesDBAdapter(getActivity());
        try {
            db.open();
            fillAdapter();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the data adapter and fill it with data...
        adapter = new RulesMenuAdapter(getActivity());
        adapter.setData(items);

        // Initialise the listview and populate it with the above data
        emptyButton = (Button) v.findViewById(R.id.refresh);
        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMenu();
                downloadRules();
            }
        });
        listView = (AnimatedExpandableListView) v.findViewById(R.id.expanderList);
        listView.setEmptyView(emptyButton);
        listView.setGroupIndicator(null);
        listView.setAdapter(adapter);



        // Handle listview group onclicks
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String rule_id = items.get(groupPosition).ruleId;
                // TODO: Launch the next fragment with rules
                mListener.onMenuSelected(rule_id);
                //Toast toast = new Toast(getActivity());
                //toast.makeText(getActivity(), rule_id, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Handle listview child onclicks
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String rule_id = items.get(groupPosition).items.get(childPosition).ruleId;
                mListener.onMenuSelected(rule_id);
                //Toast toast = new Toast(getActivity());
                //toast.makeText(getActivity(),rule_id, Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        return v;
    }


    // Starts the AsyncTasks to download all the things!
    private void downloadMenu() {
        getMenu getmenu = new getMenu(getActivity());
        getmenu.setListener(this);
        getmenu.execute("http://www.wftda.com/rules/all/20140301");
    }

    private void downloadRules() {
        getRules getrules = new getRules(getActivity());
        getrules.setListener(this);
        getrules.execute("http://www.wftda.com/rules/all/20140301/");
    }



    // Opens 2 cursors and cycles through them, populating the Items listgroup.
    private void fillAdapter() {
        items = new ArrayList<GroupItem>();
        Cursor parentC = db.getTopMenu();

        while (parentC.moveToNext()) {
            // Clear the child array, ready for the next lot
            Cursor childC = null;


            GroupItem parent = new GroupItem();
            parent.ruleId = parentC.getString(0);
            parent.title = parentC.getString(1);

            try {
                childC = db.getSecondMenu(parent.ruleId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (childC.moveToNext()) {
                ChildItem child = new ChildItem();
                child.ruleId = childC.getString(0);
                child.content = childC.getString(1);
                parent.items.add(child);
            }
            items.add(parent);
        }
    }

    // This is called when the getMenu AsyncTask completes... Hopefully....
    @Override
    public void callback() {
        items.clear();
        try {
            db.open();
            fillAdapter();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.items.addAll(items);
        adapter.notifyDataSetChanged();
    }


    // Store the group title and create an ArrayList of children for each group
    private static class GroupItem {
        String title;
        String ruleId;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    // Store the text for each child item
    private static class ChildItem {
        String content;
        String ruleId;
    }

    // ViewHolders with the appropriate TexViews (maybe add images to this...)
    private static class ChildHolder {
        TextView content;
    }
    private static class GroupHolder {
        TextView title;
    }


    // Going to try and get group info to use for layouts....

    public class RulesMenuAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {



        private LayoutInflater inflater;
        private List<GroupItem> items;

        public RulesMenuAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }




        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        @Override
        public int getRealChildTypeCount() {
            return 3;
        }

        @Override
        public int getRealChildType(int groupPosition, int childPosition) {
            int result = 0;
            if (childPosition == 0) {
                result = 1;
            }
            if (childPosition == getRealChildrenCount(groupPosition) -1) {
                result = 2;
            }
            return result;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);

            if (convertView == null) {
                holder = new ChildHolder();
                int childType = getRealChildType(groupPosition,childPosition);

                switch (childType) {
                    case 1:
                        convertView = inflater.inflate(R.layout.child_item_first, parent, false);
                        break;
                    case 2:
                        convertView = inflater.inflate(R.layout.child_item_last, parent, false);
                        break;
                    default:
                        convertView = inflater.inflate(R.layout.child_item, parent, false);
                        break;
                }


                holder.content =  (TextView) convertView.findViewById(R.id.childContent);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            holder.content.setText(item.content);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getGroupTypeCount() {
            return 2;
        }

        @Override
        public int getGroupType(int groupPosition) {
            int result = 0;
            if (getGroup(groupPosition).items.isEmpty()) {
                result = 1;
            }
            return result;
        }

        /*
        Exactly the same as getChildView, but for the groups. Using the Holder pattern for groups since there are 2
        different layouts, depending on whether the group has any children or not.
        If it does have children, use the layout with the ImageView, then set a listener to that ImageView to
        expand or collapse the group. I could do the same with one layout and show or hide the ImageView at runtime, but I think
        statically selecting the view when loaded may be less expensive. Not entirely sure.
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            final int position = groupPosition;
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                int groupType = getGroupType(groupPosition);

                switch (groupType) {
                    case 1: // Is empty
                        convertView = inflater.inflate(R.layout.group_item_empty, parent, false);
                        break;
                    default:
                        convertView = inflater.inflate(R.layout.group_item, parent, false);

                        // Use the dropdown arrow to expand and contract
                        ImageView dropdown = (ImageView) convertView.findViewById(R.id.dropdown);
                        dropdown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listView.isGroupExpanded(position)) {
                                    listView.collapseGroupWithAnimation(position);
                                } else {
                                    listView.expandGroupWithAnimation(position);
                                }
                            }
                        });
                        break;
                }

                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }
            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }



    /*
    * All the options menu stuff. Show the refresh button and handle what it does.
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem refreshItem = menu.findItem(R.id.menuRefresh);
        refreshItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuRefresh:
                downloadMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
