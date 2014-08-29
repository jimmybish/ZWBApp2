package com.jamesbishop.zwbapp2.getdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String RULES_TABLE = "rules_20140301";
    private static final String MENU_TABLE = "m_" + RULES_TABLE;

    // Columns in the rules table
    private static final String KEY_RULE_ID = "rule_id";
    private static final String KEY_RULE_CONTENT = "content";

    // Columns in the menu table
    // KEY_RULE_ID is the same value. No point doubling up, but this comment is a reminder it exists
    private static final String KEY_RULE_TITLE = "rule_title";
    private static final String KEY_MENU_INDEX = "level";

    // Full queries to create the 2 required tables in the DB
    private static final String RULESTABLE_CREATE =
            "create table "
            + RULES_TABLE + " (_id integer primary key autoincrement, "
            + KEY_RULE_ID + " text, "
            + KEY_RULE_CONTENT + " text);";

    private static final String MENUTABLE_CREATE =
            "create table "
            + MENU_TABLE + " (_id integer primary key autoincrement, "
            + KEY_MENU_INDEX + " integer, "
            + KEY_RULE_ID + " text, "
            + KEY_RULE_TITLE + " text);";

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

    // Insert a rule into the DB
    public long insertRule(String rule_id, String content) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RULE_ID, rule_id);
        initialValues.put(KEY_RULE_CONTENT, content);

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

        // EXAMPLE for section 2: Select rule_title where rule_id like '2.%' and index = 1 order by '_id';
        String args = KEY_RULE_ID + " like '" + section + "%' and " + KEY_MENU_INDEX + "= 1";
        Log.d (TAG, args);
        String[] column = {KEY_RULE_TITLE};

        Cursor mCursor = mDB.query(true, MENU_TABLE, column, args, null, null, null, "_id", null);
        //if (mCursor != null) {
        //    mCursor.moveToFirst();
        //}
        return mCursor;
    }

    public Cursor getRules(String section) throws SQLException {

        // EXAMPLE for section 2: Select content from 20140301 where rule_id like '2.%' order by '_id';
        String args = KEY_RULE_ID + " like '" + section + "%';";
        String[] column = {KEY_RULE_CONTENT};

        Cursor mCursor = mDB.query(true, RULES_TABLE, column, args, null, null, null, "_id", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}