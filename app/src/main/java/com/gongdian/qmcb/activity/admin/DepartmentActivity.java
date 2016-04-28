package com.gongdian.qmcb.activity.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.DepartmentListAdapter;
import com.gongdian.qmcb.adapter.UserChooseAdapter;
import com.gongdian.qmcb.model.Department;
import com.gongdian.qmcb.model.DepartmentListResult;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.model.UsersListResult;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.gongdian.qmcb.utils.WebServiceUntils2;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class DepartmentActivity extends AbActivity {

    @AbIocView(id = R.id.contact_add, click = "btnclick")
    ImageButton imageButton;
    @AbIocView(id = R.id.button_name)
    TextView button_name;
    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private Users users;
    private List<Department> mList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private DepartmentListAdapter myListViewAdapter = null;
    private AbSoapUtil mabSoapUtil = null;
    private int currentPage = 1;
    private View mAvatarView = null;
    private InputMethodManager imm;
    AlertView mAlertViewExt;//窗口拓展例子
    private EditText etName;//拓展View内容
    private AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
    private int choose = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.pull_to_refresh_list2);
        application = (MyApplication) abApplication;
        mabSoapUtil = AbSoapUtil.getInstance(this);
        mabSoapUtil.setTimeout(10000);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(Constant.MENU1);
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
        button_name.setText("添加部门");
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

        //使用自定义的Adapter
        myListViewAdapter = new DepartmentListAdapter(this, mList);
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


    /**
     * 下载数据
     */
    public void refreshTask() {
        currentPage = 1;
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(DepartmentActivity.this, Constant.GetDepartMent, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        DepartmentListResult departmentListResult = (DepartmentListResult) AbJsonUtil.fromJson(result1, DepartmentListResult.class);
                        List<Department> departmentsList = departmentListResult.getItems();
                        if (departmentsList != null && departmentsList.size() > 0) {
                            mList.addAll(departmentsList);
                            myListViewAdapter.notifyDataSetChanged();
                            departmentsList.clear();
                        }
                        mAbPullToRefreshView.onHeaderRefreshFinish();
                    }
                }
            }
        });

    }

    public void loadMoreTask() {
        currentPage++;
        // 绑定参数
        AbSoapParams params = new AbSoapParams();
        mabSoapUtil.call(Constant.SOAPURL, Constant.SOAPNSP, Constant.GetDepartMent, params, new AbSoapListener() {
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                mList.clear();
                String rtn = object.getProperty(0).toString();
                AbResult result = new AbResult(rtn);
                if (result.getResultCode() > 0) {
                    //成功
                    DepartmentListResult departmentListResult = (DepartmentListResult) AbJsonUtil.fromJson(rtn, DepartmentListResult.class);
                    List<Department> departmentsList = departmentListResult.getItems();
                    if (departmentsList != null && departmentsList.size() > 0) {
                        mList.addAll(departmentsList);
                        myListViewAdapter.notifyDataSetChanged();
                        departmentsList.clear();
                    }
                    mAbPullToRefreshView.onFooterLoadFinish();
                }
            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            @Override
            public void onFailure(int statusCode, SoapFault fault) {

            }
        });

    }

    public void btnclick(View view) {

        modifyDialog("add", "", "");
    }

    private void modifyDialog(final String action, final String pid, String text) {

        mAlertViewExt = new AlertView("提示", "请输入部门名称！", "取消", null, new String[]{"完成"}, DepartmentActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                closeKeyboard();
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    String input = etName.getText().toString().trim();
                    if (input.equals("")) {
                        MsgUtil.sendMsgTop(DepartmentActivity.this, Constant.MSG_CONFIRM, "部门名称不能为空");
                    } else {
                        modifyDepartment(action, pid, input,"");
                    }
                    return;
                }
            }
        });
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form, null);
        etName = (EditText) extView.findViewById(R.id.etName);
        etName.setText(text);
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.show();
    }

    private void modifyDepartment(String action, String pid, String pname,String userid) {
        AbSoapParams params = new AbSoapParams();
        params.put("action", action);
        params.put("pid", pid);
        params.put("pname", pname);
        params.put("userid", userid);
        mabSoapUtil.call(Constant.SOAPURL, Constant.SOAPNSP, Constant.Modify_department, params, new AbSoapListener() {
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                String rtn = object.getProperty(0).toString();
                if (rtn.equals("1")) {
                    MsgUtil.sendMsgTop(DepartmentActivity.this, Constant.MSG_INFO, "操作成功");
                    loadMoreTask();
                }
            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                MsgUtil.sendMsgTop(DepartmentActivity.this, Constant.MSG_ALERT, "操作失败");

            }

            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                MsgUtil.sendMsgTop(DepartmentActivity.this, Constant.MSG_ALERT, "操作失败");

            }
        });

    }


    private void modifyFzr(final String pid,final String pname) {
        AbSoapParams params = new AbSoapParams();
        params.put("pid", pid);
        WebServiceUntils.call(DepartmentActivity.this, Constant.GET_users_by_pid, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
                        List<Users> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            initView(UserssList,pid,pname);
                        } else {
                            MsgUtil.sendMsgTop(DepartmentActivity.this, Constant.MSG_ALERT, "该部门内没有人员,请联系管理员添加");
                        }
                    }
                }
            }
        });
    }

    private void initView(final List<Users> mList_ry,final String pid,final String pname){
        View mView = mInflater.inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        UserChooseAdapter mUserChooseAdapter = new UserChooseAdapter(DepartmentActivity.this, mList_ry);
        listView.setAdapter(mUserChooseAdapter);
        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, DepartmentActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    for (int i=0;i<mList_ry.size();i++) {
                        if (mList_ry.get(i).isChoose()) {
                            if (AbStrUtil.isEmpty(mList_ry.get(i).getId())||mList_ry.get(i).getId().equals("null")) {
                                MsgUtil.sendMsgTop(DepartmentActivity.this,Constant.MSG_ALERT,"该部门内没有人员,请联系管理员添加");
                            }else {
                                modifyDepartment("update", pid, pname, mList_ry.get(i).getId());
                            }
                            break;
                        }
                    }
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();
    }


    private void showDiag(final int position) {
        final Department department = mList.get(position);
        choose = -1;

        mAlertView = new AlertView("操作菜单", null, "取消", null,
                new String[]{"部门负责人","修改部门名称", "删除部门信息"},
                DepartmentActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                choose = position;
            }
        });
        mAlertView.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                AbLogUtil.d("xxxx", String.valueOf(choose));
                switch (choose) {
                    case 0:
                        modifyFzr(department.getPid(),department.getPname());
                        break;
                    case 1:
                        modifyDialog("update", department.getPid(), department.getPname());
                        break;
                    case 2:
                        new AlertView("请再次确认", "删除 " + department.getPname() + " ?" , "取消", new String[]{"确定"}, null, DepartmentActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    modifyDepartment("delete", department.getPid(), department.getPname(),"");
                                }
                            }
                        }).show();
                        break;
                }

            }
        });
        mAlertView.show();

    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(DepartmentActivity.this, false);
        super.onResume();
    }


    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
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
