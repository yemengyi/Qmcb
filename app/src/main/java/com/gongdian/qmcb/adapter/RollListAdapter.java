package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ab.soap.AbSoapParams;
import com.ab.util.AbStrUtil;
import com.ab.view.sliding.AbSlidingButton;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.admin.RollActivity;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.WebServiceUntils2;

import java.util.List;

/**
 * 名称：MyListViewAdapter
 * 描述：ListView自定义Adapter例子
 */
public class RollListAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Menu> mData;
    private String uids;
    private WebServiceUntils2 webServiceUntils2;

    /**
     * 构造方法
     * --@param context
     * --@param data 列表展现的数据
     * --@param resource 单行的布局
     * --@param to view的id
     */
    public RollListAdapter(Context context, List<Menu> data, String uid) {
        mContext = context;
        mData = data;
        uids = uid;
        webServiceUntils2 = new WebServiceUntils2(context,Constant.Modify_Rolls);
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            //使用自定义的list_items作为Layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_list_slider_button, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.itemsMenu = (TextView) convertView.findViewById(R.id.itemsMenu);
            holder.itemsCheck = (AbSlidingButton) convertView.findViewById(R.id.mSliderBtn);
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        final Menu dataSet = mData.get(position);

        //设置数据到View
        holder.itemsMenu.setText(dataSet.getMenu());

        //设置开关显示所用的图片
        holder.itemsCheck.setImageResource(R.drawable.btn_bottom, R.drawable.btn_frame, R.drawable.btn_mask, R.drawable.btn_unpressed, R.drawable.btn_pressed);

//        if ( AbStrUtil.isEmpty(dataSet.getFlag())){
//            dataSet.setFlag("0");
//        }
        //设置开关的监听
        holder.itemsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                String temp = dataSet.getFlag();
                String flag = null;
                if (arg1) {
                    flag = "1";
                    dataSet.setFlag("1");
                } else {
                    flag = "0";
                    dataSet.setFlag("0");
                }
                if (!AbStrUtil.isEquals(flag,temp)) {
                    modifyRoll(dataSet.getId(), flag);
                }

            }
        });


        if (AbStrUtil.isEquals(dataSet.getFlag(),"1")) {
            holder.itemsCheck.setChecked(true);
        } else {
            holder.itemsCheck.setChecked(false);
        }

        //holder.itemsCheck.setFocusable(false);
        //设置开关的默认状态    true开启状态
        //holder.itemsCheck.setToggleState(true);



        return convertView;
    }

    /**
     * ViewHolder类
     */
    public static class ViewHolder {
        TextView itemsMenu;
        public AbSlidingButton itemsCheck;
    }

    private void modifyRoll(String menu_id, String flag) {
        AbSoapParams params = new AbSoapParams();
        params.put("uids", uids);
        params.put("menu_id", menu_id);
        params.put("flag", flag);

        webServiceUntils2.setParams(params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (!AbStrUtil.isEquals(result, "1")) {
                    MsgUtil.sendMsgTop((RollActivity)mContext,Constant.MSG_ALERT,"变动失败");
                }
            }
        });

    }


}
