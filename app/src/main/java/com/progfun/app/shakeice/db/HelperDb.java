package com.progfun.app.shakeice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by p on 10/11/14.
 *
 * From the Android documentation for "Saving Data in SQL Databases" -
 *
 * "
 * A useful set of APIs is available in the SQLiteOpenHelper class.
 * When you use this class to obtain references to your database, the system
 * performs the potentially long-running operations of creating and updating the
 * database only when needed and not during app startup. All you need to do is
 * call getWritableDatabase() or getReadableDatabase().
 *
 * "
 *
 */

public class HelperDb extends SQLiteOpenHelper {

  // If you change the database schema, you must increment the database version.
  public static final int VERSION_DATABASE = 1;
  private static final String NAME_DATABASE = "shakeice.db";

  public HelperDb(Context context) {
    super(context,
         NAME_DATABASE,
         null,
         VERSION_DATABASE);
  }

  public void onCreate(SQLiteDatabase db) {
    db.execSQL(ContractDb.SQL_CREATE_TABLE_CONTACTS);
    db.execSQL(ContractDb.SQL_CREATE_TABLE_ACTIVITIES);
    db.execSQL(ContractDb.SQL_INSERT_ACTIVITY_1);
    db.execSQL(ContractDb.SQL_INSERT_ACTIVITY_2);
    db.execSQL(ContractDb.SQL_INSERT_ACTIVITY_3);
    db.execSQL(ContractDb.SQL_INSERT_ACTIVITY_4);
    db.execSQL(ContractDb.SQL_INSERT_ACTIVITY_5);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    db.execSQL(ContractDb.SQL_DELETE_ENTRIES);
    onCreate(db);
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }


}
