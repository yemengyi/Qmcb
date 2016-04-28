package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class QdListResult extends AbResult {

	private List<Qd> items;

	public List<Qd> getItems() {
		return items;
	}

	public void setItems(List<Qd> items) {
		this.items = items;
	}

}
