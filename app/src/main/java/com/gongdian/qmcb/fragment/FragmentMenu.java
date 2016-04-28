
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

import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.bigkoo.alertview.AlertView;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.RegisterActivity;
import com.gongdian.qmcb.activity.photo.QdActivity;
import com.gongdian.qmcb.activity.photo.YxcPhotoActivity;
import com.gongdian.qmcb.adapter.MenuListAdapter;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.model.MenuListResult;
import com.gongdian.qmcb.model.Yxc;
import com.gongdian.qmcb.model.YxcListResult;
import com.gongdian.qmcb.utils.AppUtil;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.util.ArrayList;
import java.util.List;


public class FragmentMenu extends Fragment {

    private MyApplication application;
    private Activity mActivity = null;
    private List<Menu> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private MenuListAdapter myListViewAdapter = null;
    private int currentPage = 1;
    private int total = 50;
    private int pageSize = 10;
    private AbSoapUtil mAbSoapUtil = null;
    private LinearLayout mLoginLayout = null;
    private ImageView mLoginButton = null;

    private List<Yxc> mlist_yxc = null;
    AlertView mAlertViewExt;//窗口拓展例子


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
        mlist_yxc = new ArrayList<>();

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
               
                switch (menu.getMenu()) {
                    case Constant.MENU1: //启动
                        showDiag();
                        break;
                    case Constant.MENU2: //签到
                        AppUtil.start_Activity(mActivity, QdActivity.class);
                        break;
//                    case Constant.MENU8: //完工
//                        intent.setClass(mActivity, ShowProjectByMenuActivity.class);
//                        mActivity.startActivityForResult(intent,Constant.ModifyProjectResultCode);
//                        break;
//                    case Constant.MENU9: //一览
//                        intent.setClass(mActivity, ShowAllProject.class);
//                        mActivity.startActivityForResult(intent, Constant.ModifyProjectResultCode);
//                        break;
                    default:
                        break;
                }

            }
        });


        mAbSoapUtil = AbSoapUtil.getInstance(mActivity);
        mAbSoapUtil.setTimeout(10000);

        refreshTask();
        checkLogin();
        return view;
    }

    private void checkLogin() {
        if (application.isLogin) {
            mAbPullToRefreshView.setVisibility(View.VISIBLE);
            mLoginLayout.setVisibility(View.INVISIBLE);
        } else {
            mAbPullToRefreshView.setVisibility(View.INVISIBLE);
            mLoginLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 下载数据
     */
    public void refreshTask() {
        currentPage = 1;
        // 绑定参数
        AbSoapParams params = new AbSoapParams();
        params.put("role", String.valueOf(application.getUser().getRole()));
        params.put("sjhm",application.getUser().getUsername());
        WebServiceUntils.call(mActivity, Constant.GetMenu, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        MenuListResult menuListResult = AbJsonUtil.fromJson(rtn, MenuListResult.class);
                        List<Menu> menuList = menuListResult.getItems();
                        if (menuList != null && menuList.size() > 0) {
                            mList.addAll(menuList);
                            application.setMenuList(mList);
                            myListViewAdapter.notifyDataSetChanged();
                            menuList.clear();
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
        params.put("role", String.valueOf(application.getUser().getRole()));
        params.put("sjhm",application.getUser().getUsername());
        WebServiceUntils.call(mActivity, Constant.GetMenu, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                mList.clear();
                AbResult result = new AbResult(rtn);
                if (result.getResultCode() > 0) {
                    //成功
                    MenuListResult menuListResult = AbJsonUtil.fromJson(rtn, MenuListResult.class);
                    List<Menu> menuList = menuListResult.getItems();
                    if (menuList != null && menuList.size() > 0) {
                        mList.addAll(menuList);
                        myListViewAdapter.notifyDataSetChanged();
                        menuList.clear();
                    }
                    mAbPullToRefreshView.onFooterLoadFinish();
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
//        if (application.getUserChanged1()) {
        checkLogin();
//            application.setUserChanged1(false);
//        }
    }

    public void initData(List<Menu> projects) {
        mList.clear();
        mList.addAll(projects);
        myListViewAdapter.notifyDataSetChanged();
    }

    private void showDiag() {
        AbSoapParams params = new AbSoapParams();
        params.put("sjhm", application.getUser().getUsername());
        WebServiceUntils.call(mActivity, Constant.GetYXC, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                if (aBoolean && !AbStrUtil.isEquals(rtn, "0")) {
                    mlist_yxc.clear();
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        YxcListResult yxcListResult = AbJsonUtil.fromJson(rtn, YxcListResult.class);
                        List<Yxc> menuList = yxcListResult.getItems();
                        if (menuList != null && menuList.size() > 0) {
                            mlist_yxc.addAll(menuList);
                            menuList.clear();
                            showMenu();
                        }
                    }
                } else {
                    new AlertView("提示", "没有未启动的村任务!", null, new String[]{"确定"}, null, mActivity, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                        }
                    }).show();
                }
            }
        });
    }

    private void showMenu() {
        //按钮＋列表 dialog
//        View mView = mActivity.getLayoutInflater().inflate(R.layout.dialog_choose2, null);
//        ListView listView = (ListView) mView.findViewById(R.id.list);
//        YxcChooseOneAdapter chooseOneAdapter = new YxcChooseOneAdapter(mActivity, mlist_yxc);
//        listView.setAdapter(chooseOneAdapter);
//        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, mActivity, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
//            @Override
//            public void onItemClick(Object o, int position) {
//                //判断是否是拓展窗口View，而且点击的是非取消按钮
//                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
//                    for (int i=0;i<mlist_yxc.size();i++) {
//                        if (mlist_yxc.get(i).getChoose()) {
//                            // TODO: 4/23/16
//                            break;
//                        }
//                    }
//                    return;
//                }
//            }
//        });
//        mAlertViewExt.addExtView(mView);
//        mAlertViewExt.show();
        String[] s = new String[mlist_yxc.size()];
        for (int i=0;i<mlist_yxc.size();i++) {
            s[i] = mlist_yxc.get(i).getDwmc();
        }

        new AlertView("请选择", null, "取消", null, s, mActivity, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

                if (position!=-1) {

                Yxc yxc = mlist_yxc.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("yxc", yxc);
                intent.putExtras(bundle);
                intent.setClass(mActivity, YxcPhotoActivity.class);
                mActivity.startActivityForResult(intent,Constant.YxcResultCode);
                }

            }
        }).show();
    }





}

