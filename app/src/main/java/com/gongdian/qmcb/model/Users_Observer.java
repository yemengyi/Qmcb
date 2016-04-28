package com.gongdian.qmcb.model;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

public class Users_Observer extends Observable implements Observer,Serializable {
	private String id;
	private String pcode;
	private String uname;
	private boolean isChecked;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public void changeChecked(){
		isChecked = !isChecked;
		setChanged();
		notifyObservers();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof Boolean) {
			this.isChecked = (Boolean) data;
		}
	}

}
