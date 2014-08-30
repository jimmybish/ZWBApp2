package com.jamesbishop.zwbapp2;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamesbishop.zwbapp2.getdata.RulesDBAdapter;
import com.jamesbishop.zwbapp2.getdata.getMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bishopj on 28/08/2014.
 */

//TODO: This whole class needs looking at. Switch to Animated ExpandableListView.
// https://github.com/idunnololz/AnimatedExpandableListView/blob/master/src/com/example/animatedexpandablelistview/MainActivity.java

public class RuleMenuFragment extends Fragment {

    private AnimatedExpandableListView listView;
    public RulesMenuAdapter adapter;
    private static final String TAG = "RuleMenuFragment";
    public RuleMenuFragment() {    }


    // TODO: Possibly not needed. Comment out once it's all running and see.
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    // TODO: Possibly not needed. Comment out once it's all running and see.
    private Callbacks mCallbacks = sDummyCallbacks;

    private RulesDBAdapter db;
    private List<GroupItem> items = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule_menu, null);

        setHasOptionsMenu(true);

        // TODO: Move this to a button
        getMenu getmenu = new getMenu(getActivity());
        getmenu.execute("http://www.wftda.com/rules/all/20140301");


        db = new RulesDBAdapter(getActivity());
        try {
            db.open();
            fillData();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // Create the data adapter and fill it with data...
        adapter = new RulesMenuAdapter(getActivity());
        adapter.setData(items);

        // Initialise the listview and populate it with the above data
        Drawable divider = getResources().getDrawable(R.drawable.line);

        listView = (AnimatedExpandableListView) v.findViewById(R.id.expanderList);
        listView.setGroupIndicator(null);
        listView.setDivider(divider);
        listView.setAdapter(adapter);

        // Handle listview group onclicks (currently only expand and contract groups.
        // TODO: Handle empty groups like child entries... Oh god.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {


                // If it's open, close it. If it's not, open it.
                /*
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                */
                return true;
            }
        });

        return v;
    }

    private void fillData() {
        items = new ArrayList<GroupItem>();
        Cursor parentC = db.getTopMenu();

        while (parentC.moveToNext()) {
            // Clear the child array, ready for the next lot
            Cursor childC = null;


            GroupItem parent = new GroupItem();
            parent.ruleId = parentC.getString(0);
            parent.title = parentC.getString(1);
            Log.d(TAG, parent.ruleId);


            try {
                childC = db.getSecondMenu(parent.ruleId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (childC.moveToNext()) {
                ChildItem child = new ChildItem();
                child.content = childC.getString(0);
                Log.d(TAG, child.content);
                parent.items.add(child);
            }
            items.add(parent);
        }
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
            if (childPosition == getRealChildrenCount(groupPosition) - getRealChildrenCount(groupPosition)) {
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
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            final int position = groupPosition;
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                if (item.items.isEmpty()) {
                    convertView = inflater.inflate(R.layout.group_item_empty, parent, false);
                } else {
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



    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };








}
