package com.gongdian.qmcb.activity.project;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.DepartmentChooseAdapter;
import com.gongdian.qmcb.adapter.DepartmentChooseOneAdapter;
import com.gongdian.qmcb.adapter.DzListAdapter;
import com.gongdian.qmcb.adapter.UserChooseAdapter;
import com.gongdian.qmcb.dao.ProjectDao;
import com.gongdian.qmcb.model.Department;
import com.gongdian.qmcb.model.DepartmentListResult;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.Project_dw;
import com.gongdian.qmcb.model.Project_ry;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.model.UsersListResult;
import com.gongdian.qmcb.utils.AppUtil;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProjectActivity extends AbActivity {

    private MyApplication application;
    private View mTimeView1 = null;
    private View mTimeView2 = null;
    private AbTitleBar mAbTitleBar = null;
    private TextView timeTextView1 = null;
    private TextView timeTextView2 = null;
    @AbIocView(id = R.id.commit)
    Button mbutton;
    @AbIocView(id = R.id.dw)
    TextView mView_dw;
    @AbIocView(id = R.id.ry)
    TextView mView_ry;
    @AbIocView(id = R.id.xk_pid)
    TextView mView_xk;
    @AbIocView(id = R.id.nr)
    EditText mView_nr;
    @AbIocView(id = R.id.mc)
    TextView mView_mc;
    @AbIocView(id = R.id.dz)
    EditText mView_dz;
    @AbIocView(id = R.id.dzList)
    ListView mView_dzList;

    private List<Department> mList_dw = null;
    private List<Department> mList_dwxk = null;
    private List<Users> mList_ry = null;
    private DzListAdapter mDzListAdapter = null;
    private Project mProject = null;
    //定义数据库操作实现类
    private ProjectDao mProjectDao = null;
    //本地存储的信息
    private List<Project> mProjects = null;
    private List<Project_dw> mList_dw_choose = null;
    private List<Project_ry> mList_ry_choose = null;
    private int totalCount;
    private boolean isCommit = false;
    private String s[] = {Constant.FLAG1};
    AlertView mAlertViewExt;//窗口拓展例子
    private String xk_pid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_add_project);

        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("创建计划");
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

        application = (MyApplication) getApplication();
        //初始化数据库操作实现类
        mProjectDao = new ProjectDao(AddProjectActivity.this);
        //(1)获取数据库
        mProjectDao.startReadableDatabase();
        //(2)执行查询
        mProjects = mProjectDao.queryList(" pro_id = ?", s);
        totalCount = mProjectDao.queryCount(" pro_id = ?", s);
        //(3)关闭数据库
        mProjectDao.closeDatabase();

        mProject = new Project();
        mList_dw = new ArrayList<>();
        mList_dwxk = new ArrayList<>();
        mList_ry = new ArrayList<>();
        mList_dw_choose = new ArrayList<>();
        mList_ry_choose = new ArrayList<>();
        mView_dw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dwClick();
            }
        });
        mView_ry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ryClick();
            }
        });
        mView_xk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                xkClick();
            }
        });
        /**标识为不可编辑*/
//        mView_dw.setKeyListener(null);
//        mView_ry.setKeyListener(null);

        timeTextView1 = (TextView) findViewById(R.id.measureTimeText1);
        timeTextView2 = (TextView) findViewById(R.id.measureTimeText2);

        timeTextView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeView1 = mInflater.inflate(R.layout.choose_date_three, null);
                initWheelTime(mTimeView1, timeTextView1);
                AbDialogUtil.showDialog(mTimeView1, Gravity.BOTTOM);
            }
        });

        timeTextView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTimeView2 = mInflater.inflate(R.layout.choose_date_three, null);
                initWheelTime(mTimeView2, timeTextView2);
                AbDialogUtil.showDialog(mTimeView2, Gravity.BOTTOM);
            }

        });

        mbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });

        final String[] project_mc = getResources().getStringArray(R.array.project_mc);
        mView_mc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertView(null, null, null, null, project_mc, AddProjectActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        mView_mc.setText(project_mc[position]);
                    }
                }).show();

            }
        });
        //将单位信息和人员信息下载到本地
        init_dw();

    }


    public void initWheelTime(View mTimeView, TextView mText) {
        final AbWheelView mWheelViewMD = (AbWheelView) mTimeView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewMM = (AbWheelView) mTimeView.findViewById(R.id.wheelView2);
        final AbWheelView mWheelViewHH = (AbWheelView) mTimeView.findViewById(R.id.wheelView3);
        Button okBtn = (Button) mTimeView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) mTimeView.findViewById(R.id.cancelBtn);
        mWheelViewMD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        AbWheelUtil.initWheelTimePicker(this, mText, mWheelViewMD, mWheelViewMM, mWheelViewHH, okBtn, cancelBtn, 2016, 1, 1, 10, 0, true);
    }


    private void init_dw() {
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(AddProjectActivity.this, Constant.getDepartmentFilter, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList_dw.clear();
                mList_dwxk.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        DepartmentListResult departmentListResult =  AbJsonUtil.fromJson(result1, DepartmentListResult.class);
                        List<Department> departmentsList = departmentListResult.getItems();
                        if (departmentsList != null && departmentsList.size() > 0) {
                            mList_dw.addAll(departmentsList);

                            try {
                                mList_dwxk = AppUtil.deepCopy(mList_dw);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            //本地库反写
                            if (totalCount > 0) {
                                if (mProjects.get(0).getProject_dw() != null) {
                                    mList_dw_choose = mProjects.get(0).getProject_dw();
                                    for (int i = 0; i < mList_dw_choose.size(); i++) {
                                        String s = mList_dw_choose.get(i).getPid();
                                        String dz = mList_dw_choose.get(i).getDz();
                                        String fzr = mList_dw_choose.get(i).getFzr();
                                        String fzrxm = mList_dw_choose.get(i).getFzrxm();
                                        for (int j = 0; j < mList_dw.size(); j++) {
                                            if (AbStrUtil.isEquals(mList_dw.get(j).getPid(), s)) {
                                                mList_dw.get(j).setChoose(true);
                                                mList_dw.get(j).setDz(dz);
                                                mList_dw.get(j).setFzr(fzr);
                                                mList_dw.get(j).setFzrxm(fzrxm);
                                            }
                                        }
                                    }
                                }
                                //add xf_pid
                                if (mProjects.get(0).getXk_pid()!=null) {
                                    xk_pid = mProjects.get(0).getXk_pid();
                                    for (int i=0;i<mList_dwxk.size();i++) {
                                        if (AbStrUtil.isEquals(mList_dwxk.get(i).getPid(),xk_pid)) {
                                            mList_dwxk.get(i).setChoose(true);
                                            mView_xk.setText(mList_dwxk.get(i).getPname());
                                        }
                                    }
                                }


                            }
                            departmentsList.clear();
                            init_ry();
                        }
                    }
                }
            }
        });
    }



    private void init_ry() {
        AbSoapParams params = new AbSoapParams();
//        params.put("menu", Constant.MENU6);
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(AddProjectActivity.this, Constant.GET_users_by_menu, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList_ry.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
                        List<Users> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            mList_ry.addAll(UserssList);
                            //本地库反写
                            if (totalCount > 0) {
                                if (mProjects.get(0).getProject_ry() != null) {
                                    mList_ry_choose = mProjects.get(0).getProject_ry();
                                    for (int i = 0; i < mList_ry_choose.size(); i++) {
                                        String s = mList_ry_choose.get(i).getUser_id();
                                        for (int j = 0; j < mList_ry.size(); j++) {
                                            if (AbStrUtil.isEquals(mList_ry.get(j).getId(), s)) {
                                                mList_ry.get(j).setChoose(true);
                                            }
                                        }
                                    }
                                }
                            }
                            UserssList.clear();
                            init_project();
                        }
                    }
                }

            }
        });
    }

    private void init_project() {
        if (totalCount > 0) {
            Project project = mProjects.get(0);
            mView_nr.setText(project.getNr());
            mView_mc.setText(project.getMc());
            timeTextView1.setText(project.getKssj());
            timeTextView2.setText(project.getJssj());
            mView_dz.setText(project.getDz());
            create_string_dw();
            create_string_ry();
        }
    }

    private void dwClick() {
        //按钮＋列表 dialog
        View mView = mInflater.inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        DepartmentChooseAdapter mDepartmentChooseAdapter = new DepartmentChooseAdapter(AddProjectActivity.this, mList_dw);
        listView.setAdapter(mDepartmentChooseAdapter);
        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, AddProjectActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    create_string_dw();
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();
    }


    private void ryClick() {
        View mView = mInflater.inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        UserChooseAdapter mUserChooseAdapter = new UserChooseAdapter(AddProjectActivity.this, mList_ry);
        listView.setAdapter(mUserChooseAdapter);
        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, AddProjectActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    create_string_ry();
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();
    }

    private void xkClick() {
        //按钮＋列表 dialog
        View mView = mInflater.inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        DepartmentChooseOneAdapter chooseOneAdapter = new DepartmentChooseOneAdapter(AddProjectActivity.this, mList_dwxk);
        listView.setAdapter(chooseOneAdapter);
        mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, AddProjectActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                    xk_pid="";
                    mView_xk.setText(xk_pid);
                    for (int i=0;i<mList_dwxk.size();i++) {
                        if (mList_dwxk.get(i).isChoose()) {
                            xk_pid = mList_dwxk.get(i).getPid();
                            mView_xk.setText(mList_dwxk.get(i).getPname());
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

    private void create_string_dw() {
        String temp = "";
        mList_dw_choose.clear();
        for (int i = 0; i < mList_dw.size(); i++) {
            if (mList_dw.get(i).isChoose()) {
                Project_dw project_dw = new Project_dw();
                project_dw.setId(Constant.FLAG1);
                project_dw.setPid(mList_dw.get(i).getPid());
                project_dw.setPname(mList_dw.get(i).getPname());
                project_dw.setDz((mList_dw.get(i).getDz()));
                project_dw.setFzr((mList_dw.get(i).getFzr()));
                project_dw.setFzrxm((mList_dw.get(i).getFzrxm()));
                mList_dw_choose.add(project_dw);
                if (temp.length() > 0) {
                    temp = temp + "、" + mList_dw.get(i).getPname();
                } else {
                    temp = temp + mList_dw.get(i).getPname();
                }
            }
            mView_dw.setText(temp);
        }
        setDzList();
    }

    private void setDzList() {
        mDzListAdapter = new DzListAdapter(AddProjectActivity.this, mList_dw_choose);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setListViewHeightBasedOnChildren(mView_dzList);
            }
        });
        mView_dzList.setAdapter(mDzListAdapter);
    }

    private void create_string_ry() {
        String temp = "";
        mList_ry_choose.clear();
        for (int i = 0; i < mList_ry.size(); i++) {
            if (mList_ry.get(i).isChoose()) {
                Project_ry project_ry = new Project_ry();
                project_ry.setId(Constant.FLAG1);
                project_ry.setUser_id(mList_ry.get(i).getId());
                project_ry.setUsername(mList_ry.get(i).getUname());
                mList_ry_choose.add(project_ry);
                if (temp.length() > 0) {
                    temp = temp + "、" + mList_ry.get(i).getUname();
                } else {
                    temp = temp + mList_ry.get(i).getUname();
                }
            }
            mView_ry.setText(temp);
        }
    }


    private void saveProject() {
        String nr = mView_nr.getText().toString().trim();
        String mc = mView_mc.getText().toString().trim();
        String kssj = timeTextView1.getText().toString().trim();
        String zzsj = timeTextView2.getText().toString().trim();
        String dz = "";
        String dw = mView_dw.getText().toString().trim();
        String ry = mView_ry.getText().toString().trim();
        String yf = null;

        for (int i = 0; i < mList_dw_choose.size(); i++) {
            DzListAdapter.ViewHolder mViewHolder = (DzListAdapter.ViewHolder) mView_dzList.getChildAt(i).getTag();
            if (mViewHolder != null) {
                String s = mViewHolder.dzText.getText().toString();
                String fzr = mViewHolder.fzrText.getText().toString();
                String fzrxm = mViewHolder.fzrxmText.getText().toString();
                mList_dw_choose.get(i).setFzr(fzr);
                mList_dw_choose.get(i).setFzrxm(fzrxm);
                if (!AbStrUtil.isEmpty(s)) {
                    mList_dw_choose.get(i).setDz(s);
                    if (dz.length() > 0) {
                        dz = dz + "、" + s;
                    } else {
                        dz = dz + s;
                    }
                }
            }
        }
        mProject.setId(Constant.FLAG1);
        mProject.setFlag("1");
        mProject.setNr(nr);
        mProject.setMc(mc);
        mProject.setYf(yf);
        mProject.setKssj(kssj);
        mProject.setJssj(zzsj);
        mProject.setDz(dz);
//        mProject.setCreateuser(application.getUsers().getId());
//        mProject.setCreateusername(application.getUsers().getUname());
        mProject.setProject_dw(mList_dw_choose);
        mProject.setProject_ry(mList_ry_choose);
        mProject.setDw(dw);
        mProject.setRy(ry);
        mProject.setXk_pid(xk_pid);
        mProjectDao.startWritableDatabase(true);
        mProjectDao.delete(" pro_id = ?", s);
        mProjectDao.insert(mProject);
        mProjectDao.closeDatabase();
    }

    private void btnClick() {
        commit();
    }

    private void commit() {
        mbutton.setEnabled(false);
        saveProject();

        if (AbStrUtil.isEmpty(mProject.getMc())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请选择项目名称");
            mbutton.setEnabled(true);
            return;
        }
        if (AbStrUtil.isEmpty(mProject.getNr())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入工作内容");
            mbutton.setEnabled(true);
            return;
        }
        if (AbStrUtil.isEmpty(mProject.getKssj())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入工作开始时间");
            mbutton.setEnabled(true);
            return;
        } else {
            mProject.setYf(mProject.getKssj().substring(0, 4) + mProject.getKssj().substring(5, 7));
        }
        if (AbStrUtil.isEmpty(mProject.getJssj())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入工作结束时间");
            mbutton.setEnabled(true);
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date1 = format.parse(mProject.getKssj());
            Date date2 = format.parse(mProject.getJssj());
            if (date1.getTime() > date2.getTime() ) {
                mbutton.setEnabled(true);
                MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "开始时间不能早于终止时间!");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mList_dw_choose.size() <= 0) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请选择工作施工单位!");
            mbutton.setEnabled(true);
            return;
        }
        if (mList_ry_choose.size() <= 0) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请选择到岗到位人员!");
            mbutton.setEnabled(true);
            return;
        }

        if (AbStrUtil.isEmpty(mProject.getDz())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入工作地点");
            mbutton.setEnabled(true);
            return;
        }
        if (AbStrUtil.isEmpty(mProject.getXk_pid())) {
            MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请选择开完工许可单位");
            mbutton.setEnabled(true);
            return;
        }
        for (int i = 0; i < mList_dw_choose.size(); i++) {
            if (AbStrUtil.isEmpty(mList_dw_choose.get(i).getDz())) {
                MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入 " + mList_dw_choose.get(i).getPname() + " 的工作地点!");
                return;
            }
            if (AbStrUtil.isEmpty(mList_dw_choose.get(i).getFzr())) {
                MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_CONFIRM, "请输入 " + mList_dw_choose.get(i).getPname() + " 的负责人!");
                return;
            }
        }

        String gson = AbJsonUtil.toJson(mProject);
        AbSoapParams params = new AbSoapParams();
        params.put("action", "add");
        params.put("gson", gson);

        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(AddProjectActivity.this, Constant.Modify_Project, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean && AbStrUtil.isEquals(result, "1")) {
                    MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_INFO, "计划上传成功!");
                    isCommit = true;
                    mProjectDao.startWritableDatabase(true);
                    mProjectDao.delete(" pro_id = ?", s);
                    mProjectDao.closeDatabase();
                    application.setIsAddProject(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    }, 1000);
                } else {
                    MsgUtil.sendMsgTop(AddProjectActivity.this, Constant.MSG_ALERT, "计划上传失败!");
                    mbutton.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isCommit) {
            saveProject();
        }
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