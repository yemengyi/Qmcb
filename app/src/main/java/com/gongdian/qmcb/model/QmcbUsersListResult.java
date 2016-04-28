package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class QmcbUsersListResult extends AbResult {

	private List<QmcbUsers> items;

	public List<QmcbUsers> getItems() {
		return items;
	}

	public void setItems(List<QmcbUsers> items) {
		this.items = items;
	}

}
