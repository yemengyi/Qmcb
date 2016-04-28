
package com.gongdian.qmcb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.CfAdapter;
import com.gongdian.qmcb.model.Cf1;
import com.gongdian.qmcb.model.Cf1ListResult;
import com.gongdian.qmcb.model.Cf2;
import com.gongdian.qmcb.model.Cf2ListResult;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.util.ArrayList;
import java.util.List;


public class FragmentPublicity extends Fragment {
	
	private MyApplication application;
	private Activity mActivity = null;
	private List<Object> mList = null;
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private CfAdapter myListViewAdapter = null;
	private int currentPage = 1;
	private int total = 50;
	private int pageSize = 5;
	private String method;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		application = (MyApplication) mActivity.getApplication();
		method = getArguments().getString("method");

		View view = inflater.inflate(R.layout.pull_to_refresh_list3, null);
		//获取ListView对象
		mAbPullToRefreshView = (AbPullToRefreshView)view.findViewById(R.id.mPullRefreshView);
		mListView = (ListView)view.findViewById(R.id.mListView);

		//设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(AbPullToRefreshView view) {
				refreshTask();
			}
		});
		mAbPullToRefreshView.setOnFooterLoadListener(new OnFooterLoadListener() {

			@Override
			public void onFooterLoad(AbPullToRefreshView view) {
				loadMoreTask();

			}
		});

		//设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_circular));

		//ListView数据
		mList = new ArrayList<>();

		//使用自定义的Adapter
		myListViewAdapter = new CfAdapter(mActivity, mList);
		mListView.setAdapter(myListViewAdapter);
		//item被点击事件

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				//// TODO: 12/26/15
			}
		});

		refreshTask();


		return view;
	}


	/**
	 * 下载数据
	 */
	public void refreshTask() {
		currentPage = 1;
		// 绑定参数
		AbSoapParams params = new AbSoapParams();

		WebServiceUntils.call(mActivity, method, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
			@Override
			public void callback(Boolean aBoolean, String rtn) {
				mList.clear();
				if (aBoolean) {
					AbResult result = new AbResult(rtn);
					if (result.getResultCode() > 0) {
						//成功
						if (method.equals(Constant.Cf1)) {
							Cf1ListResult listResult1= AbJsonUtil.fromJson(rtn, Cf1ListResult.class);
							List<Cf1> menuList = listResult1.getItems();
							if (menuList != null && menuList.size() > 0) {
								mList.addAll(menuList);
								myListViewAdapter.notifyDataSetChanged();
								menuList.clear();
							}
						}else {
							Cf2ListResult listResult2 = AbJsonUtil.fromJson(rtn, Cf2ListResult.class);
							List<Cf2> menuList = listResult2.getItems();
							if (menuList != null && menuList.size() > 0) {
								mList.addAll(menuList);
								myListViewAdapter.notifyDataSetChanged();
								menuList.clear();
							}
						}

						mAbPullToRefreshView.onHeaderRefreshFinish();
					}
				}
			}
		});

	}
    
    public void loadMoreTask(){
    	currentPage++;
		// 绑定参数
		AbSoapParams params = new AbSoapParams();

		WebServiceUntils.call(mActivity, Constant.Cf1, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
			@Override
			public void callback(Boolean aBoolean, String rtn) {
				mList.clear();
				if (aBoolean) {
					AbResult result = new AbResult(rtn);
					if (result.getResultCode() > 0) {
						//成功
						if (method.equals(Constant.Cf1)) {
							Cf1ListResult listResult1= AbJsonUtil.fromJson(rtn, Cf1ListResult.class);
							List<Cf1> menuList = listResult1.getItems();
							if (menuList != null && menuList.size() > 0) {
								mList.addAll(menuList);
								myListViewAdapter.notifyDataSetChanged();
								menuList.clear();
							}
						}else {
							Cf2ListResult listResult2 = AbJsonUtil.fromJson(rtn, Cf2ListResult.class);
							List<Cf2> menuList = listResult2.getItems();
							if (menuList != null && menuList.size() > 0) {
								mList.addAll(menuList);
								myListViewAdapter.notifyDataSetChanged();
								menuList.clear();
							}
						}

						mAbPullToRefreshView.onFooterLoadFinish();
					}

				}
			}
		});
    }

	public static FragmentPublicity newInstance(String method) {
		FragmentPublicity f = new FragmentPublicity();
		Bundle args = new Bundle();
		args.putString("method", method);
		f.setArguments(args);
		return f;
	}
}

