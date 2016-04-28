package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class Cf2ListResult extends AbResult {

	public List<Cf2> getItems() {
		return items;
	}

	private List<Cf2> items;

	public void setItems(List<Cf2> items) {
		this.items = items;
	}

}
