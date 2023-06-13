package com.example.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "city_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "cities";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {

        }
    }

    public long addCity(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the city already exists
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{cityName});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return -2;  // City already exists, return -1 to indicate failure
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, cityName);

        long id = db.insert(TABLE_NAME, null, values);

        db.close();

        return id;
    }


    public void deleteCity(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_NAME + " = ?";
        String[] whereArgs = {cityName};

        db.delete(TABLE_NAME, whereClause, whereArgs);

        db.close();
    }


    public List<String> getAllCities() {
        List<String> cities = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        int columnIndex = cursor.getColumnIndex(COLUMN_NAME);
        if (columnIndex != -1) {
            if (cursor.moveToFirst()) {
                do {
                    String cityName = cursor.getString(columnIndex);
                    cities.add(cityName);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();

        return cities;
    }

}