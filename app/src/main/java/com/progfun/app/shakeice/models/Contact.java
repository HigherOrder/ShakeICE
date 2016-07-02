package com.progfun.app.shakeice.models;

/**
 * Created by p on 01/02/15.
 */
public class Contact {

  long id;
  String name;
  String number;
  String isSelected;
  String isPrimary;

  public
  Contact() {
  }

  public
  Contact(long id, String name, String number, String isSelected, String isPrimary) {
    this.id = id;
    this.name = name;
    this.number = number;
    this.isSelected = isSelected;
    this.isPrimary = isPrimary;
  }

  public
  Contact(Contact that) {
    this.id = that.getId();
    this.name = that.getName();
    this.number = that.getNumber();
    this.isSelected = that.getIsSelected();
    this.isPrimary = that.getIsPrimary();
  }

  // Getter and Setter Methods

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(String isSelected) {
    this.isSelected = isSelected;
  }

  public String getIsPrimary() {
    return isPrimary;
  }

  public void setIsPrimary(String isPrimary) {
    this.isPrimary = isPrimary;
  }

  @Override
  public boolean equals(Object o) {
    Contact that = (Contact) o;
    return ( (that.id == id) && (that.number.equals(number)) ) ? true : false;
  }

  @Override
  public int hashCode() {
    return (int) id;
  }

}
