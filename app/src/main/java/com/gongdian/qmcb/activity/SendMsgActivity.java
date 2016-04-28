package com.gongdian.qmcb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.MyGridAdapter;
import com.gongdian.qmcb.model.DepartmentListResult_Observer;
import com.gongdian.qmcb.model.Department_Observer;
import com.gongdian.qmcb.model.PhoneNumber;
import com.gongdian.qmcb.model.Smg;
import com.gongdian.qmcb.model.Users_Observer;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.util.ArrayList;
import java.util.List;

public class SendMsgActivity extends AbActivity {

    private MyApplication application;
    private Button btn_choose;
    private Button btn_commit;
    private EditText txt_nr;
    private Button btn_url;
    private ArrayList<Department_Observer> mList;
    private MyGridAdapter gridAdapter;
    private GridView gridView;
    private List<Object> gridList = new ArrayList<>();
    List<Department_Observer> groupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_sendmsg);
        application = (MyApplication) abApplication;
        mList = new ArrayList<>();

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("短信群发");
        mAbTitleBar.setLogo(R.drawable.title_selector_back);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

       initView();

    }

    private void initView(){
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new MyGridAdapter(this, gridList);
        gridView.setAdapter(gridAdapter);
        txt_nr = (EditText)findViewById(R.id.nr);
        btn_url = (Button)findViewById(R.id.btn_url);
        btn_commit = (Button)findViewById(R.id.commit);
        btn_choose = (Button)findViewById(R.id.choose);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTask();
            }
        });
        btn_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = txt_nr.getText().toString() + Constant.APPURL;
                txt_nr.setText(s);
            }
        });

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });

    }


    /**
     * 下载数据
     */
    public void refreshTask() {
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = new WebServiceUntils2(SendMsgActivity.this,Constant.GET_department_users,params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        DepartmentListResult_Observer departmentListResult = AbJsonUtil.fromJson(result1, DepartmentListResult_Observer.class);
                        List<Department_Observer> departmentsList = departmentListResult.getItems();
                        if (departmentsList != null && departmentsList.size() > 0) {
                            mList.addAll(departmentsList);
                            departmentsList.clear();
                            Intent intent = new Intent(SendMsgActivity.this, ChooseActivity.class);
                            intent.putExtra("department", mList);
                            startActivityForResult(intent, 1);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000 ) {
            groupList = (List<Department_Observer>)data.getSerializableExtra("department");
            reFlashGridView(groupList);
        }
    }

    public void reFlashGridView(List<Department_Observer> groupList){
        gridList.clear();
        if(groupList!=null) {
            for (Department_Observer group : groupList) {
                if (group.isChecked()) {
                    gridList.add(group);
                } else {
                    if(group.getUsers()!=null) {
                        for (Users_Observer user : group.getUsers()) {
                            if (user.isChecked()) {
                                gridList.add(user);
                            }
                        }
                    }
                }
            }
        }
        gridAdapter.notifyDataSetChanged();
    }

    private void commit(){
        String msg = txt_nr.getText().toString();
        if (AbStrUtil.isEmpty(msg)) {
            MsgUtil.sendMsgTop(SendMsgActivity.this,Constant.MSG_ALERT,"请输入发送内容");
            return;
        }

        List<PhoneNumber> phone = new ArrayList<>();
        if(groupList!=null) {
            for (Department_Observer group : groupList) {
                if (group.isChecked()) {
                    if(group.getUsers()!=null) {
                        for (Users_Observer user : group.getUsers()) {
                            PhoneNumber phoneNumber = new PhoneNumber();
                            phoneNumber.setPhonenumber(user.getPcode());
                            phone.add(phoneNumber);
                            }
                    }
                } else {
                    if(group.getUsers()!=null) {
                        for (Users_Observer user : group.getUsers()) {
                            if (user.isChecked()) {
                                PhoneNumber phoneNumber = new PhoneNumber();
                                phoneNumber.setPhonenumber(user.getPcode());
                                phone.add(phoneNumber);
                            }
                        }
                    }
                }
            }
        }
        Smg smg = new Smg();
        smg.setNr(msg);
        smg.setPhoneNumbers(phone);

        AbSoapParams params = new AbSoapParams();
        String gson = AbJsonUtil.toJson(smg);
        params.put("gson",gson);
        AbLogUtil.d("xxxx",gson);
        WebServiceUntils.call(SendMsgActivity.this, Constant.Send_msgs, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean) {
                    MsgUtil.sendMsgTop(SendMsgActivity.this, Constant.MSG_INFO, "发送成功!");
//                    groupList.clear();
//                    gridList.clear();
//                    gridAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}


