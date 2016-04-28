package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class Cf1ListResult extends AbResult {

	public List<Cf1> getItems() {
		return items;
	}

	private List<Cf1> items;

	public void setItems(List<Cf1> items) {
		this.items = items;
	}

}
