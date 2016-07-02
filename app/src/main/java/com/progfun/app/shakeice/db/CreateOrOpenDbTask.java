package com.progfun.app.shakeice.db;

/**
 * Created by p on 21/11/14.
 */

import android.os.AsyncTask;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by p on 21/11/14.
 */
//public class ContactsDao {
//  private CreateDbTask createDbTask;
//
//  public ContactsDao(Context context) {
//    c = context;
//    createDbTask = new CreateDbTask();
//  }
//
//  public CreateDbTask getNewCreateDbTask() {
//    return (createDbTask == null) ? (new CreateDbTask()) : createDbTask;
//  }

/**
 * Create the DB in a background thread via an AsyncTask.
 * <p/>
 * Reason: from the Android documentation for "Saving Data in SQL Databases" -
 * "
 * Note: Because they can be long-running, be sure that you call
 * getWritableDatabase() or getReadableDatabase() in a background
 * thread, such as with AsyncTask or IntentService.
 * "
 */

public class CreateOrOpenDbTask extends AsyncTask<Void, Void, Void> {

  // The applications context
  protected Context c;

  protected HelperDb helperDb;

  protected SQLiteDatabase db;

  public CreateOrOpenDbTask(Context context) {
    c = context;
  }

  public SQLiteDatabase getDb() {
    if (db == null) {
      helperDb = new HelperDb(c);
      db = helperDb.getWritableDatabase();
    }

    return db;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    Void v = null;

    helperDb = new HelperDb(c);
    // Gets the data repository in write mode
    db = helperDb.getWritableDatabase();

    try {
      v = Void.class.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return v;
  }

  protected void onProgressUpdate(Integer... progress) {
  }

  protected void onPostExecute(Long result) {
  }
}
