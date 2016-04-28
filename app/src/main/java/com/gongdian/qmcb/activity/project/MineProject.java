package com.gongdian.qmcb.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.baidu.BaiduMapShowTask;
import com.gongdian.qmcb.adapter.MineProjectAdapter;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.model.Yxc;
import com.gongdian.qmcb.model.YxcListResult;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MineProject extends AbActivity {

    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private QmcbUsers users;
    private List<Yxc> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private MineProjectAdapter myListViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_mine_project);
        application = (MyApplication) abApplication;
        application.setIsAddProject(false);
        users = application.getUser();

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("我的工作");
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
        initTitleRightLayout();


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
        myListViewAdapter = new MineProjectAdapter(this, MineProject.this,mList);
        mListView.setAdapter(myListViewAdapter);
        //item被点击事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });

        //第一次下载数据
        refreshTask();
    }

    private void initTitleRightLayout(){
            mAbTitleBar.clearRightView();
            View rightViewMore = mInflater.inflate(R.layout.title_map_btn, null);
            mAbTitleBar.addRightView(rightViewMore);
            Button about = (Button) rightViewMore.findViewById(R.id.mapBtn);
            about.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MineProject.this, BaiduMapShowTask.class);
                    intent.putExtra("project", (Serializable) mList);
                    startActivity(intent);
                }

            });

    }

    /**
     * 下载数据
     */
    public void refreshTask() {
        AbSoapParams params = new AbSoapParams();
        params.put("sjhm", application.getUser().getUsername());
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(MineProject.this, Constant.GetYXC_QD, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean && !AbStrUtil.isEquals(rtn,"0")) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功le
                        YxcListResult projectListResult = AbJsonUtil.fromJson(rtn, YxcListResult.class);
                        List<Yxc> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getDwmc() != null) {
                            mList.addAll(projectList);
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
        AbSoapParams params = new AbSoapParams();
        params.put("sjhm", application.getUser().getUsername());
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(MineProject.this, Constant.GetYXC_QD, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean && !AbStrUtil.isEquals(rtn,"0")) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        YxcListResult projectListResult = AbJsonUtil.fromJson(rtn, YxcListResult.class);
                        List<Yxc> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getDwmc() != null) {
                            mList.addAll(projectList);
                            myListViewAdapter.notifyDataSetChanged();
                            projectList.clear();
                        }
                        mAbPullToRefreshView.onFooterLoadFinish();
                    }
                }

            }
        });

    }


    private void itemClick(int position) {
    }




}
