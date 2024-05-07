package com.example.hatliiiiiiiiii;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "try.db";
    private static final int VERSION = 1;

    //table of colleges
    public static final String COLLEGES_TABLENAME = "colleges";
    public static final String COLLEGES_COLUMNLOCALID = "local id";
    public static final String COLLEGES_COLUMNID = "id";
    public static final String COLLEGES_NAME = "name";


    //table of Departments
    public static final String DEPARTEMENTS_TABLENAME = "departments";
    public static final String DEPARTEMENT_COLUMNLOCALID = "local id";
    public static final String DEPARTEMENT_COLUMNID = "id";
    public static final String DEPARTEMENT_NAME = "name";

    public static final String PARENTDEPARTEMENTID = "parent id";




    public DataBase(@Nullable Context context) {

        super(context, DATABASE_NAME , null , VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAllTables();
        onCreate(db);
    }

    public void insertTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Query to create a new table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "LocalID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "columnName TEXT);";
        // Executing the query
        db.execSQL(createTableQuery);
        db.close();
    }


    public long addRow(String tableName, String columnName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id" , id);
        values.put("columnName" , columnName);
        // Inserting the row into the table
        long res = db.insert(tableName, null, values);

        db.close();
        return res;
    }

    Cursor readAllData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        SQLiteDatabase dp = this.getReadableDatabase();

        Cursor cursor = null;
        if (dp != null) {
            cursor = dp.rawQuery(query, null);
        }
        return cursor;
    }


    @SuppressLint("Range")
    public List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    tables.add(cursor.getString(cursor.getColumnIndex("name")));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return tables;
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> tables = getAllTables();
        for (String table : tables) {
            if(table != "sqlite_sequence" && table != "android_metadata") {
//                db.execSQL("DROP TABLE IF EXISTS " + table);
                deleteAllRowsFromTable(table);
            }
        }
        db.close();
    }

    public void deleteAllRowsFromTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Deleting all rows from the table
        db.delete(tableName, null, null);
        db.close();
    }


    //function of colleges
    public void insertCollegesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Query to create a new table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + COLLEGES_TABLENAME + " ("
                + "LocalID INTEGER PRIMARY KEY,"
                + "id TEXT,"
                + "columnName TEXT);";
        // Executing the query
        db.execSQL(createTableQuery);
        db.close();
    }

    public void addCollege(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("columnName", name);

        // Inserting Row
        long rowId = db.insert(COLLEGES_TABLENAME, null, values);

        // Showing the result using a Toast message
        Toast.makeText(context , "Row inserted with ID: " + rowId, Toast.LENGTH_SHORT).show();

        db.close();
    }


    //function of departements
    public void insertDepartementsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Query to create a new table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DEPARTEMENTS_TABLENAME + " ("
                + "LocalID INTEGER PRIMARY KEY,"
                + "id TEXT,"
                + "parentID TEXT , "
                + "columnName TEXT);";
        // Executing the query
        db.execSQL(createTableQuery);
        db.close();
    }

    public void addDepartement(String name, String id , String parentID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("parentID" , parentID);
        values.put("columnName", name);
        // Inserting Row
        long rowId = db.insert(DEPARTEMENTS_TABLENAME, null, values);

        // Showing the result using a Toast message
        Toast.makeText(context , "Row inserted with ID: " + rowId, Toast.LENGTH_SHORT).show();

        db.close();
    }
}

