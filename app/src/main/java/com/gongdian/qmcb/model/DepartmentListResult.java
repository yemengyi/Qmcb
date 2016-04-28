package com.gongdian.qmcb.model;

import com.ab.model.AbResult;

import java.util.List;

/**
 * 
 *
 */
public class DepartmentListResult extends AbResult {

	private List<Department> items;

	public List<Department> getItems() {
		return items;
	}

	public void setItems(List<Department> items) {
		this.items = items;
	}
	
	

}
