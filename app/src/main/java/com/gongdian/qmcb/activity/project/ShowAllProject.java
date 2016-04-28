package com.gongdian.qmcb.activity.project;

import android.content.Intent;
import android.os.Bundle;
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
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.DxtzAdapter;
import com.gongdian.qmcb.adapter.MyexpandableListAdapter;
import com.gongdian.qmcb.model.Dxtz;
import com.gongdian.qmcb.model.DxtzResult;
import com.gongdian.qmcb.model.Project2;
import com.gongdian.qmcb.model.ProjectListResult2;
import com.gongdian.qmcb.model.Project_dw2;
import com.gongdian.qmcb.others.PinnedHeaderExpandableListView;
import com.gongdian.qmcb.others.StickyLayout;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.util.ArrayList;
import java.util.List;

public class ShowAllProject extends AbActivity implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener, StickyLayout.OnGiveUpTouchEventListener {
    private PinnedHeaderExpandableListView expandableListView;
    private StickyLayout stickyLayout;
    private AbTitleBar mAbTitleBar = null;
    private MyApplication application;
    private int currentPage = 1;
    private MyexpandableListAdapter adapter;
    private int pageSize = 50;
    private ArrayList<Project2> groupList = null;
    private ArrayList<List<Project_dw2>> childList = null;
    AlertView mAlertViewExt;//窗口拓展例子
    List<Dxtz> mDxtzList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_project_expand);
        application = (MyApplication) getApplication();
        expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);
        stickyLayout = (StickyLayout) findViewById(R.id.sticky_layout);

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("工程一览");
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

        groupList = new ArrayList<>();

        List<Project_dw2> project_dw2s = new ArrayList<>();
        childList = new ArrayList<>();
        childList.add(project_dw2s);


        adapter = new MyexpandableListAdapter(this, groupList, childList);
        setListViewHeightBasedOnChildren(expandableListView);
        expandableListView.setAdapter(adapter);

        /**展开所有group
         for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
         expandableListView.expandGroup(i);
         }*/


        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);
        stickyLayout.setOnGiveUpTouchEventListener(this);


        refreshTask();

    }

    /***
     * InitData
     */

    /**
     * 下载数据
     */
    public void refreshTask() {
        currentPage = 1;
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        params.put("rowstart", "0");
        params.put("rowend", String.valueOf(currentPage * pageSize));
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowAllProject.this, Constant.GetProject_all, params);
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
                            for (int i = 0; i < projectList.size(); i++) {
                                List<Project_dw2> project_dw2s = projectList.get(i).getProject_dw();
                                childList.add(project_dw2s);
                            }
                            adapter.notifyDataSetChanged();
                            expandableListView.setOnHeaderUpdateListener(ShowAllProject.this);
                            projectList.clear();
                        }
//                        mAbPullToRefreshView.onHeaderRefreshFinish();
                    }
                }

            }
        });

    }

    public void loadMoreTask() {
        currentPage++;
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        params.put("rowstart", String.valueOf(currentPage * (pageSize - 1)));
        params.put("rowend", String.valueOf(currentPage * pageSize));
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowAllProject.this, Constant.GetProject_all, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        ProjectListResult2 projectListResult = AbJsonUtil.fromJson(rtn, ProjectListResult2.class);
                        List<Project2> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getId() != null) {
                            groupList.addAll(projectList);
                            for (int i = 0; i < projectList.size(); i++) {
                                List<Project_dw2> project_dw2s = projectList.get(i).getProject_dw();
                                childList.add(project_dw2s);
                            }
                            adapter.notifyDataSetChanged();
                            expandableListView.setOnHeaderUpdateListener(ShowAllProject.this);
                            projectList.clear();
                        }
//                        mAbPullToRefreshView.onFooterLoadFinish();
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
        final Project2 project2 = groupList.get(groupPosition);
        final Project_dw2 project_dw2 = childList.get(groupPosition).get(childPosition);
        final String pro_id = project2.getId();
        v.findViewById(R.id.child_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("project_dw", project_dw2);
                intent.putExtra("project", project2);
                intent.setClass(ShowAllProject.this, ProjectInfo.class);
                startActivity(intent);
            }
        });
        v.findViewById(R.id.btn202).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDxtzList(pro_id, "202");
            }
        });
        v.findViewById(R.id.btn203).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDxtzList(pro_id, "203");
            }
        });
        v.findViewById(R.id.btn204).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDxtzList(pro_id, "204");
            }
        });
        v.findViewById(R.id.btn205).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDxtzList(pro_id, "205");
            }
        });
        v.findViewById(R.id.btn206).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDxtzList(pro_id, "206");
            }
        });

        return false;
    }

    private void getDxtzList(String pro_id, String menu_id) {
        AbSoapParams params = new AbSoapParams();
        params.put("pro_id", pro_id);
        params.put("menu_id", menu_id);
        WebServiceUntils.call(ShowAllProject.this, Constant.Get_dxtz, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mDxtzList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        DxtzResult dxtzResult = AbJsonUtil.fromJson(result1, DxtzResult.class);
                        List<Dxtz> dxtzList = dxtzResult.getItems();
                        if (dxtzList != null && dxtzList.size() > 0) {
                            mDxtzList.addAll(dxtzList);
                            showDiag();
                        }
                    }
                }
            }
        });

    }

    private void showDiag() {
        View mView = mInflater.inflate(R.layout.dialog_dxtz, null);
        ListView listView = (ListView) mView.findViewById(R.id.chooseList);
        DxtzAdapter adapter = new DxtzAdapter(ShowAllProject.this, mDxtzList);
        listView.setAdapter(adapter);

        mAlertViewExt = new AlertView(null, "短信通知", "取消", null, new String[]{"发送"}, ShowAllProject.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    List<Dxtz> dxtz = new ArrayList<>();
                    for (int i = 0; i < mDxtzList.size(); i++) {
                        if (mDxtzList.get(i).isChoose()) {
                            dxtz.add(mDxtzList.get(i));
                        }
                    }
                    if (dxtz.size() > 0) {
                        AbSoapParams params = new AbSoapParams();
                        DxtzResult dxtzResult = new DxtzResult();
                        dxtzResult.setItems(dxtz);
                        String gson = AbJsonUtil.toJson(dxtzResult);
                        params.put("gson", gson);
                        AbLogUtil.d("xxxxx",gson);
                        WebServiceUntils.call(ShowAllProject.this, Constant.Send_msgs_tz, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
                            @Override
                            public void callback(Boolean aBoolean, String result) {
                                if (AbStrUtil.isEquals(result, "1")) {
                                    MsgUtil.sendMsgTop(ShowAllProject.this, Constant.MSG_INFO, "短信发送成功");
                                }
                            }
                        });
                    }
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();

    }


    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.expand_project_group, null);
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

}