package com.progfun.app.shakeice.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.dal.DataAccessObject;
import com.progfun.app.shakeice.mal.ModelAccessObject;
import com.progfun.app.shakeice.models.Contact;
import com.progfun.app.shakeice.ui.clicklisteners.IIvClickListenerDeleteContact;
import com.progfun.app.shakeice.ui.clicklisteners.IIvClickListenerMakePrimaryContact;
import com.progfun.app.shakeice.ui.clicklisteners.IIvClickListenerNotMakePrimaryContact;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogDeleteContact;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogMakePrimaryContact;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogNotMakePrimaryContact;

import java.util.ArrayList;

public
class ContactsManagementActivity
  extends AppCompatActivity
  implements AdapterView.OnItemClickListener,
             IIvClickListenerDeleteContact,
             IIvClickListenerMakePrimaryContact,
             IIvClickListenerNotMakePrimaryContact,
             FragmentDialogDeleteContact.IListenerFragmentDialogDeleteContact,
             FragmentDialogMakePrimaryContact.IListenerFragmentDialogMakePrimaryContact,
             FragmentDialogNotMakePrimaryContact.IListenerFragmentDialogNotMakePrimaryContact {

  private final String TAG = this.getClass().getSimpleName();

  // This is the Adapter being used to display the list's data
  protected SimpleCursorAdapter mAdapter;

  // The application's context
  protected Context c;

  private FloatingActionButton fabAddContact;

  private ListView listView;

  private ArrayAdapterItemContacts adapter;

  private DataAccessObject dao;

  private ModelAccessObject mao;

  private long idContact;

  private Contact contactToMakePrimary;
  private Contact contactToNotMakePrimary;

  private Common common;

  private int positionOfContactToMakePrimary;
  private int positionOfContactToNotMakePrimary;

  private ArrayList<Contact> items;

  private MenuItem miDiscardContact;
  private MenuItem miAddContact;

  @Override
  protected
  void onCreate(Bundle savedInstanceState) {
    String TAG2 = "onCreate: ";
    Log.i(TAG, TAG2);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contacts_management);

    // Get the application's context
    c = getApplicationContext();
    common = new Common(c);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    TextView tvTitle = (TextView) toolbar.findViewById(R.id.tvBody);
    tvTitle.setTextColor(getResources().getColor(R.color.text_title));
    //    tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/timeburner_regular.ttf"));

    fabAddContact = (FloatingActionButton) findViewById(R.id.fabAddContact);
    ;
    fabAddContact.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public
        void onClick(View v) {
          Intent i = new Intent(c, ContactsSelectionActivity.class);
          startActivity(i);
          finish();
        }
      });

    adapter = getAdapter();
    listView = (ListView) findViewById(R.id.lvContacts);
    listView.setAdapter(adapter);
  }

  private
  ArrayAdapterItemContacts getAdapter() {
    String TAG2 = "getAdapter: ";
    Log.i(TAG, TAG2);

    ArrayAdapterItemContacts adapter;
    dao = new DataAccessObject(c);
    mao = new ModelAccessObject();
    items = new ArrayList<>();
    items = mao.createItemContacts(dao.fetchContactsSelected());

    adapter = new ArrayAdapterItemContacts(c, R.layout.list_item_contacts_management, items);
    adapter.setIIvClickListenerDeleteItemContacts(this);
    adapter.setIIvClickListenerMakePrimaryItemContacts(this);
    adapter.setIIvClickListenerNotMakePrimaryItemContacts(this);

    return adapter;
  }

  @Override
  public
  void onClickedIvDeleteItemContact(Contact itemCurrent) {
    String TAG2 = "onClickedIvDeleteItemContact: ";
    Log.i(TAG, TAG2);

    idContact = itemCurrent.getId();
    showDialogDiscardContacts();
  }

  @Override
  public
  void onClickYes(DialogFragment dialog) {
    String TAG2 = "onClickYes: ";
    Log.i(TAG, TAG2);

    Log.d(TAG, "onClickYes");
    dao.deleteContact(idContact);
    reloadItems();

    if (items.size() == 0) { // Note that there are no emergency contacts.
      common.saveSpBoolean(Constants.SP_KEY_ALERT_NUMBERS_ADDED, false);
    }
  }

  private
  void reloadItems() {
    String TAG2 = "reloadItems: ";
    Log.i(TAG, TAG2);
    
    items.clear();
    items = mao.createItemContacts(dao.fetchContactsSelected());
    adapter.updateList(items);
  }

  @Override
  public
  void onClickNo(DialogFragment dialog) {
    String TAG2 = "onClickNo: ";
    Log.i(TAG, TAG2);

    Log.d(TAG, "onClickNo");
  }

  @Override
  public
  void onClickedIvMakePrimaryContact(
    Contact itemCurrent,
    int position
  ) {
    String TAG2 = "onClickedIvMakePrimaryContact: ";
    Log.i(TAG, TAG2);

    contactToMakePrimary = new Contact(itemCurrent);
    positionOfContactToMakePrimary = position;

    showDialogMakePrimaryContact();
  }

  private
  void showDialogDiscardContacts() {
    String TAG2 = "showDialogDiscardContacts: ";
    Log.i(TAG, TAG2);

    DialogFragment fragment = new FragmentDialogDeleteContact();
    fragment.show(getSupportFragmentManager(), "");
  }

  private
  void showDialogMakePrimaryContact() {
    String TAG2 = "showDialogMakePrimaryContact: ";
    Log.i(TAG, TAG2);

    DialogFragment fragment = new FragmentDialogMakePrimaryContact();
    fragment.show(getSupportFragmentManager(), "");
  }

  @Override
  public
  void onClickYesMakePrimary(DialogFragment dialog) {
    String TAG2 = "onClickYesMakePrimary: ";
    Log.i(TAG, TAG2);

    common.saveSpString(Constants.SP_KEY_NUMBER_PRIMARY, contactToMakePrimary.getNumber()); // Save the number of the primary contact.

    /* If a primary contact has been assigned already, then make it a regular contact. */
//    ArrayList<Contact> alTmp = new ArrayList<>();
//    alTmp.addAll(items);
//
//    for (Contact item : alTmp) {

    items.clear();
    items = mao.createItemContacts(dao.fetchContactsSelected());

    for (Contact item : items) {
      if (item.getIsPrimary().equals(Constants.YES)) {
        item.setIsPrimary(Constants.NO);

        dao.updateContact(
          item.getId(),
          mao.getValuesContact(item)
        ); // Update the db immediately.
      }
    }

    dao.updateContact(
      contactToMakePrimary.getId(),
      mao.getValuesContact(contactToMakePrimary)
    );

    reloadItems();
  }

  @Override
  public
  void onClickNoMakePrimary(DialogFragment dialog) {
    String TAG2 = "onClickNoMakePrimary: ";
    Log.i(TAG, TAG2);

  }
  
  @Override
  public
  void onClickedIvNotMakePrimaryContact(
    Contact itemCurrent,
    int position
  ) {
    String TAG2 = "onClickedIvNotMakePrimaryContact: ";
    Log.i(TAG, TAG2);

//    contactToNotMakePrimary = new Contact(itemCurrent);
//    positionOfContactToNotMakePrimary = position;

    showDialogNotMakePrimaryContact();
  }

  private
  void showDialogNotMakePrimaryContact() {
    String TAG2 = "showDialogNotMakePrimaryContact: ";
    Log.i(TAG, TAG2);

    DialogFragment fragment = new FragmentDialogNotMakePrimaryContact();
    fragment.show(getSupportFragmentManager(), "");
  }

  @Override
  public
  void onClickYesNotMakePrimary(DialogFragment dialog) {
    String TAG2 = "onClickYesNotMakePrimary: ";
    Log.i(TAG, TAG2);

    common.delete(Constants.SP_KEY_NUMBER_PRIMARY); // Delete the number of the primary contact.
    
    /* If a primary contact has been assigned already, then make it a regular contact. */
//    ArrayList<Contact> alTmp = new ArrayList<>();
//    alTmp.addAll(items);
//
//    for (Contact item : alTmp) {

    items.clear();
    items = mao.createItemContacts(dao.fetchContactsSelected());
    
    for (Contact item : items) {
      if (item.getIsPrimary().equals(Constants.YES)) {
        item.setIsPrimary(Constants.NO);

        dao.updateContact(
          item.getId(),
          mao.getValuesContact(item)
        ); // Update the db immediately.
      }
    }

    reloadItems();
  }
  
  @Override
  public
  void onClickNoNotMakePrimary(DialogFragment dialog) {
    String TAG2 = "onClickNoNotMakePrimary: ";
    Log.i(TAG, TAG2);

  }
  
  @Override
  public
  void onItemClick(
    AdapterView<?> parent,
    View view,
    int position,
    long id
  ) {
    String TAG2 = "onItemClick: ";
    Log.i(TAG, TAG2);

  }

  @Override
  protected
  void onResume() {
    String TAG2 = "onResume: ";
    Log.i(TAG, TAG2);

    super.onResume();
    reloadItems();
  }

  /**
   * A simple array adapter that creates a list of activities.
   */
  protected
  class ArrayAdapterItemContacts
    extends ArrayAdapter<Contact> {

    private final String TAG = this.getClass().getSimpleName();

    private Context c;

    private int resourceId;

    private ArrayList<Contact> contacts;

    private IIvClickListenerDeleteContact listenerDeleteItemContact;

    private IIvClickListenerMakePrimaryContact listenerMakePrimaryItemContact;
    private IIvClickListenerNotMakePrimaryContact listenerNotMakePrimaryItemContact;

    private
    class ViewHolder {

      TextView tvName;
      TextView tvPrimary;
      TextView tvNumber;
      ImageView ivRemove;
      ImageView ivPrimary;
      ImageView ivPrimarySelected;
    }

    public
    ArrayAdapterItemContacts(
      Context context,
      int layoutResourceId,
      ArrayList<Contact> data
    ) {
      super(context, layoutResourceId, data);
      
      String TAG2 = "ArrayAdapterItemContacts: ";
      Log.i(TAG, TAG2);

      c = context;
      resourceId = layoutResourceId;
      contacts = data;
    }

    @Override
    public
    int getCount() {
      String TAG2 = "getCount: ";
//      Log.i(TAG, TAG2);

      return contacts.size();
    }

    @Override
    public
    View getView(
      final int position,
      View convertView,
      ViewGroup parent
    ) {
      String TAG2 = "getView: ";
//      Log.i(TAG, TAG2);

//      Log.i(TAG, "position = " + position);

      View itemView = convertView;

      ViewHolder holder = null;

      if (itemView == null) {
        final LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = li.inflate(resourceId, parent, false);
        
        holder = new ViewHolder();
        holder.tvName = (TextView) itemView.findViewById(R.id.tvName);
        holder.tvPrimary = (TextView) itemView.findViewById(R.id.tvPrimary);
        holder.tvNumber = (TextView) itemView.findViewById(R.id.tvNumber);
        holder.ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
        holder.ivPrimary = (ImageView) itemView.findViewById(R.id.ivPrimary);
        holder.ivPrimarySelected = (ImageView) itemView.findViewById(R.id.ivPrimarySelected);
        
        itemView.setTag(holder);
      }
      else {
        holder = (ViewHolder) itemView.getTag();
      }

      final Contact item = getItem(position);

      holder.tvName.setText(item.getName());
      holder.tvNumber.setText(item.getNumber());

      if (item.getIsPrimary().equals(Constants.YES)) {
        holder.tvPrimary.setVisibility(View.VISIBLE);
        holder.ivPrimary.setVisibility(View.INVISIBLE);
        holder.ivPrimarySelected.setVisibility(View.VISIBLE);
      }
      else {
        holder.tvPrimary.setVisibility(View.INVISIBLE);
        holder.ivPrimarySelected.setVisibility(View.INVISIBLE);
        holder.ivPrimary.setVisibility(View.VISIBLE);
      }

      holder.ivRemove.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public
          void onClick(View v) {
            String TAG2 = "holder.ivRemove.setOnClickListener: onClick: ";
            Log.i(TAG, TAG2);

            if (listenerDeleteItemContact != null) {
              listenerDeleteItemContact.onClickedIvDeleteItemContact(item);
            }
          }
        }
      );

      holder.ivPrimary.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public
          void onClick(View v) {
            String TAG2 = "holder.ivPrimary.setOnClickListener: onClick: ";
            Log.i(TAG, TAG2);

            if (listenerMakePrimaryItemContact != null) {
              item.setIsPrimary(Constants.YES);

//              Contact tmp = contacts.get(0);
//              contacts.set(0, item);
//              contacts.set(position, tmp);
//              notifyDataSetChanged();

              listenerMakePrimaryItemContact.onClickedIvMakePrimaryContact(item, position);
            }
          }
        }
      );
      
      holder.ivPrimarySelected.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public
          void onClick(View v) {
            String TAG2 = "holder.ivPrimarySelected.setOnClickListener: onClick: ";
            Log.i(TAG, TAG2);

            if (listenerNotMakePrimaryItemContact != null) {
              item.setIsPrimary(Constants.NO);

              listenerNotMakePrimaryItemContact.onClickedIvNotMakePrimaryContact(item, position);
            }
          }
        }
      );

      return itemView;
    }

    public
    void updateList(ArrayList<Contact> items) {
      String TAG2 = "updateList: ";
      Log.i(TAG, TAG2);

      contacts.clear();
      contacts.addAll(items);
      notifyDataSetChanged();
    }

    public
    void setIIvClickListenerDeleteItemContacts(IIvClickListenerDeleteContact listener) {
      String TAG2 = "setIIvClickListenerDeleteItemContacts: ";
      Log.i(TAG, TAG2);

      this.listenerDeleteItemContact = listener;
    }

    public
    void setIIvClickListenerMakePrimaryItemContacts(IIvClickListenerMakePrimaryContact listener) {
      String TAG2 = "setIIvClickListenerMakePrimaryItemContacts: ";
      Log.i(TAG, TAG2);

      this.listenerMakePrimaryItemContact = listener;
    }

    public
    void setIIvClickListenerNotMakePrimaryItemContacts(IIvClickListenerNotMakePrimaryContact listener) {
      String TAG2 = "setIIvClickListenerNotMakePrimaryItemContacts: ";
      Log.i(TAG, TAG2);

      this.listenerNotMakePrimaryItemContact = listener;
    }
  }

}
