package com.example.chambersnotofsecret.argo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TAG";
    private static String DB_NAME = "argo.db";
    private static String DB_PATH;
    private final Context context;
    private SQLiteDatabase myDatabase = null;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.context = context;
        Log.d(TAG,DB_PATH);
        Log.d(TAG,DB_NAME);
        Log.d(TAG,"Dbopenhelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
        db.execSQL("CREATE TABLE argo( ID text PRIMARY KEY," +
                "Active text," +
                "Manufacturer text," +
                "Instrument text," +
                "Controller text," +
                "Duty_Cycle text," +
                "Pref_press text," +
                "Start_date text," +
                "Start_Lat text," +
                "Start_Long text," +
                "L_Date text," +
                "Exp_Levels text," +
                "Exp_cycles text," +
                "NFull text," +
                "NPart text," +
                "Missing text);");
        Log.d("TAG", "execSQL");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}