package com.hammad13060.datingapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hammad on 21-10-2015.
 */
public class PeopleDBHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private Context activity = null;
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "dating_application.db";

    // Contacts table name
    private static final String TABLE_PEOPLE = "people";

    //table columns
    private static final String USER_ID = "user_id";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String URL = "url";


    private static PeopleDBHandler instance = null;
    private static Object mutex= new Object();

    private PeopleDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.activity = context;
    }

    public static PeopleDBHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (mutex) {
              if (instance == null)  instance = new PeopleDBHandler(context.getApplicationContext());
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_PEOPLE + "( " +
                USER_ID + " VARCHAR(255) PRIMARY KEY NOT NULL," +
                NAME + " TEXT NOT NULL, " +
                GENDER + " BOOLEAN , " +
                AGE + " INTEGER NOT NULL, " +
                URL + " TEXT NOT NULL " +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);

        // Create tables again
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(USER_ID, user.get_user_id());
        values.put(NAME, user.get_name());
        values.put(GENDER, user.is_gender());
        values.put(AGE, user.get_age());
        values.put(URL, user.get_url());

        db.insert(TABLE_PEOPLE, null, values);

    }

    public User getUser(String user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PEOPLE, new String[] { USER_ID,
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

    public List<User> getAllUser() {
        SharedPreferences pref = activity.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);

        boolean male = pref.getBoolean("male", true);
        boolean female = pref.getBoolean("female", true);

        SQLiteDatabase db = this.getReadableDatabase();

        List<User> list = new ArrayList<User>(0);

        String query = null;
        String[] comparison = null;

        if (male == true && female == false) {
            query = GENDER + "=?";
            comparison = new String[] {"No"};
        } else if (male == false && female == true) {
            query = GENDER + "=?";
            comparison = new String[] {"Yes"};
        } else if (male && female) {
            query = GENDER + "=? OR " + GENDER + "=?";
            comparison = new String[] {"No", "Yes"};
        } else {
            return list;
        }

        Cursor cursor = db.query(TABLE_PEOPLE, new String[] { USER_ID,
                        NAME, GENDER, AGE, URL }, query,
                comparison, null, null, null, null);

        while(cursor.moveToNext()) {
            User user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    Boolean.parseBoolean(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4)
            );

            list.add(user);
        }

        return list;
    }

    public void removeUser(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_PEOPLE + " WHERE " + USER_ID + "=" + user_id;
        db.execSQL(sql);
    }
}