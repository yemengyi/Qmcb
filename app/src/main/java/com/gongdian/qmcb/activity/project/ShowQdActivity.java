package com.gongdian.qmcb.activity.project;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.QdAdapter;
import com.gongdian.qmcb.model.Qd;
import com.gongdian.qmcb.model.QdListResult;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.util.ArrayList;
import java.util.List;

public class ShowQdActivity extends AbActivity {

    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private QmcbUsers users;
    private List<Qd> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private QdAdapter myListViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_mine_project);
        application = (MyApplication) abApplication;
        application.setIsAddProject(false);
        users = application.getUser();

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("签到记录");
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
        myListViewAdapter = new QdAdapter(this, ShowQdActivity.this,mList);
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


    /**
     * 下载数据
     */
    public void refreshTask() {
        AbSoapParams params = new AbSoapParams();
        params.put("sjhm", application.getUser().getUsername());
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowQdActivity.this, Constant.Get_QD, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean && !AbStrUtil.isEquals(rtn,"0")) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功le
                        QdListResult projectListResult = AbJsonUtil.fromJson(rtn, QdListResult.class);
                        List<Qd> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getAddress() != null) {
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
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(ShowQdActivity.this, Constant.Get_QD, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean && !AbStrUtil.isEquals(rtn,"0")) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        QdListResult projectListResult = AbJsonUtil.fromJson(rtn, QdListResult.class);
                        List<Qd> projectList = projectListResult.getItems();
                        if (projectList != null && projectList.get(0).getAddress() != null) {
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
