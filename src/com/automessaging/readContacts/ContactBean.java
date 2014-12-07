package com.automessaging.readContacts;

public class ContactBean {
	private String name;
	private String phoneNo;
	boolean selected;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public boolean isSelected() {
	    return selected;
	  }

	  public void setSelected(boolean selected) {
	    this.selected = selected;
	  }
	
}
