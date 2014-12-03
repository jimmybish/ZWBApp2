package com.jamesbishop.zwbapp2.getdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jamesbishop.zwbapp2.RuleMenuActivity;

import java.sql.SQLException;

/**
 * Created by bishopj on 28/08/2014.
 */

public class RulesDBAdapter {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDB;

    private static final String TAG = "RulesDBAdapter";
    private static final String DATABASE_NAME = "rulesdb";
    private static final int DATABASE_VERSION = 1;

    // TODO: Change the below to accommodate different rulesets (date picker in settings)
    private static final String KEY_RULESET = RuleMenuActivity.currentRuleset;
    public static String RULES_TABLE = "rules_" + KEY_RULESET;
    // This String will be set from preferences and remain static so other classes can use it.

    private static final String MENU_TABLE = "menu_" + KEY_RULESET;
    private static final String NOTES_TABLE = "notes_" + KEY_RULESET;

    // Columns in the rules table
    private static final String KEY_RULE_ID = "rule_id";
    private static final String KEY_RULE_CONTENT = "content";
    private static final String KEY_RULE_BOOKMARK = "bookmark";
    private static final String KEY_RULE_TEXT = "text"; // Pure text, no HTML. Good for sharing.

    // Columns in the menu table
    // KEY_RULE_ID
    private static final String KEY_RULE_TITLE = "rule_title";
    private static final String KEY_MENU_INDEX = "level";

    // Columns in the Notes table
    // KEY_RULE_ID
    private static final String KEY_NOTE_CONTENT = "note";
    private static final String KEY_RULESET_COLUMN = "ruleset";


    // Full queries to create the 2 required tables in the DB
    private static final String RULESTABLE_CREATE =
            "create table if not exists "
            + RULES_TABLE + " (_id integer primary key autoincrement, "
            + KEY_RULE_ID + " text, "
            + KEY_RULE_CONTENT + " text, "
            + KEY_RULE_BOOKMARK + " integer, "
            + KEY_RULE_TEXT + " text);";

    private static final String MENUTABLE_CREATE =
            "create table if not exists "
            + MENU_TABLE + " (_id integer primary key autoincrement, "
            + KEY_MENU_INDEX + " integer, "
            + KEY_RULE_ID + " text, "
            + KEY_RULE_TITLE + " text);";

    /*
    The Notes table will support multiple rulesets. The set will be stored
    in the column KEY_RULE_SET, which will match the table name of the appropriate ruleset.
    Then there will be options to view notes by rule, all notes for just the active ruleset, or all notes for all rulesets.
     */
    private static final String NOTESTABLE_CREATE =
            "create table if not exists "
            + NOTES_TABLE + " (_id integer primary key autoincrement, "
            + KEY_RULE_ID + " text, "
            + KEY_NOTE_CONTENT + " text, "
            + KEY_RULESET_COLUMN + " text);";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, RULESTABLE_CREATE);
            db.execSQL(RULESTABLE_CREATE);

            Log.w(TAG, MENUTABLE_CREATE);
            db.execSQL(MENUTABLE_CREATE);

            Log.w(TAG, NOTESTABLE_CREATE);
            db.execSQL(NOTESTABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + RULES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MENU_TABLE);
            onCreate(db);
        }
    }

    public RulesDBAdapter (Context ctx) {
        this.mCtx = ctx;
    }

    public RulesDBAdapter open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, 1);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDBHelper.close();
    }

    public void dropMenu() {
        mDB.execSQL("DROP TABLE IF EXISTS " + MENU_TABLE);
        mDB.execSQL(MENUTABLE_CREATE);
    }

    public void dropRules() {
        mDB.execSQL("DROP TABLE IF EXISTS " + RULES_TABLE);
        mDB.execSQL(RULESTABLE_CREATE);
    }

    // Insert a rule into the DB
    public long insertRule(String rule_id, String content, String text) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RULE_ID, rule_id + ".");
        initialValues.put(KEY_RULE_CONTENT, content);
        initialValues.put(KEY_RULE_TEXT, text);
        return mDB.insert(RULES_TABLE, null, initialValues);
    }

    // Insert a Menu entry into the DB
    public long insertMenu(String rule_id, String title, int index) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RULE_ID, rule_id + ".");
        initialValues.put(KEY_RULE_TITLE, title);
        initialValues.put(KEY_MENU_INDEX, index);
        return mDB.insert(MENU_TABLE, null, initialValues);
    }

    // Retrieve the top level menu
    public Cursor getTopMenu() {
        return mDB.query(true, MENU_TABLE, new String[] {KEY_RULE_ID, KEY_RULE_TITLE}, KEY_MENU_INDEX + "= 0", null, null, null, "_id", null);
    }

    public Cursor getSecondMenu(String section) throws SQLException {
        String args = KEY_RULE_ID + " like '" + section + "%' and " + KEY_MENU_INDEX + "= 1";
        String[] column = {KEY_RULE_ID, KEY_RULE_TITLE};
        Cursor mCursor = mDB.query(true, MENU_TABLE, column, args, null, null, null, "_id", null);
        return mCursor;
    }

    public Cursor getRules(String section) throws SQLException {

        // EXAMPLE for section 2: Select content from 20140301 where rule_id like '2.%' order by '_id';
        String args = KEY_RULE_ID + " like '" + section + "%';";
        String[] column = {KEY_RULE_ID, KEY_RULE_CONTENT, KEY_RULE_TEXT};
        Cursor mCursor = mDB.query(true, RULES_TABLE, column, args, null, null, null, "_id", null);
        return mCursor;
    }
}