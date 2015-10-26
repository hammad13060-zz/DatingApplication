package com.hammad13060.datingapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hammad on 21-10-2015.
 */
public class PeopleDBHandler extends SQLiteOpenHelper {
    private Context context = null;

    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "dating_application_people.db";
    private static final String TABLE_PEOPLE = "peoples";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_URL = "url";

    public PeopleDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_PEOPLE + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " VARCHAR(255), " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GENDER + " BOOLEAN, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_URL + " VARCHAR(5000) " +
                ");";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE + ";");

        onCreate(db);
    }

    public void addUser(Person user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, user.get_user_id());
        values.put(COLUMN_NAME, user.get_name());
        values.put(COLUMN_GENDER, user.is_gender());
        values.put(COLUMN_AGE, user.get_age());
        values.put(COLUMN_URL, user.get_url());

        try {
            db.insert(TABLE_PEOPLE, null, values);
            db.close();
            Log.d("PEOPLE Handler", "User data inserted");
        } catch(SQLiteException e) {
            e.printStackTrace();
        }

    }

    public List<Person> getAllUser() {
        SharedPreferences user_settings = context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        boolean male_boolean = user_settings.getBoolean("male", true);
        boolean female_boolean = user_settings.getBoolean("female", true);

        String query = "SELECT * FROM " + TABLE_PEOPLE + " WHERE ";
        String where_clause = null;

        if (male_boolean && female_boolean) {
            where_clause = COLUMN_GENDER  + " is 1 OR " + COLUMN_GENDER + " is 0 ;";
        } else if (male_boolean) {
            where_clause = COLUMN_GENDER + " is 0 ;";
        } else if (female_boolean) {
            where_clause = COLUMN_GENDER  + " is 1 ;";
        }

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query + where_clause, null);

        c.moveToFirst();

        List<Person> peopleAround = new ArrayList<>(0);

        while(!c.isAfterLast()){
            String user_id = c.getString(c.getColumnIndex(COLUMN_USER_ID));
            String name = c.getString(c.getColumnIndex(COLUMN_NAME));
            boolean gender = new Boolean(c.getString(c.getColumnIndex(COLUMN_GENDER))).booleanValue();
            int age = c.getInt(c.getColumnIndex(COLUMN_AGE));
            String url = c.getString(c.getColumnIndex(COLUMN_URL));

            Person newUser = new Person(
                    user_id,
                    name,
                    gender,
                    age,
                    url
            );

            peopleAround.add(newUser);
            c.moveToNext();
        }

        return peopleAround;
    }

    public Person getUser(String user_id) {
        String query = "SELECT * FROM " + TABLE_PEOPLE + " WHERE " + COLUMN_USER_ID + "=\'" + user_id +"\';";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        String userId = c.getString(c.getColumnIndex(COLUMN_USER_ID));
        String name = c.getString(c.getColumnIndex(COLUMN_NAME));
        boolean gender = new Boolean(c.getString(c.getColumnIndex(COLUMN_GENDER))).booleanValue();
        int age = c.getInt(c.getColumnIndex(COLUMN_AGE));
        String url = c.getString(c.getColumnIndex(COLUMN_URL));

        Person user = new Person(
                userId,
                name,
                gender,
                age,
                url
        );

        return user;
    }

    public void deleteUser(String user_id) {
        String query = "DELETE FROM " + TABLE_PEOPLE + " WHERE " + COLUMN_USER_ID + "=\'" + user_id + "\';";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

}