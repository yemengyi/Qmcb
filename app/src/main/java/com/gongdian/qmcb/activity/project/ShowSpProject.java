package com.gongdian.qmcb.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.SpListAdapter;
import com.gongdian.qmcb.model.Project2;
import com.gongdian.qmcb.model.ProjectListResult2;
import com.gongdian.qmcb.model.Project_jd2;
import com.gongdian.qmcb.others.PinnedHeaderExpandableListView;
import com.gongdian.qmcb.others.StickyLayout;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.util.ArrayList;
import java.util.List;

public class ShowSpProject extends AbActivity implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener, StickyLayout.OnGiveUpTouchEventListener {
    private PinnedHeaderExpandableListView expandableListView;
    private AbTitleBar mAbTitleBar = null;
    private MyApplication application;
    private SpListAdapter adapter;
    private ArrayList<Project2> groupList = null;
    private ArrayList<List<Project_jd2>> childList =null;
    private AbPullToRefreshView mAbPullToRefreshView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_project_sp);
        application = (MyApplication)getApplication();
        expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);
        mAbPullToRefreshView = (AbPullToRefreshView)findViewById(R.id.mPullRefreshView);

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("违规审批");
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        //设置监听器
        mAbPullToRefreshView.setOnHeaderRefreshListener(new AbPullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(AbPullToRefreshView view) {
                refreshTask();
            }
        });
        mAbPullToRefreshView.setOnFooterLoadListener(new AbPullToRefreshView.OnFooterLoadListener() {
            @Override
            public void onFooterLoad(AbPullToRefreshView view) {
                loadMoreTask();
            }
        });
        //设置进度条的样式
        mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(ContextCompat.getDrawable(this, R.drawable.progress_circular));
        mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(ContextCompat.getDrawable(this, R.drawable.progress_circular));

        groupList = new ArrayList<>();
        childList = new ArrayList<>();

        adapter = new SpListAdapter(this,groupList,childList);
        setListViewHeightBasedOnChildren(expandableListView);
        expandableListView.setAdapter(adapter);

//       展开所有group
        for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
            expandableListView.expandGroup(i);
        }


        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);

        refreshTask();

    }

    /***
     * InitData
     */

    /**
     * 下载数据
     */
    public void refreshTask() {
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowSpProject.this, Constant.GetProject_sp, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                groupList.clear();
                childList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功le
                        ProjectListResult2 projectListResult = AbJsonUtil.fromJson(rtn, ProjectListResult2.class);
                        List<Project2> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getId() != null) {
                            groupList.addAll(projectList);
                            for (int i=0;i<projectList.size();i++) {
                                List<Project_jd2> project_jd2s = projectList.get(i).getProject_jd();
                                childList.add(project_jd2s);
                            }
                            adapter.notifyDataSetChanged();
                            expandableListView.setOnHeaderUpdateListener(ShowSpProject.this);
                            projectList.clear();
                        }
                        mAbPullToRefreshView.onHeaderRefreshFinish();
                    }
                }

            }
        });

    }

    public void loadMoreTask() {
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowSpProject.this, Constant.GetProject_sp, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                groupList.clear();
                childList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        ProjectListResult2 projectListResult = AbJsonUtil.fromJson(rtn, ProjectListResult2.class);
                        List<Project2> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getId() != null) {
                            groupList.addAll(projectList);
                            for (int i=0;i<projectList.size();i++) {
                                List<Project_jd2> project_jd2s = projectList.get(i).getProject_jd();
                                childList.add(project_jd2s);
                            }
                            adapter.notifyDataSetChanged();
                            expandableListView.setOnHeaderUpdateListener(ShowSpProject.this);
                            projectList.clear();
                        }
                        mAbPullToRefreshView.onFooterLoadFinish();
                    }
                }

            }
        });

    }


    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
                                int groupPosition, final long id) {

        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Project2 project2 = groupList.get(groupPosition);
        Project_jd2 project_jd2 = childList.get(groupPosition).get(childPosition);
        Intent intent = new Intent();
        intent.putExtra("project_jd",project_jd2);
        intent.putExtra("project",project2);
        intent.putExtra("sp","1");
        intent.setClass(ShowSpProject.this, ProjectInfo.class);
        startActivityForResult(intent, Constant.SpResultCode);
//        Toast.makeText(ShowAllProject.this, childList.get(groupPosition).get(childPosition).getTitle(),Toast.LENGTH_LONG).show();
        return false;
    }


    @Override
    public View getPinnedHeader() {
        View headerView = getLayoutInflater().inflate(R.layout.expand_project_group, null);
        headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Project2 firstVisibleGroup = (Project2) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group_title);
        textView.setText(firstVisibleGroup.getTitle());
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.SpResultCode ) {
            refreshTask();
        }
    }

}