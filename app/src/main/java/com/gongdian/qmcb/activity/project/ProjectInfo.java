package com.gongdian.qmcb.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbAppUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.baidu.BaiduMapShowPhoto;
import com.gongdian.qmcb.adapter.ProjectInfoAdapter;
import com.gongdian.qmcb.model.Project2;
import com.gongdian.qmcb.model.Project_dw2;
import com.gongdian.qmcb.model.Project_jd2;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectInfo extends AbActivity {

    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private Users users;
    private Project_dw2 project_dw = null;
    private List<Project_jd2> mList = null;
    private ListView mListView = null;
    private ProjectInfoAdapter myListViewAdapter = null;
    private Boolean isSp = false; //是否是审批界面
    @AbIocView(id = R.id.title)
    TextView title;
    @AbIocView(id = R.id.commit)
    Button commit;
    @AbIocView(id = R.id.noCommit)
    Button noCommit;
    @AbIocView(id = R.id.checkbox)
    CheckBox checkbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_project_info);
        application = (MyApplication) abApplication;
//        users = application.getUsers();

        mAbTitleBar = this.getTitleBar();

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

        DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(this); //获取屏幕长宽
        int w = displayMetrics.widthPixels; //720 1440
        int h = displayMetrics.heightPixels; //1280 2392

        mListView = (ListView) findViewById(R.id.mListView);
        //使用自定义的Adapter
        mList = new ArrayList<>();
        myListViewAdapter = new ProjectInfoAdapter(this, mList, (w - 100) / 3, (w - 100) / 3);
        mListView.setAdapter(myListViewAdapter);
        //item被点击事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommit("1");
            }
        });
        noCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommit("2");
            }
        });

//        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    checkbox.setChecked(true);
//                }else {
//                    checkbox.setChecked(false);
//                }
//
//            }
//        });
        initDate();
    }

    private void initDate() {
        Intent intent = this.getIntent();

        if (AbStrUtil.isEquals(intent.getStringExtra("sp"), "1")) {
            isSp = true;
        } else {
            commit.setVisibility(View.GONE);
            noCommit.setVisibility(View.GONE);
            checkbox.setVisibility(View.GONE);
        }
        Project2 project = (Project2) intent.getSerializableExtra("project");
        if (isSp) {
            Project_jd2 project_jd2 = (Project_jd2) intent.getSerializableExtra("project_jd");
            if (AbStrUtil.isEquals(project_jd2.getSfgs(), "1")) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }
            mList.add(project_jd2);
        } else {
            project_dw = (Project_dw2) intent.getSerializableExtra("project_dw");
            List<Project_jd2> temp = project_dw.getProject_jd();
            if (temp != null) {
                mList.addAll(temp);
            }
        }

        title.setText(project.getTitle());
        mAbTitleBar.setTitleText(project.getMc());
        myListViewAdapter.notifyDataSetChanged();
    }


    private void itemClick(int position) {

    }


    private void initTitleRightLayout() {
        mAbTitleBar.clearRightView();
        View rightViewMore = mInflater.inflate(R.layout.title_map_btn, null);
        mAbTitleBar.addRightView(rightViewMore);
        Button about = (Button) rightViewMore.findViewById(R.id.mapBtn);
        about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProjectInfo.this, BaiduMapShowPhoto.class);
                if (isSp) {
                    intent.putExtra("project_jd", (Serializable) mList);
                    intent.putExtra("sp", "1");
                } else {
                    intent.putExtra("project_dw", project_dw);
                    intent.putExtra("sp", "0");
                }

                startActivity(intent);
            }

        });

    }


    private void setCommit(String commit) {
        String jd_id = mList.get(0).getId();
        AbSoapParams params = new AbSoapParams();
        params.put("id", jd_id);
        params.put("yxbz", commit);
        if (checkbox.isChecked()) {
            params.put("sfgs", "1");
        } else {
            params.put("sfgs", "0");
        }
        WebServiceUntils.call(ProjectInfo.this, Constant.Commit_sp, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean && AbStrUtil.isEquals(result, "1")) {
                    MsgUtil.sendMsgTop(ProjectInfo.this, Constant.MSG_INFO, "审批成功!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    }, 1000);
                } else {
                    MsgUtil.sendMsgTop(ProjectInfo.this, Constant.MSG_ALERT, "审批失败!");
                }
            }
        });
    }


}
