package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class UsersListResult extends AbResult {

	private List<Users> items;

	public List<Users> getItems() {
		return items;
	}

	public void setItems(List<Users> items) {
		this.items = items;
	}

}
