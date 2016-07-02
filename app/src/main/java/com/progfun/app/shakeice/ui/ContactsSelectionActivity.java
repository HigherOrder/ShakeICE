package com.progfun.app.shakeice.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.common.Common;
import com.progfun.app.shakeice.common.Constants;
import com.progfun.app.shakeice.dal.DataAccessObject;
import com.progfun.app.shakeice.mal.ModelAccessObject;
import com.progfun.app.shakeice.models.Contact;
import com.progfun.app.shakeice.ui.clicklisteners.IIvClickListenerSelectContact;
import com.progfun.app.shakeice.ui.dialogs.FragmentDialogContactsSaved;

import java.util.ArrayList;


public class ContactsSelectionActivity
        extends AppCompatActivity
  implements IIvClickListenerSelectContact,
             AdapterView.OnItemClickListener,
             FragmentDialogContactsSaved.IListenerFragmentDialogContactsSaved {

  private final String TAG = this.getClass().getSimpleName();

  private Context c;

  private Common common;

  private DataAccessObject dao;

  private EditText edSearch;

  private ImageView ivErase;

  private ListView lv;

  private ArrayAdapterItemContacts adapter;

  private ModelAccessObject mao;

  private ArrayList<Contact> items;
  private ArrayList<Contact> itemsSelected = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    // Set our custom view
    setContentView(R.layout.activity_contacts_selection);

    // Get application's context.
    c = getApplicationContext();
    common = new Common(c);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    TextView tvTitle = (TextView) toolbar.findViewById(R.id.tvBody);
    tvTitle.setTextColor(getResources().getColor(R.color.text_title));
//    tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/timeburner_regular.ttf"));

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);

    adapter = getAdapter();
    lv = (ListView) findViewById(R.id.lvContacts);
    lv.setAdapter(adapter);

    ivErase = (ImageView) findViewById(R.id.ivErase);
    ivErase.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                edSearch.setText("");
              }
            });

    edSearch = (EditText) findViewById(R.id.etSearch);
    edSearch.addTextChangedListener(
            new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged: " + edSearch.getText());

                //                adapter.clear();
                items.clear();
                items = mao.createItemContacts(dao.fetchContactsFiltered(edSearch.getText().toString()));
                setIsSelected(items);
                adapter.updateList(items);

                if (edSearch.getText().length() > 0) {
                  ivErase.setVisibility(View.VISIBLE);
                }
                else {
                  ivErase.setVisibility(View.INVISIBLE);
                }
              }

              @Override
              public void afterTextChanged(Editable s) {
              }
            });

    // Create a progress bar to display while the list loads
    //    ProgressBar progressBar = new ProgressBar(this);
    //    progressBar.setLayoutParams(
    //            new LinearLayout.LayoutParams(
    //                    LinearLayout.LayoutParams.WRAP_CONTENT,
    //                    LinearLayout.LayoutParams.WRAP_CONTENT,
    //                    Gravity.CENTER));
    //    progressBar.setIndeterminate(true);
    //    getListView().setEmptyView(progressBar);

    // Must add the progress bar to the root of the layout
    //    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    //    root.addView(progressBar);
  }

  private ArrayAdapterItemContacts getAdapter() {
    ArrayAdapterItemContacts adapter;
    dao = new DataAccessObject(c);
    mao = new ModelAccessObject();
    items = mao.createItemContacts(dao.fetchContactsAll());
    setIsSelected(items);
    adapter = new ArrayAdapterItemContacts(c, R.layout.list_item_contacts_selection, items);
    adapter.setIIvClickListenerContactsItemSelected(this);
    return adapter;
  }

  public void setIsSelected(ArrayList<Contact> items) {
    ArrayList<Contact> itemsSelected = mao.createItemContacts(dao.fetchContactsSelected());
    for (Contact item : items) {
      if (itemsSelected.contains(item)) {
        item.setIsSelected(Constants.YES);
      }
    }
  }

  @Override
  public void onClickedIvContactsItemSelected(Contact item, String status) {
    Log.d(TAG, "onClickedIvContactsItemSelected: ");

    Contact itemCurrent = item;
    if (status.equals(Constants.YES)) {
      itemCurrent.setIsSelected(Constants.YES);
      itemsSelected.add(itemCurrent);
    }
    else {
      itemCurrent.setIsSelected(Constants.NO);
      itemsSelected.remove(itemCurrent);
    }
    setIsSelected(items);
    adapter.updateList(items);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Log.d(TAG, "onItemClick: ");
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_contacts_selection, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        saveContacts();
        common.saveSpBoolean(Constants.SP_KEY_ALERT_NUMBERS_ADDED, true);
        showDialogSavedContacts();
//        Toast.makeText(c, R.string.s_toast_contacts_saved, Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void saveContacts() {
    Log.d(TAG, "saveContacts: ");
    for (Contact item : itemsSelected) {
      dao.addNewContact(mao.getValuesContact(item));
    }
  }

  private void showDialogSavedContacts() {
    DialogFragment fragment = new FragmentDialogContactsSaved();
    fragment.show(getSupportFragmentManager(), "");
  }

  @Override
  protected void onResume() {
    super.onResume();
    items = mao.createItemContacts(dao.fetchContactsAll());
    setIsSelected(items);
    adapter.updateList(items);
  }

  @Override
  public void onClickOkContactsSaved(DialogFragment dialog) {
    Log.i(TAG, "onClickOkContactsSaved: ");
    Intent i = new Intent(c, ContactsManagementActivity.class);
    startActivity(i);
    finish();
  }

  /**
   * A simple array adapter that creates a list of activities.
   */
  protected class ArrayAdapterItemContacts
          extends ArrayAdapter<Contact> {

    private final String TAG = this.getClass().getSimpleName();

    private Context c;

    private int resourceId;

    private ArrayList<Contact> contacts;

    private IIvClickListenerSelectContact listenerContactsItemSelected;

    private
    class ViewHolder {

      RelativeLayout rlItem;
      TextView tvItemName;
      TextView tvItemNumber;
      ImageView ivItemSelected;
    }

    public
    void setIIvClickListenerContactsItemSelected(IIvClickListenerSelectContact listener) {
      this.listenerContactsItemSelected = listener;
    }

    public
    ArrayAdapterItemContacts(
      Context context,
      int layoutResourceId,
      ArrayList<Contact> data
    ) {
      super(context, layoutResourceId, data);
      this.c = context;
      this.resourceId = layoutResourceId;
      this.contacts = data;
    }

    @Override
    public
    int getCount() {
      return contacts.size();
    }

    @Override
    public
    View getView(
      final int position,
      View convertView,
      ViewGroup parent
    ) {
      Log.i(TAG, "position = " + position);

      View itemView = convertView;

      ViewHolder holder;

      if (itemView == null) { // Create a new view if it doesn't exist.
        final LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = li.inflate(resourceId, parent, false);
        holder = new ViewHolder();
        holder.tvItemName = (TextView) itemView.findViewById(R.id.tvName);
        holder.tvItemNumber = (TextView) itemView.findViewById(R.id.tvNumber);
        holder.ivItemSelected = (ImageView) itemView.findViewById(R.id.ivItemSelected);
        holder.rlItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        itemView.setTag(holder);
      }
      else {
        holder = (ViewHolder) itemView.getTag(); // Reuse a view that already exists.
      }

      final Contact item = getItem(position);

      holder.tvItemName.setText(item.getName());
      holder.tvItemNumber.setText(item.getNumber());
      if (item.getIsSelected().equals(Constants.YES)) {
        holder.ivItemSelected.setBackgroundResource(R.drawable.checkmark_selected);
      }
      else {
        holder.ivItemSelected.setBackgroundResource(R.drawable.checkmark);
      }

      final ViewHolder holderTmp = holder;
      holder.rlItem.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public
          void onClick(View v) {
            if (listenerContactsItemSelected != null) {
              String newStatus = (item.getIsSelected().equals(Constants.YES)) ? Constants.NO : Constants.YES;
              listenerContactsItemSelected.onClickedIvContactsItemSelected(item, newStatus);
            }
          }
        });
      
      return itemView;
    }

    public
    void updateList(ArrayList<Contact> items) {
      contacts.clear();
      contacts.addAll(items);
      notifyDataSetChanged();
    }

  }
  
}
