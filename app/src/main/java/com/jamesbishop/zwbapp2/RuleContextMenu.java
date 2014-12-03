package com.jamesbishop.zwbapp2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * Created by bishopj on 25/11/2014.
 */
public class RuleContextMenu implements ActionMode.Callback {
    private String TAG = "RuleContextMenu";

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_rule_list, menu);
        Log.d(TAG, "Menu inflated");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

        Object[] tags = (Object[]) actionMode.getTag();
        RuleListActivity activity = (RuleListActivity) tags[0];
        int index = (Integer) tags[1];
        String content = (String) tags[2];

        switch (menuItem.getItemId()) {
            case R.id.menuCopy:
                // TODO: Clean the HTML out of this. How? .... Ummmm.... Jsoup?

                ClipboardManager clip = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("WFTDA Rule", content);
                clip.setPrimaryClip(data);
                Log.d(TAG, content);
                Toast.makeText(activity, "Copied to Clipboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.menuShare:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, content);
                activity.startActivity(Intent.createChooser(i, "Share Rule"));
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }
}
