package com.gongdian.qmcb.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.DepartmentChooseOneAdapter;
import com.gongdian.qmcb.adapter.UsersListAdapter;
import com.gongdian.qmcb.model.Department;
import com.gongdian.qmcb.model.DepartmentListResult;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.model.UsersListResult;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils2;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AbActivity {

    @AbIocView(id = R.id.contact_add, click = "btnclick")
    ImageButton imageButton;
    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private Users users;
    private List<Users> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private UsersListAdapter myListViewAdapter = null;
    private int currentPage = 1;
    private AbSoapUtil mabSoapUtil = null;
    private View mAvatarView = null;
    View mView = null;
    private DepartmentChooseOneAdapter mDepartmentChooseAdapter = null;
    private List<Department> mList_dw = null;
    private String t_uname="",t_udis="",t_pcode="";

    private InputMethodManager imm;
    AlertView mAlertViewExt;//窗口拓展例子
    private AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
    private int choose = -1;

    private View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.pull_to_refresh_list2);
        application = (MyApplication) abApplication;
//        users = application.getUsers();
        mabSoapUtil = AbSoapUtil.getInstance(this);
        mabSoapUtil.setTimeout(10000);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(Constant.MENU2);
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        //mAbTitleBar.setLogoLine(R.drawable.title_line);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setDividerHeight(5);

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
        mList_dw = new ArrayList<>();

        //使用自定义的Adapter
        myListViewAdapter = new UsersListAdapter(this, mList);
        mListView.setAdapter(myListViewAdapter);
        //item被点击事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDiag(position);
            }
        });

        //第一次下载数据
        refreshTask();
    }


    public void btnclick(View view) {
        modifyDialog("add", "", "", "", "", "", "");
    }

    private void modifyDialog(final String action, final String id, String uname, String uids, String pcode, final String pid, final String pname) {
        view = mInflater.inflate(R.layout.modify_user_layout, null);
        final EditText ed1 = (EditText) view.findViewById(R.id.user_uname);
        final EditText ed2 = (EditText) view.findViewById(R.id.user_uids);
        final EditText ed3 = (EditText) view.findViewById(R.id.user_pcode);
        ed2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
        ed3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        if (AbStrUtil.isEquals(action,"add")) {
            ed1.setText(t_uname);
            ed2.setText(t_udis);
            ed3.setText(t_pcode);
        }else {
            ed1.setText(uname);
            ed2.setText(uids);
            ed3.setText(pcode);
        }

        mAlertViewExt = new AlertView("提示", "请完善相关信息！", "取消", null, new String[]{"完成"}, UsersActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                closeKeyboard();
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    String uname = ed1.getText().toString().trim();
                    String uids = ed2.getText().toString().trim();
                    String pcode = ed3.getText().toString().trim();
                    t_uname = uname;
                    t_udis = uids;
                    t_pcode=pcode;

                    if (AbStrUtil.isEmpty(uname) || AbStrUtil.isEmpty(uids) || AbStrUtil.isEmpty(pcode)) {
                        MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_CONFIRM, "内容不能为空！");
                        return;
                    }
                    if (!(pcode.length() == 11)) {
                        MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_CONFIRM, "手机号码位数不正确！");
                        return;
                    }
                    if (!(uids.length() == 18)) {
                        MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_CONFIRM, "身份证号码位数不正确！");
                        return;
                    }
                    modifyUsers(action, id, uids, uname, pid, pcode, pname);
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(view);
        mAlertViewExt.show();
    }

    private void modifyUsers(String action, String id, String uids, String uname, String pid, String pcode, String pname) {
        AbSoapParams params = new AbSoapParams();
        params.put("action", action);
        params.put("id", id);
        params.put("uids", uids);
        params.put("uname", uname);
        params.put("pid", pid);
        params.put("pcode", pcode);
        params.put("pname", pname);

        mabSoapUtil.call(Constant.SOAPURL, Constant.SOAPNSP, Constant.Modify_Users, params, new AbSoapListener() {
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                String rtn = object.getProperty(0).toString();
                if (rtn.equals("1")) {
                    MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_INFO, "操作成功");
                    t_udis = "";
                    t_pcode = "";
                    t_uname = "";
                    loadMoreTask();
                } else {
                    MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_ALERT, "操作失败");
                }
            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_ALERT, "操作失败");

            }

            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                MsgUtil.sendMsgTop(UsersActivity.this, Constant.MSG_ALERT, "操作失败");

            }
        });

    }

    /**
     * 下载数据
     */
    public void refreshTask() {
        currentPage = 1;
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(UsersActivity.this, Constant.GetUsers, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
                        List<Users> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            mList.addAll(UserssList);
                            myListViewAdapter.notifyDataSetChanged();
                            UserssList.clear();
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
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(UsersActivity.this, Constant.GetUsers, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList.clear();
                AbResult result = new AbResult(result1);
                if (aBoolean) {
                    if (result.getResultCode() > 0) {
                        //成功
                        UsersListResult UsersListResult = (UsersListResult) AbJsonUtil.fromJson(result1, UsersListResult.class);
                        List<Users> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            mList.addAll(UserssList);
                            myListViewAdapter.notifyDataSetChanged();
                            UserssList.clear();
                        }
                    }
                    mAbPullToRefreshView.onFooterLoadFinish();

                }

            }
        });
    }


    private void showDiag(final int position) {
        final Users Users =  mList.get(position);
        choose = -1;
        mAlertView = new AlertView("操作菜单", null, "取消", null,
                new String[]{"分配人员部门", "分配人员权限","修改人员信息","删除人员信息"},
                UsersActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                choose = position;
            }
        });
        mAlertView.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                switch (choose) {
                    case 0://分配人员部门
                        init_dw(position);
                        break;
                    case 1://分配人员权限
                        Intent intent = new Intent();
                        intent.putExtra("user", Users);
                        intent.setClass(UsersActivity.this, RollActivity.class);
                        startActivity(intent);
                        break;
                    case 2://修改人员信息
                        modifyDialog("update", Users.getId(), Users.getUname(), Users.getUids(), Users.getPcode(), Users.getPid(), Users.getPname());
                    case 3:
                        new AlertView("请再次确认", "删除 " +  Users.getUname() + " ?" , "取消", new String[]{"确定"}, null, UsersActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    modifyUsers("delete", Users.getId(), "", "", "", "", "");
                                }
                            }
                        }).show();
                        break;
                }

            }
        });
        mAlertView.show();
//        mAvatarView = mInflater.inflate(R.layout.choose_avatar_user, null);
//        Button b1 = (Button) mAvatarView.findViewById(R.id.choose1);
//        Button b2 = (Button) mAvatarView.findViewById(R.id.choose2);
//        Button b3 = (Button) mAvatarView.findViewById(R.id.choose3);
//        Button b4 = (Button) mAvatarView.findViewById(R.id.choose4);
//        Button b5 = (Button) mAvatarView.findViewById(R.id.choose5);
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//                //分配人员部门
//                init_dw(position);
//            }
//
//        });
//
//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//                //分配人员权限
//                Intent intent = new Intent();
//                intent.putExtra("user", Users);
//                intent.setClass(UsersActivity.this, RollActivity.class);
//                startActivity(intent);
//            }
//
//        });
//
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//                //修改人员信息
//                modifyDialog("update", Users.getId(), Users.getUname(), Users.getUids(), Users.getPcode(), Users.getPid(), Users.getPname());
//
//            }
//        });
//
//        b4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //删除人员信息
//                AbDialogUtil.removeDialog(UsersActivity.this);
//                AbDialogUtil.showAlertDialog(UsersActivity.this, R.drawable.ic_action_help,
//                        "请再次确认", "删除 " + Users.getUname() + " ?", new AbAlertDialogFragment.AbDialogOnClickListener() {
//                            @Override
//                            public void onPositiveClick() {
//                                //delete
//                                modifyUsers("delete", Users.getId(), "", "", "", "", "");
//                            }
//
//                            @Override
//                            public void onNegativeClick() {
//                            }
//                        });
//            }
//        });
//
//        b5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//
//            }
//        });
//
//        //显示选择按钮
//        AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);

    }


    /**
     * 选择单位
     */
    private void dwChoose(final int po) {
        //按钮＋列表 dialog
        mView = mInflater.inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        mDepartmentChooseAdapter = new DepartmentChooseOneAdapter(UsersActivity.this, mList_dw);
        listView.setAdapter(mDepartmentChooseAdapter);
        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, UsersActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    for (int i = 0; i < mList_dw.size(); i++) {
                        if (mList_dw.get(i).isChoose()) {
                            String pid = mList_dw.get(i).getPid();
                            String pname = mList_dw.get(i).getPname();
                            Users users = mList.get(po);
                            /**修改归属单位*/
                            modifyUsers("update", users.getId(), users.getUids(), users.getUname(), pid, users.getPcode(), pname);
                        }
                    }
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();

//
//        Button leftBtn = (Button) mView.findViewById(R.id.left_btn);
//        Button rightBtn = (Button) mView.findViewById(R.id.right_btn);
//        AbDialogUtil.showDialog(mView, R.animator.fragment_top_enter, R.animator.fragment_top_exit, R.animator.fragment_pop_top_enter, R.animator.fragment_pop_top_exit);
//        leftBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//            }
//
//        });
//        rightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AbDialogUtil.removeDialog(UsersActivity.this);
//                for (int i = 0; i < mList_dw.size(); i++) {
//                    if (mList_dw.get(i).isChoose()) {
//                        String pid = mList_dw.get(i).getPid();
//                        String pname = mList_dw.get(i).getPname();
//                        Users users = mList.get(position);
//                        /**修改归属单位*/
//                        modifyUsers("update", users.getId(), users.getUids(), users.getUname(), pid, users.getPcode(), pname);
//                    }
//                }
//            }
//
//        });
    }

    private void init_dw(final int position) {
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(UsersActivity.this, Constant.GetDepartMent, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList_dw.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        DepartmentListResult departmentListResult = AbJsonUtil.fromJson(result1, DepartmentListResult.class);
                        List<Department> departmentsList = departmentListResult.getItems();
                        if (departmentsList != null && departmentsList.size() > 0) {
                            mList_dw.addAll(departmentsList);
                            dwChoose(position);
                            departmentsList.clear();
                        }
                    }
                }
            }
        });
    }




    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(UsersActivity.this, false);
        super.onResume();
    }

    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mAlertView != null && mAlertView.isShowing()) {
                mAlertView.dismiss();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
