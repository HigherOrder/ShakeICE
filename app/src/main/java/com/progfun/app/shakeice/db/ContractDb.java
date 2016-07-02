package com.progfun.app.shakeice.db;

import android.provider.BaseColumns;

/**
 * Created by p on 10/11/14.
 * <p/>
 * From the Android documentation for "Saving Data in SQL Databases" -
 * <p/>
 * "A contract class is a container for constants that define names for URIs,
 * tables, and columns. The contract class allows you to use the same constants
 * across all the other classes in the same package. This lets you change a
 * column name in one place and have it propagate throughout your code."
 * <p/>
 * "A good way to organize a contract class is to put definitions that are
 * global to your whole database in the root level of the class. Then create
 * an inner class for each table that enumerates its columns."
 */
public
class ContractDb {

  // Give the class an empty constructor. This will prevent someone from
  // accidentally instantiating the contract class.
  public
  ContractDb() {}

  /* Inner class that defines the table contents */
  public static abstract
  class TableContacts
    implements BaseColumns {

    public static final String NAME = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_IS_SELECTED = "isselected";
    public static final String COLUMN_IS_PRIMARY = "isprimary";
  }

  public static abstract
  class TableActivities
    implements BaseColumns {

    public static final String NAME = "activities";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_MESSAGE_ALERT = "messagealert";
    public static final String COLUMN_IMPACT = "impact";
    public static final String COLUMN_IS_SELECTED = "isselected";
  }

  public static final String SQL_CREATE_TABLE_CONTACTS =
    "CREATE TABLE " +
      TableContacts.NAME + " (" +
      TableContacts.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      TableContacts.COLUMN_NAME + " TEXT NOT NULL, " +
      TableContacts.COLUMN_NUMBER + " TEXT NOT NULL, " +
      TableContacts.COLUMN_IS_SELECTED + " TEXT NOT NULL, " +
      TableContacts.COLUMN_IS_PRIMARY + " TEXT NOT NULL" +
    " )";

  public static final String SQL_CREATE_TABLE_ACTIVITIES =
    "CREATE TABLE " +
      TableActivities.NAME + " (" +
      TableActivities.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      TableActivities.COLUMN_NAME + " TEXT NOT NULL, " +
      TableActivities.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
      TableActivities.COLUMN_MESSAGE_ALERT + " TEXT NOT NULL, " +
      TableActivities.COLUMN_IMPACT + " TEXT NOT NULL, " +
      TableActivities.COLUMN_IS_SELECTED + " TEXT NOT NULL" +
    " )";

  public static final String SQL_INSERT_ACTIVITY_1 =
    "INSERT INTO \"activities\" VALUES(1, 'Need help', 'Shake your phone or device to alert your contact(s) instantly', 'HELP! I am in an emergency', '1.44', 'y')";
  public static final String SQL_INSERT_ACTIVITY_2 =
    "INSERT INTO \"activities\" VALUES(2, 'Taking the stairs', 'Walking up or down a flight of stairs', 'HELP! I fell while taking a flight of stairs', '2.16', 'n')";
  public static final String SQL_INSERT_ACTIVITY_3 =
    "INSERT INTO \"activities\" VALUES(3, 'Riding in vehicle at low speed', 'Riding in a two or four wheeler within the city', 'HELP! I met with an accident while riding in a two or four wheeler within the city', '3.00', 'n')";
  public static final String SQL_INSERT_ACTIVITY_4 =
    "INSERT INTO \"activities\" VALUES(4, 'Riding in vehicle at high speed', 'Riding in a two or four wheeler on a highway', 'HELP! I met with an accident while riding in a two or four wheeler within the city', '3.96', 'n')";
  public static final String SQL_INSERT_ACTIVITY_5 =
    "INSERT INTO \"activities\" VALUES(5, 'No activity', 'Choose this option when you want to switch off alerts temporarily', 'HELP! I am in an emergency', '12.00', 'n')";

  public static final String SQL_SELECT_ALL = "SELECT * FROM " + TableContacts.NAME;

  public static final String SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS " + TableContacts.NAME;

}



