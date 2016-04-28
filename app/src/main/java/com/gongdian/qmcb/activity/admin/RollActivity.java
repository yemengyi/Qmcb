package com.gongdian.qmcb.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.RollListAdapter;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.model.MenuListResult;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class RollActivity extends AbActivity {

    private MyApplication application;
    private Users user;
    private AbSoapUtil mAbSoapUtil = null;
    private List<Menu> mList=null;
    private ListView mListView;
    private RollListAdapter rollListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_roll);
        application = (MyApplication) abApplication;

        mAbSoapUtil = AbSoapUtil.getInstance(this);
        mAbSoapUtil.setTimeout(10000);

        user = (Users) getIntent().getSerializableExtra("user");

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(user.getUname() + " 权限分配");
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

        //获取ListView对象
        mListView = (ListView) findViewById(R.id.mListView);
        mList = new ArrayList<>();
        //够造SimpleAdapter对象，适配数据
        rollListAdapter = new RollListAdapter(RollActivity.this, mList,user.getUids());
        mListView.setAdapter(rollListAdapter);


        //item被点击事件
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RollListAdapter.ViewHolder viewHolder = (RollListAdapter.ViewHolder)view.getTag();
//                String menu_id = mList.get(position).getId();
//                String flag = viewHolder.itemsCheck.isChecked()?"1":"0";
//                modifyRoll(menu_id,flag);

            }
        });

        getMenu();

    }



    public void getMenu() {
        AbSoapParams params = new AbSoapParams();
        params.put("uids", user.getUids());
        mAbSoapUtil.call(Constant.SOAPURL, Constant.SOAPNSP, Constant.GetMenuAll, params, new AbSoapListener() {
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                mList.clear();
                String rtn = object.getProperty(0).toString();
                AbResult result = new AbResult(rtn);
                if (result.getResultCode() > 0) {
                    //成功
                    MenuListResult menuListResult = AbJsonUtil.fromJson(rtn, MenuListResult.class);
                    List<Menu> menuList = menuListResult.getItems();
                    if (menuList != null && menuList.size() > 0) {
                        mList.addAll(menuList);
                        rollListAdapter.notifyDataSetChanged();
                        menuList.clear();
                        AbLogUtil.d("xxxxxx", "cccccc");

                    }
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

//    private void modifyRoll(String menu_id, String flag) {
//        AbSoapParams params = new AbSoapParams();
//        params.put("uids", user.getUids());
//        params.put("menu_id", menu_id);
//        params.put("flag", flag);
//
//        AbLogUtil.d("xxxxxx2", menu_id + "   " + flag);
//
//        WebServiceUntils.call(RollActivity.this, Constant.Modify_Rolls, params, 10000, false, "更新中...", new WebServiceUntils.webServiceCallBack() {
//            @Override
//            public void callback(Boolean aBoolean, String result) {
//                if (!AbStrUtil.isEquals(result, "1")) {
//                    AbToastUtil.showToast(RollActivity.this, "变动失败!");
//                }
//            }
//        });
//
//    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(RollActivity.this, false);
        super.onResume();
    }


}


