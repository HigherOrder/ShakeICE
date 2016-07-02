package com.progfun.app.shakeice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by p on 07/02/15.
 */
public class UtilitiesDb {

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  protected HelperDb dbHelper = new HelperDb(c);

  protected SQLiteDatabase db;

  public UtilitiesDb(Context context) {
    c = context;
  }

  public SQLiteDatabase getDatabase() {
    if (db == null) {
      dbHelper = new HelperDb(c);
      db = dbHelper.getWritableDatabase();
    }
    return db;

//    DbOpenerTask dbOpenerTask = new DbOpenerTask(c);
//    dbOpenerTask.execute();
//    return dbOpenerTask.getDb();
  }


}
