package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class DepartmentListResult_Observer extends AbResult {

	private List<Department_Observer> items;

	public List<Department_Observer> getItems() {
		return items;
	}

	public void setItems(List<Department_Observer> items) {
		this.items = items;
	}
	
	

}
