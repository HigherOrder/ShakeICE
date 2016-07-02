package com.progfun.app.shakeice.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.db.ContractDb.TableActivities;
import com.progfun.app.shakeice.db.ContractDb.TableContacts;
import com.progfun.app.shakeice.db.UtilitiesDb;

import java.util.ArrayList;

/**
 * Created by p on 17/04/15.
 */
public class DataAccessObject {

  private final String TAG = this.getClass().getSimpleName();

  private SQLiteDatabase db;

  private Context context;


  public DataAccessObject(Context c) {
    context = c;
    db = (new UtilitiesDb(context)).getDatabase();
  }

  public long addNewContact(ContentValues values) {
    return db.insert(
            TableContacts.NAME,
            null,
            values
    );
  }

  public
  ArrayList<String> fetchNumbers() {
    ArrayList<String> numbers = new ArrayList<>();
    Cursor c = db.query(TableContacts.NAME, (new String[]{TableContacts.COLUMN_NUMBER}), null, // selection
            null, // selectionArgs
            null, // groupBy
            null, // having
            null // orderBy
    );
    if (c != null) {
      c.moveToFirst();
      while (!c.isAfterLast()) {
        numbers.add(c.getString(c.getColumnIndex(TableContacts.COLUMN_NUMBER)));
        c.moveToNext();
      }
    }
    return numbers;
  }
  
  public Cursor fetchContactsSelectedToDelete() {
    return db.query(TableContacts.NAME, (new String[]{TableContacts.COLUMN_NAME, TableContacts.COLUMN_NUMBER}), null, null, null, null, null);
  }


  public ContentValues[] fetchContactsAll() {
    ContentValues[] items = null;
    Cursor c = context.getContentResolver().query(
            Phone.CONTENT_URI, (new String[]{Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}), Phone.HAS_PHONE_NUMBER + "=?", (new String[]{Constants.ONE}), Phone.DISPLAY_NAME);
    if (c != null) {
      items = new ContentValues[c.getCount()];
      c.moveToFirst();
      int i = 0;
      while (!c.isAfterLast()) {
        ContentValues v = new ContentValues();
        v.put(TableContacts.COLUMN_ID, c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
        v.put(TableContacts.COLUMN_NAME, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        v.put(TableContacts.COLUMN_NUMBER, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        v.put(TableContacts.COLUMN_IS_SELECTED, Constants.NO);
        v.put(TableContacts.COLUMN_IS_PRIMARY, Constants.NO);
        items[i++] = v;
        c.moveToNext();
      }
      c.close();
    }
    return items;
  }

  public ContentValues[] fetchContactsFiltered(String filter) {
    ContentValues[] items = null;
    String selection = Phone.HAS_PHONE_NUMBER + "=" + Constants.ONE + " AND " +
            Phone.DISPLAY_NAME + " LIKE '%" + filter + "%'";
//                       Phone.DISPLAY_NAME + " LIKE '" + filter + "'" + " OR '%" + filter + "'"+ " OR '" + filter + "%'"+ " OR '%" + filter + "%'";
    Cursor c = context.getContentResolver().query(
      Phone.CONTENT_URI,
      (new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}), // Columns
      selection,
      null,
      Phone.DISPLAY_NAME); // Order by
    if (c != null) {
      items = new ContentValues[c.getCount()];
      c.moveToFirst();
      int i = 0;
      while (!c.isAfterLast()) {
        ContentValues v = new ContentValues();
        v.put(TableContacts.COLUMN_ID, c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
        v.put(TableContacts.COLUMN_NAME, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        v.put(TableContacts.COLUMN_NUMBER, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        v.put(TableContacts.COLUMN_IS_SELECTED, Constants.NO);
        v.put(TableContacts.COLUMN_IS_PRIMARY, Constants.NO);
        items[i++] = v;
        c.moveToNext();
      }
      c.close();
    }
    return items;
  }

  public ContentValues[] fetchContactsSelected() {
    ContentValues[] items = null;
    Cursor c = db.query(TableContacts.NAME, null, null, null, null, null, TableContacts.COLUMN_IS_PRIMARY + " DESC");
    if (c != null) {
      items = new ContentValues[c.getCount()];
      c.moveToFirst();
      int i = 0;
      while (!c.isAfterLast()) {
        ContentValues v = new ContentValues();
        v.put(TableContacts.COLUMN_ID, c.getLong(c.getColumnIndex(TableContacts.COLUMN_ID)));
        v.put(TableContacts.COLUMN_NAME, c.getString(c.getColumnIndex(TableContacts.COLUMN_NAME)));
        v.put(TableContacts.COLUMN_NUMBER, c.getString(c.getColumnIndex(TableContacts.COLUMN_NUMBER)));
        v.put(TableContacts.COLUMN_IS_SELECTED, c.getString(c.getColumnIndex(TableContacts.COLUMN_IS_SELECTED)));
        v.put(TableContacts.COLUMN_IS_PRIMARY, c.getString(c.getColumnIndex(TableContacts.COLUMN_IS_PRIMARY)));
        items[i++] = v;
        c.moveToNext();
      }
      c.close();
    }
    return items;
  }

  public int deleteContact(long id) {
    return db.delete(
            TableContacts.NAME,
            TableContacts.COLUMN_ID + "=?",
            (new String[]{Long.toString(id)})
    );
  }

  public int updateContact(
    long id,
    ContentValues values
  ) {
    return db.update(
      TableContacts.NAME, // Name of the table to update
      values, // Values to update the table with
      TableContacts.COLUMN_ID + "=?", // Key for the WHERE clause
      (new String[]{Long.toString(id)}) // Value for the WHERE clause
    );

  }

  public ContentValues[] fetchAllActivities() {
    ContentValues[] items = null;
    String[] columnsActivities = new String[]{TableActivities.COLUMN_ID, TableActivities.COLUMN_NAME, TableActivities.COLUMN_DESCRIPTION, TableActivities.COLUMN_MESSAGE_ALERT, TableActivities.COLUMN_IMPACT, TableActivities.COLUMN_IS_SELECTED};
    Cursor c = db.query(TableActivities.NAME, columnsActivities, null, null, null, null, null);
    if (c != null) {
      items = new ContentValues[c.getCount()];
      c.moveToFirst();
      int i = 0;
      while (!c.isAfterLast()) {
        ContentValues v = new ContentValues();
        v.put(TableActivities.COLUMN_ID, c.getLong(c.getColumnIndex(TableActivities.COLUMN_ID)));
        v.put(TableActivities.COLUMN_NAME, c.getString(c.getColumnIndex(TableActivities.COLUMN_NAME)));
        v.put(TableActivities.COLUMN_DESCRIPTION, c.getString(c.getColumnIndex(TableActivities.COLUMN_DESCRIPTION)));
        v.put(TableActivities.COLUMN_MESSAGE_ALERT, c.getString(c.getColumnIndex(TableActivities.COLUMN_MESSAGE_ALERT)));
        v.put(TableActivities.COLUMN_IMPACT, c.getFloat(c.getColumnIndex(TableActivities.COLUMN_IMPACT)));
        v.put(TableActivities.COLUMN_IS_SELECTED, c.getString(c.getColumnIndex(TableActivities.COLUMN_IS_SELECTED)));
        items[i++] = v;
        c.moveToNext();
      }
      c.close();
    }
    return items;
  }

  public int saveChangesActivity(long idActivity, ContentValues values) {
    return db.update(TableActivities.NAME, values, TableActivities.COLUMN_ID + "=?", (new String[]{Long.toString(idActivity)}));
  }

  public int deselectActivity() {
    ContentValues values = new ContentValues();
    values.put(TableActivities.COLUMN_IS_SELECTED, Constants.NO);
    return db.update(TableActivities.NAME, values, TableActivities.COLUMN_IS_SELECTED + "=?", (new String[]{Constants.YES}));
  }

  public boolean activitiesSelectedNone() {
    boolean activitiesSelectedNone = false;
    Cursor c = db.query(TableActivities.NAME, null, // We are not interested in any particular set of columns.
            TableActivities.COLUMN_IS_SELECTED + "=?", (new String[]{Constants.YES}), null, // groupBy
            null, // having
            null // orderBy
    );
    if ((c != null) && (c.getCount() < 1)) {
      activitiesSelectedNone = true;
    }
    return activitiesSelectedNone;
  }

  public long fetchIdActivitySelected() {
    long idActivitySelected = 0;
    Cursor c = db.query(TableActivities.NAME, (new String[]{TableActivities.COLUMN_ID}), // We are not interested in any particular set of columns.
            TableActivities.COLUMN_IS_SELECTED + "=?", (new String[]{Constants.YES}), null, // groupBy
            null, // having
            null // orderBy
    );
    if ((c != null) && (c.getCount() == 1)) {
      c.moveToFirst();
      idActivitySelected = c.getLong(c.getColumnIndex(TableActivities.COLUMN_ID));
    }
    return idActivitySelected;
  }


}



