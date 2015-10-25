package com.hammad13060.datingapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hammad on 19-10-2015.
 */
public class UserDBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "dating_application.db";

    // Contacts table name
    private static final String TABLE_USERS = "users";

    //table columns
    private static final String USER_ID = "user_id";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String URL = "url";

    private static UserDBHandler instance = null;
    private static Object mutex= new Object();

    private UserDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static UserDBHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null)   instance = new UserDBHandler(context.getApplicationContext());
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_USERS + "' ( " +
                "'" + "_id" + "'" + " PRIMARY KEY INTEGER AUTOINCREMENT, " +
                "'" + USER_ID + "'" + " VARCHAR(255), " +
                "'" + NAME + "'" + " TEXT NOT NULL, " +
                "'" + GENDER + "'" + " BOOLEAN NOT NULL, " +
                "'" + AGE + "'" + " INTEGER NOT NULL, " +
                "'" + URL + "'" + " VARCHAR(5000) NOT NULL " +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            // Create tables again
            onCreate(db);
        }
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(USER_ID, user.get_user_id());
        values.put(NAME, user.get_name());
        values.put(GENDER, user.is_gender());
        values.put(AGE, user.get_age());
        values.put(URL, user.get_url());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public User getUser(String user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] { USER_ID,
                        NAME, GENDER, AGE, URL }, USER_ID + "=?",
                new String[] { user_id }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(
                             cursor.getString(0),
                             cursor.getString(1),
                             Boolean.parseBoolean(cursor.getString(2)),
                             Integer.parseInt(cursor.getString(3)),
                             cursor.getString(4)
        );

        return user;
    }



}
