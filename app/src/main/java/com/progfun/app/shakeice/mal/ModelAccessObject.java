package com.progfun.app.shakeice.mal;

import android.content.ContentValues;

import com.progfun.app.shakeice.models.Contact;
import com.progfun.app.shakeice.models.ItemActivity;
import com.progfun.app.shakeice.db.ContractDb.TableContacts;
import com.progfun.app.shakeice.db.ContractDb.TableActivities;

import java.util.ArrayList;

/**
 * Created by p on 19/04/15.
 */
public
class ModelAccessObject {

  public
  ArrayList<Contact> createItemContacts(ContentValues[] valuesArray) {
    ArrayList<Contact> items = new ArrayList<>();
    for (ContentValues values : valuesArray) {
      Contact item = new Contact(
        values.getAsLong(TableContacts.COLUMN_ID),
        values.getAsString(TableContacts.COLUMN_NAME),
        values.getAsString(TableContacts.COLUMN_NUMBER),
        values.getAsString(TableContacts.COLUMN_IS_SELECTED),
        values.getAsString(TableContacts.COLUMN_IS_PRIMARY)
      );
      items.add(item);
    }
    return items;
  }

  public
  ContentValues getValuesContact(Contact item) {
    ContentValues values = new ContentValues();
    values.put(TableContacts.COLUMN_ID, item.getId());
    values.put(TableContacts.COLUMN_NAME, item.getName());
    values.put(TableContacts.COLUMN_NUMBER, item.getNumber());
    values.put(TableContacts.COLUMN_IS_SELECTED, item.getIsSelected());
    values.put(TableContacts.COLUMN_IS_PRIMARY, item.getIsPrimary());
    return values;
  }

  public
  ArrayList<ItemActivity> createItemActivities(ContentValues[] valuesArray) {
    ArrayList<ItemActivity> items = new ArrayList<>();
    for (ContentValues values : valuesArray) {
      ItemActivity item = new ItemActivity(values.getAsLong(TableActivities.COLUMN_ID), values.getAsString(TableActivities.COLUMN_NAME), values.getAsString(TableActivities.COLUMN_DESCRIPTION), values.getAsString(TableActivities.COLUMN_MESSAGE_ALERT), values.getAsFloat(TableActivities.COLUMN_IMPACT), values.getAsString(TableActivities.COLUMN_IS_SELECTED));
      items.add(item);
    }
    return items;
  }

}
