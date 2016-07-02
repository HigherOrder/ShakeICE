package com.progfun.app.shakeice.models;

/**
 * Created by p on 01/02/15.
 */
public class ItemActivity {

  long id;
  String name;
  String description;
  String messageAlert;
  double impact;
  String isSelected;

  // Empty Constructor
  public ItemActivity() {

  }

  // Constructor
  public ItemActivity(
          long id,
          String name,
          String description,
          String messageAlert,
          double impact,
          String isSelected
          ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.messageAlert = messageAlert;
    this.impact = impact;
    this.isSelected = isSelected;
  }

  // Getter and Setter Methods

  public long getActivityId() {
    return id;
  }
  public void setActivityId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getMessageAlert() {
    return messageAlert;
  }
  public void setMessageAlert(String messageAlert) {
    this.messageAlert = messageAlert;
  }

  public double getImpact() {
    return impact;
  }
  public void setImpact(double impact) {
    this.impact = impact;
  }

  public String getIsSelected() {
    return isSelected;
  }
  public void setIsSelected(String isSelected) {
    this.isSelected = isSelected;
  }

}
