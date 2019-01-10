package com.smacon.fish2marine.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Athira on 2/17/2017.
 */

public class SqliteHelper extends SQLiteOpenHelper {

    ContentValues cv;
    public Context mContext;
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "Fish2Marine.db";

    public static final String ADMIN_DETAILS_TABLE = "admin_details_table";
    public static final String CATEGORY_TABLE="category_table";
    public static final String CARTCOUNT_TABLE="cartcount_table";

    public static final String ADMIN_ID = "admin_id";
    public static final String ADMIN_NAME = "admin_name";
    public static final String ADMIN_MOBILE = "admin_mobile";
    public static final String ADMIN_EMAIL = "admin_email";

    public static final String CATEGORY_ID= "category_id";
    public static final String CATEGORY_NAME= "category_name";

    public static final String CARTCOUNT= "cartcount";



    private static final String CREATE_ADMIN_TABLE =
            "create table " + ADMIN_DETAILS_TABLE + " ("
                    + ADMIN_ID + " text, "
                    + ADMIN_NAME + " text, "
                    + ADMIN_MOBILE + " text, "
                    + ADMIN_EMAIL + " text)";

    private static final String CREATE_CATEGORY_TABLE =
            "create table " + CATEGORY_TABLE + " ("
                    + CATEGORY_ID + " text, "
                    + CATEGORY_NAME + " text)";

    private static final String CREATE_CARTCOUNT_TABLE =
            "create table " + CARTCOUNT_TABLE + " ("
                    + CARTCOUNT + " text)";


    public SqliteHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_CARTCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void Insert_admin_details(List<HashMap<String, String>> list) {
        for(int i=0;i<list.size();i++) {

            HashMap<String, String> map = new HashMap<String, String>();
            map = list.get(i);

            cv = new ContentValues();
            cv.put(SqliteHelper.ADMIN_ID, map.get("customerId"));
            cv.put(SqliteHelper.ADMIN_NAME, map.get("customerName"));
            cv.put(SqliteHelper.ADMIN_MOBILE, map.get("mobile"));
            cv.put(SqliteHelper.ADMIN_EMAIL, map.get("email"));
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(SqliteHelper.ADMIN_DETAILS_TABLE, null, cv);
            db.close(); // Closing database connection
        }
    }

    public void Insert_Count(List<HashMap<String, String>> list) {
        for(int i=0;i<list.size();i++) {

            HashMap<String, String> map = new HashMap<String, String>();
            map = list.get(i);

            cv = new ContentValues();
            cv.put(SqliteHelper.CARTCOUNT, map.get("itemsCount"));
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(SqliteHelper.CARTCOUNT_TABLE, null, cv);
            db.close(); // Closing database connection
        }
    }

    public List<HashMap<String, String>> getadmindetails() {

        String selectQuery = "SELECT * FROM " + ADMIN_DETAILS_TABLE;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    map = new HashMap<String, String>();
                    map.put("admin_id", cursor.getString(cursor.getColumnIndexOrThrow("admin_id")));
                    map.put("admin_name", cursor.getString(cursor.getColumnIndexOrThrow("admin_name")));
                    map.put("admin_mobile", cursor.getString(cursor.getColumnIndexOrThrow("admin_mobile")));
                    map.put("admin_email", cursor.getString(cursor.getColumnIndexOrThrow("admin_email")));
                    fillMaps.add(map);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
        } catch (Exception e) {
        }
        return fillMaps;
    }

    public List<HashMap<String, String>> getCount() {

        String selectQuery = "SELECT * FROM " + CARTCOUNT_TABLE;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    map = new HashMap<String, String>();
                    map.put("cartcount", cursor.getString(cursor.getColumnIndexOrThrow("cartcount")));
                    fillMaps.add(map);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
        } catch (Exception e) {
        }
        return fillMaps;
    }



    public void Delete_admin_details() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SqliteHelper.ADMIN_DETAILS_TABLE, null, null);
        db.close(); // Closing database connection
    }
    public void Delete_cartcount() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SqliteHelper.CARTCOUNT_TABLE, null, null);
        db.close(); // Closing database connection
    }

}
