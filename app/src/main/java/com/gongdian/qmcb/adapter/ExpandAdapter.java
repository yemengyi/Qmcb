package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.ChooseActivity;
import com.gongdian.qmcb.model.Department_Observer;
import com.gongdian.qmcb.model.Users_Observer;

import java.util.List;

public class ExpandAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private List<Department_Observer> groupList;
	LayoutInflater inflater;

	public ExpandAdapter(Context ctx, List<Department_Observer> groupList){
		this.ctx = ctx;
		this.groupList = groupList;
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupList.get(groupPosition).getUsers().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupList.get(groupPosition).getUsers().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final Department_Observer group = (Department_Observer) getGroup(groupPosition);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group_item, null);
		}
		TextView groupText = (TextView) convertView.findViewById(R.id.groupText);
		CheckBox groupCheckBox = (CheckBox) convertView.findViewById(R.id.groupCheckBox);
		groupText.setText(group.getPname());
		groupCheckBox.setChecked(group.isChecked());
		groupCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				group.changeChecked();
				notifyDataSetChanged();
				((ChooseActivity)ctx).reFlashGridView();
			}
		});
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Users_Observer user = (Users_Observer) getChild(groupPosition, childPosition);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_item, null);
		}
		TextView childText = (TextView) convertView.findViewById(R.id.childText);
		CheckBox childCheckBox = (CheckBox) convertView.findViewById(R.id.childCheckBox);
		childText.setText(user.getUname());
		childCheckBox.setChecked(user.isChecked());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
