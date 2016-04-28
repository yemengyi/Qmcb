package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Department;
import com.gongdian.qmcb.model.Users_Observer;

import java.util.ArrayList;
import java.util.List;

public class MyGridAdapter extends BaseAdapter {

	private List<Object> list = new ArrayList<>();
	private LayoutInflater inflater;
	public MyGridAdapter(Context ctx, List<Object> list){
		this.list = list;
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.grid_view_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.textView);
		Object obj = getItem(position);
		if (obj instanceof Department) {
			textView.setText(((Department)obj).getPname());
		} else if (obj instanceof Users_Observer) {
			textView.setText(((Users_Observer) obj).getUname());
		}
		return convertView;
	}

}
