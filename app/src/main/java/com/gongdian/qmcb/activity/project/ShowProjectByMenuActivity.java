package com.gongdian.qmcb.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.ShowProjectByMenuAdapter;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.ProjectListResult;
import com.gongdian.qmcb.model.Project_dw;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.utils.AppUtil;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowProjectByMenuActivity extends AbActivity {

    @AbIocView(id = R.id.lay_add_project)
    LinearLayout lay_add_project;
    @AbIocView(id = R.id.contact_add)
    ImageButton addButton;
    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private Users users;
    private List<Project> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private ShowProjectByMenuAdapter myListViewAdapter = null;
    private int currentPage = 1;
    private int pageSize = 10;
    private Menu mCurrentMenu = null;
    private int choose = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_show_project_by_menu);
        application = (MyApplication) abApplication;
        application.setIsAddProject(false);
//        users = application.getUsers();
        Intent intent = this.getIntent();
        mCurrentMenu = (Menu) intent.getSerializableExtra("menu");
        if (!AbStrUtil.isEquals(mCurrentMenu.getMenu(), Constant.MENU3)) {
            lay_add_project.setVisibility(View.GONE);
        }

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(mCurrentMenu.getMenu());
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
        mListView = (ListView) findViewById(R.id.mListView);

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

        //ListView数据
        mList = new ArrayList<>();

        //使用自定义的Adapter
        myListViewAdapter = new ShowProjectByMenuAdapter(this, mList);
        mListView.setAdapter(myListViewAdapter);
        //item被点击事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });
        /**添加工程*/
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.start_ResultActivity(ShowProjectByMenuActivity.this, AddProjectActivity.class, Constant.AddProjectResultCode);
            }
        });

        //第一次下载数据
        refreshTask();
    }


    /**
     * 下载数据
     */
    public void refreshTask() {
        currentPage = 1;
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        params.put("menu_id", mCurrentMenu.getId());
        params.put("rowstart", "0");
        params.put("rowend", String.valueOf(currentPage * pageSize));
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowProjectByMenuActivity.this, Constant.GetProject, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功le
                        ProjectListResult projectListResult = AbJsonUtil.fromJson(rtn, ProjectListResult.class);
                        List<Project> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getId() != null) {
                            /**根据PID筛选重组工程*/
//                            ToListProject toListProject = new ToListProject(projectList, mCurrentMenu.getId(), application.getUsers().getPid());
//                            List<Project> templist = toListProject.toList();
//                            mList.addAll(templist);
                            myListViewAdapter.notifyDataSetChanged();
                            projectList.clear();
                        }
                        mAbPullToRefreshView.onHeaderRefreshFinish();
                    }
                }

            }
        });

    }

    public void loadMoreTask() {
        currentPage++;
        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        params.put("menu_id", mCurrentMenu.getId());
        params.put("rowstart", String.valueOf(currentPage * (pageSize - 1)));
        params.put("rowend", String.valueOf(currentPage * pageSize));
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowProjectByMenuActivity.this, Constant.GetProject, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        ProjectListResult projectListResult = AbJsonUtil.fromJson(rtn, ProjectListResult.class);
                        List<Project> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getId() != null) {
                            /**根据PID筛选重组工程*/
//                            ToListProject toListProject = new ToListProject(projectList, mCurrentMenu.getId(), application.getUsers().getPid());
//                            List<Project> templist = toListProject.toList();
//                            mList.addAll(templist);
                            myListViewAdapter.notifyDataSetChanged();
                            projectList.clear();
                        }
                        mAbPullToRefreshView.onFooterLoadFinish();
                    }
                }

            }
        });

    }

    private void showDiag(final Project project) {

        choose = -1;
        AlertView mAlertView = new AlertView("操作菜单", null, "取消", null,
                new String[]{"修改计划", "删除计划"},
                ShowProjectByMenuActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                choose = position;
            }
        });
        mAlertView.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                switch (choose) {
                    case 0://修改
                        if (checkProject(project)) {
                            AppUtil.start_ResultActivity_bundle(ShowProjectByMenuActivity.this, ModifyProjectlActivity.class, "project", project, Constant.AddProjectResultCode);
                        } else {
                            MsgUtil.sendMsgTop(ShowProjectByMenuActivity.this, Constant.MSG_CONFIRM, "该计划已经开工,不能更改!");
                        }
                        break;
                    case 1://删除
                        if (checkProject(project)) {
                        new AlertView("请再次确认", "是否删除 " + project.getId() + "计划信息 ?", "取消", new String[]{"确定"}, null, ShowProjectByMenuActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    AbSoapParams params = new AbSoapParams();
                                    params.put("action", "delete");
                                    params.put("gson", AbJsonUtil.toJson(project));
                                    WebServiceUntils.call(ShowProjectByMenuActivity.this, Constant.Modify_Project, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
                                        @Override
                                        public void callback(Boolean aBoolean, String result) {
                                            if (aBoolean && result.equals("1")) {
                                                MsgUtil.sendMsgTop(ShowProjectByMenuActivity.this, Constant.MSG_INFO, "删除计划成功");
                                                refreshTask();
                                            }
                                        }
                                    });
                                }
                            }
                        }).show();
                        } else {
                            MsgUtil.sendMsgTop(ShowProjectByMenuActivity.this, Constant.MSG_CONFIRM, "该计划已经开工,不能删除!");
                        }
                        break;
                }
            }
        });
        mAlertView.show();
    }

    private void itemClick(int position) {
        Project project = mList.get(position);
        Intent intent = new Intent();
        intent.setClass(ShowProjectByMenuActivity.this, AddProjectPhotoActivity.class);
        intent.putExtra("menu_id", mCurrentMenu.getId());
        intent.putExtra("menu", mCurrentMenu.getMenu());
        intent.putExtra("project", project);
        switch (mCurrentMenu.getMenu()) {
            case Constant.MENU3: //计划
//                if (AbStrUtil.isEquals(project.getCreateuser(), application.getUsers().getId())) {
                    showDiag(project);
//                } else {
//                    MsgUtil.sendMsgTop(ShowProjectByMenuActivity.this, Constant.MSG_ALERT, "创建人员不是当前登陆人员,不能操作");
//                }
                break;
            case Constant.MENU5: //开工
                //检查开工日期
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String s = project.getKssj().toString();
                try {
                    Date date = format.parse(s);
                    Date now = new Date();
                    if (date.getTime() > now.getTime() ) {
                        MsgUtil.sendMsgTop(ShowProjectByMenuActivity.this, Constant.MSG_CONFIRM, "还未到计划开工时间!");
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, Constant.AddProjectResultCode);
                break;
            case Constant.MENU4: //勘查
            case Constant.MENU6: //到岗
            case Constant.MENU7: //督察
            case Constant.MENU8: //完工
                startActivityForResult(intent, Constant.AddProjectResultCode);
                break;
            default:
                break;
        }
    }

    private Boolean checkProject(Project project) {
        List<Project_dw> project_dws = project.getProject_dw();
        for (int i = 0; i < project_dws.size(); i++) {
            if (AbStrUtil.isEquals(project_dws.get(i).getFlag(), "3") || AbStrUtil.isEquals(project_dws.get(i).getFlag(), "4")) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.AddProjectResultCode && application.getIsAddProject()) {
            refreshTask();
        }
    }


}
