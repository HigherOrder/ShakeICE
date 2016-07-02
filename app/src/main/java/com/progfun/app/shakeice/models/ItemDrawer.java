package com.progfun.app.shakeice.models;

/**
 * Created by p on 01/03/15.
 */
public class ItemDrawer {

    String name;
    int imageResourceId;

    public ItemDrawer(String itemName, int imageResourceId) {
      super();
      name = itemName;
      this.imageResourceId = imageResourceId;
    }

    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public int getImageResourceId() {
      return imageResourceId;
    }
    public void setImageResourceId(int imageResourceId) {
      this.imageResourceId = imageResourceId;
    }

}
