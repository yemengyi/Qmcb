package com.gongdian.qmcb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.AboutActivity;
import com.gongdian.qmcb.activity.RegisterActivity;
import com.gongdian.qmcb.activity.SendMsgActivity;
import com.gongdian.qmcb.activity.profile.MineActivity;
import com.gongdian.qmcb.activity.profile.PublicityActivity;
import com.gongdian.qmcb.activity.project.MineProject;
import com.gongdian.qmcb.activity.project.ShowQdActivity;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.utils.AppUtil;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pgyersdk.feedback.PgyFeedback;

import java.util.ArrayList;
import java.util.List;


//我
public class FragmentProfile extends Fragment implements OnClickListener {
    private View layout;
    private TextView tvname, tv_accout, tvdebug, qd, project_task, sp_task, txt_dx;
    private RelativeLayout lay03;
    private MyApplication application;
    private Activity mActivity = null;
    private RoundedImageView mImageHead = null;
    private AbImageLoader mAbImageLoader = null;
    private QmcbUsers user;
    private List<Project> mList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();
        user = application.getUser();
        mAbImageLoader = new AbImageLoader(mActivity);
        if (layout == null) {
            layout = mActivity.getLayoutInflater().inflate(R.layout.fragment_profile,
                    null);
            initViews();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        checkLogin();
        return layout;
    }

    private void initViews() {
        mImageHead = (RoundedImageView) layout.findViewById(R.id.head);
        /**100为圆,<100为椭圆*/
        mImageHead.setCornerRadius(100);
        mImageHead.setOval(true);
        tvname = (TextView) layout.findViewById(R.id.tvname);
        tv_accout = (TextView) layout.findViewById(R.id.tvmsg);
        tvdebug = (TextView) layout.findViewById(R.id.debug);
        qd = (TextView) layout.findViewById(R.id.qd);
        project_task = (TextView) layout.findViewById(R.id.project_task);
        sp_task = (TextView) layout.findViewById(R.id.sp_task);
        txt_dx = (TextView) layout.findViewById(R.id.txt_dx);
        lay03 = (RelativeLayout) layout.findViewById(R.id.lay03);
        if (AbStrUtil.isEquals(String.valueOf(user.getRole()), "0")) {
            txt_dx.setVisibility(View.VISIBLE);
        } else {
            txt_dx.setVisibility(View.GONE);
        }
    }

    private void setOnListener() {
        layout.findViewById(R.id.view_user).setOnClickListener(this);
        layout.findViewById(R.id.qd).setOnClickListener(this);
        layout.findViewById(R.id.txt_cast).setOnClickListener(this);
        layout.findViewById(R.id.txt_project).setOnClickListener(this);
        layout.findViewById(R.id.text_fk).setOnClickListener(this);
        layout.findViewById(R.id.text_about).setOnClickListener(this);
        layout.findViewById(R.id.head).setOnClickListener(this);
        layout.findViewById(R.id.txt_dx).setOnClickListener(this);
    }

    private void checkLogin() {
        if (application.isLogin) {
            user = application.getUser();
            if (AbStrUtil.isEmpty(user.getHeadurl())) {
                if (AbStrUtil.isEquals(user.getXb(), "2")) {
                    mImageHead.setBackgroundResource(R.drawable.ic_sex_female);
                } else {
                    mImageHead.setBackgroundResource(R.drawable.icon_login);
                }

        } else {
            mAbImageLoader.display(mImageHead, user.getHeadurl());
        }

        tvname.setText(application.getUser().getFzr());
        tv_accout.setText(application.getUser().getMc());
    }

    else

    {
        mImageHead.setBackgroundResource(R.drawable.icon_addfriend);
        tvname.setText("未登陆");
        tv_accout.setText("手机号：--");
    }

}


    public void initDataSp(String spnumber) {
        if (Integer.parseInt(spnumber) > 0) {
            project_task.setVisibility(View.VISIBLE);
            project_task.setText(spnumber);
        } else {
            project_task.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_dx:
                AppUtil.start_Activity(mActivity, SendMsgActivity.class);
                break;
            case R.id.view_user:
                AppUtil.start_Activity(mActivity, MineActivity.class);
                break;
            case R.id.qd://签到
                AppUtil.start_Activity(mActivity, ShowQdActivity.class);
                break;
            case R.id.txt_cast:
                AppUtil.start_Activity(mActivity, PublicityActivity.class);
                break;
            case R.id.txt_project: //当前工作
                AppUtil.start_ResultActivity(mActivity, MineProject.class, Constant.MineProjectResultCode);
                break;
            case R.id.text_fk:
                PgyFeedback.getInstance().showDialog(mActivity);
                break;
            case R.id.text_about:
                AppUtil.start_Activity(mActivity, AboutActivity.class);
                break;
            case R.id.head:
                if (application.isLogin) {
                    AppUtil.start_Activity(mActivity, MineActivity.class);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, RegisterActivity.class);
                    getActivity().startActivityForResult(intent, Constant.LoginResultCode);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (application.getUserChanged2()) {
            checkLogin();
            application.setUserChanged2(false);
        }
    }
}