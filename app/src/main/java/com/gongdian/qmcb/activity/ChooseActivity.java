package com.gongdian.qmcb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.ExpandAdapter;
import com.gongdian.qmcb.adapter.MyGridAdapter;
import com.gongdian.qmcb.model.Department;
import com.gongdian.qmcb.model.Department_Observer;
import com.gongdian.qmcb.model.Users_Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AbActivity {
	private GridView gridView;
	private ExpandableListView expandableListView;
	private List<Department_Observer> groupList = new ArrayList<>();
	private ExpandAdapter adapter;
	private MyGridAdapter gridAdapter;
	private List<Object> gridList = new ArrayList<>();
	private Button btn_commit;
	AbTitleBar mAbTitleBar=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_sendmsg_choose);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("选择人员");
		mAbTitleBar.setLogo(R.drawable.title_selector_back);
		mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
		mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
		mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});
//		initTitleRightLayout();

		setUpViews();
		setUpListeners();
		init();
	}

	private void initTitleRightLayout(){
		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.title_all_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		Button about = (Button) rightViewMore.findViewById(R.id.mapBtn);
		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

	}
	
	private void setUpViews(){
		gridView = (GridView) findViewById(R.id.gridView);
		expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		btn_commit = (Button) findViewById(R.id.commit);
	}
	
	private void setUpListeners(){
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Users_Observer user = groupList.get(groupPosition).getUsers().get(childPosition);
				user.changeChecked();
				adapter.notifyDataSetChanged();
				reFlashGridView();
				return false;
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Object obj = gridList.get(position);
				if (obj instanceof Department) {
					((Department_Observer) obj).changeChecked();
				} else if (obj instanceof Users_Observer) {
					((Users_Observer) obj).changeChecked();
				}
				adapter.notifyDataSetChanged();
				reFlashGridView();
			}
		});

		btn_commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("department", (Serializable) groupList);
				setResult(1000, intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});
	}
	
	private void init(){

		adapter = new ExpandAdapter(this, groupList);
		expandableListView.setAdapter(adapter);
		gridAdapter = new MyGridAdapter(this, gridList);
		gridView.setAdapter(gridAdapter);
		initData();
	}
	

	private void initData(){
		Intent intent = getIntent();
		List<Department_Observer> departments = (List<Department_Observer>)intent.getSerializableExtra("department");
		if(departments!=null){
			groupList.addAll(departments);
			reFlashGridView();
		}
	}

	public void reFlashGridView(){
		gridList.clear();
		if(groupList!=null) {
			for (Department_Observer group : groupList) {
				if (group.isChecked()) {
					gridList.add(group);
				} else {
					if(group.getUsers()!=null) {
						for (Users_Observer user : group.getUsers()) {
							if (user.isChecked()) {
								gridList.add(user);
							}
						}
					}
				}
			}
		}
		gridAdapter.notifyDataSetChanged();
	}

}
