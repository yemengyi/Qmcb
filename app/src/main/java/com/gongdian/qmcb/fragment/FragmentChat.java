
package com.gongdian.qmcb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ab.soap.AbSoapUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.RegisterActivity;
import com.gongdian.qmcb.activity.admin.DepartmentActivity;
import com.gongdian.qmcb.activity.admin.UsersActivity;
import com.gongdian.qmcb.activity.project.ShowAllProject;
import com.gongdian.qmcb.activity.project.ShowProjectByMenuActivity;
import com.gongdian.qmcb.adapter.MenuListAdapter;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;


public class FragmentChat extends Fragment {

    private MyApplication application;
    private Activity mActivity = null;
    private List<Menu> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private MenuListAdapter myListViewAdapter = null;
    private AbSoapUtil mAbSoapUtil = null;
    private LinearLayout mLoginLayout = null;
    private ImageView mLoginButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();

        View view = inflater.inflate(R.layout.pull_to_refresh_list, null);
        //获取ListView对象
        mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);
        mListView = (ListView) view.findViewById(R.id.mListView);
        mLoginLayout = (LinearLayout) view.findViewById(R.id.login);
        mLoginButton = (ImageView) view.findViewById(R.id.head);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RegisterActivity.class);
                getActivity().startActivityForResult(intent, Constant.LoginResultCode);
            }
        });


        //设置进度条的样式
        mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_circular));
        mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_circular));

        //ListView数据
        mList = new ArrayList<>();

        //使用自定义的Adapter
        myListViewAdapter = new MenuListAdapter(mActivity, mList);
        mListView.setAdapter(myListViewAdapter);
        //item被点击事件

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //// TODO: 12/26/15
                Menu menu = (Menu) parent.getItemAtPosition(position);
                if (menu == null || menu.getMenu() == null) {
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("menu", menu);
                intent.putExtras(bundle);
                switch (menu.getMenu()) {
                    case Constant.MENU1: //部门
                        intent.setClass(mActivity, DepartmentActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    case Constant.MENU2: //人员
                        intent.setClass(mActivity, UsersActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    case Constant.MENU3: //计划
                    case Constant.MENU4: //勘查
                    case Constant.MENU5: //开工
                    case Constant.MENU6: //到岗到位
                    case Constant.MENU7: //现场督察
                    case Constant.MENU8: //完工
                        intent.setClass(mActivity, ShowProjectByMenuActivity.class);
                        mActivity.startActivityForResult(intent,Constant.ModifyProjectResultCode);
                        break;
                    case Constant.MENU9: //一览
                        intent.setClass(mActivity, ShowAllProject.class);
                        mActivity.startActivityForResult(intent, Constant.ModifyProjectResultCode);
                        break;
                    default:
                        break;
                }

            }
        });


        mAbSoapUtil = AbSoapUtil.getInstance(mActivity);
        mAbSoapUtil.setTimeout(10000);

        return view;
    }





}

