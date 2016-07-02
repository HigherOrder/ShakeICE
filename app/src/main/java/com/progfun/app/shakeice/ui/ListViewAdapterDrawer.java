package com.progfun.app.shakeice.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.progfun.app.shakeice.R;
import com.progfun.app.shakeice.models.ItemDrawer;

/**
 * Created by p on 01/03/15.
 */
public class ListViewAdapterDrawer extends ArrayAdapter<ItemDrawer> {

    Context context;
    List<ItemDrawer> lItemDrawers;
    int layoutResID;

    public ListViewAdapterDrawer(Context context, int layoutResourceID,
                                 List<ItemDrawer> listItems) {
      super(context, layoutResourceID, listItems);
      this.context = context;
      this.lItemDrawers = listItems;
      this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // TODO Auto-generated method stub

      HolderItemDrawer holder;
      View view = convertView;

      if (view == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        holder = new HolderItemDrawer();

        view = inflater.inflate(layoutResID, parent, false);
        holder.tvName = (TextView) view.findViewById(R.id.tvName);
        holder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);

        view.setTag(holder);

      } else {
        holder = (HolderItemDrawer) view.getTag();
      }

      ItemDrawer item = (ItemDrawer) this.lItemDrawers.get(position);
      holder.ivIcon.setImageDrawable(view.getResources().getDrawable(item.getImageResourceId()));
      holder.tvName.setText(item.getName());

      return view;
    }

    private static class HolderItemDrawer {
      TextView tvName;
      ImageView ivIcon;
    }
}
