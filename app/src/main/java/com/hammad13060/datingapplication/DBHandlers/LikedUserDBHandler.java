package com.hammad13060.datingapplication.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Hammad on 18-11-2015.
 */
public class LikedUserDBHandler extends SQLiteOpenHelper {
    private Context context = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dating_application_people.db";
    private static final String TABLE_LIKES = "likes";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USER_ID = "_user_id";

    public LikedUserDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_LIKES + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " VARCHAR(255) " +
                ");";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES + ";");
        onCreate(db);
    }


    public void addLikedUserId(String user_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user_id);
        db.insert(TABLE_LIKES, null, values);
        db.close();
    }


    public void addLikedUserIds(List<String> userIds) {
        for (String user_id:userIds) {
            addLikedUserId(user_id);
        }
    }

    public boolean hasLiked(String user_id) {
        String query = "SELECT * FROM " + TABLE_LIKES + " WHERE " + COLUMN_USER_ID + "=\'" + user_id +"\';";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if (!c.isAfterLast()) {
            return true;
        }

        return false;
    }
}
