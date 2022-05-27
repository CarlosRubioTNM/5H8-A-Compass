package com.example.compass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DB_FILENAME = "Login.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER = "username";
    public static final String COLUMN_PASS = "password";
    public SQLiteHelper(@Nullable Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USERS + " ( " + COLUMN_USER + " TEXT PRIMARY KEY, " + COLUMN_PASS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    }

    public boolean insertUser(String username, String pass) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER, username);
        contentValues.put(COLUMN_PASS, pass);
        long result = sqLiteDatabase.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER + " = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public boolean checkUsernamePass(String username, String pass) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER + " = ? AND " + COLUMN_PASS + " = ?", new String[]{username,pass});
        return cursor.getCount() > 0;
    }
}
